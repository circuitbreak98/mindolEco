package com.sktst.batch.acc.sss.biz;

 

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;


/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/정산</li>
 * <li>설 명 : SKT수수료공제 정보를 U.Key로 부터 I/F받아 영업 업무용 Temp Table에 저장한다.</li>
 * <li>작성일 : 2009-03-30 09:00:00</li>
 * </ul>
 *
 * @author 심연정(shimyunjung)
 */

public class ACCSSS08120 extends AbsBatchJobBiz  {
	
	private static final String PROG_ID = "ACCSSS08120";
	private static final String USER_ID = "ACCSSS0812";
//	private static final String IN_FILE_ID = "SKTPSM88";
//	private static final String FILE_EXTENSION = ".dat";
//	private static final String FILE_TIME_FORMAT = yyyymmdd; 
	private static String inFileName = "";

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 심연정(shimyunjung)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	
	public int execute(Map request) throws Exception {
		
		super.getProperties();
		
		logMng.openLogFile(PROG_ID);
		
		if (log.isDebugEnabled()) {
			log.debug(PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();
		
		inFileName = (String)request.get("args1");
		logMng.writeLogFile("args1 : " + inFileName);
		
		try {
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

		} catch(Exception e) {
			logMng.writeLogFile("execute Exception : " + e.getMessage());
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
	 * @author 심연정 (shimyunjung)
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
//		int skipCnt = 0;
		int insertCnt = 0;
		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append(inFileName)
			//.append(IN_FILE_ID + ".")
			//.append(FILE_TIME_FORMAT)
			//.append(FILE_EXTENSION)
			.toString();
	
		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);
 
 
			for (String readLine; (readLine = br.readLine()) != null;) {
 
				try {
					addSktCmmsDedtIF(sqlMap, readLine);	
					insertCnt++;						
				} catch (Exception ex) {
					logMng.writeLogFile(ex.getMessage());
					if (log.isDebugEnabled()) {
						log.debug(ex.getMessage());
					}
				}
				
				readCnt++;			
			}
		}  catch (Exception e) {
			logMng.writeLogFile("openDataFileAddDB Exception : " + e.getMessage());
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				logMng.writeLogFile(e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage());
				}
			}
			logMng.writeLogFile("------------------------------------------------------------");
			logMng.writeLogFile("File Read Count : " + readCnt);
			logMng.writeLogFile("   Insert Count : " + insertCnt);
			logMng.writeLogFile("openDataFileAddDB method end......");
		}
		
	}
	
	/**
	 * SKT수수료공제 I/F Table (TACC_SKT_CMMS_DEDT_IF)에 insert한다.
	 *
	 * @author 심연정 (shimyunjung)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fieldBuffer
	 *            String
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void addSktCmmsDedtIF(SqlMapClient sqlMap,
			String fieldBuffer) throws Exception {
	
		Map<String, String> requestMap = new HashMap<String, String>();
	
		Calendar cl = Calendar.getInstance();
	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat stf = new SimpleDateFormat("HHmmss");
	
		Date cDate = cl.getTime();
		 
		String sCurdate = sdf.format(cDate);
		String sCurTime = stf.format(cDate);
	
		requestMap.put("REC_STR", fieldBuffer);
		requestMap.put("USER_ID", USER_ID);
		requestMap.put("OP_DT", sCurdate+sCurTime);
		requestMap.put("OP_TM", sCurTime);
		
		/*
		if (log.isDebugEnabled()) {
			log.debug("rec_str==>[" + fieldBuffer + "]");
		}
		*/
 
		sqlMap.insert("ACCSSS08120.addSktCmmsDedtIF", requestMap);
 
		
	}
}
