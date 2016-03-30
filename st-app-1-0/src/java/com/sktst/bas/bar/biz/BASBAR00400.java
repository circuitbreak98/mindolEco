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

import org.apache.commons.logging.Log;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;


import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-05-07 15:13:08</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class BASBAR00400 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * -단말기 정보 조회
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getBarCodePrchsInfo(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASBAR00400.getBarCodePrchsInfo method start >>>");
		}
		
		Map query = requestData.getFieldMap();

		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("BASBAR00400.getBarCodePrchsInfo",
				query);
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		
		if (rs == null) {
			
			rs = new RecordSet("ds_prchs");
			responseData.putRecordSet("ds_prchs", rs);
			return responseData;
			
		}else{
			if(rs.getRecordCount() > 0){
				query.put("PRCHS_MGMT_NO", rs.get(0,"PRCHS_MGMT_NO"));
				
				//질의를 수행한다.
				IRecordSet rs_dtl = queryForRecordSet("BASBAR00400.getPrchsDtlLst",
						query);
				responseData.putRecordSet("ds_consult_d", rs_dtl);
				
				// 보고서
				IRecordSet rs_rd = queryForRecordSet("BASBAR00400.getBarCodePrchsRD",
						query);
				responseData.putRecordSet("ds_prchs_rd", rs_rd);
				
				log.debug("rs_rd  =============" + rs_rd);
				
			}
			
			responseData.putRecordSet("ds_prchs", rs);

			BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");

			IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
			IRecordSet iSet = itemp.getRecordSet("cptItem");

			IRecord rec = iSet.newRecord();
			rec.add("ds_name", "ds_prchs"); 		// 복호화 할 데이터셋 명
			rec.add("col_name1", "ACC_NO"); 		// 복호화 컬럼1
			rec.add("col_name2", "TEL_NO"); 		// 복호화 컬럼2
			iSet.addRecord(rec);

			responseData.putRecordSet("cptItem", iSet);
			IDataSet rsData = bco.decode(responseData, onlineCtx);

			return rsData;
		}
		
		//응답데이터를 생성한다.
		
	}

}