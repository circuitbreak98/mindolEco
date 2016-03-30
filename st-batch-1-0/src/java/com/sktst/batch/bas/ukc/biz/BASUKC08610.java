package com.sktst.batch.bas.ukc.biz;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST</li>
 * <li>설 명 : I/F 상담 상세  배치 DB에 반영한다.</li>
 * <li>작성일 : 2012-01-16 09:00:00</li>
 * </ul>
 *
 * @author 이문규
 */
public class BASUKC08610 extends AbsBatchJobBiz {

	private static final String PROG_ID = "BASUKC08610";
	private static SimpleDateFormat todayFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    private static SimpleDateFormat timeFormatMilSec = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS", Locale.KOREA); 

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 이문규 
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	public int execute(Map request) throws Exception {
		
		super.getProperties();
		
		//logMng.openLogFile(PROG_ID);
		
		if (log.isDebugEnabled()) {
			log.debug(PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();
		
		try {

			//logMng.writeLogFile(PROG_ID + ".execute.startTransaction");
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();
			
			Map requestMap = new HashMap();
			
			// procedure param setting
			String sCurDate = "";
			if( request.size() > 1 ) {
				sCurDate = (String)request.get("args1");
				//logMng.writeLogFile("args1 : " + sCurDate);
			}

			requestMap.put("OP_DT", sCurDate);

			// 프로시저 호출 
//			logMng.writeLogFile("requestMap.toString()"+requestMap.toString());
			
			sqlMap.queryForObject("BASUKC08610.callSP_BASUKC08610", requestMap);

//			logMng.writeLogFile("Commit Record(s) : " + requestMap.get("INS_CNT"));
			
			sqlMap.getCurrentConnection().commit();					
			
		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
		} finally {
			// 처리 완료. (commit이 안된 경우 rollback)
			//logMng.writeLogFile(PROG_ID + ".execute.endTransaction");
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.endTransaction");
			}
			sqlMap.endTransaction();
			//logMng.closeLogFile();
		}

		//logMng.closeLogFile();
		
		return 0;
	}

}
