/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.aum.biz;

import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : 권한 관리 </li>
 * <li>작성일 : 2009-02-02 10:24:39</li>
 * </ul>
 *
 * @author 심연정 (shimyunjung)
 */
public class ADMAUM00100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * ==>  권한 목록을 가져온다. 
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getAuthList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMAUM00100.getAuth start >>>>>>>");
		}

		IRecordSet rs = queryForRecordSet("ADMAUM00100.getAuth", requestData
				.getFieldMap(), onlineCtx);
		 
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_Auth", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * ==>  권한 수정 
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveAuth(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMAUM00100.saveAuth start >>>>>>>");
		}
 	
		IRecordSet rs = requestData.getRecordSet("ds_Auth");
		String userGrp = requestData.getField("user_grp");

		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				rs.getRecord(i).set("user_grp", userGrp);
				String sSearch = rs.getRecord(i).get("SEARCH_AUTH_YN").toString();
				
				if ("0".equals(sSearch)) {
					delete("ADMAUM00100.deleteAuth", rs.getRecord(i), onlineCtx);
					
				} else {
					insert("ADMAUM00100.saveAuth", rs.getRecord(i), onlineCtx);
					
				}
			 
			}
		}
 
		IRecordSet result = queryForRecordSet("ADMAUM00100.getAuth",
				requestData.getFieldMap(), onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_Auth", result);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 메뉴에 따른 사용자 그룹 권한 취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getAuthFromMenu(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMAUM00100.getAuthFromMenu method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("ADMAUM00100.getAuthFromMenu",
				requestData.getFieldMap(), onlineCtx);

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
	 * @author 심연정 (shimyunjung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getUserGrp(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMAUM00100.getUserGrp start >>>>>>>");
		}

		IRecordSet rsNot = queryForRecordSet("ADMAUM00100.getNotGrntUserGrp", requestData
				.getFieldMap(), onlineCtx);
		IRecordSet rs = queryForRecordSet("ADMAUM00100.getGrntUserGrp", requestData
				.getFieldMap(), onlineCtx);
		//System.err.println(rsNot);
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_NotGrntUserGrp", rsNot);
		responseData.putRecordSet("ds_grntUserGrp",    rs);
		return responseData;
	}

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
	public IDataSet saveAuthSync(IDataSet requestData, IOnlineContext onlineCtx) {
 
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMAUM00100.saveAuthSync start >>>>>>>");
		}
		
		Map dsCnd = null;
		dsCnd = requestData.getRecordSet("ds_conditionSync").getRecordMap(0);
		
		dsCnd.put("USER_GRP", dsCnd.get("USER_GRP_BF").toString());
	 
		IRecordSet rsAuth= queryForRecordSet("ADMAUM00100.getAuth", dsCnd, onlineCtx);

		if (rsAuth != null) {
			for (int i = 0; i < rsAuth.getRecordCount(); i++) {
				rsAuth.getRecord(i).set("USER_GRP", dsCnd.get("USER_GRP_AF").toString());
				
				String sSearch = rsAuth.getRecord(i).get("SEARCH_AUTH_YN").toString();
				
				
				if ("0".equals(sSearch)) {
					delete("ADMAUM00100.deleteAuth", rsAuth.getRecord(i), onlineCtx);
					
				} else {
					insert("ADMAUM00100.addAuth", rsAuth.getRecord(i), onlineCtx);
					
				}
			}
		}
 
		
		
		
		dsCnd.put("USER_GRP", dsCnd.get("USER_GRP_AF").toString());
		
		IRecordSet rsSyncAF = queryForRecordSet("ADMAUM00100.getAuth",
				dsCnd, onlineCtx);
		 
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsSyncAF.getRecordCount()) });
		responseData.putRecordSet("ds_Auth", rsSyncAF);
		return responseData;
		 
	}

}