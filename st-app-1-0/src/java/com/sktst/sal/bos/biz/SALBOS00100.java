/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.sal.bos.biz;

import java.rmi.RemoteException;
import java.sql.Date;
import java.text.SimpleDateFormat;
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
import com.sktst.sal.sco.ejb.SCO;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-10-17 11:01:38</li>
 * </ul>
 *
 * @author 김만수 (kimmansoo)
 */
public class SALBOS00100 extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveOnlineSaleInsert(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00100.saveOnlineSaleInsert method start");
		}
		
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
		rec.add("ds_name"	, "ds_list");		// 암호화 할 데이터셋 명
		rec.add("col_name1"	, "CUST_BIZ_NUM"); 	// 암호화 컬럼1
		rec.add("col_name2"	, "CUST_TEL_NO");	// 암호화 컬럼2
		iSet.addRecord(rec);
		
		requestData.putRecordSet("cptItem", iSet);

		IDataSet rsData = bco.encode(requestData, onlineCtx);
		/*
		 * 암호화 종료 
		 */
		
		IRecordSet xls = rsData.getRecordSet("ds_list");
		
		SCO sco = (SCO) lookupBizComponent("sktst.sal.SCO");	// 재고처리 			
				
		String sDate = "";
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		
		long currentTime = System.currentTimeMillis();
		sDate = df.format(new Date(currentTime));

		//log.debug(xls.getRecordCount() + "");

		Map mMgmt = null;
		Map mSeq = null;
		Map mMif = null;

		if (xls != null) {
			
			/*
			for (int i = 0; i < xls.getRecordCount(); i++) {
				mMgmt = xls.getRecordMap(i);
				
				String seq = getMaxSeq(mMgmt, onlineCtx);
				mMgmt.put("seq", seq);
				
				insert("SALBOS00100.saveIfOnlineSaleInsert", mMgmt, onlineCtx);
			}
			*/
			
			for (int i = 0; i < xls.getRecordCount(); i++) {
				mMif = xls.getRecordMap(i);
				mMgmt = xls.getRecordMap(i);
				log.debug("for " + mMif.get("CUST_BIZ_NUM"));
				
				// 인터페이스 테이블 데이터 중복시 순서 번호 부여
				String seq = getMaxSeq(mMif, onlineCtx);
				mMif.put("seq", seq);
				
				String sSaleCnclYn = "N";
				String sSaleCnclDtm = "";
				String sSaleCnclCl = "";
				
				String ifRecTyp = mMif.get("if_rec_typ") == null ? "01" : mMif.get("if_rec_typ").toString();
				
				if(!ifRecTyp.equals("01"))
				{
					sSaleCnclYn = "Y";
					sSaleCnclDtm = sDate;
					sSaleCnclCl = "1";
				}
				
				mMif.put("sale_cncl_yn", sSaleCnclYn);
				mMif.put("sale_cncl_dtm", sSaleCnclDtm);
				mMif.put("sale_cncl_cl", sSaleCnclCl);
				
				mMgmt.put("sale_cncl_yn", sSaleCnclYn);
				mMgmt.put("sale_cncl_dtm", sSaleCnclDtm);
				mMgmt.put("sale_cncl_cl", sSaleCnclCl);
				
				insert("SALBOS00100.saveIfOnlineSaleInsert", mMif, onlineCtx);				
				
				
				if ("1".equals(mMgmt.get("prod_yn").toString())) {
					
						
					// 일반판매 테이블  주문번호 로 판매/판매취소 정보 조회
					IRecordSet iData = getOrderNum(mMif, onlineCtx);
					
					if(iData == null || iData.getRecordCount() == 0)
					{	
						
						try 
						{
							if (mMgmt.get("gnrl_sale_no") == null
									|| "".equals(mMgmt.get("gnrl_sale_no").toString())) {
		
								mSeq = (Map) queryForObject(
										"SALBOS00100.getSaleMgmtSeq", mMgmt, onlineCtx);
		
								log.debug(mSeq.get("saleMgmtNo"));
		
								mMgmt.put("gnrl_sale_no", mSeq.get("SALEMGMTNO"));
							}
		
							if (mMgmt.get("gnrl_sale_chg_seq") == null
									|| "".equals(mMgmt.get("gnrl_sale_chg_seq").toString())) {
								mMgmt.put("gnrl_sale_chg_seq", "1");
							}
		
							mMgmt.put("SALE_TYP_CD", "03");
	
							insert("SALBOS00100.saveOnlineSaleInsert", mMgmt, onlineCtx);
							insert("SALBOS00100.saveOnlinePayInsert", mMgmt, onlineCtx);
							insert("SALBOS00100.saveOnlineCashInsert", mMgmt, onlineCtx);
							insert("SALBOS00100.saveOnlineEquiInsert", mMgmt, onlineCtx);
							
							/*
							 * mMaster 필드구성
							  #gnrl_sale_no#		: requestData.getField("gnrl_sale_no")
							  #gnrl_sale_chg_seq#	: requestData.getField("gnrl_sale_chg_seq")
							  #sale_dt#				: requestData.getField("sale_dtm")
							  #sale_plc#			: requestData.getField("sale_plc")
							  #rmks#				: requestData.getField("rmks")
							 */
							
							
							IDataSet oDsDisSale = new DataSet();
							oDsDisSale.putFieldObjectMap(mMgmt);	
							
							IRecordSet rs = queryForRecordSet("SALBOS00100.getItemList",
									mMgmt, onlineCtx);
							
							oDsDisSale.putRecordSet("itemList", rs);
							
							oDsDisSale = sco.addDisSaleOut(oDsDisSale, onlineCtx);
							
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
					}
					else if(iData != null && iData.getRecordCount() > 0 && !ifRecTyp.equals("01"))
					{	
						try 
						{
							Map mRows = iData.getRecordMap(0);							
							
							mRows.put("sale_cncl_yn", sSaleCnclYn);
							mRows.put("sale_cncl_dtm", sSaleCnclDtm);
							mRows.put("sale_cncl_cl", sSaleCnclCl);
							
							mRows.put("ser_num", mMgmt.get("PROD_SER_NUM"));
							mRows.put("color_cd", mMgmt.get("EQP_COLOR_CD"));
							mRows.put("prod_cd", mMgmt.get("EQP_MDL_CD"));
							mRows.put("prod_ser_num", mMgmt.get("PROD_SER_NUM"));
							mRows.put("eqp_color_cd", mMgmt.get("EQP_COLOR_CD"));
							mRows.put("eqp_mdl_cd", mMgmt.get("EQP_MDL_CD"));
							
							insert("SALBOS00100.saveSaleRmksUpdate", mRows, onlineCtx);
							insert("SALBOS00100.saveSalePaymentUpdate", mRows, onlineCtx);
							insert("SALBOS00100.saveSaleAmtUpdate", mRows, onlineCtx);
							insert("SALBOS00100.saveEquiUpdate", mRows, onlineCtx);							
							
							int chgSeq = getIntMem(mRows.get("gnrl_sale_chg_seq"));	
							String outMgmtNo = getOutMgmtNo(mRows, onlineCtx);
							
							mRows.put("gnrl_sale_chg_seq", (chgSeq + 1));
							mRows.put("out_mgmt_no", outMgmtNo);	
							
							IDataSet oDsDisSale = new DataSet();
							oDsDisSale.putFieldObjectMap(mRows);	
							
							IRecordSet rs = queryForRecordSet("SALBOS00100.getItemList",
									mRows, onlineCtx);
							
							oDsDisSale.putRecordSet("itemList", rs);
							
							oDsDisSale = sco.addDisSaleIn(oDsDisSale, onlineCtx);
							
						} catch (RemoteException rEx) {
							if (log.isDebugEnabled()) {
								log.debug("addDisSaleIn insert RemoteException rEx: "
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
								log.debug("addDisSaleIn insert Exception ex: " + ex.getMessage());
							}
							throw ex;
						}
					}					
				}	
							
			}
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });
		
	}

	/**
	 * 
	 * 
	 * @author 김만수 (kimmansoo)
	 * @param mgmtCd
	 * @param requestData
	 * @param onlineCtx
	 * @return
	 */
	public String getSaleMgmtNo(String mgmtCd, IDataSet requestData,
			IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);

		if (log.isDebugEnabled()) {
			log.debug("SALBOS00100.getSaleMgmtNo method start");
		}

		Map map = (Map) queryForObject("SALBOS00100.getSaleMgmtSeq", mgmtCd);

		return map.get("saleMgmtNo").toString();
	}

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getlfOnlineSaleList(IDataSet requestData,
			IOnlineContext onlineCtx)  throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00100.getlfOnlineSaleList method start");
		}

		IRecordSet rs = queryForRecordSet("SALBOS00100.getlfOnlineSaleList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("List", rs);	
		
		/*
		 * 복호화 시작
		 * 복호화 시작 -> 복호화 종료 소스 모두 복사 
		 * 변경 : 복호화 할 데이터셋 명/복호화 컬럼1/복호화 컬럼2
		 * 복호화 컬럼은 최대 6개 까지 확장 가능 : col_name1 ~ 6
		 */
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");
		
		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);		
		IRecordSet iSet = itemp.getRecordSet("cptItem");
		
		IRecord rec = iSet.newRecord();
		rec.add("ds_name"	, "List");			// 복호화 할 데이터셋 명
		rec.add("col_name1"	, "CUST_BIZ_NUM"); 	// 복호화 컬럼1
		rec.add("col_name2"	, "CUST_TEL_NO");	// 복호화 컬럼2
		iSet.addRecord(rec);
		
		responseData.putRecordSet("cptItem", iSet);
		
		IDataSet rsData = bco.decode(responseData, onlineCtx);		
		/*
		 * 복호화 종료
		 */
		
		return rsData;		
	}
	
	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 * @param onlineCtx
	 * @return 
	 */
	private String getMaxSeq(Map mMap, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00100.getMaxSeq method start");
		}

		IRecordSet rs = queryForRecordSet("SALBOS00100.getMaxSeq", mMap, onlineCtx);

		if (rs == null) {
			return "1";
		}

		return rs.get(0, 0);
		//return responseData;
	}
	
	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 * @param onlineCtx
	 * @return 
	 */
	private IRecordSet getOrderNum(Map mMap, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00100.getOrderNum method start");
		}

		IRecordSet rs = queryForRecordSet("SALBOS00100.getOrderNum", mMap, onlineCtx);

		if (rs == null) {
			return null;
		}

		return rs;
	}
	
	/**
	 * 
	 * 
	 * @author 김만수 (kimmansoo)
	 * @param Object
	 * @return int
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
	 * @author 김만수 (kimmansoo)
	 * @param requestData
	 * @param onlineCtx
	 * @return string
	 */
	public String getOutMgmtNo(Map mMap, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00100.getOutMgmtNo method start");
		}

		IRecordSet rs = queryForRecordSet("SALBOS00100.getOutMgmtNo", mMap, onlineCtx);

		if (rs == null) {
			return "0";
		}

		return rs.get(0, 0);
	}
}