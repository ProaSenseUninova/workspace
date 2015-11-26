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

import dataServer.database.dbobjects.Kpi;
import dataServer.database.dbobjects.KpiAggType;
import dataServer.database.dbobjects.KpiDataObject;
import dataServer.database.dbobjects.KpiFormula;
import dataServer.database.dbobjects.KpiTarget;
import dataServer.database.dbobjects.KpiValues;
import dataServer.database.dbobjects.Machine;
import dataServer.database.dbobjects.Mould;
import dataServer.database.dbobjects.Product;
import dataServer.database.dbobjects.Sensor;
import dataServer.database.enums.TableValueType;

public class fileLoader {
	
	private DatabaseAccessObject dAO = new DatabaseAccessObject();
	
	private HashMap <String,Integer> auxiliarMachineIds = new HashMap<String,Integer>();
	private HashMap <String,Integer> auxiliarProductIds = new HashMap<String,Integer>();
	private HashMap <String,Integer> auxiliarKpiIds 	= new HashMap<String,Integer>();
	LoggingSystem log = LoggingSystem.getLog();
	
	private int bufferSize = 10000;
	private int totalLines = 0;
	private int fileLines = 0;
	
	private int clioLowerCaseCount = 0;
	private int clioUpperCaseCount = 0;
	private int opelCorsa2kOr2 = 0;

	public void readCSVFile(String csvFile, TableValueType table){
		File csvFileRootPath = new File("");
		String csvFullPath = csvFileRootPath.getAbsolutePath() + csvFile;
		
		BufferedReader bufReader = null;
		String dataLine = "";
		String valueSeparator = ",";
		Integer readCount = 0;		
		ArrayList<KpiDataObject> buffer = new ArrayList<KpiDataObject>();
		
		try {
			bufReader = new BufferedReader(new FileReader(csvFullPath));
			while ((dataLine = bufReader.readLine()) != null) {
				String[] lineValues = dataLine.split(valueSeparator);
				
				buffer.add(processLine(lineValues, table));
				
				if ((buffer.size() % bufferSize) == 0){
					if (flushBuffer(buffer)){
						buffer.clear();
						readCount++;
					}
				}
				
			}
			totalLines += (readCount)*bufferSize;
			
			if (!buffer.isEmpty()){
				totalLines += buffer.size();
				flushBuffer(buffer);
			}
			fileLines = (readCount)*bufferSize + buffer.size();
			
			log.saveToFile("Flushed "+fileLines+"/"+totalLines+" lines for "+csvFile);
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
	
	private KpiDataObject processLine(String[] lineValues, TableValueType table){
//		KpiValues kpiValue = new KpiValues();
		
		KpiDataObject kpiDO = null;
		switch (table) {
		case KPI_VALUES: kpiDO = new KpiValues();
			break;
		case PRODUCT: kpiDO = new Product();
			break;
		case MOULD: kpiDO = new Mould();
			break;
		case MACHINE:  kpiDO = new Machine();
			break;
		case KPI: kpiDO = new Kpi();
			break;
		case SENSOR: kpiDO = new Sensor();
			break;
		case SHIFT: kpiDO = new Sensor();
			break;
		case KPI_TARGET: kpiDO = new KpiTarget();
			break;
		case KPI_AGG_TYE:kpiDO = new KpiAggType();
			break;
		case KPI_FORMULA:kpiDO = new KpiFormula();
			break;
		default:break;
		
		}
		kpiDO.loadContents(lineValues);
		
		return kpiDO;
	}
	
	private boolean flushBuffer(ArrayList<KpiDataObject> buffer) {
		return dAO.insertBatchData(buffer);
	}

	private Timestamp getTimestampValue(String timestampValue) {
		Timestamp result = null;
		DateFormat sourceFormat, targetFormat ;
		Date date;
		sourceFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
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
	
	private int getNameForeignKeyId(String valueName, String tableName, String foreignKey, HashMap<String, Integer> auxiliarIds){
		Integer foreignKeyId  = auxiliarIds.get(valueName);
		
		if (foreignKeyId == null) {
			foreignKeyId = dAO.getForeignKeyId(tableName, foreignKey, valueName);
			if ( (foreignKeyId == 0) || (foreignKeyId == null) ) {
				log.saveToFile("id for <"+valueName+"> = <"+foreignKeyId+">");
			}
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
							 "\\ProaSense_dataGen_1503-events.csv",
							 "\\ProaSense_dataGen_1504-events.csv",
							 "\\ProaSense_dataGen_1505-events.csv"};
		
		for (String csvFile : csvFiles) {
			testLog.saveToFile("Starting uploading file: "+csvFile, logFileName);
			fl.readCSVFile(dataPath+csvFile, TableValueType.KPI_VALUES);
			testLog.saveToFile("Finished uploading file: "+csvFile, logFileName);
			testLog.saveToFile("<<------------------------->>"+csvFile, logFileName);
		}
		
//		String csvFileExtra = "\\ProaSense-HELLA-machines.csv";
//		testLog.saveToFile("Starting uploading file: "+csvFileExtra, logFileName);
//		fl.readCSVFile(dataPath+csvFileExtra, TableValueType.MACHINE);
//		testLog.saveToFile("Finished uploading file: "+csvFileExtra, logFileName);
//		testLog.saveToFile("<<------------------------->>"+csvFileExtra, logFileName);

//		String csvFileExtra = "\\ProaSense-HELLA-products.csv";
//		testLog.saveToFile("Starting uploading file: "+csvFileExtra, logFileName);
//		fl.readCSVFile(dataPath+csvFileExtra, TableValueType.PRODUCT);
//		testLog.saveToFile("Finished uploading file: "+csvFileExtra, logFileName);
//		testLog.saveToFile("<<------------------------->>"+csvFileExtra, logFileName);
		
//		String csvFileExtra = "\\ProaSense-HELLA-moulds.csv";
//		testLog.saveToFile("Starting uploading file: "+csvFileExtra, logFileName);
//		fl.readCSVFile(dataPath+csvFileExtra, TableValueType.MOULD);
//		testLog.saveToFile("Finished uploading file: "+csvFileExtra, logFileName);
//		testLog.saveToFile("<<------------------------->>"+csvFileExtra, logFileName);
		
		
	}

}
