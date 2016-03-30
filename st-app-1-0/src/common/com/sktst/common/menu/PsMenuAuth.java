package com.sktst.common.menu;

/**
 * 
 * @author Administrator
 *
 */
public class PsMenuAuth{

	/** 조회권한여부 */
	public static final String SEARCH_AUTH_YN = "searchAuthYn";

	/** 저장권한여부 */
	public static final String SAVE_AUTH_YN = "saveAuthYn";

	/** 삭제권한여부 */
	public static final String DEL_AUTH_YN = "delAuthYn";

	/** 요청권한여부 */
	public static final String REQ_AUTH_YN = "reqAuthYn";

	/** 승인권한여부 */
	public static final String APRV_AUTH_YN = "aprvAuthYn";

	/** 취소권한여부 */
	public static final String CAN_AUTH_YN = "canAuthYn";

	/** 마감권한여부 */
	public static final String CLOSE_AUTH_YN = "closeAuthYn";

	/** 인쇄권한여부 */
	public static final String PRINT_AUTH_YN = "printAuthYn";

	/** 초기화권한여부 */
	public static final String INIT_AUTH_YN = "initAuthYn";

	/** 기타1권한여부 */
	public static final String ETC1_AUTH_YN = "etc1AuthYn";

	/** 기타2권한여부 */
	public static final String ETC2_AUTH_YN = "etc2AuthYn";

	/** 기타3권한여부 */
	public static final String ETC3_AUTH_YN = "etc3AuthYn";
		
	private String menuNum;
	private String searchAuthYn;
	private String saveAuthYn;
	private String delAuthYn;
	private String reqAuthYn;
	private String aprvAuthYn;
	private String canAuthYn;
	private String closeAuthYn;
	private String printAuthYn;
	private String initAuthYn;
	private String etc1AuthYn;
	private String etc2AuthYn;
	private String etc3AuthYn;
	
	/** 
	 * 생성자
	 *
	 */
	public PsMenuAuth(){		
	}

	/**
	 * @return the aprvAuthYn
	 */
	public String getAprvAuthYn() {
		return aprvAuthYn;
	}

	/**
	 * @param aprvAuthYn the aprvAuthYn to set
	 */
	public void setAprvAuthYn(String aprvAuthYn) {
		this.aprvAuthYn = aprvAuthYn;
	}

	/**
	 * @return the canAuthYn
	 */
	public String getCanAuthYn() {
		return canAuthYn;
	}

	/**
	 * @param canAuthYn the canAuthYn to set
	 */
	public void setCanAuthYn(String canAuthYn) {
		this.canAuthYn = canAuthYn;
	}

	/**
	 * @return the closeAuthYn
	 */
	public String getCloseAuthYn() {
		return closeAuthYn;
	}

	/**
	 * @param closeAuthYn the closeAuthYn to set
	 */
	public void setCloseAuthYn(String closeAuthYn) {
		this.closeAuthYn = closeAuthYn;
	}

	/**
	 * @return the delAuthYn
	 */
	public String getDelAuthYn() {
		return delAuthYn;
	}

	/**
	 * @param delAuthYn the delAuthYn to set
	 */
	public void setDelAuthYn(String delAuthYn) {
		this.delAuthYn = delAuthYn;
	}

	/**
	 * @return the etc1AuthYn
	 */
	public String getEtc1AuthYn() {
		return etc1AuthYn;
	}

	/**
	 * @param etc1AuthYn the etc1AuthYn to set
	 */
	public void setEtc1AuthYn(String etc1AuthYn) {
		this.etc1AuthYn = etc1AuthYn;
	}

	/**
	 * @return the etc2AuthYn
	 */
	public String getEtc2AuthYn() {
		return etc2AuthYn;
	}

	/**
	 * @param etc2AuthYn the etc2AuthYn to set
	 */
	public void setEtc2AuthYn(String etc2AuthYn) {
		this.etc2AuthYn = etc2AuthYn;
	}

	/**
	 * @return the etc3AuthYn
	 */
	public String getEtc3AuthYn() {
		return etc3AuthYn;
	}

	/**
	 * @param etc3AuthYn the etc3AuthYn to set
	 */
	public void setEtc3AuthYn(String etc3AuthYn) {
		this.etc3AuthYn = etc3AuthYn;
	}

	/**
	 * @return the initAuthYn
	 */
	public String getInitAuthYn() {
		return initAuthYn;
	}

	/**
	 * @param initAuthYn the initAuthYn to set
	 */
	public void setInitAuthYn(String initAuthYn) {
		this.initAuthYn = initAuthYn;
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
	 * @return the printAuthYn
	 */
	public String getPrintAuthYn() {
		return printAuthYn;
	}

	/**
	 * @param printAuthYn the printAuthYn to set
	 */
	public void setPrintAuthYn(String printAuthYn) {
		this.printAuthYn = printAuthYn;
	}

	/**
	 * @return the reqAuthYn
	 */
	public String getReqAuthYn() {
		return reqAuthYn;
	}

	/**
	 * @param reqAuthYn the reqAuthYn to set
	 */
	public void setReqAuthYn(String reqAuthYn) {
		this.reqAuthYn = reqAuthYn;
	}

	/**
	 * @return the saveAuthYn
	 */
	public String getSaveAuthYn() {
		return saveAuthYn;
	}

	/**
	 * @param saveAuthYn the saveAuthYn to set
	 */
	public void setSaveAuthYn(String saveAuthYn) {
		this.saveAuthYn = saveAuthYn;
	}

	/**
	 * @return the searchAuthYn
	 */
	public String getSearchAuthYn() {
		return searchAuthYn;
	}

	/**
	 * @param searchAuthYn the searchAuthYn to set
	 */
	public void setSearchAuthYn(String searchAuthYn) {
		this.searchAuthYn = searchAuthYn;
	}
	
}
