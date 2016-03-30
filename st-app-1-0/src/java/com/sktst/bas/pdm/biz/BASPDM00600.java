/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.pdm.biz;

import java.math.BigDecimal;
import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;
import nexcore.framework.integration.db.NoRecordFoundException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-11 17:04:49</li>
 * </ul>
 *
 * @author 전현주 (junhyunjoo)
 */
public class BASPDM00600 extends com.sktst.common.base.BaseBizUnit {

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
	public IDataSet getUsedProdInfo(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		if (log.isDebugEnabled()) {
			log.debug("getUsedProdInfo method start");
		}

		BigDecimal maxPrice = null;
		BigDecimal setoffNum = null;

		/*
		 Map map = (Map) queryForObject("BASPDM00600.getUsedProdInfo",
		 requestData.getFieldMap());
		 */

		Map map = null;
		IRecordSet rs = queryForRecordSet("BASPDM00600.getUsedProdInfo",
				requestData.getFieldMap());

		map = rs.getRecordMap(0);

		if (map == null) {
			throw new NoRecordFoundException();
		}

		Map sUplstMap = (Map) queryForObject("BASPDM00600.getUplstAplyMdlCnt",
				requestData.getFieldMap());

		IRecordSet rsAdd = queryForRecordSet("BASPDM00600.getAddedProdList",
				requestData.getFieldMap());

		IRecordSet rsColor = queryForRecordSet("BASPDM00600.getUsedProdColor",
				requestData.getFieldMap());

		//IRecordSet rsExt = queryForRecordSet("BASPDM00600.getExtProdList",requestData.getFieldMap());

		map.put("UPLST_MDL_CNT", sUplstMap.get("CNT").toString());

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		responseData.putFieldMap(map);
		responseData.putRecordSet("ds_suppl", rsAdd);
		responseData.putRecordSet("ds_color", rsColor);
		//responseData.putRecordSet("ds_ext", rsExt);
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
	public IDataSet addUsedProdInfo(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("addUsedProdInfo method start");
		}

		insert("BASPDM00600.insertUsedProdInfo", requestData.getFieldMap(),
				onlineCtx);

		IRecordSet rs = requestData.getRecordSet("ds_suppl");

		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {

				insert("BASPDM00600.insertAddedProdInfo", rs.getRecordMap(i),
						onlineCtx);

			}
		}

		IRecordSet rsColor = requestData.getRecordSet("ds_color");

		if (rsColor != null) {
			for (int i = 0; i < rsColor.getRecordCount(); i++) {

				insert("BASPDM00600.insertUsedProdColor", rsColor
						.getRecordMap(i), onlineCtx);
			}
		}

