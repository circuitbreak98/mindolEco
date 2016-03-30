/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.inn.biz;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.core.util.DateUtils;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;
import org.springframework.jdbc.object.UpdatableSqlQuery;

import com.sktst.dis.inn.ejb.INN;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-25 10:18:48</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class DISINN00400 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 발주 mater/detail 정보를 취득한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getOrd(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00400.getOrd method start");
		}

		// 발주 detail List 조회.
		IRecordSet rsOrdDtlList = queryForRecordSet(
				"DISINN00400.getOrdDtlList", requestData.getFieldMap(),
				onlineCtx);

		if (rsOrdDtlList == null) {
			rsOrdDtlList = new RecordSet("ds_inDtlList");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsOrdDtlList.getRecordCount()) });

		responseData.putRecordSet("ds_inDtlList", rsOrdDtlList);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteInnSchd(IDataSet requestData, IOnlineContext onlineCtx) {

		IDataSet result = new DataSet();
		// TODO 업무 로직 작성 필요

		return result;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 입고예정에 필요한 입고 마스터와 입고 디테일, 상품 리스트를 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getInnSchdDtlList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00400.getInnSchdDtlList method start");
		}

		String sInCl = requestData.getField("in_cl"); //입고구분.
		String sPurchInCd = "101"; //구매입고 코드

		// 입고마스터 조회.
		IRecordSet rsInMaster = queryForRecordSet("DISINN00400.getInSchd",
				requestData.getFieldMap(), onlineCtx);

		if (rsInMaster == null) {
			rsInMaster = new RecordSet("ds_inMaster");
		}

		// 상품,색상으로 그룹핑 된 입고디테일  조회.
		IRecordSet rsInDtlGrpList = null;

		// 입고구분이 구매입고일 경우와 아닌경우에 따라 쿼리를 분기한다.
		if (sInCl == null || sInCl.equals("")) {

			rsInDtlGrpList = new RecordSet("ds_inDtlList");

		} else {

			if (sInCl.equals(sPurchInCd)) {
				// 구매입고
				rsInDtlGrpList = queryForRecordSet(
						"DISINN00400.getInDtlSchdGrpOrdList", requestData
								.getFieldMap(), onlineCtx);
			} else {
				// 교품입고, A/S입고, 교품반환입고 
				//				rsInDtlGrpList = queryForRecordSet(
				//						"DISINN00400.getInDtlSchdGrpList", requestData
				//								.getFieldMap(), onlineCtx);
				// 교품입고, A/S입고, 교품반환입고 는 2차 추가

			}

			if (rsInDtlGrpList == null) {
				rsInDtlGrpList = new RecordSet("ds_inDtlList");
			}
		}

		// 입고디테일 조회.
		IRecordSet rsInDtlList = queryForRecordSet(
				"DISINN00400.getInSchdProdList", requestData.getFieldMap(),
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
	 *  - 입고예정 정보를 등록한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveInnSchd(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00400.saveInnSchd method start");
		}

		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;
		String sInMgmtNo = null;
		String sMgmtNoCl = "IN"; //관리번호구분 IN:입고
		String sErrMsg = ""; // error message
		String sErrCode = ""; // error code
		String sSuccessCd = "S"; // success code
		Map minData = null;
		String sLockCnt = "";
		String sDisLockCnt = "";
		String sProdDisLockCnt = "";
		String sZero = "0";
		int iZero = 0;

		//사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		// 입고 MASTER. 
		IRecordSet rsInMst = requestData.getRecordSet("ds_inMaster");

		Map mInMst = rsInMst.getRecordMap(0);

		sInMgmtNo = (String) mInMst.get("in_mgmt_no");

		//신규인 경우에 입고관리번호를 생성한다.
		if (sInMgmtNo == null || sInMgmtNo.equals("")) {

			/*************** lock 처리 시작 (입고예정등록이 이미 되었는지 체크.) ***************/
			minData = (Map) queryForObject("DISINN00400.getInnOrdByLock",
					mInMst, onlineCtx);
			sLockCnt = ((BigDecimal) minData.get("CNT")).toString();

			if (!sLockCnt.equals(sZero)) {
				// 이미 등록된 발주 입니다. 확인 하시기 바랍니다.
				throw new BizRuntimeException("PSMW5012");
			}
			/*************** lock 처리 종료 ***************/

			// 입고관리번호 얻기.
			Map mMgmt = new HashMap();
			mMgmt.put("OV_ERRCODE", ""); // 에러.
			mMgmt.put("OV_ERRMSG", ""); // 에러메세지.
			mMgmt.put("IV_MGMT_NO_CD", sMgmtNoCl); // 관리번호구분.
			mMgmt.put("IV_USER_ID", iUserInfo.getLoginId()); // 처리자.
			mMgmt.put("OV_MGMT_NO", "");

			sInMgmtNo = this.getMgmtNo(mMgmt, onlineCtx);

			mInMst.put("in_mgmt_no", sInMgmtNo); // 입고관리번호 셋팅.
		}

		// 공통 데이터 셋팅.
		Map mCommData = this.setCommMapData(iUserInfo);

		// 입고  상품 리스트. 
		IRecordSet rsProdList = requestData.getRecordSet("ds_prodSerNumList");

		if (rsProdList != null) {

			Map mProdData = null;
			Map mSaveData = null;
			String sProcFlag = null;
			String sPrchInCd = "101"; // 구매입고
			String sChgInCd = "102"; // 교품입고
			String[] keyList = null;

			for (int i = 0; i < rsProdList.getRecordCount(); i++) {

				mProdData = rsProdList.getRecordMap(i);

				mSaveData = new HashMap();
				// 마스터 데이터 넣기.
				mSaveData.putAll(mInMst);
				// 상품 데이터 넣기.
				mSaveData.putAll(mProdData);
				// 공통 데이터 넣기.
				mSaveData.putAll(mCommData);

				if (DELETE_FLAG.equalsIgnoreCase((String) mProdData
						.get(CUD_FLAG_PARAM))) {

					/*************** lock 처리 시작 ***************/
					minData = null;
					sLockCnt = "";

					minData = (Map) queryForObject(
							"DISINN00400.getInnDtlByLock", mSaveData, onlineCtx);
					sLockCnt = ((BigDecimal) minData.get("CNT")).toString();

					if (sLockCnt.equals(sZero)) {
						// 변경된 데이터가 있습니다. 재 조회 하시기 바랍니다.
						throw new BizRuntimeException("PSMW5010");
					}
					/*************** lock 처리 종료 ***************/

					sProcFlag = "D"; // I:등록,U:수정,D:개별삭제,AD:전체삭제
					deleteCount++;

					mSaveData.put("PROC_FLAG", sProcFlag);
					mSaveData.put("in_qty", (int) Double
							.parseDouble((String) mProdData.get("in_qty")));
					mSaveData.put("in_seq", (int) Double
							.parseDouble((String) mProdData.get("in_seq")));
					mSaveData.put("in_fix_qty", 0);

					keyList = (String[]) mSaveData.keySet().toArray(
							new String[iZero]);

					for (int j = 0; j < keyList.length; j++) {

						mSaveData.put(keyList[j].toUpperCase(), mSaveData
								.get(keyList[j]));
					}

					// 입고구분이 구매입고, 교품입고 인 경우 와 as입고, 교품반환입고 인 경우를 분기한다.
					if (mInMst.get("in_cl").equals(sPrchInCd)
							|| mInMst.get("in_cl").equals(sChgInCd)) {
						mSaveData.put("BAD_YN", "01"); // 불량여부(정상)
						mSaveData.put("DIS_ST", "01"); // 재고상태(가용)	
					} else {
						mSaveData.put("BAD_YN", "02"); // 불량여부(불량)
						mSaveData.put("DIS_ST", "02"); // 재고상태(비가용)	
					}

					/******************************************************************
					 * 입고예정등록
					 * 
					 */
					queryForObject("DISINN00400.saveInSchd", mSaveData,
							onlineCtx);

					// 프로시저 에러 메세지 
					sErrMsg = (String) mSaveData.get("ERRMSG");
					sErrCode = (String) mSaveData.get("ERRCODE");

					if (sErrCode == null
							|| (sErrCode != null && !sErrCode
									.equals(sSuccessCd))) {
						throw new BizRuntimeException(sErrMsg);
					}

					// 인터페이스는 2차 적용  *******
					// 인터페이스 통해서 등록한 상품은 TDIS_PROD_INOUT_IF의 REFL_YN(반영여부)를 업데이트한다.
					//mProdData.put("in_mgmt_no", sInMgmtNo);
					//update("DISINN00400.deleteProdInoutIf", mProdData,
					//		onlineCtx);
				}
			}

			//			for (int i = 0; i < rsProdList.getRecordCount(); i++) {

			//				mProdData = rsProdList.getRecordMap(i);

			//				mSaveData = new HashMap();
			//				// 마스터 데이터 넣기.
			//				mSaveData.putAll(mInMst);
			//				// 상품 데이터 넣기.
			//				mSaveData.putAll(mProdData);
			//				// 공통 데이터 넣기.
			//				mSaveData.putAll(mCommData);

			if (INSERT_FLAG.equalsIgnoreCase((String) mProdData
					.get(CUD_FLAG_PARAM))
					|| UPDATE_FLAG.equalsIgnoreCase((String) mProdData
							.get(CUD_FLAG_PARAM))) {

				if (UPDATE_FLAG.equalsIgnoreCase((String) mProdData
						.get(CUD_FLAG_PARAM))) {
					/*************** lock 처리 시작 ***************/
					minData = null;
					sLockCnt = "";
					minData = (Map) queryForObject(
							"DISINN00400.getInnDtlByLock", mSaveData, onlineCtx);
					sLockCnt = ((BigDecimal) minData.get("CNT")).toString();

					if (sLockCnt.equals(sZero)) {
						// 변경된 데이터가 있습니다. 재 조회 하시기 바랍니다.
						throw new BizRuntimeException("PSMW5010");
					}
					/*************** lock 처리 종료 ***************/
				}

				log.debug("---------------------------------");
				log.debug("mSaveData==" + mSaveData);
				log.debug("---------------------------------");

				mSaveData.put("in_qty", (int) Double
						.parseDouble((String) mProdData.get("in_qty")));
				mSaveData.put("in_amt", (int) Double
						.parseDouble((String) mProdData.get("in_amt")));
				mSaveData.put("in_fix_qty", 0);

				if (INSERT_FLAG.equalsIgnoreCase((String) mProdData
						.get(CUD_FLAG_PARAM))) {

					log.debug("**********************************");
					log.debug("입력       ==" + mProdData.get("in_amt"));
					log.debug("**********************************");

					sProcFlag = "I"; // I:등록
					insertCount++;
					mSaveData.put("in_seq", 0);
					mSaveData.put("PROC_FLAG", sProcFlag);

				} else if (UPDATE_FLAG.equalsIgnoreCase((String) mProdData
						.get(CUD_FLAG_PARAM))) {

					sProcFlag = "U"; // U:수정
					updateCount++;
					mSaveData.put("in_seq", (int) Double
							.parseDouble((String) mProdData.get("in_seq")));
					mSaveData.put("PROC_FLAG", sProcFlag);

				}

				keyList = (String[]) mSaveData.keySet().toArray(
						new String[iZero]);

				for (int j = 0; j < keyList.length; j++) {

					mSaveData.put(keyList[j].toUpperCase(), mSaveData
							.get(keyList[j]));
				}

				// 입고구분이 구매입고, 교품입고 인 경우 와 as입고, 교품반환입고 인 경우를 분기한다.
				if (mInMst.get("in_cl").equals(sPrchInCd)
						|| mInMst.get("in_cl").equals(sChgInCd)) {
					mSaveData.put("BAD_YN", "01"); // 불량여부(정상)
					mSaveData.put("DIS_ST", "01"); // 재고상태(가용)	
				} else {
					mSaveData.put("BAD_YN", "02"); // 불량여부(불량)
					mSaveData.put("DIS_ST", "02"); // 재고상태(비가용)	
				}

				System.out.println("Insert-------------------------");
				System.out.println(mSaveData);

				queryForObject("DISINN00400.saveInSchd", mSaveData, onlineCtx);

				// 프로시저 에러 메세지 
				sErrMsg = (String) mSaveData.get("ERRMSG");
				sErrCode = (String) mSaveData.get("ERRCODE");

				if (sErrCode == null
						|| (sErrCode != null && !sErrCode.equals(sSuccessCd))) {
					throw new BizRuntimeException(sErrMsg);
				}
			}
			//			}
		}

		String sMgmtNo = (String) mInMst.get("in_mgmt_no");

		//신규인 경우에 
		if (sMgmtNo != null && !sMgmtNo.equals("")) {

			// 마스터 저장.
			if (rsInMst != null) {
				if (UPDATE_FLAG.equalsIgnoreCase((String) mInMst
						.get(CUD_FLAG_PARAM))) {
					updateCount = updateCount
							+ update("DISINN00400.updateInSchdMaster", mInMst,
									onlineCtx);
				}
			}
		}

		// ukey 인터페이스로 받은 상품이 있을 경우 TDIS_PROD_INOUT_IF의 REFL_YN(반영여부)를 업데이트한다.
		//		IRecordSet rsProdInoutIf = requestData.getRecordSet("ds_prodInoutIf");
		//
		//		if (rsProdInoutIf != null) {
		//
		//			Map mProdIfData = null;
		//
		//			for (int i = 0; i < rsProdInoutIf.getRecordCount(); i++) {
		//
		//				mProdIfData = rsProdInoutIf.getRecordMap(i);
		//
		//				if (INSERT_FLAG.equalsIgnoreCase((String) mProdIfData
		//						.get(CUD_FLAG_PARAM))) {
		//
		//					mProdIfData.put("in_mgmt_no", sInMgmtNo);
		//					update("DISINN00400.updateProdInoutIf", mProdIfData,
		//							onlineCtx);
		//				}
		//			}
		//		}

		cudAllCount = insertCount + updateCount + deleteCount;

		if (cudAllCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount, "" + deleteCount });

		// 저장 후 다시 조회 하기 위해 필요한 key
		Map mReturn = new HashMap();
		mReturn.put("in_mgmt_no", mInMst.get("in_mgmt_no"));

		responseData.putFieldMap(mReturn);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
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
			log.debug("DISINN00400.getMgmtNo method start");
		}
		String sMgmtNo = ""; //관리번호
		queryForObject("DISINN00200.getMgmtNo", data, onlineCtx);

		sMgmtNo = (String) data.get("OV_MGMT_NO");

		if (sMgmtNo == null || sMgmtNo.equals("")) {
			throw new BizRuntimeException("관리번호 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}

		return sMgmtNo;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 공통데이터 셋팅.
	 * @param iUserInfo
	 *            사용자정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public Map setCommMapData(IUserInfo iUserInfo) {

		Map mCommData = new HashMap();
		mCommData.put("ERRCODE", ""); //ERROR CODE
		mCommData.put("ERRMSG", ""); //ERROR MESSAGE
		mCommData.put("ACT_FLAG", "S"); //업무처리구분 (S:예정등록,F:확정등록,SI:판매취소입고,EI:기기교환입고)
		mCommData.put("GNRL_SALE_NO", ""); // 일반판매번호
		mCommData.put("GNRL_SALE_CHG_SEQ", ""); // 일반판매변경순번
		mCommData.put("SETTL_COND_CD", ""); // 결제조건코드
		mCommData.put("IN_FIX_DT", ""); // 입고확정일자
		mCommData.put("IN_FIX_YN", ""); // 입고확정여부
		mCommData.put("IN_FIX_QTY", 0); // 입고확정수량
		mCommData.put("OP_LCL_CD", ""); // 불량사유 대분류
		mCommData.put("OP_MCL_CD", ""); // 불량사유 중분류
		mCommData.put("CNCL_YN", ""); // 취소여부
		mCommData.put("USER_ID", iUserInfo.getLoginId()); // 처리자
		mCommData.put("UPD_CNT", 0); // update count
		mCommData.put("upd_cnt", 0); // update count

		return mCommData;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 매입출고 mater/detail 정보를 취득한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getPrchsOut(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00400.getPrchsOut method start");
		}

		// 발주 detail List 조회.
		IRecordSet rsOrdDtlList = queryForRecordSet(
				"DISINN00400.getPrchsOutDtlList", requestData.getFieldMap(),
				onlineCtx);

		if (rsOrdDtlList == null) {
			rsOrdDtlList = new RecordSet("ds_inDtlList");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsOrdDtlList.getRecordCount()) });

		responseData.putRecordSet("ds_inDtlList", rsOrdDtlList);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveInn(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00400.saveInn method start");
		}

		// 발주,매입에 대한 입고 처리
		// 1. 입고관리번호 생성
		// 2. 입고  처리 (입고 상태를 입고확정으로 처리함)
		// 2-1. 입고 tabel insert
		// 2-2. 입고상세 tabel insert
		// 2-3. 입고구성품 tabel insert
		// 3. 재고  처리
		// 3-1. 재고 tabel insert
		// 3-2. 재고금액 tabel insert
		// 3-3. 재고입출고이력 tabel insert
		// 3-4. 재고이력 tabel insert
		// 3-5. 기타재고 tabel insert
		// 3-6. 기타재고입출고이력 tabel insert
		// 3-7. 기타재고이력 tabel insert
		// 

		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;

		String sInMgmtNo = "";
		String sPrchsMgmtNo = "";
		String sMgmtNoCl = "IN"; //관리번호구분 IN:입고
		String sErrMsg = ""; // error message
		String sErrCode = ""; // error code
		String sSuccessCd = "S"; // success code

		// lock 처리 변수
		Map minData = null;
		String sLockCnt = "";
		String sDisLockCnt = "";
		String sProdDisLockCnt = "";
		String sZero = "0";
		String sSerNum = ""; // 일련번호
		String sInSeq = ""; // 입고순번
		String sEtcSeq = ""; // 기타재고순번
		int iZero = 0;
		int iInQty = 0; //입고수량
		int iAmt = 0; //입고금액
		int iCount = 0;

		//사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();
log.debug("============================ 1");
		// 입고 MASTER. 
		IRecordSet rsInMst = requestData.getRecordSet("ds_inMaster");
log.debug("============================ 2");
		Map mInMst = rsInMst.getRecordMap(0);
log.debug("============================ 3");
		sInMgmtNo = (String) mInMst.get("in_mgmt_no");
log.debug("============================ 4");
		// 입고  상품 리스트. 
		IRecordSet rsProdList = requestData.getRecordSet("ds_prodSerNumList");
log.debug("============================ 5");
		Map mProdCond = rsProdList.getRecordMap(0);
log.debug("============================ 6");
		// 1. 입고관리번호 생성
		// 신규인 경우에 입고관리번호를 생성한다.
		if (sInMgmtNo == null || sInMgmtNo.equals("")) {
log.debug("============================ 7");			
			// lock 처리 시작 
			// a. 재고 (tdis_dis) check
			minData = (Map) queryForObject("DISINN00400.getDisByLock",
					mProdCond, onlineCtx);
log.debug("============================ 8");			
			sDisLockCnt = ((BigDecimal) minData.get("DIS_CNT")).toString();
log.debug("============================ 9");
			if (!sDisLockCnt.equals(sZero)) {
				// 매입 되어있는 상품입니다. 확인 하시기 바랍니다.
				throw new BizRuntimeException("구성품 매입 되어있는 상품입니다. 확인 하시기 바랍니다.");
			}

			// b. 상품재고 (tdis_prod_dis) check
			minData = (Map) queryForObject("DISINN00400.getProdDisByLock",
					mProdCond, onlineCtx);
			
			sProdDisLockCnt = ((BigDecimal) minData.get("PROD_DIS_CNT")).toString();

			if (!sProdDisLockCnt.equals(sZero)) {
				// 매입 되어있는 상품입니다. 확인 하시기 바랍니다.
				throw new BizRuntimeException("입고 되어있는 상품입니다. 확인 하시기 바랍니다.");
			}

			// c. 입고예정등록 (tdis_in_m) check
			minData = (Map) queryForObject("DISINN00400.getInnOrdByLock",
					mInMst, onlineCtx);

			sLockCnt = ((BigDecimal) minData.get("CNT")).toString();

			if (!sLockCnt.equals(sZero)) {
				// 이미 등록된 발주 입니다. 확인 하시기 바랍니다.
				throw new BizRuntimeException("PSMW5012");
			}
			// lock 처리 종료

			// 입고관리번호 얻기.
			Map mMgmt = new HashMap();
			mMgmt.put("OV_ERRCODE", ""); // 에러.
			mMgmt.put("OV_ERRMSG", ""); // 에러메세지.
			mMgmt.put("IV_MGMT_NO_CD", sMgmtNoCl); // 관리번호구분.
			mMgmt.put("IV_USER_ID", iUserInfo.getLoginId()); // 처리자.
			mMgmt.put("OV_MGMT_NO", "");

			sInMgmtNo = this.getMgmtNo(mMgmt, onlineCtx);

			//mInMst.put("in_mgmt_no", sInMgmtNo); // 입고관리번호 셋팅.
		}

		// 공통 데이터 셋팅.
		Map mCommData = this.setCommMapData(iUserInfo);

		log.debug("mCommData========" + mCommData);

		if (rsProdList != null) {
			log.debug("ds_prodSerNumList not null ========" + rsProdList);
			log.debug("mInMst not null ========" + mInMst);

			Map mProdData = null;
			Map mSaveData = null;
			Map mInSaveData = null; // 입고마스터
			Map mInDtlSaveData = null; // 입고상세
			Map mInCpntSaveData = null; // 입고구성품
			Map mDisSaveData = null; // 재고
			Map mDisAmtSaveData = null; // 재고금액
			Map mDisHstSaveData = null; // 재고이력
			Map mDisInoutHstSaveData = null; // 재고입출력이력
			Map mEtcDisSaveData = null; // 기타재고
			Map mEtcDisHstSaveData = null; // 기타재고이력
			Map mEtcDisInoutHstSaveData = null; // 기타재고입출고이력

			String sProcFlag = null;
			String sInCd = "100"; // 입고구분
			String sInDtlCd = ""; // 입고상세구분
			String sDisSt = "01"; // 재고상태 : 01 가용, 02 비가용
			String sInSt = "03"; // 입고상태 : 01 미입고, 02 부분입고, 03 입고완료
			String[] keyList = null;
			//			String sObjCl    = "";    // 발주,매입 구분 변수
			String sCurrDt = DateUtils.getCurrentDate(); // 현재일자
			String sInPlcId = ""; // 입고처

			sInCd = (String) mInMst.get("in_cl");

			if (sInCd.equals("114")) {
				sInCd = "100";
				sInDtlCd = "114"; //매입입고
				
				sPrchsMgmtNo = requestData.getField("prchs_mgmt_no") == null ? mInMst.get("prchs_mgmt_no").toString() : requestData.getField("prchs_mgmt_no");
			} else {
				sInCd = "100";
				sInDtlCd = "101"; //구매입고
			}

			//입고처 
			sInPlcId = (String) mInMst.get("in_plc_id");
			if (sInPlcId == null || sInPlcId.equals("")) {
				sInPlcId = "11010"; // 본사창고..
			}

			// 2. 입고  처리 (입고상태를 입고완료로 처리함)
			// 2-1. 입고 tabel insert
			log.debug("마스터 생성 ------------------["
					+ (String) mInMst.get(CUD_FLAG_PARAM) + "]");

			if (INSERT_FLAG.equalsIgnoreCase((String) mInMst
					.get(CUD_FLAG_PARAM))
					|| UPDATE_FLAG.equalsIgnoreCase((String) mInMst
							.get(CUD_FLAG_PARAM))) {

				if (UPDATE_FLAG.equalsIgnoreCase((String) mInMst
						.get(CUD_FLAG_PARAM))) {

					// 입고 마스터 데이블 조회 lock 처리
				}
				mInSaveData = new HashMap();
				mInSaveData.putAll(mInMst); //마스터 데이터 넣기.
				mInSaveData.put("in_mgmt_no", sInMgmtNo); //입고관리번호 셋팅			
				//				mInSaveData.put("ord_mgmt_no" , ""); //발주관리번호 셋팅			
				//				mInSaveData.put("out_mgmt_no" , "");  //매입출고관리번호 셋팅			
				//				mInSaveData.put("in_schd_dt"  , "");  //입고관리번호 셋팅			
				//				mInSaveData.put("prchs_plc_id", "");  //매입처 셋팅			
				//				mInSaveData.put("in_plc_id"   , "");  //입고처 셋팅			
				mInSaveData.put("in_cl", sInDtlCd); //입고구분	
				mInSaveData.put("in_fix_dt", sCurrDt); //입고확정일자	
				mInSaveData.put("in_st", sInSt); //입고상태	

				if (INSERT_FLAG.equalsIgnoreCase((String) mInMst
						.get(CUD_FLAG_PARAM))) {

					insert("DISINN00400.insertInM", mInSaveData, onlineCtx);
					insertCount++;
				}
			}

			log.debug("입고마스터 생성완료 ------------------[" + insertCount + "]");

			if (sInDtlCd.equals("114")) {
				// 매입입고
				iCount = 1;
			} else {
				//구매입고
				iCount = rsProdList.getRecordCount();
			}

			// 2-2. 입고상세 tabel insert
			for (int i = 0; i < iCount; i++) {

				mProdData = rsProdList.getRecordMap(i);

				mInDtlSaveData = new HashMap();
				// 마스터 데이터 넣기.
				mInDtlSaveData.putAll(mInMst);
				// 상품 데이터 넣기.
				mInDtlSaveData.putAll(mProdData);
				// 공통 데이터 넣기.
				mInDtlSaveData.putAll(mCommData);

				if (INSERT_FLAG.equalsIgnoreCase((String) mProdData
						.get(CUD_FLAG_PARAM))
						|| UPDATE_FLAG.equalsIgnoreCase((String) mProdData
								.get(CUD_FLAG_PARAM))) {

					if (UPDATE_FLAG.equalsIgnoreCase((String) mProdData
							.get(CUD_FLAG_PARAM))) {
						/*************** lock 처리 시작 ***************/
						minData = null;
						sLockCnt = "";
						minData = (Map) queryForObject(
								"DISINN00400.getInnDtlByLock", mSaveData,
								onlineCtx);
						sLockCnt = ((BigDecimal) minData.get("CNT")).toString();

						if (sLockCnt.equals(sZero)) {
							// 변경된 데이터가 있습니다. 재 조회 하시기 바랍니다.
							throw new BizRuntimeException("PSMW5010");
						}
						/*************** lock 처리 종료 ***************/
					}

					// 입고순번 얻기
					Map mInSeq = new HashMap();
					mInSeq.put("in_mgmt_no", sInMgmtNo); // 상품코드

					sInSeq = this.getInSeq(mInSeq, onlineCtx);

					log.debug(" 입고순번 ------------------[" + sInSeq + "]");
					//mProdData.put("in_seq", sInSeq); // 입고순번  셋팅.
					mInDtlSaveData.put("in_seq", sInSeq); // 기타재고순번  셋팅.

					mInDtlSaveData.put("in_mgmt_no", sInMgmtNo);
					mInDtlSaveData.put("prchs_mgmt_no", sPrchsMgmtNo);
					mInDtlSaveData.put("in_qty", (int) Double
							.parseDouble((String) mProdData.get("in_qty"))); //입고확정수량	
					mInDtlSaveData.put("in_fix_dt", sCurrDt); //입고확정일자	
					mInDtlSaveData.put("in_fix_yn", "Y"); //입고확정여부	
					mInDtlSaveData.put("in_fix_qty", (int) Double
							.parseDouble((String) mProdData.get("in_qty"))); //입고확정수량	
					mInDtlSaveData.put("in_amt", (int) Double
							.parseDouble((String) mProdData.get("in_amt"))); //입고금

					if (INSERT_FLAG.equalsIgnoreCase((String) mProdData
							.get(CUD_FLAG_PARAM))) {
						log.debug("입고상세 value ------------------["
								+ mInDtlSaveData + "]");

						insert("DISINN00400.insertInDtl", mInDtlSaveData,
								onlineCtx);
						insertCount++;

						log.debug("입고상세 생성중 ------------------[" + insertCount
								+ "]");

					} else if (UPDATE_FLAG.equalsIgnoreCase((String) mProdData
							.get(CUD_FLAG_PARAM))) {

						sProcFlag = "U"; // U:수정
						updateCount++;
						mInDtlSaveData.put("in_seq", (int) Double
								.parseDouble((String) mProdData.get("in_seq")));
						mInDtlSaveData.put("PROC_FLAG", sProcFlag);

					}

					/*
					 keyList = (String[]) mInDtlSaveData.keySet().toArray(
					 new String[iZero]);

					 
					 for (int j = 0; j < keyList.length; j++) {
					 log.debug("keyList ---[" + mInDtlSaveData.get(keyList[j]) + "]");

					 mInDtlSaveData.put(keyList[j].toUpperCase(), mInDtlSaveData
					 .get(keyList[j]));
					 }
					 */

					//				}
					//			}
					log.debug("입고상세 생성완료 ------------------[" + insertCount
							+ "]");

					// 2-3. 입고구성품 tabel insert
					// 매입일경우만 구성품을 생성시켜줌
					if (sInDtlCd == "114") {
						//				for (int i = 0; i < rsProdList.getRecordCount(); i++) {
						mProdData = rsProdList.getRecordMap(i);

						//					mInCpntSaveData = new HashMap();
						//					// 상품 데이터 넣기.
						//					mInCpntSaveData.putAll(mProdData);
						//					
						//					mInCpntSaveData.put("in_mgmt_no", sInMgmtNo);

						insert("DISINN00400.insertInCpnt", mInDtlSaveData,
								onlineCtx);
						insertCount++;
						//				}
					}
					log.debug("입고구성품 생성완료 ------------------[" + insertCount
							+ "]");

					// 3. 재고  처리
					// 3-1. 재고 tabel insert
					//			for (int i = 0; i < rsProdList.getRecordCount(); i++) {

					//				mProdData = rsProdList.getRecordMap(i);

					mDisSaveData = new HashMap();
					// 마스터 데이터 넣기.
					mDisSaveData.putAll(mInMst);
					// 상품 데이터 넣기.
					mDisSaveData.putAll(mProdData);
					// 공통 데이터 넣기.
					mDisSaveData.putAll(mCommData);

					log.debug("mInMst  ------------------[" + mInMst + "] \n");
					log.debug("mProdData ------------------[" + mProdData
							+ "] \n");
					log.debug("mCommData ------------------[" + mCommData
							+ "] \n");

					if (INSERT_FLAG.equalsIgnoreCase((String) mProdData
							.get(CUD_FLAG_PARAM))
							|| UPDATE_FLAG.equalsIgnoreCase((String) mProdData
									.get(CUD_FLAG_PARAM))) {

						if (UPDATE_FLAG.equalsIgnoreCase((String) mProdData
								.get(CUD_FLAG_PARAM))) {
							/*************** lock 처리 시작 ***************/
							minData = null;
							sLockCnt = "";
							minData = (Map) queryForObject(
									"DISINN00400.getInnDtlByLock", mSaveData,
									onlineCtx);
							sLockCnt = ((BigDecimal) minData.get("CNT"))
									.toString();

							if (sLockCnt.equals(sZero)) {
								// 변경된 데이터가 있습니다. 재 조회 하시기 바랍니다.
								throw new BizRuntimeException("PSMW5010");
							}
							/*************** lock 처리 종료 ***************/
						}

						mDisSaveData.put("in_mgmt_no", sInMgmtNo);
						mDisSaveData.put("in_qty", (int) Double
								.parseDouble((String) mProdData.get("in_qty"))); //입고확정수량	
						mDisSaveData.put("in_fix_dt", sCurrDt); //입고확정일자	
						mDisSaveData.put("in_fix_yn", "Y"); //입고확정여부	
						mDisSaveData.put("in_fix_qty", (int) Double
								.parseDouble((String) mProdData.get("in_qty"))); //입고확정수량	
						mDisSaveData.put("in_amt", (int) Double
								.parseDouble((String) mProdData.get("in_amt"))); //입고금

						if (INSERT_FLAG.equalsIgnoreCase((String) mProdData
								.get(CUD_FLAG_PARAM))) {

							/*
							 ,#hld_plc_id#
							 ,#dis_st#
							 ,#eqp_st#
							 ,#last_inout_dt#
							 ,#last_inout_cl#
							 ,#last_inout_dtl_cl#
							 ,#fst_in_fix_dt#
							 ,#fst_in_plc_id#
							 ,#fst_prchs_plc_id#
							 ,#last_mov_in_dt#
							 ,#old_cl#
							 */

							mDisSaveData.put("hld_plc_id", sInPlcId); //입고처 -> 보유처	
							mDisSaveData.put("dis_st", sDisSt); //재고상태	
							mDisSaveData.put("eqp_st", mProdData.get("eqp_st")); //단말기상태
							mDisSaveData.put("last_inout_dt", sCurrDt); //최종입출고일자
							mDisSaveData.put("last_inout_cl", sInCd); //최종입출고구분	
							mDisSaveData.put("last_inout_dtl_cl", sInDtlCd); //최종일출고상세구분	
							mDisSaveData.put("fst_in_fix_dt", sCurrDt); //최초 입고확정일자
							mDisSaveData.put("fst_in_plc_id", mInMst
									.get("in_plc_id")); //최초입고처	
							mDisSaveData.put("fst_prchs_plc_id", mInMst
									.get("prchs_plc_id")); //최초매입처	
							mDisSaveData.put("last_mov_in_dt", sCurrDt); //마지막이동입고일자
							mDisSaveData.put("old_cl", "1"); //중고구분	
							mDisSaveData
									.put("dis_amt", mProdData.get("in_amt")); //재고금액

							/*
							 prod_cd
							 ,color_cd
							 ,ser_num
							 ,hld_plc_id
							 ,qty
							 ,unit_prc
							 ,amt
							 ,un_use_qty
							 */
							mDisSaveData.put("qty", mProdData.get("in_qty")); //입고수량 ->수량
							mDisSaveData.put("unit_prc", mProdData
									.get("in_amt")); //입고단가 ->금액
							mDisSaveData.put("amt", mProdData.get("in_amt")); //입고금액 ->금액

							if (mProdData.get("prod_cl").toString().equals("1")) {

								// 단말기 재고 추가(매입 단말기)
								insert("DISINN00400.insertEqpDis",
										mDisSaveData, onlineCtx);

								// 단말기 재고 금액 추가
								mDisSaveData.put("occr_cl", "01"); //발생구분
								mDisSaveData.put("occr_dt", sCurrDt); //발생일자

								insert("DISINN00400.insertDisAmt",
										mDisSaveData, onlineCtx);

								// 단말기 재고 입출고 이력
								/*
								 prod_cd
								 ,color_cd
								 ,ser_num
								 ,inout_seq
								 ,in_mgmt_no
								 ,in_seq
								 ,out_mgmt_no
								 ,out_seq
								 ,inout_dt
								 ,inout_cl
								 ,inout_dtl_cl
								 ,prchs_plc_id
								 ,out_plc_id
								 ,in_plc_id
								 */
								mDisSaveData.put("in_mgmt_no", sInMgmtNo); //입고관리번호
								mDisSaveData.put("in_seq", sInSeq); //입고관리순번
								mDisSaveData.put("out_mgmt_no", ""); //출고관리번호
								mDisSaveData.put("out_seq", ""); //출고관리순번
								mDisSaveData.put("inout_dt", sCurrDt); //입출고일자
								mDisSaveData.put("inout_cl", sInCd); //입출고구분
								mDisSaveData.put("inout_dtl_cl", sInDtlCd); //입출고상세구분
								mDisSaveData.put("prchs_plc_id", mInMst
										.get("prchs_plc_id")); // 매입처
								mDisSaveData.put("out_plc_id", mInMst
										.get("prchs_plc_id")); //출고처
								mDisSaveData.put("in_plc_id", mInMst
										.get("in_plc_id")); //입고처

								insert("DISINN00400.insertDisInoutHst",
										mDisSaveData, onlineCtx);

								// 단말기 재고 이력
								mDisSaveData.put("inout_mgmt_no", sInMgmtNo); //입출고관리번호
								insert("DISINN00400.insertDisHst",
										mDisSaveData, onlineCtx);

								/* 20110809 배터리 재고 관리 제외  
								 if ( sInDtlCd.equals("114") ){

								 // 배터리 재고 추가(매입 단말기의 배터리)
								 insert("DISINN00400.insertPrchsBatDis", mDisSaveData, onlineCtx);							
								 }
								 */

								/*  20110809 배터리 재고 관리 제외  (기타 재고 처리)
								 } else if ( mProdData.get("prod_cl").toString().equals("3") ){

								 iInQty = (int) Double.parseDouble((String) mProdData.get("in_qty"));
								 
								 // 배터리 재고 추가(발주)
								 // 입고 수량 만큼 생성시켜준다.
								 for ( int k = 0 ; k < iInQty ; k++) {
								 //배터리 
								 // 일련번호번호 얻기.
								 Map mSerNum = new HashMap();
								 mSerNum.put("prod_cl", mProdData.get("prod_cl").toString());  // 상품구분
								 mSerNum.put("prod_cd", mProdData.get("prod_cd").toString()); // 상품코드
								 mSerNum.put("ser_num", "");
								 
								 sSerNum = this.getSerNum(mSerNum, onlineCtx);
								 log.debug("sSerNum  생성 ------------------[" + sSerNum + "]");
								 
								 mDisSaveData.put("ser_num", sSerNum); // 일련번호  셋팅.
								 
								 insert("DISINN00400.insertOrdBatDis", mDisSaveData, onlineCtx);							

								 // 단말기 재고 금액 추가
								 mDisSaveData.put("occr_cl"  , "01");        //발생구분
								 mDisSaveData.put("occr_dt"  , sCurrDt);     //발생일자
								 
								 insert("DISINN00400.insertDisAmt", mDisSaveData, onlineCtx);
								 
								 // 배터리 재고 입출고 이력
								 mDisSaveData.put("in_mgmt_no"   , sInMgmtNo);  //입고관리번호
								 mDisSaveData.put("in_seq"       , sInSeq);         //입고관리순번
								 mDisSaveData.put("out_mgmt_no"  , "");         //출고관리번호
								 mDisSaveData.put("out_seq"      , "");         //출고관리순번
								 mDisSaveData.put("inout_dt"     , sCurrDt);    //입출고일자
								 mDisSaveData.put("inout_cl"     , sInCd);     //입출고구분
								 mDisSaveData.put("inout_dtl_cl" , sInDtlCd);  //입출고상세구분
								 mDisSaveData.put("prchs_plc_id" , mInMst.get("prchs_plc_id"));  // 매입처
								 mDisSaveData.put("out_plc_id"   , mInMst.get("prchs_plc_id"));  //출고처
								 mDisSaveData.put("in_plc_id"    , mInMst.get("in_plc_id"));  //입고처
								 
								 insert("DISINN00400.insertDisInoutHst", mDisSaveData, onlineCtx);
								 
								 // 배터리 재고 이력
								 mDisSaveData.put("inout_mgmt_no"   , sInMgmtNo);  //입출고관리번호
								 insert("DISINN00400.insertDisHst", mDisSaveData, onlineCtx);
								 
								 }
								 */
							} else {
								// 기타재고 일련번호번호 얻기.
								Map mSerNum = new HashMap();
								mSerNum.put("prod_cl", mProdData.get("prod_cl")
										.toString()); // 상품구분
								mSerNum.put("prod_cd", mProdData.get("prod_cd")
										.toString()); // 상품코드
								mSerNum.put("ser_num", "");

								sSerNum = this.getSerNum(mSerNum, onlineCtx);
								mDisSaveData.put("ser_num", sSerNum); // 일련번호  셋팅.

								// 기타재고순번 얻기
								Map mEtcSeq = new HashMap();
								mEtcSeq.put("prod_cd", mProdData.get("prod_cd")
										.toString()); // 상품코드
								mEtcSeq.put("color_cd", mProdData.get(
										"color_cd").toString()); // 색상코드
								mEtcSeq.put("ser_num", sSerNum); // 일련번호

								sEtcSeq = this.getEtcSeq(mEtcSeq, onlineCtx);
								log.debug("etc_seq  생성end ------------------["
										+ sEtcSeq + "]");
								mDisSaveData.put("etc_seq", sEtcSeq); // 기타재고순번  셋팅.

								// 기타 재고 추가(발주에 의한 젠더 등 기타 구성품)
								insert("DISINN00400.insertEtcDis",
										mDisSaveData, onlineCtx);

								// 배터리 재고 입출고 이력
								mDisSaveData.put("in_mgmt_no", sInMgmtNo); //입고관리번호
								mDisSaveData.put("in_seq", sInSeq); //입고관리순번
								mDisSaveData.put("out_mgmt_no", ""); //출고관리번호
								mDisSaveData.put("out_seq", ""); //출고관리순번
								mDisSaveData.put("inout_dt", sCurrDt); //입출고일자
								mDisSaveData.put("inout_cl", sInCd); //입출고구분
								mDisSaveData.put("inout_dtl_cl", sInDtlCd); //입출고상세구분
								mDisSaveData.put("inout_qty", mProdData
										.get("in_qty")); //입출고수량
								mDisSaveData.put("prchs_plc_id", mInMst
										.get("prchs_plc_id")); // 매입처
								mDisSaveData.put("out_plc_id", mInMst
										.get("prchs_plc_id")); //출고처
								mDisSaveData.put("in_plc_id", mInMst
										.get("in_plc_id")); //입고처

								insert("DISINN00400.insertEtcDisInoutHst",
										mDisSaveData, onlineCtx);

								// 배터리 재고 이력
								insert("DISINN00400.insertEtcDisHst",
										mDisSaveData, onlineCtx);

							}

							insertCount++;

							log.debug("재고  생성중 ------------------["
									+ insertCount + "]");

						}

					}
				}
				log.debug("재고  완료 ------------------[" + insertCount + "]");

				//			}
				//			}			
				// 3-2. 재고금액 tabel insert
				// 3-3. 재고입출고이력 tabel insert
				// 3-4. 재고이력 tabel insert
				// 3-5. 기타재고 tabel insert
				// 3-6. 기타재고입출고이력 tabel insert
				// 3-7. 기타재고이력 tabel insert			
			}
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount, "" + deleteCount });

		// 저장 후 다시 조회 하기 위해 필요한 key
		//		Map mReturn = new HashMap();
		//		mReturn.put("in_mgmt_no", mInMst.get("in_mgmt_no"));
		//
		//		responseData.putFieldMap(mReturn);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 일련번호를 얻는다.
	 * @param data
	 *            요청정보를 Wrapping하고 있는 Map 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public String getSerNum(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00400.getSerNum method start");
		}
		String sSerNum = ""; //일련번호

		Map mSerNum = (Map) queryForObject("DISINN00400.getSerNum", data,
				onlineCtx);

		sSerNum = (String) mSerNum.get("SER_NUM");

		if (sSerNum == null || sSerNum.equals("")) {
			throw new BizRuntimeException("일련번호 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}

		return sSerNum;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 기타재고 순번을 얻는다.
	 * @param data
	 *            요청정보를 Wrapping하고 있는 Map 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public String getEtcSeq(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00400.getEtcSeq method start");
		}
		String sEtcSeq = ""; //기타재고순번

		Map mEtcSeq = (Map) queryForObject("DISINN00400.getEtcSeq", data,
				onlineCtx);

		sEtcSeq = mEtcSeq.get("ETC_SEQ").toString();

		if (sEtcSeq == null || sEtcSeq.equals("")) {
			throw new BizRuntimeException(
					"기타재고순번 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}

		return sEtcSeq;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 입고 순번을 얻는다.
	 * @param data
	 *            요청정보를 Wrapping하고 있는 Map 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public String getInSeq(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00400.getInSeq method start");
		}
		String sInSeq = ""; //입고순번

		Map mInSeq = (Map) queryForObject("DISINN00400.getInSeq", data,
				onlineCtx);
		log.debug("mInSeq 생성중1 ===" + mInSeq);
		sInSeq = mInSeq.get("IN_SEQ").toString();
		log.debug("mInSeq 생성중2 ===" + mInSeq);
		if (sInSeq == null || sInSeq.equals("")) {
			throw new BizRuntimeException("입고순번 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}

		return sInSeq;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveInnCncl(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00400.saveInnCncl method start");
		}
		
		// 매입에 대한 입고 취소 처리
		// 1. 입고  취소 처리 ( 판매관리번호로 입고정보 조회) 
		// 1-1. 입고          tabel delete
		// 1-2. 입고상세    tabel delete
		// 1-3. 입고구성품 tabel delete
		// 2. 재고  취소 처리
		// 2-1. 재고                 tabel delete
		// 2-2. 재고금액           tabel delete
		// 2-3. 재고입출고이력 tabel delete
		// 2-4. 재고이력           tabel delete

		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;
		
		String sInMgmtNo    = "";  // 입고관리번호
		String sPrchsMgmtNo = "";  // 매입관리번호
		String sInoutCl     = "";  // 입출고구분
		
		Map mCond = null;          // 조회조건

		// 사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		// 판매관리번호 check
//		IRecordSet mgmt = requestData.getRecordSet("ds_consult_m");
//		IRecordSet rsInMst = requestData.getRecordSet("ds_inMaster");

		IRecordSet rsInMst = requestData.getRecordSet("ds_consult_m");
		Map mInMst = rsInMst.getRecordMap(0);
		sPrchsMgmtNo = (String) mInMst.get("prchs_mgmt_no");		

		if (sPrchsMgmtNo == null || sPrchsMgmtNo.equals("")) {
			throw new BizRuntimeException("입고취소 처리중 판매관리번호 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}
		
		// 입고관리번호 조회 및 상태 check
		mCond = new HashMap();
		mCond.put("prchs_mgmt_no", sPrchsMgmtNo);           // 판매관리번호
		mCond.put("prod_cd"      , mInMst.get("prod_cd"));  // 상품코드
		mCond.put("color_cd"     , mInMst.get("color_cd")); // 색상코드
		mCond.put("ser_num"      , mInMst.get("ser_num"));  // 일련번호
		// return : prchs_mgmt_no, prod_cd, color_cd, ser_num, in_mgmt_no, inout_cl, in_seq
		//          in_upd_cnt, dis_upd_cnt
		log.debug("============================ 1");
		Map mInInfo = (Map) queryForObject("DISINN00400.getInnInfo", mCond, onlineCtx);
		log.debug("============================ 2");
		// 입고관리번호 check
		sInMgmtNo = mInInfo.get("IN_MGMT_NO").toString();
		log.debug("============================ 3");
		if (sInMgmtNo == null || sInMgmtNo.equals("")) {
			throw new BizRuntimeException("입고정보가 없습니다. 담당자에게 문의하세요.");
		}
		log.debug("============================ 4");
		// 입출고구분 check
		sInoutCl = mInInfo.get("INOUT_CL").toString();
		log.debug("============================ 5");
		if (sInoutCl == null || sInoutCl.equals("")) {
			throw new BizRuntimeException("입출고상태 정보가 없습니다. 담당자에게 문의하세요.");
		}else if (sInoutCl.substring(1, 1).equals("3")) {
			throw new BizRuntimeException("매입 단말기가 출고되어 처리할수 없습니다. 담당자에게 문의하세요.");
		}
		log.debug("============================ 6");
		// 1. 입고  취소 처리 ( 판매관리번호로 입고정보 조회) 
		// 1-1. 입고          tabel delete
		// 1-2. 입고상세    tabel delete
		// 1-3. 입고구성품 tabel delete
		update("DISINN00400.deleteInM"   ,	mInInfo, onlineCtx);	
		updateCount++;
		log.debug("============================ 7");
		update("DISINN00400.deleteInDtl" ,	mInInfo, onlineCtx);		
		updateCount++;
		log.debug("============================ 8");
		update("DISINN00400.deleteInCpnt",	mInInfo, onlineCtx);		
		updateCount++;
		log.debug("============================ 9");
		// 2. 재고  취소 처리
		// 2-1. 재고                 tabel delete
		// 2-2. 재고금액           tabel delete
		// 2-3. 재고입출고이력 tabel delete
		// 2-4. 재고이력           tabel delete
		update("DISINN00400.deleteDis"        ,	mInInfo, onlineCtx);		
		updateCount++;

		update("DISINN00400.deleteDisAmt"     ,	mInInfo, onlineCtx);		
		updateCount++;

		update("DISINN00400.deleteDisInoutHst",	mInInfo, onlineCtx);	
		updateCount++;

		update("DISINN00400.deleteDisHst"     ,	mInInfo, onlineCtx);	
		updateCount++;
		
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount, "" + deleteCount });

		// 저장 후 다시 조회 하기 위해 필요한 key
		//		Map mReturn = new HashMap();
		//		mReturn.put("in_mgmt_no", mInMst.get("in_mgmt_no"));
		//
		//		responseData.putFieldMap(mReturn);

		return responseData;
	}
}