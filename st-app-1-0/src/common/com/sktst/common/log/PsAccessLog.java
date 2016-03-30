package com.sktst.common.log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nexcore.framework.core.Constants;
import nexcore.framework.core.log.impl.AbsLog;
import nexcore.framework.core.util.BaseUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sktst.common.user.PsLoginUserInfo;

/**
 * 
 * @author admin
 *
 */
public class PsAccessLog extends AbsLog implements IPsAccessLog {

	protected Log log = null;
	
	/* (non-Javadoc)
	 * @see nexcore.framework.core.log.impl.AbsLog#getId()
	 */
	@Override
	public Class getId() {
		return IPsAccessLog.class;
	}

	/* (non-Javadoc)
	 * @see com.sktst.common.log.IPsAccessLog#log(javax.servlet.http.HttpServletRequest, String)
	 */
	public void log(HttpServletRequest request, String flag) {
        if (log == null) {
            log = LogFactory.getLog("__accesslog");        
        }
		HttpSession session = request.getSession(true);
		PsLoginUserInfo loginUserInfo = (PsLoginUserInfo)session.getAttribute(Constants.USER);
        String loginId = null;
		if(loginUserInfo != null){
			loginId = loginUserInfo.getLoginId();
		}else{
			loginId = "";
		}

		// dateTime : [wasInstanceId]_[loginId]_[logTypeXd]_[accessIp]
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("[");
        strBuffer.append(BaseUtils.getCurrentWasInstanceId());
        strBuffer.append("]_[");
        strBuffer.append(loginId);
        strBuffer.append("]_[");
       	strBuffer.append(flag);
        strBuffer.append("]_[");
        strBuffer.append(request.getRemoteAddr());
        strBuffer.append("]");

        log.info(strBuffer.toString());		
	}	
}