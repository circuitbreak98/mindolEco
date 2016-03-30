/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.sal.asm.biz;

import java.util.Map;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-10-25 10:20:07</li>
 * </ul>
 *
 * @author 전희경 (jeonheekyung)
 */
public class SALASM00200 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전희경 (jeonheekyung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addProdAssignMgmt(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALASM00200.insertProdAssignMgmt method start");
		}
		
		String procFlag = "";
		int nProcCnt = 0;
		String sResult = "";

		//사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		Map keyMap = requestData.getFieldMap();
//log.debug(">>>>>>>> "+keyMap);		
		procFlag = keyMap.get("flag").toString();
		
		if ("MOD".equalsIgnoreCase(procFlag)) {
			update("SALASM00200.updateProdAssign", keyMap, onlineCtx);
			nProcCnt++;
			sResult = "Update:";
		}
		else if ("NEW".equalsIgnoreCase(procFlag)) {
			insert("SALASM00200.insertProdAssign", keyMap, onlineCtx);
			nProcCnt++;
			sResult = "Insert:";
		}
		
		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { sResult + nProcCnt });

		return result;
	}

	/**
	 * 
	 *
	 * @author 전희경 (jeonheekyung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdColorList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALASM00200.getProdColorList method start");
		}

		// 상품 색상 리스트 조회.
		IRecordSet rsProdColorList = queryForRecordSet(
				"SALASM00200.getProdColorList", requestData.getFieldMap(),
				onlineCtx);

		if (rsProdColorList == null) {
			rsProdColorList = new RecordSet("ds_prodColorList");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsProdColorList.getRecordCount()) });

		responseData.putRecordSet("ds_prodColorList", rsProdColorList);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전희경 (jeonheekyung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdCl(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALASM00200.getProdCl method start");
		}
//log.debug("@@@" + requestData.getFieldMap());
		// 상품 색상 리스트 조회.
		IRecordSet rsProdCl = queryForRecordSet("SALASM00200.getProdCl",
				requestData.getFieldMap(), onlineCtx);

		if (rsProdCl == null) {
			rsProdCl = new RecordSet("ds_prodCl");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsProdCl.getRecordCount()) });

		responseData.putRecordSet("ds_prodCl", rsProdCl);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전희경 (jeonheekyung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdAssignMgmt(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALASM00200.getProdAssign method start");
		}
//log.debug("@@@" + requestData.getFieldMap());
		// 배정요청상세조회.
		IRecordSet rsProdAssign = queryForRecordSet("SALASM00200.getProdAssign",
				requestData.getFieldMap(), onlineCtx);

		if (rsProdAssign == null) {
			rsProdAssign = new RecordSet("ds_prodAssign");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsProdAssign.getRecordCount()) });

		responseData.putRecordSet("ds_prodAssign", rsProdAssign);

		return responseData;
	}

}