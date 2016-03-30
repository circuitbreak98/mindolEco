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
 * <li>작성일 : 2010-04-02 16:46:39</li>
 * </ul>
 *
 * @author 전현주 (junhyunjoo)
 */
public class ADMADR00200 extends BaseBizUnit {

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
	public IDataSet getNoticeGroupList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getNoticeGroupList method start");
		}

		IRecordSet rs = queryForRecordSet("ADMADR00200.getNoticeGroupList", requestData
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
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getMyNoticeGroupList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getMyNoticeGroupList method start");
		}

		IRecordSet rs = queryForRecordSet("ADMADR00200.getMyNoticeGroupList", requestData
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
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteMyNoticeGroup(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("deleteMyNoticeGroup method start");
		}
		int updateCount = delete("ADMADR00200.deleteMyNoticeGroup",
				requestData.getFieldMap(), onlineCtx);
		
		if (updateCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);			
		}
		
		delete("ADMADR00200.deleteMyNoticeAddr",
				requestData.getFieldMap(), onlineCtx);		
		
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[]{"" + updateCount});
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
	public IDataSet getMyNoticeList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getMyNoticeList method start");
		}

		IRecordSet rs = queryForRecordSet("ADMADR00200.getMyNoticeList", requestData
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
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getNoticeUserList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getNoticeUserList method start");
		}

		IRecordSet rs = queryForRecordSet("ADMADR00200.getNoticeUserList", requestData
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
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveNoticeAddr(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveNoticeAddr method start");
		}
		 
		Map oMapRowData = requestData.getFieldMap();
		
		String sGroupNm = oMapRowData.get("NOTICE_GROUP").toString();
			
		if (INSERT_FLAG.equalsIgnoreCase(oMapRowData.get(
				"FLAG").toString())) {
						
			insert("ADMADR00200.addMyNoticeGroup", oMapRowData, onlineCtx);
		}
		else if (UPDATE_FLAG.equalsIgnoreCase(oMapRowData.get(
		"FLAG").toString())) {
		
			update("ADMADR00200.updateMyNoticeGroup", oMapRowData, onlineCtx);	
			// UI단 변경전 그룹명
			oMapRowData.put("NOTICE_GROUP", oMapRowData.get("org_notice_group").toString());
		}			
		else if (DELETE_FLAG.equalsIgnoreCase(oMapRowData.get(
		"FLAG").toString())) {
			
			delete("ADMADR00200.deleteMyNoticeGroup", oMapRowData, onlineCtx);
		}
		else
		{

		}
		
		// 나의 주소록 그룹별 주소록 삭제 / 재등록			
		delete("ADMADR00200.deleteMyNoticeAddr", oMapRowData, onlineCtx);
		
		IRecordSet rsMyAddList = requestData.getRecordSet("ds_myAddrList");
		
		Map oMapRowAddrData = null;
		
		for(int j=0; j < rsMyAddList.getRecordCount(); j++)
		{		
			
			oMapRowAddrData = rsMyAddList.getRecordMap(j);
			oMapRowAddrData.put("NOTICE_GROUP",sGroupNm );
			log.debug("NOTICE_GROUP : " + sGroupNm);
			delete("ADMADR00200.addMyNoticeAddr", oMapRowAddrData, onlineCtx);
		}
		

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[]{"" + "1"});
	}

}