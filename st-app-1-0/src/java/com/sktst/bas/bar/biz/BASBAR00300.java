/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.bar.biz;

import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import org.apache.commons.logging.Log;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-03-09 13:12:37</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class BASBAR00300 extends BaseBizUnit {

	/**
	 * 박스 재고 조회
	 *
	 * @author 전미량 (jeonmiryang)
	 * - 재고 Table 에서 박스 재고 리스트를 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getBoxProdLst(IDataSet requestData, IOnlineContext onlineCtx)
			throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		Map query = requestData.getFieldMap();
		//		질의를 수행한다.
		IRecordSet rs = queryForRecordSet("BASBAR00300.getBoxProdLst", query,
				onlineCtx);

		IRecordSet rs_box = queryForRecordSet("BASBAR00300.getBoxInfo", query,
				onlineCtx);
		
		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_boxLst", rs);

		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");

		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "ds_boxLst"); // 복호화 할 데이터셋 명
		rec.add("col_name7", "RES_NO"); // col_name7 :: 주민번호 앞 6자리 잘라줌....
		iSet.addRecord(rec);

		responseData.putRecordSet("cptItem", iSet);

		IDataSet rsData = bco.decode(responseData, onlineCtx);
		
		responseData.putRecordSet("ds_boxInfo", rs_box);
		
		return rsData;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * -박스재고 삭제
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteBoxProd(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASBAR00300.deleteBoxProd method start");
		}
		IRecordSet rs_box = requestData.getRecordSet("ds_boxInfo");
		IRecordSet rs = requestData.getRecordSet("ds_boxLst");
		Map mSave = null;
		int prod_cnt = 0;
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				mSave = rs.getRecordMap(i);
				if (mSave.get("CHK").equals("1")) {
					
//					if(mSave.get("S_GB").equals("C")){
						update("BASBAR00300.deleteConsultBoxProd", mSave, onlineCtx);
//						prod_cnt ++;
//					}else if(mSave.get("S_GB").equals("P")){
						update("BASBAR00300.deletePrchsBoxProd", mSave, onlineCtx);
						prod_cnt ++;
//					}
				}
			}
		}
		update("BASBAR00300.updateBoxInfo", rs_box.getRecordMap(0), onlineCtx);	
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "1" });
	}

}