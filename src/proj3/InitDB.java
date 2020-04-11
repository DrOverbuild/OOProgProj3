// Jasper Reddin
// OOP with Java - Spring 2020
// Mark Doderer

package proj3;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Small servlet to initialize the database with the right table structure. This will never be on a production web server.
 */
@WebServlet("/InitDB")
public class InitDB extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	public final static String CONN_URL = "jdbc:mysql://localhost:3306/patients?user=reader&password=readDBdat@";
	
	Connection conn = null;
	
    public InitDB() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// set up the connection
		makeDBConnection();
		
		if (conn != null) {
			response.getWriter().append("successful connection");
		} else {
			response.getWriter().append("connection is null");

		}
		
		// build the table
		buildTables(response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	// establishes the database connection
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
	
	// builds the patients table
	protected void buildTables(HttpServletResponse response) throws IOException {
		if (conn == null) {
			return;
		}
		
		// table record structure:
		// |  id  |  result  |  prediction  |  protein1,protein2,...,protein4776  |
		//   int     varchar     varchar       bigger varchar
		
		String statement= "CREATE TABLE patients ("
                + "ID INT(11) NOT NULL,"
                + "RESULT VARCHAR(10) NOT NULL,"
                + "PRED VARChAR(10) NOT NULL,"
                + "PROTEINS VARCHAR(60000) NOT NULL,"
                + "PRIMARY KEY (ID))";
		
		// execute the statement
		try {
			PreparedStatement st = conn.prepareStatement(statement);
			st.execute();
		} catch (SQLException e) {
			response.getWriter().append("\nFailed to build table");
			e.printStackTrace();
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
