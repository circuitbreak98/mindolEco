/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.cst.skn.biz;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

import java.util.Map;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2014-01-17 10:10:51</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class CSTSKN00110 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveConsultMgmtSknXlsIn(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("CSTSKN00110.saveConsultMgmtXlsIn method start");
		}

		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");
		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "ds_upLoadXlsList"); // 암호화 할 데이터셋 명
		rec.add("col_name1", "RES_NO"); // 암호화 컬럼1
		rec.add("col_name2", "TEL_NO"); // 암호화 컬럼2
		rec.add("col_name3", "ACC_NO"); // 암호화 컬럼3

		iSet.addRecord(rec);
		requestData.putRecordSet("cptItem", iSet); // 암호화 정보 추가
		IDataSet rsData = bco.encode(requestData, onlineCtx);

		IRecordSet xls = rsData.getRecordSet("ds_upLoadXlsList");

		//		IRecordSet xls = requestData.getRecordSet("ds_upLoadXlsList");

		Map mMgmt = null;
		Map mProdDtl = null;
		Map mConNo = null;
		String sConNo = "";
		String sConPlcCd = "";
		String sPrcGb = "";

		if (xls != null) {
			for (int i = 0; i < xls.getRecordCount(); i++) {

				mMgmt = xls.getRecordMap(i);
				sConPlcCd = mMgmt.get("CON_PLC_CD").toString();
				sPrcGb = mMgmt.get("PRC_GB").toString();
/*
				if ("11050".equals(sConPlcCd)) {		
					sConNo = "U" + mMgmt.get("T_MGMT_NO").toString();
				} else {					
					mConNo = (Map) queryForObject("CSTSKN00110.getConMgmtNo",
							mMgmt, onlineCtx);
					sConNo = mConNo.get("CON_MGMT_NO").toString();
				}
*/				
				if ("UC".equals(sPrcGb) || "UN".equals(sPrcGb)) {		
					sConNo = "U" + mMgmt.get("T_MGMT_NO").toString();
				} else if ("T".equals(sPrcGb)){
					sConNo = "T" + mMgmt.get("T_MGMT_NO").toString();
				} else {					
					mConNo = (Map) queryForObject("CSTSKN00110.getConMgmtNo",
							mMgmt, onlineCtx);
					sConNo = mConNo.get("CON_MGMT_NO").toString();
				}				
				IRecordSet rsProdDtl = queryForRecordSet("CSTSKN00110.getProdDtl", mMgmt);
				
				for (int j = 0; j < rsProdDtl.getRecordCount(); j++) {

					mProdDtl = rsProdDtl.getRecordMap(j);
					mProdDtl.put("CON_MGMT_NO", sConNo);

					if (mProdDtl.get("DTL_CL").toString().equals("1")) {
						if (Integer.parseInt(mMgmt.get("BATTERY").toString()) > 0) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("2")) {
						if (Integer.parseInt(mMgmt.get("BATTERY").toString()) > 1) {
							mProdDtl.put("HLD_QTY", Integer.parseInt(mMgmt.get(
									"BATTERY").toString()) - 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("4")) {
						if (mMgmt.get("CHARGER").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("5")) {
						if (mMgmt.get("ZENDER").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("6")) {
						if (mMgmt.get("EARPHONES").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("6")) {
						if (mMgmt.get("EARPHONES").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("7")) {
						if (mMgmt.get("CASE").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("8")) {
						if (mMgmt.get("USB").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("9")) {
						if (mMgmt.get("MEMORY").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if ("UN".equals(sPrcGb) || "N".equals(sPrcGb)) {
						insert("CSTSKN00110.addConsultDtlSknXls", mProdDtl,	onlineCtx);
					} else {
						insert("CSTSKN00110.addConsultDtlXls", mProdDtl, onlineCtx);
					}

				}

				mMgmt.put("CON_MGMT_NO", sConNo);
				if ("UN".equals(sPrcGb) || "N".equals(sPrcGb)) {
					insert("CSTSKN00110.addConsultMgmtSknXlsIn", mMgmt, onlineCtx);
				} else {
					insert("CSTSKN00110.addConsultMgmtXlsIn", mMgmt, onlineCtx);
				}				
				
			}
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });
	}

}