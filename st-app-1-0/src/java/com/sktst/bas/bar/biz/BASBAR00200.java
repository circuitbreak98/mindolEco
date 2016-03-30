/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.bar.biz;

import java.util.Map;

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
 * <li>업무 그룹명 : 프로젝트/SKTST/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-03-08 09:54:35</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class BASBAR00200 extends BaseBizUnit {

	/**
	 * 박스 정보 조회
	 *
	 * @author 전미량 (jeonmiryang)
	 * - 박스 바코드를 입력 받아 박스 정보를 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getBoxInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASBAR00200.getBoxInfo method start >>>");
		}

		IRecordSet rs = queryForRecordSet("BASBAR00200.getBoxInfo", requestData
				.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_boxInfo");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_boxInfo", rs);

		return responseData;
	}

	/**
	 * 박스 재고 등록
	 *
	 * @author 전미량 (jeonmiryang)
	 *  - 박스 재고를 등록한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveBoxLst(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveBoxLst method start");
		}
		IRecordSet rs = requestData.getRecordSet("ds_prodLst");
		IRecordSet rs_box = requestData.getRecordSet("ds_boxInfo");

		if (rs != null) {

			for (int i = 0; i < rs.getRecordCount(); i++) {
				Map query = rs.getRecordMap(i);
				
				if (!DELETE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					if("C".equals(query.get("S_GB"))){
						update("BASBAR00200.updateConsultBoxNo", query, onlineCtx);
					}else{
						update("BASBAR00200.updatePrchsBoxNo", query, onlineCtx);
					}
				}
			}
		}
		update("BASBAR00200.updateBoxInfo", rs_box.getRecordMap(0), onlineCtx);
		
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { "1" });
	}

	/**
	 * 상품 정보 조회
	 *
	 * @author 전미량 (jeonmiryang)
	 * - 바코드 입력 시 매입 Table에 상품 정보를 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASBAR00200.getProdInfo method start >>>");
		}
		
		Map query = requestData.getFieldMap();

		IRecordSet rs = queryForRecordSet("BASBAR00200.getProdInfo", query, onlineCtx);
		
		if (rs.getRecordCount() > 0) {
			Map prodMap = rs.getRecord(0);
			prodMap.put("PROC_GB", query.get("PROC_GB"));
			
			Map check_yn = (Map)queryForObject("BASBAR00200.getProdPrcCheck", prodMap, onlineCtx);
			rs.getRecord(0).set("CHECK_YN", check_yn.get("CHECK_YN").toString());
		} else {
			rs = new RecordSet("ds_prodInfo");
		}
/*		
		if (rs == null) {
			rs = new RecordSet("ds_prodInfo");
		}else{
			Map prodMap = rs.getRecord(0);
			prodMap.put("PROC_GB", query.get("PROC_GB"));
			
			Map check_yn = (Map)queryForObject("BASBAR00200.getProdPrcCheck", prodMap, onlineCtx);
			rs.getRecord(0).set("CHECK_YN", check_yn.get("CHECK_YN").toString());
		}
*/
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_prodInfo", rs);

		return responseData;
	}

}