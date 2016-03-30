package com.sktst.batch.acc.dcl.biz;

import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/정산</li>
 * <li>설 명 : 입금정산 월마감 </li>
 * <li>작성일 : 2009-06-30</li>
 * </ul>
 *
 * @author 이영진 (leeyoungjin)
 */
public class ACCDCL08250_NEW extends AbsBatchJobBiz {

	private static final String PROG_ID = "ACCDCL08250_NEW";

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 이영진 (leeyoungjin)
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

			String sDate = "";
			String pAgency = "";
			if( request.size() > 1 ) {
				sDate = (String)request.get("args1");
				pAgency	= (String)request.get("args2");
				log.debug(sDate + ".sDate");
				log.debug(pAgency + ".pAgency");
			}

			//② Transaction 시작
			sqlMap.startTransaction();

			//③ 요청 데이터 처리, ④ DB 처리, ⑤ 결과 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");

			//String sDate = ( new SimpleDateFormat("yyyyMM").format(new Date()) )+"01";

			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("OP_STDT", sDate);
			requestMap.put("OP_POS_AGENCY", pAgency);
			requestMap.put("OP_USER", PROG_ID.substring(0, 10));
			// 프로시저 호출
			if(log.isDebugEnabled()){
				log.debug("ACCDCL08250_NEW.execute.callSP_ACCDCL08250_NEW");
				log.debug("requestMap.toString()"+requestMap.toString());
			}
			sqlMap.queryForObject("ACCDCL08250_NEW.callSP_ACCDCL08250_NEW", requestMap);
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