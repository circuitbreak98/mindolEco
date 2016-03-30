/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.sal.bcs.biz;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseConstants;
import com.sktst.common.user.PsLoginUserInfo;
import com.sktst.sal.sco.ejb.SCO;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-10-11 13:57:03</li>
 * </ul>
 *
 * @author 안희수 (ahnheesoo)
 */
public class SALBCS00200 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 안희수 (ahnheesoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveProdSale(IDataSet requestData, IOnlineContext onlineCtx)
	throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("saveProdSale method start");
		}

		Map mMov = new HashMap();
		Map mAll = new HashMap();
		
		/*
		 * 암호화 시작
		 * 암호화 시작 -> 암호화 종료 소스 모두 복사 
		 * 변경 : 암호화 할 데이터셋 명/암호화 컬럼1/암호화 컬럼2
		 * 암호화 컬럼은 최대 6개 까지 확장 가능 : col_name1 ~ 6
		 */
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");
		
		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);		
		IRecordSet iSet = itemp.getRecordSet("cptItem");
		
		IRecord rec = iSet.newRecord();
		rec.add("ds_name"	, "ds_custInfo");	// 암호화 할 데이터셋 명
		rec.add("col_name3"	, "CUST_BIZ_NUM"); 	// 암호화 컬럼1
		iSet.addRecord(rec);
		
		requestData.putRecordSet("cptItem", iSet);

		IDataSet rsData = bco.encode(requestData, onlineCtx);
		
		requestData = rsData;
		/*
		 * 암호화 종료 
		 */
		
		
		
		SCO sco = (SCO) lookupBizComponent("sktst.sal.SCO");	// 재고처리 
		
		int insCnt = 0;
		int uptCnt = 0;
		int delCnt = 0;
		String sSaveGubun = "";
		String sCash = "";
		String sGnrlSaleNo = "";
		
		//		거래처정보및 기본정보 
		IRecordSet rsGeneral = requestData.getRecordSet("ds_general");
		//		 고객정보
		IRecordSet rsCustInfo = requestData.getRecordSet("ds_custInfo");
		//		 단말기정보
		IRecordSet rsEqpInfo = requestData.getRecordSet("ds_eqpInfo");
		//		 수납정보
		IRecordSet rsPayInfo = requestData.getRecordSet("ds_payInfo");

		mAll = getProMap(rsGeneral.getRecordMap(0), onlineCtx);
		mMov.putAll(mAll);
		mAll = getProMap(rsCustInfo.getRecordMap(0), onlineCtx);
		mMov.putAll(mAll);
		mAll = getProMap(rsEqpInfo.getRecordMap(0), onlineCtx);
		mMov.putAll(mAll);
		mAll = getProMap(rsPayInfo.getRecordMap(0), onlineCtx);
		mMov.putAll(mAll);
log.debug("***************************ollllllllllllllllllllll xxx : "+ mMov);
		
		sSaveGubun = mMov.get("save_gubun").toString(); // I : 신규등록 / C : 판매취소 / U : 수정 	
		mMov.put("save_gubun", sSaveGubun); 			// 
		
		sCash = mMov.get("cash_pay_amt").toString(); 	// 현금매출여부 
		
//		 신규등록건 일반판매번호 채번 
		if ("I".equals(sSaveGubun)) {
			// 일련번호 채번 
			mMov.put("sale_gubun", "S"); 				// 일반판매번호 구분자 
			Map saleNo = (Map) queryForObject("SALBCS00200.getGnrlSaleNo",
					mMov, onlineCtx);
			sGnrlSaleNo = saleNo.get("GNRL_SALE_NO").toString();
			mMov.put("gnrl_sale_no", sGnrlSaleNo); 		// 일반판매번호
		}
		
		// INSERT할 기본값 추출  ( )
		IRecordSet rsInsertDefaultData = queryForRecordSet("SALBCS00200.getInsertDefaultData",
				mMov, onlineCtx);
		mMov.putAll(getProMap(rsInsertDefaultData.getRecordMap(0), onlineCtx));
		
		
		// 정산처 추출 
		mMov.put("deal_dtm",  mMov.get("sale_dtm").toString().substring(0,8)); 
		mMov.put("deal_co_cd",  mMov.get("sale_plc")); 
		IRecordSet rsGetStlPlc = queryForRecordSet("SALBCS00200.getDealStlPlcByDealCoCd",
				mMov, onlineCtx);
		mMov.put("stl_plc",rsGetStlPlc.get(0, "stl_plc"));		
		
		
log.debug("***************************mMov xxx : "+ mMov);	
		
//		 신규등록
		if ("I".equals(sSaveGubun)) {
			mMov.put("sale_chg_hst_cl", "01");	/* 판매변경이력구분 (ZSAL_C_00110 : 01:판매 , 08:판매취소) */
			mMov.put("sale_dtm", rsGeneral.get(0, "sale_dtm")+"00");
		}else {
			
			// 판매취소일경우 화면상의 현재판매금액은 0으로 셋팅함. 
			if ("C".equals(sSaveGubun)) {
				mMov.put("cash_pay_amt", 0);
				mMov.put("crdtcrd_pay_amt1", 0);
				mMov.put("crdtcrd_pay_amt2", 0);
				mMov.put("dis_amt", 0);
				mMov.put("mar_amt", 0);
				mMov.put("cmms_amt", 0);
				mMov.put("vat_amt", 0);
				mMov.put("sale_amt", 0);
			}			
			// INSERT할 값을 추출  ( )
			IRecordSet rsInsertData = queryForRecordSet("SALBCS00200.getInsertData",
					mMov, onlineCtx);
			mAll = getProMap(rsInsertData.getRecordMap(0), onlineCtx);
			mMov.putAll(mAll);
		}
	
		//				일반판매 테이블 등록 
		insert("SALBCS00200.InsertTsalGnrlSale", mMov, onlineCtx);

		//				단말기판매 테이블 등록 
		insert("SALBCS00200.InsertTsalEquipmentSale", mMov, onlineCtx);

		//				수납정보 테이블 등록 
		insert("SALBCS00200.InsertTsalPayment", mMov, onlineCtx);

