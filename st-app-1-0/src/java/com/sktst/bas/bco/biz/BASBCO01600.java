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
 * <li>설 명 : 세션 연장을 위해 실행한다.</li>
 * <li>작성일 : 2009-08-20 16:00:05</li>
 * </ul>
 *
 * @author 한병곤 (hanbyunggon)
 */
public class BASBCO01600 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getSessionExtension(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASBCO01600.getSessionExtension method start");
		}

		IRecordSet rs = queryForRecordSet("BASBCO01600.getDummy", requestData
				.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_dummy");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_SIMPLE_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_dummy", rs);

		return responseData;
	}

}