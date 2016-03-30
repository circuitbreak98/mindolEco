/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.mnu.biz;

import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : 메뉴관리 </li>
 * <li>작성일 : 2009-01-29 10:00:02</li>
 * </ul>
 *
 * @author 심연정 (shimyunjung)
 */
public class ADMMNU00100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 *  메뉴 리스트 가져오기 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getMenuList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMMNU00100.getMenuList start >>>>>>>");
		}
		/*
		 onlineCtx.getUserInfo();
		 IPsMenuManager menuManager = (IPsMenuManager) ComponentRegistry.lookup(ServiceConstants.BIZ_MENU);
		 //PsMenu rootMenu = menuManager.getTempRootMenu();

		 
		 PsMenu rootMenu = menuManager.getTempRootMenuWithAuth("S0");
		 
		 
		 
		 IDataSet responseData = DataSetFactory.createWithOKResultMessage(
		 BaseConstants.QUERY_OK_SIMPLE_MESSAGE_ID, null);
		 //responseData.putRecordSet(menuManager.getMenuTreeRecordSet(rootMenu));
		 responseData.putRecordSets(menuManager.getMenuTreeRecordSet(rootMenu));
		 return responseData;
		 */
		IRecordSet rs = queryForRecordSet("ADMMNU00100.getMenuList",
				requestData.getFieldMap(), onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("output", rs);
		return responseData;

	}

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * 	메뉴 번호 생성  
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getMenuNum(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMMNU00100.getMenuNum start >>>>>>>");
		}

		IRecordSet rs = queryForRecordSet("ADMMNU00100.getMenuNum", requestData
				.getFieldMap(), onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_menuNo", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * 	메뉴 수정 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveMenuList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMMNU00100.saveMenuList start >>>>>>>");
		}

		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;

		IRecordSet rsMenu = requestData.getRecordSet("menu");
		if (rsMenu != null) {
			for (int i = 0; i < rsMenu.getRecordCount(); i++) {

				if (INSERT_FLAG.equalsIgnoreCase((String) rsMenu.getRecord(i)
						.get(CUD_FLAG_PARAM))) {
					insert("ADMMNU00100.saveMenuList_add", rsMenu.getRecord(i),
							onlineCtx);
					insertCount++;

				} else if (UPDATE_FLAG.equalsIgnoreCase((String) rsMenu
						.getRecord(i).get(CUD_FLAG_PARAM))) {
					updateCount += update("ADMMNU00100.saveMenuList_update",
							rsMenu.getRecord(i), onlineCtx);

				} else if (DELETE_FLAG.equalsIgnoreCase(rsMenu.getRecord(i)
						.get(CUD_FLAG_PARAM))) {

					deleteCount += delete("ADMMNU00100.saveMenuList_delete",
							rsMenu.getRecord(i), onlineCtx);
				}
			}

			cudAllCount = insertCount + updateCount + deleteCount;
		}

		IRecordSet rsAuth = requestData.getRecordSet("auth");
		if (rsAuth != null) {
			for (int i = 0; i < rsAuth.getRecordCount(); i++) {

				if (INSERT_FLAG.equalsIgnoreCase((String) rsAuth.getRecord(i)
						.get(CUD_FLAG_PARAM))) {
					insert("ADMMNU00100.saveMenuList_add", rsAuth.getRecord(i),
							onlineCtx);
					insertCount++;

				} else if (UPDATE_FLAG.equalsIgnoreCase((String) rsAuth
						.getRecord(i).get(CUD_FLAG_PARAM))) {
					updateCount += update("ADMMNU00100.saveMenuList_update",
							rsAuth.getRecord(i), onlineCtx);

				} else if (DELETE_FLAG.equalsIgnoreCase(rsAuth.getRecord(i)
						.get(CUD_FLAG_PARAM))) {

					deleteCount += delete("ADMMNU00100.saveMenuList_delete",
							rsAuth.getRecord(i), onlineCtx);
				}
			}

			cudAllCount = insertCount + updateCount + deleteCount;
		}

		if (cudAllCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount, "" + deleteCount });

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * - 상위메뉴번호 취득 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getSupMenuNum(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMMNU00100.getSupMenuNum start >>>>>>>");
		}

		IRecordSet rs = queryForRecordSet("ADMMNU00100.getSupMenuNum",
				requestData.getFieldMap(), onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_supMenuNo", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 하위 메뉴의 존재 여부 체크
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet checkChild(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMMNU00100.checkChild method start");
		}

		// 2. CRUD 처리
		IRecordSet rs = queryForRecordSet("ADMMNU00100.checkChild", requestData
				.getFieldMap(), onlineCtx);

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("output", rs);
		return responseData;
	}

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
	public IDataSet saveMenuAuth(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMMNU00100.saveMenuAuth start >>>>>>>");
		}

		// 2. CRUD 처리
		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;

		IRecordSet rsMenu = requestData.getRecordSet("menu");
		if (rsMenu != null) {
			for (int i = 0; i < rsMenu.getRecordCount(); i++) {

				if (INSERT_FLAG.equalsIgnoreCase((String) rsMenu.getRecord(i)
						.get(CUD_FLAG_PARAM))) {
					insert("ADMMNU00100.addMenu", rsMenu.getRecord(i),
							onlineCtx);
					insertCount++;

				} else if (UPDATE_FLAG.equalsIgnoreCase((String) rsMenu
						.getRecord(i).get(CUD_FLAG_PARAM))) {
					updateCount += update("ADMMNU00100.updateMenu", rsMenu
							.getRecord(i), onlineCtx);

				} else if (DELETE_FLAG.equalsIgnoreCase(rsMenu.getRecord(i)
						.get(CUD_FLAG_PARAM))) {

					deleteCount += delete("ADMMNU00100.deleteMenu", rsMenu
							.getRecord(i), onlineCtx);

					if ("4".compareTo(rsMenu.get(0, "MENU_LVL_CD")) == 0) {
						delete("ADMMNU00100.deleteAuth", rsMenu.getRecord(i),
								onlineCtx);
					}
				}
			}

			cudAllCount = insertCount + updateCount + deleteCount;
		}

		IRecordSet rsAuth = requestData.getRecordSet("auth");
		if (rsAuth != null) {
			for (int i = 0; i < rsAuth.getRecordCount(); i++) {

				if (INSERT_FLAG.equalsIgnoreCase((String) rsAuth.getRecord(i)
						.get(CUD_FLAG_PARAM))) {
					insert("ADMMNU00100.addAuth", rsAuth.getRecord(i),
							onlineCtx);
					insertCount++;

				} else if (UPDATE_FLAG.equalsIgnoreCase((String) rsAuth
						.getRecord(i).get(CUD_FLAG_PARAM))) {
					updateCount += update("ADMMNU00100.updateAuth", rsAuth
							.getRecord(i), onlineCtx);

				} else if (DELETE_FLAG.equalsIgnoreCase(rsAuth.getRecord(i)
						.get(CUD_FLAG_PARAM))) {

					deleteCount += delete("ADMMNU00100.deleteAuth", rsAuth
							.getRecord(i), onlineCtx);
				}
			}

			cudAllCount = insertCount + updateCount + deleteCount;
		}

		// 3. 결과 지정
		if (cudAllCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount, "" + deleteCount });

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 				- 마이 메뉴 정보 저장
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveMyMenu(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMMNU00100.saveMyMenu method start");
		}

		// 2. CRUD 처리
		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;
		IRecordSet rs = requestData.getRecordSet("input");

		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (DELETE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					deleteCount += delete("ADMMNU00100.deleteMyMenu", rs
							.getRecord(i), onlineCtx);
				} else if (INSERT_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					insert("ADMMNU00100.addMyMenu", rs.getRecord(i), onlineCtx);
					insertCount++;
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
	 * @author 최승호 (choiseungho)
	 * 			- 마이메뉴 목록 취득
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getMyMenu(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMMNU00100.getMyMenu method start");
		}

		// 2. CRUD 처리
		
		System.out.println("ADMMNU00100.getMyMenu================="+requestData.getFieldMap());
		IRecordSet rs = queryForRecordSet("ADMMNU00100.getMyMenu", requestData
				.getFieldMap(), onlineCtx);
		if (rs == null) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("output", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 최승호 (choiseungho)
	 * 			- 대메뉴 자동 추가
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveFirstMenu(IDataSet requestData, IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMMNU00100.saveFirstMenu method start");
		}

		// 2. CRUD 처리

		IRecordSet rs = requestData.getRecordSet("menu");
		insert("ADMMNU00100.addFirstMenu", rs.getRecord(0), onlineCtx);

		// 3. 결과지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_SIMPLE_MESSAGE_ID, null);
	}

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
	public IDataSet getLoginMenuAuthInfo(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMMNU00100.getLoginMenuAuthInfo method start");
		}

		
		String sUserGrp = requestData.getField("USER_GRP");
		String sAdmCode = "ADM";
		IRecordSet rsMenu = null;
		IRecordSet rsAuth = null;
		Map mUserGrp = null;
		
		System.out.println("ADMMNU00100.getLoginMenuAuthInfo================="+requestData.getFieldMap());
		
		mUserGrp = (Map) queryForObject("ADMMNU00100.getUserGrp" , requestData.getFieldMap(), onlineCtx);
		//requestData.put("CON_MGMT_NO", mUserGrp.get("CON_MGMT_NO"));
		//requestData.getField("USER_GRP").
		// 2. CRUD 처리 ( 유저그룹 USER_GRP = 'ADM' 이면 슈퍼어드민 이므로 쿼리를 분기한다.  )
		if(sUserGrp != null && sUserGrp.equals(sAdmCode)){
			
			rsMenu = queryForRecordSet("ADMMNU00100.getLoginMenuTreeBySuperAdm", requestData
					.getFieldMap(), onlineCtx);
			rsAuth = queryForRecordSet("ADMMNU00100.getLoginMenuAuthListBySuperAdm", requestData
					.getFieldMap(), onlineCtx);
		}else{			
			
//			rsMenu = queryForRecordSet("ADMMNU00100.getLoginMenuTree", requestData
//					.getFieldMap(), onlineCtx);
			rsMenu = queryForRecordSet("ADMMNU00100.getLoginMenuTree", mUserGrp, onlineCtx);					
			rsAuth = queryForRecordSet("ADMMNU00100.getLoginMenuAuthList", requestData
					.getFieldMap(), onlineCtx);
		}
		
		
		if (rsMenu == null || rsAuth == null) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		// 3. 결과 지정
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.LOGIN_SUCCESS, null);
		responseData.putRecordSet("leftMenu", rsMenu);
		responseData.putRecordSet("authMenu", rsAuth);
		return responseData;
	}

}