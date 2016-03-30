/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.dco.biz;

import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.DataSet;
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
 * <li>작성일 : 2011-07-19 15:12:10</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class DISDCO00100 extends BaseBizUnit {

	/**
	 * 거래처 코드별 리스트 조회
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 거래처 구분 코드별로 그룹되어진 리스트를 리턴한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : deal_co_cl [거래처구분코드] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : deal_co_list
	 *		- field : code [코드] 
	 *		- field : value [명] 
	 */
	public IDataSet getDealCoList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("getDealCoList method start");
		}

		IRecordSet rs = queryForRecordSet("DISDCO00100.selectDealCo",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("DealCoList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("DealCoList", rs);

		return result;
	}

	/**
	 * 상품 색상 조회
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 상품코드에 해당하는 색상 리스트를 리턴한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdColorList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdColorList method start");
		}
		IRecordSet rs = queryForRecordSet("DISDCO00100.selectProdColor",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ProdColorList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("ProdColorList", rs);

		return result;
	}

	/**
	 * 공통 코드 조회
	 *
	 * @author 김중일 (kimjoongil)
	 *  - 공통 코드를 입력받아 사용되는 명칭들의 리스트를 리턴한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getCommList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getCommList method start");
		}

		IRecordSet rs = queryForRecordSet("DISDCO00100.selectComm", requestData
				.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("CommList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("CommList", rs);

		return result;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 일련번호로 상품정보를 취득한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdInfoBySerNum(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("DISDCO00100.getProdInfoBySerNum method start");
		}

		IRecordSet rs = queryForRecordSet("DISDCO00100.getProdInfoBySerNum",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_prod");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("ds_prod", rs);

		return result;
	}

	/**
	 * 상품 정보 조회
	 *
	 * @author 김중일 (kimjoongil)
	 *  - 입력된 상품 코드의 유효성을 검사를 위한 데이터를 리턴한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : prod_cd [상품코드] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : prod_cd [상품구분] 
	 *	- field : prod_nm [상품명] 
	 *	- field : mfact_id [제조사] 
	 *	- field : prod_cl [상품구분] 
	 *	- field : bar_cd_typ [바코드타입] 
	 */
	public IDataSet getProdInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		String cnt = "1";

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdInfo method start");
		}

		Map map = (Map) queryForObject("DISDCO00100.selectProdInfo",
				requestData.getFieldMap(), onlineCtx);

		if (map == null) {
			cnt = "0";
			map = new HashMap();
			// throw new NoRecordFoundException();
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { cnt });

		result.putFieldObjectMap(map);

		return result;
	}

}