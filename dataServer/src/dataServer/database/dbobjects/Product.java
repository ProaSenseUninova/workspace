package dataServer.database.dbobjects;

public class Product extends KpiDataObject {
	public String name;
	public String code;
	
	public Product() {
		super("product");
	}
}
