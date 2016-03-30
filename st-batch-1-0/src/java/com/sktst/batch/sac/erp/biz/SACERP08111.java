package com.sktst.batch.sac.erp.biz;

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
 * <li>설 명 : . </li>
 * <li>작성일 : 2009-04-28</li>
 * </ul>
 *
 * @author 하창주 (hachangjoo)
 */
public class SACERP08111 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08111";

	private SAP sap = null;

	private List lstJourStdInfo	= null;		//분개기준정보
	private List lstAgencyList	= null;		//대리점목록
	private List lstAmtInfo		= null;		//금액정보

	private String userCd		= "";		//대리점 정산담당자 사번
	private String agency		= "";		//대리점 거래처코드
	private String accPlc		= "";		//정산처
	private String accMth		= "";		//정산월
	private String fixDt		= "";		//확정일
	private String zbudat		= "";		//귀속일자
	private String budat 		= "";		//전표기장일
	private String pAgency		= "";		//대리점 거래처코드(shell parameter)
	private String pDealCo		= "";		//거래처코드(shell parameter)
	private String pZgubun		= "";		//전송구분(shell parameter)
	private String remarkMsg	= "";		//비고 message

	private Map<String, Object> imParams = null;
	private Map<String, Object> imTables = null;
	private ArrayList alHead = null;
	private ArrayList alItem = null;
	private Map<String, Object> mKey = null;

	private Object[] oKey = null;	//Erp Key table row map array
	private Map mHead = null;		//Erp Head table row map
	private Map mJourStd = null;	//분개기준 row map
	//private Map mAgency = null;		//대리점 map

	private Map<String, Object> requestMap = new HashMap<String, Object>();
	private Map<String, Object> retMap = null;	//전송결과 map

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
			String accMonth = "";		//월
			if( request.size() > 1 ) {
				accMth	= (String)request.get("args1");
				pAgency	= (String)request.get("args2");
				pDealCo	= (String)request.get("args3");
				pZgubun	= (String)request.get("args4");
				
				accMth = accMth.equals("?") ? "" : accMth;
				pAgency = pAgency.equals("?") ? "" : pAgency;
				pDealCo = pDealCo.equals("?") ? "" : pDealCo;
				pZgubun = pZgubun.equals("?") ? "" : pZgubun;
				
				accMonth = accMth.substring(4,6);
				logMng.writeLogFile("args1[정산년월] : " + accMth);
				logMng.writeLogFile("args1[정산월]   : " + accMonth);
				logMng.writeLogFile("args2[대리점]   : " + pAgency);
				logMng.writeLogFile("args3[거래처]   : " + pDealCo);
				logMng.writeLogFile("args4[전송구분] : " + pZgubun);

			}
			
			if (accMth.equals("")) {
				accMth = ErpCommon.getLastMonth();	//입금년월(전월)
				accMonth = accMth.substring(4,6);
			}
			String lastDay = ErpCommon.getLastDay(Integer.parseInt(accMth.substring(0, 4)),
					Integer.parseInt(accMth.substring(4)) );
			zbudat = accMth + lastDay;	//귀속일자
			budat  = ErpCommon.getDayInterval(zbudat, 1); // 전기일자


			/**
			 *   회계팀 확정
			 *   2010년 5월 마감부터 적용
			 *   3,6,9,12월은 전기일 증빙일이 동일한 일자
			 */
			if(accMth.compareTo("201005") < 0 || "03".equals(accMonth) || "06".equals(accMonth) || "09".equals(accMonth)|| "12".equals(accMonth)){
				budat = zbudat; // 전기일자
			}
			
			log.debug("accMth(정산년월) : " + accMth);
			log.debug("zbudat(귀속일자) : " + zbudat);
			log.debug("budat(전기일자) : " + budat);

			requestMap.put("ACC_MTH",	accMth);
			requestMap.put("YYMM",		accMth);
			requestMap.put("AGENCY", 	pAgency);
			requestMap.put("TRMS_ITEM", pZgubun);

			//④ DB 처리
			/** 
			 *  B03 : ZGUBUN_ALLOT_SALE         : (월)할부매출발생
			 *  B04 : ZGUBUN_ALLOT_INT          : (월)할부채권이자발생
			 *  F16 : ZGUBUN_SKT_AHEAD_DPST     : (월)SKT선입금
			 *  B05 : ZGUBUN_ASTAMT_SALE        : (월)보조금매출
			 *  B06 : ZGUBUN_PR_MNY_SALE_SKT    : (월)장려금매출 - SKT
			 *  B07 : ZGUBUN_PR_MNY_SALE_OTHERS : (월)장려금매출 - 타제조업체
			 *  B08 : ZGUBUN_CMMS_SALE_SKT      : (월)위탁수수료매출 - SKT
			 *  C01 : ZGUBUN_ALLOT_MTCH         : (월)할부채권 1, 2차 상계
			 */
			if ( "".equals(pZgubun) ) {
				logMng.writeLogFile("(월)할부매출발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_ALLOT_SALE);
				sendAllotSale(sqlMap);
				logMng.writeLogFile("(월)할부채권이자발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_ALLOT_INT);
				sendAllotInt(sqlMap);
				logMng.writeLogFile("(월)SKT선입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_SKT_AHEAD_DPST);
				sendSktAheadDpst(sqlMap);
				logMng.writeLogFile("(월)보조금매출 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_ASTAMT_SALE);
				sendAstamtSale(sqlMap);
				logMng.writeLogFile("(월)장려금매출 SKTsend >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_PR_MNY_SALE_SKT);
				sendPrMnySaleSkt(sqlMap);

				logMng.writeLogFile("(월)장려금매출 - 타제조업체 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_PR_MNY_SALE_OTHERS);
				//sendPrMnySaleOthers(sqlMap);

				logMng.writeLogFile("(월)위탁수수료매출 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_CMMS_SALE_SKT);
				sendCmmsSaleSkt(sqlMap);
				logMng.writeLogFile("(월)할부채권 1, 2차 상계 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_ALLOT_MTCH);
				sendAllotMtch(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_ALLOT_SALE) ) {
				logMng.writeLogFile("(월)할부매출발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_ALLOT_SALE);
				sendAllotSale(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_ALLOT_INT) ) {
				logMng.writeLogFile("(월)할부채권이자발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_ALLOT_INT);
				sendAllotInt(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_SKT_AHEAD_DPST) ) {
				logMng.writeLogFile("(월)SKT선입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_SKT_AHEAD_DPST);
				sendSktAheadDpst(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_ASTAMT_SALE) ) {
				logMng.writeLogFile("(월)보조금매출 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_ASTAMT_SALE);
				sendAstamtSale(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_PR_MNY_SALE_SKT) ) {
				logMng.writeLogFile("(월)장려금매출 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_PR_MNY_SALE_SKT);
				sendPrMnySaleSkt(sqlMap);

			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_PR_MNY_SALE_OTHERS) ) {
				logMng.writeLogFile("(월)장려금매출 - 타제조업체 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_PR_MNY_SALE_OTHERS);
				sendPrMnySaleOthers(sqlMap);

			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_CMMS_SALE_SKT) ) {
				logMng.writeLogFile("(월)위탁수수료매출 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_CMMS_SALE_SKT);
				sendCmmsSaleSkt(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_ALLOT_MTCH) ) {
				logMng.writeLogFile("(월)할부채권 1, 2차 상계 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_ALLOT_MTCH);
				sendAllotMtch(sqlMap);
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
	 * (월)할부매출발생 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendAllotSale(SqlMapClient sqlMap) throws Exception {

		//할부매출발생 row map
		Map mAllotSale = null;
		BigDecimal allotSale	= null;		//할부매출
		BigDecimal wrbrt		= null;		//Item 금액
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAmtInfo = (List)sqlMap.queryForList("SACERP08111.getAllotSaleList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(월)할부매출발생 월정산 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);

			return;
		}

		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAllotSale = (Map)lstAmtInfo.get(idx);
			agency	= (String)mAllotSale.get("AGENCY");
			accPlc	= (String)mAllotSale.get("ACC_PLC");
			fixDt	= (String)mAllotSale.get("FIX_DT");

			userCd	= ErpCommon.nullToSpace((String)mAllotSale.get("USER_CD"));
			userCd	= ErpCommon.fillZeroFront(userCd, 8);
			allotSale = (BigDecimal)mAllotSale.get("ALLOT_SALE");
			
			//set key info
			oKey = ErpCommon.makeKey((String)mAllotSale.get("UKEY_AGENCY_CD"),
					zbudat,
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
					"1"
			); 

			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", budat);
			oKey[0] = mKey;

			alHead = new ArrayList();
			alItem = new ArrayList();

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					"1",
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					allotSale.toString()
			);
			mHead.put("ACC_PLC", accPlc);	//정산처
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				//SEQ_NO 별 금액지정
				wrbrt = allotSale;					//차변 : 002 + 003
				if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = allotSale.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);	//대변1 : 상품매출
				} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = allotSale.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);
					wrbrt = allotSale.subtract(wrbrt);	//대변 2:매출부가세
				}

				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", accPlc);
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mAllotSale.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						accPlc,
						wrbrt.toString(),
						(String)mAllotSale.get("UKEY_SUB_CD"))
				);
			}

			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());

			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			Map mKey = (Map<String, Object>)alKey.get(0);
			mAllotSale.put("ZIFDATE", mKey.get("ZIFDATE"));
			mAllotSale.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08111.updateAllotBondSendInfo", mAllotSale);

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}

	/**
	 * (월)할부채권이자발생 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendAllotInt(SqlMapClient sqlMap) throws Exception {

		//할부채권이자발생 row map
		Map mAllotInt = null;
		BigDecimal allotInt	= null;		//할부채권이자
		BigDecimal wrbrt	= null;		//Item 금액
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAmtInfo = (List)sqlMap.queryForList("SACERP08111.getAllotIntList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(월)할부채권이자발생 월정산 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);

			return;
		}

		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAllotInt = (Map)lstAmtInfo.get(idx);
			agency	= (String)mAllotInt.get("AGENCY");
			accPlc	= (String)mAllotInt.get("ACC_PLC");
			fixDt	= (String)mAllotInt.get("FIX_DT");

			userCd	= ErpCommon.nullToSpace((String)mAllotInt.get("USER_CD"));
			userCd	= ErpCommon.fillZeroFront(userCd, 8);
			allotInt = (BigDecimal)mAllotInt.get("ALLOT_INT");

			//set key info
			oKey = ErpCommon.makeKey((String)mAllotInt.get("UKEY_AGENCY_CD"),
					zbudat,
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
					"1"
			);

			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", budat);
			oKey[0] = mKey;

			oKey[0] = mKey;

			alHead = new ArrayList();
			alItem = new ArrayList();

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					"1",
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					allotInt.toString()
			);
			mHead.put("ACC_PLC", accPlc);
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				//SEQ_NO 별 금액지정
				wrbrt = allotInt;					//차변 : 002 + 003
				
				if( "001".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = allotInt.add(wrbrt.multiply(new BigDecimal("10")));
				} else if( "002".equals((String)mJourStd.get("SEQ_NO")) ||
						"004".equals((String)mJourStd.get("SEQ_NO")) ||
						"005".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = wrbrt.multiply(new BigDecimal("10"));
				}
				
				/*
				if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = allotInt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);	//대변1 : 할부채권이자액
				} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = allotInt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);
					wrbrt = allotInt.subtract(wrbrt);	//대변 2:매출부가세
				}
				*/
				
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", accPlc);
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mAllotInt.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						accPlc,
						wrbrt.toString(),
						(String)mAllotInt.get("UKEY_SUB_CD"))
				);
			}

			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());

			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			//⑤ 결과 데이터 처리
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}


	/**
	 * (월)SKT선입금
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendSktAheadDpst(SqlMapClient sqlMap) throws Exception {

		//할부채권_월정산 row map
		Map mAdpayAmt = null;
		BigDecimal adpayAmt	= null;		//SKT선입금
		BigDecimal wrbrt	= null;		//Item 금액
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAmtInfo = (List)sqlMap.queryForList("SACERP08111.getAdpayAmtList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(월)SKT선입금 월정산 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);

			return;
		}

		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAdpayAmt = (Map)lstAmtInfo.get(idx);
			agency	= (String)mAdpayAmt.get("AGENCY");
			accPlc	= (String)mAdpayAmt.get("ACC_PLC");
			fixDt	= (String)mAdpayAmt.get("FIX_DT");

			userCd	= ErpCommon.nullToSpace((String)mAdpayAmt.get("USER_CD"));
			userCd	= ErpCommon.fillZeroFront(userCd, 8);
			adpayAmt = (BigDecimal)mAdpayAmt.get("ADPAY_AMT");

			//set key info
			oKey = ErpCommon.makeKey((String)mAdpayAmt.get("UKEY_AGENCY_CD"),
					zbudat,
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
					"1"
			);

			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", budat);
			oKey[0] = mKey;

			alHead = new ArrayList();
			alItem = new ArrayList();

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					"1",
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					adpayAmt.toString()
			);
			mHead.put("ACC_PLC", accPlc);
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);

				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", accPlc);
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mAdpayAmt.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						accPlc,
						adpayAmt.toString(),
						(String)mAdpayAmt.get("UKEY_SUB_CD"))
				);
			}

			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());

			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			//⑤ 결과 데이터 처리
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}



	/**
	 * (월)보조금매출 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendAstamtSale(SqlMapClient sqlMap) throws Exception {
		
		Map mAstamt = null;					//보조금매출 row map
		BigDecimal agrmtAstamt	= null;		//약정보조금
		BigDecimal wrbrt		= null;		//Item 금액
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAmtInfo = (List)sqlMap.queryForList("SACERP08111.getAstamtList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(월)보조금매출 월정산 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);

			return;
		}

		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAstamt = (Map)lstAmtInfo.get(idx);
			agency	= (String)mAstamt.get("AGENCY");
			accPlc	= (String)mAstamt.get("ACC_PLC");
			fixDt	= (String)mAstamt.get("FIX_DT");

			userCd	= ErpCommon.nullToSpace((String)mAstamt.get("USER_CD"));
			userCd	= ErpCommon.fillZeroFront(userCd, 8);
			agrmtAstamt = (BigDecimal)mAstamt.get("AGRMT_ASTAMT");

			//set key info
			oKey = ErpCommon.makeKey((String)mAstamt.get("UKEY_AGENCY_CD"),
					zbudat,
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
					"1"
			); 

			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", budat);
			oKey[0] = mKey;

			alHead = new ArrayList();
			alItem = new ArrayList();
			
			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					"1",
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					agrmtAstamt.toString()
			);
			mHead.put("ACC_PLC", accPlc);
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				//SEQ_NO 별 금액지정
				wrbrt = agrmtAstamt;	//차변 : 002 + 003
				if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = agrmtAstamt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);	//대변1 : 상품매출
				} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = agrmtAstamt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);
					wrbrt = agrmtAstamt.subtract(wrbrt);	//대변 2:매출부가세
				}
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", accPlc);
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mAstamt.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						accPlc,
						wrbrt.toString(),
						(String)mAstamt.get("UKEY_SUB_CD"))
				);
			}

			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());

			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}

	/**
	 * (월)장려금금매출 SKT ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendPrMnySaleSkt(SqlMapClient sqlMap) throws Exception {
		
		Map mPrMnySkt = null;		//장려금금매출 row map
		BigDecimal fixSplyPrc		= null;		// 공급가
		BigDecimal fixVat			= null;		// 부가세
		BigDecimal fixAmt			= null;		// 금액
		BigDecimal wrbrt			= null;		// Item 금액
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAmtInfo = (List)sqlMap.queryForList("SACERP08111.getPrMnySktList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(월)장려금금매출 SKT 월정산 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);

			return;
		}

		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mPrMnySkt = (Map)lstAmtInfo.get(idx);
			agency	= (String)mPrMnySkt.get("AGENCY");
			accPlc	= (String)mPrMnySkt.get("ACC_PLC");
			fixDt	= (String)mPrMnySkt.get("FIX_DT");

			userCd	= ErpCommon.nullToSpace((String)mPrMnySkt.get("USER_CD"));
			userCd	= ErpCommon.fillZeroFront(userCd, 8);
			fixSplyPrc	= (BigDecimal)mPrMnySkt.get("FIX_SPLY_PRC");	// 공급가 */
			fixVat		= (BigDecimal)mPrMnySkt.get("FIX_VAT");			// 부가세 */
			fixAmt		= (BigDecimal)mPrMnySkt.get("FIX_AMT");			// 금액 */
			log.debug("fixSplyPrc : " + fixSplyPrc.toString());
			log.debug("fixVat : " + fixVat.toString());
			log.debug("fixAmt : " + fixAmt.toString());
			
			//set key info
			oKey = ErpCommon.makeKey((String)mPrMnySkt.get("UKEY_AGENCY_CD"),
					zbudat,
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
					"1"
			); 

			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", budat);
			oKey[0] = mKey;

			alHead = new ArrayList();
			alItem = new ArrayList();

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					"1",
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					fixAmt.toString()
			);
			mHead.put("ACC_PLC", accPlc);
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				//SEQ_NO 별 금액지정
				if( "001".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = fixAmt;			//차변 : 002 + 003
				} else if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = fixSplyPrc;		//대변1 : 장려금매출
				} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = fixVat;			//대변 2 : 매출부가세
				}
				log.debug("wrbrt : " + wrbrt.toString());
				
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", accPlc);
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mPrMnySkt.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						accPlc,
						wrbrt.toString(),
						(String)mPrMnySkt.get("UKEY_SUB_CD"))
				);
			}
			
			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			Map mKey = (Map<String, Object>)alKey.get(0);
			mPrMnySkt.put("ZIFDATE", mKey.get("ZIFDATE"));
			mPrMnySkt.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08111.updatePrMnySendInfo", mPrMnySkt);
			sqlMap.update("SACERP08111.updateSktCmmsM", mPrMnySkt);
			sqlMap.update("SACERP08111.updateSktCmmsDedtM", mPrMnySkt);

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}

	}


	/**
	 * (월)장려금금매출 - 제조업체 SKT ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendPrMnySaleOthers(SqlMapClient sqlMap) throws Exception {
		
		Map mPrMnySkt = null;		//장려금금매출 row map
		BigDecimal fixSplyPrc		= null;		// 공급가
		BigDecimal fixVat			= null;		// 부가세
		BigDecimal fixAmt			= null;		// 금액
		BigDecimal wrbrt			= null;		// Item 금액
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAmtInfo = (List)sqlMap.queryForList("SACERP08111.getPrMnyOthersList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(월)장려금금매출-제조업체 월정산 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);

			return;
		}

		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();
		
		mPrMnySkt = (Map)lstAmtInfo.get(0);
		userCd	= ErpCommon.nullToSpace((String)mPrMnySkt.get("USER_CD"));
		userCd	= ErpCommon.fillZeroFront(userCd, 8);
		//set key info
		oKey = ErpCommon.makeKey((String)mPrMnySkt.get("UKEY_AGENCY_CD"),
				zbudat,
				(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
				userCd.equals("") ? PROG_ID : userCd,
				"1"
		); 

		mKey = (Map<String, Object>) oKey[0];
		mKey.put("BUDAT", budat);
		oKey[0] = mKey;

		alHead = new ArrayList();
		alItem = new ArrayList();

		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			mPrMnySkt = (Map)lstAmtInfo.get(idx);
			agency	= (String)mPrMnySkt.get("AGENCY");
			accPlc	= (String)mPrMnySkt.get("ACC_PLC");

			fixSplyPrc	= (BigDecimal)mPrMnySkt.get("FIX_SPLY_PRC");	// 공급가 */
			fixVat		= (BigDecimal)mPrMnySkt.get("FIX_VAT");			// 부가세 */
			fixAmt		= (BigDecimal)mPrMnySkt.get("FIX_AMT");			// 금액 */
			log.debug("fixSplyPrc : " + fixSplyPrc.toString());
			log.debug("fixVat : " + fixVat.toString());
			log.debug("fixAmt : " + fixAmt.toString());

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					"1",
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					fixAmt.toString()
			);
			mHead.put("ACC_PLC", accPlc);
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				//SEQ_NO 별 금액지정
				if( "001".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = fixAmt;			//차변 : 002 + 003
				} else if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = fixSplyPrc;		//대변1 : 장려금매출
				} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = fixVat;			//대변 2 : 매출부가세
				}
				log.debug("wrbrt : " + wrbrt.toString());
				
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", accPlc);
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mPrMnySkt.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						accPlc,
						wrbrt.toString(),
						(String)mPrMnySkt.get("UKEY_SUB_CD"))
				);
			}
			
			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			//⑤ 결과 데이터 처리

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}

	}

	/**
	 * (월)위탁수수료매출(SKT) ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendCmmsSaleSkt(SqlMapClient sqlMap) throws Exception {
		
		Map mSktCmms = null;		//위탁수수료매출(SKT) row map
		BigDecimal fixSplyPrc		= null;		// 공급가
		BigDecimal fixVat			= null;		// 부가세
		BigDecimal fixAmt			= null;		// 금액
		BigDecimal dedtFixAmt		= null;		// 수수료공제금액
		BigDecimal wrbrt			= null;		// Item 금액
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";
		
		lstAmtInfo = (List)sqlMap.queryForList("SACERP08111.getSktCmmsList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(월)위탁수수료매출(SKT) 월정산 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);

			return;
		}

		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mSktCmms = (Map)lstAmtInfo.get(idx);
			agency	= (String)mSktCmms.get("AGENCY");
			accPlc	= (String)mSktCmms.get("ACC_PLC");
			fixDt	= (String)mSktCmms.get("FIX_DT");

			userCd	= ErpCommon.nullToSpace((String)mSktCmms.get("USER_CD"));
			userCd	= ErpCommon.fillZeroFront(userCd, 8);
			fixSplyPrc		= (BigDecimal)mSktCmms.get("FIX_SPLY_PRC");		// 공급가 */
			fixVat			= (BigDecimal)mSktCmms.get("FIX_VAT");			// 부가세 */
			fixAmt			= (BigDecimal)mSktCmms.get("FIX_AMT");			// 금액 */
			dedtFixAmt		= (BigDecimal)mSktCmms.get("DEDT_FIX_AMT");		// 수수료공제금액 */

			//set key info
			oKey = ErpCommon.makeKey((String)mSktCmms.get("UKEY_AGENCY_CD"),
					zbudat,
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
					"1"
					); 

			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", budat);
			oKey[0] = mKey;

			alHead = new ArrayList();
			alItem = new ArrayList();

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					"1",
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					fixAmt.toString()
			);
			mHead.put("ACC_PLC", accPlc);
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				//SEQ_NO 별 금액지정
				if( "001".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = fixSplyPrc.add(fixVat).subtract(dedtFixAmt);	//차변 : 002 + 003 - 004
				} else if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = fixSplyPrc;		//대변1 : 공급가
				} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = fixVat;			//대변 2 : 매출부가세
				} else if( "004".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = dedtFixAmt;		//차번 2 : 수수료공제금액
				}
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", accPlc);
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mSktCmms.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						accPlc,
						wrbrt.toString(),
						(String)mSktCmms.get("UKEY_SUB_CD"))
				);
			}
			
			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}

	}
	
	/**
	 * (월)할부채권 1, 2차 상계
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendAllotMtch(SqlMapClient sqlMap) throws Exception {
		
		Map mMtchAmt = null;		//상계금액 row map
		BigDecimal mtchAmt			= null;		// 1차상계금액 + 2차상계금액
		//BigDecimal mtchAmt2			= null;		// 2차상계금액
		BigDecimal wrbrt			= null;		// Item 금액
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";
		
		lstAmtInfo = (List)sqlMap.queryForList("SACERP08111.getMtchAmtList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(월)할부채권 1, 2차 상계 월정산 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);

			return;
		}

		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mMtchAmt = (Map)lstAmtInfo.get(idx);
			agency	= (String)mMtchAmt.get("AGENCY");
			fixDt	= (String)mMtchAmt.get("FIX_DT");

			userCd	= ErpCommon.nullToSpace((String)mMtchAmt.get("USER_CD"));
			userCd	= ErpCommon.fillZeroFront(userCd, 8);
			mtchAmt	= (BigDecimal)mMtchAmt.get("MTCH_AMT");		// 1차상계금액 + 2차상계금액
			//mtchAmt2	= (BigDecimal)mMtchAmt.get("MTCH_AMT_2");		// 2차상계금액

			//set key info
			oKey = ErpCommon.makeKey((String)mMtchAmt.get("UKEY_AGENCY_CD"),
					zbudat,
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
					"1"
					); 

			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", budat);
			oKey[0] = mKey;

			alHead = new ArrayList();
			alItem = new ArrayList();

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					"1",
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					mtchAmt.toString()
			);
			mHead.put("ACC_PLC", accPlc);
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				accPlc	= (String)mMtchAmt.get("ACC_PLC");
				//SEQ_NO 별 금액지정
				if( "001".equals((String)mJourStd.get("SEQ_NO")) ) {
					//wrbrt = mtchAmt;					//차변1 : 1차상계금액 + 2차상계금액
					accPlc = ErpCommon.getSknDealCoCd(agency);	//SKN
				} else if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
					//wrbrt = mtchAmt1.add(mtchAmt2);		//대변 : 001 + 003
				} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
					continue;
					//wrbrt = mtchAmt2;					//차변2 : 2차상계금액
				}
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", accPlc);
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mMtchAmt.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						accPlc,
						mtchAmt.toString(),
						(String)mMtchAmt.get("UKEY_SUB_CD"))
				);
			}
			
			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		}

	}

}
