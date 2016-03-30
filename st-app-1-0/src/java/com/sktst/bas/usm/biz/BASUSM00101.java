/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.usm.biz;

import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.CryptoUtils;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.common.sso.SHA1Hash;
import com.sktst.common.user.PsLoginUserInfo;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2010-07-23 11:49:33</li>
 * </ul>
 *
 * @author 장화수 (janghwasoo)
 */
public class BASUSM00101 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 장화수 (janghwasoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getUserCnv(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getUserCnv method start");
		}
		Map oMap = null;
		
		Map fields = requestData.getFieldMap();
		IRecordSet rs = queryForRecordSet("BASUSM00101.getUserCnv", fields,
				onlineCtx);
		if (rs == null) {
			rs = new RecordSet("userList");
		}

	   SHA1Hash md = new SHA1Hash();
		for (int i = 0; i < rs.getRecordCount(); i++) {
			oMap = rs.getRecordMap(i);
			
			if (oMap.get("SPWD").toString().length() == 40) {
				//oMap.put("PWD", md.hash(CryptoUtils.decode(oMap.get("PWD").toString())));								
			} else if (oMap.get("SPWD").toString().equals("0000")) {
			} else {
				//oMap.put("PWD", md.hash(CryptoUtils.decode(oMap.get("PWD").toString())));				
				oMap.put("SPWD", CryptoUtils.decode(oMap.get("SPWD").toString()));	
				rs.getRecord(i).put("SPWD", oMap.get("SPWD").toString());
			}
			//System.out.println(oMap.get("PWD").toString());
						
			//oMap.put("SPWD", CryptoUtils.decode(oMap.get("SPWD").toString()));	
			rs.getRecord(i).put("SPWD", oMap.get("SPWD").toString());
			if (oMap.get("BIZ_JM_NUM") == null || oMap.get("BIZ_JM_NUM").toString().equals("")) {
				rs.getRecord(i).put("BIZ_JM_NUM", "");
				rs.getRecord(i).put("M_BIZ_JM_NUM", "");
			} else {				
				oMap.put("BIZ_JM_NUM", CryptoUtils.decode(oMap.get("BIZ_JM_NUM").toString()));
				rs.getRecord(i).put("BIZ_JM_NUM", oMap.get("BIZ_JM_NUM").toString());
				rs.getRecord(i).put("M_BIZ_JM_NUM", oMap.get("BIZ_JM_NUM").toString());
//				rs.getRecord(i).put("SPWD", "");
			}
			//rsd.put("PWD", oMap.get("PWD").toString());
			
		}
		
		
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_userList", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 장화수 (janghwasoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateUserCnv(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("updateUserCnv method start");
		}
		PsLoginUserInfo userInfo = (PsLoginUserInfo) onlineCtx.getUserInfo();

		IRecordSet rs = null;
		int updateCount = 0;
		int deleteCount = 0;
		int insertCount = 0;
		int cudAllCount = 0;
		Map oMap = null;

		rs = requestData.getRecordSet("ds_userList");
		for (int i = 0; i < rs.getRecordCount(); i++) {
			oMap = rs.getRecordMap(i);
			updateCount = updateCount + update("BASUSM00101.updateUserCnv", oMap,onlineCtx); 
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount, "" + deleteCount });
	}

}