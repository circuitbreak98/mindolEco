/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */
 
package com.sktst.adm.qna.ejb;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-29 09:57:42</li>
 * </ul>
 *
 * @author 심연정 (shimyunjung)
 */
public interface QNAHome extends javax.ejb.EJBHome {

	 /**
	  *
	  * @return
	  * @throws javax.ejb.CreateException
	  * @throws java.rmi.RemoteException
	  */
	 public QNA create() throws javax.ejb.CreateException,
	         java.rmi.RemoteException;

}