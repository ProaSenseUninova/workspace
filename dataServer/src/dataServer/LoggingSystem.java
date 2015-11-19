package dataServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

// This class creates a log system to save messages into a file.
// Path: where the program is running; File: see _logFileName field below.
// To use it, just declare into the class to use it as follows:
//
// LoggingSystem _log = LoggingSystem.getLog();
//
public class LoggingSystem {
	
	private static LoggingSystem _log;
	private String _logFileName = "dataServer.log";
	private String _logFolder = System.getProperty("user.dir")+"/";
	
    private FileOutputStream fis = null;
    private PrintStream out = null;
    private File file = null;
    
    private SimpleDateFormat date = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss:S");
    private final Lock lock = new ReentrantLock();
    
	private LoggingSystem()
	{
		
	}
	
	public static LoggingSystem getLog(){
		if(_log == null)
			_log = new LoggingSystem();

		return _log;
	}
	
	public void saveToFile(String msg){
		lock.lock();
		this.openFile(_logFolder+_logFileName);
		this.writeLog(msg);
		this.closeFile();
		lock.unlock();
	}
	
	public void saveToFile(String msg, String logFileName){
		lock.lock();
		this.openFile(_logFolder+logFileName);
		this.writeLog(msg);
		this.closeFile();
		lock.unlock();
	}
	
	private void writeLog(String msg)
	{
		String msgOutput = "[" + date.format(Calendar.getInstance().getTime()) +"] "+msg;
		this.out.println(msgOutput);
	}

    private void openFile(String fileName)
    {
        
    	if (file == null)
    		file = new File(fileName);
    
        this.fis = null;
        try {
        	if (file.exists())
        		this.fis = new FileOutputStream(file, true);
        	else
        		this.fis = new FileOutputStream(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoggingSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.out = new PrintStream(this.fis);
    };
    
    private void closeFile()
    {
        try {
             this.out.close();
             this.fis.close();
         } catch (IOException ex) {
             Logger.getLogger(LoggingSystem.class.getName()).log(Level.SEVERE, null, ex);
         }
     };
    

	public static void main(String[] args)
	{
		
		LoggingSystem ls = LoggingSystem.getLog();
		
		// ls._logFolder = "C:\\Users\\Luis\\Documents\\Uninova\\ProaSense\\workspace\\logs\\";
		// ls._logFileName = "logTest.log";
		
		ls.saveToFile("Hello");
		
		ls.saveToFile("Hello");
		
	}
}
