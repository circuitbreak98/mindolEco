/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.atr.biz;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-15 17:31:21</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class DISATR00400 extends BaseBizUnit {

	/**
	 * 구성품이동출고확정내역관리
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 이동 출고 확정 내역 조회 및 관련 업무처리를 설정한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : from_out_dt [출고지시시작일] 
	 *	- field : to_out_dt [출고지시종료일] 
	 *	- field : out_fix_yn [출고확정여부] 
	 *	- field : out_plc_id [출고처] 
	 *	- field : in_plc_id [입고처] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : outFixList
	 *		- field : out_mgmt_no [출고관리번호] 
	 *		- field : out_plc_id [출고처] 
	 *		- field : out_plc_nm [출고처명] 
	 *		- field : in_plc_id [입고처] 
	 *		- field : in_plc_nm [입고처명] 
	 *		- field : out_schd_dt [출고지시일] 
	 *		- field : out_fix_dt [출고확정일] 
	 *		- field : out_fix_yn [출고확정여부] 
	 *		- field : mov_out_qty [이동출고수량] 
	 *		- field : dlvy_typ [배송유형] 
	 *		- field : dlv_co_id [배송사ID] 
	 *		- field : dlvy_unit [단위] 
	 *		- field : dlvy_qty [배송수량] 
	 *		- field : dlvy_dtm [배송일시] 
	 *		- field : upd_cnt [UPDATE COUNT] 
	 */
	public IDataSet getOutFixList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("getOutFixList method start");
		}

		IRecordSet rs = queryForRecordSet("DISATR00400.selectOutFixList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("OutFixList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("OutFixList", rs);

		return result;
	}

	/**
	 * 거래명세조회
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 *  - 거래명세(출고증)에 사용할 리스트를 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : out_mgmt_no [출고관리번호] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : dealDescList
	 *		- field : out_mgmt_no [출고관리번호] 
	 *		- field : prod_cd [상품코드] 
	 *		- field : prod_nm [상품명] 
	 *		- field : color_cd [색상코드] 
	 *		- field : ser_num [일련번호] 
	 *		- field : out_fix_qty [출고확정수량] 
	 */
	public IDataSet getDealDescList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getDealDescList method start");
		}

		IRecordSet rs = queryForRecordSet("DISATR00400.selectDealDesc",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("DealDescList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("DealDescList", rs);

		return result;
	}

	/**
	 * 거래처정보조회
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 거래명세표용 거래처 정보를 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : out_mgmt_no [출고관리번호] 
	 *	- field : deal_co_cd [거래처코드] 
	 *	- field : eff_dt [유효일자] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : out_mgmt_no [필드1] 
	 *	- field : cur_dt [필드2] 
	 *	- field : biz_num [필드3] 
	 *	- field : deal_co_nm [필드4] 
	 *	- field : rep_user_nm [필드5] 
	 *	- field : addr [필드6] 
	 *	- field : biz_con [필드7] 
	 *	- field : typ_of_biz [필드8] 
	 */
	public IDataSet getDealCoInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getDealCoInfo method start");
		}

		IRecordSet rs = queryForRecordSet("DISATR00400.selectDealCoInfo",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("DealCoInfo");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("DealCoInfo", rs);

		return result;
	}

}