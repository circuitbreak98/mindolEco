package com.sktst.batch.bas.ukc.biz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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
 * <li>업무 그룹명 : 프로젝트/SKTST</li>
 * <li>설 명 : 상담별 처리상태를 전송한다.. </li>
 * <li>작성일 : 2009-03-30 10:43:46</li>
 * </ul>
 *
 * @author 이문규
 */
public class BASUKC09000 extends AbsBatchJobBiz {

	private static final String PROG_ID = "BASUKC09000";
//	private static final String OUT_FILE_ID = "zeqpbbiz00630";
	private static final String OUT_FILE_ID = "EQ12.SKCC.";	
	private static final String FILE_EXTENSION = ".dat";
	private static final String NULL_VALUE = "";
	private static String fileTime = "";
	private PrintWriter dataFile;

	/**
	 * 
	 *
	 * @author 이문규
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
			
//			logMng.openLogFile(PROG_ID);
//			logMng.writeLogFile("args1 : " + fileTime);

			// Transaction Start Logging...
			if(log.isDebugEnabled()){
				log.debug( PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();

			// DB를 읽어서 FILE로 down.
//			logMng.writeLogFile("------------------------------------------------------------");
			getDBwriteDataFile(sqlMap);
//			logMng.writeLogFile("------------------------------------------------------------");

			// Transaction Commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			sqlMap.commitTransaction ();

		} catch(Exception e) {
//			logMng.writeLogFile("ERRCODE:F");
//			logMng.writeLogFile("execute Exception : " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
			}

		} finally {
			//처리 완료. (commit이 안된 경우 rollback)
			if(log.isDebugEnabled()){
				log.debug("BASUKC09000.execute.endTransaction");
			}			
			sqlMap.endTransaction ();

//			logMng.closeLogFile();
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
//		logMng.writeLogFile("getDBwriteDataFile method start......");
		
		openDataFile();


        String pMessage    = "";
        String sCurdate    = "";

	    int writeCnt   = 0;

		Map<String, Object> requestMapCond = new HashMap<String, Object>();
		Map                 printList      = new HashMap();

		try {
			// Calendar 생성
	        Date             cDate = null;
			Calendar         cal   = Calendar.getInstance(Locale.KOREA);
			SimpleDateFormat sdf   = new SimpleDateFormat("yyyyMMdd");

			// Parameter로 받은 fileTime(YYYYMMDD)을  일자(YYYY-MM-DD)형식으로 변환
			// 		sbMonth : 0 - January, 1 - February, 2 - March, .....
			//if (!(fileTime == null) && !(NULL_VALUE.equals(fileTime))) {
				sCurdate = fileTime;
			//} else {
			//	cal.add(Calendar.DATE, -1);        // 작업이 새벽에 수행 되므로 일자를 하루 감해서 Working 일자를 Setting 한다.
			//	cDate    = cal.getTime();
			//	sCurdate = sdf.format(cDate);
			//}


		 	requestMapCond.put("IF_CON_DT", sCurdate);
		 	
			List resultList = (List) sqlMap.queryForList("BASUKC09000.getConsultPrcStList", requestMapCond);

			int selectCnt = resultList.size();

//			logMng.writeLogFile(" Condition Table Select Complete ... " + selectCnt);
			
			for (int i   = 0; i < selectCnt; i++) {
				
				printList = (Map)    resultList.get(i);
				pMessage  = (String) printList.get("MSG");

				if(writeDataFile(pMessage) == true){
					writeCnt++;
				}
			}
	
			closeDataFile();
			
			sqlMap.update("BASUKC09000.updateConsultMgmtSend", requestMapCond);
			
//			logMng.writeLogFile("File Write Count : " + writeCnt);
//			logMng.writeLogFile("getDBwriteDataFile method end......");
		} catch (Exception e) {
//			logMng.writeLogFile("  Error ..." + e.toString());
			return;
		}
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
//			logMng.writeLogFile(e.toString());
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
/*		
		String dataPath = new StringBuffer()
			.append(outFilePath + "/")
			.append(OUT_FILE_ID + "_")
			.append(fileTime)
			.append(FILE_EXTENSION)
			.toString();
*/
		String dataPath = new StringBuffer()
		.append(outFilePath + "/")
		.append(OUT_FILE_ID)
		.append(fileTime)
		.append(FILE_EXTENSION)		
		.toString();
//		logMng.writeLogFile("Output File : " + dataPath);

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
