/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.ntc.biz;

import java.util.Map;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
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
 * <li>설 명 : 공지사항 관리</li>
 * <li>작성일 : 2009-03-16 09:32:17</li>
 * </ul>
 * 
 * @author 최승호 (choiseungho)
 */
public class ADMNTC00100 extends BaseBizUnit {

	/**
	 * 
	 * 
	 * @author 최승호 (choiseungho) - 공지 대상 취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getNoticeTarget(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00100.getNoticeTarget method start");
		}
		
		IPsFileUploadManager fileManager = (IPsFileUploadManager) ComponentRegistry
		.lookup(ServiceConstants.FILEUPLOAD);
		
		String root = fileManager.getRootDirectory();
		
		
		
		// 2. CRUD 처리
		String sClassification = requestData.getField("classification");

		String sQuery = "ADMNTC00100.getNoticeUser";
		if ("ZBAS_C_00130".compareTo(sClassification) == 0) {
			sQuery = "ADMNTC00100.getNoticeDeal";
		} else if ("ZBAS_C_00000".compareTo(sClassification) == 0) {
			sQuery = "ADMNTC00100.getSaleP";
		} else if ("ZBAS_C_00001".compareTo(sClassification) == 0) {
			sQuery = "ADMNTC00100.getMemListByOrg";
		} else if ("ZBAS_C_00002".compareTo(sClassification) == 0) {
			sQuery = "ADMNTC00100.getOrgArea";
		} else if ("ZBAS_C_00003".compareTo(sClassification) == 0) {
			sQuery = "ADMNTC00100.getMyGroup";
		}
		
		IRecordSet rs = queryForRecordSet(sQuery, requestData.getFieldMap(),
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
	 * @author 최승호 (choiseungho) - 공지 사항 등록
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addNoticeInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		String docId = "";

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00100.addNoticeInfo method start");
		}

		// 공지사항 내역 맵 객체로  전환
		IRecordSet rsDetail = requestData.getRecordSet("detail");
		Map detailMap = rsDetail.getRecordMap(0);

		// 2. 첨부 파일 처리
		IRecordSet rsFile = requestData.getRecordSet("nc_fileDs");
		if (rsFile.getRecordCount() > 0) {
			addAttachFiles(requestData, onlineCtx);

			docId = rsFile.get(0, "DOC_ID"); // 최초 등록시 첨부 파일 정보에서 DOC_ID
			detailMap.put("DOC_ID", docId); // 
		}

		// 3. CRUD 처리

		// 3-1) 공지사항 번호 취득
		IRecordSet noticeNumRs = queryForRecordSet("ADMNTC00100.getNoticeNo",
				requestData.getFieldMap(), onlineCtx);
		String noticNum = noticeNumRs.get(0, "notice_num");
		detailMap.put("NOTICE_NUM", noticNum);

		log.debug("ADMNTC00100.detailMap!!!!!!!!!!!!!!!!!!!!!!!!!!1"+detailMap);
		
		// 3-2) 공지사항 내역 등록
		insert("ADMNTC00100.addNoticeInfo", detailMap, onlineCtx);

		// 3-3) 공지대상 타겟 등록

		// 3-3-1) 사용자 그룹별 공지 대상 등록
		IRecordSet rsTargetUser = null;
		if(detailMap.get("NOTICE_TYPE").equals("3")){
			rsTargetUser = queryForRecordSet("ADMNTC00100.getEffUserList",
					requestData.getFieldMap(), onlineCtx);
			
		}else{
			rsTargetUser = requestData.getRecordSet("target");
		}
		
		int targetCount = rsTargetUser.getRecordCount();
		if (targetCount > 0) {
			Map targetMap = null;
			for (int i = 0; i < targetCount; i++) {
				targetMap = rsTargetUser.getRecordMap(i);
				targetMap.put("NOTICE_NUM", noticNum);
				if ("1".compareTo(targetMap.get("cl").toString()) == 0) {
					targetMap.put("grp", "");
				}
				insert("ADMNTC00100.addNoticeTarget", targetMap, onlineCtx);
			}
		}

		// 4. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "1" });

	}

	/**
	 * 
	 * 
	 * @author 최승호 (choiseungho) - 공지사항 번호 취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getNoticeNo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00100.getNoticeNo method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("ADMNTC00100.getNoticeNo",
				requestData.getFieldMap(), onlineCtx);
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
	 * @author 최승호 (choiseungho) - 첨부파일등록
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addAttachFiles(IDataSet requestData,
			IOnlineContext onlineCtx) throws BizRuntimeException {

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
			throw new BizRuntimeException(BaseConstants.UPLOAD_FAIL);
		}
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 영업그룹별 공지대상 취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getNoticeUserFromDeal(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00100.getNoticeUserFromDeal method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("ADMNTC00100.getNoticeUserFromDeal",
				requestData.getFieldMap(), onlineCtx);
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
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getNoticeDetail(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00100.getNoticeDetail method start");
		}

		// 2. CRUD 처리

		// 2-1) 공지사항 내용 취득
		IRecordSet rsDetail = queryForRecordSet("ADMNTC00100.getNoticeDetail",
				requestData.getFieldMap(), onlineCtx);
		Map mapDetail = rsDetail.getRecordMap(0);
		String docId = (String) mapDetail.get("DOC_ID");

		// 2-2) 공지사항 대상 취득
		IRecordSet rsTarget = queryForRecordSet("ADMNTC00100.getNoticeTarget",
				requestData.getFieldMap(), onlineCtx);

		// 2-3) 첨부파일 취득
		IRecordSet rsAttatch = null;
		if (docId != null) {
			requestData.putField("DOC_ID", docId);
			IPsFileUploadManager fileManager = (IPsFileUploadManager) ComponentRegistry
					.lookup(ServiceConstants.FILEUPLOAD);
			rsAttatch = fileManager.getFileInfoList2Rs(requestData, onlineCtx);
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsDetail.getRecordCount()) });

		responseData.putRecordSet("detail", rsDetail);
		responseData.putRecordSet("target", rsTarget);
		if (rsAttatch != null) {
			responseData.putRecordSet("attatch", rsAttatch);
		}
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 공지사항 수정
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateNoticeInfo(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00100.updateNoticeInfo method start");
		}

		// 공지사항 내역 맵 객체로  전환
		IRecordSet rsDetail = requestData.getRecordSet("detail");

		String noticeNum = requestData.getField("notice_num");

		// 3. 첨부 파일 처리
		addAttachFiles(requestData, onlineCtx);

		// 2. CRUD 처리

		// 2-2) 공지사항 내역 등록
		int updatCount = 0;
		Map detailMap = null;
		if (rsDetail.getRecordCount() > 0) {
			detailMap = rsDetail.getRecordMap(0);
			updatCount = update("ADMNTC00100.updateNoticeInfo", detailMap,
					onlineCtx);
		}
		// 2-3) 공지대상 타겟 등록

		// 2-3-1) 사용자 그룹별 공지 대상 등록*/
		IRecordSet rsTargetUser = requestData.getRecordSet("target");
		int insertCount = 0;
		int deleteCount = 0;
		if (rsTargetUser != null && rsTargetUser.getRecordCount() > 0) {
			Map targetMap = null;
			for (int i = 0, n = rsTargetUser.getRecordCount(); i < n; i++) {
				targetMap = rsTargetUser.getRecordMap(i);
				targetMap.put("NOTICE_NUM", noticeNum);

				if (DELETE_FLAG.equalsIgnoreCase(rsTargetUser.getRecord(i).get(
						CUD_FLAG_PARAM))) {

					deleteCount += delete("ADMNTC00100.deleteNoticeTarget",
							targetMap, onlineCtx);
				} else if (INSERT_FLAG.equalsIgnoreCase(rsTargetUser.getRecord(
						i).get(CUD_FLAG_PARAM))) {

					if ("1".compareTo(targetMap.get("cl").toString()) == 0) {
						targetMap.put("grp", "");
					}
					insert("ADMNTC00100.addNoticeTarget", targetMap, onlineCtx);
					insertCount++;
				}
			}
		}

