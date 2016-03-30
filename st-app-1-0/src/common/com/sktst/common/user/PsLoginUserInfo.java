package com.sktst.common.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.core.util.BaseUtils;


/**
 * 로그인 사용자 정보 클래스 (세션에 보관되는 클래스)
 * 
 * @author admin
 *
 */
public class PsLoginUserInfo extends AbsPsUserInfo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4715933538135145596L;

	//////////////////////////////////////////////////////////////////////////
	//로그인 정보
    //////////////////////////////////////////////////////////////////////////
    /** 사용자이름 */
	public static final String USER_NM		= "userNm";

    /** 사용자그룹 */
	public static final String USER_GRP		= "userGrp";

    /** 직책 */
	public static final String DUTY			= "duty";
	
    /** 조직구분 */
	public static final String ORG_CL		= "orgCl";
    
    /** 조직ID */
	public static final String ORG_ID		= "orgId";

    /** 조직명 */
	public static final String ORG_NM		= "orgNm";
    
    /** 상위조직ID */
	public static final String SUP_ORG_ID	= "supOrgId";
    
    /** 상위조직명 */
	public static final String SUP_ORG_NM	= "supOrgNm";
    
    /** 근무지코드 */
	public static final String ORG_AREA_ID	= "orgAreaId";
	
    /** 근무지구분코드 */
	public static final String ORG_AREA_CL1	= "orgAreaCl1";
    
    /** 근무지명 */
	public static final String ORG_AREA_NM	= "orgAreaNm";    
	
    /** UKey 근무지코드 */
	public static final String ORG_AREA_UKEY_ID	= "orgAreaUkeyId";	
	
    /** 소속 대리점 코드 */
	public static final String POS_AGENCY_ID = "posAgencyId";		
	
	/** 소속 대리점 명 */
	public static final String POS_AGENCY_NM = "posAgencyNm";		
	
	/** 소속 대리점 Ukey 코드 */
	public static final String POS_AGENCY_UKEY_ID = "posAgencyUkeyId";		
	
    /** 근무지 대리점 코드 */
	public static final String ORG_AREA_POS_AGENCY_CD = "orgAreaPosAgencyCd";		
	
	/** 근무지 서브 코드 */
	public static final String ORG_AREA_UKEY_SUB_CD = "orgAreaUkeySubCd";		
	
	/** 근무지 채널 코드 */
	public static final String ORG_AREA_UKEY_CHANNEL_CD = "orgAreaUkeyChannelCd";
	
	
    /** 주민등록번호 */
	public static final String BIZ_JM_NUM		= "bizJmNum";

	/** 실명인증여부 */
	public static final String REAL_BIZ_YN		= "realBizYn";

    /** 판매점포탈 유저 ID */
	public static final String PORTAL_USER_ID		= "portalUserId";

    /** 유저 U.Key ID */
	public static final String U_KEY_ID		= "uKeyId";

	public static final String USER_CL		= "userCl";
	
	/**
     * 생성자
     */
    public PsLoginUserInfo(){
    	super();
    }    
	
    /**
     * 사용자ID 취득
     * @return
     */
    public String getUserId(){
    	return getLoginId();
    }

    /**
     * 사용자ID 설정
     * @param userId
     */
    public void setUserId(String userId){
    	setLoginId(userId);
    }
    
    /**
     * 비밀번호 취득
     * @return
     */
    public String getPwd(){
    	return getLoginPassword();
    }

    /**
     * 비밀번호 설정
     * @param pwd
     */
    public void setPwd(String pwd){
    	setLoginPassword(pwd);
    }

    /**
     * 비밀번호종료일 취득
     * @return
     */
    public String getPwdEndDt(){
    	return getPasswordExpireDate();
    }

    /**
     * 비밀번호종료일 설정
     * @param pwd
     */
    public void setPwdEndDt(String pwdEndDt){
    	setPasswordExpireDate(pwdEndDt);
    }
        
    /**
     * 사용자명 취득
     * @return
     */
	public String getUserNm() {
		return getMap().getString(USER_NM);
	}

	/**
	 * 사용자명 설정
	 * @param
	 */
	public void setUserNm(String userNm) {
		getMap().put(USER_NM, userNm);
	}
	
    /**
     * 사용자그룹코드 취득
     * @return
     */
	public String getUserGrp() {
		return getMap().getString(USER_GRP);
	}

	/**
	 * 사용자그룹코드 설정
	 * @param
	 */
	public void setUserGrp(String userGrp) {
		getMap().put(USER_GRP, userGrp);
	}

    /**
     * 직책 취득
     * @return
     */
	public String getDuty() {
		return getMap().getString(DUTY);
	}

	/**
	 * 직책 설정
	 * @param
	 */
	public void setDuty(String duty) {
		getMap().put(DUTY, duty);
	}
	
    /**
     * 소속조직구분 취득
     * @return
     */
	public String getOrgCl() {
		return getMap().getString(ORG_CL);
	}

	/**
	 * 소속조직구분 설정
	 * @param
	 */
	public void setOrgCl(String orgCl) {
		getMap().put(ORG_CL, orgCl);
	}

    /**
     * 조직ID 취득
     * @return
     */
	public String getOrgId() {
		return getMap().getString(ORG_ID);
	}

	/**
	 * 조직ID 설정
	 * @param
	 */
	public void setOrgId(String orgId) {
		getMap().put(ORG_ID, orgId);
	}

    /**
     * 조직명 취득
     * @return
     */
	public String getOrgNm() {
		return getMap().getString(ORG_NM);
	}

	/**
	 * 조직명 설정
	 * @param
	 */
	public void setOrgNm(String orgNm) {
		getMap().put(ORG_NM, orgNm);
	}

    /**
     * 상위조직ID 취득
     * @return
     */
	public String getSupOrgId() {
		return getMap().getString(SUP_ORG_ID);
	}

	/**
	 * 상위조직ID 설정
	 * @param
	 */
	public void setSupOrgId(String supOrgId) {
		getMap().put(SUP_ORG_ID, supOrgId);
	}

    /**
     * 상위조직명 취득
     * @return
     */
	public String getSupOrgNm() {
		return getMap().getString(SUP_ORG_NM);
	}

	/**
	 * 상위조직명 설정
	 * @param
	 */
	public void setSupOrgNm(String supOrgNm) {
		getMap().put(SUP_ORG_NM, supOrgNm);
	}

    /**
     * 근무지ID 취득
     * @return
     */
	public String getOrgAreaId() {
		return getMap().getString(ORG_AREA_ID);
	}

	/**
	 * 근무지ID 설정
	 * @param
	 */
	public void setOrgAreaId(String orgAreaId) {
		getMap().put(ORG_AREA_ID, orgAreaId);
	}
	
    /**
     * 근무지구분코드 취득
     * @return
     */
	public String getOrgAreaCl1() {
		return getMap().getString(ORG_AREA_CL1);
	}

	/**
	 * 근무지구분코드 설정
	 * @param
	 */
	public void setOrgAreaCl1(String orgAreaCl1) {
		getMap().put(ORG_AREA_CL1, orgAreaCl1);
	}	

    /**
     * 근무지명 취득
     * @return
     */
	public String getOrgAreaNm() {
		return getMap().getString(ORG_AREA_NM);
	}

	/**
	 * 근무지명 설정
	 * @param
	 */
	public void setOrgAreaNm(String orgAreaNm) {
		getMap().put(ORG_AREA_NM, orgAreaNm);
	}
	
	/**
	 * UKEY 근무지ID 취득
	 * @param
	 */
	public String getOrgAreaUkeyId() {
		return getMap().getString(ORG_AREA_UKEY_ID);
	}
	
	/**
	 * UKEY 근무지ID 설정
	 * @param
	 */
	public void setOrgAreaUkeyId(String orgAreaUkeyId) {
		getMap().put(ORG_AREA_UKEY_ID, orgAreaUkeyId);
	}
	
	/**
	 * 소속 대리점 ID 취득
	 * @param
	 */
	public String getPosAgencyId() {
		return getMap().getString(POS_AGENCY_ID);
	}
	
	/**
	 * 소속 대리점 ID 설정
	 * @param
	 */
	public void setPosAgencyId(String posAgencyId) {
		getMap().put(POS_AGENCY_ID, posAgencyId);
	}
	
	/**
	 * 소속 대리점 명 취득
	 * @param
	 */
	public String getPosAgencyNm() {
		return getMap().getString(POS_AGENCY_NM);
	}
	
	/**
	 * 소속 대리점 명 설정
	 * @param
	 */
	public void setPosAgencyNm(String posAgencyNm) {
		getMap().put(POS_AGENCY_NM, posAgencyNm);
	}	
	
	/**
	 * 소속 대리점 Ukey ID 취득
	 * @param
	 */
	public String getPosAgencyUkeyId() {
		return getMap().getString(POS_AGENCY_UKEY_ID);
	}
	
	/**
	 * 소속 대리점 Ukey ID 설정
	 * @param
	 */
	public void setPosAgencyUkeyId(String posAgencyUkeyId) {
		getMap().put(POS_AGENCY_UKEY_ID, posAgencyUkeyId);
	}	
	
	
	
	/**
	 * 근무지 대리점 코드 취득
	 */
	public String getOrgAreaPosAgencyCd() {
		return getMap().getString(ORG_AREA_POS_AGENCY_CD);
	}

	/**
	 * 근무지 대리점 코드 설정
	 * @param orgAreaAgencyCd
	 */
	public void setOrgAreaPosAgencyCd(String orgAreaPosAgencyCd) {
		getMap().put(ORG_AREA_POS_AGENCY_CD, orgAreaPosAgencyCd);
	}

	/**
	 * 근무지 서브 코드 취득
	 */
	public String getOrgAreaUkeySubCd() {
		return getMap().getString(ORG_AREA_UKEY_SUB_CD);
	}

	/**
	 * 근무지 서브 코드 설정
	 * @param orgAreaUkeySubCd
	 */
	public void setOrgAreaUkeySubCd(String orgAreaUkeySubCd) {
		getMap().put(ORG_AREA_UKEY_SUB_CD, orgAreaUkeySubCd);
	}

	/**
	 * 근무지 채널 코드 취득
	 */
	public String getOrgAreaUkeyChannelCd() {
		return getMap().getString(ORG_AREA_UKEY_CHANNEL_CD);
	}

	/**
	 * 근무지 채널 코드 설정
	 * @param orgAreaUkeyChannelCd
	 */
	public void setOrgAreaUkeyChannelCd(String orgAreaUkeyChannelCd) {
		getMap().put(ORG_AREA_UKEY_CHANNEL_CD, orgAreaUkeyChannelCd);
	}
	
	
	
	/**
	 * 주민등록번호 취득
	 */
	public String getBizJmNum() {
		return getMap().getString(BIZ_JM_NUM);
	}
	
	/**
	 * 주민등록번호 설정
	 */
	public void setBizJmNum(String bizJmNum) {
		getMap().put(BIZ_JM_NUM, bizJmNum);
	}

	
	/**
	 * 실명인증여부 취득
	 */
	public String getRealBizYn() {
		return getMap().getString(REAL_BIZ_YN);
	}
	
	/**
	 * 실명인증여부 설정
	 */
	public void setRealBizYn(String realBizYn) {
		getMap().put(REAL_BIZ_YN, realBizYn);
	}
	

	/**
	 * 판매점포탈 유저 ID 취득
	 */
	public String getPortalUserId() {
		return getMap().getString(PORTAL_USER_ID);
	}
	
	/**
	 * 판매점포탈 유저 ID 설정
	 */
	public void setPortalUserId(String portalUserId) {
		getMap().put(PORTAL_USER_ID, portalUserId);
	}


	/**
	 * 유저 U.key ID 취득
	 */
	public String getUKeyId() {
		return getMap().getString(U_KEY_ID);
	}
	
	/**
	 * 유저 U.key ID 설정
	 */
	public void setUKeyId(String uKeyId) {
		getMap().put(U_KEY_ID, uKeyId);
	}
	
	public String getUserCl() {
		return getMap().getString(USER_CL);
	}
	
	public void setUserCl(String userCl) {
		getMap().put(USER_CL, userCl);
	}

    /**
     * 설정정보를 map으로 리턴
     * @return
     */
    public Map getUserInfoMap(){
    	Map<String, String> result = new HashMap<String, String>();
    	result.put(LOGIN_ID, getLoginId());
    	result.put(LOGIN_PASSWORD, getLoginPassword());
    	result.put(IP, getIp());
    	result.put(LOGIN_TIME, getLoginTime().toString());
    	result.put(PASSWORD_EXPIRE_DATE, getPasswordExpireDate());
		
    	result.put(USER_NM, getUserNm());
    	result.put(USER_GRP, getUserGrp());
    	result.put(DUTY, getDuty());
    	result.put(ORG_CL, getOrgCl());
    	result.put(ORG_ID, getOrgId());
    	result.put(ORG_NM, getOrgNm());
    	result.put(SUP_ORG_ID, getSupOrgId());
    	result.put(SUP_ORG_NM, getSupOrgNm());
    	result.put(ORG_AREA_ID, getOrgAreaId());
    	result.put(ORG_AREA_CL1, getOrgAreaCl1());
    	result.put(ORG_AREA_NM, getOrgAreaNm());
    	result.put(ORG_AREA_UKEY_ID, getOrgAreaUkeyId());
    	result.put(POS_AGENCY_ID, getPosAgencyId());
    	result.put(POS_AGENCY_NM, getPosAgencyNm());
    	result.put(POS_AGENCY_UKEY_ID, getPosAgencyUkeyId());
    	
    	result.put(ORG_AREA_POS_AGENCY_CD, getOrgAreaPosAgencyCd());
    	result.put(ORG_AREA_UKEY_SUB_CD, getOrgAreaUkeySubCd());
    	result.put(ORG_AREA_UKEY_CHANNEL_CD, getOrgAreaUkeyChannelCd());

    	result.put(BIZ_JM_NUM, getBizJmNum());
    	result.put(REAL_BIZ_YN, getRealBizYn());
    	result.put(PORTAL_USER_ID, getPortalUserId());
    	//result.put(U_KEY_ID, getUKeyId());
    	result.put(U_KEY_ID, "");
    	//System.out.println("===========: "+getUKeyId());
    	result.put(USER_CL, getUserCl());

    	return result;
    }
}