/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.psm.ejb;

import java.rmi.RemoteException;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.exception.BaseException;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-08-30 13:37:57</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public interface PSM extends javax.ejb.EJBObject {

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
	IDataSet getProdDisList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getProdDisDtl(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet saveProdSale(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet saveProdSaleList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet saveProdSaleCncl(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

}