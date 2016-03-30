/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.qna.biz;

import java.util.Map;

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
 * <li>작성일 : 2009-02-25 17:49:39</li>
 * </ul>
 *
 * @author 심연정 (shimyunjung)
 */
public class ADMQNA00100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getQna(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMQNA00100.getQna start >>>>>>>");
		}

		IRecordSet rs = queryForRecordSet("ADMQNA00100.getQna", requestData
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
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveQna(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMQNA00100.saveQna start >>>>>>>");
		}

		IRecordSet rs = requestData.getRecordSet("ds_result");
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {

				if (UPDATE_FLAG.equalsIgnoreCase((String) rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {

					update("ADMQNA00100.updateAnswer", rs.getRecord(i),
							onlineCtx);

				}
				//				else if (DELETE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
				//						CUD_FLAG_PARAM))) {
				//					System.err.println("DELETE_FLAG");
				//					update("ADMQNA00300.deleteFaqList", rs.getRecord(i),
				//							onlineCtx);
				//				}
			}
		}

		IRecordSet result = queryForRecordSet("ADMQNA00100.getQna", requestData
				.getFieldMap(), onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_result", result);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 *  - 여러건 삭제한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteQnaBySelect(IDataSet requestData,
			IOnlineContext onlineCtx) {

		IRecordSet rs = requestData.getRecordSet("ds_result");
		Map mRs = null;
		String sCheckValue = "1";
		
		int iUpdateCnt = 0;
		
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {

				mRs = rs.getRecordMap(i);
					
				if (mRs.get("chk").equals(sCheckValue) &&  UPDATE_FLAG.equalsIgnoreCase((String) rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {

					iUpdateCnt = update("ADMQNA00100.deleteQnaBySelect", rs.getRecord(i),
							onlineCtx);
				}
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.DELETE_OK_MESSAGE_ID, new String[] { iUpdateCnt+""});
		
		return responseData;
	}

}