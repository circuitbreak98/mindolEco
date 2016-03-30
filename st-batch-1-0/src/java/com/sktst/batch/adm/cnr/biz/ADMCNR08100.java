package com.sktst.batch.adm.cnr.biz;

import com.eai.mq.api.EaiApiData;
import com.eai.mq.api.EaiMQApi;
import com.eai.mq.conf.EaiException;
import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준</li>
 * <li>설 명 : 접속로그 삭제 프로그램</li>
 * <li>작성일 : 2009-05-11</li>
 * </ul>
 *
 * @author 정재열 (wonjongyoon)
 */
public class ADMCNR08100 extends AbsBatchJobBiz {

	private static final String PROG_ID = "ADMCNR08100";
	
	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 정재열
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	public int execute(Map request) throws Exception {
		
		super.getProperties();
		SqlMapClient sqlMap = getSqlMapClient();
		try {
			
			logMng.openLogFile(PROG_ID);
			
			if (log.isDebugEnabled()) {
				log.debug("ADMCNR08100.execute.startTransaction");
			}
			logMng.writeLogFile("ADMCNR08100.execute.startTransaction");
			sqlMap.startTransaction();
			

			sqlMap.delete("ADMCNR08100.deleteLog", request);
			
			sqlMap.commitTransaction();
			
		} catch(Exception e) {
			logMng.writeLogFile("execute Exception : " + e);
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e);
			}
		} finally {
			sqlMap.endTransaction();
			// 처리 완료. (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug("ADMCNR08100.execute.endTransaction");
			}
			logMng.writeLogFile("ADMCNR08100.execute.endTransaction");
			logMng.closeLogFile();
		}
		return 0;
	}
}