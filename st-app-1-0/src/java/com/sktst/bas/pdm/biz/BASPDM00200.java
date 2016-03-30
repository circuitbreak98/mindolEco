/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.pdm.biz;

import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;
import nexcore.framework.integration.db.NoRecordFoundException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : 상품 등록 및 수정</li>
 * <li>작성일 : 2009-01-21 10:24:38</li>
 * </ul>
 *
 * @author 전현주 (junhyunjoo)
 */
public class BASPDM00200 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * @title  상품관리 수정 및 상품 색상 수정
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : PROD_CL [필드1] 
	 *	- field : SKT_OPER_YN [필드2] 
	 *	- field : USE_YN [필드3] 
	 *	- field : USE_STOP_DT [필드4] 
	 *	- field : PROD_CHRTIC_1 [필드5] 
	 *	- field : PROD_CHRTIC_2 [필드6] 
	 *	- field : BARCD_TYP [필드7] 
	 *	- field : RMKS [필드8] 
	 *	- field : PROD_CD [필드9] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */

	public IDataSet updateProductMgmt(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("updateProductMgmt method start");
		}

		int updateCount = update("BASPDM00200.updateProductMgmt", requestData
				.getFieldMap(), onlineCtx);

		IRecordSet rs = requestData.getRecordSet("ds_color");

		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				delete("BASPDM00200.deleteProductColor", rs.getRecord(i));
			}
			for (int i = 0; i < rs.getRecordCount(); i++) {

				insert("BASPDM00200.insertProductColor", rs.getRecordMap(i),
						onlineCtx);
			}
		}

		String ifYn = requestData.getField("op_dt");
		if (ifYn != null && "".compareTo(ifYn) != 0) {
			update("BASPDM00200.updateIfModelApplyYn", requestData
					.getFieldMap(), onlineCtx);
		}

		if (updateCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
						+ updateCount });
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * @title  상품 정보 조회
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : PROD_CD [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : PROD_CD [필드1] 
	 *	- field : PROD_NM [필드2] 
	 *	- field : MFACT_ID [필드3] 
	 *	- field : MKTG_DT [필드4] 
	 *	- field : END_DT [필드5] 
	 *	- field : PROD_CL [필드6] 
	 *	- field : PROD_DTL_CL [필드7] 
	 *	- field : SKT_OPER_YN [필드8] 
	 *	- field : USE_YN [필드9] 
	 *	- field : USE_STOP_DT [필드10] 
	 *	- field : PROD_CHRTIC_1 [필드11] 
	 *	- field : PROD_CHRTIC_2 [필드12] 
	 *	- field : PROD_CHRTIC_3 [필드13] 
	 *	- field : PROD_CHRTIC_4 [필드14] 
	 *	- field : BAR_CD_TYP [필드15] 
	 *	- field : RGST_CL [필드16] 
	 *	- field : RMKS [필드17] 
	 *	- field : COM_MTHD [필드18] 
	 *	- field : MST_PROD_CD [필드19] 
	 *	- field : MST_PROD_NM [필드20] 
	 *	- field : SER_LEN [필드21] 
	 *	- field : U_KEY_IF_YN [필드22] 
	 */
	public IDataSet getProductDetail(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		if (log.isDebugEnabled()) {
			log.debug("getProductDetail method start");
		}

		Map map = (Map) queryForObject("BASPDM00200.getProductDetail",
				requestData.getFieldMap());
		if (map == null) {
			throw new NoRecordFoundException();
		}

		Map sUplstMap = (Map) queryForObject("BASPDM00200.getUplstAplyMdlCnt",
				requestData.getFieldMap());

		map.put("UPLST_MDL_CNT", sUplstMap.get("CNT").toString());

		IRecordSet rs = queryForRecordSet("BASPDM00200.getProductColor",
				requestData.getFieldMap());

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		responseData.putFieldMap(map);
		responseData.putRecordSet("ds_color", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * @title  상품 정보 조회
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : color_cd [필드1] 
	 *	- field : name [필드2] 
	 */
	public IDataSet getColorList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getColorList method start");
		}

		IRecordSet rs = queryForRecordSet("BASPDM00200.getColorList",
				requestData.getFieldMap());

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_color", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * @title 상품관리 등록 및 상품 색상 등록
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : PROD_CD [필드1] 
	 *	- field : PROD_NM [필드2] 
	 *	- field : MFACT_ID [필드3] 
	 *	- field : MKTG_DT [필드4] 
	 *	- field : END_DT [필드5] 
	 *	- field : U_KEY_IF_YN [필드6] 
	 *	- field : PROD_CL [필드7] 
	 *	- field : SKT_OPER_YN [필드8] 
	 *	- field : USE_YN [필드9] 
	 *	- field : USE_STOP_DT [필드10] 
	 *	- field : PROD_CHRTIC_1 [필드11] 
	 *	- field : PROD_CHRTIC_2 [필드12] 
	 *	- field : BARCD_TYP [필드13] 
	 *	- field : RGST_CL [필드14] 
	 *	- field : RMKS [필드15] 
	 *	- field : DEL_YN [필드16] 
	 *	- field : UPD_CNT [필드17] 
	 *	- field : INS_DTM [필드18] 
	 *	- field : INS_USER_ID [필드19] 
	 *	- field : MOD_DTM [필드20] 
	 *	- field : MOD_USER_ID [필드21] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addProductMgmt(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("addProductMgmt method start");
		}

		insert("BASPDM00200.insertProductMgmt", requestData.getFieldMap(),
				onlineCtx);

		IRecordSet rs = requestData.getRecordSet("ds_color");

		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {

				insert("BASPDM00200.insertProductColor", rs.getRecordMap(i),
						onlineCtx);
			}
		}

		String ifYn = requestData.getField("op_dt");
		if (ifYn != null && "".compareTo(ifYn) != 0) {
			update("BASPDM00200.updateIfModelApplyYn", requestData
					.getFieldMap(), onlineCtx);
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "1" });
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * @title  상품코드 중복체크
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdCodeCheck(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdCodeCheck method start");
		}

		IRecordSet rs = queryForRecordSet("BASPDM00200.getProdCodeCheck",
				requestData.getFieldMap());

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_check", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * @title  상품명 중복 체크
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdNmCheck(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdNmCheck method start");
		}

		IRecordSet rs = queryForRecordSet("BASPDM00200.getProdNmCheck",
				requestData.getFieldMap());

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_check", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * @title  제조사 정보 조회
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getChoData(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getChoData method start");
		}

		IRecordSet rs = queryForRecordSet("BASPDM00200.getChoData", requestData
				.getFieldMap());

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_mfact_id", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * 			- 상품 정보 삭제
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteProductMgmt(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("deleteProductMgmt method start");
		}

		int updateCount = update("BASPDM00200.deleteProductMgmt", requestData
				.getFieldMap());

		IRecordSet rs = requestData.getRecordSet("ds_color");

		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				delete("BASPDM00200.deleteProductColor", rs.getRecord(i));
			}
		}

		if (updateCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
						+ updateCount });
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 상품 등록 중복 체크
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet checkDuplication(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdNmCheck method start");
		}

		// 2. CRUD 처리
		IRecordSet rs1 = queryForRecordSet("BASPDM00200.getProdCodeCheck",
				requestData.getFieldMap());

		IRecordSet rs2 = queryForRecordSet("BASPDM00200.getProdNmCheck",
				requestData.getFieldMap());

		int cnt1Count = Integer.parseInt(rs1.get(0, "CNT1"));
		int cnt2Count = Integer.parseInt(rs2.get(0, "CNT2"));

		// 3. 예외지정
		if (cnt1Count > 0) {
			throw new BizRuntimeException(BaseConstants.CHECK_REG,
					new String[] { "모델코드" });
		}

		if (cnt2Count > 0) {
			throw new BizRuntimeException(BaseConstants.CHECK_REG,
					new String[] { "모델명" });
		}

		// 4. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, null);

		return responseData;
	}

}