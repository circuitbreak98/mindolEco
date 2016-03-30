/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.btw.biz;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-03-15 13:14:34</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class ADMBTW00200 extends BaseBizUnit {

	private static String valueAllot = "할부";
	
	private static String valueAgent       = "02";
	private static String valueOnlineAgent = "04";
	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getGeneralAdjust(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getGeneralAdjust method start");
		}

		IRecordSet rsList = queryForRecordSet("ADMBTW00200.getGeneralAdjust", requestData.getFieldMap());

		if (rsList == null) {
			rsList = new RecordSet("ds_General");
		}

		IRecord iRecList  = null;
		IRecord iRecBTW   = null;
		IRecordSet rsBTW  = null;

		String sSettlCond = "";		// 단말기결재구분

		   int sListCnt   =  0;		// List Count

		// rsList
		String sSlCl      = "";		// 영업구분
		double sBfAmt     =  0;		// 적용 전 할부지원보조금
		double sAfAmt     =  0;		// 적용 후 할부지원보조금
		double sNewAmt    =  0; 	// 적용 후 금액
		double sAdjAmt    =  0;		// 적용금액

		// rsBTW
		double sBfCnt       =  0;		// 적용 전 건수
		double sBfAllotCnt  =  0;		// 적용 전 건수
		double sBfCashCnt   =  0;		// 적용 전 건수
        double sAfCnt       =  0;		// 적용 후 건수
        double sAfAllotCnt  =  0;		// 적용 후 건수
        double sAfCashCnt   =  0;		// 적용 후 건수
		double sFromAmt     =  0;		// 적용구간 - 이상
		double sToAmt       =  0;		// 적용구간 - 미만

		rsBTW = queryForRecordSet("ADMBTW00200.getBTWAdjust", requestData.getFieldMap());

		sListCnt = rsList.getRecordCount();
		for (int i = 0; i < sListCnt; i++) {
			iRecList     = rsList.getRecord(i);

			sSettlCond   = iRecList.get("settl_cond") == null ? "" : iRecList.get("settl_cond").toString();
			sSlCl        = iRecList.get("sl_cl_cd")   == null ? "" : iRecList.get("sl_cl_cd").toString();
			sBfAmt       = Math.abs((double) Double.parseDouble(iRecList.get("bf_agrmt_amt").toString()));
			sAdjAmt      =          (double) Double.parseDouble(iRecList.get("adj_amt").toString());

			if(valueAllot.equals(sSettlCond)) {
				sNewAmt  = (double) Double.parseDouble(iRecList.get("new_allot_sale").toString());
				if (sNewAmt >= 0) {
					sAfAmt   = (double) Double.parseDouble(iRecList.get("eqp_chulgo")    .toString())
					         - (double) Double.parseDouble(iRecList.get("new_allot_sale").toString())
					         + (double) Double.parseDouble(iRecList.get("agrmt_amt")     .toString());
					iRecList.put("af_agrmt_amt",  Double.toString(sAfAmt));
				} else {
					sAfAmt   = Math.abs((double) Double.parseDouble(iRecList.get("new_allot_sale").toString()) * (-1)
			                          - (double) Double.parseDouble(iRecList.get("eqp_chulgo")    .toString())
                                      - (double) Double.parseDouble(iRecList.get("agrmt_amt")     .toString()));
					iRecList.put("af_agrmt_amt",  Double.toString(sAfAmt  * -1));
					iRecList.put("adj_amt",       Double.toString(sAdjAmt * -1));
				}
			} else {
				// 도매, 온라인 도매의 경우 현금판매일때 적용보정금 Zero 처리
				if (valueAgent.equals(sSlCl) || valueOnlineAgent.equals(sSlCl)) {
					sAfAmt = 0;
					iRecList.put("af_agrmt_amt",  "0");
				} else {

					sNewAmt  = (double) Double.parseDouble(iRecList.get("new_cash_sale").toString());
					if (sNewAmt >= 0) {
						sAfAmt   = (double) Double.parseDouble(iRecList.get("eqp_chulgo")    .toString())
						         - (double) Double.parseDouble(iRecList.get("new_cash_sale") .toString());
						iRecList.put("af_agrmt_amt",  Double.toString(sAfAmt));
					} else {
						sAfAmt   = Math.abs((double) Double.parseDouble(iRecList.get("eqp_chulgo")    .toString())
						                  + (double) Double.parseDouble(iRecList.get("new_cash_sale") .toString()));
						iRecList.put("af_agrmt_amt",  Double.toString(sAfAmt  * -1));
						iRecList.put("adj_amt",       Double.toString(sAdjAmt * -1));
					}
				}
			}

			for (int j = 0; j < rsBTW.getRecordCount(); j++) {
				iRecBTW  = rsBTW.getRecord(j);

				sFromAmt    = (double) Double.parseDouble(iRecBTW.get("from_amt")    .toString());
				sToAmt      = (double) Double.parseDouble(iRecBTW.get("to_amt")      .toString());
				sBfCnt      = (double) Double.parseDouble(iRecBTW.get("bf_cnt")      .toString());
				sBfCashCnt  = (double) Double.parseDouble(iRecBTW.get("bf_cash_cnt") .toString());
				sBfAllotCnt = (double) Double.parseDouble(iRecBTW.get("bf_allot_cnt").toString());
				sAfCnt      = (double) Double.parseDouble(iRecBTW.get("af_cnt")      .toString());
				sAfCashCnt  = (double) Double.parseDouble(iRecBTW.get("af_cash_cnt") .toString());
				sAfAllotCnt = (double) Double.parseDouble(iRecBTW.get("af_allot_cnt").toString());

				if (sBfAmt >= sFromAmt && sBfAmt <  sToAmt	) {

					if(valueAllot.equals(sSettlCond)) {
						// 적용 전 건수
						sBfCnt  = sBfCnt + 1;
						iRecBTW.put("bf_cnt",  Double.toString(sBfCnt));

						sBfAllotCnt  = sBfAllotCnt + 1;
						iRecBTW.put("bf_allot_cnt",  Double.toString(sBfAllotCnt));
					}else{
						// 현금매출의 경우 소매, M채널, 온라인소매만 계산
						if (valueAgent.equals(sSlCl) || valueOnlineAgent.equals(sSlCl)) {
						} else {

							sBfCnt  = sBfCnt + 1;
							iRecBTW.put("bf_cnt",  Double.toString(sBfCnt));

							sBfCashCnt   = sBfCashCnt + 1;
							iRecBTW.put("bf_cash_cnt",  Double.toString(sBfCashCnt));	
						}
					}
				}

				if (sAfAmt >= sFromAmt && sAfAmt <  sToAmt	) {

					if(valueAllot.equals(sSettlCond)) {

						// 적용 후 건수
						sAfCnt  = sAfCnt + 1;
						iRecBTW.put("af_cnt",  Double.toString(sAfCnt));

						sAfAllotCnt  = sAfAllotCnt + 1;
						iRecBTW.put("af_allot_cnt",  Double.toString(sAfAllotCnt));
					}else {
						// 현금매출의 경우 소매, M채널, 온라인소매만 계산
						if (valueAgent.equals(sSlCl) || valueOnlineAgent.equals(sSlCl)) {
						} else {
							sAfCnt  = sAfCnt + 1;
							iRecBTW.put("af_cnt",  Double.toString(sAfCnt));

							sAfCashCnt  = sAfCashCnt + 1;
							iRecBTW.put("af_cash_cnt",  Double.toString(sAfCashCnt));
						}

					}
				}
			}
		}

		// Adjust 구역별 Count 계산 후 비율 계산
		// 1. 전용 전/후, 할부/현금, 전체 건수 누적
		double cBfCnt       = 0;
		double cBfCashCnt   = 0;
		double cBfAllotCnt  = 0;
		double cAfCnt       = 0;
		double cAfCashCnt   = 0;
		double cAfAllotCnt  = 0;
		double sBfRate      =  0;		// 적용 전 비율
		double sBfAllotRate =  0;		// 적용 전 비율
		double sBfCashRate  =  0;		// 적용 전 비율
        double sAfRate      =  0;		// 적용 후 비율
        double sAfAllotRate =  0;		// 적용 후 비율
        double sAfCashRate  =  0;		// 적용 후 비율
		for (int j = 0; j < rsBTW.getRecordCount(); j++) {
			iRecBTW  = rsBTW.getRecord(j);

			sBfCnt      = (double) Double.parseDouble(iRecBTW.get("bf_cnt")      .toString());
			sBfCashCnt  = (double) Double.parseDouble(iRecBTW.get("bf_cash_cnt") .toString());
			sBfAllotCnt = (double) Double.parseDouble(iRecBTW.get("bf_allot_cnt").toString());
			sAfCnt      = (double) Double.parseDouble(iRecBTW.get("af_cnt")      .toString());
			sAfCashCnt  = (double) Double.parseDouble(iRecBTW.get("af_cash_cnt") .toString());
			sAfAllotCnt = (double) Double.parseDouble(iRecBTW.get("af_allot_cnt").toString());

			cBfCnt      = cBfCnt      + sBfCnt;
			cBfCashCnt  = cBfCashCnt  + sBfCashCnt;
			cBfAllotCnt = cBfAllotCnt + sBfAllotCnt;
			cAfCnt      = cAfCnt      + sAfCnt;
			cAfCashCnt  = cAfCashCnt  + sAfCashCnt;
			cAfAllotCnt = cAfAllotCnt + sAfAllotCnt;
		}
		//2. 각각의 비율 계산
		for (int j = 0; j < rsBTW.getRecordCount(); j++) {
			iRecBTW  = rsBTW.getRecord(j);

			sBfCnt      = (double) Double.parseDouble(iRecBTW.get("bf_cnt")      .toString());
			sBfCashCnt  = (double) Double.parseDouble(iRecBTW.get("bf_cash_cnt") .toString());
			sBfAllotCnt = (double) Double.parseDouble(iRecBTW.get("bf_allot_cnt").toString());
			sAfCnt      = (double) Double.parseDouble(iRecBTW.get("af_cnt")      .toString());
			sAfCashCnt  = (double) Double.parseDouble(iRecBTW.get("af_cash_cnt") .toString());
			sAfAllotCnt = (double) Double.parseDouble(iRecBTW.get("af_allot_cnt").toString());

			sBfRate      = sBfCnt      * 100 / cBfCnt;
			sBfCashRate  = sBfCashCnt  * 100 / cBfCashCnt;
			sBfAllotRate = sBfAllotCnt * 100 / cBfAllotCnt;
			sAfRate      = sAfCnt      * 100 / cAfCnt;
			sAfCashRate  = sAfCashCnt  * 100 / cAfCashCnt;
			sAfAllotRate = sAfAllotCnt * 100 / cAfAllotCnt;

			iRecBTW.put("bf_rate",       Double.toString(sBfRate));
			iRecBTW.put("bf_allot_rate", Double.toString(sBfAllotRate));
			iRecBTW.put("bf_cash_rate",  Double.toString(sBfCashRate));
			iRecBTW.put("af_rate",       Double.toString(sAfRate));
			iRecBTW.put("af_allot_rate", Double.toString(sAfAllotRate));
			iRecBTW.put("af_cash_rate",  Double.toString(sAfCashRate));
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String.valueOf(rsList.getRecordCount()) });

		responseData.putRecordSet("ds_General", rsList);
		responseData.putRecordSet("ds_Adjust",  rsBTW);
		return responseData;
	}

}