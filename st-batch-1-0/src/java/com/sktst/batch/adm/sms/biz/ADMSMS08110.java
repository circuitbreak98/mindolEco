package com.sktst.batch.adm.sms.biz;

import com.eai.mq.api.EaiApiData;
import com.eai.mq.api.EaiMQApi;
import com.eai.mq.conf.EaiException;

import icasApi.AuthICAS;
import icasApi.common.CommRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.math.BigDecimal;


import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;
import com.sktst.batch.base.BatchBizRuntimeException;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준</li>
 * <li>설 명 : SMS 송신 프로그램</li>
 * <li>작성일 : 2009-06-07</li>
 * </ul>
 *
 * @author 원종윤 (wonjongyoon)
 */
public class ADMSMS08110 extends AbsBatchJobBiz {

	private static final String ICAS_LOGIN_ID = "WPSM1241"; // ICAS 로그인 아이디
	private static final String ICAS_LOGIN_PW = "ms8231ts"; // ICAS 로그인 비밀번호
	
	private static final String PROG_ID = "ADMSMS08110";
	private static final SimpleDateFormat TODAY_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
	private static final SimpleDateFormat TIME_FORMAT= new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
	private static final String TODAY = TODAY_FORMAT.format(new Date());
	private static final String TIME = TIME_FORMAT.format(new Date());
	private static final String SMS_BLANK_DATA = " "; // SMS 전문 구성시 블랭크 채우는 데이타
	private static final int SIZE = 100;
	
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
				log.debug("ADMSMS08100.execute.startTransaction");
			}
			logMng.writeLogFile("ADMSMS08100.execute.startTransaction");
			sqlMap.startTransaction();
			
			// 업무 시작.
			
			
			String msgCtxt = (String)request.get("args1");
			logMng.writeLogFile("args1=[" + msgCtxt + "]");
			
			String rcvPhonNum = (String)request.get("args2");
			logMng.writeLogFile("args2=[" + rcvPhonNum + "]");
			Map<String, String> msgInfo = new HashMap<String, String >();
			msgInfo.put("msgCtxt", msgCtxt);
			msgInfo.put("rcvPhonNum", rcvPhonNum);
			
			String smsContents = addSmsTran(sqlMap, msgInfo);
			
			
			// MQ를 통해 SMS 전송
			putMQ(smsContents);
			
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
				log.debug("ADMSMS08100.execute.endTransaction");
			}
			logMng.writeLogFile("ADMSMS08100.execute.endTransaction");
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
	private String addSmsTran(SqlMapClient sqlMap, Map msgInfo) {
		try {
			logMng.writeLogFile("addSmsTran method start......");

			// requestMap, resultMap 선언
			Map<String, Object> requestMap = new HashMap<String, Object>();
			Map resultMap = null;	
			

			
			
			// 오늘날짜 requestMap에 넣기
			requestMap.put("TRAN_DT", TODAY);
			
			// 전송차수 구해서 requestMap에 넣기
			resultMap = (Map)sqlMap.queryForObject("ADMSMS08110.getTranCnt", requestMap);
			
			BigDecimal tranCnt = (BigDecimal)resultMap.get("TRAN_CNT");
//			int tran_cnt = Integer.parseInt(tranCnt);
			
			requestMap.put("TRAN_CNT", tranCnt);
			
			log.debug("###########################"+tranCnt);
			
			// 메시지 내용 읽어서 requestMap에 넣기
			String msgCtxt = msgInfo.get("msgCtxt").toString();
			requestMap.put("CONTEXT", msgCtxt);
			
			// Title 구해서 requestMap에 넣기(
			String title = "";
			byte[] titleByte = msgCtxt.getBytes();
			
			if(titleByte.length > 40) {
				title = new String(titleByte, 0, 39);
			} else {
				title = msgCtxt;
			}
			
			requestMap.put("TITLE", title);
			
			// 메시지일련번호 구해서 requestMap에 넣기
			resultMap = (Map)sqlMap.queryForObject("ADMSMS08110.getMsgSerNum");
			String msgSerNum = resultMap.get("MSG_SER_NUM").toString();
			requestMap.put("MSG_SER_NUM", msgSerNum);

			// SMS 전송 테이블에 Insert
			sqlMap.insert("ADMSMS08110.addSmsTran", requestMap);
			
			logMng.writeLogFile(requestMap.toString());	
			
			// 전화번호 리스트를 읽어서 처리하기
			// 전화번호 읽어서 requestMap에 넣기
			String tel_list = msgInfo.get("rcvPhonNum").toString();
			String[] fieldBuffer = new String[SIZE];
			
			fieldBuffer = tel_list.split(";");
			int tel_cnt = fieldBuffer.length;
			logMng.writeLogFile("tel_cnt=[" + tel_cnt + "]");
			
			String tel_no = "";
			String skt_yn = "";
			
			ArrayList<String> alTelNo = new ArrayList<String>();
			ArrayList<String> alSktYn = new ArrayList<String>();
			
			int icasRetVal = 0;
			
			////////////////////////////////////////////////////////////////////			
			// ICAS통해서 SKT고객여부 읽어서 requestMap에 넣기
			AuthICAS oAuthICAS = null;			
			CommRecord oReqValue = null;
			
			
			// 전화번호, SKT고객여부를 가지고 오기 
			for (int i = 0; i < tel_cnt; i++) {
				tel_no = fieldBuffer[i];
				alTelNo.add(i, tel_no);
				// AuthICAS 객체 생성
				oAuthICAS = new AuthICAS(ICAS_LOGIN_ID, ICAS_LOGIN_PW);

				// 메소드 추가
				oAuthICAS.addMethod("AuthCtrSvcActiveSV");

				// 파라메터 설정
				oReqValue = new CommRecord();				
				oReqValue.addValue("SVCNUMBER", getFixedTelno(tel_no));
				
				oAuthICAS.addParam(oReqValue);
				
				icasRetVal = oAuthICAS.call();
				logMng.writeLogFile("icasRetVal=[" + icasRetVal + "]");
				skt_yn = getSktCustYn(icasRetVal);
				alSktYn.add(i, skt_yn);
				
				// AuthICAS 객체 클리어			
				oAuthICAS.clear();
			}


			
			// SMS 전송내역 테이블에 Insert
			for (int i = 0; i < tel_cnt; i++) {
				requestMap.put("TEL_NO", alTelNo.get(i));
				requestMap.put("SKT_YN", alSktYn.get(i));
				
				log.debug(requestMap);
				//requestMap.put("NAME", "원종윤");
				sqlMap.insert("ADMSMS08110.addSmsTranDtl", requestMap);
				
				// 처리 결과 commit
				sqlMap.commitTransaction();			
			}
			
			// MQ에 전송할 메시지 구성
			StringBuffer sb = new StringBuffer();
			
			// MQ에 넣을 HEADER 구성
			sb.append("0100"); //  1. 메세지 코드(4)
			sb.append("S"); //  2. SMS 처리 구분 코드(1)
			sb.append("SMS.TPS_SMS_RSLT_MFF" + getSpace(5, 6)); //  3. 인터페이스 ID(50)
			sb.append("_TPS_NTC_____" + getSpace(7, 1)); //  4. 프로그램 ID(20)
			sb.append(setSpace(msgSerNum, 15, SMS_BLANK_DATA)); //  5. 메세지 일련번호(15)
			sb.append(getSpace(5, 4)); //  6. UKEY ID(20)
			sb.append(getSpace(5, 4)); //  7. UKEY ID(20)
			sb.append(getSpace(5, 4)); //  8. UKEY ID(20)
			sb.append(getSpace(3, 4)); //  9. 발신 전화번호(12)
			sb.append(setSpace("0000", 12, SMS_BLANK_DATA)); // 10. 회신 전화번호(12)
			sb.append(getSpace(8, 10)); // 11. 회신 URL(80)
			sb.append(TIME); // 12. 생성일시(14)
			sb.append(getSpace(2, 7)); // 13. 전송시작일시(14)
			sb.append(getSpace(2, 7)); // 14. 전송완료일시(14)
			sb.append("0000-2359"); // 15. 전송가능시간대1(9)
			sb.append(getSpace(3, 3)); // 16. 전송가능시간대2(9)
			sb.append("Y"); // 17. 착신전환여부(1)
			sb.append(getSpace(1, 5)); // 18. 전송주기(5)
			sb.append("120"); // 19. 타임아웃시간(3)
			sb.append("Y"); // 20. 응답여부(1)
			sb.append("0"); // 21. 장문처리유형(1)
			sb.append(getSpace(3, 3)); // 22. 템플릿 ID(9)
			sb.append(getSpace(8, 10)); // 23. 템플릿 MSG(80)
			String content = setSpace(msgCtxt, 2000, SMS_BLANK_DATA); 
			sb.append(content);     // 24. SMS 내용(2000)
			
			// MQ에 넣을 body 구성
//			sb.append(setSpace(tel_no, 12, SMS_BLANK_DATA)); // 25. 수신전화번호리스트(12)
//			sb.append(getSpace(5, 2)); // 26. 서비스관리번호(10)
//			sb.append(skt_yn); // 27. SKT고객여부(1)
			
			// 수신자 수만큼 정보를 담는다.
			for (int i = 0; i < tel_cnt; i++) {
				sb.append(setSpace(alTelNo.get(i), 12, SMS_BLANK_DATA)); // 25. 수신전화번호리스트(12)
				sb.append(getSpace(5, 2)); // 26. 서비스관리번호(10)
				sb.append(alSktYn.get(i)); // 27. SKT고객여부(1)
			}
			
			// 100-수신자 수만큼 blank. 
			for (int i = 0; i < 100-tel_cnt; i++) {
				sb.append(setSpace("", 12, SMS_BLANK_DATA)); // 25. 수신전화번호리스트(12)
				sb.append(getSpace(5, 2)); // 26. 서비스관리번호(10)
				sb.append(getSpace(1, 1)); // 27. SKT고객여부(1)
			}
			
			logMng.writeLogFile("smsContents=[" + sb + "]");
			logMng.writeLogFile("smsContents.length()-byte=[" +  sb.toString().getBytes().length +"]");
			
			return sb.toString();
			
		} catch(Exception e) {
			if (log.isDebugEnabled()) {
				log.debug(e);
			}
			logMng.writeLogFile("addSmsTran Exception = [" + e + "]");
			throw new BatchBizRuntimeException("addSmsTran Exception");
		}
		finally {
			logMng.writeLogFile("addSmsTran method end......");
		}
	}
	
	/**
	 * skt 사용자 여부값 가져오기
	 * 
	 * @author 원종윤 (wonjongyoon) 
	 * @param rslt_cd
	 * @return skt사용자여부
	 */
	private String getSktCustYn(int rslt_cd) {
		String resultYn = "N";
		// 3442 : SKT AC인 고객, 3443 : SKT SP인 고객
		if (rslt_cd == 3442 || rslt_cd == 3443) {
			resultYn = "Y";
		}
		return resultYn;
	}
	
	/**
	 * 공백문자  생성
	 * 
	 * @author 원종윤 (wonjongyoon)
	 * @param count	생성 반복 횟수
	 * @param spaceSize	생성문자를 만들기위한 반복 횟수
	 * @return 결과 문자열
	 */
	private String getSpace(int count, int spaceSize) {
		StringBuffer space = new StringBuffer();
		for (int i = 0; i < spaceSize; i++) {
			space.append(SMS_BLANK_DATA);
		}

		StringBuffer totalSpace = new StringBuffer();

		for (int i = 0; i < count; i++) {
			totalSpace.append(space);
		}

		return totalSpace.toString();
	}

	/**
	 * 대상 문자열에 지정하는 길이 까지 지정하는 문자를 추가로 채운다
	 * 
	 * @author 원종윤 (wonjongyoon)
	 * @param str 대상문자열
	 * @param total 총길이
	 * @param apStr 추가 문자
	 * @return 결과 문자열
	 */
	private String setSpace(String str, int total, String apStr) {
		if(str == null){
			str = "";
		}
		String result = "";
		byte[] strByte = str.getBytes();
		int gab = total - strByte.length;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < gab; i++) {
			sb.append(apStr);
		}
		
		result = str + sb.toString();
		return result;
	}
	
	/**
	 * 전화번호를 11자리로 고정
	 * 
	 * @author 원종윤 (wonjongyoon) 
	 * @param telNo 전화번호
	 * @return 결과 문자열
	 */
	private String getFixedTelno(String telNo) {
		if(telNo == null){
			telNo = "";
		}
		// 전화번호가 10자리이면, 국번 앞에 0을 채운다.
		if (telNo.length() == 10) {
			telNo = telNo.substring(0, 3) + "0" + telNo.substring(3);
		}
		return telNo;
	}
	
	/**
	 * MQ로 보낼 데이터 put
	 * 
	 * @author 원종윤 (wonjongyoon)
	 * @param smsContents SMS 내용
	 * @return void
	 */
	private void putMQ(String smsContents) {
		
		// mq connection 획득 
		try {
			logMng.writeLogFile("putMQ method start......");
			EaiMQApi mqapi = new EaiMQApi();

			int ret; // MQ의 리턴값 확인용 변수
			// EAI MQ API를 사용하기 위해 해당 큐 매니저에 연결 함
			ret = mqapi.mq_connect();
			
			if ((ret = mqapi.mq_connect()) != 0) {
				logMng.writeLogFile("mqapi.mq_connect error: ret = " + ret);
				throw new BatchBizRuntimeException("mqapi.mq_connect error");
			}
			
			EaiApiData ApiData = new EaiApiData();
			ApiData.initPut();
			ApiData.m_mqput_t.in_intf_id = "TPS.SMS_SND_MFF";
			ApiData.m_mqput_t.in_intf_msg_id = "TPS.SMS_SND_MFF"
					+ System.currentTimeMillis();
			ApiData.m_mqput_t.in_intf_op_code = "SKTPS_SMS";

			ApiData.m_mqput_t.in_send_buf = smsContents;
			ApiData.m_mqput_t.in_send_buf_len = smsContents.getBytes().length;
			
			// mq put
			if ((mqapi.mq_put(ApiData)) != 0) {
				logMng.writeLogFile("mqapi.mq_put error");
				throw new BatchBizRuntimeException("mqapi.mq_put error");
			}
		
			// mq connection 반환 
			if ((ret = mqapi.mq_disconnect()) != 0) {
				logMng.writeLogFile("mqapi.mq_disconnect error");
				throw new BatchBizRuntimeException("mqapi.mq_disconnect error");
			}
		}
		catch(EaiException e) {
			throw new BatchBizRuntimeException("putMq eai error "+e.getMessage());
		}
		catch(Exception e) {
			throw new BatchBizRuntimeException("pubMq exception "+e.getMessage());
		}
		finally {
			logMng.writeLogFile("putMQ method end......");
		}
	}
}