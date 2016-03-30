package com.sktst.common.user;

/**
 * 사용자 정보 클래스
 * 
 * @author admin
 *
 */
public class PsUserInfo extends AbsPsUserInfo{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7573397308070268950L;

	//////////////////////////////////////////////////////////////////////////
	//사용자 정보 테이블의  필드 정보
    //////////////////////////////////////////////////////////////////////////
    /** 사용자 ID : NEXCORE LOGIN_ID와 연동 */
    protected static final String USER_ID		= "userId";
    
    /** UKey ID */
    protected static final String U_KEY_ID		= "uKeyId";
    
    /** 사번 */
    protected static final String USER_CD		= "userCd";
    
    /** 사용자이름 */
    protected static final String USER_NM		= "userNm";
    
    /** 비밀번호 : NEXCORE LOGIN_PASSWORD와 연동 */
    protected static final String PWD			= "pwd";
    
    /** 비밀번호종료일 : NEXCORE PASSWORD_EXPIRE_DATE와 연동 */
    protected static final String PWD_END_DT	= "pwdEndDt";    
    
    /** 유효사용자여부 */
    protected static final String EFF_USER_YN	= "effUserYn";
    
    /** 유선전화 */
    protected static final String WPHON		= "wphon";
    
    /** 이동전화 */
    protected static final String MBL_PHON		= "mblPhon";

    /** 이메일1 */
    protected static final String EMAIL1		= "email1";

    /** 이메일2 */
    protected static final String EMAIL2		= "email2";    
    
    /** 사용자그룹 */
    protected static final String USER_GRP	= "userGrp";
    
    /** 직책 */
    protected static final String DUTY		= "duty";
    
    /** 조직ID */
    protected static final String ORG_ID		= "orgId";
    
    /** 근무지 */
    protected static final String ORG_AREA		= "orgArea";
    
    /** 비고 */
    protected static final String RMKS			= "rmks";
    
    /** 삭제여부 */
    protected static final String DEL_YN		= "delYn";
        
    /** 주민등록번호 */
    protected static final String BIZ_JM_NUM		= "bizJmNum";

    /** 실명인증여부 */
    protected static final String REAL_BIZ_YN		= "realBizYn";
    
    /** 판매점포탈 유저 ID */
    protected static final String PORTAL_USER_ID		= "portalUserId";

    /** 비밀번호 구분 */
    protected static final String P_FLAG		= "pFlag";
    
    /** 사용자 구분  */
    protected static final String USER_CL		= "userCl";
    

    
//    /** UPDATE_COUNT */
//    protected static final String UPD_CNT	= "updCnt";
//    
//    /** 입력일시 */
//    protected static final String INS_DTM		= "insDtm";
//    
//    /** 입력자 */
//    protected static final String INS_USER_ID	= "insUserId";
//    
//    /** 수정일시 */
//    protected static final String MOD_DTM		= "modDtm";
//    
//    /** 수정자 */
//    protected static final String MOD_USER_ID	= "modUserId";
        
        
    /**
     * 생성자
     */
    public PsUserInfo(){
    	super();
    }    
	
    /**
     * 
     * @return
     */
	public String getUserId() {
		//return getMap().getString(USER_ID);
		return getLoginId();
	}

	/**
	 * 
	 * @return
	 */
	public String getUKeyId() {
		return getMap().getString(U_KEY_ID);
	}

	/**
	 * 
	 * @return
	 */
	public String getUserCd() {
		return getMap().getString(USER_CD);
	}

