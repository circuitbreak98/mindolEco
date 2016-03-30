/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.ntc.biz;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : 메인 페이지의 정보를 관리한다.</li>
 * <li>작성일 : 2009-05-11 15:04:18</li>
 * </ul>
 *
 * @author 한병곤 (hanbyunggon)
 */
public class ADMNTC00400 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 *  - 공지사항 q/a를 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getMainInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00400.getMainInfo method start");
		}
		
		// 공지사항 List
		IRecordSet rsBoard = queryForRecordSet("ADMNTC00400.getMainBoardList",
				requestData.getFieldMap(), onlineCtx);

		if (rsBoard == null) {
			rsBoard = new RecordSet("ds_board");
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
		
		// 업무지원 공지사항 List
		requestData.putField("notice_type", "1"); // 업무지원
		IRecordSet rsOpNotics = queryForRecordSet("ADMNTC00400.getMainNoticsList",
				requestData.getFieldMap(), onlineCtx);

		if (rsOpNotics == null) {
			rsOpNotics = new RecordSet("ds_op_notics");
		}
		
		// 영업정책 공지사항 List
		requestData.putField("notice_type", "2"); // 영업정책
		
		IRecordSet rsSpNotics = queryForRecordSet("ADMNTC00400.getMainNoticsList",
				requestData.getFieldMap(), onlineCtx);

		if (rsSpNotics == null) {
			rsSpNotics = new RecordSet("ds_sp_notics");
		}		

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_SIMPLE_MESSAGE_ID, new String[] { String
						.valueOf(rsBoard.getRecordCount()) });
		responseData.putRecordSet("ds_board", rsBoard);
		responseData.putRecordSet("ds_op_notics", rsOpNotics);
		responseData.putRecordSet("ds_sp_notics", rsSpNotics);

		return responseData;
	}

}