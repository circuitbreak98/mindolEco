package com.sktst.common.user;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nexcore.framework.core.Constants;
import nexcore.framework.core.exception.CertificationFailedException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.prototype.AbsFwkService;
import nexcore.framework.core.util.BaseUtils;
import nexcore.framework.core.util.DateUtils;
import nexcore.framework.online.biz.auth.IAuthManager;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;
import com.sktst.common.sso.SHA1Hash;
import com.sktst.common.sso.SHA256SaltHash;

/**
 * 로그인/로그아웃 확장 컴포넌트
 * @author admin
 *
 */
public class PsAuthManager extends AbsFwkService implements IAuthManager {

	/**
     * IUserManager
     */
    private IPsUserManager userManager = null;

    /**
     * IUserManager 설정 (nexcore-biz.xml에서 설정)
     * @param userManager
     */
    public void setUserManager(IPsUserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * 로그인 처리를 수행한다.
     * @param userId 사용자ID
     * @param password 패스워드
     * @param locale 로케일정보문자열
     * @param readProtocol 
     * @param writeProtocol
     */
	public void login(String userId, String password, String locale, Object readProtocol, Object writeProtocol) throws CertificationFailedException {
		
		Log logger = LogManager.getFwkLog();
		byte[] sByte = null;
		
		if (!(readProtocol instanceof HttpServletRequest)) {
            if (logger.isErrorEnabled()) {
                logger.error("Read protocol interface type is't HttpServletRequest");
            }
            throw new RuntimeException("Read Protocol interface isn't HttpServletRequest.");
        }
        HttpServletRequest request = (HttpServletRequest) readProtocol;
        
        PsUserInfo userInfo = (PsUserInfo)userManager.getUser(userId);

        logger.debug("****************userInfo******************");
        logger.debug(userInfo);
        logger.debug("****************userInfo*******************");
        
        if (userInfo == null) {
        	//사용자 정보가 없는 경우
            throw new CertificationFailedException(BaseConstants.LOGIN_ID_FAIL);
        }
        if(!"Y".equalsIgnoreCase(userInfo.getEffUserYn())){
        	//사용중지된 사용자의 경우
        	throw new CertificationFailedException(BaseConstants.LOGIN_USE_FAIL);
        }

        SHA1Hash md = new SHA1Hash();
        SHA256SaltHash enc = new SHA256SaltHash(); 
        if (password.equals("sso")) {        
        } else {

       	   if("Y".equalsIgnoreCase(userInfo.getPFlag())){
       		   
       		logger.debug("*******************YYYYYY*************************");
       		logger.debug("enc.encode(password,userId):"+enc.encode(password,userId));
       		logger.debug("userInfo.getPwd():"+userInfo.getPwd());
       		logger.debug("*******************YYYYYY*************************");
       		               
       		System.out.println("*******************YYYYYY*************************");
       		System.out.println("enc.encode(password,userId):"+enc.encode(password,userId));
       		System.out.println("userInfo.getPwd():"+userInfo.getPwd());
       		System.out.println("*******************YYYYYY*************************");       		   
       		   
       		   
       		   
       			  if (!enc.encode(password,userId).equals(userInfo.getPwd())) {	  
         			 throw new CertificationFailedException(BaseConstants.LOGIN_PWD_FAIL);
                  }  
        	   ////SHA-1 암호화 방식
        	   }else {
        		   
        		   
        		   logger.debug("*******************NNNNNN*************************");
        		   logger.debug("md.hash(password):"+md.hash(password));
        		   logger.debug("userInfo.getPwd():"+userInfo.getPwd());
        		   logger.debug("*******************NNNNNN*************************");
        		           		   
        		   System.out.println("*******************NNNNNNN*************************");
        		   System.out.println("md.hash(password):"+md.hash(password));
        		   System.out.println("userInfo.getPwd():"+userInfo.getPwd());
        		   System.out.println("*******************NNNNNNNN*************************");
        		   
        		   
        		 if (!md.hash(password).equals(userInfo.getPwd())) {        //SHA-1 암호화 방식
                   //if (!password.equals(CryptoUtils.decode(userInfo.getPwd()))) {        //AES 암호화방식
                       //비밀번호가 틀린 경우ㄷ
                       throw new CertificationFailedException(BaseConstants.LOGIN_PWD_FAIL);
                   }      	   
        	   }        	

        }
        
        // IP 대역폭을 체크한다.계정이 D로 시작되는 사용자만.
        String sIpAddr = request.getRemoteAddr();
        String sUserIdHeader = (userId.substring(0, 1)).toUpperCase();
        String[] aIp = sIpAddr.split("[.]");
        String sAclass = aIp[0];
        String sBclass = aIp[1];
        int iCclass = Integer.parseInt(aIp[2]);
        
        //90.5.192.0 ~ 90.5.255.255 사용 불가.
        if(sUserIdHeader.equals("D")){
        	
        	if(sAclass == "90" && sBclass == "5" ){
        		
        		if(iCclass >= 192 && iCclass <= 255){
        			 throw new CertificationFailedException(BaseConstants.LOGIN_IP_FAIL);
        		}
        	}
        }
        
        //비밀번호 만료일 확인 및 처리.
        String currentDate = DateUtils.getCurrentDate();
        String passwordExpireDate = userInfo.getPwdEndDt();
        String userGrp = userInfo.getUserGrp();
        
        boolean isSetAttribute = false;
        String sAdmCd = "ADM";
        
        /**
        //비밀번호 만료일이 설정되어 있는 경우.
        if(passwordExpireDate != null && !passwordExpireDate.trim().equals("")){
        	
        	//유저그룹이 ADM(super admin)이 아닌경우.
        	if( userGrp != null && !userGrp.equals(sAdmCd)){
        		
	            int dayGap = DateUtils.getDaysBetween(currentDate, passwordExpireDate);        
	            if(dayGap <= 7){
	            	if(dayGap >= 0){
	                	//비밀번호 만료일 7일전.
	            		request.setAttribute(BaseConstants.LOGIN_RESULT_KEY, BaseConstants.LOGIN_PWD_EXPIRE_CHECK);
	            		request.setAttribute(BaseConstants.LOGIN_RESULT_PARAMETER, new String[]{DateUtils.formatDate(passwordExpireDate,"-"), ""+dayGap});
	            		isSetAttribute = true;
	            	}else{
	                	//비밀번호 만료일 경과 처리.
	            		throw new CertificationFailedException(BaseConstants.LOGIN_PWD_EXPIRE_FAIL);
	            	}
	            }     
        	}
        }
        **/
        
        PsLoginUserInfo loginUserInfo = (PsLoginUserInfo)userManager.getLoginUser(userId);
        if(loginUserInfo == null){
        	//세션  정보 취득 실패의 경우
        	throw new CertificationFailedException(BaseConstants.LOGIN_ORG_FAIL);
        }

        // 사용자가 접속한 시간(java.util.Date) 설정
        loginUserInfo.setLoginTime(new Date());
        
        // 사용자 Locale 설정
        loginUserInfo.setLocale(BaseUtils.asLocale(locale));
        
        // 사용자가 접속한 IP 주소를 세팅한다.
        if (request != null) {
        	loginUserInfo.setIp(request.getRemoteAddr());
        }

        // 로그인 정보 세션 설정 
        //userManager.setUserInfo(request, loginUserInfo);
        if (request != null) {            
            HttpSession session = request.getSession(true);
            if (session != null) {
                session.setAttribute(Constants.USER, loginUserInfo);
            }
        }
        
        if (!isSetAttribute) {
			request.setAttribute(BaseConstants.LOGIN_RESULT_KEY, BaseConstants.LOGIN_SUCCESS);
			request.setAttribute(BaseConstants.LOGIN_RESULT_PARAMETER, new String[]{});
        }
	}

    /**
     * 로그아웃 처리를 수행한다.
     * @param readProtocol 
     * @param writeProtocol
     */
	public void logout(Object readProtocol, Object writeProtocol) {
		Log logger = LogManager.getFwkLog();

		if (!(readProtocol instanceof HttpServletRequest)) {
            if (logger.isErrorEnabled()) {
                logger.error("Read protocol interface type is't HttpServletRequest");
            }
            throw new RuntimeException("Read Protocol interface isn't HttpServletRequest.");
        }
		
		HttpServletRequest request = (HttpServletRequest) readProtocol;

		if (request != null){
			
			HttpSession session = request.getSession(true);
			if (session != null){
				
				//로그인 정보 세션 삭제
				session.invalidate();
			}			
		}		
	}  

	// 불필요 메소드 정리 ////////////////////////////////////////
	/* (non-Javadoc)
	 * @see nexcore.framework.online.biz.auth.IAuthManager#login(java.lang.String, java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public void login(String userId, String password, String locale, HttpServletRequest request) throws CertificationFailedException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see nexcore.framework.online.biz.auth.IAuthManager#logout(javax.servlet.http.HttpServletRequest)
	 */
	public void logout(HttpServletRequest request) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see nexcore.framework.online.biz.auth.IAuthManager#isAccessPermitted()
	 */
	public boolean isAccessPermitted() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
