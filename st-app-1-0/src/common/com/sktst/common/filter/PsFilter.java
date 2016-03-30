package com.sktst.common.filter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nexcore.framework.core.Constants;
import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.message.IMessageManager;
import nexcore.framework.core.util.BaseUtils;
import nexcore.framework.core.util.StringUtils;
import nexcore.framework.online.channel.web.XMIView;

import org.apache.commons.logging.Log;

import com.sktst.common.user.PsLoginUserInfo;
import com.tobesoft.platform.PlatformConstants;
import com.tobesoft.platform.PlatformResponse;
import com.tobesoft.platform.data.DatasetList;
import com.tobesoft.platform.data.PlatformData;
import com.tobesoft.platform.data.VariableList;

/**
 * 
 * @author admin
 *
 */
public class PsFilter implements Filter{
	
	public Log log = null;

	public static final String LOGIN_PAGE_PATHS = "loginPagePaths";
	public static final String DEFAULT_LOGIN_PAGE = "/login.jsp";

	public String[] loginPagePath;	
	
	private XMIView view = null;


	private final static SimpleDateFormat TIME_FORMAT = new java.text.SimpleDateFormat("HHmm");
	private final static SimpleDateFormat DATE_TIME_FORMAT = new java.text.SimpleDateFormat("yyyyMMddHHmm");
	private final static String MID_TIME1 = "2400";
	private final static String MID_TIME2 = "0000";	

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		//framework logger setting
    	log = LogManager.getFwkLog();
    	
    	//view setting (for msg_flag varialbe name setting)
    	view = (XMIView)BaseUtils.lookupWebComponent("nc.channel.XmiView", filterConfig.getServletContext()); 
    	
    	String loginPagePaths = filterConfig.getInitParameter(LOGIN_PAGE_PATHS);    	
    	log.info("PsFilter is initiated!\nLOGIN_PAGE_PATH="+loginPagePaths);
    	
    	if(StringUtils.isEmpty(loginPagePaths)){
    		loginPagePath = new String[]{DEFAULT_LOGIN_PAGE};
    	}else{
    		loginPagePath = StringUtils.tokenizeToStringArray(loginPagePaths, ",");
    	}		
	}
	

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    	HttpServletRequest req = (HttpServletRequest) request;
    	HttpServletResponse res = (HttpServletResponse) response;
    	String servletPath = req.getServletPath();
    	
    	PsLoginUserInfo ui = getUserInfo(req);
    	
        if (ui == null) {
        	if (!checkLoginPagePath(servletPath)) {
        		
//            	if(".xmi".endsWith(servletPath)){
        			sendXmiLogoutResponse(res);
//            	}else if(".jsp".endsWith(servletPath)){
//            		sendJspLogoutResponse(req, res);
//            	}else{
//            		sendJspLogoutResponse(req, res);
//            	}    

        		return;
        	}
        } else {
	        // 특정 사용자는 점검 시간 (Close Time)을 체크하지 않는다.
 	       if (!checkAuth(ui)) {
 	
 	           	Date currDate = new Date();
 	        	
 	           	// 주기적  점검 시간 체크
 	           	if (this.checkDefCloseTime(currDate)){
 	        		sendXmiDefCloseTimeResponse(res);
 	        		return;
 	           	}
 	        	
 	           	// 특정일 점검 시간 체크
 	           	if (this.checkCloseTime(currDate)) {
 	        		sendXmiCloseTimeResponse(res);
 	        		return;
 	           	}
 	        }
        }
        		
        
        filterChain.doFilter(request, response);

	}
	
	private boolean checkAuth(PsLoginUserInfo ui){
		boolean rtnValue = false;
		
		if ("ADM".equalsIgnoreCase(ui.getUserGrp()))
			// super adm	
			rtnValue = true;
		if ("P12".equalsIgnoreCase(ui.getUserGrp()))
			// adm
			rtnValue = true;
			
		return rtnValue;
	}
	
    /**
     * check default rest time (format : HHmm)
     * @param currDate
     * @return boolean
     */
	private boolean checkDefCloseTime(Date currDate) {
		boolean check = false;
		
    	String defCloseFromTime = BaseUtils.getConfiguration("operation.default.close.from");
    	String defCloseToTime = BaseUtils.getConfiguration("operation.default.close.to");
		
		if (defCloseFromTime != null && defCloseToTime != null) {
           	String currTimeStr = TIME_FORMAT.format(currDate);
    		if (defCloseToTime.compareTo(defCloseFromTime) >= 0) {
    			if (currTimeStr.compareTo(defCloseFromTime) >= 0 && currTimeStr.compareTo(defCloseToTime) <= 0) {
    				check = true;
    			}
    		} else {
    			if ((currTimeStr.compareTo(defCloseFromTime) >= 0 && currTimeStr.compareTo(MID_TIME1) <= 0) 
    					|| (currTimeStr.compareTo(MID_TIME2) >= 0 && currTimeStr.compareTo(defCloseToTime) <= 0)) {
    				check = true;
    			}
    		}
    	}
    	
    	return check;
	}
	
    /**
     * check rest time (format : YYYYMMDDHHmm)
     * @param currDate
     * @return boolean
     */
	private boolean checkCloseTime(Date currDate) {
		boolean check = false;
    	
    	String closeFromTime = BaseUtils.getConfiguration("operation.close.from");
    	String closeToTime = BaseUtils.getConfiguration("operation.close.to");
    	
		if (closeFromTime != null && closeToTime != null) {
           	String currTimeStr = DATE_TIME_FORMAT.format(currDate);

			if (currTimeStr.compareTo(closeFromTime) >= 0 && currTimeStr.compareTo(closeToTime) <= 0) {
				check = true;
			}
    	}
    	
    	return check;
	}

    /**
     * 이용자 정보를 세션에서 취득한다.
     * @param request
     * @return
     */
    private PsLoginUserInfo getUserInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);        
        return session != null ? (PsLoginUserInfo)session.getAttribute(Constants.USER) : null;
    }
    
    /**
     * 해당 접속 URL이 로그인 페이지인지 여부를 확인한다.
     * @param servletPath
     * @return
     */
    private boolean checkLoginPagePath(String servletPath) {
    	for ( int i=0; i < loginPagePath.length; i++) {    		
    		if ( loginPagePath[i].equals(servletPath) ) {
    			return true;
    		}
    	}
    	return false;
    }
    
