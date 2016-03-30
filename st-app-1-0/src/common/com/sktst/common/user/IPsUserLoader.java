package com.sktst.common.user;

import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.online.biz.auth.IUserInfoLoader;


/**
 * SKT PS 사용자 정보 취득 컴포넌트 인터페이스
 * @author admin
 *
 */
public interface IPsUserLoader extends IUserInfoLoader{

	/**
	 * 로그인 사용자 정보 취득
	 * @param userId
	 * @return
	 */
	IUserInfo loadLoginInfo(String userId);
}
