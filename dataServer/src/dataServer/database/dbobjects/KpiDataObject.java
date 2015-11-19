package dataServer.database.dbobjects;
import java.lang.UnsupportedOperationException;
import java.util.ArrayList;
import java.util.Arrays;
public class KpiDataObject {
	public int id;
	public String tableName;
	public ArrayList<String> columnsNames = new ArrayList<String>(/*Arrays.asList("id")*/);
	
	public KpiDataObject(String tableName){
		this.tableName = tableName;
	}
	
	public int getId(){
			throw new UnsupportedOperationException();
		}
	
	public String getTableName() {
		return this.tableName;
	}
}
