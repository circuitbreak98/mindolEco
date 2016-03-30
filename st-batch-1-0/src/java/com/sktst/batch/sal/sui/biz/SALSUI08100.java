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
public class SALSUI08100 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSUI08100";
	private static final String OUT_FILE_ID = "TPSD97";
	private static final String FILE_EXTENSION = ".dat";
	private static       String fileTime = "";
	private         PrintWriter dataFile;

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
				log.debug("SALSUI08100.execute.endTransaction");
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


		String wkStartDTM = "";
		String wkEndDTM   = "";
		String wkCurdate   = "";

		// Batch Log에 사용할 시작일시 Get
		Map                 resultTime;
		Map<String, Object> requestTime    = new HashMap<String, Object>();

		resultTime = (Map)sqlMap.queryForObject("SALSUI08110.getMonthClose", requestTime);
		wkStartDTM   = (String)  resultTime.get("SYS_DTM");

		openDataFile();

		Calendar cl = Calendar.getInstance(Locale.KOREA);
		cl.add(Calendar.DATE, -1);                                 // 작업이 새벽에 수행 되므로 일자를 하루 감해서 Working 일자를 Setting 한다.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date   cDate    = cl.getTime();
	       wkCurdate    = sdf.format(cDate);

		Map<String, Object> requestMap    = new HashMap<String, Object>();
	 	requestMap.put("SCRB_MONTH", wkCurdate.substring(0, 6));

		List resultList = (List) sqlMap.queryForList("SALSUI08100.getTgtSvcMgmtnumList", requestMap);
		int selectCnt = resultList.size();
		int writeCnt = 0;

		Map msgMap = new HashMap();
		for (int i = 0; i < selectCnt; i++) {
			msgMap = (Map) resultList.get(i);
			if(writeDataFile((String) msgMap.get("MSG")) == true){
				writeCnt++;
			}
		}
		closeDataFile();

		// Batch Job Log 프로시저 호출
		resultTime = (Map)sqlMap.queryForObject("SALSUI08110.getMonthClose", requestTime);
		wkEndDTM   = (String)  resultTime.get("SYS_DTM");

		requestTime.put("ov_errcode",     "S");
		requestTime.put("ov_errmsg",      "정상");
		requestTime.put("iv_proc_dt_fr",   wkStartDTM);
		requestTime.put("iv_proc_dt_to",   wkEndDTM);
		requestTime.put("iv_prog_id",     "SALSUI0810");
		requestTime.put("iv_result_flag", "S");
		requestTime.put("iv_fail_cnt",    "0");
		requestTime.put("iv_rmks",        "정상수행");
		requestTime.put("iv_obj_dt",       wkCurdate);

		sqlMap.update("SALSUI08100.addBatLog", requestTime);
		logMng.writeLogFile("Success : SALSUI08100.addBatLog ");

		logMng.writeLogFile("   Working Month : " + wkCurdate.substring(0, 6));
		logMng.writeLogFile("    Select Count : " + selectCnt);
		logMng.writeLogFile("File Write Count : " + writeCnt);
		logMng.writeLogFile("getDBwriteDataFile method end......");
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
			.append(OUT_FILE_ID + ".")
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