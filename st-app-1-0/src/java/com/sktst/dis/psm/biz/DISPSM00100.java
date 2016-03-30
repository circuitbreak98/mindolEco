/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.psm.biz;

import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.core.util.DateUtils;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.common.user.PsLoginUserInfo;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-08-30 13:39:02</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class DISPSM00100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdDisList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISPSM00100.getProdDisList method start");
		}

		IRecordSet rs = queryForRecordSet("DISPSM00100.getProdDisList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ProdDisList");
		}
    
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ProdDisList", rs);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveProdSaleList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("saveProdSaleList method start");
		}
		
		int updateCount = 0;
		int insertCount = 0;
		int iUpdCnt     = 0;   // 처리여부(중복처리여부 check)
		int iProdRsCnt  = 0;   // 상품정보 레코드 count
		int i           = 0;   // 반복문 변수

		String sCurrDt       = DateUtils.getCurrentDate();   // 처리일자(현재일자)
		String sInoutCl      = "200";                        // 입출고구분 : 200 출고
		String sInoutDtlCl   = "205";                        // 입출고구분 : 205 판매출고
		String sMgmtNoCl     = "OT"; //관리번호구분 출고
		String sOutMgmtNo    = "";   //출고관리번호
		String sOutMgmtSeq   = "";   //출고순번
		
		PsLoginUserInfo userInfo = (PsLoginUserInfo) onlineCtx.getUserInfo(); // 사용자 정보 
		IRecordSet rsProdDisList = requestData.getRecordSet("ds_prodDisList");    // 상품정보리스트	
		
		log.debug("rsProdDisList ====== " + rsProdDisList);
		
		iProdRsCnt = rsProdDisList.getRecordCount();
		
		log.debug("iProdRsCnt ====== " + iProdRsCnt);
		
		for ( i = 0; i < iProdRsCnt ; i++ ){
			
			IRecord rProdDis = rsProdDisList.getRecord(i);
			
			log.debug("rProdDis ====== " + rProdDis);
			
			// upd_cnt check
			// 이미 출고된 것인지 확인 필요......****
			
			if (rProdDis.get("sale_yn").equals("Y") && 
				rProdDis.get("nc_rec_status").equals("update") &&
				!rProdDis.get("last_inout_cl").equals("200") )
			{
				// 저장 처리전 중복처리여부 check
				Map mCond = new HashMap();
				mCond.put("prod_cd"  , rProdDis.get("prod_cd"));
				mCond.put("color_cd" , rProdDis.get("color_cd"));
				mCond.put("ser_num"  , rProdDis.get("prod_ser_num"));
				mCond.put("upd_cnt"  , rProdDis.get("upd_cnt"));
				
				// TDIS_PROD_DIS의 upd_cnt check
				iUpdCnt = this.getProdDisChk(mCond, onlineCtx);
	
				int iTemp = (int) Double.parseDouble(rProdDis.get("upd_cnt").toString());
				if (iUpdCnt != iTemp) {
					throw new BizRuntimeException("조회 후 다시 처리하십시요.");
				}
				
				// 1. 상품재고 출고처리
				mCond.put("last_inout_dt"    , sCurrDt);
				mCond.put("last_inout_cl"    , sInoutCl);
				mCond.put("last_inout_dtl_cl", sInoutDtlCl);
				mCond.put("sale_yn"          , rProdDis.get("sale_yn"));
				mCond.put("fix_sale_amt"     , rProdDis.get("fix_sale_amt"));
				mCond.put("fix_sale_dt"      , rProdDis.get("fix_sale_dt"));
				mCond.put("rmks"             , "");
			
				update("DISPSM00200.updateProdSale", mCond, onlineCtx);
				updateCount++;
				
				// 2. 상품출고 tdis_prod_out_m
				// 2.1 출고관리번호 생성
				sOutMgmtNo = "";
				if (sOutMgmtNo == null || "".equals(sOutMgmtNo)) {
					sOutMgmtNo = this.getMgmtNo(sMgmtNoCl, userInfo.getLoginId(),
							onlineCtx);
					//rsActFlg.getRecord(0).put("out_mgmt_no", strMgmtNo);
				}
	
				log.debug("출고관리번호생성--------------------");
				log.debug("strOutMgmtNo == " + sOutMgmtNo);
				log.debug("---------------------------------");
	
				// 2.2 상품출고등록
				mCond.put("out_mgmt_no"      , sOutMgmtNo);
				mCond.put("gnrl_sale_no"     , "");
				mCond.put("gnrl_sale_chg_seq", "");
				mCond.put("out_cl"           , sInoutDtlCl);
				mCond.put("out_schd_dt"      , sCurrDt);
				mCond.put("out_plc_id"       , rProdDis.get("hld_plc_id"));
				mCond.put("in_plc_id"        , "");
				mCond.put("out_fix_dt"       , sCurrDt);
				mCond.put("out_fix_yn"       , "Y");
				mCond.put("rmks"             , "");
				
				insert("DISPSM00200.insertProdOutM", mCond, onlineCtx);
				insertCount++;
				
				// 2.3 상품출고상세등록 tdis_prod_out_dtl_d
				// 출고순번 생성
				sOutMgmtSeq = this.getOutSeq(sOutMgmtNo, userInfo.getLoginId(),
						onlineCtx);
	
				log.debug("출고순번생성 --------------------" + sOutMgmtSeq);
				
				// 상품출고상세 등록
				mCond.put("out_seq"       , sOutMgmtSeq);
				mCond.put("eqp_st"        , rProdDis.get("eqp_st"));
				mCond.put("dis_st"        , rProdDis.get("dis_st"));
				mCond.put("out_qty"       , "1");
				mCond.put("settl_cond_cd" , "");
				mCond.put("unit_prc"      , rProdDis.get("fix_sale_amt"));
				mCond.put("amt"           , rProdDis.get("fix_sale_amt"));
				mCond.put("cncl_yn"       , "N");
				
				insert("DISPSM00200.insertProdOutDtlD", mCond, onlineCtx);
				insertCount++;
				
				// 2.4 상품입출고상세 tdis_prod_inout_hst
				mCond.put("in_mgmt_no"    , "");
				mCond.put("in_seq"        , "");
				mCond.put("prchs_plc_id"  , rProdDis.get("fst_prchs_plc_id"));
	
				insert("DISPSM00200.insertProdInoutHst", mCond, onlineCtx);
				insertCount++;

			}
		}
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { "insert :"
						+ insertCount, "update :"+ updateCount });
		
		return responseData;	
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 구성품 재고 처리여부를 얻는다.
	 * @param data
	 *            요청정보를 Wrapping하고 있는 Map 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public int getProdDisChk(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISPSM00200.getProdDisChk method start");
		}
		int iUpdCnt = 0; //일련번호

		Map msDisCnt = (Map) queryForObject("DISPSM00200.getProdDisChk", data, onlineCtx);

		iUpdCnt = (int) Double.parseDouble(msDisCnt.get("UPD_CNT").toString());

		return iUpdCnt;
	}	
	
	/**
	 * 관리 번호 생성
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 관리 번호를 생성한다.
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
		
		Log log = LogManager.getLog(onlineCtx);

		String retMgmtNo = "";
		Map inMap = new HashMap();

		inMap.put("mgmt_no_cd", mgmtCd);
		inMap.put("user_id", userId);
		inMap.put("errcode", "");
		inMap.put("errmsg", "");
		inMap.put("mgmt_no", "");

		queryForObject("DISPSM00200.getMgmtNo", inMap, onlineCtx);
		
		log.debug("채번--------------------");
		log.debug("채번 = " +inMap.get("mgmt_no"));
		log.debug("---------------------------------");
		
		retMgmtNo = inMap.get("mgmt_no").toString();

		return retMgmtNo;
	}

	/**
	 * 출고순번 생성
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 관리 번호를 생성한다.
	 * @param mgmtCd 
	 *            관리번호종류코드 
	 * @param userId 
	 *            사용자아이디 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public String getOutSeq(String mgmtCd, String userId,
			IOnlineContext onlineCtx) {

		String retOutSeq = "";
		int iOutSeq = 0;

		Map inMap = new HashMap();

		inMap.put("out_mgmt_no", mgmtCd);
		inMap.put("user_id", userId);
		inMap.put("errcode", "");
		inMap.put("errmsg", "");
		inMap.put("mgmt_no", "");

		Map mgmtNo = (Map) queryForObject("DISPSM00200.getOutSeq", inMap,
				onlineCtx);

		retOutSeq = mgmtNo.get("OUT_SEQ").toString();

		return retOutSeq;
	}	
}