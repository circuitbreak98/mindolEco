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

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-06-25 10:57:13</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class CSTSKN00500 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet savePrchs(IDataSet requestData, IOnlineContext onlineCtx)
			throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("CSTSKN00500.savePrchs method start");
		}

		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");
		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "ds_consult_m"); // 암호화 할 데이터셋 명
		rec.add("col_name1", "RES_NO"); // 암호화 컬럼1
		rec.add("col_name2", "TEL_NO"); // 암호화 컬럼2
		rec.add("col_name3", "ACC_NO"); // 암호화 컬럼3

		iSet.addRecord(rec);
		requestData.putRecordSet("cptItem", iSet); // 암호화 정보 추가
		IDataSet rsData = bco.encode(requestData, onlineCtx);

		IRecordSet mgmt = rsData.getRecordSet("ds_consult_m");
		IRecordSet dtl = rsData.getRecordSet("ds_consult_d");

		Map mMgmt = null;
		Map mDtl = null;
		Map mPrchsNo = null;
		Map mUpdCnt = null;
		int bUpdCnt = 0;
		int aUpdCnt = 0;

		if (mgmt != null) {

			mMgmt = mgmt.getRecordMap(0);
			bUpdCnt = (int) Double.parseDouble(mMgmt.get("UPD_CNT").toString());
			mUpdCnt = (Map) queryForObject("CSTSKN00500.getConUpdCnt", mMgmt,
					onlineCtx);
			aUpdCnt = (int) Double.parseDouble(mUpdCnt.get("UPD_CNT")
					.toString());

			if (bUpdCnt != aUpdCnt) {
				throw new BizRuntimeException("조회 후 다시 처리하십시요.");
			}

			mPrchsNo = (Map) queryForObject("CSTSKN00500.getPrchsMgmtNo",
					mMgmt, onlineCtx);
			mMgmt.put("PRCHS_MGMT_NO", mPrchsNo.get("PRCHS_MGMT_NO"));

			insert("CSTSKN00500.addPrchsMgmt", mMgmt, onlineCtx);

			for (int i = 0; i < dtl.getRecordCount(); i++) {
				mDtl = dtl.getRecordMap(i);
				mDtl.put("PRCHS_MGMT_NO", mPrchsNo.get("PRCHS_MGMT_NO"));
				mDtl.put("PROD_CD", mMgmt.get("PROD_CD"));
				mDtl.put("SER_NUM", mMgmt.get("SER_NUM"));
				mDtl.put("PRCHS_SEQ", i + 1);
				insert("CSTSKN00500.addPrchsDtl", mDtl, onlineCtx);
			}

			update("CSTSKN00500.updateConsultMgmt", mMgmt, onlineCtx);

		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {});
		if (mPrchsNo != null) {
			responseData.putFieldMap(mPrchsNo);
		}

		return responseData;
	}

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
	public IDataSet getConsultMgmt(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("CSTSKN00500.getConsultMgmt method start");
		}

		IRecordSet conDtl;
		IRecordSet conM;

		if (requestData.getFieldMap().get("PRC_GB").toString().equals("P")) {
			conM = queryForRecordSet("CSTSKN00500.getPrchsMgmt", requestData
					.getFieldMap());
			conDtl = queryForRecordSet("CSTSKN00500.getPrchsDtl", requestData
					.getFieldMap());
		} else {
			conM = queryForRecordSet("CSTSKN00500.getConsultMgmt", requestData
					.getFieldMap());
			conDtl = queryForRecordSet("CSTSKN00500.getConsultDtl", requestData
					.getFieldMap());
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(conM.getRecordCount()) });

		responseData.putRecordSet("ds_consult_m", conM);
		responseData.putRecordSet("ds_consult_d", conDtl);

		IRecordSet color = queryForRecordSet("CSTSKN00500.getColorList",
				requestData.getFieldMap());

		responseData.putRecordSet("ds_color", color);

		IRecordSet prod = queryForRecordSet("CSTSKN00500.getProdMgmt",
				requestData.getFieldMap());

		responseData.putRecordSet("ds_prod_m", prod);

		IRecordSet rate = queryForRecordSet("CSTSKN00500.getRateMgmt",
				requestData.getFieldMap());

		responseData.putRecordSet("ds_rate", rate);

		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");

		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "ds_consult_m"); // 복호화 할 데이터셋 명
		rec.add("col_name1", "ACC_NO"); // 복호화 컬럼1
		rec.add("col_name2", "TEL_NO"); // 복호화 컬럼2
		rec.add("col_name3", "RES_NO"); // 복호화 컬럼3
		iSet.addRecord(rec);

		responseData.putRecordSet("cptItem", iSet);

		IDataSet rsData = bco.decode(responseData, onlineCtx);

		return rsData;

	}

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
	public IDataSet getConsultMgmtNo(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		IRecordSet prchsM;
		IRecordSet conDtl;
		IRecordSet conM;
		Map mCon = null;
		Map mPrchs = null;
		
		prchsM = queryForRecordSet("CSTSKN00500.getPrchsMgmtCheck", requestData
				.getFieldMap());

		if (prchsM.getRecordCount() > 0) {
			conM = queryForRecordSet("CSTSKN00500.getPrchsMgmt", requestData
					.getFieldMap());
			mCon = conM.getRecordMap(0);
			conDtl = queryForRecordSet("CSTSKN00500.getPrchsDtl", mCon);
		} else {
			conM = queryForRecordSet("CSTSKN00500.getConsultMgmt", requestData
					.getFieldMap());
			mCon = conM.getRecordMap(0);
			conDtl = queryForRecordSet("CSTSKN00500.getConsultDtl", mCon);
		}
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(conM.getRecordCount()) });
		
		responseData.putRecordSet("ds_consult_m", conM);
		responseData.putRecordSet("ds_consult_d", conDtl);
		
		IRecordSet color = queryForRecordSet("CSTSKN00500.getColorList", mCon);

		responseData.putRecordSet("ds_color", color);

		IRecordSet prod = queryForRecordSet("CSTSKN00500.getProdMgmt", mCon);

		responseData.putRecordSet("ds_prod_m", prod);

		IRecordSet rate = queryForRecordSet("CSTSKN00500.getRateMgmt", mCon);

		responseData.putRecordSet("ds_rate", rate);

		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");

		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "ds_consult_m"); // 복호화 할 데이터셋 명
		rec.add("col_name1", "ACC_NO"); // 복호화 컬럼1
		rec.add("col_name2", "TEL_NO"); // 복호화 컬럼2
		rec.add("col_name3", "RES_NO"); // 복호화 컬럼3
		iSet.addRecord(rec);

		responseData.putRecordSet("cptItem", iSet);

		IDataSet rsData = bco.decode(responseData, onlineCtx);

		return rsData;
	}

}