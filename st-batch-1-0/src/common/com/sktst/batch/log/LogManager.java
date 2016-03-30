package com.sktst.batch.log;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Log4jConfigurer;

import com.sktst.batch.base.AbsPropertyManager;

/**
* 업무 그룹명 : com.sktst.batch.log
* 서브 업무명 : LogManager.java
* 작성일 : 2009. 04. 02
* 설 명 : LogManager
* @author : admin
*/
public class LogManager extends AbsPropertyManager{
 
	private static final String log4jConfigFile = "classpath:Log4jConfig.xml";	
    private static final String BASE_DEV_LOGGER_NAME = "__devlog";
    private static final String BASE_DEV_INFO_LOGGER_NAME = "__devInfolog";
    private static final String BASE_DEV_ERROR_LOGGER_NAME = "__devErrorlog";
    private static final String BASE_FWK_LOGGER_NAME = "__fwklog";
    private PrintWriter logFile;
    private String logPath = "";
	private String pgmId = "";
	
	SimpleDateFormat todayFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    SimpleDateFormat timeFormatMilSec = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS", Locale.KOREA);
    
	/**
	 * 생성자 호출 시, Property 정보를 호출한다.
	 * 
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 *             
	 * @return void
	 */		
	public LogManager() {
		getProperties();
	}
	
	static {
		try {
			Log4jConfigurer.initLogging(log4jConfigFile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error initializing class. Cause:" + e);
		}
	}    
    /**
     * 로그 출력
     * 
     * @author admin
     * 
     * @param void
     * 
     * @return Log
     */
    public static Log getFwkLog(){    	
        Log fwkLog = LogFactory.getLog(BASE_FWK_LOGGER_NAME);
        return fwkLog;
    }

    /**
     * Debug Level 이상 로그 출력
     * 
     * @author admin
     * 
     * @param void
     * 
     * @return Log
     */
    public static Log getLog() {
        Log devLog = LogFactory.getLog(BASE_DEV_LOGGER_NAME);
        return devLog;
    }
    
    /**
     * Info Level 이상 로그만 출력
     * 
     * @author admin
     * 
     * @param void
     * 
     * @return Log
     */
    public static Log getInfoLog() {
        Log devLog = LogFactory.getLog(BASE_DEV_INFO_LOGGER_NAME);
        return devLog;
    }

    /**
     * Error Level 이상 로그만 출력
     * 
     * @author admin
     * 
     * @param void
     * 
     * @return Log
     */
    public static Log getErrorLog() {
        Log devLog = LogFactory.getLog(BASE_DEV_ERROR_LOGGER_NAME);
        return devLog;
    }

    /**
	 * Log File을 open한다.
	 * 
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param pgmId
	 *             파일명
	 * @return void
	 */		
	public void openLogFile(String pgmId) throws IOException {
		this.pgmId = pgmId;
		long currentDate = System.currentTimeMillis();
		String today = todayFormat.format(new Date(currentDate));
		
		String dataPath = new StringBuffer()
			.append(logPath + "/")
			.append(pgmId + "_")
			.append(today + ".log")
			.toString();
		
		FileOutputStream rsTemp = new FileOutputStream(dataPath, true);
		logFile = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				rsTemp)), true);
		
		String startTime = timeFormat.format(new Date(currentDate));
		
		writeLogFile("* -----------<<< " + pgmId + " Program Start >>>------------ *");
		writeLogFile("PGM ID       : " + pgmId);
		writeLogFile("Start Time   : " + startTime);
		writeLogFile("------------------------------------------------------------");
	}

    /**
	 * Log File을 open한다(시작 로그를 출력하지 않는다). 
	 * 
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param pgmId
	 *             파일명
	 * @return void
	 */		
	public void openLogFileWithNoMessage(String pgmId) throws IOException {
		this.pgmId = pgmId;
		long currentDate = System.currentTimeMillis();
		String today = todayFormat.format(new Date(currentDate));
		
		String dataPath = new StringBuffer()
			.append(logPath + "/")
			.append(pgmId + "_")
			.append(today + ".log")
			.toString();
		
		FileOutputStream rsTemp = new FileOutputStream(dataPath, true);
		logFile = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				rsTemp)), true);
	}
	
	/**
	 * Log File에 write한다.
	 * 
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param message
	 *             로그파일에 기록할 메세지
	 * @return void
	 */		
	public void writeLogFile(String message) {

		try {
			logFile.println("[" + timeFormatMilSec.format(new Date()) + "] " + message);
			logFile.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Log File을 close한다(종료 로그를 출력하지 않는다).
	 * 
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 * 
	 * @return void
	 */	
	public void closeLogFileWithNoMessage() throws IOException {
		logFile.close();
	}

	/**
	 * Log File을 close한다.
	 * 
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 * 
	 * @return void
	 */	
	public void closeLogFile() throws IOException {
		long currentDate = System.currentTimeMillis();
		String endTime = timeFormat.format(new Date(currentDate));
		
		writeLogFile("------------------------------------------------------------");
		writeLogFile("End Time     : " + endTime);
		writeLogFile("* -----------<<< " + pgmId + " Program End >>>-------------- *");
		
		logFile.close();
	}
	/**
	 * Property File로부터 property 값을 읽어온다.
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 * 
	 * @return void
	 * 
	 */
	public void getProperties() {
		Properties props = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(super.getPropertyPath());
			props.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fis != null ) {
				safeClose(fis);
			}
		}
		
		logPath = props.getProperty("LOG_HOME");
	}

	/**
	 * Log File Path 값을 읽어온다.
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 * 
	 * @return log file path
	 * 
	 */
	public String getLogPath() {
		return logPath;
	}
	
	/**
	 * FileInputStream 리소스를 해제한다.
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param FileInputStream
	 * 
	 * @return void
	 * 
	 */
	public void safeClose(FileInputStream fis) {
		if(fis != null) {
			try {
				fis.close();
			} catch(Exception e){
				throw new RuntimeException("Error safeClose Cause:" + e);
			}
		}
	}
}
