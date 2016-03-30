/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.cst.adv.biz;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-23 15:42:04</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class CSTPRC00300 extends BaseBizUnit {

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
	public IDataSet getRmksInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("CSTPRC00300.getRmksInfo",
				requestData.getFieldMap());

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_rmks", rs);
		return responseData;
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
	public IDataSet getPrchsOutList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("CSTPRC00300.getPrchsOutList",
				requestData.getFieldMap());

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("InProdOrgList", rs);
		return responseData;
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
	public IDataSet saveMovPrchsList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		Map mMovList = null;
		Map mMov = null;
		Map mOutNo = null;
		Map mOutSeq = null;

		String procFlag = "";
		String strMgmtNo = "";

		IRecordSet rsActFlg = requestData.getRecordSet("ds_procFlag");
		IRecordSet rsMovList = requestData.getRecordSet("ds_inProdOrgList");
		
		if (rsActFlg != null && rsActFlg.getRecordCount() > 0) {
			procFlag = rsActFlg.getRecordMap(0).get("proc_flag").toString();

			strMgmtNo = rsActFlg.getRecordMap(0).get("out_mgmt_no") == null?"":
				rsActFlg.getRecordMap(0).get("out_mgmt_no").toString();
		}
		if (procFlag == null || "".equals(strMgmtNo)) {
			mOutNo = (Map) queryForObject("CSTPRC00300.getOutMgmtNo", requestData.getFieldMap(),onlineCtx);
		}
		if (rsMovList != null) {
			mMov = rsActFlg.getRecordMap(0);
			
			for (int i = 0; i < rsMovList.getRecordCount(); i++) {							
				mMovList = rsMovList.getRecordMap(i);
				
				if (procFlag == null || "".equals(procFlag)) {
					mMovList.put("OUT_SEQ", i + 1);				
					if (i==0) {						
						mMov.put("OUT_MGMT_NO", mOutNo.get("OUT_MGMT_NO"));						
						insert("CSTPRC00300.addPrchsOutM", mMov, onlineCtx);
					}
					mMovList.put("OUT_MGMT_NO", mOutNo.get("OUT_MGMT_NO"));
					insert("CSTPRC00300.addPrchsMovOutDtl", mMovList, onlineCtx);
					mMovList.put("PRCHS_OUT_YN", "Y");
					mMovList.put("PRC_ST","02");
					if(mMovList.get("PRC_GB").toString().equals("C")){
						update("CSTPRC00300.updateConsultMgmt", mMovList, onlineCtx);
					} else {
						update("CSTPRC00300.updatePrchsMgmt", mMovList, onlineCtx);
					}
				} else if (procFlag.equals("AD")){
					if (i==0) {
						update("CSTPRC00300.updatePrchsOutMDel", mMov, onlineCtx);
					}
					mMovList.put("OUT_MGMT_NO", mMov.get("OUT_MGMT_NO"));
					update("CSTPRC00300.updatePrchsMovOutDtlDel", mMovList, onlineCtx);
					mMovList.put("PRCHS_OUT_YN", "N");
					mMovList.put("PRC_ST","01");
					update("CSTPRC00300.updateConsultMgmt", mMovList, onlineCtx);
					update("CSTPRC00300.updatePrchsMgmt", mMovList, onlineCtx);
				} else if (procFlag.equals("AU")){
					update("CSTPRC00300.updatePrchsOutM", mMov, onlineCtx);
					mOutSeq = (Map) queryForObject("CSTPRC00300.getOutSeq", mMov,onlineCtx);
//					mMovList.put("OUT_SEQ", mOutSeq.get("OUT_SEQ"));
					mMovList.put("OUT_MGMT_NO", mMov.get("OUT_MGMT_NO"));
					if (mMovList.get("nc_rec_status").toString().equals("delete")){
						update("CSTPRC00300.updatePrchsMovOutDtlDel", mMovList, onlineCtx);
						mMovList.put("PRCHS_OUT_YN", "N");
						mMovList.put("PRC_ST","02");
						update("CSTPRC00300.updateConsultMgmt", mMovList, onlineCtx);
					} else {
						insert("CSTPRC00300.addPrchsMovOutDtl", mMovList, onlineCtx);
						mMovList.put("PRCHS_OUT_YN", "Y");
						update("CSTPRC00300.updateConsultMgmt", mMovList, onlineCtx);
					}
				}
			}	
		}
		
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });
	}

}