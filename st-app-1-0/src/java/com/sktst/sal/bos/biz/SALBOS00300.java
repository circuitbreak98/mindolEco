/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.sal.bos.biz;

import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-10-27 15:39:16</li>
 * </ul>
 *
 * @author 김만수 (kimmansoo)
 */
public class SALBOS00300 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getOnlineSaleList(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00300.getOnlineSaleList method start");
		}

		IRecordSet rs = queryForRecordSet("SALBOS00300.getOnlineSaleList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("List", rs);

		/*
		 * 복호화 시작
		 * 복호화 시작 -> 복호화 종료 소스 모두 복사 
		 * 변경 : 복호화 할 데이터셋 명/복호화 컬럼1/복호화 컬럼2
		 * 복호화 컬럼은 최대 6개 까지 확장 가능
		 */
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");

		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "List"); // 복호화 할 데이터셋 명
		rec.add("col_name1", "CUST_BIZ_NUM"); // 복호화 컬럼1
		rec.add("col_name2", "CUST_TEL_NO"); // 복호화 컬럼2
		iSet.addRecord(rec);

		responseData.putRecordSet("cptItem", iSet); // 복호화 정보 추가

		IDataSet rsData = bco.decode(responseData, onlineCtx);
		/*
		 * 복호화 종료
		 */

		return rsData;
	}

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveCrypto(IDataSet requestData, IOnlineContext onlineCtx)
			throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00300.saveCrypto method start");
		}

		IRecordSet rs = queryForRecordSet("SALBOS00300.getConsultMgmt",
				requestData.getFieldMap(), onlineCtx);

		log.debug("SALBOS00300.saveCrypto : " + rs.getRecordCount());

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("List", rs);

		/*
		 * 암호화 시작
		 * 암호화 시작 -> 암호화 종료 소스 모두 복사 
		 * 변경 : 암호화 할 데이터셋 명/암호화 컬럼1/암호화 컬럼2
		 * 암호화 컬럼은 최대 6개 까지 확장 가능 : col_name1 ~ 6
		 */
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");

		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		//RES_NO, ACC_NO, TEL_NO, SVC_NO
		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "List"); // 암호화 할 데이터셋 명
		rec.add("col_name1", "RES_NO"); // 암호화 컬럼1
		rec.add("col_name2", "ACC_NO"); // 암호화 컬럼2
		rec.add("col_name3", "TEL_NO"); // 암호화 컬럼3
//		rec.add("col_name4", "SVC_NO"); // 암호화 컬럼4
		iSet.addRecord(rec);

		responseData.putRecordSet("cptItem", iSet); // 암호화 정보 추가

		IDataSet rsData = bco.encode(responseData, onlineCtx);
		/*
		 * 암호화 종료 
		 */

		IRecordSet rows = rsData.getRecordSet("List");
		Map map = null;
		int cnt = 0;
		for (int i = 0; i < rows.getRecordCount(); i++) {
			map = rows.getRecordMap(i);

			update("SALBOS00300.saveCrypto", map, onlineCtx);

			cnt++;
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID,
				new String[] { "Update : " + cnt });
	}

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveInputCrypto(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00300.saveInputCrypto method start");
		}

		IRecordSet rs = queryForRecordSet("SALBOS00300.getPrchsMgmt",
				requestData.getFieldMap(), onlineCtx);

		log.debug("SALBOS00300.saveInputCrypto : " + rs.getRecordCount());

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("List", rs);

		/*
		 * 암호화 시작
		 * 암호화 시작 -> 암호화 종료 소스 모두 복사 
		 * 변경 : 암호화 할 데이터셋 명/암호화 컬럼1/암호화 컬럼2
		 * 암호화 컬럼은 최대 6개 까지 확장 가능 : col_name1 ~ 6
		 */
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");
		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		//RES_NO, ACC_NO, TEL_NO, SVC_NO
		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "List"); // 암호화 할 데이터셋 명
		rec.add("col_name1", "RES_NO"); // 암호화 컬럼1
		rec.add("col_name2", "ACC_NO"); // 암호화 컬럼2
		rec.add("col_name3", "TEL_NO"); // 암호화 컬럼3
//		rec.add("col_name4", "SVC_NO"); // 암호화 컬럼4
		iSet.addRecord(rec);

		responseData.putRecordSet("cptItem", iSet); // 암호화 정보 추가

		IDataSet rsData = bco.encode(responseData, onlineCtx);
		/*
		 * 암호화 종료 
		 */

		IRecordSet rows = rsData.getRecordSet("List");
		Map map = null;
		int cnt = 0;
		for (int i = 0; i < rows.getRecordCount(); i++) {
			map = rows.getRecordMap(i);

			update("SALBOS00300.saveInputCrypto", map, onlineCtx);

			cnt++;
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID,
				new String[] { "Update : " + cnt });
	}

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveSalInputCrypto(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00300.saveSalInputCrypto method start");
		}

		IRecordSet rs = queryForRecordSet("SALBOS00300.getSalGnrlList",
				requestData.getFieldMap(), onlineCtx);

		log.debug("SALBOS00300.saveSalInputCrypto : " + rs.getRecordCount());

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("List", rs);

		/*
		 * 암호화 시작
		 * 암호화 시작 -> 암호화 종료 소스 모두 복사 
		 * 변경 : 암호화 할 데이터셋 명/암호화 컬럼1/암호화 컬럼2
		 * 암호화 컬럼은 최대 6개 까지 확장 가능 : col_name1 ~ 6
		 */
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");
		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		//RES_NO, ACC_NO, TEL_NO, SVC_NO
		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "List"); // 암호화 할 데이터셋 명
		rec.add("col_name1", "CUST_BIZ_NUM"); // 암호화 컬럼1
		rec.add("col_name2", "CUST_TEL_NO"); // 암호화 컬럼2
		iSet.addRecord(rec);

		responseData.putRecordSet("cptItem", iSet); // 암호화 정보 추가

		IDataSet rsData = bco.encode(responseData, onlineCtx);
		/*
		 * 암호화 종료 
		 */

		IRecordSet rows = rsData.getRecordSet("List");
		Map map = null;
		int cnt = 0;
		for (int i = 0; i < rows.getRecordCount(); i++) {
			map = rows.getRecordMap(i);

			update("SALBOS00300.saveSalInputCrypto", map, onlineCtx);

			cnt++;
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID,
				new String[] { "Update : " + cnt });
	}

}