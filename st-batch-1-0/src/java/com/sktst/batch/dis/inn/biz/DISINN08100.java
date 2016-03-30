package com.sktst.batch.dis.inn.biz;

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
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : Ukey SK출고/대리점입고 배치 I/F 추가 전문을 DB에 반영한다.</li>
 * <li>작성일 : 2009-03-30 09:00:00</li>
 * </ul>
 *
 * @author 한병곤
 */
public class DISINN08100 extends AbsBatchJobBiz {

	private static final String PROG_ID = "DISINN08100";

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 김중일 (kimjoongil)
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
		
		String sFileName = "";
		if( request.size() > 1 ) {
			sFileName = (String)request.get("args1");
			logMng.writeLogFile("args1 : " + sFileName);
		}
		
		try {
			logMng.writeLogFile(PROG_ID + ".execute.startTransaction");
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();
			
			// FILE을 읽어서 DB insert.
			openDataFileAddDB(sqlMap, sFileName);
			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			logMng.writeLogFile(PROG_ID + ".execute.commitTransaction");

			//sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
		} finally {
			// 처리 완료. (commit이 안된 경우 rollback)
			logMng.writeLogFile(PROG_ID + ".execute.endTransaction");
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.endTransaction");
			}
			sqlMap.endTransaction();
			logMng.closeLogFile();
		}

		logMng.closeLogFile();
		
		return 0;
	}

	/**
	 * 파일을 읽어서 DB에 기록한다.
	 *
	 * @author 한병곤
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fFileName
	 *            파일명
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void openDataFileAddDB(SqlMapClient sqlMap, String fFileName) throws Exception {
		logMng.writeLogFile("openDataFileAddDB method start......");
		FileReader fr = null;
		BufferedReader br = null;

		int insertCnt = 0;
		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append(fFileName)
			.toString();
	
		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {
					
					addProdInoutIf(sqlMap, readLine);
					insertCnt++;
					
					sqlMap.getCurrentConnection().commit();

				} catch (Exception ex) {
					logMng.writeLogFile("ex "+ex.getMessage());
				}
			}
		} catch (Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("openDataFileAddDB Exception : " + e.getMessage());

			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				logMng.writeLogFile(e.getMessage());
			}
		}
		logMng.writeLogFile("Insert Count : " + insertCnt);
		logMng.writeLogFile("openDataFileAddDB method end......");
	}

	/**
	 * tdis_prod_inout_if에 insert한다.
	 *
	 * @author han byung gon
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fieldBuffer
	 *            String
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void addProdInoutIf(SqlMapClient sqlMap,
			String fieldBuffer) throws Exception {

		Map requestMap = new HashMap();
		
		// procedure param setting
		String opDt = "";
		Date date = new Date();
		SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd");
		
		Calendar cal = Calendar.getInstance();
		date = cal.getTime();
		
		opDt = day.format(date);
		
		requestMap.put("OP_DT", opDt);
		requestMap.put("OP_TM", "999999"); // MQ로 들어오는 정상적인 데이터와 듐을 피하기 위해 
		requestMap.put("SEQ", 1);
		requestMap.put("IF_CL", "11"); // S51:매출입고,S55:교품입고
		requestMap.put("IF_CTT", fieldBuffer);
		
		// 프로시저 호출 
		logMng.writeLogFile("requestMap.toString()"+requestMap.toString());
		
		sqlMap.queryForObject("DISINN08100.callSP_DISINN08100", requestMap);

		logMng.writeLogFile("Commit Record(s) : " + requestMap.get("INS_CNT"));
		
	}
}