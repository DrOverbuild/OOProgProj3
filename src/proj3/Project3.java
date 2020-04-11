// Jasper Reddin
// OOP with Java - Spring 2020
// Mark Doderer

package proj3;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Consumer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import patientpredictor.Patient;
import patientpredictor.PatientCollection;

/**
 * Loads all patients, builds an html table, and sends the table to a jsp page
 * 
 */
@WebServlet("/Project3")
public class Project3 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public final static String CONN_URL = "jdbc:mysql://localhost:3306/patients?user=reader&password=readDBdat@";
	
	// table record structure:
	// |id|result|prediction|proteins|
	
	Connection conn = null;
      
    public Project3() {
        super();
        // TODO Auto-generated constructor stub
    }

    // if signed in, load the table.
    // if the logout param was added (from the logout link), delete the session and refer the user to the log in page
    // if not, just refer the user to the log in page
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// check if signed in
		// the session attributes are stored on the server side and cannot be modified by the client, but they get a cookie
		// with a session id that matches with the sessions on the server.
		// therefore it is safe to check if a user is signed in like this
		if (request.getSession().getAttribute("signed_in") != null && (boolean)request.getSession().getAttribute("signed_in")) {
			
			// if logout parameter is set, remove the signed in attribute and send user to the log in page
			if (request.getParameter("logout") != null) {
				request.getSession().removeAttribute("signed_in");
				request.setAttribute("error", "You have logged out. Please <a href='./index.html'>Log in</a>.");
			} else {
				// otherwise, build the table
				signedIn(request);
			}
			
			
		} else {
			request.setAttribute("error", "Please <a href='./index.html'>Log in</a>.");
		}
		
		// send to jsp
		// there will be one of two attributes that the jsp page will check for and only add it if it exists: error, and patients_table
		RequestDispatcher rd = request.getRequestDispatcher("/patients.jsp");
		rd.forward(request,response);  //forwarded to patients.jsp
	}

	// the post request means the user is coming from the sign in page
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// check if already signed in
		if (request.getSession().getAttribute("signed_in") != null && (boolean)request.getSession().getAttribute("signed_in")) {
			signedIn(request);
		} else {
			if (request.getParameter("username") != null && request.getParameter("password") != null) {
				// normally I would be checking this with a database but I don't have time to set the user table up
				// plus, it's not required so I'm not going to worry about it
				if (request.getParameter("username").equals("md") && request.getParameter("password").equals("pw")) {
					request.getSession().setAttribute("signed_in", true);
					signedIn(request);
				} else {
					request.setAttribute("error", "Username or password is incorrect.");
				}
			} else {
				request.setAttribute("error", "Please log in.");
			}
		}
		
		// send to jsp
		RequestDispatcher rd = request.getRequestDispatcher("/patients.jsp");
		rd.forward(request,response);  //forwarded to patients.jsp
	}
	
	// set up database connection
	protected void makeDBConnection() {
		if (conn != null) {
			return;
		}
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = java.sql.DriverManager.getConnection(CONN_URL);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		} 
	}
	
	// loads the patients from the database and builds an html table to send to the jsp page
	protected void signedIn(HttpServletRequest request) {
		// set up db connecdtion
		makeDBConnection();
					
		if (conn == null) {
			request.setAttribute("error", "Error connecting to database (check console)");
			return;
		}
		
		// send data from file to database if needed
		importPatientsIntoDatabase(request);
		
		// build table of patients
		loadPatients(request);	
	}
	
	// imports patient data from file if the table is empty
	protected void importPatientsIntoDatabase(HttpServletRequest request) {
		try { 
			// check if patients need to be loaded in (empty table)
			PreparedStatement st = conn.prepareStatement("select COUNT(*) from patients.patients");
			ResultSet rs = st.executeQuery();
			rs.first();
			int count = rs.getInt(1);
			if (count == 0) {
				st.close();
				rs.close();
				// do import
				
				// load from file
				PatientCollection patientCollection = new PatientCollection(this.getServletContext().getRealPath("/data.csv"));
				
				StringBuilder statement = new StringBuilder("INSERT INTO `patients` (`ID`, `RESULT`, `PRED`, `PROTEINS`) VALUES ");
				String prefix = "";
				
				// iterate through loaded patients
				for (Patient p: patientCollection.list()) {
					String id = p.getPatientID();
					String result = p.getTreatmentResults();
					String pred = p.getPrediction();
					
					// create string array and join proteins separated by commas
					String[] sarr = Arrays.stream(p.getProteins()).mapToObj(String::valueOf).toArray(String[]::new);
					String proteins = String.join(",", sarr);
					
					statement.append(prefix);
					prefix = ", ";
					
					statement.append("('").append(id).append("','").append(result).append("','").append(pred).append("','")
						.append(proteins).append("')");
				}
				
				st = conn.prepareStatement(statement.toString());
				st.execute();
			}
		} catch (SQLException e){
			e.printStackTrace();
			request.setAttribute("error", "Error connecting to database (check console)");
		}
	}
	
	// loads the id, result, and pred from all patients of the database and builds a table with them
	protected void loadPatients(HttpServletRequest request) {
		try {
			// set up statement and execute
			PreparedStatement st = conn.prepareStatement("SELECT id, result, pred FROM patients.patients ORDER BY id ASC");
			ResultSet rs = st.executeQuery(); 
			
			// start stringbuilder
			StringBuilder table = new StringBuilder("<table><tr><td>Patient ID</td><td>Result</td><td>Prediction</td></tr>\n");
			
			// for each row in the result set
			while (rs.next()) {
				// get id, result, and prediction
				String id = rs.getString(1);
				String result = rs.getString(2);
				String pred = rs.getString(3);
				
				// add all three to a new table row
				table.append("<tr data-patient-id=\"").append(id).append("\"><td>")
					.append(id).append("</td><td>").append(result).append("</td><td>")
					.append(pred).append("</td></tr>\n");
			}
			
			// conclude the html
			table.append("</table>\n");
			
			// send to the request to send to the jsp
			request.setAttribute("patients_table", table.toString());
			
			rs.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
			request.setAttribute("error", "Error connecting to database (check console)");
		}
	}

	public void dispose() {
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
