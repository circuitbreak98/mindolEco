/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.usm.biz;

import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.CryptoUtils;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-04-22 18:03:46</li>
 * </ul>
 *
 * @author 하창주 (hachangjoo)
 */
public class BASUSM00300 extends BaseBizUnit {

	public static final String CHK_TRUE = "1";

	/**
	 * @author	하창주 (hachangjoo)
	 * @param	requestData : 요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param	onlineCtx : 사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return	요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getInfPersonInfoList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		if (log.isDebugEnabled()) {
			log.debug("BASUSM00300.getInfPersonInfoList method start >>>");
		}

		IRecordSet rsCondition = requestData.getRecordSet("ds_condition");
		Map mCondition = rsCondition.getRecordMap(0);
		//HR부서코드 조회
		IRecordSet rsDeptCd = queryForRecordSet("BASUSM00300.getDeptCd", mCondition);
		log.debug("rsDeptCd getRecordCount >> " + rsDeptCd.getRecordCount());

		Map mDeptCd = rsDeptCd.getRecordMap(0);
		String indept = (String) mDeptCd.get("dept_cd");
		log.debug("indept >> " + indept);
		
		if ( indept == null ) {
			mCondition.put("org_id", "");
			mCondition.put("org_nm", "");
		} else {
			mCondition.put("indept", indept);
		}
		log.debug("mCondition >> " + mCondition.toString());

		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("BASUSM00300.getInfPersonInfoList",
				mCondition);

		//응답데이터를 생성한다.
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_inf_person_info", rs);
		return responseData;
	}

	/**
	 * @author	하창주 (hachangjoo)
	 * @param	requestData : 요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param	onlineCtx : 사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return	요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addBasUserMgmt(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASUSM00300.addBasUserMgmt method start");
		}

		int insertCount = 0;

		IRecordSet rsUser = requestData.getRecordSet("ds_inf_person_info");
		int rsCount = rsUser.getRecordCount();
		IRecordSet rsUserCd = null;
		String email = "";
		int index = -1;

		for (int i = 0; i < rsCount; i++) {
			log.debug("chk : " + rsUser.getRecord(i).get("chk"));
			//체크된 건만 수행
			if (rsUser.getRecord(i).get("chk").equals(CHK_TRUE)) {

				//Email @, 앞, 뒤 분리
				email = rsUser.getRecord(i).get("email1");
				email = email == null ? "" : email;
				index = email.lastIndexOf('@');
				if (index != -1) {
					rsUser.getRecord(i)
							.set("email1", email.substring(0, index));
					rsUser.getRecord(i).set("email2",
							email.substring(index + 1));
				}

				rsUserCd = queryForRecordSet("BASUSM00300.getUserCd", rsUser
						.getRecord(i));

				if (rsUserCd.getRecordCount() > 0) {
					if ("U".equals(rsUser.getRecord(i).get("T_FLAG"))) {
						update("BASUSM00300.saveBasUserMgmt", rsUser
								.getRecord(i), onlineCtx);
					} else if ("D".equals(rsUser.getRecord(i).get("T_FLAG"))) {
						update("BASUSM00300.updateEffUserYn", rsUser
								.getRecord(i), onlineCtx);
					}
				} else {
					//비밀번호 '0000' 으로 초기화(암호)
					rsUser.getRecord(i).set("pwd", CryptoUtils.encode("0000"));
					insert("BASUSM00300.addBasUserMgmt", rsUser.getRecord(i),
							onlineCtx);
				}

				insertCount++;
				update("BASUSM00300.updateInfPersonInfo", rsUser.getRecord(i),
						onlineCtx);
			}
		}

		if (insertCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { String
						.valueOf(insertCount) });

	}

	/**
	 * 
	 *
	 * @author 하창주 (hachangjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deletePerson(IDataSet requestData, IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASUSM00300.deletePerson method start");
		}

		int insertCount = 0;

		IRecordSet rsUser = requestData.getRecordSet("ds_inf_person_info");
		int rsCount = rsUser.getRecordCount();

		for (int i = 0; i < rsCount; i++) {
			//체크된 건만 수행
			if (rsUser.getRecord(i).get("chk").equals(CHK_TRUE)) {
				update("BASUSM00300.updateTFlag", rsUser.getRecord(i),
						onlineCtx);
				insertCount++;
			}
		}

		if (insertCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { String
						.valueOf(insertCount) });
	}

}