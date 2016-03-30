package com.sktst.common.user;

import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.online.biz.auth.IWebUserManager;

/**
 * SKT PS 사용자 관리 컴포넌트 인터페이스
 * @author admin
 *
 */
public interface IPsUserManager extends IWebUserManager{

	/**
	 * 로그인 사용자 정보 취득
	 * @param userId
	 * @return
	 */
	IUserInfo getLoginUser(String userId);
}
