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
import java.util.List;
import java.util.Map;

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
import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.core.log.LogManager;

import org.apache.commons.logging.Log;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-10-17 17:56:36</li>
 * </ul>
 *
 * @author 전희경 (jeonheekyung)
 */
public class SALSCO00300 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전희경 (jeonheekyung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addDisSaleIn(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("addDisSaleIn method start");
		}

		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		/*
		 * mMaster 필드구성
		 #in_mgmt_no#		: 공통생성
		 #in_cl#			: 공통정의
		 #sale_dt#			: requestData.getField("sale_dtm")
		 #sale_plc#		: requestData.getField("sale_plc")
		 #in_st#			: 공통정의
		 #rmks#			: requestData.getField("rmks")
		 */
		Map mMaster = new HashMap();
		/*
		 * mDisSale 필드구성
		 #in_mgmt_no#		: 공통생성
		 #out_mgmt_no#		: requestData.getField("out_mgmt_no")
		 #prod_cd#			: requestData.getRecordSet("itemList")
		 #color_cd#			: requestData.getRecordSet("itemList")
		 #ser_num#			: requestData.getRecordSet("itemList")
		 #dis_st#			: 공통정의
		 #inout_cl#			: 공통정의
		 #inout_dtl_cl#		: 공통정의
		 #sale_qty#			: requestData.getRecordSet("itemList")
		 #sale_amt#			: requestData.getRecordSet("itemList")
		 #sale_dt#			: requestData.getField("sale_dtm")
		 #sale_plc#			: requestData.getField("sale_plc")
		 #sale_yn#			: 공통정의
		 */
		Map mDisSale = null;

		String sMgmtNo = "";
		String sSaleDt = "";
		String sInSt = "03"; // 입고완료
		String sInCl = "105"; // 판매입고
		String sDisSt = "01"; // 가용
		String sInOutCl = "100"; // 입고
		String sInOutDtlCl = "105"; // 판매입고
		String sSaleYn = "N";

		//출고마스터 :판매변경순번 업데이트
		this.updateDisSaleOut(requestData, onlineCtx);

		// 입고관리번호 생성
		sMgmtNo = this.getMgmtNo("IN", iUserInfo.getLoginId(), onlineCtx);
		//log.debug("$$$ sale_dtm = "+requestData.getField("sale_dtm"));		
		sSaleDt = requestData.getField("sale_dtm").substring(0, 8);

		//IRecordSet rsMaster = requestData.getRecordSet("master");
		mMaster.put("in_mgmt_no", sMgmtNo);
		mMaster.put("in_cl", sInCl);
		mMaster.put("sale_dt", sSaleDt);
		mMaster.put("in_st", sInSt);

		mMaster.put("sale_plc", requestData.getField("sale_plc"));
		mMaster.put("rmks", requestData.getField("rmks"));

		insert("SALSCO00300.addProdIn", mMaster, onlineCtx);
		//log.debug("$$$$$$ = " + sMgmtNo + "  @@@@@@@@@@@@@@@ ==> " + mMaster);

		IRecordSet rsSaleList = requestData.getRecordSet("itemList");

		if (rsSaleList != null) {
			for (int i = 0; i < rsSaleList.getRecordCount(); i++) {
				mDisSale = rsSaleList.getRecordMap(i);

				mDisSale.put("in_mgmt_no", sMgmtNo);
				mDisSale
						.put("out_mgmt_no", requestData.getField("out_mgmt_no"));
				mDisSale.put("dis_st", sDisSt);
				mDisSale.put("inout_cl", sInOutCl);
				mDisSale.put("inout_dtl_cl", sInOutDtlCl);
				mDisSale.put("sale_yn", sSaleYn);
				mDisSale.put("sale_dt", sSaleDt);
				mDisSale.put("sale_plc", requestData.getField("sale_plc"));
				//log.debug("@@^^^^^^^^^ " + mDisSale);
				// 상품판매취소입고상세 추가
				insert("SALSCO00300.addProdInDtl", mDisSale, onlineCtx);

				// 상품입출고상세 추가
				insert("SALSCO00300.addProdInHst", mDisSale, onlineCtx);

				// 상품재고 판매취소입고 처리
				update("SALSCO00300.updateProdDis", mDisSale, onlineCtx);
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		responseData.putFieldObjectMap(mMaster);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전희경 (jeonheekyung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addDisSaleOut(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("addDisSaleOut method start");
		}

		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		/*
		 * mMaster 필드구성
		 #out_mgmt_no#			: 공통생성
		 #gnrl_sale_no#		: requestData.getField("gnrl_sale_no")
		 #gnrl_sale_chg_seq#	: requestData.getField("gnrl_sale_chg_seq")
		 #out_cl#				: 공통정의
		 #sale_dt#				: requestData.getField("sale_dtm")
		 #sale_plc#			: requestData.getField("sale_plc")
		 #rmks#				: requestData.getField("rmks")
		 */
		Map mMaster = new HashMap();
		/*
		 * mDisSale 필드구성
		 #out_mgmt_no#		: 공통생성
		 #prod_cd#			: requestData.getRecordSet("itemList")
		 #color_cd#			: requestData.getRecordSet("itemList")
		 #ser_num#			: requestData.getRecordSet("itemList")
		 #inout_cl#			: 공통정의
		 #inout_dtl_cl#		: 공통정의
		 #eqp_st#			: requestData.getRecordSet("itemList")
		 #dis_st#			: 공통정의
		 #settl_cond_cd#		: 공통정의
		 #sale_unit_prc#		: requestData.getRecordSet("itemList")
		 #sale_qty#			: requestData.getRecordSet("itemList")
		 #sale_amt#			: requestData.getRecordSet("itemList")
		 #sale_dt#			: requestData.getField("sale_dtm")
		 #sale_plc#			: requestData.getField("sale_plc")
		 #sale_yn#			: 공통정의
		 */
		Map mDisSale = null;

		String sMgmtNo = "";
		String sSaleDt = "";
		String sGnrlSaleNo = "";
		int nGnrlSaleChgSeq = 0;
		String sSalePlc = "";
		String sRmks = "";
		String sOutCl = "205"; // 판매출고
		String sDisSt = "02"; // 비가용
		String sInOutCl = "200"; // 출고
		String sInOutDtlCl = "205"; // 판매출고
		String sSettlCondCd = "C";
		String sSaleYn = "Y";
		String sTrmsSt = "A";

		IRecordSet rsSaleList = requestData.getRecordSet("itemList");

		log.debug("rsSaleList)  addDisSaleOut -------------------\n");
		log.debug(rsSaleList);
		log.debug("-------------------\n");

		sSaleDt = requestData.getField("sale_dtm").substring(0, 8);
		sGnrlSaleNo = requestData.getField("gnrl_sale_no").toString();
		nGnrlSaleChgSeq = getIntMem(requestData.getField("gnrl_sale_chg_seq"));
		sSalePlc = requestData.getField("sale_plc").toString();
		String sale_typ_cd = requestData.getField("sale_typ_cd").toString();
		
		if (requestData.getField("rmks") != null) {
			sRmks = requestData.getField("rmks").toString();
		}

		if (rsSaleList != null) {
			for (int i = 0; i < rsSaleList.getRecordCount(); i++) {
				mDisSale = rsSaleList.getRecordMap(i);

				// 출고관리번호 생성
				sMgmtNo = this.getMgmtNo("OT", iUserInfo.getLoginId(),
						onlineCtx);

				mMaster.put("out_mgmt_no", sMgmtNo);
				mMaster.put("gnrl_sale_no", sGnrlSaleNo);
				mMaster.put("gnrl_sale_chg_seq", nGnrlSaleChgSeq);
				mMaster.put("sale_plc", sSalePlc);
				mMaster.put("out_cl", sOutCl);
				mMaster.put("sale_dt", sSaleDt);
				mMaster.put("rmks", sRmks);

				log.debug(i + ")addDisSaleOut_addProdOut-------------------\n");
				log.debug(mMaster);
				log.debug("-------------------\n");
				insert("SALSCO00300.addProdOut", mMaster, onlineCtx);

				mDisSale.put("out_mgmt_no", sMgmtNo);
				mDisSale.put("dis_st", sDisSt);
				mDisSale.put("inout_cl", sInOutCl);
				mDisSale.put("inout_dtl_cl", sInOutDtlCl);
				mDisSale.put("settl_cond_cd", sSettlCondCd);
				mDisSale.put("sale_dt", sSaleDt);
				mDisSale.put("sale_yn", sSaleYn);
				mDisSale.put("sale_plc", sSalePlc);
				
				if("04".equals(sale_typ_cd)){
					mDisSale.put("trms_st", "T");
				}else{
					mDisSale.put("trms_st", sTrmsSt);
				}
				
				log.debug(i
						+ ")addDisSaleOut_addProdOutDtl-------------------\n");
				log.debug(mDisSale);
				log.debug("-------------------\n");
				// 상품판매출고상세 추가
				insert("SALSCO00300.addProdOutDtl", mDisSale, onlineCtx);

				// 상품입출고상세 추가
				insert("SALSCO00300.addProdOutHst", mDisSale, onlineCtx);

				// 상품재고 판매출고 처리
				update("SALSCO00300.updateProdDis", mDisSale, onlineCtx);
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		responseData.putFieldObjectMap(mMaster);

		return responseData;
	}

	/**
	 * 이동 출고 관리 번호 생성
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 이동 출고 관리 번호를 생성한다.
	 * @param mgmtCd 
	 *            관리번호종류코드 
	 * @param userId 
	 *            사용자아이디 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public String getMgmtNo(String mgmtCd, String userId,
			IOnlineContext onlineCtx) {
		String retMgmtNo = "";

		Map inMap = new HashMap();

		inMap.put("mgmt_no_cd", mgmtCd);
		inMap.put("user_id", userId);
		inMap.put("errcode", "");
		inMap.put("errmsg", "");
		inMap.put("mgmt_no", "");

		queryForObject("SALSCO00300.selectDisMgmtNo", inMap, onlineCtx);

		retMgmtNo = inMap.get("mgmt_no").toString();

		return retMgmtNo;
	}

	/**
	 * 
	 *
	 * @author 전희경 (jeonheekyung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdDisSt(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdDisSt method start");
		}

		IRecordSet rsProdDisSt = queryForRecordSet(
				"SALSCO00300.selectProdDisSt", requestData.getFieldMap());

		if (rsProdDisSt == null) {
			rsProdDisSt = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsProdDisSt.getRecordCount()) });

		responseData.putRecordSet("List", rsProdDisSt);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전희경 (jeonheekyung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateDisSaleOut(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("updateDisSaleOut method start");
		}

		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		/*
		 * mMaster 필드구성
		 #out_mgmt_no#			: requestData.getField("out_mgmt_no")
		 #gnrl_sale_no#			: requestData.getField("gnrl_sale_no")
		 #gnrl_sale_chg_seq#	: requestData.getField("gnrl_sale_chg_seq")
		 */
		Map mMaster = new HashMap();

		mMaster.put("out_mgmt_no", requestData.getField("out_mgmt_no"));
		mMaster.put("gnrl_sale_no", requestData.getField("gnrl_sale_no"));
		mMaster.put("gnrl_sale_chg_seq", requestData
				.getField("gnrl_sale_chg_seq"));

		update("SALSCO00300.updateProdOut", mMaster, onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		responseData.putFieldObjectMap(mMaster);

		return responseData;
	}

	/**
	 * 소수점 제거
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 문자열을 입력받아 소수점을 제거한 후 int 형태로 반환한다.
	 * @param oIn
	 *            소수점을 제거하기 위한 입력 객체
	 * @return 문자열에서 소수점을 제거한 값
	 */
	@SuppressWarnings("unchecked")
	public int getIntMem(Object oIn) {
		int iOut = 0;

		if (oIn == null || "".equals(oIn.toString())) {
			iOut = 0;
		} else {
			String sTmp = oIn.toString();
			if (sTmp.indexOf(".") != -1) {
				sTmp = sTmp.substring(0, sTmp.indexOf("."));
			}
			iOut = Integer.parseInt(sTmp);
		}

		return iOut;
	}

	/**
	 * 
	 *
	 * @author 전희경 (jeonheekyung)
	 * -상품별 판매 입고
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addDisSaleInBySernum(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("addDisSaleInBySernum method start");
		}

		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		/*
		 * mMaster 필드구성
		 #in_mgmt_no#		: 공통생성
		 #in_cl#			: 공통정의
		 #sale_dt#			: requestData.getField("sale_dtm")
		 #sale_plc#		: requestData.getField("sale_plc")
		 #in_st#			: 공통정의
		 #rmks#			: requestData.getField("rmks")
		 */
		Map mMaster = new HashMap();
		/*
		 * mDisSale 필드구성
		 #in_mgmt_no#		: 공통생성
		 #out_mgmt_no#		: requestData.getRecordSet("itemList")
		 #prod_cd#			: requestData.getRecordSet("itemList")
		 #color_cd#			: requestData.getRecordSet("itemList")
		 #ser_num#			: requestData.getRecordSet("itemList")
		 #dis_st#			: 공통정의
		 #inout_cl#			: 공통정의
		 #inout_dtl_cl#		: 공통정의
		 #sale_qty#			: requestData.getRecordSet("itemList")
		 #sale_amt#			: requestData.getRecordSet("itemList")
		 #sale_dt#			: requestData.getField("sale_dtm")
		 #sale_plc#			: requestData.getField("sale_plc")
		 #sale_yn#			: 공통정의
		 */
		Map mDisSale = null;

		String sMgmtNo = "";
		String sSaleDt = "";
		String sSalePlc = "";
		String sRmks = "";
		String sGnrlSaleNo = "";
		int nGnrlSaleChgSeq = 0;
		String sInSt = "03"; // 입고완료
		String sInCl = "105"; // 판매입고
		String sDisSt = "01"; // 가용
		String sInOutCl = "100"; // 입고
		String sInOutDtlCl = "105"; // 판매입고
		String sSaleYn = "N";
		String sTrmsSt = "";

		sSaleDt = requestData.getField("sale_dtm").substring(0, 8);
		sSalePlc = requestData.getField("sale_plc").toString();
		sTrmsSt = requestData.getField("trms_st").toString();
		if (requestData.getField("rmks") != null) {
			sRmks = requestData.getField("rmks").toString();
		}
		sGnrlSaleNo = requestData.getField("gnrl_sale_no").toString();
		nGnrlSaleChgSeq = getIntMem(requestData.getField("gnrl_sale_chg_seq"));

		IDataSet oDsDisSale;
		IRecordSet rsSaleList = requestData.getRecordSet("itemList");
		IRecordSet rsDelSaleList = requestData.getRecordSet("delItemList");

		// 판매
		if (rsSaleList != null) {
			for (int i = 0; i < rsSaleList.getRecordCount(); i++) {
				oDsDisSale = new DataSet();

				mDisSale = rsSaleList.getRecordMap(i);

				mMaster.put("out_mgmt_no", mDisSale.get("out_mgmt_no"));
				mMaster.put("gnrl_sale_no", sGnrlSaleNo);
				mMaster.put("gnrl_sale_chg_seq", nGnrlSaleChgSeq);

				// 출고마스터 :판매변경순번 업데이트
				oDsDisSale.putFieldObjectMap(mMaster);
				this.updateDisSaleOut(oDsDisSale, onlineCtx);
			}
		}

		//판매취소
		if (rsDelSaleList != null) {
			for (int i = 0; i < rsDelSaleList.getRecordCount(); i++) {
				oDsDisSale = new DataSet();

				mDisSale = rsDelSaleList.getRecordMap(i);

				// 입고관리번호 생성
				sMgmtNo = this.getMgmtNo("IN", iUserInfo.getLoginId(),
						onlineCtx);

				//IRecordSet rsMaster = requestData.getRecordSet("master");
				mMaster.put("in_mgmt_no", sMgmtNo);
				mMaster.put("in_cl", sInCl);
				mMaster.put("sale_dt", sSaleDt);
				mMaster.put("in_st", sInSt);
				mMaster.put("sale_plc", sSalePlc);
				mMaster.put("rmks", sRmks);
				mMaster.put("out_mgmt_no", mDisSale.get("out_mgmt_no"));
				mMaster.put("gnrl_sale_no", sGnrlSaleNo);
				mMaster.put("gnrl_sale_chg_seq", nGnrlSaleChgSeq);

				// 출고마스터 :판매변경순번 업데이트
				oDsDisSale.putFieldObjectMap(mMaster);
				this.updateDisSaleOut(oDsDisSale, onlineCtx);

				insert("SALSCO00300.addProdIn", mMaster, onlineCtx);
				log.debug("$$$$$$ = " + sMgmtNo + "  @@@@@@@@@@@@@@@ ==> "
						+ mMaster);

				mDisSale.put("in_mgmt_no", sMgmtNo);
				mDisSale.put("dis_st", sDisSt);
				mDisSale.put("inout_cl", sInOutCl);
				mDisSale.put("inout_dtl_cl", sInOutDtlCl);
				mDisSale.put("sale_yn", sSaleYn);
				mDisSale.put("sale_dt", sSaleDt);
				mDisSale.put("sale_plc", requestData.getField("sale_plc"));
				mDisSale.put("trms_st", sTrmsSt);
				
				log.debug("@@^^^^^^^^^ " + mDisSale);
				// 상품판매취소입고상세 추가
				insert("SALSCO00300.addProdInDtl", mDisSale, onlineCtx);

				// 상품입출고상세 추가
				insert("SALSCO00300.addProdInHst", mDisSale, onlineCtx);

				// 상품재고 판매취소입고 처리
				update("SALSCO00300.updateProdDis", mDisSale, onlineCtx);
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		responseData.putFieldObjectMap(mMaster);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * - 상품 판매 취소
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 *        requestData
	 *         - PROD_CD
	 *         - COLOR_CD
	 *         - SER_NUM
	 *         - GNRL_SALE_NO
	 *         - GNRL_SALE_CHG_SEQ
	 *         - HLD_PLC_ID
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveSaleCncl(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveSaleCncl method start");
		}
		IUserInfo iUserInfo = onlineCtx.getUserInfo();
		String sDate = new java.text.SimpleDateFormat("yyyyMMdd")
				.format(new java.util.Date());

		IRecordSet rsSaleCnclList = requestData.getRecordSet("saleCnclList");
		IRecordSet rsGnrlSaleCnclList = requestData
				.getRecordSet("gnrlSaleCnclList");

		Map requestMap = new HashMap();
		Map requestGnrlSaleMap = new HashMap();
		Map prodDis = new HashMap();
		Map saleAmt = new HashMap();
		Map inoutHist = new HashMap();
		Map inM = new HashMap();
		Map inDtl = new HashMap();
		Map chgSeq = new HashMap();
		Map gnrlSale = new HashMap();
		Map gnrlSaleMap = new HashMap();
		Map equipmentSaleDel = new HashMap();
		Map payMent = new HashMap();

		String dis_st = "01"; // 판매구분 : 판매취소
		String last_inout_cl = "100"; // 입출고 구분 : 입고
		String last_inout_dtl_cl = "105"; // 입출고상세구분 : 판매취소입고
		String in_st = "03"; // 입고 상태 : 입고완료
		String sale_chg_hst_cl = "09";
		

		/***********************
		 * 판매 
		 ***********************/
		if (rsGnrlSaleCnclList != null) {
			for (int i = 0; i < rsGnrlSaleCnclList.getRecordCount(); i++) {
				requestGnrlSaleMap = rsGnrlSaleCnclList.getRecordMap(i);
				//log.debug("start==>>"+requestGnrlSaleMap);
				chgSeq = (Map) queryForObject("SALSCO00300.getGnrlSaleChgSeq",
						requestGnrlSaleMap, onlineCtx);
				String gnrl_sale_no = (String) requestGnrlSaleMap
						.get("GNRL_SALE_NO");
				String gnrl_sale_chg_seq_new = (String) chgSeq
						.get("GNRL_SALE_CHG_SEQ");
				String gnrl_sale_chg_seq_old = (String) requestGnrlSaleMap
						.get("GNRL_SALE_CHG_SEQ");
				gnrlSaleMap.put(requestGnrlSaleMap.get("GNRL_SALE_NO"),
						gnrl_sale_chg_seq_new);
				//log.debug("gnrlSaleMap==>>"+gnrlSaleMap);
				//log.debug("일반판매 INSERT start");
				/**
				 * 일반판매 INSERT
				 */

				gnrlSale.put("gnrl_sale_no", gnrl_sale_no);
				gnrlSale.put("gnrl_sale_chg_seq", gnrl_sale_chg_seq_new);
				gnrlSale.put("sale_chg_hst_cl", sale_chg_hst_cl);
				gnrlSale.put("gnrl_sale_chg_seq_old", gnrl_sale_chg_seq_old);

				insert("SALSCO00300.addGnrlSaleForCncl", gnrlSale, onlineCtx);

				//log.debug("단말기 판매 INSERT start");
				/**
				 * 단말기 판매 새로운 SEQ 로 전체 INSERT
				 */
				insert("SALSCO00300.insertEquipmentSaleAll", gnrlSale,
						onlineCtx);

				//log.debug("판매 금액 변경 INSERT start");
				/**
				 * 판매 금액 변경 INSERT 
				 * UPDATE : TSAL_SALE_AMT
				 */
				saleAmt.put("dis_amt", requestGnrlSaleMap.get("DIS_AMT"));
				saleAmt.put("mar_amt", requestGnrlSaleMap.get("MAR_AMT"));
				saleAmt.put("cmms_amt", requestGnrlSaleMap.get("CMMS_AMT"));
				saleAmt.put("vat_amt", requestGnrlSaleMap.get("VAT_AMT"));
				saleAmt.put("sale_amt", requestGnrlSaleMap.get("SALE_AMT"));
				saleAmt.put("gnrl_sale_no", gnrl_sale_no);
				saleAmt.put("gnrl_sale_chg_seq_old", gnrl_sale_chg_seq_old);
				saleAmt.put("gnrl_sale_chg_seq", gnrl_sale_chg_seq_new);

				insert("SALSCO00300.addSaleAmtForCncl", saleAmt, onlineCtx);

				//log.debug("수납정보 INSERT start");

				/**
				 * 수납정보 INSERT
				 */
				payMent.put("gnrl_sale_no", gnrl_sale_no);
				payMent.put("gnrl_sale_chg_seq", gnrl_sale_chg_seq_new);
				payMent.put("sale_chg_hst_cl", sale_chg_hst_cl);
				payMent.put("gnrl_sale_chg_seq_old", gnrl_sale_chg_seq_old);
				payMent.put("sale_amt", requestGnrlSaleMap.get("SALE_AMT"));
				insert("SALSCO00300.addPayMentForCncl", payMent, onlineCtx);
			}
		}

		if (rsSaleCnclList != null) {
			for (int i = 0; i < rsSaleCnclList.getRecordCount(); i++) {
				requestMap = rsSaleCnclList.getRecordMap(i);
				String prod_cd = (String) requestMap.get("PROD_CD");
				String color_cd = (String) requestMap.get("COLOR_CD");
				String ser_num = (String) requestMap.get("SER_NUM");
				String gnrl_sale_no = (String) requestMap.get("GNRL_SALE_NO");
				String sale_amt = (String) requestMap.get("SALE_AMT");
				String gnrl_sale_chg_seq_new = (String) gnrlSaleMap
						.get((String) requestMap.get("GNRL_SALE_NO"));
				String sale_plc = (String) requestMap.get("HLD_PLC_ID");
				String sale_typ_cd = (String) requestMap.get("SALE_TYP_CD");
				String trms_yn = "";
				String trms_st = "";
				

				//log.debug("재고 상태 판매 -> 판매취소 UPDATE start");
				/**
				 * 재고 상태 판매 -> 판매취소 
				 * UPDATE : TDIS_PROD_DIS
				 */

				prodDis.put("dis_st", dis_st);
				prodDis.put("last_inout_cl", last_inout_cl);
				prodDis.put("last_inout_dtl_cl", last_inout_dtl_cl);
				prodDis.put("prod_cd", prod_cd);
				prodDis.put("ser_num", ser_num);
				prodDis.put("sale_typ_cd", sale_typ_cd);
				
				if(!sale_typ_cd.equals("04")){
					trms_yn = "N";
					trms_st = "C";
					
					prodDis.put("trms_yn", trms_yn);
					prodDis.put("trms_st", trms_st);
				}else{
					trms_yn = "Y";
					trms_st = "T";
					
					prodDis.put("trms_yn", trms_yn);
					prodDis.put("trms_st", trms_st);
				}
				
				
				
				update("SALSCO00300.updateProdDisForCncl", prodDis, onlineCtx);

				//log.debug("단말기 판매 취소  DELETE start");

				/**
				 * 단말기 판매 취소 
				 * DELETE : TSAL_EQUIPMENT_SALE
				 */

				equipmentSaleDel.put("gnrl_sale_no", gnrl_sale_no);
				equipmentSaleDel
						.put("gnrl_sale_chg_seq", gnrl_sale_chg_seq_new);
				equipmentSaleDel.put("ser_num", ser_num);
				update("SALSCO00300.updateEquipmentSaleForCncl",
						equipmentSaleDel, onlineCtx);

				//log.debug("입출고상세이력+1  INSERT start");

				/**
				 * 재고처리 - 입출고상세이력+1
				 * INSERT : TDIS_PROD_INOUT_HST
				 */
				String sMgmtNo = this.getMgmtNo("IN", iUserInfo.getLoginId(),
						onlineCtx);
				inoutHist.put("prod_cd", prod_cd);
				inoutHist.put("color_cd", color_cd);
				inoutHist.put("ser_num", ser_num);
				inoutHist.put("in_mgmt_no", sMgmtNo);
				inoutHist.put("inout_cl", last_inout_cl);
				inoutHist.put("inout_dtl_cl", last_inout_dtl_cl);
				inoutHist.put("sale_plc", sale_plc);
				insert("SALSCO00300.addProdInHst", inoutHist, onlineCtx);

				//log.debug("상품입고M+1  INSERT start");
				/**
				 * 재고처리 - 상품입고M + 1
				 * INSERT : TDIS_IN_PROD_M
				 */
				inM.put("in_mgmt_no", sMgmtNo);
				inM.put("in_cl", last_inout_cl);
				inM.put("sale_dt", sDate);
				inM.put("sale_plc", sale_plc);
				inM.put("in_st", in_st);
				inM.put("rmks", last_inout_cl);

				insert("SALSCO00300.addProdIn", inM, onlineCtx);

				//log.debug("상품입고 DTL+1  INSERT start");
				/**
				 * 재고처리 - 상품입고 DTL + 1
				 * INSERT : TDIS_IN_PROD_DTL_D
				 */
				inDtl.put("in_mgmt_no", sMgmtNo);
				inDtl.put("prod_cd", prod_cd);
				inDtl.put("color_cd", color_cd);
				inDtl.put("ser_num", ser_num);
				inDtl.put("sale_qty", "1");
				inDtl.put("sale_amt", sale_amt);

				insert("SALSCO00300.addProdInDtl", inDtl, onlineCtx);
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdDisStAll(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdDisStAll method start");
		}

		Map requestMap = new HashMap();
		IRecordSet rsProdSt = new RecordSet("List");
		rsProdSt.addHeader(new RecordHeader("err_str", java.sql.Types.VARCHAR));
		String err_str = "";
		IRecordSet rsProdList = requestData.getRecordSet("itemList");
		if(rsProdList != null){
			for (int i = 0; i < rsProdList.getRecordCount(); i++) {
				requestMap = rsProdList.getRecordMap(i);
				IRecordSet rsProdDisSt = queryForRecordSet(
						"SALSCO00300.selectProdDisSt", requestMap);
				if (rsProdDisSt == null) {
					err_str = err_str + rsProdDisSt.get(0, "SER_NUM")
							+ " : 해당상품이 존재하지 않습니다.\\n";
				} else {
					String saleSt = rsProdDisSt.get(0, "DIS_ST");
					if ("02".equals(saleSt)) {
						err_str = err_str + rsProdDisSt.get(0, "SER_NUM")
								+ " : 비가용상태입니다.\\n";
					} else {
						String trmsSt = rsProdDisSt.get(0, "TRMS_ST");
						String trmsYn = rsProdDisSt.get(0, "TRMS_YN");
						
						if("N".equals(trmsYn)){
							if(!"N".equals(trmsSt)){
								log.debug("N.equals(trmsYn)) " + (("N".equals(trmsYn))));
								log.debug("!N.equals(trmsSt)) " + ((!"N".equals(trmsSt))));
								err_str = err_str + rsProdDisSt.get(0, "SER_NUM")
										+ " : U.key, T.Key 인터페이스 작업 중인 데이터 입니다.\\n";
							}
						}
					}
				}
			}
		}
		
		

		if (!err_str.equals("")) {
			IRecord ncRecord = new Record();
			ncRecord.add(err_str);
			rsProdSt.addRecord(ncRecord);
		}

		if (rsProdSt == null) {
			rsProdSt = new RecordSet("List");
		}
		log.debug("getProdDisStAll method end");
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsProdSt.getRecordCount()) });

		responseData.putRecordSet("List", rsProdSt);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * - 재고별 판매출고내역 삭제 처리 (Row Del)
	 * - I/F 미반영 대상 DATA만 가능
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteDisSale(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveDisSaleInForDel method start");
		}

		IRecordSet rsDelSaleList = requestData.getRecordSet("itemList");

		log.debug("rsDelSaleList)  saveDisSaleInForDel -------------------\n");
		log.debug(rsDelSaleList);
		log.debug("-------------------\n");

		Map mDelSale = null;
		Map mInoutHst = null;

		String strInoutSeq = "";
		String strOutMgmtNo = "";
		String strOutSeq = "";
		String strInoutDtlCl = "";
		String strInoutClY = "";
		String strInoutDtlClY = "";

		String dis_st = "01";

		for (int i = 0; i < rsDelSaleList.getRecordCount(); i++) {
			mDelSale = rsDelSaleList.getRecordMap(i);

			// 입출고 이력 정보 조회
			mInoutHst = (Map) queryForObject("SALSCO00300.getProdInoutHstInfo",
					mDelSale, onlineCtx);
			strOutMgmtNo = mInoutHst.get("OUT_MGMT_NO") == null ? ""
					: mInoutHst.get("OUT_MGMT_NO").toString();
			strOutSeq = mInoutHst.get("OUT_SEQ") == null ? "" : mInoutHst.get(
					"OUT_SEQ").toString();
			strInoutDtlCl = mInoutHst.get("INOUT_DTL_CL") == null ? ""
					: mInoutHst.get("INOUT_DTL_CL").toString();
			strInoutSeq = mInoutHst.get("INOUT_SEQ") == null ? "" : mInoutHst
					.get("INOUT_SEQ").toString();
			strInoutClY = mInoutHst.get("INOUT_CL_Y") == null ? "" : mInoutHst
					.get("INOUT_CL_Y").toString();
			strInoutDtlClY = mInoutHst.get("INOUT_DTL_CL_Y") == null ? ""
					: mInoutHst.get("INOUT_DTL_CL_Y").toString();

			if (!strOutMgmtNo.equals(mDelSale.get("out_mgmt_no"))
					|| !strOutSeq.equals(mDelSale.get("out_seq"))
					|| !strInoutDtlCl.equals("205")) {

				log.debug(i + " : 재고 err -------------------\n");
				log.debug(strOutMgmtNo + ":" + mDelSale.get("out_mgmt_no"));
				log.debug(strOutSeq + ":" + mDelSale.get("out_seq"));
				log.debug(strInoutDtlCl);
				log.debug("-------------------\n");

				// 해당상품의 가장 최근이력이 판매 이력이 아닌경우

			} else {
				mDelSale.remove("gnrl_sale_chg_seq");
				mDelSale.put("gnrl_sale_chg_seq", requestData
						.getField("gnrl_sale_chg_seq"));
				log.debug(i + " : updateProdOutMForDel -------------------\n");
				log.debug(mDelSale);
				log.debug("-------------------\n");
				update("SALSCO00300.updateProdOut", mDelSale, onlineCtx);
				// 출고 Master 삭제
				update("SALSCO00300.updateProdOutMForDel", mDelSale, onlineCtx);
				// 출고 상세 삭제
				update("SALSCO00300.updateProdOutDtlForDel", mDelSale,
						onlineCtx);
				// 출고 이력 삭제
				mDelSale.put("inout_seq", strInoutSeq);

				log
						.debug(i
								+ " : updateProdOutHstForDel -------------------\n");
				log.debug(mDelSale);
				log.debug("-------------------\n");

				update("SALSCO00300.updateProdOutHstForDel", mDelSale,
						onlineCtx);

				mDelSale.put("dis_st", dis_st);
				mDelSale.put("last_inout_cl", strInoutClY);
				mDelSale.put("last_inout_dtl_cl", strInoutDtlClY);
				mDelSale.put("trms_yn", "N");
				mDelSale.put("trms_st", "N");

				log.debug(i + " : updateProdDisForCncl -------------------\n");
				log.debug(mDelSale);
				log.debug("-------------------\n");
				// 재고 update
				update("SALSCO00300.updateProdDisForCncl", mDelSale, onlineCtx);
			}

		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateDisSaleOutAll(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("updateDisSaleOutAll method start");
		}

		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		/*
		 * mMaster 필드구성
		 #out_mgmt_no#			: requestData.getField("out_mgmt_no")
		 #gnrl_sale_no#			: requestData.getField("gnrl_sale_no")
		 #gnrl_sale_chg_seq#	: requestData.getField("gnrl_sale_chg_seq")
		 */
		Map mMaster = new HashMap();

		mMaster.put("out_mgmt_no", requestData.getField("out_mgmt_no"));
		mMaster.put("gnrl_sale_no", requestData.getField("gnrl_sale_no"));
		mMaster.put("gnrl_sale_chg_seq", requestData
				.getField("gnrl_sale_chg_seq"));

		update("SALSCO00300.updateProdOutAll", mMaster, onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		responseData.putFieldObjectMap(mMaster);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * - 재고 판매 삭제 처리
	 * - 해당 판매번호와 관련된 모든 데이터 삭제 처리
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteDisSaleAll(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("deleteDisSaleAll method start");
		}
		IRecordSet rs = requestData.getRecordSet("itemList");
		IRecordSet rsDelList = requestData.getRecordSet("itemDelList");
		
		Map mRs = rs.getRecordMap(0);
		Map mOut = null;
		Map mInoutHst = null ;
		Map mDel = null;
		
		String strOutMgmtNo = "";
		String strOutSeq = "";
		String strInoutDtlCl = "";
		String strInoutClY = "";
		String strInoutDtlClY = "";
		String dis_st = "01";
		
		IRecordSet outList = queryForRecordSet("SALSCO00300.getProdOutMForDel", mRs, onlineCtx);
		
		if(rsDelList != null){
			for(int i=0; i<rsDelList.getRecordCount(); i++){
				mDel = rsDelList.getRecordMap(i);
				mInoutHst = (Map) queryForObject("SALSCO00300.getProdInoutHstInfo", mDel, onlineCtx);
				
				
				strOutMgmtNo = mInoutHst.get("OUT_MGMT_NO") == null ? ""
						: mInoutHst.get("OUT_MGMT_NO").toString();
				strOutSeq = mInoutHst.get("OUT_SEQ") == null ? "" : mInoutHst.get(
						"OUT_SEQ").toString();
				strInoutDtlCl = mInoutHst.get("INOUT_DTL_CL") == null ? ""
						: mInoutHst.get("INOUT_DTL_CL").toString();
				strInoutClY = mInoutHst.get("INOUT_CL_Y") == null ? "" : mInoutHst
						.get("INOUT_CL_Y").toString();
				strInoutDtlClY = mInoutHst.get("INOUT_DTL_CL_Y") == null ? ""
						: mInoutHst.get("INOUT_DTL_CL_Y").toString();
			
				if (!strOutMgmtNo.equals(mDel.get("out_mgmt_no"))
						|| !strOutSeq.equals(mDel.get("out_seq"))
						|| !strInoutDtlCl.equals("205")) {

					log.debug(i + " : 재고 err -------------------\n");
					log.debug(strOutMgmtNo + ":" + mDel.get("out_mgmt_no"));
					log.debug(strOutSeq + ":" + mDel.get("out_seq"));
					log.debug(strInoutDtlCl);
					log.debug("-------------------\n");

					// 해당상품의 가장 최근이력이 판매 이력이 아닌경우

				} else {
					mDel.put("dis_st", dis_st);
					mDel.put("last_inout_cl", strInoutClY);
					mDel.put("last_inout_dtl_cl", strInoutDtlClY);
					mDel.put("trms_yn", "N");
					mDel.put("trms_st", "N");

					log.debug(i + " : updateProdDisForCncl -------------------\n");
					log.debug(mDel);
					log.debug("-------------------\n");
					// 재고 update
					update("SALSCO00300.updateProdDisForCncl", mDel, onlineCtx);
				}
			}
		}
		
		if(outList != null){
			for(int i=0; i<outList.getRecordCount(); i++){
				
				mOut = outList.getRecordMap(i);
				// 출고 마스터 삭제
				update("SALSCO00300.deleteProdOutMAll", mOut, onlineCtx);
				// 출고 Dtl 삭제
				update("SALSCO00300.deleteProdOutDtlAll", mOut, onlineCtx);
				// 입출력 이력 삭제
				update("SALSCO00300.deleteProdInoutHstAll", mOut, onlineCtx);
				
				
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		return responseData;
	}

}