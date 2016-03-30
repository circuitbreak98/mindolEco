package com.sktst.batch.bas.prm.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;
/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준정보</li>
 * <li>설 명 : 대리점, 직영점, 판매점  전문을 DB에 반영한다.</li>
 * <li>작성일 : 2009-04-14 16:00:00</li>
 * </ul>
 *
 * @author 최승호 (choiseungho)
 */
public class BASPRM08100 extends AbsBatchJobBiz {

	private static final String PROG_ID = "BASPRM08100";
	//private static final String USER_ID = "BASPRM0810";
	private static final String IN_FILE_ID = "SKTPSH05";
	private static final String FILE_EXTENSION = ".DAT";
	private static final String FILE_TIME_FORMAT = yyyymmdd; 

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 최승호 (choiseungho)
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

		} finally {
			// 처리 완료. (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.endTransaction");
			}
			sqlMap.endTransaction();
		}

		logMng.closeLogFile();
		return 0;
	}

	/**
	 * 파일을 읽어서 DB에 기록한다.
	 *
	 * @author 최승호 (choiseungho)
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
			.append(IN_FILE_ID + "_")
			.append(FILE_TIME_FORMAT)
			.append(FILE_EXTENSION)
			.toString();
	
		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {
					addOldRtnIfTable(sqlMap, readLine);
					insertCnt++;
				} catch (Exception ex) {
					logMng.writeLogFile(ex.getMessage());
					if (log.isDebugEnabled()) {
						log.debug(ex.getMessage());
					}
				}
				readCnt++;
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
		logMng.writeLogFile("File Read Count : " + readCnt);
		logMng.writeLogFile("Insert Count : " + insertCnt);
		logMng.writeLogFile("openDataFileAddDB method end......");
	}

	/**
	 * 중고반납IF 테이블(tdis_old_rtn_if)에 insert한다.
	 *
	 * @author 최승호 (choiseungho)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fieldBuffer
	 *            String
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void addOldRtnIfTable(SqlMapClient sqlMap,
			String fieldBuffer) throws Exception {

		Map<String, String> requestMap = new HashMap<String, String>();
		
		requestMap.put("REC_STR", fieldBuffer);
		//requestMap.put("USER_ID", USER_ID);
		requestMap.put("OP_TM", FILE_TIME_FORMAT);

		if (log.isDebugEnabled()) {
			log.debug("rec_str==>[" + fieldBuffer + "]");
		}
		
		sqlMap.insert("BASPRM08100.saveDealIf", requestMap);
	}
}
