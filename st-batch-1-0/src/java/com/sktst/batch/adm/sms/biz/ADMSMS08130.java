package com.sktst.batch.adm.sms.biz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
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
 * <li>업무 그룹명 : 프로젝트/SKTST/기준정보관리</li>
 * <li>설 명 : 거래처별 보증보험만료일을 Check하여 담당자에게 SMS를 송신 </li>
 * <li>작성일 : 2010-03-15</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class ADMSMS08130 extends AbsBatchJobBiz {

	private static final String PROG_ID = "ADMSMS08130";
	private static String fileTime = "";

	/**
	 * 작업일자로 부터 15일, 30일 후가 보증보험 만료일인 거래처 정보를 추출하여 담당자에게 SMS를 발송
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
		logMng.writeLogFile("args1 : " + fileTime);

		try{

			logMng.openLogFile(PROG_ID);


			// Transaction Start Logging...
			if(log.isDebugEnabled()){
				log.debug( PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();

			// DB를 읽어서 FILE로 down.
			logMng.writeLogFile("------------------------------------------------------------");
			getSMStoManager(sqlMap);
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
				log.debug("ADMSMS08130.execute.endTransaction");
			}			
			sqlMap.endTransaction ();

			logMng.closeLogFile();
		}
	
		// 처리 결과 코드 리턴
		return 0;
		}

	/**
	 * 내용 : 부가상품 가입정보를 취득하기 위하여 현재 PS 시스템에 유효한(판매취소되지 않은) 서비스 관리번호를 추출
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void getSMStoManager(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("getSMStoManager method start......");

		// 작업용 변수 선언
		String cNull        = "";

		String pDealNm      = "";
		String pInspExpDt   = "";
		String pPhonNum     = "";
		String pRecvTelNo   = "";
		String pRecvUser    = "";
		String pSendUser    = "";
		String pSendName    = "";
		BigDecimal   pSmsSeq;
		BigDecimal   pTranSeq;
		StringBuffer pSmsMsg = new StringBuffer();

		// SMS 순번 조회용 Map Class
        Map    resultSmsSeq; 

		Map<String, Object> requestSmsSeq  = new HashMap<String, Object>();
		Map<String, Object> requestTable   = new HashMap<String, Object>();


		// Calendar 생성
        Date             cDate = null;
		Calendar         cal   = Calendar.getInstance(Locale.KOREA);
		SimpleDateFormat sdf   = new SimpleDateFormat("yyyyMMdd");

		// Parameter로 받은 fileTime(YYYYMMDD)을  일자(YYYY-MM-DD)형식으로 변환
		// 		sbMonth : 0 - January, 1 - February, 2 - March, .....
		if (!(fileTime == null) && !(cNull.equals(fileTime))) {
	        int sbYear  = Integer.parseInt(fileTime.substring(0, 4));
	        int sbMonth = Integer.parseInt(fileTime.substring(4, 6)) - 1;
	        int sbDay   = Integer.parseInt(fileTime.substring(6, 8));
			cal.set(sbYear, sbMonth, sbDay);
		}

		// D-15일
		cal.add(cal.DATE, 15);
               cDate       = cal.getTime();
		String sInspExpDt1 = sdf.format(cDate);

		// D-30일
		cal.add(cal.DATE, 15);
               cDate       = cal.getTime();
		String sInspExpDt2 = sdf.format(cDate);
		logMng.writeLogFile("    Work Date : [" + fileTime + "] Expired Date : [" + sInspExpDt1 + "/" + sInspExpDt2 + "]");

		Map<String, Object> requestMap    = new HashMap<String, Object>();
	 	//requestMap.put("INSP_EXP_DT", sInspExpDt1);
	 	requestMap.put("INSP_EXP_DT1", sInspExpDt1);
	 	requestMap.put("INSP_EXP_DT2", sInspExpDt2);

		List resultList = (List) sqlMap.queryForList("ADMSMS08130.getExpiredDeal", requestMap);
		int selectCnt = resultList.size();
		int writeCnt = 0;

		// select count가 있을 경우 만...
		if(selectCnt > 0){

			Map msgMap = new HashMap();

			for (int i = 0; i < selectCnt; i++) {
				msgMap = (Map) resultList.get(i);

				pDealNm    = (String) msgMap.get("DEAL_CO_NM");
				pInspExpDt = (String) msgMap.get("INSP_EXP_DT");
				pPhonNum   = (String) msgMap.get("MBL_PHON");
				pRecvTelNo = (String) msgMap.get("TEL_NO");
				pSendUser  = (String) msgMap.get("SEND_USER");
				pRecvUser  = (String) msgMap.get("RECV_USER");
				pSendName  = (String) msgMap.get("SEND_NAME");

				// Telink SMS 순번
				resultSmsSeq = (Map)sqlMap.queryForObject("ADMSMS08130.getAdmSmsCmpMsgSeq", requestSmsSeq);
				pSmsSeq      = (BigDecimal) resultSmsSeq.get("SMS_SEQ");

				// SMS 전송이력  순번
				resultSmsSeq = (Map)sqlMap.queryForObject("ADMSMS08130.getAdmSmsTranCnt", requestSmsSeq);
				pTranSeq     = (BigDecimal) resultSmsSeq.get("TRAN_CNT");

				// SMS Message Setting : [온누리텔레콤]은 2010-01-01 일자로 보증보험 증권이 만료 됩니다.
				pSmsMsg = new StringBuffer("");
				pSmsMsg.append("[" + pDealNm + "]은(는) ");
				pSmsMsg.append(pInspExpDt.substring(0, 4) + "-");
				pSmsMsg.append(pInspExpDt.substring(4, 6) + "-");
				pSmsMsg.append(pInspExpDt.substring(6, 8) + " 일자로 보증보험 증권이 만료 됩니다.");

				// SMS 전송이력 Master(TADM_SMS_TRAN)  Insert ...
				requestTable.put("TRAN_SEQ",     pTranSeq);
				requestTable.put("SMS_MSG",      pSmsMsg.toString());
				requestTable.put("SMS_USER",     pSendUser);
				requestTable.put("SMS_RECV_TEL", pRecvTelNo);
				requestTable.put("SMS_RECV_TEL", pRecvTelNo);

				sqlMap.insert("ADMSMS08130.addSmsTran", requestTable);

				// SMS 전송이력 상세(TADM_SMS_TRAN_DTL)  Insert ...
				requestTable.put("TRAN_SEQ",  pTranSeq);
				requestTable.put("USER_NAME", pRecvUser);
				requestTable.put("PHONE_NUM", pPhonNum);
				requestTable.put("SMS_SEQ",   pSmsSeq);

				sqlMap.insert("ADMSMS08130.addSmsTranDtl", requestTable);

				// Telink SMS (telink_sms)  Insert ...
				requestTable.put("SMS_SEQ",      pSmsSeq);
				requestTable.put("SEND_USER",    pSendName);
				requestTable.put("PHONE_NUM",    pPhonNum);
				requestTable.put("SMS_RECV_TEL", pRecvTelNo);
				requestTable.put("SMS_MSG",      pSmsMsg.toString());

				sqlMap.insert("ADMSMS08130.addTelinkSMS", requestTable);

				// SMS 전송갯수 Count
				writeCnt++;
			}
		}else{

			logMng.writeLogFile("    조회조건에 맞는 자료가 없습니다. : [" + sInspExpDt1 + "/" + sInspExpDt2 + "]");

		}

		logMng.writeLogFile("    Select Count : " + selectCnt);
		logMng.writeLogFile("  SMS Send Count : " + writeCnt);
		logMng.writeLogFile("getSMStoManager method end......");
	}

	/**
	 * 한글이 포함된 문자열의 byte 단위 substring을 수행 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param str        String 객체,  분할 대상이 되는 문자열
	 *        startPos   Integer 객체, 문자 추출 시작 위치
	 *        getLength  Integer 객체, 문자 추출 Length
	 * @return String
	 * 
	 * @throws Exception
	 */
	public static String getSubstrByte(String str, int startPos, int getLength) {
		  if (str==null) return ""; 

		  String returnStr = new String(str.getBytes(), startPos, getLength);

		  return returnStr.trim();
	}

}