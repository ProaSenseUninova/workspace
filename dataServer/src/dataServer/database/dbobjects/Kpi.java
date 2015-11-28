package dataServer.database.dbobjects;

import java.util.Arrays;

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
		super.columnsNames.addAll(Arrays.asList("parent_id", "name", "description", "sampling_rate", "sampling_interval", "context_product", 
												"context_machine", "context_mould", "context_shift","calculation_type"));
	}

	@Override
	public void loadContents(String[] contents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getColumnValue(String column) {
		Object columnObj = null;
		switch (column) {
			case "id": 					columnObj = super.id; 
										break;
			case "parent_id": 			columnObj = parentId;
							  			break;
			case "name": 				columnObj = name;
										break;
			case "description":			columnObj = description;
							  			break;
			case "sampling_rate": 		columnObj = samplingRate;
										break;
			case "sampling_interval":	columnObj = samplingRate;
										break;
			case "context_product": 	columnObj = contextProduct;
							 			break;
			case "context_machine": 	columnObj = contextMachine;
										break;
			case "context_mould": 		columnObj = contextMould;
										break;
			case "context_shift": 		columnObj = contextShift;
										break;
			case "calculation_type": 	columnObj = calculationType;
										break;
			default: 					break;
		}
		return columnObj;
	}

	@Override
	public Object toJSonObject() {
		// TODO Auto-generated method stub
		return null;
	}
}
