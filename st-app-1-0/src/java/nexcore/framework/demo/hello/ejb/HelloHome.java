/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */
 
package nexcore.framework.demo.hello.ejb;

/**
 * <ul>
 * <li>업무 그룹명 : Nexcore/프레임워크/Demo</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-07 11:01:20</li>
 * </ul>
 *
 * @author admin (admin)
 */
public interface HelloHome extends javax.ejb.EJBHome {

	 /**
	  *
	  * @return
	  * @throws javax.ejb.CreateException
	  * @throws java.rmi.RemoteException
	  */
	 public Hello create() throws javax.ejb.CreateException,
	         java.rmi.RemoteException;

}