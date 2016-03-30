package com.sktst.batch.base;

import java.util.Map;

/**
 * 
 * @author admin
 *
 */
public interface IBatchJobBiz {

	/**
	 * 
	 * @param request
	 * @return
	 */
	int execute(Map request) throws Exception;
	
}
