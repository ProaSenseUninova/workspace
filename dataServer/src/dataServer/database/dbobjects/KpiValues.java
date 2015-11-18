package dataServer.database.dbobjects;
import java.sql.Timestamp;

public class KpiValues extends KpiDataObject {
	public Timestamp timestamp;
	public int value;
	public int machineId;
	public int productId;
	public String designation;
	public boolean goodPart;
	public int kpiId;
	
	public KpiValues() {
		super("kpi_values");
	}
}