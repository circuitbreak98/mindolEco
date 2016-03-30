package com.sktst.batch.sac.erp.biz;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.mapping.statement.DeleteStatement;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/판매회계</li>
 * <li>설 명 : ERP 비용 IF. </li>
 * <li>작성일 : 2009-05-18</li>
 * </ul>
 *
 * @author 이강수 (leekangsu)
 */
public class SACERP08400 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08400";
	
	private SAP sap = null;
	private String dealCoCd		= "";	//직영점코드
	private String dtFr	    	= "";	//대상일자 From
	private String tranDt		= "";	//직영점코드
	private String dtTo   		= "";	//대상일자 To
	private String ukeyAgencyCd = "";	//ukey_agency_cd
	private String ukeySubCd   	= "";	//UKEY_SUB_CD

	/**
	 * ERP 비용 IF 배치 프로그램을 수행한다.
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
//			if( request.size() > 1 ) {
//				dealCoCd	= (String)request.get("args1");
//				dtFr	 	= (String)request.get("args2");
//				dtTo		= (String)request.get("args3");
//
//				dealCoCd = dealCoCd.equals("?") ? "" : dealCoCd;
//				dtFr = dtFr.equals("?") ? "" : dtFr;
//				dtTo = dtTo.equals("?") ? "" : dtTo;
//
//				logMng.writeLogFile("args1[직영점] : " + dealCoCd);
//				logMng.writeLogFile("args2[대상일자Fr] : " + dtFr);
//				logMng.writeLogFile("args3[대상일자To] : " + dtTo);
//			}
//
//			if (dealCoCd.equals("")) {
//				logMng.writeLogFile("직영점코드가 입력되지 않았습니다.");
//				log.debug("직영점코드가 입력되지 않았습니다.");
//				return 1;
//			}
//
//			if (dtFr.equals("")) {
//				logMng.writeLogFile("대상일자 From가 입력되지 않았습니다.");
//				log.debug("대상일자 From가 입력되지 않았습니다.");
//				return 1;
//			} else if (dtFr.length() != 8) {
//				logMng.writeLogFile("args1[대상일자 From] : " + dtFr + " 입력이 올바르지 않습니다.(ex. 200901)");
//				log.debug("args1[대상일자 From] : " + dtFr + " 입력이 올바르지 않습니다.(ex. 200901)");
//				return 1;
//			}
//
//			if (dtTo.equals("")) {
//				logMng.writeLogFile("대상일자 To가 입력되지 않았습니다.");
//				log.debug("대상일자 To가 입력되지 않았습니다.");
//				return 1;
//			} else if (dtTo.length() != 8) {
//				logMng.writeLogFile("args1[대상일자 To] : " + dtTo + " 입력이 올바르지 않습니다.(ex. 20101001)");
//				log.debug("args1[대상일자 To] : " + dtTo + " 입력이 올바르지 않습니다.(ex. 20101001)");
//				return 1;
//			}			
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
			List dealCoList  = (List)sqlMap.queryForList("SACERP08400.getDealCoCd", dataMap);

			//ERP전송
			for (int i = 0; i < dealCoList.size(); i++) {
				dataMap = (Map) dealCoList.get(i);
				
				sendZifCostCompute(sqlMap, dataMap);
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
	 * ERP비용 조회
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
		responseMap = sap.executeRFC("ZIF_COST_COMPUTE", imParams, imTables);
		//ErpCommon.writeLogResultMsg(retMap, logMng);
       
		if (log.isDebugEnabled()) {
			Map arrayMap =  new HashMap();
			arrayList   = (List) responseMap.get("T_COST");
//log.debug("retMap : " + arrayList.size());
			for (int i = 0; i < arrayList.size(); i++) {
				arrayMap = new HashMap();
				arrayMap = (Map)arrayList.get(i);
                arrayMap.put("COST_CL"    , arrayMap.get("ZGJOB"));
                arrayMap.put("COST_AMT"   , arrayMap.get("DMBTR"));
                arrayMap.put("DEAL_CO_CD" , dataMap.get("DEAL_CO_CD"));
                arrayMap.put("TRAN_DT"    , dataMap.get("TRAN_DT"));
				arrayMap.put("USER_ID"    , "SACERP0840");
				
//log.debug("## retMap : " + arrayMap);
				//⑤ 결과 데이터 처리
				if(i == 0){
					sqlMap.delete("SACERP08400.deleteTsacErpCostIf", arrayMap);
				}
				sqlMap.insert("SACERP08400.insertTsacErpCostIf", arrayMap);
			}
 		}
	}
}

