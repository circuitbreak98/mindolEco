package com.sktst.common.organization;

import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.integration.db.ISqlManager;

/**
 * 
 * @author admin
 *
 */
public class PsOrgLoader implements IPsOrgLoader{

	//sql 처리용
    private ISqlManager sqlManager = null;

    /**
     * ISqlManager 설정 (nexcore-biz.xml에서 설정) 
     * @param sqlManager
     */
    public void setSqlManager(ISqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }

	/* (non-Javadoc)
	 * @see com.sktst.common.organization.IPsOrgLoader#getOrgComboList(java.lang.String, java.lang.String)
	 */
	public IRecordSet getOrgComboList(String orgCl, String orgId) {
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("orgCl", orgCl);
		requestMap.put("orgId", orgId);		
		return sqlManager.queryForRecordSet("biz.org.getOrgComboList", requestMap);
	}
}
