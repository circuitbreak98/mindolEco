/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.ogm.biz;

import java.util.Map;

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

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보관리</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-15 14:11:23</li>
 * </ul>
 *
 * @author 이종혁 (leejonghyuk)
 */
public class BASOGM00100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 이종혁 (leejonghyuk)
	 * 조직구분 조회 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : ORG_CL [필드1] 
	 *	- field : SUP_ORG [필드2] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : ORG_ID [필드1] 
	 *	- field : USER_ID [필드2] 
	 *	- field : USER_NM [필드3] 
	 *	- field : ORG_NM [필드4] 
	 *	- field : ORG_CL [필드5] 
	 *	- field : ORG_CL_NM [필드6] 
	 *	- field : SUP_ORG [필드7] 
	 *	- field : SUP_ORG_NM [필드8] 
	 *	- field : U_KEY_CD [필드9] 
	 *	- field : EFF_ORG_YN [필드10] 
	 *	- field : MBL_PHON [필드11] 
	 *	- field : ZIP_CD1 [필드12] 
	 *	- field : ZIP_CD2 [필드13] 
	 *	- field : OTH_ADDR [필드14] 
	 *	- field : RMKS [필드15] 
	 *	- field : DEL_YN [필드16] 
	 *	- field : UPD_CNT [필드17] 
	 */
	public IDataSet getOrgMgmtList(IDataSet requestData,
			IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getOrgMgmtList method start");
		}
		IRecordSet rs = queryForRecordSet("BASOGM00100.getOrgMgmtList",
				requestData.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("orgList");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("orgList", rs);
		return responseData;

	}

	/**
	 * 
	 *
	 * @author 이종혁 (leejonghyuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : org_cl [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : code [필드1] 
	 *	- field : org_cl_nm [필드2] 
	 *	- field : org_id [필드3] 
	 */
	public IDataSet getPartSearch(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getPartSearch method start");
		}

		IRecordSet rs = queryForRecordSet("BASOGM00100.getPartSearch",
				requestData.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("loadOrg");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("orgList", rs);
		return responseData;

	}

	/**
	 * 
	 *
	 * @author 이종혁 (leejonghyuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : org_cl [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : id [필드1] 
	 *	- field : value [필드2] 
	 */
	public IDataSet getHqSearch(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getHqSearch method start");
		}

		IRecordSet rs = queryForRecordSet("BASOGM00100.getHqSearch",
				requestData.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("svcSelOrg");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("orgList", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이종혁 (leejonghyuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : org_id [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : code [필드1] 
	 *	- field : value [필드2] 
	 *	- field : id [필드3] 
	 */
	public IDataSet getTeamSearch(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getTeamSearch method start");
		}

		IRecordSet rs = queryForRecordSet("BASOGM00100.getTeamSearch",
				requestData.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("svcSelOrg1");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("orgList", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이종혁 (leejonghyuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : ORG_CL [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getSupOrgList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getSupOrgList method start");
		}

		IRecordSet rs = queryForRecordSet("BASOGM00100.getSupOrgList",
				requestData.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("svcSupOrg");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("orgList", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이종혁 (leejonghyuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : ORG_ID [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : userorg_id [필드1] 
	 *	- field : dealorg_id [필드2] 
	 */
	public IDataSet getDelSearch(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getDelSearch method start");
		}

		IRecordSet rs = queryForRecordSet("BASOGM00100.getDelSearch",
				requestData.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("delSearch");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("delorg", rs);
		return responseData;

	}

	/**
	 * 
	 *
	 * @author 이종혁 (leejonghyuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveOrg(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveOrg method start");
		}

		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		IRecordSet rs = requestData.getRecordSet("input");
		IRecordSet rsOrgId = null;//org_id값
		Map org = null;//i번째 row 셋팅 
		String orgId = "";//i번째 row 에서 조직코드(org_id) 셋팅

		if (rs != null) {
			//for (int i = 0; i < rs.getRecordCount(); i++) {
				if (INSERT_FLAG.equalsIgnoreCase(rs.getRecord(0).get(
						CUD_FLAG_PARAM))) {
					//조직코드(org_id)중복체크
					rsOrgId = queryForRecordSet("BASOGM00100.getOrgId", rs
							.getRecord(0));
					
					if(rsOrgId != null && rsOrgId.getRecordCount() > 0)
					{
						org = rsOrgId.getRecordMap(0);
						//System.out.println()
						orgId = (String) org.get("ORG_ID");
					}
					
					if (!"".equals(orgId)&& orgId != null) {
						throw new BizRuntimeException(BaseConstants.CHECK_REG,
								new String[] { "조직코드" });
					}

					insert("BASOGM00100.addOrg", rs.getRecord(0), onlineCtx);
					insertCount++;
				} else if (UPDATE_FLAG.equalsIgnoreCase(rs.getRecord(0).get(
						CUD_FLAG_PARAM))) {

					updateCount = updateCount
							+ update("BASOGM00100.updateOrg", rs.getRecord(0),
									onlineCtx);
				}
			//}
			cudAllCount = insertCount + updateCount;
		}//rs != null end
		if (cudAllCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount,""+0 });
	}

	/**
	 * 
	 *
	 * @author 이종혁 (leejonghyuk)
	 * 조직업무구분을 가져온다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getBizCl(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getBizCl method start");
		}

		IRecordSet rs = queryForRecordSet("BASOGM00100.getBizCl", requestData
				.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("svcBizCl");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("bizCl", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이종혁 (leejonghyuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getOrgTree(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getOrgTree method start");
		}
		IRecordSet rs = queryForRecordSet("BASOGM00100.getOrgTree", requestData
				.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("output");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("output", rs);
		return responseData;
	}

}