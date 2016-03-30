/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.atr.biz;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.common.user.PsLoginUserInfo;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-18 14:13:27</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class DISATR00200 extends BaseBizUnit {

	/**
	 * 비고조회
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 출고관리번호별 비고를 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : out_mgmt_no [출고관리번호] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : rmks [비고] 
	 */
	public IDataSet getRmksInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		String cnt = "1";

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getRmksInfo method start");
		}

		Map map = (Map) queryForObject("DISATR00200.selectRmksInfo",
				requestData.getFieldMap(), onlineCtx);

		if (map == null) {
			cnt = "0";
			map = new HashMap();
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { cnt });

		result.putFieldObjectMap(map);

		return result;
	}

	/**
	 * 비고저장
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 출고관리번호별 비고를 저장한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : out_mgmt_no [출고관리번호] 
	 *	- field : rmks [비고] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveRmksInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		int cnt = 0;

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveRmksInfo method start");
		}

		cnt = update("DISATR00200.updateRmksInfo", requestData.getFieldMap(),
				onlineCtx);

		if (cnt < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { Integer
						.toString(cnt) });

		return result;
	}

	/**
	 * 이동 출고 지시 등록 조회
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 이동 출고 지시 등록 내역을 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : out_mgmt_no [출고관리번호] 
	 *	- field : out_schd_dt [출고예정일자] 
	 *	- field : out_plc_id [출고ID] 
	 *	- field : in_plc_id [입고ID] 
	 *	- field : out_fix_yn [출고확정여부] 
	 *	- field : in_fix_yn [입고확정여부] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : movRegList
	 *		- field : out_mgmt_no [출고관리번호] 
	 *		- field : prod_cl [출고구분] 
	 *		- field : mfact_id [제조사] 
	 *		- field : color_cd [색상] 
	 *		- field : mov_out_qty [이동출고수량] 
	 *		- field : rmks [비고] 
	 */
	public IDataSet getMovRegList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("getMovRegList method start");
		}

		IRecordSet rs = queryForRecordSet("DISATR00200.selectMovReg",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("MovRegList");
		}
