/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.cdm.biz;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.message.IMessageManager;
import nexcore.framework.core.message.internal.DefaultMessageManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : 유키 인터페이스 코드 관리</li>
 * <li>작성일 : 2009-04-14 18:47:14</li>
 * </ul>
 *
 * @author 최승호 (choiseungho)
 */
public class BASCDM08100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 유키 인터페이스 코드 취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getUkeyIfCd(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASCDM08100.getUkeyIfCd method start");
		}

		// 2. CRUD 처리
		String serviceCode = requestData.getField("service_code");
		String query = "";
		if ("1".compareTo(serviceCode) == 0) {
			query = "BASCDM08100.getProdList";
		} else if ("2".compareTo(serviceCode) == 0) {
			query = "BASCDM08100.getModelList";
		} else if ("3".compareTo(serviceCode) == 0) {
			query = "BASCDM08100.getCommList";
		} else {
			query = "BASCDM08100.getUkeyList";
		}
		IRecordSet rs = queryForRecordSet(query, requestData.getFieldMap(),
				onlineCtx);
		if (rs == null) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("output", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 부가상품 칯 공통코드 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveIfTable(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASCDM08100.saveIfTable method start");
		}

		// 2. CRUD 처리		
		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		int updateCnt = 0;

		String serviceCode = requestData.getField("service_code");
		IRecordSet rs = requestData.getRecordSet("input");
		if (rs != null) {
			if ("1".compareTo(serviceCode) == 0) {

				for (int i = 0; i < rs.getRecordCount(); i++) {
					
					updateCnt = 0;
					updateCnt = update("BASCDM08100.updateProd", rs.getRecord(i), onlineCtx);
					
					if(updateCnt == 0 ){
						insert("BASCDM08100.addProd", rs.getRecord(i),
								onlineCtx);
						insertCount++;						
					}else{
						updateCount = updateCount + updateCnt;
					}

					update("BASCDM08100.updateIfProdApplyYn", rs.getRecord(i),
							onlineCtx);
				}

			} else {
				
				for (int i = 0; i < rs.getRecordCount(); i++) {
					
					updateCnt = 0;
					updateCnt = update("BASCDM08100.updateCommCd", rs.getRecord(i), onlineCtx);
					
					if(updateCnt == 0 ){
						insert("BASCDM08100.addCommCd", rs.getRecord(i),onlineCtx);
						insertCount++;						
					}else{
						updateCount = updateCount + updateCnt;
					}
					
					update("BASCDM08100.updateIfCommCdApplyYn",
							rs.getRecord(i), onlineCtx);
				}
			}
			cudAllCount = insertCount + updateCount;
			if (rs == null) {
				throw new NoRecordAffectedException(
						BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
			}

			if (cudAllCount < 1) {
				throw new NoRecordAffectedException(
						BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
			}
		}

		// 3. 결과 지정
		IMessageManager messageManager = (IMessageManager) ComponentRegistry
				.lookup(ServiceConstants.MESSAGE);
		((DefaultMessageManager) messageManager).refresh();

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount, "" + 0 });
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getUkeyPop(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASCDM08100.getUkeyIfCd method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("BASCDM08100.getUkeyPop", requestData.getFieldMap(),
				onlineCtx);
		
		if (rs != null && rs.getRecordCount() < 1) {
			requestData.putField("user_nm", "");
			rs = queryForRecordSet("BASCDM08100.getUkeyPop", requestData.getFieldMap(),
					onlineCtx);
		}
		
		if (rs == null) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("output", rs);
		return responseData;
	}

}