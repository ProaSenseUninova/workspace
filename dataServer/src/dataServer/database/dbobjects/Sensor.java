package dataServer.database.dbobjects;

import dataServer.database.enums.SamplingInterval;

public class Sensor extends KpiDataObject {
	public String name;
	public int samplingRate;
	public SamplingInterval samplingInterval;
	
	public Sensor() {
		super("sensor");
	}
}
