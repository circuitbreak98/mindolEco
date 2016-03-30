/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.prm.ejb;

import java.rmi.RemoteException;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-19 10:55:59</li>
 * </ul>
 *
 * @author 전현주 (junhyunjoo)
 */
public interface PRM extends javax.ejb.EJBObject {

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
	IDataSet getDeliverFee(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet saveDeliverFee(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getAccList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet saveAcc(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getDealCoList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet updateDealCo(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet addDealCo(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getDealCoCd(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getOrg(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getDealCoByCd(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet checkDuplication(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getUserInfo(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getDealIfList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getSalEmpList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getExcelDeal(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getDealList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet updateDealList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getDealCoHistList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getDealCoHistByCd(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getDealIncreList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getChgrgUserWeeklyDeal(IDataSet requestData,
			IOnlineContext onlineCtx) throws RemoteException;

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
	IDataSet addExcelExportHst(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

}