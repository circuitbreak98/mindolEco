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
public class SACERP08160 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08160";
	
	private SAP sap = null;

	private List lstJourStdInfo	= null;		//분개기준정보
	private List lstAmtInfo		= null;		//금액정보
	private List lstAgency		= null;		//대리점목록
	private List lstCardAgency	= null;		//카드대리점목록
	
	private String zbudat		= "";		//귀속일자
	private String budat 		= "";		//전표기장일
	private String userCd		= "";		//대리점 정산담당자 사번
	private String agency		= "";		//대리점 거래처코드
	private String pAgency		= "";		//대리점 거래처코드(shell parameter)
	private String pDealCo		= "";		//거래처코드(shell parameter)
	private String pZgubun		= "";		//전송구분(shell parameter)
	private String remarkMsg	= "";		//비고 message

	private Map<String, Object> imParams = null;
	private Map<String, Object> imTables = null;
	private ArrayList alHead = null;
	private ArrayList alItem = null;

	private Object[] oKey = null;	//Erp Key table row map array
	private Map mHead = null;		//Erp Head table row map
	private Map mJourStd = null;	//분개기준 row map
	private Map mAgency = null;		//대리점 map

	private Map<String, Object> retMap = null;	//전송결과 map
	private Map<String, Object> mKey = null;

	private Map<String, Object> requestMap = new HashMap<String, Object>();
	
	//과납 역전기 전송에 필요함
	private Map<String, Object> imTablesInv = null;
	private ArrayList alHeadInv = null;
	private ArrayList alItemInv = null;
	private Object[] oKeyInv = null;	//Erp Key table row map array
	private Map mHeadInv = null;		//Erp Head table row map
	private Map mItemInv = null;		//Erp Item table row map
	

	/**
	 * 입금적용 배치 프로그램을 수행한다.
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
				log.debug(request.toString());
			}
			
			//② Transaction 시작
			sqlMap.startTransaction();
			
			//③ 요청 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");
			String dpstYM    = "";		//입금년월
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
			} else if (dpstYM.length() != 6) {
				logMng.writeLogFile("args1[입금년월] : " + dpstYM + " 입력이 올바르지 않습니다.(ex. 200901)");
				log.debug("args1[입금년월] : " + dpstYM + " 입력이 올바르지 않습니다.(ex. 200901)");
				return 1;
			}
			
			String lastDay = ErpCommon.getLastDay(Integer.parseInt(dpstYM.substring(0, 4)),
					Integer.parseInt(dpstYM.substring(4)) );
			zbudat = dpstYM + lastDay;	//귀속일자
			
			/**
			 *  F91 : ZGUBUN_DPST_EXCESS_CHAG   : (월)요금과납
			 *  F92 : ZGUBUN_DPST_EXCESS_PRP    : (월)예수금,현금매출과납
			 *  F93 : ZGUBUN_DPST_EXCESS_CARD   : (월)카드과납
			 *  과납인 경우 전기일 매월 2일
			 */
//			if(ErpCommon.ZGUBUN_DPST_EXCESS_CHAG.equals(pZgubun) || ErpCommon.ZGUBUN_DPST_EXCESS_PRP.equals(pZgubun) || ErpCommon.ZGUBUN_DPST_EXCESS_CARD.equals(pZgubun)){
//				budat = ErpCommon.getDayInterval(zbudat, 2); // 전기일자
//			}else{
				budat = ErpCommon.getDayInterval(zbudat, 1); // 전기일자
//			}

			/**
			 *   회계팀 확정
			 *   3,6,9,12월은 전기일 증빙일이 동일한 일자
			 */
			if(dpstYM.compareTo("201005") < 0 || "03".equals(dpstMonth) || "06".equals(dpstMonth) || "09".equals(dpstMonth)|| "12".equals(dpstMonth)){
				budat = zbudat; // 전기일자
			}

			/**
			 *   회계팀 확정
			 *   2010년 12월 마감부터 적용
			 *  F12 : ZGUBUN_DPST_ACC_CHAG   : (월)요금입금
			 *  전기일 귀속일 해당월 25일
			 */
			if(ErpCommon.ZGUBUN_DPST_ACC_CHAG.equals(pZgubun)  && dpstYM.compareTo("201011") > 0 ){
				budat  = dpstYM + "25";
				zbudat = dpstYM + "25";
			} 
			
			/**
			 *   임시 로직
			 *   지정월 지정대리점의 지정 전송구분에 대한 처리
			 */
