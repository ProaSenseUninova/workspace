package dataServer.database.dbobjects;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import dataServer.database.enums.TableValueType;

public class ResultTableElement extends KpiDataObject {
	
	public String[] columnValues;
	
	TableValueType tableValueType; 
	
	public ResultTableElement(String tableName) {
		super(tableName);
		// TODO Auto-generated constructor stub
	}
	
	public ResultTableElement(TableValueType tVT, Integer colN) {
		this("");
		tableValueType = tVT;
		columnValues = new String[colN];
	}

	@Override
	public void loadContents(String[] contents) {
		columnValues = contents;
	}

	@Override
	public Object getColumnValue(String column) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object toJSonObject(){
		Object jsonObject = new JSONObject();
		JSONParser parser = new JSONParser();
		String temp = "[";
		for (int i = 0; i<columnValues.length;i++) {
			temp += "\""+columnValues[i]+"\",";
		}
		temp = temp.substring(0, temp.length()-1);
		temp +="]";
		try {
			jsonObject = parser.parse(temp);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public Object toJSonObject(Integer column){
		Object jsonObject = new JSONObject();
		JSONParser parser = new JSONParser();
		String temp = "";
		temp += "\""+columnValues[column-1]+"\"";
//		temp = temp.substring(0, temp.length()-1);
		temp +="";
		try {
			jsonObject = parser.parse(temp);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	


}
