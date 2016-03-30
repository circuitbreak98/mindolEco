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
 * <li>설 명 : (일)입금 ERP전송 </li>
 * <li>작성일 : 2009-04-28</li>
 * </ul>
 *
 * @author 하창주 (hachangjoo)
 */
public class SACERP08140 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08140";
	
	private SAP sap = null;

	private List lstJourStdInfo	= null;		//분개기준정보
	private List lstAmtInfo		= null;		//금액정보
	private List lstObsAmtInfo	= null;		//불명금액정보

	private String dpstDt		= "";		//입금일자 == 귀속일자
	private String userCd		= "";		//대리점 정산담당자 사번
	private String agency		= "";		//대리점 거래처코드
	private String pAgency		= "";		//대리점 거래처코드(shell parameter)
	private String pZgubun		= "";		//전송구분(shell parameter)
	private String remarkMsg	= "";		//비고 message

	private Map<String, Object> imParams = null;
	private Map<String, Object> imTables = null;
	private ArrayList alHead = null;
	private ArrayList alItem = null;

	private Object[] oKey = null;	//Erp Key table row map array
	private Map mHead = null;		//Erp Head table row map
	private Map mJourStd = null;	//분개기준 row map

	private Map<String, Object> retMap = null;	//전송결과 map
	private Map<String, Object> requestMap = new HashMap<String, Object>();
	private Map<String, Object> mKey = null;
	
	/**
	 * 입금처리 배치 프로그램을 수행한다.
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
			if( request.size() > 1 ) {
				dpstDt	= (String)request.get("args1");
				pAgency	= (String)request.get("args2");
				pZgubun	= (String)request.get("args3");
				userCd	= (String)request.get("args4");
				
				dpstDt = dpstDt.equals("?") ? "" : dpstDt;
				pAgency = pAgency.equals("?") ? "" : pAgency;
				pZgubun = pZgubun.equals("?") ? "" : pZgubun;

				logMng.writeLogFile("args1[입금일자] : " + dpstDt);
				logMng.writeLogFile("args2[대리점] : " + pAgency);
				logMng.writeLogFile("args3[전송구분] : " + pZgubun);
				logMng.writeLogFile("args4[요청사번] : " + userCd);
			}

			if (dpstDt.equals("")) {
				dpstDt = ErpCommon.getDayInterval(-1);	//입금일자(어제)
				logMng.writeLogFile("입금일자[dpstDt] : " + dpstDt);
			}
			
			requestMap.put("DPST_DT", dpstDt);
			requestMap.put("AGENCY", pAgency);
			requestMap.put("TRMS_ITEM", pZgubun);

			//④ DB 처리
			/** 
			 *  F01 : ZGUBUN_DPST_PROC_SKT_CHAG      : (일)요금입금
			 *  F02 : ZGUBUN_DPST_PROC_SKT_PRP       : (일)예수금,현금매출입금
			 *  F05 : ZGUBUN_DPST_PROC_CARD          : (일)입금(카드사)
			 *  F18 : ZGUBUN_DPST_PROC_OCCR          : (일) 예수금 발생
			 *  
			 *  액서서리 관련 추가 - 2011-05-08
			 *  F20 : ZGUBUN_ACCESSARY_CASH          : (일)현금매출발생 
			 *  F21 : ZGUBUN_ACCESSARY_CARD          : (일)카드매출발생
			 *  F22 : ZGUBUN_ACCESSARY_COST          : (일)매출원가
			 */ 
			if ( "".equals(pZgubun) ) {
				logMng.writeLogFile("(일)요금입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_PROC_SKT_CHAG);
				sendDpstProcSktChag(sqlMap);
				logMng.writeLogFile("(일)예수금입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_PROC_SKT_PRP);
				sendDpstProcSktPrp(sqlMap);
				//(일)현금매출입금
				//lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_PROC_CASA_AMT);
				//sendDpstProcCasaAmt(sqlMap);
				logMng.writeLogFile("(일)입금(카드사) send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_PROC_CARD);
				sendDpstProcCard(sqlMap);
			} else if ( ErpCommon.ZGUBUN_DPST_PROC_SKT_CHAG.equals(pZgubun) ) {
				logMng.writeLogFile("(일)요금입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_PROC_SKT_CHAG);
				sendDpstProcSktChag(sqlMap);

			} else if ( ErpCommon.ZGUBUN_DPST_PROC_SKT_PRP.equals(pZgubun) ) {
				logMng.writeLogFile("(일)예수금입금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_PROC_SKT_PRP);
				sendDpstProcSktPrp(sqlMap);

			} else if ( ErpCommon.ZGUBUN_DPST_PROC_CARD.equals(pZgubun) ) {
				logMng.writeLogFile("(일)입금(카드사) send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_PROC_CARD);
				sendDpstProcCard(sqlMap);

			} else if ( ErpCommon.ZGUBUN_DPST_PROC_OCCR.equals(pZgubun) ) {
				logMng.writeLogFile("(일)예수금발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DPST_PROC_OCCR);
				sendDpstProcOccr(sqlMap);
			} else if ( ErpCommon.ZGUBUN_ACCESSARY_CASH.equals(pZgubun) ) {
				logMng.writeLogFile("(일)현금매출발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_ACCESSARY_CASH);
				sendAccessaryCash(sqlMap);
			} else if ( ErpCommon.ZGUBUN_ACCESSARY_CARD.equals(pZgubun) ) {
				logMng.writeLogFile("(일)카드매출발생 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_ACCESSARY_CARD);
				sendAccessaryCard(sqlMap);
			} else if ( ErpCommon.ZGUBUN_ACCESSARY_COST.equals(pZgubun) ) {
				logMng.writeLogFile("(일)매출원가 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_ACCESSARY_COST);
				sendAccessaryCost(sqlMap);
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
	 * 대리점별 불명금
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private BigDecimal getObsAmt(String agency) {
		
		Map mObsAmt = null;
		String obsAgency = "";
		BigDecimal obsAmt = new BigDecimal("0");	//불명금액
		
		for (int i = 0; i < lstObsAmtInfo.size(); i++) {
			mObsAmt = (Map)lstObsAmtInfo.get(i);
			obsAgency = (String)mObsAmt.get("AGENCY");
			
			if (obsAgency.equals(agency)) {
				obsAmt = (BigDecimal)mObsAmt.get("DPST_AMT");
				break;
			}
		}
		
		return obsAmt;
	}
	
	/**
	 * (일)요금입금 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDpstProcSktChag(SqlMapClient sqlMap) throws Exception {
		
		//입금처리 row map
		Map mDpstProc = null;
		BigDecimal dpstAmt = null;	//입금처리금액
		BigDecimal obsAmt = null;	//불명금액
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAmtInfo = (List)sqlMap.queryForList(PROG_ID + ".getDpstSktChagList", requestMap);
		lstObsAmtInfo = (List)sqlMap.queryForList(PROG_ID + ".getObsAmtList", requestMap);
		
		log.debug("lstAmtInfo.size() : " + lstAmtInfo.size());
		log.debug("lstObsAmtInfo.size() : " + lstObsAmtInfo.size());
		
		if ( lstAmtInfo.size() == 0 && lstObsAmtInfo.size() == 0 ) {
			remarkMsg = "(일)요금입금 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}
		
		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			mDpstProc = (Map)lstAmtInfo.get(idx);
			agency	= (String)mDpstProc.get("AGENCY");
			
			dpstAmt = (BigDecimal)mDpstProc.get("DPST_AMT");
			log.debug("DPST_AMT : " + dpstAmt.toString());
			obsAmt = getObsAmt(agency);
			log.debug("obsAmt : " + obsAmt.toString());
			dpstAmt = dpstAmt.add(obsAmt);
			log.debug("DPST_AMT + obsAmt : " + dpstAmt.toString());

			if ( dpstAmt.compareTo(new BigDecimal("0")) == 0 ) {
				logMng.writeLogFile("대리점[" + agency + "] : (일)요금입금[DPST_AMT] : " + dpstAmt.toString());
				logMng.writeLogFile("금액이 0 이므로 전송처리 하지 않음.");
				continue;
			}
			
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			//set key info
			userCd = ErpCommon.nullToSpace((String)mDpstProc.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			oKey = ErpCommon.makeKey((String)mDpstProc.get("UKEY_AGENCY_CD"),
					(String)mDpstProc.get("DPST_DT"),
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
							"1"
			);

			/**
			 *   일 전표 전송은 전기일 증빙일이 동일하다.
			 */
			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", (String)mDpstProc.get("DPST_DT"));
			oKey[0] = mKey;			//입금처리조회 건수 만큼  Item map 추가

			alHead = new ArrayList();
			alItem = new ArrayList();

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					"1",
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					dpstAmt.toString()
			);
			mHead.put("ACC_PLC", agency);	//정산처 : 대리점
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", (String)mDpstProc.get("DPST_PLC"));
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mDpstProc.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						(String)mDpstProc.get("DPST_PLC"),
						dpstAmt.toString(),
						(String)mDpstProc.get("UKEY_SUB_CD"))
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
	 * SKT예수금 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDpstProcSktPrp(SqlMapClient sqlMap) throws Exception {
		
		//입금처리 row map
		Map mDpstProc = null;
		BigDecimal dpstAmt = null;	//입금처리금액
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAmtInfo = (List)sqlMap.queryForList(PROG_ID + ".getDpstSktPrpList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(일)예수금입금 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}
		
		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			mDpstProc = (Map)lstAmtInfo.get(idx);
			agency	= (String)mDpstProc.get("AGENCY");
			
			dpstAmt = (BigDecimal)mDpstProc.get("DPST_AMT");
			log.debug("SKT_PRP : " + dpstAmt.toString());
			if ( dpstAmt.compareTo(new BigDecimal("0")) == 0 ) {
				logMng.writeLogFile("대리점[" + agency + "] : SKT예수금[dpstAmt] : " + dpstAmt.toString());
				logMng.writeLogFile("금액이 0 이므로 전송처리 하지 않음.");
				continue;
			}

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			//set key info
			userCd = ErpCommon.nullToSpace((String)mDpstProc.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			oKey = ErpCommon.makeKey((String)mDpstProc.get("UKEY_AGENCY_CD"),
					(String)mDpstProc.get("DPST_DT"),
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
							"1"
			); 

			/**
			 *   일 전표 전송은 전기일 증빙일이 동일하다.
			 */
			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", (String)mDpstProc.get("DPST_DT"));
			oKey[0] = mKey;	
			
			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					"1",
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					dpstAmt.toString()
			);
			mHead.put("ACC_PLC", agency);	//정산처 : 대리점
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", mDpstProc.get("DPST_PLC"));
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mDpstProc.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						(String)mDpstProc.get("DPST_PLC"),
						dpstAmt.toString(),
						(String)mDpstProc.get("UKEY_SUB_CD"))
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
	 * 카드사 총입금액 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDpstProcCard(SqlMapClient sqlMap) throws Exception {
		
		//카드입금처리 row map
		Map mCardDpstProc 		= null;
		BigDecimal dpstAmt		= null;	//입금금액
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAmtInfo = (List)sqlMap.queryForList(PROG_ID + ".getDpstCardList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(일)카드사입금 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}

		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			mCardDpstProc = (Map)lstAmtInfo.get(idx);
			agency	= (String)mCardDpstProc.get("AGENCY");

			dpstAmt	= (BigDecimal)mCardDpstProc.get("DPST_AMT");
			if ( dpstAmt.compareTo(new BigDecimal("0")) == 0 ) {
				logMng.writeLogFile("대리점[" + agency + "] : 카드사입금금액[DPST_AMT] : " + dpstAmt.toString());
				logMng.writeLogFile("금액이 0 이므로 전송처리 하지 않음.");
				continue;
			}

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			//set key info
			userCd = ErpCommon.nullToSpace((String)mCardDpstProc.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			oKey = ErpCommon.makeKey((String)mCardDpstProc.get("UKEY_AGENCY_CD"),
					(String)mCardDpstProc.get("DPST_DT"),
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
					"1"
			); 

			/**
			 *   일 전표 전송은 전기일 증빙일이 동일하다.
			 */
			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", (String)mCardDpstProc.get("DPST_DT"));
			oKey[0] = mKey;	
			
			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					"1",
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					dpstAmt.toString()
			);
			mHead.put("ACC_PLC", mCardDpstProc.get("AGENCY"));	//정산처 : 대리점
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", mCardDpstProc.get("DPST_PLC"));
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mCardDpstProc.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						(String)mCardDpstProc.get("DPST_PLC"),
						dpstAmt.toString(),
						(String)mCardDpstProc.get("UKEY_SUB_CD"))
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
	 * (일) 예수금 발생
	 * @author	이강수
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDpstProcOccr(SqlMapClient sqlMap) throws Exception {
		
		//카드입금처리 row map
		Map mDpstProc     		= null;
		BigDecimal dpstAmt		= null;	//입금금액
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAmtInfo = (List)sqlMap.queryForList(PROG_ID + ".getDpstProdOccrList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(일)예수금 발생 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}

		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			mDpstProc = (Map)lstAmtInfo.get(idx);
			agency	= (String)mDpstProc.get("AGENCY");
			
			dpstAmt = (BigDecimal)mDpstProc.get("DPST_AMT");
			log.debug("SKT_PRP : " + dpstAmt.toString());
			if ( dpstAmt.compareTo(new BigDecimal("0")) == 0 ) {
				logMng.writeLogFile("대리점[" + agency + "] : SKT예수금[dpstAmt] : " + dpstAmt.toString());
				logMng.writeLogFile("금액이 0 이므로 전송처리 하지 않음.");
				continue;
			}

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			//set key info
			userCd = ErpCommon.nullToSpace((String)mDpstProc.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			oKey = ErpCommon.makeKey((String)mDpstProc.get("UKEY_AGENCY_CD"),
					(String)mDpstProc.get("DPST_DT"),
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
							"1"
			); 

			/**
			 *   일 전표 전송은 전기일 증빙일이 동일하다.
			 */
			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", (String)mDpstProc.get("DPST_DT"));
			oKey[0] = mKey;	
			
			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					"1",
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					dpstAmt.toString()
			);
			mHead.put("ACC_PLC", agency);	//정산처 : 대리점
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", mDpstProc.get("DPST_PLC"));
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mDpstProc.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						(String)mDpstProc.get("DPST_PLC"),
						dpstAmt.toString(),
						(String)mDpstProc.get("UKEY_SUB_CD"))
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
			sqlMap.update("SACERP08140.updateTaccDealDtlOutputIf", mKey);  /* 온라인 결제 수수료 월정산   */
			
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}

	/**
	 * (일) 현금매출발생
	 * @author	이강수(leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendAccessaryCash(SqlMapClient sqlMap) throws Exception {
		
		//현금매출발생처리 row map
		Map mAccessaryCashProc 	= null;
		BigDecimal dpstAmt		= null;	// 금액
		BigDecimal wrbrt	    = null;		//Item 금액
		Map ukeySubCdMap        = null;
		String ukeySubCd        = "";

		/**
		 *  TSAC_ACCESSARY_IF : 악세서리 IF 테이블
		 *  REC_CL : 레코드구분 
		 *    01 : 매입 
		 *    02 : 매출 
		 *    03 : 카드
		 *    04 : 매출원가 
		 */
		requestMap.put("REC_CL", "02");     // 매출
		
		lstAmtInfo = (List)sqlMap.queryForList(PROG_ID + ".getAccessaryIf", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(일)카드매출발생 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}

		//set key info
		mAccessaryCashProc = (Map)lstAmtInfo.get(0);
		userCd = ErpCommon.nullToSpace((String)mAccessaryCashProc.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
		oKey = ErpCommon.makeKey((String)mAccessaryCashProc.get("UKEY_AGENCY_CD"),
				(String)mAccessaryCashProc.get("TRAN_DT"),
				(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
				userCd.equals("") ? PROG_ID : userCd,
				String.valueOf(lstAmtInfo.size())
		); 

		/**
		 *   일 전표 전송은 전기일 증빙일이 동일하다.
		 */
		mKey = (Map<String, Object>) oKey[0];
		mKey.put("BUDAT", (String)mAccessaryCashProc.get("TRAN_DT"));
		oKey[0] = mKey;	
		
		
		//입금처리조회 건수 만큼  Item map 추가
		alHead = new ArrayList();
		alItem = new ArrayList();

		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();
		
		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			mAccessaryCashProc = (Map)lstAmtInfo.get(idx);
			agency	= (String)mAccessaryCashProc.get("POS_AGENCY");

			dpstAmt	= (BigDecimal)mAccessaryCashProc.get("AMT");
			if ( dpstAmt.compareTo(new BigDecimal("0")) == 0 ) {
				logMng.writeLogFile("대리점[" + agency + "] : 현금매출발생[AMT] : " + dpstAmt.toString());
				logMng.writeLogFile("금액이 0 이므로 전송처리 하지 않음.");
				continue;
			}

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					String.valueOf(idx+1),
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					dpstAmt.toString()
			);
			mHead.put("ACC_PLC", mAccessaryCashProc.get("DEAL_CO_CD"));	//정산처 : 거래처
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				//SEQ_NO 별 금액지정
				wrbrt = dpstAmt;	//차변 : 002 + 003
				if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = dpstAmt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);	//대변1 : 상품매출
				} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = dpstAmt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);
					wrbrt = dpstAmt.subtract(wrbrt);	//대변 2:매출부가세
				}
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", mAccessaryCashProc.get("DEAL_CO_CD"));
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) mAccessaryCashProc.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mAccessaryCashProc.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						(String)mAccessaryCashProc.get("DEAL_CO_CD"),
						wrbrt.toString(),
						(String)mAccessaryCashProc.get("UKEY_SUB_CD"))
				);
			}
		}		
		imTables.put("KEY",		oKey);
		imTables.put("HEAD",	alHead.toArray());
		imTables.put("ITEM",	alItem.toArray());

		retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);

		/*
		 *  TSAC_ACCESSARY_IF : 악세서리 IF
		 *  전송여부, 전송일자 수정
		 */
		ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
		Map mKey = (Map<String, Object>)alKey.get(0);
		mKey.put("AGENCY" , agency);
		mKey.put("REC_CL" , "02");
		
		sqlMap.update("SACERP08140.updateTsacAccessaryIf", mKey);  /* 온라인 결제 수수료 월정산   */

		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}
	
	/**
	 * (일) 카드매출발생
	 * @author	이강수(leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendAccessaryCard(SqlMapClient sqlMap) throws Exception {
		//카드매출발생처리 row map
		Map mAccessaryCardProc 	= null;
		BigDecimal dpstAmt		= null;	// 금액
		BigDecimal wrbrt	    = null;		//Item 금액
		Map ukeySubCdMap        = null;
		String ukeySubCd        = "";

		/**
		 *  TSAC_ACCESSARY_IF : 악세서리 IF 테이블
		 *  REC_CL : 레코드구분 
		 *    01 : 매입 
		 *    02 : 매출 
		 *    03 : 카드
		 *    04 : 매출원가 
		 */
		requestMap.put("REC_CL", "03");     // 카드
		
		lstAmtInfo = (List)sqlMap.queryForList(PROG_ID + ".getAccessaryIf", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(일)카드매출발생 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}

		//set key info
        mAccessaryCardProc = (Map)lstAmtInfo.get(0);
		userCd = ErpCommon.nullToSpace((String)mAccessaryCardProc.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
		oKey = ErpCommon.makeKey((String)mAccessaryCardProc.get("UKEY_AGENCY_CD"),
				(String)mAccessaryCardProc.get("TRAN_DT"),
				(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
				userCd.equals("") ? PROG_ID : userCd,
				String.valueOf(lstAmtInfo.size())
		); 

		/**
		 *   일 전표 전송은 전기일 증빙일이 동일하다.
		 */
		mKey = (Map<String, Object>) oKey[0];
		mKey.put("BUDAT", (String)mAccessaryCardProc.get("TRAN_DT"));
		oKey[0] = mKey;	
		
		
		//입금처리조회 건수 만큼  Item map 추가
		alHead = new ArrayList();
		alItem = new ArrayList();

		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();
		
		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			mAccessaryCardProc = (Map)lstAmtInfo.get(idx);
			agency	= (String)mAccessaryCardProc.get("POS_AGENCY");

			dpstAmt	= (BigDecimal)mAccessaryCardProc.get("AMT");
			if ( dpstAmt.compareTo(new BigDecimal("0")) == 0 ) {
				logMng.writeLogFile("대리점[" + agency + "] : 카드매출발생[AMT] : " + dpstAmt.toString());
				logMng.writeLogFile("금액이 0 이므로 전송처리 하지 않음.");
				continue;
			}

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					String.valueOf(idx+1),
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					dpstAmt.toString()
			);
			mHead.put("ACC_PLC", mAccessaryCardProc.get("DEAL_CO_CD"));	//정산처 : 거래처
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				//SEQ_NO 별 금액지정
				wrbrt = dpstAmt;	//차변 : 002 + 003
				if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = dpstAmt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);	//대변1 : 상품매출
				} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = dpstAmt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);
					wrbrt = dpstAmt.subtract(wrbrt);	//대변 2:매출부가세
				}
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", mAccessaryCardProc.get("DEAL_CO_CD"));
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) mAccessaryCardProc.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mAccessaryCardProc.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						(String)mAccessaryCardProc.get("DEAL_CO_CD"),
						wrbrt.toString(),
						(String)mAccessaryCardProc.get("UKEY_SUB_CD"))
				);
			}
		}		
		imTables.put("KEY",		oKey);
		imTables.put("HEAD",	alHead.toArray());
		imTables.put("ITEM",	alItem.toArray());

		retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
		
		/*
		 *  TSAC_ACCESSARY_IF : 악세서리 IF
		 *  전송여부, 전송일자 수정
		 */
		ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
		Map mKey = (Map<String, Object>)alKey.get(0);
		mKey.put("AGENCY" , agency);
		mKey.put("REC_CL" , "03");
		
		sqlMap.update("SACERP08140.updateTsacAccessaryIf", mKey);  /* 온라인 결제 수수료 월정산   */
		
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}
	
	/**
	 * (일) 매출원가
	 * @author	이강수(leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendAccessaryCost(SqlMapClient sqlMap) throws Exception {
		//매출원가처리 row map
		Map mAccessaryCostProc 	= null;
		BigDecimal dpstAmt		= null;	// 금액
		Map ukeySubCdMap        = null;
		String ukeySubCd        = "";

		/**
		 *  TSAC_ACCESSARY_IF : 악세서리 IF 테이블
		 *  REC_CL : 레코드구분 
		 *    01 : 매입 
		 *    02 : 매출 
		 *    03 : 카드
		 *    04 : 매출원가 
		 */
		requestMap.put("REC_CL", "04");     // 매출원가
		
		lstAmtInfo = (List)sqlMap.queryForList(PROG_ID + ".getAccessaryIf", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(일)매출원가 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}

		//set key info
		mAccessaryCostProc = (Map)lstAmtInfo.get(0);
		userCd = ErpCommon.nullToSpace((String)mAccessaryCostProc.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
		oKey = ErpCommon.makeKey((String)mAccessaryCostProc.get("UKEY_AGENCY_CD"),
				(String)mAccessaryCostProc.get("TRAN_DT"),
				(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
				userCd.equals("") ? PROG_ID : userCd,
				String.valueOf(lstAmtInfo.size())
		); 

		/**
		 *   일 전표 전송은 전기일 증빙일이 동일하다.
		 */
		mKey = (Map<String, Object>) oKey[0];
		mKey.put("BUDAT", (String)mAccessaryCostProc.get("TRAN_DT"));
		oKey[0] = mKey;	
		
		
		//입금처리조회 건수 만큼  Item map 추가
		alHead = new ArrayList();
		alItem = new ArrayList();

		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();
		
		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			mAccessaryCostProc = (Map)lstAmtInfo.get(idx);
			agency	= (String)mAccessaryCostProc.get("POS_AGENCY");

			dpstAmt	= (BigDecimal)mAccessaryCostProc.get("AMT");
			if ( dpstAmt.compareTo(new BigDecimal("0")) == 0 ) {
				logMng.writeLogFile("대리점[" + agency + "] : 매출원가[AMT] : " + dpstAmt.toString());
				logMng.writeLogFile("금액이 0 이므로 전송처리 하지 않음.");
				continue;
			}

			//set head info
			mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
					String.valueOf(idx+1),
					(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
					dpstAmt.toString()
			);
			mHead.put("ACC_PLC", mAccessaryCostProc.get("DEAL_CO_CD"));	//정산처 : 거래처
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);

				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", mAccessaryCostProc.get("DEAL_CO_CD"));
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) mAccessaryCostProc.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mAccessaryCostProc.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						(String)mAccessaryCostProc.get("DEAL_CO_CD"),
						dpstAmt.toString(),
						(String)mAccessaryCostProc.get("UKEY_SUB_CD"))
				);
			}
		}		
		imTables.put("KEY",		oKey);
		imTables.put("HEAD",	alHead.toArray());
		imTables.put("ITEM",	alItem.toArray());

		retMap = sap.executeRFC("ZIF_DOC_INTERFACE", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);

		/*
		 *  TSAC_ACCESSARY_IF : 악세서리 IF
		 *  전송여부, 전송일자 수정
		 */
		ArrayList<Object> alKey	= (ArrayList<Object>)retMap.get("KEY");
		Map mKey = (Map<String, Object>)alKey.get(0);
		mKey.put("AGENCY" , agency);
		mKey.put("REC_CL" , "04");
		
		sqlMap.update("SACERP08140.updateTsacAccessaryIf", mKey);  /* 온라인 결제 수수료 월정산   */
		
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}	
}
