/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.ogm.ejb;

import nexcore.framework.core.biz.AbsBizSlsBean;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.exception.BaseException;
import com.sktst.bas.ogm.biz.BASOGM00100;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-19 13:25:28</li>
 * </ul>
 *
 * @author 이종혁 (leejonghyuk)
 */
public class OGMBean extends AbsBizSlsBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; // FIXME

	/**
	 * @see nexcore.framework.core.biz.AbsBizSlsBean#getFqId()
	 */
	@Override
	public String getFqId() {
		return "sktst.bas.OGM";
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
	public IDataSet getOrgMgmtList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASOGM00100 bizUnit = (BASOGM00100) lookupBizUnit(BASOGM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getOrgMgmtList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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

		// Step1. 업무 컴포넌트 룩업
		BASOGM00100 bizUnit = (BASOGM00100) lookupBizUnit(BASOGM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getPartSearch(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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

		// Step1. 업무 컴포넌트 룩업
		BASOGM00100 bizUnit = (BASOGM00100) lookupBizUnit(BASOGM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getHqSearch(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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

		// Step1. 업무 컴포넌트 룩업
		BASOGM00100 bizUnit = (BASOGM00100) lookupBizUnit(BASOGM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getTeamSearch(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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

		// Step1. 업무 컴포넌트 룩업
		BASOGM00100 bizUnit = (BASOGM00100) lookupBizUnit(BASOGM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getSupOrgList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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

		// Step1. 업무 컴포넌트 룩업
		BASOGM00100 bizUnit = (BASOGM00100) lookupBizUnit(BASOGM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getDelSearch(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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

		// Step1. 업무 컴포넌트 룩업
		BASOGM00100 bizUnit = (BASOGM00100) lookupBizUnit(BASOGM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveOrg(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getBizCl(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASOGM00100 bizUnit = (BASOGM00100) lookupBizUnit(BASOGM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getBizCl(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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

		// Step1. 업무 컴포넌트 룩업
		BASOGM00100 bizUnit = (BASOGM00100) lookupBizUnit(BASOGM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getOrgTree(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

}