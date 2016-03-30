/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.brd.biz;

import java.util.Map;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.common.file.IPsFileUploadManager;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-08-17 09:53:45</li>
 * </ul>
 *
 * @author 정재열 (jungjaeyul)
 */
public class ADMBRD00100 extends BaseBizUnit {

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
	public IDataSet saveBoard(IDataSet requestData, IOnlineContext onlineCtx)
			throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMBRD00100.saveBoard start >>>>>>>");
		}
		int nCnt = 0;
		IRecordSet rs = requestData.getRecordSet("List");
		IRecordSet rsFile = requestData.getRecordSet("nc_fileDs");

		String sDocId = null;
		log.debug("rs : " + rs);
		if (rs != null) {
			Map oMapBoard = rs.getRecordMap(0);
			addAttachFiles(requestData, onlineCtx);

			if (rsFile.getRecordCount() > 0)
				sDocId = rsFile.get(0, "DOC_ID"); // 최초 등록시 첨부 파일 정보에서 DOC_ID

			oMapBoard.put("DOC_ID", sDocId);

			log.debug("oMapBoard : " + oMapBoard);
			if (UPDATE_FLAG.equalsIgnoreCase((String) oMapBoard
					.get(CUD_FLAG_PARAM))) {

				nCnt = update("ADMBRD00100.updateBoard", oMapBoard, onlineCtx);

			} else if (INSERT_FLAG.equalsIgnoreCase((String) oMapBoard
					.get(CUD_FLAG_PARAM))) {

				IRecordSet result = queryForRecordSet(
						"ADMBRD00100.getBoardNum", requestData.getFieldMap(),
						onlineCtx);

				String sBoardNum = result.get(0, "BOARD_NUM").toString();
				if (oMapBoard.get("ORG_BOARD_NUM") == null) { // 본글
					oMapBoard.put("ORG_BOARD_NUM", sBoardNum);
					oMapBoard.put("SUP_BOARD_NUM", "0");
				}
				oMapBoard.put("BOARD_NUM", sBoardNum);

				insert("ADMBRD00100.addBoard", oMapBoard, onlineCtx);
				nCnt = 1;
			}
		}

		if (nCnt == 0) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		// 4. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "1" });
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
	public IDataSet getBoardList(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMBRD00100.getBoardList method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("ADMBRD00100.getBoardList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

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
	public IDataSet deleteBoard(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMBRD00100.deleteBoard method start");
		}

		// 2. CRUD 처리
		int nCnt = update("ADMBRD00100.deleteBoard", requestData.getFieldMap(),
				onlineCtx);

		if (nCnt == 0) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		// 3. 첨부 파일 처리
		addAttachFiles(requestData, onlineCtx);

		// 4. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.DELETE_OK_SIMPLE_MESSAGE_ID, null);
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
	public IDataSet addAttachFiles(IDataSet requestData,
			IOnlineContext onlineCtx) {

		IPsFileUploadManager fileManager = (IPsFileUploadManager) ComponentRegistry
				.lookup(ServiceConstants.FILEUPLOAD);
		int updateCount = 0;
		try {
			updateCount = fileManager.saveAllFileInfo("nc_fileDs", requestData,
					onlineCtx);
			fileManager.commitFile("nc_fileDs", requestData);
		} catch (Exception ex) {
			fileManager.rollbackFile("nc_fileDs", requestData);
			throw new BizRuntimeException(BaseConstants.UPLOAD_FAIL);
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
						+ updateCount });

	}

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getBoard(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMBRD00100.getBoard start >>>>>>>");
		}

		IRecordSet rs = queryForRecordSet("ADMBRD00100.getBoard", requestData
				.getFieldMap(), onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_result", rs);
		return responseData;
	}

}