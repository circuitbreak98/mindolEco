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
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : 대리점 팝업 검색</li>
 * <li>작성일 : 2009-03-02 15:54:12</li>
 * </ul>
 *
 * @author 최승호 (choiseungho)
 */
public class BASBCO01300 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 대리점 팝업 검색
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getAgencyPop(IDataSet requestData, IOnlineContext onlineCtx) {
		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASBCO01300.getAgencyPop method start");
		}
		
		// 2. CRUD 처리
		String sEffDtm = requestData.getField("eff_dtm");//
		
		String sQueryXml = "BASBCO01300.getAgencyPop";
		if(sEffDtm == null || "".equals(sEffDtm))
		{
			sQueryXml = "BASBCO01300.getAgencyPopForMax";
		}
		
		IRecordSet rs = queryForRecordSet(sQueryXml,
				requestData.getFieldMap(), onlineCtx);
		if (rs == null) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("output", rs);
		return responseData;
	}

}