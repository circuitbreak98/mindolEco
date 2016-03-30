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
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.CryptoUtils;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : 사용자의 비밀번호를 변경한다.</li>
 * <li>작성일 : 2009-05-20 11:11:08</li>
 * </ul>
 *
 * @author 한병곤 (hanbyunggon)
 */
public class BASUSM00400 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 *  - 비밀번호를 변경한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updatePassWord(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASUSM00400.updatePassWord method start");
		}
		
		int updateCount = 0;
		Map mPassWord = new HashMap();
		
		String sNewPassWordByEncode = CryptoUtils.encode(requestData.getField("newPassWord")); // 새 비밀번호
		String sPassWordByEncode = CryptoUtils.encode(requestData.getField("passWord")); // 현재 비밀번호
		
		mPassWord.put("passWord", sPassWordByEncode);
		mPassWord.put("newPassWord", sNewPassWordByEncode);
		
		// 1. 현재 비밀번호 검증.
		IRecordSet rs = queryForRecordSet("BASUSM00400.getCheckPassWord", mPassWord, onlineCtx);
		
		if(rs.getRecordCount() == 0){
			// 현재 비밀번호가 일치 하지 않습니다.
			throw new BizRuntimeException("PSMW2001");
		}
		
		// 2. 비밀번호 저장.		
		updateCount = update("BASUSM00400.updatePassWord", mPassWord,
				onlineCtx);		
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] {"" + updateCount });
		
		return responseData;
	}

}