package dataServer.database.dbobjects;

import java.sql.Timestamp;

public class KpiTarget extends KpiDataObject {
	public int upperBound;
	public int lowerBound;
	public int kpiId;
	public int machineId;
	public int mouldId;
	public int productId;

	public KpiTarget() {
		super("kpi_target");
	}
}
