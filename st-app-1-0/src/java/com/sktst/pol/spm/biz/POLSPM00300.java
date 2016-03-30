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

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/정책</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-10-21 13:34:35</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class POLSPM00300 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveSplstMdlChg(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveSplstMdlChg method start");
		}
		// 로컬 변수 선언
		int iSplstCnt = 0;
		int iPreferCnt = 0;
		int iInsertCnt = 1;

		Map mSeq = null; // 가격표 ID 생성
		Map mCondition = null; // 조건
		Map mSplst = null; // 판매가격표

		// 사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		// 판매가격표 정보
		IRecordSet rsCondition = requestData.getRecordSet("ds_condition"); // 조건
		IRecordSet rsSplst = requestData.getRecordSet("ds_splst"); // 판매가격표

		// 변경 판매가격표 Mdl 생성(copy) 이전 판매가격표 기준
		// new 가격표 ID 얻기.
		mSeq = new HashMap();
		mSeq = (Map) queryForObject("POLSPM00200.getSequenceNo", mSeq,
				onlineCtx);

		String sSplstId = ""; //가격표ID
		sSplstId = (String) mSeq.get("SPLST_ID");

		if (sSplstId == null || sSplstId.equals("")) {
			throw new BizRuntimeException(
					"가격표ID 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}

		// 변경 판매가격표 Master 생성(copy) 이전 판매가격표 master기준
		mCondition = rsCondition.getRecordMap(0);
		mSplst = rsSplst.getRecordMap(0);

		mSplst.put("new_splst_id", sSplstId);

		mSplst.put("aply_mod_dtm", mCondition.get("aply_mod_dtm").toString()
				+ mCondition.get("mod_hour").toString()
				+ mCondition.get("mod_min").toString());

		insert("POLSPM00300.insertSplstChg", mSplst, onlineCtx);

		// 변경 판매가격표 Prefer 생성(copy) 이전 판매가격표 Prefer기준
		insert("POLSPM00300.insertSplstMdlChg", mSplst, onlineCtx);

		// 이전 판매가격표 Master 종료처리 (APLY_END_DTM 변경) 
		update("POLSPM00300.updateObjSplstClose", mSplst, onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { ""
						+ iInsertCnt });

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateSplstClose(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveSplstClose method start");
		}
		// 로컬 변수 선언
		int iUpdateCnt = 1;

		Map mCondition = null; // 조건
		Map mSplst = null; // 판매가격표

		// 사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		// 판매가격표 정보
		IRecordSet rsCondition = requestData.getRecordSet("ds_condition"); // 조건
		IRecordSet rsSplst = requestData.getRecordSet("ds_splst"); // 판매가격표

		// 변경 판매가격표 Master 생성(copy) 이전 판매가격표 master기준
		mCondition = rsCondition.getRecordMap(0);
		mSplst = rsSplst.getRecordMap(0);

		mSplst.put("aply_mod_dtm", mCondition.get("aply_mod_dtm").toString()
				+ mCondition.get("mod_hour").toString()
				+ mCondition.get("mod_min").toString());

		// 이전 판매가격표 Master 종료처리 (APLY_END_DTM 변경) 
		update("POLSPM00300.updateSplstClose", mSplst, onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
						+ iUpdateCnt });

		return responseData;
	}

}