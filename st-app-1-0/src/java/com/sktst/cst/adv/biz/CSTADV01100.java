/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.cst.adv.biz;

import java.util.Map;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;

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

import org.apache.commons.logging.Log;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-12-26 18:27:18</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class CSTADV01100 extends BaseBizUnit {

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
	public IDataSet savePrchsCustRmks(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);

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
		IRecordSet rmks = rsData.getRecordSet("ds_prchsCustRmks");

		//		IRecordSet mgmt = requestData.getRecordSet("ds_consult_m");
		//		IRecordSet rmks = requestData.getRecordSet("ds_prchsCustRmks");

		Map mRmks = null;
		Map mMgmt = null;
		String sSeq = "";
		String sRmk = "";

		if (rmks != null) {
			mRmks = rmks.getRecordMap(0);
			mMgmt = mgmt.getRecordMap(0);

			sSeq = (String) mRmks.get("seq");
			sRmk = mRmks.get("rmks_cl") == null ? "" : (String) mRmks
					.get("rmks_cl");
			if (sRmk.equals("1")) {
				insert("CSTADV01100.addPrchsCustRmksOnly", mRmks, onlineCtx);
			} else {
				if (sSeq == null || sSeq.equals("")) {
					insert("CSTADV01100.addPrchsCustRmks", mRmks, onlineCtx);
					update("CSTADV01100.updatePrchsCustPrc", mRmks, onlineCtx);
					update("CSTADV00200.updatePrchsCust", mMgmt, onlineCtx);
				} else {
					update("CSTADV01100.updatePrchsCustRmks", mRmks, onlineCtx);
					update("CSTADV00200.updatePrchsCust", mMgmt, onlineCtx);
					if (mRmks.get("cust_yn").toString().equals("28")
							|| mRmks.get("cust_yn_dtl").toString().equals("24")
							|| mRmks.get("cust_yn_dtl").toString().equals("27")) {
						update("CSTADV01100.updatePrchsReturnCl", mRmks,
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
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getPrchsCustRmksList(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		IRecordSet conDtl;
		IRecordSet conM;
		conM = queryForRecordSet("CSTADV00200.getPrchsMgmt", requestData
				.getFieldMap());
		conDtl = queryForRecordSet("CSTADV00200.getPrchsDtl", requestData
				.getFieldMap());

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(conM.getRecordCount()) });

		responseData.putRecordSet("ds_consult_m", conM);
		responseData.putRecordSet("ds_consult_d", conDtl);

		IRecordSet color = queryForRecordSet("CSTADV00200.getColorList",
				requestData.getFieldMap());

		responseData.putRecordSet("ds_color", color);

		IRecordSet prod = queryForRecordSet("CSTADV00200.getProdMgmt",
				requestData.getFieldMap());

		responseData.putRecordSet("ds_prod_m", prod);
		//		log.debug("conM : " + conM);
		IRecordSet rate = queryForRecordSet("CSTADV00200.getRateMgmt",
				requestData.getFieldMap());
		//		IRecordSet rate = queryForRecordSet("CSTADV00200.getRateMgmt", conM);

		responseData.putRecordSet("ds_rate", rate);

		IRecordSet rmks = queryForRecordSet("CSTADV01100.getPrchsCustRmksList",
				requestData.getFieldMap());

		responseData.putRecordSet("ds_rmk_list", rmks);

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

		//		return responseData;

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
	public IDataSet getPrchsCustRmksNo(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		IRecordSet conDtl;
		IRecordSet conM;
		IRecordSet prchsM;
		Map mPrchs = null;
		
		prchsM = queryForRecordSet("CSTADV00200.getPrchsMgmtCheck", requestData
				.getFieldMap());
		
		if (prchsM.getRecordCount() == 0) {
			throw new BizRuntimeException("상담대상이 아닙니다. 진행상태를 확인하세요.");
		}
		conM = queryForRecordSet("CSTADV00200.getPrchsMgmt", requestData
				.getFieldMap());
		mPrchs = conM.getRecordMap(0);
		conDtl = queryForRecordSet("CSTADV00200.getPrchsDtl", mPrchs);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(conM.getRecordCount()) });

		responseData.putRecordSet("ds_consult_m", conM);
		responseData.putRecordSet("ds_consult_d", conDtl);

		IRecordSet color = queryForRecordSet("CSTADV00200.getColorList", mPrchs);

		responseData.putRecordSet("ds_color", color);

		IRecordSet prod = queryForRecordSet("CSTADV00200.getProdMgmt", mPrchs);

		responseData.putRecordSet("ds_prod_m", prod);
		//		log.debug("conM : " + conM);
		IRecordSet rate = queryForRecordSet("CSTADV00200.getRateMgmt", mPrchs);
		//		IRecordSet rate = queryForRecordSet("CSTADV00200.getRateMgmt", conM);

		responseData.putRecordSet("ds_rate", rate);

		IRecordSet rmks = queryForRecordSet("CSTADV01100.getPrchsCustRmksList", mPrchs);

		responseData.putRecordSet("ds_rmk_list", rmks);
		log.debug("rmks:"+rmks);
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

//				return responseData;
	}

}