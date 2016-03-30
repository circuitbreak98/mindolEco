/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.cst.sta.ejb;

import nexcore.framework.core.biz.AbsBizSlsBean;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.exception.BaseException;
import com.sktst.cst.sta.biz.CSTSTA00200;
import com.sktst.cst.sta.biz.CSTSTA00400;
import com.sktst.cst.sta.biz.CSTSTA00410;
import com.sktst.cst.sta.biz.CSTSTA00500;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-02-20 16:29:54</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class STABean extends AbsBizSlsBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; // FIXME

	/**
	 * @see nexcore.framework.core.biz.AbsBizSlsBean#getFqId()
	 */
	@Override
	public String getFqId() {
		return "sktst.cst.STA";
	}

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getCstSta200(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		CSTSTA00200 bizUnit = (CSTSTA00200) lookupBizUnit(CSTSTA00200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getCstSta200(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdPrcSt(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		CSTSTA00400 bizUnit = (CSTSTA00400) lookupBizUnit(CSTSTA00400.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getProdPrcSt(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdPrcStDetail(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		CSTSTA00410 bizUnit = (CSTSTA00410) lookupBizUnit(CSTSTA00410.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getProdPrcStDetail(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getHqAll(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		CSTSTA00500 bizUnit = (CSTSTA00500) lookupBizUnit(CSTSTA00500.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getHqAll(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

}