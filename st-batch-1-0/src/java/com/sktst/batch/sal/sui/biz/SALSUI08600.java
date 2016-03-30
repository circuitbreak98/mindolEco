package com.sktst.batch.sal.sui.biz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업관리</li>
 * <li>설 명 : 부가상품 가입정보를 취득하기 위해 서비스 관리번호 파일을 생성한다.. </li>
 * <li>작성일 : 2009-03-30 10:43:46</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class SALSUI08600 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSUI08600";
	private static final String OUT_FILE_ID = "APPL_FORM_TKEY_PLUS";
	private static final String FILE_EXTENSION = ".DAT";
	private static String fileTime = "";
	private PrintWriter dataFile;

	/**
	 * PS를 통해 가입된 서비스관리번호를 추출한다.
	 *
	 * @author 김연석 (kimyeunsuk)
	 *  
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */	
	public int execute(Map request) throws Exception {
		
		super.getProperties();
		
		if (log.isDebugEnabled()) {
			log.debug(PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();
		
		fileTime = (String)request.get("args1");


		
		try{

			logMng.openLogFile(PROG_ID);
			logMng.writeLogFile("args1 : " + fileTime);

			// Transaction Start Logging...
			if(log.isDebugEnabled()){
				log.debug( PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();

			// DB를 읽어서 FILE로 down.
			logMng.writeLogFile("------------------------------------------------------------");
			getDBwriteDataFile(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");

			
			// Transaction Commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			sqlMap.commitTransaction ();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
			}

		} finally {
			//처리 완료. (commit이 안된 경우 rollback)
			if(log.isDebugEnabled()){
				log.debug("SALSUI08600.execute.endTransaction");
			}			
			sqlMap.endTransaction ();

			logMng.closeLogFile();
		}
	
		// 처리 결과 코드 리턴
		return 0;
		}

	/**
	 * 내용 : 부가상품 가입정보를 취득하기 위하여 현재 PS 시스템에 유효한(판매취소되지 않은) 서비스 관리번호를 추출
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void getDBwriteDataFile(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("getDBwriteDataFile method start......");
		
		openDataFile();

		

		List resultList = (List) sqlMap.queryForList("SALSUI08600.getBatchInfo");
		int selectCnt = resultList.size();
		int writeCnt = 0;
		String sDtm = "";
		String eDtm = "";

		Map msgMap = new HashMap();
		for (int i = 0; i < selectCnt; i++) {
			msgMap = (Map) resultList.get(i);
			sDtm = msgMap.get("MOD_DTM").toString();
			//sDtm = sDtm + "00";
		}
		
		logMng.writeLogFile("selectCnt......||| "+selectCnt);
		

		int y = Integer.parseInt(sDtm.substring(0, 4));
		int mt = Integer.parseInt(sDtm.substring(4, 6));
		int d = Integer.parseInt(sDtm.substring(6, 8));
		int h = Integer.parseInt(sDtm.substring(8, 10));
		int m = Integer.parseInt(sDtm.substring(10, 12));
		int s = Integer.parseInt(sDtm.substring(12, 14));

		Calendar cal = Calendar.getInstance();
		cal.set(y, mt-1, d, h, m, s+1);				
		//cal.set(y, mt-1, d, h+12, m, s);				

		//cal.set(cal.YEAR,y);
		//cal.set(cal.MONTH,mt);
		//cal.set(cal.DATE,d);
		//cal.set(cal.HOUR_OF_DAY,h);
		//cal.set(cal.MINUTE,m - 1);

		String yyyy = Integer.toString(cal.get(cal.YEAR));
		String month = "0" + Integer.toString(cal.get(cal.MONTH)+1);
		String dd = "0" + Integer.toString(cal.get(cal.DATE));
		String hh = "0" + Integer.toString(cal.get(cal.HOUR_OF_DAY));
		String mm = "0" + Integer.toString(cal.get(cal.MINUTE));
		String ss = "0" + Integer.toString(cal.get(cal.SECOND));

		
		month = month.substring(month.length() - 2, month.length());
		dd = dd.substring(dd.length() - 2, dd.length());
		hh = hh.substring(hh.length() - 2, hh.length());
		mm = mm.substring(mm.length() - 2, mm.length());
		ss = ss.substring(ss.length() - 2, ss.length());

		sDtm = new StringBuffer(yyyy).append(month).append(dd)
				.append(hh).append(mm).append(ss).toString();
		logMng.writeLogFile("sDtm...........||| "+sDtm);

		
		eDtm = fileTime+"00";
		logMng.writeLogFile("eDtm...........||| "+eDtm);
		//System.out.println("========" + eDtm);

		Map<String, Object> requestMap    = new HashMap<String, Object>();
	 	requestMap.put("SDTM", sDtm);
	 	requestMap.put("EDTM", eDtm);
	 	//requestMap.put("SDTM", "20100914105001");
	 	//requestMap.put("EDTM", "20100915094000");
	 	                        
		resultList = (List) sqlMap.queryForList("SALSUI08600.getGsIfList", requestMap);
		selectCnt = resultList.size();
		writeCnt = 0;

		msgMap = new HashMap();
		for (int i = 0; i < selectCnt; i++) {
			msgMap = (Map) resultList.get(i);
			if(writeDataFile((String) msgMap.get("MSG")) == true){
				writeCnt++;
			}
		}
		closeDataFile();
		logMng.writeLogFile("   Working Date : " + sDtm + "~" + eDtm);
		logMng.writeLogFile("    Select Count : " + selectCnt);
		logMng.writeLogFile("File Write Count : " + writeCnt);
		logMng.writeLogFile("getDBwriteDataFile method end......");

	
		//Map<String, Object> requestMap    = new HashMap<String, Object>();
	 	//requestMap.put("MOD_DTM", "XXXX");
		sqlMap.update("SALSUI08600.saveBatchInfo", requestMap);
	
	}

	/**
	 * message를 읽어서 File에 기록한다. 
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param message
	 *            File에 기록할 메세지
	 * @return File에 정상적으로 기록될 경우 true를 리턴,
	 *            그렇지 않을 경우 false를 리턴한다.
	 */
	private boolean writeDataFile(String message) {
		try {

			dataFile.println(message);
			dataFile.flush();
		} catch (Exception e) {
			logMng.writeLogFile(e.toString());
			return false;
		}
		return true;
	}

	/**
	 * File로부터 message를 읽어온다. 
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 * 
	 * @return void
	 * 
 	 * @throws IOException
	 */	
	private void openDataFile() throws IOException {
		String dataPath = new StringBuffer()
			.append(outFilePath + "/")
			.append(OUT_FILE_ID + "_")
			.append(fileTime)
			.append(FILE_EXTENSION)
			.toString();
		
		logMng.writeLogFile("Output File : " + dataPath);

		File orgFile = new File(dataPath);
		orgFile.delete();

		FileOutputStream rsTemp = new FileOutputStream(dataPath, true);
		dataFile = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				rsTemp)), true);

	}
	
	/**
	 * File을 닫는다. 
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 * 
	 * @return void
	 * 
 	 * @throws IOException
	 */
	private void closeDataFile() throws IOException {
		dataFile.close();
	}

}