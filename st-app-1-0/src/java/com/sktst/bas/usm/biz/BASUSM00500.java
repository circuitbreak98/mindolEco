/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.usm.biz;

import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.CryptoUtils;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : 사용자의 패스워드를 초기화한다.</li>
 * <li>작성일 : 2009-05-28 19:18:43</li>
 * </ul>
 *
 * @author 한병곤 (hanbyunggon)
 */
public class BASUSM00500 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 *  - 사용자의 패스워드를 초기화한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public IDataSet updatePassWordReset(IDataSet requestData,
			IOnlineContext onlineCtx) {
		
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASUSM00500.updatePassWordReset method start");
		}		
		
		Map mSuccessYn = new HashMap();
		Map mParam = requestData.getFieldMap();
		
		// ID, Email check.
		Map mUserData = (Map) queryForObject("BASUSM00500.getUserData", mParam);
		
		if (mUserData == null) {
			mSuccessYn.put("success_yn", "N");
		}else{		
			
			// 패스워드를 0000으로 초기화 한다.
			mParam.put("passWord", CryptoUtils.encode("0000"));
			update("BASUSM00500.updatePassWordReset", mParam);				
			
			mSuccessYn.put("success_yn", "Y");
		}
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_SIMPLE_MESSAGE_ID, new String[] {"0"});		
		
		responseData.putFieldMap(mSuccessYn);

		return responseData;
	}

}