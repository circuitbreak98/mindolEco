/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.usm.biz;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.CryptoUtils;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordFoundException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.common.sso.SHA1Hash;
import com.sktst.common.sso.SHA256SaltHash;
import com.sktst.common.telnet.TelnetWrapper;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보관리</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-15 18:09:37</li>
 * </ul>
 * 
 * @author 정재열 (jungjaeyul)
 */
public class BASUSM00200 extends BaseBizUnit {

	private static final String tpsDev = "10.40.10.29"; 	// 팀 개발 서버
	private static final String tpsUser = "150.2.236.145"; 	// 사용자 서버
	private static final String tpsWas1 = "10.40.10.25"; 	// 운영 WAS 2 서버
	private static final String tpsWas2 = "10.40.10.27"; 	// 운영 WAS 1 서버
	private static final String tpsProd = "10.40.10.21"; 	// 운영 DB서버

	private static final String notEffUser = "N";

	private static final String userSale = "D14";

	private static final String nullValue = "";

	/**
	 * 
	 * 
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData -
	 *	- field : user_id [사용자명] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public IDataSet getUserInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getUserInfo method start");
		}

		Map map = (Map) queryForObject("BASUSM00200.getUserInfo", requestData
				.getFieldMap());
		if (map == null) {
			throw new NoRecordFoundException();
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		//String pwd = (String) map.get("PWD");

		//		if (pwd != null && pwd.length() > 0) {
		//			String srcPwd = CryptoUtils.decode(pwd);
		//			map.put("PWD", srcPwd);
		//
		//			log.debug("getUserInfo srcPwd <" + map.get("PWD") + ">");
		//		}

		responseData.putFieldMap(map);
		return responseData;
	}

	/**
	 * 
	 * 
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 * @throws Exception 
	 */
	public IDataSet addUser(IDataSet requestData, IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("\n ****** addUser method start");
		}

		/**************************************************************
		* 2014.06.11 추가작업 : 썬(ksj)
		***************************************************************/
		//****************** 암호화 START *********************************
//		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");
//		
//		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
//		IRecordSet iSet = itemp.getRecordSet("cptItem");
//
//		IRecord rec = iSet.newRecord();
//		rec.add("ds_name", "ds_user"); // 암호화 할 데이터셋 명
//		rec.add("col_name1", "PWD"); // 암호화 컬럼1
//		iSet.addRecord(rec);
//		
//		requestData.putRecordSet("cptItem", iSet); // 암호화 정보 추가
//
//		IDataSet rsData = bco.encode(requestData, onlineCtx);
//		
//		requestData = rsData;
//		IRecordSet rsDsUser = requestData.getRecordSet("ds_user");
//		
//		Map requestMap = rsDsUser.getRecordMap(0);
		//****************** 암호화 END *********************************
        SHA1Hash md = new SHA1Hash();
        SHA256SaltHash enc = new SHA256SaltHash(); 

		Map requestMap = requestData.getFieldMap();

		String pwd = (String) requestMap.get("PWD");
		String userId = (String) requestMap.get("USER_ID");
		String newPwd = md.hash(pwd);
//		String newPwd256 = enc.encode(pwd);
		String newPwd256 = enc.encode(pwd,userId);
	
		
		
//		System.out.println("PWD:"+requestMap.get("PWD"));
//		System.out.println("newPwd:"+newPwd);
//		System.out.println("newPwd256:"+newPwd256);
		
//		requestMap.put("PWD", newPwd);
		requestMap.put("PWD", newPwd256);

		int updateCount = 0;

		String duty = (String) requestMap.get("DUTY");

		// ID 중복체크
		IRecordSet rs = queryForRecordSet("BASUSM00200.getUserId", requestData
				.getFieldMap());
		if (rs != null && rs.getRecordCount() > 0) {
			return DataSetFactory.createWithOKResultMessage(
					BaseConstants.CHECK_REG, new String[] { "사용자ID" });
		}

