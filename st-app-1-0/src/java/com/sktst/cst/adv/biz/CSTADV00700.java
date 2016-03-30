/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.cst.adv.biz;

import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-08-30 13:32:16</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class CSTADV00700 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getAllConPrch(IDataSet requestData, IOnlineContext onlineCtx)
			throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("CSTADV00700.getAllConPrch",
				requestData.getFieldMap(), onlineCtx);

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_list", rs);

		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");

		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "ds_list"); // 복호화 할 데이터셋 명
		rec.add("col_name1", "ACC_NO"); // 복호화 컬럼1
		rec.add("col_name2", "TEL_NO"); // 복호화 컬럼2
		rec.add("col_name3", "ASIANA_CARD_NO"); // 복호화 컬럼3
		rec.add("col_name7", "RES_NO"); // col_name7 :: 주민번호 앞 6자리 잘라줌....
		iSet.addRecord(rec);

		responseData.putRecordSet("cptItem", iSet);

		IDataSet rsData = bco.decode(responseData, onlineCtx);

		return rsData;

//		return responseData;
	}

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveConCrypto(IDataSet requestData, IOnlineContext onlineCtx)
			throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("CSTADV00700.saveConCrypto method start");
		}
/*
		IRecordSet rs = queryForRecordSet("CSTADV00700.getConsultMgmtAll",
				requestData.getFieldMap(), onlineCtx);

		log.debug("CSTADV00700.saveConCrypto : " + rs.getRecordCount());

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("List", rs);
*/
		/*
		 * 암호화 시작
		 * 암호화 시작 -> 암호화 종료 소스 모두 복사 
		 * 변경 : 암호화 할 데이터셋 명/암호화 컬럼1/암호화 컬럼2
		 * 암호화 컬럼은 최대 6개 까지 확장 가능 : col_name1 ~ 6
		 */
/*				
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");

		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		//RES_NO, ACC_NO, TEL_NO, SVC_NO
		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "List"); // 암호화 할 데이터셋 명
		rec.add("col_name1", "RES_NO"); // 암호화 컬럼1
		rec.add("col_name2", "ACC_NO"); // 암호화 컬럼2
		rec.add("col_name3", "TEL_NO"); // 암호화 컬럼3
		iSet.addRecord(rec);

		responseData.putRecordSet("cptItem", iSet); // 암호화 정보 추가

		IDataSet rsData = bco.encode(responseData, onlineCtx);
*/		
		/*
		 * 암호화 종료 
		 */
/*
		IRecordSet rows = rsData.getRecordSet("List");
		Map map = null;
*/		
		int cnt = 0;
/*		
		for (int i = 0; i < rows.getRecordCount(); i++) {
			map = rows.getRecordMap(i);

//			update("CSTADV00700.updateConCrypto", map, onlineCtx);

			cnt++;
		}
*/
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID,
				new String[] { "Update : " + cnt });
	}

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet savePrcCrypto(IDataSet requestData, IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("CSTADV00700.savePrcCrypto method start");
		}
/*
		IRecordSet rs = queryForRecordSet("CSTADV00700.getPrchsMgmtAll",
				requestData.getFieldMap(), onlineCtx);

		log.debug("CSTADV00700.savePrcCrypto : " + rs.getRecordCount());

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("List", rs);
*/
		/*
		 * 암호화 시작
		 * 암호화 시작 -> 암호화 종료 소스 모두 복사 
		 * 변경 : 암호화 할 데이터셋 명/암호화 컬럼1/암호화 컬럼2
		 * 암호화 컬럼은 최대 6개 까지 확장 가능 : col_name1 ~ 6
		 */
/*		
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");
		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		//RES_NO, ACC_NO, TEL_NO, SVC_NO
		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "List"); // 암호화 할 데이터셋 명
		rec.add("col_name1", "RES_NO"); // 암호화 컬럼1
		rec.add("col_name2", "ACC_NO"); // 암호화 컬럼2
		rec.add("col_name3", "TEL_NO"); // 암호화 컬럼3

		iSet.addRecord(rec);

		responseData.putRecordSet("cptItem", iSet); // 암호화 정보 추가

		IDataSet rsData = bco.encode(responseData, onlineCtx);
*/		
		/*
		 * 암호화 종료 
		 */
/*
		IRecordSet rows = rsData.getRecordSet("List");
		Map map = null;
*/		
		int cnt = 0;
/*		
		for (int i = 0; i < rows.getRecordCount(); i++) {
			map = rows.getRecordMap(i);

//			update("CSTADV00700.updatePrcCrypto", map, onlineCtx);

			cnt++;
		}
*/
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID,
				new String[] { "Update : " + cnt });
				
	}

}