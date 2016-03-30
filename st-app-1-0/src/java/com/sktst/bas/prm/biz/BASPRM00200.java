/*
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.prm.biz;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.common.telnet.TelnetWrapper;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-21 10:03:19</li>
 * </ul>
 *
 * @author 심연정 (shimyunjung)
 */
public class BASPRM00200 extends BaseBizUnit {
	private static final String tpsDev     = "10.40.10.29";		// 팀 개발 서버
	private static final String tpsUser    = "150.2.236.145";  	// 사용자 서버
	private static final String tpsWas1    = "10.40.10.25";  	// 운영 WAS 2 서버
	private static final String tpsWas2    = "10.40.10.27";  	// 운영 WAS 1 서버
	private static final String tpsProd    = "10.40.10.21";  	// 운영 DB서버

	private static final String userD14    = "Y";

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * ==> 거래처 정보 수정
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateDealCo(IDataSet requestData, IOnlineContext onlineCtx) {
		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPRM00200.updateDealCo method start");
		}
		
		// 2. CRUD 처리	
		int nResult = update("BASPRM00200.updateDealCo", requestData.getFieldMap(), onlineCtx); // 기존 데이타 갱신

		// 2차점에서 3차점으로 변경 시 
		// 기존 2차점이 다른 3차점의 정산처로 등록 되어 있는 경우 return		
		String sTwo = "2"; // 2차점.
		String sThe = "3"; // 3차점.
		
		String sDealCoCl2Org = requestData.getField("DEAL_CO_CL2_ORG");
		String sDealCoCl2 = requestData.getField("DEAL_CO_CL2");
		String sBlank = "";
		if(sDealCoCl2Org != null && !sDealCoCl2Org.equals(sBlank)
			&& sDealCoCl2 != null && !sDealCoCl2.equals(sBlank)) {
			if(sDealCoCl2Org.equals(sTwo) 
				&&  sDealCoCl2.equals(sThe)){
				
				IRecordSet rsCheck = queryForRecordSet("BASPRM00200.getCheckStlPlc",
						requestData.getFieldMap(), onlineCtx);
				
				if (rsCheck != null && rsCheck.getRecordCount() > 0 ) {
					//해당 거래처가 다른 거래처의 정산처로 등록되어 2차점에서 3차점으로 수정 할 수 없습니다.
					throw new BizRuntimeException("PSMW3003");
				}		
				
				IRecordSet rsDisHldPlcCheck = queryForRecordSet("BASPRM00200.getCheckDisHldPlc",
						requestData.getFieldMap(), onlineCtx);
				
				if (rsDisHldPlcCheck != null && rsDisHldPlcCheck.getRecordCount() > 0 ) {
					//해당 거래처가 다른 거래처의 재고 보유처로 등록되어 2차점에서 3차점으로 수정 할 수 없습니다.
					throw new BizRuntimeException("PSMW3013");
				}					
			}
		}
		
		// 거래종료 시 재고를 확인하여 가용재고가 있는 경우 거래 종료를 하지 못한다.
		String sDealEndYnOrg = requestData.getField("DEAL_END_YN_ORG");
		String sDealEndYn = requestData.getField("DEAL_END_YN");
		String sYes = "Y";
		String sNo  = "N";
		if(sDealEndYnOrg.equals(sNo) && sDealEndYn.equals(sYes) ){
			IRecordSet rsDisCheck = queryForRecordSet("BASPRM00200.getCheckDis", requestData.getFieldMap(), onlineCtx);
			
			if (rsDisCheck != null && rsDisCheck.getRecordCount() > 0 ) {
				//가용재고를 보유하고 있는 거래처이므로 거래종료가 불가능합니다.
				throw new BizRuntimeException("PSMW3004");
			}				
		}
		
