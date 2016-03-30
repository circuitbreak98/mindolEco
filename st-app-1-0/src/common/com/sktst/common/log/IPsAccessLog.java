package com.sktst.common.log;

import javax.servlet.http.HttpServletRequest;

import nexcore.framework.core.log.ILog;

/**
 * 
 * @author admin
 *
 */
public interface IPsAccessLog extends ILog{

	/**
	 * 
	 * @param request
	 * @param loginFlag
	 */
	void log(HttpServletRequest request, String flag);
}
