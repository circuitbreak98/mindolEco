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
public class SACERP08310 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08310";

	private SAP sap = null;

	private List lstAgency		= null;		//대리점목록
	private Map mAgency = null;		//대리점 map
	
	private Map dealMap = null;		//거래처 map
	
	private String agency		= "";		//대리점 거래처코드
	private String result		= "";		//직영점, 카드사
	
	private Map<String, Object> imParams = null;
	private Map<String, Object> imTables = null;
	private Map<String, Object> retMap = null;	//전송결과 map

	private Map<String, Object> requestMap = new HashMap<String, Object>();

	/**
	 * 매출발생 배치 프로그램을 수행한다.
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
			String dpstYM = "";
			if( request.size() > 1 ) {
				dpstYM	= (String)request.get("args1");
				
				dpstYM = dpstYM.equals("?") ? "" : dpstYM;
				
				logMng.writeLogFile("args1[조회년월] : " + dpstYM);
			}
			
			if (dpstYM.equals("")) {
				dpstYM = ErpCommon.getLastMonth();	//조회년월(전월)
			}
			String lastDay = ErpCommon.getLastDay(Integer.parseInt(dpstYM.substring(0, 4)),
					Integer.parseInt(dpstYM.substring(4)) );
			
			log.debug("dpstYM(조회년월) : " + dpstYM);
			requestMap.put("YYMM",	dpstYM);
			
			sendVendorList(sqlMap);
			
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
	 * (월)거래처 점검
	 * @author	이영진 (nofixing)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendVendorList(SqlMapClient sqlMap) throws Exception {

		sqlMap.delete("SACERP08310.deleteDealList", requestMap);
		
		lstAgency = (List)sqlMap.queryForList("SACERP08310.getDealList", requestMap);
		
		//log.debug("lstAgency : " + lstAgency.toString());
		
		ArrayList alKey 	= new ArrayList();
		
		dealMap = new HashMap();		//거래처map
		
		for (int idx = 0; idx < lstAgency.size(); idx++) {
			mAgency = (Map)lstAgency.get(idx);
			//dealMap.put("RESULT", "2");	//결과값
			//dealMap.put("LIFNR", (String)mAgency.get("LIFNR"));
			alKey.add(mAgency);
		}

		imParams = new HashMap<String, Object>();
		imTables = new HashMap<String, Object>();
		//log.debug("alKey : " + alKey.toString());
		imTables.put("ITAB",	alKey.toArray());
		retMap = sap.executeRFC("ZIF_VENDOR_STATUS", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//⑤ 결과 데이터 처리
		ArrayList<Object> alKey2	= (ArrayList<Object>)retMap.get("ITAB");
		//log.debug("alKey2 : " + alKey2.toString());
		Map rowMap	= null;
		Map rtnMap	= new HashMap();
		String lifnr = "";
		for (int i = 0; i < alKey2.size(); i++) {
			rowMap	= (Map<String, Object>)alKey2.get(i);
			lifnr = (String)rowMap.get("LIFNR");
			if(lifnr.length() > 5) lifnr = lifnr.substring(5);
			rtnMap.put("LIFNR", lifnr);
			rtnMap.put("RESULT", (String)rowMap.get("RESULT"));
			//log.debug("rtnMap : " + rtnMap.toString());
			sqlMap.insert("SACERP08310.insertDeal", rtnMap);
		};
		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();

	}
}