//    /**
//     * 세션에 사용자 정보가 없을 경우, 클라이언트로 웹 로그아웃 정보를 전달한다. 
//     * @param request
//     * @param response
//     * @exception IOException
//     *
//     */
//    private void sendJspLogoutResponse(HttpServletRequest request, HttpServletResponse response) throws IOException{
//    	try{
//    		response.sendRedirect(request.getContextPath() + loginPagePath[0]);
//    	}catch (IOException ex) {
//            if (log.isErrorEnabled()) {
//            	log.error("Exception occurred while writing html to HttpServletResponse Stream.", ex);
//            }
//            throw ex;
//    	}
//    }
    
    /**
     * 세션에 사용자 정보가 없을 경우, 클라이언트로 마이플랫폼 로그아웃 정보를 전달한다.
     * @param response
     * @exception IOException
     */
    private void sendXmiLogoutResponse(HttpServletResponse response) throws IOException{
        try {
	        //logout response data setting
	    	VariableList miVariableList = new VariableList();
	        miVariableList.add(view.getMsgFlagVarName()	, "LOGOUT");        
	        miVariableList.add("errorCode"				, "-1");
	        miVariableList.add("errorMsg"				, "ERROR");	        
	        PlatformData platformData = new PlatformData(miVariableList, new DatasetList());      
	        
	        //send PlatformResponse
            new PlatformResponse(response, PlatformConstants.CHARSET_UTF8).sendData(platformData); 
            
        } catch (IOException ex) {
            if (log.isErrorEnabled()) {
            	log.error("Exception occurred while writing xml to MiPlatform Stream.", ex);
            }
            throw ex;
        }
    	
    }
    
    
    
    /**
     * 주기적 점검 시간에 해당되는 경우 마이플랫폼 클라이언트로 점검 시간 오류 정보를 전달한다. 
     * @param response
     * @exception IOException
     */
    private void sendXmiDefCloseTimeResponse(HttpServletResponse response) throws IOException{
    	
    	String defCloseFromTime = BaseUtils.getConfiguration("operation.default.close.from");
    	String defCloseToTime = BaseUtils.getConfiguration("operation.default.close.to");
    	
    	String fromTimeStr = defCloseFromTime.substring(0, 2) + ":" + defCloseFromTime.substring(2, 4);
    	String toTimeStr = defCloseToTime.substring(0, 2) + ":" + defCloseToTime.substring(2, 4);
    	
        try {
	        //logout response data setting
	    	VariableList miVariableList = new VariableList();
	    	
	        miVariableList.add(view.getMsgFlagVarName()	, "CLOSE_TIME");    	    	
//	        miVariableList.add(view.getMsgFlagVarName()	, "WARN");        
	        miVariableList.add("errorCode"				, "-1");
	        
	        IMessageManager mm = (IMessageManager)ComponentRegistry.lookup(ServiceConstants.MESSAGE);
	        //서버 점검 중입니다. 점검 시간 {0} ~ {1}
			String msg = mm.getMessage("PSMW1008").getName(new String[]{fromTimeStr, toTimeStr});
	        
	        // TODO 임시 코드 
//	        String msg = "서버 점검 중입니다. 점검 시간 " + fromTimeStr + " ~ " + toTimeStr; 

	        miVariableList.add("msg_message"				, msg);	        
	        miVariableList.add("errorMsg"				, "WARN");	    

	        PlatformData platformData = new PlatformData(miVariableList, new DatasetList());      
	        
	        //send PlatformResponse
            new PlatformResponse(response, PlatformConstants.CHARSET_UTF8).sendData(platformData); 
            
        } catch (IOException ex) {
            if (log.isErrorEnabled()) {
            	log.error("Exception occurred while writing xml to MiPlatform Stream.", ex);
            }
            throw ex;
        }
    	
    }
    
    /**
     * 특정 점검 일자/시간 에 해당되는 경우 마이플랫폼 클라이언트로 점검 시간 오류 정보를 전달한다. 
     * @param response
     * @exception IOException
     */
    private void sendXmiCloseTimeResponse(HttpServletResponse response) throws IOException{
    	String closeFromTime = BaseUtils.getConfiguration("operation.close.from");
    	String closeToTime = BaseUtils.getConfiguration("operation.close.to");
    	
    	String fromTimeStr = closeFromTime.substring(0, 4) + "년 " + closeFromTime.substring(4, 6) + "월 " + closeFromTime.substring(6, 8) + "일 " 
    		+ closeFromTime.substring(8, 10) + ":" + closeFromTime.substring(10, 12);
    	String toTimeStr = closeToTime.substring(0, 4) + "년 " + closeToTime.substring(4, 6) + "월 " + closeToTime.substring(6, 8) + "일 " 
			+ closeToTime.substring(8, 10) + ":" + closeToTime.substring(10, 12);
    	
        try {
	        //logout response data setting
	    	VariableList miVariableList = new VariableList();
	        miVariableList.add(view.getMsgFlagVarName()	, "CLOSE_TIME");        
//	    	miVariableList.add(view.getMsgFlagVarName()	, "WARN");        
	        miVariableList.add("errorCode"				, "-1");

	        IMessageManager mm = (IMessageManager)ComponentRegistry.lookup(ServiceConstants.MESSAGE);
	        //서버 점검 중입니다. {0}  ~  {1}
	        String msg = mm.getMessage("PSMW1007").getName(new String[]{fromTimeStr, toTimeStr});
	        
	        // TODO 임시 코드 
//	        String msg = "서버 점검 중입니다. " + fromTimeStr + " ~ " + toTimeStr; 
	        
	        miVariableList.add("msg_message"				, msg);	        
	        miVariableList.add("errorMsg"				, "WARN");	        

	        PlatformData platformData = new PlatformData(miVariableList, new DatasetList());      
	        
	        //send PlatformResponse
            new PlatformResponse(response, PlatformConstants.CHARSET_UTF8).sendData(platformData); 
            
        } catch (IOException ex) {
            if (log.isErrorEnabled()) {
            	log.error("Exception occurred while writing xml to MiPlatform Stream.", ex);
            }
            throw ex;
        }
    	
    }
    
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}
	
}
