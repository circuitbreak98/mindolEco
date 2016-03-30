/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.bco.biz;

import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;
import com.sktst.common.file.IPsFileUploadManager;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2013-05-15 16:08:46</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class AttachFileManager extends com.sktst.common.base.BaseBizUnit {

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
	public IDataSet addAttachFile(IDataSet requestData, IOnlineContext onlineCtx) {

		IPsFileUploadManager fileManager = (IPsFileUploadManager) ComponentRegistry.lookup(ServiceConstants.FILEUPLOAD);
		try {
			int updateCount = fileManager.saveAllFileInfo("nc_fileDs",
					requestData, onlineCtx);
			fileManager.commitFile("nc_fileDs", requestData);
			return DataSetFactory.createWithOKResultMessage(
					BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
							+ updateCount });
		} catch (Exception ex) {
			fileManager.rollbackFile("nc_fileDs", requestData);
			throw new BizRuntimeException(BaseConstants.UPLOAD_FAIL);
		}
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
	public IDataSet getAttatchList(IDataSet requestData, IOnlineContext onlineCtx) {
		//	 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("AttachFileManager.getAttatchList method start");
		}
	
		// 2. 파일 내역 조회
		IPsFileUploadManager fileManager = (IPsFileUploadManager) ComponentRegistry
				.lookup(ServiceConstants.FILEUPLOAD);
	
		// 3. 결과 지정
		return fileManager.getFileInfoList("nc_fileDs", requestData, onlineCtx);
	}
}