/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.cst.skn.biz;

import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2013-01-17 11:03:41</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class CSTSKN00310 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveInPrchsAll(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		log.debug("CSTSKN03100.savePrchs method start");
		IRecordSet rs = requestData.getRecordSet("ds_upLoadXlsList");

		IRecordSet iDtl = null;

		Map mIn = null;
		Map mMgmt = null;
		Map mDtl = null;
		Map mPrchsNo = null;

		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {

				mMgmt = (Map) queryForObject("CSTSKN00310.getConsultMgmtSkn", rs.getRecord(i));
				iDtl = queryForRecordSet("CSTSKN00310.getConsultDtlSkn", rs.getRecord(i));
				mPrchsNo = (Map) queryForObject("CSTSKN00500.getPrchsMgmtNo",
						mMgmt, onlineCtx);

				mMgmt.put("PRCHS_MGMT_NO", mPrchsNo.get("PRCHS_MGMT_NO"));
				insert("CSTSKN00310.addInPrhcsAll", mMgmt, onlineCtx);

				for (int j = 0; j < iDtl.getRecordCount(); j++) {
					mDtl = iDtl.getRecordMap(j);
					mDtl.put("PRCHS_MGMT_NO", mPrchsNo.get("PRCHS_MGMT_NO"));
					mDtl.put("PRCHS_SEQ", j + 1);
					insert("CSTSKN00500.addPrchsDtl", mDtl, onlineCtx);
				}

				update("CSTSKN00500.updateConsultMgmt", mMgmt, onlineCtx);

			}
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });

	}

}