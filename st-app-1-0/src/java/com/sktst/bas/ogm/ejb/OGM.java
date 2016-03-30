/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.ogm.ejb;

import java.rmi.RemoteException;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.exception.BaseException;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-19 13:25:28</li>
 * </ul>
 *
 * @author 이종혁 (leejonghyuk)
 */
public interface OGM extends javax.ejb.EJBObject {

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
	IDataSet getOrgMgmtList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getPartSearch(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getHqSearch(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getTeamSearch(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getSupOrgList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getDelSearch(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet saveOrg(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getBizCl(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getOrgTree(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

}