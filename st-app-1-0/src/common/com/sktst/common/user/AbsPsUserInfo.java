package com.sktst.common.user;

import java.util.Date;
import java.util.Locale;

import nexcore.framework.core.data.user.AbsUserInfo;
import nexcore.framework.core.data.user.CastingMap;

/**
 * NEXCORE용 필드 처리를 위한 클래스
 * @author admin
 *
 */
public abstract class AbsPsUserInfo extends AbsUserInfo{

    /** 데이터 설정용 map */
    protected CastingMap map = new CastingMap();	

	/** 로그인ID (NEXCORE용) */
    public static final String LOGIN_ID             = "loginId";

	/** 로그인 비밀번호 (NEXCORE용) */
    public static final String LOGIN_PASSWORD       = "loginPassword";
    
	/** 로케일 지역정보 (NEXCORE용) */
    public static final String LOCALE               = "locale";
    
	/** 접속IP (NEXCORE용) */
    public static final String IP                   = "ip";
    
	/** 로그인 시간 (NEXCORE용) */
    public static final String LOGIN_TIME           = "loginTime";
    
	/** 비밀번호 만료일시 (NEXCORE용) */
    public static final String PASSWORD_EXPIRE_DATE = "passwordExpireDate";
    
    public static final String USER_CL = "userCl";
    /**
     * 설정값 취득
     * @return CastingMap 설정값 map
     */
    protected CastingMap getMap(){
        return map;
    }
    
    public String getIp() {
        return getMap().getString(IP);
    }
    
    public Locale getLocale() {
        return getMap().getLocale(LOCALE);
    }
    
    public String getLoginId() {
        return getMap().getString(LOGIN_ID);
    }
    
    public Date getLoginTime() {
        return getMap().getDate(LOGIN_TIME);
    }
    
    public String getLoginPassword() {
        return getMap().getString(LOGIN_PASSWORD);
    }
    
    public String getPasswordExpireDate() {
        return getMap().getString(PASSWORD_EXPIRE_DATE);
    }

    public String getUserCl() {
        return getMap().getString(USER_CL);
    }
    
    public void setIp(String ip) {
        getMap().put(IP, ip);
    }
    
    public void setLocale(Locale locale){
       getMap().put(LOCALE, locale); 
    }
    
    public void setLoginId(String loginId) {
        getMap().put(LOGIN_ID, loginId);
    }
    
    public void setLoginPassword(String loginPassword) {
        getMap().put(LOGIN_PASSWORD, loginPassword);
    }
    
    public void setLoginTime(Date loginTime) {
        getMap().put(LOGIN_TIME, loginTime);
    }
    
    public void setPasswordExpireDate(String passwordExpireDate) {
        getMap().put(PASSWORD_EXPIRE_DATE, passwordExpireDate);
    }
    
    public void setUserCl(String userCl) {
        getMap().put(USER_CL, userCl);        
    }

}
