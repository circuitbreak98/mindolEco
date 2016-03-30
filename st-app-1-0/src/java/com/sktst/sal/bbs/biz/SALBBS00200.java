/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.sal.bbs.biz;

import java.util.Map;

import org.apache.commons.logging.Log;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.common.file.IPsFileUploadManager;
import com.sktst.sal.sco.ejb.SCO;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-10-07 17:30:01</li>
 * </ul>
 *
 * @author 전희경 (jeonheekyung)
 */
public class SALBBS00200 extends BaseBizUnit {

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
	public IDataSet saveSaleList(IDataSet requestData, IOnlineContext onlineCtx)
			throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("saveSaleList method start");
		}

		int insCnt = 0;
		int uptCnt = 0;
		int delCnt = 0;

		SCO sco = (SCO) lookupBizComponent("sktst.sal.SCO");

		IRecordSet rsMaster = requestData.getRecordSet("master");
		IRecordSet rsPayment = requestData.getRecordSet("payment");

		Map mMaster = null;
		Map mPayment = null;
		Map mSaleProd = null;

		String procFlag = "";
		String strMgmtNo = "";
		String sSuccessCd = "S";
		String sCnt = "0";
		String sDate = "";

		int nGnrlSaleChgSeq = 1;
		String sSaleChgHstCl = "";
		String sSaleCnclYn = "";
		String sSaleCnclDtm = "";
		String sSaleCnclCl = "";
		String sTrmsSt = "";
		String sTrmsYn = "";

		String docId = "";

		// 현재 일시 추출
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");

		long currentTime = System.currentTimeMillis();
		sDate = df.format(new Date(currentTime));

		// 마스터정보,수납정보
		if (rsMaster != null && rsPayment != null) {
			
			mMaster = rsMaster.getRecordMap(0);
			mPayment = rsPayment.getRecordMap(0);
			procFlag = mMaster.get("flag") == null ? "" : mMaster.get("flag").toString();
			sTrmsYn = mMaster.get("trms_yn") == null ? "" : mMaster.get("trms_yn").toString();
			
			
			
			log.debug("첨부 파일 처리 start");
			//		  첨부 파일 처리
			IRecordSet rsFile = requestData.getRecordSet("nc_fileDs");
			if (rsFile.getRecordCount() > 0) {
				addAttachFiles(requestData, onlineCtx);
				
				docId = rsFile.get(0, "DOC_ID"); // 최초 등록시 첨부 파일 정보에서 DOC_ID
				mMaster.put("DOC_ID", docId); // 
			}

			// 일반판매 관리번호와 변경순번
			String strGnrlSaleNo = mMaster.get("gnrl_sale_no") == null ? ""
					: mMaster.get("gnrl_sale_no").toString();
			String strGrnlSaleChgSeq = mMaster.get("gnrl_sale_chg_seq") == null ? ""
					: mMaster.get("gnrl_sale_chg_seq").toString();

			// 일반판매관리번호가 없는 경우
			if (strGnrlSaleNo == null || "".equals(strGnrlSaleNo)) {
				strGnrlSaleNo = getSaleMgmtNo("S", requestData, onlineCtx);
				mMaster.put("gnrl_sale_no", strGnrlSaleNo);

			} else {
				nGnrlSaleChgSeq = getIntMem(strGrnlSaleChgSeq);
				nGnrlSaleChgSeq++;
			}

			mMaster.put("gnrl_sale_chg_seq", nGnrlSaleChgSeq);

			// 판매취소인경우
			if ("DEL".equalsIgnoreCase(procFlag)) {
				sSaleChgHstCl = "07"; // 판매취소
				sSaleCnclYn = "Y";
				sSaleCnclDtm = sDate;
				sSaleCnclCl = "1";
				if("Y".equals(sTrmsYn)){
					sTrmsSt = "C";
				}else{
					sTrmsSt = "N";
				}
				
			} else if ("MOD".equalsIgnoreCase(procFlag)) {
				sSaleChgHstCl = "09"; // 판매수정
				sSaleCnclYn = "N";
				sSaleCnclDtm = "";
				sSaleCnclCl = "";
				sTrmsSt = "";
			} else if ("NEW".equalsIgnoreCase(procFlag)) {
				sSaleChgHstCl = "01"; // 판매
				sSaleCnclYn = "N";
				sSaleCnclDtm = "";
				sSaleCnclCl = "";
				sTrmsSt = "A";
			}

			mMaster.put("sale_chg_hst_cl", sSaleChgHstCl);
			mMaster.put("sale_cncl_yn", sSaleCnclYn);
			mMaster.put("sale_cncl_dtm", sSaleCnclDtm);
			mMaster.put("sale_cncl_cl", sSaleCnclCl);
			mMaster.put("sale_prc_plc", mPayment.get("SALE_PRC_PLC"));
			mMaster.put("trms_st", sTrmsSt);

			mPayment.put("gnrl_sale_no", strGnrlSaleNo);
			mPayment.put("gnrl_sale_chg_seq", nGnrlSaleChgSeq);

			// 일반판매저장
			insert("SALBBS00200.addGnrlSale", mMaster, onlineCtx);

			// 현금매출저장
			insert("SALBBS00200.addSaleAmt", mPayment, onlineCtx);

			// 수납저장
			insert("SALBBS00200.addPayment", mPayment, onlineCtx);

			// 단말기판매정보
			IRecordSet rsSaleList = requestData.getRecordSet("itemList");

			if (rsSaleList != null) {
				if ("DEL".equalsIgnoreCase(procFlag)) {
					for (int i = 0; i < rsSaleList.getRecordCount(); i++) {
						mSaleProd = rsSaleList.getRecordMap(i);

						mSaleProd.put("gnrl_sale_no", strGnrlSaleNo);
						mSaleProd.put("gnrl_sale_chg_seq", nGnrlSaleChgSeq);

						//단말기판매
						insert("SALBBS00200.addEquipmentSaleForCncl", mSaleProd, onlineCtx);

					}
				}else{
					for (int i = 0; i < rsSaleList.getRecordCount(); i++) {
						mSaleProd = rsSaleList.getRecordMap(i);

						mSaleProd.put("gnrl_sale_no", strGnrlSaleNo);
						mSaleProd.put("gnrl_sale_chg_seq", nGnrlSaleChgSeq);

						//단말기판매
						insert("SALBBS00200.addEquipmentSale", mSaleProd, onlineCtx);

					}
				}
			}
		} else {
			log.debug("^^^^^^^^^^^^ Err");
		}

		// 상품판매 재고처리
		IDataSet oDsDisSale = new DataSet();

		try {
			if ("DEL".equalsIgnoreCase(procFlag)) {
				oDsDisSale.putRecordSet("delItemList", requestData
						.getRecordSet("itemList"));
				mMaster.put("sale_dtm", mMaster.get("sale_chg_dtm"));
				oDsDisSale.putFieldObjectMap(mMaster);

				oDsDisSale = sco.addDisSaleInBySernum(oDsDisSale, onlineCtx);
			} else if ("MOD".equalsIgnoreCase(procFlag)) {
				oDsDisSale.putRecordSet("itemList", requestData
						.getRecordSet("itemList"));
				oDsDisSale.putRecordSet("delItemList", requestData
						.getRecordSet("delItemList"));
				oDsDisSale.putFieldObjectMap(mMaster);

				oDsDisSale = sco.addDisSaleInBySernum(oDsDisSale, onlineCtx);
				//oDsDisSale = sco.updateDisSaleOut(oDsDisSale, onlineCtx);
			} else if ("NEW".equalsIgnoreCase(procFlag)) {
				oDsDisSale.putRecordSet("itemList", requestData
						.getRecordSet("itemList"));
				oDsDisSale.putFieldObjectMap(mMaster);

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
				log.debug("addDisSaleOut insert Exception ex: "
						+ ex.getMessage());
			}
			throw ex;
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"Insert:" + insCnt, " Update:" + uptCnt,
						" Delete:" + delCnt });

		return result;
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
	public String getSaleMgmtNo(String mgmtCd, IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		String saleMgmtNo = "";

		Map oParam = requestData.getFieldMap();
		Map map = null;

		map = (Map) queryForObject("SALBBS00200.getSaleMgmtSeq", mgmtCd);

		saleMgmtNo = map.get("SALEMGMTNO").toString();

		return saleMgmtNo;
	}

	public IDataSet getSaleMgmtNo(IDataSet requestData, IOnlineContext onlineCtx) {

		IDataSet result = new DataSet();
		// TODO 업무 로직 작성 필요

		return result;
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
	public IDataSet getSaleDtlList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getSaleDtlList method start");
		}

		IRecordSet rsMaster = queryForRecordSet("SALBBS00200.getSaleMaster",
				requestData.getFieldMap());
		IRecordSet rsItemList = queryForRecordSet(
				"SALBBS00200.getEquipSaleDtList", requestData.getFieldMap());
		IRecordSet rsPayment = queryForRecordSet("SALBBS00200.getSalePayment",
				requestData.getFieldMap());
		IRecordSet rsRDItemList = queryForRecordSet(
				"SALBBS00200.getEquipSaleListForRD", requestData.getFieldMap());
		if (rsMaster == null) {
			rsMaster = new RecordSet("ds_master");
		}
		if (rsItemList == null) {
			rsItemList = new RecordSet("ds_itemList");
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
		responseData.putRecordSet("ds_itemList", rsItemList);
		responseData.putRecordSet("ds_payment", rsPayment);
		responseData.putRecordSet("ds_report1", rsRDItemList);
		return responseData;
	}

	/**
	 * 소수점 제거
	 *
	 * @author 전희경 (jeonheekyung)
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
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getUpdateYn(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getUpdateYn method start");
		}

		IRecordSet rsMaster = queryForRecordSet("SALBBS00200.getUpdateYn",
				requestData.getFieldMap());

		if (rsMaster == null) {
			rsMaster = new RecordSet("ds_updateYn");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsMaster.getRecordCount()) });

		responseData.putRecordSet("ds_updateYn", rsMaster);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * -첨부파일 정보 취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getAttatchList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		//		 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMNTC00200.getAttatchList method start");
		}

		// 2. 파일 내역 조회
		IPsFileUploadManager fileManager = (IPsFileUploadManager) ComponentRegistry
				.lookup(ServiceConstants.FILEUPLOAD);

		// 3. 결과 지정
		return fileManager.getFileInfoList("nc_fileDs", requestData, onlineCtx);
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
	public IDataSet addAttachFiles(IDataSet requestData,
			IOnlineContext onlineCtx) {

		IPsFileUploadManager fileManager = (IPsFileUploadManager) ComponentRegistry.lookup(ServiceConstants.FILEUPLOAD);
		try {
			int updateCount = fileManager.saveAllFileInfo("nc_fileDs",
					requestData, onlineCtx);
			fileManager.commitFile("nc_fileDs", requestData);
			return DataSetFactory.createWithOKResultMessage(
					BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
							+ updateCount });
		} catch (Exception ex) {
			fileManager.rollbackFile("nc_fileDs", requestData);
			throw new BizRuntimeException(BaseConstants.UPLOAD_FAIL);
		}
	}
}