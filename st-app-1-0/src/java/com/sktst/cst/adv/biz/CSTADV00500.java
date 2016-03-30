/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.cst.adv.biz;

import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-08-03 15:03:32</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class CSTADV00500 extends BaseBizUnit {

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
	public IDataSet saveConMgmt(IDataSet requestData, IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		log.debug("saveConMgmt method start");
		
		
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");
		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "ds_consult_m"); // 암호화 할 데이터셋 명
		rec.add("col_name1", "RES_NO"); // 암호화 컬럼1
		rec.add("col_name2", "TEL_NO"); // 암호화 컬럼2
		rec.add("col_name3", "ACC_NO"); // 암호화 컬럼3
		
		iSet.addRecord(rec);
		requestData.putRecordSet("cptItem", iSet); // 암호화 정보 추가
		IDataSet rsData = bco.encode(requestData, onlineCtx);

		IRecordSet mgmt = rsData.getRecordSet("ds_consult_m");
		IRecordSet dtl = rsData.getRecordSet("ds_consult_d");	
		
//		IRecordSet mgmt = requestData.getRecordSet("ds_consult_m");
//		IRecordSet dtl = requestData.getRecordSet("ds_consult_d");
		
		Map mMgmt = null;
		Map mDtl = null;
		
		
		if (mgmt != null) {
			mMgmt = mgmt.getRecordMap(0);
			update("CSTADV00500.updateConsultMgmt", mMgmt, onlineCtx);
			delete("CSTADV00500.deleteConsultDtl", mMgmt, onlineCtx);
			for (int i = 0; i < dtl.getRecordCount(); i++) {
				mDtl = dtl.getRecordMap(i);
				mDtl.put("CON_MGMT_NO", mMgmt.get("CON_MGMT_NO"));
				mDtl.put("CON_SEQ", i + 1);				
				update("CSTADV00200.addConsultDtl", mDtl, onlineCtx);
			}
		}		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
/*

	    throw new BizRuntimeException("UKey 접수등록을 사용하세요. ");
  
		Map mMgmt = null;
		Map mDtl = null;
		Map mConNo = null;

		if (mgmt != null) {
			mMgmt = mgmt.getRecordMap(0);
			String conMgmtNo = mMgmt.get("CON_MGMT_NO") == null ? "" : mMgmt
					.get("CON_MGMT_NO").toString().replaceAll(" ", "");
			if (conMgmtNo.equals("")) {
				mConNo = (Map) queryForObject("CSTADV00500.getConMgmtNo",
						mMgmt, onlineCtx);
				mMgmt.put("CON_MGMT_NO", mConNo.get("CON_MGMT_NO"));
				
				insert("CSTADV01200.addConsultMgmt", mMgmt, onlineCtx);
				for (int i = 0; i < dtl.getRecordCount(); i++) {
					mDtl = dtl.getRecordMap(i);
					mDtl.put("CON_MGMT_NO", mConNo.get("CON_MGMT_NO"));
					mDtl.put("PROD_CD", mMgmt.get("PROD_CD"));
					mDtl.put("SER_NUM", mMgmt.get("SER_NUM"));
					mDtl.put("CON_SEQ", i + 1);
					
					insert("CSTADV01200.addConsultDtl", mDtl, onlineCtx);
				}
			} else {
				update("CSTADV00500.updateConsultMgmt", mMgmt, onlineCtx);
				for (int i = 0; i < dtl.getRecordCount(); i++) {
					mDtl = dtl.getRecordMap(i);
					update("CSTADV01200.updateConsultDtl", mDtl, onlineCtx);
				}
			}
		}
*/
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });

	}

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
	public IDataSet getConsult(IDataSet requestData, IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		//질의를 수행한다.
		IRecordSet conM = queryForRecordSet("CSTADV00500.getConsultMgmt",
				requestData.getFieldMap());

		//응답데이터를 생성한다.     

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(conM.getRecordCount()) });

		responseData.putRecordSet("ds_consult_m", conM);

		IRecordSet color = queryForRecordSet("CSTADV00200.getColorList",
				requestData.getFieldMap());

		responseData.putRecordSet("ds_color", color);

		IRecordSet prod = queryForRecordSet("CSTADV00200.getProdMgmt",
				requestData.getFieldMap());

		responseData.putRecordSet("ds_prod_m", prod);

		IRecordSet conDtl = queryForRecordSet("CSTADV00500.getConsultDtl",
				requestData.getFieldMap());

		responseData.putRecordSet("ds_consult_d", conDtl);

		IRecordSet rate = queryForRecordSet("CSTADV00200.getRateMgmt",
				requestData.getFieldMap());

		responseData.putRecordSet("ds_rate", rate);
		
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");

		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "ds_consult_m"); 		// 복호화 할 데이터셋 명
		rec.add("col_name1", "ACC_NO"); 		// 복호화 컬럼1
		rec.add("col_name2", "TEL_NO"); 		// 복호화 컬럼2
		rec.add("col_name3", "RES_NO"); 		// 복호화 컬럼3
		iSet.addRecord(rec);

		responseData.putRecordSet("cptItem", iSet);

		IDataSet rsData = bco.decode(responseData, onlineCtx);
		
		return rsData;
	}

}