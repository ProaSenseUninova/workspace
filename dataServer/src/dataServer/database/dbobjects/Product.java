package dataServer.database.dbobjects;

import java.util.Arrays;

public class Product extends KpiDataObject {
	public String name;
	public String code;
	
	public Product() {
		super("product");
		super.columnsNames.addAll(Arrays.asList("name", "code"));
	}

	@Override
	public void loadContents(String[] contents) {
		name = "'"+contents[1]+"'";
		code = "'"+contents[2]+"'";
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