		/*
		 IRecordSet rs1 = requestData.getRecordSet("ds_ext");

		 if (rs1 != null) {
		 for (int i = 0; i < rs1.getRecordCount(); i++) {

		 insert("BASPDM00600.insertExtProdInfo", rs1.getRecordMap(i),
		 onlineCtx);
		 }
		 }
		 */
		String ifYn = requestData.getField("op_dt");
		if (ifYn != null && "".compareTo(ifYn) != 0) {
			update("BASPDM00600.updateIfModelApplyYn", requestData
					.getFieldMap(), onlineCtx);
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "1" });
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
	public IDataSet updateUsedProdInfo(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("updateUsedProdInfo method start");
		}

		int updateCount = update("BASPDM00600.updateUsedProdInfo", requestData
				.getFieldMap(), onlineCtx);

		IRecordSet rs1 = requestData.getRecordSet("ds_suppl");

		if (rs1 != null) {

			for (int i = 0; i < rs1.getRecordCount(); i++) {

				if (DELETE_FLAG.equalsIgnoreCase(rs1.getRecord(i).get(
						CUD_FLAG_PARAM))) {

					delete("BASPDM00600.deleteAddedProdIList", rs1
							.getRecordMap(i), onlineCtx);

				} else {

					update("BASPDM00600.updateAddedProdInfo", rs1
							.getRecordMap(i), onlineCtx);
				}
			}
		}

		IRecordSet rsColor = requestData.getRecordSet("ds_color");

		if (rsColor != null) {
			for (int i = 0; i < rsColor.getRecordCount(); i++) {
				delete("BASPDM00600.deleteUsedProdColor", rsColor.getRecord(i));
			}
			for (int i = 0; i < rsColor.getRecordCount(); i++) {

				insert("BASPDM00600.insertUsedProdColor", rsColor
						.getRecordMap(i), onlineCtx);
			}
		}

		/*
		 IRecordSet rs2 = requestData.getRecordSet("ds_ext");

		 if (rs2 != null) {

		 for (int i = 0; i < rs2.getRecordCount(); i++) {
		 if (DELETE_FLAG.equalsIgnoreCase(rs2.getRecord(i).get(
		 CUD_FLAG_PARAM))) {
		 
		 delete("BASPDM00600.deleteExtProdList",rs2.getRecordMap(i), onlineCtx);
		 
		 }else{
		 update("BASPDM00600.updateExtProdInfo", rs2.getRecordMap(i),
		 onlineCtx);
		 }
		 }
		 }
		 */
		String ifYn = requestData.getField("op_dt");
		if (ifYn != null && "".compareTo(ifYn) != 0) {
			update("BASPDM00600.updateIfModelApplyYn", requestData
					.getFieldMap(), onlineCtx);
		}

		if (updateCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
						+ updateCount });
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
	public IDataSet deleteUsedProdInfo(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("deleteUsedProdInfo method start");
		}

		int updateCount = update("BASPDM00600.deleteUsedProdInfo", requestData
				.getFieldMap());

		IRecordSet rs1 = requestData.getRecordSet("ds_suppl");

		if (rs1 != null) {
			for (int i = 0; i < rs1.getRecordCount(); i++) {
				delete("BASPDM00600.deleteAddedProdInfo", rs1.getRecord(i));
			}
		}

		IRecordSet rsColor = requestData.getRecordSet("ds_color");

		if (rsColor != null) {
			for (int i = 0; i < rsColor.getRecordCount(); i++) {
				delete("BASPDM00600.deleteUsedProdColor", rsColor.getRecord(i));
			}
		}

		/*
		 IRecordSet rs2 = requestData.getRecordSet("ds_ext");

		 if (rs2 != null) {
		 for (int i = 0; i < rs2.getRecordCount(); i++) {
		 delete("BASPDM00600.deleteExtProdInfo", rs2.getRecord(i));
		 }
		 }
		 */
		if (updateCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
						+ updateCount });
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
	public IDataSet getMfactPlcList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getMfactPlcList method start");
		}

		IRecordSet rs = queryForRecordSet("BASPDM00600.getMfactPlcList",
				requestData.getFieldMap());

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_mfact_id", rs);
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
	public IDataSet checkUsedProdDup(IDataSet requestData,
			IOnlineContext onlineCtx) {

		//		 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("checkUsedProdDup method start");
		}

		// 2. CRUD 처리
		IRecordSet rs1 = queryForRecordSet("BASPDM00600.getUsedProdCodeCheck",
				requestData.getFieldMap());

		IRecordSet rs2 = queryForRecordSet("BASPDM00600.getUsedProdNmCheck",
				requestData.getFieldMap());

		int cnt1Count = Integer.parseInt(rs1.get(0, "CNT1"));
		int cnt2Count = Integer.parseInt(rs2.get(0, "CNT2"));

		// 3. 예외지정
		if (cnt1Count > 0) {
			throw new BizRuntimeException(BaseConstants.CHECK_REG,
					new String[] { "모델코드" });
		}

		if (cnt2Count > 0) {
			throw new BizRuntimeException(BaseConstants.CHECK_REG,
					new String[] { "모델명" });
		}

		// 4. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, null);

		return responseData;
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
	public IDataSet getProdDtlList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		if (log.isDebugEnabled()) {
			log.debug("getProdDtlList method start");
		}

		IRecordSet rsAdd = queryForRecordSet("BASPDM00600.getProdDtlList",
				requestData.getFieldMap());

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		responseData.putRecordSet("ds_suppl", rsAdd);
		return responseData;
	}

}