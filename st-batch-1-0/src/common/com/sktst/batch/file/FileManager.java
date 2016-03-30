package com.sktst.batch.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;
import com.sktst.batch.base.*;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/batch</li>
 * <li>설 명 : 송수신 파일 관리용 클래스.</li>
 * <li>작성일 : 2009-03-30 </li>
 * </ul>
 *
 * @author 원종윤 (wonjongyoon)
 */

public class FileManager extends AbsPropertyManager {
	
    private String inFilePath = "";
	private String outFilePath = "";
	
	SimpleDateFormat todayFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    
	/**
	 * 생성자 호출 시, Property 정보를 호출한다.
	 * 
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 *             
	 * @return void
	 */		
	public FileManager() {
		getProperties();
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
		inFilePath = props.getProperty("FILE_IN_HOME");
		outFilePath = props.getProperty("FILE_OUT_HOME");
	}
	
	/**
	 * In File Path 값을 읽어온다.
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 * 
	 * @return in file path
	 * 
	 */
	public String getInFilePath() {
		return inFilePath;
	}
	
	/**
	 * Out File Path 값을 읽어온다.
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 * 
	 * @return out file path
	 * 
	 */
	public String getOutFilePath() {
		return outFilePath;
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
