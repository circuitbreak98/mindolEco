/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.sal.sco.biz;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.Record;
import nexcore.framework.core.data.RecordHeader;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-10-07 09:08:20</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class SALSCO00100 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * - 매출액 취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getSalesAmt(IDataSet requestData, IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);

		Map query = requestData.getFieldMap();
		int disAmt = 0; //재고금액
		String seq; //금액 차등구분
		String amt_cl; //비율구분
		int mar_amt = 0; //마진금액
		int sale_cmms_amt = 0; //수수료 금액

		IRecordSet rs_splstPreferAmt = null;
		IRecord ncRecord = new Record();
		IRecordSet rs_tmp = null;

		rs_splstPreferAmt = queryForRecordSet("SALSCO00100.getSplstAmt", query);
		log.debug("rs_splstPreferAmt ■■■■■■ " + rs_splstPreferAmt);

		if (rs_splstPreferAmt.getRecordCount() == 0) {
			/**
			 * 계산하여 재고금액 취득
			 */
			rs_splstPreferAmt = new RecordSet("List");
			rs_splstPreferAmt.addHeader(new RecordHeader("SALE_PRC",
					java.sql.Types.VARCHAR));
			rs_splstPreferAmt.addHeader(new RecordHeader("SALE_CMMS",
					java.sql.Types.VARCHAR));
			rs_splstPreferAmt.addHeader(new RecordHeader("MAR",
					java.sql.Types.VARCHAR));
			rs_splstPreferAmt.addHeader(new RecordHeader("SPLST_YN",
					java.sql.Types.VARCHAR));

			/**
			 * 1. 재고금액 취득
			 */
			Map rs_disAmt = (Map) queryForObject("SALSCO00100.getDisAmt",
					query, onlineCtx);
			if (rs_disAmt != null) {
				String disAmt_str = rs_disAmt.get("DIS_AMT").toString();
				disAmt = Integer.parseInt(disAmt_str);
			}
			log.debug("rs_disAmt 재고금액 ■■■■■■ " + disAmt);
			query.put("dis_amt", disAmt);

			/**
			 * 2. 재고금액으로 추가금액 비율관리 조회
			 */
			//Map rs_amtRate = (Map)queryForObject("SALSCO00100.getAmtRate", query, onlineCtx);
			//log.debug("rs_amtRate추가금액 비율관리 ■■■■■■ " + rs_amtRate);
			//if(rs_amtRate != null){
			/**
			 * 3.추가금액 비율관리 조회 데이터가 존재 할 때 SEQ로 선호도별 판매 가격표 조회
			 */

			//seq = rs_amtRate.get("SEQ").toString() ;
			//query.put("seq", seq);
			rs_tmp = queryForRecordSet("SALSCO00100.getSplstPreferAmt", query,
					onlineCtx);
			log.debug("rs_splstPreferAmt ■■■■■■ " + rs_tmp);

			if (rs_tmp.getRecordCount() == 0) {
				/**
				 * 선호도별 판매 가격표 데이터 없을 시 
				 */
				log.debug("선호도별 판매 가격표 데이터 없음  ■■■■■■ ");
				ncRecord.add(Integer.toString(disAmt));
				ncRecord.add("0");
				ncRecord.add("0");
				ncRecord.add("N");
			} else {
				/**
				 * 4.마진 금액 계산
				 */
				String mar_cl = rs_tmp.get(0, "MAR_CL"); // 마진구분
				if (mar_cl.equals("2")) {
					int mar_fix_lt = Integer.parseInt(rs_tmp.get(0,
							"MAR_FIX_RT")); // 마진율
					mar_amt = (disAmt * mar_fix_lt) / 100;
				} else {
					mar_amt = Integer.parseInt(rs_tmp.get(0, "MAR_FIX_AMT"));
				}
				log.debug("mar_amt마진 금액 ■■■■■■ " + mar_amt);

				/**
				 * 5.수수료 금액 계산
				 */

				String sale_cmms_cl = rs_tmp.get(0, "SALE_CMMS_CL"); // 마진구분
				if (sale_cmms_cl.equals("2")) {
					int sale_cmms_fix_lt = Integer.parseInt(rs_tmp.get(0,
							"SALE_CMMS_FIX_RT")); // 마진율
					sale_cmms_amt = (disAmt * sale_cmms_fix_lt) / 100;
				} else {
					sale_cmms_amt = Integer.parseInt(rs_tmp.get(0,
							"SALE_CMMS_FIX_AMT"));
				}
				log.debug("sale_cmms_amt수수료 금액 ■■■■■■ " + sale_cmms_amt);

				ncRecord.add(Integer.toString(disAmt));
				ncRecord.add(Integer.toString(sale_cmms_amt));
				ncRecord.add(Integer.toString(mar_amt));
				ncRecord.add("N");
			}
			rs_splstPreferAmt.addRecord(ncRecord);

			//}else{
			/**
			 * 추가금액 비율관리 조회 데이터 없을 시
			 */
			//	log.debug("추가금액 비율관리 데이터 없음  ■■■■■■ ");
			//	ncRecord.add(Integer.toString(disAmt) );
			//	ncRecord.add("0");
			//	ncRecord.add("0");		
			//	ncRecord.add("N");		
			//	rs_splstPreferAmt.addRecord(ncRecord);	
			//}
		}
		log.debug("rs_splstPreferAmt----" + rs_splstPreferAmt);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs_splstPreferAmt.getRecordCount()) });
		responseData.putRecordSet("List", rs_splstPreferAmt);
		return responseData;
	}

}