		if (!"9".equals(duty)) {
			// 사번(USER_CD) 중복체크
			IRecordSet rs2 = queryForRecordSet("BASUSM00200.getUserCd",
					requestData.getFieldMap());
			if (rs2 != null && rs2.getRecordCount() > 0) {
				return DataSetFactory.createWithOKResultMessage(
						BaseConstants.CHECK_REG, new String[] { "사번" });
			}
		}

		String uKeyId = (String) requestMap.get("U_KEY_ID");

		if (uKeyId != null && uKeyId.length() > 0) {
			// U_KEY_ID 중복체크
			IRecordSet rs3 = queryForRecordSet("BASUSM00200.getUserUKeyId",
					requestData.getFieldMap());
			if (rs3 != null && rs3.getRecordCount() > 0) {
				return DataSetFactory.createWithOKResultMessage(
						BaseConstants.CHECK_REG, new String[] { "U.Key ID" });
			}
		}

		if(pwd.length() < 8){
			throw new BizRuntimeException("***** 비밀번호는 문자, 숫자의 조합으로 8자리 이상으로 입력해주세요 *****");
		}
		
		if(pwd.equals(userId)){
			throw new BizRuntimeException("***** 비밀번호에 사용자ID를 사용할 수 없습니다. *****");
		}
		
	    String chr_pass_0;
	    String chr_pass_1;
	    int SamePass_0 = 0;
	    
	    for(int j=0; j < pwd.length()-1; j++) {
	    	
	        chr_pass_0 = String.valueOf(pwd.charAt(j));
	        chr_pass_1 = String.valueOf(pwd.charAt(j+1));
	        
	        //동일문자 카운트
	        if(chr_pass_0.equals(chr_pass_1)) {
	            SamePass_0 = SamePass_0 + 1;
	        }       
	    }
	    if(SamePass_0 > 1)	    {
	    	throw new BizRuntimeException("***** 동일문자를 3번 이상 사용할 수 없습니다. *****");
	    }

		int pass_num_check_count = 0;
		for( int k = 0 ; k < pwd.length() ; k++ ) {
			if(pwd.charAt(k) >= '0' && pwd.charAt(k) <= '9' ) {
			pass_num_check_count = pass_num_check_count +1;
			}
		}
		if( pwd.length() == pass_num_check_count ) {
			throw new BizRuntimeException("***** 비밀번호는 숫자와 영문자를 혼용하여야 합니다. *****");
		}
		
		
		int x = 0, count2 = 1, count3 = 1;

		for( x = 0 ; x < pwd.length() ; x++) {
			
			char temp_p = '\u0000';
			char temp_pass1 = '\u0000';
			char temp_c = '\u0000';

			temp_pass1 = pwd.charAt(x);// 입력받은 비밀번호의 char값

			if((x+1) == pwd.length()) {
				break;
			} else {
				temp_p = pwd.charAt(x+1);// 입력받은 비밀번호의 현재 index의 다음 index char값(index+1)
				temp_c = (char)(temp_pass1+1);// 입력받은 char값의 다음 char값 ( ex. a 다음은 b, 1 다음은 2...)
			}

			if(temp_c == temp_p) {// 입력받은 char값의 다음 index char값과 다음 char값을 비교
				count3 = count3 + 1;
			} else {
				count3 = 1;
			}

			if (count3 > 3) {
				break;
			}
		}

		if(count3 > 3) {
			throw new BizRuntimeException("***** 연속된 문자열(1234 또는 4321, abcd, dcba 등)을\n 4자 이상 사용 할 수 없습니다. *****");
		} 
		
		insert("BASUSM00200.addUser", requestMap, onlineCtx);

