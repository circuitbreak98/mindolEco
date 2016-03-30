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

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-03-14 14:23:47</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class CSTRET00300 extends BaseBizUnit {

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
	public IDataSet getPrchsMgmtRetLst(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("CSTRET00300.getPrchsMgmtRetLst method start >>>");
		}

		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("CSTRET00300.getPrchsMgmtRetLst",
				requestData.getFieldMap(), onlineCtx);

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_retLst", rs);

		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");

		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "ds_retLst"); // 복호화 할 데이터셋 명
		rec.add("col_name2", "TEL_NO"); // 복호화 컬럼2
		iSet.addRecord(rec);

		responseData.putRecordSet("cptItem", iSet);

		IDataSet rsData = bco.decode(responseData, onlineCtx);

		return rsData;
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
	public IDataSet updatePrchsMgmtRtn(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("CSTRET00300.updatePrchsMgmtRtn method start");
		}

		IRecordSet rs = requestData.getRecordSet("ds_retLst");

		Map mSave = null;
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				mSave = rs.getRecordMap(i);
				if(mSave.get("CHK").equals("1")){
					update("CSTRET00300.updatePrchsMgmtRtn", mSave, onlineCtx);
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
	public IDataSet updatePrchsMgmtRtnCancel(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("CSTRET00300.updatePrchsMgmtRtnCancel method start");
		}

		IRecordSet rs = requestData.getRecordSet("ds_retLst");

		Map mSave = null;
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				mSave = rs.getRecordMap(i);
				if(mSave.get("CHK").equals("1")){
					update("CSTRET00300.updatePrchsMgmtRtnCancel", mSave, onlineCtx);
				}
			}
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });
	}

}