/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.pol.spm.biz;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/정책</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-07-13 13:58:28</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class POLSPM00800 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * -상품그룹을 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("POLSPM00800 getProdList method start");
		}

		IRecordSet rsProdList = queryForRecordSet("POLSPM00800.getProdList",
				requestData.getFieldMap(), onlineCtx);

		if (rsProdList == null) {
			rsProdList = new RecordSet("dsProdList");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsProdList.getRecordCount()) });

		responseData.putRecordSet("dsProdList", rsProdList);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * -상품그룹을 등록한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveProdGroup(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("POLSPM00800.saveProdGroup method start");
		}

		int iInsertCnt = 0;
		int iDeleteCnt = 0;
		
		Map rsCondition = requestData.getFieldMap();
		log.debug("rsCondition =  " + rsCondition);
		IRecordSet rs = requestData.getRecordSet("dsProdList_target");
		log.debug("rs =  " + rs);
		Map mSave = null;
		if (rs != null) {
			/**
			 * 상품 그룹 등록
			 * I : 신규   U : 기존   M : 등록 후 대상그룹에서 선택하여 변경
			 */
			if("I".equals(rsCondition.get("SAVE_GB"))){
				
				/**
				 *  SEQ 
				 */
				Map mSeq = (Map) queryForObject("POLSPM00800.getNewProdGroupId", rsCondition, onlineCtx);
				
				for (int i = 0; i < rs.getRecordCount(); i++) {
					/**
					 * 신규 상품 등록
					 */
					mSave = rs.getRecordMap(i);
					String sGroupId = (String) mSeq.get("PROD_GROUP_ID");
						
					if (sGroupId == null || sGroupId.equals("")) {
						throw new BizRuntimeException(
								"상품그룹 ID 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
					}
					
					mSave.put("PROD_GROUP_ID", sGroupId);
					rsCondition.put("PROD_GROUP_ID", sGroupId);
					
					insert("POLSPM00800.insertProdGroup", mSave, onlineCtx);
					iInsertCnt++;
				}
				
			}else if("U".equals(rsCondition.get("SAVE_GB"))){
				for (int i = 0; i < rs.getRecordCount(); i++) {

					mSave = rs.getRecordMap(i);

					if (INSERT_FLAG.equalsIgnoreCase((String) mSave
							.get(CUD_FLAG_PARAM))) {

						insert("POLSPM00800.insertProdGroup", mSave, onlineCtx);
						iInsertCnt++;

					} else if (DELETE_FLAG.equalsIgnoreCase((String) mSave
							.get(CUD_FLAG_PARAM))) {

						update("POLSPM00800.deleteProdGroup", mSave, onlineCtx);
						iDeleteCnt++;

					}

				}
			}else if("M".equals(rsCondition.get("SAVE_GB"))){
				
				log.debug("SAVE_GB = m ");
				delete("POLSPM00800.deleteProdGroupAll", rsCondition, onlineCtx);
				
				for (int i = 0; i < rs.getRecordCount(); i++) {

					mSave = rs.getRecordMap(i);

					insert("POLSPM00800.insertProdGroup", mSave, onlineCtx);
					iInsertCnt++;

				}
			}
			
		}

		IRecordSet rsProdGroupList = queryForRecordSet( "POLSPM00800.getProdGroupList", rsCondition, onlineCtx);

		if (rsProdGroupList == null) {
			rsProdGroupList = new RecordSet("dsProdList_target");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "insert="
						+ iInsertCnt + "delete=" + iDeleteCnt });

		responseData.putRecordSet("dsProdList_target", rsProdGroupList);

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
	public IDataSet getProdGroupList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("POLSPM00800 getProdGroupList method start");
		}

		IRecordSet rsProdGroupList = queryForRecordSet(
				"POLSPM00800.getProdGroupList", requestData.getFieldMap(),
				onlineCtx);

		if (rsProdGroupList == null) {
			rsProdGroupList = new RecordSet("dsProdList_target");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsProdGroupList.getRecordCount()) });

		responseData.putRecordSet("dsProdList_target", rsProdGroupList);

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
	public IDataSet saveProdGroupChg(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("POLSPM00800.saveProdGroupChg method start");
		}
		
		Map mSeq = null; // 상품그룹 ID 생성
		
		IRecordSet rsModDate = requestData.getRecordSet("ds_modDate"); // 조건
		
		mSeq = new HashMap();
		mSeq = (Map) queryForObject("POLSPM00800.getNewProdGroupId", mSeq, onlineCtx);
		
		String sGroupId = ""; //가격표ID
		sGroupId = (String) mSeq.get("PROD_GROUP_ID");
		
		if (sGroupId == null || sGroupId.equals("")) {
			throw new BizRuntimeException(
					"상품그룹 ID 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}
		
		/**
		 * 상품 그룹 copy
		 */
		Map modDate = rsModDate.getRecordMap(0);
		modDate.put("NEW_PROD_GROUP_ID", sGroupId);
		
		log.debug("modDate start == " + modDate);
		
		insert("POLSPM00800.insertProdGroupCopy",modDate,onlineCtx);

		
		/**
		 * 이전 상품 그룹 copy
		 */
		
		// 이전 판매가격표 Master 종료처리 (APLY_END_DTM 변경) 
		update("POLSPM00800.updateProdGroupClose", modDate, onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "1" });

		return responseData;
	}

}