package proj3;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.function.Consumer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import patientpredictor.Patient;
import patientpredictor.PatientCollection;

/**
 * Servlet implementation class Project3
 */
@WebServlet("/Project3")
public class Project3 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public final static String CONN_URL = "jdbc:mysql://localhost:3306/patients?user=reader&password=readDBdat@";
	
	// table record structure:
	// |id|result|prediction|protein1|...|protein4776|
	
	Connection conn = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Project3() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		doGet(request, response);
		// check password
		
		if (request.getParameter("username") != null && request.getParameter("password") != null) {
			// normally I would be checking this with a database but I don't have time to set the user database up
			// plus, it's not required so I'm not going to worry about it
			if (request.getParameter("username").equals("md") && request.getParameter("password").equals("pw")) {
				request.getSession().setAttribute("signed_in", true);
			} else {
				request.setAttribute("error", "Username or password is incorrect.");
			}
		} else {
			request.setAttribute("error", "Username or password is incorrect.");
		}
		
		if (request.getSession().getAttribute("signed_in") != null && (boolean)request.getSession().getAttribute("sign_in")) {
			signedIn(request);
		} else {
			// require log in.
		}
		
		// send to jsp
		RequestDispatcher rd=request.getRequestDispatcher("/patients.jsp");
		rd.forward(request,response);  //forwarded to welcome.jsp
	}
	
	protected void makeDBConnection() {
		if (conn != null) {
			return;
		}
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = java.sql.DriverManager.getConnection(CONN_URL);
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	protected void signedIn(HttpServletRequest request) {
		// set up db connecdtion
		makeDBConnection();
					
		if (conn == null) {
			request.setAttribute("error", "Error connecting to database (check console)");
			return;
		}
					
		importPatientsIntoDatabase(request);
		
		loadPatients(request);	
	}
	
	protected void importPatientsIntoDatabase(HttpServletRequest request) {
		try {
			DatabaseMetaData meta = conn.getMetaData();
		      ResultSet res = meta.getTables(null, null, null, new String[] {"TABLE"});
		      System.out.println("List of tables: ");
		      while (res.next()) {
		         System.out.println("   "+res.getString("TABLE_CAT")
		           + ", "+res.getString("TABLE_SCHEM")
		           + ", "+res.getString("TABLE_NAME")
		           + ", "+res.getString("TABLE_TYPE")
		           + ", "+res.getString("REMARKS"));
		      }
		// check if table exists and has the right schema
		// - fix if needed
		
		// check if patients need to be loaded in
		// if so:
		//  - new patients collection from file
		//  - for each patient:
		//    - append to stringbuilder record for that patient
		} catch (SQLException e){
			e.printStackTrace();
			request.setAttribute("error", "Error connecting to database (check console)");
		}
	}
	
	protected void loadPatients(HttpServletRequest request) {
		try {
			PreparedStatement st = conn.prepareStatement("SELECT (id,result,pred) FROM patients.patients;");
			ResultSet rs = st.executeQuery(); 
			
			StringBuilder table = new StringBuilder("<table>\n");
			
			while (rs.next()) {
				String id = rs.getString(0);
				String result = rs.getString(1);
				String pred = rs.getString(2);
				
				table.append("<tr><td>").append(id).append("</td><td>").append(result).append("</td><td>").append(pred).append("</td></tr>\n");
			}
			
			table.append("</table>\n");
			
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
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
