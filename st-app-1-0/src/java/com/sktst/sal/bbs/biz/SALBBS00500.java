/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.sal.bbs.biz;

import java.util.Map;

import org.apache.commons.logging.Log;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2013-01-07 10:50:14</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class SALBBS00500 extends BaseBizUnit {

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
	public IDataSet getErrorCheckXls(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBBS00500.getErrorCheckXls method start >>>");
		}

		IRecordSet xls = requestData.getRecordSet("ds_upLoadXlsList");
		Map mXls = null;
		Map mOnlineSaleProd = null;
		IRecord ir = null;

		for (int i = 0; i < xls.getRecordCount(); i++) {
			ir = xls.getRecord(i);
			mXls = xls.getRecordMap(i);
			String tSerNum = mXls.get("SER_NUM") == null ? "" : mXls.get(
					"SER_NUM").toString().replaceAll(" ", "");
			if (tSerNum.equals("")) {
				ir.set("ERR_DESC", "일련번호가 없습니다.");
			} else {
				mOnlineSaleProd = (Map) queryForObject(
						"SALBBS00500.getSaleProdCheck", mXls, onlineCtx);
				if (mOnlineSaleProd == null) {
					ir.set("ERR_DESC", "판매 상품이 아닙니다.");
				}else{				
					ir.set("PROD_CD", mOnlineSaleProd.get("PROD_CD").toString());
					ir.set("PROD_NM", mOnlineSaleProd.get("PROD_NM").toString());
					ir.set("COLOR_CD", mOnlineSaleProd.get("COLOR_CD").toString());
					ir.set("COLOR_NM", mOnlineSaleProd.get("COLOR_NM").toString());
					ir.set("SER_NUM", mOnlineSaleProd.get("SER_NUM").toString());
					ir.set("OLD_SER_NUM", mOnlineSaleProd.get("OLD_SER_NUM").toString());
					ir.set("HLD_PLC_ID", mOnlineSaleProd.get("HLD_PLC_ID").toString());
					ir.set("HLD_PLC_NM", mOnlineSaleProd.get("HLD_PLC_NM").toString());
					ir.set("SALE_PRC_PLC_NM", mOnlineSaleProd.get("SALE_PRC_PLC_NM") == null ? "" : mOnlineSaleProd.get("SALE_PRC_PLC_NM").toString());
					ir.set("SALE_DTM", mOnlineSaleProd.get("SALE_DTM").toString());
					ir.set("SALE_AMT", mOnlineSaleProd.get("SALE_AMT").toString());
					ir.set("CON_MGMT_NO", mOnlineSaleProd.get("CON_MGMT_NO").toString());
					ir.set("GNRL_SALE_NO", mOnlineSaleProd.get("GNRL_SALE_NO").toString());
					ir.set("GNRL_SALE_CHG_SEQ", mOnlineSaleProd.get("GNRL_SALE_CHG_SEQ").toString());
					ir.set("SALE_SEQ", mOnlineSaleProd.get("SALE_SEQ").toString());
					ir.set("CASH_PAY_AMT", mOnlineSaleProd.get("CASH_PAY_AMT").toString());
					ir.set("CRDTCRD_PAY_AMT", mOnlineSaleProd.get("CRDTCRD_PAY_AMT").toString());
					ir.set("DIS_AMT", mOnlineSaleProd.get("SALE_PRC").toString());
					ir.set("MAR_AMT", mOnlineSaleProd.get("SALE_MAR").toString());
					ir.set("CMMS_AMT", mOnlineSaleProd.get("SALE_CMMS").toString());
					ir.set("VAT_AMT", mOnlineSaleProd.get("SALE_VAT_PRC").toString());
					//ir.set("GNRL_SALE_NO", mOnlineSaleProd.get("GNRL_SALE_NO") == null ? "" : mOnlineSaleProd.get("GNRL_SALE_NO").toString());
				}
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(xls.getRecordCount()) });

		responseData.putRecordSet("ds_upLoadXlsList", xls);
		return responseData;
	}

}