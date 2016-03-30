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
 * <li>설 명 : 수납대행 전송 </li>
 * <li>작성일 : 2009-04-28</li>
 * </ul>
 *
 * @author 하창주 (hachangjoo)
 */
public class SACERP08120 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08120";

	private SAP sap = null;

	private List lstJourStdInfo	= null;		//분개기준정보
	private List lstAmtInfo		= null;		//금액정보
	private List lstAgency		= null;		//대리점목록
	
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

	/**
	 * 입금회수 배치 프로그램을 수행한다.
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
			String dpstYM = "";
			String dpstMonth = "";		//월
			if( request.size() > 1 ) {
				dpstYM	= (String)request.get("args1");
				pAgency	= (String)request.get("args2");
				pDealCo	= (String)request.get("args3");
				pZgubun	= (String)request.get("args4");
			
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
			budat = ErpCommon.getDayInterval(zbudat, 1); // 전기일자

			/**
			 *   회계팀 확정
			 *   2010년 5월 마감부터 적용
			 *   3,6,9,12월은 전기일 증빙일이 동일한 일자
			 */
			if(dpstYM.compareTo("201005") < 0 || "03".equals(dpstMonth) || "06".equals(dpstMonth) || "09".equals(dpstMonth)|| "12".equals(dpstMonth)){
				budat = zbudat; // 전기일자
			}


			/**
			 *   회계팀 확정
			 *   2010년 12월 마감부터 적용
			 *  D02 : ZGUBUN_DPST_OCCR_CHAG   : (월)요금발생
			 *  전기일 귀속일 해당월 25일
			 */
			if(ErpCommon.ZGUBUN_DPST_OCCR_CHAG.equals(pZgubun) && dpstYM.compareTo("201011") > 0){
				budat  = dpstYM + "25";
				zbudat = dpstYM + "25";
			}

			logMng.writeLogFile("[귀속일] [: " + budat);
			logMng.writeLogFile("[전기일] [: " + zbudat);
			
			/**
			 *   임시 로직
			 *   지정월 지정대리점의 지정 전송구분에 대한 처리
			 */
