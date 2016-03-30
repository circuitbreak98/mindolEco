package com.sktst.batch.sac.erp.biz;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/판매회계</li>
 * <li>설 명 : ERP 거래처 리스트 IF. </li>
 * <li>작성일 : 2009-05-18</li>
 * </ul>
 *     ERP : ZFI_LIFNR_KUNNR_LIST  : 거래처 리스트 
 *           IN 
 *               I_BUDAT
 *           OUT
 *               KOART
 *               ZCODE
 *               ZNAME
 *
 * @author 이강수 (leekangsu)
 */
public class SACERP08700 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08700";
	
	private SAP sap = null;
	private String tranDt		= "";	//직영점코드

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
			
			if( request.size() > 1 ) {
				tranDt	= (String)request.get("args1");
				tranDt = tranDt.equals("?") ? "" : tranDt;

				logMng.writeLogFile("args1[거래일자] : " + tranDt);
			}
			if (tranDt.equals("")) {
				tranDt = ErpCommon.getDate();	//거래일자(현재)
				logMng.writeLogFile("거래일자[tranDt] : " + tranDt);
			}
			
			//④ DB 처리
			Map dataMap = new HashMap();
			dataMap.put("TRAN_DT", tranDt);
				
			sendZifLifnrKunnrList(sqlMap, dataMap);

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
	private void sendZifLifnrKunnrList(SqlMapClient sqlMap, Map dataMap) throws Exception {
		Map<String, Object> imParams = new HashMap<String, Object>();
		Map<String, Object> imTables = new HashMap<String, Object>();

		//Erp Head table row map
		imParams.put("I_BUDAT",	   dataMap.get("TRAN_DT"));

		Map  responseMap = new HashMap();
		List arrayList   = null;
		Map  tbasDeal    = null; 
		responseMap = sap.executeRFC("ZFI_LIFNR_KUNNR_LIST", imParams, imTables);

		if (log.isDebugEnabled()) {
			Map arrayMap =  new HashMap();
			arrayList   = (List) responseMap.get("T_RESULT");

			for (int i = 0; i < arrayList.size(); i++) {
				arrayMap = new HashMap();
				arrayMap = (Map)arrayList.get(i);
				
				arrayMap.put("DEAL_CO_CD"  , arrayMap.get("ZCODE"));
                arrayMap.put("INSP_NO"     , arrayMap.get("ZNAME"));
				
				//⑤ 결과 데이터 처리
				if(i == 0){
					sqlMap.delete("SACERP08700.deleteTbasDeal", arrayMap);
				}

				tbasDeal = (Map) sqlMap.queryForObject("SACERP08700.getTbasDeal", arrayMap);
				if(tbasDeal == null){
					sqlMap.insert("SACERP08700.insertTbasDeal", arrayMap);
				}
			}
 		}
	}
}

