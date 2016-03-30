package com.sktst.common.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nexcore.framework.core.Constants;
import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.IChannel;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.exception.BaseException;
import nexcore.framework.core.exception.BaseRuntimeException;
import nexcore.framework.core.exception.CertificationFailedException;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.log.AuditManager;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.online.biz.auth.IAuthManager;
import nexcore.framework.online.channel.core.ICommandViewMap;
import nexcore.framework.online.channel.core.IRequestContext;
import nexcore.framework.online.channel.core.IResponseContext;
import nexcore.framework.online.channel.core.command.AbstractCommand;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;
import com.sktst.common.log.IPsAccessLog;
import com.sktst.common.log.LogToDbUtil;
import com.sktst.common.menu.IPsMenuManager;
import com.sktst.common.menu.PsMenu;
import com.sktst.common.organization.IPsOrgManager;
import com.sktst.common.user.PsLoginUserInfo;

/**
 * 
 * @author Administrator
 *
 */
public class PsLoginCommand extends AbstractCommand {

    /**
     * login Id key
     */
    private static final String ID = "id";
    
    /**
     * password key
     */
    private static final String PASSWORD = "password";
        

    /**
     * default login Id parameterName
     */
    private String loginIdKey       = "J_USERNAME";
    
    /**
     * default password parameterName
     */
    private String passwdKey        = "J_PASSWORD";
    
	/** RecordSet Id */
	private String recordsetId = "GBL_MENU_DS"; //기본값 설정 
	
	/** noUseRecorsetid */
	private String noUseRecorsetId = "GBL_NOUSE_MENU_DS"; //내부적 사용 메뉴 DS 기본값 설정
		

    /**
     * 디버깅용 Logger
     */
    private Log    logger           = LogManager.getFwkLog();

    public void setKeys(Properties p){
        loginIdKey = p.containsKey(ID) ? p.getProperty(ID) : loginIdKey;
        passwdKey = p.containsKey(PASSWORD) ? p.getProperty(PASSWORD) : passwdKey;
    }
    
    /**
     * 
     * @see nexcore.framework.online.channel.core.ICommand#execute(nexcore.framework.online.channel.core.IRequestContext,
     *      nexcore.framework.online.channel.core.ICommandViewMap)
     */
    public IResponseContext execute(IRequestContext requestCtx, ICommandViewMap cmdViewMap)
            throws BaseException, BaseRuntimeException {
           	
        IDataSet requestData = (IDataSet)requestCtx.getBizData();
        String loginId = requestData.getField(loginIdKey);
        String passwd = requestData.getField(passwdKey);
        
        if (logger.isDebugEnabled()) {
            logger.debug(">>====================================================");
            logger.debug("check request data at LoginController");
            logger.debug("Login id = " + loginId);
            logger.debug("Password = " + passwd);
            logger.debug("<<====================================================");
        }
                
        IDataSet responseData = null;
        IAuthManager authManager = (IAuthManager) ComponentRegistry.lookup(ServiceConstants.BIZ_AUTH);
        IPsOrgManager orgManager = (IPsOrgManager) ComponentRegistry.lookup("nc.biz.IOrgManager");
        IPsMenuManager menuManager = (IPsMenuManager) ComponentRegistry.lookup(ServiceConstants.BIZ_MENU);
        
        HttpServletRequest request = (HttpServletRequest) requestCtx.getReadProtocol();            
        IPsAccessLog accessLog = (IPsAccessLog)AuditManager.getLogMap().get(IPsAccessLog.class);
        try {
            authManager.login(loginId, passwd, "ko_KR", requestCtx.getReadProtocol(), requestCtx.getWriteProtocol());
            String loginResult = (String)request.getAttribute(BaseConstants.LOGIN_RESULT_KEY);
            String[] loginResultParam = (String[])request.getAttribute(BaseConstants.LOGIN_RESULT_PARAMETER);            
    		responseData = DataSetFactory.createWithOKResultMessage(loginResult, loginResultParam);
    		
    		//로그인 정보 설정
    		HttpSession session = request.getSession(true);
    		PsLoginUserInfo loginUserInfo = (PsLoginUserInfo)session.getAttribute(Constants.USER);
    		responseData.putFieldMap(loginUserInfo.getUserInfoMap());
    		
    		//조직콤보 설정
    		responseData.putRecordSet(orgManager.getRecordsetId(), orgManager.getOrgComboList(loginUserInfo));
    		
    		//메뉴정보 설정
    		//responseData.putRecordSet(recordsetId, menuManager.getUserMenuTree(loginUserInfo.getUserGrp()));
    		//responseData.putRecordSet(noUseRecorsetId, menuManager.getUserMenuListWidhAuth(loginUserInfo.getUserGrp()));
    		//PsMenu rootMenu = menuManager.getTempRootMenuWithAuth(loginUserInfo.getUserGrp());
    		//responseData.putRecordSets(menuManager.getMenuTreeRecordSet(rootMenu));
    		
    		accessLog.log(request, "LOGIN");

    		
    		String startTime = getDefaultCurrentDateTime();
    		
			Map<String, String> queryParam = new HashMap<String, String>();
			queryParam.put("tranDt", getDefaultCurrentDateTime() .substring(0, 8));
			queryParam.put("userId", loginId);
			queryParam.put("startTime", startTime.substring(8, 17));
			queryParam.put("endTime", startTime.substring(8, 17));
			queryParam.put("menuId", "");
			queryParam.put("txId", "Login");
			queryParam.put("clientIP", loginUserInfo.getIp());
			queryParam.put("orgId", loginUserInfo.getOrgId());
			queryParam.put("agencyCd", loginUserInfo.getPosAgencyId());
			queryParam.put("orgArea", loginUserInfo.getOrgAreaId());
			queryParam.put("txTime", "2");
			LogToDbUtil logDb = (LogToDbUtil) ComponentRegistry.lookup("ps.biz.LogToDb");
			logDb.logToDB("biz.log.insertTxLog", queryParam);		
    		
    		
        } catch (CertificationFailedException e) {        	
    		accessLog.log(request, "LOGIN_FAIL");    		
            if (logger.isErrorEnabled()) {
                logger.debug("Exception Caughted. in LoginCommand", e);
            }
            throw e;
        }        
        return getResponseContext(requestCtx, responseData, cmdViewMap);
    }

	public static String getDefaultCurrentDateTime() {
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sFormat.format(new Date());
    }	
}
