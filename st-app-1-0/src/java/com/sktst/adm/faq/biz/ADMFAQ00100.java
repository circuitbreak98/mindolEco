/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.faq.biz;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-02-25 10:40:15</li>
 * </ul>
 *
 * @author 심연정 (shimyunjung)
 */
public class ADMFAQ00100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * ==> faq 취득 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getFaqList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMFAQ00100.getFaqList start >>>>>>>");
		}
		System.err.println(requestData.getFieldMap());
		IRecordSet rs = queryForRecordSet("ADMFAQ00100.getFaqList", requestData
				.getFieldMap(), onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_result", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * --> faq 수
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveFaqList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMFAQ00100.saveFaqList start >>>>>>>");
		}

		IRecordSet rs = requestData.getRecordSet("ds_result");
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {

				if (INSERT_FLAG.equalsIgnoreCase((String) rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					System.err.println("INSERT_FLAG");
					insert("ADMFAQ00100.addFaqList", rs.getRecord(i), onlineCtx);

				} else if (UPDATE_FLAG.equalsIgnoreCase((String) rs
						.getRecord(i).get(CUD_FLAG_PARAM))) {

					update("ADMFAQ00100.updateFaqList", rs.getRecord(i),
							onlineCtx);

				} else if (DELETE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					System.err.println("DELETE_FLAG");
					update("ADMFAQ00100.deleteFaqList", rs.getRecord(i),
							onlineCtx);
				}
			}
		}

		IRecordSet result = queryForRecordSet("ADMFAQ00100.getFaqList",
				requestData.getFieldMap(), onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_result", result);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * 사용자 조회용 페이지. 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getFaqByUser(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMFAQ00100.getFaqByUser start >>>>>>>");
		}

		IRecordSet rs = queryForRecordSet("ADMFAQ00100.getFaqByUser",
				requestData.getFieldMap(), onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_result", rs);
		return responseData;
	}

}