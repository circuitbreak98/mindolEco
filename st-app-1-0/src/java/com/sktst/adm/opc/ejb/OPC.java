/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.opc.ejb;

import java.rmi.RemoteException;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.exception.BaseException;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-06-04 13:30:35</li>
 * </ul>
 *
 * @author admin (admin)
 */
public interface OPC extends javax.ejb.EJBObject {

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
	IDataSet getCloseProperties(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet updateCloseProperties(IDataSet requestData,
			IOnlineContext onlineCtx) throws RemoteException;

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
	IDataSet refreshCloseProperties(IDataSet requestData,
			IOnlineContext onlineCtx) throws RemoteException;

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
	IDataSet refreshConfigAll(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

}