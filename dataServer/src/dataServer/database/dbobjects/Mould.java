package dataServer.database.dbobjects;

public class Mould extends KpiDataObject {
	public String name;
	public String code;
	public int cycle;
	public int productId;

	public Mould() {
		super("mould");
	}
}
