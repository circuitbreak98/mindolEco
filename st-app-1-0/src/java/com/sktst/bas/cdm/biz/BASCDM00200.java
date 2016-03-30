/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.cdm.biz;

import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;
import nexcore.framework.integration.db.NoRecordFoundException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : 공통 코드 등록 </li>
 * <li>작성일 : 2009-01-28 16:40:50</li>
 * </ul>
 *
 * @author 조민지 (jominji)
 */
public class BASCDM00200 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 조민지 (jominji)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : comm_cd_id [공통코드ID] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : comm_cd_id [공통코드ID] 
	 *	- field : comm_cd_nm [공통코드] 
	 *	- field : comm_cd_desc [공통코드설명] 
	 *	- field : sup_comm_cd_id [슈퍼공통코드ID] 
	 *	- field : sup_comm_cd_nm [슈퍼공통코드] 
	 */
	public IDataSet getCommonCode(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASCDM00200.getCommonCode method start");
		}

		Map map = (Map) queryForObject("BASCDM00200.getCommonCode", requestData
				.getFieldMap());

		int iCnt = 1;
		
		if (map == null) {
			map = new HashMap();
			iCnt = 0;
		}
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String.valueOf(iCnt) });
		responseData.putFieldMap(map);
		return responseData;

	}

	/**
	 * 
	 *
	 * @author 조민지 (jominji)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : comm_cd_id [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : ds_comm_cd_dtl
	 *		- field : comm_cd_val [코드값] 
	 *		- field : comm_cd_val_nm [코드값명] 
	 *		- field : comm_cd_val_desc [코드값설명] 
	 *		- field : prt_seq [출력순서] 
	 *		- field : eff_sta_dt [유효시작일] 
	 *		- field : eff_end_dt [유효종료일] 
	 *		- field : sub_comm_cd_id [필드7] 
	 */
	public IDataSet getCommonCodeDetail(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASCDM00200.getCommonCodeDetail method start >>>");
		}
		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("BASCDM00200.getCommonCodeDetail",
				requestData.getFieldMap());

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_comm_cd_dtl", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 조민지 (jominji)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : comm_cd_id [공통코드ID] 
	 *	- field : comm_cd_nm [공통코드] 
	 *	- field : comm_cd_desc [공통코드설명] 
	 *	- field : sup_comm_cd_id [슈퍼공통코드ID] 
	 *	- field : del_yn [삭제여부] 
	 *	- field : upd_cnt [update_count] 
	 *	- field : ins_dtm [입력일시] 
	 *	- field : ins_user_id [입력자] 
	 *	- field : mod_dtm [수정일시] 
	 *	- field : mod_user_id [수정자] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveCommonCodeList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASCDM00200.saveCommonCodeList method start");
		}

		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;

		IRecordSet rsHeader = requestData.getRecordSet("ds_comm_cd_lst");
		int rsCount = rsHeader.getRecordCount();
		if (rsHeader != null && rsCount > 0) {
	
			for (int i = 0; i < rsCount; i++) 
			{
				if (INSERT_FLAG.equalsIgnoreCase(rsHeader.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					insert("BASCDM00200.addCommonCodeList", rsHeader.getRecord(i),
							onlineCtx);
					insertCount++;
				} else if (UPDATE_FLAG.equalsIgnoreCase(rsHeader.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					updateCount = updateCount
							+ update("BASCDM00200.updateCommonCodeList", rsHeader
									.getRecord(i), onlineCtx);
				}else if (DELETE_FLAG.equalsIgnoreCase(rsHeader.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					
					deleteCount = deleteCount+ delete("BASCDM00200.deleteCommonCodeHeader",rsHeader.getRecord(i), onlineCtx);
					
				}
				
			}
			cudAllCount = insertCount + updateCount+ deleteCount;
			
			if (cudAllCount < 1) {
				throw new NoRecordAffectedException(
						BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
			}
		}



		IRecordSet rsDetail = requestData.getRecordSet("comm_cd_dtl");
	
		int rsDetailCount = rsDetail.getRecordCount();
		String sNoFlag = "N";
		
		if (rsDetail != null && rsDetailCount > 0) {
			
			Map mSubCheckMap = null;
			Map mMstCheckMap = null;
			
			for (int i = 0; i < rsDetailCount; i++) {
				
				if (INSERT_FLAG.equalsIgnoreCase(rsDetail.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					insert("BASCDM00200.addCommonCodeDetail", rsDetail.getRecord(i), onlineCtx);
					insertCount++;
					/*
					if("ZADM_C_00010".compareTo(rsDetail.getRecord(i).get("comm_cd_id")) == 0)
					{
						try {
						CDM cdm = (CDM) lookupBizComponent("sktst.bas.CDM");
						cdm.saveCommonCodeList(requestData, onlineCtx);
						} catch (RemoteException rEx) {
							if (log.isDebugEnabled()) {
								log.debug("saveCommonCodeList RemoteException : " + rEx.getMessage());
							}
						} catch (Exception ex) {
							if (log.isDebugEnabled()) {
								log.debug("saveCommonCodeList Exception : " + ex.getMessage());
							}
						}
					}
					*/
					
				}else if (UPDATE_FLAG.equalsIgnoreCase(rsDetail.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					updateCount = updateCount
							+ update("BASCDM00200.updateCommonCodeDetail",rsDetail.getRecord(i), onlineCtx);
				}else if (DELETE_FLAG.equalsIgnoreCase(rsDetail.getRecord(i).get(
							CUD_FLAG_PARAM))) {
					 
					// 자신에 속한 하위 공통 코드가 있는 경우.
					mSubCheckMap = (Map) queryForObject("BASCDM00200.getSubCheck", rsDetail.getRecord(i), onlineCtx);
					if (mSubCheckMap == null) {
						throw new NoRecordFoundException();
					}		
				
					if(mSubCheckMap.get("SUB_COMM_CD_ID").equals(sNoFlag)){
						// 해당 코드에 하위공통코드가 존재 하여 삭제 할 수 없습니다.
						throw new BizRuntimeException("PSMW3001");
					}
					 
					    // 자신이 다른 코드에 하위 공통 코드로 등록 되어 있는 경우.					 
					mMstCheckMap = (Map) queryForObject("BASCDM00200.getMasterCheck", rsDetail.getRecord(i), onlineCtx);
					if (mMstCheckMap != null) {
						if(mMstCheckMap.get("COMM_CD_ID").equals(sNoFlag)){
							// 해당 코드가 다른 코드에 하위공통코드로 등록 되어 있어 삭제 할 수 없습니다.
							throw new BizRuntimeException("PSMW3002");
						}	
					}	
					
					deleteCount = deleteCount+ delete("BASCDM00200.deleteCommonCodeDetail",rsDetail.getRecord(i), onlineCtx);
				}
			}

			cudAllCount = insertCount + updateCount + deleteCount;
			
			if (cudAllCount < 1) {
				throw new NoRecordAffectedException(
						BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
			}
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_SIMPLE_MESSAGE_ID, new String[] {"" });
	}
}
