package com.sktst.common.menu;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author admin
 *
 */
public class PsMenu {
	
	/** 메뉴번호 */
	public static final String MENU_NUM = "menuNum";
	
	/** 메뉴명 */
	public static final String MENU_NM = "menuNm";
	
	/** 메뉴축양명 */
	public static final String MENU_SHOT_NM = "menuShotNm";
	
	/** 최상위메뉴번호 */
	public static final String TOP_MENU_NUM = "topMenuNum";

	/** 메뉴그룹 */
	public static final String MENU_GROUP = "menuGroup";
	
	/** 메뉴레벨코드 */
	public static final String MENU_LVL_CD = "menuLvlCd";
	
	/** 메뉴URL */
	public static final String MENU_URL = "menuUrl";
	
	/** 정렬순서 */
	public static final String SORT_SEQ = "sortSeq";
	
	/** 화면ID */
	public static final String SCREEN_ID = "screenId";
	
	/** 상위메뉴번호 */
	public static final String SUP_MENU_NUM = "supMenuNum";
	
	/** 최상위PREPIX코드 */
	public static final String TOP_PREFIX_CD = "topPrefixCd";
	
	/** 화면SIZE */
	public static final String SCREEN_SIZE = "screenSize";
	
	/** 사용여부 */
	public static final String USE_YN = "useYn";
	
	/** 비고 */ 
	public static final String REMARK ="remark";
	
	/** 메뉴 트리 단계 */
	public static final String TREE_STEP = "treeStep";

	private String menuNum;
	private String menuNm;
	private String menuShotNm;
	private String topMenuNum;
	private String menuGroup;
	private String menuLvlCd;
	private String menuUrl;
	private String sortSeq;
	private String screenId;
	private String supMenuNum;
	private String topPrefixCd;
	private String screenSize;
	private String useYn;
	private String remark;
	private String treeStep;
	
	private List<PsMenu> children;
	private PsMenu parent;
	
	private PsMenuAuth menuAuth;
	
	/** 
	 * 생성자
	 *
	 */
	public PsMenu(){		
	}
	
	/**
	 * 
	 * @param child
	 */
	public void addChild(PsMenu child){
		if(children == null){
			children = new ArrayList<PsMenu>();
		}
		children.add(child);
	}
	
	/**
	 * 
	 * @return
	 */
	public List getChildren(){
		return children;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasChildren(){
		if(children == null){
			return false;
		}else if(children.size() == 0){
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param parent
	 */
	public void setParent(PsMenu parent){
		this.parent = parent;
		if(parent != null){
			parent.addChild(this);
		}
	}

	/**
	 * 
	 * @return
	 */
	public PsMenu getParent(){
		return parent;
	}

	
	/**
	 * @return the menuAuth
	 */
	public PsMenuAuth getMenuAuth() {
		return menuAuth;
	}

	/**
	 * 
	 * @param menuAuth the menuAuth to set
	 */
	public void setMenuAuth(PsMenuAuth menuAuth) {
		this.menuAuth = menuAuth;
		if(parent != null){
			PsMenuAuth parentMenuAuth = parent.getMenuAuth();
			if(parentMenuAuth == null){
				parentMenuAuth = new PsMenuAuth();
				parentMenuAuth.setSearchAuthYn("Y");
				parent.setMenuAuth(parentMenuAuth);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasAuth(){
		if(menuAuth == null){
			return false;
		}else if("Y".equalsIgnoreCase(menuAuth.getSearchAuthYn())){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * @return the menuGroup
	 */
	public String getMenuGroup() {
		return menuGroup;
	}

	/**
	 * @param menuGroup the menuGroup to set
	 */
	public void setMenuGroup(String menuGroup) {
		this.menuGroup = menuGroup;
	}

	/**
	 * @return the menuLvlCd
	 */
	public String getMenuLvlCd() {
		return menuLvlCd;
	}

	/**
	 * @param menuLvlCd the menuLvlCd to set
	 */
	public void setMenuLvlCd(String menuLvlCd) {
		this.menuLvlCd = menuLvlCd;
	}

	/**
	 * @return the menuNm
	 */
	public String getMenuNm() {
		return menuNm;
	}

	/**
	 * @param menuNm the menuNm to set
	 */
	public void setMenuNm(String menuNm) {
		this.menuNm = menuNm;
	}

	/**
	 * @return the menuNum
	 */
	public String getMenuNum() {
		return menuNum;
	}

	/**
	 * @param menuNum the menuNum to set
	 */
	public void setMenuNum(String menuNum) {
		this.menuNum = menuNum;
	}

	/**
	 * @return the menuShotNm
	 */
	public String getMenuShotNm() {
		return menuShotNm;
	}

	/**
	 * @param menuShotNm the menuShotNm to set
	 */
	public void setMenuShotNm(String menuShotNm) {
		this.menuShotNm = menuShotNm;
	}

	/**
	 * @return the menuUrl
	 */
	public String getMenuUrl() {
		return menuUrl;
	}

	/**
	 * @param menuUrl the menuUrl to set
	 */
	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the screenId
	 */
	public String getScreenId() {
		return screenId;
	}

	/**
	 * @param screenId the screenId to set
	 */
	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

	/**
	 * @return the screenSize
	 */
	public String getScreenSize() {
		return screenSize;
	}

	/**
	 * @param screenSize the screenSize to set
	 */
	public void setScreenSize(String screenSize) {
		this.screenSize = screenSize;
	}

	/**
	 * @return the sortSeq
	 */
	public String getSortSeq() {
		return sortSeq;
	}

	/**
	 * @param sortSeq the sortSeq to set
	 */
	public void setSortSeq(String sortSeq) {
		this.sortSeq = sortSeq;
	}

	/**
	 * @return the supMenuNum
	 */
	public String getSupMenuNum() {
		return supMenuNum;
	}

	/**
	 * @param supMenuNum the supMenuNum to set
	 */
	public void setSupMenuNum(String supMenuNum) {
		this.supMenuNum = supMenuNum;
	}

	/**
	 * @return the topMenuNum
	 */
	public String getTopMenuNum() {
		return topMenuNum;
	}

	/**
	 * @param topMenuNum the topMenuNum to set
	 */
	public void setTopMenuNum(String topMenuNum) {
		this.topMenuNum = topMenuNum;
	}

	/**
	 * @return the topPrefixCd
	 */
	public String getTopPrefixCd() {
		return topPrefixCd;
	}

	/**
	 * @param topPrefixCd the topPrefixCd to set
	 */
	public void setTopPrefixCd(String topPrefixCd) {
		this.topPrefixCd = topPrefixCd;
	}

	/**
	 * @return the useYn
	 */
	public String getUseYn() {
		return useYn;
	}

	/**
	 * @param useYn the useYn to set
	 */
	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

	/**
	 * @return the treeStep
	 */
	public String getTreeStep() {
		return treeStep;
	}

	/**
	 * @param treeStep the treeStep to set
	 */
	public void setTreeStep(String treeStep) {
		this.treeStep = treeStep;
	}

}
