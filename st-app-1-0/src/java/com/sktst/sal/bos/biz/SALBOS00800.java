/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.sal.bos.biz;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.sal.sco.ejb.SCO;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordHeader;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordHeader;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-04-25 14:04:27</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class SALBOS00800 extends BaseBizUnit {

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
	public IDataSet getErrorCheckXls(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00800.getErrorCheckXls method start >>>");
		}

		IRecordSet xls = requestData.getRecordSet("ds_upLoadXlsList");
		Map mXls = null;
		Map mOnlineSaleProd = null;
		IRecord ir = null;
		String sSaveGubun = "I";

		for (int i = 0; i < xls.getRecordCount(); i++) {
			ir = xls.getRecord(i);
			mXls = xls.getRecordMap(i);
			String tSerNum = mXls.get("SER_NUM") == null ? "" : mXls.get(
					"SER_NUM").toString().replaceAll(" ", "");
			mXls.put("SAVE_GUBUN", sSaveGubun);
			if (tSerNum.equals("")) {
				ir.set("ERR_DESC", "관리번호가 없습니다.");
			} else {
				mOnlineSaleProd = (Map) queryForObject(
						"SALBOS00800.getOnlineSaleProdCheck", mXls, onlineCtx);
				if (mOnlineSaleProd == null) {
					ir.set("ERR_DESC", "해당 data가 없습니다.");
				}else{
					ir.set("STL_PLC", mOnlineSaleProd.get("STL_PLC").toString());
					ir.set("SER_NUM", mOnlineSaleProd.get("SER_NUM").toString());
					ir.set("PROD_CD", mOnlineSaleProd.get("PROD_CD").toString());
					ir.set("PROD_NM", mOnlineSaleProd.get("PROD_NM").toString());
					ir.set("COLOR_NM", mOnlineSaleProd.get("COLOR_NM").toString());
					ir.set("COLOR_CD", mOnlineSaleProd.get("COLOR_CD").toString());
					ir.set("SALE_AMT", mOnlineSaleProd.get("SALE_AMT").toString());
					ir.set("DIS_AMT", mOnlineSaleProd.get("DIS_AMT").toString());
					ir.set("MAR_AMT", mOnlineSaleProd.get("MAR_AMT").toString());
					ir.set("VAT_AMT", mOnlineSaleProd.get("VAT_AMT").toString());
					ir.set("OUT_MGMT_NO", mOnlineSaleProd.get("OUT_MGMT_NO") == null ? "" : mOnlineSaleProd.get(
					"OUT_MGMT_NO").toString());
					ir.set("GNRL_SALE_NO", mOnlineSaleProd.get("GNRL_SALE_NO") == null ? "" : mOnlineSaleProd.get(
					"GNRL_SALE_NO").toString());
					ir.set("EQP_ST", mOnlineSaleProd.get("EQP_ST").toString());
					ir.set("SALE_UNIT_PRC", mOnlineSaleProd.get("SALE_UNIT_PRC").toString());
				}
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(xls.getRecordCount()) });

		responseData.putRecordSet("ds_upLoadXlsList", xls);
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
	public IDataSet saveOnlineSaleProdXls(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("saveProdSale method start");
		}
		
		SCO sco = (SCO) lookupBizComponent("sktst.sal.SCO");	// 재고처리 
		
		IRecordSet xls = requestData.getRecordSet("ds_upLoadXlsList");
		Map mXls = null;
		Map mGnrlSale = new HashMap();
		IRecord ir = null;
		
		String sSaveGubun = "";
		String sGnrlSaleNo = "";
		String sGnrlSaleChgSeq = "";
		int insCnt = 0;
		for (int i = 0; i < xls.getRecordCount(); i++) {
			ir = xls.getRecord(i);
			mXls = xls.getRecordMap(i);
			//sSaveGubun = mXls.get("SAVE_GB").toString(); // I : 신규등록 / C : 판매취소
			sSaveGubun = "I";
			/**
			 * 판매 처리
			 */
			
			mGnrlSale.put("SALE_DTM", mXls.get("SALE_DT")+""+mXls.get("SALE_TM"));
			mGnrlSale.put("SALE_CHGRG_ID", mXls.get("SALE_CHGRG_ID"));
			mGnrlSale.put("STL_PLC", mXls.get("STL_PLC"));
			mGnrlSale.put("DIS_HLD_PLC", "11030");
			mGnrlSale.put("SALE_PLC", mXls.get("SALE_PLC"));
			mGnrlSale.put("RMKS", mXls.get("RMKS"));
			mGnrlSale.put("SALE_TYP_CD", "03");
			mGnrlSale.put("INDEN_NUM", mXls.get("INDEN_NUM"));
			mGnrlSale.put("OLD_SER_NUM", mXls.get("OLD_SER_NUM"));
			mGnrlSale.put("PROD_CD", mXls.get("PROD_CD"));
			mGnrlSale.put("COLOR_CD", mXls.get("COLOR_CD"));
			mGnrlSale.put("SER_NUM", mXls.get("SER_NUM"));
			mGnrlSale.put("SALE_QTY", 1);
			
			
			if("I".equals(sSaveGubun)){			// 신규등록
				// 일련번호 채번
				mXls.put("sale_gubun", "S"); 				// 일반판매번호 구분자 
				Map saleNo = (Map) queryForObject("SALBOS00800.getGnrlSaleNo", mXls, onlineCtx);
				sGnrlSaleNo = saleNo.get("GNRL_SALE_NO").toString();
				mGnrlSale.put("GNRL_SALE_NO", sGnrlSaleNo);
				
				mGnrlSale.put("GNRL_SALE_CHG_SEQ", "1"); 
				mGnrlSale.put("SALE_CHG_HST_CL", "01");	/* 판매변경이력구분 (ZSAL_C_00110 : 01:판매 , 08:판매취소) */
				mGnrlSale.put("SETTL_COND", "C");
				mGnrlSale.put("SALE_CNCL_YN", "N");
				
				mGnrlSale.put("CASH_PAY_AMT", mXls.get("CASH_PAY_AMT"));
				mGnrlSale.put("CRDTCRD_PAY_AMT1", mXls.get("CRDTCRD_PAY_AMT1"));
				mGnrlSale.put("CRDTCRD_PAY_AMT2", mXls.get("CRDTCRD_PAY_AMT2"));
				mGnrlSale.put("DIS_AMT", mXls.get("DIS_AMT"));
				mGnrlSale.put("MAR_AMT", mXls.get("MAR_AMT"));
				mGnrlSale.put("CMMS_AMT", mXls.get("CMMS_AMT"));
				mGnrlSale.put("VAT_AMT", mXls.get("VAT_AMT"));
				mGnrlSale.put("SALE_AMT", mXls.get("SALE_AMT"));
				
			}else if("C".equals(sSaveGubun)){	// 판매취소
				mGnrlSale.put("GNRL_SALE_NO", mXls.get("GNRL_SALE_NO"));
				
				Map gnrlSaleChgSeq = (Map) queryForObject("SALBOS00800.getGnrlSaleChgSeq", mXls, onlineCtx);
				sGnrlSaleChgSeq = gnrlSaleChgSeq.get("GNRL_SALE_CHG_SEQ").toString();
				mGnrlSale.put("GNRL_SALE_CHG_SEQ", sGnrlSaleChgSeq);

				mXls.put("SALE_CHG_HST_CL", "08");	/* 판매변경이력구분 (ZSAL_C_00110 : 01:판매 , 08:판매취소) */
				mGnrlSale.put("SALE_CNCL_YN", "Y");
				mGnrlSale.put("CASH_PAY_AMT", 0);
				mGnrlSale.put("CRDTCRD_PAY_AMT1", 0);
				mGnrlSale.put("CRDTCRD_PAY_AMT2", 0);
				mGnrlSale.put("DIS_AMT", 0);
				mGnrlSale.put("MAR_AMT", 0);
				mGnrlSale.put("CMMS_AMT", 0);
				mGnrlSale.put("VAT_AMT", 0);
				mGnrlSale.put("SALE_AMT", 0);
				
			}
			
			//일반판매
			insert("SALBOS00800.addGnrlSale", mGnrlSale, onlineCtx);
			//단말기판매
			insert("SALBOS00800.addEquipmentSale", mGnrlSale, onlineCtx);
			//수납정보
			insert("SALBOS00800.addPayment", mGnrlSale, onlineCtx);
			//매출판매
			insert("SALBOS00800.addSaleAmt", mGnrlSale, onlineCtx);
			
			/**
			 * 재고 처리
			 */
			log.debug("$$$$$$ = 재고 처리  @@@@@@@@@@@@@@@ ==> ");
	//			상품판매 재고처리
			Map mDis = new HashMap();
			IDataSet oDsDisSale = new DataSet();
	
			/*
			 * mMaster 필드구성
			 * 	  #out_mgmt_no#			: 공통생성
				  #gnrl_sale_no#		: requestData.getField("gnrl_sale_no")
				  #gnrl_sale_chg_seq#	: requestData.getField("gnrl_sale_chg_seq")
				  #sale_dt#				: requestData.getField("sale_dtm")
				  #sale_plc#			: requestData.getField("sale_plc")
				  #rmks#				: requestData.getField("rmks")
			 */
			mDis.put("OUT_MGMT_NO", "");
			mDis.put("OUT_SEQ", "");
			mDis.put("GNRL_SALE_NO", mGnrlSale.get("GNRL_SALE_NO"));
			mDis.put("GNRL_SALE_CHG_SEQ", mGnrlSale.get("GNRL_SALE_CHG_SEQ"));
			mDis.put("SALE_DTM", mGnrlSale.get("SALE_DTM"));
			mDis.put("SALE_PLC", mGnrlSale.get("SALE_PLC"));
			mDis.put("RMKS", mGnrlSale.get("RMKS"));
			
			log.debug("$$$$$$ = 재고 처리 1  @@@@@@@@@@@@@@@ ==> ");
			oDsDisSale.putFieldObjectMap(mDis);
			oDsDisSale.putFieldObjectMap(mGnrlSale);
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
			IRecordSet oDsDisProd = new RecordSet("itemList");
			
			
			log.debug("$$$$$$ = xls.getHeaderCount()  @@@@@@@@@@@@@@@ ==> " + xls.getHeaderCount());
			for(int k = 0; k < xls.getHeaderCount(); k++){
				IRecordHeader iHeader = new RecordHeader(xls.getHeader(k).toString());
				oDsDisProd.addHeader(iHeader);
				log.debug("$$$$$$ = oDsDisProd.getHeader(k)  @@@@@@@@@@@@@@@ ==> " + oDsDisProd.getHeader(k));
			}
			oDsDisProd.addRecord(xls.getRecord(i));
			oDsDisSale.putRecordSet("itemList", oDsDisProd);

			if("I".equals(sSaveGubun)){			// 신규등록
				oDsDisSale = sco.addDisSaleOut(oDsDisSale, onlineCtx);
			}else if("C".equals(sSaveGubun)){	// 판매취소
				log.debug("$$$$$$ = 재고 처리 3  @@@@@@@@@@@@@@@ ==> " + oDsDisSale);
				oDsDisSale = sco.addDisSaleIn(oDsDisSale, onlineCtx);
				
			}
			
			insCnt ++ ;
		}
		
		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"Insert:" + insCnt });

		return result;
	}

}