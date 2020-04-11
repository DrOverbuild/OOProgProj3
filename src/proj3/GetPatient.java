// Jasper Reddin
// OOP with Java - Spring 2020
// Mark Doderer
package proj3;

import patientpredictor.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Servlet that handles loading, adding, and editing a single patient
@WebServlet("/GetPatient")
public class GetPatient extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	public final static String CONN_URL = "jdbc:mysql://localhost:3306/patients?user=reader&password=readDBdat@";

	public Connection conn = null;
	
	// sets up DB connection
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
	
	// select patient from database and return it
	protected Patient loadPatient(String id, boolean includeProteins) {
		// do nothing if connection failed
		if (conn == null) {
			return null;
		}
		
		try {
			// check if we want to load proteins in our query (we don't unless we are using them to make predictions) 
			String proteinsAddition = "";
			if (includeProteins) {
				proteinsAddition = ",proteins";
			}
			
			// set up statement and execute
			PreparedStatement statement = conn.prepareStatement("SELECT id,result,pred" + proteinsAddition + " FROM patients WHERE id = ?");
			int idInt = Integer.parseInt(id);
			statement.setInt(1, idInt);
			ResultSet rs = statement.executeQuery();
			
			// there should only be one row returned (or zero) because id is a primary key
			// so, select last (only) item in resultset
			if  (rs.last()) {
				// grab data from result set and put it into new patient object
				String result = rs.getString(2);
				String pred = rs.getString(3);
				
				// use empty proteins array if we're not using the proteins for this request
				double[] proteins = new double[]{};
				
				if (includeProteins) {
					proteins = new double[4776];
					String[] proteinsStr = rs.getString(4).split(",");
					for (int i = 0; i < proteins.length; i++) {
						proteins[i] = Double.parseDouble(proteinsStr[i]);
					}
				}
				
				Patient patient = new Patient(result, pred, id, proteins);
				
				statement.close();
				rs.close();
				
				return patient;
			}
		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	// update patient in database
	// note: ignores changes to the proteins
	protected void updatePatient(Patient p) {
		// do nothing if connection failed
		if (conn == null) {
			return;
		}
		
		try {
			// set up statement and execute
			PreparedStatement st = conn.prepareStatement("UPDATE patients SET result = ?, pred = ? WHERE id = ?");
			st.setString(1, p.getTreatmentResults());
			st.setString(2, p.getPrediction());
			
			int idInt = Integer.parseInt(p.getPatientID());
			
			st.setInt(3, idInt);
			
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected void deletePatient(String id) {
		// do nothing if connection failed
		if (conn == null) {
			return;
		}
		
		try {
			// set up statement
			PreparedStatement statement = conn.prepareStatement("DELETE FROM patients WHERE id = ?");
			int idInt = Integer.parseInt(id);
			statement.setInt(1, idInt);
			
			// execute
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// insert patient into database
	protected void insertPatient(String id, String result, String pred, String proteins) {
		// do nothing if connection failed
		if (conn == null) {
			return;
		}
		
		try {
			// set up statement
			PreparedStatement statement = conn.prepareStatement("INSERT INTO patients (id, result, pred, proteins) VALUES (?, ?, ?, ?)");
			statement.setString(1, id);
			statement.setString(2, result);
			statement.setString(3, pred);
			statement.setString(4, proteins);
			
			// execute
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
    public GetPatient() {
        super();
    }

	/**
	 * Get request: depending on the parameter this will send html code to add to the right column,
	 *  update a patient, make a prediction, or delete a patient
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// check if signed in
		if (request.getSession().getAttribute("signed_in") != null && (boolean)request.getSession().getAttribute("signed_in")) {
			
			// if there's no id provided, we want to send a form for user to add a new patient
			if (request.getParameter("id")==null) {
				response.getWriter().append("<form class=\"small\">\n" + 
						" 			<div class=\"form-line-small\">\n" + 
						" 				<label for=\"id\">ID:</label>\n" + 
						" 				<input type=\"text\" id=\"id\" name=\"id\"></input>\n" + 
						" 			</div>\n" + 
						" 			<div class=\"form-line-small\">\n" + 
						" 				<label for=\"result\">Result:</label>\n" + 
						" 				<input id=\"result\" name=\"result\" type=\"text\"></input>\n" + 
						" 			</div>\n" + 
						" 			<div class=\"form-line-small\">\n" + 
						" 				<label for=\"pred\">Prediction:</label>\n" + 
						" 				<input  id=\"pred\" name=\"pred\" type=\"text\"></input>\n" + 
						" 			</div>\n" + 
						" 			<div class=\"form-line-small\">\n" + 
						" 				<label for=\"proteins\">Proteins:</label>\n" + 
						" 				<textarea rows=\"4\" id=\"proteins\" name=\"proteins\"></textarea>\n" + 
						" 			</div>\n" + 
						" 			<div class=\"form-line-small\">\n" + 
						" 				<button class=\"save\">Save</button>\n" + 
						" 			</div>\n" + 
						" 		</form>");
				return;
			}
			
			// otherwise, we need to get the id
			makeDBConnection();
			String id = "";
			id = (String)request.getParameter("id");
			
			// decide if we need to predict, delete, or update patient
			if (request.getParameter("predict") != null) {
				// we are making a prediction
				// load patient (with proteins), set the prediction, then update the patient
				Patient patient = loadPatient(id, true);
				patient.setPrediction(patientpredictor.Predictor.predict(patient));
				updatePatient(patient);
			} else if (request.getParameter("delete") != null) {
				// we're deleting the patient
				deletePatient(id);
			} else if (request.getParameter("save") != null) {
				// we're updating the result or prediction of the patient by hand
				String result;
				String pred;
				
				// make sure we have all the parameters
				if (request.getParameter("result") != null) {
					result = (String)request.getParameter("result");
				} else {
					response.getWriter().append("Please provide all the parameters.");
					return;
				}
				
				if (request.getParameter("pred") != null) {
					pred = (String)request.getParameter("pred");
				} else {
					response.getWriter().append("Please provide all the parameters.");
					return;
				}
				
				// build a new patient and update it
				Patient patient = new Patient(result, pred, id, new double[]{});
				updatePatient(patient);
			} else {
				// nothing else except an id is provided, so we're just loading the patient 
				// and giving a form for user to update
				Patient patient = loadPatient(id, false);
				if (patient == null) {
					response.getWriter().append("Could not load patient");
				} else {
					response.getWriter().append("<form class=\"small\">\n" +
						" 			<div class=\"form-line-small\">\n" + 
						" 				<label for=\"id\">ID:</label>\n" + 
						" 				<input value=\"" + id + "\" type=\"text\" id=\"id\" name=\"id\"></input>\n" + 
						" 			</div>\n" + 
						" 			<div class=\"form-line-small\">\n" + 
						" 				<label for=\"result\">Result:</label>\n" + 
						" 				<input value=\"" + patient.getTreatmentResults() + "\" id=\"result\" name=\"result\" type=\"text\"></input>\n" + 
						" 			</div>\n" + 
						" 			<div class=\"form-line-small\">\n" + 
						" 				<label for=\"pred\">Prediction:</label>\n" + 
						" 				<input value=\"" + patient.getPrediction() + "\" id=\"pred\" name=\"pred\" type=\"text\"></input>\n" + 
						" 			</div>\n" + 
						" 			<div class=\"form-line-small\">\n" + 
						" 				<button class=\"delete\">Delete</button>\n" + 
						" 				<button class=\"predict\">Predict</button>\n" + 
						" 				<button class=\"save\">Save</button>\n" + 
						" 			</div>\n" + 
						" 		</form>");
				}
			}
		} else {
			response.getWriter().append("Not Authorized");
		}
	}

	/**
	 * If a post request was sent, we want to add a patient
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// make sure we're signed in
		if (request.getSession().getAttribute("signed_in") != null && (boolean)request.getSession().getAttribute("signed_in")) {
			// make sure 'add' parameter exists
			if (request.getParameter("add") != null) {
				String id = "";
				String result = "";
				String pred = "";
				String proteinsStr = "";
				
				// make sure we have all the other parameters
				
				if (request.getParameter("id") != null) {
					id = (String)request.getParameter("id");
				} else {
					response.getWriter().append("Please provide all the parameters.");
					return;
				}
				
				if (request.getParameter("result") != null) {
					result = (String)request.getParameter("result");
					
					if (result.isEmpty()) {
						result = "unknown";
					}
				} else {
					response.getWriter().append("Please provide all the parameters.");
					return;
				}
				
				if (request.getParameter("pred") != null) {
					pred = (String)request.getParameter("pred");
					
					if (pred.isEmpty()) {
						pred = "unknown";
					}
				} else {
					response.getWriter().append("Please provide all the parameters.");
					return;
				}
				
				if (request.getParameter("proteins") != null) {
					proteinsStr = (String)request.getParameter("proteins");
				} else {
					response.getWriter().append("Please provide all the parameters.");
					return;
				}
				
				// make sure we have the right number of proteins
				String[] proteinsStrArr = proteinsStr.split(",");
				if (proteinsStrArr.length != 4776) {
					response.getWriter().append("Not enough proteins.");
					return;
				}
				
				// make the connection and execute the insertion
				makeDBConnection();
				insertPatient(id, result, pred, proteinsStr);
				response.getWriter().append("Success");
				
			} else {
				response.getWriter().append("Please add the 'add' parameter.");
			}
		} else {
			response.getWriter().append("Not Authorized");
		}
		
//		doGet(request, response);
	}
}
