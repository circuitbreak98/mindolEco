/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.pdm.ejb;

import java.rmi.RemoteException;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.exception.BaseException;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-20 11:28:10</li>
 * </ul>
 *
 * @author 전현주 (junhyunjoo)
 */
public interface PDM extends javax.ejb.EJBObject {

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : PROD_CL [필드1] 
	 *	- field : PROD_NM [필드2] 
	 *	- field : MFACT_ID [필드3] 
	 *	- field : SKT_OPER_YN [필드4] 
	 *	- field : PROD_CHRTIC_1 [필드5] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : PROD_CL [필드1] 
	 *	- field : MFACT_ID [필드2] 
	 *	- field : PROD_NM [필드3] 
	 *	- field : PROD_CD [필드4] 
	 *	- field : SKT_OPER_YN [필드5] 
	 *	- field : PROD_CHRTIC_1 [필드6] 
	 *	- field : PROD_CHRTIC_2 [필드7] 
	 *	- field : END_DT [필드8] 
	 */
	IDataSet getProductListByProdCl(IDataSet requestData,
			IOnlineContext onlineCtx) throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : PROD_CL [필드1] 
	 *	- field : SKT_OPER_YN [필드2] 
	 *	- field : USE_YN [필드3] 
	 *	- field : USE_STOP_DT [필드4] 
	 *	- field : PROD_CHRTIC_1 [필드5] 
	 *	- field : PROD_CHRTIC_2 [필드6] 
	 *	- field : BARCD_TYP [필드7] 
	 *	- field : RMKS [필드8] 
	 *	- field : PROD_CD [필드9] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet updateProductMgmt(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : PROD_CD [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : PROD_CD [필드1] 
	 *	- field : PROD_NM [필드2] 
	 *	- field : MFACT_ID [필드3] 
	 *	- field : MKTG_DT [필드4] 
	 *	- field : END_DT [필드5] 
	 *	- field : PROD_CL [필드6] 
	 *	- field : PROD_DTL_CL [필드7] 
	 *	- field : SKT_OPER_YN [필드8] 
	 *	- field : USE_YN [필드9] 
	 *	- field : USE_STOP_DT [필드10] 
	 *	- field : PROD_CHRTIC_1 [필드11] 
	 *	- field : PROD_CHRTIC_2 [필드12] 
	 *	- field : PROD_CHRTIC_3 [필드13] 
	 *	- field : PROD_CHRTIC_4 [필드14] 
	 *	- field : BAR_CD_TYP [필드15] 
	 *	- field : RGST_CL [필드16] 
	 *	- field : RMKS [필드17] 
	 *	- field : COM_MTHD [필드18] 
	 *	- field : MST_PROD_CD [필드19] 
	 *	- field : MST_PROD_NM [필드20] 
	 *	- field : SER_LEN [필드21] 
	 *	- field : U_KEY_IF_YN [필드22] 
	 */
	IDataSet getProductDetail(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : color_cd [필드1] 
	 *	- field : name [필드2] 
	 */
	IDataSet getColorList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : SUPL_SVC_NM [필드1] 
	 *	- field : USE_YN [필드2] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : SUPL_SVC_CD [필드1] 
	 *	- field : SUPL_SVC_NM [필드2] 
	 *	- field : RGST_DT [필드3] 
	 *	- field : USE_YN [필드4] 
	 */
	IDataSet getSuplSvc(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : SUPL_SVC_CD [필드1] 
	 *	- field : SUPL_SVC_NM [필드2] 
	 *	- field : RGST_DT [필드3] 
	 *	- field : USE_YN [필드4] 
	 *	- field : RGST_CL [필드5] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet addSuplSvc(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 조민지 (jominji)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : prod_cl [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : prod_bar_cd_mgmt
	 *		- field : prod_cl [상품구분] 
	 *		- field : bar_cd_typ [바코드유형] 
	 *		- field : all_len_num [전체자리수] 
	 *		- field : mdl_sta_len [모델시작자리] 
	 *		- field : mdl_end_len [모델끝자리] 
	 *		- field : mdl_len_num [모델자리수] 
	 *		- field : color_sta_len [색상시작자리] 
	 *		- field : color_end_len [색상끝자리] 
	 *		- field : color_len_num [색상자리수] 
	 *		- field : ser_num_sta_len [일련번호시작자리] 
	 *		- field : ser_num_end_len [일련번호끝자리] 
	 *		- field : ser_num_len_num [일련번호자리수] 
	 *		- field : upd_cnt [update count] 
	 *		- field : ins_dtm [입력일시] 
	 *		- field : ins_user_id [입력자id] 
	 *		- field : mod_dtm [수정일시] 
	 *		- field : mod_user_id [필드16] 
	 */
	IDataSet getProductBarcodeList(IDataSet requestData,
			IOnlineContext onlineCtx) throws RemoteException;

	/**
	 * 
	 *
	 * @author 조민지 (jominji)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet getProdClChoData(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet addProductMgmt(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet saveSuplSvc(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet deleteSuplSvc(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 조민지 (jominji)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : prod_bar_cd_mgmt
	 *		- field : prod_cl [상품구분] 
	 *		- field : bar_cd_typ [바코드유형] 
	 *		- field : all_len_num [전체자리수] 
	 *		- field : mdl_sta_len [모델시작자리] 
	 *		- field : mdl_end_len [모델끝자리] 
	 *		- field : mdl_len_num [모델자리수] 
	 *		- field : color_sta_len [색상시작자리] 
	 *		- field : color_end_len [색상끝자리] 
	 *		- field : color_len_num [색상자리수] 
	 *		- field : ser_num_sta_len [일련번호시작자리] 
	 *		- field : ser_num_end_len [일련번호끝자리] 
	 *		- field : ser_num_len_num [일련번호자리수] 
	 *		- field : upd_cnt [UPDATE COUNT] 
	 *		- field : ins_dtm [입력일시] 
	 *		- field : ins_user_id [입력자ID] 
	 *		- field : mod_dtm [수정일시] 
	 *		- field : mod_user_id [수정자ID] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet saveProductBarcodeList(IDataSet requestData,
			IOnlineContext onlineCtx) throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet getProdCodeCheck(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet getProdNmCheck(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet getChoData(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet deleteProductMgmt(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 최승호 (choiseungho)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet checkDuplication(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 상품 매핑 정보를 조회한다.
	 * 
	 * @author 김중일 (kimjoongil)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : prod_map_cd [상품매핑코드] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : prodMapList
	 *		- field : prod_map_cd [상품매핑코드] 
	 *		- field : prod_map_cl [상품매핑구분] 
	 *		- field : prod_cd [상품코드] 
	 *		- field : prod_nm [상품명] 
	 *		- field : color_cd [색상코드] 
	 */
	IDataSet getProdMapList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 상품 매핑 정보를 저장, 수정, 삭제 한다.
	 * 
	 * @author 김중일 (kimjoongil)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet saveProdMapInfo(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 김중일 (kimjoongil)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : prod_cd [상품코드] 
	 *	- field : color_cd [색상코드] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : prodMapCheck
	 *		- field : cnt [레코드수] 
	 */
	IDataSet getProdMapCheck(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet getUsedProdInfo(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet addUsedProdInfo(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet updateUsedProdInfo(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet deleteUsedProdInfo(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet getMfactPlcList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet getUserProdListByProdCl(IDataSet requestData,
			IOnlineContext onlineCtx) throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet checkUsedProdDup(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet getPriceRateList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

	/**
	 * 
	 * 
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	IDataSet savePriceRateList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getProdFixPrice(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet saveProdFixPrice(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getProdDtlList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getPriceMasterList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getPriceList(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getPriceErrorCheckXls(IDataSet requestData,
			IOnlineContext onlineCtx) throws RemoteException;

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
	IDataSet savePriceXls(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet deleteProdFixPrice(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

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
	IDataSet getProdDtlPrice(IDataSet requestData, IOnlineContext onlineCtx)
			throws RemoteException;

}