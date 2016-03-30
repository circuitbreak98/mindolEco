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
import java.util.HashMap;
import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import com.sktst.common.base.BaseBizUnit;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2013-01-25 13:57:22</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class BASPDM00910 extends BaseBizUnit {

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
	public IDataSet getPriceList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getPriceList method start");
		}

		IRecordSet rs = queryForRecordSet("BASPDM00910.getPriceList",
				requestData.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("ds_price");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_price", rs);
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
	public IDataSet deleteProdFixPrice(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("deleteProdFixPrice method start");
		}
		
		IRecordSet rs_m = requestData.getRecordSet("ds_condition");
		IRecordSet rs = requestData.getRecordSet("ds_price");
		
		Map sMap = rs_m.getRecordMap(0);

		update("BASPDM00910.updateProdFixPriceMasterDel", sMap, onlineCtx);
		update("BASPDM00910.updateProdFixPriceDel", sMap, onlineCtx);
		
		Map mPrice = new HashMap();
		
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				
				mPrice = rs.getRecordMap(i);
				mPrice.put("ST_DT", sMap.get("ST_DT"));
				update("BASPDM00910.updateProdFixPriceYet", mPrice, onlineCtx);

			}
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { "1" });
	}

}