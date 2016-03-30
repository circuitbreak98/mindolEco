package com.sktst.batch.sac.erp.biz;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/판매회계</li>
 * <li>설 명 : 단말기 매입 ERP전송 </li>
 * <li>작성일 : 2009-04-28</li>
 * </ul>
 *
 * @author 하창주 (hachangjoo)
 */
public class SACERP08150 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08150";
	
	private SAP sap = null;
	
	private List lstJourStdInfo	= null;		//분개기준정보
	private List lstAmtInfo		= null;		//금액정보
	private List lstAgency		= null;		//대리점목록
	
	private String zbudat 		= "";		//귀속일자
	private String budat 		= "";		//전표기장일
	private String userCd		= "";		//대리점 정산담당자 사번
	private String agency		= "";		//대리점 거래처코드
	private String prchsPlc		= "";		//매입처
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

	private Map<String, Object> requestMap = new HashMap<String, Object>();
	private Map<String, Object> retMap = null;	//전송결과 map
	private Map<String, Object> mKey = null;

	/**
	 * (월)단말기매입 배치 프로그램을 수행한다.
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
			String yymm = "";
			String dpstMonth = "";		//월
			if( request.size() > 1 ) {
				yymm	= (String)request.get("args1");
				pAgency	= (String)request.get("args2");
				pZgubun	= (String)request.get("args3");
				pDealCo	= (String)request.get("args4");
				
				yymm = yymm.equals("?") ? "" : yymm;
				pAgency = pAgency.equals("?") ? "" : pAgency;
				pDealCo = pDealCo.equals("?") ? "" : pDealCo;
				pZgubun = pZgubun.equals("?") ? "" : pZgubun;

				logMng.writeLogFile("args1[정산월] : " + yymm);
				logMng.writeLogFile("args2[대리점] : " + pAgency);
				logMng.writeLogFile("args3[전송구분] : " + pZgubun);
				logMng.writeLogFile("args4[거래처] : " + pDealCo);
			}

			if (yymm.equals("")) {
				yymm = ErpCommon.getLastMonth();	//입금년월(전월)
				dpstMonth = yymm.substring(4,6);
			}
			String lastDay = ErpCommon.getLastDay(Integer.parseInt(yymm.substring(0, 4)),
					Integer.parseInt(yymm.substring(4)) );
			zbudat = yymm + lastDay;	//귀속일자
			budat = ErpCommon.getDayInterval(zbudat, 1); // 전기일자

			/**
			 *   회계팀 확정
			 *   2010년 5월 마감부터 적용
			 *   3,6,9,12월은 전기일 증빙일이 동일한 일자
			 */
			if(yymm.compareTo("201005") < 0 || "03".equals(dpstMonth) || "06".equals(dpstMonth) || "09".equals(dpstMonth)|| "12".equals(dpstMonth)){
				budat = zbudat; // 전기일자
			}
		
			log.debug("yymm(정산월) : " + yymm);
			log.debug("zbudat(귀속일자) : " + zbudat);
			log.debug("budat(전기일자) : " + budat);

			requestMap.put("YYMM",			yymm);
			requestMap.put("AGENCY",		pAgency);
			requestMap.put("DEAL_CO_CD",	pDealCo);
			requestMap.put("TRMS_ITEM",		pZgubun);
			
			pDealCo = "";

			//④ DB 처리
			/** 
			 *  A01 : ZGUBUN_EQP_PRCHS_SKN       : (월)단말기매입
			 *  A02 : ZGUBUN_EQP_PRCHS_OTHERS    : (월)단말기매입 - 타제조업체
			 *  A03 : ZGUBUN_ACCESSARY_EQP_PRCHS : (월)악세사리 상품매입
			 */
			if ( "".equals(pZgubun) ) {
				logMng.writeLogFile("(월)단말기매입 - SKN send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_EQP_PRCHS_SKN);
				sendEqpPrchsSkn(sqlMap);

				logMng.writeLogFile("(월)단말기매입 - 타제조업체 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_EQP_PRCHS_OTHERS);
				sendEqpPrchsOthers(sqlMap);

			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_EQP_PRCHS_SKN) ) {
				logMng.writeLogFile("(월)단말기매입 - SKN send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_EQP_PRCHS_SKN);
				sendEqpPrchsSkn(sqlMap);

			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_EQP_PRCHS_OTHERS) ) {
				logMng.writeLogFile("(월)단말기매입 - 타제조업체 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_EQP_PRCHS_OTHERS);
				sendEqpPrchsOthers(sqlMap);

			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_ACCESSARY_EQP_PRCHS) ) {
				logMng.writeLogFile("(월)악세사리 상품매입 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_ACCESSARY_EQP_PRCHS);
				sendAccessaryEqpPrchs(sqlMap);
			}
			
			//⑤ 결과 데이터 처리
			
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
	 * (월)단말기매입 - SKN ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendEqpPrchsSkn(SqlMapClient sqlMap) throws Exception {

		//매입채무 월정산 row map
		Map mPrchsDebtAcc = null;
		BigDecimal fixCashCrdt	= null;		//확정현금여신
		BigDecimal fixSvcCrdt	= null;		//확정개통여신
		BigDecimal splyPrcAmt	= null;		//공급가액
		BigDecimal vat			= null;		//부가세
		BigDecimal wrbrt		= null;		//Item 금액
		String dpstYm = (String) requestMap.get("YYMM");

		lstAgency = (List)sqlMap.queryForList("SACERP08150.getAgencySknList", requestMap);
		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)매입채무 SKN 월정산 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}
		
		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency	= (String)mAgency.get("ORG_CD");
			userCd	= ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd	= ErpCommon.fillZeroFront(userCd, 8);
			
			requestMap.put("AGENCY", agency);
			//(월)단말기매입 - SKN
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08150.getPrchsDebtAccSknList", requestMap);

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
			
			//매입채무 월정산 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mPrchsDebtAcc = (Map)lstAmtInfo.get(i);
				prchsPlc = (String)mPrchsDebtAcc.get("PRCHS_PLC");
				
				fixCashCrdt	= (BigDecimal)mPrchsDebtAcc.get("FIX_CASH_CRDT");
				fixSvcCrdt	= (BigDecimal)mPrchsDebtAcc.get("FIX_SVC_CRDT");
				splyPrcAmt	= (BigDecimal)mPrchsDebtAcc.get("SPLY_PRC_AMT");
				vat			= (BigDecimal)mPrchsDebtAcc.get("VAT");

				if ( splyPrcAmt.add(vat).compareTo(new BigDecimal("0")) == 0 ) {
					logMng.writeLogFile("대리점[" + agency + "] : 매입처[" + prchsPlc + "] : (월)단말기매입 - SKN : " + splyPrcAmt.add(vat).toString());
					logMng.writeLogFile("금액이 0 이므로 전송처리 하지 않음.");
					continue;
				}
				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						splyPrcAmt.add(vat).toString()
				);
				mHead.put("ACC_PLC", prchsPlc);	//정산처 : 매입처
				alHead.add(mHead);

				//set item info
				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//SEQ_NO 별 금액지정
					if( "001".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = splyPrcAmt;		//차변1 : 공급가액
					} else if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = vat;			//차변2 : 부가세
					} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = fixCashCrdt;	//대변1 : 확정현금여신
					} else if( "004".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = fixSvcCrdt;		//대변2 : 확정개통여신
					}

					//SKN 거래처코드는 ErpCommon.getSknDealCoCd()를 이용하여 대리점별로 구분하여 전송한다. 
					alItem.add(ErpCommon.makeItem(
							mHead,
							String.valueOf(j+1),
							mJourStd,
							ErpCommon.getSknDealCoCd(agency),
							wrbrt.toString(),
							(String)mPrchsDebtAcc.get("UKEY_SUB_CD"))
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
			mPrchsDebtAcc.put("ZIFDATE", mKey.get("ZIFDATE"));
			mPrchsDebtAcc.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08150.updateTsacPrchsDebtAcc", mPrchsDebtAcc);

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
		}
	}


	/**
	 * (월)단말기매입 - 타제조업체 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendEqpPrchsOthers(SqlMapClient sqlMap) throws Exception {
		
		//매입채무 월정산 row map
		Map mPrchsDebtAcc = null;
		BigDecimal fixCashCrdt	= null;		//확정현금여신
		BigDecimal fixSvcCrdt	= null;		//확정개통여신
		BigDecimal splyPrcAmt	= null;		//공급가액
		BigDecimal vat			= null;		//부가세
		BigDecimal wrbrt		= null;		//Item 금액
		String dpstYm = (String) requestMap.get("YYMM");
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAgency = (List)sqlMap.queryForList("SACERP08150.getAgencyOthersList", requestMap);
		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)매입채무 타제조업체 월정산 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}

		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency	= (String)mAgency.get("ORG_CD");
			userCd	= ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd	= ErpCommon.fillZeroFront(userCd, 8);
			requestMap.put("ORG_CD", agency);
			//(월)단말기매입 - 타제조업체 : 확정개통여신 금액이 존재하지 않는 경우
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08150.getPrchsDebtAccOthersList", requestMap);

			//set header info
			oKey = ErpCommon.makeKey((String)mAgency.get("UKEY_AGENCY_CD"),
					zbudat,
					(String)((Map)lstJourStdInfo.get(0)).get("JOUR_CD"),
					userCd.equals("") ? PROG_ID : userCd,
					String.valueOf(lstAmtInfo.size())
					); 

			mKey = (Map<String, Object>) oKey[0];
			mKey.put("BUDAT", budat);
			oKey[0] = mKey;

			//매입채무 월정산 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();
			
			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mPrchsDebtAcc = (Map)lstAmtInfo.get(i);
				prchsPlc = (String)mPrchsDebtAcc.get("PRCHS_PLC");
				
				fixCashCrdt	= (BigDecimal)mPrchsDebtAcc.get("FIX_CASH_CRDT");
				fixSvcCrdt	= (BigDecimal)mPrchsDebtAcc.get("FIX_SVC_CRDT");
				splyPrcAmt	= (BigDecimal)mPrchsDebtAcc.get("SPLY_PRC_AMT");
				vat			= (BigDecimal)mPrchsDebtAcc.get("VAT");

				if ( splyPrcAmt.add(vat).compareTo(new BigDecimal("0")) == 0 ) {
					logMng.writeLogFile("대리점[" + agency + "] : [매입처[" + prchsPlc + "] : (월)단말기매입 - 타제조업체 : " + splyPrcAmt.add(vat).toString());
					logMng.writeLogFile("금액이 0 이므로 전송처리 하지 않음.");
					continue;
				}

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						splyPrcAmt.add(vat).toString()
				);
				mHead.put("ACC_PLC", prchsPlc);	//정산처 : 매입처
				alHead.add(mHead);

				//set item info
				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//SEQ_NO 별 금액지정
					if( "001".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = splyPrcAmt;				//차변1 : 공급가액
					} else if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = vat;					//차변2 : 부가세
					} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = splyPrcAmt.add(vat);	//대변 : 공급가액 + 부가세
					}

					/**
					 *    현대백화점 서브점코드  조회
					 *    ZSAC_C_00110 : 현대백화점 서브점코드
					 *    ukey_sub_cd : 서브점코드
					 */
					ukeySubCdMap     = new HashMap();
					ukeySubCdMap.put("deal_co_cd", mPrchsDebtAcc.get("DPST_PLC"));
					ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
					if(ukeySubCdMap != null){
						ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
						if(!"0000".equals(ukeySubCd)){
							mPrchsDebtAcc.put("UKEY_SUB_CD", ukeySubCd);
						}
					}

					alItem.add(ErpCommon.makeItem(
							mHead,
							String.valueOf(j+1),
							mJourStd,
							(String)mPrchsDebtAcc.get("DPST_PLC"),
							wrbrt.toString(),
							(String)mPrchsDebtAcc.get("UKEY_SUB_CD"))
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
			mPrchsDebtAcc.put("ZIFDATE", mKey.get("ZIFDATE"));
			mPrchsDebtAcc.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08150.updateTsacPrchsDebtAcc2", mPrchsDebtAcc);

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
		}
	}

	/**
	 * (월)악세사리 상품매입
	 * @author	이강수(leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendAccessaryEqpPrchs(SqlMapClient sqlMap) throws Exception {
		//매출원가처리 row map
		Map mAccessaryEqpPrchs 	= null;
		BigDecimal dpstAmt		= null;	// 금액
		Map ukeySubCdMap        = null;
		String ukeySubCd        = "";
		BigDecimal wrbrt		= null;		//Item 금액

		/**
		 *  TSAC_ACCESSARY_IF : 악세서리 IF 테이블
		 *  REC_CL : 레코드구분 
		 *    01 : 매입 
		 *    02 : 매출 
		 *    03 : 카드
		 *    04 : 매출원가 
		 */
		requestMap.put("REC_CL", "01");     // 매출원가

		lstAmtInfo = (List)sqlMap.queryForList("SACERP08150.getAccessaryIf", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			remarkMsg = "(월)악세사리 상품매입 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}

		//set key info
		mAccessaryEqpPrchs = (Map)lstAmtInfo.get(0);
		userCd = ErpCommon.nullToSpace((String)mAccessaryEqpPrchs.get("USER_CD"));
		userCd = ErpCommon.fillZeroFront(userCd, 8);
		oKey = ErpCommon.makeKey((String)mAccessaryEqpPrchs.get("UKEY_AGENCY_CD"),
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

		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();
		
		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {

			mAccessaryEqpPrchs = (Map)lstAmtInfo.get(idx);
			agency	= (String)mAccessaryEqpPrchs.get("POS_AGENCY");

			dpstAmt	= (BigDecimal)mAccessaryEqpPrchs.get("AMT");
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
			mHead.put("ACC_PLC", mAccessaryEqpPrchs.get("DEAL_CO_CD"));	//정산처 : 거래처
			alHead.add(mHead);

			for (int j = 0; j < lstJourStdInfo.size(); j++) {
				mJourStd = (Map)lstJourStdInfo.get(j);
				//SEQ_NO 별 금액지정
				if( "001".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = dpstAmt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);	//대변1 : 상품매출
				} else if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = dpstAmt.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP);
					wrbrt = dpstAmt.subtract(wrbrt);	//대변 2:매출부가세
				} else if( "003".equals((String)mJourStd.get("SEQ_NO")) ) {
					wrbrt = dpstAmt;	//차변 : 002 + 003
				}

				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", mAccessaryEqpPrchs.get("DEAL_CO_CD"));
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) mAccessaryEqpPrchs.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mAccessaryEqpPrchs.put("UKEY_SUB_CD", ukeySubCd);
					}
				}

				//set item info
				alItem.add(ErpCommon.makeItem(mHead,
						String.valueOf(j+1),
						mJourStd,
						(String)mAccessaryEqpPrchs.get("DEAL_CO_CD"),
						wrbrt.toString(),
						(String)mAccessaryEqpPrchs.get("UKEY_SUB_CD"))
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
		mKey.put("REC_CL" , "01");
		
		sqlMap.update("SACERP08140.updateTsacAccessaryIf", mKey);  /* 온라인 결제 수수료 월정산   */
		
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
	}
	
}

