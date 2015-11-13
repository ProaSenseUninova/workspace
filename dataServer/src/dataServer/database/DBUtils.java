package dataServer.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import dataServer.LoggingSystem;

public class DBUtils {

	/*
	getConnection();~
	query();
	close();
	*/
	
	private Connection dbConnection = null;
	LoggingSystem _log = LoggingSystem.getLog();
	
	public Connection getConnection() {
		try {
			if (dbConnection == null)
				dbConnection = DriverManager.getConnection("");
		} catch (SQLException e) {
		
		}
		return dbConnection;
	}
	
	public Connection getConnection(DBConfig dbConfig, String dbName) {
		try {
			if (dbConnection == null)
				dbConnection = DriverManager.getConnection(dbConfig.jdbcURL+dbName, dbConfig.userName, dbConfig.password);
		} catch (SQLException e) {
			
		}
		return dbConnection;
	}
	
	public boolean closeConnection(){
		try {
			if (dbConnection != null)
				dbConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static void main(String[] args) {
		DBUtils db = new DBUtils();
		DBConfig dbConfig = new DBConfig("jdbc:hsqldb:file:db/", "sa", "");
		Connection cn = db.getConnection(dbConfig, "proasense_hella");
		
		Statement s = null;
		try {
			s = cn.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String query = "SELECT * FROM \"kpi\" WHERE \"id\"=1";

		System.out.println("SQL Query: "+query);
		
		try {
			ResultSet r = s.executeQuery(query);
			System.out.println("SQL Query response: ");
			
	        ResultSetMetaData meta;
			
			String qm=null;
			meta = r.getMetaData();
			String str ="[";
	        int               colmax = meta.getColumnCount();
	        int               i;
	        Object            o = null;
			
	        for (; r.next(); ) {
	        	str=str+"{";
	            for (i = 0; i < colmax; ++i) {
	            	o = r.getObject(i+1);
	            	
	            	if(o!=null)
	            	{
	            		qm=o instanceof Integer  || o.equals(true)|| o.equals(false)? "":"\""; 
	            	}
	            	else
	            	{
	            		qm="";
	            	}
	            	str=str+"\""+meta.getColumnName(i+1)+"\":"+qm+o+qm;
	                if(i<colmax-1)
	                {
	                	str=str+",";
	                }
	                
	                // with 1 not 0
	                
	            }
	            System.out.println(i+":"+str);
	            str=str+"},";
	        }
			
			
			
			cn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
