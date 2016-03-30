/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.cst.adv.biz;

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

import org.apache.commons.logging.Log;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-02-23 13:55:44</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class CSTADV00810 extends BaseBizUnit {

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
	public IDataSet saveConsultMgmtXlsIn(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("CSTADV00810.saveConsultMgmtXlsIn method start");
		}

		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");
		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);
		IRecordSet iSet = itemp.getRecordSet("cptItem");

		IRecord rec = iSet.newRecord();
		rec.add("ds_name", "ds_upLoadXlsList"); // 암호화 할 데이터셋 명
		rec.add("col_name1", "RES_NO"); // 암호화 컬럼1
		rec.add("col_name2", "TEL_NO"); // 암호화 컬럼2
		rec.add("col_name3", "ACC_NO"); // 암호화 컬럼3  

		iSet.addRecord(rec);
		requestData.putRecordSet("cptItem", iSet); // 암호화 정보 추가
		IDataSet rsData = bco.encode(requestData, onlineCtx);

		IRecordSet xls = rsData.getRecordSet("ds_upLoadXlsList");

		//		IRecordSet xls = requestData.getRecordSet("ds_upLoadXlsList");

		Map mMgmt = null;
		Map mProdDtl = null;
		Map mConNo = null;
		String sConNo = "";
		String sConPlcCd = "";

		if (xls != null) {
			for (int i = 0; i < xls.getRecordCount(); i++) {

				mMgmt = xls.getRecordMap(i);
				sConPlcCd = mMgmt.get("CON_PLC_CD").toString();
				if ("11030".equals(sConPlcCd)) {
					sConNo = "T" + mMgmt.get("T_MGMT_NO").toString();
				} else if ("11050".equals(sConPlcCd)) {
					sConNo = "U" + mMgmt.get("T_MGMT_NO").toString();
				} else {
					mConNo = (Map) queryForObject("CSTADV00200.getConMgmtNo",
							mMgmt, onlineCtx);
					sConNo = mConNo.get("CON_MGMT_NO").toString();
				}
				IRecordSet rsProdDtl = queryForRecordSet(
						"CSTADV00600.getProdDtl", mMgmt);
				for (int j = 0; j < rsProdDtl.getRecordCount(); j++) {

					mProdDtl = rsProdDtl.getRecordMap(j);
					mProdDtl.put("CON_MGMT_NO", sConNo);

					if (mProdDtl.get("DTL_CL").toString().equals("1")) {
						if (Integer.parseInt(mMgmt.get("BATTERY").toString()) > 0) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("2")) {
						if (Integer.parseInt(mMgmt.get("BATTERY").toString()) > 1) {
							mProdDtl.put("HLD_QTY", Integer.parseInt(mMgmt.get(
									"BATTERY").toString()) - 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("4")) {
						if (mMgmt.get("CHARGER").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("5")) {
						if (mMgmt.get("ZENDER").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("6")) {
						if (mMgmt.get("EARPHONES").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("6")) {
						if (mMgmt.get("EARPHONES").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("7")) {
						if (mMgmt.get("CASE").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("8")) {
						if (mMgmt.get("USB").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					if (mProdDtl.get("DTL_CL").toString().equals("9")) {
						if (mMgmt.get("MEMORY").toString().endsWith("Y")) {
							mProdDtl.put("HLD_QTY", 1);
						}
					}
					insert("CSTADV00600.addConsultDtlXls", mProdDtl, onlineCtx);

				}

				mMgmt.put("CON_MGMT_NO", sConNo);
				//				mMgmt.put("CON_MGMT_NO", mConNo.get("CON_MGMT_NO"));
				insert("CSTADV00810.addConsultMgmtXlsIn", mMgmt, onlineCtx);

			}
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });
	}

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getErrorCheckXlsForInn(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		IRecordSet rsXlsList = requestData.getRecordSet("ds_upLoadXlsList");
		IRecord ir = null;
		
		Map mXls = null;
		Map mCustNm = null;
		Map rsProdMap = null;
		Map mColorMap = null;
		Map mConPlcMap = null;
		Map mGradeMap = null;
		Map mBankMap = null;
		IRecordSet rsConsultList = null;
		
		String sProdCd = ""; //상품코드
		String sProdNm = ""; //상품명
		String sColorCd = ""; //색상코드 
		String sColorNm = ""; //색상명
		String sSerNum = "";	//일련번호
		String sCustNm = "";	//고객명
		String sConPlcCd = "";	//상담처코드
		String sConPlcNm = "";	//상담처명
		String sGradeCd = "";	//등급
		String sPrchsAmt = "";	//매입가 
		String sBankCd	= "";	//은행코드 
		String sBankNm	= "";	//은행명 
		String sImei	= "";	//IMEI
		
		if (rsXlsList != null) {
			
			HashMap hmResult = null;
			String sNoValue = "N";
			
			/*********************************************
			 * 1. 모델조회.
			 *********************************************/			
			IRecordSet rsProdList = queryForRecordSet("CSTADV00810.getProdList", requestData
					.getFieldMap(), onlineCtx);

			if (rsProdList == null) {
				throw new BizRuntimeException("시스템에 등록된 상품 정보가 없습니다. 기준정보의 상품관리를 확인하세요. ");
			}			
			
			/*********************************************
			 * 2. 색상조회.
			 *********************************************/			
			IRecordSet rsColorList = queryForRecordSet("CSTADV00810.getColorList", requestData
					.getFieldMap(), onlineCtx);

			if (rsColorList == null) {
				throw new BizRuntimeException("시스템에 등록된 색상 정보가 없습니다. 기준정보의 상품관리를 확인하세요. ");
			}			
			
			/*********************************************
			 * 3. 거래처조회.
			 *********************************************/			
			IRecordSet rsDealList = queryForRecordSet("CSTADV00810.getDealList", requestData
					.getFieldMap(), onlineCtx);

			if (rsDealList == null) {
				throw new BizRuntimeException("시스템에 등록된 거래처 정보가 없습니다. 기준정보의 거래처관리를 확인하세요. ");
			}	
			
			/*********************************************
			 * 4. 단말기 등급 조회.
			 *********************************************/			
			IRecordSet rsGradeList = queryForRecordSet("CSTADV00810.getGradeList", requestData
					.getFieldMap(), onlineCtx);

			if (rsGradeList == null) {
				throw new BizRuntimeException("시스템에 등록된 등급 정보가 없습니다. 기준정보를 확인하세요. ");
			}	
			
			/*********************************************
			 * 5. 은행코드 조회.
			 *********************************************/			
			IRecordSet rsBankList = queryForRecordSet("CSTADV00810.getBankList", requestData
					.getFieldMap(), onlineCtx);

			if (rsBankList == null) {
				throw new BizRuntimeException("시스템에 등록된 은행 정보가 없습니다. 기준정보를 확인하세요. ");
			}	
			
			/*********************************************
			 * 6. 모델을 맵에 담는다.(검증시 사용.)
			 *********************************************/	
			Map mAllProd = new HashMap();
			Map mProd = null;
			for(int k = 0 ; k < rsProdList.getRecordCount() ; k++){
				mProd = rsProdList.getRecordMap(k);
				mAllProd.put(mProd.get("prod_cd"), mProd);
			}

			/*********************************************
			 * 7. 색상을 맵에 담는다.(검증시 사용.)
			 *********************************************/	
			Map mAllColor = new HashMap();
			Map mColor = null;
			String sKey = "";
			for(int k = 0 ; k < rsColorList.getRecordCount() ; k++){
				mColor = rsColorList.getRecordMap(k);
				sKey = (String)mColor.get("prod_cd") + (String)mColor.get("color_cd");
				mAllColor.put(sKey, mColor);
			}		

			/*********************************************
			 * 8. 거래처를 맵에 담는다.(검증시 사용.)
			 *********************************************/	
			Map mAllDeal = new HashMap();
			Map mDeal = null;
			for(int k = 0 ; k < rsDealList.getRecordCount() ; k++){
				mDeal = rsDealList.getRecordMap(k);
				mAllDeal.put(mDeal.get("deal_co_cd"), mDeal);
			}

			/*********************************************
			 * 9. 거래처를 맵에 담는다.(검증시 사용.)
			 *********************************************/	
			Map mAllGrade = new HashMap();
			Map mGrade = null;
			for(int k = 0 ; k < rsGradeList.getRecordCount() ; k++){
				mGrade = rsGradeList.getRecordMap(k);
				mAllGrade.put(mGrade.get("comm_cd_val"), mGrade);
			}

			/*********************************************
			 * 10. 거래처를 맵에 담는다.(검증시 사용.)
			 *********************************************/	
			Map mAllBank = new HashMap();
			Map mBank = null;
			for(int k = 0 ; k < rsBankList.getRecordCount() ; k++){
				mBank = rsBankList.getRecordMap(k);
				mAllBank.put(mBank.get("comm_cd_val"), mGrade);
			}

			for (int i = 0; i < rsXlsList.getRecordCount(); i++) {
				ir = rsXlsList.getRecord(i);

				/*********************************************
				 * 11. 값이 존재 하는 지 체크하여 메세지를 셋팅한다.
				 *********************************************/
				hmResult = this.checkContent(ir);

				if ((hmResult.get("validateYN")).equals(sNoValue)) {
					ir.set("err_desc", "엑셀파일에 " + hmResult.get("msg")
							+ "값이 누락되었습니다.");
					continue;
				}

				/*********************************************
				 * 12. 상품코드 검증
				 *********************************************/
				//	모델코드 미존재 시
				if(!mAllProd.containsKey(ir.get("prod_cd"))){
					ir.set("err_desc", "존재하지 않는 상품입니다.");
					continue;					
				}
				
				rsProdMap = new HashMap();
				rsProdMap.putAll((Map) mAllProd.get(ir.get("prod_cd")));				

				sProdCd = String.valueOf(rsProdMap.get("PROD_CD"));
				ir.set("prod_cd", sProdCd); //상품 모델 코드 셋팅.

				sProdNm = String.valueOf(rsProdMap.get("PROD_NM"));
				ir.set("prod_nm", sProdNm); //상품명 셋팅.
				
				/*********************************************
				 * 13. 일련번호
				 *********************************************/
				sSerNum = ir.get("ser_num");
				if(sSerNum.length() > 30){
					ir.set("err_desc", "일련번호 입력가능 자릿수 초과입니다.");
					continue;
				}
				ir.set("ser_num", sSerNum);

				/*********************************************
				 * 14. 고객명
				 *********************************************/
				sCustNm = ir.get("cust_nm");
				if(sCustNm.length() > 50){
					ir.set("err_desc", "고객명 입력가능 자릿수 초과입니다.");
					continue;
				}
				ir.set("cust_nm", sCustNm);

				/*********************************************
				 * 15. 상담처 코드
				 *********************************************/
				if(!mAllDeal.containsKey(ir.get("con_plc_cd"))){
					ir.set("err_desc", "존재하지 않는 거래처 입니다.");
					continue;					
				}
				
				mConPlcMap = new HashMap();
				mConPlcMap.putAll((Map) mAllDeal.get(ir.get("con_plc_cd")));				
				
				sConPlcCd = String.valueOf(mConPlcMap.get("DEAL_CO_CD"));
				ir.set("con_plc_cd", sConPlcCd); //상품 모델 코드 셋팅.
				
				sConPlcNm = String.valueOf(mConPlcMap.get("DEAL_CO_NM"));
				ir.set("con_plc_nm", sConPlcNm); //상품명 셋팅.

				/*********************************************
				 * 16. 단말기 등급
				 *********************************************/
				if(!mAllGrade.containsKey(ir.get("eqp_st"))){
					ir.set("err_desc", "존재하지 않는 등급코드 입니다.");
					continue;					
				}
				ir.set("eqp_st", ir.get("eqp_st"));

				/*********************************************
				 * 17. 색상코드 검증
				 *********************************************/
				mColorMap = new HashMap();
				if(mAllColor.containsKey(ir.get("prod_cd")+ir.get("color_cd"))){
					mColorMap.putAll((Map)(mAllColor.get(ir.get("prod_cd")+ir.get("color_cd"))));
				}
				if(mColorMap!= null){
					sColorNm = mColorMap.get("COLOR_NM") == null ? "" : mColorMap
							.get("COLOR_NM").toString();
					sColorCd = ir.get("color_cd");
				}
				ir.set("color_nm", sColorNm); //색상명 셋팅.	

				/*********************************************
				 * 18. 매입가 check 및 포멧팅.
				 *********************************************/
				sPrchsAmt = ir.get("prchs_amt").replace(",", "");
				
				try {
					Long.parseLong(ir.get("prchs_amt"));

				} catch (NumberFormatException e) {
					ir.set("err_desc", "잘못된 매입가 형식입니다.");
					continue;
				}

				/*********************************************
				 * 19. 예금주명
				 *********************************************/
				sCustNm = ir.get("cust_nm");
				if(sCustNm.length() > 50){
					ir.set("err_desc", "고객명 입력가능 자릿수 초과입니다.");
					continue;
				}
				ir.set("cust_nm", sCustNm);
				
				/*********************************************
				 * 20. 은행코드
				 *********************************************/
				sBankCd = ir.get("bank_cd");
				if(sBankCd.length() == 2){
					sBankCd = "0"+sBankCd;
				}
				
				if(!mAllBank.containsKey(sBankCd)){
					ir.set("err_desc", "존재하지 않는 은행코드 입니다.");
					continue;					
				}
				
				mBankMap = new HashMap();
				mBankMap.putAll((Map) mAllBank.get(sBankCd));				

				sBankCd = String.valueOf(mBankMap.get("COMM_CD_VAL"));
				ir.set("bank_cd", sBankCd);

				sBankNm = String.valueOf(mBankMap.get("COMM_CD_VAL_NM"));
				ir.set("bank_nm", sBankNm); 
				
				/*********************************************
				 * 21. IMEI
				 *********************************************/
				sImei = ir.get("imei").replace(" ", "");
				if(sCustNm.length() > 15){
					ir.set("err_desc", "IMEI 입력가능 자릿수 초과입니다.");
					continue;
				}
				ir.set("imei", sImei);
				
				/*********************************************
				 * 22. 등록 유무 조회 
				 *********************************************/
				Map mProdMap = new HashMap();
				mProdMap.put("prod_cd", sProdCd);
				mProdMap.put("ser_num", sSerNum);
				mProdMap.put("color_cd", sColorCd);
				rsConsultList = queryForRecordSet("CSTADV00810.getConsultMgmt",
						mProdMap, onlineCtx);

				if (rsConsultList.getRecordCount() != 0) {
					ir.set("err_desc", "이미 등록된 단말기 입니다.");
					continue;
				}	
				
				ir.set("err_desc", "");
				
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsXlsList.getRecordCount()) });
		responseData.putRecordSet("ds_upLoadXlsList", rsXlsList);
		return responseData;
	}
	
	/**
	 * @author 전미량
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
		
		if (ir.get("prod_cd") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("상품코드");
			sValidateYN = sNoValue;
		}
		
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
		
		if (ir.get("con_dt") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("상담일자");
			sValidateYN = sNoValue;
		}
		
		if (ir.get("cust_nm") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("고객명");
			sValidateYN = sNoValue;
		}

		if (ir.get("con_plc_cd") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("상담처 코드");
			sValidateYN = sNoValue;
		}
		
		if (ir.get("eqp_st") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("단말기 등급");
			sValidateYN = sNoValue;
		}
		
		if (ir.get("prchs_amt") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("매입금액");
			sValidateYN = sNoValue;
		}
		
		hmReturnMap.put("msg", sbMsg.toString());
		hmReturnMap.put("validateYN", sValidateYN);

		return hmReturnMap;

	}

}