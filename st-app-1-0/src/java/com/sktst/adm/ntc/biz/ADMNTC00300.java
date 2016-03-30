/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.ntc.biz;

import icasApi.AuthICAS;
import icasApi.common.CommRecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.eai.mq.api.EaiApiData;
import com.eai.mq.api.EaiMQApi;
import com.eai.mq.conf.EaiException;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : SMS 전송 관리</li>
 * <li>작성일 : 2009-04-21 15:44:29</li>
 * </ul>
 *
 * @author 최승호 (choiseungho)
 */
public class ADMNTC00300 extends BaseBizUnit {

	private String ICAS_LOGIN_ID  = "WPSM1241"; // ICAS 로그인 아이디
	private String ICAS_LOGIN_PW  = "ms8231ts"; // ICAS 로그인 비밀번호
	private String SMS_BLANK_DATA = " "; // SMS 전문 구성시 블랭크 채우는 데이타

	private String sProcValue     = "1"; 

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 				- SMS 목록 취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getSmsList(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00300.getSmsList method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("ADMNTC00300.getSmsList", requestData
				.getFieldMap(), onlineCtx);
		if (rs == null) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("output", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- SMS 그룹에 따른 유저 취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getSmsTarget(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00300.getSmsTarget method start");
		}

		// 2. CRUD 처리
		String sClassification = requestData.getField("classification");
		
		System.out.println("("+sClassification+")========================="+requestData.getFieldMap());
		String sQuery = "";
		if ("ZBAS_C_00250".compareTo(sClassification) == 0) {			/*사용자그룹별*/
			sQuery = "ADMNTC00300.getUserByUserGrp";
		} else if ("ZBAS_C_00130".compareTo(sClassification) == 0) {	/*영업그룹별*/
			sQuery = "ADMNTC00300.getUserByBizGrp";
		} else if ("ZBAS_C_00380".compareTo(sClassification) == 0) {	/*소속그룹별*/
			sQuery = "ADMNTC00300.getUserByUserCl";
		} else if ("ZBAS_C_00000".compareTo(sClassification) == 0) {	/*나의전화번호부*/
			sQuery = "ADMNTC00300.getMyAddrBook";
		} else if ("ZBAS_C_00240".compareTo(sClassification) == 0) {	/*거래처구분별*/
			sQuery = "ADMNTC00300.getUserByDealCl";
		} else if ("ZBAS_C_00001".compareTo(sClassification) == 0) {	/*성명검색*/
			sQuery = "ADMNTC00300.getUserByUserNm";
		} else if ("ZBAS_C_00002".compareTo(sClassification) == 0) {	/*거래처검색*/
			sQuery = "ADMNTC00300.getUserByDealCoNm";
		}
		
		IRecordSet rs = queryForRecordSet(sQuery, requestData.getFieldMap(),
				onlineCtx);

		if (rs == null) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("output", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 전송자 전화번호 취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getSenderTelNo(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00300.getSenderTelNo method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("ADMNTC00300.getSenderTelNo",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("output");
		}

		// 3. 결과 지정
		/*
		 IDataSet responseData = DataSetFactory.createWithOKResultMessage(
		 BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
		 .valueOf(rs.getRecordCount()) });
		 */
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_SIMPLE_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("output", rs);
		return responseData;

	}

	public IDataSet saveSmsInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00300.sendSms method start");
		}

		IRecordSet rsSms = requestData.getRecordSet("sms");
		Map smsMap = rsSms.getRecordMap(0);
		IRecordSet rsAddr = requestData.getRecordSet("addr");
		int targetCount = rsAddr.getRecordCount(); // 수신자의 수  
		int ret = 0; // mq 정상 유무 확인 변수
		String msgSerNum = "";
		IRecordSet rsSeq = null;

		// sms 메세지 일련번호 취득
		rsSeq = queryForRecordSet("ADMNTC00300.getSmsMsgSerNum", smsMap,
				onlineCtx);
		msgSerNum = rsSeq.get(0, "MSG_SER_NUM");

		// 3. CURD 처리
		// 3-1. sms 기본 정보 등록
		IRecordSet rsTranCnt = queryForRecordSet("ADMNTC00300.getTranCnt",
				smsMap, onlineCtx);
		String tranCnt = rsTranCnt.get(0, "tran_cnt");
		String tranDt = "";
		Map targetMap = null;

		smsMap.put("tran_cnt", tranCnt);
		smsMap.put("msg_ser_num", msgSerNum);

		insert("ADMNTC00300.addSmsBas", smsMap, onlineCtx);

		// 3-2. sms 상세 정보 등록
		if (targetCount > 0) {

			tranDt = (String) smsMap.get("tran_dt");
			targetMap = null;
		}

		// 2. mq 전송
		EaiMQApi mqapi = new EaiMQApi();

		// mq connection 획득 
		try {
			if ((ret = mqapi.mq_connect()) != 0) {
				throw new BizRuntimeException("mqapi.mq_connect error");
			}
		} catch (EaiException e) {
			throw new BizRuntimeException("mqapi error " + e.getMessage());
		}

		EaiApiData ApiData = new EaiApiData();

		int endIndex = targetCount;

		String smsContents = "";
		int i = 0;
		while (i < targetCount) {
			if (targetCount > i + 100) {
				endIndex = i + 100;
			} else {
				endIndex = targetCount;
			}

			// 전문
			smsContents = getSmsContents(smsMap, rsAddr, msgSerNum, i,
					endIndex, onlineCtx, tranDt, tranCnt);
			log.info("smsContents='" + smsContents + "'");

			// 기본정보  설정
			ApiData.initPut();
			ApiData.m_mqput_t.in_intf_id = "TPS.SMS_SND_MFF";
			ApiData.m_mqput_t.in_intf_msg_id = "TPS.SMS_SND_MFF"
					+ System.currentTimeMillis();
			ApiData.m_mqput_t.in_intf_op_code = "SKTPS_SMS";

			ApiData.m_mqput_t.in_send_buf = smsContents;
			ApiData.m_mqput_t.in_send_buf_len = smsContents.getBytes().length;

			// put
			ret = mqapi.mq_put(ApiData);
			//log.debug("  ret  = " + ret);
			if (ret != 0) {
				throw new BizRuntimeException("mqapi.mq_put error");
			}

			i = i + 100;
		}

		// mq connection 반환 
		if ((ret = mqapi.mq_disconnect()) != 0) {
			throw new BizRuntimeException("mqapi.mq_disconnect error");
		}

		// 4. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_SIMPLE_MESSAGE_ID, null);
	}

	/**
	 * SMS 전문 구성
	 * 
	 * @param baseInfo	sms 기본 정보
	 * @param detailInfo sms 상세 정보
	 * @return
	 */
	private String getSmsContents(Map baseInfo, IRecordSet detailInfo,
			String msgSerNum, int startIndex, int endIndex,
			IOnlineContext onlineCtx, String tranDt, String tranCnt) {

		Log log = LogManager.getLog(onlineCtx);

		// 전송일시
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String sendDate = (String) sdf.format(cal.getTime());

		StringBuffer sb = new StringBuffer();

		// HEADER 구성
		sb.append("0100"); 												//  1. 메세지 코드(4)
		sb.append("S"); 												//  2. SMS 처리 구분 코드(1)
		sb.append("SMS.TPS_SMS_RSLT_MFF" + getSpace(5, 6)); 			//  3. 인터페이스 ID(50)
	sb.append("_TPS_NTC_____" + getSpace(7, 1)); 						//  4. 프로그램 ID(20)
		sb.append(setSpace(msgSerNum, 15, SMS_BLANK_DATA, onlineCtx)); 	//  5. 메세지 일련번호(15)
		sb.append(getSpace(5, 4)); 										//  6. UKEY ID(20)
		sb.append(getSpace(5, 4)); 										//  7. UKEY ID(20)
		sb.append(getSpace(5, 4));										//  8. UKEY ID(20)
		sb.append(getSpace(3, 4));										//  9. 발신 전화번호(12)
		//sb.append(((String)baseInfo.get("TRAN_USER_ID")+(String)baseInfo.get("RTN_TEL_NO")).substring(0, 12)); 	
		sb.append(setSpace(getFixedTelno((String) baseInfo.get("RTN_TEL_NO")),
				12, SMS_BLANK_DATA, onlineCtx)); 						// 10. 회신 전화번호(12)
		sb.append(getSpace(8, 10)); 									// 11. 회신 URL(80)
		sb.append(sendDate);        									// 12. 생성일시(14)
		sb.append(getSpace(2, 7)); 									    // 13. 전송시작일시(14)
		sb.append(getSpace(2, 7));										// 14. 전송완료일시(14)

		IUserInfo iUserInfo = onlineCtx.getUserInfo();
		String sLoginId = String.valueOf(iUserInfo.getLoginId());
		String sAdminId = "psadm";
		// 원종윤대리 요청.
		if (sLoginId != null && sLoginId.equals(sAdminId)) {
			sb.append("0000-2359"); 									// 15-1. 전송가능시간대1(9)
		} else {
			sb.append("0700-2300"); 									// 15-2. 전송가능시간대1(9)
		}

		sb.append(getSpace(3, 3)); 										// 16. 전송가능시간대2(9)
		sb.append("Y"); 												// 17. 착신전환여부(1)
		sb.append(getSpace(1, 5)); 										// 18. 전송주기(5)
		sb.append("10 "); 												// 19. 타임아웃시간(3)
		sb.append("Y"); 												// 20. 응답여부(1)
		sb.append("0"); 												// 21. 장문처리유형(1)
		sb.append(getSpace(3, 3)); 										// 22. 템플릿 ID(9)
		sb.append(getSpace(8, 10)); 									// 23. 템플릿 MSG(80)
		String content = setSpace((String) baseInfo.get("context"), 160,
				SMS_BLANK_DATA, onlineCtx)
				+ getSpace(50, 36) + getSpace(8, 5);
		sb.append(content); 											// 24. SMS 내용(2000)

		// BODY 구성
		Map detailMap = null;
		String telNo = "";
		AuthICAS oAuthICAS = null;
		int checkCount = 0;
		int icasRetVal = 0;
		String sRetnSktYn = null;
		for (int i = startIndex; i < endIndex; i++) {
			detailMap = detailInfo.getRecordMap(i);
			telNo = (String) detailMap.get("cd");
			sb.append(setSpace(getFixedTelno(telNo), 12, SMS_BLANK_DATA, onlineCtx)); 	// 25. 수신전화번호리스트(12)
			sb.append(getSpace(5, 2)); 													// 26. 서비스관리번호(10)

			// AuthICAS 객체 생성
			oAuthICAS = new AuthICAS(ICAS_LOGIN_ID, ICAS_LOGIN_PW);

			// 메소드 추가
			oAuthICAS.addMethod("AuthCtrSvcActiveSV");

			// 파라메터 설정
			CommRecord oReqValue = new CommRecord();
			oReqValue.addValue("SVCNUMBER", getFixedTelno(telNo));

			log.info("SVCNUMBER='" + getFixedTelno(telNo) + "'");

			oAuthICAS.addParam(oReqValue);

			icasRetVal = oAuthICAS.call();

			// 결과
			sRetnSktYn = getSktCustYn(icasRetVal);
			sb.append(sRetnSktYn); // 27. SKT고객여부(1)

			// sms 상세내역 저장
			detailMap.put("tran_dt", tranDt);
			detailMap.put("tran_cnt", tranCnt);
			detailMap.put("skt_yn", sRetnSktYn);

			insert("ADMNTC00300.addSmsDtl", detailMap, onlineCtx);

			// AuthICAS 객체 클리어 
			oAuthICAS.clear();

			checkCount++;
		}

		if (checkCount < 100) {
			for (int i = 0, n = 100 - checkCount; i < n; i++) {
				sb.append(getSpace(5, 4) + getSpace(1, 3));
			}
		}
		return sb.toString();
	}

	/**
	 * skt 사용자 지정 
	 * 
	 * @param result
	 * @return
	 */
	private String getSktCustYn(int result) {
		String resultYn = "N";
		// 3442 : SKT AC인 고객, 3443 : SKT SP인 고객
		if (result == 3442 || result == 3443) {
			resultYn = "Y";
		}
		return resultYn;
	}

	/**
	 * 전화번호를 11자리로 고정
	 * 
	 * @param telNo 전화번호
	 * @return 결과 문자열
	 */
	private String getFixedTelno(String telNo) {

		if (telNo == null) {
			telNo = "";
		}

		if (telNo.length() == 10) {
			telNo = telNo.substring(0, 3) + "0" + telNo.substring(3);
		}

		return telNo;
	}

	/**
	 * 대상 문자열에 지정하는 길이 까지 지정하는 문자를 추가로 채운다
	 * 
	 * @param str 대상문자열
	 * @param total 총길이
	 * @param apStr 추가 문자
	 * @return 결과 문자열
	 */
	private String setSpace(String str, int total, String apStr,
			IOnlineContext onlineCtx) {
		//Log log = LogManager.getLog(onlineCtx);
		if (str == null) {
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
	 * 공백문자  생성
	 * 
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
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getSmsDtlList(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00300.getSmsDtlList method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = null;
		String sMsgSerNum = requestData.getField("msg_ser_num");

		// msg_ser_num 값에 따라 기존 또는 새로운 SMS 쿼리  분기.
		if (sMsgSerNum == null || sMsgSerNum.equals("")) {
			// 새로 적용된 SMS
			rs = queryForRecordSet("ADMNTC00300.getSmsDtlNewList", requestData.getFieldMap(), onlineCtx);
		} else {
			// 기존 SMS
			rs = queryForRecordSet("ADMNTC00300.getSmsDtlList",    requestData.getFieldMap(), onlineCtx);
		}

		if (rs == null) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("output", rs);
		return responseData;
	}

	/**
	 *  SMS를 전송한다.
	 *
	 * @author 한병곤 (hanbyunggon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveSendSms(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00300.saveSendSms method start");
		}

		IRecordSet rsSms = requestData.getRecordSet("sms");
		Map smsMap = rsSms.getRecordMap(0);
		IRecordSet rsAddr = requestData.getRecordSet("addr");

		// 전송차수 얻기.
		IRecordSet rsTranCnt = queryForRecordSet("ADMNTC00300.getTranCnt",
				smsMap, onlineCtx);
		String tranCnt = rsTranCnt.get(0, "tran_cnt");

		// SMS Master 저장.
		smsMap.put("tran_cnt", tranCnt);
		insert("ADMNTC00300.addSmsBas", smsMap, onlineCtx);

		// SMS Detail 저장, SMS 전송 table 저장.
		Map detailMap = null;
		IRecordSet rsSeq = null;
		String sCmpMsgId = null;
		String tranDt = (String) smsMap.get("tran_dt");
		String rtnTelNo = (String) smsMap.get("RTN_TEL_NO");
		String context = (String) smsMap.get("context");
		String sendTime = (String) smsMap.get("send_time");
		
		String[] arrayContext = null;

		for (int i = 0; i < rsAddr.getRecordCount(); i++) {
			detailMap = rsAddr.getRecordMap(i);
			
			// 메세지가 80byte 초과시 
			arrayContext = getParsingContext(context);
			
			for (int j = 0; j < arrayContext.length; j++) {

				// 메시지 고유 아이디 생성.(sequence)
				rsSeq = queryForRecordSet("ADMNTC00300.getCmpMsgId", smsMap,
						onlineCtx);

				sCmpMsgId = "";
				sCmpMsgId = rsSeq.get(0, "CMP_MSG_ID");
				
				detailMap.put("tran_dt", tranDt);
				detailMap.put("tran_cnt", tranCnt);
				detailMap.put("CMP_MSG_ID", sCmpMsgId);
				detailMap.put("RTN_TEL_NO", rtnTelNo);
				detailMap.put("SEND_TIME", sendTime);
				detailMap.put("context", arrayContext[j]);
				
				// sms Detail 저장
				
				log.debug("ADMNTC00300.saveReSend method start======================="+detailMap);
				
				insert("ADMNTC00300.addSmsDtl", detailMap, onlineCtx);

				// sms 전송 table 저장.
				insert("ADMNTC00300.addTelinkSms", detailMap, onlineCtx);
			}
		}

		// 4. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_SIMPLE_MESSAGE_ID, null);

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

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getMyAddr(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00300.getMyAddr method start");
		}

		IRecordSet rs = queryForRecordSet("ADMNTC00300.getMyAddr", requestData
				.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_myAddr");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_myAddr", rs);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            TRAND_DT	  : 재 전송 대상 일자
	 *            TRAN_CNT	  : 재 전송 대상 순번
	 *            NEW_TRAN_DT : 재 전송 일자
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveReSend(IDataSet requestData, IOnlineContext onlineCtx) throws Exception {
		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00300.saveReSend method start");
		}

		// 화면에서 보낸 Dataset 보관용
		IRecordSet rsParam  = requestData.getRecordSet("ds_dtl");
		Map oMapData        = rsParam.getRecordMap(0);

		String sTRAN_DT     = oMapData.get("TRAN_DT").toString();
		String sTRAN_CNT    = oMapData.get("TRAN_CNT").toString();
		String sNEW_TRAN_DT = oMapData.get("NEW_TRAN_DT").toString();

		// 전송차수 얻기.
		IRecordSet rsTranCnt    = queryForRecordSet("ADMNTC00300.getTranCnt", oMapData, onlineCtx);
		String    sNEW_TRAN_CNT = rsTranCnt.get(0, "tran_cnt");

		// SMS Master 저장.
		oMapData.put("NEW_TRAN_DT",  sNEW_TRAN_DT);
		oMapData.put("NEW_TRAN_CNT", sNEW_TRAN_CNT);

		insert("ADMNTC00300.addReSendSmsTran", oMapData, onlineCtx);


		// SMS Detail 저장, SMS 전송 table 저장. ============================================================

		// SMS 메시지 보내기위한 변수 선언
		Map        reSendTranDtl = new HashMap();				// TADM_SMS_TRAN_DTL Resend
		IRecordSet rsSeq         = null;
		String     sCmpMsgId     = null;
		String     sChk          =   "";
		int        sSeq          =    0;

		// 기존 전송정보 조회
		reSendTranDtl.put("TRAN_DT",      sNEW_TRAN_DT);
		reSendTranDtl.put("TRAN_CNT",     sNEW_TRAN_CNT);
		reSendTranDtl.put("OLD_TRAN_DT",  sTRAN_DT);
		reSendTranDtl.put("OLD_TRAN_CNT", sTRAN_CNT);


		// 전송하였던 정보를 기준으로 SMS 재 전송
		for (int i = 0; i < rsParam.getRecordCount(); i++) {

			sChk = rsParam.get(i, "CHK");
			if (sProcValue.equals(sChk)) {

				// 메시지 고유 아이디 생성.(sequence)
				rsSeq = queryForRecordSet("ADMNTC00300.getCmpMsgId", reSendTranDtl, onlineCtx);
				sCmpMsgId = rsSeq.get(0, "CMP_MSG_ID");

				sSeq = sSeq + 1;

				reSendTranDtl.put("TRAN_DT",    sNEW_TRAN_DT);
				reSendTranDtl.put("TRAN_CNT",   sNEW_TRAN_CNT);
				reSendTranDtl.put("SEQ",        sSeq);
				reSendTranDtl.put("NAME",       rsParam.get(i, "NAME"));
				reSendTranDtl.put("TEL_NO",     rsParam.get(i, "TEL_NO"));
				reSendTranDtl.put("ADDR_CAT",   rsParam.get(i, "ADDR_CAT"));
				reSendTranDtl.put("SKT_YN",     rsParam.get(i, "SKT_YN"));
				reSendTranDtl.put("CMP_MSG_ID", sCmpMsgId);

				reSendTranDtl.put("RTN_TEL_NO", rsParam.get(i, "RTN_TEL_NO"));
				reSendTranDtl.put("CD",         rsParam.get(i, "TEL_NO"));
				reSendTranDtl.put("CONTEXT",    rsParam.get(i, "CONTEXT"));

				// sms Detail 저장
				insert("ADMNTC00300.addReSendSmsTranDtl", reSendTranDtl, onlineCtx);

				// sms 전송 table 저장.
				insert("ADMNTC00300.addTelinkSms",        reSendTranDtl, onlineCtx);

			}
		}

		// 4. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_SIMPLE_MESSAGE_ID, null);
	}
}