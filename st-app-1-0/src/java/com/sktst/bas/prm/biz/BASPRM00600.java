/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.prm.biz;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.common.user.PsLoginUserInfo;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-07-08 11:02:22</li>
 * </ul>
 *
 * @author 한병곤 (hanbyunggon)
 */
public class BASPRM00600 extends BaseBizUnit {

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
	public IDataSet getDealCoHistList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPRM00600.getDealCoHistList method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("BASPRM00600.getDealCoHistList",
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
	 * @author 한병곤 (hanbyunggon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getDealCoHistByCd(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPRM00600.getDealCoHistByCd method start");
		}
		
		IRecordSet rs = null;
		IRecordSet rs1 = null;
		
		String sDealCoCd = requestData.getField("deal_co_cd");	
		
		if(sDealCoCd == null || sDealCoCd.equals("")){
			// 거래처 코드가 없는 경우 로그인 사용자의 근무지로 셋팅한다.
			PsLoginUserInfo userInfo = (PsLoginUserInfo) onlineCtx.getUserInfo();
			String sOrgAreaId = userInfo.getOrgAreaId();
			requestData.putField("deal_co_cd", sOrgAreaId);
			
			if(sOrgAreaId == null || sOrgAreaId.equals("")){
				// 근무지가 없는 경우.
				// 로그인 사용자의 근무지가 없습니다. 판매점 정보는 로그인 사용자의 근무지에 대한 정보입니다.
				throw new BizRuntimeException("PSMW3007");
			}
			
			rs = queryForRecordSet("BASPRM00600.getDealCoInfo",
					requestData.getFieldMap(), onlineCtx);
			if (rs == null) {
				throw new NoRecordAffectedException(
						BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
			}			
			
		}else{

			// 2. CRUD 처리
			// (기존 정보 조회)
			rs = queryForRecordSet("BASPRM00600.getDealCoHistByCd",
					requestData.getFieldMap(), onlineCtx);
			if (rs == null) {
				throw new NoRecordAffectedException(
						BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
			}
			
			// (이전 정보 조회)
			String sHseq = requestData.getField("hseq_no");
			String sFirst = "1";
			
			if(!sHseq.equals(sFirst)){
				
				requestData.putField("hseq_no", String.valueOf((Integer.parseInt(sHseq)-1)));
				
				rs1 = queryForRecordSet("BASPRM00600.getDealCoHistByCd",
						requestData.getFieldMap(), onlineCtx);
			}
		}
		
		if (rs1 == null) {
			rs1 = new RecordSet("ds_detail_pre");
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("output", rs);
		responseData.putRecordSet("ds_detail_pre", rs1);
		return responseData;
	}

}