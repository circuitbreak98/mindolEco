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
 * <li>설 명 : 정산에정일 관리  </li>
 * <li>작성일 : 2009-01-19 16:42:13</li>
 * </ul>
 *
 * @author 심연정
 */
public class BASPRM00400 extends BaseBizUnit {


	/**
	 * 
	 *
	 * @author 심연정 
	 * ==> 정산 예정일 목록
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : DEAL_CO_CD [필드1] 
	 *	- field : ACC_TYP [필드2] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : DEAL_CO_CD [필드1] 
	 *	- field : ACC_TYP [필드2] 
	 *	- field : ACC_MTH [필드3] 
	 *	- field : ACC_DT [필드4] 
	 *	- field : RMKS [필드5] 
	 */
	public IDataSet getAccList(IDataSet requestData, IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPRM00400.getAccList start >>>>>>>");
		}
		
		IRecordSet rs = queryForRecordSet("BASPRM00400.getAccList", 
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_acc");
		}
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_acc", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 심연정 
	 * ==>  정산 예정일 수정
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveAcc(IDataSet requestData, IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPRM00400.saveAcc start >>>>>>>");
		}

		IRecordSet rs = requestData.getRecordSet("ds_acc");
		Map map = null;
		int cnt = 0;
		
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				
				if (INSERT_FLAG.equalsIgnoreCase((String) rs.getRecordMap(i)
						.get(CUD_FLAG_PARAM))) {
					
					map = (Map) queryForObject("BASPRM00400.saveAcc_getChk",
							rs.getRecordMap(i));
					cnt = Integer.parseInt(map.get("CNT").toString());

					if (cnt > 0) {	
						throw new BizRuntimeException(BaseConstants.DUPLICATION_EXCEPTION_SIMPLE_MESSAGE_ID);
					} else {
						insert("BASPRM00400.saveAcc_add", rs.getRecord(i), 
								onlineCtx);
					}		
					
//					try{
//						insert("BASPRM00400.saveAcc_add", ir);
//					}catch (DuplicationException e) {
//						throw new BizRuntimeException("");
//					}
					
				} else if (UPDATE_FLAG.equalsIgnoreCase((String) rs.getRecordMap(i)
						.get(CUD_FLAG_PARAM))) {
					update("BASPRM00400.saveAcc_update", rs.getRecordMap(i), 
							onlineCtx);

				} else if (DELETE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						 CUD_FLAG_PARAM))) {
					update("BASPRM00400.saveAcc_delete", rs.getRecordMap(i), 
							onlineCtx);
				}
			}
		}

		IRecordSet result = queryForRecordSet("BASPRM00400.getAccList",
				requestData.getFieldMap(), onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_acc", result);
		return responseData;
	}

}