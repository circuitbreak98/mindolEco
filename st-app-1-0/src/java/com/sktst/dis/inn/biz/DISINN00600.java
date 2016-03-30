/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.inn.biz;

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
import nexcore.framework.integration.db.DuplicationException;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-25 15:26:24</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class DISINN00600 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 입고 확정 마스터와 디테일, 상품리스트 정보를 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getInnFixDtlList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00600.getInnFixDtlList method start");
		}

		// 입고마스터 조회.
		IRecordSet rsInMaster = queryForRecordSet("DISINN00600.getInFix",
				requestData.getFieldMap(), onlineCtx);

		if (rsInMaster == null) {
			rsInMaster = new RecordSet("ds_inMaster");
		}

		// 상품,색상으로 그룹핑 된 입고디테일  조회.
		IRecordSet rsInDtlGrpList = null;

		rsInDtlGrpList = queryForRecordSet("DISINN00600.getInFixDtlGrpList",
				requestData.getFieldMap(), onlineCtx);

		if (rsInDtlGrpList == null) {
			rsInDtlGrpList = new RecordSet("ds_inDtlList");
		}

		// 입고디테일 조회.
		IRecordSet rsInDtlList = queryForRecordSet(
				"DISINN00600.getInFixProdList", requestData.getFieldMap(),
				onlineCtx);

		if (rsInDtlList == null) {
			rsInDtlList = new RecordSet("ds_prodSerNumList");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsInDtlList.getRecordCount()) });

		responseData.putRecordSet("ds_inMaster", rsInMaster);
		responseData.putRecordSet("ds_inDtlList", rsInDtlGrpList);
		responseData.putRecordSet("ds_prodSerNumList", rsInDtlList);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 입고를 확정한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public IDataSet saveInnFix(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00600.saveInnFix method start");
		}

		int updateCount = 0;
		String sInMgmtNo = null;
		String sOutMgmtNo = null;
		String sErrMsg = ""; // error message
		String sErrCode = ""; // error code
		String sSuccessCd = "S"; // success code

		//사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		// 입고 MASTER. 
		IRecordSet rsInMst = requestData.getRecordSet("ds_inMaster");
		Map mInMst = rsInMst.getRecordMap(0);

		sInMgmtNo = (String) mInMst.get("in_mgmt_no");

		// 공통 데이터 셋팅.
		Map mInCommData = this.setInCommMapData(iUserInfo);
		Map mOutCommData = this.setOutCommMapData(iUserInfo);

		// 입고  상품 리스트. 
		IRecordSet rsProdList = requestData.getRecordSet("ds_prodSerNumList");

		if (rsProdList != null) {

			Map mProdData = null;
			Map mSaveData = null;
			String sProcFlag = null;
			String sInFixYn = null;
			String sInFixYnOrg = null;
			String sFixYes = "1";
			String sEtcProd = "9";
			int iInFixQty = 0;
			String sPrchInCd = "101"; //구매입고
			String sChgInCd = "102"; //교품입고	
			String sPrchsOutInCd = "114"; //매입입고	
			Map mOutSaveData = null;
			Map mMgmt = null;

			for (int i = 0; i < rsProdList.getRecordCount(); i++) {

				mProdData = rsProdList.getRecordMap(i);

				mSaveData = new HashMap();
				// 마스터 데이터 넣기.
				mSaveData.putAll(mInMst);
				// 상품 데이터 넣기.
				mSaveData.putAll(mProdData);
				// 공통 데이터 넣기.
				mSaveData.putAll(mInCommData);

				mSaveData.put("IN_AMT", (int) Double
						.parseDouble((String) mProdData.get("IN_AMT")));
				mSaveData.put("IN_QTY", (int) Double
						.parseDouble((String) mProdData.get("IN_QTY")));
				mSaveData.put("IN_FIX_QTY", (int) Double
						.parseDouble((String) mProdData.get("IN_FIX_QTY")));

				sInFixYn = (String) mProdData.get("IN_FIX_YN");
				sInFixYnOrg = (String) mProdData.get("IN_FIX_YN_ORG");

				mSaveData.put("IN_FIX_YN", sInFixYn.equals("1") ? "Y" : "N");

				// 변경된 데이터중 입고확정여부가 변경되었거나 입고확정수량이 변경된 건만 처리.
				if (UPDATE_FLAG.equalsIgnoreCase((String) mProdData
						.get(CUD_FLAG_PARAM))
						&& (!sInFixYn.equals(sInFixYnOrg) || !mProdData.get(
								"IN_FIX_QTY").equals(
								mProdData.get("IN_FIX_QTY_ORG")))) {

					sProcFlag = "U"; // I:등록,U:수정,D:개별삭제,AD:전체삭제
					updateCount++;

					mSaveData.put("PROC_FLAG", sProcFlag);
					mSaveData.put("IN_SEQ", (int) Double
							.parseDouble((String) mProdData.get("IN_SEQ")));
					mSaveData.put("UPD_CNT", (int) Double
							.parseDouble((String) mProdData.get("UPD_CNT")));

					// 기타상품은 수량이 - 인경우 확정취소 + 인 경우 확정.
					if (mProdData.get("PROD_CL").equals(sEtcProd)) {

						// 기타상품인 경우 차익을 넣는다.
						iInFixQty = (int) Double.parseDouble((String) mProdData
								.get("IN_FIX_QTY"))
								- (int) Double.parseDouble((String) mProdData
										.get("IN_FIX_QTY_ORG"));

						mSaveData.put("IN_FIX_QTY", iInFixQty);

						if (iInFixQty > 0) {
							mSaveData.put("ACT_FLAG", "F"); //업무처리구분 (F:확정등록)
						} else {
							mSaveData.put("ACT_FLAG", "IC"); //업무처리구분 (IC:입고확정취소)
						}
					} else {

						if (sInFixYn.equals(sFixYes)) {
							mSaveData.put("ACT_FLAG", "F"); //업무처리구분 (F:확정등록)
						} else {
							mSaveData.put("ACT_FLAG", "IC"); //업무처리구분 (IC:입고확정취소)
						}
					}

					// 입고구분이 구매입고, 교품입고 인 경우 와 as입고, 교품반환입고 인 경우를 분기한다.
					if (mInMst.get("in_cl").equals(sPrchInCd)
							|| mInMst.get("in_cl").equals(sChgInCd)
							|| mInMst.get("in_cl").equals(sPrchsOutInCd)) {
						mSaveData.put("BAD_YN", "01"); // 불량여부(정상)
						mSaveData.put("DIS_ST", "01"); // 재고상태(가용)	
					} else {
						mSaveData.put("BAD_YN", "02"); // 불량여부(불량)
						mSaveData.put("DIS_ST", "02"); // 재고상태(비가용)	
					}

					// 출고용 map
					mOutSaveData = new HashMap();
					mOutSaveData.putAll(mSaveData);

					/*********************************
					 *  입고 프로시져 호출
					 *********************************/
					queryForObject("DISINN00600.saveInFix", mSaveData,
							onlineCtx);

					// 프로시저 에러 메세지 
					sErrMsg = (String) mSaveData.get("ERRMSG");
					sErrCode = (String) mSaveData.get("ERRCODE");

					if (sErrCode == null
							|| (sErrCode != null && !sErrCode
									.equals(sSuccessCd))) {
						throw new BizRuntimeException(sErrMsg);
					}

					//교품입고시 교품출고 상품을 처리한다.
					/*
					 if (mInMst.get("in_cl").equals(sChgInCd)) {
					 
					 if (sInFixYn.equals(sFixYes)) {
					 
					 // tdsi_dis 에 금액을 업데이트 한다.
					 update("DISINN00600.updateDisDisAmt",mOutSaveData, onlineCtx);							

					 // 확정등록 - 재고단가 변경 이력 을 생성한다.
					 insert("DISINN00600.insertDisUnitChgHst",mOutSaveData, onlineCtx);

					 } else {
					 // 확정취소 - 재고단가 변경 이력 을 삭제한다.
					 update("DISINN00600.deleteDisUnitChgHst",mOutSaveData, onlineCtx);
					 }

					 mOutSaveData.putAll(mOutCommData);
					 mOutSaveData.put("BAD_YN", "02"); // 불량
					 mOutSaveData.put("DIS_ST", "02"); // 비가용
					 mOutSaveData.put("PROC_FLAG", "I"); // I: 등록

					 // 입고관리번호 얻기.
					 mMgmt = new HashMap();
					 mMgmt.put("OV_ERRCODE", ""); // 에러.
					 mMgmt.put("OV_ERRMSG", ""); // 에러메세지.
					 mMgmt.put("IV_MGMT_NO_CD", "OT"); // 관리번호구분.
					 mMgmt.put("IV_USER_ID", iUserInfo.getLoginId()); // 처리자.
					 mMgmt.put("OV_MGMT_NO", "");

					 sOutMgmtNo = this.getMgmtNo(mMgmt, onlineCtx);

					 mOutSaveData.put("OUT_MGMT_NO", sOutMgmtNo); // 출고관리번호 셋팅.						
					 mOutSaveData.put("OUT_SCHD_DT", mOutSaveData.get("IN_FIX_DT")); //	출고예정일
					 //mOutSaveData.put("OUT_PLC_ID", mOutSaveData.get("IN_PLC_ID")); //	출고처ID
					 mOutSaveData.put("IN_PLC_ID", mOutSaveData.get("PRCHS_PLC_ID")); //	입고처
					 mOutSaveData.put("OUT_FIX_DT", mOutSaveData.get("IN_FIX_DT")); //	출고확정일
					 mOutSaveData.put("OUT_FIX_YN", "Y"); //	출고확정
					 mOutSaveData.put("OUT_QTY", 1); //	출고수량
					 mOutSaveData.put("UNIT_PRC", (int) Double.parseDouble((String) mProdData.get("OUT_UNIT_PRC"))); //	출고단가
					 mOutSaveData.put("AMT", (int) Double.parseDouble((String) mProdData.get("OUT_UNIT_PRC"))); //	출고금액
					 mOutSaveData.put("UPD_CNT", (int) Double.parseDouble((String) mProdData.get("OUT_UPD_CNT")));
					 mOutSaveData.put("OUT_SEQ", 0);
					 
					 if (sInFixYn.equals(sFixYes)) {

					 // 2.출고_일련번호_매칭 저장.
					 try {
					 insert("DISINN00600.insertOutSerNumMap",mOutSaveData, onlineCtx);
					 } catch (DuplicationException e) {
					 update("DISINN00600.updateOutSerNumMap",mOutSaveData, onlineCtx);
					 }

					 mOutSaveData.put("ACT_FLAG", "CO"); //업무처리구분 (교품반품출고)
					 mOutSaveData.put("OUT_CL", "206"); //	교품반품출고
					 // 확정등록
					 mOutSaveData.put("COLOR_CD", mProdData.get("OUT_COLOR_CD"));
					 mOutSaveData.put("OUT_SCHD_DT", mOutSaveData.get("IN_FIX_DT")); //	출고예정일자.

					 } else {

					 // 2.출고_일련번호_매칭 삭제.
					 update("DISINN00600.deleteOutSerNumMap",mOutSaveData, onlineCtx);

					 mOutSaveData.put("ACT_FLAG", "COC"); //업무처리구분 (교품반품출고취소)
					 mOutSaveData.put("OUT_CL", "210"); //	교품반품출고취소
					 // 확정취소
					 mOutSaveData.put("COLOR_CD", mProdData.get("OUT_COLOR_CD_OLD"));	
					 mOutSaveData.put("OUT_SCHD_DT", mOutSaveData.get("IN_FIX_DT_ORG")); //	출고예정일자.
					 }

					 mOutSaveData.put("SER_NUM", mProdData.get("OUT_SER_NUM"));
					 
					 ///////////////////////////////////
					 //  출고 프로시져 호출
					 /////////////////////////////////// 
					 queryForObject("DISINN00600.saveOutFix", mOutSaveData,onlineCtx);
					 sErrMsg = (String) mOutSaveData.get("ERRMSG");
					 sErrCode = (String) mOutSaveData.get("ERRCODE");

					 if (sErrCode == null || (sErrCode != null && !sErrCode.equals(sSuccessCd))) {
					 throw new BizRuntimeException(sErrMsg);
					 }
					 }
					 */
				}
			}
		}

		// 마스터 저장.
		if (rsInMst != null) {
			if (UPDATE_FLAG.equalsIgnoreCase((String) mInMst
					.get(CUD_FLAG_PARAM))) {
				updateCount = updateCount
						+ update("DISINN00400.updateInSchdMaster", mInMst,
								onlineCtx);
			}
		}

		if (updateCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
						+ updateCount });

		// 저장 후 다시 조회 하기 위해 필요한 key
		Map mReturn = new HashMap();
		mReturn.put("in_mgmt_no", sInMgmtNo);

		responseData.putFieldMap(mReturn);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 *  - 공통데이터 셋팅.
	 * @param iUserInfo
	 *            사용자정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public Map setOutCommMapData(IUserInfo iUserInfo) {

		Map mCommData = new HashMap();
		mCommData.put("ERRCODE", ""); //ERROR CODE
		mCommData.put("ERRMSG", ""); //ERROR MESSAGE
		mCommData.put("RMKS", "");
		mCommData.put("DLVY_TYP", "");
		mCommData.put("SETTL_COND_CD", "");
		mCommData.put("DLV_CO_ID", "");
		mCommData.put("DLVY_UNIT", "");
		mCommData.put("DLVY_QTY", 0);
		mCommData.put("DLVY_DTM", "");
		mCommData.put("BF_DLVY_REQ_DT", "");
		mCommData.put("BF_DLVY_REQ_SEQ", 0);
		mCommData.put("BF_DLV_CO_ID", "");
		mCommData.put("CNCL_YN", "");
		mCommData.put("SALE_CNTR_ID", "");
		mCommData.put("USER_ID", iUserInfo.getLoginId()); // 처리자

		return mCommData;
	}

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 *  - 공통데이터 셋팅.
	 * @param iUserInfo
	 *            사용자정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public Map setInCommMapData(IUserInfo iUserInfo) {

		Map mCommData = new HashMap();
		mCommData.put("ERRCODE", ""); //ERROR CODE
		mCommData.put("ERRMSG", ""); //ERROR MESSAGE
		mCommData.put("GNRL_SALE_NO", ""); // 일반판매번호
		mCommData.put("GNRL_SALE_CHG_SEQ", ""); // 일반판매변경순번
		mCommData.put("IN_SEQ", 0); // 입고순번
		mCommData.put("OP_LCL_CD", ""); // 불량사유 대분류
		mCommData.put("OP_MCL_CD", ""); // 불량사유 중분류
		mCommData.put("CNCL_YN", ""); // 취소여부
		mCommData.put("USER_ID", iUserInfo.getLoginId()); // 처리자	

		return mCommData;
	}

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 *  - 관리번호를 얻는다.
	 * @param data
	 *            요청정보를 Wrapping하고 있는 Map 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public String getMgmtNo(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00200.getMgmtNo method start");
		}
		String sMgmtNo = ""; //관리번호

		queryForObject("DISINN00200.getMgmtNo", data, onlineCtx);

		sMgmtNo = (String) data.get("OV_MGMT_NO");

		if (sMgmtNo == null || sMgmtNo.equals("")) {
			throw new BizRuntimeException("관리번호 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}

		return sMgmtNo;
	}
}