//						매출판매 테이블 등록 
		insert("SALBCS00200.InsertTsalSaleAmt", mMov, onlineCtx);

		
		insCnt++;
		
//		 상품판매 재고처리
		Map mDis = new HashMap();
		IDataSet oDsDisSale = new DataSet();

		/*
		 * mMaster 필드구성
		 * 	  #out_mgmt_no#		: 공통생성
			  #gnrl_sale_no#		: requestData.getField("gnrl_sale_no")
			  #gnrl_sale_chg_seq#	: requestData.getField("gnrl_sale_chg_seq")
			  #sale_dt#				: requestData.getField("sale_dtm")
			  #sale_plc#			: requestData.getField("sale_plc")
			  #rmks#				: requestData.getField("rmks")
		 */
		mDis.put("out_mgmt_no", mMov.get("out_mgmt_no"));
		mDis.put("out_seq", mMov.get("out_seq"));
		mDis.put("gnrl_sale_no", mMov.get("gnrl_sale_no"));
		mDis.put("gnrl_sale_chg_seq", mMov.get("gnrl_sale_chg_seq"));
		mDis.put("sale_dtm", mMov.get("sale_dtm"));
		mDis.put("sale_plc", mMov.get("sale_plc"));
		mDis.put("rmks", mMov.get("rmks"));
		oDsDisSale.putFieldObjectMap(mDis);
		/*
		 * mDisSale 필드구성
			#prod_cd#			: requestData.getRecordSet("itemList")
			#color_cd#			: requestData.getRecordSet("itemList")
			#ser_num#			: requestData.getRecordSet("itemList")
			#eqp_st#			: requestData.getRecordSet("itemList")
			#sale_unit_prc#		: requestData.getRecordSet("itemList")
			#sale_qty#			: requestData.getRecordSet("itemList")
			#sale_amt#			: requestData.getRecordSet("itemList")
			#dis_st#
			#sale_qty#
		    #settl_cond_cd#
		    #sale_unit_prc#
		    #sale_amt#
		 */

		 oDsDisSale.putRecordSet("itemList", requestData.getRecordSet("ds_eqpInfo"));
		 
log.debug("mDis 2 : "+ mDis);		
log.debug("itemList 2 : "+ oDsDisSale);
		
		try {
			if ("C".equalsIgnoreCase(sSaveGubun)) {
				
				oDsDisSale = sco.addDisSaleIn(oDsDisSale, onlineCtx);
			}else if ("U".equalsIgnoreCase(sSaveGubun)) {
				
				oDsDisSale = sco.updateDisSaleOut(oDsDisSale, onlineCtx);
			}else if ("I".equalsIgnoreCase(sSaveGubun)) {
				
				oDsDisSale = sco.addDisSaleOut(oDsDisSale, onlineCtx);
			}
			
		} catch (RemoteException rEx) {
			if (log.isDebugEnabled()) {
				log.debug("addDisSaleOut insert RemoteException rEx: "
						+ rEx.getMessage());
			}
			throw new BizRuntimeException(
					rEx
							.getCause()
							.toString()
							.replace(
									"nexcore.framework.core.exception.BizRuntimeException:",
									""));

		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
				log.debug("addDisSaleOut insert Exception ex: " + ex.getMessage());
			}
			throw ex;
		}
		

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"Insert:" + insCnt });

		return result;
	}

	/**
	 * 
	 *
	 * @author 안희수 (ahnheesoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveProdSaleCncl(IDataSet requestData,
			IOnlineContext onlineCtx) {

		IDataSet result = new DataSet();
		// TODO 업무 로직 작성 필요

		return result;
	}

	/**
	 * 
	 *
	 * @author 안희수 (ahnheesoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdDisDtl(IDataSet requestData, IOnlineContext onlineCtx) {

		IDataSet result = new DataSet();
		// TODO 업무 로직 작성 필요

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

		PsLoginUserInfo userInfo = (PsLoginUserInfo) onlineCtx.getUserInfo();

		// 레코드별 입력값 추출 및 셋팅
		for (Iterator iter = orgMap.keySet().iterator(); iter.hasNext();) {
			key = iter.next().toString().toLowerCase();
			value = "";

			tmpStr = orgMap.get(key);
			value = tmpStr == null ? "" : tmpStr.toString();

			retMap.put(key, value);
		}

		// 공통값 셋팅
		retMap.put("errcode", "");
		retMap.put("errmsg", "");
		retMap.put("user_id", userInfo.getLoginId());

		return retMap;
	}

	/**
	 * 
	 *
	 * @author 안희수 (ahnheesoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getTdisProdCpnt(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdCpnt method start.");
		}

		IRecordSet rs = queryForRecordSet("SALBCS00200.getTdisProdCpnt",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_prodCpnt");
		}
		log.debug("rs.getRecordCount() == " + rs.getRecordCount());
		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("ds_prodCpnt", rs);

		return result;
	}

}