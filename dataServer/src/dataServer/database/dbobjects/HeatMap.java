package dataServer.database.dbobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import dataServer.database.enums.SamplingInterval;
import dataServer.database.enums.TableValueType;

public class HeatMap extends ResultTable {
	public ArrayList<String> varXUnique = new ArrayList<String>();
	public ArrayList<String> varYUnique = new ArrayList<String>();

	private String[][] _heatMap;

	private Object _heatMapXLabels;
	private Object _heatMapYLabels;
	
	private Integer kpiId;
	/* moment of the point requested in UI*/
	private Timestamp moment;

	/*name of the context element for which a heatmap is requested. ex: for machine id = 2, this field would have the name of the machine number 2*/
	private String contextElementName;
	
	/*context type to appear on the heatmap X axis*/
	private TableValueType varXtype;
	/*context type to appear on the heatmap X axis*/
	private TableValueType varYtype;

	private Timestamp startTime;
	private Timestamp endTime;
	
	

	public HeatMap(TableValueType type, SamplingInterval samplingInterval) {
		super(type, samplingInterval);
	}
	
	public String getHeatMapQueryString(){
		String query 	= "SELECT pd.\"name\" as \"varX\", sfht.\"name\" as \"varY\", COUNT(*) as \"value\""
				+ " FROM \"kpi_values\" kv"
				+ "	INNER JOIN \"product\" pd ON \"product_id\" = pd.\"id\""
				+ " INNER JOIN \"shift\" sfht ON \"shift_id\" = sfht.\"id\""
				+ " WHERE MONTH(CAST(kv.\"timestamp\" AS DATE)) = /* MONTH(CAST(kv.\"timestamp\" AS DATE)) */2"
				+ " AND \"machine_id\" = 1"
				+ " AND \"kpi_id\" = 3"
				+ " GROUP BY \"varX\", \"varY\""
				+ " ORDER BY \"varX\", \"varY\";";
		return query;
	}
	
	public void setHeatMapValues(){
		Integer xSize = varXUnique.size();
		Integer ySize = varYUnique.size();
		Integer varXPos = -1, varYPos = -1;
		String value = "";
		
		_heatMap = new String[xSize][ySize];
		
		// _heatMap matrix initialization
		for (int i=0;i<xSize;i++)
			for (int j=0;j<ySize;j++){
				_heatMap[i][j] = "null";
			}
		
		// populate _heatMap matrix 
		for (int i = 0; i<resultsRows.size();i++) {
			varXPos = varXUnique.indexOf(resultsRows.get(i).columnValues[0]);
			varYPos = varYUnique.indexOf(resultsRows.get(i).columnValues[1]);
			value = resultsRows.get(i).columnValues[2];
			
			_heatMap[varXPos][varYPos] = value;
		}
	}
	
	public void setHeatMapLabels() {
		setHeatMapXLabels(varXUnique);
		setHeatMapYLabels(varYUnique);
	}

	public Object getHeatMapYLabels() {
		return _heatMapYLabels;
	}
	
	public void setHeatMapYLabels(ArrayList<String> labels) {
		_heatMapYLabels = toJsonObjectHeatMapLabels(labels);
	}

	public Object getHeatMapXLabels(){
		return _heatMapXLabels;
	}

	public void setHeatMapXLabels(ArrayList<String> labels) {
		_heatMapXLabels = toJsonObjectHeatMapLabels(labels);
	}
	
	public Object toJSonObjectHeatMap(){
		Object jsonObject = new JSONObject();
		JSONParser parser = new JSONParser();
		ArrayList<String> tempArrStr = new ArrayList<String>();

		for (int i=0;i<varXUnique.size();i++){
			for (int j=0;j<varYUnique.size();j++){
				tempArrStr.add("{\"varX\":"+(i+1)+", \"varY\":"+(j+1)+", \"value\":"+_heatMap[i][j]+"}");
			}
		}

		try {
			jsonObject = parser.parse(tempArrStr.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	private Object toJsonObjectHeatMapLabels(ArrayList<String> labels){
		Object jsonObject = null;
		JSONParser parser = new JSONParser();
		
		String[] x = new String[labels.size()];
		for (int i=0;i<labels.size();i++){
			x[i] = "\""+labels.get(i)+"\"";
		}
		
		try {
			jsonObject = parser.parse(Arrays.toString(x));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}

}
