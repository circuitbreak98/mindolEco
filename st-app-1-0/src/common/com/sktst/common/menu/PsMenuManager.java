package com.sktst.common.menu;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nexcore.framework.core.prototype.AbsFwkService;

/**
 * 
 * @author Administrator
 *
 */
public class PsMenuManager extends AbsFwkService implements IPsMenuManager{

	//
    private IPsMenuLoader menuLoader = null;

    //
    private IPsMenuToRecordSet menuToRecordSet = null;
    
    /**
     * setter for menuLoader (nexcore-biz.xml에서 설정)
     * @param menuLoader
     */
    public void setMenuLoader(IPsMenuLoader menuLoader) {
        this.menuLoader = menuLoader;
    }
    
    /**
     * 
     * @param menuConverter
     */
    public void setMenuToRecordSet(IPsMenuToRecordSet menuToRecordSet){
    	 this.menuToRecordSet = menuToRecordSet;
    }
    
    /**
     * 
     * @param menuId
     * @return
     */
    public PsMenu getMenu(String menuId){
    	Map menuTreeMap = menuLoader.load();
    	return (PsMenu)menuTreeMap.get(menuId);    	
    }

    /**
     * 
     * @return
     */
    public PsMenu getTempRootMenu(){
    	return getMenu(menuLoader.getTempRootMenuId());
    }    
    
	/**
	 * 
	 * @param userGrpCd
	 * @param menuId
	 * @return
	 */
	public PsMenu getMenuWithAuth(String userGrpCd, String menuId){
    	Map menuTreeMap = menuLoader.load();
    	List menuAuthList = menuLoader.loadMenuAuth(userGrpCd);
    	    	
        for (Iterator it = menuAuthList.iterator(); it.hasNext();) {
        	PsMenuAuth menuAuth = (PsMenuAuth) it.next();
        	PsMenu menu = (PsMenu)menuTreeMap.get(menuAuth.getMenuNum());
        	if(menu != null){
        		menu.setMenuAuth(menuAuth);
        	}
        }
        return (PsMenu)menuTreeMap.get(menuId);		
	}
	
    /**
     * 
     * @param userGrpCd
     * @return
     */
    public PsMenu getTempRootMenuWithAuth(String userGrpCd){
    	return getMenuWithAuth(userGrpCd, menuLoader.getTempRootMenuId());
    }
    
    /**
     *
     * @param menu
     */
    public Map getMenuTreeRecordSet(PsMenu menu){
    	return menuToRecordSet.getMenuRecordSet(menu, menuLoader.getTempRootMenuId());
    }
    
}
