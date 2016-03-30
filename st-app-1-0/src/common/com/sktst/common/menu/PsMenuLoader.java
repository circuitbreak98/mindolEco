package com.sktst.common.menu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nexcore.framework.integration.db.ISqlManager;


public class PsMenuLoader implements IPsMenuLoader{

	//내부적으로 사용되는 임시루트메뉴ID
	private String tempRootMenuId = "nc_rootMenu";
	
	//sql 처리용
    private ISqlManager sqlManager = null;

    /**
     * 
     * @param rootMenuKey
     */
    public void setTempRootMenuId(String tempRootMenuId){
		if(tempRootMenuId != null && !tempRootMenuId.trim().equals("")){
			this.tempRootMenuId = tempRootMenuId;		
		}
    }
    
    /**
     * 
     * @return
     */
    public String getTempRootMenuId(){
    	return tempRootMenuId;
    }
    
    /**
     * ISqlManager 설정 (nexcore-biz.xml에서 설정) 
     * @param sqlManager
     */
    public void setSqlManager(ISqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }
	
    /**
     * 임시루트메뉴를 최상위로한 메뉴map취득
     * @return
     */
	public Map load(){		
		PsMenu rootTempMenu = new PsMenu();
		rootTempMenu.setMenuNum(tempRootMenuId);
		rootTempMenu.setSupMenuNum(tempRootMenuId);
		
		Map<String, PsMenu> menuPointer = new HashMap<String, PsMenu>();
		menuPointer.put(tempRootMenuId, rootTempMenu);
		
		List menuList = sqlManager.queryForList("biz.menu.getMenuList");
        for (Iterator it = menuList.iterator(); it.hasNext();) {
            PsMenu menu = (PsMenu) it.next();
            putMenuPointer(menuPointer, menu);
        }
        
        setParent(menuList, menuPointer);
        
        return menuPointer;
	}
	
	/**
	 * 
	 * @param menuPointer
	 * @param menu
	 */
    private void putMenuPointer(Map<String, PsMenu> menuPointer, PsMenu menu){
        String menuId = menu.getMenuNum();
        PsMenu savedMenu = (PsMenu)menuPointer.get(menuId);
        if(savedMenu == null){
        	menuPointer.put(menuId, menu);
        }
    }
		
    /**
     * 
     * @param menuPointer
     */
    private void setParent(List menuList, Map<String, PsMenu> menuPointer){
        for (Iterator it = menuList.iterator(); it.hasNext();) {
            PsMenu menu = (PsMenu) it.next();
        	String supMenuId = menu.getSupMenuNum();        	
        	if(supMenuId == null || supMenuId.equals("")){
        		supMenuId = tempRootMenuId;
        	}
        	if(supMenuId.equals(menu.getMenuNum())){
        		continue;
        	}
        	PsMenu parentMenu = menuPointer.get(supMenuId);
        	if(parentMenu != null){
        		menu.setParent(parentMenu);
        	}        		
        }
    }    
    
	/**
	 * 사용자그룹에 해당하는 메뉴리스트 취득
	 * @param menuNum
	 * @return
	 */
	public List loadMenuAuth(String userGrpCd){		
		return sqlManager.queryForList("biz.menu.getMenuAuthByUserGrp", userGrpCd);
	}
}
