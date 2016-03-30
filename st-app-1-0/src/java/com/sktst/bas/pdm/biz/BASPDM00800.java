/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.pdm.biz;

import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;
import nexcore.framework.integration.db.NoRecordFoundException;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-13 14:44:20</li>
 * </ul>
 *
 * @author 전현주 (junhyunjoo)
 */
public class BASPDM00800 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getPriceRateList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		if (log.isDebugEnabled()) {
			log.debug("getPriceRateList method start");
		}

		IRecordSet rsGrade = queryForRecordSet(
				"BASPDM00800.getPriceRateListByGrade", requestData
						.getFieldMap());

		IRecordSet rsPrefer = queryForRecordSet(
				"BASPDM00800.getPriceRateListByPrefer", requestData
						.getFieldMap());
		
		IRecordSet rsAddAmt = queryForRecordSet(
				"BASPDM00800.getPriceRateListByAmt", requestData
						.getFieldMap());

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		responseData.putRecordSet("ds_grade", rsGrade);
		responseData.putRecordSet("ds_prefer", rsPrefer);
		responseData.putRecordSet("ds_addAmt", rsAddAmt);
		
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet savePriceRateList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("savePriceRateList method start");
		}

		IRecordSet rs1 = requestData.getRecordSet("ds_grade");

		if (rs1 != null) {

			for (int i = 0; i < rs1.getRecordCount(); i++) {
				
				if (DELETE_FLAG.equalsIgnoreCase(rs1.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					
					delete("BASPDM00800.deletePriceRateByGrade",rs1.getRecordMap(i), onlineCtx);
					
				}else{
					
					if(rs1.getRecordMap(i).get("seq") == null){
						
						update("BASPDM00800.updatePrePriceRateByGrade", rs1.getRecordMap(i),
								onlineCtx);
						
						insert("BASPDM00800.insertPriceRateByGrade", rs1.getRecordMap(i),onlineCtx);
						
					}else{
						
						update("BASPDM00800.updatePriceRateByGrade", rs1.getRecordMap(i),
								onlineCtx);
					}
					
				}
			}
		}

		
		IRecordSet rs2 = requestData.getRecordSet("ds_prefer");

		if (rs2 != null) {

			for (int i = 0; i < rs2.getRecordCount(); i++) {
				if (DELETE_FLAG.equalsIgnoreCase(rs2.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					
					delete("BASPDM00800.deletePriceRateByPrefer",rs2.getRecordMap(i), onlineCtx);
					
				}else{
					
					if(rs2.getRecordMap(i).get("seq") == null){
						update("BASPDM00800.updatePrePriceRateByPrefer", rs2.getRecordMap(i),
								onlineCtx);
						
						insert("BASPDM00800.insertPriceRateByPrefer", rs2.getRecordMap(i),onlineCtx);
					}else{
						
						update("BASPDM00800.updatePriceRateByPrefer", rs2.getRecordMap(i),
								onlineCtx);
					}			
				}
			}
		}
		
		IRecordSet rs3 = requestData.getRecordSet("ds_addAmt");

		if (rs3 != null) {

			for (int i = 0; i < rs3.getRecordCount(); i++) {
				if (DELETE_FLAG.equalsIgnoreCase(rs3.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					
					delete("BASPDM00800.deleteAddAmtRate",rs3.getRecordMap(i), onlineCtx);
					
				}else{
					
					update("BASPDM00800.saveAddAmtRate", rs3.getRecordMap(i),
								onlineCtx);
				}
			}
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { "1"
						 });
	}

}