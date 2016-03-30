package com.sktst.batch.acc.ifm.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;
/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/정산인터페이스</li>
 * <li>설 명 : UKEY -> TKEY+ -> ERP</li>
 * <li>작성일 : 2010-03-22 15:00:00</li>
 * </ul>
 *
 * @author 이영진 (leeyoungjin)
 */
public class ACCIFM08100 extends AbsBatchJobBiz {

	private static final String PROG_ID = "ACCIFM08100";
	private static final String FILE_TIME_FORMAT = hhmmss; 
	private static String inFileTimeFormat = "";

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 이영진 (leeyoungjin)
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
			
			logMng.writeLogFile(PROG_ID + ".execute");
			inFileTimeFormat = (String)request.get("args1");
			logMng.writeLogFile("args1 : " + inFileTimeFormat);
			logMng.writeLogFile(PROG_ID + ".execute.startTransaction");
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}			

			// 업무 시작.
			sqlMap.startTransaction();
			
			// FILE을 읽어서 DB insert.
			openDataFileAddDB(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			logMng.writeLogFile(PROG_ID + ".execute.commitTransaction");
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}			
			
			sqlMap.commitTransaction();
			
			sqlMap.insert("ACCIFM08100.saveERP");
			
			sqlMap.commitTransaction();
			

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

		
		return 0;
	}

	/**
	 * 파일을 읽어서 DB에 기록한다.
	 *
	 * @author 이영진 (leeyoungjin)
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
			.append(inFileTimeFormat)
			.toString();
	
		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {
					insertCnt = insertCnt + addOldRtnIfTable(sqlMap, readLine);
				} catch (Exception ex) {
					logMng.writeLogFile(ex.getMessage());
				}
				readCnt++;
			}
		} catch(Exception e) {
			logMng.writeLogFile("openDataFileAddDB Exception : " + e.getMessage());
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				logMng.writeLogFile(e.getMessage());
			}
		}
		logMng.writeLogFile("File Read Count : " + readCnt);
		logMng.writeLogFile("Insert Count : " + insertCnt);
		logMng.writeLogFile("openDataFileAddDB method end......");
	}

	/**
	 * 테이블에 insert한다.
	 *
	 * @author 이영진 (leeyoungjin)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fieldBuffer
	 *            String
	 * @return void
	 * 
	 * @throws Exception
	 */
	private int addOldRtnIfTable(SqlMapClient sqlMap,
			String fieldBuffer) throws Exception {

		Map<String, String> requestMap = new HashMap<String, String>();
		
		requestMap.put("REC_STR", fieldBuffer);
		requestMap.put("OP_TM", FILE_TIME_FORMAT);
		
		Map dataMap = new HashMap<String, String>();
		
		String recStr = fieldBuffer;

		dataMap.put("ORG_CD"          , recStr.substring(2   ,8).trim());
		dataMap.put("DEAL_DT"         , recStr.substring(12  ,20).trim());
		dataMap.put("REC_TYP"         , recStr.substring(0   ,2).trim());
		dataMap.put("SUB_ORG_CD"      , recStr.substring(8   ,12).trim());
		dataMap.put("SVC_CD"          , recStr.substring(20  ,21).trim());
		dataMap.put("CRNCY_CD"        , recStr.substring(21  ,24).trim());
		dataMap.put("CASH_DPST_CL_CD" , recStr.substring(24  ,25).trim());
		dataMap.put("PDAY_BAL_BAMT"   , recStr.substring(25  ,40).trim());
		dataMap.put("TDAY_PAY_AMT"    , recStr.substring(40  ,55).trim());
		dataMap.put("TDAY_RFND_AMT"   , recStr.substring(55  ,70).trim());
		dataMap.put("INR_DEAL_AMT"    , recStr.substring(70  ,85).trim());
		dataMap.put("UNRMT_DEDT_AMT"  , recStr.substring(85  ,100).trim());
		dataMap.put("HQ_RMT_AMT"      , recStr.substring(100 ,115).trim());
		dataMap.put("TDAY_BAL_AMT"    , recStr.substring(115 ,130).trim());

		/**
		 *   ERP 송신금액
		 *   본사송금 = 0 인 경우
		 *   당일잔액 - 전일잔액(단, 전일잔액이 0 보다 큰 경우)
		 *   본사송금 = 0 이 아닌 경우
		 *   당일잔액(단, 당일잔액이 0 보다 큰 경우)
		 */
		String amt = recStr.substring(100 ,115);

		BigDecimal hqRmtAmt = new BigDecimal(amt.trim());
		BigDecimal tdayBalAmt = new BigDecimal(recStr.substring(115 ,130).trim());
		BigDecimal pdayBalBamt = new BigDecimal(recStr.substring(25  ,40).trim());
		if(new BigDecimal("0").compareTo(hqRmtAmt) == 0){
			if(new BigDecimal("0").compareTo(pdayBalBamt) > 0){
				dataMap.put("ERP_RMT_AMT"     , tdayBalAmt);
			}else{
				dataMap.put("ERP_RMT_AMT"     , tdayBalAmt.subtract(pdayBalBamt));
			}
		}else{
			if(new BigDecimal("0").compareTo(tdayBalAmt) > 0){
				dataMap.put("ERP_RMT_AMT"     , "0");
			}else{
				dataMap.put("ERP_RMT_AMT"     , tdayBalAmt);
			}
		}

		sqlMap.insert("ACCIFM08100.saveCommCdIf", dataMap);
		
		logMng.writeLogFile("rec_str==>[" + fieldBuffer + "]");
		
		return 1;
	}
	
}
