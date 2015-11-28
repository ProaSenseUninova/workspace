package dataServer.database.dbobjects;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import dataServer.database.enums.SamplingInterval;
import dataServer.database.enums.TableValueType;

public class ResultTable {
	SamplingInterval samplingInterval = SamplingInterval.NONE;
	TableValueType tableVT = TableValueType.NONE;
	
	String partsTableAlias = "";
	String scrappedPartsTableAlias = "";
	String typeTableAlias = "";
	String typeTableName = "";
	String partsTableName = "";
	String scrappedPartsTableName = "";
	
	public ArrayList<ResultTableElement> resultsRows = new ArrayList<ResultTableElement>();
			
	public ResultTable(TableValueType tVT, SamplingInterval sI) {
		tableVT = tVT;
		configureTable(tVT);
		samplingInterval = sI;
		
		partsTableName = tVT.toString().toLowerCase()
				+ "_parts_"+getSamplingIntervalAlias(sI);
		
		scrappedPartsTableName = tVT.toString().toLowerCase()
				+ "_scrapped_parts_"+getSamplingIntervalAlias(sI);
	}
	
	
	private void configureTable(TableValueType tableVT){
		switch (tableVT){
		case MACHINE: partsTableAlias = "mp";
					  scrappedPartsTableAlias = "ms";
					  typeTableAlias = "MC";
					  typeTableName = "MACHINE";
		  break;
		case PRODUCT: partsTableAlias = "pp";
					  scrappedPartsTableAlias = "ps";
					  typeTableAlias = "PRD";
					  typeTableName = "PRODUCT";
					  break;
		case MOULD:
			break;
		case NONE:
			break;
		case KPI:
			break;
		case KPI_AGG_TYE:
			break;
		case KPI_FORMULA:
			break;
		case KPI_TARGET:
			break;
		case KPI_VALUES:
			break;
		case SENSOR:
			break;
		case SHIFT:
			break;
		default:
			break;
		}
	}
	
	private String getSamplingIntervalAlias(SamplingInterval sI){
		String result = "";
		switch (sI){
		case DAYLY: result = "per_day";
			break;
		case HOURLY: result = "per_hour";
			break;
		case MINUTELY: result = "per_minute";
			break;
		case MONTHLY: result = "per_month";
			break;
		case NONE: result = "";
			break;
		case WEEKLY: result = "per_week";
			break;
		case YEARLY:result = "per_year";
			break;
		default:
			break;
		
		}
		
		return result;
	}
	
	public String getResultTableQueryString(Integer id){
		String query = "SELECT "+partsTableAlias+"."+typeTableName+", "
				+ ""+partsTableAlias+".HOUR"+typeTableAlias+", "
				+ ""+partsTableAlias+".DATE"+typeTableAlias+", "+partsTableAlias+".COUNT"+typeTableAlias+", "+scrappedPartsTableAlias+".SCR"+typeTableAlias+", ("+scrappedPartsTableAlias+".SCR"+typeTableAlias+"/"+partsTableAlias+".COUNT"+typeTableAlias+") as ScrapRate "
				+ "FROM \""+partsTableName+"\" "+partsTableAlias+" "
				+ "INNER JOIN \""+scrappedPartsTableName+"\" "+scrappedPartsTableAlias+" "
				+ "ON "+partsTableAlias+"."+typeTableName+"="+scrappedPartsTableAlias+"."+typeTableName+" "
				+ "	AND "+partsTableAlias+".HOUR"+typeTableAlias+"="+scrappedPartsTableAlias+".HOUR"+typeTableAlias+" " 
				+ "	AND "+partsTableAlias+".DATE"+typeTableAlias+"="+scrappedPartsTableAlias+".DATE"+typeTableAlias+" "
				+ "INNER JOIN \""+typeTableName.toLowerCase()+"\" "+typeTableAlias.toLowerCase()+" "
				+ "ON "+partsTableAlias+"."+typeTableName+" = "+typeTableAlias.toLowerCase()+".\"name\" "
				+ "WHERE "+typeTableAlias.toLowerCase()+".\"id\" = '"+id+"' "
				+ "ORDER BY "+partsTableAlias+".DATE"+typeTableAlias+", "+partsTableAlias+".HOUR"+typeTableAlias+";";
		  
		return query;
	}
	
	private String getTableValueType(){
		return tableVT.toString();
	}
	
	public Object toJSonObject(){
		Object jsonObject = new JSONObject();
		JSONParser parser = new JSONParser();
		String temp = "[";
		for (int i = 0; i<resultsRows.size();i++) {
			temp += resultsRows.get(i).toJSonObject()+",";
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
		String temp = "[";
		for (int i = 0; i<resultsRows.size();i++) {
			temp += resultsRows.get(i).toJSonObject(column)+",";
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
	
	
}
