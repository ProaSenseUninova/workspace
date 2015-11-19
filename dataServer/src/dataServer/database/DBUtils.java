package dataServer.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import dataServer.LoggingSystem;

public class DBUtils {

	private Connection dbConnection = null;
	LoggingSystem _log = LoggingSystem.getLog();
	private DBConfig dbConfig = null;
	private String dbName;
	
	public DBUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public DBUtils(DBConfig dbC) {
		this.dbConfig = dbC;
	}
	
	public Connection getConnection() {
		try {
			if ((dbConnection == null) || (dbConnection.isClosed())) {
				throw new SQLException("Database not conected.");
				//dbConnection = DriverManager.getConnection("");
			}
		} catch (SQLException e) {
		
		}
		return dbConnection;
	}
	
	public Connection getConnection(DBConfig dbConfig, String dbName) {
		try {
			if ( (dbConnection == null) || (dbConnection.isClosed()) ){
				this.dbName = dbName;
				this.dbConfig = new DBConfig(dbConfig.jdbcURL, dbName, dbConfig.userName, dbConfig.password);
				dbConnection = DriverManager.getConnection(this.dbConfig.jdbcURL+dbName, this.dbConfig.userName, this.dbConfig.password);
			}
		} catch (SQLException e) {
			
		}
		return dbConnection;
	}
	
	public boolean openConnection(DBConfig dbConfig, String dbName) {

		boolean result = false;
		try {
			if ((dbConnection == null) || (dbConnection.isClosed())){
				dbConnection = getConnection(dbConfig, dbName);
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean openConnection(String dbName) {
		boolean result = false;
		try {
			if ( ((dbConnection == null) || (dbConnection.isClosed())) || (dbConfig == null)){
				dbConnection = getConnection(dbConfig, dbName);
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean closeConnection(){
		try {
			if ( (dbConnection != null) || (!dbConnection.isClosed()) )
				dbConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public ResultSet processQuery(String query){
		Statement s;
		ResultSet r = null;
		try {
			s = dbConnection.createStatement();
			r = s.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return r;
	}
	
	public boolean processBatchQuery(ArrayList<String> batchQuery){
		Statement s;
//		ResultSet r = null;

		try {
			s = dbConnection.createStatement();
			for (String query : batchQuery) {
				s.addBatch(query);
			}
			
			int[] n = s.executeBatch();
//			if (n >= 0){
//				
//			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean insertData(String tableName, Object data){
		try {
			//String str="";
			//JSONParser parser = new JSONParser();
			
			Object data1 = "{\"data\":[{\"varY\":1,\"varX\":1,\"value\":9},"
									+ "{\"varY\":1,\"varY\":2,\"value\":78},"
									+ "{\"varY\":1,\"varY\":3,\"value\":123}, "
									+ "{\"varY\":1,\"varY\":4,\"value\":114},"
									+ "{\"varY\":1,\"varY\":5,\"value\":8},"
									+ "{\"varY\":1,\"varY\":6,\"value\":12},"
									+ "{\"varY\":2,\"varY\":1,\"value\":19},"
									+ "{\"varY\":2,\"varY\":2,\"value\":58},"
									+ "{\"varY\":2,\"varY\":3,\"value\":15},"
									+ "{\"varY\":2,\"varY\":4,\"value\":132},"
									+ "{\"varY\":2,\"varY\":5,\"value\":5},"
									+ "{\"varY\":2,\"varY\":6,\"value\":32},"
									+ "{\"varY\":3,\"varY\":1,\"value\":10},"
									+ "{\"varY\":3,\"varY\":2,\"value\":92},"
									+ "{\"varY\":3,\"varY\":3,\"value\":35},"
									+ "{\"varY\":3,\"varY\":4,\"value\":72},"
									+ "{\"varY\":3,\"varY\":5,\"value\":38},"
									+ "{\"varY\":3,\"varY\":6,\"value\":88}]}";
							
//			Object data2 = "{\"data\":[{\"varY\":1,\"varX\":1,\"value\":9}," + "{\"varY\":1,\"varY\":2,\"value\":78}]}";
			String temp = data1.toString();
			JSONObject data1Obj = (JSONObject)new JSONParser().parse(data1.toString());
			JSONObject data1Value = (JSONObject)JSONValue.parse(data1.toString());
			JSONArray dataArray = (JSONArray)data1Value.get("data");

			JSONObject value = (JSONObject)dataArray.get(0);
			JSONObject value1 = (JSONObject)dataArray.get(0);
			int sz = dataArray.size();
			
			dataArray.add(value);
			dataArray.add(value1);
			
			JSONObject objTest = new JSONObject();
			objTest.put("data", dataArray);
			
			JSONObject objTestInside = new JSONObject();
			objTest.put("xLabel", objTestInside);
			
			
			
			JSONArray parsedDataArray =  (JSONArray)data1;
			JSONObject parsedData =  (JSONObject)data1;
			JSONObject obj=(JSONObject)parsedData.get(0);
			//obj=(JSONObject)parsedData.get(0);
			Object[] propertiesVect = obj.keySet().toArray();
			
			// Build Column string
			String properties="(";
			
			for(int i =0;i<propertiesVect.length;i++)
			{
				properties=properties+"\""+propertiesVect[i]+"\",";
			}
			
			properties=properties.substring(0,properties.length()-1)+")";
			
			// Build Values String
			String values = "(";

			// Build Query String
			String query="INSERT INTO \""+tableName+"\" "+properties+" VALUES ";
			
			for(int i=0;i<parsedData.size();i++)
			{
				obj=(JSONObject)parsedData.get(i);
				query=query+"(";
				
				for(int j=0;j<propertiesVect.length;j++)
				{
					properties=properties+"\""+propertiesVect[i]+"\",";
					
					Object val = obj.get(propertiesVect[j]);
					
					query=query+(val instanceof String?"'"+val+"'":val);
					
					values += (val instanceof String?"'"+val+"'":val);
					
					if(j<propertiesVect.length-1)
					{
						query=query+",";
						properties=",";
						values +=",";
					}
					
				}
				
				query=query+")";
				properties=")";
				values+=")";
				
				if(i<parsedData.size()-1)
				{
					query=query+",";
				}
			}
			
			// Connection c = dbConnection.getConnection(dbConfig.jdbcURL+dbName, dbConfig.userName, dbConfig.password);
			// writeLogMsg("SQL Query: "+query);
			Integer insRows = null;
			String insertId="";
			/*String*/ query="INSERT INTO \""+tableName+"\" "+properties+" VALUES "+values;
			openConnection(dbConfig, "proasense_hella");
			PreparedStatement s =  getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			insRows = s.executeUpdate();

			// TODO: Does the next lines should be in a log method on its own??
			insertId="[";
			if(insRows>0)
			{
				ResultSet generatedKeys = s.getGeneratedKeys();	
				while(generatedKeys.next())
				{
					insertId=insertId+generatedKeys.getInt(1)+",";
				}
				insertId=insertId.substring(0,insertId.length()-1);
			}
			insertId=insertId+"]";
			//response.getWriter().println("{\"succeeded\":true,\"result\":\""+insRows+" records added\",\"insertId\":"+insertId+"}");
			//writeLogMsg("Response at: "+remoteAddress);
			closeConnection();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true; 
	}
	
	
	public static void main(String[] args) {
		DBUtils db = new DBUtils();

		
		db.insertData("", new Object());
		
		DBConfig dbConfig = new DBConfig("jdbc:hsqldb:file:db/", "sa", "");
		
		db.openConnection(dbConfig, "proasense_hella");
		
		
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
