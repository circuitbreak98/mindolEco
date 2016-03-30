/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.inn.biz;

import java.util.Map;

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
 * <li>설 명 : 발주정보 조회</li>
 * <li>작성일 : 2011-07-19 09:51:02</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class DISINN00100 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * - 발주 리스트 조회
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getOrdList(IDataSet requestData, IOnlineContext onlineCtx) {

		IRecordSet rs = queryForRecordSet("DISINN00100.getOrdList", requestData
				.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_ordList");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_ordList", rs);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 발주관리번호List취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getOrdMgmtNoList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00100.getOrdMgmtNoList method start");
		}

		IRecordSet rs = null;
		Map mData = requestData.getFieldMap();
		String sPosAgencyId = (String) mData.get("posAgencyId");
		String sInPlcId = (String) mData.get("inPlcId");

		// 입고처가 있을 경우
		if (sInPlcId != null && !sInPlcId.equals("")) {

			rs = queryForRecordSet("DISINN00100.getOrdMgmtListByInPlcId",
					requestData.getFieldMap(), onlineCtx);

		} else {

			// 대리점이 있을 경우
			if (sPosAgencyId != null && !sPosAgencyId.equals("")) {

				rs = queryForRecordSet(
						"DISINN00100.getOrdMgmtListByPosAgencyId", requestData
								.getFieldMap(), onlineCtx);
			} else {

				rs = queryForRecordSet("DISINN00100.getOrdMgmtListByOrgId",
						requestData.getFieldMap(), onlineCtx);
			}
		}

		if (rs == null) {
			rs = new RecordSet("ds_ordMgmtNoList");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_ordMgmtNoList", rs);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 매입관리번호List취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getPrchsMgmtNoList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN00100.getPrchsMgmtNoList method start");
		}

		IRecordSet rs = null;
		Map mData = requestData.getFieldMap();
		String sPosAgencyId = (String) mData.get("posAgencyId");
		String sInPlcId = (String) mData.get("inPlcId");

		// 입고처가 있을 경우
		if (sInPlcId != null && !sInPlcId.equals("")) {

			rs = queryForRecordSet("DISINN00100.getPrchsOutMgmtListByInPlcId",
					requestData.getFieldMap(), onlineCtx);

		} else {

			// 대리점이 있을 경우
			if (sPosAgencyId != null && !sPosAgencyId.equals("")) {

				rs = queryForRecordSet(
						"DISINN00100.getPrchsOutMgmtListByPosAgencyId", requestData
								.getFieldMap(), onlineCtx);
			} else {

				rs = queryForRecordSet("DISINN00100.getPrchsOutMgmtListByOrgId",
						requestData.getFieldMap(), onlineCtx);
			}
		}

		if (rs == null) {
			rs = new RecordSet("ds_ordMgmtNoList");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_ordMgmtNoList", rs);

		return responseData;
	}

}