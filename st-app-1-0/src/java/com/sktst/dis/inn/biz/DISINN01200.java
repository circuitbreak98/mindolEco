/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.dis.inn.biz;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.core.util.DateUtils;
import nexcore.framework.core.util.StringUtils;
import nexcore.framework.integration.db.NoRecordAffectedException;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-07-23 19:52:40</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class DISINN01200 extends BaseBizUnit {

	/**
	 * 상품확정대상조회
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getProdFixObjList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdFixObjList method start.");
		}

		IRecordSet rsProd = queryForRecordSet("DISINN01200.getProdFixObjList",
				requestData.getFieldMap(), onlineCtx);

		if (rsProd == null) {
			rsProd = new RecordSet("ProdFixObjList");
		}

		IRecordSet rsProdCpnt = queryForRecordSet(
				"DISINN01200.getProdCpntList", requestData.getFieldMap(),
				onlineCtx);

		if (rsProdCpnt == null) {
			rsProdCpnt = new RecordSet("ProdCpntList");
		}

		IDataSet result = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsProd.getRecordCount()) });

		result.putRecordSet("ProdFixObjList", rsProd);
		result.putRecordSet("ProdCpntList", rsProdCpnt);

		return result;
	}

	/**
	 * 상품확정저장
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveProdFixList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveProdFixList method start");
		}

		// 2. CRUD 처리
		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;
		IRecordSet rs = requestData.getRecordSet("ds_prodFixObjList");

		if (rs != null) {

			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (UPDATE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					updateCount += update("DISINN01200.updateProdFix", rs
							.getRecord(i), onlineCtx);
				}
			}

			cudAllCount = insertCount + updateCount + deleteCount;
		}

		if (cudAllCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
		}

		// 3. 결과지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount, "" + deleteCount });
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
	@SuppressWarnings("unchecked")
	public IDataSet savePrchsProdFixList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("savePrchsProdFixList method start");
		}

		int updateCount = 0;
		String sInMgmtNo = null;
		String sPrchsMgmtNo = null;
		String sMgmtNoCl = "IN"; //관리번호구분 IN:입고		
		String sErrMsg = ""; // error message
		String sErrCode = ""; // error code
		String sSuccessCd = "S"; // success code

		//사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		// 매입출고 MASTER. 
		IRecordSet rsPrchsMst = requestData.getRecordSet("ds_prchsOutM");
		Map mPrchsMst = rsPrchsMst.getRecordMap(0);

		// 매입관리번호
		sPrchsMgmtNo = (String) mPrchsMst.get("prchs_mgmt_no");

		// 입고관리번호 얻기.
		Map mMgmt = new HashMap();
		mMgmt.put("OV_ERRCODE", ""); // 에러.
		mMgmt.put("OV_ERRMSG", ""); // 에러메세지.
		mMgmt.put("IV_MGMT_NO_CD", sMgmtNoCl); // 관리번호구분.
		mMgmt.put("IV_USER_ID", iUserInfo.getLoginId()); // 처리자.
		mMgmt.put("OV_MGMT_NO", "");

		sInMgmtNo = this.getMgmtNo(mMgmt, onlineCtx); // 입고관리번호

		Map mInData = null;
		mInData = new HashMap();
		// 마스터 데이터 넣기.

		mInData.put("prchs_mgmt_no", sPrchsMgmtNo); // 매입관리번호
		mInData.put("in_mgmt_no", sInMgmtNo); // 입고관리번호
		mInData.put("user_id", iUserInfo.getLoginId());// 처리자

		///////////////////////////////////
		//  출고 프로시져 호출
		/////////////////////////////////// 
		queryForObject("DISINN01200.savePrchsProdFix", mInData, onlineCtx);
		sErrMsg = (String) mInData.get("ERRMSG");
		sErrCode = (String) mInData.get("ERRCODE");

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { ""
						+ updateCount });

		// 저장 후 다시 조회 하기 위해 필요한 key
		Map mReturn = new HashMap();
		mReturn.put("in_mgmt_no", mInData.get("in_mgmt_no"));

		responseData.putFieldMap(mReturn);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 관리번호를 얻는다.
	 * @param data
	 *            요청정보를 Wrapping하고 있는 Map 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public String getMgmtNo(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getMgmtNo method start");
		}
		String sMgmtNo = ""; //관리번호
		queryForObject("DISINN00200.getMgmtNo", data, onlineCtx);

		sMgmtNo = (String) data.get("OV_MGMT_NO");

		if (sMgmtNo == null || sMgmtNo.equals("")) {
			throw new BizRuntimeException("관리번호 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}

		return sMgmtNo;
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
	public IDataSet saveProdRegCncl(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveProdRegCncl method start");
		}

		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;

		int i = 0; // 반복문 변수
		int j = 0; // 반복문 변수
		int iProdCnclCnt = 0; // 상품레코드갯수
		int iCpntCnclCnt = 0; // 구성품레코드갯수
		String sProdInfo = ""; // 상품정보
		String sInoutCl = "100"; // 입출고구분 (입고)
		String sInoutDtlCl = "113"; // 입출고상세구분 (구성품출고취소)
		String sCurrDt = DateUtils.getCurrentDate(); // 현재일자		

		// 사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		// 상품 구성 취소 리스트 
		IRecordSet rsProdCnclList = requestData
				.getRecordSet("ds_prodFixObjList");
		IRecordSet rsCpntCnclList = requestData.getRecordSet("ds_prodCpntList");
		iProdCnclCnt = rsProdCnclList.getRecordCount();
		iCpntCnclCnt = rsCpntCnclList.getRecordCount();

		Map mProdCncl = new HashMap(); // 상품 취소 Map
		Map mCpntCncl = new HashMap(); // 구성품 취소 Map
		Map mCond = new HashMap(); // 조회, update value map

		for (i = 0; i < iProdCnclCnt; i++) {

			mProdCncl = rsProdCnclList.getRecordMap(i);

			mCond.put("prod_cd", mProdCncl.get("prod_cd")); // 상품코드
			mCond.put("color_cd", mProdCncl.get("color_cd")); // 색상코드
			mCond.put("ser_num", mProdCncl.get("prod_ser_num")); // 상품일련번호

			// 1.상품 재고 상태 체크
			// 입고 상태이면 취소 가능, 출고이면 취소 불가
			IRecordSet rsProdDisSt = this.getProdDisSt(mCond, onlineCtx); // 상품재고상태조회

			sProdInfo = "[" + mProdCncl.get("prod_nm") + "/"
					+ mProdCncl.get("color_nm") + "/"
					+ mProdCncl.get("prod_ser_num") + "] ";

			// null check
			if (rsProdDisSt == null) {
				throw new BizRuntimeException(sProdInfo
						+ "해당되는 상품이 없습니다. 담당자에게 문의하세요.");
			}

			// upd_cnt check
			if (rsProdDisSt.get(0, "upd_cnt").equals(mProdCncl.get("upd_cnt"))) {
				throw new BizRuntimeException("재조회 후 처리하세요.");
			}

			// 재고 상태 "01" 가용이 아니면
			if (!rsProdDisSt.get(0, "dis_st").equals("01")) {
				throw new BizRuntimeException(sProdInfo
						+ "재고상태가 가용이 아닙니다. 담당자에게 문의하세요.");
			}

			// 최종입출고구분 체크
			if (rsProdDisSt.get(0, "last_inout_cl").equals("200")) {
				throw new BizRuntimeException(sProdInfo + "출고 된 상품입니다.");
			} else if (rsProdDisSt.get(0, "last_inout_cl").equals("300")) {
				throw new BizRuntimeException(sProdInfo + "재고이동 된 상품입니다.");
			} else if (!rsProdDisSt.get(0, "last_inout_cl").equals("100")) {
				throw new BizRuntimeException(sProdInfo
						+ "입고 상태인 상품이 아닙니다. 담당자에게 문의하세요.");
			}

			// 2. 상품취소처리 시작
			// 2.1 상품 재고 취소
			update("DISINN01200.updateProdCncl", mCond, onlineCtx);
			updateCount++;

			// 2.2 상품 가격 취소
			update("DISINN01200.updateProdUnitPrcCncl", mCond, onlineCtx);
			updateCount++;

			// 2.3 상품 구성품 취소
			update("DISINN01200.updateProdCpntCncl", mCond, onlineCtx);
			updateCount++;

			// 2.4 상품 입출고 상세 이력 
			update("DISINN01200.updateProdInoutHstCncl", mCond, onlineCtx);
			updateCount++;

			// 3. 재고 취소 처리 시작(구성품 출고 취소)

			// 3.1 입고 
			String sMgmtNoCl = "IN"; // 관리번호구분 IN:입고		
			String sInMgmtNo = ""; // 입고관리번호
			String sInSeq = ""; // 입고순번
			String sLastInMgmtNo = ""; // 마지막 입고관리번호
			String sLastInSeq = ""; // 마지막 입고순번

			// 3.1.1 입고관리번호 생성
			Map mMgmt = new HashMap();
			mMgmt.put("OV_ERRCODE", ""); // 에러.
			mMgmt.put("OV_ERRMSG", ""); // 에러메세지.
			mMgmt.put("IV_MGMT_NO_CD", sMgmtNoCl); // 관리번호구분.
			mMgmt.put("IV_USER_ID", iUserInfo.getLoginId()); // 처리자.
			mMgmt.put("OV_MGMT_NO", "");

			sInMgmtNo = this.getMgmtNo(mMgmt, onlineCtx); // 입고관리번호	

			// 3.1.2 입고 순번 생성
			Map mInSeq = new HashMap();
			mInSeq.put("in_mgmt_no", sInMgmtNo); // 입고순번

			sInSeq = this.getInSeq(mInSeq, onlineCtx);

			// 3.1.3 마지막 입고 관리번호 조회
			// prod_cd  : 상품 처리에서 셋팅  
			// color_cd : 상품 처리에서 셋팅
			mCond.put("ser_num", mProdCncl.get("eqp_ser_num")); // 단말기 일련번호

			Map mLastIn = this.getLastInInfo(mCond, onlineCtx); // 마지막 입고 조회

			mCond.put("in_mgmt_no", sInMgmtNo); // 입고관리번호 셋팅
			mCond.put("in_seq", sInSeq); // 입고 순번 셋팅
			mCond.put("last_in_mgmt_no", mLastIn.get("IN_MGMT_NO")); // 마지막 입고관리번호 셋팅
			mCond.put("last_in_seq", mLastIn.get("IN_SEQ")); // 마지막 입고 순번 셋팅
			mCond.put("last_inout_seq", mLastIn.get("INOUT_SEQ")); // 마지막 입고출고 순번 셋팅
			mCond.put("curr_dt", sCurrDt); // 처리일자

			// 3.1.4 입고 마스터
			// 마지막 입고관리번호를 기준으로 생성      
			mCond.put("in_cl", sInoutDtlCl);

			insert("DISINN01200.insertCnclInM", mCond, onlineCtx);
			insertCount++;

			// 3.1.5 입고 상세
			// 마지막 입고관리번호, 입고순번을 기준으로 생성
			mCond.put("inout_cl", sInoutCl);
			mCond.put("inout_dtl_cl", sInoutDtlCl); //구성품출고취소

			insert("DISINN01200.insertCnclInDtlD", mCond, onlineCtx);
			insertCount++;

			// 3.1.6 입고 구성품
			insert("DISINN01200.insertCnclInCpnt", mCond, onlineCtx);
			insertCount++;

			// 3.2 재고	
			// 3.2.1 재고 마스터
			// prod_cd, color_cd, ser_num 기준 업데이트
			update("DISINN01200.updateCnclDis", mCond, onlineCtx);
			updateCount++;

			// 3.2.2 재고 입출고 이력
			// prod_cd, color_cd, ser_num, inout_seq 기준 insert			
			insert("DISINN01200.insertCnclDisInoutHst", mCond, onlineCtx);
			insertCount++;

			// 3.2.3 재고 이력
			// tdis_dis 테이블의 prod_cd, color_cd, ser_num  기준 insert					
			insert("DISINN01200.insertCnclDisHst", mCond, onlineCtx);
			insertCount++;

			// 3.3 기타재고
			for (j = 0; j < iCpntCnclCnt; j++) {
				mCpntCncl = rsCpntCnclList.getRecordMap(j);

				// tdis_prod_cpnt 기준
				// prod_cd(상품코드), color_cd(색상코드), ser_num(상품일련번호)
				// cpnt_ser_num(구성품일련번호), cpnt_etc_seq(구성품기타재고순번)이 null,"" 아닐때 처리  -> 기타재고에서 추가된 경우
				if (mCpntCncl.get("prod_cd").equals(mProdCncl.get("prod_cd"))
						&& mCpntCncl.get("color_cd").equals(
								mProdCncl.get("color_cd"))
						&& mCpntCncl.get("ser_num").equals(
								mProdCncl.get("prod_ser_num"))
						&& (mCpntCncl.get("cpnt_ser_num") != null && mCpntCncl
								.get("cpnt_ser_num") != "")
						&& (mCpntCncl.get("cpnt_etc_seq") != null && mCpntCncl
								.get("cpnt_etc_seq") != "")) {
					mCond.put("cpnt_prod_cd", mCpntCncl.get("cpnt_prod_cd")); // 구성품 상품코드
					mCond.put("cpnt_color_cd", mCpntCncl.get("cpnt_color_cd")); // 구성품 색상코드
					mCond.put("cpnt_ser_num", mCpntCncl.get("cpnt_ser_num")); // 구성품 일련번호
					mCond.put("cpnt_etc_seq", mCpntCncl.get("cpnt_etc_seq")); // 구성품 기타재고순번
					mCond.put("cpnt_in_qty", mCpntCncl.get("in_qty")); // 상품구성품의 입고수량

					// 3.3.1 기타재고
					// dis_etc_dis 테이블 가용재고 업데이트
					update("DISINN01200.updateCnclEtcDis", mCond, onlineCtx);
					updateCount++;

					// 3.2.3 기타재고 입출고 이력
					// tdis_dis 테이블의 prod_cd, color_cd, ser_num 기준 insert					
					insert("DISINN01200.insertCnclEtcDisInoutHst", mCond,
							onlineCtx);
					insertCount++;

					// 3.3.3 기타재고 이력	
					insert("DISINN01200.insertCnclEtcDisHst", mCond, onlineCtx);
					insertCount++;
				}
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"Insert:" + insertCount, " Update:" + updateCount,
						" Delete:" + deleteCount });

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 * 
	 * @param data
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IRecordSet getProdDisSt(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getProdDisSt method start");
		}

		IRecordSet rsProdDis = queryForRecordSet("DISINN01200.getProdDisSt",
				data, onlineCtx);

		return rsProdDis;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 입고 순번을 얻는다.
	 * @param data
	 *            요청정보를 Wrapping하고 있는 Map 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public String getInSeq(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("DISINN01200.getInSeq method start");
		}
		String sInSeq = ""; //입고순번

		Map mInSeq = (Map) queryForObject("DISINN00400.getInSeq", data,
				onlineCtx);
		sInSeq = mInSeq.get("IN_SEQ").toString();
		if (sInSeq == null || sInSeq.equals("")) {
			throw new BizRuntimeException("입고순번 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}

		return sInSeq;
	}

	/**
	 * 
	 *
	 * @author 이문규 (leemunkyu)
	 *  - 입고 순번을 얻는다.
	 * @param data
	 *            요청정보를 Wrapping하고 있는 Map 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public Map getLastInInfo(Map data, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getLastInInfo method start");
		}

		Map mInInfo = (Map) queryForObject("DISINN01200.getLastInInfo", data,
				onlineCtx);

		if (mInInfo == null || mInInfo.equals("")) {
			throw new BizRuntimeException("입고정보 조회시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}

		return mInInfo;
	}
}