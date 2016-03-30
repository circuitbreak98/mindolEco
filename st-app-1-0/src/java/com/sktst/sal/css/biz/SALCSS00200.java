/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.sal.css.biz;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.util.DataSetFactory;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.sal.sco.ejb.SCO;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2013-05-20 16:09:48</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class SALCSS00200 extends BaseBizUnit {

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
	public IDataSet getSaleInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getSaleInfo method start");
		}

		IRecordSet rsMaster = queryForRecordSet("SALBBS00200.getSaleMaster",
				requestData.getFieldMap());
		/*
		 IRecordSet rsEquipmentList = queryForRecordSet("SALCSS00200.getEquipmentSaleList", requestData.getFieldMap());
		 IRecordSet rsPayment = queryForRecordSet("SALCSS00200.getSalePayment", requestData.getFieldMap());
		 IRecordSet rsRDItemList = queryForRecordSet("SALCSS00200.getEquipSaleListForRD", requestData.getFieldMap());
		 
		 if (rsMaster == null) {
		 rsMaster = new RecordSet("ds_master");
		 }
		 if (rsEquipmentList == null) {
		 rsEquipmentList = new RecordSet("ds_equipmentList");
		 }
		 if (rsPayment == null) {
		 rsPayment = new RecordSet("ds_payment");
		 }
		 if (rsRDItemList == null) {
		 rsRDItemList = new RecordSet("ds_report1");
		 }
		 
		 IDataSet responseData = DataSetFactory.createWithOKResultMessage(
		 BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
		 .valueOf(rsMaster.getRecordCount()) });
		 */
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(1) });

		/*
		 responseData.putRecordSet("ds_master", rsMaster);
		 responseData.putRecordSet("ds_equipmentList", rsEquipmentList);
		 responseData.putRecordSet("ds_payment", rsPayment);
		 responseData.putRecordSet("ds_report1", rsRDItemList);
		 */
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
	public IDataSet getConSaleInfo(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getConSaleInfo method start");
		}

		IRecordSet rsMaster = queryForRecordSet("SALCSS00200.getSaleMaster",
				requestData.getFieldMap());
		IRecordSet rsEquipmentList = queryForRecordSet(
				"SALCSS00200.getEquipmentSaleList", requestData.getFieldMap());
		IRecordSet rsPayment = queryForRecordSet("SALCSS00200.getSalePayment",
				requestData.getFieldMap());
		IRecordSet rsRDItemList = queryForRecordSet(
				"SALCSS00200.getEquipSaleListForRD", requestData.getFieldMap());

		if (rsMaster == null) {
			rsMaster = new RecordSet("ds_master");
		}
		if (rsEquipmentList == null) {
			rsEquipmentList = new RecordSet("ds_equipmentList");
		}
		if (rsPayment == null) {
			rsPayment = new RecordSet("ds_payment");
		}
		if (rsRDItemList == null) {
			rsRDItemList = new RecordSet("ds_report1");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsMaster.getRecordCount()) });

		responseData.putRecordSet("ds_master", rsMaster);
		responseData.putRecordSet("ds_equipmentList", rsEquipmentList);
		responseData.putRecordSet("ds_payment", rsPayment);
		responseData.putRecordSet("ds_report1", rsRDItemList);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * - 위탁판매 등록
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveConSale(IDataSet requestData, IOnlineContext onlineCtx)
			throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("saveConSale method start");
		}

		SCO sco = (SCO) lookupBizComponent("sktst.sal.SCO");
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");

		IRecordSet rsMaster = requestData.getRecordSet("master");
		IRecordSet rsPayment = requestData.getRecordSet("payment");
		IRecordSet rsProdList = requestData.getRecordSet("prodList");
		IRecordSet rsDelProdList = requestData.getRecordSet("delProdList");

		Map mMaster = null;
		Map mPayment = null;
		Map mProd = null;

		String procFlag = "";
		String docId = "";
		String sSaleChgHstCl = "";
		String sSaleCnclYn = "";

		int grnlSaleChgSeq = 0;
		int intSaleSeq = 1;
		log.debug("rsMaster■■■■■■■■■■■■■■■■■");
		log.debug(rsMaster);
		log.debug("rsPayment■■■■■■■■■■■■■■■■■");
		log.debug(rsPayment);
		log.debug("rsProdList■■■■■■■■■■■■■■■■■");
		log.debug(rsProdList);
		log.debug("rsDelProdList■■■■■■■■■■■■■■■■■");
		log.debug(rsDelProdList);
		if (rsMaster != null && rsPayment != null) {
			mMaster = rsMaster.getRecordMap(0);
			mPayment = rsPayment.getRecordMap(0);
			procFlag = mMaster.get("flag").toString();

			/*
			 * 첨부파일 등록
			 */
			IRecordSet rsFile = requestData.getRecordSet("nc_fileDs");
			if (rsFile.getRecordCount() > 0) {
				bco.addAttachFile(requestData, onlineCtx);

				docId = rsFile.get(0, "DOC_ID"); // 최초 등록시 첨부 파일 정보에서 DOC_ID
				mMaster.put("DOC_ID", docId); // 
			}
			log.debug("1■■■■■■■■■■■■■■■■■");
			/*
			 * 판매 관리번호와 변경순번 취득
			 */
			String strGnrlSaleNo = mMaster.get("gnrl_sale_no") == null ? ""
					: mMaster.get("gnrl_sale_no").toString();
			String strGrnlSaleChgSeq_old = mMaster.get("gnrl_sale_chg_seq") == null ? ""
					: mMaster.get("gnrl_sale_chg_seq").toString();

			if (strGnrlSaleNo == null || "".equals(strGnrlSaleNo)) {
				strGnrlSaleNo = getSaleMgmtNo("S", requestData, onlineCtx);
				mMaster.put("gnrl_sale_no", strGnrlSaleNo);
				grnlSaleChgSeq = 1;

			} else {
				grnlSaleChgSeq = Integer.parseInt(strGrnlSaleChgSeq_old);
				grnlSaleChgSeq++;
			}

			mMaster.put("gnrl_sale_chg_seq", grnlSaleChgSeq);

			/*
			 * 일반판매 start
			 */

			sSaleCnclYn = "N";

			if ("MOD".equalsIgnoreCase(procFlag)) {
				sSaleChgHstCl = "09"; // 판매수정
			} else if ("NEW".equalsIgnoreCase(procFlag)) {
				sSaleChgHstCl = "01"; // 판매
			}

			mMaster.put("sale_chg_hst_cl", sSaleChgHstCl);
			mMaster.put("sale_cncl_yn", sSaleCnclYn);
			mMaster.put("sale_prc_plc", mPayment.get("SALE_PRC_PLC"));

			mPayment.put("gnrl_sale_no", strGnrlSaleNo);
			mPayment.put("gnrl_sale_chg_seq", grnlSaleChgSeq);

			log.debug("mMaster-------------------");
			log.debug(mMaster);
			log.debug("-------------------");

			// 일반판매저장
			insert("SALCSS00200.addGnrlSale", mMaster, onlineCtx);

			// 현금매출저장
			insert("SALCSS00200.addSaleAmt", mPayment, onlineCtx);
			log.debug("mPayment-------------------");
			log.debug(mPayment);
			log.debug("-------------------");
			// 수납저장
			insert("SALCSS00200.addPayment", mPayment, onlineCtx);

			// 단말기판매정보
			// 1. 판매정보
			log.debug(procFlag + " : 단말기판매정보-------------------");
			log.debug(mPayment);
			log.debug("-------------------");
			if ("NEW".equalsIgnoreCase(procFlag)) {
				for (int i = 0; i < rsProdList.getRecordCount(); i++) {
					mProd = rsProdList.getRecordMap(i);
					mProd.remove("gnrl_sale_no");
					mProd.remove("gnrl_sale_chg_seq");
					mProd.put("gnrl_sale_no", strGnrlSaleNo);
					mProd.put("gnrl_sale_chg_seq", grnlSaleChgSeq);

					mProd.put("sale_seq", intSaleSeq);
					log.debug("NEW_mProd-------------------");
					log.debug(mProd);
					log.debug("-------------------");
					insert("SALCSS00200.addEquipmentSale", mProd, onlineCtx);
					intSaleSeq++;
				}
			} else {
				if (rsProdList != null) {

					mMaster.remove("gnrl_sale_no");
					mMaster.remove("gnrl_sale_chg_seq");
					mMaster.remove("gnrl_sale_chg_seq_old");
					mMaster.put("gnrl_sale_no", strGnrlSaleNo);
					mMaster.put("gnrl_sale_chg_seq", grnlSaleChgSeq);
					mMaster.put("gnrl_sale_chg_seq_old", strGrnlSaleChgSeq_old);
					insert("SALCSS00200.insertEquipmentSaleAll", mMaster,
							onlineCtx);

				}
			}

			log.debug("2.판매삭제정보■■■■■■■■■■■■■■■■■");
			//2.판매삭제정보
			if (rsDelProdList != null) {
				for (int i = 0; i < rsDelProdList.getRecordCount(); i++) {
					mProd = rsDelProdList.getRecordMap(i);

					mProd.remove("gnrl_sale_no");
					mProd.remove("gnrl_sale_chg_seq");
					mProd.put("gnrl_sale_no", strGnrlSaleNo);
					mProd.put("gnrl_sale_chg_seq", grnlSaleChgSeq);

					log.debug("DEL_insert_mProd-------------------");
					log.debug(mProd);
					log.debug("-------------------");
					insert("SALCSS00200.updateEquipmentSale", mProd, onlineCtx);
				}
			}

			/*
			 * 재고처리
			 */
			log.debug("재고처리■■■■■■■■■■■■■■■■■");
			IDataSet oDsDisSale = new DataSet();
			IDataSet oDsDisSaleDel = new DataSet();

			if ("MOD".equalsIgnoreCase(procFlag)) {

				// 입력
				mMaster.remove("gnrl_sale_chg_seq");
				mMaster.put("gnrl_sale_chg_seq", grnlSaleChgSeq);
				oDsDisSale.putFieldObjectMap(mMaster);

				log.debug("oDsDisSale■■■■■■■■■■■■■■■■■");
				log.debug(oDsDisSale);
				log.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
				oDsDisSale = sco.updateDisSaleOutAll(oDsDisSale, onlineCtx);

				// 삭제
				oDsDisSaleDel.putRecordSet("itemList", rsDelProdList);
				oDsDisSaleDel.putFieldObjectMap(mMaster);
				log.debug("MOD DIS■■■■■■■■■■■■■■■■■");
				log.debug(oDsDisSaleDel);
				log.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
				oDsDisSaleDel = sco.deleteDisSale(oDsDisSaleDel, onlineCtx);

			} else if ("NEW".equalsIgnoreCase(procFlag)) {
				oDsDisSale.putRecordSet("itemList", rsProdList);
				oDsDisSale.putFieldObjectMap(mMaster);
				log.debug("NEW DIS■■■■■■■■■■■■■■■■■");
				log.debug(oDsDisSale);
				log.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
				oDsDisSale = sco.addDisSaleOut(oDsDisSale, onlineCtx);
			}

		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });

		return result;
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
	public String getSaleMgmtNo(String mgmtCd, IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		String saleMgmtNo = "";

		Map oParam = requestData.getFieldMap();
		Map map = null;

		map = (Map) queryForObject("SALCSS00200.getSaleMgmtSeq", mgmtCd);

		saleMgmtNo = map.get("SALEMGMTNO").toString();

		return saleMgmtNo;
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
	public IDataSet deleteConSale(IDataSet requestData, IOnlineContext onlineCtx)
			throws Exception {
		
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("deleteConSale method start");
		}
		
		IRecordSet rsMaster = requestData.getRecordSet("master");
		IRecordSet rsProdList = requestData.getRecordSet("prodList");
		
		if (rsMaster != null){
			Map mMaster = rsMaster.getRecordMap(0);
			
			insert("SALCSS00200.deleteGnrlSale", mMaster, onlineCtx);
			insert("SALCSS00200.deletePayMent", mMaster, onlineCtx);
			insert("SALCSS00200.deleteSaleAmt", mMaster, onlineCtx);
			insert("SALCSS00200.deleteEquipmentSale", mMaster, onlineCtx);

		}
		
		if (rsProdList != null){
			
			SCO sco = (SCO) lookupBizComponent("sktst.sal.SCO");

			IDataSet oDsDisSaleDel = new DataSet();
			oDsDisSaleDel.putRecordSet("itemList", rsMaster);
			oDsDisSaleDel.putRecordSet("itemDelList", rsProdList);
			oDsDisSaleDel = sco.deleteDisSaleAll(oDsDisSaleDel, onlineCtx);

		}
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf("1") });

		return responseData;
	}
}