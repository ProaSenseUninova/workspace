package dataServer;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.hsqldb.result.ResultMetaData;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import dataServer.database.DBConfig;
import dataServer.database.DBUtils;
import dataServer.database.dbobjects.KpiDataObject;
import dataServer.database.dbobjects.ResultTable;
import dataServer.database.dbobjects.ResultTableElement;
import dataServer.database.enums.SamplingInterval;
import dataServer.database.enums.TableValueType;

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
	
	
	public Object getData(Integer kpiId, TableValueType type, SamplingInterval granularity, Timestamp startTime, Timestamp endTime){
		Object result = null;
		JSONParser parser = new JSONParser();
		switch (kpiId){
			case 1:break;
			case 2:break;
			case 3:break;
			case 4: ArrayList<ResultTable> tempResultTable = getScrapRate(type, granularity, startTime, endTime);
				String tmp = "[";
				for (ResultTable rt : tempResultTable){
					tmp += rt.toJSonObject(rt.columnQty)+",";
				}
				tmp = tmp.substring(0, tmp.length()-1);
				tmp +="]";
				try {
					result = parser.parse(tmp);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
			case 5:break;
			case 6:break;
			case 7:break;
			default: break;
			
			
		}
		return result;
	}
	
	public ArrayList<ResultTable> getScrapRate(TableValueType type, SamplingInterval granularity, Timestamp startTime, Timestamp endTime){
		Integer numTableElements = (type.equals(TableValueType.GLOBAL))?1:getMaxId(type.toString().toLowerCase());
		ArrayList<ResultTable> alrt = new ArrayList<ResultTable>();
		for (int k = 1; k<=numTableElements;k++)
			alrt.add((type.equals(TableValueType.GLOBAL))?getOneScrapRate(type, granularity, startTime, endTime):getOneScrapRate(type, granularity, startTime, endTime, k));
		return alrt;
	}
	
	public ResultTable getOneScrapRate(TableValueType type, SamplingInterval granularity, Timestamp startTime, Timestamp endTime, Integer id){
		ResultTable resultTable = new ResultTable(type, granularity);
		String query = resultTable.getResultTableQueryString(id, startTime, endTime);
		
		dBUtil.openConnection(dbName);
		
		ResultSet queryResult = dBUtil.processQuery(query);
		
        try {
        	ResultSetMetaData rMD = queryResult.getMetaData();
        	Integer colN = rMD.getColumnCount();
        	resultTable.columnQty = colN;

        	ResultTableElement resultRow = new ResultTableElement(type, colN);
        	
        	for (; queryResult.next(); ) {

				for (int i = 0; i<rMD.getColumnCount(); i++) {
					resultRow.columnsNames.add(rMD.getColumnName(i+1));
					resultRow.columnValues[i] = queryResult.getObject(i+1).toString();
				}
//				resultRow.toJSonObject();

				resultTable.resultsRows.add(resultRow);
				resultRow = new ResultTableElement(type, colN);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		dBUtil.closeConnection();		
		return resultTable;
	}
	
	public ResultTable getOneScrapRate(TableValueType type, SamplingInterval granularity, Timestamp startTime, Timestamp endTime){
		ResultTable resultTable = new ResultTable(type, granularity);
		String query = resultTable.getResultTableQueryString(startTime, endTime);
		
		dBUtil.openConnection(dbName);
		
		ResultSet queryResult = dBUtil.processQuery(query);
		
        try {
        	ResultSetMetaData rMD = queryResult.getMetaData();
        	Integer colN = rMD.getColumnCount();
        	resultTable.columnQty = colN;
        	
        	ResultTableElement resultRow = new ResultTableElement(type, colN);
        	
        	for (; queryResult.next(); ) {

				for (int i = 0; i<rMD.getColumnCount(); i++) {
					resultRow.columnsNames.add(rMD.getColumnName(i+1));
					resultRow.columnValues[i] = queryResult.getObject(i+1).toString();
				}
//				resultRow.toJSonObject();

				resultTable.resultsRows.add(resultRow);
				resultRow = new ResultTableElement(type, colN);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		dBUtil.closeConnection();		
		return resultTable;
	}
	
	public void getScrapRateMachineDaily(Timestamp from, Timestamp to){}
	public void getScrapRateProductDaily(Timestamp from, Timestamp to){}
	
	public ResultTable getLast30DaysScrapRate(){
		ResultTable resultTable = new ResultTable();
		String query = resultTable.getLastNDays(30);
		
		dBUtil.openConnection(dbName);
		
		ResultSet queryResult = dBUtil.processQuery(query);
		
        try {
        	ResultSetMetaData rMD = queryResult.getMetaData();
        	Integer colN = rMD.getColumnCount();

        	ResultTableElement resultRow = new ResultTableElement(TableValueType.NONE, colN);
        	
        	resultRow.columnsNames = new ArrayList<String>();
        	resultRow.columnValues = new String[colN];
        	
        	for (; queryResult.next(); ) {

				for (int i = 0; i<rMD.getColumnCount(); i++) {
					resultRow.columnsNames.add(rMD.getColumnName(i+1));
					resultRow.columnValues[i] = queryResult.getObject(i+1).toString();
				}

				resultTable.resultsRows.add(resultRow);
				resultRow = new ResultTableElement(TableValueType.NONE, colN);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		dBUtil.closeConnection();		
		return resultTable;
	}
	
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
