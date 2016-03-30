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
public class SACERP08170 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08170";
	
	private SAP sap = null;
	
	private List lstJourStdInfo	= null;		//분개기준정보
	private List lstAmtInfo		= null;		//금액정보
	private List lstAgency		= null;		//대리점목록
	
	private String zbudat 		= "";		//귀속일자
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

	private Map<String, Object> requestMap = new HashMap<String, Object>();
	private Map<String, Object> retMap = null;	//전송결과 map
	private Map<String, Object> mKey = null;

	/**
	 * (월)매출원가 배치 프로그램을 수행한다.
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
			if( request.size() > 1 ) {
				yymm	= (String)request.get("args1");
				pAgency	= (String)request.get("args2");
				pDealCo	= (String)request.get("args3");
				pZgubun	= (String)request.get("args4");
				
				yymm = yymm.equals("?") ? "" : yymm;
				pAgency = pAgency.equals("?") ? "" : pAgency;
				pDealCo = pDealCo.equals("?") ? "" : pDealCo;
				pZgubun = pZgubun.equals("?") ? "" : pZgubun;

				logMng.writeLogFile("args1[정산월] : " + yymm);
				logMng.writeLogFile("args2[대리점] : " + pAgency);
				logMng.writeLogFile("args3[거래처] : " + pDealCo);
				logMng.writeLogFile("args4[전송구분] : " + pZgubun);
			}
			
			if (yymm.equals("")) {
				yymm = ErpCommon.getLastMonth();	//정산월(전월)
			}
			String lastDay = ErpCommon.getLastDay(Integer.parseInt(yymm.substring(0, 4)),
					Integer.parseInt(yymm.substring(4)) );
			zbudat = yymm + lastDay;	//귀속일자
			budat = ErpCommon.getDayInterval(zbudat, 1); // 전기일자

			/**
			 *   회계팀 확정후 삭제예정
			 */
			budat = zbudat; // 전기일자
			
			log.debug("yymm(정산월) : " + yymm);
			log.debug("zbudat(귀속일자) : " + zbudat);
			log.debug("budat(전기일자) : " + budat);

			requestMap.put("YYMM",			yymm);
			requestMap.put("AGENCY",		pAgency);
			requestMap.put("DEAL_CO_CD",	pDealCo);
			requestMap.put("TRMS_ITEM",		pZgubun);

			//④ DB 처리
			/** 
			 *  G01 : ZGUBUN_DIS_SALE_AMT      : (월)매출원가
			 *  G03 : ZGUBUN_DIS_MOV_OUT       : (월)재고이동 - 출고
			 *  G04 : ZGUBUN_DIS_MOV_IN        : (월)재고이동 - 입고
			 */
			if ( "".equals(pZgubun) ) {
				logMng.writeLogFile("(월)매출원가 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DIS_SALE_AMT);
				sendDisSsaleAmt(sqlMap);

				logMng.writeLogFile("(월)재고이동 - 출고 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DIS_MOV_OUT);
				sendDisMovOut(sqlMap);

				logMng.writeLogFile("(월)재고이동 - 입고 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DIS_MOV_IN);
				sendDisMovIn(sqlMap);

			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DIS_SALE_AMT) ) {
				logMng.writeLogFile("(월)매출원가 - SKN send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DIS_SALE_AMT);
				sendDisSsaleAmt(sqlMap);

			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DIS_MOV_OUT) ) {
				logMng.writeLogFile("(월)재고이동 - 출고 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DIS_MOV_OUT);
				sendDisMovOut(sqlMap);

			} else if ( pZgubun.equals(ErpCommon.ZGUBUN_DIS_MOV_IN) ) {
				logMng.writeLogFile("(월)재고이동 - 입고 send >>> ");
				lstJourStdInfo	= ErpCommon.getTsacJourStdInfoList(sqlMap, ErpCommon.ZGUBUN_DIS_MOV_IN);
				sendDisMovIn(sqlMap);

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
	 * (월)매출원가 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendDisSsaleAmt(SqlMapClient sqlMap) throws Exception {

		//매출원가 row map
		Map mPrchsDebtAcc = null;
		BigDecimal saleAmt	= null;		//매출원가
		String dpstYm = (String) requestMap.get("YYMM");

		lstAgency = (List)sqlMap.queryForList("SACERP08170.getAgencyListTmSaleAmt", requestMap);
		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)매출원가 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}
		
		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency	= (String)mAgency.get("ORG_ID");
			userCd	= ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd	= ErpCommon.fillZeroFront(userCd, 8);
			
			requestMap.put("AGENCY", agency);
			//(월)매출원가 조회
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08170.getTmSaleAmt", requestMap);

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
			
				saleAmt	= (BigDecimal)mPrchsDebtAcc.get("SALE_AMT");

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						saleAmt.toString()
				);
				mHead.put("ACC_PLC", agency);	//정산처 : 대리점
				alHead.add(mHead);

				//set item info
				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);

					alItem.add(ErpCommon.makeItem(
							mHead,
							String.valueOf(j+1),
							mJourStd,
							agency,
							saleAmt.toString(),
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
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
		}
	}


	/**
	 * (월)재고이동 - 출고 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendDisMovOut(SqlMapClient sqlMap) throws Exception {
		
		//매출원가 row map
		Map mPrchsDebtAcc = null;
		BigDecimal disMovOutAmt	= null;		//재고이동출고금액
		String dpstYm = (String) requestMap.get("YYMM");

		lstAgency = (List)sqlMap.queryForList("SACERP08170.getAgencyListDisMovOutAmt", requestMap);
		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)재고이동 - 출고 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}
		
		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency	= (String)mAgency.get("ORG_ID");
			userCd	= ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd	= ErpCommon.fillZeroFront(userCd, 8);
			
			requestMap.put("AGENCY", agency);
			//(월)재고이동출고금액 조회
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08170.getDisMovOutAmt", requestMap);

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
			
				disMovOutAmt = (BigDecimal)mPrchsDebtAcc.get("DIS_MOV_OUT_AMT");

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						disMovOutAmt.toString()
				);
				mHead.put("ACC_PLC", agency);	//정산처 : 대리점
				alHead.add(mHead);

				//set item info
				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);

					alItem.add(ErpCommon.makeItem(
							mHead,
							String.valueOf(j+1),
							mJourStd,
							agency,
							disMovOutAmt.toString(),
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
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
		}
	}



	/**
	 * (월)재고이동 - 입고 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendDisMovIn(SqlMapClient sqlMap) throws Exception {
		
		//매출원가 row map
		Map mPrchsDebtAcc = null;
		BigDecimal disMovInAmt	= null;		//재고이동입고금액
		String dpstYm = (String) requestMap.get("YYMM");

		lstAgency = (List)sqlMap.queryForList("SACERP08170.getAgencyListDisMovInAmt", requestMap);
		if ( lstAgency.size() == 0 ) {
			remarkMsg = "(월)재고이동 - 입고 ERP전송 건이 존재하지 않습니다.";
			ErpCommon.updateTsacErpTrms(sqlMap, requestMap, remarkMsg);
			return;
		}
		
		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency	= (String)mAgency.get("ORG_ID");
			userCd	= ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd	= ErpCommon.fillZeroFront(userCd, 8);
			
			requestMap.put("AGENCY", agency);
			//(월)매출원가 조회
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08170.getDisMovInAmt", requestMap);

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
			
				disMovInAmt	= (BigDecimal)mPrchsDebtAcc.get("DIS_MOV_IN_AMT");

				//set head info
				mHead = ErpCommon.makeHead((Map<String, Object>)oKey[0],
						String.valueOf(i+1),
						(String)((Map)lstJourStdInfo.get(0)).get("ACC_TYP"),
						disMovInAmt.toString()
				);
				mHead.put("ACC_PLC", agency);	//정산처 : 대리점
				alHead.add(mHead);

				//set item info
				for (int j = 0; j < lstJourStdInfo.size(); j++) {
					mJourStd = (Map)lstJourStdInfo.get(j);

					alItem.add(ErpCommon.makeItem(
							mHead,
							String.valueOf(j+1),
							mJourStd,
							agency,
							disMovInAmt.toString(),
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
			ErpCommon.insertAccTrTable(sqlMap, imTables, retMap);
		}
	}
}

