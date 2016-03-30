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
import com.sktst.bas.bco.ejb.BCO;

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
 * <li>작성일 : 2013-05-02 14:50:09</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class DISATR01100 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * -대리점간 상품이동 상세 조회
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdMovList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdMovList method start.");
		}

		IRecordSet rs = queryForRecordSet("DISATR01100.getProdMovList",
				requestData.getFieldMap(), onlineCtx);

		IRecordSet rs2 = queryForRecordSet("DISATR01100.getProdMovMaster",
				requestData.getFieldMap(), onlineCtx);
		
		IRecordSet rsRd = queryForRecordSet("DISATR01100.getProdMovListForRD",
				requestData.getFieldMap(), onlineCtx);
		
		if (rs == null) {
			rs = new RecordSet("ds_prodMovList");
		}

		if (rs2 == null) {
			rs2 = new RecordSet("ds_movMaster");
		}
		
		if (rsRd == null) {
			rsRd = new RecordSet("ds_report1");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("ds_prodMovList", rs);
		result.putRecordSet("ds_movMaster", rs2);
		result.putRecordSet("ds_report1", rsRd);

		return result;

	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * - 재고 상태 조회
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdSt(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdSt method start.");
		}

		IRecordSet rs = queryForRecordSet("DISATR01100.getProdSt", requestData
				.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_st");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("ds_st", rs);

		return result;
	}

	/**
	 * 
	 * 
	 * @author 전미량 (jeonmiryang)
	 * - 대리점간 재고 이동 등록
	 * 
	 * 1. 재고이동 신규 등록
	 * 2. 재고이동 등록 수정
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveProdMovList(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception  {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("saveProdMovList method start");
		}
		String sDate = "";
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		
		long currentTime = System.currentTimeMillis();
		sDate = df.format(new Date(currentTime));
		
		IRecordSet rsMovMaster = requestData.getRecordSet("ds_movMaster");
		IRecordSet rsProdMovList = requestData.getRecordSet("ds_prodMovList");
		PsLoginUserInfo userInfo = (PsLoginUserInfo) onlineCtx.getUserInfo();
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");

		Map mMovMaster = null;
		Map mMovDtl = null;
		Map mUnitPrc = null;
		

		String strMgmtNo = "";
		String strOutSeq = "";
		String strInOutSeq = "";
		String sInMgmtNo = "";
		String strInSeq = "";

		String strAmt = "";
		String docId = "";
		

		// 가자!!!!

		if (rsMovMaster != null) {
			mMovMaster = rsMovMaster.getRecordMap(0);
			strMgmtNo = mMovMaster.get("out_mgmt_no") == null ? "" : mMovMaster
					.get("out_mgmt_no").toString();
			log.debug("[[mMovMaster]] : " + mMovMaster);
			if (strMgmtNo == null || "".equals(strMgmtNo)) { // 1. 신규등록
				
				log.debug("첨부 파일 처리 start");
				//		  첨부 파일 처리
				IRecordSet rsFile = requestData.getRecordSet("nc_fileDs");
				if (rsFile.getRecordCount() > 0) {
					bco.addAttachFile(requestData, onlineCtx);
					
					docId = rsFile.get(0, "DOC_ID"); // 최초 등록시 첨부 파일 정보에서 DOC_ID
					mMovMaster.put("DOC_ID", docId); // 
				}

				// 1-1. Master 

				// 1-1-1. 이동출고 Master
				strMgmtNo = getMgmtNo("MV", userInfo.getLoginId(), onlineCtx);
				mMovMaster.put("out_mgmt_no", strMgmtNo);
				mMovMaster.put("out_cl", "301"); // 출고구분:301(이동출고)(ZDIS_C_00050)

				insert("DISATR01100.addProdOutM", mMovMaster, onlineCtx);

				// 1-1-2. 이동입고 Master

				// 입고관리번호 생성 	
				sInMgmtNo = getMgmtNo("IN", userInfo.getLoginId(), onlineCtx);

				mMovMaster.put("in_cl", "302"); // 입고구분 : 302 (이동입고)(ZDIS_C_00050)
				mMovMaster.put("in_mgmt_no", sInMgmtNo);
				mMovMaster.put("in_st", "03"); // 입고상태 - 입고 
				mMovMaster.put("in_fix_dt", mMovMaster.get("out_schd_dt"));

				//	 입고 테이블 생성
				insert("DISATR01100.addProdInM", mMovMaster, onlineCtx);

				// 1-2. Dtl
				if (rsProdMovList != null) {
					for (int i = 0; i < rsProdMovList.getRecordCount(); i++) {

						// 1-2-1. 이동 출고 Dtl

						mMovDtl = rsProdMovList.getRecordMap(i);
						
						strOutSeq = (String) queryForObject(
								"DISATR01100.getOutSeq", strMgmtNo);
						strInOutSeq = (String) queryForObject(
								"DISATR01100.getInoutSeq", mMovDtl);
						mUnitPrc = (Map) queryForObject(
								"DISATR01100.getProdUnitPrcInfo", mMovDtl,
								onlineCtx);
						strAmt = (String) mUnitPrc.get("AMT");
						
						mMovDtl.put("out_mgmt_no", strMgmtNo);
						mMovDtl.put("out_seq", strOutSeq);
						mMovDtl.put("inout_seq", strInOutSeq);
						mMovDtl.put("dis_st", "01"); // 재고상태 : 01(가용)
						mMovDtl.put("amt", strAmt); // 상품단가 : TDIS_PROD_UNIT_PRC
						mMovDtl.put("inout_cl", "300"); // 입출고구분 : 300(재고이동)
						mMovDtl.put("inout_dtl_cl", "301"); // 입출고상세구분 : 301(이동출고)
						mMovDtl.put("inout_dt", mMovMaster.get("out_schd_dt"));
						mMovDtl.put("in_plc_id", mMovMaster.get("in_plc_id"));
						mMovDtl.put("out_plc_id", mMovMaster.get("out_plc_id"));
						mMovDtl.put("hld_plc_id", mMovDtl.get("in_plc_id"));
						//mMovDtl.put("out_schd_dt", mMovMaster.get("out_schd_dt"));

						// 상품이동출고상세 추가
						insert("DISATR01100.addProdOutD", mMovDtl, onlineCtx);
						insert("DISATR01100.addProdMovOutD", mMovDtl, onlineCtx);

						// 상품입출고상세 추가 (출고)
						insert("DISATR01100.addProdInoutHst", mMovDtl,
								onlineCtx);

						// 1-2-2. 이동 입고 Dtl

						strInSeq = (String) queryForObject(
								"DISATR01100.getInSeq", sInMgmtNo);
						strInOutSeq = (String) queryForObject(
								"DISATR01100.getInoutSeq", mMovDtl);

						mMovDtl.put("in_mgmt_no", sInMgmtNo);
						mMovDtl.put("in_seq", strInSeq);
						mMovDtl.put("in_fix_dt", mMovMaster.get("out_schd_dt"));
						mMovDtl.put("in_fix_yn", "Y");
						mMovDtl.put("inout_seq", strInOutSeq);
						mMovDtl.remove("inout_cl");
						mMovDtl.put("inout_cl", "300"); // 입고구분 : 이동입고(ZDIS_C_00050)
						mMovDtl.remove("inout_dtl_cl");
						mMovDtl.put("inout_dtl_cl", "302"); // 입고구분 : 이동입고(ZDIS_C_00050)
						mMovDtl.put("prchs_plc_id", mMovMaster.get("in_plc_id"));
						mMovDtl.put("trms_yn","N");
						mMovDtl.put("trms_st","T");
						
						mMovDtl.put("con_sale_yn","Y");
						mMovDtl.put("con_sale_dt",mMovMaster.get("out_schd_dt"));
						mMovDtl.put("con_sale_cncl_dt","");
						mMovDtl.put("con_sale_plc_id",mMovMaster.get("in_plc_id"));

						// 상품이동입고상세 추가
						insert("DISATR01100.addProdMovInD", mMovDtl, onlineCtx);

						// 상품입출고상세 추가(입고)
						insert("DISATR01100.addProdInoutHst", mMovDtl,
								onlineCtx);

						// 상품재고 이동 처리
						update("DISATR01100.updateProdDis", mMovDtl, onlineCtx);
					}
				}

			} else { // 2. 수정

				if (rsProdMovList != null) {
					for (int i = 0; i < rsProdMovList.getRecordCount(); i++) {
						
						mMovDtl = rsProdMovList.getRecordMap(i);
						sInMgmtNo = (String)mMovMaster.get("in_mgmt_no");
						
						log.debug("[ mMovDtl ]" + mMovDtl );
						if (INSERT_FLAG.equalsIgnoreCase(mMovDtl.get(
								CUD_FLAG_PARAM).toString())) {
							log.debug("INSERT_FLAG ■■■■■■ " + strMgmtNo);
							// 2-1. 이동 출고 Dtl

							log.debug("strMgmtNo : " + strMgmtNo);
							strOutSeq = (String) queryForObject(
									"DISATR01100.getOutSeq", strMgmtNo);
							log.debug("strOutSeq : " + strOutSeq);
							strInOutSeq = (String) queryForObject(
									"DISATR01100.getInoutSeq", mMovDtl);
							log.debug("strInOutSeq : " + strInOutSeq);
							mUnitPrc = (Map) queryForObject(
									"DISATR01100.getProdUnitPrcInfo", mMovDtl,
									onlineCtx);
							strAmt = (String) mUnitPrc.get("AMT");
							log.debug("strAmt : " + strAmt);
							
							mMovDtl.put("out_mgmt_no", strMgmtNo);
							mMovDtl.put("out_seq", strOutSeq);
							mMovDtl.put("inout_seq", strInOutSeq);
							mMovDtl.put("dis_st", "01"); // 재고상태 : 01(가용)
							mMovDtl.put("amt", strAmt); // 상품단가 : TDIS_PROD_UNIT_PRC
							mMovDtl.put("inout_cl", "300"); // 입출고구분 : 300(재고이동)
							mMovDtl.put("inout_dtl_cl", "301"); // 입출고상세구분 : 301(이동출고)
							mMovDtl.put("inout_dt", sDate);
							mMovDtl.put("in_plc_id", mMovMaster.get("in_plc_id"));
							mMovDtl.put("out_plc_id", mMovMaster.get("out_plc_id"));
							mMovDtl.put("hld_plc_id", mMovDtl.get("in_plc_id"));
							//mMovDtl.put("out_schd_dt", mMovMaster.get("out_schd_dt"));
							log.debug("[I:mMovDtl] " + mMovDtl);
							// 상품이동출고상세 추가
							insert("DISATR01100.addProdMovOutD", mMovDtl,
									onlineCtx);

							// 상품입출고상세 추가 (출고)
							insert("DISATR01100.addProdInoutHst", mMovDtl,
									onlineCtx);

							// 2-2. 이동 입고 Dtl

							strInSeq = (String) queryForObject(
									"DISATR01100.getInSeq", sInMgmtNo);
							strInOutSeq = (String) queryForObject(
									"DISATR01100.getInoutSeq", mMovDtl);

							mMovDtl.put("in_mgmt_no", sInMgmtNo);
							mMovDtl.put("in_seq", strInSeq);
							mMovDtl.put("in_fix_dt", mMovMaster.get("out_schd_dt"));
							mMovDtl.put("in_fix_yn", "Y");
							mMovDtl.put("inout_seq", strInOutSeq);
							mMovDtl.remove("inout_cl");
							mMovDtl.put("inout_cl", "300"); // 입고구분 : 이동입고(ZDIS_C_00050)
							mMovDtl.remove("inout_dtl_cl");
							mMovDtl.put("inout_dtl_cl", "302"); // 입고구분 : 이동입고(ZDIS_C_00050)
							mMovDtl.put("prchs_plc_id", mMovMaster.get("in_plc_id"));
							mMovDtl.put("trms_yn","N");
							mMovDtl.put("trms_st","T");
							mMovDtl.put("con_sale_yn", "Y");
							mMovDtl.put("con_sale_dt", mMovMaster.get("out_schd_dt"));
							mMovDtl.put("con_sale_cncl_dt", "");
							mMovDtl.put("con_sale_plc_id", mMovMaster.get("in_plc_id"));
							
							// 상품이동입고상세 추가
							insert("DISATR01100.addProdMovInD", mMovDtl,
									onlineCtx);

							// 상품입출고상세 추가(입고)
							insert("DISATR01100.addProdInoutHst", mMovDtl, onlineCtx);

							// 상품재고 이동 처리
							update("DISATR01100.updateProdDis", mMovDtl, onlineCtx);

						} else if (DELETE_FLAG.equalsIgnoreCase(mMovDtl.get(
								CUD_FLAG_PARAM).toString())) {
							
							log.debug("DELETE_FLAG ■■■■■■ " + strMgmtNo);
							mMovDtl.put("dis_st", "01"); // 재고상태 : 01(가용)
							mMovDtl.put("hld_plc_id", mMovDtl.get("out_plc_id"));
							mMovDtl.put("inout_dt", sDate);
							mMovDtl.put("inout_cl", "300"); // 입출고구분 : 300(재고이동)
							mMovDtl.put("inout_dtl_cl", "304"); // 입출고상세구분 : 304(이동출고취소)
							
							if("Y".equals(mMovDtl.get("TRMS_YN"))){
								mMovDtl.put("trms_yn","N");
								mMovDtl.put("trms_st","R");
								
								mMovDtl.put("con_sale_yn","N");
								mMovDtl.put("con_sale_cncl_dt",sDate);
								
							}else{
								mMovDtl.put("trms_yn","N");
								mMovDtl.put("trms_st","N");
								
								mMovDtl.put("con_sale_yn","N");
								mMovDtl.put("con_sale_dt","");
								mMovDtl.put("con_sale_cncl_dt","");
								mMovDtl.put("con_sale_plc_id","");
								
							}
							
							log.debug("[D:mMovDtl] " + mMovDtl);
							
							update("DISATR01100.updateProdMovOutDForDel", mMovDtl, onlineCtx);
							update("DISATR01100.updateProdMovInDForDel", mMovDtl, onlineCtx);
							update("DISATR01100.updateProdInoutHstForDel", mMovDtl, onlineCtx);
							update("DISATR01100.updateProdDis", mMovDtl, onlineCtx);
						}
					}
				}
				log.debug("[mMovMaster] " + mMovMaster);
				if ("Y".equals(mMovMaster.get("UPDATE_YN"))) {
					
					log.debug("첨부 파일 처리 start");
					//		  첨부 파일 처리
					IRecordSet rsFile = requestData.getRecordSet("nc_fileDs");
					if (rsFile.getRecordCount() > 0) {
						bco.addAttachFile(requestData, onlineCtx);
						
						docId = rsFile.get(0, "DOC_ID"); // 최초 등록시 첨부 파일 정보에서 DOC_ID
						mMovMaster.put("DOC_ID", docId); // 
					}
					
					update("DISATR01100.updateProdOutM", mMovMaster, onlineCtx);
				
				}
			}
		}
		
		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"Insert:" + 0, " Update:" + 0, " Delete:" + 0 });

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
	
	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * -대리점간 이동등록 삭제
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteProdMovList(IDataSet requestData,
			IOnlineContext onlineCtx) {
		
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("deleteProdMovList method start.");
		}

		IRecordSet rsMovMaster = requestData.getRecordSet("ds_movMaster");
		IRecordSet rsProdMovList = requestData.getRecordSet("ds_prodMovList");
		
		Map mMovMaster = null;
		Map mMovDtl = null;
		String sDate = "";
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		
		long currentTime = System.currentTimeMillis();
		sDate = df.format(new Date(currentTime));
		
		if (rsMovMaster != null) {
			
			mMovMaster = rsMovMaster.getRecordMap(0);
			update("DISATR01100.updateProdInMForDel", mMovMaster, onlineCtx);
			update("DISATR01100.updateProdOutMForDel", mMovMaster, onlineCtx);
			
			update("DISATR01100.updateProdInoutHstForDelAll", mMovMaster, onlineCtx);
			update("DISATR01100.updateProdMovInDForDelAll", mMovMaster, onlineCtx);
			update("DISATR01100.updateProdMovOutDForDelAll", mMovMaster, onlineCtx);
			
			for (int i = 0; i < rsProdMovList.getRecordCount(); i++) {
				
				mMovDtl = rsProdMovList.getRecordMap(i);
				
				mMovDtl.put("dis_st", "01"); // 재고상태 : 01(가용)
				mMovDtl.put("hld_plc_id", mMovDtl.get("out_plc_id"));
				mMovDtl.put("inout_dt", sDate);
				mMovDtl.put("inout_cl", "300"); // 입출고구분 : 300(재고이동)
				mMovDtl.put("inout_dtl_cl", "304"); // 입출고상세구분 : 304(이동출고취소)
				
				if("Y".equals(mMovDtl.get("TRMS_YN"))){
					mMovDtl.put("trms_yn","N");
					mMovDtl.put("trms_st","R");
					
					mMovDtl.put("con_sale_yn","N");
					mMovDtl.put("con_sale_cncl_dt",sDate);
					
				}else{
					mMovDtl.put("trms_yn","N");
					mMovDtl.put("trms_st","N");
					
					mMovDtl.put("con_sale_yn","N");
					mMovDtl.put("con_sale_dt","");
					mMovDtl.put("con_sale_cncl_dt","");
					mMovDtl.put("con_sale_plc_id","");
				}
				
				update("DISATR01100.updateProdDis", mMovDtl, onlineCtx);
				
			}
			
		}
		
		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"Insert:" + 0, " Update:" + 0, " Delete:" + 0 });

		return result;
	}

}