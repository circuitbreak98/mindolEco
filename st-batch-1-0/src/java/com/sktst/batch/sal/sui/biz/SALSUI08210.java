package com.sktst.batch.sal.sui.biz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;
import java.text.*;


import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : U.Key에서 I/F된 부가상품 가입정보 SAM File을 읽어서 부가서비스판매 자료에 INSERT / UPDATE 한다.</li>
 * <li>작성일 : 2009-03-30 09:00:00</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class SALSUI08210 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSUI08210";
	private static final String USER_ID = "SALSUI0821";
	private static String fileTime      = "";
	private static String workProc      = "";

	// 작업처리에 필요한 상수 설정
//	private static final String VALUE_N     = "N";
	private static final String VALUE_Y     = "Y";
//	private static final String VALUE_ZERO  = "0";
//	private static final String VALUE_ONE   = "1";
//	private static final String VALUE_TWO   = "2";
//	private static final String VALUE_THREE = "3";
	private static final String VALUE_NULL  = "";
	private static final    int ARRAY_COUNT = 10;
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
		
		fileTime = (String)request.get("args1");

		try {
			
			logMng.openLogFile(PROG_ID);
			logMng.writeLogFile("args1 : " + fileTime);

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

		int readCnt    = 0;
		int skipCnt    = 0;
		int insertCnt  = 0;
		int updateCnt  = 0;
		int scrbCnt    = 0;
		int termCnt    = 0;
		int errorCnt   = 0;
		int scrbDtNull = 0;
		int suplNull   = 0;

		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append(fileTime)
			.toString();

		// 자료처리를 위한 변수 선언
		String fAgencyCd     = "";
		String fSubCd        = "";
		String fScrbDt       = "";
		String fScrbTm       = "";
		String fSvcMgmtNum   = "";
		String fMdlCd        = "";
		String fSerNum       = "";
		String fGnrlSaleNo   = "";
		String fIfReqDt      = "";
		String fIfReqSeq     = "";
		String fSuplSvcCd[]  = new String[ARRAY_COUNT]; 
		String fScrbTermCl[] = new String[ARRAY_COUNT];
		String fScrbDt1[]    = new String[ARRAY_COUNT];
		String fTermDt1[]    = new String[ARRAY_COUNT];
		String fScrbDt2[]    = new String[ARRAY_COUNT];
		String fTermDt2[]    = new String[ARRAY_COUNT];

		// 일반판매정보 관련 변수 선언
		String gGnrlSaleNo     = "";
		   int gGnrlSaleChgSeq = 0;
		String gSaleDtm        = "";
//		String gSaleDtlTyp     = "";
//		String gSlNetCd        = "";
//		String gDsNetCd        = "";
//		String gAgencyCd       = "";
        String gScrbDt         = "";
        String gTermDt         = "";
        String gDtClsYn        = "";

        //부가상품 장려금 정보
        String pIdmLmtCd     = "";
        String pEndsPrdCd    = "";

        // 부가서비스판매 INSERT / UPDATE를 위한 변수 선언
		String sPrMnyYn        = "";
		String sScrbDt         = "";
		String sTermDt         = "";
		String sCloseYM        = "";
		String sSaleChgDtm     = "";

		   int sGnrlSaleChgSeq = 0;

		Boolean sUpdateFlag    = false;
		Boolean sMonthClose    = false;


        // QueryForObject를 위한 Map 선언
        Map    resultSale; 

		Map<String, Object> requestMap = new HashMap<String, Object>();
		Map<String, Object> requestMapUpd = new HashMap<String, Object>();


		// 일마감(월마감)일자 조회
		workProc  = "[getMonthClose]";
		resultSale = (Map)     sqlMap.queryForObject("SALSUI08210.getMonthClose", requestMap);
    	sCloseYM   = (String)  resultSale.get("CLOSE_YYYYMM");

		try {

			workProc  = "[BufferedReader]";
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {

					readCnt++;

					// 전문양식에 맞게 읽은 자료를 자른다.
					workProc  = "[Split I/F Record]";
					fAgencyCd   = readLine.substring(  2,   8).trim();	// 대리점코드(D Code)
					fSubCd      = readLine.substring(  8,  12).trim();	// SUB 코드
					fScrbDt     = readLine.substring( 12,  20).trim();	// 가입일자
					fScrbTm     = readLine.substring( 20,  26).trim();	// 가입시간
					fSvcMgmtNum = readLine.substring( 26,  36).trim();	// 서비스관리번호	
					fMdlCd      = readLine.substring( 36,  41).trim();	// 단말기모델코드
					fSerNum     = readLine.substring( 41,  56).trim();	// 단말기일련번호
					fGnrlSaleNo = readLine.substring( 56,  67).trim();	// 일반판매번호
					fIfReqDt    = readLine.substring(497, 505).trim();	// 유지확인 요청일자
					fIfReqSeq   = readLine.substring(505, 510).trim();	// 유지확인 요청순번

					/* 현재는 부가상품 한건씩 I/F 되고 있음 ...
					 * 때문에 Looping 돌리지 않고 한건만 처리하면 됨 !!!
					for(int i = 0; i <= 9; i++) {
                     */
					for(int i = 0; i <= 0; i++) {
						fSuplSvcCd[i]  = readLine.substring( 67 + 43 * i,  77 + 43 * i).trim();	// 부가상품코드
						fScrbTermCl[i] = readLine.substring( 77 + 43 * i,  78 + 43 * i).trim();	// 0:무가입, 1:유지, 2:해지
						fScrbDt1[i]    = readLine.substring( 78 + 43 * i,  86 + 43 * i).trim();	// 가입일자 1
						fTermDt1[i]    = readLine.substring( 86 + 43 * i,  94 + 43 * i).trim();	// 해지일자 1
						fScrbDt2[i]    = readLine.substring( 94 + 43 * i, 102 + 43 * i).trim();	// 가입일자 2
						fTermDt2[i]    = readLine.substring(102 + 43 * i, 110 + 43 * i).trim();	// 해지일자 2
					}

				    // 일반판매정보 조회
					workProc  = "[getSaleInfo]";
				    requestMap.put("GNRL_SALE_NO", fGnrlSaleNo);

				    resultSale = (Map)sqlMap.queryForObject("SALSUI08210.getSaleInfo", requestMap);
				    if (resultSale == null)  {
				    	logMng.writeLogFile("[TSAL_GENERAL_SALE] NOT FOUND [일반판매번호/서비스관리번호/단말기모델/일련번호] ==> ["
				    		                + fGnrlSaleNo + "/" + fSvcMgmtNum + "/" + fMdlCd + "/" + fSerNum + "]");
				    	errorCnt++;

				    	// 부가상품유지확인요청결과에 UPDATE
						workProc  = "[updateSuplProdRslt 01]";
	                	requestMapUpd.put("IF_REQ_DT",      fIfReqDt);
	                	requestMapUpd.put("IF_REQ_SEQ",     fIfReqSeq);
	                	requestMapUpd.put("UKEY_AGENCY_CD", fAgencyCd);
	                	requestMapUpd.put("UKEY_SUB_CD",    fSubCd);
	                	requestMapUpd.put("SCRB_DT",        fScrbDt);
	                	requestMapUpd.put("SCRB_TM",        fScrbTm);
	                	requestMapUpd.put("SVC_MGMT_NUM",   fSvcMgmtNum);
	                	requestMapUpd.put("EQP_MDL_CD",     fMdlCd);
	                	requestMapUpd.put("EQP_SER_NUM",    fSerNum);
	                	requestMapUpd.put("GNRL_SALE_NO",   fGnrlSaleNo);
	                	requestMapUpd.put("SUPL_PROD_CD",   fSuplSvcCd[0]);
	                	requestMapUpd.put("SCRB_TERM_CL",   fScrbTermCl[0]);
	                	requestMapUpd.put("SCRB_DT1",       fScrbDt1[0]);
	                	requestMapUpd.put("TERM_DT1",       fTermDt1[0]);
	                	requestMapUpd.put("SCRB_DT2",       fScrbDt2[0]);
	                	requestMapUpd.put("TERM_DT2",       fTermDt2[0]);
	                	requestMapUpd.put("PS_APND_YN",     "N");
	                	requestMapUpd.put("RMKS",           "[TSAL_GENERAL_SALE] NOT FOUND");
	                	requestMapUpd.put("USER_ID",        USER_ID);

	                	sqlMap.update("SALSUI08210.updateSuplProdRslt", requestMapUpd);
				    	continue;
				    }

	        	    gGnrlSaleNo     = (String)      resultSale.get("GNRL_SALE_NO");
		        	gGnrlSaleChgSeq = ((BigDecimal) resultSale.get("GNRL_SALE_CHG_SEQ")).intValue();
	        	    gSaleDtm        = (String)      resultSale.get("SALE_DTM");
		        	gDtClsYn        = (String)      resultSale.get("DT_CLS_YN");

					sUpdateFlag     = false;

					//for (int i = 0; i <= 9; i++) {
					for (int i = 0; i <= 0; i++) {

						if (fSuplSvcCd[i].trim() == null || VALUE_NULL.equals(fSuplSvcCd[i].trim())) {
							suplNull++;

							workProc  = "[updateSuplProdRslt 02]";
		                	requestMapUpd.put("IF_REQ_DT",      fIfReqDt);
		                	requestMapUpd.put("IF_REQ_SEQ",     fIfReqSeq);
		                	requestMapUpd.put("UKEY_AGENCY_CD", fAgencyCd);
		                	requestMapUpd.put("UKEY_SUB_CD",    fSubCd);
		                	requestMapUpd.put("SCRB_DT",        fScrbDt);
		                	requestMapUpd.put("SCRB_TM",        fScrbTm);
		                	requestMapUpd.put("SVC_MGMT_NUM",   fSvcMgmtNum);
		                	requestMapUpd.put("EQP_MDL_CD",     fMdlCd);
		                	requestMapUpd.put("EQP_SER_NUM",    fSerNum);
		                	requestMapUpd.put("GNRL_SALE_NO",   fGnrlSaleNo);
		                	requestMapUpd.put("SUPL_PROD_CD",   fSuplSvcCd[0]);
		                	requestMapUpd.put("SCRB_TERM_CL",   fScrbTermCl[0]);
		                	requestMapUpd.put("SCRB_DT1",       fScrbDt1[0]);
		                	requestMapUpd.put("TERM_DT1",       fTermDt1[0]);
		                	requestMapUpd.put("SCRB_DT2",       fScrbDt2[0]);
		                	requestMapUpd.put("TERM_DT2",       fTermDt2[0]);
		                	requestMapUpd.put("PS_APND_YN",     "N");
		                	requestMapUpd.put("RMKS",           "T.Key+ 대리점 자료 아님");
		                	requestMapUpd.put("USER_ID",        USER_ID);

		                	sqlMap.update("SALSUI08210.updateSuplProdRslt", requestMapUpd);

							continue;
						}
						if (fTermDt1[i].trim() == null || VALUE_NULL.equals(fTermDt1[i].trim())) {
							scrbDtNull++;

							workProc  = "[updateSuplProdRslt 02]";
		                	requestMapUpd.put("IF_REQ_DT",      fIfReqDt);
		                	requestMapUpd.put("IF_REQ_SEQ",     fIfReqSeq);
		                	requestMapUpd.put("UKEY_AGENCY_CD", fAgencyCd);
		                	requestMapUpd.put("UKEY_SUB_CD",    fSubCd);
		                	requestMapUpd.put("SCRB_DT",        fScrbDt);
		                	requestMapUpd.put("SCRB_TM",        fScrbTm);
		                	requestMapUpd.put("SVC_MGMT_NUM",   fSvcMgmtNum);
		                	requestMapUpd.put("EQP_MDL_CD",     fMdlCd);
		                	requestMapUpd.put("EQP_SER_NUM",    fSerNum);
		                	requestMapUpd.put("GNRL_SALE_NO",   fGnrlSaleNo);
		                	requestMapUpd.put("SUPL_PROD_CD",   fSuplSvcCd[0]);
		                	requestMapUpd.put("SCRB_TERM_CL",   fScrbTermCl[0]);
		                	requestMapUpd.put("SCRB_DT1",       fScrbDt1[0]);
		                	requestMapUpd.put("TERM_DT1",       fTermDt1[0]);
		                	requestMapUpd.put("SCRB_DT2",       fScrbDt2[0]);
		                	requestMapUpd.put("TERM_DT2",       fTermDt2[0]);
		                	requestMapUpd.put("PS_APND_YN",     "N");
		                	requestMapUpd.put("RMKS",           "해지정보 없음(유지상태 지속) ");
		                	requestMapUpd.put("USER_ID",        USER_ID);

		                	sqlMap.update("SALSUI08210.updateSuplProdRslt", requestMapUpd);
							continue;
						}

						// 최종 가입 및 해지일자를 작업변수에 할당
		                if (fScrbDt2[i] == null || VALUE_NULL.equals(fScrbDt2[i].trim())) {
		                	sScrbDt = fScrbDt1[i].trim();
		                } else {
		                	sScrbDt = fScrbDt2[i].trim();
		                }
		                if (fTermDt2[i] == null || VALUE_NULL.equals(fTermDt2[i].trim())) {
		                	sTermDt = fTermDt1[i].trim();
		                } else {
		                	sTermDt = fTermDt2[i].trim();
		                }

					    // 부가서비스판매정보 조회
					    requestMap.put("GNRL_SALE_NO", fGnrlSaleNo);
					    requestMap.put("SUPL_SVC_CD",  fSuplSvcCd[i]);

						workProc  = "[getSuplInfo]";
					    resultSale = (Map)sqlMap.queryForObject("SALSUI08210.getSuplInfo", requestMap);
					    if (resultSale == null) {
					    	logMng.writeLogFile("[TSAL_SUPLSERVICE] Not Found [일반판매번호/부가상품코드] ==> [" + gGnrlSaleNo + "/" + fSuplSvcCd[i] + "]");
		        	    	skipCnt++;

							workProc  = "[updateSuplProdRslt 03]";
		                	requestMapUpd.put("IF_REQ_DT",      fIfReqDt);
		                	requestMapUpd.put("IF_REQ_SEQ",     fIfReqSeq);
		                	requestMapUpd.put("UKEY_AGENCY_CD", fAgencyCd);
		                	requestMapUpd.put("UKEY_SUB_CD",    fSubCd);
		                	requestMapUpd.put("SCRB_DT",        fScrbDt);
		                	requestMapUpd.put("SCRB_TM",        fScrbTm);
		                	requestMapUpd.put("SVC_MGMT_NUM",   fSvcMgmtNum);
		                	requestMapUpd.put("EQP_MDL_CD",     fMdlCd);
		                	requestMapUpd.put("EQP_SER_NUM",    fSerNum);
		                	requestMapUpd.put("GNRL_SALE_NO",   fGnrlSaleNo);
		                	requestMapUpd.put("SUPL_PROD_CD",   fSuplSvcCd[0]);
		                	requestMapUpd.put("SCRB_TERM_CL",   fScrbTermCl[0]);
		                	requestMapUpd.put("SCRB_DT1",       fScrbDt1[0]);
		                	requestMapUpd.put("TERM_DT1",       fTermDt1[0]);
		                	requestMapUpd.put("SCRB_DT2",       fScrbDt2[0]);
		                	requestMapUpd.put("TERM_DT2",       fTermDt2[0]);
		                	requestMapUpd.put("PS_APND_YN",     "N");
		                	requestMapUpd.put("RMKS",           "[TSAL_SUPLSERVICE] Not Found");
		                	requestMapUpd.put("USER_ID",        USER_ID);

		                	sqlMap.update("SALSUI08210.updateSuplProdRslt", requestMapUpd);
		        	    	continue;
					    }

					    gScrbDt    = (String)  resultSale.get("SVC_STA_DT");
					    gTermDt    = (String) (resultSale.get("SVC_END_DT") == null ? "" : resultSale.get("SVC_END_DT"));

					    // 영업의 가입/해지 정보와 I/F된 가입/해지 정보가 같을 경우 Skip...
					    if (sScrbDt.equals(gScrbDt) && sTermDt.equals(gTermDt)) {
					    	logMng.writeLogFile("[TSAL_SUPLSERVICE] Not Found [일반판매번호/부가상품코드] ==> [" + gGnrlSaleNo + "/" + fSuplSvcCd[i] + "]");
		        	    	skipCnt++;

							workProc  = "[updateSuplProdRslt 04]";
		                	requestMapUpd.put("IF_REQ_DT",      fIfReqDt);
		                	requestMapUpd.put("IF_REQ_SEQ",     fIfReqSeq);
		                	requestMapUpd.put("UKEY_AGENCY_CD", fAgencyCd);
		                	requestMapUpd.put("UKEY_SUB_CD",    fSubCd);
		                	requestMapUpd.put("SCRB_DT",        fScrbDt);
		                	requestMapUpd.put("SCRB_TM",        fScrbTm);
		                	requestMapUpd.put("SVC_MGMT_NUM",   fSvcMgmtNum);
		                	requestMapUpd.put("EQP_MDL_CD",     fMdlCd);
		                	requestMapUpd.put("EQP_SER_NUM",    fSerNum);
		                	requestMapUpd.put("GNRL_SALE_NO",   fGnrlSaleNo);
		                	requestMapUpd.put("SUPL_PROD_CD",   fSuplSvcCd[0]);
		                	requestMapUpd.put("SCRB_TERM_CL",   fScrbTermCl[0]);
		                	requestMapUpd.put("SCRB_DT1",       fScrbDt1[0]);
		                	requestMapUpd.put("TERM_DT1",       fTermDt1[0]);
		                	requestMapUpd.put("SCRB_DT2",       fScrbDt2[0]);
		                	requestMapUpd.put("TERM_DT2",       fTermDt2[0]);
		                	requestMapUpd.put("PS_APND_YN",     "N");
		                	requestMapUpd.put("RMKS",           "부가상품 가입/해지 정보 변동 없음");
		                	requestMapUpd.put("USER_ID",        USER_ID);

		                	sqlMap.update("SALSUI08210.updateSuplProdRslt", requestMapUpd);
		        	    	continue;					    	
					    }

		                // 정책정보에 부가상품 장려금이 등록되어 있고 일반판매에 부가상품이 이미 등록된 경우
		                // 해지 후 재 가입인 경우를 생각하여 부가서비스판매(TSAL_SUPLSERVICE) 정보를 Insert / Update한다.
		                if (!gScrbDt.equals(sScrbDt) || !gTermDt.equals(sTermDt)) {

			            	// 월마감 및 추정 일마감 Check하여 판매변경이력 증가 여부 판단
			            	sMonthClose = false;
			            	if (VALUE_Y.equals(gDtClsYn)) {
			            		sMonthClose = true;
			            	}
			            	// 최종 마감월과 최초 판매년월을 Long Type으로 변환하여 
			            	if ( Long.parseLong(sCloseYM) >= Long.parseLong(gSaleDtm.substring(0, 6).trim())) {
			            		sMonthClose = true;
			            	}

				        	// 추정 일마감된 자료 또는 부가서비스 가입월과 해지월이 다른 경우만 순번 추가
			            	if (sMonthClose || !sScrbDt.substring(0, 6).equals(sTermDt.substring(0, 6))) {
				        	    sGnrlSaleChgSeq = gGnrlSaleChgSeq + 1;  // 일반판매변경순번 증가...
			            	} else {
			            		sGnrlSaleChgSeq = gGnrlSaleChgSeq;      // 일반판매변경순번 증가하지 않음...	
			            		sUpdateFlag     = true;					// 이력을 생성시키지 않기 위해 Flag Setting ...
			            	}

							workProc  = "[saveSuplService]";
		                	requestMapUpd.put("IDM_LMT_CD",        pIdmLmtCd);
		                	requestMapUpd.put("ENDS_PRD_CD",       pEndsPrdCd);
		                	requestMapUpd.put("PR_MNY_YN",         sPrMnyYn);
		                	requestMapUpd.put("SVC_STA_DT",        sScrbDt);
		                	requestMapUpd.put("SVC_END_DT",        sTermDt);
		                	requestMapUpd.put("USER_ID",           USER_ID);
		                	requestMapUpd.put("GNRL_SALE_NO",      gGnrlSaleNo);
		                	requestMapUpd.put("GNRL_SALE_CHG_SEQ", sGnrlSaleChgSeq);
		                	requestMapUpd.put("SUPL_SVC_CD",       fSuplSvcCd[0]);

		                	sqlMap.update("SALSUI08210.saveSuplService", requestMapUpd);

		                	updateCnt++;

		                	// 부가상품유지확인요청결과에 INSERT
							workProc  = "[updateSuplProdRslt 07]";
		                	requestMapUpd.put("IF_REQ_DT",      fIfReqDt);
		                	requestMapUpd.put("IF_REQ_SEQ",     fIfReqSeq);
		                	requestMapUpd.put("UKEY_AGENCY_CD", fAgencyCd);
		                	requestMapUpd.put("UKEY_SUB_CD",    fSubCd);
		                	requestMapUpd.put("SCRB_DT",        fScrbDt);
		                	requestMapUpd.put("SCRB_TM",        fScrbTm);
		                	requestMapUpd.put("SVC_MGMT_NUM",   fSvcMgmtNum);
		                	requestMapUpd.put("EQP_MDL_CD",     fMdlCd);
		                	requestMapUpd.put("EQP_SER_NUM",    fSerNum);
		                	requestMapUpd.put("GNRL_SALE_NO",   fGnrlSaleNo);
		                	requestMapUpd.put("SUPL_PROD_CD",   fSuplSvcCd[0]);
		                	requestMapUpd.put("SCRB_TERM_CL",   fScrbTermCl[0]);
		                	requestMapUpd.put("SCRB_DT1",       fScrbDt1[0]);
		                	requestMapUpd.put("TERM_DT1",       fTermDt1[0]);
		                	requestMapUpd.put("SCRB_DT2",       fScrbDt2[0]);
		                	requestMapUpd.put("TERM_DT2",       fTermDt2[0]);
		                	requestMapUpd.put("PS_APND_YN",     "Y");
		                	requestMapUpd.put("RMKS",           "");
		                	requestMapUpd.put("USER_ID",        USER_ID);

		                	sqlMap.update("SALSUI08210.updateSuplProdRslt", requestMapUpd);

		                	// 일반판매 변경이력 생성
		                	if (sUpdateFlag == false) {

		                		// 부가상품변경일시를 Setting - 부가상품유지확인 요청일자 + 23:59:59
		                		sSaleChgDtm = fIfReqDt + "235959";
				        	    
		                		sUpdateFlag = true;
								workProc  = "[Sales History Create]";
				            	requestMapUpd.put("GNRL_SALE_NO", gGnrlSaleNo);    		// 일반판매번호
				            	requestMapUpd.put("OLD_CHG_SEQ",  gGnrlSaleChgSeq);		// 변경전 일반판매변경순번
				            	requestMapUpd.put("NEW_CHG_SEQ",  sGnrlSaleChgSeq);		// 변경후 일반판매변경순번
				            	requestMapUpd.put("SALE_CHG_DTM", sSaleChgDtm);			// 판매변경일자
				            	requestMapUpd.put("USER_ID",      USER_ID);				// 변경자 ID

					            // 일반판매 - 일반판매변경순번증가, 변경이력구분 '06'(부가서비스변경)
				            	sqlMap.update("SALSUI08210.addGeneralSale", requestMapUpd);
					            
					            // 단말기판매 - 변경순번 증가
				            	sqlMap.update("SALSUI08210.addEquipmentSale", requestMapUpd);

					            // USIM판매 - 변경순번 증가
				            	sqlMap.update("SALSUI08210.addUsimSale", requestMapUpd);

					            // 중고반납기기 - 변경순번 증가
				            	sqlMap.update("SALSUI08210.addOldrtnEquipment", requestMapUpd);

					            // 할부매출 - 변경순번 증가
				            	sqlMap.update("SALSUI08210.addAllotSale", requestMapUpd);

					            // SKT예수금 - 변경순번 증가
				            	sqlMap.update("SALSUI08210.addSktPrprc", requestMapUpd);

					            // 약정보조금 - 변경순번 증가
				            	sqlMap.update("SALSUI08210.addAgreementAstamt", requestMapUpd);

					            // 현금매출 - 변경순번 증가
				            	sqlMap.update("SALSUI08210.addCashSale", requestMapUpd);

					            // 수납정보 - 변경순번 증가
				            	sqlMap.update("SALSUI08210.addPayment", requestMapUpd);

					            // 판매수수료 - 변경순번 증가
				            	sqlMap.update("SALSUI08210.addSaleCmms", requestMapUpd);

					            // 장려금 - 변경순번 증가
				            	sqlMap.update("SALSUI08210.addPromotionMoney", requestMapUpd);

					            // OCB/MCARD - 변경순번 증가
				            	sqlMap.update("SALSUI08210.addOcbmcard", requestMapUpd);

					            // 온라인수납 - 변경순번 증가
				            	sqlMap.update("SALSUI08210.addOnlinePayment", requestMapUpd);

					            // 온라인수수료 - 변경순번 증가
				            	sqlMap.update("SALSUI08210.addOnlineCmms", requestMapUpd);
		                	}

		                }	

					}

				} catch (Exception ex) {
					logMng.writeLogFile("ERRCODE:F");
					logMng.writeLogFile(ex.getMessage());
					logMng.writeLogFile(workProc + " [" + readLine + "]");
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
		logMng.writeLogFile("------------------------------------------------------------");
		logMng.writeLogFile("File Read Count : " + readCnt);
		logMng.writeLogFile("   Insert Count : " + insertCnt);
		logMng.writeLogFile("   Update Count : " + updateCnt);
		logMng.writeLogFile("     Scrb Count : " + scrbCnt);
		logMng.writeLogFile("     Term Count : " + termCnt);
		logMng.writeLogFile("------------------------------------------------------------");
		logMng.writeLogFile(" Supl Svc Count : " + suplNull);
		logMng.writeLogFile(" Scrb Dtm Count : " + scrbDtNull);
		logMng.writeLogFile("     Skip Count : " + skipCnt);
		logMng.writeLogFile("    Error Count : " + errorCnt);
		logMng.writeLogFile("openDataFileAddDB method end......");
	}
}