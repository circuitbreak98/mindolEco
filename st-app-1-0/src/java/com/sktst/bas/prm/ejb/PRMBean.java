/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.prm.ejb;

import nexcore.framework.core.biz.AbsBizSlsBean;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;

import com.sktst.bas.prm.biz.BASPRM00100;
import com.sktst.bas.prm.biz.BASPRM00200;
import com.sktst.bas.prm.biz.BASPRM00300;
import com.sktst.bas.prm.biz.BASPRM00400;
import com.sktst.bas.prm.biz.BASPRM08100;
import com.sktst.bas.prm.biz.BASPRM00500;
import com.sktst.bas.prm.biz.BASPRM00600;
import com.sktst.bas.prm.biz.BASPRM00800;
import com.sktst.bas.prm.biz.BASPRM00900;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-19 10:55:59</li>
 * </ul>
 *
 * @author 전현주 (junhyunjoo)
 */
public class PRMBean extends AbsBizSlsBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; // FIXME

	/**
	 * @see nexcore.framework.core.biz.AbsBizSlsBean#getFqId()
	 */
	@Override
	public String getFqId() {
		return "sktst.bas.PRM";
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : ST_DT [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : SER_NUM [필드1] 
	 *	- field : DEAL_CO_CD [필드2] 
	 *	- field : DEL_YN [필드3] 
	 *	- field : DLV_TYP [필드4] 
	 *	- field : UNIT [필드5] 
	 *	- field : UNIT_PRC [필드6] 
	 *	- field : STA_DT [필드7] 
	 *	- field : END_DT [필드8] 
	 *	- field : RMKS [필드9] 
	 *	- field : INS_DTM [필드10] 
	 *	- field : INS_USER_ID [필드11] 
	 *	- field : MOD_DTM [필드12] 
	 *	- field : MOD_USER_ID [필드13] 
	 *	- field : UPD_CNT [필드14] 
	 *	- field : MAX_FLAG [필드15] 
	 */
	public IDataSet getDeliverFee(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00300 bizUnit = (BASPRM00300) lookupBizUnit(BASPRM00300.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getDeliverFee(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveDeliverFee(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00300 bizUnit = (BASPRM00300) lookupBizUnit(BASPRM00300.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveDeliverFee(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : DEAL_CO_CD [필드1] 
	 *	- field : ACC_TYP [필드2] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : DEAL_CO_CD [필드1] 
	 *	- field : ACC_TYP [필드2] 
	 *	- field : ACC_MTH [필드3] 
	 *	- field : ACC_DT [필드4] 
	 *	- field : RMKS [필드5] 
	 */
	public IDataSet getAccList(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00400 bizUnit = (BASPRM00400) lookupBizUnit(BASPRM00400.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getAccList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveAcc(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00400 bizUnit = (BASPRM00400) lookupBizUnit(BASPRM00400.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.saveAcc(requestData, onlineCtx);

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
	public IDataSet getDealCoList(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00100 bizUnit = (BASPRM00100) lookupBizUnit(BASPRM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getDealCoList(requestData, onlineCtx);

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
	public IDataSet updateDealCo(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00200 bizUnit = (BASPRM00200) lookupBizUnit(BASPRM00200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.updateDealCo(requestData, onlineCtx);

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
	public IDataSet addDealCo(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00200 bizUnit = (BASPRM00200) lookupBizUnit(BASPRM00200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.addDealCo(requestData, onlineCtx);

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
	public IDataSet getDealCoCd(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00200 bizUnit = (BASPRM00200) lookupBizUnit(BASPRM00200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getDealCoCd(requestData, onlineCtx);

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
	public IDataSet getOrg(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00100 bizUnit = (BASPRM00100) lookupBizUnit(BASPRM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getOrg(requestData, onlineCtx);

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
	 *	- field : deal_co_cd [필드1] 
	 *	- field : hseq_no' [필드2] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getDealCoByCd(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00100 bizUnit = (BASPRM00100) lookupBizUnit(BASPRM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getDealCoByCd(requestData, onlineCtx);

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
	public IDataSet checkDuplication(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00200 bizUnit = (BASPRM00200) lookupBizUnit(BASPRM00200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.checkDuplication(requestData, onlineCtx);

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
	public IDataSet getUserInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00200 bizUnit = (BASPRM00200) lookupBizUnit(BASPRM00200.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getUserInfo(requestData, onlineCtx);

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
	public IDataSet getSalEmpList(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00100 bizUnit = (BASPRM00100) lookupBizUnit(BASPRM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getSalEmpList(requestData, onlineCtx);

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
	public IDataSet getDealIfList(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM08100 bizUnit = (BASPRM08100) lookupBizUnit(BASPRM08100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getDealIfList(requestData, onlineCtx);

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
	public IDataSet getExcelDeal(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00100 bizUnit = (BASPRM00100) lookupBizUnit(BASPRM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getExcelDeal(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getDealList(IDataSet requestData, IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00500 bizUnit = (BASPRM00500) lookupBizUnit(BASPRM00500.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getDealList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateDealList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00500 bizUnit = (BASPRM00500) lookupBizUnit(BASPRM00500.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.updateDealList(requestData, onlineCtx);

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
	public IDataSet getDealCoHistList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00600 bizUnit = (BASPRM00600) lookupBizUnit(BASPRM00600.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getDealCoHistList(requestData, onlineCtx);

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
	public IDataSet getDealCoHistByCd(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00600 bizUnit = (BASPRM00600) lookupBizUnit(BASPRM00600.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getDealCoHistByCd(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getDealIncreList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00800 bizUnit = (BASPRM00800) lookupBizUnit(BASPRM00800.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getDealIncreList(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getChgrgUserWeeklyDeal(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00900 bizUnit = (BASPRM00900) lookupBizUnit(BASPRM00900.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.getChgrgUserWeeklyDeal(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addExcelExportHst(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// Step1. 업무 컴포넌트 룩업
		BASPRM00100 bizUnit = (BASPRM00100) lookupBizUnit(BASPRM00100.class);

		IDataSet output = null;

		try {

			// Step2. 업무로직 호출
			output = bizUnit.addExcelExportHst(requestData, onlineCtx);

		} catch (Throwable t) {
			handleUncheckedException(t, bizUnit, requestData, onlineCtx);
		}

		// Step3. 응답
		return output;
	}

}