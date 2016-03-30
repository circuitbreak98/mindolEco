/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.pdm.biz;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-02-04 18:06:06</li>
 * </ul>
 *
 * @author 조민지 (jominji)
 */
public class BASPDM00400 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 조민지 (jominji)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : prod_cl [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : prod_bar_cd_mgmt
	 *		- field : prod_cl [상품구분] 
	 *		- field : bar_cd_typ [바코드유형] 
	 *		- field : all_len_num [전체자리수] 
	 *		- field : mdl_sta_len [모델시작자리] 
	 *		- field : mdl_end_len [모델끝자리] 
	 *		- field : mdl_len_num [모델자리수] 
	 *		- field : color_sta_len [색상시작자리] 
	 *		- field : color_end_len [색상끝자리] 
	 *		- field : color_len_num [색상자리수] 
	 *		- field : ser_num_sta_len [일련번호시작자리] 
	 *		- field : ser_num_end_len [일련번호끝자리] 
	 *		- field : ser_num_len_num [일련번호자리수] 
	 *		- field : upd_cnt [update count] 
	 *		- field : ins_dtm [입력일시] 
	 *		- field : ins_user_id [입력자id] 
	 *		- field : mod_dtm [수정일시] 
	 *		- field : mod_user_id [필드16] 
	 */
	public IDataSet getProductBarcodeList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPDM00400.getProductBarcodeList method start >>>");
		}


		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("BASPDM00400.getProductBarcodeList",
				requestData.getFieldMap(),onlineCtx);



		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("prod_bar_cd_mgmt", rs);
		return responseData;

	}

	/**
	 * 
	 *
	 * @author 조민지 (jominji)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdClChoData(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPDM00400.getProdClChoData method start");
		}

		IRecordSet rs = queryForRecordSet("BASPDM00400.getProdClChoData",
				requestData.getFieldMap(),onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_prod_cl", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 조민지 (jominji)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : prod_bar_cd_mgmt
	 *		- field : prod_cl [상품구분] 
	 *		- field : bar_cd_typ [바코드유형] 
	 *		- field : all_len_num [전체자리수] 
	 *		- field : mdl_sta_len [모델시작자리] 
	 *		- field : mdl_end_len [모델끝자리] 
	 *		- field : mdl_len_num [모델자리수] 
	 *		- field : color_sta_len [색상시작자리] 
	 *		- field : color_end_len [색상끝자리] 
	 *		- field : color_len_num [색상자리수] 
	 *		- field : ser_num_sta_len [일련번호시작자리] 
	 *		- field : ser_num_end_len [일련번호끝자리] 
	 *		- field : ser_num_len_num [일련번호자리수] 
	 *		- field : upd_cnt [UPDATE COUNT] 
	 *		- field : ins_dtm [입력일시] 
	 *		- field : ins_user_id [입력자ID] 
	 *		- field : mod_dtm [수정일시] 
	 *		- field : mod_user_id [수정자ID] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveProductBarcodeList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPDM00400.saveProductBarcodeList method start");
		}

		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;

		IRecordSet rs = requestData.getRecordSet("prod_bar_cd_mgmt");
		
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (INSERT_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {

					insert("BASPDM00400.addProductBarcodeList", rs.getRecord(i),onlineCtx);
					insertCount++;
				} else if (UPDATE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {

					updateCount = updateCount
							+ update("BASPDM00400.updateProductBarcodeList", rs
									.getRecord(i),onlineCtx);
				}
			}
			cudAllCount = insertCount + updateCount;
		}
		if (cudAllCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount });

	}

}