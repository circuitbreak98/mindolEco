package com.sktst.common.user;

import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.integration.db.ISqlManager;

/**
 * 사용자 정보 로딩 처리 클래스
 * @author admin
 *
 */
public class PsUserLoader implements IPsUserLoader {

	//sql 처리용
    private ISqlManager sqlManager = null;

    /**
     * ISqlManager 설정 (nexcore-biz.xml에서 설정) 
     * @param sqlManager
     */
    public void setSqlManager(ISqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    /**
     * 사용자 정보를 로드한다.
     * 
     * @param userId 사용자ID
     * @return IUserInfo 사용자정보 (PsUserInfo)
     */
	public IUserInfo load(String userId) {
        return (IUserInfo) sqlManager.queryForObject("biz.user.getUserInfo", userId);
	}

    /**
     * 로그인 정보를 로드한다.
     * 
     * @param userId 사용자ID
     * @return IUserInfo 로그인 정보 (PsLoginUserInfo)
     */
	public IUserInfo loadLoginInfo(String userId){
        return (IUserInfo) sqlManager.queryForObject("biz.user.getLoginUserInfo", userId);
	}
	
	// 불필요 메소드 정리 ////////////////////////////////////////
	/* (non-Javadoc)
	 * @see nexcore.framework.online.biz.auth.IUserInfoLoader#loadByEmployeeId(java.lang.String)
	 */
	public IUserInfo loadByEmployeeId(String emplyeeId) {
		// TODO Auto-generated method stub
		return null;
	}


	
}