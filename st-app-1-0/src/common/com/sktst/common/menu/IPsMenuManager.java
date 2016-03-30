package com.sktst.common.menu;

import java.util.Map;

/**
 * 
 * @author admin
 *
 */
public interface IPsMenuManager {

	/**
	 * 메뉴ID에 해당하는 메뉴정보 취득
	 * @param menuId
	 * @return
	 */
	PsMenu getMenu(String menuId);
	
	/**
	 * 루트메뉴정보 취득
	 * @return
	 */
	PsMenu getTempRootMenu();
	
	/**
	 * 
	 * @param userGrpCd
	 * @param menuId
	 * @return
	 */
	PsMenu getMenuWithAuth(String userGrpCd, String menuId);
	
	/**
	 * 루트메뉴정보(권한포함) 취득
	 * @param userGrpCd
	 * @return
	 */
	PsMenu getTempRootMenuWithAuth(String userGrpCd);
	
	/**
	 * 
	 * @param menu
	 * @return
	 */
	Map getMenuTreeRecordSet(PsMenu menu);
	
	
	
}