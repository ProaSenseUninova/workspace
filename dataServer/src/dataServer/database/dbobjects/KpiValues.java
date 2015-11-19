package dataServer.database.dbobjects;
import java.sql.Timestamp;
import java.util.Arrays;

public class KpiValues extends KpiDataObject {
	public Timestamp timestamp = null;
	public int value = 0;
	public int machineId = 0;
	public int productId = 0;
	public String designation = null;
	public boolean goodPart = true;
	public int kpiId = 0;
	
	public KpiValues() {
		super("kpi_values");
		super.columnsNames.addAll(Arrays.asList("timestamp", "value", "machine_id", "product_id", "designation", "good_part", "kpi_id"));
	}
	
	public Object getColumnValue(String column){
		Object columnObj = null;
		switch (column) {
			case "id": 			columnObj = super.id; 
								break;
			case "timestamp": 	columnObj = "'"+timestamp.toString()+"'";
							  	break;
			case "value": 		columnObj = value;
						  		break;
			case "machine_id": 	columnObj = machineId;
							  	break;
			case "product_id": 	columnObj = productId;
			  				  	break;
			case "designation": columnObj = designation;
								break;
			case "good_part": 	columnObj = goodPart;
							 	break;
			case "kpi_id": 		columnObj = kpiId;
						  		break;
			default: 			break;
		}
		return columnObj;
	}
}