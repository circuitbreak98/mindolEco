/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.sal.bcs.biz;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordHeader;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.RecordHeader;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import com.sktst.bas.bco.ejb.BCO;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.common.user.PsLoginUserInfo;
import com.sktst.sal.sco.ejb.SCO;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2012-09-19 09:35:26</li>
 * </ul>
 *
 * @author 전미량 (jeonmiryang)
 */
public class SALBCS00300 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전미량 (jeonmiryang)
	 * - B2C 상품판매 엑셀 등록
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveProdSaleExcel(IDataSet requestData,
			IOnlineContext onlineCtx) throws Exception {
		
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {

			log.debug("saveProdSaleExcel method start");
		}

		Map mSale = new HashMap();
		Map mReq = new HashMap();
		String sGnrlSaleNo = "";
		IRecord ir = null;
		String sale_chg_hst_cl = "01";	/* 판매변경이력구분 (ZSAL_C_00310 : 01:판매 , 07:판매취소) */
		String cust_cl = "1";			/* 고객구분 (1:개인)								*/
		String settl_cond = "C";		/* 단말기결제조건 (C:현금, A:할부 ) 					*/
		String sale_typ_cd = "02";		/*  판매유형(01:B2B,02:B2C(지점),03:B2C(On-Line)) */
		int insCnt = 0;
		
		/*
		 * 암호화 시작
		 * 암호화 시작 -> 암호화 종료 소스 모두 복사 
		 * 변경 : 암호화 할 데이터셋 명/암호화 컬럼1/암호화 컬럼2
		 * 암호화 컬럼은 최대 6개 까지 확장 가능 : col_name1 ~ 6
		 */
		BCO bco = (BCO) lookupBizComponent("sktst.bas.BCO");
		
		IDataSet itemp = bco.getDataSet(requestData, onlineCtx);		
		IRecordSet iSet = itemp.getRecordSet("cptItem");
		
		IRecord rec = iSet.newRecord();
		rec.add("ds_name"	, "ds_upLoadXlsList");	// 암호화 할 데이터셋 명
		rec.add("col_name1"	, "CUST_BIZ_NUM"); 	// 암호화 컬럼1
		iSet.addRecord(rec);
		
		requestData.putRecordSet("cptItem", iSet);

		IDataSet rsData = bco.encode(requestData, onlineCtx);
		
		requestData = rsData;
		/*
		 * 암호화 종료 
		 */
		IRecordSet rsXlsList = requestData.getRecordSet("ds_upLoadXlsList");
		if (rsXlsList != null) {
			log.debug("rsXlsList.getRecordCount()======" + rsXlsList.getRecordCount() );
			for (int i = 0; i < rsXlsList.getRecordCount(); i++) {
				log.debug("i======" + i );
				ir = rsXlsList.getRecord(i);
				log.debug("ir======" + ir );
				
				mReq = getProMap(ir, onlineCtx);
				mSale.putAll(mReq);
				
				mSale.put("sale_gubun", "S"); 				// 일반판매번호 구분자 
				Map saleNo = (Map) queryForObject("SALBCS00200.getGnrlSaleNo", mSale, onlineCtx);
				sGnrlSaleNo = saleNo.get("GNRL_SALE_NO").toString();
				mSale.put("gnrl_sale_no", sGnrlSaleNo); 		// 일반판매번호
			
				Map rsInsertDefaultData = (Map)queryForObject("SALBCS00200.getInsertDefaultData",
						mSale, onlineCtx);
				log.debug("rsInsertDefaultData======" + rsInsertDefaultData );
				log.debug("gnrl_sale_chg_seq======" + rsInsertDefaultData.get("GNRL_SALE_CHG_SEQ") );
				mSale.put("gnrl_sale_chg_seq", rsInsertDefaultData.get("GNRL_SALE_CHG_SEQ"));
				
				mSale.put("sale_chg_hst_cl", sale_chg_hst_cl);
				
				mReq.put("deal_dtm",  mReq.get("sale_dtm").toString().substring(0,8)); 
				mReq.put("deal_co_cd",  mReq.get("sale_plc_id")); 
				
				
				IRecordSet rsGetStlPlc = queryForRecordSet("SALBCS00200.getDealStlPlcByDealCoCd",
						mReq, onlineCtx);
				mSale.put("stl_plc",rsGetStlPlc.get(0, "stl_plc"));	
				mSale.put("sale_plc",mReq.get("sale_plc_id"));	
				mSale.put("cust_cl", cust_cl);
				mSale.put("settl_cond", settl_cond);
				mSale.put("sale_typ_cd", sale_typ_cd);
				
				
				log.debug("mSale======" + mSale );
				log.debug("mReq======" + mReq );
				
				//	일반판매 테이블 등록 
				insert("SALBCS00200.InsertTsalGnrlSale", mSale, onlineCtx);

				//	단말기판매 테이블 등록 
				insert("SALBCS00200.InsertTsalEquipmentSale", mSale, onlineCtx);

				//	수납정보 테이블 등록 
				insert("SALBCS00200.InsertTsalPayment", mSale, onlineCtx);

				//	매출판매 테이블 등록 
				insert("SALBCS00200.InsertTsalSaleAmt", mSale, onlineCtx);
				
				
				insCnt++;
				
				/**
				 * 상품판매 재고처리
				 */
				
				SCO sco = (SCO) lookupBizComponent("sktst.sal.SCO");	// 재고처리 
				Map mDis = new HashMap();
				IDataSet oDsDisSale = new DataSet();
				
				/*
				 * mMaster 필드구성
				 * 	  #out_mgmt_no#		: 공통생성
					  #gnrl_sale_no#		: requestData.getField("gnrl_sale_no")
					  #gnrl_sale_chg_seq#	: requestData.getField("gnrl_sale_chg_seq")
					  #sale_dt#				: requestData.getField("sale_dtm")
					  #sale_plc#			: requestData.getField("sale_plc")
					  #rmks#				: requestData.getField("rmks")
				 */
				
				log.debug("======" );
				
				mDis.put("out_mgmt_no", "");
				mDis.put("out_seq", "1");
				mDis.put("gnrl_sale_no", mSale.get("gnrl_sale_no"));
				mDis.put("gnrl_sale_chg_seq", mSale.get("gnrl_sale_chg_seq"));
				mDis.put("sale_dtm", mSale.get("sale_dtm"));
				mDis.put("sale_plc", mSale.get("sale_plc"));
				mDis.put("rmks", mSale.get("rmks"));
				
				log.debug("mDis======" + mDis );
				
				oDsDisSale.putFieldObjectMap(mDis);

				/*
				 * mDisSale 필드구성
					#prod_cd#			: requestData.getRecordSet("itemList")
					#color_cd#			: requestData.getRecordSet("itemList")
					#ser_num#			: requestData.getRecordSet("itemList")
					#eqp_st#			: requestData.getRecordSet("itemList")
					#sale_unit_prc#		: requestData.getRecordSet("itemList")
					#sale_qty#			: requestData.getRecordSet("itemList")
					#sale_amt#			: requestData.getRecordSet("itemList")
					#dis_st#
					#sale_qty#
				    #settl_cond_cd#
				    #sale_unit_prc#
				    #sale_amt#
				*/ 
				
				IRecordSet rs = new RecordSet("ds_eqpInfo");
				
				for(int k = 0; k < rsXlsList.getHeaderCount(); k++){
					IRecordHeader iHeader = new RecordHeader(rsXlsList.getHeader(k).toString());
					rs.addHeader(iHeader);
				}
				
				rs.addRecord(ir);
				
				 oDsDisSale.putRecordSet("itemList", rs);
				 
				try {
					oDsDisSale = sco.addDisSaleOut(oDsDisSale, onlineCtx);
					
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
				
		}
		
		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
					"Insert:" + insCnt });
		return result;

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
	public IDataSet getSaleProdListByExcel(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("SALBCS00300.getSaleProdListByExcel method start");
		}
		
		IRecordSet rsXlsList = requestData.getRecordSet("ds_upLoadXlsList");
		IRecordSet rsOutPlc = requestData.getRecordSet("ds_outplc");
		Map mOutPlc = rsOutPlc.getRecordMap(0);
		
		
		if (rsXlsList != null) {

			IRecord ir = null;
			
			Map rsProdMap = null;
			Map rsSerNumMap = null;
			HashMap hmResult = null;
			
			String serNum = null;
			String sProdCd = ""; //상품코드
			String sProdNm = ""; //상품명
			String sBarCdTyp = ""; //바코드유형
			String sSaleUnitPrc = "";
			String sSalePrc = "";
			String sSaleMar = "";
			String sSaleCmms = "";
			String sSplstYn = "";
			String sSaleVatPrc = "";
			String sSaleAmt = "";
			String sSaleTypCd = "02";
			String sColorCd = "";
			String sColorNm = "";
			
			/*********************************************
			 * 1. 모델조회.
			 *********************************************/			
			
			IRecordSet rsProdList = queryForRecordSet("SALBCS00300.getProdList", requestData
					.getFieldMap(), onlineCtx);

			if (rsProdList == null) {
				throw new BizRuntimeException("시스템에 등록된 상품 정보가 없습니다. 기준정보의 상품관리를 확인하세요. ");
			}			
			
			/*********************************************
			 * 2. 색상조회.
			 *********************************************/			
			
			IRecordSet rsColorList = queryForRecordSet("SALBCS00300.getColorList", requestData
					.getFieldMap(), onlineCtx);

			if (rsColorList == null) {
				throw new BizRuntimeException("시스템에 등록된 색상 정보가 없습니다. 기준정보의 상품관리를 확인하세요. ");
			}			
			
			/*********************************************
			 * 3. 모델을 맵에 담는다.(검증시 사용.)
			 *********************************************/	
			Map mAllProd = new HashMap();
			Map mProd = null;
			Map mUser = new HashMap();
			
			for(int k = 0 ; k < rsProdList.getRecordCount() ; k++){
				mProd = rsProdList.getRecordMap(k);
				mAllProd.put(mProd.get("prod_cd"), mProd);
			}

			for (int i = 0; i < rsXlsList.getRecordCount(); i++) {
				ir = rsXlsList.getRecord(i);
				
				/*********************************************
				 * 4. 값이 존재 하는 지 체크하여 메세지를 셋팅한다.
				 *********************************************/
				
				hmResult = this.checkContent(ir);
				if ((hmResult.get("validateYN")).equals("N")) {
					ir.set("err_desc", "엑셀파일에 " + hmResult.get("msg")
							+ "값이 누락되었습니다.");
					continue;
				}
				
				/*********************************************
				 * 5. 판매일시 체크 
				 *********************************************/
				
				if(ir.get("sale_dtm").length() != 14){
					ir.set("err_desc", "잘못된 판매일시 형식입니다.");
				}
				
				/*********************************************
				 * 6. 상품정보 체크
				 *********************************************/

				if(!mAllProd.containsKey(ir.get("prod_cd"))){
					ir.set("err_desc", "존재하지 않는 상품입니다.");
					continue;					
				}
				rsProdMap = new HashMap();
				rsProdMap.putAll((Map) mAllProd.get(ir.get("prod_cd")));				

				sProdCd = String.valueOf(rsProdMap.get("PROD_CD"));
				ir.set("prod_cd", sProdCd); 

				sProdNm = String.valueOf(rsProdMap.get("PROD_NM"));
				ir.set("prod_nm", sProdNm); 
				
				sBarCdTyp = String.valueOf(rsProdMap.get("BAR_CD_TYP"));
				
				if (rsProdMap.get("USE_YN") == null
						|| !rsProdMap.get("USE_YN").equals("Y")) {

					ir.set("err_desc", "사용중지인 모델입니다.");
					continue;
				}
				
				/*********************************************
				 * 7. 일련번호 체크
				 *    I Phone 일련번호 11자리 sBarCdTyp : 4
				 *    상품의 일련번호 길이가 7자리인 경우는 숫자만 가능 하다.
				 *********************************************/
				serNum = ir.get("old_ser_num");

				if(serNum.length() == 5 || serNum.length() == 6 || serNum.length() == 7){
					try {
						Long.parseLong(ir.get("old_ser_num"));
	
					} catch (NumberFormatException e) {
						ir.set("err_desc", "잘못된 일련번호 형식입니다.");
						continue;
					}
				}
				
				/*********************************************
				 * 8. 사용자 체크 & 세팅.
				 *********************************************/
				mUser.put("user_id", ir.get("sale_chgrg_id"));

				mUser = (Map)queryForObject("SALBCS00300.getUserNm", mUser, onlineCtx);
				
				if(mUser != null){
					
					mUser.put("user_id", ir.get("sale_chgrg_id"));
					mUser.put("sale_plc", mOutPlc.get("sale_plc"));
					mUser = (Map)queryForObject("SALBCS00300.getUserNm", mUser, onlineCtx);
					
					
					if(mUser != null){
						ir.set("user_nm", String.valueOf(mUser.get("USER_NM")));
					}else{
						
						ir.set("err_desc", "판매처 소속의 사원이 아닙니다.");
						continue;
					}
					
				}else{
					ir.set("err_desc", "등록된 사원이 아닙니다.");
					continue;
				}
				
				
				/*********************************************
				 * 9. 상품정보 체크 및 세팅
				 *********************************************/
				rsProdMap.put("prod_cd", ir.get("prod_cd"));
				rsProdMap.put("ser_num", ir.get("ser_num"));
				rsProdMap.put("bar_cd_typ", sBarCdTyp);
				rsProdMap.put("out_plc_id", mOutPlc.get("sale_plc"));
				rsProdMap.put("sale_dtm", ir.get("sale_dtm"));
				rsProdMap.put("sale_typ_cd", sSaleTypCd);
				rsSerNumMap = (Map) queryForObject("SALBCS00300.getSaleProdByExcel", rsProdMap, onlineCtx);

				if (rsSerNumMap == null) {
					ir.set("err_desc", "재고에 입력된 일련번호로 등록된 상품이 없습니다.");
					continue;
				}
				
				ir.set("sale_plc_id", String.valueOf(mOutPlc.get("sale_plc"))); 
				
				sColorCd = String.valueOf(rsSerNumMap.get("COLOR_CD"));				
				ir.set("color_cd", sColorCd);
				
				sColorNm = String.valueOf(rsSerNumMap.get("COLOR_NM"));				
				ir.set("color_nm", sColorNm);
				
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
				
				sSaleAmt = String.valueOf(rsSerNumMap.get("SALE_AMT"));	
				ir.set("sale_amt", sSaleAmt);
				ir.set("cash_pay_amt", sSaleAmt);
				
				
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

		if (ir.get("sale_dtm") == null) {
			sbMsg.append("판매일시");
			sValidateYN = sNoValue;
		}

		if (ir.get("sale_chgrg_id") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("판매담당자 ID");
			sValidateYN = sNoValue;
		}

		if (ir.get("cust_nm") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("고객명");
			sValidateYN = sNoValue;
		}
		
		if (ir.get("prod_cd") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("모델코드");
			sValidateYN = sNoValue;
		}
		
		if (ir.get("ser_num") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("단말기일련번호");
			sValidateYN = sNoValue;
		}
		
		if (ir.get("old_ser_num") == null) {
			if (sValidateYN.equals(sNoValue))
				sbMsg.append(",");
			sbMsg.append("상품일련번호");
			sValidateYN = sNoValue;
		}
		
		hmReturnMap.put("msg", sbMsg.toString());
		hmReturnMap.put("validateYN", sValidateYN);

		return hmReturnMap;

	}
	
	/**
	 * 입력 멥 생성
	 *
	 * @author 김중일 (kimjoongil)
	 *  - 프로시저를 호출하기 위한 멥을 구성한다.
	 * @param orgMap
	 *            멥을 구성하기 위한 원본 멥
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 Map 객체
	 */
	@SuppressWarnings("unchecked")
	public Map getProMap(Map orgMap, IOnlineContext onlineCtx) {

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
		retMap.put("user_id", userInfo.getLoginId());

		return retMap;
	}
}

