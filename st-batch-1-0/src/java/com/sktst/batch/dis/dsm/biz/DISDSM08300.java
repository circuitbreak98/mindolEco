package com.sktst.batch.dis.dsm.biz;

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
public class DISDSM08300 extends AbsBatchJobBiz {

	private static final String PROG_ID = "DISDSM08300";
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
			procDailyDisCount(sqlMap);
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
				log.debug("DISDSM08300.execute.endTransaction");
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
	private void procDailyDisCount(SqlMapClient sqlMap) throws Exception {
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

	 	// 처리일자의 현재고 및 불량재고, 가용재고  Count 생성
		sqlMap.update("DISDSM08300.addDailyDisCount", requestMap);
		logMng.writeLogFile(" ***  addDailyDisCount Success !!!  ***");

	 	// TDIS_DIS를 기준으로 판매출고(205) 자료를 조회하여 판매실적 Update
		List resultList = (List) sqlMap.queryForList("DISDSM08300.getDailySalesCount", requestMap);
		int selectCnt = resultList.size();
		int writeCnt = 0;

		Map    msgMap     = new HashMap();
        Map    resultSale; 

		String sDisDt     = "";
		String sPosAgency = "";
		String sDealCl    = "";
		String sHldPlcId  = "";
		String sUserId    = "";
		String sProdCl    = "";
		String sProdCd    = "";
		String sColorCd   = "";
		String sMfactId   = "";
		   int sSalesQty  =  0;

		for (int i = 0; i < selectCnt; i++) {
			msgMap = (Map) resultList.get(i);

			sDisDt     = (String)      msgMap.get("DIS_DT");
			sPosAgency = (String)      msgMap.get("POS_AGENCY");
        	sDealCl    = (String)      msgMap.get("DEAL_CL");
        	sHldPlcId  = (String)      msgMap.get("HLD_PLC_ID");
        	sUserId    = (String)      msgMap.get("USER_ID");
        	sProdCl    = (String)      msgMap.get("PROD_CL");
        	sProdCd    = (String)      msgMap.get("PROD_CD");
        	sColorCd   = (String)      msgMap.get("COLOR_CD");
        	sMfactId   = (String)      msgMap.get("MFACT_ID");
        	sSalesQty  = ((BigDecimal) msgMap.get("SALES_QTY")).intValue();

        	if ("N".equals(sUserId)) {
        		sUserId = "";
        	}

			requestMap.put("DIS_DT",     sDisDt);
			requestMap.put("POS_AGENCY", sPosAgency);
			requestMap.put("DEAL_CL",    sDealCl);
			requestMap.put("HLD_PLC_ID", sHldPlcId);
			requestMap.put("USER_ID",    sUserId);
			requestMap.put("PROD_CL",    sProdCl);
			requestMap.put("PROD_CD",    sProdCd);
			requestMap.put("COLOR_CD",   sColorCd);
			requestMap.put("MFACT_ID",   sMfactId);
			requestMap.put("SALES_QTY",  sSalesQty);

			resultSale = (Map)sqlMap.queryForObject("DISDSM08300.getDailySalesCheck", requestMap);

			if (resultSale == null)  {
				sqlMap.update("DISDSM08300.addDailySalesCount", requestMap);
			} else {
				sqlMap.update("DISDSM08300.updateDailySalesCount", requestMap);
			}
		}

		logMng.writeLogFile("procDailyDisCount method end......");
	}

}