		// 2차점을 거래종료 시키는 경우  이 2차점을 정산처나 재고 보유처로 가진 3차점이 있는 경우를 체크.
		if(sDealCoCl2.equals(sTwo) && sDealEndYn.equals(sYes) ){		
			
			IRecordSet rsStlPlcCheck = queryForRecordSet("BASPRM00200.getCheckStlPlc", requestData.getFieldMap(), onlineCtx);
			
			if (rsStlPlcCheck != null && rsStlPlcCheck.getRecordCount() > 0 ) {
				//해당 거래처를 정산처로 가지고 있는 3차점이 존재하므로 거래 종료가 불가능합니다.
				throw new BizRuntimeException("PSMW3014",new String[] { "정산처" });
			}	
			
			IRecordSet rsDisHldPlcCheck = queryForRecordSet("BASPRM00200.getCheckDisHldPlc", requestData.getFieldMap(), onlineCtx);
			
			if (rsDisHldPlcCheck != null && rsDisHldPlcCheck.getRecordCount() > 0 ) {
				//해당 거래처를 재고 보유처로 가지고 있는 3차점이 존재하므로 거래 종료가 불가능합니다.
				throw new BizRuntimeException("PSMW3014",new String[] { "재고 보유처" });
			}				
		}
		
		if (nResult == 1) {

			log.debug("EFF_END_DTM_OLD : " + requestData.getField("EFF_END_DTM_OLD"));
			log.debug("EFF_STA_DTM_NEW : " + requestData.getField("EFF_STA_DTM_NEW"));
			insert("BASPRM00200.addChildDealCo", requestData.getFieldMap(), onlineCtx); // 일련번호 증가한  데이타 추가
		}		
		
		// 거래처가 거래종료된 경우 해당 거래처를 근무지로 사용하는 사용자의 유효여부 변경
		// 판매점포탈 SSO I/F 추가 - 2010.08.10  KIMYS
		if (sYes.equals(sDealEndYn)) {
			nResult = update("BASPRM00200.updateUserMgmt", requestData.getFieldMap(), onlineCtx);

			/* ============= Start MQ Put ===================== */

			IRecordSet rsPortalUser = queryForRecordSet("BASPRM00200.getPortalUser", requestData.getFieldMap());

			if (rsPortalUser != null) {

				for (int i = 0; i < rsPortalUser.getRecordCount(); i++) {

					String mqUserTypeCd  = (String) rsPortalUser.get(i, "USER_GRP");
					String mqLoginID     = (String) rsPortalUser.get(i, "PORTAL_USER_ID");
					String mqSysJobID    = (String) rsPortalUser.get(i, "USER_ID");
					String mqOrgID       = rsPortalUser.get(i, "PTL_ORG_ID")        == null ? "" : rsPortalUser.get(i, "PTL_ORG_ID").toString();
					String mqRelOrgID    = rsPortalUser.get(i, "PTL_REL_ORG_ID")    == null ? "" : rsPortalUser.get(i, "PTL_REL_ORG_ID").toString();
					String mqAuditUserID = rsPortalUser.get(i, "PTL_AUDIT_USER_ID") == null ? "" : rsPortalUser.get(i, "PTL_AUDIT_USER_ID").toString();
					String mqAuditDtm    = rsPortalUser.get(i, "PTL_AUDIT_DTM")     == null ? "" : rsPortalUser.get(i, "PTL_AUDIT_DTM").toString();
					String mqTransDtm    = rsPortalUser.get(i, "PTL_TRANS_DTM")     == null ? "" : rsPortalUser.get(i, "PTL_TRANS_DTM").toString();
					String mqSysCl       = rsPortalUser.get(i, "PTL_SYS_CL")        == null ? "" : rsPortalUser.get(i, "PTL_SYS_CL").toString();
					String mqHndStsCd    = rsPortalUser.get(i, "PTL_HND_STS_CD")    == null ? "" : rsPortalUser.get(i, "PTL_HND_STS_CD").toString();
					String mqEffStaDt    = rsPortalUser.get(i, "PTL_EFF_STA_DT")    == null ? "" : rsPortalUser.get(i, "PTL_EFF_STA_DT").toString();
					String mqEffEndDt    = rsPortalUser.get(i, "PTL_EFF_END_DT")    == null ? "" : rsPortalUser.get(i, "PTL_EFF_END_DT").toString();

					String mqSendMessage  = rPad(mqLoginID, 15, "_");
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
				}

			}

			log.debug(" MQ Put Batch Call End ....");
			/* -------------  End  --------------------- */
		}

