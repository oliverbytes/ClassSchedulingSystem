package scheduling.system.classes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import scheduling.system.M;

public class Database{
	
	public Connection connection;
	public Statement statement;
	
	public void open(){ // open database connection
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver"); // setup ODBC driver
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			//connection = DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=dbScheduling.mdb;"); // connect to database
			connection = DriverManager.getConnection("jdbc:odbc:db"); // connect to database
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			e.printStackTrace();
			//M.messageBox("There are some database errors.\n\n ERRORS: " + e);
		}
	}
	
	public void close(){ // close the database connection
		try {connection.close();} catch (SQLException e) {e.printStackTrace();}
	}
}