package dataServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class Main extends AbstractHandler
{
	static LoggingSystem _log = LoggingSystem.getLog();
	
	public void getData(HttpServletResponse response,String dbName,String tableName,Integer idReq,String remoteAddress)
	{
		try {
			if(dbName.equals("func"))
			{
				if(tableName.equals("getGraphData"))
				{
					response.getWriter().println(getGraphData());
				}
				else if(tableName.equals("getHeatMapData"))
				{
					response.getWriter().println(getHeatMapData());
				}
						
			}
			else
			{
				Connection c = DriverManager.getConnection("jdbc:hsqldb:file:db/"+dbName, "sa", "");
				Statement s =  c.createStatement();
				String query = "SELECT * FROM \""+tableName+"\"";
				if(idReq!=null)
				{
					writeLogMsg(tableName);
	
					if(tableName.equals("kpi_target"))
					{
						query=query+" WHERE \"kpi_target_id\"="+idReq;
					}
					else
					{
						query=query+" WHERE \"id\"="+idReq;
					}
				}
	
				writeLogMsg("SQL Query: "+query);
				
				
				ResultSet r = s.executeQuery(query);
				 // are implementation dependent unless you use the SQL ORDER statement
		        ResultSetMetaData meta;
		
				String qm=null;
				meta = r.getMetaData();
				String str ="[";
		        int               colmax = meta.getColumnCount();
		        int               i;
		        Object            o = null;
		        // the result set is a cursor into the data.  You can only
		        // point to one row at a timeop
		        // assume we are pointing to BEFORE the first row
		        // rs.next() points to next row and returns true
		        // or false if there is no next row, which breaks the loop
		        for (; r.next(); ) {
		        	str=str+"{";
		            for (i = 0; i < colmax; ++i) {
		            	o = r.getObject(i+1);
		            	
		            	if(o!=null)
		            	{
		            		qm=o instanceof Integer  || o.equals(true)|| o.equals(false)? "":"\""; 
		            	}
		            	else
		            	{
		            		qm="";
		            	}
		            	str=str+"\""+meta.getColumnName(i+1)+"\":"+qm+o+qm;
		                if(i<colmax-1)
		                {
		                	str=str+",";
		                }
		                
		                // with 1 not 0
		                
		            }
		            str=str+"},";
		        }
		        if(!str.endsWith("["))
		        {
		        	str=str.substring(0,str.length()-1);
		        }
		        str=str+"]";
		       response.getWriter().println(str);
		       writeLogMsg(str);
		       writeLogMsg("Response at: "+remoteAddress);
		       c.close();
			}		
				
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			writeLogMsg("Error: "+e.getMessage());
		}
			
	}
	
	
	public Object getGraphData()
	{
		try
		{
			JSONParser parser = new JSONParser();
			JSONObject obj = new JSONObject();
			Object data = parser.parse("[[10.63, 5.95, 4.93, 9.06, 5.95, 6.30],"
					+ "[15.49, 11.31, 3.10, 16.36, 0.70, 0.22],"
					+ "[13.40, 13.87, 0.25, 8.80, 9.17, 0.56],"
					+ "[7.05, 3.68, 9.10, 4.58, 7.33, 9.40],"
					+ "[1.41, 0.19, 2.04, 7.57, 2.71, 6.46]]");
			
					
			Object legend = parser.parse("[\"Global\",\"Machine 1\",\"Machine 2\",\"Machine 3\",\"Machine 4\"]");
			Object labels = parser.parse("[\"December\",\"January\",\"February\",\"March\",\"April\",\"May\"]");
			obj.put("data", data);
			obj.put("legend", legend);
			obj.put("labels", labels);
			obj.put("title", "Availability");
			obj.put("subTitle", "Source: use case data");
			return obj;
		}
		catch(Exception e)
		{
			writeLogMsg(e.getMessage());
			return "";
		}
	}
	
	
	public Object getHeatMapData()
	{
		try
		{
			JSONObject obj = new JSONObject();
			JSONParser parser = new JSONParser();
			Object data = parser.parse("[{\"varY\":1,\"varX\":1,\"value\":9},"
					+ "{\"varY\":1,\"varX\":2,\"value\":78},"
					+ "{\"varY\":1,\"varX\":3,\"value\":123},"
					+ "{\"varY\":1,\"varX\":4,\"value\":114},"
					+ "{\"varY\":1,\"varX\":5,\"value\":8},"
					+ "{\"varY\":1,\"varX\":6,\"value\":12},"
					+ "{\"varY\":2,\"varX\":1,\"value\":19},"
					+ "{\"varY\":2,\"varX\":2,\"value\":58},"
					+ "{\"varY\":2,\"varX\":3,\"value\":15},"
					+ "{\"varY\":2,\"varX\":4,\"value\":132},"
					+ "{\"varY\":2,\"varX\":5,\"value\":5},"
					+ "{\"varY\":2,\"varX\":6,\"value\":32},"
					+ "{\"varY\":3,\"varX\":1,\"value\":10},"
					+ "{\"varY\":3,\"varX\":2,\"value\":92},"
					+ "{\"varY\":3,\"varX\":3,\"value\":35},"
					+ "{\"varY\":3,\"varX\":4,\"value\":72},"
					+ "{\"varY\":3,\"varX\":5,\"value\":38},"
					+ "{\"varY\":3,\"varX\":6,\"value\":88}]");
			Object yLabels = parser.parse("[\"Evening\", \"Afternoon\", \"Moorning\"]");
			Object xLabels = parser.parse("[\"Product A\", \"Product B\", \"Product C\", \"Product D\", \"product E\",\"Product F\"]");
			obj.put("data", data);
			obj.put("xLabels", xLabels);
			obj.put("yLabels", yLabels);
			obj.put("title", "Availability per shift per product");
			return obj;
		}
		catch(Exception e)
		{
			writeLogMsg(e.getMessage());
			return "";
		}
		
	}
	public void insertData(HttpServletResponse response,String dbName,String tableName,Object data,String remoteAddress)
	{
		try {
			String str="";
			String query="";
			Integer insRows = null;
			JSONParser parser = new JSONParser();
			JSONArray parsedData =  (JSONArray)data;
			JSONObject obj=null;
			String insertId="";
			obj=(JSONObject)parsedData.get(0);
			Object[] propertiesVect = obj.keySet().toArray();
			String properties="(";
			for(int i =0;i<propertiesVect.length;i++)
			{
				properties=properties+"\""+propertiesVect[i]+"\",";
			}
			properties=properties.substring(0,properties.length()-1)+")";
			
			query="INSERT INTO \""+tableName+"\" "+properties+" VALUES ";
			for(int i=0;i<parsedData.size();i++)
			{
				obj=(JSONObject)parsedData.get(i);
				query=query+"(";
				for(int j=0;j<propertiesVect.length;j++)
				{
					Object val = obj.get(propertiesVect[j]);
					query=query+(val instanceof String?"'"+val+"'":val);
					if(j<propertiesVect.length-1)
					{
						query=query+",";
					}
					
				}
				query=query+")";
				if(i<parsedData.size()-1)
				{
					query=query+",";
				}
			}
			
			Connection c = DriverManager.getConnection("jdbc:hsqldb:file:db/"+dbName, "sa", "");
			writeLogMsg("SQL Query: "+query);

			PreparedStatement s =  c.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			insRows = s.executeUpdate();

			insertId="[";
			if(insRows>0)
			{
				ResultSet generatedKeys = s.getGeneratedKeys();	
				while(generatedKeys.next())
				{
					insertId=insertId+generatedKeys.getInt(1)+",";
				}
				insertId=insertId.substring(0,insertId.length()-1);
			}
			insertId=insertId+"]";
			response.getWriter().println("{\"succeeded\":true,\"result\":\""+insRows+" records added\",\"insertId\":"+insertId+"}");
			writeLogMsg("Response at: "+remoteAddress);
			c.close();
		} 
		catch(Exception e)
		{
			try {
				response.getWriter().println("{\"succeeded\":false,\"result\":\""+e.toString().replace("\"", "\\\"")+"\"}");
				writeLogMsg("Response at: "+remoteAddress);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} 

		
	}
	
	public void updateData(HttpServletResponse response,String dbName,String tableName,Object data,String remoteAddress)
	{
		try {
			String str="";
			String query="";
			Integer upRows = null;
			JSONObject obj=(JSONObject)data;
			String idEl=null;
			idEl=tableName=="kpi_target"?"kpi_target_id":"id";
			Object id = obj.get(idEl);
			Object[] propertiesVect = obj.keySet().toArray();
			query="UPDATE \""+tableName+"\" SET ";
			
			for(int i=0;i<propertiesVect.length;i++)
			{
				if(propertiesVect[i].equals(idEl))
				{
					continue;
				}
				Object val = obj.get(propertiesVect[i]);
				query=query+"\""+propertiesVect[i]+"\"="+(val instanceof String?"'"+val+"'":val)+",";

				
			}
			query=query.substring(0,query.length()-1)+" WHERE \""+idEl+"\"="+id;
			Connection c = DriverManager.getConnection("jdbc:hsqldb:file:db/"+dbName, "sa", "");
			Statement s =  c.createStatement();
			writeLogMsg("SQL Query: "+query);

			upRows = s.executeUpdate(query);
			response.getWriter().println("{\"succeeded\":"+(upRows==0?"false":"true")+",\"result\":\""+(upRows==0?"Record not found":"Record updated")+"\"}");
			writeLogMsg("Response at: "+remoteAddress);
			c.close();
		} 
		catch(Exception e)
		{
			try {
				response.getWriter().println("{\"succeeded\":false,\"result\":\""+e.toString().replace("\"", "\\\"")+"\"}");
				writeLogMsg("Response at: "+remoteAddress);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public void deleteData(HttpServletResponse response,String dbName,String tableName,Object data,String remoteAddress)
	{
		try {
			Integer delRows =null;
			String query = "DELETE FROM \""+tableName+"\" WHERE ";
			JSONArray parsedData =  (JSONArray)data;
			JSONObject obj=null;
			Connection c = DriverManager.getConnection("jdbc:hsqldb:file:db/"+dbName, "sa", "");
			Statement s =  c.createStatement();
			for(int i=0;i<parsedData.size();i++)
			{
				obj =(JSONObject)parsedData.get(i);
				if(tableName.equals("kpi_target"))
				{
					query = query+"\"kpi_target_id\"="+obj.get("kpi_target_id");
				}
				else
				{
					query = query+"\"id\"="+obj.get("id");
				}
				if(i<parsedData.size()-1)
				{
					query=query+" OR ";
				}
				
			}
			writeLogMsg("SQL Query: "+query);
			delRows = s.executeUpdate(query);
			response.getWriter().println("{\"succeeded\":"+(delRows==0?"false":"true")+",\"result\":\""+delRows+" records deleted\"}");
			writeLogMsg("Response at: "+remoteAddress);
		    c.close();
		} 
		catch(Exception e)
		{
			try {
				response.getWriter().println("{\"succeeded\":false,\"result\":\""+e.toString().replace("\"", "\\\"")+"\"}");
				writeLogMsg("Response at: "+remoteAddress);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		
	}
	
    public void handle(String target,
    		
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response) 
    
			throws IOException, ServletException
			{		
    				String method = baseRequest.getMethod();
    				String remoteAddress = baseRequest.getHeader("X-Forwarded-for")==null?baseRequest.getRemoteAddr():baseRequest.getHeader("X-Forwarded-for");
    				writeLogMsg(method+" Request from: "+ remoteAddress + " Request target: " + target);
    				
    				Enumeration<String> headers = baseRequest.getHeaderNames();
    				while (headers.hasMoreElements()) 
    				{
    					String header = headers.nextElement();
    					String headerContent = baseRequest.getHeader(header);
    					writeLogMsg("Header: "+ header + " " + headerContent);
    				}

    				String[] parts = target.split("/");
    				JSONParser parser = new JSONParser();
    				Object obj = null;
    				String data ="";
    				String line=null;
    				Integer idReq = null;
    				try
    				{
	    				BufferedReader reader = baseRequest.getReader();
	    				while((line=reader.readLine())!=null)
	    				{
	    					data=data+line;
	    				}

    				}
    				catch(Exception e)
    				{
    					
    				}

					response.setContentType("application/json;charset=utf-8");
					response.setStatus(HttpServletResponse.SC_OK);
					baseRequest.setHandled(true);
					
					
					

					if(parts.length>2)
					{
						if(data!="")
						{
							writeLogMsg("Payload: "+data);
						}
						if(parts.length>3)
						{
							try
							{
								idReq = Integer.parseInt(parts[3]);
								writeLogMsg("Id requested: "+idReq);
							}
							catch(Exception e)
							{
							
							}
						}
						String dbName=parts[1];
						String tableName = parts[2];
						JSONObject tmpObj = null;
						JSONArray tmpArr = null;
						if(method=="GET")
						{
							getData(response,dbName,tableName,idReq,remoteAddress);
						}
						else
						{
							try {
								obj = parser.parse(data);
								if(method=="POST")
								{
									tmpObj = (JSONObject)obj;
									Object type = tmpObj.get("type");
									Object reqData = tmpObj.get("data");
									
									if(type!=null )
									{
										if(type.equals("GET"))
										{
											getData(response,dbName,tableName,idReq,remoteAddress);
										}
										else if(type.equals("INSERT") && reqData!=null)
										{
											insertData(response,dbName,tableName,reqData,remoteAddress);
										}
										else if(type.equals("UPDATE") && reqData!=null)
										{
											updateData(response,dbName,tableName,reqData,remoteAddress);
										}
										else if(type.equals("DELETE") && reqData!=null)
										{
											deleteData(response,dbName,tableName,reqData,remoteAddress);
										}
									}
									
								}
								else if(method.equals("PUT"))
								{
									insertData(response,dbName,tableName,obj,remoteAddress);
									
								}
								else if(method.equals("PATCH"))
								{
									updateData(response,dbName,tableName,obj,remoteAddress);
								}
								else if(method.equals("DELETE"))
								{
									deleteData(response,dbName,tableName,obj,remoteAddress);
								}							
							} catch (Exception e) {
								// TODO Auto-generated catch block
								response.getWriter().println("{\"succeeded\":false,\"result\":\""+e.toString().replace("\"", "\\\"")+"\"}");
								writeLogMsg("Response at: "+remoteAddress);
							}
						}

					}						

					

			}
 
    
    private static void writeLogMsg(String msg)
    {
		System.out.println(msg);
		_log.saveToFile(msg);
    }
  
    
	public static void main(String[] args) throws SQLException
	{
		int port = 8085;
		try
		{
	        Server server = new Server(port);
	        server.setHandler(new Main());
			server.start();
	        writeLogMsg("Server listening on port: "+port);
	
		}
		catch(Exception e)
		{
			writeLogMsg("Error: "+e.getMessage());
			e.printStackTrace();
		}

	}

	
	//test
}
