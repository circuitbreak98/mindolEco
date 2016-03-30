package com.sktst.common.menu;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Administrator
 *
 */
public interface IPsMenuLoader{
	
	/**
	 * 임시 루트 메뉴ID 취득
	 * @return
	 */
	String getTempRootMenuId();
	
	/**
	 * 메뉴정보 취득(임시루트메뉴포함)
	 * @return
	 */
	Map load();
	
	/**
	 * 사용자그룹에 해당하는 메뉴권한정보 취득
	 * @param userGrpCd
	 * @return
	 */
	List loadMenuAuth(String userGrpCd);
}
