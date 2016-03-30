/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.bco.biz;

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
 * <li>설 명 : 상품색상을 조회한다.</li>
 * <li>작성일 : 2009-01-21 09:55:31</li>
 * </ul>
 *
 * @author 심연정 (shimyunjung)
 */
public class BASBCO00600 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdColorList(IDataSet requestData,
			IOnlineContext onlineCtx) {
		
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdColorList method start");
		}			

		// 상품에 해당하는 색상을 조회한다.
		IRecordSet prodColorRs = queryForRecordSet("BASBCO00600.getProdColorListByProd",
				requestData.getFieldMap(), onlineCtx);

		if (prodColorRs == null) {
			prodColorRs = new RecordSet("prodColorList");
		}
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(prodColorRs.getRecordCount()) });
		
		responseData.putRecordSet("prodColorList", prodColorRs);
		
		// 상품에 해당하지 않는  전체 색상을 가져온다.
		IRecordSet colorRs = queryForRecordSet("BASBCO00600.getProdColorListByProd_totolColor",
				requestData.getFieldMap());
		
		if (colorRs == null) {
			colorRs = new RecordSet("colorList");
		}		
		responseData.putRecordSet("colorList", colorRs);
		return responseData;
	}

}