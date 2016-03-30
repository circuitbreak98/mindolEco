package com.sktst.batch.bas.prm.biz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업관리</li>
 * <li>설 명 : 판매점 증감현황을 위한 기초자료를 생성. </li>
 * <li>작성일 : 2010-04-30 10:43:46</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class BASPRM08200 extends AbsBatchJobBiz {

	private static final String PROG_ID = "BASPRM08200";;
	private static final String CNULL   = "";
	private static String fileTime      = "";
	private static Boolean jobFlag      = false;

	/**
	 * 작업기간 : 일주일(월 - 일) 또는 월부터 월말(월 마지막 주일)
	 *
	 * @author 김연석 (kimyeunsuk)
	 *  
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */	
	public int execute(Map request) throws Exception {
		
		super.getProperties();
		
		if (log.isDebugEnabled()) {
			log.debug(PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();

		try{

			logMng.openLogFile(PROG_ID);
			
			fileTime = (String)request.get("args1");
			logMng.writeLogFile("args1 : " + fileTime);

			// Transaction Start Logging...
			if(log.isDebugEnabled()){
				log.debug( PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();

			// 작업 Main Process
			getDealIncreSummary(sqlMap);

			// Transaction Commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			sqlMap.commitTransaction ();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
			}

		} finally {
			//처리 완료. (commit이 안된 경우 rollback)
			if(log.isDebugEnabled()){
				log.debug("BASPRM08200.execute.endTransaction");
			}			
			sqlMap.endTransaction ();

			logMng.closeLogFile();
		}
	
		// 처리 결과 코드 리턴
		return 0;
		}

	/**
	 * 내용 : 작업기간내의 유효 거래처 수와 판매실적 및 재고가 있는 거래처 수, 단말기 판매실적을 집계한다.
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void getDealIncreSummary(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("getDealIncreSummary method start......");

		// 1. Batch 실행 Shell에서 일자를 받아 들인 경우 해당일자가 일요일/월말일이 아니면 작업종료
		//                               들이지 않은 경우 System Date를 구하여 일요일/월말일이 아니면 작업종료
		// 2. 작업유효일인 경우 6일전일자(월요일)을 구하여 작업 진행

		// 일자 Setting을 위해 변수 선언
		int sbYear  = 0;
		int sbMonth = 0;
		int sbDay   = 0;

        Date             sDate = null;
		Calendar         cal   = Calendar.getInstance(Locale.KOREA);
		cal.add(Calendar.DATE, -1);                                 // 작업이 새벽에 수행 되므로 일자를 하루 감해서 Working 일자를 Setting 한다.
		SimpleDateFormat sdf   = new SimpleDateFormat("yyyyMMdd");

		// Parameter로 받은 fileTime(YYYYMMDD)을  일자(YYYY-MM-DD)형식으로 변환
		// 		sbMonth : 0 - January, 1 - February, 2 - March, .....
		if (!(fileTime == null) && !(CNULL.equals(fileTime))) {
	        sbYear  = Integer.parseInt(fileTime.substring(0, 4));
	        sbMonth = Integer.parseInt(fileTime.substring(4, 6)) - 1;
	        sbDay   = Integer.parseInt(fileTime.substring(6, 8));
			cal.set(sbYear, sbMonth, sbDay);
		}

		// 1. 작업일자가 유효한 일자인지 Check
		jobFlag = false;

		int     sDayOfWeek     = 0;
		String  sStartDT       = "";
		String  sEndDT         = "";
		Boolean sLastMonthFlag = false;

		// 작업종료일자
		sDate  = cal.getTime();
		sEndDT = sdf.format(sDate);

		// 1-1. 일요일 Check
            /* SUNDAY    = 1, MONDAY    = 2, THURSDAY  = 3, WEDNESDAY = 4
               TUESDAY   = 5, FRIDAY    = 6, SATURDAY  = 7 */
		sDayOfWeek = cal.get(cal.DAY_OF_WEEK);
		if (sDayOfWeek == cal.SUNDAY) {
			jobFlag = true;
		}

		// 작업시작일자
		cal.add(cal.DATE, (7 - sDayOfWeek) * -1);
        sDate      = cal.getTime();
        sStartDT   = sdf.format(sDate);

		// 1-2. 월 말일 Check
		String tmpEndDay = sEndDT.substring(6, 8).toString();
		String tmpLastofMonth = Integer.toString(cal.getActualMaximum(cal.DAY_OF_MONTH)).toString();
		if (tmpLastofMonth.equals(tmpEndDay)) {
			jobFlag        = true;
			sLastMonthFlag = true;
		}

		if (jobFlag == false) {
			logMng.writeLogFile(" Terminate Job....  Not Monday or LastDay of Month");
			return;
		}

        // 작업시작 월요일이 전월인 경우 처리
        if (!(sStartDT.substring(0, 6).equals(sEndDT.substring(0, 6)))) {
        	sStartDT = sEndDT.substring(0, 6) + "01";
        }

		logMng.writeLogFile(" Working Date From - TO : " + sStartDT + " - " + sEndDT);

		// 작업 시작 ...

        // QueryForObject를 위한 Map 선언
		Map<String, Object> requestMap    = new HashMap<String, Object>();

		// 이미 처리된 자료인 경우 재처리를 위해 이전 작업 자료 삭제...
	 	requestMap.put("END_DT", sEndDT);
		sqlMap.update("BASPRM08200.deleteWeeklyDeal", requestMap);

		// 자료 조회후 바로 Insert
	 	requestMap.put("START_DT", sStartDT);
	 	requestMap.put("END_DT",   sEndDT);
    	sqlMap.update("BASPRM08200.addWeeklyDeal", requestMap);

    	// 월말인 경우 월 말일 기준 이전 일주일간 자료 Summary (전월말 실적/재고 거래처 수 때문..)
    	if (sLastMonthFlag) {
    		// 작업시작일자 계산으로 인해 Calendar의 일자가 변경되었으므로 말일자 Setting
	        sbYear  = Integer.parseInt(sEndDT.substring(0, 4));
	        sbMonth = Integer.parseInt(sEndDT.substring(4, 6)) - 1;
	        sbDay   = Integer.parseInt(sEndDT.substring(6, 8));
			cal.set(sbYear, sbMonth, sbDay);		// 월 말일 Setting

			// 일주일 전 일자 Setting
			cal.add(cal.DATE, -6);
	        sDate      = cal.getTime();
	        sStartDT   = sdf.format(sDate);

    	 	requestMap.put("END_DT", sEndDT.substring(0, 6));
    		sqlMap.update("BASPRM08200.deleteWeeklyDeal", requestMap);

    	 	requestMap.put("START_DT", sStartDT);
    	 	requestMap.put("END_DT",   sEndDT);
        	sqlMap.update("BASPRM08200.addMonthlyDeal", requestMap);
    	}

		logMng.writeLogFile("getDealIncreSummary method end......");

	}

}