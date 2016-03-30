/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.opc.biz;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import nexcore.framework.core.Constants;
import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.parameter.IConfigurationManager;
import nexcore.framework.core.parameter.internal.DefaultConfigurationManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.ISqlManager;
import nexcore.framework.online.channel.util.DummyCoreDataUtil;

import com.sktst.adm.opc.ejb.OPC;
import com.sktst.adm.opc.ejb.OPCHome;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-06-04 13:31:40</li>
 * </ul>
 *
 * @author admin (admin)
 */
public class ADMOPC00100 extends BaseBizUnit {

	private final static String DEFAULT_CLOSE_FROM_TIME_KEY = "operation.default.close.from";

	private final static String DEFAULT_CLOSE_TO_TIME_KEY = "operation.default.close.to";

	private final static String CLOSE_FROM_DATE_KEY = "operation.close.from";

	private final static String CLOSE_TO_DATE_KEY = "operation.close.to";

	/**
	 * 점검 시간 정보를 DB로 부터 가져온다.
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : defCloseFromTime [defCloseFromTime] 
	 *	- field : defCloseToTime [defCloseToTime] 
	 *	- field : closeFromDate [closeFromDate] 
	 *	- field : closeToDate [closeToDate] 
	 */
	public IDataSet getCloseProperties(IDataSet requestData,
			IOnlineContext onlineCtx) {

		IRecordSet result = getSqlManager(Constants.DEFAULT_DS)
				.queryForRecordSet("ADMOPC00100.getDBProperties",
						requestData.getFieldMap(), onlineCtx);

		String defCloseFromTime = null;
		String defCloseToTime = null;
		String closeFromDate = null;
		String closeToDate = null;

		for (int i = 0; i < result.getRecordCount(); i++) {
			if (DEFAULT_CLOSE_FROM_TIME_KEY.equalsIgnoreCase(result
					.getRecord(i).get("PARAMETER_KEY")))
				defCloseFromTime = result.getRecord(i).get("PARAMETER_VALUE");
			else if (DEFAULT_CLOSE_TO_TIME_KEY.equalsIgnoreCase(result
					.getRecord(i).get("PARAMETER_KEY")))
				defCloseToTime = result.getRecord(i).get("PARAMETER_VALUE");
			else if (CLOSE_FROM_DATE_KEY.equalsIgnoreCase(result.getRecord(i)
					.get("PARAMETER_KEY")))
				closeFromDate = result.getRecord(i).get("PARAMETER_VALUE");
			else if (CLOSE_TO_DATE_KEY.equalsIgnoreCase(result.getRecord(i)
					.get("PARAMETER_KEY")))
				closeToDate = result.getRecord(i).get("PARAMETER_VALUE");

		}

		Map rtnMap = new HashMap();

		rtnMap.put("defCloseFromTime", defCloseFromTime);
		rtnMap.put("defCloseToTime", defCloseToTime);
		rtnMap.put("closeFromDate", closeFromDate);
		rtnMap.put("closeToDate", closeToDate);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "4" });

		responseData.putFieldObjectMap(rtnMap);

		return responseData;
	}

	/**
	 * 점검 시간 정보를 DB에 저장한다.
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : defCloseFromTime [defCloseFromTime] 
	 *	- field : defCloseToTime [defCloseToTime] 
	 *	- field : closeFromDate [closeFromDate] 
	 *	- field : closeToDate [closeToDate] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateCloseProperties(IDataSet requestData,
			IOnlineContext onlineCtx) {

		String defCloseFromTime = requestData.getField("defCloseFromTime");
		String defCloseToTime = requestData.getField("defCloseToTime");
		String closeFromDate = requestData.getField("closeFromDate");
		String closeToDate = requestData.getField("closeToDate");

		ISqlManager sqlManager = getSqlManager(Constants.DEFAULT_DS);

		Map updateParam = new HashMap();
		int updateCount = 0;

		updateParam.put("key", DEFAULT_CLOSE_FROM_TIME_KEY);
		updateParam.put("value", defCloseFromTime);
		updateCount = updateCount
				+ sqlManager.update("ADMOPC00100.updateDBProperty",
						updateParam, onlineCtx);

		updateParam.put("key", DEFAULT_CLOSE_TO_TIME_KEY);
		updateParam.put("value", defCloseToTime);
		updateCount = updateCount
				+ sqlManager.update("ADMOPC00100.updateDBProperty",
						updateParam, onlineCtx);

		updateParam.put("key", CLOSE_FROM_DATE_KEY);
		updateParam.put("value", closeFromDate);
		updateCount = updateCount
				+ sqlManager.update("ADMOPC00100.updateDBProperty",
						updateParam, onlineCtx);

		updateParam.put("key", CLOSE_TO_DATE_KEY);
		updateParam.put("value", closeToDate);
		updateCount = updateCount
				+ sqlManager.update("ADMOPC00100.updateDBProperty",
						updateParam, onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
						+ updateCount });

		return responseData;
	}

	/**
	 * 점검 시간 정보를 현재 서버의 Cache에 저장하여 IConfigurationManager에서 공통으로 사용할 수 있게 한다.
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet refreshCloseProperties(IDataSet requestData,
			IOnlineContext onlineCtx) {

		IConfigurationManager cm = (IConfigurationManager) ComponentRegistry
				.lookup(ServiceConstants.CONFIGURATION);
		((DefaultConfigurationManager) cm).refresh();
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_SIMPLE_MESSAGE_ID, new String[] {});
	}

	/**
	 * 
	 *
	 * @author 한병곤 (hanbyunggon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet refreshConfigAll(IDataSet requestData,
			IOnlineContext onlineCtx) {

		OPC opcComp = null;

		// IP 정보 취득.
		IRecordSet rsIp = queryForRecordSet("ADMOPC00100.getIpList",
				requestData.getFieldMap(), onlineCtx);

		if (rsIp == null || rsIp.getRecordCount() == 0) {
			// 서버IP정보가 존재 하지 않습니다. 공통코드를 확인하세요.
			throw new BizRuntimeException("PSMW3006");
		}

		Context iniCtx = null;
		Properties props = null;
		Object obj = null;
		OPCHome home = null;
		Map mIp = null;

		for (int i = 0; i < rsIp.getRecordCount(); i++) {

			mIp = rsIp.getRecordMap(i);

			try {

				props = new Properties();
				props.put(Context.INITIAL_CONTEXT_FACTORY,
						"jeus.jndi.JEUSContextFactory");
				props.put(Context.URL_PKG_PREFIXES, "jeus.jndi.jns.url");
				props.put(Context.PROVIDER_URL, mIp.get("IP"));
				iniCtx = new InitialContext(props);

				obj = iniCtx.lookup("ejb/sktst/adm/OPC");
				home = (OPCHome) PortableRemoteObject
						.narrow(obj, OPCHome.class);
				opcComp = (OPC) home.create();

			} catch (Exception ex) {
				if (iniCtx != null) {
					try {
						iniCtx.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				ex.printStackTrace();
				continue;
			}

			try {
				opcComp.refreshCloseProperties(requestData, DummyCoreDataUtil
						.getDummyOnlineCtx());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_SIMPLE_MESSAGE_ID, new String[] {});
	}

}