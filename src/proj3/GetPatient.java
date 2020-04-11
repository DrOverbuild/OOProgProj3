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

/**
 * Servlet implementation class Patient
 */
@WebServlet("/GetPatient")
public class GetPatient extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	public final static String CONN_URL = "jdbc:mysql://localhost:3306/patients?user=reader&password=readDBdat@";

	public Connection conn = null;
	
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
	
	protected Patient loadPatient(String id, boolean includeProteins) {
		if (conn == null) {
			return null;
		}
		
		try {
			
			String proteinsAddition = "";
			
			if (includeProteins) {
				proteinsAddition = ",proteins";
			}
			
			PreparedStatement statement = conn.prepareStatement("SELECT id,result,pred" + proteinsAddition + " FROM patients WHERE id = ?");
			int idInt = Integer.parseInt(id);
			statement.setInt(1, idInt);
			ResultSet rs = statement.executeQuery();
			
			if  (rs.last()) {
				String result = rs.getString(2);
				String pred = rs.getString(3);
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
			// TODO: handle exception
		}
		
		return null;
	}
	
	protected void updatePatient(Patient p) {
		if (conn == null) {
			return;
		}
		
		try {
			PreparedStatement st = conn.prepareStatement("UPDATE patients SET result = ?, pred = ? WHERE id = ?");
			st.setString(1, p.getTreatmentResults());
			st.setString(2, p.getPrediction());
			
			int idInt = Integer.parseInt(p.getPatientID());
			
			st.setInt(3, idInt);
			
			st.executeUpdate();
		} catch (SQLException e) {
			// TODO: handle exception
		}
	}
	
	protected void deletePatient(String id) {
		if (conn == null) {
			return;
		}
		
		try {
			PreparedStatement statement = conn.prepareStatement("DELETE FROM patients WHERE id = ?");
			int idInt = Integer.parseInt(id);
			statement.setInt(1, idInt);
			
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO: handle exception
		}
	}
	
	protected void insertPatient(String id, String result, String pred, String proteins) {
		if (conn == null) {
			return;
		}
		
		try {
			PreparedStatement statement = conn.prepareStatement("INSERT INTO patients (id, result, pred, proteins) VALUES (?, ?, ?, ?)");
			statement.setString(1, id);
			statement.setString(2, result);
			statement.setString(3, pred);
			statement.setString(4, proteins);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPatient() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (request.getSession().getAttribute("signed_in") != null && (boolean)request.getSession().getAttribute("signed_in")) {
			
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
			
			makeDBConnection();
			String id = "";
			
			if (request.getParameter("id") != null) {
				id = (String)request.getParameter("id");
			} else {
				response.getWriter().append("Please provide patient id.");
				return;
			}
			
			if (request.getParameter("predict") != null) {
				Patient patient = loadPatient(id, true);
				patient.setPrediction(patientpredictor.Predictor.predict(patient));
				updatePatient(patient);
			} else if (request.getParameter("delete") != null) {
				deletePatient(id);
			} else if (request.getParameter("save") != null) {
				String result;
				String pred;
				
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
				
				
				Patient patient = new Patient(result, pred, id, new double[]{});
				updatePatient(patient);
			} else {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		if (request.getSession().getAttribute("signed_in") != null && (boolean)request.getSession().getAttribute("signed_in")) {
			if (request.getParameter("add") != null) {
				String id = "";
				String result = "";
				String pred = "";
				String proteinsStr = "";
				
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
				
				
				String[] proteinsStrArr = proteinsStr.split(",");
				if (proteinsStrArr.length != 4776) {
					response.getWriter().append("Not enough proteins.");
					return;
				}
				
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
