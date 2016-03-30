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

import org.apache.commons.logging.Log;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.core.util.DateUtils;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.common.user.PsLoginUserInfo;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-19 20:50:46</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class DISINN01000 extends BaseBizUnit {

	/**
	 * 구성품재고목록조회
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getCpntDisList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getCpntDisList method start.");
		}

		IRecordSet rs = queryForRecordSet("DISINN01000.getCpntDisList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("CpntDisList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("CpntDisList", rs);

		return result;
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
	public IDataSet saveCpntDisAll(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveCpntDisAll method start");
		}

		String sMgmtNoCl = ""; //관리번호구분 출고
		String strOutMgmtNo = ""; //출고관리번호
		String strOutMgmtSeq = ""; //출고순번
		String strInOutCl = ""; // 입출고구분 
		String strInOutDtlCl = ""; // 입출고상세구분
		String strCurrDt = DateUtils.getCurrentDate();
		String strUpdCnt = ""; // 처리여부(중복처리여부 check)
		String strZero = "";

		int iUpdCnt = 0; // 처리여부(중복처리여부 check)

		IRecordSet rsCpntDis = requestData.getRecordSet("ds_cpntDisList");
		PsLoginUserInfo userInfo = (PsLoginUserInfo) onlineCtx.getUserInfo(); // 사용자 정보 
		//log.debug("rsCpntDis:"+rsCpntDis);		
		if (rsCpntDis != null) {
			//log.debug("RecordCount : "+rsCpntDis.getRecordCount());			
			for (int i = 0; i < rsCpntDis.getRecordCount(); i++) {
				//log.debug(i + "   " + rsCpntDis.get(i, "color_cd") + rsCpntDis.get(i, "PRC_CHECK"));				
				if (rsCpntDis.get(i, "PRC_CHECK").equals("1")) {
					//log.debug("*******************" +i+ "*******************");					
					Map chkMap = new HashMap();
					Map disMap = null;

					sMgmtNoCl = "OT"; //출고
					strOutMgmtNo = "";
					strOutMgmtSeq = "";
					strInOutCl = "200"; //200 출고 
					strInOutDtlCl = "213"; //213 구성품출고
					strCurrDt = DateUtils.getCurrentDate();
					strUpdCnt = "";
					strZero = "0";

					chkMap.put("prod_cd", rsCpntDis.get(i, "prod_cd"));
					chkMap.put("color_cd", rsCpntDis.get(i, "color_cd"));
					chkMap.put("ser_num", rsCpntDis.get(i, "ser_num"));
					chkMap.put("upd_cnt", rsCpntDis.get(i, "upd_cnt"));

					// TDIS_DIS의 upd_cnt check
					iUpdCnt = this.getChkDisCnt(chkMap, onlineCtx);

					int iTemp = (int) Double.parseDouble(rsCpntDis.get(0,
							"upd_cnt").toString());
					if (iUpdCnt != iTemp) {
						throw new BizRuntimeException("조회 후 다시 처리하십시요.");
					}
					// 출고관리번호 생성
					if (strOutMgmtNo == null || "".equals(strOutMgmtNo)) {
						strOutMgmtNo = this.getMgmtNo(sMgmtNoCl, userInfo
								.getLoginId(), onlineCtx);
					}
					// 1.출고 
					//log.debug("출고 ");				
					Map outMasterMap = new HashMap();

					outMasterMap.put("out_mgmt_no", strOutMgmtNo);
					outMasterMap.put("out_cl", strInOutDtlCl);
					outMasterMap.put("out_schd_dt", "");
					outMasterMap.put("out_plc_id", rsCpntDis.get(i,
							"hld_plc_id"));
					outMasterMap.put("in_plc_id", rsCpntDis
							.get(i, "hld_plc_id"));
					outMasterMap.put("out_fix_dt", strCurrDt);
					outMasterMap.put("out_fix_yn", "Y");
					outMasterMap.put("rmks", "");
					//log.debug("DISINN01100.addOutM s");
					insert("DISINN01100.addOutM", outMasterMap, onlineCtx);
					//log.debug("DISINN01100.addOutM e");
					// 2. 출고상세
					//log.debug("출고상세");
					Map outDtlMap = new HashMap(); // 출고상세
					Map saveInOutDtl = new HashMap(); // 입출고상세
					Map saveDis = new HashMap(); // 재고

					// 구성품(단말기) 정보처리
					//log.debug("구성품(단말기) 정보처리");				
					if (rsCpntDis != null) {

						strOutMgmtSeq = this.getOutSeq(strOutMgmtNo, userInfo
								.getLoginId(), onlineCtx);

						// 2-1. 구성품  정보  저장(단말기) - 출고상세			
						outDtlMap.put("out_mgmt_no", strOutMgmtNo);
						outDtlMap.put("out_seq", strOutMgmtSeq);
						outDtlMap.put("prod_cd", rsCpntDis.get(i, "prod_cd"));
						outDtlMap.put("color_cd", rsCpntDis.get(i, "color_cd"));
						outDtlMap.put("ser_num", rsCpntDis.get(i, "ser_num"));
						outDtlMap.put("eqp_st", rsCpntDis.get(i, "eqp_st"));
						outDtlMap.put("dis_st", rsCpntDis.get(i, "dis_st"));
						outDtlMap.put("out_qty", rsCpntDis.get(i, "out_qty"));
						outDtlMap.put("unit_prc", rsCpntDis.get(i, "unit_prc"));
						outDtlMap.put("amt", rsCpntDis.get(i, "amt"));
						//log.debug("DISINN01100.addOutDtl s");
						insert("DISINN01100.addOutDtl", outDtlMap, onlineCtx);
						//log.debug("DISINN01100.addOutDtl e");
						// 2-2. 구성품정보(단말기) 입출고이력상세
						saveInOutDtl
								.put("prod_cd", rsCpntDis.get(i, "prod_cd"));
						saveInOutDtl.put("color_cd", rsCpntDis.get(i,
								"color_cd"));
						saveInOutDtl
								.put("ser_num", rsCpntDis.get(i, "ser_num"));
						saveInOutDtl.put("inout_seq", "");
						saveInOutDtl.put("in_mgmt_no", "");
						saveInOutDtl.put("in_seq", "");
						saveInOutDtl.put("out_mgmt_no", strOutMgmtNo);
						saveInOutDtl.put("out_seq", strOutMgmtSeq);
						saveInOutDtl.put("inout_dt", strCurrDt);
						saveInOutDtl.put("inout_cl", strInOutCl);
						saveInOutDtl.put("inout_dtl_cl", strInOutDtlCl);
						saveInOutDtl.put("prchs_plc_id", rsCpntDis.get(i,
								"fst_prchs_plc_id"));
						saveInOutDtl.put("out_plc_id", rsCpntDis.get(i,
								"hld_plc_id"));
						saveInOutDtl.put("in_plc_id", rsCpntDis.get(i,
								"hld_plc_id"));
						//log.debug("DISINN01100.addDisInOutHst s");
						insert("DISINN01100.addDisInOutHst", saveInOutDtl,
								onlineCtx);
						//log.debug("DISINN01100.addDisInOutHst e");
						// 2-3. 구성품정보(단말기) 재고(update)
						saveDis.put("prod_cd", rsCpntDis.get(i, "prod_cd"));
						saveDis.put("color_cd", rsCpntDis.get(i, "color_cd"));
						saveDis.put("ser_num", rsCpntDis.get(i, "ser_num"));
						saveDis.put("last_inout_dt", strCurrDt);
						saveDis.put("last_inout_cl", strInOutCl);
						saveDis.put("last_inout_dtl_cl", strInOutDtlCl);
						//log.debug("DISINN01100.updateDisLastInOut s");
						update("DISINN01100.updateDisLastInOut", saveDis,
								onlineCtx);
						//log.debug("DISINN01100.updateDisLastInOut e");
						// 2-4. 구성품정보(단말기) 재고이력(insert)
						//log.debug("DISINN01100.addDisHst s");
						insert("DISINN01100.addDisHst", saveInOutDtl, onlineCtx);
						//log.debug("DISINN01100.addDisHst e");
					}

					// 구성품(기타) 처리
					IRecordSet rsCpntEtcDis = queryForRecordSet(
							"DISINN01100.getCpntEtcDisDtl", rsCpntDis
									.getRecordMap(i), onlineCtx);
					Map etcDisMap = null;
					int iOutQty = 0;
					int iPrchsQty = 0;
					int iAddQty = 0;
					int iAmt = 0;
					//log.debug("rsCpntEtcDis:"+rsCpntEtcDis);					
					if (rsCpntEtcDis != null) {

						for (int j = 0; j < rsCpntEtcDis.getRecordCount(); j++) {

							etcDisMap = rsCpntEtcDis.getRecordMap(j);
							iOutQty = 0;
							iAddQty = (int) Double.parseDouble(etcDisMap.get(
									"add_qty").toString());

							if (iAddQty > 0) {
								//기타재고 처리
								etcDisMap.put("out_qty", iAddQty);

								update("DISINN01100.updateEtcDis", etcDisMap,
										onlineCtx);

								//기타재고입출력이력상세
								etcDisMap.put("inout_seq", "");
								etcDisMap.put("in_mgmt_no", "");
								etcDisMap.put("in_seq", "");
								etcDisMap.put("etc_seq", etcDisMap
										.get("etc_seq"));
								etcDisMap.put("out_mgmt_no", strOutMgmtNo);
								etcDisMap.put("out_seq", strOutMgmtSeq);
								etcDisMap.put("inout_dt", strCurrDt);
								etcDisMap.put("inout_cl", strInOutCl);
								etcDisMap.put("inout_dtl_cl", strInOutDtlCl);
								etcDisMap.put("prchs_plc_id", rsCpntDis.get(i,
										"prchs_plc_id"));
								etcDisMap.put("out_plc_id", rsCpntDis.get(i,
										"hld_plc_id"));
								etcDisMap.put("in_plc_id", rsCpntDis.get(i,
										"hld_plc_id"));
								etcDisMap.put("inout_qty", iAddQty);

								insert("DISINN01100.addEtcDisInOutHst",
										etcDisMap, onlineCtx);

								strOutMgmtSeq = this.getOutSeq(strOutMgmtNo,
										userInfo.getLoginId(), onlineCtx);

								// 출고상세 - 기타재고			
								etcDisMap.put("out_mgmt_no", strOutMgmtNo);
								etcDisMap.put("out_seq", strOutMgmtSeq);
								etcDisMap.put("prod_cd", etcDisMap
										.get("cpnt_prod_cd"));
								etcDisMap.put("color_cd", etcDisMap
										.get("cpnt_color_cd"));
								etcDisMap.put("ser_num", etcDisMap
										.get("cpnt_ser_num"));
								etcDisMap.put("eqp_st", "");
								etcDisMap.put("dis_st", "");
								etcDisMap.put("out_qty", iAddQty);
								etcDisMap.put("unit_prc", etcDisMap
										.get("dis_amt"));
								etcDisMap.put("amt", etcDisMap.get("sale_prc"));

								insert("DISINN01100.addOutDtl", etcDisMap,
										onlineCtx);

							}
						}
					}
					//log.debug("상품재고");					
					// 3. 상품재고
					int insertProdCount = 0;
					Map saveProdDis = new HashMap(); // 상품재고
					Map saveProdUnitPrc = new HashMap(); // 상품단가
					Map saveProdCpnt = new HashMap(); // 상품구성품
					Map saveProdInoutHst = new HashMap(); // 상품입출고이력
					String sProdSerNum = ""; // 상품일련번호(new)

					if (rsCpntDis != null) {

						// 상품 일련번호(ser_num) 채번
						Map mSerNum = new HashMap();
						mSerNum.put("prod_cl", "1"); // 상품구분
						mSerNum.put("prod_cd", rsCpntDis.get(i, "prod_cd")); // 상품코드
						mSerNum.put("ser_num", "");

						sProdSerNum = this.getProdSerNum(mSerNum, onlineCtx);

						// 상품재고  저장			
						saveProdDis.put("prod_cd", rsCpntDis.get(i, "prod_cd")); // 상품코드
						saveProdDis.put("color_cd", rsCpntDis
								.get(i, "color_cd")); // 색상코드
						saveProdDis.put("ser_num", sProdSerNum); // 일련번호(New) 
						saveProdDis.put("old_ser_num", rsCpntDis.get(i,
								"ser_num")); // 일련번호(Old) 
						saveProdDis.put("hld_plc_id", rsCpntDis.get(i,
								"hld_plc_id"));// 보유처
						saveProdDis.put("dis_st", "01"); // 재고상태 : 가용
						saveProdDis.put("eqp_st", rsCpntDis.get(i, "eqp_st")); // 단말기등급
						saveProdDis.put("dis_amt", rsCpntDis.get(i, "dis_amt")); // 재고금액
						saveProdDis.put("last_inout_dt", strCurrDt); // 최종입출고일자                       
						saveProdDis.put("last_inout_cl", "100"); // 최종입출고구분 
						saveProdDis.put("last_inout_dtl_cl", "114"); // 최종입출고상세구분
						saveProdDis.put("fst_in_fix_dt", ""); // 최초입고확정일   
						saveProdDis.put("fst_in_plc_id", rsCpntDis.get(i,
								"hld_plc_id"));// 최초입고처
						saveProdDis.put("fst_prchs_plc_id", rsCpntDis.get(i,
								"prchs_plc_id")); // 최초매입처
						saveProdDis.put("last_mov_in_dt", strCurrDt); // 최종이동입고일
						saveProdDis.put("fix_sale_amt", rsCpntDis.get(i,
								"dis_amt")); // 확정판매금액
						saveProdDis.put("fix_sale_dt", ""); // 확정여부
						saveProdDis.put("sale_yn", "N"); // 확정여부
						saveProdDis.put("rmks", rsCpntDis.get(i, "rmks"));
						saveProdDis.put("con_mgmt_no", rsCpntDis.get(i, "con_mgmt_no")); //상담관리번호
						
						//log.debug("DISINN01100.addProdDis s");
						insert("DISINN01100.addProdDis", saveProdDis, onlineCtx);
						//log.debug("DISINN01100.addProdDis e");
						//log.debug("4.상품단가 -상품단가는 단말기만 관리함");
						// 4. 상품단가 - 상품단가는 단말기만 관리함.
						saveProdUnitPrc.put("prod_cd", rsCpntDis.get(i,
								"prod_cd"));
						saveProdUnitPrc.put("color_cd", rsCpntDis.get(i,
								"color_cd"));
						saveProdUnitPrc.put("ser_num", sProdSerNum);
						saveProdUnitPrc.put("occr_cl", "01"); // 발생구분 : 01 
						saveProdUnitPrc.put("occr_dt", strCurrDt);
						saveProdUnitPrc.put("amt", rsCpntDis.get(i, "dis_amt"));
						//log.debug("DISINN01100.addProdUnitPrc s");
						insert("DISINN01100.addProdUnitPrc", saveProdUnitPrc,
								onlineCtx);
						//log.debug("DISINN01100.addProdUnitPrc e");
						//log.debug("5.구성품");
						// 5. 구성품
						// 배터리 - tdis_dis의 수량
						// 젠터,기타 - tdis_etc_dis_inout_hst의 수량 참조해서 처리해야함.
						if (rsCpntEtcDis != null) {

							for (int j = 0; j < rsCpntEtcDis.getRecordCount(); j++) {
								etcDisMap = rsCpntEtcDis.getRecordMap(j);

								iPrchsQty = (int) Double.parseDouble(etcDisMap
										.get("prchs_qty").toString());
								iAddQty = (int) Double.parseDouble(etcDisMap
										.get("add_qty").toString());
								iOutQty = iPrchsQty + iAddQty;
								iAmt  = (int) Double.parseDouble(etcDisMap.get("prchs_prc").toString());

								saveProdCpnt.put("prod_cd", rsCpntDis.get(i,
										"prod_cd"));
								saveProdCpnt.put("color_cd", rsCpntDis.get(i,
										"color_cd"));
								saveProdCpnt.put("ser_num", sProdSerNum);
								saveProdCpnt.put("seq", ""); // 순번 : 일련번호 

								saveProdCpnt.put("cpnt_prod_cd", etcDisMap
										.get("cpnt_prod_cd"));
								saveProdCpnt.put("cpnt_color_cd", etcDisMap
										.get("cpnt_color_cd"));
								saveProdCpnt.put("cpnt_ser_num", etcDisMap
										.get("cpnt_ser_num"));
								saveProdCpnt.put("cpnt_etc_seq", etcDisMap
										.get("cpnt_etc_seq"));

								saveProdCpnt.put("in_qty", iOutQty);
								saveProdCpnt.put("amt"  , iAmt);
								
								saveProdCpnt.put("cncl_yn", "N");
								//log.debug("DISINN01100.addProdCpnt s");
								insert("DISINN01100.addProdCpnt", saveProdCpnt,
										onlineCtx);
								//log.debug("DISINN01100.addProdCpnt e");								
							}
						}
						//log.debug("6.상품입출고이력");
						// 6. 상품입출고이력
						saveProdInoutHst.put("prod_cd", rsCpntDis.get(i,
								"prod_cd"));
						saveProdInoutHst.put("color_cd", rsCpntDis.get(i,
								"color_cd"));
						saveProdInoutHst.put("ser_num", sProdSerNum);
						saveProdInoutHst.put("inout_seq", "");
						saveProdInoutHst.put("in_mgmt_no", "");
						saveProdInoutHst.put("in_seq", "");
						saveProdInoutHst.put("out_mgmt_no", "");
						saveProdInoutHst.put("out_seq", "");
						saveProdInoutHst.put("inout_dt", strCurrDt);
						saveProdInoutHst.put("inout_cl", "100");
						saveProdInoutHst.put("inout_dtl_cl", "114");
						saveProdInoutHst.put("prchs_plc_id", rsCpntDis.get(i,
								"prchs_plc_id"));
						saveProdInoutHst.put("out_plc_id", rsCpntDis.get(i,
								"hld_plc_id"));
						saveProdInoutHst.put("in_plc_id", rsCpntDis.get(i,
								"hld_plc_id"));
						//log.debug("DISINN01100.addProdInoutHst s");
						insert("DISINN01100.addProdInoutHst", saveProdInoutHst,
								onlineCtx);
						//log.debug("DISINN01100.addProdInoutHst e");
					}
				}
			}
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });
	}

	public int getChkDisCnt(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN01100.getDisProcYn method start");
		}
		int iUpdCnt = 0; //일련번호

		Map msDisCnt = (Map) queryForObject("DISINN01100.getChkDisCnt", data,
				onlineCtx);

		iUpdCnt = (int) Double.parseDouble(msDisCnt.get("UPD_CNT").toString());

		return iUpdCnt;
	}

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

	public String getProdSerNum(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN01100.getProdSerNum method start");
		}
		String sProdSerNum = ""; //일련번호

		Map mProdSerNum = (Map) queryForObject("DISINN00400.getSerNum", data,
				onlineCtx);

		sProdSerNum = (String) mProdSerNum.get("SER_NUM");

		if (sProdSerNum == null || sProdSerNum.equals("")) {
			throw new BizRuntimeException(
					"모델일련번호 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}

		return sProdSerNum;
	}

	/**
	 * 
	 * 
	 * @author 전미량 (jeonmiryang)
	 * -박스바코드를 입력하여 박스 재고를 조회한다.
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getCpntDisBoxList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getCpntDisBoxList method start.");
		}

		IRecordSet rs = queryForRecordSet("DISINN01000.getCpntDisBoxList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("CpntDisList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("CpntDisList", rs);

		return result;
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
	public IDataSet saveCpntDisAll2(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveCpntDisAll2 method start");
		}

		String sMgmtNoCl = ""; //관리번호구분 출고
		String strOutMgmtNo = ""; //출고관리번호
		String strOutMgmtSeq = ""; //출고순번
		String strInOutCl = ""; // 입출고구분 
		String strInOutDtlCl = ""; // 입출고상세구분
		String strCurrDt = DateUtils.getCurrentDate();
		String strUpdCnt = ""; // 처리여부(중복처리여부 check)
		String strZero = "";

		int iUpdCnt = 0; // 처리여부(중복처리여부 check)

		IRecordSet rsCpntDis = queryForRecordSet("DISINN01000.getCpntDisList",
				requestData.getFieldMap(), onlineCtx);
		PsLoginUserInfo userInfo = (PsLoginUserInfo) onlineCtx.getUserInfo(); // 사용자 정보 
		//log.debug("rsCpntDis:"+rsCpntDis);		
		if (rsCpntDis != null) {
			//log.debug("RecordCount : "+rsCpntDis.getRecordCount());			
			for (int i = 0; i < rsCpntDis.getRecordCount(); i++) {
				//log.debug(i + "   " + rsCpntDis.get(i, "color_cd") + rsCpntDis.get(i, "PRC_CHECK"));				
					//log.debug("*******************" +i+ "*******************");					
					Map chkMap = new HashMap();
					Map disMap = null;

					sMgmtNoCl = "OT"; //출고
					strOutMgmtNo = "";
					strOutMgmtSeq = "";
					strInOutCl = "200"; //200 출고 
					strInOutDtlCl = "213"; //213 구성품출고
					strCurrDt = DateUtils.getCurrentDate();
					strUpdCnt = "";
					strZero = "0";

					chkMap.put("prod_cd", rsCpntDis.get(i, "prod_cd"));
					chkMap.put("color_cd", rsCpntDis.get(i, "color_cd"));
					chkMap.put("ser_num", rsCpntDis.get(i, "ser_num"));
					chkMap.put("upd_cnt", rsCpntDis.get(i, "upd_cnt"));

					// TDIS_DIS의 upd_cnt check
					iUpdCnt = this.getChkDisCnt(chkMap, onlineCtx);

					int iTemp = (int) Double.parseDouble(rsCpntDis.get(0,
							"upd_cnt").toString());
					if (iUpdCnt != iTemp) {
						throw new BizRuntimeException("조회 후 다시 처리하십시요.");
					}
					// 출고관리번호 생성
					if (strOutMgmtNo == null || "".equals(strOutMgmtNo)) {
						strOutMgmtNo = this.getMgmtNo(sMgmtNoCl, userInfo
								.getLoginId(), onlineCtx);
					}
					// 1.출고 
					//log.debug("출고 ");				
					Map outMasterMap = new HashMap();

					outMasterMap.put("out_mgmt_no", strOutMgmtNo);
					outMasterMap.put("out_cl", strInOutDtlCl);
					outMasterMap.put("out_schd_dt", "");
					outMasterMap.put("out_plc_id", rsCpntDis.get(i,
							"hld_plc_id"));
					outMasterMap.put("in_plc_id", rsCpntDis
							.get(i, "hld_plc_id"));
					outMasterMap.put("out_fix_dt", strCurrDt);
					outMasterMap.put("out_fix_yn", "Y");
					outMasterMap.put("rmks", "");
					//log.debug("DISINN01100.addOutM s");
					insert("DISINN01100.addOutM", outMasterMap, onlineCtx);
					//log.debug("DISINN01100.addOutM e");
					// 2. 출고상세
					//log.debug("출고상세");
					Map outDtlMap = new HashMap(); // 출고상세
					Map saveInOutDtl = new HashMap(); // 입출고상세
					Map saveDis = new HashMap(); // 재고

					// 구성품(단말기) 정보처리
					//log.debug("구성품(단말기) 정보처리");				
					if (rsCpntDis != null) {

						strOutMgmtSeq = this.getOutSeq(strOutMgmtNo, userInfo
								.getLoginId(), onlineCtx);

						// 2-1. 구성품  정보  저장(단말기) - 출고상세			
						outDtlMap.put("out_mgmt_no", strOutMgmtNo);
						outDtlMap.put("out_seq", strOutMgmtSeq);
						outDtlMap.put("prod_cd", rsCpntDis.get(i, "prod_cd"));
						outDtlMap.put("color_cd", rsCpntDis.get(i, "color_cd"));
						outDtlMap.put("ser_num", rsCpntDis.get(i, "ser_num"));
						outDtlMap.put("eqp_st", rsCpntDis.get(i, "eqp_st"));
						outDtlMap.put("dis_st", rsCpntDis.get(i, "dis_st"));
						outDtlMap.put("out_qty", rsCpntDis.get(i, "out_qty"));
						outDtlMap.put("unit_prc", rsCpntDis.get(i, "unit_prc"));
						outDtlMap.put("amt", rsCpntDis.get(i, "amt"));
						//log.debug("DISINN01100.addOutDtl s");
						insert("DISINN01100.addOutDtl", outDtlMap, onlineCtx);
						//log.debug("DISINN01100.addOutDtl e");
						// 2-2. 구성품정보(단말기) 입출고이력상세
						saveInOutDtl
								.put("prod_cd", rsCpntDis.get(i, "prod_cd"));
						saveInOutDtl.put("color_cd", rsCpntDis.get(i,
								"color_cd"));
						saveInOutDtl
								.put("ser_num", rsCpntDis.get(i, "ser_num"));
						saveInOutDtl.put("inout_seq", "");
						saveInOutDtl.put("in_mgmt_no", "");
						saveInOutDtl.put("in_seq", "");
						saveInOutDtl.put("out_mgmt_no", strOutMgmtNo);
						saveInOutDtl.put("out_seq", strOutMgmtSeq);
						saveInOutDtl.put("inout_dt", strCurrDt);
						saveInOutDtl.put("inout_cl", strInOutCl);
						saveInOutDtl.put("inout_dtl_cl", strInOutDtlCl);
						saveInOutDtl.put("prchs_plc_id", rsCpntDis.get(i,
								"fst_prchs_plc_id"));
						saveInOutDtl.put("out_plc_id", rsCpntDis.get(i,
								"hld_plc_id"));
						saveInOutDtl.put("in_plc_id", rsCpntDis.get(i,
								"hld_plc_id"));
						//log.debug("DISINN01100.addDisInOutHst s");
						insert("DISINN01100.addDisInOutHst", saveInOutDtl,
								onlineCtx);
						//log.debug("DISINN01100.addDisInOutHst e");
						// 2-3. 구성품정보(단말기) 재고(update)
						saveDis.put("prod_cd", rsCpntDis.get(i, "prod_cd"));
						saveDis.put("color_cd", rsCpntDis.get(i, "color_cd"));
						saveDis.put("ser_num", rsCpntDis.get(i, "ser_num"));
						saveDis.put("last_inout_dt", strCurrDt);
						saveDis.put("last_inout_cl", strInOutCl);
						saveDis.put("last_inout_dtl_cl", strInOutDtlCl);
						//log.debug("DISINN01100.updateDisLastInOut s");
						update("DISINN01100.updateDisLastInOut", saveDis,
								onlineCtx);
						//log.debug("DISINN01100.updateDisLastInOut e");
						// 2-4. 구성품정보(단말기) 재고이력(insert)
						//log.debug("DISINN01100.addDisHst s");
						insert("DISINN01100.addDisHst", saveInOutDtl, onlineCtx);
						//log.debug("DISINN01100.addDisHst e");
					}

					// 구성품(기타) 처리
					IRecordSet rsCpntEtcDis = queryForRecordSet(
							"DISINN01100.getCpntEtcDisDtl", rsCpntDis
									.getRecordMap(i), onlineCtx);
					Map etcDisMap = null;
					int iOutQty = 0;
					int iPrchsQty = 0;
					int iAddQty = 0;
					//log.debug("rsCpntEtcDis:"+rsCpntEtcDis);					
					if (rsCpntEtcDis != null) {

						for (int j = 0; j < rsCpntEtcDis.getRecordCount(); j++) {

							etcDisMap = rsCpntEtcDis.getRecordMap(j);
							iOutQty = 0;
							iAddQty = (int) Double.parseDouble(etcDisMap.get(
									"add_qty").toString());

							if (iAddQty > 0) {
								//기타재고 처리
								etcDisMap.put("out_qty", iAddQty);

								update("DISINN01100.updateEtcDis", etcDisMap,
										onlineCtx);

								//기타재고입출력이력상세
								etcDisMap.put("inout_seq", "");
								etcDisMap.put("in_mgmt_no", "");
								etcDisMap.put("in_seq", "");
								etcDisMap.put("etc_seq", etcDisMap
										.get("etc_seq"));
								etcDisMap.put("out_mgmt_no", strOutMgmtNo);
								etcDisMap.put("out_seq", strOutMgmtSeq);
								etcDisMap.put("inout_dt", strCurrDt);
								etcDisMap.put("inout_cl", strInOutCl);
								etcDisMap.put("inout_dtl_cl", strInOutDtlCl);
								etcDisMap.put("prchs_plc_id", rsCpntDis.get(i,
										"prchs_plc_id"));
								etcDisMap.put("out_plc_id", rsCpntDis.get(i,
										"hld_plc_id"));
								etcDisMap.put("in_plc_id", rsCpntDis.get(i,
										"hld_plc_id"));
								etcDisMap.put("inout_qty", iAddQty);

								insert("DISINN01100.addEtcDisInOutHst",
										etcDisMap, onlineCtx);

								strOutMgmtSeq = this.getOutSeq(strOutMgmtNo,
										userInfo.getLoginId(), onlineCtx);

								// 출고상세 - 기타재고			
								etcDisMap.put("out_mgmt_no", strOutMgmtNo);
								etcDisMap.put("out_seq", strOutMgmtSeq);
								etcDisMap.put("prod_cd", etcDisMap
										.get("cpnt_prod_cd"));
								etcDisMap.put("color_cd", etcDisMap
										.get("cpnt_color_cd"));
								etcDisMap.put("ser_num", etcDisMap
										.get("cpnt_ser_num"));
								etcDisMap.put("eqp_st", "");
								etcDisMap.put("dis_st", "");
								etcDisMap.put("out_qty", iAddQty);
								etcDisMap.put("unit_prc", etcDisMap
										.get("dis_amt"));
								etcDisMap.put("amt", etcDisMap.get("sale_prc"));

								insert("DISINN01100.addOutDtl", etcDisMap,
										onlineCtx);

							}
						}
					}
					//log.debug("상품재고");					
					// 3. 상품재고
					int insertProdCount = 0;
					Map saveProdDis = new HashMap(); // 상품재고
					Map saveProdUnitPrc = new HashMap(); // 상품단가
					Map saveProdCpnt = new HashMap(); // 상품구성품
					Map saveProdInoutHst = new HashMap(); // 상품입출고이력
					String sProdSerNum = ""; // 상품일련번호(new)

					if (rsCpntDis != null) {

						// 상품 일련번호(ser_num) 채번
						Map mSerNum = new HashMap();
						mSerNum.put("prod_cl", "1"); // 상품구분
						mSerNum.put("prod_cd", rsCpntDis.get(i, "prod_cd")); // 상품코드
						mSerNum.put("ser_num", "");

						sProdSerNum = this.getProdSerNum(mSerNum, onlineCtx);

						// 상품재고  저장			
						saveProdDis.put("prod_cd", rsCpntDis.get(i, "prod_cd")); // 상품코드
						saveProdDis.put("color_cd", rsCpntDis
								.get(i, "color_cd")); // 색상코드
						saveProdDis.put("ser_num", sProdSerNum); // 일련번호(New) 
						saveProdDis.put("old_ser_num", rsCpntDis.get(i,
								"ser_num")); // 일련번호(Old) 
						saveProdDis.put("hld_plc_id", rsCpntDis.get(i,
								"hld_plc_id"));// 보유처
						saveProdDis.put("dis_st", "01"); // 재고상태 : 가용
						saveProdDis.put("eqp_st", rsCpntDis.get(i, "eqp_st")); // 단말기등급
						saveProdDis.put("dis_amt", rsCpntDis.get(i, "dis_amt")); // 재고금액
						saveProdDis.put("last_inout_dt", strCurrDt); // 최종입출고일자                       
						saveProdDis.put("last_inout_cl", "100"); // 최종입출고구분 
						saveProdDis.put("last_inout_dtl_cl", "114"); // 최종입출고상세구분
						saveProdDis.put("fst_in_fix_dt", ""); // 최초입고확정일   
						saveProdDis.put("fst_in_plc_id", rsCpntDis.get(i,
								"hld_plc_id"));// 최초입고처
						saveProdDis.put("fst_prchs_plc_id", rsCpntDis.get(i,
								"prchs_plc_id")); // 최초매입처
						saveProdDis.put("last_mov_in_dt", strCurrDt); // 최종이동입고일
						saveProdDis.put("fix_sale_amt", rsCpntDis.get(i,
								"dis_amt")); // 확정판매금액
						saveProdDis.put("fix_sale_dt", ""); // 확정여부
						saveProdDis.put("sale_yn", "N"); // 확정여부
						saveProdDis.put("rmks", rsCpntDis.get(i, "rmks"));
						saveProdDis.put("con_mgmt_no", rsCpntDis.get(i, "con_mgmt_no")); //상담관리번호
						
						//log.debug("DISINN01100.addProdDis s");
						insert("DISINN01100.addProdDis", saveProdDis, onlineCtx);
						//log.debug("DISINN01100.addProdDis e");
						//log.debug("4.상품단가 -상품단가는 단말기만 관리함");
						// 4. 상품단가 - 상품단가는 단말기만 관리함.
						saveProdUnitPrc.put("prod_cd", rsCpntDis.get(i,
								"prod_cd"));
						saveProdUnitPrc.put("color_cd", rsCpntDis.get(i,
								"color_cd"));
						saveProdUnitPrc.put("ser_num", sProdSerNum);
						saveProdUnitPrc.put("occr_cl", "01"); // 발생구분 : 01 
						saveProdUnitPrc.put("occr_dt", strCurrDt);
						saveProdUnitPrc.put("amt", rsCpntDis.get(i, "dis_amt"));
						//log.debug("DISINN01100.addProdUnitPrc s");
						insert("DISINN01100.addProdUnitPrc", saveProdUnitPrc,
								onlineCtx);
						//log.debug("DISINN01100.addProdUnitPrc e");
						//log.debug("5.구성품");
						// 5. 구성품
						// 배터리 - tdis_dis의 수량
						// 젠터,기타 - tdis_etc_dis_inout_hst의 수량 참조해서 처리해야함.
						if (rsCpntEtcDis != null) {

							for (int j = 0; j < rsCpntEtcDis.getRecordCount(); j++) {
								etcDisMap = rsCpntEtcDis.getRecordMap(j);

								iPrchsQty = (int) Double.parseDouble(etcDisMap
										.get("prchs_qty").toString());
								iAddQty = (int) Double.parseDouble(etcDisMap
										.get("add_qty").toString());
								iOutQty = iPrchsQty + iAddQty;

								saveProdCpnt.put("prod_cd", rsCpntDis.get(i,
										"prod_cd"));
								saveProdCpnt.put("color_cd", rsCpntDis.get(i,
										"color_cd"));
								saveProdCpnt.put("ser_num", sProdSerNum);
								saveProdCpnt.put("seq", ""); // 순번 : 일련번호 

								saveProdCpnt.put("cpnt_prod_cd", etcDisMap
										.get("cpnt_prod_cd"));
								saveProdCpnt.put("cpnt_color_cd", etcDisMap
										.get("cpnt_color_cd"));
								saveProdCpnt.put("cpnt_ser_num", etcDisMap
										.get("cpnt_ser_num"));
								saveProdCpnt.put("cpnt_etc_seq", etcDisMap
										.get("cpnt_etc_seq"));

								saveProdCpnt.put("in_qty", iOutQty);
								saveProdCpnt.put("cncl_yn", "N");
								//log.debug("DISINN01100.addProdCpnt s");
								insert("DISINN01100.addProdCpnt", saveProdCpnt,
										onlineCtx);
								//log.debug("DISINN01100.addProdCpnt e");								
							}
						}
						//log.debug("6.상품입출고이력");
						// 6. 상품입출고이력
						saveProdInoutHst.put("prod_cd", rsCpntDis.get(i,
								"prod_cd"));
						saveProdInoutHst.put("color_cd", rsCpntDis.get(i,
								"color_cd"));
						saveProdInoutHst.put("ser_num", sProdSerNum);
						saveProdInoutHst.put("inout_seq", "");
						saveProdInoutHst.put("in_mgmt_no", "");
						saveProdInoutHst.put("in_seq", "");
						saveProdInoutHst.put("out_mgmt_no", "");
						saveProdInoutHst.put("out_seq", "");
						saveProdInoutHst.put("inout_dt", strCurrDt);
						saveProdInoutHst.put("inout_cl", "100");
						saveProdInoutHst.put("inout_dtl_cl", "114");
						saveProdInoutHst.put("prchs_plc_id", rsCpntDis.get(i,
								"prchs_plc_id"));
						saveProdInoutHst.put("out_plc_id", rsCpntDis.get(i,
								"hld_plc_id"));
						saveProdInoutHst.put("in_plc_id", rsCpntDis.get(i,
								"hld_plc_id"));
						//log.debug("DISINN01100.addProdInoutHst s");
						insert("DISINN01100.addProdInoutHst", saveProdInoutHst,
								onlineCtx);
						//log.debug("DISINN01100.addProdInoutHst e");
					}
				}
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });
	}
}