/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.ntc.biz;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.user.IUserInfo;
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
 * <li>설 명 : 공지사항 조회</li>
 * <li>작성일 : 2009-03-12 10:10:13</li>
 * </ul>
 *
 * @author 최승호 (choiseungho)
 */
public class ADMNTC00200 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 공지사항 목록 취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getNoticeList(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00200.getNoticeList method start");
		}
		
		IUserInfo iu = onlineCtx.getUserInfo();		
		String sUserGrp = String.valueOf(iu.get("userGrp"));
		String sAllRead = "N";
		String admin = "P12"; // 본사 admin
		String extrAdmin = "P17"; // 본사 부admin
		String superAdmin = "ADM"; // 슈퍼 adm
		
		// 본사admin (P12), 본사 부 admin(P17)은 모든 게시물 조회 가능 하도록 셋팅.
		if(sUserGrp != null && (sUserGrp.equals(admin) || sUserGrp.equals(extrAdmin) || sUserGrp.equals(superAdmin) )){
			sAllRead = "Y";
		}
		
		requestData.putField("allRead", sAllRead);
		
		// 2. CRUD 처리
		System.out.println("ADMNTC00200.getNoticeList======================="+requestData.getFieldMap());
		IRecordSet rs = queryForRecordSet("ADMNTC00200.getNoticeList",
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
	 * 			- 첨부파일 정보 취
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getAttatchList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00200.getAttatchList method start");
		}

		// 2. 파일 내역 조회
		IPsFileUploadManager fileManager = (IPsFileUploadManager) ComponentRegistry
				.lookup(ServiceConstants.FILEUPLOAD);

		System.out.println("requestData=================="+requestData);
		// 3. 결과 지정
		return fileManager.getFileInfoList("nc_fileDs", requestData, onlineCtx);
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 열람수 업데이트
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateCount(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00200.updateCount method start");
		}

		// 2. CRUD 처리
		int nResult = update("ADMNTC00200.updateCount", requestData
				.getFieldMap(), onlineCtx);

		// 3. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_SIMPLE_MESSAGE_ID,
				new String[] { "" + nResult });

	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 공지사항 팝업 정보 취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getNoticePopInfo(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00200.getNoticePopInfo method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("ADMNTC00200.getNoticePopInfo",
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

}