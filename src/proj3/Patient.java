package proj3;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Patient
 */
@WebServlet("/Patient")
public class Patient extends HttpServlet {
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
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Patient() {
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
				// load patient by id
				// fill out form data
				// write to response
				
				response.getWriter().append("<form><div class=\"form-line-small\"><label>ID:</label><input type=\"text\"></div></form>");
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
