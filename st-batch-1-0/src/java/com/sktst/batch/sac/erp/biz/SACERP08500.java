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
 * <li>설 명 : ERP 채권잔액 IF. </li>
 * <li>작성일 : 2011-01-17</li>
 * </ul>
 *
 * @author 이강수 (leekangsu)
 */
public class SACERP08500 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08500";
	
	private SAP sap = null;
	private String tranDt	    	= "";	// 마감월

	/**
	 * ERP 채권잔액 IF 배치 프로그램을 수행한다.
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
				logMng.writeLogFile("args1[처리일자] : " + tranDt);
			}
			if (tranDt.equals("")) {
				tranDt = ErpCommon.getDayInterval(-1);	//처리일자(어제)
				logMng.writeLogFile("처리일자[tranDt] : " + tranDt);
			}
			
			//④ DB 처리
			Map dataMap = new HashMap();
			dataMap.put("TRAN_DT", tranDt);
			sendZifSalesCustBalance(sqlMap, dataMap);

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
	 * 채권잔액 처리
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
 	 * @throws	Exception
 	 * 
 	 * 
	 */
	private void sendZifSalesCustBalance(SqlMapClient sqlMap, Map dataMap) throws Exception {
		Map<String, Object> imParams = new HashMap<String, Object>();
		Map<String, Object> imTables = new HashMap<String, Object>();

		//Erp Head table row map
		Map mHead = null;
		Map<String, Object> retMap = null;
		
		imParams.put("I_BUDAT" ,	dataMap.get("TRAN_DT"));
		
		Map responseMap = new HashMap();
		List arrayList  = null;
		responseMap = sap.executeRFC("ZFI_SALES_CUST_BALANCE", imParams, imTables);
		//ErpCommon.writeLogResultMsg(retMap, logMng);
       
		if (log.isDebugEnabled()) {
			Map arrayMap =  new HashMap();
			arrayList   = (List) responseMap.get("T_RESULT");

			for (int i = 0; i < arrayList.size(); i++) {
				arrayMap = new HashMap();
				arrayMap = (Map)arrayList.get(i);

				arrayMap.put("ACC_YM"      , tranDt.substring(0,6));
				arrayMap.put("ACC_PLC"     , arrayMap.get("KUNNR"));
				arrayMap.put("ACC_BAL_AMT" , arrayMap.get("DMBTR"));
				arrayMap.put("USER_ID"     , "SACERP0850");

				//⑤ 결과 데이터 처리
				if(i == 0){
					sqlMap.delete("SACERP08500.deleteTsacBondAccBalIf", arrayMap);
				} 
				sqlMap.insert("SACERP08500.insertTsacBondAccBalIf", arrayMap);
			}
 		}
	}
}

