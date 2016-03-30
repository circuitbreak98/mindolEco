package com.sktst.batch.sal.sui.biz;

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
import java.math.BigDecimal;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업관리</li>
 * <li>설 명 : 일별, 대리점별, 영업사원별 개통실적을 관리.. </li>
 * <li>작성일 : 2010-9-17 10:43:46</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class SALSUI08800 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSUI08800";
	private static String fileTime = "";
	private static String cNullValue = "";

	/**
	 * 일별, 대리점별, 영업사원별 개통실적을 관리
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
		
		fileTime = (String)request.get("args1");

		try{

			logMng.openLogFile(PROG_ID);
			logMng.writeLogFile("처리일자 : " + fileTime);

			// Transaction Start Logging...
			if(log.isDebugEnabled()){
				log.debug( PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();

			// DB를 읽어서 FILE로 down.
			logMng.writeLogFile("------------------------------------------------------------");
			procDailyResultUpdate(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");

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
				log.debug("SALSUI08800.execute.endTransaction");
			}			
			sqlMap.endTransaction ();

			logMng.closeLogFile();
		}
	
		// 처리 결과 코드 리턴
		return 0;
		}

	/**
	 * 내용 : 처리일자 기준 발생된 영업이력을 조회하여 판매일자/대리점/영업사원별 누적
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void procDailyResultUpdate(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("procDailyResult method start......");

		String sCurdate = "";

		if (fileTime == null || cNullValue.equals(fileTime)) {
			// 인자값으로 입력된 일자가 없을 경우 현재일자 취득
			Calendar cl = Calendar.getInstance(Locale.KOREA);
			cl.add(Calendar.DATE, -1);                                 // 작업이 새벽에 수행 되므로 일자를 하루 감해서 Working 일자를 Setting 한다.
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date   cDate = cl.getTime();
			    sCurdate = sdf.format(cDate);
		} else {
			sCurdate = fileTime;
		}

		logMng.writeLogFile("처리일자 : [" + sCurdate + "]");

		Map<String, Object> requestMap    = new HashMap<String, Object>();
	 	requestMap.put("PROC_DT", sCurdate);

		List resultList = (List) sqlMap.queryForList("SALSUI08800.getSalesResult", requestMap);
		int selectCnt = resultList.size();
		int writeCnt = 0;

		Map    msgMap     = new HashMap();
        Map    resultSale; 

		String sSaleDT    = "";
		String sPosAgency = "";
		String sUserID    = "";
		String sSalePlc   = "";
		   int sNewCnt    =  0;
		   int sChgCnt    =  0;

		for (int i = 0; i < selectCnt; i++) {
			msgMap = (Map) resultList.get(i);

			sSaleDT    = (String)      msgMap.get("SALE_DT");
			sPosAgency = (String)      msgMap.get("POS_AGENCY");
        	sUserID    = (String)      msgMap.get("USER_ID");
        	sSalePlc   = (String)      msgMap.get("SALE_PLC");
			sNewCnt    = ((BigDecimal) msgMap.get("NEW_CNT")).intValue();
			sChgCnt    = ((BigDecimal) msgMap.get("CHG_CNT")).intValue();

			requestMap.put("SALE_DT",    sSaleDT);
			requestMap.put("POS_AGENCY", sPosAgency);
			requestMap.put("USER_ID",    sUserID);
			requestMap.put("SALE_PLC",   sSalePlc);
			requestMap.put("RESULT_NEW", sNewCnt);
			requestMap.put("RESULT_CHG", sChgCnt);

			resultSale = (Map)sqlMap.queryForObject("SALSUI08800.getResultInfo", requestMap);

			if (resultSale == null)  {
				sqlMap.update("SALSUI08800.addDailyResult", requestMap);
			} else {
				sqlMap.update("SALSUI08800.UpdateDailyResult", requestMap);
			}
		}

		logMng.writeLogFile("procDailyResultUpdate method end......");
	}

}