/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.dtr.biz;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-19 16:19:37</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class DISDTR00300 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 이동재고상품조회
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 이동 시킬 재고 상품들을 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : out_plc_id [출고처ID] 
	 *	- field : ser_num [일련번호] 
	 *	- field : prod_cd [상품코드] 
	 *	- field : prod_cl [상품구분] 
	 *	- field : mfact_id [제조사ID] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : inProdList
	 *		- field : prod_cl [상품구분] 
	 *		- field : mfact_id [제조사ID] 
	 *		- field : prod_cd [상품코드] 
	 *		- field : color_cd [색상코드] 
	 *		- field : ser_num [일련번호] 
	 *		- field : bad_yn [불량여부] 
	 *		- field : dis_st [재고상태] 
	 *		- field : cur_cnt [현재고] 
	 *		- field : mov_cnt [이동재고] 
	 *		- field : mov_chk [선택여부] 
	 *		- field : out_cl [출고구분] 
	 *		- field : mov_seq [이동순번] 
	 *		- field : out_fix_yn [출고확정여부] 
	 *		- field : in_fix_yn [입고확정여부] 
	 */
	public IDataSet getInProdList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("getInProdList method start");
		}

		IRecordSet rs = queryForRecordSet("DISDTR00300.selectInProdList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("InProdList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("InProdList", rs);

		return result;
	}

}