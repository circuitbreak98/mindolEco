/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.dsm.biz;

import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-11-04 10:02:53</li>
 * </ul>
 *
 * @author 김만수 (kimmansoo)
 */
public class DISDSM00400 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getDisDsm400Info(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISDSM00400.getDisDsm400Info method start");
		}

		IRecordSet rs = queryForRecordSet("DISDSM00400.getDisDsm400Info",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("List", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getDisDsm400(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISDSM00400.getDisDsm400 method start");
		}

		IRecordSet rs = queryForRecordSet("DISDSM00400.getDisDsm400",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("List", rs);
		return responseData;
	}

}