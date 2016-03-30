/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.prm.biz;

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
 * <li>작성일 : 2010-04-21 17:04:18</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class BASPRM00800 extends BaseBizUnit {

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
	public IDataSet getDealIncreList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPRM00800  -  getDealIncreList method start");
		}

		// 전월말 기준 조회
		log.debug("                    getDealIncreList  -  getDealIncrePrev method start");
		IRecordSet rsDealPrev = queryForRecordSet("BASPRM00800.getDealIncrePrev", requestData.getFieldMap(), onlineCtx);
		if (rsDealPrev == null) {
			rsDealPrev = new RecordSet("ds_DealIncrePrev");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsDealPrev.getRecordCount()) });

		responseData.putRecordSet("ds_DealIncrePrev", rsDealPrev);

		// 1주차 자료 조회
		IRecordSet rsDeal1st = queryForRecordSet("BASPRM00800.getDealIncre1st", requestData.getFieldMap(), onlineCtx);
		if (rsDeal1st == null) {
			rsDeal1st = new RecordSet("ds_DealIncre1st");
		}

		responseData.putRecordSet("ds_DealIncre1st", rsDeal1st);

		// 2주차 자료 조회
		IRecordSet rsDeal2nd = queryForRecordSet("BASPRM00800.getDealIncre2nd", requestData.getFieldMap(), onlineCtx);
		if (rsDeal2nd == null) {
			rsDeal2nd = new RecordSet("ds_DealIncre2nd");
		}

		responseData.putRecordSet("ds_DealIncre2nd", rsDeal2nd);

		// 3주차 자료 조회
		IRecordSet rsDeal3rd = queryForRecordSet("BASPRM00800.getDealIncre3rd", requestData.getFieldMap(), onlineCtx);
		if (rsDeal3rd == null) {
			rsDeal3rd = new RecordSet("ds_DealIncre3rd");
		}

		responseData.putRecordSet("ds_DealIncre3rd", rsDeal3rd);


		// 4주차 자료 조회
		IRecordSet rsDeal4th = queryForRecordSet("BASPRM00800.getDealIncre4th", requestData.getFieldMap(), onlineCtx);
		if (rsDeal4th == null) {
			rsDeal4th = new RecordSet("ds_DealIncre4th");
		}

		responseData.putRecordSet("ds_DealIncre4th", rsDeal4th);

		// 5주차 자료 조회
		IRecordSet rsDeal5th = queryForRecordSet("BASPRM00800.getDealIncre5th", requestData.getFieldMap(), onlineCtx);
		if (rsDeal5th == null) {
			rsDeal5th = new RecordSet("ds_DealIncre5th");
		}

		responseData.putRecordSet("ds_DealIncre5th", rsDeal5th);

		// 6주차 자료 조회
		IRecordSet rsDeal6th = queryForRecordSet("BASPRM00800.getDealIncre6th", requestData.getFieldMap(), onlineCtx);
		if (rsDeal6th == null) {
			rsDeal6th = new RecordSet("ds_DealIncre6th");
		}

		responseData.putRecordSet("ds_DealIncre6th", rsDeal6th);

		log.debug("                   getDealIncreList  END....");

		return responseData;
	}

}