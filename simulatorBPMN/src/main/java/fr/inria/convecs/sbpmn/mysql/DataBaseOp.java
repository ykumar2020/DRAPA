package fr.inria.convecs.sbpmn.mysql;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataBaseOp {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "root";
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	public void dropDatabase() {

		Connection conn = null;
		Statement stmt = null;

		try {
			// STEP 2: Register JDBC driver
			//Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			//System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			//System.out.println("Connected database successfully...");

			// STEP 4: Execute a query
			//System.out.println("Deleting database...");
			stmt = conn.createStatement();

			String sql = "DROP DATABASE IF EXISTS activiti2";
			stmt.executeUpdate(sql);
			//System.out.println("Database deleted successfully...");
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		//System.out.println("Goodbye!");
		logger.info("Database deleted successfully.");
	}// end main

	public void createDatabase() {

		Connection conn = null;
		Statement stmt = null;

		try {
			// STEP 2: Register JDBC driver
			//Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			//System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			//System.out.println("Connected database successfully...");

			// STEP 4: Execute a query
			//System.out.println("Creating database...");
			stmt = conn.createStatement();

			String sql = "CREATE DATABASE IF NOT EXISTS activiti2";

			stmt.executeUpdate(sql);
			//System.out.println("Database create successfully...");
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		//System.out.println("Goodbye!");
		logger.info("Database create successfully.");
	}// end main
	
	
	public static void main (String[] args) {
		DataBaseOp db = new DataBaseOp();
		
		db.dropDatabase();
		
		db.createDatabase();
	}

}
