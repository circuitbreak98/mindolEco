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

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.DuplicationException;
import nexcore.framework.integration.db.NoRecordAffectedException;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-19 09:53:03</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class DISINN00200 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getOrdDtlList(IDataSet requestData, IOnlineContext onlineCtx) {

		//		 발주관리번호 레코드 셋 추출.
		IRecordSet rsOrder = requestData.getRecordSet("ds_ordMaster");

		// 발주 master 조회.
		IRecordSet rsOrdMaster = queryForRecordSet("DISINN00200.getOrdMaster",
				rsOrder.getRecordMap(0), onlineCtx);

		if (rsOrdMaster == null) {
			rsOrdMaster = new RecordSet("ds_ordMaster");
		}

		// 발주 detail List 조회.
		IRecordSet rsOrdDtlList = queryForRecordSet(
				"DISINN00200.getOrdDtlList", rsOrder.getRecordMap(0), onlineCtx);

		if (rsOrdDtlList == null) {
			rsOrdDtlList = new RecordSet("ds_ordDtlList");
		}

		// 색상 List 조회.
		IRecordSet rsColorList = queryForRecordSet("DISINN00200.getColorList",
				rsOrder.getRecordMap(0), onlineCtx);

		if (rsColorList == null) {
			rsColorList = new RecordSet("ds_colorList");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsOrdDtlList.getRecordCount()) });

		responseData.putRecordSet("ds_ordMaster", rsOrdMaster);
		responseData.putRecordSet("ds_ordDtlList", rsOrdDtlList);
		responseData.putRecordSet("ds_colorList", rsColorList);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdColorList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00200.getProdColorList method start");
		}

		// 상품 색상 리스트 조회.
		IRecordSet rsProdColorList = queryForRecordSet(
				"DISINN00200.getProdColorList", requestData.getFieldMap(),
				onlineCtx);

		if (rsProdColorList == null) {
			rsProdColorList = new RecordSet("ds_prodColorList");
		}

		// 색상 List 조회.
		IRecordSet rsColorList = queryForRecordSet("DISINN00200.getColorList",
				requestData.getFieldMap(), onlineCtx);

		if (rsColorList == null) {
			rsColorList = new RecordSet("ds_colorList");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsProdColorList.getRecordCount()) });

		responseData.putRecordSet("ds_prodColorList", rsProdColorList);
		responseData.putRecordSet("ds_colorList", rsColorList);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getUnitPrc(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00200.getUnitPrc method start");
		}

		IRecordSet rsList = queryForRecordSet("DISINN00200.getUnitPrc",
				requestData.getFieldMap(), onlineCtx);

		if (rsList == null) {
			rsList = new RecordSet("ds_unitPrc");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsList.getRecordCount()) });

		responseData.putRecordSet("ds_unitPrc", rsList);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이강수 (leekangsu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveOrd(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00200.saveOrd method start");
		}

		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;
		String sMgmtNoCl = "OD"; // 관리번호구분 : OD(발주)
		String sOrgMgmtNo = null;
		String sZero = "0";

		//사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		// 발주관리번호.
		IRecordSet rsKey = requestData.getRecordSet("ds_key");
		sOrgMgmtNo = rsKey.getRecord(0).get("ord_mgmt_no");

		// 발주 MASTER 처리. 
		IRecordSet rsOrdMst = requestData.getRecordSet("ds_ordMaster");

		if (rsOrdMst != null && rsOrdMst.getRecordCount() > 0) {

			IRecord rOrdMst = rsOrdMst.getRecord(0); // 발주 MASTER record

			// 발주관리번호가 없을 경우 INSERT
			if (sOrgMgmtNo == null || sOrgMgmtNo.equals("")) {

				// 발주관리번호 얻기.
				Map mMgmt = new HashMap();
				mMgmt.put("OV_ERRCODE", ""); // 에러.
				mMgmt.put("OV_ERRMSG", ""); // 에러메세지.
				mMgmt.put("IV_MGMT_NO_CD", sMgmtNoCl); // 관리번호구분.
				mMgmt.put("IV_USER_ID", iUserInfo.getLoginId()); // 처리자.
				mMgmt.put("OV_MGMT_NO", "");

				sOrgMgmtNo = this.getMgmtNo(mMgmt, onlineCtx);
				rOrdMst.put("ord_mgmt_no", sOrgMgmtNo);

				insert("DISINN00200.insertOrdMaster", rOrdMst, onlineCtx);
				insertCount++;

			} else {

				// 변경 되었을 경우에만 처리.
				if (UPDATE_FLAG.equalsIgnoreCase(rOrdMst.get(CUD_FLAG_PARAM))) {

					updateCount = updateCount
							+ update("DISINN00200.updateOrdMaster", rOrdMst,
									onlineCtx);
				}
			}
		}

		// 발주 DETAIL 처리
		IRecordSet rsOrdDtl = requestData.getRecordSet("ds_ordDtlList");
		Map mInnData = null;
		String sInnCnt = "";
		String sLockCnt = "";
		Map mOrdData = null;

		if (rsOrdDtl != null) {

			for (int i = 0; i < rsOrdDtl.getRecordCount(); i++) {

				if (DELETE_FLAG.equalsIgnoreCase(rsOrdDtl.getRecord(i).get(
						CUD_FLAG_PARAM))) {

					/*************** lock 처리 시작 ***************/
					mOrdData = (Map) queryForObject(
							"DISINN00200.getOrdDetailByLock", rsOrdDtl
									.getRecord(i), onlineCtx);
					sLockCnt = ((BigDecimal) mOrdData.get("CNT")).toString();

					if (sLockCnt.equals(sZero)) {
						// 변경된 데이터가 있습니다. 재 조회 하시기 바랍니다.
						throw new BizRuntimeException("PSMW5010");
					}
					/*************** lock 처리 종료 ***************/

					/*** 이미 처리된 발주건인지 조회하여 처리된 경우 exception 처리. 시작 ***/
					mInnData = (Map) queryForObject(
							"DISINN00200.getInnDataCnt", rsOrdDtl.getRecord(i),
							onlineCtx);

					sInnCnt = ((BigDecimal) mInnData.get("CNT")).toString();

					if (!sInnCnt.equals(sZero)) {
						// 이미 처리된 데이터 입니다.
						throw new BizRuntimeException("PSMW5011");
					}
					/*** 이미 처리된 발주건인지 조회하여 처리된 경우 exception 처리. 종료 ***/

					deleteCount = deleteCount
							+ delete("DISINN00200.deleteOrdDetail", rsOrdDtl
									.getRecord(i), onlineCtx);
				}
			}

			Map ordMap = null;

			for (int i = 0; i < rsOrdDtl.getRecordCount(); i++) {

				// 발주관리번호 셋팅.
				rsOrdDtl.getRecord(i).put("ord_mgmt_no", sOrgMgmtNo);

				if (INSERT_FLAG.equalsIgnoreCase(rsOrdDtl.getRecord(i).get(
						CUD_FLAG_PARAM))) {

					try {
						insert("DISINN00200.insertOrdDetail", rsOrdDtl
								.getRecord(i), onlineCtx);
					} catch (DuplicationException e) {

						ordMap = rsOrdDtl.getRecord(i);

						ordMap.put("settl_cond_cd_org", ordMap
								.get("settl_cond_cd"));

						updateCount = updateCount
								+ update("DISINN00200.updateOrdDetailByDum",
										ordMap, onlineCtx);
					}

					insertCount++;

				} else if (UPDATE_FLAG.equalsIgnoreCase(rsOrdDtl.getRecord(i)
						.get(CUD_FLAG_PARAM))) {

					/*************** lock 처리 시작 ***************/
					mOrdData = (Map) queryForObject(
							"DISINN00200.getOrdDetailByLock", rsOrdDtl
									.getRecord(i), onlineCtx);
					sLockCnt = ((BigDecimal) mOrdData.get("CNT")).toString();

					if (sLockCnt.equals(sZero)) {
						// 변경된 데이터가 있습니다. 재 조회 하시기 바랍니다.
						throw new BizRuntimeException("PSMW5010");

					}
					/*************** lock 처리 종료 ***************/

					/*** 이미 처리된 발주건인지 조회하여 처리된 경우 exception 처리. 시작 ***/
					mInnData = (Map) queryForObject(
							"DISINN00200.getInnDataCnt", rsOrdDtl.getRecord(i),
							onlineCtx);

					sInnCnt = ((BigDecimal) mInnData.get("CNT")).toString();

					if (!sInnCnt.equals(sZero)) {
						// 이미 처리된 데이터 입니다.
						throw new BizRuntimeException("PSMW5011");
					}
					/*** 이미 처리된 발주건인지 조회하여 처리된 경우 exception 처리. 종료 ***/

					updateCount = updateCount
							+ update("DISINN00200.updateOrdDetail", rsOrdDtl
									.getRecord(i), onlineCtx);
				}
			}

			cudAllCount = insertCount + updateCount + deleteCount;
		}

		if (cudAllCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount, "" + deleteCount });

		// 저장 후 다시 조회 하기 위해 필요한 key
		Map mReturn = new HashMap();
		mReturn.put("ord_mgmt_no", sOrgMgmtNo);

		responseData.putFieldMap(mReturn);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이강수 (leekangsu)
	 *  - 관리번호를 얻는다.
	 * @param data
	 *            요청정보를 Wrapping하고 있는 Map 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public String getOrdMgmtNo(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00200.getMgmtNo method start");
		}
		String sMgmtNo = ""; //관리번호
		log.debug("##### data [" + data);
		Map mgmtNo = (Map) queryForObject("DISINN00200.getOrdMgmtNo", data,
				onlineCtx);
		log.debug("##### mgmtNo [" + mgmtNo);

		sMgmtNo = (String) mgmtNo.get("MGMT_NO");

		if (sMgmtNo == null || sMgmtNo.equals("")) {
			throw new BizRuntimeException("관리번호 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}

		return sMgmtNo;
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
	
	/**
	 * 
	 *
	 * @author 이강수 (leekangsu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteOrd(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00200.deleteOrd method start");
		}

		int deleteCount = 0;
		String sZero = "0";

		// 발주 MASTER 처리.
		IRecordSet rsOrdMst = requestData.getRecordSet("ds_ordMaster");
		IRecord rOrdMst = rsOrdMst.getRecord(0); // 발주 MASTER record

		deleteCount = deleteCount
				+ delete("DISINN00200.deleteOrdMaster", rOrdMst, onlineCtx);

		// 발주 DETAIL 처리
		IRecordSet rsOrdDtl = requestData.getRecordSet("ds_ordDtlList");
		Map mInnData = null;
		String sInnCnt = "";
		String sLockCnt = "";
		Map mOrdData = null;

		if (rsOrdDtl != null) {

			for (int i = 0; i < rsOrdDtl.getRecordCount(); i++) {

				/*************** lock 처리 시작 ***************/
				mOrdData = (Map) queryForObject(
						"DISINN00200.getOrdDetailByLock",
						rsOrdDtl.getRecord(i), onlineCtx);
				sLockCnt = ((BigDecimal) mOrdData.get("CNT")).toString();

				if (sLockCnt.equals(sZero)) {
					// 변경된 데이터가 있습니다. 재 조회 하시기 바랍니다.
					throw new BizRuntimeException("PSMW5010");
				}
				/*************** lock 처리 종료 ***************/

				/*** 이미 처리된 발주건인지 조회하여 처리된 경우 exception 처리. 시작 ***/
				mInnData = (Map) queryForObject("DISINN00200.getInnDataCnt",
						rsOrdDtl.getRecord(i), onlineCtx);

				sInnCnt = ((BigDecimal) mInnData.get("CNT")).toString();

				if (!sInnCnt.equals(sZero)) {
					// 이미 처리된 데이터 입니다.
					throw new BizRuntimeException("PSMW5011");
				}
				/*** 이미 처리된 발주건인지 조회하여 처리된 경우 exception 처리. 종료 ***/

				deleteCount = deleteCount
						+ delete("DISINN00200.deleteOrdDetail", rsOrdDtl
								.getRecord(i), onlineCtx);
			}
		}

		if (deleteCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.DELETE_OK_MESSAGE_ID, new String[] { ""
						+ deleteCount });
	}
}