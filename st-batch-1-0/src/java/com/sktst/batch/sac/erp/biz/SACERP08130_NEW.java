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
public class SACERP08130_NEW extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08130_NEW";

	private SAP sap = null;

	private List lstJourStdInfo	= null;		//분개기준정보
	private List lstAgencyList	= null;		//대리점목록
	private List lstAmtInfo		= null;		//금액정보

	private String userCd		= "";		//대리점 정산담당자 사번
	private String agency		= "";		//대리점 거래처코드
	private String accPlc		= "";		//정산처
	private String accMth		= "";		//정산월
	private String fixDt 		= "";		//정산확정일
	private String zbudat 		= "";		//귀속일자
	private String pAgency		= "";		//대리점 거래처코드(shell parameter)
	private String pAccPlc		= "";		//정산처(shell parameter)
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


	/**
	 * 판매수수료 배치 프로그램을 수행한다.
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
			if( request.size() > 1 ) {
				accMth	= (String)request.get("args1");
				pAgency	= (String)request.get("args2");
				pAccPlc	= (String)request.get("args3");
				pZgubun	= (String)request.get("args4");

				accMth = accMth.equals("?") ? "" : accMth;
				pAgency = pAgency.equals("?") ? "" : pAgency;
				pAccPlc = pAccPlc.equals("?") ? "" : pAccPlc;
				pZgubun = pZgubun.equals("?") ? "" : pZgubun;

				logMng.writeLogFile("args1[정산월] : " + accMth);
				logMng.writeLogFile("args2[대리점] : " + pAgency);
				logMng.writeLogFile("args3[정산처] : " + pAccPlc);
				logMng.writeLogFile("args4[전송구분] : " + pZgubun);
			}
			
			if (accMth.equals("")) {
				accMth = ErpCommon.getLastMonth();	//입금년월(전월)
			}
			String lastDay = ErpCommon.getLastDay(Integer.parseInt(accMth.substring(0, 4)),
					Integer.parseInt(accMth.substring(4)) );
			zbudat = accMth + lastDay;	//귀속일자

			log.debug("accMth(정산월) : " + accMth);
			log.debug("zbudat(귀속일자) : " + zbudat);

			requestMap.put("ACC_MTH",	accMth);
			requestMap.put("YYMM",		accMth);
			requestMap.put("AGENCY",	pAgency);
			requestMap.put("ACC_PLC",	pAccPlc);
			requestMap.put("TRMS_ITEM", pZgubun);

			/**
			 *    대리점 조회 
			 */
			lstAgencyList = (List)sqlMap.queryForList("SACERP08130_NEW.getAgencyList", requestMap);
			if ( lstAgencyList.size() == 0 ) {
				remarkMsg = "(월)판매수수료(부가세) ERP전송 건이 존재하지 않습니다1.";
				log.debug("execute Exception : " + remarkMsg);
				ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
				return 0;
			}
			
			//④ DB 처리
			/** 
			 *  E01 : ZGUBUN_SALE_CMMS            : (월)판매수수료(부가세)
			 *  E02 : ZGUBUN_SALE_CMMS_ADD        : (월)판매수수료(부가세없는 증가)
			 *  E03 : ZGUBUN_SALE_CMMS_SUB        : (월)판매수수료(부가세없는 차감)
			 *  E06 : ZGUBUN_FIX_VIRTL_ACC_CMMS   : (월)확정가상계좌수수료
			 *  E07 : ZGUBUN_FIX_ACC_TRNSF_CMMS   : (월)확정계좌이체수수료
			 *  E08 : ZGUBUN_FIX_CARD_SETTLE_CMMS : (월)확정카드결제수수료
			 *  E09 : ZGUBUN_MC_SALE_CMMS         : (월)M채널 판매수수료(부가세)
			 *  
			 *  B10 : ZGUBUN_FIX_AGRMT_ASTAMT     : (월)확정_약정보조금
			 *  B11 : ZGUBUN_FIX_ALLOT_SALE_AMT   : (월)확정_할부매출
			 */
			if ( "".equals(pZgubun) ) {
				logMng.writeLogFile("(월)판매수수료(부가세) send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_SALE_CMMS);
				sendSaleCmms(sqlMap);
				
				logMng.writeLogFile("(월)판매수수료(부가세없는 증가) send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_SALE_CMMS_ADD);
				sendSaleCmmsAdd(sqlMap);
				
				logMng.writeLogFile("(월)판매수수료(부가세없는 차감) send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_SALE_CMMS_SUB);
				sendSaleCmmsSub(sqlMap);

				logMng.writeLogFile("(월)확정가상계좌수수료 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_FIX_VIRTL_ACC_CMMS);
				sendFixVirtlAccCmms(sqlMap);

				logMng.writeLogFile("(월)확정계좌이체수수료 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_FIX_ACC_TRNSF_CMMS);
				sendFixAccTrnsfCmms(sqlMap);

				logMng.writeLogFile("(월)확정카드결제수수료 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_FIX_CARD_SETTLE_CMMS);
				sendFixCardSettleCmms(sqlMap);

				logMng.writeLogFile("(월)M채널 판매수수료(부가세) send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_MC_SALE_CMMS);
				sendMcSaleCmms(sqlMap);

				logMng.writeLogFile("(월)확정_약정보조금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_FIX_AGRMT_ASTAMT);
				sendFixAgrmtAstAmt(sqlMap);

				logMng.writeLogFile("확정_할부매출 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_FIX_ALLOT_SALE_AMT);
				sendFixAllotSaleAmt(sqlMap);

			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_SALE_CMMS) ) {
				logMng.writeLogFile("(월)판매수수료(부가세) send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_SALE_CMMS);
				sendSaleCmms(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_SALE_CMMS_ADD) ) {
				logMng.writeLogFile("(월)판매수수료(부가세없는 증가) send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_SALE_CMMS_ADD);
				sendSaleCmmsAdd(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_SALE_CMMS_SUB) ) {
				logMng.writeLogFile("(월)판매수수료(부가세없는 차감) send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_SALE_CMMS_SUB);
				sendSaleCmmsSub(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_FIX_VIRTL_ACC_CMMS) ) {
				logMng.writeLogFile("(월)확정가상계좌수수료 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_FIX_VIRTL_ACC_CMMS);
				sendFixVirtlAccCmms(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_FIX_ACC_TRNSF_CMMS) ) {
				logMng.writeLogFile("(월)확정계좌이체수수료 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_FIX_ACC_TRNSF_CMMS);
				sendFixAccTrnsfCmms(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_FIX_CARD_SETTLE_CMMS) ) {
				logMng.writeLogFile("(월)확정카드결제수수료 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_FIX_CARD_SETTLE_CMMS);
				sendFixCardSettleCmms(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_MC_SALE_CMMS) ) {
				logMng.writeLogFile("(월)M채널 판매수수료(부가세) send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_MC_SALE_CMMS);
				sendMcSaleCmms(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_FIX_AGRMT_ASTAMT) ) {
				logMng.writeLogFile("(월)확정_약정보조금 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_FIX_AGRMT_ASTAMT);
				sendFixAgrmtAstAmt(sqlMap);
			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_FIX_ALLOT_SALE_AMT) ) {
				logMng.writeLogFile("확정_할부매출 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_FIX_ALLOT_SALE_AMT);
				sendFixAllotSaleAmt(sqlMap);
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
	 * (월)판매수수료(부가세) ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendSaleCmms(SqlMapClient sqlMap) throws Exception {

		//수수료월정산 row map
		Map mCmmsAcc = null;
		BigDecimal cmmsAmt	= null;		//수수료
		BigDecimal wrbrt	= null;		//Item 금액
		String lastDay = "";

		for (int idx = 0; idx < lstAgencyList.size(); idx++) {
			mAgency = (Map)lstAgencyList.get(idx);

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			
			requestMap.put("AGENCY", agency);
			requestMap.put("FIX_DT", fixDt);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08130_NEW.getCmmsAccList", requestMap);
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)판매수수료(부가세) ERP전송 건이 존재하지 않습니다.";
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

			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCmmsAcc = (Map)lstAmtInfo.get(i);
				accPlc = (String)mCmmsAcc.get("ACC_PLC");

				cmmsAmt = (BigDecimal)mCmmsAcc.get("CMMS_AMT");
				//log.debug("CMMS_AMT : " + cmmsAmt.toString());
				if ( cmmsAmt.compareTo(new BigDecimal("0")) == 0 ) {
					logMng.writeLogFile("대리점[" + agency + "] : [정산처[" + accPlc + "] : (월)판매수수료[CMMS_AMT] : " + cmmsAmt.toString());
				}

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						cmmsAmt.toString()
				);
				mHead.put("ACC_PLC", accPlc);
				alHead.add(mHead);

				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//SEQ_NO 별 금액지정
					wrbrt = cmmsAmt;	//차변 : 002 + 003
					if( "001".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = cmmsAmt.divide(new BigDecimal("1.1"), BigDecimal.ROUND_HALF_UP);	//대변1 : 상품매출
					} else if( "002".equals((String)mJourStd.get("SEQ_NO")) ) {
						wrbrt = cmmsAmt.divide(new BigDecimal("1.1"), BigDecimal.ROUND_HALF_UP);
						wrbrt = cmmsAmt.subtract(wrbrt);	//대변 2:매출부가세
					}

					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							accPlc,
							wrbrt.toString(),
							(String)mCmmsAcc.get("UKEY_SUB_CD"))
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
			mCmmsAcc.put("ZIFDATE", mKey.get("ZIFDATE"));
			mCmmsAcc.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08130_NEW.updateCmmsAccSendInfo"    , mCmmsAcc);  /* 수수료_월정산             */
			sqlMap.update("SACERP08130_NEW.updateOaCmmsAccSendInfo"  , mCmmsAcc);  /* 도매 수수료_월정산  */
			sqlMap.update("SACERP08130_NEW.updateOsCmmsAccSendInfo"  , mCmmsAcc);  /* 소매 수수료_월정산  */
			
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		}
	}

	/**
	 * (월)판매수수료(부가세없는 증가)
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendSaleCmmsAdd(SqlMapClient sqlMap) throws Exception {

		//수수료월정산 row map
		Map mCmmsAcc = null;
		BigDecimal accMtchAmt = null;	//정산상계금액
		String lastDay = "";

		for (int idx = 0; idx < lstAgencyList.size(); idx++) {
			mAgency = (Map)lstAgencyList.get(idx);

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);

			requestMap.put("AGENCY", agency);
			requestMap.put("FIX_DT", fixDt);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08130_NEW.getAccMtchAmtAddList", requestMap);
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)판매수수료(부가세없는 증가) ERP전송 건이 존재하지 않습니다.";
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

			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCmmsAcc = (Map)lstAmtInfo.get(i);
				accPlc = (String)mCmmsAcc.get("ACC_PLC");

				accMtchAmt = (BigDecimal)mCmmsAcc.get("ACC_MTCH_AMT");
				//log.debug("acc_mtch_amt : " + accMtchAmt.toString());
				if ( accMtchAmt.compareTo(new BigDecimal("0")) == 0 ) {
					logMng.writeLogFile("대리점[" + agency + "] : [정산처[" + accPlc + "] : 정산상계금액[acc_mtch_amt] : " + accMtchAmt.toString());
				}

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						accMtchAmt.toString()
				);
				mHead.put("ACC_PLC", accPlc);
				alHead.add(mHead);

				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							accPlc,
							accMtchAmt.toString(),
							(String)mCmmsAcc.get("UKEY_SUB_CD"))
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
			mCmmsAcc.put("ZIFDATE", mKey.get("ZIFDATE"));
			mCmmsAcc.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08130_NEW.updateCmmsAccSendInfo"    , mCmmsAcc);  /* 수수료_월정산             */
			sqlMap.update("SACERP08130_NEW.updateMcCmmsAccSendInfo"  , mCmmsAcc);  /* MC채널 수수료_월정산  */
			sqlMap.update("SACERP08130_NEW.updateOtlCmmsAccSendInfo" , mCmmsAcc);  /* 결제 수수료_월정산  */
			sqlMap.update("SACERP08130_NEW.updateOaCmmsAccSendInfo"  , mCmmsAcc);  /* 도매 수수료_월정산  */
			sqlMap.update("SACERP08130_NEW.updateOsCmmsAccSendInfo"  , mCmmsAcc);  /* 소매 수수료_월정산  */

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		}
	}	

	/**
	 * (월)판매수수료(부가세없는 차감)
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendSaleCmmsSub(SqlMapClient sqlMap) throws Exception {

		//수수료월정산 row map
		Map mCmmsAcc = null;
		BigDecimal accMtchAmt = null;	//정산상계금액
		String lastDay = "";

		for (int idx = 0; idx < lstAgencyList.size(); idx++) {
			mAgency = (Map)lstAgencyList.get(idx);

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);

			requestMap.put("AGENCY", agency);
			requestMap.put("FIX_DT", fixDt);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08130_NEW.getAccMtchAmtSubList", requestMap);
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)판매수수료(부가세없는 차감) ERP전송 건이 존재하지 않습니다.";
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

			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCmmsAcc = (Map)lstAmtInfo.get(i);
				accPlc = (String)mCmmsAcc.get("ACC_PLC");

				accMtchAmt = (BigDecimal)mCmmsAcc.get("ACC_MTCH_AMT");
				
				//log.debug("acc_mtch_amt : " + accMtchAmt.toString());
				accMtchAmt = accMtchAmt.abs();
				if ( accMtchAmt.compareTo(new BigDecimal("0")) == 0 ) {
					logMng.writeLogFile("대리점[" + agency + "] : [정산처[" + accPlc + "] : 정산상계금액[acc_mtch_amt] : " + accMtchAmt.toString());
				}

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						accMtchAmt.toString()
				);
				mHead.put("ACC_PLC", accPlc);
				alHead.add(mHead);

				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							accPlc,
							accMtchAmt.toString(),
							(String)mCmmsAcc.get("UKEY_SUB_CD"))
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
			mCmmsAcc.put("ZIFDATE", mKey.get("ZIFDATE"));
			mCmmsAcc.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08130_NEW.updateCmmsAccSendInfo"   , mCmmsAcc);  /* 수수료_월정산             */
			sqlMap.update("SACERP08130_NEW.updateMcCmmsAccSendInfo" , mCmmsAcc);  /* MC채널 수수료_월정산  */
			sqlMap.update("SACERP08130_NEW.updateOtlCmmsAccSendInfo", mCmmsAcc);  /* 결제 수수료_월정산  */
			sqlMap.update("SACERP08130_NEW.updateOaCmmsAccSendInfo" , mCmmsAcc);  /* 도매 수수료_월정산  */
			sqlMap.update("SACERP08130_NEW.updateOsCmmsAccSendInfo" , mCmmsAcc);  /* 소매 수수료_월정산  */

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		}
	}

	/**
	 * (월)확정가상계좌수수료
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendFixVirtlAccCmms(SqlMapClient sqlMap) throws Exception {

		//수수료월정산 row map
		Map mCmmsAcc = null;
		BigDecimal fixVirtlAccCmms = null;	//확정가상계좌수수료
		String lastDay = "";

		for (int idx = 0; idx < lstAgencyList.size(); idx++) {
			mAgency = (Map)lstAgencyList.get(idx);

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);

			requestMap.put("AGENCY", agency);
			requestMap.put("FIX_DT", fixDt);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08130_NEW.getFixVirtlAccCmmsList", requestMap);
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)확정가상계좌수수료 ERP전송 건이 존재하지 않습니다.";
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

			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCmmsAcc = (Map)lstAmtInfo.get(i);
				accPlc = (String)mCmmsAcc.get("ACC_PLC");

				fixVirtlAccCmms = (BigDecimal)mCmmsAcc.get("FIX_VIRTL_ACC_CMMS");  // 확정가상계좌수수료
				
				//log.debug("acc_mtch_amt : " + accMtchAmt.toString());
				fixVirtlAccCmms = fixVirtlAccCmms.abs();
				if ( fixVirtlAccCmms.compareTo(new BigDecimal("0")) == 0 ) {
					logMng.writeLogFile("대리점[" + agency + "] : [정산처[" + accPlc + "] : 확정가상계좌수수료[fix_virtl_acc_cmms] : " + fixVirtlAccCmms.toString());
				}

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						fixVirtlAccCmms.toString()
				);
				mHead.put("ACC_PLC", accPlc);
				alHead.add(mHead);

				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							accPlc,
							fixVirtlAccCmms.toString(),
							(String)mCmmsAcc.get("UKEY_SUB_CD"))
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
			mCmmsAcc.put("ZIFDATE", mKey.get("ZIFDATE"));
			mCmmsAcc.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08130_NEW.updateOtlCmmsAccSendInfo", mCmmsAcc);  /* 결제 수수료_월정산  */

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		}
	}

	/**
	 * (월)확정계좌이체수수료
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendFixAccTrnsfCmms(SqlMapClient sqlMap) throws Exception {

		//수수료월정산 row map
		Map mCmmsAcc = null;
		BigDecimal fixAccTrnsfCmms = null;	//(월)확정계좌이체수수료
		String lastDay = "";

		for (int idx = 0; idx < lstAgencyList.size(); idx++) {
			mAgency = (Map)lstAgencyList.get(idx);

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);

			requestMap.put("AGENCY", agency);
			requestMap.put("FIX_DT", fixDt);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08130_NEW.getFixAccTrnsfCmmsList", requestMap);
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)확정계좌이체수수료 ERP전송 건이 존재하지 않습니다.";
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

			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCmmsAcc = (Map)lstAmtInfo.get(i);
				accPlc = (String)mCmmsAcc.get("ACC_PLC");

				fixAccTrnsfCmms = (BigDecimal)mCmmsAcc.get("FIX_ACC_TRNSF_CMMS");  // 확정계좌이체수수료
				
				//log.debug("acc_mtch_amt : " + accMtchAmt.toString());
				fixAccTrnsfCmms = fixAccTrnsfCmms.abs();
				if ( fixAccTrnsfCmms.compareTo(new BigDecimal("0")) == 0 ) {
					logMng.writeLogFile("대리점[" + agency + "] : [정산처[" + accPlc + "] : 확정계좌이체수수료[fix_virtl_acc_cmms] : " + fixAccTrnsfCmms.toString());
				}

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						fixAccTrnsfCmms.toString()
				);
				mHead.put("ACC_PLC", accPlc);
				alHead.add(mHead);

				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							accPlc,
							fixAccTrnsfCmms.toString(),
							(String)mCmmsAcc.get("UKEY_SUB_CD"))
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
			mCmmsAcc.put("ZIFDATE", mKey.get("ZIFDATE"));
			mCmmsAcc.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08130_NEW.updateOtlCmmsAccSendInfo", mCmmsAcc);  /* 결제 수수료_월정산  */

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		}
	}
	
	/**
	 * (월)확정카드결제수수료
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendFixCardSettleCmms(SqlMapClient sqlMap) throws Exception {

		//수수료월정산 row map
		Map mCmmsAcc = null;
		BigDecimal fixCardSettleCmms = null;	// 확정카드결제수수료
		String lastDay = "";

		for (int idx = 0; idx < lstAgencyList.size(); idx++) {
			mAgency = (Map)lstAgencyList.get(idx);

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);

			requestMap.put("AGENCY", agency);
			requestMap.put("FIX_DT", fixDt);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08130_NEW.getFixCardSettleCmmsList", requestMap);
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)확정카드결제수수료 ERP전송 건이 존재하지 않습니다.";
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

			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCmmsAcc = (Map)lstAmtInfo.get(i);
				accPlc = (String)mCmmsAcc.get("ACC_PLC");

				fixCardSettleCmms = (BigDecimal)mCmmsAcc.get("FIX_CARD_SETTLE_CMMS");  // 확정카드결제수수료
				
				//log.debug("acc_mtch_amt : " + accMtchAmt.toString());
				fixCardSettleCmms = fixCardSettleCmms.abs();
				if ( fixCardSettleCmms.compareTo(new BigDecimal("0")) == 0 ) {
					logMng.writeLogFile("대리점[" + agency + "] : [정산처[" + accPlc + "] : 확정카드결제수수료[fix_card_settle_cmms] : " + fixCardSettleCmms.toString());
				}

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						fixCardSettleCmms.toString()
				);
				mHead.put("ACC_PLC", accPlc);
				alHead.add(mHead);

				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							accPlc,
							fixCardSettleCmms.toString(),
							(String)mCmmsAcc.get("UKEY_SUB_CD"))
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
			mCmmsAcc.put("ZIFDATE", mKey.get("ZIFDATE"));
			mCmmsAcc.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08130_NEW.updateOtlCmmsAccSendInfo", mCmmsAcc);  /* 결제 수수료_월정산  */

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		}
	}	
	
	/**
	 * (월)M채널 판매수수료(부가세)
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendMcSaleCmms(SqlMapClient sqlMap) throws Exception {

		//수수료월정산 row map
		Map mCmmsAcc = null;
		BigDecimal cmmsAmt = null;	//M채널 판매수수료(부가세)
		String lastDay = "";

		for (int idx = 0; idx < lstAgencyList.size(); idx++) {
			mAgency = (Map)lstAgencyList.get(idx);

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);

			requestMap.put("AGENCY", agency);
			requestMap.put("FIX_DT", fixDt);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08130_NEW.getMcSaleCmms", requestMap);
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)M채널 판매수수료(부가세) ERP전송 건이 존재하지 않습니다.";
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

			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCmmsAcc = (Map)lstAmtInfo.get(i);
				accPlc = (String)mCmmsAcc.get("ACC_PLC");

				cmmsAmt = (BigDecimal)mCmmsAcc.get("CMMS_AMT");  // M채널 판매수수료(부가세)
				
				//log.debug("acc_mtch_amt : " + accMtchAmt.toString());
				cmmsAmt = cmmsAmt.abs();
				if ( cmmsAmt.compareTo(new BigDecimal("0")) == 0 ) {
					logMng.writeLogFile("대리점[" + agency + "] : [정산처[" + accPlc + "] : M채널 판매수수료(부가세)[cmms_amt] : " + cmmsAmt.toString());
				}

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						cmmsAmt.toString()
				);
				mHead.put("ACC_PLC", accPlc);
				alHead.add(mHead);

				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							accPlc,
							cmmsAmt.toString(),
							(String)mCmmsAcc.get("UKEY_SUB_CD"))
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
			mCmmsAcc.put("ZIFDATE", mKey.get("ZIFDATE"));
			mCmmsAcc.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08130_NEW.updateMcCmmsAccSendInfo", mCmmsAcc);  /* MC채널 수수료_월정산  */

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		}
	}
	/**
	 * (월)확정_약정보조금
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendFixAgrmtAstAmt(SqlMapClient sqlMap) throws Exception {

		//수수료월정산 row map
		Map mCmmsAcc = null;
		BigDecimal fixAgrmtAstAmt = null;	//확정_약정보조금
		String lastDay = "";

		for (int idx = 0; idx < lstAgencyList.size(); idx++) {
			mAgency = (Map)lstAgencyList.get(idx);

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);

			requestMap.put("AGENCY", agency);
			requestMap.put("FIX_DT", fixDt);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08130_NEW.getFixAgrmtAstAmtList", requestMap);
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)확정_약정보조금 ERP전송 건이 존재하지 않습니다.";
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

			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCmmsAcc = (Map)lstAmtInfo.get(i);
				accPlc = (String)mCmmsAcc.get("ACC_PLC");

				fixAgrmtAstAmt = (BigDecimal)mCmmsAcc.get("FIX_AGRMT_ASTAMT");  // 확정_약정보조금
				
				//log.debug("acc_mtch_amt : " + accMtchAmt.toString());
				fixAgrmtAstAmt = fixAgrmtAstAmt.abs();
				if ( fixAgrmtAstAmt.compareTo(new BigDecimal("0")) == 0 ) {
					logMng.writeLogFile("대리점[" + agency + "] : [정산처[" + accPlc + "] : 확정_약정보조금[fix_agrmt_astamt] : " + fixAgrmtAstAmt.toString());
				}

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						fixAgrmtAstAmt.toString()
				);
				mHead.put("ACC_PLC", accPlc);
				alHead.add(mHead);

				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							accPlc,
							fixAgrmtAstAmt.toString(),
							(String)mCmmsAcc.get("UKEY_SUB_CD"))
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
			mCmmsAcc.put("ZIFDATE", mKey.get("ZIFDATE"));
			mCmmsAcc.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08130_NEW.updateOaCmmsAccSendInfo", mCmmsAcc);  /* 도매 수수료_월정산  */

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		}
	}
	/**
	 * (월)확정_할부매출
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendFixAllotSaleAmt(SqlMapClient sqlMap) throws Exception {

		//수수료월정산 row map
		Map mCmmsAcc = null;
		BigDecimal fixAllotSaleAmt = null;	//확정_할부매출
		String lastDay = "";

		for (int idx = 0; idx < lstAgencyList.size(); idx++) {
			mAgency = (Map)lstAgencyList.get(idx);

			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);

			requestMap.put("AGENCY", agency);
			requestMap.put("FIX_DT", fixDt);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08130_NEW.getFixAllotSaleAmtList", requestMap);
			if ( lstAmtInfo.size() == 0 ) {
				remarkMsg = "(월)확정_할부매출 ERP전송 건이 존재하지 않습니다.";
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

			//입금처리조회 건수 만큼  Item map 추가
			alHead = new ArrayList();
			alItem = new ArrayList();

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCmmsAcc = (Map)lstAmtInfo.get(i);
				accPlc = (String)mCmmsAcc.get("ACC_PLC");

				fixAllotSaleAmt = (BigDecimal)mCmmsAcc.get("FIX_ALLOT_SALE_AMT");  // 확정_할부매출
				
				//log.debug("acc_mtch_amt : " + accMtchAmt.toString());
				fixAllotSaleAmt = fixAllotSaleAmt.abs();
				if ( fixAllotSaleAmt.compareTo(new BigDecimal("0")) == 0 ) {
					logMng.writeLogFile("대리점[" + agency + "] : [정산처[" + accPlc + "] : 확정_할부매출[fix_allot_sale_amt] : " + fixAllotSaleAmt.toString());
				}

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						fixAllotSaleAmt.toString()
				);
				mHead.put("ACC_PLC", accPlc);
				alHead.add(mHead);

				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);
					//set item info
					alItem.add(ErpCommon.makeItem(mHead,
							String.valueOf(j+1),
							mJourStd,
							accPlc,
							fixAllotSaleAmt.toString(),
							(String)mCmmsAcc.get("UKEY_SUB_CD"))
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
			mCmmsAcc.put("ZIFDATE", mKey.get("ZIFDATE"));
			mCmmsAcc.put("ZCONFIRM", mKey.get("ZCONFIRM"));
			sqlMap.update("SACERP08130_NEW.updateOaCmmsAccSendInfo", mCmmsAcc);  /* 도매 수수료_월정산  */

			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		}
	}	
}

