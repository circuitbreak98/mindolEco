/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.qna.biz;

import java.util.Map;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.common.file.IPsFileUploadManager;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-02-26 13:26:47</li>
 * </ul>
 *
 * @author 심연정 (shimyunjung)
 */
public class ADMQNA00300 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveQnaList(IDataSet requestData, IOnlineContext onlineCtx) {

		String docId = "";

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMQNA00300.saveQnaList start");
		}

		//  공지사항 내역 맵 객체로  전환
		IRecordSet rsInput = requestData.getRecordSet("input");
		Map inputMap = rsInput.getRecordMap(0);

		//  2. 첨부 파일 처리
		IRecordSet rsFile = requestData.getRecordSet("nc_fileDs");
		if (rsFile.getRecordCount() > 0) {
			addAttachFiles(requestData, onlineCtx);
			docId = rsFile.get(0, "DOC_ID"); // 최초 등록시 첨부 파일 정보에서 DOC_ID
		}

		if (inputMap != null) {

			if (INSERT_FLAG.equalsIgnoreCase((String) inputMap
					.get(CUD_FLAG_PARAM))) {
				inputMap.put("DOC_ID", docId); // 
				insert("ADMQNA00300.addQna", inputMap, onlineCtx);

			} else if (UPDATE_FLAG.equalsIgnoreCase((String) inputMap
					.get(CUD_FLAG_PARAM))) {

				update("ADMQNA00300.updateQna", inputMap, onlineCtx);

			}
		}

		// 3. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_SIMPLE_MESSAGE_ID, null);
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 첨부파일처리
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addAttachFiles(IDataSet requestData,
			IOnlineContext onlineCtx) throws  BizRuntimeException{

		IPsFileUploadManager fileManager = (IPsFileUploadManager) ComponentRegistry
				.lookup(ServiceConstants.FILEUPLOAD);
		try {
			int updateCount = fileManager.saveAllFileInfo("nc_fileDs",
					requestData, onlineCtx);
			fileManager.commitFile("nc_fileDs", requestData);
			return DataSetFactory.createWithOKResultMessage(
					BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
							+ updateCount });
		} catch (Exception ex) {
			fileManager.rollbackFile("nc_fileDs", requestData);
			throw new BizRuntimeException("addAttachFiles", ex);
		}
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- Q&A 삭제
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteQna(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMQNA00300.deleteQna method start");
		}
		
		// 2. CRUD 처리
		delete("ADMQNA00300.deleteQnA", requestData
				.getFieldMap(), onlineCtx);

		// 3. 첨부 파일 처리
		addAttachFiles(requestData, onlineCtx);

		
		// 4. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.DELETE_OK_SIMPLE_MESSAGE_ID, null);
	}

}