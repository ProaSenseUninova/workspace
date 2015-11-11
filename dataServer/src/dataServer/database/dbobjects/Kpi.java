package dataServer.database.dbobjects;

import dataServer.database.enums.SamplingInterval;

public class Kpi extends KpiDataObject {
	public String name;
	public int parentId;
	public String description;
	public int samplingRate;
	public SamplingInterval samplingInterval;
	public int contextProduct;
	public int contextMachine;
	public int contextMould;
	public int contextShift;
	public String calculationType;
	public int aggregationId;

	public Kpi() {
		super("kpi");
	}
}
