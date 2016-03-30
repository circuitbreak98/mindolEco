package com.sktst.common.btw;

import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.integration.db.ISqlManager;
import java.util.Map;

public class btwAdjust implements IbtwAdjust {

	private ISqlManager sqlManager = (ISqlManager)ComponentRegistry.lookup("ps.db.ISqlManager");
	//private ISqlManager sqlManager = null;

    private   static String valueAllot   = "할부";
	private   static String valueAllotCD = "A";

    private   static String valueIPhoneNM01 = "IPHONE4_16G";
    private   static String valueIPhoneNM02 = "IPHONE4_32G";
    private   static String valueIPhoneNM03 = "IPHONE4_매장용";
    private   static String valueIPhoneNM04 = "IPHONE4_16GW";
    private   static String valueIPhoneNM05 = "IPHONE4_32GW";
    private   static String valueIPhoneCD01 = "CGW4";			// IPHONE4_16G
    private   static String valueIPhoneCD02 = "CGW5";			// IPHONE4_32G
    private   static String valueIPhoneCD03 = "CGW6";			// IPHONE4_매장용
    private   static String valueIPhoneCD04 = "CGW9"; 			// IPHONE4_16GW
    private   static String valueIPhoneCD05 = "CGWA";			// IPHONE4_32GW
    private   static String valueGalaxyNM01 = "SHW-M250SSO";
    private   static String valueGalaxyNM02 = "SHW-M250S";
    private   static String valueGalaxyCD01 = "SSPG";			// SHW-M250SSO
    private   static String valueGalaxyCD02 = "SSN7";			// SHW-M250S
    /**
	 * 
	 *
	 * @author 김연석 (Kim YeonSeok)
	 * 
	 * @param fPosAgency : 소속 대리점
	 * @param fEqpModel  : 판매 단말기
	 * @param fSettlCond : 할부/현금 구분
	 * @param fSaleDtm   : 단말기 판매일자
	 * @param fSaleAmt   : 할부/현금 판매가
	 * @param listAdjust : T-약정보조금 정보 RecordSet
	 *
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public  double getAdjustAmt(String fPosAgency, String fEqpModel, String fSettlCond, String fSaleDtm, double fSaleAmt,
			                    IRecordSet listAgency, IRecordSet listAdjust){

		String sPosAgency = "";	// 소속 대리점
		String sEqpModel  = "";	// 대상 단말기

		double sAgrmtAmt  = 0;	// 약정 보조금
		double sRangeAmt  = 0;	// 보정대상 범위
		double sSaleAmt   = 0;	// 절대값 (판매가)
		double sChulgoAmt = 0;	// 출고가
		double sSaleDtm   = 0;  // 판매일자
		double sSaleFrom  = 0;  // 적용일자 - From
		double sSaleTo    = 0;  // 적용일자 - To

		double sFromAmt   = 0;	// 매출 이상 금액
		double sToAmt     = 0;	// 매출 미만 금액
		double sAllotAdj  = 0;  // 할부매출 보정금액
		double sCashAdj   = 0;  // 현금매출 보정금액

		double cCheckAmt  = 0;	// 금액계산용 Temp

		// 판매취소의 경우 판매가가 (-) 금액이므로 판매가를 절대값으로 취환
		sSaleAmt   = Math.abs(fSaleAmt);

		// 일자의 범위비교를 위해 Double형으로 변환 후 작업 진행
		sSaleDtm   = (double) Double.parseDouble(fSaleDtm);

		for (int i = 0; i < listAgency.getRecordCount(); i++) {

			sPosAgency   = listAgency.getRecord(i).get("pos_agency") == null ? "" : listAgency.getRecord(i).get("pos_agency").toString();
			sEqpModel    = listAgency.getRecord(i).get("eqp_nm")     == null ? "" : listAgency.getRecord(i).get("eqp_nm")    .toString();
			sSaleFrom    = (double) Double.parseDouble(listAgency.getRecord(i).get("sale_from") .toString());
			sSaleTo      = (double) Double.parseDouble(listAgency.getRecord(i).get("sale_to")   .toString());

			if (fPosAgency.equals(sPosAgency) &&   // 대리점 비교
				fEqpModel.equals(sEqpModel)   &&   // 단말기 모델비교
				sSaleDtm >= sSaleFrom         &&   // 운영모델단가표 시작일자
				fSaleAmt <= sSaleTo           ) {  //             종료일자

				/* 2011.05.24 강성장M 요건
				 * 일반단말기   : 160,800
                 * iPhone    : 121,200
                 * S2        : 140,000
				sAgrmtAmt       = (double) Double.parseDouble(listAgency.getRecord(i).get("agrmt_amt").toString());
				 */
				sAgrmtAmt  = 160800;
				if (fEqpModel == valueIPhoneNM01 || fEqpModel == valueIPhoneNM02 || fEqpModel == valueIPhoneNM03 ||
					fEqpModel == valueIPhoneNM04 ||	fEqpModel == valueIPhoneNM05 ||
					fEqpModel == valueIPhoneCD01 || fEqpModel == valueIPhoneCD02 || fEqpModel == valueIPhoneCD03 ||
					fEqpModel == valueIPhoneCD04 ||	fEqpModel == valueIPhoneCD05) {
					sAgrmtAmt  = 121200;	
				}
				if (fEqpModel == valueGalaxyNM01 || fEqpModel == valueGalaxyNM02 ||
					fEqpModel == valueGalaxyCD01 || fEqpModel == valueGalaxyCD02) {
					sAgrmtAmt  = 140000;
				}
				sRangeAmt       = (double) Double.parseDouble(listAgency.getRecord(i).get("range_amt").toString());

