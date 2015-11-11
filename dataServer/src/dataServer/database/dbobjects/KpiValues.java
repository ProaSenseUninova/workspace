package dataServer.database.dbobjects;
import java.sql.Timestamp;

public class KpiValues extends KpiDataObject {
	public Timestamp timestamp;
	public int value;
	public int kpiId;
	public int machineId;
	public int mouldId;
	public int productId;
	
	public KpiValues() {
		super("kpi_values");
	}
}