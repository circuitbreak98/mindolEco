/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.inn.biz;

import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
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
 * <li>작성일 : 2011-07-22 14:36:14</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class DISINN01100 extends BaseBizUnit {

	/**
	 * 매입정보조회
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getPrchsInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getPrchsInfo method start.");
		}

		IRecordSet rs = queryForRecordSet("DISINN01100.selectPrchsInfo",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("PrchsInfo");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("PrchsInfo", rs);

		return result;
	}

	/**
	 * 구성품상세조회
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getCpntDisDtl(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getCpntDisDtl method start.");
		}

		//구성품재고 조회(단말기)		
		IRecordSet rsDis = queryForRecordSet("DISINN01100.getCpntDisDtl",
				requestData.getFieldMap(), onlineCtx);

		if (rsDis == null) {
			rsDis = new RecordSet("CpntDis");
		}

		//구성품기타재고 조회(단말기 외)
		IRecordSet rsEtcDis = queryForRecordSet("DISINN01100.getCpntEtcDisDtl",
				requestData.getFieldMap(), onlineCtx);

		if (rsEtcDis == null) {
			rsEtcDis = new RecordSet("CpntEtcDis");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsDis.getRecordCount()) });

		result.putRecordSet("CpntDis", rsDis);
		result.putRecordSet("CpntEtcDis", rsEtcDis);

		return result;
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
	public IDataSet saveCpntDis(IDataSet requestData, IOnlineContext onlineCtx) {

		// 구성품관리-------
		// 1. 출고
		// 2. 출고상세
		// 2-1. 구성품정보(단말기) 출고상세
		// 2-2. 구성품정보(단말기) 입출고이력상세
		// 2-3. 구성품정보(단말기) 재고(update)  - 단말기
		// 2-4. 구성품정보(기타) 출고상세
		// 2-5. 구성품정보(기타) 입출고이력상세
		// 2-6. 구성품정보(기타) 기타재고(update) - 배터리,기타 구분필요

		// 상품관리---------
		// 3. 상품재고
		// 4. 상품단가
		// 5. 구성품

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveCpntDis method start");
		}

		String sMgmtNoCl = "OT"; //관리번호구분 출고
		String strOutMgmtNo = ""; //출고관리번호
		String strOutMgmtSeq = ""; //출고순번
		String strInOutCl = "200"; // 입출고구분  : 300  이동출고 -> 200 출고 
		String strInOutDtlCl = "213"; // 입출고상세구분  : 301  이동출고  -> 213 구성품출고
		String strCurrDt = DateUtils.getCurrentDate();
		String strUpdCnt = "";   // 처리여부(중복처리여부 check)
		String strZero   = "0";  // 
		
		int iUpdCnt = 0;   // 처리여부(중복처리여부 check)
		
		int insertCount = 0;
		int updateCount = 0;
		
		PsLoginUserInfo userInfo = (PsLoginUserInfo) onlineCtx.getUserInfo(); // 사용자 정보 
		IRecordSet rsCpntDis = requestData.getRecordSet("ds_cpntDis");        // 구성품재고정보
		
		// 저장 처리전 중복처리여부 check
		Map chkMap = new HashMap();
		chkMap.put("prod_cd"  , rsCpntDis.get(0, "prod_cd"));
		chkMap.put("color_cd" , rsCpntDis.get(0, "color_cd"));
		chkMap.put("ser_num"  , rsCpntDis.get(0, "ser_num"));
		chkMap.put("upd_cnt"  , rsCpntDis.get(0, "upd_cnt"));
		
		// TDIS_DIS의 upd_cnt check
		iUpdCnt = this.getChkDisCnt(chkMap, onlineCtx);

		int iTemp = (int) Double.parseDouble(rsCpntDis.get(0, "upd_cnt").toString());
		if (iUpdCnt != iTemp) {
			throw new BizRuntimeException("조회 후 다시 처리하십시요.");
		}
		
		// 출고관리번호 생성
		if (strOutMgmtNo == null || "".equals(strOutMgmtNo)) {
			strOutMgmtNo = this.getMgmtNo(sMgmtNoCl, userInfo.getLoginId(),
					onlineCtx);
			//rsActFlg.getRecord(0).put("out_mgmt_no", strMgmtNo);
		}

		// 1.출고 
		Map outMasterMap = new HashMap();

		outMasterMap.put("out_mgmt_no", strOutMgmtNo);
		outMasterMap.put("out_cl"     , strInOutDtlCl);
		outMasterMap.put("out_schd_dt", "");
		outMasterMap.put("out_plc_id" , rsCpntDis.get(0, "hld_plc_id"));
		outMasterMap.put("in_plc_id"  , rsCpntDis.get(0, "hld_plc_id"));
		outMasterMap.put("out_fix_dt" , strCurrDt);
		outMasterMap.put("out_fix_yn" , "Y");
		outMasterMap.put("rmks"       , "");

		insert("DISINN01100.addOutM", outMasterMap, onlineCtx);

		// 2. 출고상세
		Map outDtlMap = new HashMap(); // 출고상세
		Map saveInOutDtl = new HashMap(); // 입출고상세
		Map saveDis = new HashMap(); // 재고

		// 구성품(단말기) 정보처리
		if (rsCpntDis != null) {

			strOutMgmtSeq = this.getOutSeq(strOutMgmtNo, userInfo.getLoginId(),
					onlineCtx);

			// 2-1. 구성품  정보  저장(단말기) - 출고상세			
			outDtlMap.put("out_mgmt_no", strOutMgmtNo);
			outDtlMap.put("out_seq"    , strOutMgmtSeq);
			outDtlMap.put("prod_cd"    , rsCpntDis.get(0, "prod_cd"));
			outDtlMap.put("color_cd"   , rsCpntDis.get(0, "color_cd"));
			outDtlMap.put("ser_num"    , rsCpntDis.get(0, "ser_num"));
			outDtlMap.put("eqp_st"     , rsCpntDis.get(0, "eqp_st"));
			outDtlMap.put("dis_st"     , rsCpntDis.get(0, "dis_st"));
			outDtlMap.put("out_qty"    , rsCpntDis.get(0, "out_qty"));
			outDtlMap.put("unit_prc"   , rsCpntDis.get(0, "unit_prc"));
			outDtlMap.put("amt"        , rsCpntDis.get(0, "amt"));

			insert("DISINN01100.addOutDtl", outDtlMap, onlineCtx);
			insertCount++;

			// 2-2. 구성품정보(단말기) 입출고이력상세
			saveInOutDtl.put("prod_cd"     , rsCpntDis.get(0, "prod_cd"));
			saveInOutDtl.put("color_cd"    , rsCpntDis.get(0, "color_cd"));
			saveInOutDtl.put("ser_num"     , rsCpntDis.get(0, "ser_num"));
			saveInOutDtl.put("inout_seq"   , "");
			saveInOutDtl.put("in_mgmt_no"  , "");
			saveInOutDtl.put("in_seq"      , "");
			saveInOutDtl.put("out_mgmt_no" , strOutMgmtNo);
			saveInOutDtl.put("out_seq"     , strOutMgmtSeq);
			saveInOutDtl.put("inout_dt"    , strCurrDt);
			saveInOutDtl.put("inout_cl"    , strInOutCl);
			saveInOutDtl.put("inout_dtl_cl", strInOutDtlCl);
			saveInOutDtl.put("prchs_plc_id", rsCpntDis.get(0, "prchs_plc_id"));
			saveInOutDtl.put("out_plc_id"  , rsCpntDis.get(0, "hld_plc_id"));
			saveInOutDtl.put("in_plc_id"   , rsCpntDis.get(0, "hld_plc_id"));

			insert("DISINN01100.addDisInOutHst", saveInOutDtl, onlineCtx);
			insertCount++;

			// 2-3. 구성품정보(단말기) 재고(update)
			saveDis.put("prod_cd"          , rsCpntDis.get(0, "prod_cd"));
			saveDis.put("color_cd"         , rsCpntDis.get(0, "color_cd"));
			saveDis.put("ser_num"          , rsCpntDis.get(0, "ser_num"));
			saveDis.put("last_inout_dt"    , strCurrDt);
			saveDis.put("last_inout_cl"    , strInOutCl);
			saveDis.put("last_inout_dtl_cl", strInOutDtlCl);

			update("DISINN01100.updateDisLastInOut", saveDis, onlineCtx);
			updateCount++;

			// 2-4. 구성품정보(단말기) 재고이력(insert)
			insert("DISINN01100.addDisHst", saveInOutDtl, onlineCtx);
			insertCount++;

		}

		// 구성품(기타) 처리
		IRecordSet rsCpntEtcDis = requestData.getRecordSet("ds_cpntEtcDis");
		Map etcDisMap = null;
        int iOutQty   = 0;
        int iPrchsQty  = 0;
        int iAddQty   = 0;
        int iAmt	  = 0;
		
		if (rsCpntEtcDis != null) {

			for (int i = 0; i < rsCpntEtcDis.getRecordCount(); i++) {


				etcDisMap = rsCpntEtcDis.getRecordMap(i);
				
				iOutQty  = 0;
				
//				iPrchsQty = (int) Double.parseDouble(etcDisMap.get("prchs_qty").toString());
				iAddQty   = (int) Double.parseDouble(etcDisMap.get("add_qty").toString());
//				iOutQty   = iPrchsQty + iAddQty;	

				if ( iAddQty > 0 ){
					//기타재고 처리
					etcDisMap.put("out_qty", iAddQty);
					
					update("DISINN01100.updateEtcDis", etcDisMap,	onlineCtx);
					updateCount++;
					
					//기타재고입출력이력상세
			        etcDisMap.put("inout_seq"   , "");
					etcDisMap.put("in_mgmt_no"  , "");
					etcDisMap.put("in_seq"      , "");
					etcDisMap.put("etc_seq"     , etcDisMap.get("etc_seq"));
					etcDisMap.put("out_mgmt_no" , strOutMgmtNo);
					etcDisMap.put("out_seq"     , strOutMgmtSeq);
					etcDisMap.put("inout_dt"    , strCurrDt);
					etcDisMap.put("inout_cl"    , strInOutCl);
					etcDisMap.put("inout_dtl_cl", strInOutDtlCl);
					etcDisMap.put("prchs_plc_id", rsCpntDis.get(0, "prchs_plc_id"));
					etcDisMap.put("out_plc_id"  , rsCpntDis.get(0, "hld_plc_id"));
					etcDisMap.put("in_plc_id"   , rsCpntDis.get(0, "hld_plc_id"));
					etcDisMap.put("inout_qty"   , iAddQty);
	
					insert("DISINN01100.addEtcDisInOutHst", etcDisMap, onlineCtx);
					insertCount++;
						
					// 기타재고이력
					/*
    				etcDisMap.put("etc_seq"    , "" );			
					etcDisMap.put("hld_plc_id" , rsCpntDis.get(0, "hld_plc_id") );			
					etcDisMap.put("qty"        , "" );			
					etcDisMap.put("unit_prc"   , "" );			
					etcDisMap.put("amt"        , "" );			
					etcDisMap.put("un_use_qty" , "" );			
					
					update("DISINN01100.addEtcDisHst", etcDisMap,	onlineCtx);
					updateCount++;	
					*/	
					
					strOutMgmtSeq = this.getOutSeq(strOutMgmtNo, userInfo.getLoginId(),
							onlineCtx);

					// 출고상세 - 기타재고			
					etcDisMap.put("out_mgmt_no", strOutMgmtNo);
					etcDisMap.put("out_seq"    , strOutMgmtSeq);
					etcDisMap.put("prod_cd"    , etcDisMap.get("cpnt_prod_cd"));
					etcDisMap.put("color_cd"   , etcDisMap.get("cpnt_color_cd"));
					etcDisMap.put("ser_num"    , etcDisMap.get("cpnt_ser_num"));
					etcDisMap.put("eqp_st"     , "");
					etcDisMap.put("dis_st"     , "");
					etcDisMap.put("out_qty"    , iAddQty);
					etcDisMap.put("unit_prc"   , etcDisMap.get("dis_amt"));
					etcDisMap.put("amt"        , etcDisMap.get("sale_prc"));

					insert("DISINN01100.addOutDtl", etcDisMap, onlineCtx);
					insertCount++;					
				}
			}
		}

		// 3. 상품재고
		int insertProdCount  = 0;
		Map saveProdDis      = new HashMap(); // 상품재고
		Map saveProdUnitPrc  = new HashMap(); // 상품단가
		Map saveProdCpnt     = new HashMap(); // 상품구성품
		Map saveProdInoutHst = new HashMap(); // 상품입출고이력
        String sProdSerNum   = "";            // 상품일련번호(new)
		
		if (rsCpntDis != null) {

			// 상품 일련번호(ser_num) 채번
			Map mSerNum = new HashMap();
			mSerNum.put("prod_cl", "1");  // 상품구분
			mSerNum.put("prod_cd", rsCpntDis.get(0, "prod_cd"));  // 상품코드
			mSerNum.put("ser_num", "");

			sProdSerNum = this.getProdSerNum(mSerNum, onlineCtx);

			// 상품재고  저장			
			saveProdDis.put("prod_cd"          , rsCpntDis.get(0, "prod_cd"));   // 상품코드
			saveProdDis.put("color_cd"         , rsCpntDis.get(0, "color_cd"));  // 색상코드
			saveProdDis.put("ser_num"          , sProdSerNum );                  // 일련번호(New) 
			saveProdDis.put("old_ser_num"      , rsCpntDis.get(0, "ser_num"));   // 일련번호(Old) 
			saveProdDis.put("hld_plc_id"       , rsCpntDis.get(0, "hld_plc_id"));// 보유처
			saveProdDis.put("dis_st"           , "01");                          // 재고상태 : 가용
			saveProdDis.put("eqp_st"           , rsCpntDis.get(0, "eqp_st"));    // 단말기등급
			saveProdDis.put("dis_amt"          , rsCpntDis.get(0, "dis_amt"));   // 재고금액
			saveProdDis.put("last_inout_dt"    , strCurrDt);                     // 최종입출고일자                       
			saveProdDis.put("last_inout_cl"    , "100");                         // 최종입출고구분 
			saveProdDis.put("last_inout_dtl_cl", "114");                         // 최종입출고상세구분
			saveProdDis.put("fst_in_fix_dt"    , "");                            // 최초입고확정일   
			saveProdDis.put("fst_in_plc_id"    , rsCpntDis.get(0, "hld_plc_id"));// 최초입고처
			saveProdDis.put("fst_prchs_plc_id" , rsCpntDis.get(0, "prchs_plc_id")); // 최초매입처
			saveProdDis.put("last_mov_in_dt"   , strCurrDt);                        // 최종이동입고일
			saveProdDis.put("fix_sale_amt"     , rsCpntDis.get(0, "dis_amt"));      // 확정판매금액
			saveProdDis.put("fix_sale_dt"      , "");                              // 확정여부
			saveProdDis.put("sale_yn"          , "N");                              // 확정여부
			saveProdDis.put("rmks"             , rsCpntDis.get(0, "rmks"));
			saveProdDis.put("con_mgmt_no"      , rsCpntDis.get(0, "con_mgmt_no"));

			insert("DISINN01100.addProdDis", saveProdDis, onlineCtx);
			insertProdCount++;

			// 4. 상품단가 - 상품단가는 단말기만 관리함.
			saveProdUnitPrc.put("prod_cd" , rsCpntDis.get(0, "prod_cd"));
			saveProdUnitPrc.put("color_cd", rsCpntDis.get(0, "color_cd"));
			saveProdUnitPrc.put("ser_num" , sProdSerNum );
			saveProdUnitPrc.put("occr_cl" , "01"); // 발생구분 : 01 
			saveProdUnitPrc.put("occr_dt" , strCurrDt);
			saveProdUnitPrc.put("amt"     , rsCpntDis.get(0, "dis_amt"));

			insert("DISINN01100.addProdUnitPrc", saveProdUnitPrc, onlineCtx);
			insertProdCount++;

			// 5. 구성품
			// 배터리 - tdis_dis의 수량
			// 젠터,기타 - tdis_etc_dis_inout_hst의 수량 참조해서 처리해야함.
			if (rsCpntEtcDis != null) {

				for (int i = 0; i < rsCpntEtcDis.getRecordCount(); i++) {
					etcDisMap = rsCpntEtcDis.getRecordMap(i);
					
					iPrchsQty = (int) Double.parseDouble(etcDisMap.get("prchs_qty").toString());
					iAddQty  = (int) Double.parseDouble(etcDisMap.get("add_qty").toString());
					iAmt  = (int) Double.parseDouble(etcDisMap.get("prchs_prc").toString());
					iOutQty = iPrchsQty + iAddQty;	
					
					saveProdCpnt.put("prod_cd" , rsCpntDis.get(0, "prod_cd"));
					saveProdCpnt.put("color_cd", rsCpntDis.get(0, "color_cd"));
					saveProdCpnt.put("ser_num" , sProdSerNum);
					saveProdCpnt.put("seq"     , ""); // 순번 : 일련번호 

					saveProdCpnt.put("cpnt_prod_cd" , etcDisMap.get("cpnt_prod_cd"));
					saveProdCpnt.put("cpnt_color_cd", etcDisMap.get("cpnt_color_cd"));
					saveProdCpnt.put("cpnt_ser_num" , etcDisMap.get("cpnt_ser_num"));
					saveProdCpnt.put("cpnt_etc_seq" , etcDisMap.get("cpnt_etc_seq"));

					saveProdCpnt.put("in_qty"  , iOutQty);
					saveProdCpnt.put("amt"  , iAmt);
					saveProdCpnt.put("cncl_yn" , "N");

					insert("DISINN01100.addProdCpnt", saveProdCpnt,
							onlineCtx);
					insertProdCount++;
				}
			}

			// 6. 상품입출고이력
			saveProdInoutHst.put("prod_cd"     , rsCpntDis.get(0, "prod_cd"));
			saveProdInoutHst.put("color_cd"    , rsCpntDis.get(0, "color_cd"));
			saveProdInoutHst.put("ser_num"     , sProdSerNum );
			saveProdInoutHst.put("inout_seq"   , "");
			saveProdInoutHst.put("in_mgmt_no"  , "");
			saveProdInoutHst.put("in_seq"      , "");
			saveProdInoutHst.put("out_mgmt_no" , "");
			saveProdInoutHst.put("out_seq"     , "");
			saveProdInoutHst.put("inout_dt"    , strCurrDt);
			saveProdInoutHst.put("inout_cl"    , "100");
			saveProdInoutHst.put("inout_dtl_cl", "114");
			saveProdInoutHst.put("prchs_plc_id", rsCpntDis.get(0,"prchs_plc_id"));
			saveProdInoutHst.put("out_plc_id"  , rsCpntDis.get(0,"hld_plc_id"));
			saveProdInoutHst.put("in_plc_id"   , rsCpntDis.get(0,"hld_plc_id"));

			insert("DISINN01100.addProdInoutHst", saveProdInoutHst, onlineCtx);
			insertProdCount++;
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { ""
						+ insertProdCount });

		return responseData;
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
		String retMgmtNo = "";
		Log log = LogManager.getLog(onlineCtx);
		Map inMap = new HashMap();

		inMap.put("mgmt_no_cd", mgmtCd);
		inMap.put("user_id", userId);
		inMap.put("errcode", "");
		inMap.put("errmsg", "");
		inMap.put("mgmt_no", "");

		queryForObject("DISINN01100.getMgmtNo", inMap, onlineCtx);
		
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

		Map mgmtNo = (Map) queryForObject("DISINN01100.getOutSeq", inMap,
				onlineCtx);

		retOutSeq = mgmtNo.get("OUT_SEQ").toString();

		return retOutSeq;
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
	public IDataSet getEtcDisList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("getEtcDisList method start");
		}

		IRecordSet rs = queryForRecordSet("DISINN01100.getEtcDisList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("EtcDisList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("EtcDisList", rs);

		return result;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 일련번호를 얻는다.
	 * @param data
	 *            요청정보를 Wrapping하고 있는 Map 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public String getProdSerNum(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN01100.getProdSerNum method start");
		}
		String sProdSerNum = ""; //일련번호

		Map mProdSerNum = (Map) queryForObject("DISINN00400.getSerNum", data, onlineCtx);

		sProdSerNum = (String) mProdSerNum.get("SER_NUM");

		if (sProdSerNum == null || sProdSerNum.equals("")) {
			throw new BizRuntimeException("모델일련번호 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}

		return sProdSerNum;
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
	public int getChkDisCnt(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN01100.getDisProcYn method start");
		}
		int iUpdCnt = 0; //일련번호

		Map msDisCnt = (Map) queryForObject("DISINN01100.getChkDisCnt", data, onlineCtx);

		iUpdCnt = (int) Double.parseDouble(msDisCnt.get("UPD_CNT").toString());

//		if (iUpdCnt == null || iUpdCnt.equals("")) {
//			throw new BizRuntimeException("재고 처리여부 확인시 에러가 발생하였습니다. 담당자에게 문의하세요.");
//		}

		return iUpdCnt;
	}	
}