/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.prm.biz;

import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : 배송비 관리 </li>
 * <li>작성일 : 2009-01-19 10:57:04</li>
 * </ul>
 *
 * @author 심연정 (shimyunjung)
 */
public class BASPRM00300 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * ==> 배송비 정보 입력 및 수정
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveDeliverFee(IDataSet requestData,
			IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPRM00300.saveDeliverFee start >>>>>>>");
		}
		 		
		IRecordSet rs = requestData.getRecordSet("ds_Dev");

		Map map = null;
		int cnt = 0;
		int cntStadt = 0;
		
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {

				if (INSERT_FLAG.equalsIgnoreCase((String) rs.getRecord(i)
						.get(CUD_FLAG_PARAM))) {
					
					map = (Map) queryForObject(
							"BASPRM00300.saveDeliverFee_getDup", rs.getRecord(i), onlineCtx);
					cnt = Integer.parseInt(map.get("CNT").toString());
					
					if (cnt > 0) {	
						throw new BizRuntimeException(BaseConstants.DUPLICATION_EXCEPTION_SIMPLE_MESSAGE_ID);
					} else {					
						insert("BASPRM00300.saveDeliverFee_addDeliverFee", rs.getRecord(i), 
								onlineCtx);	
					}
					
				} else if (UPDATE_FLAG.equalsIgnoreCase((String) rs.getRecord(i)
						.get(CUD_FLAG_PARAM))) {
					map = (Map) queryForObject(
							"BASPRM00300.getNewStaDt", rs.getRecord(i), onlineCtx);
					cntStadt = Integer.parseInt(map.get("CNT").toString());
										
					if (cntStadt ==0) {
						insert("BASPRM00300.saveDeliverFee_addStaDt", rs.getRecord(i), onlineCtx);
						update("BASPRM00300.saveDeliverFee_updateEndDt", rs.getRecord(i), onlineCtx);
					} else {
						update("BASPRM00300.updateDlv", rs.getRecord(i), onlineCtx);
					}
				}
			}
		}
		
		IRecordSet result = null;
		String chk = requestData.getFieldMap().get("CHK").toString();
		
		if ("1".equals(chk)) {
			result = queryForRecordSet("BASPRM00300.getDeliverFeeHst",
					requestData.getFieldMap(), onlineCtx);
		} else {
			result = queryForRecordSet("BASPRM00300.getDeliverFee",
					requestData.getFieldMap(), onlineCtx);
		}
	
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_Dev", result);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 *   
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : ST_DT [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : SER_NUM [필드1] 
	 *	- field : DEAL_CO_CD [필드2] 
	 *	- field : DEL_YN [필드3] 
	 *	- field : DLV_TYP [필드4] 
	 *	- field : UNIT [필드5] 
	 *	- field : UNIT_PRC [필드6] 
	 *	- field : STA_DT [필드7] 
	 *	- field : END_DT [필드8] 
	 *	- field : RMKS [필드9] 
	 *	- field : INS_DTM [필드10] 
	 *	- field : INS_USER_ID [필드11] 
	 *	- field : MOD_DTM [필드12] 
	 *	- field : MOD_USER_ID [필드13] 
	 *	- field : UPD_CNT [필드14] 
	 *	- field : MAX_FLAG [필드15] 
	 */
	public IDataSet getDeliverFee(IDataSet requestData, IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPRM00300.getDeliverFee start >>>>>>>");
		}
		IRecordSet rs = null;
		String chk = requestData.getFieldMap().get("CHK").toString();
		
		if ("1".equals(chk)) {
			rs = queryForRecordSet("BASPRM00300.getDeliverFeeHst",
					requestData.getFieldMap(), onlineCtx);
		} else {
			rs = queryForRecordSet("BASPRM00300.getDeliverFee",
					requestData.getFieldMap(), onlineCtx);
		}
		
		if (rs == null) {
			rs = new RecordSet("ds_Dev");
		}
		System.err.println(rs);
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_Dev", rs);
		return responseData;
	}

}