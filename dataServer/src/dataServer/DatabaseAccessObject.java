package dataServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dataServer.database.DBConfig;
import dataServer.database.DBUtils;
import dataServer.database.dbobjects.KpiValues;

public class DatabaseAccessObject {
	
	DBUtils dBUtil = new DBUtils(new DBConfig("jdbc:hsqldb:file:db/", "", "sa", ""));
	String dbName = "proasense_hella";
	
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
	
	private ArrayList<String> prepareInsertQuery(ArrayList<KpiValues> buffer){
		// create insert query string

		String columnNames = "";
		String values = "";
		ArrayList<String> queryList = new ArrayList<>();
		//int id = getMaxId("kpi_values");

		for (KpiValues kpiValue : buffer) {
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
	
	public boolean insertBatchData(ArrayList<KpiValues> buffer){
		ArrayList<String> batchQuery = prepareInsertQuery(buffer);
		dBUtil.openConnection(dbName);
		dBUtil.processBatchQuery(batchQuery);
		dBUtil.closeConnection();
		return true;
	}
	
	public static void main(String[] args) {
		DatabaseAccessObject dAO = new DatabaseAccessObject();
		dAO.getNameId("machine", "KM1");
		dAO.getNameId("kpi", "");
	}
}
