package com.sktst.batch.acc.sbc.biz;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;
import com.sktst.batch.sac.erp.biz.ErpCommon;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/정산</li>
 * <li>설 명 : 판매점여신 현황 일마감 </li>
 * <li>작성일 : 2010-05-25</li>
 * </ul>
 *
 * @author 전현주 (junhyunjoo)
 */
public class ACCSBC08000 extends AbsBatchJobBiz {

	private static final String PROG_ID = "ACCSBC08000";
	private static final String SHELL = "/bin/sh";
	private static final String BATCH_PATH = "/app/batch/";

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 전현주 (junhyunjoo)
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
			
			if( request.size() > 1 ) {
				sProcDt = (String)request.get("args1");
				log.debug(sProcDt + ".sProcDt");
			}
			
			//parameter 로 채권조회 일이 없을 경우 어제 날짜로 기본 setting
			if (sProcDt.equals("")) {
				Calendar cal = new GregorianCalendar();
				cal.add(Calendar.DATE , -1);
				Date d = cal.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				sProcDt = sdf.format(d);
				//logMng.writeLogFile("yesterday : " + sCashPayDt);
			}

			//② Transaction 시작
			sqlMap.startTransaction();

			/**
			 *   현재월 전월 가져오기
			 */
			String tranYm = ErpCommon.getLastMonth();
			log.debug("######### 전월  ######### : [" + tranYm + "]");
			
			/************************************************************
			 *   판매점여신_월마감
			 ************************************************************/
			sendACCSBC08100(tranYm);

			/************************************************************
			 *   판매점여신_일마감
			 ************************************************************/
			sendACCSBC08200(sProcDt);
			
			//③ 요청 데이터 처리, ④ DB 처리, ⑤ 결과 데이터 처리
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
	
	/**
	 * 판매점여신_전월마감
	 * @author	전현주 (junhyunjoo)
	 * @param	String tranYm : 마감월
	 * @return	void
	 */
	private void sendACCSBC08100(String tranYm) throws Exception {

		String[] command = new String[3];
		command[0] = SHELL;
		String shellName = "ACCSBC08100_001.sh";
		Process proc = null;
		BufferedReader br = null;
		String line = null;

		command[1] = BATCH_PATH + shellName;
		command[2] = tranYm;
		
		log.debug("######### 판매점여신_전월마감 #########");
		proc = Runtime.getRuntime().exec(command);

		br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		while ((line = br.readLine()) != null) {
			logMng.writeLogFile(line);
		}
	}
	/**
	 * 판매점여신_전일마감
	 * @author	전현주 (junhyunjoo)
	 * @param	String tranYm : 마감월
	 * @return	void
	 */
	private void sendACCSBC08200(String sProcDt) throws Exception {

		String[] command = new String[3];
		command[0] = SHELL;
		String shellName = "ACCSBC08200_001.sh";
		Process proc = null;
		BufferedReader br = null;
		String line = null;

		command[1] = BATCH_PATH + shellName;
		command[2] = sProcDt;
		
		log.debug("######### 판매점여신_전일마감  #########");
		proc = Runtime.getRuntime().exec(command);

		br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		while ((line = br.readLine()) != null) {
			logMng.writeLogFile(line);
		}
	}	
	
}