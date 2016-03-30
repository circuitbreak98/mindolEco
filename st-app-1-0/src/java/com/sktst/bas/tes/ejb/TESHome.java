/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */
 
package com.sktst.bas.tes.ejb;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-11-30 11:25:40</li>
 * </ul>
 *
 * @author 권현호 (kwonhyunho)
 */
public interface TESHome extends javax.ejb.EJBHome {

	 /**
	  *
	  * @return
	  * @throws javax.ejb.CreateException
	  * @throws java.rmi.RemoteException
	  */
	 public TES create() throws javax.ejb.CreateException,
	         java.rmi.RemoteException;

}