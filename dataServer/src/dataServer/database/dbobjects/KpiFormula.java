package dataServer.database.dbobjects;

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
	}
}
