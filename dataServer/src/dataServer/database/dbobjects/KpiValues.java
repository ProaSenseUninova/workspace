package dataServer.database.dbobjects;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class KpiValues extends KpiDataObject {
	public Timestamp timestamp = null;
	public int kpiId = 0;
	public int machineId = 0;
	public int value = 0;
	public String designation = null;
	public boolean goodPart = true;
	public int productId = 0;
	public int mouldId = 0;
	
	public KpiValues() {
		super("kpi_values");
		super.columnsNames.addAll(Arrays.asList("timestamp", "kpi_id", "machine_id", "value", "designation", "good_part", "product_id", "mould_id"));
	}
	
	public Object getColumnValue(String column){
		Object columnObj = null;
		switch (column) {
			case "id": 			columnObj = super.id; 
								break;
			case "timestamp": 	columnObj = "'"+timestamp.toString()+"'";
							  	break;
			case "kpi_id": 		columnObj = kpiId;
								break;
			case "machine_id": 	columnObj = machineId;
							  	break;
			case "value": 		columnObj = value;
								break;
			case "designation": columnObj = designation;
								break;
			case "good_part": 	columnObj = goodPart;
							 	break;
			case "product_id": 	columnObj = productId;
								break;
			case "mould_id": 	columnObj = mouldId;
			  					break;
			default: 			break;
		}
		return columnObj;
	}

	@Override
	public void loadContents(String[] contents) {
		timestamp = getTimestampValue(contents[0]);
		kpiId = Integer.parseInt(contents[1]);
		machineId = Integer.parseInt(contents[2]); 
		value = Integer.parseInt(contents[3]);
		designation = "'"+contents[4]+"'";
		goodPart = Boolean.parseBoolean(contents[5]);
		productId = Integer.parseInt(contents[6]);
		mouldId = Integer.parseInt(contents[7]);		
	}
	
	private Timestamp getTimestampValue(String timestampValue) {
		Timestamp result = null;
		DateFormat sourceFormat, targetFormat ;
		Date date;
		sourceFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			date = sourceFormat.parse(timestampValue);
			
			String s = targetFormat.format(date);

			result = Timestamp.valueOf(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public Object toJSonObject() {
		// TODO Auto-generated method stub
		return null;
	}
	
}