/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.cst.adv.biz;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.dis.inn.ejb.INN;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-01-03 10:36:57</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class CSTPRC01000 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getPrchsPolList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("CSTPRC01000.getPrchsPolList",
				requestData.getFieldMap(), onlineCtx);

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_list", rs);

		IRecordSet dtl = queryForRecordSet("CSTPRC01000.getPrchsDtlPolList",
				requestData.getFieldMap(), onlineCtx);

		responseData.putRecordSet("ds_prchs_d", dtl);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updatePrchsPolOp(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		IRecordSet rs1 = requestData.getRecordSet("ds_list");
		IRecordSet rs2 = requestData.getRecordSet("ds_prchs_d");
		IRecordSet rs3 = requestData.getRecordSet("ds_inMaster");
		IRecordSet rs4 = requestData.getRecordSet("ds_prodSerNumList");
		IRecordSet rs5 = requestData.getRecordSet("ds_consult_m");

		IRecord iMaster  = null;
		IRecord iProd  = null;

		Map minData = null; 
		minData = (Map) queryForObject("CSTPRC00100.getTime",
				requestData.getFieldMap(), onlineCtx);
		
		int iTime = Integer.parseInt(minData.get("CTIME").toString());
			
		if (iTime >= 160000 ) {
			if (minData.get("CUSER").toString().equals("SC02400169") ||
				minData.get("CUSER").toString().equals("SC02103834") ||
				minData.get("CUSER").toString().equals("SC02101783") ||
				minData.get("CUSER").toString().equals("OPS0040002")) {
			} else {
				throw new BizRuntimeException("***** 16시 이후 처리가 불가능합니다. *****");
			}
		}
		
		String sDate = "";
		int inx = 1;
		
		if (rs1 != null) {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			long currentTime = System.currentTimeMillis();
			sDate = df.format(new Date(currentTime));	
			
			for (int i = 0; i < rs1.getRecordCount(); i++) {	
				if (rs1.get(i, "PRC_CHECK").equals("1")) {
					iMaster     = rs3.getRecord(0);
					iMaster.put("in_schd_dt", sDate);
					iMaster.put("pos_agency_id", rs1.get(i, "POS_AGENCY").toString());
					iMaster.put("prchs_plc_id", rs1.get(i, "CON_PLC_CD").toString());
					iMaster.put("in_cl", "114");
					iMaster.put("in_fix_qty", "1");
					iMaster.put("PRCHS_MGMT_NO", rs1.get(i, "PRCHS_MGMT_NO").toString());
					
					rs3.setRecord(0, iMaster);
							
					iProd     = rs4.getRecord(0);
					iProd.put("ser_num", rs1.get(i, "SER_NUM").toString());
					iProd.put("color_cd", rs1.get(i, "COLOR_CD").toString());
					iProd.put("prod_cd", rs1.get(i, "PROD_CD").toString());
					iProd.put("prod_cl", "1");
					iProd.put("in_qty", "1");
					iProd.put("in_amt", rs1.get(i, "PRCHS_AMT").toString());
					iProd.put("eqp_st", rs1.get(i, "EQP_ST").toString());
					iProd.put("PRCHS_MGMT_NO", rs1.get(i, "PRCHS_MGMT_NO").toString());
					iProd.put("nc_rec_status", "insert");
	
					rs4.setRecord(0, iProd);
					rs5.setRecord(0, iProd);
					requestData.putRecordSet("ds_consult_m", rs5);
					
					for (int j = 0; j < rs2.getRecordCount(); j++) {
						if (rs1.get(i, "PRCHS_MGMT_NO").equals(rs2.get(j, "PRCHS_MGMT_NO").toString())) {
//log.debug(" j : " + j + "  Yes   " + rs2.get(j, "PRCHS_MGMT_NO").toString());								
							rs4.addRecord(rs2.getRecord(j));
						}
					}
					requestData.putRecordSet("ds_inMaster", rs3);
					requestData.putRecordSet("ds_prodSerNumList", rs4);
//log.debug("requestData : " + requestData);
						
					update("CSTPRC01000.updatePrchsPolOp", rs1.getRecord(i),
							onlineCtx);
						
					INN inn = (INN) lookupBizComponent("sktst.dis.INN");
					if (rs1.get(i, "POL_OP_YN").equals("N")) {
						try {
							IDataSet dsSaveInn = inn.saveInn(requestData, onlineCtx);
						} catch (RemoteException rEx) {
							if (log.isDebugEnabled()) {
								log.debug("savePrchs insert RemoteException rEx: "
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
								log.debug("savePrchs insert Exception ex: " + ex.getMessage());
							}
							throw ex;
						}
						
						rs3.initRecord();
						rs3.newRecord();
						rs4.initRecord();
						rs4.newRecord();
					} else {
						if (rs1.get(i, "POL_OP_YN").equals("Y")){
							try {
								IDataSet dsInnCncl = inn.saveInnCncl(requestData, onlineCtx);
							} catch (RemoteException rEx) {
								if (log.isDebugEnabled()) {
									log.debug("savePrchs delete RemoteException rEx: "
										+ rEx.getMessage());
								}
								throw new BizRuntimeException(rEx.getCause().toString()
											.replace("nexcore.framework.core.exception.BizRuntimeException:",""));

							} catch (Exception ex) {
								if (log.isDebugEnabled()) {
								log.debug("savePrchs delete Exception ex: " + ex.getMessage());
								}
								throw ex;
							}
						}	
					}
				}
			}
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });
	}

}