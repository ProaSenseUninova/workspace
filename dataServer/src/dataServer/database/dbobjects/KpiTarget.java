package dataServer.database.dbobjects;

import java.util.Arrays;

public class KpiTarget extends KpiDataObject {
	public int kpiId;
	public int productId;
	public int mouldId;
	public int machineId;
	public int shiftId;
	public int lowerBound;
	public int upperBound;

	public KpiTarget() {
		super("kpi_target");
		super.columnsNames.addAll(Arrays.asList("kpi_id","product_id", "mould_id","machine_id","shift_id", "lower_bound","upper_bound"));
	}

	@Override
	public void loadContents(String[] contents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getColumnValue(String column) {
		Object columnObj = null;
		switch (column) {
			case "id": 			columnObj = super.id; 
								break;
			case "kpi_id": 		columnObj = kpiId;
							  	break;
			case "product_id": 	columnObj = productId;
								break;
			case "mould_id":	columnObj = mouldId;
							  	break;
			case "machine_id": 	columnObj = machineId;
								break;
			case "shift_id":	columnObj = shiftId;
								break;
			case "lower_bound": columnObj = lowerBound;
								break;
			case "upper_bound": columnObj = upperBound;
	 						    break;
			default: 			break;
		}
		return columnObj;
	}

	@Override
	public Object toJSonObject() {
		// TODO Auto-generated method stub
		return null;
	}
}
