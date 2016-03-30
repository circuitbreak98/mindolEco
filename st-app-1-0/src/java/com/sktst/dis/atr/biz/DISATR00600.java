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

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.user.PsLoginUserInfo;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-18 16:41:20</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class DISATR00600 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 이동입고내역조회
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 이동 입고 내역을 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : from_out_schd_dt [출고지시일(시작)] 
	 *	- field : to_out_schd_dt [출고지시일(끝)] 
	 *	- field : pos_agency_id [대리점코드] 
	 *	- field : in_plc_id [입고처코드] 
	 *	- field : out_plc_id [출고처코드] 
	 *	- field : ser_num [일련번호] 
	 *	- field : in_fix_yn [입고확정여부] 
	 *	- field : ser_in [개별입력여부] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : inFixSerList
	 *		- field : out_mgmt_no [출고관리번호] 
	 *		- field : prod_cl [상품구분] 
	 *		- field : mfact_id [제조사코드] 
	 *		- field : prod_cd [상품코드] 
	 *		- field : prod_nm [상품명] 
	 *		- field : color_cd [색상코드] 
	 *		- field : ser_num [일련번호] 
	 *		- field : out_fix_qty [출고확정수량] 
	 *		- field : out_fix_yn [출고확정여부] 
	 *		- field : out_fix_dt [출고확정일] 
	 *		- field : in_fix_qty [입고확정수량] 
	 *		- field : in_fix_yn [입고확정여부] 
	 *		- field : in_fix_dt [입고확정일] 
	 *		- field : mov_out_qty [이동출고수량] 
	 *		- field : out_schd_dt [출고지시일] 
	 *		- field : out_plc_id [출고처코드] 
	 *		- field : in_plc_id [입고처코드] 
	 *		- field : dis_dis_st [재고상태] 
	 *		- field : dis_hld_plc_id [보유처코드] 
	 *		- field : dis_last_inout_cl [최종입출고구분] 
	 *		- field : dis_last_inout_dtl_cl [최종입출고상세구분] 
	 *		- field : rmks [비고]
	 *		- field : upd_cnt [수정카운트] 
	 *		- field : ch_yn [체크여부]
	 *		- field : mfact_nm [제조사명] 
	 *		- field : out_plc_nm [출고처명] 
	 *		- field : in_plc_nm [입고처명] 
	 *		- field : dis_hld_plc_nm [보유처명] 
	 */
	public IDataSet getInFixList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {	
			log.debug("getInFixList method start.");
		}
		
		Map mCond = new HashMap();

		IRecordSet rsCond = requestData.getRecordSet("ds_cond");	// 조회조건 

		mCond = getProMap(rsCond.getRecordMap(0), onlineCtx);

		IRecordSet rs = queryForRecordSet("DISATR00600.selectInFixList",
				mCond, onlineCtx);

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


}