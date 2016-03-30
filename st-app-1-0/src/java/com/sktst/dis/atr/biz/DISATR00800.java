/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.atr.biz;

import java.util.HashMap;
import java.util.Iterator;
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
import nexcore.framework.integration.db.NoRecordAffectedException;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-11-01 15:18:51</li>
 * </ul>
 *
 * @author 안희수 (ahnheesoo)
 */
public class DISATR00800 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 안희수 (ahnheesoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet selectAllInFixList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("selectAllInFixList method start.");
		}

		Map mCond = new HashMap();

		IRecordSet rsCond = requestData.getRecordSet("ds_cond"); // 조회조건 

		mCond = getProMap(rsCond.getRecordMap(0), onlineCtx);

		IRecordSet rs = queryForRecordSet("DISATR00800.selectInFixList", mCond,
				onlineCtx);

		log.debug("## DISATR00800.selectInFixList ## :::::::::::::::::" + rs);

		if (rs == null) {
			rs = new RecordSet("InFixList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("InFixList", rs);

		return result;
	}

	/**
	 * 
	 *
	 * @author 안희수 (ahnheesoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveAllMoveInFix(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("############### saveAllMoveInFix method start");
		}

		Map mMov = new HashMap();
		Map mBas = new HashMap();
		Map mAll = new HashMap();
		int insCnt = 0;
		int uptCnt = 0;
		int delCnt = 0;
		String sSuccessCd = "S";
		String sInMgmtNo = "";
		String sSaveFlag = "";

		IRecordSet rsBasInfo = requestData.getRecordSet("ds_in");
		IRecordSet rsFixList = requestData.getRecordSet("ds_inFixList");

		PsLoginUserInfo userInfo = (PsLoginUserInfo) onlineCtx.getUserInfo();

		if (rsBasInfo != null) {
			mBas = rsBasInfo.getRecordMap(0);
		}
		log.debug("###############>>>>> rsFixList : " + rsFixList);
		log.debug("###############>>>>> rsBasInfo : " + rsBasInfo);
		log.debug("###############>>>>> rsFixList.getRecordCount() : "
				+ rsFixList.getRecordCount());

		if (rsFixList != null) {

			for (int i = 0; i < rsFixList.getRecordCount(); i++) {
				
				// 대리점간이동입고관리에서 호출시만! - 출고관리번호별 해당 모델별 FOR문 수행후 MAP 정보 CLEAR시킴. 
				if("ALL".equals(mMov.get("save_gubun"))){
					mMov.clear();
					mAll.clear();
				}
				
				mAll = getProMap(rsFixList.getRecordMap(i), onlineCtx);
				mMov.putAll(mAll);
				mAll = getProMap(rsBasInfo.getRecordMap(0), onlineCtx);
				mMov.putAll(mAll);
				

				// 출고수량 
				//sTotOutQty = mMov.get("tot_out_qty").toString();
				//				// 이동입고확정 시 재고정보 변경유무 체크 
				//				sDisUpdCnt = getDisLastUpdCnt(mMov, onlineCtx);

				//				log.debug("############### sDisUpdCnt 1 : " + sDisUpdCnt);
				//				log.debug("###############  upd_cnt 1 : " + mMov.get("upd_cnt"));
				//				if (!sDisUpdCnt.equals(mMov.get("upd_cnt"))) {
				//					throw new NoRecordAffectedException(
				//							"이동입고확정 시 재고정보가 변경되었습니다.");
				//				}

				//				// SAVE_FALG : Y => 입고처리  N => 입고취소 
				//				sSaveFlag = mMov.get("save_flag").toString();

				//입고관리번호 생성 
				sInMgmtNo = mMov.get("in_mgmt_no") == null ? "" : mMov.get(
						"in_mgmt_no").toString();
				
				if (sInMgmtNo == null || "".equals(sInMgmtNo)) {

					// 입고관리번호 생성 	
					sInMgmtNo = getInMgmtNo("IN", userInfo.getLoginId(),
							onlineCtx);
					
					mMov.put("in_cl", "302"); // 입고구분 : 이동입고(ZDIS_C_00050)
					mMov.put("in_mgmt_no", sInMgmtNo);
					mMov.put("in_st", "01"); // 입고상태 - 미입고 

					//					 입고 테이블 생성
					insert("DISATR00800.insertTdisInProdM", mMov, onlineCtx);
				}
				
				log.debug("###############  mMov 1 : " + mMov);
				
				// 저장할 출고관리번호에 해당하는 데이터 
				IRecordSet rsSaveListData = queryForRecordSet(
						"DISATR00800.selectSaveListData", mMov, onlineCtx);

				log.debug("###############  rsSaveListData.getRecordCount() : "
						+ rsSaveListData.getRecordCount());

				for (int m = 0; m < rsSaveListData.getRecordCount(); m++) {
					
					mMov.putAll(getProMap(rsSaveListData.getRecordMap(m), onlineCtx));
					log.debug("###############  mMov 2 : " + mMov);

					sSaveFlag = mMov.get("save_flag").toString();
					log.debug("###############  sSaveFlag : " + sSaveFlag);

					if ("Y".equals(sSaveFlag)) {

						mMov.put("in_mgmt_no", sInMgmtNo);
						mMov.put("inout_cl", "300"); // 입고구분 : 이동입고(ZDIS_C_00050)
						mMov.put("in_cl", "302"); // 입고구분 : 이동입고(ZDIS_C_00050)
						mMov.put("cncl_yn", "N");
						mMov.put("in_fix_yn", "Y");
						mMov.put("dis_st", "01"); //  재고상태 : 비가용(01)

						// 입고상세 테이블 생성					
						insert("DISATR00800.insertTdisInProdDtlD", mMov,
								onlineCtx);

					} else if ("N".equals(sSaveFlag)) {

						mMov.put("in_mgmt_no", sInMgmtNo);
						mMov.put("inout_cl", "300"); // 입고구분 : 이동입고(ZDIS_C_00050)
						mMov.put("in_cl", "303"); // 입고구분 : 이동입고취소(ZDIS_C_00050)
						mMov.put("in_st", "01"); //입고상태 : 미입고 
						mMov.put("in_fix_yn", "N");
						mMov.put("cncl_yn", "Y");
						mMov.put("in_fix_qty", "0");
						mMov.put("dis_st", "02"); //  재고상태 : 가용(02)
						mMov.put("in_fix_dt", "");

						// 입고상세 테이블 생성					
						insert("DISATR00800.updateTdisInProdDtlD", mMov,
								onlineCtx);
					}

					log.debug("###############5 LAST VIEW mMov : " + mMov);

					// 상품입출고상세 테이블 생성					
					insert("DISATR00800.insertTdisProdInoutHst", mMov,
							onlineCtx);

					//				 상품재고 테이블 갱신
					update("DISATR00800.updateTdisProdDis", mMov, onlineCtx);

					if (rsSaveListData.getRecordCount() == m + 1) {

						//mMov.put("tot_out_qty", sTotOutQty);
						log.debug("###############6  LAST VIEW mMov : " + mMov);
						//					입고테이블 최종상태 갱신 
						IRecordSet rsUpdateInMdata = queryForRecordSet(
								"DISATR00800.selectTdisInProdM", mMov,
								onlineCtx);

						mMov.putAll(getProMap(rsUpdateInMdata.getRecordMap(0),
								onlineCtx));
						mMov.put("in_mgmt_no", sInMgmtNo);

						log.debug("###############8  LAST VIEW mMov : " + mMov);
						//					 입고 테이블 갱신
						update("DISATR00800.updateTdisInProdM", mMov, onlineCtx);
					}

					insCnt++;
				}
			}
			if (!sSuccessCd.equals(mMov.get("errcode").toString())) {
				//					throw new BizRuntimeException(mMov.get("errmsg").toString());
			}
		}
		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"Insert:" + insCnt, " Update:" + uptCnt,
						" Delete:" + delCnt });

		return result;
	}

	/**
	 * 이동 입고 관리 번호 생성
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 이동 입고 관리 번호를 생성한다.
	 * @param mgmtCd 
	 *            관리번호종류코드 
	 * @param userId 
	 *            사용자아이디 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public String getInMgmtNo(String mgmtCd, String userId,
			IOnlineContext onlineCtx) {
		String retMgmtNo = "";

		Map inMap = new HashMap();

		inMap.put("mgmt_no_cd", mgmtCd);
		inMap.put("user_id", userId);
		inMap.put("errcode", "");
		inMap.put("errmsg", "");
		inMap.put("mgmt_no", "");

		queryForObject("DISATR00800.selectOutMgmtNo", inMap, onlineCtx);

		retMgmtNo = inMap.get("mgmt_no").toString();

		return retMgmtNo;
	}

	/**
	 * 
	 *
	 * @author 안희수 (ahnheesoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public String getDisLastUpdCnt(Map mAll, IOnlineContext onlineCtx) {

		String sDisUpdCnt = "";

		queryForObject("DISATR00800.getDisUpdCnt", mAll, onlineCtx);

		sDisUpdCnt = mAll.get("upd_cnt").toString();

		return sDisUpdCnt;
	}

	/**
	 * 입력 멥 생성
	 *
	 * @author 김중일 (kimjoongil)
	 *  - 프로시저를 호출하기 위한 멥을 구성한다.
	 * @param orgMap
	 *            멥을 구성하기 위한 원본 멥
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 Map 객체
	 */
	@SuppressWarnings("unchecked")
	public Map getProMap(Map orgMap, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("getProMap method start");
		}

		Map retMap = new HashMap();
		String key = "";
		String value = "";
		Object tmpStr = null;

		PsLoginUserInfo userInfo = (PsLoginUserInfo) onlineCtx.getUserInfo();

		// 레코드별 입력값 추출 및 셋팅
		for (Iterator iter = orgMap.keySet().iterator(); iter.hasNext();) {
			key = iter.next().toString().toLowerCase();
			value = "";

			tmpStr = orgMap.get(key);
			value = tmpStr == null ? "" : tmpStr.toString();

			retMap.put(key, value);
		}
		//		 공통값 셋팅
		retMap.put("errcode", "");
		retMap.put("errmsg", "");
		retMap.put("user_id", userInfo.getLoginId());

		return retMap;
	}

	/**
	 * 소수점 제거
	 *
	 * @author 김중일 (kimjoongil)
	 *  - 문자열을 입력받아 소수점을 제거한 후 int 형태로 반환한다.
	 * @param oIn
	 *            소수점을 제거하기 위한 입력 객체
	 * @return 문자열에서 소수점을 제거한 값
	 */
	@SuppressWarnings("unchecked")
	public int getIntMem(Object oIn) {
		int iOut = 0;

		if (oIn == null || "".equals(oIn.toString())) {
			iOut = 0;
		} else {
			String sTmp = oIn.toString();
			if (sTmp.indexOf(".") != -1) {
				sTmp = sTmp.substring(0, sTmp.indexOf("."));
			}
			iOut = Integer.parseInt(sTmp);
		}

		return iOut;
	}

	public IDataSet getDisLastUpdCnt(IDataSet requestData,
			IOnlineContext onlineCtx) {

		IDataSet result = new DataSet();
		// TODO 업무 로직 작성 필요

		return result;
	}

}