/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.adm.msg.biz;

import java.rmi.RemoteException;
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
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.message.IMessageManager;
import nexcore.framework.core.message.internal.DefaultMessageManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;
import nexcore.framework.online.channel.util.DummyCoreDataUtil;

import org.apache.commons.logging.Log;

import com.sktst.adm.msg.ejb.MSG;
import com.sktst.adm.msg.ejb.MSGHome;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/Admin</li>
 * <li>설 명 : 메시지를 관리하는 컴포넌트. 
 * 본 컴포넌트에서 관리하는 메시지는 프레임워크 단의 DB를 사용하므로 
 * 일단 다른 컴포넌트와는 달리 쿼리 실행/호출 메소드를 
 * getSqlManager(Constants.DEFAULT_DS)로 부터 받아서 처리해야 한다.
 * </li>
 * <li>작성일 : 2009-02-11 18:46:52</li>
 * </ul>
 * 
 * @author admin (admin)
 */
public class ADMMSG00100 extends BaseBizUnit {

	/**
	 * 메시지 리스트를 취득한다.
	 * 
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체 - field : messageId [필드1] -
	 *            field : messageName [필드2] - field : messageTypeXd [필드3]
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getMessageList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMMSG00100.getMessageList start");
		}

		IRecordSet result = getSqlManager(Constants.DEFAULT_DS)
				.queryForRecordSet("ADMMSG00100.getMessageList",
						requestData.getFieldMap(), onlineCtx);
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(result.getRecordCount()) });
		responseData.putRecordSet("ds_messageList", result);
		return responseData;
	}

	/**
	 * 추가 및 수정, 삭제된 메시지를 저장, 처리한다.
	 * 
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveMessageList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("ADMMSG00100.saveMessageList start");
		}

		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;

		IRecordSet rs = requestData.getRecordSet("ds_messageList");

		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (DELETE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					deleteCount = deleteCount
							+ getSqlManager(Constants.DEFAULT_DS).delete(
									"ADMMSG00100.saveMessageList_delete",
									rs.getRecord(i), onlineCtx);
				}
			}
			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (INSERT_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					getSqlManager(Constants.DEFAULT_DS).insert(
							"ADMMSG00100.saveMessageList_add", rs.getRecord(i),
							onlineCtx);
					insertCount++;
				} else if (UPDATE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					updateCount = updateCount
							+ getSqlManager(Constants.DEFAULT_DS).update(
									"ADMMSG00100.saveMessageList_update",
									rs.getRecord(i), onlineCtx);
				}
			}
			cudAllCount = insertCount + updateCount + deleteCount;
		}
		if (cudAllCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID);
		}

		// message cache reset
		//		IMessageManager messageManager = (IMessageManager) ComponentRegistry
		//				.lookup(ServiceConstants.MESSAGE);
		//		((DefaultMessageManager) messageManager).refresh();

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount, "" + deleteCount });
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
	public IDataSet refreshMessage(IDataSet requestData,
			IOnlineContext onlineCtx) {

		// message cache reset
		IMessageManager messageManager = (IMessageManager) ComponentRegistry
				.lookup(ServiceConstants.MESSAGE);
		((DefaultMessageManager) messageManager).refresh();

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_SIMPLE_MESSAGE_ID, new String[] {});
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
	public IDataSet refreshMessageAll(IDataSet requestData,
			IOnlineContext onlineCtx) {

		MSG msgComp = null;

		// IP 정보 취득.
		IRecordSet rsIp = queryForRecordSet("ADMMSG00100.getIpList",
				requestData.getFieldMap(), onlineCtx);

		if (rsIp == null || rsIp.getRecordCount() == 0) {
			// 서버IP정보가 존재 하지 않습니다. 공통코드를 확인하세요.
			throw new BizRuntimeException("PSMW3006");
		}

		Context iniCtx = null;
		Properties props = null;
		Object obj = null;
		MSGHome home = null;
		Map mIp = null;

		for (int i = 0; i < rsIp.getRecordCount(); i++) {

			mIp = rsIp.getRecordMap(i);

			try {

				props = new Properties();
				props.put(Context.INITIAL_CONTEXT_FACTORY,
						"jeus.jndi.JEUSContextFactory");
				props.put(Context.URL_PKG_PREFIXES, "jeus.jndi.jns.url");
				props.put(Context.PROVIDER_URL, mIp.get("IP"));
				//props.put(Context.PROVIDER_URL, "localhost:9736");
				iniCtx = new InitialContext(props);

				obj = iniCtx.lookup("ejb/sktst/adm/MSG");
				home = (MSGHome) PortableRemoteObject
						.narrow(obj, MSGHome.class);
				msgComp = (MSG) home.create();

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
				msgComp.refreshMessage(requestData, DummyCoreDataUtil
						.getDummyOnlineCtx());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_SIMPLE_MESSAGE_ID, new String[] {});
	}

}