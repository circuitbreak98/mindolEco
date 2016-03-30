/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.atr.biz;

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

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-10-04 16:57:02</li>
 * </ul>
 *
 * @author 전희경 (jeonheekyung)
 */
public class DISATR00700 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전희경 (jeonheekyung)
	 *  - 이동 시킬 재고 상품들을 조회한다.
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : out_plc_id [출고처ID] 
	 *	- field : ser_num [일련번호] 
	 *	- field : prod_cd [상품코드] 
	 *	- field : prod_cl [상품구분] 
	 *	- field : mfact_id [제조사ID] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : inProdList
	 *		- field : prod_cl [상품구분] 
	 *		- field : mfact_id [제조사ID] 
	 *		- field : prod_cd [상품코드] 
	 *		- field : color_cd [색상코드] 
	 *		- field : ser_num [일련번호] 
	 *		- field : bad_yn [불량여부] 
	 *		- field : dis_st [재고상태] 
	 *		- field : cur_cnt [현재고] 
	 *		- field : mov_cnt [이동재고] 
	 *		- field : mov_chk [선택여부] 
	 *		- field : out_cl [출고구분] 
	 *		- field : mov_seq [이동순번] 
	 *		- field : out_fix_yn [출고확정여부] 
	 *		- field : in_fix_yn [입고확정여부] 
	 */
	public IDataSet getInProdList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("getInProdList method start");
		}

		IRecordSet rs = queryForRecordSet("DISATR00700.selectInProdList",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("InProdList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		result.putRecordSet("InProdList", rs);

		return result;
	}

	/**
	 * 
	 *
	 * @author 전희경 (jeonheekyung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public IDataSet getMovProdListByExcel(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISATR00700.getMovProdListByExcel method start");
		}
		
		IRecordSet rsXlsList = requestData.getRecordSet("ds_upLoadXlsList");
		IRecordSet rsOutPlc = requestData.getRecordSet("ds_outplc");
		Map mOutPlc = rsOutPlc.getRecordMap(0);

		if (rsXlsList != null) {

			IRecord ir = null;
			String serNum = null;
			Map rsProdMap = null;
			Map rsSerNumMap = null;
			Map rsIFMap = null;
			String sDisCnt = null;
			HashMap hmResult = null;
			String sPROD_CL = "1"; //단말기
			String sYesValue = "Y";
			String sNoValue = "N";
			String sZero = "0";
			String sProdCd = ""; //상품코드
			String sProdCl = ""; //상품구분코드
			String sProdClNm = ""; //상품구분명
			String sProdNm = ""; //상품명
			String sColorNm = ""; //색상명
			String sFactNm = ""; //제조사명
			String sInQty = "1"; //상품수량
			String sBadYn = ""; //불량여부
			String sDisSt = ""; //재고상태
			String sUpdCnt = ""; //Update Count
			String sBarCdTyp = ""; //바코드유형
			String sFactId = ""; //제조사ID
			String sSaleUnitPrc = "";
			String sSalePrc = "";
			String sSaleMar = "";
			String sSaleCmms = "";
			String sSplstYn = "";
			String sSaleVatPrc = "";
			String sSaleAmt = "";
			String sSaleChgHstCl = "";
			String sConMgmtNo = "";
			String sSaleTypCd = mOutPlc.get("sale_typ_cd") == null ? "" : mOutPlc.get("sale_typ_cd").toString();
			
			
			Map mColorMap = null;
			
			/*********************************************
			 * 1. 모델조회.
			 *********************************************/			
			
			IRecordSet rsProdList = queryForRecordSet("DISATR00700.getMovProdList", requestData
					.getFieldMap(), onlineCtx);

			if (rsProdList == null) {
				throw new BizRuntimeException("시스템에 등록된 상품 정보가 없습니다. 기준정보의 상품관리를 확인하세요. ");
			}			
			
			/*********************************************
			 * 2. 색상조회.
			 *********************************************/			
			
			IRecordSet rsColorList = queryForRecordSet("DISATR00700.getMovColorList", requestData
					.getFieldMap(), onlineCtx);

			if (rsColorList == null) {
				throw new BizRuntimeException("시스템에 등록된 색상 정보가 없습니다. 기준정보의 상품관리를 확인하세요. ");
			}			
			
			/*********************************************
			 * 3. 모델을 맵에 담는다.(검증시 사용.)
			 *********************************************/	
			Map mAllProd = new HashMap();
			Map mProd = null;
			for(int k = 0 ; k < rsProdList.getRecordCount() ; k++){
				mProd = rsProdList.getRecordMap(k);
				mAllProd.put(mProd.get("prod_cd"), mProd);
			}
			
			/*********************************************
			 * 4. 색상을 맵에 담는다.(검증시 사용.)
			 *********************************************/	
			Map mAllColor = new HashMap();
			Map mColor = null;
			String sKey = "";
			for(int k = 0 ; k < rsColorList.getRecordCount() ; k++){
				mColor = rsColorList.getRecordMap(k);
				sKey = (String)mColor.get("prod_cd") + (String)mColor.get("color_cd");
				mAllColor.put(sKey, mColor);
			}		
			
			for (int i = 0; i < rsXlsList.getRecordCount(); i++) {

				ir = rsXlsList.getRecord(i);

				/*********************************************
				 * 5. 값이 존재 하는 지 체크하여 메세지를 셋팅한다.
				 *********************************************/
				hmResult = this.checkContent(ir);

				if ((hmResult.get("validateYN")).equals(sNoValue)) {
					ir.set("err_desc", "엑셀파일에 " + hmResult.get("msg")
							+ "값이 누락되었습니다.");
					continue;
				}

				// 모델코드 미존재 시
				if(!mAllProd.containsKey(ir.get("prod_cd"))){
					ir.set("err_desc", "존재하지 않는 상품입니다.");
					continue;					
				}
				
				rsProdMap = new HashMap();
				rsProdMap.putAll((Map) mAllProd.get(ir.get("prod_cd")));				

				sProdCl = String.valueOf(rsProdMap.get("PROD_CL"));
				ir.set("prod_cl", sProdCl); //상품 구분 코드 셋팅.

				sProdClNm = String.valueOf(rsProdMap.get("PROD_CL_NM"));
				ir.set("prod_cl_nm", sProdClNm); //상품 구분 코드 셋팅.

				sProdCd = String.valueOf(rsProdMap.get("PROD_CD"));
				ir.set("prod_cd", sProdCd); //상품 모델 코드 셋팅.

				sProdNm = String.valueOf(rsProdMap.get("PROD_NM"));
				ir.set("prod_nm", sProdNm); //상품명 셋팅.
				
				sFactId = String.valueOf(rsProdMap.get("MFACT_ID"));
				ir.set("mfact_id", sFactId); //제조사ID.
				
				sFactNm = String.valueOf(rsProdMap.get("MFACT_NM"));
				ir.set("mfact_nm", sFactNm); //제조사명.
				
				sBarCdTyp = String.valueOf(rsProdMap.get("BAR_CD_TYP"));
				ir.set("bar_cd_typ", sBarCdTyp); //바코드유형.
				
				ir.set("in_qty", sInQty); //수량
				ir.set("out_qty", sInQty); //수량
				ir.set("chk", ""); //check
				ir.set("fix_yn", "N"); //입고확정여부
				//사용여부가 'N'일 경우
				if (rsProdMap.get("USE_YN") == null
						|| !rsProdMap.get("USE_YN").equals(sYesValue)) {

					ir.set("err_desc", "사용중지인 모델입니다.");
					continue;
				}

				// 색상코드 미존재 시
				/*
				if(!mAllColor.containsKey(ir.get("prod_cd")+ir.get("color_cd"))){
					ir.set("err_desc", "색상이 존재 하지 않습니다.");
					continue;					
				}
				*/				
				mColorMap = new HashMap();
				
				if(mAllColor.containsKey(ir.get("prod_cd")+ir.get("color_cd"))){
					mColorMap.putAll((Map)(mAllColor.get(ir.get("prod_cd")+ir.get("color_cd"))));
				}
				
				if(mColorMap!= null){
					sColorNm = mColorMap.get("COLOR_NM") == null ? "" : mColorMap
							.get("COLOR_NM").toString();
				}
				ir.set("color_nm", sColorNm); //색상명 셋팅.	
				/*********************************************
				 * 6. 일련번호 포멧팅.
				 * I Phone 일련번호 11자리 sBarCdTyp : 4
				 *********************************************/

				/*if (ir.get("prod_cl").equals(sPROD_CL)) {
					if("4".equals(sBarCdTyp)){
						serNum = this.setSerNumFormat(ir.get("ser_num"), 11);
					}else{
						serNum = this.setSerNumFormat(ir.get("ser_num"), 7);
					}
				} else {
					serNum = ir.get("ser_num");
				}*/
				serNum = ir.get("ser_num");
				ir.set("ser_num", serNum);
				ir.set("end_ser_num", serNum);
				/*********************************************
				 * 7. 일련번호 길이 check 및 포멧팅.
				 * 상품의 일련번호 길이가 7자리인 경우는 숫자만 가능 하다.
				 *********************************************/
				//일련번호  number check
//				if(serNum.length() == 7 || serNum.length() == 8){
//					try {
//	
//						Long.parseLong(ir.get("ser_num"));
//	
//					} catch (NumberFormatException e) {
//						ir.set("err_desc", "잘못된 일련번호 형식입니다.");
//						continue;
//					}
//				}

				/*if (ir.get("prod_cl").equals(sPROD_CL)) {
					//일련번호 자리수 체크. 7자리.
					// BAR_CD_TYP에 따라 분류
					if("4".equals(sBarCdTyp)){
						if (serNum.length() != 11) {
							ir.set("err_desc", "상품의 일련번호를 확인하세요1.");
							continue;
						}
					}else{
						if (serNum.length() != 8) {
							ir.set("err_desc", "상품의 일련번호를 확인하세요2." );
							continue;
						}
					}
				} else {
					//일련번호 자리수 체크. 7자리.
					if (serNum.length() != 8) {
						ir.set("err_desc", "상품의 일련번호를 확인하세요.");
						continue;
					}
				}*/
				//일련번호 자리수 체크. 7자리.
				if (serNum.length() != 8) {
					ir.set("err_desc", "상품의 일련번호를 확인하세요.");
					continue;
				}
				/*********************************************
				 * 8. 일련번호 체크
				 *********************************************/
				rsProdMap.put("prod_cd", ir.get("prod_cd"));
				rsProdMap.put("color_cd", ir.get("color_cd"));
				rsProdMap.put("ser_num", serNum);
				rsProdMap.put("bar_cd_typ", sBarCdTyp);
				rsProdMap.put("out_plc_id", mOutPlc.get("out_plc_id"));
				rsProdMap.put("sale_dtm", mOutPlc.get("sale_dtm"));
				rsProdMap.put("sale_typ_cd", sSaleTypCd);
				
				
				rsIFMap = (Map) queryForObject("DISATR00700.getMovProdSt",
						rsProdMap, onlineCtx);

				if (!"N".equals(rsIFMap.get("TRMS_ST"))&&"N".equals(rsIFMap.get("TRMS_YN"))) {
					ir.set("err_desc", "I/F 중인 데이터 입니다.");
					continue;
				}		
				
				
				
				rsSerNumMap = (Map) queryForObject("DISATR00700.getMovProdListByExcel",
						rsProdMap, onlineCtx);

				if (rsSerNumMap == null) {
					ir.set("err_desc", "재고에 입력된 일련번호로 등록된 상품이 없습니다.");
					continue;
				}			
				
				
				/*********************************************
				 * 9. 판매가 check 및 포멧팅.
				 * 판매가 입력하지 않은경우 계산하여 가져온다
				 *********************************************/
				if("04".equals(sSaleTypCd)){   // 위탁판매
					
					sSaleAmt = String.valueOf(rsSerNumMap.get("FIX_SALE_AMT"));
					
				}else{
					if(ir.get("sale_amt") == null){
						
						sSaleAmt = String.valueOf(rsSerNumMap.get("SALE_AMT"));
					
					}else{
						sSaleAmt = ir.get("sale_amt").replace(",", "");
						
						try {
							Long.parseLong(ir.get("sale_amt"));
		
						} catch (NumberFormatException e) {
							ir.set("err_desc", "잘못된 판매가 형식입니다.");
							continue;
						}
						
					}
					
				}
				
				ir.set("old_ser_num", String.valueOf(rsSerNumMap.get("OLD_SER_NUM")));
				ir.set("eqp_st", String.valueOf(rsSerNumMap.get("EQP_ST")));
				ir.set("hld_plc_nm", String.valueOf(rsSerNumMap.get("HLD_PLC_NM")));
				ir.set("mktg_dt", String.valueOf(rsSerNumMap.get("MKTG_DT")));
				ir.set("mov_qty", String.valueOf(rsSerNumMap.get("MOV_QTY")));
				ir.set("sale_qty", String.valueOf(rsSerNumMap.get("MOV_QTY")));
				
				sDisSt = String.valueOf(rsSerNumMap.get("DIS_ST"));
				ir.set("dis_st", sDisSt);
				
				sSaleChgHstCl = String.valueOf(rsSerNumMap.get("SALE_CHG_HST_CL"));
				ir.set("sale_chg_hst_cl", sSaleChgHstCl);
				
				sUpdCnt = String.valueOf(rsSerNumMap.get("UPD_CNT"));				
				ir.set("upd_cnt", sUpdCnt);
				
				sSaleUnitPrc = String.valueOf(rsSerNumMap.get("SALE_UNIT_PRC"));				
				ir.set("sale_unit_prc", sSaleUnitPrc);
				
				sSalePrc = String.valueOf(rsSerNumMap.get("SALE_PRC"));				
				ir.set("sale_prc", sSalePrc);
				
				sSaleMar = String.valueOf(rsSerNumMap.get("SALE_MAR"));				
				ir.set("sale_mar", sSaleMar);
				
				sSaleCmms = String.valueOf(rsSerNumMap.get("SALE_CMMS"));				
				ir.set("sale_cmms", sSaleCmms);
				
				sSplstYn = String.valueOf(rsSerNumMap.get("SPLST_YN"));				
				ir.set("splst_yn", sSplstYn);
				
				sSaleVatPrc = String.valueOf(rsSerNumMap.get("SALE_VAT_PRC"));				
				ir.set("sale_vat_prc", sSaleVatPrc);
				
				ir.set("sale_amt", sSaleAmt);

				ir.set("unit_prc", "");
				
				ir.set("order_key", ir.get("prod_cd")+ir.get("ser_num"));
				
				sConMgmtNo = String.valueOf(rsSerNumMap.get("CON_MGMT_NO"));				
				ir.set("con_mgmt_no", sConMgmtNo);
				
				ir.set("err_desc", "");
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsXlsList.getRecordCount()) });
		responseData.putRecordSet("ds_upLoadXlsList", rsXlsList);

		//log.debug("END TIME : "+DateUtils.getDefaultCurrentDateTime());
		
		return responseData;
	}

	/**
	 * @author 한병곤
	 * - 업로드 파일에 값이 존재하는지 체크하여 결과를 return한다.
	 * @param ir
	 * @return HashMap (메시지, 검사결과)
	 */
	@SuppressWarnings("unchecked")
	private HashMap checkContent(IRecord ir) {

		StringBuffer sbMsg = new StringBuffer();
		HashMap hmReturnMap = new HashMap();
		String sValidateYN = "Y";
		String sNoValue = "N";

		/*if (ir.get("prod_nm") == null) {
			sbMsg.append("상품명");
			sValidateYN = sNoValue;
		}

		if (ir.get("color_nm_org") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("색상명");
			sValidateYN = sNoValue;
		}*/

		if (ir.get("color_cd") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("색상별코드");
			sValidateYN = sNoValue;
		}

		if (ir.get("ser_num") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("일련번호");
			sValidateYN = sNoValue;
		}

		hmReturnMap.put("msg", sbMsg.toString());
		hmReturnMap.put("validateYN", sValidateYN);

		return hmReturnMap;

	}
	
	/**
	 * @author 한병곤
	 * - 일련번호를 자리수에 맡게 포멧팅한다.
	 * @param serNum
	 * @param formatSize
	 * @return String
	 */
	private String setSerNumFormat(String serNum, int formatSize) {

		int addNum = formatSize - serNum.length();

		for (int j = 0; j < addNum; j++) {
			serNum = "0" + serNum;
		}

		return serNum;
	}
}