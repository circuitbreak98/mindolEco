/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.cdm.biz;

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
 * <li>설 명 : 공통코드 검색 팝업</li>
 * <li>작성일 : 2009-01-22 14:52:21</li>
 * </ul>
 *
 * @author 조민지 (jominji)
 */
public class BASCDM00100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 조민지 (jominji)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : comm_cd_id [검색조건_코드ID] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 * 
	 *	- record : comm_cdList
	 *		- field : chk [체크박스용] 
	 *		- field : comm_cd_id [공통코드ID] 
	 *		- field : comm_cd_nm [공통코드] 
	 */
	public IDataSet getCommonCodeList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASCDM00100.getCommonCodeList method start >>>");
		}
		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("BASCDM00100.getCommonCodeList",
				requestData.getFieldMap());

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_comm_cd_list", rs);
		return responseData;

	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 *         - 넘겨받은 공통 코드 ID에 해당하는 공통 코드셋을 반환
	 *         
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getCommonCodeListByID(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getCommonCodeListByID method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("BASCDM00100.getCommonCodeListByID",
				requestData.getFieldMap());
		if (rs == null) {
			rs = new RecordSet("commonCode");
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("commonCode", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getStlPlcListByID(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getStlPlcListByID method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("BASCDM00100.getStlPlcListByID",
				requestData.getFieldMap());
		if (rs == null) {
			rs = new RecordSet("ds_stlPlc");
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_stlPlc", rs);
		return responseData;
	}

}