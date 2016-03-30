package com.sktst.batch.acc.sss.biz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/정산</li>
 * <li>설 명 : SKT수수료정산을 파일로 생성한다. </li>
 * <li>작성일 : 2009-01-19 10:43:46</li>
 * </ul>
 *
 * @author 장화수 (hanbyunggon)
 * 
 *  CRON에 등록되어 
 * 
 */
public class ACCSSS08210 extends AbsBatchJobBiz {

	private PrintWriter dataFile;
	private static final String PROG_ID = "ACCSSS08210";
	private static final String OUT_FILE_ID = "TPSM89"; // SKT수수료정산
	private static final String REC_TYP = "89"; // 레코드구분:SKT수수료정산
	private static final String FILE_EXTENSION = ".dat";
	private static final String FILE_TIME_FORMAT = yyyymmdd; 

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 장화수 (hanbyunggon)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */	
	public int execute(Map request) throws Exception {

		super.getProperties();
		
		//① DB 정보 취득 (SqlMapClient 인스턴스 취득)
		//SqlMapClient sqlMap = getSqlMapClient();
		boolean bDebugEnabled = log.isDebugEnabled();
		
		try {
			
			logMng.openLogFile(PROG_ID);

			//로그 출력 (AbsBatchJobBiz 클래스에서 제공하는 변수 log를 사용)
			if (bDebugEnabled) log.debug(PROG_ID + ".execute");
			
			if (bDebugEnabled) log.debug(PROG_ID + ".execute.startTransaction");

			//② Transaction 시작
			//sqlMap.startTransaction();
			
			//③ 요청 데이터 처리, ④ DB 처리, ⑤ 결과 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");
			//args1=년월
			getDBwriteDataFile((String)request.get("args1"));
			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			if (bDebugEnabled) log.debug(PROG_ID + ".execute.commitTransaction");
			
			//⑥-1 Transaction Commit
			//sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
			
			if (bDebugEnabled) log.debug("execute Exception : " + e.getMessage());
			
		} finally {
			//⑥-2 Transaction rollback (commit이 안된 경우 rollback)
			if (bDebugEnabled) log.debug(PROG_ID + ".execute.commitTransaction");
			
			//sqlMap.endTransaction();
			
			logMng.closeLogFile();
		}
		
		//⑦ 처리 결과 코드 리턴
		return 0;
	}
	
	/**
	 * File을 읽어서 DB에 기록한다. 
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void getDBwriteDataFile(String SETTL_YM) throws Exception{
		logMng.writeLogFile("getDBwriteDataFile method start......");
		
		openDataFile();
		
		/**
		List resultList = (List) sqlMap.queryForList("ACCSSS08200.getDisListForUkey");
		int selectCnt = resultList.size();
		int writeCnt = 0;

		Map msgMap = new HashMap();
		for (int i = 0; i < selectCnt; i++) {
			msgMap = (Map) resultList.get(i);
			if(writeDataFile((String) msgMap.get("MSG")) == true){
				writeCnt++;
			}
		}
		**/

		int selectCnt = 1;
		int writeCnt = 0;
		String sContent = new StringBuffer()
//		.append(REC_TYP)
		.append(SETTL_YM)
		.toString();

		if(writeDataFile(sContent) == true){
			writeCnt++;
		}
		closeDataFile();
		logMng.writeLogFile("Select Count : " + selectCnt);
		logMng.writeLogFile("File Write Count : " + writeCnt);
		logMng.writeLogFile("getDBwriteDataFile method end......");
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
			.append(FILE_TIME_FORMAT)
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