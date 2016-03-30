package com.sktst.common.menu;

import java.util.Map;

/**
 * 
 * @author Administrator
 *
 */
public interface IPsMenuToRecordSet {

	/**
	 * 
	 * @param menu
	 * @return
	 */
	Map getMenuRecordSet(PsMenu menu);
	
	/**
	 * 
	 * @param menu
	 * @param exceptMenuId
	 * @return
	 */
	Map getMenuRecordSet(PsMenu menu, String exceptMenuId);
	
}
