/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.pol.spm.biz;

import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/정책</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-10-12 17:38:06</li>
 * </ul>
 *
 * @author 이문규 (leemunkyu)
 */
public class POLSPM00200 extends com.sktst.common.base.BaseBizUnit {

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
	public IDataSet getAmtGrdCl(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getAmtGrdCl method start");
		}

		IRecordSet rs = queryForRecordSet("POLSPM00200.getAmtGrdCl",
				requestData.getFieldMap(), onlineCtx);

		if (rs == null) {
			rs = new RecordSet("ds_amtGrdCl");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_amtGrdCl", rs);

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
	public IDataSet savePolSplst(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveSplstPrefer method start");
		}
		// 로컬 변수 선언
		int iSplstCnt = 0;
		int iPreferCnt = 0;
		int iInsertCnt = 0;
		int iChkCnt = 0;
		String sMsg = "";

		Map mSeq = null; // 가격표 ID 생성
		Map mSplst = null; // 판매가격표
		Map mSplstPrefer = null; // 선호도별 판매가격표
		Map mCheck = null; // 판매가격표 중복 체크

		// 사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		// 판매가격표 정보
		IRecordSet rsSplst = requestData.getRecordSet("ds_splst"); // 판매가격표
		IRecordSet rsSplstPrefer = requestData
				.getRecordSet("ds_splstPreferCopy");// 선호도별 판매가격표

		iSplstCnt = rsSplst.getRecordCount();
		iPreferCnt = rsSplstPrefer.getRecordCount();

		for (int i = 0; i < iSplstCnt; i++) {

			mSplst = rsSplst.getRecordMap(i);

			/**
			 * desc : 판매가격표 테이블 check.
			 * param : ds_condition
			 * return : splst_id
			 */
			mCheck = new HashMap();
			mCheck = (Map) queryForObject("POLSPM00200.getSplstCount", mSplst,
					onlineCtx);

			iChkCnt = Integer.parseInt(mCheck.get("CNT").toString());

			if (iChkCnt > 0) {
				sMsg = "[";
				sMsg = sMsg + mSplst.get("aply_ym") + ",";
				sMsg = sMsg + mSplst.get("org_id") + ",";
				sMsg = sMsg + mSplst.get("sale_typ_cd");
				sMsg = sMsg + "]";
				sMsg = sMsg + " 등록된 판매가격표 정책입니다.";

				throw new BizRuntimeException(sMsg);
			}

			/**
			 * desc : 판매가격표 테이블 Sequence(splst_id) 조회.
			 * param : ds_condition
			 * return : splst_id
			 */
			// 가격표 ID 얻기.
			mSeq = new HashMap();
			mSeq = (Map) queryForObject("POLSPM00200.getSequenceNo", mSeq,
					onlineCtx);

			String sSplstId = ""; //가격표ID
			sSplstId = (String) mSeq.get("SPLST_ID");

			if (sSplstId == null || sSplstId.equals("")) {
				throw new BizRuntimeException(
						"가격표ID 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
			}

			mSplst.put("splst_id", sSplstId);
			mSplst.put("pol_cl", "01"); // 선호도

			insert("POLSPM00200.insertSplst", mSplst, onlineCtx);

			for (int j = 0; j < iPreferCnt; j++) {

				// 판매가격표 MASTER. 
				mSplstPrefer = rsSplstPrefer.getRecordMap(j);

				if (mSplst.get("org_id").equals(mSplstPrefer.get("org_id"))
						&& mSplst.get("sale_typ_cd").equals(
								mSplstPrefer.get("sale_typ_cd"))) {

					mSplstPrefer.put("splst_id", sSplstId);
					insert("POLSPM00200.insertSplstPrefer", mSplstPrefer,
							onlineCtx);

					iInsertCnt++;
				}
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { ""
						+ iInsertCnt });

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
	public IDataSet getSplstPreferLst(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("POLSPM00200 getSplstPreferLst method start");
		}

		IRecordSet rsSplst = queryForRecordSet("POLSPM00200.getSplst",
				requestData.getFieldMap(), onlineCtx);

		if (rsSplst == null) {
			rsSplst = new RecordSet("ds_splst");
		}

		IRecordSet rsSplstPrefer = queryForRecordSet(
				"POLSPM00200.getSplstPrefer", requestData.getFieldMap(),
				onlineCtx);

		if (rsSplstPrefer == null) {
			rsSplstPrefer = new RecordSet("ds_splstPrefer");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsSplstPrefer.getRecordCount()) });

		responseData.putRecordSet("ds_splst", rsSplst);
		responseData.putRecordSet("ds_splstPrefer", rsSplstPrefer);

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
	public IDataSet deleteSplstPrefer(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("POLSPM00200 deleteSplstPrefer method start");
		}

		int deleteCnt = 0;
		int deleteFixCnt = 1;

		/**
		 * desc : 판매가격표 삭제.
		 * param : ds_condition
		 * return null
		 */
		deleteCnt = deleteCnt
				+ delete("POLSPM00200.deleteSplst", requestData.getFieldMap(),
						onlineCtx);

		/**
		 * desc : 선호도별 판매가격표 삭제.
		 * param : ds_condition
		 * return null
		 */
		deleteCnt = deleteCnt
				+ delete("POLSPM00200.deleteSplstPrefer", requestData
						.getFieldMap(), onlineCtx);

		if (deleteCnt < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.DELETE_OK_MESSAGE_ID, new String[] { ""
						+ deleteFixCnt });
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
	public IDataSet saveSplstMdl(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveSplstMdl method start");
		}
		// 로컬 변수 선언
		int iSplstCnt = 0;
		int iMdlCnt = 0;
		int iInsertCnt = 0;
		int iChkCnt = 0;
		String sMsg = "";

		Map mSeq = null; // 가격표 ID 생성
		Map mSplst = null; // 판매가격표
		Map mSplstMdl = null; // 선호도별 판매가격표
		Map mCheck = null; // 

		// 사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		// 판매가격표 정보
		IRecordSet rsSplst = requestData.getRecordSet("ds_splst"); // 판매가격표
		IRecordSet rsSplstMdl = requestData.getRecordSet("ds_splstMdlCopy");// 선호도별 판매가격표

		iSplstCnt = rsSplst.getRecordCount();
		iMdlCnt = rsSplstMdl.getRecordCount();

		for (int i = 0; i < iSplstCnt; i++) {

			mSplst = rsSplst.getRecordMap(i);

			/**
			 * desc : 판매가격표 테이블 check.
			 * param : ds_condition
			 * return : splst_id
			 */
			mCheck = new HashMap();
			mCheck = (Map) queryForObject("POLSPM00200.getSplstCount", mSplst,
					onlineCtx);

			iChkCnt = Integer.parseInt(mCheck.get("CNT").toString());

			if (iChkCnt > 0) {
				sMsg = "[";
				sMsg = sMsg + mSplst.get("aply_ym") + ",";
				sMsg = sMsg + mSplst.get("org_id") + ",";
				sMsg = sMsg + mSplst.get("sale_typ_cd");
				sMsg = sMsg + "]";
				sMsg = sMsg + " 등록된 판매가격표 정책입니다.";

				throw new BizRuntimeException(sMsg);
			}

			/**
			 * desc : 판매가격표 테이블 Sequence(splst_id) 조회.
			 * param : ds_condition
			 * return : splst_id
			 */
			// 가격표 ID 얻기.
			mSeq = new HashMap();
			mSeq = (Map) queryForObject("POLSPM00200.getSequenceNo", mSeq,
					onlineCtx);

			String sSplstId = ""; //가격표ID
			sSplstId = (String) mSeq.get("SPLST_ID");

			if (sSplstId == null || sSplstId.equals("")) {
				throw new BizRuntimeException(
						"가격표ID 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
			}

			mSplst.put("splst_id", sSplstId);
			mSplst.put("pol_cl", "02"); // 모델별

			insert("POLSPM00200.insertSplst", mSplst, onlineCtx);

			for (int j = 0; j < iMdlCnt; j++) {

				// 판매가격표 MASTER. 
				mSplstMdl = rsSplstMdl.getRecordMap(j);

				if (mSplst.get("org_id").equals(mSplstMdl.get("org_id"))
						&& mSplst.get("sale_typ_cd").equals(
								mSplstMdl.get("sale_typ_cd"))) {

					mSplstMdl.put("splst_id", sSplstId);
					insert("POLSPM00200.insertSplstMdl", mSplstMdl, onlineCtx);

					iInsertCnt++;

				}
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { ""
						+ iInsertCnt });

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
	public IDataSet getSplstMdlLst(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("POLSPM00200 getSplstMdlLst method start");
		}

		IRecordSet rsSplst = queryForRecordSet("POLSPM00200.getSplst",
				requestData.getFieldMap(), onlineCtx);

		if (rsSplst == null) {
			rsSplst = new RecordSet("ds_splst");
		}

		IRecordSet rsSplstMdl = queryForRecordSet("POLSPM00200.getSplstMdl",
				requestData.getFieldMap(), onlineCtx);

		if (rsSplstMdl == null) {
			rsSplstMdl = new RecordSet("ds_splstMdl");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rsSplstMdl.getRecordCount()) });

		responseData.putRecordSet("ds_splst", rsSplst);
		responseData.putRecordSet("ds_splstMdl", rsSplstMdl);

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
	public IDataSet deleteSplstMdl(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("POLSPM00300 deleteSplstMdl method start");
		}

		int deleteCnt = 0;
		int deleteFixCnt = 1;

		/**
		 * desc : 판매가격표 삭제.
		 * param : ds_condition
		 * return null
		 */
		deleteCnt = deleteCnt
				+ delete("POLSPM00200.deleteSplst", requestData.getFieldMap(),
						onlineCtx);

		/**
		 * desc : 선호도별 판매가격표 삭제.
		 * param : ds_condition
		 * return null
		 */
		deleteCnt = deleteCnt
				+ delete("POLSPM00200.deleteSplstMdl", requestData
						.getFieldMap(), onlineCtx);

		if (deleteCnt < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.DELETE_OK_MESSAGE_ID, new String[] { ""
						+ deleteFixCnt });
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
	public IDataSet saveModSplstMdl(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveSplstMdl method start");
		}
		// 로컬 변수 선언
		int iSplstCnt = 0;
		int iMdlCnt = 0;
		int iInsertCnt = 0;
		int iUpdateCnt = 0;
		int iDeleteCnt = 0;

		Map mSplst = null; // 판매가격표
		Map mSplstMdl = null; // 선호도별 판매가격표

		// 사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		// 판매가격표 정보
		IRecordSet rsSplst = requestData.getRecordSet("ds_splst"); // 판매가격표
		IRecordSet rsSplstMdl = requestData.getRecordSet("ds_splstMdlCopy");// 선호도별 판매가격표

		iSplstCnt = rsSplst.getRecordCount();
		iMdlCnt = rsSplstMdl.getRecordCount();

		for (int i = 0; i < iSplstCnt; i++) {

			mSplst = rsSplst.getRecordMap(i);

			/**
			 * desc : 판매가격표 테이블 Sequence(splst_id) 조회.
			 * param : ds_condition
			 * return : splst_id
			 */
			// 가격표 ID 얻기.
			//			mSeq = new HashMap();
			//			mSeq = (Map) queryForObject("POLSPM00200.getSequenceNo", mSeq,
			//					onlineCtx);
			//
			//			String sSplstId = ""; //가격표ID
			//			sSplstId = (String) mSeq.get("SPLST_ID");
			//			if (sSplstId == null || sSplstId.equals("")) {
			//				throw new BizRuntimeException(
			//						"가격표ID 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
			//			}
			//			mSplst.put("splst_id", sSplstId);
			//			mSplst.put("pol_cl", "02"); // 모델별
			//
			//			insert("POLSPM00200.insertSplst", mSplst, onlineCtx);
			for (int j = 0; j < iMdlCnt; j++) {

				// 판매가격표 MASTER. 
				mSplstMdl = rsSplstMdl.getRecordMap(j);

				log.debug("get Map = " + mSplstMdl);

				if (mSplst.get("org_id").equals(mSplstMdl.get("org_id"))
						&& mSplst.get("sale_typ_cd").equals(
								mSplstMdl.get("sale_typ_cd"))) {

					if (INSERT_FLAG.equalsIgnoreCase((String) mSplstMdl
							.get(CUD_FLAG_PARAM))) {
						insert("POLSPM00200.insertSplstMdl", mSplstMdl,
								onlineCtx);
						iInsertCnt++;
						log.debug("insert =========== ");
					} else if (UPDATE_FLAG.equalsIgnoreCase((String) mSplstMdl
							.get(CUD_FLAG_PARAM))) {
						update("POLSPM00200.updateSplstMdl", mSplstMdl,
								onlineCtx);
						iUpdateCnt++;
						log.debug("update =========== ");
					} else if (DELETE_FLAG.equalsIgnoreCase((String) mSplstMdl
							.get(CUD_FLAG_PARAM))) {
						update("POLSPM00200.deleteSplstMdlByMdl", mSplstMdl,
								onlineCtx);
						iDeleteCnt++;
						log.debug("delete =========== ");
					} else {
						log.debug("normal =========== ");
					}

					//}else if(UPDATE_FLAG.equalsIgnoreCase((String) mSplst.get(CUD_FLAG_PARAM)) ){
					//	update("POLSPM00200.updateSplstMdl", mSplstMdl, onlineCtx);
					//}else if(DELETE_FLAG.equalsIgnoreCase((String) mSplst.get(CUD_FLAG_PARAM)) ){
					//	update("POLSPM00200.delSplstMdl", mSplstMdl, onlineCtx);

				}
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "insert="
						+ iInsertCnt + "update=" + iUpdateCnt + "delete="
						+ iDeleteCnt });

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
	public IDataSet updateSplstPrefer(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveSplstPrefer method start");
		}

		// 로컬 변수 선언
		int iPreferCnt = 0;
		int iUpdateCnt = 0;

		Map mSplstPrefer = null; // 선호도별 판매가격표

		// 사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		// 판매가격표 정보
		IRecordSet rsSplstPrefer = requestData
				.getRecordSet("ds_splstPreferCopy");// 선호도별 판매가격표

		iPreferCnt = rsSplstPrefer.getRecordCount();

		for (int j = 0; j < iPreferCnt; j++) {

			// 판매가격표 MASTER. 
			mSplstPrefer = rsSplstPrefer.getRecordMap(j);

			update("POLSPM00200.updateSplstPrefer", mSplstPrefer, onlineCtx);

			iUpdateCnt++;
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
						+ iUpdateCnt });

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
	public IDataSet saveSplstPreferChg(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveSplstPreferChg method start");
		}
		// 로컬 변수 선언
		int iSplstCnt = 0;
		int iPreferCnt = 0;
		int iInsertCnt = 1;

