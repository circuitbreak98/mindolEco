package com.sktst.batch.sac.erp.biz;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/판매회계</li>
 * <li>설 명 : 부가세 전송 </li>
 * <li>작성일 : 2009-04-28</li>
 * </ul>
 *
 * @author 이강수 (leekangsu)
 */
public class SACERP08190 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08190";

	private SAP sap = null;

	private List lstAmtInfo			= null;		//금액정보
	private List lstCmmsInfo		= null;		//판매수수료정보
	
	private String zbudat	= "";	//귀속일자
	private String userCd	= "";	//대리점 정산담당자 사번
	private String agency	= "";	//대리점 거래처코드
	private String pYymm	= "";	//년월(shell parameter)
	private String pAgency	= "";	//대리점 거래처코드(shell parameter)
	private String pDealCo	= "";	//거래처코드(shell parameter)
	private String pZgubun	= "";	//전송구분(shell parameter)
	private String pSeq	    = "";	//일련번호(shell parameter)

	private Map<String, Object> imParams = null;
	private Map<String, Object> imTables = null;
	private Map<String, Object> agencyMap = null;
	private ArrayList alItem = null;

	private Object[] oHead = null;	//Erp vat head table row map array
	private Map mHead = null;		//Erp Head table row map
	private Map mAgency = null;		//대리점 map

	private Map<String, Object> retMap = null;	//전송결과 map
	private Map<String, Object> requestMap = new HashMap<String, Object>();

	/**
	 * 전자 세금계산서 배치 프로그램을 수행한다.
	 * @author	이강수 (leekangsu)
	 * @param	Map request : request
	 * @return	int : 수행결과(0이면 정상, 아니면 비정상)
	 * @throws	Exception
	 */	
	public int execute(Map request) throws Exception {
		super.getProperties();

		//① DB 정보 취득 (SqlMapClient 인스턴스 취득)
		SqlMapClient sqlMap = getSqlMapClient();
		
		try {
			logMng.openLogFile(PROG_ID);
			sap = new SAP();

			//로그 출력 (AbsBatchJobBiz 클래스에서 제공하는 변수 log를 사용)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
				log.debug(request.toString());
			}
			
			//② Transaction 시작
			sqlMap.startTransaction();
			
			//③ 요청 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");
			String dpstYM = "";
			if( request.size() > 1 ) {
				pYymm	= (String)request.get("args1");
				pAgency	= (String)request.get("args2");
				pDealCo	= (String)request.get("args3");
				pZgubun	= (String)request.get("args4");
				pSeq	= (String)request.get("args5");

                pYymm = pYymm.equals("?") ? "" : pYymm;
				pAgency = pAgency.equals("?") ? "" : pAgency;
				pDealCo = pDealCo.equals("?") ? "" : pDealCo;
				pZgubun = pZgubun.equals("?") ? "" : pZgubun;
				pSeq    = pSeq.equals("?") ? "" : pSeq;

				logMng.writeLogFile("args1[입금년월] : " + pYymm);
				logMng.writeLogFile("args2[대리점]  : " + pAgency);
				logMng.writeLogFile("args3[거래처]  : " + pDealCo);
				logMng.writeLogFile("args4[전송구분] : " + pZgubun);
				logMng.writeLogFile("args5[일련번호] : " + pSeq);
			}
			
			if (pYymm.equals("")) {
				pYymm = ErpCommon.getLastMonth();	//입금년월(전월)
			} else if (pYymm.length() != 6) {
				logMng.writeLogFile("args1[입금년월] : " + pYymm + " 입력이 올바르지 않습니다.(ex. 200901)");
				log.debug("args1[입금년월] : " + pYymm + " 입력이 올바르지 않습니다.(ex. 200901)");
				return 1;
			}
			String lastDay = ErpCommon.getLastDay(Integer.parseInt(pYymm.substring(0, 4)),
					Integer.parseInt(pYymm.substring(4)) );
			zbudat = pYymm + lastDay;	//귀속일자

			log.debug("dpstYM(입금년월) : " + pYymm);
			log.debug("zbudat(귀속일자) : " + zbudat);

			requestMap.put("YYMM"       , pYymm);
			requestMap.put("AGENCY"     , pAgency);
			requestMap.put("DEAL_CO_CD" , pDealCo);
			requestMap.put("TAX_CL"     , pZgubun);
			requestMap.put("SEQ"        , pSeq);

			long saveTime = System.currentTimeMillis();
			long currTime = 0;
			String dealEndDt = null;
			while (currTime - saveTime < 1000) {
				currTime = System.currentTimeMillis();
			}
			
			if(pYymm == null || pAgency == null || pDealCo == null || pZgubun == null || pSeq == null
			 || "".equals(pYymm) || "".equals(pAgency) || "".equals(pDealCo) || "".equals(pZgubun) || "".equals(pSeq)){
				List lstMmTaxTrms = (List)sqlMap.queryForList("SACERP08190.getMmTaxErpTrmsList");
				for (int idx = 0; idx < lstMmTaxTrms.size(); idx++) {
					requestMap = (Map)lstMmTaxTrms.get(idx);
					
					/*
					 *   거래 종료일자가 "99991231"이 아닌 경우 해당일자를 전기일자에 표시
					 */
					dealEndDt = (String) requestMap.get("DEAL_END_DT");
					if(dealEndDt == null || "99991231".equals(dealEndDt) || "".equals(dealEndDt.trim())){
						zbudat = pYymm + lastDay;	//귀속일자
					}else{
						zbudat = dealEndDt;
					}
					
					sendTaxErpTran(sqlMap, requestMap);

					saveTime = System.currentTimeMillis();
					currTime = 0;
					while (currTime - saveTime < 1000) {
						currTime = System.currentTimeMillis();
					}
				}
			}else{
				sendTaxErpTran(sqlMap, requestMap);
			}

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:1");
			//logMng.writeLogFile("Transaction Exception = [" + e + "]");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
			}
		} finally {
			//⑥-2 Transaction rollback (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			
			sqlMap.endTransaction();
			sap.disconnect();
			logMng.closeLogFile();
		}

		//⑦ 처리 결과 코드 리턴
		return 0;		
	}

	/**
	 * 전자 세금계산서 배치 프로그램을 수행한다.
	 * @author	이강수 (leekangsu)
	 * @param	Map request : request
	 *          SqlMapClient sqlMap
	 *          Map requestMap
	 * @return	int : 수행결과(0이면 정상, 아니면 비정상)
	 * @throws	Exception
	 */	
	private void sendTaxErpTran(SqlMapClient sqlMap, Map requestMap) throws Exception {
		try {
			//로그 출력 (AbsBatchJobBiz 클래스에서 제공하는 변수 log를 사용)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
				log.debug(requestMap.toString());
			}
			
			//② Transaction 시작
//			sqlMap.startTransaction();

			//③ 요청 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");
			String dpstYM = "";
			if( requestMap.size() > 1 ) {
				pYymm	= (String)requestMap.get("YYMM");
				pAgency	= (String)requestMap.get("AGENCY");
				pDealCo	= (String)requestMap.get("DEAL_CO_CD");
				pZgubun	= (String)requestMap.get("TAX_CL");
				pSeq	= (String)requestMap.get("SEQ");

				logMng.writeLogFile("[입금년월] : " + pYymm);
				logMng.writeLogFile("[대리점]  : " + pAgency);
				logMng.writeLogFile("[거래처]  : " + pDealCo);
				logMng.writeLogFile("[전송구분] : " + pZgubun);
				logMng.writeLogFile("[일련번호] : " + pSeq);
			}
			
			if (pYymm.equals("")) {
				pYymm = ErpCommon.getLastMonth();	//입금년월(전월)
			} else if (pYymm.length() != 6) {
				logMng.writeLogFile("args1[입금년월] : " + pYymm + " 입력이 올바르지 않습니다.(ex. 200901)");
				log.debug("args1[입금년월] : " + pYymm + " 입력이 올바르지 않습니다.(ex. 200901)");
				return;
			}
			String lastDay = ErpCommon.getLastDay(Integer.parseInt(pYymm.substring(0, 4)),
					Integer.parseInt(pYymm.substring(4)) );
			zbudat = pYymm + lastDay;	//귀속일자

			log.debug("dpstYM(입금년월) : " + pYymm);
			log.debug("zbudat(귀속일자) : " + zbudat);

			/**
			 *   대리점 정보 조회
			 */
			agencyMap = (Map)sqlMap.queryForObject("SACERP08190.getAgency", requestMap);
			if ( agencyMap == null ) {
				log.debug("대리점이 존재하지 않습니다.");
				logMng.writeLogFile("대리점이 존재하지 않습니다.");
				requestMap.put("REMARK", "대리점이 존재하지 않습니다.");
				sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
				return;
			}
			
			//④ DB 처리
			/** 
			 *   전자 세금계산서 구분 코드
			 *   01 : 위탁거래 - 매출금액
			 *   02 : 위탁거래 - 수수료
			 *   03 : 직영점 거래금액
			 *   04 : SKT 장려금
			 *   05 : SKT 위탁수수료
			 *   06 : 제조업체 장려금 - skt를 제외한 장려금
			 *   07 : M채널
			 *   08 : 단말기 매입
			 *   09 : 온라인 소매 - 단말기
			 *   10 : 온라인 소매 - 판매수수료(입점수수료)
			 *   11 : 온라인 소매 - 판매수수료(가상계좌)
			 *   12 : 온라인 소매 - 판매수수료(계좌이체)
			 *   13 : 온라인 소매 - 판매수수료(카드결제)
			 *   14 : 온라인 도매 - 단말기
			 *   15 : 온라인 도매 - 판매수수료(입점수수료)
			 *   16 : 할부채권
			 *   17 : 할부채권 이자
			 *   18 : 약정보조금 
			 *   19 : 수정 전자세금계산서
			 */
			if ( "".equals(pZgubun) ) {
				logMng.writeLogFile("위탁거래 - 매출금액 send >>> ");
				sendTaxDealSales(sqlMap);

				long saveTime = System.currentTimeMillis();
				long currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				
				logMng.writeLogFile("위탁거래 - 수수료 send >>> ");
				sendTaxDealCharge(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				
				logMng.writeLogFile("직영점 거래금액 send >>> ");
				sendTaxDirectDeal(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}

				logMng.writeLogFile("SKT 장려금 send >>> ");
				sendSktGrant(sqlMap);

				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				
				logMng.writeLogFile("SKT 위탁수수료 send >>> ");
				sendSktCommiCharge(sqlMap);

				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("제조업체 장려금 - skt를 제외한 장려금 send >>> ");
				sendPrMnyCmmsVat(sqlMap);

				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("M채널 send >>> ");
				sendErpMcVat(sqlMap);

				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("단말기 매입 send >>> ");
				sendErpPrchsVat(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("온라인 소매 - 단말기 send >>> ");
				sendErpOsMobile(sqlMap);

				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("온라인 소매 - 판매수수료(입점수수료) send >>> ");
				sendErpOsCharge(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("온라인 소매 - 판매수수료(가상계좌) send >>> ");
				sendErpOsVitualAccount(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("온라인 소매 - 판매수수료(계좌이체) send >>> ");
				sendErpOsAccountTransfer(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("온라인 소매 - 판매수수료(카드결제) send >>> ");
				sendErpOsCard(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("온라인 도매 - 단말기 send >>> ");
				sendErpOaMobile(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("온라인 도매 - 판매수수료(입점수수료) send >>> ");
				sendErpOaCharge(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("할부채권 send >>> ");
				sendBondVat(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("할부채권 이자 send >>> ");
				sendBondInterest(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("약정보조금 send >>> ");
				sendBondGrant(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;
				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("수정 전자세금계산서 send >>> ");
				sendTaxChange(sqlMap);
				
			} else if ( "01".equals(pZgubun) ) {
				logMng.writeLogFile("위탁거래 - 매출금액 send >>> ");
				sendTaxDealSales(sqlMap);
			
			} else if (  "02".equals(pZgubun) ) {
		        logMng.writeLogFile("위탁거래 - 수수료 send >>> ");
		        sendTaxDealCharge(sqlMap);
				
			} else if (  "03".equals(pZgubun) ) {
		        logMng.writeLogFile("직영점 거래금액 send >>> ");
		        sendTaxDirectDeal(sqlMap);
		        
			} else if (  "04".equals(pZgubun) ) {
		        logMng.writeLogFile("SKT 장려금 send >>> ");
		        sendSktGrant(sqlMap);

			} else if (  "05".equals(pZgubun) ) {
		        logMng.writeLogFile("SKT 위탁수수료 send >>> ");
		        sendSktCommiCharge(sqlMap);

			} else if (  "06".equals(pZgubun) ) {
		        logMng.writeLogFile("제조업체 장려금 - skt를 제외한 장려금 send >>> ");
		        sendPrMnyCmmsVat(sqlMap);

			} else if (  "07".equals(pZgubun) ) {
		        logMng.writeLogFile("M채널 send >>> ");
		        sendErpMcVat(sqlMap);

			} else if (  "08".equals(pZgubun) ) {
		        logMng.writeLogFile("단말기 매입 send >>> ");
		        sendErpPrchsVat(sqlMap);
		        
			} else if (  "09".equals(pZgubun) ) {
		        logMng.writeLogFile("온라인 소매 - 단말기 send >>> ");
		        sendErpOsMobile(sqlMap);
		        
			} else if (  "10".equals(pZgubun) ) {
		        logMng.writeLogFile("온라인 소매 - 판매수수료(입점수수료) send >>> ");
		        sendErpOsCharge(sqlMap);

			} else if (  "11".equals(pZgubun) ) {
		        logMng.writeLogFile("온라인 소매 - 판매수수료(가상계좌) send >>> ");
		        sendErpOsVitualAccount(sqlMap);
		        
			} else if (  "12".equals(pZgubun) ) {
		        logMng.writeLogFile("온라인 소매 - 판매수수료(계좌이체) send >>> ");
		        sendErpOsAccountTransfer(sqlMap);
		        
			} else if (  "13".equals(pZgubun) ) {
		        logMng.writeLogFile("온라인 소매 - 판매수수료(카드결제) send >>> ");
		        sendErpOsCard(sqlMap);
		        
			} else if (  "14".equals(pZgubun) ) {
		        logMng.writeLogFile("온라인 도매 - 단말기 send >>> ");
		        sendErpOaMobile(sqlMap);
		        
			} else if (  "15".equals(pZgubun) ) {
		        logMng.writeLogFile("온라인 도매 - 판매수수료(입점수수료) send >>> ");
		        sendErpOaCharge(sqlMap);
		        
			} else if (  "16".equals(pZgubun) ) {
		        logMng.writeLogFile("할부채권 send >>> ");
		        sendBondVat(sqlMap);
		        
			} else if (  "17".equals(pZgubun) ) {
		        logMng.writeLogFile("할부채권 이자 send >>> ");
		        sendBondInterest(sqlMap);
		        
			} else if (  "18".equals(pZgubun) ) {
		        logMng.writeLogFile("약정보조금 send >>> ");
		        sendBondGrant(sqlMap);
		        
			} else if (  "19".equals(pZgubun) ) {
		        logMng.writeLogFile("수정 전자세금계산서 send >>> ");
		        sendTaxChange(sqlMap);	
			}
			
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:1");
			//logMng.writeLogFile("Transaction Exception = [" + e + "]");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
			}
		} finally {
			//⑥-2 Transaction rollback (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			
//			sqlMap.endTransaction();
			sap.disconnect();
//			logMng.closeLogFile();
		}

		//⑦ 처리 결과 코드 리턴
		return;			
	}
	
	
	/**
	 * (월)위탁거래 - 매출금액 ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendTaxDealSales(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mCnsgDealAmt = null;			//위탁거래금액조회 row map
		BigDecimal saleAmt = null;			//현금매출 + 카드매출
		BigDecimal saleAmt2 = null;			//할부채권이자
		BigDecimal splyPrc = null;			//공급가
		BigDecimal splyPrc2 = null;			//공급가
		Map ukeySubCdMap = null;
		String ukeySubCd = "";


		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY", agency);
		lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getCnsgDealSaleAmtList", requestMap);
		if ( lstAmtInfo.size() == 0) {
			log.debug(agency + " : (월)위탁거래 - 매출금액 ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile(agency + " : (월)위탁거래 - 매출금액 ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)위탁거래 - 매출금액 ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "01");

		alItem = new ArrayList();
		for (int i = 0; i < lstAmtInfo.size(); i++) {
			mCnsgDealAmt = (Map)lstAmtInfo.get(i);
			
			saleAmt = (BigDecimal)mCnsgDealAmt.get("SALE_AMT");
			splyPrc = saleAmt.divide(new BigDecimal("1.1"), BigDecimal.ROUND_HALF_UP);
			
			mVatItem = new HashMap<String, Object>();	//부가세 Item row map
			
			mVatItem.put("ZITEM"      ,	String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
			mVatItem.put("MWSKZ"      ,	"15");									//부가가치세 코드
			mVatItem.put("TAXGU"      ,	"IA");									//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("HWBAS"      ,	splyPrc.toString());		            //과세 표준 금액(공급가액)
			mVatItem.put("HWSTE"      ,	saleAmt.subtract(splyPrc).toString());	//금액(세액)
			mVatItem.put("ZTOTAMT"    ,	saleAmt.toString());		            //합계금액
			mVatItem.put("DEAL_CO_CD" ,	(String)mCnsgDealAmt.get("AGENCY"));	//대리점
			mVatItem.put("ZREFER6"    ,	(String)mCnsgDealAmt.get("AGENCY"));	//대리점
			mVatItem.put("ZTAXCOUNT"  ,	"100");		                            //세금계산서번호

			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
			
		}

		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		int size = 1;
		oHead = new Object[size];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);
		//⑤ 결과 데이터 처리
		
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}

	/**
	 * (월)위탁거래 - 수수료 ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendTaxDealCharge(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mCnsgDealAmt = null;			//위탁거래금액조회 row map
		BigDecimal saleAmt = null;			//현금매출 + 카드매출
		BigDecimal saleAmt2 = null;			//할부채권이자
		BigDecimal splyPrc = null;			//공급가
		BigDecimal splyPrc2 = null;			//공급가
		Map ukeySubCdMap = null;
		String ukeySubCd = "";


		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY", agency);
		lstCmmsInfo = (List)sqlMap.queryForList("SACERP08190.getCnsgDealCmmsAmtList", requestMap);
		if ( lstCmmsInfo.size() == 0) {
			log.debug(agency + " : (월)위탁거래 - 수수료 ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile(agency + " : (월)위탁거래 - 수수료 ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)위탁거래 - 수수료 ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "02");

		alItem = new ArrayList();
		for (int j = 0; j < lstCmmsInfo.size(); j++) {
			mCnsgDealAmt = (Map)lstCmmsInfo.get(j);
			
			saleAmt = (BigDecimal)mCnsgDealAmt.get("CNSG_CMMS");
			splyPrc = saleAmt.divide(new BigDecimal("1.1"), BigDecimal.ROUND_HALF_UP);

			mVatItem = new HashMap<String, Object>();	//부가세 Item row map

			/**
			 *    현대백화점 서브점코드  조회
			 *    ZSAC_C_00110 : 현대백화점 서브점코드
			 *    ukey_sub_cd : 서브점코드
			 */
			ukeySubCdMap     = new HashMap();
			ukeySubCdMap.put("deal_co_cd", mCnsgDealAmt.get("DEAL_CO_CD"));
			ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
			if(ukeySubCdMap != null){
				ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
				if(!"0000".equals(ukeySubCd)){
					mCnsgDealAmt.put("UKEY_SUB_CD", ukeySubCd);
				}
			}
			
			mVatItem.put("ZITEM"      , String.valueOf(alItem.size()+1));			//항목번호(k) : 레코드단위
			mVatItem.put("MWSKZ"      ,	"41");										//부가가치세 코드
			mVatItem.put("TAXGU"      ,	"IV");										//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("HWBAS"      ,	splyPrc.toString());						//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE"      ,	saleAmt.subtract(splyPrc).toString());		//금액(세액)
			mVatItem.put("ZTOTAMT"    ,	saleAmt.toString());						//합계금액
			mVatItem.put("NAME1"      ,	(String)mCnsgDealAmt.get("DEAL_CO_NM"));	//이름 1
			mVatItem.put("STCD2"      ,	(String)mCnsgDealAmt.get("BIZ_NUM"));		//사업자등록번호
			mVatItem.put("J_1KFREPRE" ,	(String)mCnsgDealAmt.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01"      ,	(String)mCnsgDealAmt.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS"  ,	(String)mCnsgDealAmt.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND"  ,	(String)mCnsgDealAmt.get("TYP_OF_BIZ"));	//업종
			mVatItem.put("DEAL_CO_CD" ,	(String)mCnsgDealAmt.get("ACC_PLC"));		//지급처
			mVatItem.put("ZARKTX1"    ,	"판매수수료");		                        //품목텍스트(단말기, 판매수수료 등 TEXT)

			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
			
		}

		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		int size = 1;
		oHead = new Object[size];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);
		//⑤ 결과 데이터 처리
		
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}	
	
	
	/**
	 * (월)직영점 거래금액 ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendTaxDirectDeal(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = new HashMap<String, Object>();	//부가세 Item row map
		Map mDirBrDealAmt = null;			//직영점거래금액조회 row map
		BigDecimal saleAmt = null;			//현금매출 + 커드매출
		BigDecimal splyPrc = null;			//공급가
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo);
		lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getDirBrDealAmtList", requestMap);
		if ( lstAmtInfo.size() == 0) {
			log.debug(agency + " : (월)직영점 거래금액 ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile(agency + " : (월)직영점 거래금액 ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)직영점 거래금액 ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "03");

		alItem = new ArrayList();
		for (int j = 0; j < lstAmtInfo.size(); j++) {
			mDirBrDealAmt = (Map)lstAmtInfo.get(j);
			
			saleAmt = (BigDecimal)mDirBrDealAmt.get("SALE_AMT");
			splyPrc = saleAmt.divide(new BigDecimal("1.1"), BigDecimal.ROUND_HALF_UP);

			/**
			 *    현대백화점 서브점코드  조회
			 *    ZSAC_C_00110 : 현대백화점 서브점코드
			 *    ukey_sub_cd : 서브점코드
			 */
			ukeySubCdMap     = new HashMap();
			ukeySubCdMap.put("deal_co_cd", mDirBrDealAmt.get("DEAL_CO_CD"));
			ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
			if(ukeySubCdMap != null){
				ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
				if(!"0000".equals(ukeySubCd)){
					mDirBrDealAmt.put("UKEY_SUB_CD", ukeySubCd);
				}
			}

			mVatItem.put("ZITEM",		String.valueOf(j+1));						//항목번호(k) : 레코드단위
			mVatItem.put("MWSKZ",		"15");										//부가가치세 코드
			mVatItem.put("ZGCODE",		(String)mDirBrDealAmt.get("UKEY_SUB_CD"));	//직영점코드 : 직영점 u-key code 4자리값
			mVatItem.put("TAXGU",		"IA");										//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("HWBAS",		splyPrc.toString());						//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		saleAmt.subtract(splyPrc).toString());		//금액(세액)
			mVatItem.put("ZTOTAMT",		saleAmt.toString());						//합계금액
			mVatItem.put("ZTAXCOUNT",	"50");										//세금계산서번호
			mVatItem.put("DEAL_CO_CD",	(String)mDirBrDealAmt.get("DEAL_CO_CD"));	//거래처(직영점에 해당하는 거래처코드)
			
			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
			
		}

		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		int size = 1;
		oHead = new Object[size];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}	

	/**
	 * (월)부가세(SKT장려금 매출) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendSktGrant(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mrMnyCmmsAmt = null;				//SKT장려금,위탁수수료 매출금액조회 row map
		Map mrPrMnyOthers = null;				//제조업체장려금 row map
		BigDecimal fixSplyPrc = null;			//공급가
		BigDecimal fixVat = null;				//부가세
		BigDecimal fixAmt = null;				//금액
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo);
		lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getSktGrantList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			log.debug("(월)부가세(SKT장려금 매출) ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile("(월)부가세(SKT장려금 매출) ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)부가세(SKT장려금 매출) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "04");
		
		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mrMnyCmmsAmt = (Map)lstAmtInfo.get(idx);

			alItem = new ArrayList();
			
			//장려금
			fixSplyPrc		= (BigDecimal)mrMnyCmmsAmt.get("FIX_SPLY_PRC");		// 공급가 */
			fixVat			= (BigDecimal)mrMnyCmmsAmt.get("FIX_VAT");			// 부가세 */
			fixAmt			= (BigDecimal)mrMnyCmmsAmt.get("FIX_AMT");			// 금액 */

			mVatItem = new HashMap<String, Object>();
			mVatItem.put("ZITEM",		String.valueOf(1));			//항목번호(k) : 레코드단위
			mVatItem.put("MWSKZ",		"11");						//부가가치세 코드
			mVatItem.put("TAXGU",		"IA");						//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("HWBAS",		fixSplyPrc.toString());		//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		fixVat.toString());			//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());			//합계금액
			mVatItem.put("NAME1",		(String)mrMnyCmmsAmt.get("DEAL_CO_NM"));	////이름 1
			mVatItem.put("STCD2",		(String)mrMnyCmmsAmt.get("BIZ_NUM"));		//사업자등록번호
			mVatItem.put("J_1KFREPRE",	(String)mrMnyCmmsAmt.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01",		(String)mrMnyCmmsAmt.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS",	(String)mrMnyCmmsAmt.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND",	(String)mrMnyCmmsAmt.get("TYP_OF_BIZ"));	//업종
			mVatItem.put("DEAL_CO_CD",	(String)mrMnyCmmsAmt.get("ACC_PLC"));		//정산처
			mVatItem.put("ZARKTX1",		"장려금");		//품목텍스트

			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
		}

			mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
			oHead = new Object[1];
			oHead[0] = mHead;
			
			imTables.put("T_HEAD",	oHead);
			imTables.put("T_BODY",	alItem.toArray());
			
			retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			//⑤ 결과 데이터 처리
			/**
			 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
			 */
			retMap.put("YYMM"   , pYymm);
			retMap.put("AGENCY" , pAgency);
			retMap.put("STL_PLC", pDealCo);
			retMap.put("SEQ"    , pSeq);
			ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
	}
	/**
	 * (월)부가세(SKT위탁수수료 매출) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendSktCommiCharge(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mrMnyCmmsAmt = null;				//SKT장려금,위탁수수료 매출금액조회 row map
		Map mrPrMnyOthers = null;				//제조업체장려금 row map
		BigDecimal fixSplyPrc = null;			//공급가
		BigDecimal fixVat = null;				//부가세
		BigDecimal fixAmt = null;				//금액
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo);
		lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getSktCommiChargeList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			log.debug("(월)부가세(SKT위탁수수료 매출) ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile("(월)부가세(SKT위탁수수료 매출) ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)부가세(SKT위탁수수료 매출) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "05");

		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mrMnyCmmsAmt = (Map)lstAmtInfo.get(idx);

			alItem = new ArrayList();

			//위탁수수료
			fixSplyPrc		= (BigDecimal)mrMnyCmmsAmt.get("CMMS_FIX_SPLY_PRC");	// 공급가 */
			fixVat			= (BigDecimal)mrMnyCmmsAmt.get("CMMS_FIX_VAT");			// 부가세 */
			fixAmt			= (BigDecimal)mrMnyCmmsAmt.get("CMMS_FIX_AMT");			// 금액 */

			mVatItem = new HashMap<String, Object>();
			mVatItem.put("ZITEM",		String.valueOf(2));			//항목번호(k) : 레코드단위
			mVatItem.put("MWSKZ",		"11");						//부가가치세 코드
			mVatItem.put("TAXGU",		"IA");						//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("HWBAS",		fixSplyPrc.toString());		//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		fixVat.toString());			//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());			//합계금액
			mVatItem.put("NAME1",		(String)mrMnyCmmsAmt.get("DEAL_CO_NM"));	////이름 1
			mVatItem.put("STCD2",		(String)mrMnyCmmsAmt.get("BIZ_NUM"));		//사업자등록번호
			mVatItem.put("J_1KFREPRE",	(String)mrMnyCmmsAmt.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01",		(String)mrMnyCmmsAmt.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS",	(String)mrMnyCmmsAmt.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND",	(String)mrMnyCmmsAmt.get("TYP_OF_BIZ"));	//업종
			mVatItem.put("DEAL_CO_CD",	(String)mrMnyCmmsAmt.get("ACC_PLC"));		//정산처
			mVatItem.put("ZARKTX1",		"위탁수수료");		//품목텍스트

			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));

		}
			
			mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
			oHead = new Object[1];
			oHead[0] = mHead;
			
			imTables.put("T_HEAD",	oHead);
			imTables.put("T_BODY",	alItem.toArray());
			
			retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			//⑤ 결과 데이터 처리
			/**
			 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
			 */
			retMap.put("YYMM"   , pYymm);
			retMap.put("AGENCY" , pAgency);
			retMap.put("STL_PLC", pDealCo);
			retMap.put("SEQ"    , pSeq);
			ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
	}

	/**
	 * (월)부가세(제조업체장려금) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendPrMnyCmmsVat(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mrMnyCmmsAmt = null;				//SKT장려금,위탁수수료 매출금액조회 row map
		Map mrPrMnyOthers = null;				//제조업체장려금 row map
		BigDecimal fixSplyPrc = null;			//공급가
		BigDecimal fixVat = null;				//부가세
		BigDecimal fixAmt = null;				//금액
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo);
		List lstPrMnyOthersInfo = (List)sqlMap.queryForList("SACERP08190.getPrMnyOthersList", requestMap);

		if ( lstPrMnyOthersInfo.size() == 0 ) {
			log.debug("(월)부가세(제조업체장려금) ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile("(월)부가세(제조업체장려금) ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)부가세(제조업체장려금) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "06");
		

		//제조업체장려금
		for ( int i = 0; i < lstPrMnyOthersInfo.size() ; i++ ) {
			mrPrMnyOthers = (Map)lstPrMnyOthersInfo.get(i);

			//장려금
			fixSplyPrc	= (BigDecimal)mrPrMnyOthers.get("FIX_SPLY_PRC");	// 공급가 */
			fixVat		= (BigDecimal)mrPrMnyOthers.get("FIX_VAT");			// 부가세 */
			fixAmt		= (BigDecimal)mrPrMnyOthers.get("FIX_AMT");			// 금액 */

			mVatItem = new HashMap<String, Object>();
			mVatItem.put("ZITEM",		String.valueOf(i+3));		//항목번호(k) : 레코드단위
			mVatItem.put("MWSKZ",		"11");						//부가가치세 코드
			mVatItem.put("TAXGU",		"IA");						//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("HWBAS",		fixSplyPrc.toString());		//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		fixVat.toString());			//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());			//합계금액
			mVatItem.put("NAME1",		(String)mrMnyCmmsAmt.get("DEAL_CO_NM"));	////이름 1
			mVatItem.put("STCD2",		(String)mrMnyCmmsAmt.get("BIZ_NUM"));		//사업자등록번호
			mVatItem.put("J_1KFREPRE",	(String)mrMnyCmmsAmt.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01",		(String)mrMnyCmmsAmt.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS",	(String)mrMnyCmmsAmt.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND",	(String)mrMnyCmmsAmt.get("TYP_OF_BIZ"));	//업종
			mVatItem.put("DEAL_CO_CD",	(String)mrMnyCmmsAmt.get("ACC_PLC"));		//정산처
			mVatItem.put("ZARKTX1",		"제조업체장려금");		//품목텍스트

			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
			
		}

		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		oHead = new Object[1];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}

	/**
	 * (월)부가세(M채널) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendErpMcVat(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = new HashMap<String, Object>();	//부가세 Item row map
		Map mDirBrDealAmt = null;			//거래금액조회 row map
		BigDecimal splyPrcAmt	= null;			//공급가
		BigDecimal vat 			= null;			//부가세
		BigDecimal fixAmt		= null;			//금액
		Map ukeySubCdMap = null;
		String ukeySubCd = "";


		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo);
		lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getErpMcVatList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			log.debug(agency + " : (월)부가세(M채널) ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile(agency + " : (월)부가세(M채널) ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)부가세(M채널) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "07");

		alItem = new ArrayList();
		
		for (int i = 0; i < lstAmtInfo.size(); i++) {
			mDirBrDealAmt = (Map)lstAmtInfo.get(i);
			
			splyPrcAmt	= (BigDecimal)mDirBrDealAmt.get("SPLY_PRC_AMT");		// 공급가 */
			vat			= (BigDecimal)mDirBrDealAmt.get("VAT");				// 부가세 */
			fixAmt		= (BigDecimal)mDirBrDealAmt.get("FIX_AMT");			// 금액 */
			
			/**
			 *    현대백화점 서브점코드  조회
			 *    ZSAC_C_00110 : 현대백화점 서브점코드
			 *    ukey_sub_cd : 서브점코드
			 */
			ukeySubCdMap     = new HashMap();
			ukeySubCdMap.put("deal_co_cd", mDirBrDealAmt.get("DEAL_CO_CD"));
			ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
			if(ukeySubCdMap != null){
				ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
				if(!"0000".equals(ukeySubCd)){
					mDirBrDealAmt.put("UKEY_SUB_CD", ukeySubCd);
				}
			}
							
			mVatItem.put("ZITEM",		String.valueOf(i+1));						//항목번호(k) : 레코드단위
			mVatItem.put("MWSKZ",		"15");										//부가가치세 코드
			mVatItem.put("ZGCODE",		(String)mDirBrDealAmt.get("UKEY_SUB_CD"));	//직영점코드 : 직영점 u-key code 4자리값
			mVatItem.put("TAXGU",		"IA");										//원천코드 : IA'(매출), 'IV'(매입)

			mVatItem.put("HWBAS",		splyPrcAmt.toString());		//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		vat.toString());			//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());			//합계금액

			mVatItem.put("ZTAXCOUNT",	"50");										//세금계산서번호
			mVatItem.put("DEAL_CO_CD",	(String)mDirBrDealAmt.get("DEAL_CO_CD"));	//거래처(직영점에 해당하는 거래처코드)
			
			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
			
		}
		
		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		oHead = new Object[1];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}

	/**
	 * 부가세(단말기 매입) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendErpPrchsVat(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mEqpPrchs			= null;			//단말기매입조회 row map
		BigDecimal splyPrcAmt	= null;			//공급가
		BigDecimal vat 			= null;			//부가세
		BigDecimal fixAmt		= null;			//금액
		
		String sknDealCoCd		= "";			//SKN 거래처 코드
		Map mSknInfo			= null;			//Skn거래처정보 map
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo); 
		
		List lstAgency = (List)sqlMap.queryForList("SACERP08190.getPrchsDebtSknList", requestMap);
		if ( lstAgency.size() == 0 ) {
			log.debug("부가세(단말기 매입) ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile("부가세(단말기 매입) ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)부가세(단말기 매입) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "08");

		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			alItem = new ArrayList();

			splyPrcAmt	= (BigDecimal)mAgency.get("SPLY_PRC_AMT");		// 공급가 */
			vat			= (BigDecimal)mAgency.get("VAT");				// 부가세 */
			fixAmt		= (BigDecimal)mAgency.get("FIX_AMT");			// 금액 */

			mVatItem = new HashMap<String, Object>();
			mVatItem.put("ZITEM",		String.valueOf(1));			//항목번호(k) : 레코드단위
			mVatItem.put("MWSKZ",		"41");						//부가가치세 코드
			mVatItem.put("TAXGU",		"IV");						//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("HWBAS",		splyPrcAmt.toString());		//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		vat.toString());			//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());			//합계금액
			mVatItem.put("NAME1",		(String)mAgency.get("DEAL_CO_NM"));		//이름 1
			mVatItem.put("STCD2",		(String)mAgency.get("BIZ_NUM"));		//사업자등록번호
			mVatItem.put("J_1KFREPRE",	(String)mAgency.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01",		(String)mAgency.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS",	(String)mAgency.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND",	(String)mAgency.get("TYP_OF_BIZ"));		//업종
			mVatItem.put("DEAL_CO_CD",	(String)mAgency.get("PRCHS_PLC"));		//매입처
			mVatItem.put("ZARKTX1",		"단말기매입");							//품목텍스트
			
			//SKN서울지사가 아닌 대리점의 경우 해당 대리점의 지정 SKN지사의 정보로 대체한다.
			if("10002".equals(pDealCo)){
				sknDealCoCd = ErpCommon.getSknDealCoCd(agency);
				if ( !"10002".equals(sknDealCoCd) ) {
					requestMap.put("DEAL_CO_CD", sknDealCoCd);
					mSknInfo = (Map)sqlMap.queryForObject("SACERP08190.getSknInfo", requestMap);

					mVatItem.put("NAME1",		(String)mSknInfo.get("DEAL_CO_NM"));	//이름 1
					mVatItem.put("STCD2",		(String)mSknInfo.get("BIZ_NUM"));		//사업자등록번호
					mVatItem.put("J_1KFREPRE",	(String)mSknInfo.get("REP_USER_NM"));	//대표자명
					mVatItem.put("ORT01",		(String)mSknInfo.get("ADDR"));			//주소
					mVatItem.put("J_1KFTBUS",	(String)mSknInfo.get("BIZ_CON"));		//업태
					mVatItem.put("J_1KFTIND",	(String)mSknInfo.get("TYP_OF_BIZ"));	//업종
					mVatItem.put("DEAL_CO_CD",	(String)mSknInfo.get("DEAL_CO_CD"));	//매입처
				}
			}

			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
			
		}
		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		oHead = new Object[1];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);
		
		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}
	
	/**
	 * 부가세(온라인소매거래-단말기) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendErpOsMobile(SqlMapClient sqlMap) throws Exception {

		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mEqpPrchs			= null;			//단말기매입조회 row map
		BigDecimal splyPrcAmt	= null;			//공급가
		BigDecimal vat 			= null;			//부가세
		BigDecimal fixAmt		= null;			//금액
		
		String sknDealCoCd		= "";			//SKN 거래처 코드
		String type             = null;
		Map mSknInfo			= null;			//Skn거래처정보 map
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo); 
		
		//제조업체 단말기 매입 조회
		List lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getErpOsMobileList", requestMap);


		if ( lstAmtInfo.size() == 0 ) {
			log.debug("부가세(온라인소매거래-단말기) ERP전송 건이 존재하지 않습니다.1");
			logMng.writeLogFile("부가세(온라인소매거래-단말기) ERP전송 건이 존재하지 않습니다.1");
			requestMap.put("REMARK", "(월)부가세(온라인소매거래-단말기) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}
		
		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "09");

		for (int i = 0; i < lstAmtInfo.size(); i++) {

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();
			alItem   = new ArrayList();
			
			mEqpPrchs = (Map)lstAmtInfo.get(i);
			mEqpPrchs.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	//거래처 코드

			splyPrcAmt	= (BigDecimal)mEqpPrchs.get("SPLY_PRC_AMT");		// 공급가 */
			vat			= (BigDecimal)mEqpPrchs.get("VAT");				// 부가세 */
			fixAmt		= (BigDecimal)mEqpPrchs.get("FIX_AMT");			// 금액 */
			type		= (String)mEqpPrchs.get("TYPE");			        // 부가세유형 */

			mVatItem = new HashMap<String, Object>();	//부가세 Item row map
			
			mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
			mVatItem.put("HWBAS",		splyPrcAmt.toString());					//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		vat.toString());						//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());						//합계금액
			mVatItem.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	    //거래처 코드
			mVatItem.put("NAME1",		(String)mEqpPrchs.get("DEAL_CO_NM"));	//이름 1
			mVatItem.put("STCD2",		(String)mEqpPrchs.get("BIZ_NUM"));		//사업자등록번호
			mVatItem.put("J_1KFREPRE",	(String)mEqpPrchs.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01",		(String)mEqpPrchs.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS",	(String)mEqpPrchs.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND",	(String)mEqpPrchs.get("TYP_OF_BIZ"));	//업종
			mVatItem.put("MWSKZ",		"15");									//부가가치세 코드
			mVatItem.put("TAXGU",		"IA");									//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("ZARKTX1",		"단말기");								//품목텍스트
			

			/**
			 *    현대백화점 서브점코드  조회
			 *    ZSAC_C_00110 : 현대백화점 서브점코드
			 *    ukey_sub_cd : 서브점코드
			 */
			ukeySubCdMap     = new HashMap();
			ukeySubCdMap.put("deal_co_cd", mEqpPrchs.get("ACC_PLC"));
			ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
			if(ukeySubCdMap != null){
				ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
				if(!"0000".equals(ukeySubCd)){
					mEqpPrchs.put("UKEY_SUB_CD", ukeySubCd);
					mEqpPrchs.put("ZGCODE", ukeySubCd);
					mVatItem.put("ZGCODE",	ukeySubCd);		//품목텍스트
				}
			}

			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
		}		
			
		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		oHead = new Object[1];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}

	/**
	 * 부가세(온라인소매거래-입점수수료) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendErpOsCharge(SqlMapClient sqlMap) throws Exception {

		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mEqpPrchs			= null;			//단말기매입조회 row map
		BigDecimal splyPrcAmt	= null;			//공급가
		BigDecimal vat 			= null;			//부가세
		BigDecimal fixAmt		= null;			//금액
		
		String sknDealCoCd		= "";			//SKN 거래처 코드
		String type             = null;
		Map mSknInfo			= null;			//Skn거래처정보 map
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo); 
		//제조업체 단말기 매입 조회
		List lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getErpOsChargeList", requestMap);

		if ( lstAmtInfo.size() == 0 ) {
			log.debug("부가세(온라인소매거래-입점수수료) ERP전송 건이 존재하지 않습니다.1");
			logMng.writeLogFile("부가세(온라인소매거래-입점수수료) ERP전송 건이 존재하지 않습니다.1");
			requestMap.put("REMARK", "(월)부가세(온라인소매거래-입점수수료) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}
		
		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "10");

		for (int i = 0; i < lstAmtInfo.size(); i++) {

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();
			alItem   = new ArrayList();
			
			mEqpPrchs = (Map)lstAmtInfo.get(i);
			mEqpPrchs.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	//거래처 코드

			splyPrcAmt	= (BigDecimal)mEqpPrchs.get("SPLY_PRC_AMT");		// 공급가 */
			vat			= (BigDecimal)mEqpPrchs.get("VAT");				// 부가세 */
			fixAmt		= (BigDecimal)mEqpPrchs.get("FIX_AMT");			// 금액 */
			type		= (String)mEqpPrchs.get("TYPE");			        // 부가세유형 */

			mVatItem = new HashMap<String, Object>();	//부가세 Item row map
			
			mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
			mVatItem.put("HWBAS",		splyPrcAmt.toString());					//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		vat.toString());						//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());						//합계금액
			mVatItem.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	    //거래처 코드
			mVatItem.put("NAME1",		(String)mEqpPrchs.get("DEAL_CO_NM"));	//이름 1
			mVatItem.put("STCD2",		(String)mEqpPrchs.get("BIZ_NUM"));		//사업자등록번호

			mVatItem.put("J_1KFREPRE",	(String)mEqpPrchs.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01",		(String)mEqpPrchs.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS",	(String)mEqpPrchs.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND",	(String)mEqpPrchs.get("TYP_OF_BIZ"));	//업종
			mVatItem.put("MWSKZ",		"41");									//부가가치세 코드
			mVatItem.put("TAXGU",		"IV");									//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("ZARKTX1",		"판매수수료(입점수수료)");					//품목텍스트

			/**
			 *    현대백화점 서브점코드  조회
			 *    ZSAC_C_00110 : 현대백화점 서브점코드
			 *    ukey_sub_cd : 서브점코드
			 */
			ukeySubCdMap     = new HashMap();
			ukeySubCdMap.put("deal_co_cd", mEqpPrchs.get("ACC_PLC"));
			ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
			if(ukeySubCdMap != null){
				ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
				if(!"0000".equals(ukeySubCd)){
					mEqpPrchs.put("UKEY_SUB_CD", ukeySubCd);
					mEqpPrchs.put("ZGCODE", ukeySubCd);
					mVatItem.put("ZGCODE",	ukeySubCd);		//품목텍스트
				}

			}
			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
		}		
			
		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		oHead = new Object[1];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);
		
		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}

	/**
	 * 부가세(온라인소매거래-가상계좌) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendErpOsVitualAccount(SqlMapClient sqlMap) throws Exception {

		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mEqpPrchs			= null;			//단말기매입조회 row map
		BigDecimal splyPrcAmt	= null;			//공급가
		BigDecimal vat 			= null;			//부가세
		BigDecimal fixAmt		= null;			//금액
		
		String sknDealCoCd		= "";			//SKN 거래처 코드
		String type             = null;
		Map mSknInfo			= null;			//Skn거래처정보 map
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo); 
		
		//제조업체 단말기 매입 조회
		List lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getErpOsVitualAccountList", requestMap);

		if ( lstAmtInfo.size() == 0 ) {
			log.debug("부가세(온라인소매거래-가상계좌) ERP전송 건이 존재하지 않습니다.1");
			logMng.writeLogFile("부가세(온라인소매거래-가상계좌) ERP전송 건이 존재하지 않습니다.1");
			requestMap.put("REMARK", "(월)부가세(온라인소매거래-가상계좌) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}
		
		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "11");

		for (int i = 0; i < lstAmtInfo.size(); i++) {

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();
			alItem   = new ArrayList();
			
			mEqpPrchs = (Map)lstAmtInfo.get(i);
			mEqpPrchs.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	//거래처 코드

			splyPrcAmt	= (BigDecimal)mEqpPrchs.get("SPLY_PRC_AMT");		// 공급가 */
			vat			= (BigDecimal)mEqpPrchs.get("VAT");				// 부가세 */
			fixAmt		= (BigDecimal)mEqpPrchs.get("FIX_AMT");			// 금액 */
			type		= (String)mEqpPrchs.get("TYPE");			        // 부가세유형 */

			mVatItem = new HashMap<String, Object>();	//부가세 Item row map
			
			mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
			mVatItem.put("HWBAS",		splyPrcAmt.toString());					//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		vat.toString());						//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());						//합계금액
			mVatItem.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	    //거래처 코드
			mVatItem.put("NAME1",		(String)mEqpPrchs.get("DEAL_CO_NM"));	//이름 1
			mVatItem.put("STCD2",		(String)mEqpPrchs.get("BIZ_NUM"));		//사업자등록번호
			mVatItem.put("J_1KFREPRE",	(String)mEqpPrchs.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01",		(String)mEqpPrchs.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS",	(String)mEqpPrchs.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND",	(String)mEqpPrchs.get("TYP_OF_BIZ"));	//업종
			mVatItem.put("MWSKZ",		"41");									//부가가치세 코드
			mVatItem.put("TAXGU",		"IV");									//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("ZARKTX1",		"판매수수료(가상계좌)");					//품목텍스트

			/**
			 *    현대백화점 서브점코드  조회
			 *    ZSAC_C_00110 : 현대백화점 서브점코드
			 *    ukey_sub_cd : 서브점코드
			 */
			ukeySubCdMap     = new HashMap();
			ukeySubCdMap.put("deal_co_cd", mEqpPrchs.get("ACC_PLC"));
			ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
			if(ukeySubCdMap != null){
				ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
				if(!"0000".equals(ukeySubCd)){
					mEqpPrchs.put("UKEY_SUB_CD", ukeySubCd);
					mEqpPrchs.put("ZGCODE", ukeySubCd);
					mVatItem.put("ZGCODE",	ukeySubCd);		//품목텍스트
				}

			}

			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));

		}		
			
		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		oHead = new Object[1];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}

	/**
	 * 부가세(온라인소매거래-계좌이체) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendErpOsAccountTransfer(SqlMapClient sqlMap) throws Exception {

		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mEqpPrchs			= null;			//단말기매입조회 row map
		BigDecimal splyPrcAmt	= null;			//공급가
		BigDecimal vat 			= null;			//부가세
		BigDecimal fixAmt		= null;			//금액
		
		String sknDealCoCd		= "";			//SKN 거래처 코드
		String type             = null;
		Map mSknInfo			= null;			//Skn거래처정보 map
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo); 
		
		//제조업체 단말기 매입 조회
		List lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getErpOsAccountTransferList", requestMap);

		if ( lstAmtInfo.size() == 0 ) {
			log.debug("부가세(온라인소매거래-계좌이체) ERP전송 건이 존재하지 않습니다.1");
			logMng.writeLogFile("부가세(온라인소매거래-계좌이체) ERP전송 건이 존재하지 않습니다.1");
			requestMap.put("REMARK", "(월)부가세(온라인소매거래-계좌이체) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}
		
		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "12");

		for (int i = 0; i < lstAmtInfo.size(); i++) {

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();
			alItem   = new ArrayList();
			
			mEqpPrchs = (Map)lstAmtInfo.get(i);
			mEqpPrchs.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	//거래처 코드

			splyPrcAmt	= (BigDecimal)mEqpPrchs.get("SPLY_PRC_AMT");		// 공급가 */
			vat			= (BigDecimal)mEqpPrchs.get("VAT");				// 부가세 */
			fixAmt		= (BigDecimal)mEqpPrchs.get("FIX_AMT");			// 금액 */
			type		= (String)mEqpPrchs.get("TYPE");			        // 부가세유형 */

			mVatItem = new HashMap<String, Object>();	//부가세 Item row map
			
			mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
			mVatItem.put("HWBAS",		splyPrcAmt.toString());					//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		vat.toString());						//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());						//합계금액
			mVatItem.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	    //거래처 코드
			mVatItem.put("NAME1",		(String)mEqpPrchs.get("DEAL_CO_NM"));	//이름 1
			mVatItem.put("STCD2",		(String)mEqpPrchs.get("BIZ_NUM"));		//사업자등록번호
			mVatItem.put("J_1KFREPRE",	(String)mEqpPrchs.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01",		(String)mEqpPrchs.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS",	(String)mEqpPrchs.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND",	(String)mEqpPrchs.get("TYP_OF_BIZ"));	//업종
			mVatItem.put("MWSKZ",		"41");									//부가가치세 코드
			mVatItem.put("TAXGU",		"IV");									//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("ZARKTX1",		"판매수수료(계좌이체)");					//품목텍스트

			/**
			 *    현대백화점 서브점코드  조회
			 *    ZSAC_C_00110 : 현대백화점 서브점코드
			 *    ukey_sub_cd : 서브점코드
			 */
			ukeySubCdMap     = new HashMap();
			ukeySubCdMap.put("deal_co_cd", mEqpPrchs.get("ACC_PLC"));
			ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
			if(ukeySubCdMap != null){
				ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
				if(!"0000".equals(ukeySubCd)){
					mEqpPrchs.put("UKEY_SUB_CD", ukeySubCd);
					mEqpPrchs.put("ZGCODE", ukeySubCd);
					mVatItem.put("ZGCODE",	ukeySubCd);		//품목텍스트
				}

			}

			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
		}		
			
		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		oHead = new Object[1];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}

	/**
	 * 부가세(온라인소매거래-카드결제) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendErpOsCard(SqlMapClient sqlMap) throws Exception {

		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mEqpPrchs			= null;			//단말기매입조회 row map
		BigDecimal splyPrcAmt	= null;			//공급가
		BigDecimal vat 			= null;			//부가세
		BigDecimal fixAmt		= null;			//금액
		
		String sknDealCoCd		= "";			//SKN 거래처 코드
		String type             = null;
		Map mSknInfo			= null;			//Skn거래처정보 map
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo); 
		
		//제조업체 단말기 매입 조회
		List lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getErpOsCardList", requestMap);

		if ( lstAmtInfo.size() == 0 ) {
			log.debug("부가세(온라인소매거래-카드결제) ERP전송 건이 존재하지 않습니다.1");
			logMng.writeLogFile("부가세(온라인소매거래-카드결제) ERP전송 건이 존재하지 않습니다.1");
			requestMap.put("REMARK", "(월)부가세(온라인소매거래-카드결제) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}
		
		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "13");

		for (int i = 0; i < lstAmtInfo.size(); i++) {

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();
			alItem   = new ArrayList();
			
			mEqpPrchs = (Map)lstAmtInfo.get(i);
			mEqpPrchs.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	//거래처 코드

			splyPrcAmt	= (BigDecimal)mEqpPrchs.get("SPLY_PRC_AMT");		// 공급가 */
			vat			= (BigDecimal)mEqpPrchs.get("VAT");				// 부가세 */
			fixAmt		= (BigDecimal)mEqpPrchs.get("FIX_AMT");			// 금액 */
			type		= (String)mEqpPrchs.get("TYPE");			        // 부가세유형 */

			mVatItem = new HashMap<String, Object>();	//부가세 Item row map
			
			mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
			mVatItem.put("HWBAS",		splyPrcAmt.toString());					//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		vat.toString());						//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());						//합계금액
			mVatItem.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	    //거래처 코드
			mVatItem.put("NAME1",		(String)mEqpPrchs.get("DEAL_CO_NM"));	//이름 1
			mVatItem.put("STCD2",		(String)mEqpPrchs.get("BIZ_NUM"));		//사업자등록번호
			mVatItem.put("J_1KFREPRE",	(String)mEqpPrchs.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01",		(String)mEqpPrchs.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS",	(String)mEqpPrchs.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND",	(String)mEqpPrchs.get("TYP_OF_BIZ"));	//업종
			mVatItem.put("MWSKZ",		"41");									//부가가치세 코드
			mVatItem.put("TAXGU",		"IV");									//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("ZARKTX1",		"판매수수료(카드결제)");					//품목텍스트

			/**
			 *    현대백화점 서브점코드  조회
			 *    ZSAC_C_00110 : 현대백화점 서브점코드
			 *    ukey_sub_cd : 서브점코드
			 */
			ukeySubCdMap     = new HashMap();
			ukeySubCdMap.put("deal_co_cd", mEqpPrchs.get("ACC_PLC"));
			ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
			if(ukeySubCdMap != null){
				ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
				if(!"0000".equals(ukeySubCd)){
					mEqpPrchs.put("UKEY_SUB_CD", ukeySubCd);
					mEqpPrchs.put("ZGCODE", ukeySubCd);
					mVatItem.put("ZGCODE",	ukeySubCd);		//품목텍스트
				}
				
			}

			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
		}		
			
		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		oHead = new Object[1];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}

	/**
	 * 부가세(온라인도매거래-단말기) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendErpOaMobile(SqlMapClient sqlMap) throws Exception {

		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mEqpPrchs			= null;			//단말기매입조회 row map
		BigDecimal splyPrcAmt	= null;			//공급가
		BigDecimal vat 			= null;			//부가세
		BigDecimal fixAmt		= null;			//금액
		
		String sknDealCoCd		= "";			//SKN 거래처 코드
		String type             = null;
		Map mSknInfo			= null;			//Skn거래처정보 map
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo); 
		//제조업체 단말기 매입 조회
		lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getErpOaMobileList", requestMap);

		if ( lstAmtInfo.size() == 0 ) {
			log.debug("부가세(온라인도매거래-단말기) ERP전송 건이 존재하지 않습니다.1");
			logMng.writeLogFile("부가세(온라인도매거래-단말기) ERP전송 건이 존재하지 않습니다.1");
			requestMap.put("REMARK", "부가세(온라인도매거래-단말기) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "14");

		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();
		alItem   = new ArrayList();
		
		for (int i = 0; i < lstAmtInfo.size(); i++) {
			mEqpPrchs = (Map)lstAmtInfo.get(i);
			mEqpPrchs.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	//거래처 코드
			
			splyPrcAmt	= (BigDecimal)mEqpPrchs.get("SPLY_PRC_AMT");	// 공급가 */
			vat			= (BigDecimal)mEqpPrchs.get("VAT");				// 부가세 */
			fixAmt		= (BigDecimal)mEqpPrchs.get("FIX_AMT");			// 금액 */
			type		= (String)mEqpPrchs.get("TYPE");			    // 부가세유형 */

			mVatItem = new HashMap<String, Object>();	//부가세 Item row map
			
			mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
			mVatItem.put("HWBAS",		splyPrcAmt.toString());					//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		vat.toString());						//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());						//합계금액
			mVatItem.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	    //거래처 코드
			mVatItem.put("NAME1",		(String)mEqpPrchs.get("DEAL_CO_NM"));	//이름 1
			mVatItem.put("STCD2",		(String)mEqpPrchs.get("BIZ_NUM"));		//사업자등록번호
			mVatItem.put("J_1KFREPRE",	(String)mEqpPrchs.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01",		(String)mEqpPrchs.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS",	(String)mEqpPrchs.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND",	(String)mEqpPrchs.get("TYP_OF_BIZ"));	//업종
			mVatItem.put("MWSKZ",		"15");									//부가가치세 코드
			mVatItem.put("TAXGU",		"IA");									//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("ZARKTX1",		"단말기");								//품목텍스트
			
			/**
			 *    현대백화점 서브점코드  조회
			 *    ZSAC_C_00110 : 현대백화점 서브점코드
			 *    ukey_sub_cd : 서브점코드
			 */
			ukeySubCdMap     = new HashMap();
			ukeySubCdMap.put("deal_co_cd", mEqpPrchs.get("ACC_PLC"));
			ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
			if(ukeySubCdMap != null){
				ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
				if(!"0000".equals(ukeySubCd)){
					mEqpPrchs.put("UKEY_SUB_CD", ukeySubCd);
					mEqpPrchs.put("ZGCODE", ukeySubCd);
					mVatItem.put("ZGCODE",	ukeySubCd);		//품목텍스트
				}
			}				

			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));

		}
		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		oHead = new Object[1];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}
	
	/**
	 * 부가세(온라인도매거래-입점수수료) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendErpOaCharge(SqlMapClient sqlMap) throws Exception {

		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mEqpPrchs			= null;			//단말기매입조회 row map
		BigDecimal splyPrcAmt	= null;			//공급가
		BigDecimal vat 			= null;			//부가세
		BigDecimal fixAmt		= null;			//금액
		
		String sknDealCoCd		= "";			//SKN 거래처 코드
		String type             = null;
		Map mSknInfo			= null;			//Skn거래처정보 map
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo); 
		//제조업체 단말기 매입 조회
		lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getErpOaChargeList", requestMap);
		
		if ( lstAmtInfo.size() == 0 ) {
			log.debug("부가세(온라인도매거래-입점수수료) ERP전송 건이 존재하지 않습니다.1");
			logMng.writeLogFile("부가세(온라인도매거래-입점수수료) ERP전송 건이 존재하지 않습니다.1");
			requestMap.put("REMARK", "부가세(온라인도매거래-입점수수료) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "15");

		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();
		alItem   = new ArrayList();
		
		for (int i = 0; i < lstAmtInfo.size(); i++) {
			mEqpPrchs = (Map)lstAmtInfo.get(i);
			mEqpPrchs.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	//거래처 코드
			
			splyPrcAmt	= (BigDecimal)mEqpPrchs.get("SPLY_PRC_AMT");	// 공급가 */
			vat			= (BigDecimal)mEqpPrchs.get("VAT");				// 부가세 */
			fixAmt		= (BigDecimal)mEqpPrchs.get("FIX_AMT");			// 금액 */
			type		= (String)mEqpPrchs.get("TYPE");			    // 부가세유형 */

			mVatItem = new HashMap<String, Object>();	//부가세 Item row map
			
			mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
			mVatItem.put("HWBAS",		splyPrcAmt.toString());					//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		vat.toString());						//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());						//합계금액
			mVatItem.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	    //거래처 코드
			mVatItem.put("NAME1",		(String)mEqpPrchs.get("DEAL_CO_NM"));	//이름 1
			mVatItem.put("STCD2",		(String)mEqpPrchs.get("BIZ_NUM"));		//사업자등록번호
			mVatItem.put("J_1KFREPRE",	(String)mEqpPrchs.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01",		(String)mEqpPrchs.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS",	(String)mEqpPrchs.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND",	(String)mEqpPrchs.get("TYP_OF_BIZ"));	//업종
			mVatItem.put("MWSKZ",		"41");									//부가가치세 코드
			mVatItem.put("TAXGU",		"IV");									//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("ZARKTX1",		"판매수수료");							//품목텍스트
			
			/**
			 *    현대백화점 서브점코드  조회
			 *    ZSAC_C_00110 : 현대백화점 서브점코드
			 *    ukey_sub_cd : 서브점코드
			 */
			ukeySubCdMap     = new HashMap();
			ukeySubCdMap.put("deal_co_cd", mEqpPrchs.get("ACC_PLC"));
			ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
			if(ukeySubCdMap != null){
				ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
				if(!"0000".equals(ukeySubCd)){
					mEqpPrchs.put("UKEY_SUB_CD", ukeySubCd);
					mEqpPrchs.put("ZGCODE", ukeySubCd);
					mVatItem.put("ZGCODE",	ukeySubCd);		//품목텍스트
				}
			}				

			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
		}

		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		oHead = new Object[1];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}
	
	/**
	 * (월)부가세(할부채권) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendBondVat(SqlMapClient sqlMap) throws Exception {

		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mCnsgDealAmt = null;			//위탁거래금액조회 row map
		BigDecimal saleAmt = null;			//현금매출 + 카드매출
		BigDecimal saleAmt2 = null;			//할부채권이자
		BigDecimal splyPrc = null;			//공급가
		BigDecimal splyPrc2 = null;			//공급가
		Map ukeySubCdMap = null;
		String ukeySubCd = "";


		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY", agency);
		lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getBondAmtList", requestMap);
		if ( lstAmtInfo.size() == 0) {
			log.debug(agency + " : (월)부가세(할부채권) ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile(agency + " : (월)부가세(할부채권) ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)부가세(할부채권) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "16");

		alItem = new ArrayList();
		for (int i = 0; i < lstAmtInfo.size(); i++) {
			mCnsgDealAmt = (Map)lstAmtInfo.get(i);
			
			saleAmt = (BigDecimal)mCnsgDealAmt.get("SALE_AMT");
			splyPrc = saleAmt.divide(new BigDecimal("1.1"), BigDecimal.ROUND_HALF_UP);
			
			mVatItem = new HashMap<String, Object>();	//부가세 Item row map
			
			mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
			mVatItem.put("MWSKZ",		"15");									//부가가치세 코드
			mVatItem.put("TAXGU",		"IA");									//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("HWBAS",		splyPrc.toString());		            //과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		saleAmt.subtract(splyPrc).toString());	//금액(세액)
			mVatItem.put("ZTOTAMT",		saleAmt.toString());		            //합계금액
			mVatItem.put("DEAL_CO_CD",	(String)mCnsgDealAmt.get("AGENCY"));	//대리점
			mVatItem.put("ZTAXCOUNT",	"100");		                            //세금계산서번호
			mVatItem.put("ZARKTX1",		"할부채권");								//품목텍스트
			
			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
			
		}

		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		int size = 1;
		oHead = new Object[size];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);
		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}

	/**
	 * (월)부가세(할부채권이자) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendBondInterest(SqlMapClient sqlMap) throws Exception {

		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mCnsgDealAmt = null;			//위탁거래금액조회 row map
		BigDecimal saleAmt = null;			//현금매출 + 카드매출
		BigDecimal saleAmt2 = null;			//할부채권이자
		BigDecimal splyPrc = null;			//공급가
		BigDecimal splyPrc2 = null;			//공급가
		Map ukeySubCdMap = null;
		String ukeySubCd = "";


		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY", agency);
		lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getBondGrantList", requestMap);
		if ( lstAmtInfo.size() == 0) {
			log.debug(agency + " : (월)부가세(할부채권이자) ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile(agency + " : (월)부가세(할부채권이자) ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)부가세(할부채권이자) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "17");

		alItem = new ArrayList();
		for (int i = 0; i < lstAmtInfo.size(); i++) {
			mCnsgDealAmt = (Map)lstAmtInfo.get(i);
			
			saleAmt = (BigDecimal)mCnsgDealAmt.get("ALLOT_AMT");
			splyPrc = saleAmt.multiply(new BigDecimal("10"));
			
			mVatItem = new HashMap<String, Object>();	//부가세 Item row map
			
			mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
			mVatItem.put("MWSKZ",		"15");									//부가가치세 코드
			mVatItem.put("TAXGU",		"IA");									//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("HWBAS",		splyPrc.toString());		            //과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		saleAmt.toString());;	                //금액(세액)
			mVatItem.put("ZTOTAMT",		saleAmt.add(splyPrc).toString());       //합계금액
			mVatItem.put("DEAL_CO_CD",	(String)mCnsgDealAmt.get("AGENCY"));	//대리점
			mVatItem.put("ZTAXCOUNT",	"100");		                            //세금계산서번호
			mVatItem.put("ZARKTX1",		"할부채권이자");							//품목텍스트
			
			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
			
		}

		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		int size = 1;
		oHead = new Object[size];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);
		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();

	}

	/**
	 * (월)부가세(약정보조금) ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendBondGrant(SqlMapClient sqlMap) throws Exception {

		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mCnsgDealAmt = null;			//위탁거래금액조회 row map
		BigDecimal saleAmt = null;			//현금매출 + 카드매출
		BigDecimal saleAmt2 = null;			//할부채권이자
		BigDecimal splyPrc = null;			//공급가
		BigDecimal splyPrc2 = null;			//공급가
		Map ukeySubCdMap = null;
		String ukeySubCd = "";


		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY", agency);
		lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getBondInterestList", requestMap);

		if ( lstAmtInfo.size() == 0) {
			log.debug(agency + " : (월)부가세(약정보조금) ERP전송 건이 존재하지 않습니다.1");
			logMng.writeLogFile(agency + " : (월)부가세(약정보조금) ERP전송 건이 존재하지 않습니다.2");
			requestMap.put("REMARK", "(월)부가세(약정보조금) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "18");

		alItem = new ArrayList();
		for (int i = 0; i < lstAmtInfo.size(); i++) {
			mCnsgDealAmt = (Map)lstAmtInfo.get(i);

			saleAmt = (BigDecimal)mCnsgDealAmt.get("AGRMT_AMT");
			splyPrc = saleAmt.divide(new BigDecimal("1.1"), BigDecimal.ROUND_HALF_UP);
			
			mVatItem = new HashMap<String, Object>();	//부가세 Item row map
			
			mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
			mVatItem.put("MWSKZ",		"15");									//부가가치세 코드
			mVatItem.put("TAXGU",		"IA");									//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("HWBAS",		splyPrc.toString());		            //과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		saleAmt.subtract(splyPrc).toString());	//금액(세액)
			mVatItem.put("ZTOTAMT",		saleAmt.toString());		            //합계금액
			mVatItem.put("DEAL_CO_CD",	(String)mCnsgDealAmt.get("AGENCY"));	//대리점
			mVatItem.put("ZTAXCOUNT",	"100");		                            //세금계산서번호
			mVatItem.put("ZARKTX1",		"약정보조금");							//품목텍스트
			
			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
			
		}

		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		int size = 1;
		oHead = new Object[size];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);
		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
		
	}

	/**
	 * 수정 전자세금계산서 ERP전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendTaxChange(SqlMapClient sqlMap) throws Exception {
		
		Map taxChangeMap             = null;	//수정 전자 세금계산서 row map

		agency = (String)agencyMap.get("AGENCY");
		userCd = ErpCommon.nullToSpace((String)agencyMap.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
			
		requestMap.put("AGENCY"    , agency);
		requestMap.put("DEAL_CO_CD", pDealCo); 

		lstAmtInfo = (List)sqlMap.queryForList("SACERP08190.getTaxChangeList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			log.debug(agency + " : 수정 전자세금계산서 ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile(agency + " : 수정 전자세금계산서 ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "수정 전자세금계산서 ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08190.updateTsacMmTaxErpTrms2", requestMap);
			return;
		}

		//set vat header info
		mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
				zbudat, userCd.equals("") ? PROG_ID : userCd, "19");

		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();
		alItem   = new ArrayList();

		for (int i = 0; i < lstAmtInfo.size(); i++) {
			taxChangeMap = (Map)lstAmtInfo.get(i);
			taxChangeMap.put("ZITEM", "1");
			
			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, taxChangeMap));
		
		}
		mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
		
		int size = 1;
		oHead = new Object[size];
		oHead[0] = mHead;
		
		imTables.put("T_HEAD",	oHead);
		imTables.put("T_BODY",	alItem.toArray());
		
		retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		/**
		 *   (월) 세금계산서 EPR 전송 요청 수정 KEY 입력
		 */
		retMap.put("YYMM"   , pYymm);
		retMap.put("AGENCY" , pAgency);
		retMap.put("STL_PLC", pDealCo);
		retMap.put("SEQ"    , pSeq);
		ErpCommon.insertTaxTrTable(sqlMap, imTables, retMap);
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}	
}
