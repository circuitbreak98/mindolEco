package com.sktst.batch.sal.sta.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <li>업무 그룹명 : 프로젝트/SKTST/영업/U.Key통계 I/F - T할부 T기본 가입통계</li>
 * <li>설 명      : U.Key에서 I/F된 통계자료를 T.Key+ Table에 INSERT 한다.</li>
 * <li>작성일     : 2010-07-19 09:00:00</li>
 * </ul>
 *
 *
 * @author 이영진 (leeyoungjin)
 */
public class SALSTA08180 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSTA08180";
	private static final String USER_ID = "SALSTA0818";
	private static String fileName = "";
	private static String ukeyIfType = "A8";

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
            	logMng.writeLogFile("Correct IF Type is 'A8'.. But '" + fileName.substring(6, 8) + "' type accepted...");
            }

			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
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

		int readCnt     = 0;	// I/F된 sam file read count
		int insertCnt   = 0; 	// 부가서비스 insert count
		int errorCnt    = 0;    // 오류건수

		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append(fileName)
			.toString();

		// Input File 자료처리를 위한 변수 선언
		String fScrbTypCd			= "";
		
		String fAgencyCd       = "";
		String fSubCd          = "";
		String fStrdDt         = "";
		String fTotCnt1				= "";
		String fNewScrbCnt1         = "";
		String fKtfsktNpScrbCnt1    = "";
		String fLgtsktNpScrbCnt1    = "";
		String fSumCnt1             = "";
		String fAgrmtDcAddCnt1      = "";
		String fNewScrbCnt2         = "";
		String fKtfsktNpScrbCnt2    = "";
		String fLgtsktNpScrbCnt2    = "";
		String fSumCnt2             = "";
		String fAgrmtDcAddCnt2      = "";
		String fNewScrbCnt9         = "";
		String fKtfsktNpScrbCnt9    = "";
		String fLgtsktNpScrbCnt9    = "";
		String fSumCnt3             = "";

		String fTotCnt2             = "";
		String fEqpRtnCnt1          = "";
		String fExclEqpRtnCnt1      = "";
		String fEqpUnrtnCnt1        = "";
		String fCumCnt4             = "";
		String fEqpDcAddUnrtnCnt1   = "";
		String fEqpRtnCnt2          = "";
		String fExclEqpRtnCnt2      = "";
		String fEqpUnrtnCnt2        = "";
		String fCumCnt5             = "";
		String fEqpDcAddUnrtnCnt2   = "";
		
		// Table 저장을 위한 변수 선언
		String pAgencyPlc     = "";
		String pProcPlc       = "";


        // QueryForObject를 위한 Map 선언
        Map    resultDeal;

		Map<String, Object> requestMap    = new HashMap<String, Object>();
		Map<String, Object> requestMapUpd = new HashMap<String, Object>();

		String sCurdate = dataPath.substring(12, 20);
		logMng.writeLogFile(" U.Key I/F SAM 일자 [" + sCurdate + "]");

		// 이미 처리된 자료인 경우 재처리를 위해 이전 작업 자료 삭제...
	 	requestMap.put("STRD_DT", sCurdate);
		sqlMap.update("SALSTA08180.deleteUkeySuplProd", requestMap);
		sqlMap.update("SALSTA08180.deleteUkeySuplProd2", requestMap);

		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {

					readCnt++;

					// 전문양식에 맞게 읽은 자료를 자른다.
					fAgencyCd       = readLine.substring(   2,   8).trim();
					fSubCd          = readLine.substring(   8,  12).trim();
					fStrdDt         = readLine.substring(  12,  20).trim();
					fScrbTypCd		= readLine.substring(  20,  21).trim();
					
					if ("1".equals(fScrbTypCd)) {
						fTotCnt1			 = readLine.substring(  21,  32).trim();
						fNewScrbCnt1         = readLine.substring(  32,  43).trim();
						fKtfsktNpScrbCnt1    = readLine.substring(  43,  54).trim();
						fLgtsktNpScrbCnt1    = readLine.substring(  54,  65).trim();
						fSumCnt1             = readLine.substring(  65,  76).trim();
						fAgrmtDcAddCnt1      = readLine.substring(  76,  87).trim();
						fNewScrbCnt2         = readLine.substring(  87,  98).trim();
						fKtfsktNpScrbCnt2    = readLine.substring(  98,  109).trim();
						fLgtsktNpScrbCnt2    = readLine.substring(  109,  120).trim();
						fSumCnt2             = readLine.substring(  120,  131).trim();
						fAgrmtDcAddCnt2      = readLine.substring(  131,  142).trim();
						fNewScrbCnt9         = readLine.substring(  142,  153).trim();
						fKtfsktNpScrbCnt9    = readLine.substring(  153,  164).trim();
						fLgtsktNpScrbCnt9    = readLine.substring(  164,  175).trim();
						fSumCnt3             = readLine.substring(  175,  186).trim();
					} else {
						fTotCnt2             = readLine.substring(  21,  32).trim();
						fEqpRtnCnt1          = readLine.substring(  32,  43).trim();
						fExclEqpRtnCnt1      = readLine.substring(  43,  54).trim();
						fEqpUnrtnCnt1        = readLine.substring(  54,  65).trim();
						fCumCnt4             = readLine.substring(  65,  76).trim();
						fEqpDcAddUnrtnCnt1   = readLine.substring(  76,  87).trim();
						fEqpRtnCnt2          = readLine.substring(  87,  98).trim();
						fExclEqpRtnCnt2      = readLine.substring(  98,  109).trim();
						fEqpUnrtnCnt2        = readLine.substring(  109,  120).trim();
						fCumCnt5             = readLine.substring(  120,  131).trim();
						fEqpDcAddUnrtnCnt2   = readLine.substring(  131,  142).trim();
					}
					
					// 대리점 정보 조회
					requestMap.put("UKEY_AGENCY_CD", fAgencyCd);
					requestMap.put("EFF_DT",         fStrdDt);
					resultDeal = (Map)sqlMap.queryForObject("SALSTA08180.getDealAgencyInfo", requestMap);

					if (resultDeal == null)  {
						pAgencyPlc = "";
					} else {
						pAgencyPlc = (String)  resultDeal.get("DEAL_CO_CD");
					}

					// 처리점(판매점) 정보 조회
					requestMap.put("UKEY_AGENCY_CD", fAgencyCd);
					requestMap.put("UKEY_SUB_CD",    fSubCd);
					requestMap.put("EFF_DT",         fStrdDt);
					resultDeal = (Map)sqlMap.queryForObject("SALSTA08180.getDealPlcInfo", requestMap);

					if (resultDeal == null)  {
						pProcPlc = "";
					} else {
						pProcPlc = (String)  resultDeal.get("DEAL_CO_CD");
					}

            		// U.Key 번호이동통계 I/F Table에 Insert
	            	requestMapUpd.put("STRD_DT",              fStrdDt);
	            	requestMapUpd.put("UKEY_AGENCY_CD",       fAgencyCd);
	            	requestMapUpd.put("UKEY_SUB_CD",          fSubCd);
	            	requestMapUpd.put("AGENCY_CD",            pAgencyPlc);
	            	requestMapUpd.put("PROC_PLC",             pProcPlc);
	            	
	            	if ("1".equals(fScrbTypCd)) {
	            		requestMapUpd.put("SCRB_TYP_CD", fScrbTypCd);
	            		requestMapUpd.put("TOT_CNT1", fTotCnt1);
	            		requestMapUpd.put("NEW_SCRB_CNT1", fNewScrbCnt1);
	            		requestMapUpd.put("KTFSKT_NP_SCRB_CNT1", fKtfsktNpScrbCnt1);
	            		requestMapUpd.put("LGTSKT_NP_SCRB_CNT1", fLgtsktNpScrbCnt1);
	            		requestMapUpd.put("SUM_CNT1", fSumCnt1);
	            		requestMapUpd.put("AGRMT_DC_ADD_CNT1", fAgrmtDcAddCnt1);
	            		requestMapUpd.put("NEW_SCRB_CNT2", fNewScrbCnt2);
	            		requestMapUpd.put("KTFSKT_NP_SCRB_CNT2", fKtfsktNpScrbCnt2);
	            		requestMapUpd.put("LGTSKT_NP_SCRB_CNT2", fLgtsktNpScrbCnt2);
	            		requestMapUpd.put("SUM_CNT2", fSumCnt2);
	            		requestMapUpd.put("AGRMT_DC_ADD_CNT2", fAgrmtDcAddCnt2);
	            		requestMapUpd.put("NEW_SCRB_CNT9", fNewScrbCnt9);
	            		requestMapUpd.put("KTFSKT_NP_SCRB_CNT9", fKtfsktNpScrbCnt9);
	            		requestMapUpd.put("LGTSKT_NP_SCRB_CNT9", fLgtsktNpScrbCnt9);
	            		requestMapUpd.put("SUM_CNT3", fSumCnt3);
					} else {
						requestMapUpd.put("SCRB_TYP_CD", fScrbTypCd);
						requestMapUpd.put("TOT_CNT2", fTotCnt2);
						requestMapUpd.put("EQP_RTN_CNT1", fEqpRtnCnt1);
						requestMapUpd.put("EXCL_EQP_RTN_CNT1", fExclEqpRtnCnt1);
						requestMapUpd.put("EQP_UNRTN_CNT1", fEqpUnrtnCnt1);
						requestMapUpd.put("CUM_CNT4", fCumCnt4);
						requestMapUpd.put("EQP_DC_ADD_UNRTN_CNT1", fEqpDcAddUnrtnCnt1);
						requestMapUpd.put("EQP_RTN_CNT2", fEqpRtnCnt2);
						requestMapUpd.put("EXCL_EQP_RTN_CNT2", fExclEqpRtnCnt2);
						requestMapUpd.put("EQP_UNRTN_CNT2", fEqpUnrtnCnt2);
						requestMapUpd.put("CUM_CNT5", fCumCnt5);
						requestMapUpd.put("EQP_DC_ADD_UNRTN_CNT2", fEqpDcAddUnrtnCnt2);
					}
	            	
	            	requestMapUpd.put("USER_ID",              USER_ID);

	            	if ("1".equals(fScrbTypCd)) {
	            		sqlMap.update("SALSTA08180.addUkeySuplProd", requestMapUpd);
					} else {
						sqlMap.update("SALSTA08180.addUkeySuplProd2", requestMapUpd);
					}
	            	
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