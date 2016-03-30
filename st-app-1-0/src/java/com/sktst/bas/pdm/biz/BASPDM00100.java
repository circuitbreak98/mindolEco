/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.pdm.biz;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : 상품 검색</li>
 * <li>작성일 : 2009-01-20 11:29:31</li>
 * </ul>
 *
 * @author 전현주 (junhyunjoo)
 */
public class BASPDM00100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * @title  상품 목록 조회
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : PROD_CL [필드1] 
	 *	- field : PROD_NM [필드2] 
	 *	- field : MFACT_ID [필드3] 
	 *	- field : SKT_OPER_YN [필드4] 
	 *	- field : PROD_CHRTIC_1 [필드5] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : PROD_CL [필드1] 
	 *	- field : MFACT_ID [필드2] 
	 *	- field : PROD_NM [필드3] 
	 *	- field : PROD_CD [필드4] 
	 *	- field : SKT_OPER_YN [필드5] 
	 *	- field : PROD_CHRTIC_1 [필드6] 
	 *	- field : PROD_CHRTIC_2 [필드7] 
	 *	- field : END_DT [필드8] 
	 */
	public IDataSet getProductListByProdCl(IDataSet requestData,
			IOnlineContext onlineCtx) {
		
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProductListByProdCl method start");
		}
		
		IRecordSet rs = queryForRecordSet("BASPDM00100.getProductList",
				requestData.getFieldMap());
		
		if (rs == null) {
			rs = new RecordSet("ds_prod_mgmt");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_prod_mgmt", rs);
		return responseData;
	}
}