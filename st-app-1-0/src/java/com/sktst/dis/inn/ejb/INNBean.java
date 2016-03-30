/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.inn.ejb;

import nexcore.framework.core.biz.AbsBizSlsBean;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.exception.BaseException;
import com.sktst.dis.inn.biz.DISINN00100;
import com.sktst.dis.inn.biz.DISINN01000;
import com.sktst.dis.inn.biz.DISINN01100;
import com.sktst.dis.inn.biz.DISINN01200;
import com.sktst.dis.inn.biz.DISINN00300;
import com.sktst.dis.inn.biz.DISINN00400;
import com.sktst.dis.inn.biz.DISINN00200;
import com.sktst.dis.inn.biz.DISINN00500;
import com.sktst.dis.inn.biz.DISINN00600;
import com.sktst.dis.inn.biz.DISINN01300;
import com.sktst.dis.inn.biz.DISINN01400;
import com.sktst.dis.inn.biz.DISINN01500;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-19 09:45:48</li>
 * </ul>
 * 
 * @author 전미량 (jeonmiryang)
 */
public class INNBean extends AbsBizSlsBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; // FIXME

	/**
	 * @see nexcore.framework.core.biz.AbsBizSlsBean#getFqId()
	 */
	@Override
	public String getFqId() {
		return "sktst.dis.INN";
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getOrdList(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00100 bizUnit = (DISINN00100) lookupBizUnit(DISINN00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getOrdList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getCpntDisList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01000 bizUnit = (DISINN01000) lookupBizUnit(DISINN01000.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getCpntDisList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getPrchsInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01100 bizUnit = (DISINN01100) lookupBizUnit(DISINN01100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getPrchsInfo(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getCpntDisDtl(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01100 bizUnit = (DISINN01100) lookupBizUnit(DISINN01100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getCpntDisDtl(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getProdFixObjList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01200 bizUnit = (DISINN01200) lookupBizUnit(DISINN01200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getProdFixObjList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet saveProdFixList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01200 bizUnit = (DISINN01200) lookupBizUnit(DISINN01200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveProdFixList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet saveCpntDis(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01100 bizUnit = (DISINN01100) lookupBizUnit(DISINN01100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveCpntDis(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getInnSchdList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00300 bizUnit = (DISINN00300) lookupBizUnit(DISINN00300.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getInnSchdList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getOrd(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00400 bizUnit = (DISINN00400) lookupBizUnit(DISINN00400.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getOrd(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet deleteInnSchd(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00400 bizUnit = (DISINN00400) lookupBizUnit(DISINN00400.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.deleteInnSchd(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getInnSchdDtlList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00400 bizUnit = (DISINN00400) lookupBizUnit(DISINN00400.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getInnSchdDtlList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet saveInnSchd(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00400 bizUnit = (DISINN00400) lookupBizUnit(DISINN00400.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveInnSchd(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getOrdMgmtNoList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00100 bizUnit = (DISINN00100) lookupBizUnit(DISINN00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getOrdMgmtNoList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getUnitPrc(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00200 bizUnit = (DISINN00200) lookupBizUnit(DISINN00200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getUnitPrc(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getProdColorList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00200 bizUnit = (DISINN00200) lookupBizUnit(DISINN00200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getProdColorList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getOrdDtlList(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00200 bizUnit = (DISINN00200) lookupBizUnit(DISINN00200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getOrdDtlList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getInnList(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00500 bizUnit = (DISINN00500) lookupBizUnit(DISINN00500.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getInnList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getInnFixDtlList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00600 bizUnit = (DISINN00600) lookupBizUnit(DISINN00600.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getInnFixDtlList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet saveInnFix(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00600 bizUnit = (DISINN00600) lookupBizUnit(DISINN00600.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveInnFix(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet savePrchsProdFixList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01200 bizUnit = (DISINN01200) lookupBizUnit(DISINN01200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.savePrchsProdFixList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet saveOrd(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00200 bizUnit = (DISINN00200) lookupBizUnit(DISINN00200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveOrd(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet deleteOrd(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00200 bizUnit = (DISINN00200) lookupBizUnit(DISINN00200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.deleteOrd(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getPrchsMgmtNoList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00100 bizUnit = (DISINN00100) lookupBizUnit(DISINN00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getPrchsMgmtNoList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getEtcDisList(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01100 bizUnit = (DISINN01100) lookupBizUnit(DISINN01100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getEtcDisList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet getPrchsOut(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00400 bizUnit = (DISINN00400) lookupBizUnit(DISINN00400.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getPrchsOut(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet saveInn(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00400 bizUnit = (DISINN00400) lookupBizUnit(DISINN00400.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveInn(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet saveInnCncl(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN00400 bizUnit = (DISINN00400) lookupBizUnit(DISINN00400.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveInnCncl(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
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
	public IDataSet saveProdRegCncl(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01200 bizUnit = (DISINN01200) lookupBizUnit(DISINN01200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveProdRegCncl(requestData, onlineCtx);

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
	public IDataSet saveCpntDisAll(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01000 bizUnit = (DISINN01000) lookupBizUnit(DISINN01000.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveCpntDisAll(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getCpntDisBoxList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01000 bizUnit = (DISINN01000) lookupBizUnit(DISINN01000.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getCpntDisBoxList(requestData, onlineCtx);

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
	public IDataSet saveCpntDisAll2(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01000 bizUnit = (DISINN01000) lookupBizUnit(DISINN01000.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveCpntDisAll2(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdDisList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01300 bizUnit = (DISINN01300) lookupBizUnit(DISINN01300.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getProdDisList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveProdCdChg(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01400 bizUnit = (DISINN01400) lookupBizUnit(DISINN01400.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveProdCdChg(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdDisListCount(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01300 bizUnit = (DISINN01300) lookupBizUnit(DISINN01300.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getProdDisListCount(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdDisHst(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		DISINN01500 bizUnit = (DISINN01500) lookupBizUnit(DISINN01500.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getProdDisHst(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

}
