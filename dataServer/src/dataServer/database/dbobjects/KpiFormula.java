package dataServer.database.dbobjects;

import java.util.Arrays;

public class KpiFormula extends KpiDataObject {
	public int kpiId;
	public int term1KpiId;
	public int term1SensorId;
	public String operator1;
	public int term2KpiId;
	public int term2SensorId;
	public String operator2;
	public int term3KpiId;
	public int term3SensorId;
	public int criteria;
	
	public KpiFormula() {
		super("kpi_formula");
		super.columnsNames.addAll(Arrays.asList("kpi_id", "term1_kpi_id", "term1_sensor_id", "operator_1", "term2_kpi_id", "term2_sensor_id", 
				"operator_2", "term3_kpi_id", "term3_sensor_id","criteria"));
	}

	@Override
	public void loadContents(String[] contents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getColumnValue(String column) {
		Object columnObj = null;
		switch (column) {
			case "id": 				columnObj = super.id; 
									break;
			case "kpi_id": 			columnObj = kpiId;
							  		break;
			case "term1_kpi_id": 	columnObj = term1KpiId;
									break;
			case "term1_sensor_id":	columnObj = term1SensorId;
							  		break;
			case "operator_1": 		columnObj = operator1;
									break;
			case "term2_kpi_id":	columnObj = term2KpiId;
									break;
			case "term2_sensor_id": columnObj = term2SensorId;
							 		break;
			case "operator_2": 		columnObj = operator2;
									break;
			case "term3_kpi_id": 	columnObj = term3KpiId;
									break;
			case "term3_sensor_id": columnObj = term3SensorId;
									break;
			case "criteria": 		columnObj = criteria;
									break;
			default: 				break;
		}
		return columnObj;
	}
}
