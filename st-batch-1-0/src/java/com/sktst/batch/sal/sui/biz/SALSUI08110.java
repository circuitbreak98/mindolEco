package com.sktst.batch.sal.sui.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.text.*;
import java.math.*;


import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : U.Key에서 I/F된 부가상품 가입정보 SAM File을 읽어서 부가서비스판매 자료에 INSERT / UPDATE 한다.</li>
 * <li>작성일 : 2009-03-30 09:00:00</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class SALSUI08110 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSUI08110";
	private static final String USER_ID = "SALSUI0811";
	private static String fileTime = "";

	// 작업처리에 필요한 상수 설정
	private static String valueY     = "Y";
	private static String valueN     = "N";
	private static String valueZero  = "0";
	private static String valueOne   = "1";
	private static String valueTwo   = "2";
	private static String valueThree = "3";
	private static String valueNull  =  "";

	private static    int valueNotFound =  -1;

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

		int readCnt     = 0;	// I/F된 sam file read count
		int insertCnt   = 0; 	// 부가서비스 insert count
		int updateCnt   = 0;	// 부가서비스 update count
		int hstIsrtCnt  = 0;    // 변경이력 생성 건수
		int skipSaleNo  = 0;    // 일반판매번호 Not Found Skip
		int skipCnclCnt = 0;    // 판매취소된 일반판매번호
		int skipOldDtm  = 0;    // 판매일자보다 이전에 가입한 부가상품
		int skipNonPol  = 0;    // 부가상품 정책에 등록되지 않은 경우
		int skipDupPol  = 0; 	// 부가상품정책에 중복된 경우
		int skipDCode  = 0; 	// 단말기개통대리점과 부가상품유치대리점이 다를 경우
		int skipSameCnt = 0;    // 이미 반영된 부가서비스
		int skipNull    = 0;    // 부가서비스가 Null
		int errorCnt    = 0;    // 오류건수

		double sScrbDtm = 0;
		double sSaleDtm = 0;

		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append(fileTime)
			.toString();

		// Input File 자료처리를 위한 변수 선언
		String fMdlCd        = "";
		String fGnrlSaleNo   = "";
		String fScrbDt       = "";
		String fScrbTm       = "";
		String fSuplSvcCd    = "";
		String fSvcDCode     = "";
		String fSuplDCode    = "";

		// 일반판매정보 관련 변수 선언
		String gsGnrlSaleNo  = "";
		   int gsGnrlSaleChgSeq = 0;
		String gsSaleDtm     = "";
		String gsSaleChgDtm  = "";
		String gsSaleDtlTyp  = "";
		String gsSlNetCd     = "";
		String gsDsNetCd     = "";
		String gsAgencyCd    = "";
        String gsSaleCnclYn  = "";
        String gsDtClsYn     = "";

        // 부가서비스판매
        String spSvcStaDt    = "";

        //부가상품 장려금 정보
        String pIdmLmtCd     = "";
        String pEndsPrdCd    = "";
           int pPolCnt       =  0;

        // 부가서비스판매 INSERT / UPDATE를 위하나 변수 선언
		String  sSaleChgDtm     = "";
		String  sPrMnyYn        = "";
		String  sGnrlSaleNo     = "";
		String  sWorkTable      = "";
		String  sCloseYM        = "";

		   int  sGnrlSaleChgSeq = 0;
           int  sCompDay        = 0;

		Boolean sNewSupl        = false;
    	Boolean sNewGnrlNo      = false;
		Boolean sSuplFlag       = false;
		Boolean sMonthClose     = false;


        // QueryForObject를 위한 Map 선언
        Map    resultSale; 

		Map<String, Object> requestMap    = new HashMap<String, Object>();
		Map<String, Object> requestMapUpd = new HashMap<String, Object>();

		String sCurdate = dataPath.substring(21, 29);
		logMng.writeLogFile(" U.Key I/F SAM 일자 [" + sCurdate + "]");

		// 작업 처리시간을 줄이기 위해 부가서비스 장려금 정보를 조회하여 처리대상 부가서비스 목록을 생성 - U.Key 처리 전일자 기준
	 	requestMap.put("FILE_DT", sCurdate);
	 	resultSale = (Map) sqlMap.queryForObject("SALSUI08110.getYesterDay", requestMap);
	 	sCurdate   = (String)  resultSale.get("WORK_DT");
		logMng.writeLogFile(" 부가서비스 목록취득 기준일시 ==> " + sCurdate + "0900");

	 	requestMap.put("SCRB_DTM", sCurdate + "0900");
		List resultList = (List) sqlMap.queryForList("SALSUI08110.getSuplList", requestMap);
		int selectCnt2 = resultList.size();

		String sPolListStr = " ";
		Map printList = new HashMap();
		for (int J = 0; J < selectCnt2; J++) {
			printList = (Map) resultList.get(J);
			sPolListStr = sPolListStr + (String) printList.get("SUPL_SVC_CD") + " "; 
		}

		// 일마감(월마감)일자 조회
		resultSale = (Map)sqlMap.queryForObject("SALSUI08110.getMonthClose", requestMap);
    	sCloseYM   = (String)  resultSale.get("CLOSE_YYYYMM");                            // 최종 마감월 Return

		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {

					readCnt++;

					// 전문양식에 맞게 읽은 자료를 자른다.
					fSvcDCode   = readLine.substring( 2,  8).trim();
					fMdlCd      = readLine.substring(22, 27).trim();
					fGnrlSaleNo = readLine.substring(42, 53).trim();
					fScrbDt     = readLine.substring(53, 61).trim();
					fScrbTm     = readLine.substring(61, 67).trim();
					fSuplSvcCd  = readLine.substring(83, 93).trim();
					fSuplDCode  = readLine.substring(93, 99).trim();

					if (fSuplSvcCd == null || valueNull.equals(fSuplSvcCd))  {
						skipNull++;
						continue;
					}

                    // 정책에 등록된 부가서비스인지 Chcek
					if (sPolListStr.indexOf(fSuplSvcCd) == valueNotFound) {
		        		// 정책에 등록되지 않은 경우 SKIP
		        		skipNonPol++;
		        		continue;
					}

                    // 단말기 개통 DCOde와 부가상품 유치 DCode가 다르면 Skip
					if (!fSvcDCode.equals(fSuplDCode)) {
		        		skipDCode++;
		        		continue;
					}


					// 판매관리번호가  이전정보와 다른 경우 일반판매 정보 Select 
		        	if (!sGnrlSaleNo.equals(fGnrlSaleNo)) {

							// 일반판매정보 조회
							requestMap.put("GNRL_SALE_NO", fGnrlSaleNo);

							resultSale = (Map)sqlMap.queryForObject("SALSUI08110.getSaleInfo", requestMap);

							if (resultSale == null)  {
								skipSaleNo++;
								continue;
							}

				        	gsGnrlSaleNo     = (String)  resultSale.get("GNRL_SALE_NO");
				        	gsGnrlSaleChgSeq = ((BigDecimal) resultSale.get("GNRL_SALE_CHG_SEQ")).intValue();
				        	gsSaleDtm        = (String)  resultSale.get("SALE_DTM");
				        	gsSaleDtlTyp     = (String)  resultSale.get("SALE_DTL_TYP");
				        	gsSlNetCd        = (String)  resultSale.get("SL_NET_CD");
				        	gsDsNetCd        = (String)  resultSale.get("DS_NET_CD");
				        	gsSaleCnclYn     = (String)  resultSale.get("SALE_CNCL_YN");
				        	gsAgencyCd       = (String)  resultSale.get("AGENCY_CD");
				        	gsDtClsYn        = (String)  resultSale.get("DT_CLS_YN");

			            	if (valueY.equals(gsSaleCnclYn)) {

								logMng.writeLogFile("[TSAL_GENERAL_SALE] 판매취소건 Skip [일반판매번호] ==> [" + fGnrlSaleNo + "]");
								skipCnclCnt++;
								continue;
			            	}

			        		sNewGnrlNo     	= true;			        // True인 경우만 판매변경이력을 생성한다.
			        		sGnrlSaleNo 	= fGnrlSaleNo;

		        	}


		        	// 부가상품 가입일자가 단말기 최초 판매일자보다 같거나 큰 경우에만 처리한다.
		        	if (Long.parseLong(fScrbDt) < Long.parseLong(gsSaleDtm.substring(0, 8))) {

		        		skipOldDtm++;
		        		continue;

		        	} else {

						// 일반판매 부가서비스 판매(TSAL_SUPLSERVICE) 조회
						requestMap.put("GNRL_SALE_NO", fGnrlSaleNo);
						requestMap.put("SUPL_SVC_CD",  fSuplSvcCd);

						resultSale = (Map)sqlMap.queryForObject("SALSUI08110.getSuplInfo", requestMap);
						if (resultSale == null)  {
				        	spSvcStaDt      = "";
				        	sNewSupl        = true;
						} else {

				        	sNewSupl        = false;
				        	spSvcStaDt      = (String)  resultSale.get("SVC_STA_DT");	
						}
/*
			        	// 	부가서비스장려금 정책 조회 - 조회된 결과가 2건 이상인 경우 오류처리
			        	requestMap.put("SUPL_SVC_CD",  fSuplSvcCd);
			        	requestMap.put("SALE_DTL_TYP", gsSaleDtlTyp);
			        	requestMap.put("EQP_MDL_CD",   fMdlCd);
			        	requestMap.put("ORG_ID",       gsAgencyCd);
			        	requestMap.put("DS_NET_CD",    gsDsNetCd);
			        	requestMap.put("SL_NET_CD",    gsSlNetCd);
			        	requestMap.put("APLY_DTM",     fScrbDt + fScrbTm);

			        	resultPol = (Map)sqlMap.queryForObject("SALSUI08110.getPolCount", requestMap);
			        	if(resultPol == null)  {
			        		// 정책에 등록되지 않은 경우 SKIP
			        		skipNonPol++;
			        		continue;
			        	}

			            pPolCnt     = ((BigDecimal) resultPol.get("POL_CNT")).intValue();
			        	if (pPolCnt > 1) {
			        		// 정책에 중복되어서 부가상품이 등록된 경우
			        		logMng.writeLogFile("부가서비스장려금 정책이 중복되어 정의되었습니다.[단말기/조직/영업망/유통망/판매유형/가입일시/일반판매번호/부가서비스 : " +
			        				fMdlCd + "/" + gsAgencyCd + "/" + gsSlNetCd + "/" + gsDsNetCd + "/" + gsSaleDtlTyp + "/" + fScrbDt + fScrbTm + "/" + gsGnrlSaleNo + "/" + fSuplSvcCd + "]");
			        		skipDupPol++;
			        	}

			        	// 	부가서비스장려금 정책 조회
			        	requestMap.put("SUPL_SVC_CD",  fSuplSvcCd);
			        	requestMap.put("SALE_DTL_TYP", gsSaleDtlTyp);
			        	requestMap.put("EQP_MDL_CD",   fMdlCd);
			        	requestMap.put("ORG_ID",       gsAgencyCd);
			        	requestMap.put("DS_NET_CD",    gsDsNetCd);
			        	requestMap.put("SL_NET_CD",    gsSlNetCd);
			        	requestMap.put("APLY_DTM",     fScrbDt + fScrbTm);

			        	resultPol = (Map)sqlMap.queryForObject("SALSUI08110.getPolInfo", requestMap);
			        	if(resultPol == null)  {
			        		// 정책에 등록되지 않은 경우 SKIP
			        		skipNonPol++;
			        		continue;
			        	}

						*/

						// Default Value Setting
			            pIdmLmtCd     = valueN;
			            pEndsPrdCd    = valueN;

	            		sPrMnyYn      = valueN;

		            	// 단말기 판매일자와 부가상품 가입일자 및 부가상품 유치기한을 기준으로 장려금지급여부 Setting
		            	if (valueOne.equals(pIdmLmtCd)) {   /* 유치기한 - 당일 */

			            	if (gsSaleDtm.substring(0, 8).equals(fScrbDt)) {
			            		sPrMnyYn = valueY;
		            		}
		            	}

		            	if (valueTwo.equals(pIdmLmtCd)) {   /* 유치기한 - 당월 */
		            		if (gsSaleDtm.substring(0, 6).equals(fScrbDt.substring(0,6))) {
		            			sPrMnyYn = valueY;
		            		}
		            	}

		            	if (valueThree.equals(pIdmLmtCd)) {   /* 유치기한 - D+1일 */

		        			sCompDay = Integer.parseInt(gsSaleDtm.substring(0, 8)) - Integer.parseInt(fScrbDt);
		        			sCompDay = Math.abs(sCompDay);

		            		if (sCompDay <= 1 ) {
		            			sPrMnyYn = valueY;
		            		}
		            	}

		            	if (valueZero.equals(pIdmLmtCd)) {   /* 유치기한 - 없음 */
	            			sPrMnyYn = valueY;
		            	}

		            	// 부가서비스 변경 여부 Setting
		            	sSuplFlag = false;

			            // 정책정보에 부가상품 장려금이 등록되어 있고 일반판매에 부가상품이 이미 등록된 경우
			            // 해지 후 재 가입인 경우를 생각하여 부가서비스판매(TSAL_SUPLSERVICE) 정보를 Insert / Update한다.

		            	if (sNewSupl == true) {

			            	sSuplFlag = true;

			            } else {

		            		if (!spSvcStaDt.equals(fScrbDt)) {

				            	sSuplFlag = true;

		            		}
			            }

		            	// 월마감 및 추정 일마감 Check하여 판매변경이력 증가 여부 판단
		            	sMonthClose = false;
		            	if (valueY.equals(gsDtClsYn)) {
		            		sMonthClose = true;
		            	}

		            	// 최종 마감월과 최초 판매년월을 Long Type으로 변환하여 
		            	if ( Long.parseLong(sCloseYM) >= Long.parseLong(gsSaleDtm.substring(0, 6).trim())) {
		            		sMonthClose = true;
		            	}

		            	if (sNewGnrlNo == true && sSuplFlag == true && sMonthClose == true) {

		            		sGnrlSaleChgSeq  = gsGnrlSaleChgSeq + 1;  // 일반판매변경순번 증가...

		            	} else {

			            	sGnrlSaleChgSeq  = gsGnrlSaleChgSeq;      // 일반판매변경순번 증가하지 않음...
		            			
		            	}

		            	if (sNewSupl == true) {
		            		// 부가서비스판매 이력이 없는 경우 - 신규 부가서비스
			            	requestMapUpd.put("GNRL_SALE_NO",       gsGnrlSaleNo);
			            	requestMapUpd.put("GNRL_SALE_CHG_SEQ",  sGnrlSaleChgSeq);
			            	requestMapUpd.put("SUPL_SVC_CD",        fSuplSvcCd);
			            	requestMapUpd.put("IDM_LMT_CD",         pIdmLmtCd);
			            	requestMapUpd.put("ENDS_PRD_CD",        pEndsPrdCd);
			            	requestMapUpd.put("PR_MNY_YN",          sPrMnyYn);
			            	requestMapUpd.put("SVC_STA_DT",         fScrbDt);
			            	requestMapUpd.put("USER_ID",            USER_ID);

			            	sqlMap.update("SALSUI08110.addSuplService", requestMapUpd);

			            	insertCnt++;

			            } else {

		            		// 가입일자를 비교하여 다른 경우만 Update
		            		if (!spSvcStaDt.equals(fScrbDt)) {

				            	requestMapUpd.put("IDM_LMT_CD",        pIdmLmtCd);
				            	requestMapUpd.put("ENDS_PRD_CD",       pEndsPrdCd);
				            	requestMapUpd.put("PR_MNY_YN",         sPrMnyYn);
				            	requestMapUpd.put("SVC_STA_DT",        fScrbDt);
				            	requestMapUpd.put("USER_ID",           USER_ID);
				            	requestMapUpd.put("GNRL_SALE_NO",      gsGnrlSaleNo);
				            	requestMapUpd.put("GNRL_SALE_CHG_SEQ", sGnrlSaleChgSeq);
				            	requestMapUpd.put("SUPL_SVC_CD",       fSuplSvcCd);

				            	sqlMap.update("SALSUI08110.saveSuplService", requestMapUpd);

				            	updateCnt++;

		            		} else {
				        		skipSameCnt++;
		            		}
			            }

			            // 처음 부가상품 Update가 발생한 경우만 판매변경이력 생성
		            	// 일마감이 완료된 경우 이력발생, 일마감 이전이면 이력발생하지 않음
			            if (sNewGnrlNo == true && sSuplFlag == true && valueY.equals(gsDtClsYn)) {

			            	sNewGnrlNo     = false;                // 동일한 고객에 대한 부가서비스 가입 정보가 여러 Row로 I/F 되므로
	                                                               // 최초 1건만 변경이력 발생 시키고 다음부터는 이력을 생성하지 않기 위해 FALSE 처리

			            	// 영업 판매변경 이력의 최종 변경일시를 조회하여 저장 
							requestMap.put("GNRL_SALE_NO", fGnrlSaleNo);
							resultSale    = (Map)sqlMap.queryForObject("SALSUI08110.getGnrlSaleInfo", requestMap);
				        	gsSaleChgDtm  = (String)  resultSale.get("SALE_CHG_DTM");

				        	// 부가상품 유치일시 저장
		            		sSaleChgDtm = fScrbDt + fScrbTm;

		            		// 영업 판매변경 이력의 최종 변경일시와 부가상품 유치일시를 비교하여 보다 나중일자를 판매변경일시로 지정
		            		sScrbDtm = (Double) Double.parseDouble(sSaleChgDtm);
		            		sSaleDtm = (Double) Double.parseDouble(gsSaleChgDtm);
			            	if (sScrbDtm < sSaleDtm) {
			            		sSaleChgDtm = gsSaleChgDtm;
			            	}

			            	requestMapUpd.put("GNRL_SALE_NO", gsGnrlSaleNo);    	// 일반판매번호
			            	requestMapUpd.put("OLD_CHG_SEQ",  gsGnrlSaleChgSeq);	// 변경전 일반판매변경순번
			            	requestMapUpd.put("NEW_CHG_SEQ",  sGnrlSaleChgSeq);		// 변경후 일반판매변경순번
			            	requestMapUpd.put("CHGRG_ID",     USER_ID);			    // 판매변경자ID
			            	requestMapUpd.put("SALE_CHG_DTM", sSaleChgDtm);         // 판매변경일자
			            	requestMapUpd.put("USER_ID",      USER_ID);				// 처리자ID - SALSUI0811

//			            	 일반판매 - 일반판매변경순번증가, 변경이력구분 '06'(부가서비스변경)
			            	sWorkTable = "TSAL_GENERAL_SALE";
			            	sqlMap.update("SALSUI08110.addGeneralSale", requestMapUpd);

//			            	 단말기판매 - 변경순번 증가
			            	sWorkTable = "TSAL_EQUIPMENT_SALE";
			            	sqlMap.update("SALSUI08110.addEquipmentSale", requestMapUpd);

//			            	 USIM판매 - 변경순번 증가
			            	sWorkTable = "TSAL_USIM_SALE";
			            	sqlMap.update("SALSUI08110.addUsimSale", requestMapUpd);

//			            	 중고반납기기 - 변경순번 증가
			            	sWorkTable = "TSAL_OLDRTN_EQUIPMENT";
			            	sqlMap.update("SALSUI08110.addOldrtnEquipment", requestMapUpd);

//			            	 할부매출 - 변경순번 증가
			            	sWorkTable = "TSAL_ALLOT_SALE";
			            	sqlMap.update("SALSUI08110.addAllotSale", requestMapUpd);

//			            	 SKT예수금 - 변경순번 증가
			            	sWorkTable = "TSAL_SKT_PRPRC";
			            	sqlMap.update("SALSUI08110.addSktPrprc", requestMapUpd);

//			            	 약정보조금 - 변경순번 증가
			            	sWorkTable = "TSAL_AGREEMENT_ASTAMT";
			            	sqlMap.update("SALSUI08110.addAgreementAstamt", requestMapUpd);

//			            	 현금매출 - 변경순번 증가
			            	sWorkTable = "TSAL_CASH_SALE";
			            	sqlMap.update("SALSUI08110.addCashSale", requestMapUpd);

//			            	 수납정보 - 변경순번 증가
			            	sWorkTable = "TSAL_PAYMENT";
			            	sqlMap.update("SALSUI08110.addPayment", requestMapUpd);

//			            	 판매수수료 - 변경순번 증가
			            	sWorkTable = "TSAL_SALE_CMMS";
			            	sqlMap.update("SALSUI08110.addSaleCmms", requestMapUpd);

//			            	 장려금 - 변경순번 증가
			            	sWorkTable = "TSAL_PROMOTION_MONEY";
			            	sqlMap.update("SALSUI08110.addPromotionMoney", requestMapUpd);

//			            	 OCB/MCARD - 변경순번 증가
			            	sWorkTable = "TSAL_OCBMCARD";
			            	sqlMap.update("SALSUI08110.addOcbmcard", requestMapUpd);

//			            	 온라인수납 - 변경순번 증가
			            	sWorkTable = "TSAL_ONLINE_PAYMENT";
			            	sqlMap.update("SALSUI08110.addOnlinePayment", requestMapUpd);

//			            	 온라인수수료 - 변경순번 증가
			            	sWorkTable = "TSAL_ONLINE_CMMS";
			            	sqlMap.update("SALSUI08110.addOnlineCmms", requestMapUpd);

			            	sWorkTable = "";
			            	hstIsrtCnt++;
			            }
			            
		        	}

				} catch (Exception ex) {
					logMng.writeLogFile("ERRCODE:E " + sWorkTable);
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
		logMng.writeLogFile("       Update Count : " + updateCnt);
		logMng.writeLogFile("   Hst Insert Count : " + hstIsrtCnt);
		logMng.writeLogFile("------------------------------------------------------------");
		logMng.writeLogFile("      부가상품 Null : " + skipNull);
		logMng.writeLogFile("  일반판매번호 없음 : " + skipSaleNo);
		logMng.writeLogFile("      판매취소 Skip : " + skipCnclCnt);
		logMng.writeLogFile("   처리 대리점 상이 : " + skipDCode);
		logMng.writeLogFile("        이미 반영됨 : " + skipSameCnt);
		logMng.writeLogFile("       판매일자이전 : " + skipOldDtm);
//		logMng.writeLogFile("           정책없음 : " + skipNonPol);
//		logMng.writeLogFile("           중복정책 : " + skipDupPol);
		logMng.writeLogFile("        Error Count : " + errorCnt);
		logMng.writeLogFile("------------------------------------------------------------");
	}
}