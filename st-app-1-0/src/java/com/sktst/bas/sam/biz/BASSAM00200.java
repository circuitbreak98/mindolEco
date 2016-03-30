/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.sam.biz;

import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-11-22 14:54:53</li>
 * </ul>
 *
 * @author 김만수 (kimmansoo)
 */
public class BASSAM00200 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getSamSelect(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASSAM00200.getSamSelect method start");
		}

		IRecordSet rs = queryForRecordSet("BASSAM00200.getSamSelect",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("List", rs);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getSamCheckSelect(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASSAM00200.getSamCheckSelect method start");
		}

		IRecordSet rs = queryForRecordSet("BASSAM00200.getSamCheckSelect",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("List", rs);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveSamInsert(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		
		if (log.isDebugEnabled()) 
		{
			log.debug("BASSAM00200.saveSamInsert method start");
		}
		
		IRecordSet ds = requestData.getRecordSet("ds_list");
		
		Map mMgmt = null;
		int iRows = 0;
		
		if (ds != null) 
		{
			for (int i = 0; i < ds.getRecordCount(); i++) 
			{
				mMgmt = ds.getRecordMap(i);
				
				insert("BASSAM00200.saveSamInsert", mMgmt, onlineCtx);
				
				iRows++;
			}
		}
		
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "Insert:" + iRows });
	}

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveSamUpdate(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		
		if (log.isDebugEnabled())
		{
			log.debug("BASSAM00200.saveSamUpdate method start");
		}
		
		IRecordSet ds = requestData.getRecordSet("ds_list");
		IRecordSet del_ds = requestData.getRecordSet("ds_del_list");
		
		Map mMgmt = null;
		int iUp = 0;
		int iIns = 0;
		int iDel = 0;
		
		if (del_ds != null) 
		{
			for (int i = 0; i < del_ds.getRecordCount(); i++) 
			{
				mMgmt = del_ds.getRecordMap(i);
				log.debug("BASSAM00200.saveSamDelete method Call");
				update("BASSAM00200.saveSamDelete", mMgmt, onlineCtx);
				iDel++;
			}
		}
		
		if (ds != null) 
		{
			for (int i = 0; i < ds.getRecordCount(); i++) 
			{
				mMgmt = ds.getRecordMap(i);
				log.debug(mMgmt.get("SEQ"));
				if(mMgmt.get("SEQ") == "" || mMgmt.get("SEQ") == null)
				{
					log.debug("BASSAM00200.saveSamInsert method Call");
					insert("BASSAM00200.saveSamInsert", mMgmt, onlineCtx);
					iIns++;
				}
				else
				{
					log.debug("BASSAM00200.saveSamUpdate method Call");
					update("BASSAM00200.saveSamUpdate", mMgmt, onlineCtx);					
					iUp++;
				}
			}
		}
		
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { 
						"Update:" + iUp, "Insert:" + iIns,  "Delete:" + iDel});
	}

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveSamDelete(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		
		if (log.isDebugEnabled()) 
		{
			log.debug("BASSAM00200.saveSamDelete method start");
		}
		
		IRecordSet ds = requestData.getRecordSet("ds_list");
		
		Map mMgmt = null;
		int iRows = 0;
		
		if (ds != null) 
		{
			for (int i = 0; i < ds.getRecordCount(); i++) 
			{
				mMgmt = ds.getRecordMap(i);
				
				update("BASSAM00200.saveSamDelete", mMgmt, onlineCtx);
				
				iRows++;
			}
		}
		
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "Delete:" + iRows });
	}

}