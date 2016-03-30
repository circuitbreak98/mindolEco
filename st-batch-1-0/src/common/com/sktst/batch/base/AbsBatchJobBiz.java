package com.sktst.batch.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.logging.Log;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.db.SqlMapConfig;
import com.sktst.batch.log.LogManager;
import com.sktst.batch.file.FileManager;

/**
 * 
 * @author admin
 *
 */
public abstract class AbsBatchJobBiz implements IBatchJobBiz{

	/** 개발 로그 출력용 */
	public Log log = LogManager.getLog();
	
	private static Log fwkLog = LogManager.getFwkLog();
	
	protected String inFilePath = "";
	protected String outFilePath = "";
	protected String logPath = "";
	public LogManager logMng = new LogManager();
	protected FileManager fileMng = new FileManager();
	private static long currentTime = System.currentTimeMillis();
	
	protected static String yyyymmdd = (new SimpleDateFormat("yyyyMMdd", Locale.KOREA)).format(new Date(currentTime));
	protected static String yyyymmddhh = (new SimpleDateFormat("yyyyMMddHH", Locale.KOREA)).format(new Date(currentTime));
	protected static String yyyymmddhhmi = (new SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA)).format(new Date(currentTime));
	protected static String hhmmss = (new SimpleDateFormat("HHmmss", Locale.KOREA)).format(new Date(currentTime));
	
	protected static final int NORMAL = 0;
	protected static final int ABNORMAL = -1;
	
	Properties props = new Properties();
	
	
	/**
	 * SqlMapClient를 반환한다.
	 * @return
	 */
	public SqlMapClient getSqlMapClient(){
		if(fwkLog.isInfoEnabled()){		
			fwkLog.info("Try to connect DB..");
		}
		SqlMapClient sqlMap = SqlMapConfig.getSqlMapInstance(); // 약 0.8초 소요
		if(fwkLog.isInfoEnabled()){		
			fwkLog.info("Success DB Connection.");
		}
		return sqlMap;
	}
	
	public void getProperties() {
		inFilePath = fileMng.getInFilePath();
		outFilePath = fileMng.getOutFilePath();
		logPath = logMng.getLogPath();
	}
}
