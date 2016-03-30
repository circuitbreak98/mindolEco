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
 * <li>작성일 : 2011-09-15 16:27:01</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class BASPDM00610 extends BaseBizUnit {

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
	public IDataSet getProdFixPrice(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPDM00610.getProdFixPrice method start");
		}

		Map query = requestData.getFieldMap();

		IRecordSet rs = queryForRecordSet("BASPDM00610.getProdFixPrice", query,
				onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_prod_fix_price");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_prod_fix_price", rs);
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
	public IDataSet saveProdFixPrice(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPDM00610.saveProdFixPrice method start");
		}

		Map condition = requestData.getFieldMap();
		String prod_cd = (String) condition.get("PROD_CD");

		IRecordSet rs = requestData.getRecordSet("ds_prod_fix_price");

		Map query = null;
		int iUpdate = 0;
		int iInsert = 0;

		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				query = rs.getRecordMap(i);
				query.put("PROD_CD", prod_cd);
				System.out.println("query====" + query);

				log.debug(query.get("SEQ"));

				log.debug("BASPDM00610.saveProdFixPrice_insert method Call");
				insert("BASPDM00610.saveProdFixPrice_insert", query, onlineCtx);
				iInsert++;

				log.debug("BASPDM00610.saveProdFixPrice_update method Call");
				update("BASPDM00610.saveProdFixPrice_update", query, onlineCtx);
				iUpdate++;
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] {
						"Update : " + iUpdate, "Insert : " + iInsert });

		responseData.putRecordSet("ds_prod_fix_price", rs);
		return responseData;
	}

}