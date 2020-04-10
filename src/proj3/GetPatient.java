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
	
	protected Patient loadPatient(String id) {
		if (conn == null) {
			return null;
		}
		
		try {
			PreparedStatement statement = conn.prepareStatement("SELECT id,result,pred FROM patients WHERE id = ?");
			int idInt = Integer.parseInt(id);
			statement.setInt(1, idInt);
			ResultSet rs = statement.executeQuery();
			
			if  (rs.last()) {
				String result = rs.getString(2);
				String pred = rs.getString(3);
				
				Patient patient = new Patient(result, pred, id, new double[]{}); // proteins are irrelevant right now
				
				return patient;
			}
		} catch (SQLException | NumberFormatException e) {
			
			e.printStackTrace();
			// TODO: handle exception
		}
		
		return null;
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
			if (request.getParameter("id") != null) {
				String id = (String)request.getParameter("id");
				makeDBConnection();
				Patient patient = loadPatient(id);
				
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
		doGet(request, response);
	}
}
