/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.sal.bcs.biz;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-10-11 13:54:38</li>
 * </ul>
 *
 * @author 안희수 (ahnheesoo)
 */                             
public class SALBCS00100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 안희수 (ahnheesoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdDisList(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBCS00100.getProdDisList method start");
		}
		
		log.debug("requestData.getFieldMap() :::::::::::::::: "+ requestData.getFieldMap());
				
				
		IRecordSet rs = queryForRecordSet("SALBCS00100.getProdDisList",
				requestData.getFieldMap(), onlineCtx);
				
		
		if (rs == null) {
			rs = new RecordSet("ProdDisList");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ProdDisList", rs);

		/*
		 * 복호화 시작
		 * 복호화 시작 -> 복호화 종료 소스 모두 복사 
		 * 변경 : 복호화 할 데이터셋 명/복호화 컬럼1/복호화 컬럼2
		 * 복호화 컬럼은 최대 6개 까지 확장 가능
		 */
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");

		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "ProdDisList"); // 복호화 할 데이터셋 명
		rec.add("col_name1", "CUST_BIZ_NUM"); // 복호화 컬럼1
		iSet.addRecord(rec);

		responseData.putRecordSet("cptItem", iSet); // 복호화 정보 추가

		IDataSet rsData = bco.decode(responseData, onlineCtx);
		/*
		 * 복호화 종료
		 */
		
		return rsData;	

	}

}