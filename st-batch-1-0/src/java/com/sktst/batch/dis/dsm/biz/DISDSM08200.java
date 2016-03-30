package com.sktst.batch.dis.dsm.biz;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : 일재고 통계정보를 생성한다. </li>
 * <li>작성일 : 2009-01-19 10:43:46</li>
 * </ul>
 *
 * @author 한병곤 (hanbyunggon)
 * 
 * 
 */
public class DISDSM08200 extends AbsBatchJobBiz {

	private static final String PROG_ID = "DISDSM08200";
	private static final String USER_ID = "DISDSM0820";

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 한병곤 (hanbyunggon)
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
		boolean bDebugEnabled = log.isDebugEnabled();
		
		try {
			
			logMng.openLogFile(PROG_ID);

			//로그 출력 (AbsBatchJobBiz 클래스에서 제공하는 변수 log를 사용)
			if (bDebugEnabled) log.debug(PROG_ID + ".execute");
			logMng.writeLogFile(PROG_ID + ".execute");
			
			String sStrd_dt = (String)request.get("args1"); // 기준일자.
			
			if( sStrd_dt == null || sStrd_dt.equals("")) {
				
				Date d = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -1);
				d = cal.getTime();
				sStrd_dt = sdf.format(d);
			}
			
			logMng.writeLogFile("args1 : " + sStrd_dt);
			log.debug("args1" + sStrd_dt);
			
			if (bDebugEnabled) log.debug(PROG_ID + ".execute.startTransaction");
			logMng.writeLogFile(PROG_ID + ".execute.startTransaction");

			//Transaction 시작
			sqlMap.startTransaction();
			
			Map mParm = new HashMap();
			mParm.put("STRD_DT", sStrd_dt);
			mParm.put("USER_ID", USER_ID);
			
			log.debug("sStrd_dt" + sStrd_dt);
			log.debug("USER_ID" + USER_ID);
			
			// 기존에 생성된 통계정보를 삭제한다.
			sqlMap.delete("DISDSM08200.deleteDisDaySum", mParm);
			
			// 통계정보를 생성한다.
			sqlMap.insert("DISDSM08200.insertDisDaySum", mParm);

			// 처리 결과 commit
			if (bDebugEnabled) log.debug(PROG_ID + ".execute.commitTransaction");
			logMng.writeLogFile(PROG_ID + ".execute.commitTransaction");
			
			sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
			
			if (bDebugEnabled) log.debug("execute Exception : " + e.getMessage());
			
		} finally {
			//⑥-2 Transaction rollback (commit이 안된 경우 rollback)
			if (bDebugEnabled) log.debug(PROG_ID + ".execute.commitTransaction");
			
			sqlMap.endTransaction();
			
			logMng.closeLogFile();
		}
		
		//⑦ 처리 결과 코드 리턴
		return 0;
	}
	
}