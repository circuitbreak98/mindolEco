/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.pdm.biz;

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
 * <li>설 명 : 부가서비스관리</li>
 * <li>작성일 : 2009-01-22 10:05:56</li>
 * </ul>
 *
 * @author 전현주 (junhyunjoo)
 */
public class BASPDM00300 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * @title  부가서비스조회
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : SUPL_SVC_NM [필드1] 
	 *	- field : USE_YN [필드2] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : SUPL_SVC_CD [필드1] 
	 *	- field : SUPL_SVC_NM [필드2] 
	 *	- field : RGST_DT [필드3] 
	 *	- field : USE_YN [필드4] 
	 */
	public IDataSet getSuplSvc(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getSuplSvc method start");
		}
		
		IRecordSet rs = queryForRecordSet("BASPDM00300.getSuplSvc", requestData
				.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("ds_Dev");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
				
		responseData.putRecordSet("ds_Dev", rs);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * @title  부가서비스등록
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : SUPL_SVC_CD [필드1] 
	 *	- field : SUPL_SVC_NM [필드2] 
	 *	- field : RGST_DT [필드3] 
	 *	- field : USE_YN [필드4] 
	 *	- field : RGST_CL [필드5] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addSuplSvc(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("addSuplSvc method start");
		}
		insert("BASPDM00300.addSuplSvc", requestData.getFieldMap(),onlineCtx);
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "1" });
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * @title  부가서비스 멀티처리
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveSuplSvc(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveSuplSvc method start");
		}

		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;

		IRecordSet rs = requestData.getRecordSet("SuplSvc");

		if (rs != null) {
		 
			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (DELETE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					deleteCount = deleteCount
							+ delete("BASPDM00300.deleteSuplSvc", rs
									.getRecord(i));
				}
			}
			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (INSERT_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					
					insert("BASPDM00300.addSuplSvc", rs.getRecord(i),onlineCtx);
					insertCount++;
				} else if (UPDATE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					updateCount = updateCount
							+ update("BASPDM00300.updateSuplSvc", rs
									.getRecord(i),onlineCtx);
				}
			}
			cudAllCount = insertCount + updateCount + deleteCount;
		}
		if (cudAllCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount, "" + deleteCount });
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * @title  부가서비스 삭제
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteSuplSvc(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("deleteSuplSvc method start");
		}

		int deleteCount = 0;

		// 부가 서비스 삭제 처리
		IRecordSet rs = requestData.getRecordSet("ds_SuplSvc");
 
		deleteCount = deleteCount
					+ delete("BASPDM00300.deleteSuplSvc", rs.getRecord(0), onlineCtx);
				
		if (deleteCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.DELETE_OK_MESSAGE_ID, new String[] { ""
						+ deleteCount });
	}

}