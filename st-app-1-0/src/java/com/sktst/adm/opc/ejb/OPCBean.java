/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.opc.ejb;

import nexcore.framework.core.biz.AbsBizSlsBean;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.exception.BaseException;
import com.sktst.adm.opc.biz.ADMOPC00100;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-06-04 13:30:35</li>
 * </ul>
 *
 * @author admin (admin)
 */
public class OPCBean extends AbsBizSlsBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; // FIXME

	/**
	 * @see nexcore.framework.core.biz.AbsBizSlsBean#getFqId()
	 */
	@Override
	public String getFqId() {
		return "sktst.adm.OPC";
	}

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : defCloseFromTime [defCloseFromTime] 
	 *	- field : defCloseToTime [defCloseToTime] 
	 *	- field : closeFromDate [closeFromDate] 
	 *	- field : closeToDate [closeToDate] 
	 */
	public IDataSet getCloseProperties(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMOPC00100 bizUnit = (ADMOPC00100) lookupBizUnit(ADMOPC00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getCloseProperties(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : defCloseFromTime [defCloseFromTime] 
	 *	- field : defCloseToTime [defCloseToTime] 
	 *	- field : closeFromDate [closeFromDate] 
	 *	- field : closeToDate [closeToDate] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateCloseProperties(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMOPC00100 bizUnit = (ADMOPC00100) lookupBizUnit(ADMOPC00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.updateCloseProperties(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet refreshCloseProperties(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMOPC00100 bizUnit = (ADMOPC00100) lookupBizUnit(ADMOPC00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.refreshCloseProperties(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet refreshConfigAll(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		ADMOPC00100 bizUnit = (ADMOPC00100) lookupBizUnit(ADMOPC00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.refreshConfigAll(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

}