//			if(dpstYM.compareTo("200910") == 0){
//				if ( "37001".equals(pAgency)&&(pZgubun.equals(ErpCommon.ZGUBUN_DPST_ACC_PRP) || pZgubun.equals(ErpCommon.ZGUBUN_DPST_ACC_CASH) || pZgubun.equals(ErpCommon.ZGUBUN_DPST_ACC_CARD))){
//					budat = "20100601";
//				}
//			}
			
			pDealCo = "";
			log.debug("dpstYM(입금년월) : " + dpstYM);
			log.debug("zbudat(귀속일자) : " + zbudat);
			log.debug("pAgency(대리점) : " + pAgency);
			log.debug("pDealCo(거래처) : " + pDealCo);
			log.debug("pZgubun(전송구분) : " + pZgubun);
			log.debug("budat(전기일자) : " + budat);

			requestMap.put("YYMM",	dpstYM);
			requestMap.put("AGENCY", pAgency);
			requestMap.put("DPST_PLC", pDealCo);
			requestMap.put("TRMS_ITEM", pZgubun);

			lstAgency = (List)sqlMap.queryForList("SACERP08160.getAgencyList", requestMap);
			lstCardAgency = (List)sqlMap.queryForList("SACERP08160.getCardAgencyList", requestMap);

			//④ DB 처리
			/** 
			 *  F12 : ZGUBUN_DPST_ACC_CHAG      : (월)요금입금
			 *  F13 : ZGUBUN_DPST_ACC_PRP       : (월)예수금입금
			 *  F14 : ZGUBUN_DPST_ACC_CASH      : (월)현금매출입금
			 *  F15 : ZGUBUN_DPST_ACC_CARD      : (월)카드입금
			 *  F91 : ZGUBUN_DPST_EXCESS_CHAG   : (월)요금과납
			 *  F92 : ZGUBUN_DPST_EXCESS_PRP    : (월)예수금,현금매출과납
			 *  F93 : ZGUBUN_DPST_EXCESS_CARD   : (월)카드과납
			 */
			if ( "".equals(pZgubun) ) {
				logMng.writeLogFile("(월)요금입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_ACC_CHAG);
				sendDpstAccChag(sqlMap);

				logMng.writeLogFile("(월)예수금입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_ACC_PRP);
				sendDpstAccPrp(sqlMap);
				
				logMng.writeLogFile("(월)현금매출입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_ACC_CASH);
				sendDpstAccCash(sqlMap);
				
				logMng.writeLogFile("(월)카드입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_ACC_CARD);
				sendDpstAccCard(sqlMap);

				logMng.writeLogFile("(월)요금과납 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_EXCESS_CHAG);
				sendDpstExcessChag(sqlMap);
				
				logMng.writeLogFile("(월)예수금,현금매출과납 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_EXCESS_PRP);
				sendDpstExcessPrp(sqlMap);
				
				logMng.writeLogFile("(월)카드과납 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_EXCESS_CARD);
				sendDpstExcessCard(sqlMap);
				
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DPST_ACC_CHAG) ) {
				logMng.writeLogFile("(월)요금입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_ACC_CHAG);
				sendDpstAccChag(sqlMap);

			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DPST_ACC_PRP) ) {
				logMng.writeLogFile("(월)예수금입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_ACC_PRP);
				sendDpstAccPrp(sqlMap);
				
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DPST_ACC_CASH) ) {
				logMng.writeLogFile("(월)현금매출입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_ACC_CASH);
				sendDpstAccCash(sqlMap);
				
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DPST_ACC_CARD) ) {
				logMng.writeLogFile("(월)카드입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_ACC_CARD);
				sendDpstAccCard(sqlMap);

			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DPST_EXCESS_CHAG) ) {
				logMng.writeLogFile("(월)요금과납 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_EXCESS_CHAG);
				sendDpstExcessChag(sqlMap);
				
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DPST_EXCESS_PRP) ) {
				logMng.writeLogFile("(월)예수금,현금매출과납 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_EXCESS_PRP);
				sendDpstExcessPrp(sqlMap);
				
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DPST_EXCESS_CARD) ) {
				logMng.writeLogFile("(월)카드과납 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_EXCESS_CARD);
				sendDpstExcessCard(sqlMap);
				
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
	 * (월)요금입금 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDpstAccChag(SqlMapClient sqlMap) throws Exception {
		
		Map mChagDpst = null;			//현금입금정보 row map
		BigDecimal accAmt	= null;		//요금입금정산액
		String dpstPlc = "";			//입금처
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)요금입금 ERP전송 건이 존재하지 않습니다.1";
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
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08160.getChagDpstAccAmtList", requestMap);

			
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)요금입금 ERP전송 건이 존재하지 않습니다.2";
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
				mChagDpst = (Map)lstAmtInfo.get(i);
				dpstPlc = (String)mChagDpst.get("DPST_PLC");
				accAmt = (BigDecimal)mChagDpst.get("CHAG_DPST_ACC_AMT");

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						accAmt.toString()
						);
				mHead.put("ACC_PLC", dpstPlc);	//정산처 : 입금처
				alHead.add(mHead);
				
				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					/**
					 *    현대백화점 서브점코드  조회
					 *    ZSAC_C_00110 : 현대백화점 서브점코드
					 *    ukey_sub_cd : 서브점코드
					 */
					ukeySubCdMap     = new HashMap();
					ukeySubCdMap.put("deal_co_cd", dpstPlc);
					ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
					if(ukeySubCdMap != null){
						ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
						if(!"0000".equals(ukeySubCd)){
							mChagDpst.put("UKEY_SUB_CD", ukeySubCd);
						}
					}
					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							dpstPlc,
							accAmt.toString(),
							(String)mChagDpst.get("UKEY_SUB_CD"))
							);
				}
			}
			
			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			if (log.isDebugEnabled()) {
				log.debug("retMap : " + retMap.toString());
			}
			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			mKey = (Map<String, Object>)alKey.get(0);
			mChagDpst.put("ZIFDATE", mKey.get("ZIFDATE"));
			mChagDpst.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08160.updateCashDpstInfo", mChagDpst);

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
		}
	}

	/**
	 * (월)예수금입금 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDpstAccPrp(SqlMapClient sqlMap) throws Exception {
		
		Map mChagDpst = null;			//현금입금정보 row map
		BigDecimal accAmt	= null;		//예수금입금정산액
		String dpstPlc = "";			//입금처
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)예수금입금 ERP전송 건이 존재하지 않습니다.1";
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
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08160.getPrprcDpstAccAmtList", requestMap);

			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)예수금입금 ERP전송 건이 존재하지 않습니다.2";
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
				mChagDpst = (Map)lstAmtInfo.get(i);
				dpstPlc = (String)mChagDpst.get("DPST_PLC");
				accAmt = (BigDecimal)mChagDpst.get("PRPRC_DPST_ACC_AMT");

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						accAmt.toString()
						);
				mHead.put("ACC_PLC", dpstPlc);	//정산처 : 입금처
				alHead.add(mHead);
				
				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					/**
					 *    현대백화점 서브점코드  조회
					 *    ZSAC_C_00110 : 현대백화점 서브점코드
					 *    ukey_sub_cd : 서브점코드
					 */
					ukeySubCdMap     = new HashMap();
					ukeySubCdMap.put("deal_co_cd", dpstPlc);
					ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
					if(ukeySubCdMap != null){
						ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
						if(!"0000".equals(ukeySubCd)){
							mChagDpst.put("UKEY_SUB_CD", ukeySubCd);
						}
					}

					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							dpstPlc,
							accAmt.toString(),
							(String)mChagDpst.get("UKEY_SUB_CD"))
							);
				}
			}
			
			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			if (log.isDebugEnabled()) {
				log.debug("retMap : " + retMap.toString());
			}
			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			mKey = (Map<String, Object>)alKey.get(0);
			mChagDpst.put("ZIFDATE", mKey.get("ZIFDATE"));
			mChagDpst.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08160.updateCashDpstInfo", mChagDpst);
			
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
		}
	}

	/**
	 * (월)현금매출입금 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDpstAccCash(SqlMapClient sqlMap) throws Exception {
		
		Map mChagDpst = null;			//현금입금정보 row map
		BigDecimal accAmt	= null;		//현금매출입금정산액
		String dpstPlc = "";			//입금처
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)현금매출입금 ERP전송 건이 존재하지 않습니다.1";
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
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08160.getCashDpstAccAmtList", requestMap);

			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)현금매출입금 ERP전송 건이 존재하지 않습니다.2";
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
				mChagDpst = (Map)lstAmtInfo.get(i);
				dpstPlc = (String)mChagDpst.get("DPST_PLC");
				accAmt = (BigDecimal)mChagDpst.get("CASH_DPST_ACC_AMT");
				
				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						accAmt.toString()
						);
				mHead.put("ACC_PLC", dpstPlc);	//정산처 : 입금처
				alHead.add(mHead);
				
				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					/**
					 *    현대백화점 서브점코드  조회
					 *    ZSAC_C_00110 : 현대백화점 서브점코드
					 *    ukey_sub_cd : 서브점코드
					 */
					ukeySubCdMap     = new HashMap();
					ukeySubCdMap.put("deal_co_cd", mChagDpst.get("DPST_PLC"));
					ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
					if(ukeySubCdMap != null){
						ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
						if(!"0000".equals(ukeySubCd)){
							mChagDpst.put("UKEY_SUB_CD", ukeySubCd);
						}
					}

					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							(String)mChagDpst.get("DPST_PLC"),
							accAmt.toString(),
							(String)mChagDpst.get("UKEY_SUB_CD"))
							);
				}
			}
			
			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			if (log.isDebugEnabled()) {
				log.debug("retMap : " + retMap.toString());
			}
			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			mKey = (Map<String, Object>)alKey.get(0);
			mChagDpst.put("ZIFDATE", mKey.get("ZIFDATE"));
			mChagDpst.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08160.updateCashDpstInfo", mChagDpst);
			
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
		}
	}

	/**
	 * (월)카드입금 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDpstAccCard(SqlMapClient sqlMap) throws Exception {
		
		Map mCardDpst = null;					//카드입금정보 row map
		BigDecimal cardDpstAccAmt	= null;		//카드입금정산액
		BigDecimal cmmsDpstAccAmt	= null;		//수수료입금정산액
		BigDecimal wrbrt	= null;				//Item 금액
		String cardCo = "";						//카드사
		String dpstYm = (String) requestMap.get("YYMM");

		if ( lstCardAgency.size() == 0 ) {
			remarkMsg = "(월)카드입금 ERP전송 건이 존재하지 않습니다.1";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}

		for (int idx = 0; idx < lstCardAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstCardAgency.get(idx);
			
			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			requestMap.put("AGENCY", agency);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08160.getCardDpstAccAmtList", requestMap);

			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)카드입금 ERP전송 건이 존재하지 않습니다.2";
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
				mCardDpst = (Map)lstAmtInfo.get(i);
				cardCo = (String)mCardDpst.get("CARD_CO_CD");
				cardDpstAccAmt	= (BigDecimal)mCardDpst.get("CARD_DPST_ACC_AMT");
				cmmsDpstAccAmt	= (BigDecimal)mCardDpst.get("CMMS_DPST_ACC_AMT");

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						cardDpstAccAmt.add(cmmsDpstAccAmt).toString()
						);
				mHead.put("ACC_PLC", cardCo);	//정산처 : 카드사
				alHead.add(mHead);

				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//SEQ_NO 별 금액지정
					if( "001".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = cardDpstAccAmt;	//차변1(카드매출금)
					} else if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = cmmsDpstAccAmt;	//차변2(카드수수료)
					} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = cardDpstAccAmt.add(cmmsDpstAccAmt);	//대변2(차변1 + 차변2)
					}

					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							cardCo,
							wrbrt.toString(),
							(String)mCardDpst.get("UKEY_SUB_CD"))
							);
				}
			}

			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			if (log.isDebugEnabled()) {
				log.debug("retMap : " + retMap.toString());
			}
			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			mKey = (Map<String, Object>)alKey.get(0);
			mCardDpst.put("ZIFDATE", mKey.get("ZIFDATE"));
			mCardDpst.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08160.updateCardDpstInfo", mCardDpst);
			
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
		}
	}
	

	/**
	 * (월)요금과납 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDpstExcessChag(SqlMapClient sqlMap) throws Exception {
		
		Map mChagDpst = null;			//현금입금정보 row map
		BigDecimal dpstBamt = null;		//정산잔액
		String dpstPlc = "";			//입금처
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)요금과납 ERP전송 건이 존재하지 않습니다.1";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}

		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();
			imTablesInv = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);
			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			requestMap.put("AGENCY", agency);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08160.getChagDpstBamtList", requestMap);
			
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)요금과납 ERP전송 건이 존재하지 않습니다.2";
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

			//귀속일자 = 전기일 : 익월 1일
			oKeyInv = ErpCommon.makeKey((String)mAgency.get("UKEY_AGENCY_CD"),
					ErpCommon.getDayInterval(zbudat, 1),
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
					String.valueOf(lstAmtInfo.size())
					); 
			
			mKey = (Map<String, Object>) oKeyInv[0];
			mKey.put("BUDAT", ErpCommon.getDayInterval(budat, 1));
			oKeyInv[0] = mKey;
			
			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();
			alHeadInv = new ArrayList();
			alItemInv = new ArrayList();
			
			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mChagDpst = (Map)lstAmtInfo.get(i);
				dpstPlc = (String)mChagDpst.get("DPST_PLC");
				dpstBamt = (BigDecimal)mChagDpst.get("CHAG_DPST_BAMT");
	
				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						dpstBamt.toString()
						);
				mHead.put("ACC_PLC", dpstPlc);	//정산처 : 입금처
				alHead.add(mHead);
				
				mHeadInv = ErpCommon.makeHead((Map<String, Object>)oKeyInv[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						dpstBamt.toString()
						);
				mHeadInv.put("ACC_PLC", dpstPlc);	//정산처 : 입금처
				alHeadInv.add(mHeadInv);
				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					/**
					 *    현대백화점 서브점코드  조회
					 *    ZSAC_C_00110 : 현대백화점 서브점코드
					 *    ukey_sub_cd : 서브점코드
					 */
					ukeySubCdMap     = new HashMap();
					ukeySubCdMap.put("deal_co_cd", dpstPlc);
					ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
					if(ukeySubCdMap != null){
						ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
						if(!"0000".equals(ukeySubCd)){
							mChagDpst.put("UKEY_SUB_CD", ukeySubCd);
						}
					}

					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							dpstPlc,
							dpstBamt.toString(),
							(String)mChagDpst.get("UKEY_SUB_CD"))
					);

					mItemInv = ErpCommon.makeItem(mHeadInv,
							String.valueOf(j+1),
							mJourStd,
							dpstPlc,
							dpstBamt.toString(),
							(String)mChagDpst.get("UKEY_SUB_CD"));
					/*
					 *    dpstBamt : 정산잔액이 "-" 금액인 경우 전기키를 넣어 준다.
					 *    2011-02-07 추가 처리.
					 */
					if(new BigDecimal("0").compareTo(dpstBamt) > 0){
						mItemInv.put("BSCHL", ErpCommon.nullToSpace((String)mJourStd.get("FIRT_KEY")));	//전기 키
					}else{
						mItemInv.put("BSCHL", ErpCommon.nullToSpace((String)mJourStd.get("INV_FIRT_KEY")));	//역전기 키
					}
					alItemInv.add(mItemInv);
				}
			}
			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			
			if (log.isDebugEnabled()) {
				log.debug("retMap : " + retMap.toString());
			}
			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			mKey = (Map<String, Object>)alKey.get(0);
			mChagDpst.put("ZIFDATE", mKey.get("ZIFDATE"));
			mChagDpst.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08160.updateCashDpstInfo", mChagDpst);

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
			
			//과납금액 역전기 전송
			imTablesInv.put("KEY",	oKeyInv);
			imTablesInv.put("HEAD",	alHeadInv.toArray());
			imTablesInv.put("ITEM",	alItemInv.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTablesInv);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			ErpCommon.insertAccTrTable(sqlMap, imTablesInv, retMap);
			
			//log.debug("retMap : " + retMap.toString());
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}

	/**
	 * (월)예수금,현금매출과납 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDpstExcessPrp(SqlMapClient sqlMap) throws Exception {
		
		Map mChagDpst = null;			//현금입금정보 row map
		BigDecimal dpstBamt = null;		//정산잔액
		String dpstPlc = "";			//입금처
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)예수금,현금매출과납 ERP전송 건이 존재하지 않습니다.1";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}

		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();
			imTablesInv = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);
			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			requestMap.put("AGENCY", agency);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08160.getEtcDpstBamtList", requestMap);
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)예수금,현금매출과납 ERP전송 건이 존재하지 않습니다.2";
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

			//귀속일자 = 전기일 : 익월 1일
			oKeyInv = ErpCommon.makeKey((String)mAgency.get("UKEY_AGENCY_CD"),
					ErpCommon.getDayInterval(zbudat, 1),
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
					String.valueOf(lstAmtInfo.size())
					); 

			
			mKey = (Map<String, Object>) oKeyInv[0];
			mKey.put("BUDAT", ErpCommon.getDayInterval(budat, 1));
			oKeyInv[0] = mKey;
			
			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();
			alHeadInv = new ArrayList();
			alItemInv = new ArrayList();
			
			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mChagDpst = (Map)lstAmtInfo.get(i);
				dpstPlc = (String)mChagDpst.get("DPST_PLC");
				dpstBamt = (BigDecimal)mChagDpst.get("ETC_DPST_BAMT");

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						dpstBamt.toString()
						);
				mHead.put("ACC_PLC", dpstPlc);	//정산처 : 입금처
				alHead.add(mHead);
				
				mHeadInv = ErpCommon.makeHead((Map<String, Object>)oKeyInv[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						dpstBamt.toString()
						);
				mHeadInv.put("ACC_PLC", dpstPlc);	//정산처 : 입금처
				alHeadInv.add(mHeadInv);
				
				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					/**
					 *    현대백화점 서브점코드  조회
					 *    ZSAC_C_00110 : 현대백화점 서브점코드
					 *    ukey_sub_cd : 서브점코드
					 */
					ukeySubCdMap     = new HashMap();
					ukeySubCdMap.put("deal_co_cd", dpstPlc);
					ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
					if(ukeySubCdMap != null){
						ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
						if(!"0000".equals(ukeySubCd)){
							mChagDpst.put("UKEY_SUB_CD", ukeySubCd);
						}
					}

					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							dpstPlc,
							dpstBamt.toString(),
							(String)mChagDpst.get("UKEY_SUB_CD"))
							);

					mItemInv = ErpCommon.makeItem(mHeadInv,
							String.valueOf(j+1),
							mJourStd,
							dpstPlc,
							dpstBamt.toString(),
							(String)mChagDpst.get("UKEY_SUB_CD"));

					/*
					 *    dpstBamt : 정산잔액이 "-" 금액인 경우 전기키를 넣어 준다.
					 *    2011-02-07 추가 처리.
					 */
					if(new BigDecimal("0").compareTo(dpstBamt) > 0){
						mItemInv.put("BSCHL", ErpCommon.nullToSpace((String)mJourStd.get("FIRT_KEY")));	//전기 키
					}else{
						mItemInv.put("BSCHL", ErpCommon.nullToSpace((String)mJourStd.get("INV_FIRT_KEY")));	//역전기 키
					}
					
					alItemInv.add(mItemInv);
				}
			}
			
			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			if (log.isDebugEnabled()) {
				log.debug("retMap : " + retMap.toString());
			}
			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			mKey = (Map<String, Object>)alKey.get(0);
			mChagDpst.put("ZIFDATE", mKey.get("ZIFDATE"));
			mChagDpst.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08160.updateCashDpstInfo", mChagDpst);
			
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
			
			//과납금액 역전기 전송
			imTablesInv.put("KEY",	oKeyInv);
			imTablesInv.put("HEAD",	alHeadInv.toArray());
			imTablesInv.put("ITEM",	alItemInv.toArray());

			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTablesInv);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			ErpCommon.insertAccTrTable(sqlMap, imTablesInv, retMap);

			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}

	/**
	 * (월)카드과납 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDpstExcessCard(SqlMapClient sqlMap) throws Exception {
		
		Map mCardDpst = null;			//카드입금정보 row map
		BigDecimal dpstBamt = null;		//입금정산잔액
		String cardCo = "";				//카드사
		String dpstYm = (String) requestMap.get("YYMM");

		if ( lstCardAgency.size() == 0 ) {
			remarkMsg = "(월)카드과납 ERP전송 건이 존재하지 않습니다.1";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}

		for (int idx = 0; idx < lstCardAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();
			imTablesInv = new HashMap<String, Object>();

			mAgency = (Map)lstCardAgency.get(idx);
			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			requestMap.put("AGENCY", agency);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08160.getCardDpstBamtList", requestMap);

			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)카드과납 ERP전송 건이 존재하지 않습니다.2";
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

			//귀속일자 = 전기일 : 익월 1일
			oKeyInv = ErpCommon.makeKey((String)mAgency.get("UKEY_AGENCY_CD"),
					ErpCommon.getDayInterval(zbudat, 1),
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
					String.valueOf(lstAmtInfo.size())
					); 

			
			mKey = (Map<String, Object>) oKeyInv[0];
			mKey.put("BUDAT", ErpCommon.getDayInterval(budat, 1));
			oKeyInv[0] = mKey;
			
			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();
			alHeadInv = new ArrayList();
			alItemInv = new ArrayList();

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCardDpst = (Map)lstAmtInfo.get(i);
				cardCo = (String)mCardDpst.get("CARD_CO_CD");
				dpstBamt = (BigDecimal)mCardDpst.get("CARD_DPST_BAMT");

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						dpstBamt.toString()
						);
				mHead.put("ACC_PLC", cardCo);	//정산처 : 카드사
				alHead.add(mHead);
				
				mHeadInv = ErpCommon.makeHead((Map<String, Object>)oKeyInv[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						dpstBamt.toString()
						);
				mHeadInv.put("ACC_PLC", cardCo);	//정산처 : 카드사
				alHeadInv.add(mHeadInv);

				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);

					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							cardCo,
							dpstBamt.toString(),
							(String)mCardDpst.get("UKEY_SUB_CD"))
							);

					mItemInv = ErpCommon.makeItem(mHeadInv,
							String.valueOf(j+1),
							mJourStd,
							cardCo,
							dpstBamt.toString(),
							(String)mCardDpst.get("UKEY_SUB_CD"));

					/*
					 *    dpstBamt : 정산잔액이 "-" 금액인 경우 전기키를 넣어 준다.
					 *    2011-02-07 추가 처리.
					 */
					if(new BigDecimal("0").compareTo(dpstBamt) > 0){
						mItemInv.put("BSCHL", ErpCommon.nullToSpace((String)mJourStd.get("FIRT_KEY")));	//전기 키
					}else{
						mItemInv.put("BSCHL", ErpCommon.nullToSpace((String)mJourStd.get("INV_FIRT_KEY")));	//역전기 키
					}
					
					alItemInv.add(mItemInv);
				}
			}

			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			if (log.isDebugEnabled()) {
				log.debug("retMap : " + retMap.toString());
			}
			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			mKey = (Map<String, Object>)alKey.get(0);
			mCardDpst.put("ZIFDATE", mKey.get("ZIFDATE"));
			mCardDpst.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08160.updateCardDpstInfo", mCardDpst);
			
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
			
			//과납금액 역전기 전송
			imTablesInv.put("KEY",	oKeyInv);
			imTablesInv.put("HEAD",	alHeadInv.toArray());
			imTablesInv.put("ITEM",	alItemInv.toArray());

			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTablesInv);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			ErpCommon.insertAccTrTable(sqlMap, imTablesInv, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}

}
