/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.bco.biz;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2010-06-03 12:51:24</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class BASBCO01700 extends BaseBizUnit {

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
	public IDataSet getConditionList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getConditionList method start");
		}

		// Excel 추출대상 Screen ID
		IRecordSet rsScreen = queryForRecordSet("BASBCO01700.getScreenList", requestData.getFieldMap());
		if (rsScreen == null) {
			rsScreen = new RecordSet("ds_Screen");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String.valueOf(rsScreen.getRecordCount()) });
		responseData.putRecordSet("ds_Screen", rsScreen);

		// Excel 추출대상 사용자 ID
		IRecordSet rsUser = queryForRecordSet("BASBCO01700.getUserList", requestData.getFieldMap());
		if (rsUser == null) {
			rsUser = new RecordSet("ds_User");
		}

		responseData.putRecordSet("ds_User", rsUser);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 권현호 (kwonhyunho)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getExcelExportList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getExcelExportList method start");
		}

		// Excel 추출대상 Screen ID
		IRecordSet rsList = queryForRecordSet("BASBCO01700.getExcelExportList", 
				requestData.getFieldMap());
		if (rsList == null) {
			rsList = new RecordSet("excel_list");
		}
		log.debug("excel list loaded");
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsList.getRecordCount()) });

		responseData.putRecordSet("excel_list", rsList);
		return responseData;
	}

}