//			if(dpstYM.compareTo("200911") == 0){
//				if ( "37001".equals(pAgency)&&(pZgubun.equals(ErpCommon.ZGUBUN_DPST_OCCR_CHAG_A))){
//					budat = "20100601";
//				}
//			}
			
			log.debug("dpstYM(입금년월) : " + dpstYM);
			log.debug("zbudat(귀속일자) : " + zbudat);
			log.debug("budat(전기일자) : " + budat);

			requestMap.put("YYMM",	dpstYM);
			requestMap.put("AGENCY", pAgency);
			requestMap.put("DPST_PLC", pDealCo);
			requestMap.put("TRMS_ITEM", pZgubun);

			/**
			 *   D02 : ZGUBUN_DPST_OCCR_CHAG    : (월)요금발생
			 *   D03 : ZGUBUN_DPST_OCCR_PRP     : (월)예수금발생
			 *   D04 : ZGUBUN_DPST_OCCR_ANO_PAY : (월)요금,예수금대납
			 *   D08 : ZGUBUN_DPST_OCCR_CHAG_A  : (월)요금발생(상계)
			 */
			//④ DB 처리
			if ( "".equals(pZgubun) ) {
				logMng.writeLogFile("(월)요금발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_OCCR_CHAG);
				sendDpstOccrChag(sqlMap);
				
				logMng.writeLogFile("(월)예수금발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_OCCR_PRP);
				sendDpstOccrPrp(sqlMap);
				
				logMng.writeLogFile("(월)요금,예수금대납 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_OCCR_ANO_PAY);
				sendDpstOccrAnoPay(sqlMap);
				
				logMng.writeLogFile("(월)요금발생(상계) send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_OCCR_CHAG_A);
				sendDpstOccrChagA(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DPST_OCCR_CHAG) ) {
				logMng.writeLogFile("(월)요금발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_OCCR_CHAG);
				sendDpstOccrChag(sqlMap);
				
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DPST_OCCR_PRP) ) {
				logMng.writeLogFile("(월)예수금발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_OCCR_PRP);
				sendDpstOccrPrp(sqlMap);
				
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DPST_OCCR_ANO_PAY) ) {
				logMng.writeLogFile("(월)요금,예수금대납 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_OCCR_ANO_PAY);
				sendDpstOccrAnoPay(sqlMap);

			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DPST_OCCR_CHAG_A) ) {
				logMng.writeLogFile("(월)요금발생(상계) send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_OCCR_CHAG_A);
				sendDpstOccrChagA(sqlMap);
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
	 * (월)요금발생 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	@SuppressWarnings("unchecked")
	private void sendDpstOccrChag(SqlMapClient sqlMap) throws Exception {
		
		Map mChagOcct = null;			//현금입금정보 row map
		BigDecimal chagOcctAmt = null;	//SKT요금발생액
		String dpstPlc = "";			//입금처
//		String payDef  = "";            //지급보류키
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAgency = (List)sqlMap.queryForList("SACERP08120.getAgencyList", requestMap);
		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)요금발생 ERP전송 건이 존재하지 않습니다(대리점)1.";
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


			/**
			 *   2010-12 이전 
			 *    getChagOcctAmtList2
			 *   2010-12 이후
			 *    getChagOcctAmtList
			 */
 		if(dpstYm.compareTo("201012") < 0){
				lstAmtInfo = (List)sqlMap.queryForList("SACERP08120.getChagOcctAmtList2", requestMap);
			}else{
				lstAmtInfo = (List)sqlMap.queryForList("SACERP08120.getChagOcctAmtList", requestMap);
			}
			
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)요금발생 ERP전송 건이 존재하지 않습니다(요금발생)2.";
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
			
			//현금입금적용정보 대리점별 입금처 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();
			
			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mChagOcct = (Map)lstAmtInfo.get(i);
				dpstPlc = (String)mChagOcct.get("DPST_PLC");
				/**
				 *   2010-12 이전 
				 *    CHAG_OCCT_AMT_A
				 *   2010-12 이후
				 *    CHAG_OCCT_AMT
				 */
				if(dpstYm.compareTo("201012") < 0){
	 				   chagOcctAmt = (BigDecimal)mChagOcct.get("CHAG_OCCT_AMT_A");
				}else{
	 				   chagOcctAmt = (BigDecimal)mChagOcct.get("CHAG_OCCT_AMT");
				}
				
				if(chagOcctAmt.intValue() == 0) continue;

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						chagOcctAmt.toString()
						);
				mHead.put("ACC_PLC", dpstPlc);	//정산처 : 입금처
				alHead.add(mHead);

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
						mChagOcct.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map) lstJourStdInfo.get(j);
			
					alItem.add(ErpCommon.makeItem(
							mHead,
							String.valueOf(j+1),
							mJourStd,
							dpstPlc,
							chagOcctAmt.toString(),
							(String)mChagOcct.get("UKEY_SUB_CD"))
							);
				}
			}
			
			if (alItem.size() == 0) continue;
			
			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			mKey = (Map<String, Object>)alKey.get(0);
			mChagOcct.put("ZIFDATE", mKey.get("ZIFDATE"));
			mChagOcct.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08120.updateCashDpstInfo", mChagOcct);
			
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}


	/**
	 * (월)예수금발생 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDpstOccrPrp(SqlMapClient sqlMap) throws Exception {

		Map mChagOcct = null;			//현금입금정보 row map
		BigDecimal prprcOccrAmt = null;	//SKT예수금발생액
		String dpstPlc = "";			//입금처
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAgency = (List)sqlMap.queryForList("SACERP08120.getAgencyList", requestMap);
		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)예수금발생 ERP전송 건이 존재하지 않습니다.";
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
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08120.getPrprcOccrAmtList", requestMap);

			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)예수금발생 ERP전송 건이 존재하지 않습니다.";
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
			
			//현금입금적용정보조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();
			
			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mChagOcct = (Map)lstAmtInfo.get(i);
				dpstPlc = (String)mChagOcct.get("DPST_PLC");
				prprcOccrAmt = (BigDecimal)mChagOcct.get("PRPRC_OCCR_AMT");

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						prprcOccrAmt.toString()
						);
				mHead.put("ACC_PLC", dpstPlc);	//정산처 : 입금처
				alHead.add(mHead);

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
						mChagOcct.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					alItem.add(ErpCommon.makeItem(
							mHead,
							String.valueOf(j+1),
							mJourStd,
							dpstPlc,
							prprcOccrAmt.toString(),
							(String)mChagOcct.get("UKEY_SUB_CD"))
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
			mKey = (Map<String, Object>)alKey.get(0);
			mChagOcct.put("ZIFDATE", mKey.get("ZIFDATE"));
			mChagOcct.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08120.updateCashDpstInfo", mChagOcct);

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}


	/**
	 * (월)요금,예수금대납 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDpstOccrAnoPay(SqlMapClient sqlMap) throws Exception {
		
		Map mChagOcct = null;			//현금입금정보 row map
		BigDecimal anoPayAmt = null;	//대납액
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		requestMap.put("AGENCY", pAgency);
		requestMap.put("DPST_PLC", pDealCo);
		lstAmtInfo = (List)sqlMap.queryForList("SACERP08120.getPrxpayAmtList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(월)요금,예수금대납 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);

			return;
		}

		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {
			mChagOcct = (Map)lstAmtInfo.get(idx);
			agency	= (String)mChagOcct.get("AGENCY");
			anoPayAmt = (BigDecimal)mChagOcct.get("PRXPAY_AMT");

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			//set key info
			userCd = ErpCommon.nullToSpace((String)mChagOcct.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			oKey = ErpCommon.makeKey((String)mChagOcct.get("UKEY_AGENCY_CD"),
					zbudat,
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
							"1"
			);
			
			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", budat);
			oKey[0] = mKey;
			
			//당월발생대납금 대리점별 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					"1",
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					anoPayAmt.toString()
			);
			mHead.put("ACC_PLC", agency);	//정산처 : 대리점
			alHead.add(mHead);

			/**
			 *    현대백화점 서브점코드  조회
			 *    ZSAC_C_00110 : 현대백화점 서브점코드
			 *    ukey_sub_cd : 서브점코드
			 */
			ukeySubCdMap     = new HashMap();
			ukeySubCdMap.put("deal_co_cd", mChagOcct.get("DPST_PLC"));
			ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
			if(ukeySubCdMap != null){
				ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
				if(!"0000".equals(ukeySubCd)){
					mChagOcct.put("UKEY_SUB_CD", ukeySubCd);
				}
			}

			//set item info
			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				alItem.add(ErpCommon.makeItem(
						mHead,
						String.valueOf(j+1),
						mJourStd,
						(String)mChagOcct.get("DPST_PLC"),
						anoPayAmt.toString(),
						(String)mChagOcct.get("UKEY_SUB_CD"))
				);
			}

			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());

			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			mKey = (Map<String, Object>)alKey.get(0);
			mChagOcct.put("ZIFDATE", mKey.get("ZIFDATE"));
			mChagOcct.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08120.updateCashDpstInfo", mChagOcct);

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}

	}

	/**
	 * (월)요금발생(상계) ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	@SuppressWarnings("unchecked")
	private void sendDpstOccrChagA(SqlMapClient sqlMap) throws Exception {

		Map mChagOcct = null;			//현금입금정보 row map
		BigDecimal chagOcctAmt = null;	//SKT요금발생액
		String dpstPlc = "";			//입금처
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAgency = (List)sqlMap.queryForList("SACERP08120.getAgencyList", requestMap);

		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)요금발생(상계) ERP전송 건이 존재하지 않습니다(대리점)1.";
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

			/**
			 *   2010-12 이전 
			 *    getChagOcctAmtList
			 *   2010-12 이후
			 *    getChagOcctAmtList2
			 */
			if(dpstYm.compareTo("201012") < 0){
				lstAmtInfo = (List)sqlMap.queryForList("SACERP08120.getChagOcctAmtList", requestMap);
			}else{
				lstAmtInfo = (List)sqlMap.queryForList("SACERP08120.getChagOcctAmtList2", requestMap);
			}

			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)요금발생(상계) ERP전송 건이 존재하지 않습니다.";
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
			
			//현금입금적용정보 대리점별 입금처 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();
			
			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mChagOcct = (Map)lstAmtInfo.get(i);
				dpstPlc = (String)mChagOcct.get("DPST_PLC");

				/**
				 *   2010-12 이전 
				 *    CHAG_OCCT_AMT
				 *   2010-12 이후
				 *    CHAG_OCCT_AMT_A
				 */
				if(dpstYm.compareTo("201012") < 0){
	 				   chagOcctAmt = (BigDecimal)mChagOcct.get("CHAG_OCCT_AMT");
				}else{
	 				   chagOcctAmt = (BigDecimal)mChagOcct.get("CHAG_OCCT_AMT_A");
				}
				
				if(chagOcctAmt.intValue() == 0) continue;

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						chagOcctAmt.toString()
						);
				mHead.put("ACC_PLC", dpstPlc);	//정산처 : 입금처
				alHead.add(mHead);

				//set item info
				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = new HashMap();
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
							mChagOcct.put("UKEY_SUB_CD", ukeySubCd);
						}
					}

					alItem.add(ErpCommon.makeItem(
							mHead,
							String.valueOf(j+1),
							mJourStd,
							dpstPlc,
							chagOcctAmt.toString(),
							(String)mChagOcct.get("UKEY_SUB_CD"))
							);
				}
			}
			
			if (alItem.size() == 0) continue;
			
			imTables.put("KEY",		oKey);
			imTables.put("HEAD",	alHead.toArray());
			imTables.put("ITEM",	alItem.toArray());
			
			retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			//⑤ 결과 데이터 처리
			ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
			mKey = (Map<String, Object>)alKey.get(0);
			mChagOcct.put("ZIFDATE", mKey.get("ZIFDATE"));
			mChagOcct.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08120.updateCashDpstInfo", mChagOcct);
			
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}	
}
