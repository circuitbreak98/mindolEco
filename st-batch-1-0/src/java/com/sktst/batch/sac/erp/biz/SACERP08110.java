package com.sktst.batch.sac.erp.biz;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/판매회계</li>
 * <li>설 명 : . </li>
 * <li>작성일 : 2009-04-28</li>
 * </ul>
 *
 * @author 하창주 (hachangjoo)
 */
public class SACERP08110 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08110";

	private SAP sap = null;

	private List lstJourStdInfo	= null;		//분개기준정보
	private List lstAmtInfo		= null;		//금액정보
	private List lstAgency		= null;		//대리점목록
	
	private String zbudat 		= "";		//귀속일자
	private String budat 		= "";		//전표기장일
	private String userCd		= "";		//대리점 정산담당자 사번
	private String agency		= "";		//대리점 거래처코드
	private String dealCoCd		= "";		//직영점, 카드사
	private String pAgency		= "";		//대리점 거래처코드(shell parameter)
	private String pDealCo		= "";		//거래처코드(shell parameter)
	private String pZgubun		= "";		//전송구분(shell parameter)
	private String remarkMsg	= "";		//비고 message

	private Map<String, Object> imParams = null;
	private Map<String, Object> imTables = null;
	private Map<String, Object> mKey = null;
	private ArrayList alHead = null;
	private ArrayList alItem = null;

	private Object[] oKey = null;	//Erp Key table row map array
	private Map mHead = null;		//Erp Head table row map
	private Map mJourStd = null;	//분개기준 row map
	private Map mAgency = null;		//대리점 map

	private Map<String, Object> retMap = null;	//전송결과 map

	private Map<String, Object> requestMap = new HashMap<String, Object>();

	/**
	 * GMT기준시간중의 한국표준시를 반환한다. 
	 * @author	하창주 (hachangjoo)
	 * @param	void
	 * @return GMT+09:00형태의 대한민국표준시
	 */
	public static Calendar getCalendar(){
		Calendar calendar =
		new GregorianCalendar(
			TimeZone.getTimeZone("GMT+09:00"),Locale.KOREA);
		calendar.setTime (new Date());

		return calendar;
	}
	
	/**
	 * 매출발생 배치 프로그램을 수행한다.
	 * @author	하창주 (hachangjoo)
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
				log.debug(PROG_ID + ".execute");
				log.debug(request.toString());
			}
			
			//② Transaction 시작
			sqlMap.startTransaction();
			
			//③ 요청 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");
			String dpstYM = "";
			String dpstMonth = "";		//월
			if( request.size() > 1 ) {
				dpstYM	= (String)request.get("args1");
				pAgency	= (String)request.get("args2");
				pDealCo	= (String)request.get("args3");
				pZgubun	= (String)request.get("args4");
				
				pDealCo = "";
				dpstYM = dpstYM.equals("?") ? "" : dpstYM;
				pAgency = pAgency.equals("?") ? "" : pAgency;
				pDealCo = pDealCo.equals("?") ? "" : pDealCo;
				pZgubun = pZgubun.equals("?") ? "" : pZgubun;

				pDealCo = "";
				dpstMonth = dpstYM.substring(4,6);
				logMng.writeLogFile("args1[입금년월] : " + dpstYM);
				logMng.writeLogFile("args1[입금월]   : " + dpstMonth);
				logMng.writeLogFile("args2[대리점] : " + pAgency);
				logMng.writeLogFile("args3[거래처] : " + pDealCo);
				logMng.writeLogFile("args4[전송구분] : " + pZgubun);
			}
			
			if (dpstYM.equals("")) {
				dpstYM = ErpCommon.getLastMonth();	//입금년월(전월)
				dpstMonth = dpstYM.substring(4,6);
			}
			String lastDay = ErpCommon.getLastDay(Integer.parseInt(dpstYM.substring(0, 4)),
					Integer.parseInt(dpstYM.substring(4)) );
			zbudat = dpstYM + lastDay;	//귀속일자
			budat = ErpCommon.getDayInterval(zbudat, 1); // 전기일자
			
			/**
			 *   회계팀 확정
			 *   2010년 5월 마감부터 적용
			 *   3,6,9,12월은 전기일 증빙일이 동일한 일자
			 */
			if(dpstYM.compareTo("201005") < 0 || "03".equals(dpstMonth) || "06".equals(dpstMonth) || "09".equals(dpstMonth)|| "12".equals(dpstMonth)){
				budat = zbudat; // 전기일자
			}

			log.debug("dpstYM(입금년월) : " + dpstYM);
			log.debug("zbudat(귀속일자) : " + zbudat);
			log.debug("budat(전기일자) : " + budat);

			requestMap.put("YYMM",	dpstYM);
			requestMap.put("AGENCY", pAgency);
			requestMap.put("DEAL_CO_CD", pDealCo);
			requestMap.put("TRMS_ITEM", pZgubun);


			//④ DB 처리
			/** 
			 *  B01 : ZGUBUN_CASH_SALE        : (월)현금매출발생
			 *  B02 : ZGUBUN_CARD_SALE        : (월)카드매출발생
			 */
			if ( "".equals(pZgubun) ) {
				logMng.writeLogFile("(월)현금매출발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_CASH_SALE);
				sendCashSale(sqlMap);
				
				logMng.writeLogFile("(월)카드매출발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_CARD_SALE);
				sendCardSale(sqlMap);
				
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_CASH_SALE) ) {
				logMng.writeLogFile("(월)현금매출발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_CASH_SALE);
				sendCashSale(sqlMap);
				
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_CARD_SALE) ) {
				logMng.writeLogFile("(월)카드매출발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_CARD_SALE);
				sendCardSale(sqlMap);

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
			
			sqlMap.endTransaction();
			sap.disconnect();
			logMng.closeLogFile();
		}

		//⑦ 처리 결과 코드 리턴
		return 0;
	}

	/**
	 * (월)현금매출발생 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendCashSale(SqlMapClient sqlMap) throws Exception {

		//현금매출금 row map
		Map mCashSale = null;
		Map ukeySubCdMap = null;
		String ukeySubCd = "";
		BigDecimal saleAmt	= null;		//현금매출금
		BigDecimal wrbrt	= null;		//Item 금액
		String dpstYm = (String) requestMap.get("YYMM");
		lstAgency = (List)sqlMap.queryForList("SACERP08110.getAgencyList", requestMap);
		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)현금매출발생 ERP전송 건이 존재하지 않습니다.1";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}
		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			requestMap.put("AGENCY", agency);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08110.getCashSaleAmtList", requestMap);
			
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)현금매출발생 ERP전송 건이 존재하지 않습니다.2";
				ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
				continue;
			}

			//set key info
			oKey = ErpCommon.makeKey((String)mAgency.get("UKEY_AGENCY_CD"),
					zbudat,
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
							String.valueOf(lstAmtInfo.size())
			); 

			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", budat);
			oKey[0] = mKey;
			
			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCashSale = (Map)lstAmtInfo.get(i);
				dealCoCd = (String)mCashSale.get("DEAL_CO_CD");

				saleAmt = (BigDecimal)mCashSale.get("SALE_AMT");
				if ( saleAmt.compareTo(new BigDecimal("0")) == 0 ) {
					logMng.writeLogFile("대리점[" + agency + "] : 직영점[" + dealCoCd + "] : (월)현금매출발생금[SALE_AMT] : " + saleAmt.toString());
				}

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						saleAmt.toString()
				);
				mHead.put("ACC_PLC", dealCoCd);
				alHead.add(mHead);

				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//SEQ_NO 별 금액지정
					wrbrt = saleAmt;	//차변 : 002 + 003
					if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = saleAmt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);	//대변1 : 상품매출
					} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = saleAmt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);
						wrbrt = saleAmt.subtract(wrbrt);	//대변 2:매출부가세
					}
					/**
					 *    현대백화점 서브점코드  조회
					 *    ZSAC_C_00110 : 현대백화점 서브점코드
					 *    ukey_sub_cd : 서브점코드
					 */
					ukeySubCdMap     = new HashMap();
					ukeySubCdMap.put("deal_co_cd", dealCoCd);
					ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
					if(ukeySubCdMap != null){
						ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
						if(!"0000".equals(ukeySubCd)){
							mCashSale.put("UKEY_SUB_CD", ukeySubCd);
						}
					}

					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							dealCoCd,
							wrbrt.toString(),
							(String)mCashSale.get("UKEY_SUB_CD"))
					);
				}
			}

			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());

			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			Map mKey = (Map<String, Object>)alKey.get(0);
			mCashSale.put("TRMS_DT", mKey.get("ZIFDATE"));
			mCashSale.put("FIX_YN", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08110.updateSaleAmtSendInfo", mCashSale);     /* 소매거래    */
			sqlMap.update("SACERP08110.updateCmmsAccSendInfo", mCashSale);     /* 수수료 월정산   */
			sqlMap.update("SACERP08110.updateMcCmmsAccSendInfo", mCashSale);   /* M채널 수수료 월정산   */
			sqlMap.update("SACERP08110.updateOaCmmsAccSendInfo", mCashSale);   /* 온라인 도매 수수료 월정산   */
			sqlMap.update("SACERP08110.updateOtlCmmsAccSendInfo", mCashSale);  /* 온라인 결제 수수료 월정산   */

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		}
	}

	/**
	 * (월)카드매출발생 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendCardSale(SqlMapClient sqlMap) throws Exception {

		Map mCardSale = null;	//카드매출금 row map
		BigDecimal cashSaleAmt	= null;		//카드매출금
		BigDecimal wrbrt		= null;		//Item 금액
		Map ukeySubCdMap = null;
		String ukeySubCd = "";
		
		requestMap.put("AGENCY", pAgency);
		requestMap.put("DPST_PLC", pDealCo);

		String dpstYm = (String) requestMap.get("YYMM");

		lstAgency = (List)sqlMap.queryForList("SACERP08110.getAgencyList", requestMap);
		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)카드매출발생 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);

			return;
		}

		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			requestMap.put("AGENCY", agency);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08110.getCardSaleAmtList", requestMap);
			
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)카드매출발생 ERP전송 건이 존재하지 않습니다.";
				ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
				continue;
			}

			//set key info
			oKey = ErpCommon.makeKey((String)mAgency.get("UKEY_AGENCY_CD"),
					zbudat,
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
							String.valueOf(lstAmtInfo.size())
			); 

			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", budat);
			oKey[0] = mKey;
			
			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCardSale = (Map)lstAmtInfo.get(i);
				dealCoCd = (String)mCardSale.get("CARD_CO");

				cashSaleAmt = (BigDecimal)mCardSale.get("CARD_AMT");
				if ( cashSaleAmt.compareTo(new BigDecimal("0")) == 0 ) {
					logMng.writeLogFile("대리점[" + agency + "] : 카드사[" + dealCoCd + "] : (월)카드매출금[CARD_AMT] : " + cashSaleAmt.toString());
				}

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						cashSaleAmt.toString()
				);
				mHead.put("ACC_PLC", dealCoCd);
				alHead.add(mHead);

				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//SEQ_NO 별 금액지정
					wrbrt = cashSaleAmt;	//차변 : 002 + 003
					if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = cashSaleAmt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);	//대변1 : 상품매출
					} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = cashSaleAmt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);
						wrbrt = cashSaleAmt.subtract(wrbrt);	//대변 2:매출부가세
					}

					/**
					 *    현대백화점 서브점코드  조회
					 *    ZSAC_C_00110 : 현대백화점 서브점코드
					 *    ukey_sub_cd : 서브점코드
					 */
					ukeySubCdMap     = new HashMap();
					ukeySubCdMap.put("deal_co_cd", dealCoCd);
					ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
					if(ukeySubCdMap != null){
						ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
						if(!"0000".equals(ukeySubCd)){
							mCardSale.put("UKEY_SUB_CD", ukeySubCd);
						}
					}

					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							dealCoCd,
							wrbrt.toString(),
							(String)mCardSale.get("UKEY_SUB_CD"))
					);
				}
			}

			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());

			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			Map mKey = (Map<String, Object>)alKey.get(0);
			mCardSale.put("TRMS_DT", mKey.get("ZIFDATE"));
			mCardSale.put("FIX_YN", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08110.updateSaleAmtSendInfo", mCardSale);

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		}
	}
	
}
