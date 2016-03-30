/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.inn.biz;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-08-08 09:56:28</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class DISINN01400 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveProdCdChg(IDataSet requestData, IOnlineContext onlineCtx) {
		
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN01400.saveProdCdChg method start");
		}
		
		Map req = requestData.getFieldMap();
		
		Map inMap = new HashMap();
		
		inMap.put("prod_cd", req.get("AF_PROD_CD"));
		inMap.put("ser_num", req.get("AF_SER_NUM"));
		inMap.put("color_cd", req.get("COLOR_CD"));
		inMap.put("con_mgmt_no", req.get("CON_MGMT_NO"));
		inMap.put("g_ser_num", req.get("G_SER_NUM"));
		inMap.put("bef_prod_cd", req.get("PROD_CD"));
		inMap.put("bef_ser_num", req.get("SER_NUM"));
		inMap.put("ins_user_id", req.get("USER_ID"));
		inMap.put("errcode", "");
		inMap.put("errmsg", "");
		log.debug("inMap : " + inMap);
		

		queryForObject("DISINN01400.updateProdCdChg", inMap, onlineCtx);
		// TODO 업무 로직 작성 필요
		
		log.debug("errmsg : " + inMap.get("errmsg"));
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(1) });
		return responseData;
	}

}