		/*
		log.debug("addUser SS "); //KIMYS
		if (uKeyId != null && uKeyId.length() > 0) {
			update("BASUSM00200.updateIfUkeyApplyYn", requestData.getFieldMap(), onlineCtx);
		}

		// MQ를 통해 판매점 포탈로 I/F 전송
		
		String mqLoginID     = (String) requestMap.get("PORTAL_USER_ID");
		String mqSysJobID    = (String) requestMap.get("USER_ID");
		String mqOrgID       = requestMap.get("PTL_ORG_ID")        == null ? "" : requestMap.get("PTL_ORG_ID").toString();
		String mqRelOrgID    = requestMap.get("PTL_REL_ORG_ID")    == null ? "" : requestMap.get("PTL_REL_ORG_ID").toString();
		String mqUserTypeCd  = (String) requestMap.get("USER_GRP");
		String mqHndStsCd    = "20";
		String mqEffStaDt    = requestMap.get("PTL_EFF_STA_DT")    == null ? "" : requestMap.get("PTL_EFF_STA_DT").toString();
		String mqEffEndDt    = "20301231";
		String mqAuditUserID = requestMap.get("PTL_AUDIT_USER_ID") == null ? "" : requestMap.get("PTL_AUDIT_USER_ID").toString();
		String mqAuditDtm    = requestMap.get("PTL_AUDIT_DTM")     == null ? "" : requestMap.get("PTL_AUDIT_DTM").toString();
		String mqTransDtm    = requestMap.get("PTL_TRANS_DTM")     == null ? "" : requestMap.get("PTL_TRANS_DTM").toString();
		String mqSysCl       = "TP";

		String mqSendMessage = rPad(mqLoginID, 15, "_");
		mqSendMessage += rPad(mqSysJobID, 15, "_");
		mqSendMessage += rPad(mqOrgID, 10, "_");
		mqSendMessage += rPad(mqRelOrgID, 10, "_");
		mqSendMessage += rPad(mqUserTypeCd, 3, "_");
		mqSendMessage += rPad(mqHndStsCd, 3, "_");
		mqSendMessage += rPad(mqEffStaDt, 8, "_");
		mqSendMessage += rPad(mqEffEndDt, 8, "_");
		mqSendMessage += rPad(mqAuditUserID, 10, "_");
		mqSendMessage += rPad(mqAuditDtm, 20, "_");
		mqSendMessage += rPad(mqTransDtm, 14, "_");
		mqSendMessage += rPad(mqSysCl, 2, "_");

		log.debug(" MQ Put Batch Call ...." + mqSendMessage);
		String mqCallCommand = "BASUKI08300_001.sh 1 2 3 4 " + mqSendMessage
				+ "\n";

		if (goTelnetClient(log, mqCallCommand) == 0) {
			log.debug(" MQ Put Batch Call Fail ....");
		}

		log.debug(" MQ Put Batch Call End ....");

		if (userSale.equals(mqUserTypeCd)) {
			update("BASUSM00200.updateMappingRecv", requestMap, onlineCtx);
		}
		*/

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_SIMPLE_MESSAGE_ID, new String[] {});
	}

	/**
	 * 
	 *
	 * @author 이영진 (leeyoungjin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public IDataSet updateUser(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("updateUser method start");
		}

		int updateCount = 0;
		Map requestMap = requestData.getFieldMap();
		//		String pwd = (String) requestMap.get("PWD");
		//		String newPwd = CryptoUtils.encode(pwd);
		//		requestMap.put("PWD", newPwd);

		String oldEffUser = (String) requestMap.get("PTL_EFF_USER_YN");
		String newEffUser = (String) requestMap.get("EFF_USER_YN");
		String userPortal = requestMap.get("PORTAL_USER_ID") == null ? ""
				: requestMap.get("PORTAL_USER_ID").toString();
		
        SHA1Hash md = new SHA1Hash();
        SHA256SaltHash enc = new SHA256SaltHash(); 
        
		String pwd = (String) requestMap.get("PWD");
		String userId = (String) requestMap.get("USER_ID");
		
		String newPwd = md.hash(pwd);
		String newPwd256 = enc.encode(pwd,userId);
		
		//requestMap.put("PWD", newPwd);
		requestMap.put("PWD", newPwd256);
		
		if(pwd.length() < 8){
			throw new BizRuntimeException("***** 비밀번호는 문자, 숫자의 조합으로 8자리 이상으로 입력해주세요 *****");
		}
		
		if(pwd.equals(userId)){
			throw new BizRuntimeException("***** 비밀번호에 사용자ID를 사용할 수 없습니다. *****");
		}
		
	    String chr_pass_0;
	    String chr_pass_1;
	    int SamePass_0 = 0;
	    
	    for(int j=0; j < pwd.length()-1; j++) {
	    	
	        chr_pass_0 = String.valueOf(pwd.charAt(j));
	        chr_pass_1 = String.valueOf(pwd.charAt(j+1));
	        
	        //동일문자 카운트
	        if(chr_pass_0.equals(chr_pass_1)) {
	            SamePass_0 = SamePass_0 + 1;
	        }       
	    }
	    if(SamePass_0 > 1)	    {
	    	throw new BizRuntimeException("***** 동일문자를 3번 이상 사용할 수 없습니다. *****");
	    }

		int pass_num_check_count = 0;
		for( int k = 0 ; k < pwd.length() ; k++ ) {
			if(pwd.charAt(k) >= '0' && pwd.charAt(k) <= '9' ) {
			pass_num_check_count = pass_num_check_count +1;
			}
		}
		if( pwd.length() == pass_num_check_count ) {
			throw new BizRuntimeException("***** 비밀번호는 숫자와 영문자를 혼용하여야 합니다. *****");
		}
		
		
		int x = 0, count2 = 1, count3 = 1;

		for( x = 0 ; x < pwd.length() ; x++) {
			
			char temp_p = '\u0000';
			char temp_pass1 = '\u0000';
			char temp_c = '\u0000';

			temp_pass1 = pwd.charAt(x);// 입력받은 비밀번호의 char값

			if((x+1) == pwd.length()) {
				break;
			} else {
				temp_p = pwd.charAt(x+1);// 입력받은 비밀번호의 현재 index의 다음 index char값(index+1)
				temp_c = (char)(temp_pass1+1);// 입력받은 char값의 다음 char값 ( ex. a 다음은 b, 1 다음은 2...)
			}

			if(temp_c == temp_p) {// 입력받은 char값의 다음 index char값과 다음 char값을 비교
				count3 = count3 + 1;
			} else {
				count3 = 1;
			}

			if (count3 > 3) {
				break;
			}
		}

		if(count3 > 3) {
			throw new BizRuntimeException("***** 연속된 문자열(1234 또는 4321, abcd, dcba 등)을\n 4자 이상 사용 할 수 없습니다. *****");
		} 
		
		
		updateCount = update("BASUSM00200.updateUser", requestMap, onlineCtx);
		
		
		
/*
		//UKEY ID 중복 체크.
		String sUkeyId = (String) requestMap.get("U_KEY_ID");
		String sUkeyIdOrg = (String) requestMap.get("U_KEY_ID_ORG");
		if (sUkeyId == null)
			sUkeyId = "";
		if (sUkeyIdOrg == null)
			sUkeyIdOrg = "";

		// 변경전과 변경후가 다를경우.
		if (!sUkeyId.equals(sUkeyIdOrg)) {

			// 값이 있는 경우.
			if (!sUkeyId.equals("")) {
				IRecordSet rs3 = queryForRecordSet("BASUSM00200.getUserUKeyId",
						requestMap);

				if (rs3 != null && rs3.getRecordCount() > 0) {

					throw new BizRuntimeException("PSMW5008",
							new String[] { "U.Key ID" });
				}
			}
		}

		updateCount = update("BASUSM00200.updateUser", requestMap, onlineCtx);

		// 유효사용자 여부가 다를 경우 SSO에 I/F
		if (!oldEffUser.equals(newEffUser) && !nullValue.equals(userPortal)) {
			String mqOrgID = null;
			String mqRelOrgID = null;
			String mqUserTypeCd = (String) requestMap.get("USER_GRP");

			if (userSale.equals(mqUserTypeCd)) {

				IRecordSet rsPortalSale = queryForRecordSet(
						"BASUSM00200.getPortalInfoSale", requestMap);

				if (rsPortalSale == null) {

					mqOrgID = "Not Found";
					mqRelOrgID = "Not Found";
				} else {
					mqOrgID = (String) rsPortalSale.get(0, "ORG_ID");
					mqRelOrgID = (String) rsPortalSale.get(0, "REL_ORG_ID");
				}

			} else {

				IRecordSet rsPortalPS = queryForRecordSet(
						"BASUSM00200.getPortalInfoPS", requestMap);

				if (rsPortalPS == null) {
					mqOrgID = "Not Found";
					mqRelOrgID = "Not Found";
				} else {

					mqOrgID = (String) rsPortalPS.get(0, "ORG_ID");
					mqRelOrgID = "";

				}

			}

			String mqLoginID = (String) requestMap.get("PORTAL_USER_ID");
			String mqSysJobID = (String) requestMap.get("USER_ID");
			//			String mqOrgID       = requestMap.get("PTL_ORG_ID")        == null ?"" : requestMap.get("PTL_ORG_ID").toString();
			//	        String mqRelOrgID    = requestMap.get("PTL_REL_ORG_ID")    == null ?"" : requestMap.get("PTL_REL_ORG_ID").toString();
			String mqAuditUserID = requestMap.get("PTL_AUDIT_USER_ID") == null ? ""
					: requestMap.get("PTL_AUDIT_USER_ID").toString();
			String mqAuditDtm = requestMap.get("PTL_AUDIT_DTM") == null ? ""
					: requestMap.get("PTL_AUDIT_DTM").toString();
			String mqTransDtm = requestMap.get("PTL_TRANS_DTM") == null ? ""
					: requestMap.get("PTL_TRANS_DTM").toString();
			String mqSysCl = "TP";
			String mqHndStsCd = "20";
			String mqEffStaDt = requestMap.get("PTL_EFF_STA_DT") == null ? ""
					: requestMap.get("PTL_EFF_STA_DT").toString();
			String mqEffEndDt = requestMap.get("PTL_EFF_END_DT") == null ? ""
					: requestMap.get("PTL_EFF_END_DT").toString();

			if (notEffUser.equals(newEffUser)) {
				mqHndStsCd = "30";
				mqEffEndDt = mqEffStaDt;
			} else {
				mqEffEndDt = "20301231";
			}

			log.debug(" EFF_DTM " + mqEffStaDt + "/" + mqEffEndDt);

			String mqSendMessage = rPad(mqLoginID, 15, "_");
			mqSendMessage += rPad(mqSysJobID, 15, "_");
			mqSendMessage += rPad(mqOrgID, 10, "_");
			mqSendMessage += rPad(mqRelOrgID, 10, "_");
			mqSendMessage += rPad(mqUserTypeCd, 3, "_");
			mqSendMessage += rPad(mqHndStsCd, 3, "_");
			mqSendMessage += rPad(mqEffStaDt, 8, "_");
			mqSendMessage += rPad(mqEffEndDt, 8, "_");
			mqSendMessage += rPad(mqAuditUserID, 10, "_");
			mqSendMessage += rPad(mqAuditDtm, 20, "_");
			mqSendMessage += rPad(mqTransDtm, 14, "_");
			mqSendMessage += rPad(mqSysCl, 2, "_");

			log.debug(" MQ Put Batch Call ...." + mqSendMessage);
			String mqCallCommand = "BASUKI08300_001.sh 1 2 3 4 "
					+ mqSendMessage + "\n";

			if (goTelnetClient(log, mqCallCommand) == 0) {
				log.debug(" MQ Put Batch Call Fail ....");
			}

			log.debug(" MQ Put Batch Call End ....");
		}

		String ifYn = requestData.getField("op_dt");
		if (updateCount > 0 && ifYn != null && "".compareTo(ifYn) != 0) {
			update("BASUSM00200.updateIfUkeyApplyYn",
					requestData.getFieldMap(), onlineCtx);
		}
*/
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_SIMPLE_MESSAGE_ID, null);
	}

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 *  - 사용자의 패스워드를 초기화 한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updatePwdReset(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("updatePwdReset method start");
		}

		int updateCount = 0;
		Map requestMap = requestData.getFieldMap();
		String pwd = "0000";
		String newPwd = CryptoUtils.encode(pwd);
		requestMap.put("PWD", newPwd);

		updateCount = update("BASUSM00200.updatePwdReset", requestMap,
				onlineCtx);

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_SIMPLE_MESSAGE_ID, null);
	}

	private static String rPad(String sStr, int size, String fStr) {
		byte[] b = sStr.getBytes();
		int len = b.length;
		int tmp = size - len;

		for (int i = 0; i < tmp; i++) {
			sStr += fStr;
		}

		return sStr;
	}

	//	=================================================================================================
	public int goTelnetClient(Log log, String sComd) {
		int iTs = 0;
		String sIP = "";
		String host = "";
		int port = 23;
		String sUser = "ps_mng";
		String sPwd = "sktngm12";

		TelnetWrapper telnetWrapper = new TelnetWrapper();
		try {
			InetAddress Address = InetAddress.getLocalHost();
			sIP = Address.getHostAddress();
		} catch (IOException e) {
			log.debug("TelnetClient: Got exception during Address: " + e);
			iTs = 0;
			return iTs;
		}

		//System.out.println("============>>>>IP: "+sIP);
		if (tpsWas1.equals(sIP) || tpsWas2.equals(sIP)) { //운영WAS1서버, 운영 WAS2서버
			host = tpsProd;
			sUser = "ps_mng";
			sPwd = "ps_mng";
		} else if (tpsUser.equals(sIP)) { //사용자서버
			host = sIP;
			sUser = "ps_mng";
			sPwd = "ps_mng";
		} else if (tpsDev.equals(sIP)) { //개발서버
			host = sIP;
			sUser = "ps_mng";
			sPwd = "sktngm12";
		} else {
			host = tpsDev;
			sUser = "ps_mng";
			sPwd = "sktngm12";
		}

		try {
			telnetWrapper.connect(host, port);
			telnetWrapper.login(sUser, sPwd);
			telnetWrapper.send("cd /app/batch");
			telnetWrapper.send(sComd);
			telnetWrapper.send("exit");

		} catch (IOException e) {
			log.debug("TelnetClient: Got exception during connect: " + e);
			iTs = 0;
			return iTs;
		}

		try {
			byte[] buf = new byte[256];
			while (telnetWrapper.read(buf) > -1) { // -1은 eof에 해당하는 코드 
				log.debug("============>>>>명령실행");
				//System.out.println("============>>>>명령실행");
			}

		} catch (IOException e) {
			log.debug("TelnetClient: Got exception in read/write loop: " + e);
			iTs = 0;
			return iTs;
		} finally {
			try {
				telnetWrapper.disconnect();
			} catch (IOException e) {
				log.debug("TelnetClient: got exception in disconnect: " + e);
				iTs = 0;
				return iTs;
			}
		}
		iTs = 1;
		return iTs;
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getDealInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getDealInfo method start");
		}

		IRecordSet  rsDealInfo = null;
		String  userGrp     = (String) requestData.getFieldMap().get("USER_GRP");
		if (userSale.equals(userGrp)) {

			rsDealInfo = queryForRecordSet("BASUSM00200.getDealInfoPCode", requestData.getFieldMap());

		} else {

			rsDealInfo = queryForRecordSet("BASUSM00200.getDealInfo", requestData.getFieldMap());

		}

		if (rsDealInfo == null) {
			rsDealInfo = new RecordSet("ds_DealInfo");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String.valueOf(rsDealInfo.getRecordCount()) });

		responseData.putRecordSet("ds_DealInfo", rsDealInfo);
		return responseData;

	}

}