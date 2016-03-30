/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.pdm.biz;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import com.sktst.common.base.BaseBizUnit;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2013-02-04 13:46:56</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class BASPDM00920 extends BaseBizUnit {

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
	public IDataSet getPriceErrorCheckXls(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPDM00920.getPriceErrorCheckXls method start >>>");
		}

		IRecordSet xls = requestData.getRecordSet("ds_upLoadXlsList");
		Map mXls = null;
		Map mPrice = null;
		IRecord ir = null;

		for (int i = 0; i < xls.getRecordCount(); i++) {
			ir = xls.getRecord(i);
			mXls = xls.getRecordMap(i);

			mPrice = (Map) queryForObject("BASPDM00920.getPriceCheck", mXls,
					onlineCtx);

			String sProdCd = mXls.get("PROD_CD") == null ? "" : mXls.get(
					"PROD_CD").toString().replaceAll(" ", "");
			if (sProdCd.equals("")) {
				ir.set("ERR_DESC", "상품코드가 없습니다.");
			} else {
				if (mPrice.get("IN_YN").equals("N")) {
					ir.set("ERR_DESC", "기준일자에 해당하는 가격표가 존재합니다.");
				} else {
					ir.set("PROD_CD", mXls.get("PROD_CD") == null ? "" : mXls
							.get("PROD_CD").toString().replaceAll(" ", ""));
					ir.set("PROD_NM", mXls.get("PROD_NM") == null ? "" : mXls
							.get("PROD_NM").toString().replaceAll(" ", ""));
					ir.set("GRADE_N", mXls.get("GRADE_N") == null ? "" : mXls
							.get("GRADE_N").toString().replaceAll(" ", ""));
					ir.set("GRADE_A", mXls.get("GRADE_A") == null ? "" : mXls
							.get("GRADE_A").toString().replaceAll(" ", ""));
					ir.set("GRADE_B", mXls.get("GRADE_B") == null ? "" : mXls
							.get("GRADE_B").toString().replaceAll(" ", ""));
					ir.set("GRADE_C", mXls.get("GRADE_C") == null ? "" : mXls
							.get("GRADE_C").toString().replaceAll(" ", ""));
					ir.set("GRADE_D", mXls.get("GRADE_D") == null ? "" : mXls
							.get("GRADE_D").toString().replaceAll(" ", ""));
					ir.set("GRADE_E", mXls.get("GRADE_E") == null ? "" : mXls
							.get("GRADE_E").toString().replaceAll(" ", ""));
					ir.set("ERR_DESC", "");
				}
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(xls.getRecordCount()) });

		responseData.putRecordSet("ds_upLoadXlsList", xls);
		return responseData;
	}

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
	public IDataSet savePriceXls(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASPDM00920.savePriceXls method start");
		}

		IRecordSet rs_m = requestData.getRecordSet("ds_condition");
		IRecordSet rs = requestData.getRecordSet("ds_upLoadXlsList");
		
		Map mSave = null;
		Map mPriceM = null;
		Map mSavePrice = new HashMap();
		Map mSavePriceM = rs_m.getRecordMap(0);
		String priceMasterNo_Str = "";
		int cnt = 0;
		
		if (rs != null) {
			mPriceM = (Map) queryForObject("BASPDM00920.getPriceMasterNo", mSavePriceM, onlineCtx);
			priceMasterNo_Str = (String)mPriceM.get("PRICE_MASTER_NO");
			
			for (int i = 0; i < rs.getRecordCount(); i++) {
				mSave = rs.getRecordMap(i);
				
				/**
				 * 이전 가격표 END 처리
				 */
				update("BASPDM00920.updateProdFixPrice", mSave, onlineCtx);
				
				
				/**
				 * 신규 가격표 등록
				 */
				
				mSavePrice.put("PROD_CD", mSave.get("PROD_CD"));
				mSavePrice.put("GRADE", "N");
				mSavePrice.put("PRICE", mSave.get("GRADE_N"));
				mSavePrice.put("ST_DT", mSave.get("ST_DT"));
				mSavePrice.put("PRICE_MASTER_NO", priceMasterNo_Str);
				insert("BASPDM00920.saveProdFixPrice", mSavePrice, onlineCtx);
				
				mSavePrice.put("PROD_CD", mSave.get("PROD_CD"));
				mSavePrice.remove("GRADE");
				mSavePrice.remove("PRICE");
				mSavePrice.put("GRADE", "A");
				mSavePrice.put("PRICE", mSave.get("GRADE_A"));
				mSavePrice.put("ST_DT", mSave.get("ST_DT"));
				mSavePrice.put("PRICE_MASTER_NO", priceMasterNo_Str);
				insert("BASPDM00920.saveProdFixPrice", mSavePrice, onlineCtx);
				
				mSavePrice.put("PROD_CD", mSave.get("PROD_CD"));
				mSavePrice.remove("GRADE");
				mSavePrice.remove("PRICE");
				mSavePrice.put("GRADE", "B");
				mSavePrice.put("PRICE", mSave.get("GRADE_B"));
				mSavePrice.put("ST_DT", mSave.get("ST_DT"));
				mSavePrice.put("PRICE_MASTER_NO", priceMasterNo_Str);
				insert("BASPDM00920.saveProdFixPrice", mSavePrice, onlineCtx);
/*				
				mSavePrice.put("PROD_CD", mSave.get("PROD_CD"));
				mSavePrice.remove("GRADE");
				mSavePrice.remove("PRICE");
				mSavePrice.put("GRADE", "C");
				mSavePrice.put("PRICE", mSave.get("GRADE_C"));
				mSavePrice.put("ST_DT", mSave.get("ST_DT"));
				mSavePrice.put("PRICE_MASTER_NO", priceMasterNo_Str);
				insert("BASPDM00920.saveProdFixPrice", mSavePrice, onlineCtx);
				
				mSavePrice.put("PROD_CD", mSave.get("PROD_CD"));
				mSavePrice.remove("GRADE");
				mSavePrice.remove("PRICE");
				mSavePrice.put("GRADE", "D");
				mSavePrice.put("PRICE", mSave.get("GRADE_D"));
				mSavePrice.put("ST_DT", mSave.get("ST_DT"));
				mSavePrice.put("PRICE_MASTER_NO", priceMasterNo_Str);
				insert("BASPDM00920.saveProdFixPrice", mSavePrice, onlineCtx);
*/				
				mSavePrice.put("PROD_CD", mSave.get("PROD_CD"));
				mSavePrice.remove("GRADE");
				mSavePrice.remove("PRICE");
				mSavePrice.put("GRADE", "E");
				mSavePrice.put("PRICE", mSave.get("GRADE_E"));
				mSavePrice.put("ST_DT", mSave.get("ST_DT"));
				mSavePrice.put("PRICE_MASTER_NO", priceMasterNo_Str);
				insert("BASPDM00920.saveProdFixPrice", mSavePrice, onlineCtx);
				
				cnt++;
			}
			
			mSavePriceM.put("PRICE_MASTER_NO", priceMasterNo_Str);
			mSavePriceM.put("PRICE_CNT", cnt);
			
			update("BASPDM00920.saveProdFixPriceMaster", mSavePriceM, onlineCtx);
			
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });
	}

}