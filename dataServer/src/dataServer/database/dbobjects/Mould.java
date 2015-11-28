package dataServer.database.dbobjects;

import java.util.Arrays;

public class Mould extends KpiDataObject {
	public int productId;
	public String name;
	public String code;
	public int cycle;

	public Mould() {
		super("mould");
		super.columnsNames.addAll(Arrays.asList("product_id", "name", "code", "cycle"));
	}

	@Override
	public void loadContents(String[] contents) {
		productId = Integer.parseInt(contents[1]);
		name = "'"+contents[2]+"'";
		code = "'"+contents[3]+"'";
		cycle = Integer.parseInt(contents[4]);
	}

	@Override
	public Object getColumnValue(String column) {
		Object columnObj = null;
		switch (column) {
			case "id": 	 	   columnObj = super.id; 
						 	   break;
			case "name": 	   columnObj = name;
						 	   break;
			case "code": 	   columnObj = code;
			 				   break;
			case "cycle": 	   columnObj = cycle;
			 				   break;
			case "product_id": columnObj = productId;
			 				   break;
			default: 	 	   break;
		}
		return columnObj;
	}

	@Override
	public Object toJSonObject() {
		// TODO Auto-generated method stub
		return null;
	}
}
