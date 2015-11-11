package dataServer.database;

public class DBConfig {

	public String jdbcURL = "";
	public String userName = "";
	public String password ="";
	
	public DBConfig(){}
	public DBConfig(String jdbcURL, String userName, String password){
		this.jdbcURL = jdbcURL;
		this.userName = userName;
		this.password = password;
	}
}
