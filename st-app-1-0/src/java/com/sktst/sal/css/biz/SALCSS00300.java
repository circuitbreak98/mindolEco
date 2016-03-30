/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.sal.css.biz;

import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;
import com.sktst.sal.sco.ejb.SCO;

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
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2013-06-10 16:16:35</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class SALCSS00300 extends com.sktst.common.base.BaseBizUnit {

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
	public IDataSet getConSaleProdList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALCSS00300.getConSaleProdList method start");
		}

		IRecordSet rs = queryForRecordSet("SALCSS00300.getConSaleProdList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_prodSaleLst");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_prodSaleLst", rs);

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
	public IDataSet saveConSaleCncl(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		SCO sco = (SCO) lookupBizComponent("sktst.sal.SCO");

		IDataSet oDsDisSaleCncl = new DataSet();
		oDsDisSaleCncl.putRecordSet("saleCnclList", requestData
				.getRecordSet("ds_prodSaleLst"));
		oDsDisSaleCncl.putRecordSet("gnrlSaleCnclList", requestData
				.getRecordSet("ds_gnrlSale"));

		oDsDisSaleCncl = sco.saveSaleCncl(oDsDisSaleCncl, onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf("1") });

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
	public IDataSet getConSaleProdListCount(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALCSS00300.getConSaleProdListCount method start");
		}

		IRecordSet rs = queryForRecordSet("SALCSS00300.getConSaleProdListCount",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_prodSaleLstCount");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_prodSaleLstCount", rs);

		return responseData;
	}

}