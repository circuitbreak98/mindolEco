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
import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;
import com.sktst.sal.sco.ejb.SCO;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-10-18 13:38:21</li>
 * </ul>
 *
 * @author 김만수 (kimmansoo)
 */
public class SALBOS00200 extends com.sktst.common.base.BaseBizUnit {

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
	public IDataSet getDealInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00200.getDealInfo method start.");
		}

		IRecordSet rs = queryForRecordSet("SALBOS00200.getDealInfo",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("List", rs);

		return result;
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
	public IDataSet getProdInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00200.getProdInfo method start.");
		}

		IRecordSet rs = queryForRecordSet("SALBOS00200.getProdInfo",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("List", rs);

		return result;
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
	public IDataSet getTdisProdList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00200.getTdisProdList method start.");
		}

		IRecordSet rs = queryForRecordSet("SALBOS00200.getTdisProdList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("List");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("List", rs);

		return result;
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
	public IDataSet saveSalePaymentUpdate(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception  {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBOS00200.saveSalePaymentUpdate method start");
		}

		SCO sco = (SCO) lookupBizComponent("sktst.sal.SCO");	// 재고처리
		
		Map mMap = requestData.getFieldMap();
		
		Map mSeq = null;
		IDataSet responseData = null;

		IRecordSet rsSeq = queryForRecordSet("SALBOS00200.getMaxGnrlSaleChgSeq"
				, mMap, onlineCtx);
		
		mSeq = rsSeq.getRecordMap(0);
		String seq1 = mSeq.get("gnrl_sale_chg_seq").toString();
		String seq2 = mMap.get("gnrl_sale_chg_seq").toString();
		
		if(!seq1.equals(seq2))
		{
			responseData = DataSetFactory.createWithOKResultMessage(
					BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "0" });
			
			responseData.putRecordSet("ds_output", rsSeq);
		}
		else
		{
			
			try
			{
				insert("SALBOS00200.saveSaleRmksUpdate", mMap, onlineCtx);
				insert("SALBOS00200.saveSalePaymentUpdate", mMap, onlineCtx);
				insert("SALBOS00200.saveSaleAmtUpdate", mMap, onlineCtx);
				insert("SALBOS00200.saveEquiUpdate", mMap, onlineCtx);
				
				int seq = getIntMem(mMap.get("gnrl_sale_chg_seq"));	
				String outMgmtNo = getOutMgmtNo(mMap, onlineCtx);
				
				mMap.put("gnrl_sale_chg_seq", (seq + 1)); //gnrl_sale_chg_seq
				mMap.put("out_mgmt_no", outMgmtNo);				
				
				IDataSet oDsDisSale = new DataSet();
				oDsDisSale.putFieldObjectMap(mMap);
				
				oDsDisSale = sco.updateDisSaleOut(oDsDisSale, onlineCtx);
				
				responseData = DataSetFactory.createWithOKResultMessage(
						BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });
				
				rsSeq = queryForRecordSet("SALBOS00200.getMaxGnrlSaleChgSeq"
					, mMap, onlineCtx);
				
				responseData.putRecordSet("ds_output", rsSeq);
				
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
		
		return responseData;
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
			log.debug("SALBOS00200.getOutMgmtNo method start");
		}

		IRecordSet rs = queryForRecordSet("SALBOS00200.getOutMgmtNo", mMap, onlineCtx);

		if (rs == null) {
			return "0";
		}

		return rs.get(0, 0);
	}
}