package dataServer.database.dbobjects;
import java.util.ArrayList;

public abstract class KpiDataObject {
	public int id;
	public String tableName;
	public ArrayList<String> columnsNames = new ArrayList<String>(/*Arrays.asList("id")*/);
	
	public KpiDataObject(String tableName){
		this.tableName = tableName;
	}
	
	public int getId(){
			return this.id;
		}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public abstract void loadContents(String[] contents);
	public abstract Object getColumnValue(String column);
}
