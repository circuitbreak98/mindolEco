/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.pdm.biz;

import java.math.BigDecimal;
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

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : 상품 매핑 정보를 조회, 입력, 수정, 삭제 한다.</li>
 * <li>작성일 : 2009-06-20 15:54:36</li>
 * </ul>
 *
 * @author 김중일 (kimjoongil)
 */
public class BASPDM00500 extends BaseBizUnit {

	/**
	 * 상품매핑정보조회
	 *
	 * @author 김중일 (kimjoongil)
	 *  - 상품 매핑 정보를 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : prod_map_cd [상품매핑코드] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : prodMapList
	 *		- field : prod_map_cd [상품매핑코드] 
	 *		- field : prod_map_cl [상품매핑구분] 
	 *		- field : prod_cd [상품코드] 
	 *		- field : prod_nm [상품명] 
	 *		- field : color_cd [색상코드] 
	 */
	public IDataSet getProdMapList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdMapList method start");
		}

		IRecordSet rs = queryForRecordSet("BASPDM00500.selectProdMapList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ProdMapList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("ProdMapList", rs);

		return result;
	}

	/**
	 * 상품 매핑 정보를 저장, 수정, 삭제 한다.
	 *
	 * @author 김중일 (kimjoongil)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public IDataSet saveProdMapInfo(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("saveProdMapInfo method start");
		}

		Map mMov = new HashMap();
		Map mRet = new HashMap();
		int insCnt = 0;
		int uptCnt = 0;
		int delCnt = 0;
		int totCnt = 0;
		String sCnt = "0";

		IRecordSet rsProdMapList = requestData.getRecordSet("ds_prodMapList");

		if (rsProdMapList != null) {
			for (int i = 0; i < rsProdMapList.getRecordCount(); i++) {
				mMov = getProMap(rsProdMapList.getRecordMap(i), onlineCtx);

				// 삭제 프로시저 호출
				if (DELETE_FLAG.equalsIgnoreCase(mMov.get(CUD_FLAG_PARAM)
						.toString())) {
					delete("BASPDM00500.deleteProdMapInfo", mMov, onlineCtx);
					delCnt++;
				}

				// 입력 프로시저 호출
				if (INSERT_FLAG.equalsIgnoreCase(mMov.get(CUD_FLAG_PARAM)
						.toString())) {
					
					mRet = null;
					sCnt = "0";

					mRet = (Map) queryForObject(
							"BASPDM00500.selectProdMapCheck", mMov, onlineCtx);
					sCnt = ((BigDecimal) mRet.get("CNT")).toString();

					if (!"0".equals(sCnt)) {
						// 이미 등록된 상품코드와 색상코드가 존재합니다.
						throw new BizRuntimeException("PSMW5008", new String[] {
								"상품코드("+mMov.get("prod_cd").toString() + ")와 색상코드(" 
								+ mMov.get("color_cd").toString() + ")"});
					}
					
					insert("BASPDM00500.saveProdMapInfo", mMov, onlineCtx);
					insCnt++;
				}

				// 수정 프로시저 호출
				if (UPDATE_FLAG.equalsIgnoreCase(mMov.get(CUD_FLAG_PARAM)
						.toString())) {

					mRet = null;
					sCnt = "0";

					mRet = (Map) queryForObject(
							"BASPDM00500.selectProdMapCheck", mMov, onlineCtx);
					sCnt = ((BigDecimal) mRet.get("CNT")).toString();

					if (!"0".equals(sCnt)) {
						// 이미 등록된 상품코드와 색상코드가 존재합니다.
						throw new BizRuntimeException("PSMW5008", new String[] {
								"상품코드("+mMov.get("prod_cd").toString() + ")와 색상코드(" 
								+ mMov.get("color_cd").toString() + ")"});
					}
					
					update("BASPDM00500.saveProdMapInfo", mMov, onlineCtx);
					uptCnt++;
				}

				log.trace("mMov==>" + i + "::" + mMov.toString());

			}
		}

		totCnt = delCnt + insCnt + uptCnt;

		if (totCnt < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"Insert:" + insCnt, " Update:" + uptCnt,
						" Delete:" + delCnt });

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

		// 레코드별 입력값 추출 및 셋팅
		for (Iterator iter = orgMap.keySet().iterator(); iter.hasNext();) {
			key = iter.next().toString().toLowerCase();
			value = "";

			tmpStr = orgMap.get(key);
			value = tmpStr == null ? "" : tmpStr.toString();

			retMap.put(key, value);
		}

		return retMap;
	}

	/**
	 * 상품매핑정보체크
	 *
	 * @author 김중일 (kimjoongil)
	 *  - 사품매핑 정보가 이미 등록되었는지 체크한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : prod_cd [상품코드] 
	 *	- field : color_cd [색상코드] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : prodMapCheck
	 *		- field : cnt [레코드수] 
	 */
	public IDataSet getProdMapCheck(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdMapCheck method start");
		}

		IRecordSet rs = queryForRecordSet("BASPDM00500.selectProdMapCheck",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ProdMapCheck");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("ProdMapCheck", rs);

		return result;
	}
}