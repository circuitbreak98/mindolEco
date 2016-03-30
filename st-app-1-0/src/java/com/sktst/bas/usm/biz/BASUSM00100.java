/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.usm.biz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.CryptoUtils;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보관리</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-15 14:31:54</li>
 * </ul>
 *
 * @author 정재열 (jungjaeyul)
 */
public class BASUSM00100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : user_grp [필드3] 
	 *	- field : org_id [필드2] 
	 *	- field : user_nm [필드3] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	@SuppressWarnings("unchecked")
	public IDataSet getUserList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getUserList method start");
		}

		Map fields = requestData.getFieldMap();
		IRecordSet rs = queryForRecordSet("BASUSM00100.getUserList", fields,
				onlineCtx);
		if (rs == null) {
			rs = new RecordSet("userList");
		}
		/*
		 setPagenatedParams(fields);
		 
		 IRecordSet rs = queryForRecordSet("BASUSM00100.getUserList",
		 fields, onlineCtx);
		 if (rs == null) {
		 rs = new RecordSet("userList");
		 }
		 
		 rs.setPageNo(Integer.parseInt((String) fields.get(Constants.PAGE_NO)));
		 rs.setRecordCountPerPage(Integer.parseInt((String) fields
		 .get(Constants.RC_COUNT_PER_PAGE)));

		 Integer totalCount = (Integer) queryForObject(
		 "BASUSM00100.getUserListCount", fields, onlineCtx);
		 rs.setTotalRecordCount(totalCount);
		 */

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("userList", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 정재열 (jungjaeyul)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getUserGrpList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getUserGrpList method start");
		}
		IRecordSet rs = queryForRecordSet("BASUSM00100.getUserGrpList",
				requestData.getFieldMap());
		if (rs == null) {
			rs = new RecordSet("userGrpList");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("userGrpList", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이영진 (leeyoungjin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getUserDutyList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getUserDutyList method start");
		}
		IRecordSet rs = queryForRecordSet("BASUSM00100.getUserDutyList",
				requestData.getFieldMap());
		if (rs == null) {
			rs = new RecordSet("userDutyList");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("userDutyList", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addExcelExportHst(IDataSet requestData,
			IOnlineContext onlineCtx) {
		// 1. 로그 기록
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("BASUSM00100.addExcelExportHst method start");
		}

		// 2. CRUD 처리	
		insert("BASUSM00100.addExcelExportHst", requestData.getFieldMap(),
				onlineCtx);

		// 3. 결과 지정
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "1" });
	}

	/**
	 * 
	 *
	 * @author 장화수 (janghwasoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateJm(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("updateJm method start");
		}

		int updateCount = 0;

		Map oMap = null;
		oMap = requestData.getFieldMap();

		//oMap.get("userName").toString();
		//oMap.get("bizJmNum").toString();
		//oMap.get("gv_url").toString();		

		String realBizYn = "N";
		if (!oMap.get("userName").toString().equals("")) {
			oMap.put("userName", oMap.get("userName").toString().trim());
		}
		//================================================================================
		//http 실명인증
		String sHttpUrl = null;
		if (oMap.get("gv_url").toString().matches(".*localhost.*")) {
			sHttpUrl = "http://220.103.248.105:8030/ciespr/jsp_user/userNameCheck.jsp?HAN_NM="
					+ oMap.get("userName").toString()
					+ "&CTZ_NUM="
					+ oMap.get("bizJmNum").toString();

		} else if (oMap.get("gv_url").toString().matches(".*10.40.10.29.*")) {
			sHttpUrl = "http://172.31.32.98:8010/ciespr/jsp_user/userNameCheck.jsp?HAN_NM="
					+ oMap.get("userName").toString()
					+ "&CTZ_NUM="
					+ oMap.get("bizJmNum").toString();

		} else if (oMap.get("gv_url").toString().matches(".*150.2.236.145.*")) {
			sHttpUrl = "http://172.31.32.98:8010/ciespr/jsp_user/userNameCheck.jsp?HAN_NM="
					+ oMap.get("userName").toString()
					+ "&CTZ_NUM="
					+ oMap.get("bizJmNum").toString();

		} else if (oMap.get("gv_url").toString().matches(
				".*sales_plus.sktelecom.com.*")) {
			sHttpUrl = "http://220.103.248.105:8030/ciespr/jsp_user/userNameCheck.jsp?HAN_NM="
					+ oMap.get("userName").toString()
					+ "&CTZ_NUM="
					+ oMap.get("bizJmNum").toString();

		} else if (oMap.get("gv_url").toString().matches(
				".*salesplus.jungboboho.com.*")) {
			sHttpUrl = "http://220.103.248.105:8030/ciespr/jsp_user/userNameCheck.jsp?HAN_NM="
					+ oMap.get("userName").toString()
					+ "&CTZ_NUM="
					+ oMap.get("bizJmNum").toString();

		} else if (oMap.get("gv_url").toString().matches(".*10.40.10.82.*/")) {
			sHttpUrl = "http://uscan.co.kr:8030/ciespr/jsp_user/userNameCheck.jsp?HAN_NM="
					+ oMap.get("userName").toString()
					+ "&CTZ_NUM="
					+ oMap.get("bizJmNum").toString();

		} else if (oMap.get("gv_url").toString().matches(".*10.40.10.83.*")) {
			sHttpUrl = "http://uscan.co.kr:8030/ciespr/jsp_user/userNameCheck.jsp?HAN_NM="
					+ oMap.get("userName").toString()
					+ "&CTZ_NUM="
					+ oMap.get("bizJmNum").toString();

		} else if (oMap.get("gv_url").toString().equals(".*10.40.10.84.*")) {
			sHttpUrl = "http://uscan.co.kr:8030/ciespr/jsp_user/userNameCheck.jsp?HAN_NM="
					+ oMap.get("userName").toString()
					+ "&CTZ_NUM="
					+ oMap.get("bizJmNum").toString();

		} else {
			sHttpUrl = "http://uscan.co.kr:8030/ciespr/jsp_user/userNameCheck.jsp?HAN_NM="
					+ oMap.get("userName").toString()
					+ "&CTZ_NUM="
					+ oMap.get("bizJmNum").toString();

		}

		URL oUrl = null;
		URLConnection oHttpConnection = null;
		InputStream oIs = null;
		InputStreamReader oIsReader = null;
		BufferedReader oBReader = null;

		//System.out.println(sHttpUrl);

		try {
			oUrl = new URL(sHttpUrl);
			oHttpConnection = oUrl.openConnection();
			oIs = oHttpConnection.getInputStream();
			oIsReader = new InputStreamReader(oIs);
			oBReader = new BufferedReader(oIsReader);

			String inputLine = null;
			while ((inputLine = oBReader.readLine()) != null) {
				//html 제거
				inputLine = inputLine.replaceAll(
						"<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
				if (inputLine.equals("T")) {
					realBizYn = "Y";
				} else if (inputLine.equals("F")) {
					realBizYn = "F";
				}
				//log.debug(oBReader.readLine());
			}
			//System.out.println(realBizYn);

		} catch (MalformedURLException e) {
			log.debug("잘못된 URL =================================");
			log.debug(e);
			log.debug("==========================================");

		} catch (IOException ieo) {
			log.debug("==========================================");
			log.debug(ieo);
			log.debug("==========================================");

		}

		//================================================================================

		Map dMap = new HashMap();
		dMap.put("realBizYn", realBizYn);
		oMap.put("realBizYn", realBizYn);
		oMap.put("bizJmNum", CryptoUtils
				.encode(oMap.get("bizJmNum").toString()));

		if (realBizYn.equals("Y") || realBizYn.equals("F")) {

			updateCount = updateCount
					+ update("BASUSM00100.updateJm", oMap, onlineCtx); //실명인증 확인 후 Y,F일경우  저장
		}

		IRecordSet rs = queryForRecordSet("BASUSM00100.getDumyResult", dMap,
				onlineCtx);
		if (rs == null) {
			rs = new RecordSet("ds_findJm");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(1) });
		responseData.putRecordSet("ds_findJm", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이영진 (leeyoungjin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getUserIdList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getUserIdList method start");
		}

		Map fields = requestData.getFieldMap();
		IRecordSet rs = queryForRecordSet("BASUSM00100.getUserIdList", fields,
				onlineCtx);
		if (rs == null) {
			rs = new RecordSet("userIdList");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("userIdList", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 장화수 (janghwasoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getUkeyInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getUkeyInfo method start");
		}

		IRecordSet rs = queryForRecordSet("BASUSM00100.getUkeyInfo",
				requestData.getFieldMap(), onlineCtx);
		if (rs == null) {
			rs = new RecordSet("ds_uKey");
		}

		//String aaa = "TKEYLINK";
		//aaa = new sun.misc.BASE64Encoder().encode(aaa.getBytes());
		//System.out.println("=============: "+aaa);

		//IRecordSet rsVirtHist = getSqlManager("PsmcsDB").queryForRecordSet("POLFPO00400.getTest",requestData.getFieldMap(), onlineCtx);

		//Map sMap = (Map)getSqlManager("PsmcsDB").queryForRecordSet("POLFPO00400.getTest",requestData.getFieldMap(), onlineCtx);
		//System.out.println("=============: "+sMap.get("comm_req_dt").toString());

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_uKey", rs);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getPortalUserInfo(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getPortalUserInfo method start");
		}

		Map fields = requestData.getFieldMap();
		IRecordSet rs = queryForRecordSet("BASUSM00100.getPortalUserInfo", fields, onlineCtx);
		if (rs == null) {
			rs = new RecordSet("ds_PortalUserInfo");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_PortalUserInfo", rs);
		return responseData;
	}

}