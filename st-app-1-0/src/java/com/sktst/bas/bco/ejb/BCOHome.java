/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */
 
package com.sktst.bas.bco.ejb;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보관리</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-15 16:44:08</li>
 * </ul>
 *
 * @author 김중일 (kimjoongil)
 */
public interface BCOHome extends javax.ejb.EJBHome {

	 /**
	  *
	  * @return
	  * @throws javax.ejb.CreateException
	  * @throws java.rmi.RemoteException
	  */
	 public BCO create() throws javax.ejb.CreateException,
	         java.rmi.RemoteException;

}