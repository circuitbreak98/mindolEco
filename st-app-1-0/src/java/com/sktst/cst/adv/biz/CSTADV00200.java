/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.cst.adv.biz;

import java.rmi.RemoteException;
import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.exception.BizException;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.dis.inn.ejb.INN;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-13 11:03:52</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class CSTADV00200 extends BaseBizUnit {

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
	public IDataSet getColorList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		//질의를 수행한다.
		IRecordSet color = queryForRecordSet("CSTADV00200.getColorList",
				requestData.getFieldMap());

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(color.getRecordCount()) });

		responseData.putRecordSet("ds_color", color);

		IRecordSet prod = queryForRecordSet("CSTADV00200.getProdMgmt",
				requestData.getFieldMap());

		responseData.putRecordSet("ds_prod_m", prod);

		IRecordSet conDtl = queryForRecordSet("CSTADV00200.getConsultDtl",
				requestData.getFieldMap());

		responseData.putRecordSet("ds_consult_d", conDtl);

		IRecordSet rate = queryForRecordSet("CSTADV00200.getRateMgmt",
				requestData.getFieldMap());

		responseData.putRecordSet("ds_rate", rate);

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
	public IDataSet getConsultM(IDataSet requestData, IOnlineContext onlineCtx)
			throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		IRecordSet conDtl;
		IRecordSet conM;

		if (requestData.getFieldMap().get("PRC_GB").toString().equals("P")
				|| requestData.getFieldMap().get("PRC_GB").toString().equals(
						"C")
				|| requestData.getFieldMap().get("PRC_GB").toString().equals(
						"S")) {
			conM = queryForRecordSet("CSTADV00200.getPrchsMgmt", requestData
					.getFieldMap());
			conDtl = queryForRecordSet("CSTADV00200.getPrchsDtl", requestData
					.getFieldMap());
		} else {
			conM = queryForRecordSet("CSTADV00200.getConsultM", requestData
					.getFieldMap());
			conDtl = queryForRecordSet("CSTADV00200.getConsultDtl", requestData
					.getFieldMap());
		}

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

		IRecordSet rate = queryForRecordSet("CSTADV00200.getRateMgmt",
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
	public IDataSet getRateMgmt(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("CSTADV00200.getRateMgmt",
				requestData.getFieldMap());

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_rate", rs);

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
	 * @throws RemoteException 
	 * @throws Exception 
	 */
	public IDataSet saveConsultMgmt(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		IRecordSet mgmt = requestData.getRecordSet("ds_consult_m");
		IRecordSet dtl = requestData.getRecordSet("ds_consult_d");

		Map mMgmt = null;
		Map mDtl = null;
		Map mSeq = null;
		Map mPrchsNo = null;
		Map mConNo = null;

		if (mgmt != null) {

			mMgmt = mgmt.getRecordMap(0);
			String conMgmtNo = mMgmt.get("CON_MGMT_NO") == null ? "" : mMgmt
					.get("CON_MGMT_NO").toString().replaceAll(" ", "");
			mPrchsNo = (Map) queryForObject("CSTADV00200.getPrchsMgmtNo",
					mMgmt, onlineCtx);
			mMgmt.put("PRCHS_MGMT_NO", mPrchsNo.get("PRCHS_MGMT_NO"));

			if (conMgmtNo.equals("")) {
				mConNo = (Map) queryForObject("CSTADV00200.getConMgmtNo",
						mMgmt, onlineCtx);
				mMgmt.put("CON_MGMT_NO", mConNo.get("CON_MGMT_NO"));
				insert("CSTADV00200.addConsultMgmt", mMgmt, onlineCtx);
				insert("CSTADV00200.addPrchsMgmt", mMgmt, onlineCtx);

				for (int i = 0; i < dtl.getRecordCount(); i++) {
					mDtl = dtl.getRecordMap(i);
					mDtl.put("CON_MGMT_NO", mConNo.get("CON_MGMT_NO"));
					mDtl.put("PROD_CD", mMgmt.get("PROD_CD"));
					mDtl.put("SER_NUM", mMgmt.get("SER_NUM"));
					mDtl.put("INS_USER_ID", mMgmt.get("INS_USER_ID"));
					mDtl.put("TYPE_CL", "1");
					mDtl.put("CON_SEQ", i + 1);
					insert("CSTADV00200.addConsultDtl", mDtl, onlineCtx);
					mDtl.put("PRCHS_MGMT_NO", mPrchsNo.get("PRCHS_MGMT_NO"));
					mDtl.put("PRCHS_SEQ", i + 1);
					insert("CSTADV00200.addPrchsDtl", mDtl, onlineCtx);
				}

			} else {
				update("CSTADV00200.updateConsultMgmt", mMgmt, onlineCtx);
				insert("CSTADV00200.addPrchsMgmt", mMgmt, onlineCtx);
				for (int i = 0; i < dtl.getRecordCount(); i++) {
					mDtl = dtl.getRecordMap(i);
					mDtl.put("INS_USER_ID", mMgmt.get("INS_USER_ID"));
					mDtl.put("TYPE_CL", "1");
					update("CSTADV00200.updateConsultDtl", mDtl, onlineCtx);
					mDtl.put("PRCHS_MGMT_NO", mPrchsNo.get("PRCHS_MGMT_NO"));
					mDtl.put("PRCHS_SEQ", i + 1);
					insert("CSTADV00200.addPrchsDtl", mDtl, onlineCtx);
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
	 * @throws RemoteException 
	 */
	public IDataSet deleteConsult(IDataSet requestData, IOnlineContext onlineCtx) {

		IRecordSet mgmt = requestData.getRecordSet("ds_consult_m");
		Map mCon = null;

		if (mgmt != null) {
			mCon = mgmt.getRecordMap(0);
			update("CSTADV00200.updateConsultMgmtDel", mCon, onlineCtx);
			update("CSTADV00200.updateConsultDtlDel", mCon, onlineCtx);
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
	 * @throws Exception 
	 */
	public IDataSet savePrchs(IDataSet requestData, IOnlineContext onlineCtx)
			throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("CSTADV00200.savePrchs method start");
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
			mUpdCnt = (Map) queryForObject("CSTADV00200.getConUpdCnt", mMgmt,
					onlineCtx);
			aUpdCnt = (int) Double.parseDouble(mUpdCnt.get("UPD_CNT")
					.toString());

			if (bUpdCnt != aUpdCnt) {
				throw new BizRuntimeException("조회 후 다시 처리하십시요.");
			}

			if (mMgmt.get("BUY_CL").toString().equals("R")) {
				update("CSTADV00200.updateConsultMgmtPrcSt", mMgmt, onlineCtx);
			} else if (mMgmt.get("PRC_GB").toString().equals("P")
					|| mMgmt.get("PRC_ST").toString().equals("23")
					|| mMgmt.get("PRC_ST").toString().equals("2C")
					|| (mMgmt.get("PRC_GB").toString().equals("C") && mMgmt
							.get("PRC_ST").toString().equals("11"))
					|| (mMgmt.get("PRC_GB").toString().equals("P") && mMgmt
							.get("PRC_ST").toString().equals("14"))
					|| (mMgmt.get("PRC_GB").toString().equals("C") && mMgmt
							.get("PRC_ST").toString().equals("13"))) {

				update("CSTADV00200.updatePrchsMgmt", mMgmt, onlineCtx);
				delete("CSTADV00200.deletePrchsDtl", mMgmt, onlineCtx);

				for (int i = 0; i < dtl.getRecordCount(); i++) {
					mDtl = dtl.getRecordMap(i);
					mDtl.put("PRCHS_MGMT_NO", mMgmt.get("PRCHS_MGMT_NO"));
					mDtl.put("PROD_CD", mMgmt.get("PROD_CD"));
					mDtl.put("SER_NUM", mMgmt.get("SER_NUM"));
					mDtl.put("PRCHS_SEQ", i + 1);
					insert("CSTADV00200.addPrchsDtl", mDtl, onlineCtx);
				}
				update("CSTADV00200.updateConsultMgmt", mMgmt, onlineCtx);

			} else {
				mPrchsNo = (Map) queryForObject("CSTADV00200.getPrchsMgmtNo",
						mMgmt, onlineCtx);
				mMgmt.put("PRCHS_MGMT_NO", mPrchsNo.get("PRCHS_MGMT_NO"));

				insert("CSTADV00200.addPrchsMgmt", mMgmt, onlineCtx);

				for (int i = 0; i < dtl.getRecordCount(); i++) {
					mDtl = dtl.getRecordMap(i);
					mDtl.put("PRCHS_MGMT_NO", mPrchsNo.get("PRCHS_MGMT_NO"));
					mDtl.put("PROD_CD", mMgmt.get("PROD_CD"));
					mDtl.put("SER_NUM", mMgmt.get("SER_NUM"));
					mDtl.put("PRCHS_SEQ", i + 1);
					insert("CSTADV00200.addPrchsDtl", mDtl, onlineCtx);
				}
				update("CSTADV00200.updateConsultMgmt", mMgmt, onlineCtx);

				for (int i = 0; i < dtl.getRecordCount(); i++) {
					mDtl = dtl.getRecordMap(i);
					mDtl.put("CON_MGMT_NO", mMgmt.get("CON_MGMT_NO"));
					mDtl.put("INS_USER_ID", mMgmt.get("INS_USER_ID"));
					mDtl.put("TYPE_CL", "1");
//					update("CSTADV00200.updateConsultDtl", mDtl, onlineCtx);
				}
			}
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
	 * @throws RemoteException 
	 */
	public IDataSet deletePrchs(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException {

		Log log = LogManager.getLog(onlineCtx);

		IRecordSet mgmt = requestData.getRecordSet("ds_consult_m");

		Map mMgmt = null;
		String sPrchsMgmtNo = ""; // 매입관리번호

		if (mgmt != null) {

			mMgmt = mgmt.getRecordMap(0);
			update("CSTADV00200.updateConsultMgmtBuyCl", mMgmt, onlineCtx);
			update("CSTADV00200.updatePrchsMgmtDel", mMgmt, onlineCtx);
			update("CSTADV00200.updatePrchsDtlDel", mMgmt, onlineCtx);

			sPrchsMgmtNo = (String) mMgmt.get("prchs_mgmt_no");
			if (sPrchsMgmtNo != null && !sPrchsMgmtNo.equals("")) {
				INN inn = (INN) lookupBizComponent("sktst.dis.INN");
				IDataSet dsInnCncl = inn.saveInnCncl(requestData, onlineCtx);
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
	public IDataSet getAddAmt(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("CSTADV00200.getAddAmt", requestData
				.getFieldMap());

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_add_amt", rs);

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
	public IDataSet updatePrchsCust(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

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

		//		IRecordSet mgmt = requestData.getRecordSet("ds_consult_m");

		Map mPrchs = null;

		if (mgmt != null) {
			mPrchs = mgmt.getRecordMap(0);
			update("CSTADV00200.updateConCust", mPrchs, onlineCtx);
			update("CSTADV00200.updatePrchsCust", mPrchs, onlineCtx);
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
	public IDataSet getConsultMgmtNo(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		IRecordSet prchsM;
		IRecordSet conDtl;
		IRecordSet conM;
		Map mCon = null;
		Map mPrchs = null;
		
		prchsM = queryForRecordSet("CSTADV00200.getPrchsMgmtCheck", requestData
				.getFieldMap());

		if (prchsM.getRecordCount() > 0) {
			conM = queryForRecordSet("CSTADV00200.getPrchsMgmt", requestData
					.getFieldMap());
			mCon = conM.getRecordMap(0);
			conDtl = queryForRecordSet("CSTADV00200.getPrchsDtl", mCon);
		} else {
			conM = queryForRecordSet("CSTADV00200.getConsultM", requestData
					.getFieldMap());
			mCon = conM.getRecordMap(0);
			conDtl = queryForRecordSet("CSTADV00200.getConsultDtl", mCon);
		}
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(conM.getRecordCount()) });
		
		responseData.putRecordSet("ds_consult_m", conM);
		responseData.putRecordSet("ds_consult_d", conDtl);
		
		IRecordSet color = queryForRecordSet("CSTADV00200.getColorList", mCon);

		responseData.putRecordSet("ds_color", color);

		IRecordSet prod = queryForRecordSet("CSTADV00200.getProdMgmt", mCon);

		responseData.putRecordSet("ds_prod_m", prod);

		IRecordSet rate = queryForRecordSet("CSTADV00200.getRateMgmt", mCon);

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

//				return responseData;
	}

}