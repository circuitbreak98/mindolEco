/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.sal.smg.biz;

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
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-10-31 09:48:34</li>
 * </ul>
 *
 * @author 안희수 (ahnheesoo)
 */
public class SALSMG00700 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 안희수 (ahnheesoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdSaleList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALSMG00700.getProdSaleList method start");
		}

		log.debug("requestData.getFieldMap() 3:::::::::::::::: "
				+ requestData.getFieldMap());

		IRecordSet rs = queryForRecordSet("SALSMG00700.getProdSaleList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ProdSaleList");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ProdSaleList", rs);

		return responseData;
	}

}