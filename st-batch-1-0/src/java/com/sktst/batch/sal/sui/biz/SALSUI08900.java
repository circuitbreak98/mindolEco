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
 * <li>업무 그룹명 : 프로젝트/SKTST/누계가입자</li>
 * <li>설 명 : 일별, 가입 및 해지 누계관리 </li>
 * <li>작성일 : 2011-05-18 14:09:00</li>
 * </ul>
 *
 * @author 김태훈 (kimtaehoon)
 */
public class SALSUI08900 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSUI08900";
	private static String fileTime = "";
	private static String cNullValue = "";

	/**
	 * 일별, 가입 및 해지 누계관리
	 *
	 * @author 김태훈 (kimtaehoon)
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
			updateDailyRecruimentScrb(sqlMap);
			//updateDailyCancelScrb(sqlMap);
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
				log.debug("SALSUI08900.execute.endTransaction");
			}			
			sqlMap.endTransaction ();

			logMng.closeLogFile();
		}
	
		// 처리 결과 코드 리턴
		return 0;
		}
	
	/**
	 * 내용 : TSAL_GENERAL_SALE_IF(모집) 읽어서 TSAL_ACC_SCRB 테이블에 저장
	 *
	 * @author 김태훈 (kimtaehoon)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void updateDailyRecruimentScrb(SqlMapClient sqlMap) throws Exception {
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
	 	requestMap.put("ACC_DTM", sCurdate);

		List resultList = (List) sqlMap.queryForList("SALSUI08900.getDailyRecruimentScrb", requestMap);
		int selectCnt = resultList.size();
		int writeCnt = 0;

		Map    msgMap     = new HashMap();
        Map    resultSale; 

		String sPosAgency   = "";
		String sChrgrUserID = "";
		String sSalePlc     = "";
		String sSalePlcNm   = "";
		String sStlPlc      = "";
		String sStlPlcNm    = "";
		String sSaleDtlTyp  = "";
		String sDsNetCd     = "";
		
		   int sNewCnt      =  0;
		   int sChgCnt      =  0;
		   
		   int sRsS101012 = 0;
		   int sRsS101011 = 0;
		   int sRsS101010 = 0;
		   int sRsS102012 = 0;
		   int sRsS102011 = 0;
		   int sRsS102010 = 0;
		   int sRsS102022 = 0;
		   int sRsS102021 = 0;
		   int sRsS102020 = 0;
		   int sRsS201012 = 0;
		   int sRsS201011 = 0;
		   int sRsS201010 = 0;
		   int sRsS201032 = 0;
		   int sRsS201031 = 0;
		   int sRsS201030 = 0;
		   int sRsS202012 = 0;
		   int sRsS202011 = 0;
		   int sRsS202010 = 0;
		   int sRsS30101  = 0;
		   int sRsS30201  = 0;
		   int sRsS30202  = 0;
		   
		for (int i = 0; i < selectCnt; i++) {
			msgMap = (Map) resultList.get(i);

			sPosAgency    = (String)      msgMap.get("POS_AGENCY");
			sChrgrUserID  = (String)      msgMap.get("CHRGR_USER_ID");
			sSalePlc      = (String)      msgMap.get("SALE_PLC");
        	sSalePlcNm    = (String)      msgMap.get("SALE_PLC_NM");
        	sStlPlc       = (String)      msgMap.get("STL_PLC");
        	sStlPlcNm     = (String)      msgMap.get("STL_PLC_NM");
        	sSaleDtlTyp   = (String)      msgMap.get("SALE_DTL_TYP");
        	sDsNetCd      = (String)      msgMap.get("DS_NET_CD");
			sNewCnt       = ((BigDecimal) msgMap.get("NEW_CNT")).intValue();
			sChgCnt       = ((BigDecimal) msgMap.get("CHG_CNT")).intValue();
			
			//logMng.writeLogFile(sSaleDT+"|"+sPosAgency+"|"+sUserID+"|"+sSalePlc+"|"+sDealCoNm+"|"+sNewCnt+"|"+sChgCnt);
			logMng.writeLogFile(sSaleDtlTyp+"|"+sDsNetCd+"|"+sNewCnt);
			if((sSaleDtlTyp == "S10101") && (sDsNetCd == "2")){
				sRsS101012 = sNewCnt;
			}else if((sSaleDtlTyp == "S10101") && (sDsNetCd == "1")){
				sRsS101011 = sNewCnt;
			}else if((sSaleDtlTyp == "S10101") && (sDsNetCd == "0")){
				sRsS101010 = sNewCnt;
			}else if((sSaleDtlTyp == "S10201") && (sDsNetCd == "2")){
				sRsS102012 = sNewCnt;
			}else if((sSaleDtlTyp == "S10201") && (sDsNetCd == "1")){
				sRsS102011 = sNewCnt;
			}else if((sSaleDtlTyp == "S10201") && (sDsNetCd == "0")){
				sRsS102010 = sNewCnt;
			}else if((sSaleDtlTyp == "S10202") && (sDsNetCd == "2")){
				sRsS102022 = sNewCnt;
			}else if((sSaleDtlTyp == "S10202") && (sDsNetCd == "1")){
				sRsS102021 = sNewCnt;
			}else if((sSaleDtlTyp == "S10202") && (sDsNetCd == "0")){
				sRsS102020 = sNewCnt;
			}else if((sSaleDtlTyp == "S20101") && (sDsNetCd == "2")){
				sRsS201012 = sNewCnt;
			}else if((sSaleDtlTyp == "S20101") && (sDsNetCd == "1")){
				sRsS201011 = sNewCnt;
			}else if((sSaleDtlTyp == "S20101") && (sDsNetCd == "0")){
				sRsS201010 = sNewCnt;
			}else if((sSaleDtlTyp == "S20103") && (sDsNetCd == "2")){
				sRsS201032 = sNewCnt;
			}else if((sSaleDtlTyp == "S20103") && (sDsNetCd == "1")){
				sRsS201031 = sNewCnt;
			}else if((sSaleDtlTyp == "S20103") && (sDsNetCd == "0")){
				sRsS201030 = sNewCnt;
			}else if((sSaleDtlTyp == "S20201") && (sDsNetCd == "2")){
				sRsS202012 = sNewCnt;
			}else if((sSaleDtlTyp == "S20201") && (sDsNetCd == "1")){
				sRsS202011 = sNewCnt;
			}else if((sSaleDtlTyp == "S20201")){
				sRsS202010 = sNewCnt;
			}else if(sSaleDtlTyp == "S30101"){
				sRsS30101 = sNewCnt;
			}else if(sSaleDtlTyp == "S30101"){
				sRsS30201 = sNewCnt;
			}else if(sSaleDtlTyp == "S30101"){
				sRsS30202 = sNewCnt;
			}else {}
			
			requestMap.put("POS_AGENCY",    sPosAgency);
			requestMap.put("CHRGR_USER_ID", sChrgrUserID);
			requestMap.put("SALE_PLC",      sSalePlc);
			requestMap.put("SALE_PLC_NM",   sSalePlcNm);
			requestMap.put("STL_PLC",       sStlPlc);
			requestMap.put("STL_PLC_NM",    sStlPlcNm);
			requestMap.put("SALE_DTL_TYP",  sSaleDtlTyp);
			
			requestMap.put("RS_S10101_2",    sRsS101012);      
			requestMap.put("RS_S10101_1",    sRsS101011);
			requestMap.put("RS_S10101_0",    sRsS101010);
			requestMap.put("RS_S10201_2",    sRsS102012);
			requestMap.put("RS_S10201_1",    sRsS102011);
			requestMap.put("RS_S10201_0",    sRsS102010);
			requestMap.put("RS_S10202_2",    sRsS102022);
			requestMap.put("RS_S10202_1",    sRsS102021);
			requestMap.put("RS_S10202_0",    sRsS102020);
			requestMap.put("RS_S20101_2",    sRsS201012);
			requestMap.put("RS_S20101_1",    sRsS201011);
			requestMap.put("RS_S20101_0",    sRsS201010);
			requestMap.put("RS_S20103_2",    sRsS201032);
			requestMap.put("RS_S20103_1",    sRsS201031);
			requestMap.put("RS_S20103_0",    sRsS201030);
			requestMap.put("RS_S20201_2",    sRsS202012);
			requestMap.put("RS_S20201_1",    sRsS202011);
			requestMap.put("RS_S20201_0",    sRsS202010);
			requestMap.put("RS_S30101",      sRsS30101); 
			requestMap.put("RS_S30201",      sRsS30201); 
			requestMap.put("RS_S30202",      sRsS30202); 

			resultSale = (Map)sqlMap.queryForObject("SALSUI08900.getResultInfo", requestMap);
			
			if (resultSale == null)  {
				
				sqlMap.update("SALSUI08900.addDailyResult", requestMap);
			} else {
				//sqlMap.update("SALSUI08900.UpdateDailyResult", requestMap);
			}
		}

		logMng.writeLogFile("procDailyResultUpdate method end......");
	}

	/**
	 * 내용 : I/F 전문(해재:77)을 읽어서 TSAL_ACC_SCRB 테이블에 저장
	 *
	 * @author 김태훈 (kimtaehoon)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void updateDailyCancelScrb(SqlMapClient sqlMap) throws Exception {
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

		List resultList = (List) sqlMap.queryForList("SALSUI08900.getSalesResult", requestMap);
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

			resultSale = (Map)sqlMap.queryForObject("SALSUI08900.getResultInfo", requestMap);





			if (resultSale == null)  {
				sqlMap.update("SALSUI08900.addDailyResult", requestMap);
			} else {
				sqlMap.update("SALSUI08900.UpdateDailyResult", requestMap);
			}
		}

		logMng.writeLogFile("procDailyResultUpdate method end......");
	}

	
}