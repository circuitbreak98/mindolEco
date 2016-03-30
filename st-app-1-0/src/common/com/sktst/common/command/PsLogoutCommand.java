package com.sktst.common.command;

import javax.servlet.http.HttpServletRequest;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.exception.BaseException;
import nexcore.framework.core.exception.BaseRuntimeException;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.log.AuditManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.online.biz.auth.IAuthManager;
import nexcore.framework.online.channel.core.ICommandViewMap;
import nexcore.framework.online.channel.core.IRequestContext;
import nexcore.framework.online.channel.core.IResponseContext;
import nexcore.framework.online.channel.core.command.AbstractCommand;

import com.sktst.common.base.BaseConstants;
import com.sktst.common.log.IPsAccessLog;

/**
 * 
 * @author Administrator
 *
 */
public class PsLogoutCommand extends AbstractCommand {
    
    /**
     * 
     * @see nexcore.framework.online.channel.core.ICommand#execute(nexcore.framework.online.channel.core.IRequestContext, nexcore.framework.online.channel.core.ICommandViewMap)
     */
    public IResponseContext execute(IRequestContext requestCtx,
                                    ICommandViewMap cmdViewMap)
            throws BaseException, BaseRuntimeException {
            	
        //로그아웃 로그 기록.
    	HttpServletRequest request = (HttpServletRequest) requestCtx.getReadProtocol(); 
    	IPsAccessLog accessLog = (IPsAccessLog)AuditManager.getLogMap().get(IPsAccessLog.class);
        accessLog.log(request, "LOGOUT");

    	IAuthManager authManager = (IAuthManager) ComponentRegistry.lookup(ServiceConstants.BIZ_AUTH);
        authManager.logout(requestCtx.getReadProtocol(), requestCtx.getWriteProtocol());
                
        IDataSet responseData = DataSetFactory.createWithOKResultMessage(BaseConstants.LOGOUT_SUCCESS, new String[]{});        
        return getResponseContext(requestCtx, responseData, cmdViewMap);
    }

}
