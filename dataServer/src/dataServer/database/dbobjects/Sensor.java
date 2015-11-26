package dataServer.database.dbobjects;

import dataServer.database.enums.SamplingInterval;

public class Sensor extends KpiDataObject {
	public String name;
	public int samplingRate;
	public SamplingInterval samplingInterval;
	
	public Sensor() {
		super("sensor");
	}

	@Override
	public void loadContents(String[] contents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getColumnValue(String column) {
		// TODO Auto-generated method stub
		return null;
	}
}