		// 4. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
						+ updatCount });
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 공지사항 읽음 여부 반영
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateReadInfo(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00100.updateReadInfo method start");
		}

		// 2. CRUD 처리
		int updatCount = update("ADMNTC00100.updateReadInfo", requestData
				.getFieldMap(), onlineCtx);

		// 3. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
						+ updatCount });
	}

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 *  - 영업담당 조회하기.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getSalP(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00100.getSalP method start");
		}
		
		IRecordSet rs = queryForRecordSet("ADMNTC00100.getSalP", requestData
				.getFieldMap(), onlineCtx);

		IRecordSet rsOrg = queryForRecordSet("ADMNTC00100.getOrgNm",
				requestData.getFieldMap(), onlineCtx);

		IRecordSet rsDealNm = queryForRecordSet("ADMNTC00100.getDealCoNm",
				requestData.getFieldMap(), onlineCtx);

		IRecordSet rsGroup = queryForRecordSet("ADMNTC00100.getMyNoticeGroup",
				requestData.getFieldMap(), onlineCtx);
		
		IRecordSet rsUser = queryForRecordSet("ADMNTC00100.getMblPhonList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_saleP");
		}
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_saleP", rs);
		responseData.putRecordSet("ds_orgNm", rsOrg);
		responseData.putRecordSet("ds_dealNm", rsDealNm);
		responseData.putRecordSet("ds_myGroup", rsGroup);
		responseData.putRecordSet("ds_user", rsUser);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 *  - 공지사항을 삭제한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteNotice(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00100.deleteNotice method start");
		}

		// 2. CRUD 처리
		update("ADMNTC00100.deleteNotice", requestData.getFieldMap(), onlineCtx);

		// 2. CRUD 처리
		update("ADMNTC00100.deleteNoticeTargetAll", requestData.getFieldMap(),
				onlineCtx);

		// 3. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.DELETE_OK_SIMPLE_MESSAGE_ID, new String[] { "" });
	}

	
}