		// 판매회계 저장. 
		IRecordSet rsCustMaster = requestData.getRecordSet("ds_custMaster");		

		Map mCustMaster = null;
		if (rsCustMaster != null && rsCustMaster.getRecordCount() > 0) {
			
			String sDealCl1 = requestData.getField("DEAL_CO_CL1");
			String sDealCl2 = requestData.getField("DEAL_CO_CL2");
			String sDis = "Z1"; // 물류창고
			String sSal = "A5"; // 영업사원
			String sThr = "3";  // 거래처 유형 3차점			
			
			// 물류창고(Z1), 영업사원(A5) 인 경우 제외.
			if(sDealCl1.equals(sDis) || sDealCl1.equals(sSal)){
				log.debug("interface pass");
			}else{
				
				if(sDealCoCl2 != null && sDealCoCl2.equals(sThe)){				
					log.debug("3차점 pass");
				}else{
					mCustMaster = rsCustMaster.getRecordMap(0);
					mCustMaster.put("DEAL_CO_CD", requestData.getField("DEAL_CO_CD"));
					
					// 삭제 후 등록.
					delete("BASPRM00200.deleteCustMaster", mCustMaster, onlineCtx);					
					insert("BASPRM00200.addCustMaster", mCustMaster, onlineCtx);		
				}
			}
		}
		
		// 3. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "1" });
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 					-  거래처 등록
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addDealCo(IDataSet requestData, IOnlineContext onlineCtx) {
		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPRM00200.addDealCo method start");
		}

		// 2. CRUD 처리	
		insert("BASPRM00200.addDealCo", requestData.getFieldMap(), onlineCtx);

		// 판매회계 저장. 
		/*IRecordSet rsCustMaster = requestData.getRecordSet("ds_custMaster");		

		Map mCustMaster = null;
		if (rsCustMaster != null && rsCustMaster.getRecordCount() > 0) {
			mCustMaster = rsCustMaster.getRecordMap(0);

			String sDealCl1 = requestData.getField("DEAL_CO_CL1");
			String sDealCl2 = requestData.getField("DEAL_CO_CL2");	
			String sDis = "Z1"; // 물류창고
			String sSal = "A5"; // 영업사원
			String sThr = "3";  // 거래처 유형 3차점

			if(sDealCl1.equals(sDis) || sDealCl1.equals(sSal)){
				// 물류창고(Z1), 영업사원(A5) 인 경우 제외.
				log.debug("interface pass");
			}else{

				if(sDealCl2 != null && sDealCl2.equals(sThr)){				
					log.debug("3차점 pass");
				}else{				
					mCustMaster.put("DEAL_CO_CD", requestData.getField("DEAL_CO_CD"));
					insert("BASPRM00200.addCustMaster", mCustMaster,onlineCtx);	
				}
			}
		}	*/	

