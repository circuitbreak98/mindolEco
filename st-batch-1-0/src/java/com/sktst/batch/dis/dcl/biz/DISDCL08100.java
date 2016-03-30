package com.sktst.batch.dis.dcl.biz;

import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : 상품재고 월 마감 </li>
 * <li>작성일 : 2009-08-14</li>
 * </ul>
 *
 * @author 이정현 (leejunghyun)
 */
public class DISDCL08100 extends AbsBatchJobBiz {

	private static final String PROG_ID = "DISDCL08100";

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 이정현 (leejunghyun)
	 *
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	public int execute(Map request) throws Exception {
		super.getProperties();
		
		//① DB 정보 취득 (SqlMapClient 인스턴스 취득)
		SqlMapClient sqlMap = getSqlMapClient();

		try {
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			logMng.openLogFile(PROG_ID);

			//로그 출력 (AbsBatchJobBiz 클래스에서 제공하는 변수 log를 사용)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute");
			}

			if (log.isDebugEnabled()) {
				log.debug(request.toString());
			}

			String sProcDt = "";
			String sActFlag = "";
			if( request.size() > 1 ) {
				sProcDt = (String)request.get("args1");
				sActFlag = (String)request.get("args2");
				log.debug(sProcDt + ".sProcDt");
				log.debug(sActFlag + ".sActFlag");
			}

			//② Transaction 시작
			sqlMap.startTransaction();

			//③ 요청 데이터 처리, ④ DB 처리, ⑤ 결과 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");

			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("OP_ACTFLAG", sActFlag);
			requestMap.put("OP_PROCDT", sProcDt);
			requestMap.put("OP_USER", PROG_ID.substring(0, 10));
			requestMap.put("ERRCODE", "");
			requestMap.put("ERRMSG", "");
			// 프로시저 호출
			if(log.isDebugEnabled()){
				log.debug("DISDCL08100.execute.callSP_DISDCL08102");
				log.debug("requestMap.toString()"+requestMap.toString());
			}
			
			log.debug("### 마감월     : ["+sProcDt+ "]");
			log.debug("### 처리구분  : ["+sActFlag+ "]");
			log.debug("### 처리자     : ["+PROG_ID.substring(0, 10)+ "]");

			
			
			
			/**
			 *   200905월 마감처리는 불가능 하다. - 처음 데이터를 생성한 월 임
			 *   200912월 마감은 별도의 마감처리를 한다.
			 */
			if("200915".equals(sProcDt)){
				log.debug("#### 구성품재고 마감 처리할 수 없는 월 입니다. ####");
			}else if("200912".equals(sProcDt)){
				sqlMap.queryForObject("DISDCL08100.callSP_DISDCL08107", requestMap);
			}else{
				sqlMap.queryForObject("DISDCL08100.callSP_DISDCL08106", requestMap);
			}
			
			if(requestMap.get("ERRMSG") != null)  {
				logMng.writeLogFile("ERRCODE:"+requestMap.get("ERRCODE").toString()
						+"/ERRMSG:" + requestMap.get("ERRMSG").toString());
			}
			
			/**
			 *   200905월 마감처리는 불가능 하다. - 처음 데이터를 생성한 월 임
			 *   200912월 마감은 별도의 마감처리를 한다.
			 */
			if("200915".equals(sProcDt)){
				log.debug("#### 상품재고 마감 처리할 수 없는 월 입니다. ####");
			}else if("200912".equals(sProcDt)){
				sqlMap.queryForObject("DISDCL08100.callSP_DISDCL08103", requestMap);
			}else{
				sqlMap.queryForObject("DISDCL08100.callSP_DISDCL08102", requestMap);
			}
			
			if(requestMap.get("ERRMSG") != null)  {
				logMng.writeLogFile("ERRCODE:"+requestMap.get("ERRCODE").toString()
						+"/ERRMSG:" + requestMap.get("ERRMSG").toString());
			}
			
			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}

			//⑥-1 Transaction Commit
			log.debug("#### 처리완료. ####");
			sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("Transaction Exception = [" + e + "]");
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
			}
		} finally {
			//⑥-2 Transaction rollback (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			sqlMap.endTransaction();
		}

		logMng.closeLogFile();

		//⑦ 처리 결과 코드 리턴
		return 0;
	}
}