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
 * <li>설 명 : </li>
 * <li>작성일 : 2009-06-01 11:22:41</li>
 * </ul>
 *
 * @author 한병곤 (hanbyunggon)
 */
public class BASUSM00600 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getP(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASUSM00600.getP method start");
		}		
		
		Map mSuccessYn = new HashMap();
		Map mParam = requestData.getFieldMap();
		
//		Map mUserData = (Map) queryForObject("BASUSM00600.getP", mParam);
//		
//		if (mUserData == null) {
//			mSuccessYn.put("Dpwd", "No User");
//		}else{		
//			
//			String sPwd = (String)mUserData.get("PWD");	
//			
//			System.err.println(sPwd);
//			
//			mSuccessYn.put("Dpwd", CryptoUtils.decode(sPwd));
//		}
//		
		String sP = (String)mParam.get("password");
		
		mSuccessYn.put("Dpwd", CryptoUtils.decode(sP));
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_SIMPLE_MESSAGE_ID, new String[] {"0"});		
		
		responseData.putFieldMap(mSuccessYn);

		return responseData;
	}

}