		// 3. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "1" });

	}

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * ==> 거래처 코드 생성
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getDealCoCd(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPRM00200.getDealCoCd start >>>>>>>");
		}

		IRecordSet rs = queryForRecordSet("BASPRM00200.getDealCoCd",
				requestData.getFieldMap(), onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_dealCoCd", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 					-  U_KEY_CD 와 사업자 번호의 중복 여부 체크
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet checkDuplication(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPRM00200.checkDuplication method start");
		}

		IRecordSet rs = null;
		String uKeyAgencyCd = requestData.getField("ukey_agency_cd");
		if(uKeyAgencyCd != null && "".compareTo(uKeyAgencyCd)  != 0)
		{
			rs = queryForRecordSet("BASPRM00200.checkDuplication",
					requestData.getFieldMap(), onlineCtx);
		}
		
		String dealCoCl1 = requestData.getField("deal_co_cl1");
		String dealEndYn = requestData.getField("DEAL_END_YN"); // 종료
		String saleStopYn = requestData.getField("SALE_STOP_YN"); // 판매정지
		String sAgencyCd = "A1"; // 대리점
		String sYes = "Y";
		String sEndDate = "99991231235959";
		
		String dealCoCd = requestData.getField("deal_co_cd");
		IRecordSet rs3 = null;		
		
		if(dealCoCl1.equals(sAgencyCd)){
			
			/****************************************************************
			 *  1-1. 대리점 중복 체크.
			 ****************************************************************/
			IRecordSet rs1 = queryForRecordSet("BASPRM00200.getCheckAgency", requestData
					.getFieldMap(), onlineCtx);

			if (rs1 != null && rs1.getRecordCount() > 0) {
				
				Map mUkey = rs1.getRecordMap(0);
				
				// 이미 등록된 U.key 대리점 코드 입니다.
				throw new BizRuntimeException("PSMW5008",new String[] { "U.key 대리점 코드["+mUkey.get("deal_co_nm")+"]" });
			}	
			
		}else{
			
			/****************************************************************
			 *  1-2. 거래처 중복 체크.
			 ****************************************************************/			
			
			// 중복된 ukey_agency_cd가 존재시 단 종료/판매정지가 아닌 경우만.
			if (rs != null && rs.getRecordCount() > 0 && !dealEndYn.equals(sYes) && !saleStopYn.equals(sYes)) {
				Map mRs = rs.getRecordMap(0); // 첫번째.
				
				// 중복된 데이터의 종료일자가 99991231인 경우와 아닌 경우 메세지 분기.
				if(mRs.get("eff_end_dtm").equals(sEndDate)){
					throw new BizRuntimeException(BaseConstants.CHECK_REG, new String[] { "["+mRs.get("deal_co_nm")+"]"+"거래처의 U.Key 코드"});	
				}else{
					// 등록하고자 하는 거래처의 거래개시일자 이전에 동일한 P코드를 가진 거래처가 존재합니다.
					throw new BizRuntimeException("PSMW3008", new String[] { "["+mRs.get("deal_co_nm")+"]"});	
				}
			}				
		}
		
		/****************************************************************
		 *  2. 계좌번호 중복 체크.
		 ****************************************************************/		
		String sPmagDpstAccNo = requestData.getField("PMAG_DPST_ACC_NO"); //요금계좌 
		String sCasaDpstAccNo = requestData.getField("CASA_DPST_ACC_NO"); //기타계좌 
		String sSlcmDfryAccNo = requestData.getField("SLCM_DFRY_ACC_NO"); //출금계좌 
		
		String sBlank = "";
		Map mAccNo = requestData.getFieldMap();
		
		if(sPmagDpstAccNo != null && !sPmagDpstAccNo.equals(sBlank)){
			
			mAccNo.put("acc_no", sPmagDpstAccNo);
			
			IRecordSet rsBankCheck = queryForRecordSet("BASPRM00200.getCheckBank",mAccNo, onlineCtx);
			
			if (rsBankCheck != null && rsBankCheck.getRecordCount() > 0 ) {
				
				Map mUkey = rsBankCheck.getRecordMap(0);
				
				// @는 @거래처에서 이미 등록한 계좌입니다. 
				throw new BizRuntimeException("PSMW3005",new String[] { "요금계좌["+mUkey.get("acc_no")+"]","["+mUkey.get("deal_co_nm")+"]" });				
			}				
		}
		
		if(sCasaDpstAccNo != null && !sCasaDpstAccNo.equals(sBlank)){
			
			mAccNo.put("acc_no", sCasaDpstAccNo);
			
			IRecordSet rsBankCheck = queryForRecordSet("BASPRM00200.getCheckBank",mAccNo, onlineCtx);
			
			if (rsBankCheck != null && rsBankCheck.getRecordCount() > 0 ) {
				
				Map mUkey = rsBankCheck.getRecordMap(0);
				
				// @계좌는 @거래처에서 이미 등록한 계좌입니다. 
				throw new BizRuntimeException("PSMW3005",new String[] { "기타계좌["+mUkey.get("acc_no")+"]","["+mUkey.get("deal_co_nm")+"]" });				
			}				
		}	
		
		// 출금계좌는 다른 유효한 출금계좌들 중에서 있는지 확인하여 ui로 정보를 알려준다.
		StringBuffer sbSlcmDfryMsg = new StringBuffer();
		String sSlcmDfryMsg = "";
		if(sSlcmDfryAccNo != null && !sSlcmDfryAccNo.equals(sBlank)){
			
			mAccNo.put("acc_no", sSlcmDfryAccNo);
			
			IRecordSet rsBankCheck = queryForRecordSet("BASPRM00200.getChecSlcmDfry",mAccNo, onlineCtx);
			
			if (rsBankCheck != null && rsBankCheck.getRecordCount() > 0 ) {
				
				Map mUkey = rsBankCheck.getRecordMap(0);
				
				// @계좌는 @거래처에서 이미 등록한 계좌입니다. 
				sbSlcmDfryMsg.append("출금계좌 [")
				           .append(mUkey.get("acc_no"))
				           .append("]계좌는 [")
				           .append(mUkey.get("deal_co_nm"))
				           .append("]거래처에서 이미 등록한 계좌입니다."); 
			}else{
				sbSlcmDfryMsg.append("N");
			}
			
			sSlcmDfryMsg = sbSlcmDfryMsg.toString();
		}	
		
		if (dealCoCd == null || "".compareTo(dealCoCd) == 0) {
			// 신규 등록 시	
			rs3 = queryForRecordSet("BASPRM00200.getDealCoCd", requestData
					.getFieldMap(), onlineCtx);
		}else{
			// 변경 시
			/****************************************************************
			 *  3. 요금계좌, 기타계좌 변경 시 
			 *     변경 전 계좌의 입금잔액 체크.
			 ****************************************************************/				
			String sPmagDpstAccNoOld = requestData.getField("PMAG_DPST_ACC_NO_OLD"); //변경 전 요금계좌 
			String sCasaDpstAccNoOld = requestData.getField("CASA_DPST_ACC_NO_OLD"); //변경 전 기타계좌 
			IRecordSet rs1 = null;
			Map mParam  = new HashMap();
			Map mReturn = null;
			mParam.put("DPST_ACC_NO_OLD", sPmagDpstAccNoOld);
			mParam.put("APLY_DT", requestData.getField("APLY_DT"));
			mParam.put("APLY_DTM", requestData.getField("APLY_DTM"));
			String sZero = "0";
			
			if(sPmagDpstAccNoOld != null && !sPmagDpstAccNoOld.equals("")){
				
				if(!sPmagDpstAccNoOld.equals(sPmagDpstAccNo)){
					
					rs1 = queryForRecordSet("BASPRM00200.getCheckAccNo", mParam, onlineCtx);
					mReturn = rs1.getRecordMap(0);
					
					if(!mReturn.get("before_month_amt").equals(sZero) && mReturn.get("dpst_dt") != null ){
						//{0}의 전월잔액{1}과 변경적용일 이후{2} 입금잔액이 존재하므로 변경할 수 없습니다.
						throw new BizRuntimeException("PSMW3009",new String[] {"요금계좌", "["+mReturn.get("before_month_amt")+"]","["+mReturn.get("dpst_dt")+"]" });
					}else if(!mReturn.get("before_month_amt").equals(sZero) && mReturn.get("dpst_dt") == null ){
						//{0}의 변경적용일 전월 잔액{1}이 존재하므로 변경 할수 없습니다.
						throw new BizRuntimeException("PSMW3010",new String[] {"요금계좌", "["+mReturn.get("before_month_amt")+"]" });
					}else if(mReturn.get("before_month_amt").equals(sZero) && mReturn.get("dpst_dt") != null ){
						//{0}의 입금잔액이 존재하므로 변경할 수 없습니다. \n\n변경적용일을 입금정산일{1} 이후로 수정하세요.
						throw new BizRuntimeException("PSMW3011",new String[] {"요금계좌", "["+mReturn.get("dpst_dt")+"]"});
					}
				}
			}
			
			mParam.put("DPST_ACC_NO_OLD", sCasaDpstAccNoOld);
			
			if(sCasaDpstAccNoOld != null && !sCasaDpstAccNoOld.equals("")){
				
				if(!sCasaDpstAccNoOld.equals(sCasaDpstAccNo)){
					
					rs1 = queryForRecordSet("BASPRM00200.getCheckAccNo", mParam, onlineCtx);
					mReturn = rs1.getRecordMap(0);
					
					if(!mReturn.get("before_month_amt").equals(sZero) && mReturn.get("dpst_dt") != null ){
						//{0}의 전월잔액{1}과 변경적용일 이후{2} 입금잔액이 존재하므로 변경할 수 없습니다.
						throw new BizRuntimeException("PSMW3009",new String[] {"기타계좌", "["+mReturn.get("before_month_amt")+"]","["+mReturn.get("dpst_dt")+"]" });
					}else if(!mReturn.get("before_month_amt").equals(sZero) && mReturn.get("dpst_dt") == null ){
						//{0}의 변경적용일 전월 잔액{1}이 존재하므로 변경 할수 없습니다.
						throw new BizRuntimeException("PSMW3010",new String[] {"기타계좌", "["+mReturn.get("before_month_amt")+"]" });
					}else if(mReturn.get("before_month_amt").equals(sZero) && mReturn.get("dpst_dt") != null ){
						//{0}의 입금잔액이 존재하므로 변경할 수 없습니다. \n\n변경적용일을 입금정산일{1} 이후로 수정하세요.
						throw new BizRuntimeException("PSMW3011",new String[] {"기타계좌", "["+mReturn.get("dpst_dt")+"]"});
					}
				}
			}			
			
			/****************************************************************
			 *  4. 정산처, 재고보유처 변경 시 
			 *     변경 전 정산처,변경 전 재고보유처의 판매건 체크.
			 ****************************************************************/					
			String sStlPlc       = requestData.getField("STL_PLC");         //정산처 
			String sStlPlcOld    = requestData.getField("STL_PLC_OLD");     //변경 전 정산처 
			String sDisHldPlc    = requestData.getField("DIS_HLD_PLC");     //재고보유처 			
			String sDisHldPlcOld = requestData.getField("DIS_HLD_PLC_OLD"); //변경 전 재고보유처 	
			
			mParam.put("DEAL_CO_CD", dealCoCd);
			mParam.put("PLC_OLD", sStlPlcOld);
			
			if(sStlPlcOld != null && !sStlPlcOld.equals("")){
				
				if(!sStlPlcOld.equals(sStlPlc)){
					
					rs1 = queryForRecordSet("BASPRM00200.getCheckSaleProd", mParam, onlineCtx);
					mReturn = rs1.getRecordMap(0);
					
					if(mReturn.get("sale_chg_dtm") != null && !mReturn.get("sale_chg_dtm").equals("") ){
						//적용일시 이 후에 {0}의 판매건이 존재 합니다[판매일자{1}].\n\n적용일시를 판매일자 이 후로 입력하세요. 
						throw new BizRuntimeException("PSMW3012",new String[] {"변경전 정산처", ":"+mReturn.get("sale_chg_dtm")});
					}
				}
			}			
			
			mParam.put("PLC_OLD", sDisHldPlcOld);
			
			if(sDisHldPlcOld != null && !sDisHldPlcOld.equals("")){
				
				if(!sDisHldPlcOld.equals(sDisHldPlc)){
					
					rs1 = queryForRecordSet("BASPRM00200.getCheckSaleProd", mParam, onlineCtx);
					mReturn = rs1.getRecordMap(0);
					
					if(mReturn.get("sale_chg_dtm") != null && !mReturn.get("sale_chg_dtm").equals("") ){
						//적용일시 이 후에 {0}의 판매건이 존재 합니다[판매일자{1}].\n\n적용일시를 판매일자 이 후로 입력하세요. 
						throw new BizRuntimeException("PSMW3012",new String[] {"변경전 재고보유처", ":"+mReturn.get("sale_chg_dtm")});
					}
				}
			}	
			
			/****************************************************************
			 *  5. 종료 시 정산처의 판매건 체크.
			 *     단 직영점[A2], 온라인 직영점[B1], 판매점[A3], 온라인 판매점[B2], M채널 인 경우만
			 ****************************************************************/			
			String sA2 = "A2";
			String sB1 = "B1";
		    String sA3 = "A3";
			String sB2 = "B2";
			String sM1 = "M1";
			
			if(dealEndYn.equals(sYes) 
					&& ( dealCoCl1.equals(sA2) || dealCoCl1.equals(sB1) || dealCoCl1.equals(sA3) 
							|| dealCoCl1.equals(sB2) || dealCoCl1.equals(sM1)  ) ){
				
				if(sStlPlcOld != null && !sStlPlcOld.equals("")){
						
					rs1 = queryForRecordSet("BASPRM00200.getCheckSaleProd", mParam, onlineCtx);
					mReturn = rs1.getRecordMap(0);
					
					if(mReturn.get("sale_chg_dtm") != null && !mReturn.get("sale_chg_dtm").equals("") ){
						//적용일시 이 후에 {0}의 판매건이 존재 합니다[판매일자{1}].\n\n적용일시를 판매일자 이 후로 입력하세요. 
						throw new BizRuntimeException("PSMW3012",new String[] {"정산처", ":"+mReturn.get("sale_chg_dtm")});
					}
				}
				
				if(sDisHldPlcOld != null && !sDisHldPlcOld.equals("")){
					
					rs1 = queryForRecordSet("BASPRM00200.getCheckSaleProd", mParam, onlineCtx);
					mReturn = rs1.getRecordMap(0);
					
					if(mReturn.get("sale_chg_dtm") != null && !mReturn.get("sale_chg_dtm").equals("") ){
						//적용일시 이 후에 {0}의 판매건이 존재 합니다[판매일자{1}].\n\n적용일시를 판매일자 이 후로 입력하세요. 
						throw new BizRuntimeException("PSMW3012",new String[] {"재고보유처", ":"+mReturn.get("sale_chg_dtm")});
					}
				}					
			}
		}
		
		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_SIMPLE_MESSAGE_ID, null);
		if(rs3 == null)
		{
			rs3 = new RecordSet("output");
		}
		
		// 저장 후 다시 조회 하기 위해 필요한 key
		Map mReturn = new HashMap();
		mReturn.put("SLCM_DFRY_MSG", sSlcmDfryMsg);

		responseData.putFieldMap(mReturn);		
		responseData.putRecordSet("output", rs3);
		
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getUserInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPRM00200.getUserInfo method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("BASPRM00200.getUserInfo",
				requestData.getFieldMap(), onlineCtx);

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
		} else if (tpsUser.equals(sIP)) { //사용자서버
			host = sIP;
		} else if (tpsDev.equals(sIP)) { //개발서버
			host = sIP;
		} else {
			host = tpsDev;
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

}