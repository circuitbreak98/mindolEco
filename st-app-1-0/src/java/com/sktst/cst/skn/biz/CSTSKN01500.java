/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.cst.skn.biz;

import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : </li>
 * <li>설 명 : </li>
 * <li>작성일 : 2015-06-12 14:29:17</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class CSTSKN01500 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getSaleEqpC(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
System.out.println("***********************getSaleEqpC************************");
		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("CSTSKN01500.getSaleEqpC", requestData
				.getFieldMap(), onlineCtx);

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_saleEqpSt", rs);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveSaleEqpC(IDataSet requestData, IOnlineContext onlineCtx) {

		Map mSaleEqp = requestData.getFieldMap();
		IRecordSet mgmt = requestData.getRecordSet("ds_saleEqpSt");
		
		mSaleEqp = mgmt.getRecordMap(0);
		insert("CSTSKN01500.addSaleEqpC", mSaleEqp, onlineCtx);
		update("CSTSKN01500.updateConsultSaleEqpC", mSaleEqp, onlineCtx);
		update("CSTSKN01500.updatePrchsSaleEqpC", mSaleEqp, onlineCtx);
		update("CSTSKN01500.updateSaleEqpC", mSaleEqp, onlineCtx);
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		return responseData;
	}

}