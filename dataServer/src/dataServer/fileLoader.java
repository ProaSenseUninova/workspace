package dataServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import dataServer.database.dbobjects.KpiValues;
import dataServer.database.enums.TableValueType;

public class fileLoader {
	
	private DatabaseAccessObject dAO = new DatabaseAccessObject();
	
	private HashMap <String,Integer> auxiliarMachineIds = new HashMap<String,Integer>();
	private HashMap <String,Integer> auxiliarProductIds = new HashMap<String,Integer>();
	private HashMap <String,Integer> auxiliarKpiIds 	= new HashMap<String,Integer>();
	
	private int bufferSize = 1000;

	public void readCSVFile(String csvFile){
		File csvFileRootPath = new File("");
		String csvFullPath = csvFileRootPath.getAbsolutePath() + csvFile;
		
		BufferedReader bufReader = null;
		String dataLine = "";
		String valueSeparator = ",";
				
		ArrayList<KpiValues> buffer = new ArrayList<KpiValues>();
		
		try {
			bufReader = new BufferedReader(new FileReader(csvFullPath));
			while ((dataLine = bufReader.readLine()) != null) {
				String[] lineValues = dataLine.split(valueSeparator);

				buffer.add(processLine(lineValues, TableValueType.MOULD));

				if ((buffer.size() % bufferSize) == 0){
					if (flushBuffer(buffer)){
						buffer.clear();
					}
				}
			}
			
			if (!buffer.isEmpty()){
				flushBuffer(buffer);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private KpiValues processLine(String[] lineValues, TableValueType table){
		KpiValues kpiValue = new KpiValues();
		kpiValue.timestamp = getTimestampValue(lineValues[0]);
		kpiValue.machineId = getNameId(lineValues[1], "machine", auxiliarMachineIds); 
		kpiValue.value = Integer.parseInt(lineValues[2]);
		kpiValue.designation = lineValues[3];
		kpiValue.goodPart = Boolean.parseBoolean(lineValues[4]);
		kpiValue.productId = (table==TableValueType.PRODUCT)?
								getNameId(lineValues[5], "product", auxiliarProductIds):
								getNameForeignKeyId(lineValues[5],TableValueType.MOULD.toString(), "product_id",auxiliarProductIds);
		kpiValue.kpiId = getNameId(getPartDesignation(kpiValue.goodPart), "kpi", auxiliarKpiIds);
		return kpiValue;
	}
	
	private boolean flushBuffer(ArrayList<KpiValues> buffer) {
		return dAO.insertBatchData(buffer);
	}

	private Timestamp getTimestampValue(String timestampValue) {
		Timestamp result = null;
		DateFormat sourceFormat, targetFormat ;
		Date date;
		sourceFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		targetFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		try {
			date = sourceFormat.parse(timestampValue);
			
			String s = targetFormat.format(date);

			result = Timestamp.valueOf(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private int getNameForeignKeyId(String valueName, String tableName, String foreignKey, HashMap<String, Integer> auxiliarIds){
		Integer foreignKeyId  = auxiliarIds.get(valueName);
		
		if (foreignKeyId == null) {
			foreignKeyId = dAO.getForeignKeyId(tableName, foreignKey, valueName);
			auxiliarIds.put(valueName, foreignKeyId);
		}
		
		return foreignKeyId;
	}

	private int getNameId(String valueName, String tableName, HashMap<String, Integer> auxiliarIds){
		// TODO: Get id of <valueName> from <tableName> if exists.
		// 1st time ask DB and save answer, following times gets saved answer
		Integer id = auxiliarIds.get(valueName);
		
		if (id == null) {
			id = dAO.getNameId(tableName, valueName);
			auxiliarIds.put(valueName, id);
		}
		
		return id;
	}
	
	private String getPartDesignation(boolean goodPart){
		// TODO: Get id of <valueName> from <tableName> if exists.
		// 1st time ask DB and save answer, following times gets saved answer
		return goodPart?"Good parts":"Scrapped parts";
	}
		
	
//	public boolean executeQuery(ArrayList<String> query){
//		boolean success = true;
//		dAO.insertBatchData(query);
//		return success;
//	}
	
	public static void main(String[] args) {
		LoggingSystem testLog = LoggingSystem.getLog();
		String logFileName = "testFileLoader.log";
		
		
		fileLoader fl = new fileLoader();
		String dataPath = "\\dataFiles";
		String[] csvFiles = {"\\ProaSense_dataGen_1410-events.csv",
							 "\\ProaSense_dataGen_1411-events.csv",
							 "\\ProaSense_dataGen_1412-events.csv",
							 "\\ProaSense_dataGen_1501-events.csv",
							 "\\ProaSense_dataGen_1502-events.csv",
							 "\\ProaSense_dataGen_1503-events.csv"};
		
		for (String csvFile : csvFiles) {
			testLog.saveToFile("Starting uploading file: "+csvFile, logFileName);
			fl.readCSVFile(dataPath+csvFile);
			testLog.saveToFile("Finished uploading file: "+csvFile, logFileName);
			testLog.saveToFile("<<------------------------->>"+csvFile, logFileName);
		}
		
	}

}
