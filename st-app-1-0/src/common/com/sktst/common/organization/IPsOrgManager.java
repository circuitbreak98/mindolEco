package com.sktst.common.organization;

import nexcore.framework.core.data.IRecordSet;

import com.sktst.common.user.PsLoginUserInfo;

/**
 * 
 * @author admin
 *
 */
public interface IPsOrgManager {

	/**
	 * 
	 * @return
	 */
	String getRecordsetId();
	
	/**
	 * 
	 * @param userInfo
	 * @return
	 */
	IRecordSet getOrgComboList(PsLoginUserInfo userInfo);
}