	/**
	 * 
	 * @return
	 */
	public String getUserNm() {
		return getMap().getString(USER_NM);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPwd() {
		//return getMap().getString(PWD);
		return getLoginPassword();
	}

	/**
	 * 
	 * @return
	 */
	public String getPwdEndDt() {
		//return getMap().getString(PWD_END_DT);
		return getPasswordExpireDate();
	}

	/**
	 * 
	 * @return
	 */
	public String getEffUserYn() {
		return getMap().getString(EFF_USER_YN);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getWphon() {
		return getMap().getString(WPHON);
	}

	/**
	 * 
	 * @return
	 */
	public String getMblPhon() {
		return getMap().getString(MBL_PHON);
	}

	/**
	 * 
	 * @return
	 */
	public String getEmail1() {
		return getMap().getString(EMAIL1);
	}

	/**
	 * 
	 * @return
	 */
	public String getEmail2() {
		return getMap().getString(EMAIL2);
	}	
	
	/**
	 * 
	 * @return
	 */
	public String getUserGrp() {
		return getMap().getString(USER_GRP);
	}

	/**
	 * 
	 * @return
	 */
	public String getDuty() {
		return getMap().getString(DUTY);
	}	

	/**
	 * 
	 * @return
	 */
	public String getOrgId() {
		return getMap().getString(ORG_ID);
	}

	/**
	 * 
	 * @return
	 */
	public String getOrgArea() {
		return getMap().getString(ORG_AREA);
	}

	/**
	 * 
	 * @return
	 */
	public String getRmks() {
		return getMap().getString(RMKS);
	}

	/**
	 * 
	 * @return
	 */
	public String getDelYn() {
		return getMap().getString(DEL_YN);
	}

	/**
	 * 
	 * @return
	 */
	public String getBizJmNum() {
		return getMap().getString(BIZ_JM_NUM);
	}

	/**
	 * 
	 * @return
	 */
	public String getRealBizYn() {
		return getMap().getString(REAL_BIZ_YN);
	}

	/**
	 * 
	 * @return
	 */
	public String getPortalUserId() {
		return getMap().getString(PORTAL_USER_ID);
	}

	/**
	 * 
	 * @return
	 */
	public String getPFlag() {
		return getMap().getString(P_FLAG);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUserCl() {
		return getMap().getString(USER_CL);
	}
	
//	/**
//	 * 
//	 * @return
//	 */
//	public int getUpdCnt() {
//		return getMap().getInt(UPD_CNT);
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	public String getInsDtm() {
//		return getMap().getString(INS_DTM);
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	public String getInsUserId() {
//		return getMap().getString(INS_USER_ID);
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	public String getModDtm() {
//		return getMap().getString(MOD_DTM);
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	public String getModUserId() {
//		return getMap().getString(MOD_USER_ID);
//	}
	
    /**
     * 
     * @param
     */
	public void setUserId(String userId) {
		//getMap().put(USER_ID, userId);
		setLoginId(userId); //NEXCORE 연동
	}

	/**
	 * 
	 * @param
	 */
	public void setUKeyId(String uKeyId) {
		getMap().put(U_KEY_ID, uKeyId);
	}

	/**
	 * 
	 * @param
	 */
	public void setUserCd(String userCd) {
		getMap().put(USER_CD, userCd);		
	}

	/**
	 * 
	 * @param
	 */
	public void setUserNm(String userNm) {
		getMap().put(USER_NM, userNm);		
	}
	
	/**
	 * 
	 * @param
	 */
	public void setPwd(String pwd) {
		//getMap().put(PWD, pwd);
		setLoginPassword(pwd); //NEXCORE 연동		
	}

	/**
	 * 
	 * @param
	 */
	public void setPwdEndDt(String pwdEndDt) {
		//getMap().put(PWD_END_DT, pwdEndDt);
		setPasswordExpireDate(pwdEndDt); //NEXCORE 연동		
	}	

	/**
	 * 
	 * @param
	 */
	public void setEffUserYn(String effUserYn) {
		getMap().put(EFF_USER_YN, effUserYn);
	}
	
	/**
	 * 
	 * @param
	 */
	public void setWphon(String wphon) {
		getMap().put(WPHON, wphon);
	}

	/**
	 * 
	 * @param
	 */
	public void setMblPhon(String mblPhon) {
		getMap().put(MBL_PHON, mblPhon);		
	}

	/**
	 * 
	 * @param
	 */
	public void setEmail1(String email1) {
		getMap().put(EMAIL1, email1);		
	}

	/**
	 * 
	 * @param
	 */
	public void setEmail2(String email2) {
		getMap().put(EMAIL2, email2);		
	}	
	
	/**
	 * 
	 * @param
	 */
	public void setUserGrp(String userGrp) {
		getMap().put(USER_GRP, userGrp);
	}
	
	/**
	 * 
	 * @param
	 */
	public void setDuty(String duty) {
		getMap().put(DUTY, duty);
	}

	/**
	 * 
	 * @param
	 */
	public void setOrgId(String orgId) {
		getMap().put(ORG_ID, orgId);		
	}

	/**
	 * 
	 * @param
	 */
	public void setOrgArea(String orgArea) {
		getMap().put(ORG_AREA, orgArea);			
	}

	/**
	 * 
	 * @param
	 */
	public void setRmks(String rmks) {
		getMap().put(RMKS, rmks);
	}

	/**
	 * 
	 * @param
	 */
	public void setDelYn(String delYn) {
		getMap().put(DEL_YN, delYn);
	}

	/**
	 * 
	 * @param
	 */
	public void setBizJmNum(String bizJmNum) {
		getMap().put(BIZ_JM_NUM, bizJmNum);
	}

	/**
	 * 
	 * @param
	 */
	public void setRealBizYn(String realBizYn) {
		getMap().put(REAL_BIZ_YN, realBizYn);
	}

	/**
	 * 
	 * @param
	 */
	public void setPortalUserId(String portalUserId) {
		getMap().put(PORTAL_USER_ID, portalUserId);
	}

	public void setPFlag(String pFlag) {
		getMap().put(P_FLAG, pFlag);
	}
	
	public void setUserCl(String userCl) {
		getMap().put(USER_CL, userCl);
	}
	
//	/**
//	 * 
//	 * @param
//	 */
//	public void setUpdCnt(int updCnt){
//		getMap().put(UPD_CNT, updCnt);
//	}
//	
//	/**
//	 * 
//	 * @param
//	 */
//	public void setInsDtm(String insDtm) {
//		getMap().put(INS_DTM, insDtm);
//	}
//	/**
//	 * 
//	 * @param
//	 */
//	public void setInsUserId(String insUserId) {
//		getMap().put(INS_USER_ID, insUserId);
//	}
//	/**
//	 * 
//	 * @param
//	 */
//	public void setModDtm(String modDtm) {
//		getMap().put(MOD_DTM, modDtm);
//	}
//	/**
//	 * 
//	 * @param
//	 */
//	public void setModUserId(String modUserId) {
//		getMap().put(MOD_USER_ID, modUserId);
//	}
}
