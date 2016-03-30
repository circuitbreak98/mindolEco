package com.sktst.batch.dis.dcl.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : 장기보유재고 SMS 전송 </li>
 * <li>작성일 : 2010-06-17</li>
 * </ul>
 *
 * @author 이강수 (leekangsu)
 */
public class DISDCL08400 extends AbsBatchJobBiz {

	private static final String PROG_ID = "DISDCL08400";

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 이강수 (leekangsu)
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
			String sActFlag = "";
			if( request.size() > 1 ) {
				sProcDt = (String)request.get("args1");
				sActFlag = (String)request.get("args2");
				log.debug(sProcDt + ".sProcDt");
				log.debug(sActFlag + ".sActFlag");
			}

			//② Transaction 시작
			sqlMap.startTransaction();

			//③ 요청 데이터 처리, ④ DB 처리, ⑤ 결과 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");

			// SMS 대상 생성
			sendDisSmsList(sqlMap);
			
			// SMS전송
			sendDisSms(sqlMap);
			
			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}

			//⑥-1 Transaction Commit
			log.debug("#### 처리완료. ####");
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
	 * 
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendDisSmsList(SqlMapClient sqlMap) throws Exception {

		/**==========================================================
		 *   1. SMS 전송 대상 대리점 조회
		 *==========================================================*/
		List lstSmsAgency = (List)sqlMap.queryForList("DISDCL08400.getSmsAgencyList");
		if ( lstSmsAgency.size() == 0 ) {
			logMng.writeLogFile("SMS 전송 대상 대리점이 존재하지 않습니다.");
			log.debug(PROG_ID + " : SMS 전송 대상 대리점이 존재하지 않습니다.");
			return;
		}
		
		Map  smsAgencyMap = null;
		List disList      = null;
		Map  disMap       = null;
		List userList     = null;
		Map  userMap      = null;

		for (int idx = 0; idx < lstSmsAgency.size(); idx++) {
			smsAgencyMap = (Map)lstSmsAgency.get(idx);
			
			// 직영점
			if("A2".equals(smsAgencyMap.get("A2"))){
				smsAgencyMap.put("DEAL_CO_CL1", smsAgencyMap.get("A2"));
				sendDisList(sqlMap, smsAgencyMap);
			}
			// 판매점
			if("A3".equals(smsAgencyMap.get("A3"))){
				smsAgencyMap.put("DEAL_CO_CL1", smsAgencyMap.get("A3"));
				sendDisList(sqlMap, smsAgencyMap);
			}
			// 온라인 직영점
			if("B1".equals(smsAgencyMap.get("B1"))){
				smsAgencyMap.put("DEAL_CO_CL1", smsAgencyMap.get("B1"));
				sendDisList(sqlMap, smsAgencyMap);
			}
			// 온라인 판매점
			if("B2".equals(smsAgencyMap.get("B2"))){
				smsAgencyMap.put("DEAL_CO_CL1", smsAgencyMap.get("B2"));
				sendDisList(sqlMap, smsAgencyMap);
			}
			// M채널
			if("C1".equals(smsAgencyMap.get("C1"))){
				smsAgencyMap.put("DEAL_CO_CL1", smsAgencyMap.get("C1"));
				sendDisList(sqlMap, smsAgencyMap);
			}
		}
	}

	/**
	 * 
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendDisList(SqlMapClient sqlMap, Map smsAgencyMap) throws Exception {

		/**==========================================================
		 *   2. 대리점 장기보유재고 조회
		 *   A2 : 직영점
		 *   A3 : 판매점
		 *   B1 : 온라인직영점
		 *   B2 : 온라인판매점
		 *   C1 : M채널
		 *==========================================================*/
		List disList = (List)sqlMap.queryForList("DISDCL08400.getDisList", smsAgencyMap);