		Map mSeq = null; // 가격표 ID 생성
		Map mCondition = null; // 조건
		Map mSplst = null; // 판매가격표

		// 사용자 기본정보
		IUserInfo iUserInfo = onlineCtx.getUserInfo();

		// 판매가격표 정보
		IRecordSet rsCondition = requestData.getRecordSet("ds_condition"); // 조건
		IRecordSet rsSplst = requestData.getRecordSet("ds_splst"); // 판매가격표

		// 변경 판매가격표 Prefer 생성(copy) 이전 판매가격표 기준
		// new 가격표 ID 얻기.
		mSeq = new HashMap();
		mSeq = (Map) queryForObject("POLSPM00200.getSequenceNo", mSeq,
				onlineCtx);

		String sSplstId = ""; //가격표ID
		sSplstId = (String) mSeq.get("SPLST_ID");

		if (sSplstId == null || sSplstId.equals("")) {
			throw new BizRuntimeException(
					"가격표ID 생성 시 에러가 발생하였습니다. 담당자에게 문의하세요.");
		}

		// 변경 판매가격표 Master 생성(copy) 이전 판매가격표 master기준
		mCondition = rsCondition.getRecordMap(0);
		mSplst = rsSplst.getRecordMap(0);
		log.debug("mSplst -------------" + mSplst);
		mSplst.put("new_splst_id", sSplstId);
		log.debug("sSplstId -----------" + sSplstId);
		mSplst.put("aply_mod_dtm", mCondition.get("aply_mod_dtm").toString()
				+ mCondition.get("mod_hour").toString()
				+ mCondition.get("mod_min").toString());
		log.debug("aply_mod_dtm -------------"
				+ mCondition.get("aply_mod_dtm").toString()
				+ mCondition.get("mod_hour").toString()
				+ mCondition.get("mod_min").toString());

		insert("POLSPM00200.insertSplstChg", mSplst, onlineCtx);

		// 변경 판매가격표 Prefer 생성(copy) 이전 판매가격표 Prefer기준
		insert("POLSPM00200.insertSplstPreferChg", mSplst, onlineCtx);

		// 이전 판매가격표 Master 종료처리 (APLY_END_DTM 변경) 
		update("POLSPM00200.updateObjSplstClose", mSplst, onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { ""
						+ iInsertCnt });

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
	public IDataSet getObjPolSplst(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getPolSpLst method start");
		}

		IRecordSet rs = queryForRecordSet("POLSPM00100.getPolSplst",
				requestData.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("ds_polSplst");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_polSplst", rs);
		return responseData;
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
	public IDataSet getProdListForSplstPrefer(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getPolSpLst method start");
		}

		IRecordSet rs = queryForRecordSet("POLSPM00200.getProdListForSplstPrefer",
				requestData.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("ds_prodLst");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_prodLst", rs);
		return responseData;
	}

}