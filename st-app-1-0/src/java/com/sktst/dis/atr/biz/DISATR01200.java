/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.atr.biz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;
import com.sktst.common.user.PsLoginUserInfo;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2013-05-28 16:21:15</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class DISATR01200 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * -대리점간이동취소List취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdMovListForCncl(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdMovListForCncl method start.");
		}

		IRecordSet rs = queryForRecordSet("DISATR01200.getProdMovListForCncl",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_prodMovList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("ds_prodMovList", rs);

		return result;
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
	public IDataSet saveProdMovCncl(IDataSet requestData,
			IOnlineContext onlineCtx) {
		
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveProdMovCncl method start.");
		}
		
		PsLoginUserInfo userInfo = (PsLoginUserInfo) onlineCtx.getUserInfo();
		IRecordSet rsProdMovCncl = requestData.getRecordSet("ds_prodMovCnclList");
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		
		Map mMovCncl = null;
		Map mMov = new HashMap();
		Map mMovDtl = new HashMap();
		Map mUnitPrc = null;
		
		String sDate = "";
		String strOutMgmtNo = "";
		String strInMgmtNo = "";
		String strInPlcId = "";
		String strOutPlcId = "";
		String strInCl = "304"; //출고구분:304(이동출고취소)(ZDIS_C_00050)
		String strOutCl = "303"; // 입고구분:303(이동입고취소)(ZDIS_C_00050)
		String strInSt = "03";  // 입고상태 - 입고 
		String strInOutCl = "300";  // 입고상태 - 입고 
		String strOutSeq = "";
		String strInSeq = "";
		String strInOutSeq = "";
		String strAmt = "";
		String strDisSt = "01"; // 재고상태 : 01(가용)
		
		int cnt = 0;
		
		long currentTime = System.currentTimeMillis();
		sDate = df.format(new Date(currentTime));
		log.debug("11.");
		if (rsProdMovCncl != null) {
			for (int i = 0; i < rsProdMovCncl.getRecordCount(); i++) {
				mMovCncl = rsProdMovCncl.getRecordMap(i);
				log.debug("2." + mMovCncl);
				strInPlcId = (String)mMovCncl.get("out_plc_id");
				strOutPlcId = (String)mMovCncl.get("in_plc_id");
				
				// 1-1. Master 
				// 1-1-1. 이동출고 Master
				strOutMgmtNo = getMgmtNo("MV", userInfo.getLoginId(), onlineCtx);
				mMov.put("out_mgmt_no", strOutMgmtNo);
				mMov.put("out_cl", strOutCl); 
				mMov.put("out_schd_dt", sDate);
				mMov.put("out_plc_id", strOutPlcId);
				mMov.put("in_plc_id", strInPlcId);
				log.debug("3." + mMov);
				insert("DISATR01100.addProdOutM", mMov, onlineCtx);
				
				
				// 1-2. Dtl
				// 1-2-1. 이동 입고취소 Dtl

				strOutSeq = (String) queryForObject("DISATR01100.getOutSeq", strOutMgmtNo);
				strInSeq = (String) queryForObject("DISATR01100.getInSeq", strInMgmtNo);
				strInOutSeq = (String) queryForObject("DISATR01100.getInoutSeq", mMovCncl);
				mUnitPrc = (Map) queryForObject("DISATR01100.getProdUnitPrcInfo", mMovCncl,onlineCtx);
				strAmt = (String) mUnitPrc.get("AMT");
				
				mMovDtl.put("out_mgmt_no", strOutMgmtNo);
				mMovDtl.put("out_seq", strOutSeq);
				mMovDtl.put("prod_cd", mMovCncl.get("prod_cd"));
				mMovDtl.put("ser_num", mMovCncl.get("ser_num"));
				mMovDtl.put("color_cd", mMovCncl.get("color_cd"));
				mMovDtl.put("eqp_st", mMovCncl.get("eqp_st"));
				mMovDtl.put("dis_st", strDisSt); 
				mMovDtl.put("amt", strAmt); // 상품단가 : TDIS_PROD_UNIT_PRC
				mMovDtl.put("sale_amt", mMovCncl.get("sale_amt"));
				log.debug("5." + mMovDtl);
				// 상품이동출고상세 추가
				
				insert("DISATR01100.addProdOutD", mMovDtl, onlineCtx);
				insert("DISATR01100.addProdMovOutD", mMovDtl, onlineCtx);
				
				mMovDtl.put("inout_dt", sDate);
				mMovDtl.put("inout_cl", strInOutCl);
				mMovDtl.put("inout_dtl_cl", strOutCl);
				mMovDtl.put("out_plc_id", strOutPlcId);
				mMovDtl.put("in_plc_id", strInPlcId);
				mMovDtl.put("inout_seq", strInOutSeq);
				log.debug("6." + mMovDtl);
				// 상품입출고상세 추가 (입고취소)
				insert("DISATR01100.addProdInoutHst", mMovDtl, onlineCtx);
				
				
				// 1-1-2. 이동입고 Master
				// 입고관리번호 생성 	
				strInMgmtNo = getMgmtNo("IN", userInfo.getLoginId(), onlineCtx);
				strInSeq = "1";
				mMov.put("in_cl", strInCl); 
				mMov.put("in_mgmt_no", strInMgmtNo);
				mMov.put("in_st", strInSt); 
				mMov.put("in_fix_dt", sDate);
				log.debug("4." + mMov);
				//	 입고 테이블 생성
				insert("DISATR01100.addProdInM", mMov, onlineCtx);
				
				// 1-2-2. 이동 출고취소 Dtl
				
				strInOutSeq = (String) queryForObject("DISATR01100.getInoutSeq", mMovDtl);
				mMovDtl.put("in_mgmt_no", strInMgmtNo);
				mMovDtl.put("in_seq", strInSeq);
				mMovDtl.put("in_fix_dt", sDate);
				mMovDtl.put("in_fix_yn", "Y");
				log.debug("7." + mMovDtl);
				// 상품이동입고상세 추가
				insert("DISATR01100.addProdMovInD", mMovDtl, onlineCtx);
				
				
				mMovDtl.remove("inout_seq");
				mMovDtl.remove("inout_dtl_cl");
				mMovDtl.remove("inout_seq");
				mMovDtl.put("inout_seq", strInOutSeq);
				mMovDtl.put("inout_dtl_cl", strInCl);
				mMovDtl.put("prchs_plc_id", strInSt);
				log.debug("8." + mMovDtl);
				// 상품입출고상세 추가(입고)
				insert("DISATR01100.addProdInoutHst", mMovDtl, onlineCtx);
				
				mMovDtl.remove("inout_dtl_cl");
				mMovDtl.put("inout_dtl_cl", strInCl); 
				mMovDtl.put("hld_plc_id", strInPlcId);
				mMovDtl.put("trms_yn","N");
				mMovDtl.put("trms_st","R");
				
				mMovDtl.put("con_sale_yn","N");
				mMovDtl.put("con_sale_cncl_dt",sDate);
				
				log.debug("9." + mMovDtl);
				// 상품재고 이동 처리
				update("DISATR01100.updateProdDis", mMovDtl, onlineCtx);
				
				cnt++;
			}
		}
		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"Insert:" + 0, " Update:" + cnt, " Delete:" + 0 });

		return result;
	}
	
	/**
	 * 관리 번호 생성
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 이동 출고 관리 번호를 생성한다.
	 * @param mgmtCd 
	 *            관리번호종류코드 
	 * @param userId 
	 *            사용자아이디 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public String getMgmtNo(String mgmtCd, String userId,
			IOnlineContext onlineCtx) {
		String retMgmtNo = "";

		Map inMap = new HashMap();

		inMap.put("mgmt_no_cd", mgmtCd);
		inMap.put("user_id", userId);
		inMap.put("errcode", "");
		inMap.put("errmsg", "");
		inMap.put("mgmt_no", "");

		queryForObject("DISATR00200.selectOutMgmtNo", inMap, onlineCtx);

		retMgmtNo = inMap.get("mgmt_no").toString();

		return retMgmtNo;
	}
}