/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.prm.biz;

import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-06-24 09:42:16</li>
 * </ul>
 *
 * @author 정재열 (jungjaeyul)
 */
public class BASPRM00500 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getDealList(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPRM00500.getDealList method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("BASPRM00500.getDealList",
				requestData.getFieldMap(), onlineCtx);
		
		if (rs == null) {
			rs = new RecordSet("List");
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("List", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateDealList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("updateDealList method start");
		}

		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;

		Map map = requestData.getFieldMap();


		int nAply = 0;

		IRecordSet rs = requestData.getRecordSet("List");
		Map oMapRowData = null;

		if (rs != null) {

			for (int i = 0; i < rs.getRecordCount(); i++) {
				oMapRowData = rs.getRecordMap(i);
				nAply = update("BASPRM00500.updateDealList", oMapRowData, onlineCtx);
				updateCount++;
			}
		}
		cudAllCount = insertCount + updateCount + deleteCount;

		if (cudAllCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[]{"" + insertCount,
						"" + updateCount, "" + deleteCount});		
	}

}