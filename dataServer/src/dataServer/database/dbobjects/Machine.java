package dataServer.database.dbobjects;

import java.util.Arrays;

public class Machine extends KpiDataObject {
	public String name;
	
	public Machine() {
		super("machine");
		super.columnsNames.addAll(Arrays.asList("name"));
	}

	@Override
	public void loadContents(String[] contents) {
		name = "'"+contents[1]+"'";
		
	}

	@Override
	public Object getColumnValue(String column) {
		Object columnObj = null;
		switch (column) {
			case "id": 	 columnObj = super.id; 
						 break;
			case "name": columnObj = name;
						 break;
			default: 	 break;
		}
		return columnObj;
	}
}