				if (valueAllot.equals(fSettlCond) || valueAllotCD.equals(fSettlCond)) {
					sChulgoAmt  = (double) Double.parseDouble(listAgency.getRecord(i).get("allot_chulgo").toString());

					// [출고가 - 할부판매가 + 보정약정보조금] 로 계산된 금액이 보정대상 범위 금액 이상인 경우만 보정
					cCheckAmt   = sChulgoAmt - sSaleAmt + sAgrmtAmt;
					if (cCheckAmt > sRangeAmt) {

						for (int j = 0; j < listAdjust.getRecordCount(); j++) {
							sFromAmt  = (double) Double.parseDouble(listAdjust.getRecord(j).get("from_amt").toString());
							sToAmt    = (double) Double.parseDouble(listAdjust.getRecord(j).get("to_amt")  .toString());

							// 보조금기준 조정금액 Check
							if (cCheckAmt >= sFromAmt && cCheckAmt < sToAmt) {
								sAllotAdj = (double) Double.parseDouble(listAdjust.getRecord(j).get("allot_adj_prc").toString());
								if (fSaleAmt >= 0) {
									cCheckAmt = fSaleAmt + sAllotAdj;
								} else {
									cCheckAmt = fSaleAmt - sAllotAdj;
								}
								return cCheckAmt;
							}
						}
					}

				} else {
					sChulgoAmt  = (double) Double.parseDouble(listAgency.getRecord(i).get("cash_chulgo").toString());

					// [출고가 - 현금판매가] 로 계산된 금액이 보정대상 범위 금액 이상인 경우만 보정
					cCheckAmt   = sChulgoAmt - sSaleAmt;
					if (cCheckAmt > sRangeAmt) {

						for (int j = 0; j < listAdjust.getRecordCount(); j++) {
							sFromAmt  = (double) Double.parseDouble(listAdjust.getRecord(j).get("from_amt").toString());
							sToAmt    = (double) Double.parseDouble(listAdjust.getRecord(j).get("to_amt")  .toString());

							// 보조금기준 조정금액 Check
							if (cCheckAmt >= sFromAmt && cCheckAmt < sToAmt) {
								sCashAdj = (double) Double.parseDouble(listAdjust.getRecord(j).get("cash_adj_prc").toString());
								if (fSaleAmt >= 0) {
									cCheckAmt = fSaleAmt + sCashAdj;
								} else {
									cCheckAmt = fSaleAmt - sCashAdj;
								}
								return cCheckAmt;
							}
						}
					}
				}

				// 대리점 - 단말기 - 판매일자  조건은 만족하였으나  보정대상 조건에 걸리지 않은 경우 원 판매금액 Return
				return fSaleAmt;

			}
		}

		// 대리점 - 단말기 - 판매일자  조건에 걸리지 않은 경우 원 판매금액 Return
		return fSaleAmt;
	}

	/**
	 * 
	 *
	 * @author 김연석 (Kim YeonSeok)
	 *
	 * @param fApplyMonth : 적용년월 
	 * @param fPosAgency  : 소속 대리점
	 *
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	//public IRecordSet getAdjustDataset(String fApplyMonth, String fPosAgency) throws Exception {
	public IRecordSet getAgencyDataset(String fApplyMonth, String fPosAgency) {

		IRecordSet rsBTW = null;
		Map<String, Object> requestMap    = new HashMap<String, Object>();

		requestMap.put("pos_agency",  fPosAgency);
		requestMap.put("apply_month", fApplyMonth.substring(0, 6));

		IRecordSet rsEQP = sqlManager.queryForRecordSet("biz.btwAdjust.getBTWProduct", requestMap);
		if (rsEQP == null || rsEQP.getRecordCount() == 0) {
			rsBTW = sqlManager.queryForRecordSet("biz.btwAdjust.getBTWEquipList", requestMap);
		} else {
			rsBTW = sqlManager.queryForRecordSet("biz.btwAdjust.getBTWProdList", requestMap);
		}

		return rsBTW;
	}

	/**
	 * 
	 *
	 * @author 김연석 (Kim YeonSeok)
	 *
	 * @param fApplyMonth : 적용년월 
	 *
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	//public IRecordSet getAdjustDataset(String fApplyMonth, String fPosAgency) throws Exception {
	public IRecordSet getAdjustDataset(String fApplyMonth) {

		Map<String, Object> requestMap    = new HashMap<String, Object>();

		requestMap.put("apply_month", fApplyMonth.substring(0, 6));
		IRecordSet rsAdjust = sqlManager.queryForRecordSet("biz.btwAdjust.getBTWAdjust", requestMap);

		return rsAdjust;
	}
}
