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
import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.dis.inn.ejb.INN;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-09-09 11:25:47</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class CSTPRC00500 extends BaseBizUnit {

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
	public IDataSet getPrchsBuyList(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("CSTPRC00500.getPrchsBuyList",
				requestData.getFieldMap());

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_list", rs);
		
//		IRecordSet dtl = queryForRecordSet("CSTPRC00500.getPrchsDtlList",
//				requestData.getFieldMap());

//		responseData.putRecordSet("ds_prchs_d", dtl);
		
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");
		
		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);		
		IRecordSet iSet = itemp.getRecordSet("cptItem");
		
		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "ds_list");			// 복호화 할 데이터셋 명
		rec.add("col_name1", "ACC_NO"); 		// 복호화 컬럼1
		rec.add("col_name2", "TEL_NO"); 		// 복호화 컬럼2
		rec.add("col_name7", "RES_NO"); 		// col_name7 :: 주민번호 앞 6자리 잘라줌....
		iSet.addRecord(rec);
		
		responseData.putRecordSet("cptItem", iSet);
		
		IDataSet rsData = bco.decode(responseData, onlineCtx);

		return rsData;
		
//		return responseData;
		
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
	public IDataSet updatePrchsCustYn(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		IRecordSet rs1 = requestData.getRecordSet("ds_list");
		IRecordSet rs2 = requestData.getRecordSet("ds_prchs_d");
		IRecordSet rs3 = requestData.getRecordSet("ds_inMaster");
		IRecordSet rs4 = requestData.getRecordSet("ds_prodSerNumList");
		IRecordSet rs5 = requestData.getRecordSet("ds_consult_m");

		IRecord iMaster  = null;
		IRecord iProd  = null;
		
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
						
					update("CSTPRC00500.updatePrchsCustYn", rs1.getRecord(i),
							onlineCtx);
						
					INN inn = (INN) lookupBizComponent("sktst.dis.INN");
					if (rs1.get(i, "CUST_YN").equals("Y")) {
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
						if (rs1.get(i, "CUST_YN_T").equals("Y")){
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