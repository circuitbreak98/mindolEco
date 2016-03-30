package com.sktst.batch.sac.erp.biz;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.mapping.statement.DeleteStatement;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/판매회계</li>
 * <li>설 명 : ERP 계정별 금액 IF. </li>
 * <li>작성일 : 2009-05-18</li>
 * </ul>
 *
 * @author 이강수 (leekangsu)
 */
public class SACERP08600 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08600";
	
	private SAP sap = null;
	private String dealCoCd		= "";	//직영점코드
	private String dtFr	    	= "";	//대상일자 From
	private String tranDt		= "";	//직영점코드
	private String dtTo   		= "";	//대상일자 To
	private String ukeyAgencyCd = "";	//ukey_agency_cd
	private String ukeySubCd   	= "";	//UKEY_SUB_CD

	/**
	 * ERP 계정별 금액 IF 배치 프로그램을 수행한다.
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
				log.debug(PROG_ID + ".execute");
				log.debug(request.toString());
			}
			
			//② Transaction 시작
			sqlMap.startTransaction();
			
			//③ 요청 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");
		
			if( request.size() > 1 ) {
				tranDt	= (String)request.get("args1");
				tranDt = tranDt.equals("?") ? "" : tranDt;


				logMng.writeLogFile("args1[거래일자] : " + tranDt);
			}
			if (tranDt.equals("")) {
				tranDt = ErpCommon.getDayInterval(-1);	//거래일자(어제)
				logMng.writeLogFile("거래일자[tranDt] : " + tranDt);
			}
			
			//④ DB 처리
			Map dataMap = new HashMap();
			dataMap.put("TRAN_DT", tranDt);
			
			String accYm    = null;
			
			if(tranDt != null){
				accYm = tranDt.substring(0,6);
			}
			
			List dealCoList  = (List)sqlMap.queryForList("SACERP08600.getDealCoCd", dataMap);

			//ERP전송
			for (int i = 0; i < dealCoList.size(); i++) {
				dataMap = (Map) dealCoList.get(i);
				
                // 이전데이터 삭제
				dataMap.put("ACC_MTH"    , accYm);
				sqlMap.delete("SACERP08600.deleteTaccRetailPl", dataMap);
				
				// ERP 계정 금액 처리
				sendZifCostCompute(sqlMap, dataMap);

				// 매출 금액 처리
				insertSalesData(sqlMap, dataMap);

			}

			
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:3");
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
	 * ERP 계정별 금액 
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
 	 * @throws	Exception
	 */
	private void sendZifCostCompute(SqlMapClient sqlMap, Map dataMap) throws Exception {
		Map<String, Object> imParams = new HashMap<String, Object>();
		Map<String, Object> imTables = new HashMap<String, Object>();

		//Erp Head table row map
		Map mHead = null;
		Map<String, Object> retMap = null;
		
		imParams.put("P_ZGSBER",	dataMap.get("UKEY_AGENCY_CD"));
		imParams.put("P_ZGCODE",	dataMap.get("UKEY_SUB_CD"));
		imParams.put("P_CPUDT" ,	dataMap.get("TRAN_DT"));
		
		Map responseMap = new HashMap();
		List arrayList  = null;
		String accYm    = (String) dataMap.get("TRAN_DT");
		String accCd    = null;
		String accCdHi  = null;
		String accCdMi  = null;
		
		BigDecimal accAmt = null;
		
		if(accYm != null){
			accYm = accYm.substring(0,6);
		}
		
		/*
		 *   ZIF_COST_COMPUTE_NEW : 계정별 ERP 잔액
		 */
		responseMap = sap.executeRFC("ZIF_COST_COMPUTE_NEW", imParams, imTables);
		//ErpCommon.writeLogResultMsg(retMap, logMng);

		if (log.isDebugEnabled()) {
			Map arrayMap =  new HashMap();
			arrayList   = (List) responseMap.get("T_COST");

			for (int i = 0; i < arrayList.size(); i++) {
				arrayMap = new HashMap();
				arrayMap = (Map)arrayList.get(i);
				
                arrayMap.put("ACC_MTH"    , accYm);
                arrayMap.put("AGENCY_CD"  , dataMap.get("POS_AGENCY"));
                arrayMap.put("DEAL_CO_CD" , dataMap.get("DEAL_CO_CD"));
                arrayMap.put("ACC_CD"     , arrayMap.get("ZHKONT"));
                arrayMap.put("ACC_AMT"    , arrayMap.get("DMBTR"));
				arrayMap.put("USER_ID"    , "SACERP0860");
				
				//⑤ 결과 데이터 처리

				sqlMap.insert("SACERP08600.insertTaccRetailPl", arrayMap);
				
				if("500010".equals(arrayMap.get("ACC_CD")) || "0000500010".equals(arrayMap.get("ACC_CD"))){
					arrayMap = new HashMap();
					arrayMap = (Map)arrayList.get(i);
					accAmt   =  new BigDecimal((String) arrayMap.get("ACC_AMT"));
					accAmt   =  accAmt.divide(new BigDecimal("12"), 0, BigDecimal.ROUND_DOWN);
	                arrayMap.put("ACC_MTH"    , accYm);
	                arrayMap.put("AGENCY_CD"  , dataMap.get("POS_AGENCY"));
	                arrayMap.put("DEAL_CO_CD" , dataMap.get("DEAL_CO_CD"));
	                arrayMap.put("ACC_CD"     , "0000500040");  // 퇴직충당금
	                arrayMap.put("ACC_AMT"    , accAmt);
					arrayMap.put("USER_ID"    , "SACERP0860");
					sqlMap.insert("SACERP08600.insertTaccRetailPl", arrayMap);
				}
			}
 		}
	}
	/**
	 * 매출 금액 
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
 	 * @throws	Exception
	 */
	private void insertSalesData(SqlMapClient sqlMap, Map dataMap) throws Exception {
		Map<String, Object> imParams = new HashMap<String, Object>();
		Map<String, Object> imTables = new HashMap<String, Object>();

		//Erp Head table row map
		Map mHead = null;
		Map<String, Object> retMap = null;
		String accYm    = (String) dataMap.get("TRAN_DT");

		if(accYm != null){
			accYm = accYm.substring(0,6);
		}
		
		dataMap.put("ACC_MTH"    , accYm);
		dataMap.put("AGENCY_CD"  , dataMap.get("POS_AGENCY"));
		dataMap.put("USER_ID"    , "SACERP0860");


		BigDecimal eqpCash      = new BigDecimal("0");
		BigDecimal phoneSafeAmt = new BigDecimal("0");

		/*
		 *   0000A00051 : 폰세이프 매출
		 *   2011-05-30 이현석M 삭제 요청
		 */
//		if(dataMap.get("PHONE_SAFE_AMT") != null){
//			dataMap.put("ACC_CD"      , "0000A00051");
//			dataMap.put("ACC_AMT"     , dataMap.get("PHONE_SAFE_AMT"));
//			phoneSafeAmt = (BigDecimal) dataMap.get("PHONE_SAFE_AMT");
//
//log.debug("#### PHONE_SAFE_AMT ["+phoneSafeAmt);			
//			sqlMap.insert("SACERP08600.insertTaccRetailPl", dataMap);
//		}
//		
		/*
		 *   0000A00011 : 단말기현금매출 
		 */
		if(dataMap.get("EQP_CASH") != null){
			dataMap.put("ACC_CD"     , "0000A00011");
			dataMap.put("ACC_AMT"    , dataMap.get("EQP_CASH"));
			eqpCash = (BigDecimal) dataMap.get("EQP_CASH");
//			eqpCash = eqpCash.subtract(phoneSafeAmt);
//
//log.debug("#### EQP_CASH ["+phoneSafeAmt);			
//            eqpCash = eqpCash.subtract(phoneSafeAmt);
//
//log.debug("#### EQP_CASH ["+phoneSafeAmt);			
			sqlMap.insert("SACERP08600.insertTaccRetailPl", dataMap);
		}

		/*
		 *   0000A00012 : 단말기할부매출 
		 */
		if(dataMap.get("EQP_ALLOT") != null){
			dataMap.put("ACC_CD"     , "0000A00012");
			dataMap.put("ACC_AMT"    , dataMap.get("EQP_ALLOT"));

			sqlMap.insert("SACERP08600.insertTaccRetailPl", dataMap);
		}

		/*
		 *   0000A00021 : USIM 현금매출 
		 */
		if(dataMap.get("USIM_CASH") != null){
			dataMap.put("ACC_CD"     , "0000A00021");
			dataMap.put("ACC_AMT"    , dataMap.get("USIM_CASH"));

			sqlMap.insert("SACERP08600.insertTaccRetailPl", dataMap);
		}

		/*
		 *   0000A00022 : USIM 할부매출 
		 */
		if(dataMap.get("USIM_ALLOT") != null){
			dataMap.put("ACC_CD"     , "0000A00022");
			dataMap.put("ACC_AMT"    , dataMap.get("USIM_ALLOT"));

			sqlMap.insert("SACERP08600.insertTaccRetailPl", dataMap);
		}

		/*
		 *   0000A00031 : 보조금 매출
		 */
		if(dataMap.get("AGRMT_ASTAMT") != null){
			dataMap.put("ACC_CD"     , "0000A00031");
			dataMap.put("ACC_AMT"    , dataMap.get("AGRMT_ASTAMT"));

			sqlMap.insert("SACERP08600.insertTaccRetailPl", dataMap);
		}

		/*
		 *   0000A00041 : SKT 장려금 매출
		 */
		if(dataMap.get("SKT_AMT") != null){
			dataMap.put("ACC_CD"     , "0000A00041");
			dataMap.put("ACC_AMT"    , dataMap.get("SKT_AMT"));

			sqlMap.insert("SACERP08600.insertTaccRetailPl", dataMap);
		}

		/*
		 *   0000A00042 : SKN 장려금 매출
		 */
		if(dataMap.get("SKN_AMT") != null){
			dataMap.put("ACC_CD"     , "0000A00042");
			dataMap.put("ACC_AMT"    , dataMap.get("SKN_AMT"));

			sqlMap.insert("SACERP08600.insertTaccRetailPl", dataMap);
		}

		/*
		 *   0000A00043 : 제조사장려금 매출
		 */
		if(dataMap.get("ETC_AMT") != null){
			dataMap.put("ACC_CD"     , "0000A00043");
			dataMap.put("ACC_AMT"    , dataMap.get("ETC_AMT"));

			sqlMap.insert("SACERP08600.insertTaccRetailPl", dataMap);
		}
		
		/*
		 *   0000A00061 : 상품매출원가(단말기+USIM)
		 */
		if(dataMap.get("EQP_PRC") != null){
			dataMap.put("ACC_CD"     , "0000A00061");
//			dataMap.put("ACC_AMT"    , dataMap.get("DIS_PRC"));  // 재고기준 금액
			dataMap.put("ACC_AMT"    , dataMap.get("EQP_PRC"));  // 운영모델 단가표 기준 금액

			sqlMap.insert("SACERP08600.insertTaccRetailPl", dataMap);
		}

		/*
		 *   0000A00001 : 개통실적
		 */
		if(dataMap.get("SALE_CNT") != null){
			dataMap.put("ACC_CD"     , "0000A00001");
			dataMap.put("ACC_AMT"    , dataMap.get("SALE_CNT"));  // 개통실적

			sqlMap.insert("SACERP08600.insertTaccRetailPl", dataMap);

		}
		
	}	
}