//log.debug("### smsAgencyMap ["+smsAgencyMap+"] 건수["+disList.size()+"]");		
		if ( disList.size() == 0 ) {
			return;
		}
		
		Map  disMap       = null;
		List userList     = null;
		Map  userMap      = null;

		for (int idx = 0; idx < disList.size(); idx++) {
			disMap = (Map)disList.get(idx);
		if(disMap != null){
	           sqlMap.insert("DISDCL08400.insertTdisLongTermDis", disMap);

				/**==========================================================
				 *   3. 장기재고 보유처별 SMS 수신 대상 조회
				 *   D14 : 판매점 M/M
				 *   D10 : (대리점) 재고담당
				 *   D22 : (대리점) ADMIN
				 *   D99 : 영업담당 : 거래처 정보에 있는 영업담당
				 *==========================================================*/
				userList = (List)sqlMap.queryForList("DISDCL08400.getUserList", disMap);
				for (int jdx = 0; jdx < userList.size(); jdx++) {
					userMap = (Map)userList.get(jdx);
					if(userMap != null){
						userMap.put("TRAN_DT"     , disMap.get("TRAN_DT"));
						userMap.put("PROD_CD"     , disMap.get("PROD_CD"));
						userMap.put("COLOR_CD"    , disMap.get("COLOR_CD"));
						userMap.put("SER_NUM"     , disMap.get("SER_NUM"));
			           sqlMap.insert("DISDCL08400.insertTdisLongTermDisUser", userMap);
					}
						
					}
				}
		}
	}
	/**
	 *   장기 보유 재고 SMS 전송
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendDisSms(SqlMapClient sqlMap) throws Exception {
		
		/**==========================================================
		 *   1. 대리점 조회
		 *==========================================================*/
		List lstAgency = (List)sqlMap.queryForList("DISDCL08400.getSmsSendAgencyList");
		if(lstAgency == null){
			return;
		}

		Map agencyMap = null;
		Map keyMap = new HashMap();
		List lstDealCo = null;
		Map dealCoMap = null;
		for (int i = 0; i < lstAgency.size(); i++) {
			agencyMap = (Map) lstAgency.get(i);
			keyMap.put("POS_AGENCY", agencyMap.get("POS_AGENCY"));
			keyMap.put("TRAN_DT"   , agencyMap.get("TRAN_DT"));
			sendDisSmsAdmin(sqlMap, keyMap);
			
			/**==========================================================
			 *   2. 대리점별 보유처 조회
			 *==========================================================*/
			lstDealCo = (List)sqlMap.queryForList("DISDCL08400.getSmsSendHldPlcIdList", keyMap);
			
			for (int j = 0; j < lstDealCo.size(); j++) {
				dealCoMap = (Map) lstDealCo.get(j);
				keyMap.put("HLD_PLC_ID" , dealCoMap.get("HLD_PLC_ID"));
				keyMap.put("HLD_PLC_NM" , dealCoMap.get("HLD_PLC_NM"));
				
				// 판매점 MM
				keyMap.put("USER_GRP"   , "D14");
				sendDisSmsMm(sqlMap, keyMap);

				// 영업사원
				keyMap.put("USER_GRP"   , "D99");
				sendDisSmsMm(sqlMap, keyMap);
			}
		}
	}

	/**
	 *   장기 보유 재고 SMS 전송
	 *   대리점 재고 담당, 대리점 Admin
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendDisSmsAdmin(SqlMapClient sqlMap, Map keyMap) throws Exception {

		/**==========================================================
		 *   1. Sms 수신 대상자
		 *==========================================================*/
		keyMap.put("AGENCY_YN", "Y");
		List smsUser  = (List)sqlMap.queryForList("DISDCL08400.getSmsUser", keyMap);
		if ( smsUser.size() == 0) {
			return;
		}

		/**==========================================================
		 *   2. Sms 내용 조회
		 *==========================================================*/
		List lstSmsText = (List)sqlMap.queryForList("DISDCL08400.getSmsText", keyMap);

		if ( lstSmsText.size() == 0) {
			return;
		}

		int jdx =0;
		String startYn = "Y";
		Map smsMap  = null;
		String smsText = (String) keyMap.get("TRAN_DT") + "일자 90 초과단말기 ";
		for (int idx = 0; idx < lstSmsText.size(); idx++) {
			smsMap = (Map) lstSmsText.get(idx);
			if("Y".equals(startYn)){
				if(jdx++ < 3){
					smsText = smsText + smsMap.get("SMS_TEXT") + "대 ";
				}else{
					jdx = 1;
					startYn = "N";
					keyMap.put("SMS_TEXT", smsText);
					sendSms(sqlMap, keyMap, smsUser);
					smsText = smsMap.get("SMS_TEXT") + "대 ";
				}
			}else{
				if(jdx++ < 5){
					smsText = smsText + smsMap.get("SMS_TEXT") + "대 ";
				}else{
					jdx = 1;
					keyMap.put("SMS_TEXT", smsText);
					sendSms(sqlMap, keyMap, smsUser);
                    smsText = smsMap.get("SMS_TEXT") + "대 ";
				}				
			}
		}
		// 최종 SMS 전송
		keyMap.put("SMS_TEXT", smsText);
		sendSms(sqlMap, keyMap, smsUser);
	}	
	
	/**
	 *   장기 보유 재고 SMS 전송
	 *   영업사원, 판매점/MM
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendDisSmsMm(SqlMapClient sqlMap, Map keyMap) throws Exception {

		/**==========================================================
		 *   1. Sms 수신 대상자
		 *==========================================================*/
		keyMap.put("AGENCY_YN", "N");
		List smsUser  = (List)sqlMap.queryForList("DISDCL08400.getSmsUser", keyMap);
		if ( smsUser.size() == 0 ) {
			return;
		}

		/**==========================================================
		 *   2. Sms 내용 조회
		 *==========================================================*/
		List lstSmsText = (List)sqlMap.queryForList("DISDCL08400.getSmsText", keyMap);
		if ( lstSmsText.size() == 0 ) {
			return;
		}

		int jdx =0;
		
		/**
		 *   D14 : 판매점/MM 대상
         *   D99 : 영업담당 대상
		 */
		String smsText = "";
		String userGrp = (String) keyMap.get("USER_GRP");
		if("D14".equals(userGrp)){
			smsText = "[PS공지]장기 보유재고를 보내드립니다. 조기소진 부탁드립니다.";
		}else{
			smsText = keyMap.get("HLD_PLC_NM") + "은 " + keyMap.get("TRAN_DT") + "일자 단말기 출고 후 90일이 초과되었습니다.";
		}
		keyMap.put("SMS_TEXT", smsText);
		sendSms(sqlMap, keyMap, smsUser);
		smsText = "";
		
		Map smsMap  = null;
		for (int idx = 0; idx < lstSmsText.size(); idx++) {
			smsMap = (Map) lstSmsText.get(idx);
			if(jdx++ < 5){
				smsText = smsText + smsMap.get("SMS_TEXT") + "대 ";
			}else{
				jdx = 1;
				keyMap.put("SMS_TEXT", smsText);
				sendSms(sqlMap, keyMap, smsUser);
                smsText = smsMap.get("SMS_TEXT") + "대 ";
			}				
		}
		// 최종 SMS 전송
		keyMap.put("SMS_TEXT", smsText);
		sendSms(sqlMap, keyMap, smsUser);
	}

	/**
	 *   SMS 전송
	 *   
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendSms(SqlMapClient sqlMap, Map keyMap, List smsUser) throws Exception {
		/**==============================================
		 *   SMS 전송
		 *   TADM_SMS_TRAN     : SMS전송
		 *   TADM_SMS_TRAN_DTL : SMS전송내역
		 *   TELINK_SMS        : 텔링크
		 *   
		 *   요청순번 찾기
		 *   tran_cnt     : 전송차수 얻기
		 *   MSG_SER_NUM  : sms 메세지 일련번호
		 *   cmp_msg_id   : 텔링크 고유 아이디
		 **==============================================*/
		Map smsMap = new HashMap();
		Map tranCntMap     = (Map) sqlMap.queryForObject("DISDCL08400.getTranCnt", keyMap);
		Map smsMsgSerMap   = (Map) sqlMap.queryForObject("DISDCL08400.getSmsMsgSerNum", keyMap);
		
		Map cmpMsgIdMap    = null;
        Map  smsUserMap    = smsUserMap = (Map) smsUser.get(0);
        String smsText     = (String) keyMap.get("SMS_TEXT");

        /**
         *   길이가 20 이상인 경우 20으로 줄인다.
         */
        if(smsText.length() > 20){
            smsText = smsText.substring(0,20);
        }

		smsMap.put("TRAN_DT"      , tranCntMap.get("TRAN_DT"));        // SMS 전송일
		smsMap.put("TRAN_CNT"     , tranCntMap.get("TRAN_CNT"));       // SMS 전송차수 
		smsMap.put("TRAN_USER_ID" , smsUserMap.get("TRAN_USER_ID"));   // SMS 전송자 
		smsMap.put("TITLE"        , smsText);                          // TITLE
		smsMap.put("SND_MSG"      , keyMap.get("SMS_TEXT"));           // SMS 전송내용
		smsMap.put("MSG_SER_NUM"  , smsMsgSerMap.get("MSG_SER_NUM"));  // 메시지 일련번호
		smsMap.put("SMS_JOB_CL"   , "02");                             // sms 업무구분 "02" 재고 , "03" : 영업 
		
		sqlMap.insert("DISDCL08400.insertTadmSmsTran", smsMap);
		
        for (int i = 0; i < smsUser.size(); i++) {
			smsUserMap = (Map) smsUser.get(i);

			cmpMsgIdMap    = (Map) sqlMap.queryForObject("DISDCL08400.getCmpMsgId");

			smsMap.put("USER_NM"    , smsUserMap.get("USER_NM"));
			smsMap.put("MBL_PHON"   , smsUserMap.get("MBL_PHON"));
			smsMap.put("CMP_MSG_ID" , cmpMsgIdMap.get("CMP_MSG_ID"));    // 텔링크 고유 아이디  
			
			sqlMap.insert("DISDCL08400.insertTadmSmsTranDtl", smsMap);

			sqlMap.insert("DISDCL08400.insertTelinkSms", smsMap);
		}
	}
	
}