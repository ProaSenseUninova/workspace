package dataServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dataServer.database.DBConfig;
import dataServer.database.DBUtils;
import dataServer.database.dbobjects.KpiDataObject;

public class DatabaseAccessObject {
	
	String dbName = "proasense_hella";
//	String dbName = "dbTest";
	DBUtils dBUtil = new DBUtils(new DBConfig("jdbc:hsqldb:file:db/", dbName, "SA", ""));
//	DBUtils dBUtil = new DBUtils(new DBConfig("jdbc:hsqldb:file:dbTest/", dbName, "SA", ""));
	LoggingSystem log = LoggingSystem.getLog();
	String logFileName = "daoTestFile.log";
		
	public int getNameId(String tableName, String valueName){
		int id=0;
		dBUtil.openConnection(dbName);
		
		String query = "SELECT \"id\" FROM \""+tableName+"\" WHERE \"name\"='"+valueName+"';";
		
		ResultSet queryResult = dBUtil.processQuery(query);
		
        try {
			for (; queryResult.next(); ) {
				id = (int)queryResult.getObject(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		dBUtil.closeConnection();
		return id;
	}
	
	
	public int getForeignKeyId(String tableName, String foreignKeyName, String valueName){
		Integer id=0;
		dBUtil.openConnection(dbName);
		
		String query = "SELECT \""+foreignKeyName+"\" FROM \""+tableName+"\" WHERE \"name\"='"+valueName+"';";
		
		ResultSet queryResult = dBUtil.processQuery(query);
		
	    try {
			for (; queryResult.next(); ) {
				id = (Integer)queryResult.getObject(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			log.saveToFile("getForeignKeyId exception: "+e );
			e.printStackTrace();
		}		
		
		dBUtil.closeConnection();
		return id;
	}

	public int getMaxId(String tableName) {
		Integer id=0;
		dBUtil.openConnection(dbName);
		
		String query = "SELECT MAX(\"id\") FROM \""+tableName+"\";";
		
		ResultSet queryResult = dBUtil.processQuery(query);
		
        try {
        	if (queryResult.next()) {
	        	id = (Integer)queryResult.getObject(1);

	        	if (id == null) {
	            	id = 0;
	            }
        	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		dBUtil.closeConnection();
		return id;
	}
	
	private ArrayList<String> prepareInsertQuery(ArrayList<KpiDataObject> buffer){
		// create insert query string

		String columnNames = "";
		String values = "";
		ArrayList<String> queryList = new ArrayList<>();
		//int id = getMaxId("kpi_values");

		for (KpiDataObject kpiValue : buffer) {
//			columnNames = "\"id\", ";
//			values = (++id) + ", ";
			columnNames = "";
			values = "";
			for (String col : kpiValue.columnsNames){
				columnNames += "\""+col+"\", ";
				values += kpiValue.getColumnValue(col) + ", ";
			}
			columnNames = columnNames.substring(0, columnNames.length()-2);
			values = values.substring(0, values.length()-2);
			queryList.add("INSERT INTO \""+kpiValue.tableName+"\" ("+columnNames+") VALUES ("+values+");");  
		}
		
		
		return queryList;
	}
	
	public boolean insertBatchData(ArrayList<KpiDataObject> buffer){
		ArrayList<String> batchQuery = prepareInsertQuery(buffer);
		dBUtil.openConnection(dbName);
		dBUtil.processBatchQuery(batchQuery);
		dBUtil.closeConnection();
		return true;
	}
	
	
	public void ScrapRateTotalPerMachine(){
		String ScrapRateTotalPerMachine = "SELECT ((BadParts.cnt2)/(BadParts.cnt2+GoodParts.cnt1))*100 ScrapRate, GoodParts.cnt1, GoodParts.Machine1, BadParts.cnt2 "
				  +"FROM (SELECT Count(*) cnt1, mc.\"name\" Machine1, kv.\"good_part\" Part1 "
						+"FROM \"kpi_values\" kv, \"machine\" mc "
						+"WHERE kv.\"machine_id\" = mc.\"id\"  "
						+"AND kv.\"good_part\" = 'true' "
						+"AND CAST(kv.\"timestamp\" AS TIME) BETWEEN TIME'00:00:00' AND TIME'23:59:59' "
						+"AND CAST(kv.\"timestamp\" AS DATE) BETWEEN DATE'2014-10-01' AND DATE'2015-04-05' "
						+"GROUP BY Machine1, Part1) AS GoodParts "
				  +"INNER JOIN (SELECT Count(*) cnt2, mc.\"name\" Machine2, kv.\"good_part\" Part2 FROM \"kpi_values\" kv, \"machine\" mc "
						 	  +"WHERE kv.\"machine_id\" = mc.\"id\"  "
							  +"AND kv.\"good_part\" = 'false' "
							  +"AND CAST(kv.\"timestamp\" AS TIME) BETWEEN TIME'00:00:00' AND TIME'23:59:59' "
							  +"AND CAST(kv.\"timestamp\" AS DATE) BETWEEN DATE'2014-10-01' AND DATE'2015-04-05' "
							  +"GROUP BY Machine2, Part2) AS BadParts "
				  +"ON Machine1 = Machine2; ";
		
		
	//	Integer id=0;
		dBUtil.openConnection(dbName);
		log.saveToFile("Connection opened", logFileName);
		
		ResultSet queryResult = dBUtil.processQuery(ScrapRateTotalPerMachine);
		
	  try {
	  	for (;queryResult.next();)
	  	{
	  		String s = "";
	      	s += "<Machine:"+queryResult.getObject(3).toString()+">";
	      	s += "<Good Parts:"+queryResult.getObject(2).toString()+">";
	      	s += "<Scrapped Parts:"+queryResult.getObject(4).toString()+">";
	      	s += "<ScrapRate:"+queryResult.getObject(1).toString()+">";
	
	      	log.saveToFile(s, logFileName);
	  	}
	  	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	  
	  log.saveToFile("Connection closed", logFileName);
	  dBUtil.closeConnection();
		
	}
	
	
	/*
	 *
	 
String query = "" 
			+"SELECT ((BadParts.cnt2)/(BadParts.cnt2+GoodParts.cnt1))*100 ScrapRate, GoodParts.cnt1, GoodParts.Machine1, BadParts.cnt2 "
			+"FROM (SELECT Count(*) cnt1, mc.\"name\" Machine1, kv.\"good_part\" Part1 "
					  +"FROM \"kpi_values\" kv, \"machine\" mc "
				 	  +"WHERE kv.\"machine_id\" = mc.\"id\"  "
					  +"AND kv.\"good_part\" = 'true' "
					  +"AND CAST(kv.\"timestamp\" AS TIME) BETWEEN TIME'00:00:00' AND TIME'23:59:59' "
					  +"AND CAST(kv.\"timestamp\" AS DATE) BETWEEN DATE'2014-10-01' AND DATE'2014-10-01' "
					  +"GROUP BY Machine1, Part1) AS GoodParts "
				+"INNER JOIN (SELECT Count(*) cnt2, mc.\"name\" Machine2, kv.\"good_part\" Part2 FROM \"kpi_values\" kv, \"machine\" mc "
				 	  +"WHERE kv.\"machine_id\" = mc.\"id\"  "
					  +"AND kv.\"good_part\" = 'false' "
					  +"AND CAST(kv.\"timestamp\" AS TIME) BETWEEN TIME'00:00:00' AND TIME'23:59:59' "
					  +"AND CAST(kv.\"timestamp\" AS DATE) BETWEEN DATE'2014-10-01' AND DATE'2014-10-01' "
					  +"GROUP BY Machine2, Part2) AS BadParts "
				+"ON Machine1 = Machine2; ";
	 
	 */
	
	
	
	public static void main(String[] args) {
		DatabaseAccessObject dAO = new DatabaseAccessObject();
		LoggingSystem log = LoggingSystem.getLog();
		String logFileName = "daoTestFile.log";
		

		String query2 = "SELECT Count(*) cnt1, mc.\"name\" Machine1 "
					 + "FROM \"kpi_values\" kv "
					 + "LEFT OUTER JOIN \"product\" mc ON kv.\"product_id\" = mc.\"id\" "
					 + "WHERE kv.\"good_part\" = 'false' "
					 + "AND CAST(kv.\"timestamp\" AS TIME) BETWEEN TIME'00:00:00' AND TIME'23:59:59' "
					 + "AND CAST(kv.\"timestamp\" AS DATE) BETWEEN DATE'2014-10-01' AND DATE'2015-05-03' "		  
					 + "GROUP BY Machine1; ";
		
		String ScrapRateTotalPerMachine = "SELECT ((BadParts.cnt2)/(BadParts.cnt2+GoodParts.cnt1))*100 ScrapRate, GoodParts.cnt1, GoodParts.Machine1, BadParts.cnt2 "
					  +"FROM (SELECT Count(*) cnt1, mc.\"name\" Machine1, kv.\"good_part\" Part1 "
							+"FROM \"kpi_values\" kv, \"machine\" mc "
							+"WHERE kv.\"machine_id\" = mc.\"id\"  "
							+"AND kv.\"good_part\" = 'true' "
							+"AND CAST(kv.\"timestamp\" AS TIME) BETWEEN TIME'00:00:00' AND TIME'23:59:59' "
							+"AND CAST(kv.\"timestamp\" AS DATE) BETWEEN DATE'2014-10-01' AND DATE'2015-04-05' "
							+"GROUP BY Machine1, Part1) AS GoodParts "
					  +"INNER JOIN (SELECT Count(*) cnt2, mc.\"name\" Machine2, kv.\"good_part\" Part2 FROM \"kpi_values\" kv, \"machine\" mc "
							 	  +"WHERE kv.\"machine_id\" = mc.\"id\"  "
								  +"AND kv.\"good_part\" = 'false' "
								  +"AND CAST(kv.\"timestamp\" AS TIME) BETWEEN TIME'00:00:00' AND TIME'23:59:59' "
								  +"AND CAST(kv.\"timestamp\" AS DATE) BETWEEN DATE'2014-10-01' AND DATE'2015-04-05' "
								  +"GROUP BY Machine2, Part2) AS BadParts "
					  +"ON Machine1 = Machine2; ";
		
		
//		Integer id=0;
		dAO.dBUtil.openConnection(dAO.dbName);
		log.saveToFile("Connection opened", logFileName);
		
		ResultSet queryResult = dAO.dBUtil.processQuery(ScrapRateTotalPerMachine);
		
        try {
        	for (;queryResult.next();)
        	{
        		String s = "";
	        	s += "<Machine:"+queryResult.getObject(3).toString()+">";
	        	s += "<Good Parts:"+queryResult.getObject(2).toString()+">";
	        	s += "<Scrapped Parts:"+queryResult.getObject(4).toString()+">";
	        	s += "<ScrapRate:"+queryResult.getObject(1).toString()+">";

	        	log.saveToFile(s, logFileName);
        	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        log.saveToFile("Connection closed", logFileName);
        dAO.dBUtil.closeConnection();
		
		
		
		
		dAO.getNameId("machine", "KM1");
		dAO.getNameId("kpi", "");
	}
}