//log.trace("====> "+rs.getRecordCount());
		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("MovRegList", rs);

		return result;
	}

	/**
	 * 이동 출고 등록
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 이동 출고 시킬 재고 리스트를 DB에 저장, 등록, 삭제한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : setMovRegList
	 *		- field : act_flag [업무처리구분] 
	 *		- field : proc_flag [트랜잭션처리] 
	 *		- field : out_mgmt_no [출고관리번호] 
	 *		- field : out_schd_dt [출고예정일자] 
	 *		- field : out_plc_id [출고처ID] 
	 *		- field : in_plc_id [입고처ID] 
	 *		- field : out_cl [출고구분] 
	 *		- field : rmks [비고] 
	 *		- field : prod_cl [상품구분] 
	 *		- field : prod_cd [상품코드] 
	 *		- field : color_cd [색상코드] 
	 *		- field : bad_yn [불량여부] 
	 *		- field : dis_st [재고상태] 
	 *		- field : ser_num [일련번호] 
	 *		- field : mov_cnt [이동출고수량] 
	 *		- field : mov_seq [이동순번] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")	
	public IDataSet saveMovRegList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("saveMovRegList method start");
		}

		int insCnt = 0;
		int uptCnt = 0;
		int delCnt = 0;

		Map mMov = null;
		Map mMovDtl = null;
		Map mRet = null;

		String procFlag = "";
		String strMgmtNo = "";
		String sSuccessCd = "S";
		String sCnt = "0";
		String sDate = "";
		
		// 현재 일시 추출
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		
		long currentTime = System.currentTimeMillis();
		sDate = df.format(new Date(currentTime));

		PsLoginUserInfo userInfo = (PsLoginUserInfo) onlineCtx.getUserInfo();

		IRecordSet rsActFlg = requestData.getRecordSet("ds_procFlag");

		if (rsActFlg != null) {
			mMov = rsActFlg.getRecordMap(0);
			
			procFlag = mMov.get("proc_flag").toString();
	
			strMgmtNo = mMov.get("out_mgmt_no") == null?"":
					mMov.get("out_mgmt_no").toString();
log.debug("out_mgmt_no1=======>"+strMgmtNo);
	
			// 상품출고 신규등록의 경우
			if (strMgmtNo == null || "".equals(strMgmtNo)) {
				strMgmtNo = getOutMgmtNo("MV", userInfo.getLoginId(), onlineCtx);
				mMov.put("out_mgmt_no", strMgmtNo);
				mMov.put("out_cl", "301");		// 출고구분:301(이동출고)
				
				insert("DISATR00200.addProdOut", mMov, onlineCtx);
			}
log.debug("out_mgmt_no2=======>"+strMgmtNo);

			IRecordSet rsMovList = requestData.getRecordSet("ds_inProdOrgList");
	
			if (rsMovList != null) {
				// 전체 삭제의 경우
				if ("AD".equalsIgnoreCase(procFlag)) {
					mMov.put("out_cl", "304");		// 출고구분:304(이동출고취소)
					// 상품출고 삭제처리
					update("DISATR00200.updateProdOutDel", mMov, onlineCtx);
					
					for (int i = 0; i < rsMovList.getRecordCount(); i++) {
						
						mMovDtl = getProMap(mMov, rsMovList.getRecordMap(i), onlineCtx);
						
						mMovDtl.put("dis_st", "01");		// 재고상태 : 01(가용)
						mMovDtl.put("inout_cl", "300");		// 입출고구분 : 300(재고이동)
						mMovDtl.put("inout_dtl_cl", "304");	// 입출고구분 : 304(이동출고취소)
						
						// 상품이동출고상세 삭제처리
						update("DISATR00200.updateProdMovOutDtlDel", mMovDtl, onlineCtx);
						
						// 상품입출고상세 삭제처리
						//update("DISATR00200.updateProdInOutHstDel", mMovDtl, onlineCtx);
						insert("DISATR00200.addProdInOutHst", mMovDtl, onlineCtx);
						
						// 재고이동처리전 입출고일자,상태 조회
						Map mLastInOutCl = (Map) queryForObject("DISATR00200.selectLastInOutCl",
								mMovDtl, onlineCtx);
						mMovDtl.put("inout_cl", mLastInOutCl.get("INOUT_CL"));
						mMovDtl.put("inout_dtl_cl", mLastInOutCl.get("INOUT_DTL_CL"));
						mMovDtl.put("inout_dt", mLastInOutCl.get("INOUT_DT"));
						
						// 상품재고 이동출고취소 처리
						mMovDtl.put("hld_plc_id", mMovDtl.get("out_plc_id"));
						update("DISATR00200.updateProdDis", mMovDtl, onlineCtx);
					}
				}
				else {
				
					for (int i = 0; i < rsMovList.getRecordCount(); i++) {
		
						mMovDtl = getProMap(mMov, rsMovList.getRecordMap(i), onlineCtx);
//log.debug(mMovDtl);
						// 삭제 
						if (DELETE_FLAG.equalsIgnoreCase(mMovDtl.get(CUD_FLAG_PARAM)
								.toString())) {
						
							mMovDtl.put("dis_st", "01");		// 재고상태 : 01(가용)
							mMovDtl.put("inout_cl", "300");		// 입출고구분 : 300(재고이동)
							mMovDtl.put("inout_dtl_cl", "304");	// 입출고구분 : 304(이동출고취소)
							
							// 상품이동출고상세 삭제처리
							update("DISATR00200.updateProdMovOutDtlDel", mMovDtl, onlineCtx);
							
							// 상품입출고상세 삭제처리
							//update("DISATR00200.updateProdInOutHstDel", mMovDtl, onlineCtx);
							insert("DISATR00200.addProdInOutHst", mMovDtl, onlineCtx);
							
							// 재고이동처리전 입출고일자,상태 조회
							Map mLastInOutCl = (Map) queryForObject("DISATR00200.selectLastInOutCl",
									mMovDtl, onlineCtx);
							mMovDtl.put("inout_cl", mLastInOutCl.get("INOUT_CL"));
							mMovDtl.put("inout_dtl_cl", mLastInOutCl.get("INOUT_DTL_CL"));
							mMovDtl.put("inout_dt", mLastInOutCl.get("INOUT_DT"));
						
							// 상품재고 이동출고취소 처리
							mMovDtl.put("hld_plc_id", mMovDtl.get("out_plc_id"));
							update("DISATR00200.updateProdDis", mMovDtl, onlineCtx);

							delCnt++;
						}
		
						// 입력
						if (INSERT_FLAG.equalsIgnoreCase(mMovDtl.get(CUD_FLAG_PARAM)
								.toString())) {
						
							mRet = null;
							sCnt = "0";

							mMovDtl.put("out_seq", insCnt+1);
							mMovDtl.put("dis_st", "02");		// 재고상태 : 02(비가용)
							mMovDtl.put("inout_cl", "300");		// 입출고구분 : 300(재고이동)
							mMovDtl.put("inout_dtl_cl", "301");	// 입출고상세구분 : 301(이동출고)
							mMovDtl.put("out_schd_dt", mMov.get("out_schd_dt"));
							mMovDtl.put("inout_dt", sDate);
							
							mRet = (Map) queryForObject(
									"DISATR00200.selectSalReg", mMovDtl, onlineCtx);
							sCnt = ((BigDecimal) mRet.get("CNT")).toString();
		
							if (!"0".equals(sCnt)) {
								// 출고지시일 이전에 등록된 일반 판매 건이 있습니다.
								throw new BizRuntimeException("PSMW5013", new String[] {
										mMovDtl.get("prod_nm").toString() + "(" + mMovDtl.get("prod_cd").toString() + ")", 
										mMovDtl.get("ser_num").toString()});
							}
							
							// 상품이동출고상세 추가
							insert("DISATR00200.addProdMovOutDtl", mMovDtl, onlineCtx);
							
							// 상품입출고상세 추가
							insert("DISATR00200.addProdInOutHst", mMovDtl, onlineCtx);
							
							// 상품재고 이동출고 처리
							mMovDtl.put("hld_plc_id", mMovDtl.get("in_plc_id"));
							update("DISATR00200.updateProdDis", mMovDtl, onlineCtx);
							
							insCnt++;
						}
		
//log.debug("mMovDtl==>" + i + "::" + mMovDtl.toString());
		
					}
//log.debug("@@@@ delCnt="+delCnt);
//log.debug("@@@@ mMov="+mMov);
					//출고취소건이 있는 경우, 
					//   : 출고건수와 입고건수가 일치하고 모두 입고확정확인 후  입고 마스터에 입고확정처리
					if((delCnt > 0)
						&& (mMov.get("out_mgmt_no") != null)
						&& (mMov.get("in_mgmt_no") != null)	
						&& (!"".equals(mMov.get("in_mgmt_no")))
					) {
log.debug("입고마스터 = "+mMov.get("in_mgmt_no")+mMov.get("out_mgmt_no"));
						update("DISATR00200.updateTdisInProdM", mMov, onlineCtx);
					}
				}
			}
		}

		
		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"Insert:" + insCnt, " Update:" + uptCnt,
						" Delete:" + delCnt });

		return result;
	}
	/**
	 * 입력 멥 생성
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 프로시저를 호출하기 위한 멥을 구성한다.
	 * @param defMap
	 *            기본 정보 멥
	 * @param orgMap
	 *            멥을 구성하기 위한 원본 멥
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 Map 객체
	 */
	@SuppressWarnings("unchecked")
	public Map getProMap(Map defMap, Map orgMap, IOnlineContext onlineCtx) {

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
		retMap.put("act_flag", "S");

		retMap.put("out_mgmt_no", defMap.get("out_mgmt_no") == null ? ""
				: defMap.get("out_mgmt_no").toString());
		retMap.put("user_id", defMap.get("user_id") == null ? userInfo
				.getLoginId() : defMap.get("user_id").toString());
		retMap.put("out_schd_dt", defMap.get("out_schd_dt") == null ? ""
				: defMap.get("out_schd_dt").toString());
		retMap.put("out_plc_id", defMap.get("out_plc_id") == null ? "" : defMap
				.get("out_plc_id").toString());
		retMap.put("in_plc_id", defMap.get("in_plc_id") == null ? "" : defMap
				.get("in_plc_id").toString());
		retMap.put("mov_out_qty", getIntMem(retMap.get("mov_cnt")));
		retMap.put("upd_cnt", getIntMem(retMap.get("upd_cnt")));
		retMap.put("mov_seq", getIntMem(retMap.get("mov_seq")));
		retMap.put("out_fix_dt", "");
		retMap.put("out_fix_yn", "");
		retMap.put("out_fix_qty", 0);
		retMap.put("in_fix_dt", "");
		retMap.put("in_fix_yn", "");
		retMap.put("in_fix_qty", 0);
		retMap.put("dlvy_typ", "");
		retMap.put("div_co_id", "");
		retMap.put("dlvy_unit", "");
		retMap.put("dlvy_qty", 0);
		retMap.put("dlvy_dtm", "");
		retMap.put("bf_dlvy_req_dt", "");
		retMap.put("bf_dlvy_req_seq", 0);
		retMap.put("bf_dlv_co_id", "");
		retMap.put("cncl_yn", "");
		retMap.put("sale_cntr_id", "");
		retMap.put("out_cl", "301");

		return retMap;
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
	public String getOutMgmtNo(String mgmtCd, String userId,
			IOnlineContext onlineCtx) {
		String retMgmtNo = "";

		Map inMap = new HashMap();

		inMap.put("mgmt_no_cd", mgmtCd);
		inMap.put("user_id", userId);
		inMap.put("errcode", "");
		inMap.put("errmsg", "");
		inMap.put("mgmt_no", "");

		queryForObject("DISATR00200.selectOutMgmtNo", inMap, onlineCtx);

		retMgmtNo = inMap.get("mgmt_no").toString();

		return retMgmtNo;
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
	
}