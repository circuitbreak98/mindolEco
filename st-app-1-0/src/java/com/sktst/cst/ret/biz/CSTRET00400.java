/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.cst.ret.biz;

import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-03-15 14:55:07</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class CSTRET00400 extends BaseBizUnit {

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
	public IDataSet savePrchsMgmtRtnForDeal(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("CSTRET00400.savePrchsMgmtRtn method start");
		}

		IRecordSet rs = requestData.getRecordSet("ds_retLst");
		Map mSave = null;
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				mSave = rs.getRecordMap(i);
				if (mSave.get("CHK").equals("1")) {
					if (mSave.get("DATA_GB").equals("C")) {
						update("CSTRET00400.updateConsultRtn", mSave, onlineCtx);
					} else if (mSave.get("DATA_GB").equals("P")) {
						update("CSTRET00400.updatePrchsMgmtRtn", mSave,
								onlineCtx);
					}

				}
			}
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });
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
	public IDataSet getBoxLst(IDataSet requestData, IOnlineContext onlineCtx)
			throws Exception {
		
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("CSTRET00400.getBoxLst method start >>>" + requestData.getFieldMap());
		}

		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("CSTRET00400.getBoxLst", requestData.getFieldMap(), onlineCtx);

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_boxLst", rs);
		
		return responseData;
	}

}