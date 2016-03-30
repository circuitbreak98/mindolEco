package com.sktst.common.organization;

import com.sktst.common.user.PsLoginUserInfo;

import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.prototype.AbsFwkService;

/**
 * 
 * @author admin
 *
 */
public class PsOrgManager extends AbsFwkService implements IPsOrgManager{

	private String recordsetId = "GBL_ORG_DS"; 
	private IPsOrgLoader orgLoader = null;	
	
	
	/**
	 * @param recordsetId the recordsetId to set
	 */
	public void setRecordsetId(String recordsetId) {
		if(recordsetId != null && !recordsetId.trim().equals("")){
			this.recordsetId = recordsetId;
		}		
	}

	/**
	 * @param orgLoader the orgLoader to set
	 */
	public void setOrgLoader(IPsOrgLoader orgLoader) {
		this.orgLoader = orgLoader;
	}

	/* (non-Javadoc)
	 * @see com.sktst.common.organization.IPsOrgManager#getRecordsetId()
	 */
	public String getRecordsetId() {
		return recordsetId;
	}

	/* (non-Javadoc)
	 * @see com.sktst.common.organization.IPsOrgManager#getOrgComboList(com.sktst.common.user.PsLoginUserInfo)
	 */
	public IRecordSet getOrgComboList(PsLoginUserInfo userInfo) {
		String orgCl = userInfo.getOrgCl();
		if(orgCl == null){
			orgCl = "";
		}
		String orgId = userInfo.getOrgId();
		if(orgId == null){
			orgId = "";			
		}
		
		IRecordSet ncRecordSet = orgLoader.getOrgComboList(orgCl, orgId);
		if(ncRecordSet == null){
			ncRecordSet = new RecordSet(recordsetId);
		}		
		return ncRecordSet;
	}



	
}
