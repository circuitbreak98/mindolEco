/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.mnu.ejb;

import nexcore.framework.core.biz.AbsBizSlsBean;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.exception.BaseException;
import com.sktst.adm.mnu.biz.ADMMNU00100;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-29 09:55:50</li>
 * </ul>
 *
 * @author 심연정 (shimyunjung)
 */
public class MNUBean extends AbsBizSlsBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; // FIXME

	/**
	 * @see nexcore.framework.core.biz.AbsBizSlsBean#getFqId()
	 */
	@Override
	public String getFqId() {
		return "sktst.adm.MNU";
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
	public IDataSet getMenuList(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMMNU00100 bizUnit = (ADMMNU00100) lookupBizUnit(ADMMNU00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getMenuList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getMenuNum(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMMNU00100 bizUnit = (ADMMNU00100) lookupBizUnit(ADMMNU00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getMenuNum(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet saveMenuList(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMMNU00100 bizUnit = (ADMMNU00100) lookupBizUnit(ADMMNU00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveMenuList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getSupMenuNum(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMMNU00100 bizUnit = (ADMMNU00100) lookupBizUnit(ADMMNU00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getSupMenuNum(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet checkChild(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMMNU00100 bizUnit = (ADMMNU00100) lookupBizUnit(ADMMNU00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.checkChild(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet saveMenuAuth(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMMNU00100 bizUnit = (ADMMNU00100) lookupBizUnit(ADMMNU00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveMenuAuth(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet saveMyMenu(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMMNU00100 bizUnit = (ADMMNU00100) lookupBizUnit(ADMMNU00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveMyMenu(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getMyMenu(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMMNU00100 bizUnit = (ADMMNU00100) lookupBizUnit(ADMMNU00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getMyMenu(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet saveFirstMenu(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMMNU00100 bizUnit = (ADMMNU00100) lookupBizUnit(ADMMNU00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveFirstMenu(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getLoginMenuAuthInfo(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMMNU00100 bizUnit = (ADMMNU00100) lookupBizUnit(ADMMNU00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getLoginMenuAuthInfo(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

}