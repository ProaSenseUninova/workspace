package dataServer.database.dbobjects;

import java.util.Arrays;

public class Shift extends KpiDataObject {
	public String name;
	
	public Shift() {
		super("shift");
		super.columnsNames.addAll(Arrays.asList("name"));
	}

	@Override
	public void loadContents(String[] contents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getColumnValue(String column) {
		Object columnObj = null;
		switch (column) {
			case "id": 	 	   columnObj = super.id; 
						 	   break;
			case "name": 	   columnObj = name;
						 	   break;
			default: 	 	   break;
		}
		return columnObj;
	}
}
