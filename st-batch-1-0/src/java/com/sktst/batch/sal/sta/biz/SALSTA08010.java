package com.sktst.batch.sal.sta.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.math.*;


import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <li>업무 그룹명 : 프로젝트/SKTST/영업/U.Key통계 I/F - 일일판매통계</li>
 * <li>설 명 : U.Key에서 I/F된 통계자료를 T.Key+ Table에 INSERT 한다.</li>
 * <li>작성일 : 2010-04-01 09:00:00</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class SALSTA08010 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSTA08010";
	private static final String USER_ID = "SALSTA0801";
	private static String fileName = "";
	private static String ukeyIfType = "98";

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
		
		if (log.isDebugEnabled()) {
			log.debug(PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();
		
		fileName = (String)request.get("args1");

		try {
			
			logMng.openLogFile(PROG_ID);
			logMng.writeLogFile("args1 : " + fileName);

			// 전문양식이 맞는지 Check
            if (ukeyIfType.equals(fileName.substring(6, 8) )) {

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

            } else {
            	logMng.writeLogFile("Correct IF Type is '98'.. But '" + fileName.substring(6, 8) + "' type accepted...");
            }

		} catch(Exception e) {
			logMng.writeLogFile("execute Exception : " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
			}

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

		int readCnt     = 0;	// I/F된 sam file read count
		int insertCnt   = 0; 	// 부가서비스 insert count
		int errorCnt    = 0;    // 오류건수

		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append(fileName)
			.toString();

		// Input File 자료처리를 위한 변수 선언
		String fAgencyCd     = "";
		String fSubCd        = "";
		String fStrdDt       = "";
		String fNewScrb      = "";
		String fMnpScrb      = "";
		String fNewScrbTot   = "";
		String fGnrlTerm     = "";
		String fMnpTerm      = "";
		String fAuthTerm     = "";
		String fDealIncre    = "";
		String fDealDecre    = "";
		String fEqpCnvIncre  = "";
		String fEqpCnvDecre  = "";
		String fSumCount     = "";

		// Table 저장을 위한 변수 선언
		String pAgencyPlc     = "";
		String pProcPlc       = "";


        // QueryForObject를 위한 Map 선언
        Map    resultDeal;

		Map<String, Object> requestMap    = new HashMap<String, Object>();
		Map<String, Object> requestMapUpd = new HashMap<String, Object>();

		String sCurdate = dataPath.substring(21, 29);
		logMng.writeLogFile(" U.Key I/F SAM 일자 [" + sCurdate + "]");

/*
		requestMap.put("FILE_DT", sCurdate);
		resultDeal      = (Map)sqlMap.queryForObject("SALSTA08010.getYesterDay", requestMap);
		sCurdate        = (String)  resultDeal.get("WORK_DT");                            // U.Key I/F File 기준 전일 획득
*/

		// 이미 처리된 자료인 경우 재처리를 위해 이전 작업 자료 삭제...
	 	requestMap.put("STRD_DT", sCurdate);
		sqlMap.update("SALSTA08010.deleteUkeyDailySale", requestMap);

		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {

					readCnt++;

					// 전문양식에 맞게 읽은 자료를 자른다.
					fAgencyCd    = readLine.substring(  2,   8).trim();
					fSubCd       = readLine.substring(  8,  12).trim();
					fStrdDt      = readLine.substring( 12,  20).trim();
					fNewScrb     = readLine.substring( 20,  35).trim();
					fMnpScrb     = readLine.substring( 35,  50).trim();
					fNewScrbTot  = readLine.substring( 50,  65).trim();
					fGnrlTerm    = readLine.substring( 65,  80).trim();
					fMnpTerm     = readLine.substring( 80,  95).trim();
					fAuthTerm    = readLine.substring( 95, 110).trim();
					fDealIncre   = readLine.substring(110, 125).trim();
					fDealDecre   = readLine.substring(125, 140).trim();
					fEqpCnvIncre = readLine.substring(140, 155).trim();
					fEqpCnvDecre = readLine.substring(155, 170).trim();
					fSumCount    = readLine.substring(170, 185).trim();

					// 대리점 정보 조회
					requestMap.put("UKEY_AGENCY_CD", fAgencyCd);
					requestMap.put("EFF_DT",         fStrdDt);
					resultDeal = (Map)sqlMap.queryForObject("SALSTA08010.getDealAgencyInfo", requestMap);

					if (resultDeal == null)  {
						pAgencyPlc = "";
					} else {
						pAgencyPlc = (String)  resultDeal.get("DEAL_CO_CD");
					}

					// 처리점(판매점) 정보 조회
					requestMap.put("UKEY_AGENCY_CD", fAgencyCd);
					requestMap.put("UKEY_SUB_CD",    fSubCd);
					requestMap.put("EFF_DT",         fStrdDt);
					resultDeal = (Map)sqlMap.queryForObject("SALSTA08010.getDealPlcInfo", requestMap);

					if (resultDeal == null)  {
						pProcPlc = "";
					} else {
						pProcPlc = (String)  resultDeal.get("DEAL_CO_CD");
					}

            		// U.Key 일일판매통계 I/F Table에 Insert
	            	requestMapUpd.put("STRD_DT",           fStrdDt);
	            	requestMapUpd.put("UKEY_AGENCY_CD",    fAgencyCd);
	            	requestMapUpd.put("UKEY_SUB_CD",       fSubCd);
	            	requestMapUpd.put("AGENCY_CD",         pAgencyPlc);
	            	requestMapUpd.put("PROC_PLC",          pProcPlc);
	            	requestMapUpd.put("NEW_SCRB_CNT",      fNewScrb);
	            	requestMapUpd.put("MNP_SCRB_CNT",      fMnpScrb);
	            	requestMapUpd.put("NEW_SCRB_TOT",      fNewScrbTot);
	            	requestMapUpd.put("GNRL_TERM_CNT",     fGnrlTerm);
                    requestMapUpd.put("MNP_TERM_CNT",      fMnpTerm);
	              	requestMapUpd.put("AUTH_TERM_CNT",     fAuthTerm);
	            	requestMapUpd.put("DEAL_INCRE_CNT",    fDealIncre);
	            	requestMapUpd.put("DEAL_DECRE_CNT",    fDealDecre);
	            	requestMapUpd.put("EQP_CNV_INCRE_CNT", fEqpCnvIncre);
	            	requestMapUpd.put("EQP_CNV_DECRE_CNT", fEqpCnvDecre);
	            	requestMapUpd.put("SUM_TOTAL_CNT",     fSumCount);
	            	requestMapUpd.put("USER_ID",           USER_ID);

	            	sqlMap.update("SALSTA08010.addUkeyDailySale", requestMapUpd);

	            	insertCnt++;

				} catch (Exception ex) {
					logMng.writeLogFile("ERRCODE:E ");
					logMng.writeLogFile(ex.getMessage());
					logMng.writeLogFile("Error Rec==>[" + readLine + "]");
					errorCnt++;
					if (log.isDebugEnabled()) {
						log.debug(ex.getMessage());
					}
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

		logMng.writeLogFile("openDataFileAddDB method end......");
		logMng.writeLogFile(" ");

		logMng.writeLogFile("------------------------------------------------------------");
		logMng.writeLogFile("    File Read Count : " + readCnt);
		logMng.writeLogFile("       Insert Count : " + insertCnt);
		logMng.writeLogFile("------------------------------------------------------------");
	}
}