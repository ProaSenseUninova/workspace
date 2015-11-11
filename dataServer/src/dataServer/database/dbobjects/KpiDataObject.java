package dataServer.database.dbobjects;
import java.lang.UnsupportedOperationException;
public class KpiDataObject {
	public int id;
	public String tableName;
	
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
