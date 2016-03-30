package com.sktst.batch.sal.sui.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.text.*;


import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : 신규 및 판매전문을 처리하기 위한 임시 프로그램.</li>
 * <li>작성일 : 2009-03-30 09:00:00</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class SALSUI08999 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSUI08999";
	private static String fileName = "";
//	private static final String USER_ID = "SALSUI0899";
	private static final String IN_FILE_ID = "WORKING";
	private static final String FILE_EXTENSION = ".dat";
	private static final String FILE_TIME_FORMAT = yyyymmdd; 

	/**
	 * 배치 프로그램을 수행한다.
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
		SqlMapClient sqlMap = getSqlMapClient();
		
		try {
			logMng.openLogFile(PROG_ID);
		
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute");
			}
			
			fileName = (String)request.get("args1");
			String args1 = "${args1}";
			
			if(fileName.equals(args1))
			{
				logMng.writeLogFile("첫번째 인자값으로 /SAMUKEY/in에 있는 파일명을 넣어주세요");
				throw new Exception("첫번째 인자값으로 /SAMUKEY/in에 있는 파일명을 넣어주세요");
			} 
			
			if (log.isDebugEnabled()) {
				log.debug("파일명 : " + fileName);
			}
			logMng.writeLogFile("파일명 : " + fileName);
		
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();
			
			// FILE을 읽어서 DB insert.
			openDataFileAddDB(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			sqlMap.commitTransaction();

		} finally {
			// 처리 완료. (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.endTransaction");
			}
			sqlMap.endTransaction();
			logMng.closeLogFile();
		}		
		return 0;
	}

	/**
	 * 파일을 읽어서 DB에 기록한다.
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void openDataFileAddDB(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("openDataFileAddDB method start......");
		FileReader fr = null;
		BufferedReader br = null;

		int readCnt = 0;
		int insertCnt = 0;
		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append(fileName)
//			.append(IN_FILE_ID + ".")
//			.append(FILE_TIME_FORMAT)
//			.append(FILE_EXTENSION)
			.toString();
	
		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);

			int mod = 0;

			Calendar cl = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat stf = new SimpleDateFormat("HHmmss");

			Date cDate = cl.getTime();
			String svCurDate = sdf.format(cDate);
			String svCurTime = stf.format(cDate);
			String sCurDate = "";
			String sCurTime = "";
			int    sSeq     = 0;

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {

					cDate = cl.getTime();
					sCurDate = sdf.format(cDate);
					sCurTime = stf.format(cDate);

					if (svCurDate.equals(sCurDate)  && svCurTime.equals(sCurTime)) {

						sSeq++;

					} else {

						svCurDate = sCurDate;
						svCurTime = sCurTime;
						sSeq = 1;

					}
					
					addUkeyIfLog(sqlMap, readLine, sCurDate, sCurTime, sSeq);
					insertCnt++;

				} catch (Exception ex) {
					logMng.writeLogFile("ERRCODE:F");
					logMng.writeLogFile(ex.getMessage());
					logMng.writeLogFile("\n Error Rec==>[" + readLine + "]");
					if (log.isDebugEnabled()) {
						log.debug(ex.getMessage());
					}
				}
				readCnt++;
				
				mod = readCnt % 500; 
				if ( mod == 0 ) {
					logMng.writeLogFile( "    Now " + readCnt + " Process ...");
				}
			}
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				logMng.writeLogFile(e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage());
				}
			}
		}
		logMng.writeLogFile("------------------------------------------------------------");
		logMng.writeLogFile("File Read Count : " + readCnt);
		logMng.writeLogFile("   Insert Count : " + insertCnt);
		logMng.writeLogFile("openDataFileAddDB method end......");
	}

	/**
	 * 신규 및 기변 전문을 읽어 UKEY I/F LOG Table(TBAS_UKEY_IF_LOG)에 insert한다.
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fieldBuffer
	 *            String
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void addUkeyIfLog(SqlMapClient sqlMap,
			String fieldBuffer, String IV_OP_DT, String IV_OP_TM, int IV_SEQ) throws Exception {

		Map<String, String> requestMap = new HashMap<String, String>();

		requestMap.put("OP_DT", IV_OP_DT);
		requestMap.put("OP_TM", IV_OP_TM);
		requestMap.put("SEQ", String.valueOf(IV_SEQ));
		requestMap.put("IF_CL", fieldBuffer.substring(0, 2));
		requestMap.put("REC_STR", fieldBuffer);

		sqlMap.insert("SALSUI08999.addUkeyIfMq", requestMap);
		sqlMap.insert("SALSUI08999.addUkeyIfLog", requestMap);

		// 프로시저 호출 
		Map<String, Object> requestMapCall = new HashMap<String, Object>();

		requestMapCall.put("IV_OP_DT", IV_OP_DT);
		requestMapCall.put("IV_OP_TM", IV_OP_TM);
		requestMapCall.put("IV_SEQ", IV_SEQ);
		requestMapCall.put("IV_IF_CL", fieldBuffer.substring(0, 2));
		requestMapCall.put("IV_IF_CTT", fieldBuffer);

		sqlMap.update("SALSUI08999.call_SP_UKEY_IF_MQ", requestMapCall);

	}

}