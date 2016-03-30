/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */
 
package com.sktst.cst.adv.ejb;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-12 14:22:56</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public interface ADVHome extends javax.ejb.EJBHome {

	 /**
	  *
	  * @return
	  * @throws javax.ejb.CreateException
	  * @throws java.rmi.RemoteException
	  */
	 public ADV create() throws javax.ejb.CreateException,
	         java.rmi.RemoteException;

}