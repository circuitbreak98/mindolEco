package com.sktst.batch.bas.usm.biz;

import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준정보</li>
 * <li>설 명 : GroupWare DB정보를 PS DB에 반영한다.</li>
 * <li>작성일 : 2009-05-01 16:00:00</li>
 * </ul>
 *
 * @author 하창주 (hachangjoo)
 */
public class BASUSM08110 extends AbsBatchJobBiz {
	private static final String PROG_ID = "BASUSM08110";

	private List lstInfCommonCode	= null;		//인사공통코드
	private List lstInfDeptInfo		= null;		//조직정보
	private List lstInfOtherJob		= null;		//겸직발령정보
	private List lstInfDispatchInfo	= null;		//파견발령정보
	private List lstinfPersonInfo	= null;		//개인별인사정보
	
	private String yesterday = "";

	/**
	 * GroupWare DB정보를 PS DB에 반영 배치 프로그램을 수행한다.
	 * @author	하창주 (hachangjoo)
	 * @param	Map request : request
	 * @return	int : 수행결과(0이면 정상, 아니면 비정상)
	 * @throws	Exception
	 */	
	public int execute(Map request) throws Exception {
		super.getProperties();
		
		//① DB 정보 취득 (SqlMapClient 인스턴스 취득)
		//SqlMapClient sqlMap = getSqlMapClient();
		//Group Ware DB 정보 취득 : SqlMapConfigGW.xml 참조
		Reader reader = null;
		SqlMapClient sqlMap = null;
		
		try {
			reader = Resources.getResourceAsReader("SqlMapConfigGW.xml");
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
			
			logMng.openLogFile(PROG_ID);

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
				yesterday = (String)request.get("args1");
				logMng.writeLogFile("args1 : " + yesterday);
				log.debug("yesterday : " + yesterday);
			}

			//parameter 로 반영일이 없을 경우 어제 날자로 기본 setting
			if (yesterday.equals("")) {
				Calendar cal = new GregorianCalendar();
				cal.add(Calendar.DATE , -1);
				Date d = cal.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				yesterday = sdf.format(d);
				logMng.writeLogFile("yesterday : " + yesterday);
			}

			Map<String, Object> requestMap = new HashMap<String, Object>();
			//requestMap.put("send_dt", "20090506");
			requestMap.put("send_dt", yesterday);
			
			//④ DB 처리
			//인사공통코드 조회
			lstInfCommonCode	= getInfTbDataList(sqlMap, "getInfCommonCodeList", requestMap);
			lstInfDeptInfo		= getInfTbDataList(sqlMap, "getInfDeptInfoList", requestMap);
			lstInfOtherJob		= getInfTbDataList(sqlMap, "getInfOtherJobList", requestMap);
			lstInfDispatchInfo	= getInfTbDataList(sqlMap, "getInfDispatchInfoList", requestMap);
			lstinfPersonInfo	= getInfTbDataList(sqlMap, "getInfPersonInfoList", requestMap);
			//sqlMap.update("BASUSM08110.updateAplyYn", requestMap);
log.debug("%%%%% 11 ");
			
			//⑤ 결과 데이터 처리
			//put the data in PS DB
			putData();
			
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
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
			logMng.closeLogFile();
			
			if (reader != null) {
				safeClose(reader);
			}
		}
		
		//⑦ 처리 결과 코드 리턴
		return 0;
	}
	
	/**
	 * 리소스 해제
	 * @author	하창주 (hachangjoo)
	 * @param	Reader reader : request
	 * @return	void
	 * @throws	
	 */	
	public void safeClose(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch(IOException ioe) {
				logMng.writeLogFile("ERRCODE:F");
				logMng.writeLogFile("execute Exception : " + ioe.getMessage());
			}
		}
	}

	/**
	 * 그룹웨어 interface table 조회
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 *			String id : sqlMap id 
	 * @return	resultList : 결과 list
 	 * @throws	Exception
	 */
	private List getInfTbDataList(SqlMapClient sqlMap, String id, Map<String, Object> requestMap) throws Exception {
		List resultList = (List)sqlMap.queryForList("BASUSM08110." + id, requestMap);
		int selectCnt = resultList.size();
		logMng.writeLogFile(id + " : " + selectCnt + " 건 조회");
		
		/*
		Map rowMap = null;
		for (int i = 0; i < selectCnt; i++) {
			rowMap = (Map)resultList.get(i);
			if (log.isDebugEnabled()) {
				log.debug("row[" + i + "] : " + rowMap.toString());
			}
		}
		*/

		return resultList;
	}

	/**
	 * put the data in PS DB
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 *			String id : sqlMap id 
	 * @return	resultList : 결과 list
 	 * @throws	Exception
	 */
	private void putData() throws Exception {
		SqlMapClient sqlMap = getSqlMapClient();
		
		//② Transaction 시작
		sqlMap.startTransaction();

log.debug("### 1");
		//④ DB 처리
		//인사공통코드
		deleteInsertData(sqlMap, lstInfCommonCode,	"deleteInfCommonCode",	"insertInfCommonCode");
log.debug("### 2");
		deleteInsertData(sqlMap, lstInfDeptInfo,	"deleteInfDeptInfo",	"insertInfDeptInfo");
log.debug("### 3");
		deleteInsertData(sqlMap, lstInfOtherJob,	"deleteInfOtherJob",	"insertInfOtherJob");
log.debug("### 4");
		deleteInsertData(sqlMap, lstInfDispatchInfo,"deleteInfDispatchInfo","insertInfDispatchInfo");
log.debug("### 5");
		
		int i = 0;
		Map rowMap = null;
		List resultList = null;
		for (i = 0; i < lstinfPersonInfo.size(); i++) {
			rowMap = (Map)lstinfPersonInfo.get(i);
log.debug("### rowMap ["+rowMap);			
			//log.debug("row[" + i + "] : " + rowMap.toString());
			resultList = (List)sqlMap.queryForList("BASUSM08110.getInfPersonInfo", rowMap);
			if ( resultList.size() > 0 ) {
				sqlMap.update("BASUSM08110.updateInfPersonInfo", rowMap);
			} else {
				sqlMap.insert("BASUSM08110.insertInfPersonInfo", rowMap);
			}
		}
		
		//⑤ 결과 데이터 처리

		//⑥-1 Transaction Commit
		sqlMap.commitTransaction();
		sqlMap.endTransaction();
	}

	/**
	 * delete insert the data in PS DB
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	 : SqlMapClient 인스턴스
	 * 			String deleteId : sqlMap id
	 *			String insertId : sqlMap id
	 * @return	resultList : 결과 list
 	 * @throws	Exception
	 */
	private void deleteInsertData(SqlMapClient sqlMap, List list, String deleteId, String insertId) throws Exception {
		
		sqlMap.delete("BASUSM08110." + deleteId);
		Map rowMap = null;
		for (int i = 0; i < list.size(); i++) {
			rowMap = (Map) list.get(i);
log.debug("## insertId ["+insertId);		
log.debug("## rowMap ["+rowMap);		
			sqlMap.insert("BASUSM08110." + insertId, rowMap);
		}
	}

}
