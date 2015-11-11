package dataServer.database.dbobjects;

import dataServer.database.enums.Aggregation;

public class KpiAggType extends KpiDataObject {
	public Aggregation aggregation;
	
	public KpiAggType() {
		super("kpi_agg_type");
	}
}