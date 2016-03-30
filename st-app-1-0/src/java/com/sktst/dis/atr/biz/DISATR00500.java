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

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.user.PsLoginUserInfo;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-18 14:23:40</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class DISATR00500 extends BaseBizUnit {

	/**
	 * 출고확정내역조회 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 출고 확정 내역을 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : out_mgmt_no [출고관리번호] 
	 *	- field : out_plc_id [출고처ID] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : outFixRegList
	 *		- field : prod_cl [상품구분] 
	 *		- field : mfact_id [제조사ID] 
	 *		- field : mfact_nm [제조사명] 
	 *		- field : bar_cd_typ [바코드타입] 
	 *		- field : out_mgmt_no [출고번호] 
	 *		- field : prod_cd [상품코드] 
	 *		- field : prod_nm [상품명] 
	 *		- field : color_cd [색상코드] 
	 *		- field : ser_num [일련번호] 
	 *		- field : out_fix_yn [출고확정여부] 
	 *		- field : out_fix_qty [출고확정수량] 
	 *		- field : rmks [비고] 
	 *		- field : in_fix_yn [입고확정여부] 
	 *		- field : row_flg [확정원본여부] 
	 *		- field : out_schd_dt [출고예정일자] 
	 *		- field : out_plc_id [출고처ID] 
	 *		- field : in_plc_id [입고처ID] 
	 *		- field : upd_cnt [UPDATE COUNT] 
	 *		- field : act_flag [업무처리구분] 
	 */
	public IDataSet getOutFixRegOrgList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("getOutFixRegList method start");
		}

		IRecordSet rs = queryForRecordSet("DISATR00500.selectOutFixRegOrgList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("OutFixRegOrgList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("OutFixRegOrgList", rs);

		return result;
	}

	/**
	 * 이동출고확정등록
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public IDataSet saveOutFixRegList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("saveOutFixRegList method start");
		}

		Map mMov = new HashMap();
		Map mBas = new HashMap();
		int insCnt = 0;
		int uptCnt = 0;
		int delCnt = 0;
		String sSuccessCd = "S";
		String sRmks = "";
		String sOutPlcID = "";
		String sInPlcID = "";
		String sOutFixDt = "";

		IRecordSet rsBasInfo = requestData.getRecordSet("ds_in");
		IRecordSet rsOutFixList = requestData
				.getRecordSet("ds_outFixRegOrgList");

		if (rsBasInfo != null) {
			mBas = rsBasInfo.getRecordMap(0);
			sOutPlcID = mBas.get("OUT_PLC_ID").toString();
			sInPlcID = mBas.get("IN_PLC_ID").toString();
			sOutFixDt = mBas.get("OUT_FIX_DT").toString();
			sRmks = mBas.get("RMKS").toString();
		}

		if (rsOutFixList != null) {
			for (int i = 0; i < rsOutFixList.getRecordCount(); i++) {
				mMov = getProMap(rsOutFixList.getRecordMap(i), onlineCtx);

				mMov.put("out_plc_id", sOutPlcID);
				mMov.put("in_plc_id", sInPlcID);
				mMov.put("out_fix_dt", sOutFixDt);
				mMov.put("rmks", sRmks);

				// 삭제 프로시저 호출
				if (DELETE_FLAG.equalsIgnoreCase(mMov.get(CUD_FLAG_PARAM)
						.toString())) {
					mMov.put("proc_flag", "D");
					mMov.put("act_flag", "OF");
					queryForObject("DISATR00200.callProcedure", mMov, onlineCtx);
					delCnt++;
				}

				// 입력 프로시저 호출
				if (INSERT_FLAG.equalsIgnoreCase(mMov.get(CUD_FLAG_PARAM)
						.toString())) {
					mMov.put("proc_flag", "I");
					mMov.put("act_flag", "OF");
					queryForObject("DISATR00200.callProcedure", mMov, onlineCtx);
					insCnt++;
				}

				// 수정 프로시저 호출
				if (UPDATE_FLAG.equalsIgnoreCase(mMov.get(CUD_FLAG_PARAM)
						.toString())) {
					mMov.put("proc_flag", "U");

					queryForObject("DISATR00200.callProcedure", mMov, onlineCtx);
					uptCnt++;
				}

				log.trace("mMov==>" + i + "::" + mMov.toString());

				if (!sSuccessCd.equals(mMov.get("errcode").toString())) {
					throw new BizRuntimeException(mMov.get("errmsg").toString());
				}

			}
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"Insert:" + insCnt, " Update:" + uptCnt,
						" Delete:" + delCnt });

		return result;
	}

	/**
	 * 상품매핑정보조회
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 상품 매핑 정보를 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : prod_map_cd [상품매핑코드] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : prodMapInfo
	 *		- field : prod_map_cd [상품매핑코드] 
	 *		- field : prod_map_cl [상품매핑구분] 
	 *		- field : prod_cd [상품코드] 
	 *		- field : color_cd [색상코드] 
	 */
	public IDataSet getProdMapInfo(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("getProdMapInfo method start");
		}

		IRecordSet rs = queryForRecordSet("DISATR00500.selectProdMapInfo",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ProdMapInfo");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("ProdMapInfo", rs);

		return result;
	}

	/**
	 * 입력 멥 생성
	 *
	 * @author 이문규 (leemunkyu)
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

		retMap.put("mov_seq", getIntMem(retMap.get("mov_seq")));
		retMap.put("mov_out_qty", getIntMem(retMap.get("mov_out_qty")));
		retMap.put("out_fix_qty", getIntMem(retMap.get("out_fix_qty")));
		retMap.put("upd_cnt", getIntMem(retMap.get("upd_cnt")));

		// 공통값 셋팅
		retMap.put("errcode", "");
		retMap.put("errmsg", "");
		retMap.put("out_cl", "301"); // 301:이동출고
		retMap.put("in_fix_dt", "");
		retMap.put("in_fix_yn", "N");
		retMap.put("in_fix_qty", 0);
		retMap.put("dlvy_typ", "");
		retMap.put("div_co_id", "");
		retMap.put("dlvy_unit", "");
		retMap.put("dlvy_qty", 0);
		retMap.put("dlvy_dtm", "");
		retMap.put("bf_dlvy_req_dt", "");
		retMap.put("bf_dlvy_req_seq", 0);
		retMap.put("bf_dlv_co_id", "");
		retMap.put("cncl_yn", "N");
		retMap.put("sale_cntr_id", "");
		retMap.put("user_id", userInfo.getLoginId());

		return retMap;
	}

	/**
	 * 소수점 제거
	 *
	 * @author 이문규 (leemunkyu)
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
}