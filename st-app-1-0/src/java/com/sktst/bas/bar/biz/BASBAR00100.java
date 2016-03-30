/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.bar.biz;

import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-03-07 10:12:57</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class BASBAR00100 extends BaseBizUnit {

	/**
	 * 바코드 등록
	 *
	 * @author 전미량 (jeonmiryang)
	 * - 박스 관련 정보를 입력받아 바코드 정보를 저장한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addBarCode(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("addBarCode method start");
		}
		Map query = requestData.getFieldMap();
		
		Map mBarCodeNo = (Map) queryForObject("BASBAR00100.getBarCodeNo", query,
				onlineCtx);
		
		query.remove("BARCODE_NO");
		query.put("BARCODE_NO", mBarCodeNo.get("BARCODE_NO"));
		
		insert("BASBAR00100.insertBarCode", query,
				onlineCtx);
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		responseData.putFieldMap(mBarCodeNo);
		
		return responseData;
	}

}