/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.adr.biz;

import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-09-10 18:49:55</li>
 * </ul>
 *
 * @author 정재열 (jungjaeyul)
 */
public class ADMADR00100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getGroupList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getGroupList method start");
		}

		IRecordSet rs = queryForRecordSet("ADMADR00100.getGroupList", requestData
				.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[]{String.valueOf(rs
						.getRecordCount())});

		responseData.putRecordSet("List", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getUserInfoList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getUserInfoList method start");
		}

		System.out.println("ADMADR00100.getUserInfoList====================="+requestData
				.getFieldMap());
		IRecordSet rs = queryForRecordSet("ADMADR00100.getUserInfoList", requestData
				.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[]{String.valueOf(rs
						.getRecordCount())});

		responseData.putRecordSet("List", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getMyAddrList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getMyAddrList method start");
		}

		IRecordSet rs = queryForRecordSet("ADMADR00100.getMyAddrList", requestData
				.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[]{String.valueOf(rs
						.getRecordCount())});

		responseData.putRecordSet("List", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getMyGroupList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getMyGroupList method start");
		}

		IRecordSet rs = queryForRecordSet("ADMADR00100.getMyGroupList", requestData
				.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[]{String.valueOf(rs
						.getRecordCount())});

		responseData.putRecordSet("List", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteMyGroup(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("updatePayDelete method start");
		}
		int updateCount = delete("ADMADR00100.deleteMyGroup",
				requestData.getFieldMap(), onlineCtx);
		
		if (updateCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);			
		}
		
		delete("ADMADR00100.deleteMyAddr",
				requestData.getFieldMap(), onlineCtx);		
		
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[]{"" + updateCount});
	}

	/**
	 * 
	 *
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveAddr(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveAddr method start");
		}
		 
		Map oMapRowData = requestData.getFieldMap();
		
		String sGroupNm = oMapRowData.get("ADDR_GROUP").toString();
			
		if (INSERT_FLAG.equalsIgnoreCase(oMapRowData.get(
				"FLAG").toString())) {
						
			insert("ADMADR00100.addMyGroup", oMapRowData, onlineCtx);
		}
		else if (UPDATE_FLAG.equalsIgnoreCase(oMapRowData.get(
		"FLAG").toString())) {
		
			update("ADMADR00100.updateMyGroup", oMapRowData, onlineCtx);	
			// UI단 변경전 그룹명
			oMapRowData.put("ADDR_GROUP", oMapRowData.get("org_addr_group").toString());
		}			
		else if (DELETE_FLAG.equalsIgnoreCase(oMapRowData.get(
		"FLAG").toString())) {
			
			delete("ADMADR00100.deleteMyGroup", oMapRowData, onlineCtx);
		}
		else
		{

		}
		
		// 나의 주소록 그룹별 주소록 삭제 / 재등록			
		delete("ADMADR00100.deleteMyAddr", oMapRowData, onlineCtx);
		
		IRecordSet rsMyAddList = requestData.getRecordSet("ds_myAddrList");
		
		Map oMapRowAddrData = null;
		
		for(int j=0; j < rsMyAddList.getRecordCount(); j++)
		{		
			
			oMapRowAddrData = rsMyAddList.getRecordMap(j);
			oMapRowAddrData.put("ADDR_GROUP",sGroupNm );
			log.debug("ADDR_GROUP : " + sGroupNm);
			delete("ADMADR00100.addMyAddr", oMapRowAddrData, onlineCtx);
		}
		

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[]{"" + "1"});
	}

}

