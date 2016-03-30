package com.sktst.common.user;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nexcore.framework.core.Constants;
import nexcore.framework.core.data.Channel;
import nexcore.framework.core.data.IChannel;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRuntimeContext;
import nexcore.framework.core.data.ITerminal;
import nexcore.framework.core.data.ITransaction;
import nexcore.framework.core.data.OnlineContext;
import nexcore.framework.core.data.RuntimeContext;
import nexcore.framework.core.data.Terminal;
import nexcore.framework.core.data.Transaction;
import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.core.prototype.AbsFwkService;
import nexcore.framework.core.util.BaseUtils;
import nexcore.framework.online.biz.role.ICommonRoleManager;

/**
 * 사용자 관리 확장 컴포넌트.
 * @author Administrator
 *
 */
public class PsUserManager extends AbsFwkService implements IPsUserManager {

    /**
     * IPsUserLoader
     */
    private IPsUserLoader userLoader = null;

    /**
     * ICommonRoleManager
     */
    private ICommonRoleManager crm = null;
    
    /**
     * setter for userLoader (nexcore-biz.xml에서 설정)
     * @param userLoader
     */
    public void setUserLoader(IPsUserLoader userLoader) {
        this.userLoader = userLoader;
    }

    /**
     * set common role component (nexcore-biz.xml에서 설정)
     * @param crm
     */
    public void setCommonRoleManager(ICommonRoleManager crm){
        this.crm = crm;
    }

    /**
     * 사용자 정보를 로드하여 반환한다.
     * 
     * @param userId 사용자ID
     * @return IUserInfo 사용자 정보 (PsUserInfo)
     */
    public IUserInfo getUser(String userId) {
        return userLoader.load(userId);
    }
    
    /**
     * 로그인 정보를 로드하여 반환한다.
     * 
     * @param userId 사용자ID
     * @return IUserInfo 로그인 정보 (PsLoginUserInfo)
     */
    public IUserInfo getLoginUser(String userId){
    	return userLoader.loadLoginInfo(userId);
    }
    
	/**
     * Returns a dummy <code>ITransaction</code> object.
     * 
     * @param txId transaction id
     * @return ITransaction object
     */
    private ITransaction getDummyTransaction(String txId) {
        return new Transaction(txId, false, new Date());
    }

    /**
     * Returns a dummy <code>IChannel</code> object.
     * 
     * @return IChannel object
     */
    private IChannel getDummyChannel() {
        String cid = "dummyhost:80";
        String pid = "10.10.10.10";
        
        IChannel channel = new Channel(cid, pid, IChannel.PROTOCOL_HTTP,
                IChannel.MSG_HTTP);
        return channel;
    }
    
    /**
     * Returns a dummy <code>ITerminal</code> object.
     * 
     * @return ITerminal object
     */
    private ITerminal getDummyTerminal() {
        String terminalId = "dummyhost:80";
        String branchCode = "UNDEFINED.";
        int agentType = ITerminal.AGENT_FIREFOX;

        ITerminal terminal = new Terminal(terminalId, branchCode, agentType);
        return terminal;
    }

    /**
     * Returns a dummy <code>IRuntimeContext</code> object.
     * 
     * @return IRuntimeContext object
     */
    private static IRuntimeContext getDummyRuntimeContext() {
        return new RuntimeContext("dummpComp", "dummyMethod");
    }
    
    /**
     * 
     */
    public IUserInfo getDummyUserInfo() {
    	PsLoginUserInfo userInfo = new PsLoginUserInfo();
        userInfo.setLoginId("00000");
        userInfo.setLocale(BaseUtils.getDefaultLocale());
        userInfo.setIp("10.10.10.10");
        userInfo.setLoginTime(new Date());
        return userInfo;
    }
    
	/**
     * @see nexcore.framework.online.biz.auth.IUserManager#getDummyOnlineCtx(java.lang.String)
     */
    public IOnlineContext getDummyOnlineCtx(String txId) {
        ITransaction transaction = getDummyTransaction(txId);
        IChannel channel = getDummyChannel();
        ITerminal terminal = getDummyTerminal();
        IRuntimeContext runtimeCtx = getDummyRuntimeContext();
        IUserInfo userInfo = getDummyUserInfo();
        IOnlineContext onlineCtx = new OnlineContext(transaction, userInfo, runtimeCtx, channel, terminal);
        return onlineCtx;
    }

    /**
	 * @see nexcore.framework.online.biz.auth.IWebUserManager#getAnonymousUserInfo(javax.servlet.http.HttpServletRequest)
	 */
	public IUserInfo getAnonymousUserInfo(HttpServletRequest request) {
        PsLoginUserInfo userInfo = new PsLoginUserInfo();
        userInfo.setLoginId(crm.getAnonymousUserId());
        userInfo.setLoginTime(new Date());
        userInfo.setIp(request.getRemoteAddr());
        userInfo.setLocale(BaseUtils.getDefaultLocale());
        return userInfo;
	}

	/**
	 * @see nexcore.framework.online.biz.auth.IWebUserManager#getLoggedInUserInfo(javax.servlet.http.HttpServletRequest)
	 */
	public IUserInfo getLoggedInUserInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        IUserInfo userInfo = (IUserInfo) session.getAttribute(Constants.USER);
        if (userInfo == null) { // maybe session has expired
            return null;
        }
        return userInfo;
	}

	/**
	 * @see nexcore.framework.online.biz.auth.IWebUserManager#getUserInfo(javax.servlet.http.HttpServletRequest)
	 */
	public IUserInfo getUserInfo(HttpServletRequest request) {
        IUserInfo userInfo = getLoggedInUserInfo(request);
        if (userInfo != null){
            return userInfo;
        }
        return getAnonymousUserInfo(request);
	}

	/**
	 * @see nexcore.framework.online.biz.auth.IWebUserManager#setUserInfo(javax.servlet.http.HttpServletRequest, nexcore.framework.core.data.user.IUserInfo)
	 */
	public void setUserInfo(HttpServletRequest request, IUserInfo userInfo) {
        HttpSession session = request.getSession(true);
        if (session != null) {
            session.setAttribute(Constants.USER, userInfo);
        }		
	}

	// 불필요 메소드 정리 ////////////////////////////////////////
	/* (non-Javadoc)
	 * @see nexcore.framework.online.biz.auth.IUserManager#getUserByEmployeeId(java.lang.String)
	 */
	public IUserInfo getUserByEmployeeId(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nexcore.framework.online.biz.auth.IUserManager#loadProfile(nexcore.framework.core.data.user.IUserInfo)
	 */
	public IUserInfo loadProfile(IUserInfo user) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
