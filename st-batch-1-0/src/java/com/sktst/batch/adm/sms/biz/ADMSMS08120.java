package com.sktst.batch.adm.sms.biz;

import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준</li>
 * <li>설 명 : SMS 송신 프로그램</li>
 * <li>작성일 : 2009-06-07</li>
 * </ul>
 *
 * @author 한병곤
 */
public class ADMSMS08120 extends AbsBatchJobBiz {

	private static final String PROG_ID = "ADMSMS08120";
	
	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 원종윤 (wonjongyoon)
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
				log.debug("ADMSMS08120.execute.startTransaction");
			}
			logMng.writeLogFile("ADMSMS08120.execute.startTransaction");
			sqlMap.startTransaction();
			
			// 업무 시작.
			String msgCtxt = (String)request.get("args1");
			logMng.writeLogFile("args1=[" + msgCtxt + "]");
			
			String rcvPhonNum = (String)request.get("args2");
			logMng.writeLogFile("args2=[" + rcvPhonNum + "]");
			Map<String, String> msgInfo = new HashMap<String, String >();
			msgInfo.put("msgCtxt", msgCtxt);
			msgInfo.put("rcvPhonNum", rcvPhonNum);
			
			sendSms(sqlMap, msgInfo);	
				
			return NORMAL;

		} catch(Exception e) {
			logMng.writeLogFile("execute Exception : " + e);
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e);
			}
			
			return ABNORMAL;
		} finally {
			sqlMap.endTransaction();
			// 처리 완료. (commit이 안된 경우 rollback)
			
			if (log.isDebugEnabled()) {
				log.debug("ADMSMS08120.execute.endTransaction");
			}
			logMng.writeLogFile("ADMSMS08120.execute.endTransaction");
			logMng.closeLogFile();
		}
	}

	/**
	 * SMS 전송 테이블(TADM_SMS_TRAN, TADM_SMS_TRAN_DTL)에 insert한다.
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param msgInfo
	 *            msgInfo(sms내용, 수신자번호, SKT가입자여부)	            
	 * @return MQ에 넣을 String값 
	 */
	private void sendSms(SqlMapClient sqlMap, Map msgInfo) throws Exception {

		logMng.writeLogFile("addSmsTran method start......");

		// requestMap, resultMap 선언
		Map<String, Object> requestMap = new HashMap<String, Object>();
		
		// 메시지 내용 읽어서 requestMap에 넣기
		String msgCtxt = msgInfo.get("msgCtxt").toString();
		requestMap.put("CONTEXT", msgCtxt);
		
		logMng.writeLogFile(requestMap.toString());	
		
		// 전화번호 리스트를 읽어서 처리하기
		// 전화번호 읽어서 requestMap에 넣기
		String sTelList = msgInfo.get("rcvPhonNum").toString();
		
		String[] arrayTelNo = null;
		String[] arrayContext = null;

		arrayTelNo = sTelList.split(";");
		logMng.writeLogFile("tel_cnt=[" + arrayTelNo.length + "]");
		
		// SMS 전송내역 테이블에 Insert
		for (int i = 0; i < arrayTelNo.length; i++) {
			requestMap.put("TEL_NO", arrayTelNo[i]);
			logMng.writeLogFile("TEL_NO=[" +arrayTelNo[i] + "]");

			// 메세지가 80byte 초과시 
			arrayContext = getParsingContext(msgCtxt);

			for (int j = 0; j < arrayContext.length; j++) {

				requestMap.put("SMSCONTEXT", arrayContext[j]);

				// sms 전송 table 저장.
				sqlMap.insert("ADMSMS08120.addTelinkSms", requestMap);
				
			}
		}			
		
		// 처리 결과 commit
		sqlMap.commitTransaction();					
		
		logMng.writeLogFile("smsContents=[" + msgCtxt + "]");			
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