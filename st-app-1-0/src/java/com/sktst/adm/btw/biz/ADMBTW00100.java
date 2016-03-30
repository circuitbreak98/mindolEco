/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.btw.biz;

import java.util.Map;

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
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-03-03 13:25:30</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class ADMBTW00100 extends BaseBizUnit {

	private static String cValue_1 = "1";
	private static String cValue_N = "N";
	private static String cValue_Y = "Y";
	private static String cValue_U = "U";
	private static String cValue_I = "I";

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getBTWList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getBTWList method start");
		}

		IRecordSet rsAgency = queryForRecordSet("ADMBTW00100.getBTWAgency",
				requestData.getFieldMap());

		if (rsAgency == null) {
			rsAgency = new RecordSet("ds_Agency");
		}

		IRecordSet rsProduct = queryForRecordSet("ADMBTW00100.getBTWProduct",
				requestData.getFieldMap());

		if (rsProduct == null) {
			rsProduct = new RecordSet("ds_Product");
		}

		IRecordSet rsAdjust = queryForRecordSet("ADMBTW00100.getBTWAdjust",
				requestData.getFieldMap());

		if (rsAdjust == null) {
			rsAdjust = new RecordSet("ds_Adjust");
		}

		IRecordSet rsRange = queryForRecordSet("ADMBTW00100.getBTWRange",
				requestData.getFieldMap());

		if (rsRange == null) {
			rsRange = new RecordSet("ds_Range");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsRange.getRecordCount()) });

		responseData.putRecordSet("ds_Agency", rsAgency);
		responseData.putRecordSet("ds_Product", rsProduct);
		responseData.putRecordSet("ds_Adjust", rsAdjust);
		responseData.putRecordSet("ds_Range", rsRange);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveBTWAgency(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveBTWAgencyList method start");
		}

		int nCnt = 0;
		int nCntApply = 0;
		String sExist_YN = "";
		String sUpdateYN = "";

		IRecordSet rs = requestData.getRecordSet("ds_Agency");
		Map oMapRowData = null;

		if (rs != null) {

			for (int i = 0; i < rs.getRecordCount(); i++) {
				oMapRowData = rs.getRecordMap(i);
				sUpdateYN = (String) oMapRowData.get("update_yn");

				if (cValue_Y.equals(sUpdateYN)) {
					sExist_YN = (String) oMapRowData.get("exist_yn");

					// 자료 Exist 구분이 "N" - Insert
					//                "Y" - UPDATE
					if (cValue_N.equals(sExist_YN)) {
						insert("ADMBTW00100.addBTWAgency", oMapRowData,
								onlineCtx);
						nCntApply++;
					} else {
						nCnt = update("ADMBTW00100.saveBTWAgency", oMapRowData,
								onlineCtx);
						nCntApply = nCntApply + nCnt;
					}
				}
			}
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { ""
						+ nCntApply });
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveBTWAdjust(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveBTWAdjust method start");
		}

		int nCnt = 0;
		int nCntApply = 0;
		String sUpdateYN = "";

		IRecordSet rs = requestData.getRecordSet("ds_Adjust");
		Map oMapRowData = null;

		if (rs != null) {

			for (int i = 0; i < rs.getRecordCount(); i++) {
				oMapRowData = rs.getRecordMap(i);
				sUpdateYN = (String) oMapRowData.get("update_yn");

				if (cValue_U.equals(sUpdateYN)) {
					nCnt = update("ADMBTW00100.saveBTWAdjust", oMapRowData,
							onlineCtx);
					nCntApply = nCntApply + nCnt;
				}
				if (cValue_I.equals(sUpdateYN)) {
					insert("ADMBTW00100.addBTWAdjust", oMapRowData, onlineCtx);
					nCntApply++;
				}
			}
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { ""
						+ nCntApply });
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getBTWAgency(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getBTWAgency method start");
		}

		IRecordSet rsAgency = queryForRecordSet("ADMBTW00100.getBTWAgency",
				requestData.getFieldMap());

		if (rsAgency == null) {
			rsAgency = new RecordSet("ds_Agency");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsAgency.getRecordCount()) });

		responseData.putRecordSet("ds_Agency", rsAgency);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getBTWAdjust(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getBTWAdjust method start");
		}

		IRecordSet rsAdjust = queryForRecordSet("ADMBTW00100.getBTWAdjust",
				requestData.getFieldMap());

		if (rsAdjust == null) {
			rsAdjust = new RecordSet("ds_Adjust");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsAdjust.getRecordCount()) });

		responseData.putRecordSet("ds_Adjust", rsAdjust);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getBTWProduct(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getBTWProduct method start");
		}

		IRecordSet rsProduct = queryForRecordSet("ADMBTW00100.getBTWProduct",
				requestData.getFieldMap());

		if (rsProduct == null) {
			rsProduct = new RecordSet("ds_Product");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsProduct.getRecordCount()) });

		responseData.putRecordSet("ds_Product", rsProduct);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveBTWProduct(IDataSet requestData,
			IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveBTWProduct method start");
		}

		int nCnt = 0;
		int nCntApply = 0;
		String sExist_YN = "";
		String sUpdateYN = "";

		IRecordSet rs = requestData.getRecordSet("ds_Product");
		Map oMapRowData = null;

		if (rs != null) {

			for (int i = 0; i < rs.getRecordCount(); i++) {
				oMapRowData = rs.getRecordMap(i);
				sUpdateYN = (String) oMapRowData.get("update_yn");

				if (cValue_Y.equals(sUpdateYN)) {
					sExist_YN = (String) oMapRowData.get("exist_yn");

					// 자료 Exist 구분이 "N" - Insert
					//                "Y" - UPDATE
					if (cValue_N.equals(sExist_YN)) {
						insert("ADMBTW00100.addBTWProduct", oMapRowData,
								onlineCtx);
						nCntApply++;
					} else {
						nCnt = update("ADMBTW00100.saveBTWProduct",
								oMapRowData, onlineCtx);
						nCntApply = nCntApply + nCnt;
					}
				}
			}
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { ""
						+ nCntApply });
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getBTWRange(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getBTWRange method start");
		}

		IRecordSet rsRange = queryForRecordSet("ADMBTW00100.getBTWRange",
				requestData.getFieldMap());

		if (rsRange == null) {
			rsRange = new RecordSet("ds_Range");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsRange.getRecordCount()) });

		responseData.putRecordSet("ds_Range", rsRange);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveBTWRange(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveBTWRange method start");
		}

		int nCnt = 0;
		int nCntApply = 0;
		String sUpdateYN = "";

		IRecordSet rs = requestData.getRecordSet("ds_Range");
		Map oMapRowData = null;

		if (rs != null) {

			for (int i = 0; i < rs.getRecordCount(); i++) {
				oMapRowData = rs.getRecordMap(i);
				sUpdateYN = (String) oMapRowData.get("update_yn");

				if (cValue_U.equals(sUpdateYN)) {
					nCnt = update("ADMBTW00100.saveBTWRange", oMapRowData,
							onlineCtx);
					nCntApply = nCntApply + nCnt;
				}
				if (cValue_I.equals(sUpdateYN)) {
					insert("ADMBTW00100.addBTWRange", oMapRowData, onlineCtx);
					nCntApply++;
				}
			}
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { ""
						+ nCntApply });
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet delBTWAdjust(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("delBTWAdjust method start");
		}

		int    nCntApply  = 0;
		int    nCntDelete = 0;
		String sDeleteYN  = "";

		IRecordSet rs = requestData.getRecordSet("ds_Adjust");
		Map oMapRowData = null;

		if (rs != null) {

			for (int i = 0; i < rs.getRecordCount(); i++) {
				oMapRowData = rs.getRecordMap(i);
				sDeleteYN = (String) oMapRowData.get("chk");

				if (cValue_1.equals(sDeleteYN)) {
					nCntApply = update("ADMBTW00100.delBTWAdjust", oMapRowData, onlineCtx);
					nCntDelete = nCntDelete + nCntApply;
				}
			}
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "" + nCntDelete });
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet delBTWRange(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("delBTWRange method start");
		}

		int    nCntApply  = 0;
		int    nCntDelete = 0;
		String sDeleteYN  = "";

		IRecordSet rs = requestData.getRecordSet("ds_Range");
		Map oMapRowData = null;

		if (rs != null) {

			for (int i = 0; i < rs.getRecordCount(); i++) {
				oMapRowData = rs.getRecordMap(i);
				sDeleteYN = (String) oMapRowData.get("chk");

				if (cValue_1.equals(sDeleteYN)) {
					nCntApply = update("ADMBTW00100.delBTWRange", oMapRowData, onlineCtx);
					nCntDelete = nCntDelete + nCntApply;
				}
			}
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "" + nCntDelete });
	}
}