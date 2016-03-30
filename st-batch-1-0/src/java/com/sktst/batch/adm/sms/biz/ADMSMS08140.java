package com.sktst.batch.adm.sms.biz;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준정보관리</li>
 * <li>설 명 : 입금정산 잔액을 담당자에게 SMS로 송신 </li>
 * <li>작성일 : 2010-03-15</li>
 * </ul>
 *
 * @author 전현주 (junhyunjoo)
 */
public class ADMSMS08140 extends AbsBatchJobBiz {

	private static final String PROG_ID = "ADMSMS08140";
	private static String fileTime = "";
	
	/**
	 * 작업일자의 전일 기준 skt 요금 채권잔액을 담당자에게 SMS로 발송
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
		
		if (log.isDebugEnabled()) {
			log.debug(PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();

		fileTime = (String)request.get("args1");		
		
		try{

			logMng.openLogFile(PROG_ID);


			// Transaction Start Logging...
			if(log.isDebugEnabled()){
				log.debug( PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();

			// DB를 읽어서 FILE로 down.
			//logMng.writeLogFile("------------------------------------------------------------");
			getSMStoManager(sqlMap);
			//logMng.writeLogFile("------------------------------------------------------------");

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
				log.debug("ADMSMS08140.execute.endTransaction");
			}			
			sqlMap.endTransaction ();

			logMng.closeLogFile();
		}
	
		// 처리 결과 코드 리턴
		return 0;
		}

	/**
	 * 내용 : 채권 잔액을 추출
	 *
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void getSMStoManager(SqlMapClient sqlMap) throws Exception {
		//logMng.writeLogFile("getSMStoManager method start......");
		log.debug("getSMStoManager!!!!!!!!!!!!!!!!!!!!!!!!!!");

		// 작업용 변수 선언
		String cNull        = "";

		String pDealNm      = "";
		String pUkeyAgencyCd = "";
		String pSktChagAmt   = "";
		String pSetOffChagAmt   = "";
		//BigDecimal pSktChagAmt ;
		String pPhonNum     = "";
		String pRecvTelNo   = "";
		String pRecvUser    = "";
		String pSendUser    = "";
		String pSendName    = "";
		String pReservTime    = "";
		BigDecimal   pSmsSeq;
		BigDecimal   pTranSeq;
		StringBuffer pSmsMsg = new StringBuffer();

		// SMS 순번 조회용 Map Class
        Map    resultSmsSeq; 

		Map<String, Object> requestSmsSeq  = new HashMap<String, Object>();
		Map<String, Object> requestTable   = new HashMap<String, Object>();
		//Map<String, Object> requestTelSeq  = new HashMap<String, Object>();
		
		String sCashPayDt ="";
		String sChagAccDt ="";
		
		if (!(fileTime == null) && !(cNull.equals(fileTime))) {
			sCashPayDt = fileTime.toString();
		}

		//parameter 로 채권조회 일이 없을 경우 어제 날짜로 기본 setting
		if (sCashPayDt.equals("")) {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.DATE , -1);
			Date d = cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sCashPayDt = sdf.format(d);
			logMng.writeLogFile("yesterday : " + sCashPayDt);
		}		
	
		//당일 날짜 YYYYMMDD 구하기
		if (sChagAccDt.equals("")) {
			Calendar cal = new GregorianCalendar();
			Date d = cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sChagAccDt = sdf.format(d);
			logMng.writeLogFile("yesterday : " + sCashPayDt);
		}
				
		Map<String, Object> requestMap    = new HashMap<String, Object>();
	 	requestMap.put("ACC_DT", sCashPayDt);
	 	requestMap.put("CHAG_DT", sChagAccDt);
	 		 	
		List resultList = (List) sqlMap.queryForList("ADMSMS08140.getSktChagAmt", requestMap);
		int selectCnt = resultList.size();
		int writeCnt = 0;
		String[] arrayContext = null;
		
		// select count가 있을 경우 만...
		if(selectCnt > 0){

			Map msgMap = new HashMap();
			Map detailMap = new HashMap();
			
			for (int i = 0; i < selectCnt; i++) {
				msgMap = (Map) resultList.get(i);
				detailMap = (Map) resultList.get(i);

				pDealNm    = (String) msgMap.get("DEAL_CO_NM");
				pUkeyAgencyCd = (String) msgMap.get("UKEY_AGENCY_CD");
				pSktChagAmt = (String) msgMap.get("CHAG_AMT");
				pSetOffChagAmt = (String) msgMap.get("SETOFF_AMT");
				pPhonNum   = (String) msgMap.get("MBL_PHON");
				//pPhonNum   = "01041122496";
				pRecvTelNo = (String) msgMap.get("TEL_NO");
				//pRecvTelNo = "01055443827";
				pSendUser  = (String) msgMap.get("SEND_USER");
				pSendName  = (String) msgMap.get("SEND_NAME");
				pRecvUser  = (String) msgMap.get("RECV_USER");
				pReservTime    = (String) msgMap.get("SND_DTTM");
				//pReservTime    = "20110404130000";
				
				// SMS 전송이력  순번
				resultSmsSeq = (Map)sqlMap.queryForObject("ADMSMS08140.getAdmSmsTranCnt", requestSmsSeq);
				pTranSeq     = (BigDecimal) resultSmsSeq.get("TRAN_CNT");

				// SMS Message Setting : [국민은행 남구로 P123456] T.Key+ 2월10일 기준 SKT요금잔액 26.440입니다
				pSmsMsg = new StringBuffer("");
				pSmsMsg.append("[" + pDealNm + " "+pUkeyAgencyCd+"] ");
				pSmsMsg.append(" T.Key+ ");
				//pSmsMsg.append(sCashPayDt.substring(0, 4) + "-");
				pSmsMsg.append(sCashPayDt.substring(4, 6) + "월");
				pSmsMsg.append(sCashPayDt.substring(6, 8) + "일 기준 SKT요금 잔액"+ pSktChagAmt +"원");
				//pSmsMsg.append(pSktChagAmt + "원 입니다.");
				if(Integer.parseInt(sChagAccDt.substring(6,8)) > 25){
					
					pSmsMsg.append(" (상계예상 " + pSetOffChagAmt + "원) ");
				}
				pSmsMsg.append(" 입니다 ");
					
				// SMS 전송이력 Master(TADM_SMS_TRAN)  Insert ...
				requestTable.put("TRAN_SEQ",     pTranSeq);
				requestTable.put("SMS_MSG",      pSmsMsg.toString());
				requestTable.put("SMS_USER",     pSendUser);
				requestTable.put("SMS_RECV_TEL", pRecvTelNo);
				
				sqlMap.insert("ADMSMS08140.addSmsTran", requestTable);
								
//				 메세지가 80byte 초과시 
				arrayContext = getParsingContext(pSmsMsg.toString());
				
				for (int j = 0; j < arrayContext.length; j++) {

					// 메시지 고유 아이디 생성.(sequence)
//					 Telink SMS 순번
					resultSmsSeq = (Map)sqlMap.queryForObject("ADMSMS08140.getAdmSmsCmpMsgSeq", requestSmsSeq);
					pSmsSeq      = (BigDecimal) resultSmsSeq.get("SMS_SEQ");
					
					detailMap.put("TRAN_SEQ",  pTranSeq);
					detailMap.put("USER_NAME", pRecvUser);
					detailMap.put("PHONE_NUM", pPhonNum);
					detailMap.put("SMS_SEQ",   pSmsSeq);
					
					sqlMap.insert("ADMSMS08140.addSmsTranDtl", detailMap);
					
					// Telink SMS (telink_sms)  Insert ...
					detailMap.put("SMS_SEQ",      pSmsSeq);
					detailMap.put("SEND_USER",    pSendName);
					detailMap.put("PHONE_NUM",    pPhonNum);
					detailMap.put("SMS_RECV_TEL", pRecvTelNo);
					
					log.debug("SMS_MSG!!!!!!!!!!!!!!!!!!!!!!!!!!"+arrayContext[j]);
					logMng.writeLogFile(arrayContext[j]);
					
					detailMap.put("SMS_MSG",      arrayContext[j]);
					detailMap.put("SND_DTTM",     pReservTime);
					
					sqlMap.insert("ADMSMS08140.addTelinkSMS", detailMap);
				}
				
				// SMS 전송갯수 Count
				writeCnt++;
			}
		}else{

			logMng.writeLogFile("    조회조건에 맞는 자료가 없습니다. : [" + sCashPayDt + "]");
		}

		logMng.writeLogFile("    Select Count : " + selectCnt);
		logMng.writeLogFile("  SMS Send Count : " + writeCnt);
		logMng.writeLogFile("getSMStoManager method end......");
	}

	/**
	 * 한글이 포함된 문자열의 byte 단위 substring을 수행 
	 *
	 * @author 전현주 (junhyunjoo)
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
	
	
	/**
	 *  SMS내용을 파싱한다.(80바이트 초과 시 두개의 메세지로 변환.)
	 *
	 * @author 한병곤 (hanbyunggon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public String[] getParsingContext(String context) {

		byte[] bContext = context.getBytes();
		String[] aReturnValue = null;

		int iSendByte = 80; // sms 자를 byte 수
		int i2Byte = 0;

		// 80byte 보다 작으면 메세지 한개 셋팅.
		if (bContext.length <= iSendByte) {
			aReturnValue = new String[] { context };

		} else {

			for (int i = 0; i < iSendByte; i++) {
				if (((int) bContext[i] & 0x80) == 128) {
					i2Byte++;
				}
			}

			if (((int) bContext[iSendByte] & 0x80) != 0) {
				iSendByte = iSendByte + (i2Byte % 2);
			}

			// 첫번째 내용 셋팅.
			String sFirstContext = new String(bContext, 0, iSendByte);

			// 두번째 내용 셋팅.
			byte[] bSecond = new byte[bContext.length - iSendByte];

			for (int i = 0; i < bSecond.length; i++) {
				bSecond[i] = bContext[iSendByte + i];
			}

			String sSecondContext = new String(bSecond);
			aReturnValue = new String[] { sFirstContext, sSecondContext };
		}

		return aReturnValue;
	}

}