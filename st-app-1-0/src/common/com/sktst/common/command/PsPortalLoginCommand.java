package com.sktst.common.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IChannel;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.ITransaction;
import nexcore.framework.core.data.IValueObject;
import nexcore.framework.core.exception.BaseException;
import nexcore.framework.core.exception.BaseRuntimeException;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.service.IServiceDelegator;
import nexcore.framework.core.service.IServiceLocator;
import nexcore.framework.core.util.DateUtils;
import nexcore.framework.online.channel.core.ICommandViewMap;
import nexcore.framework.online.channel.core.IRequestContext;
import nexcore.framework.online.channel.core.IResponseContext;
import nexcore.framework.online.channel.core.command.AbstractCommand;

import com.sktst.common.log.LogToDbUtil;
import com.sktst.common.user.PsLoginUserInfo;

public class PsPortalLoginCommand extends AbstractCommand {

	public IResponseContext execute(IRequestContext requestCtx,
			ICommandViewMap cmdViewMap) throws BaseException,
			BaseRuntimeException {

		// ServiceLocator를 이용하여 ServiceDelegator를 얻고
		// ServiceDelegator를 이용하여 Service Facade를 호출한다.

		IServiceLocator sLocator = (IServiceLocator) ComponentRegistry
				.lookup(ServiceConstants.SERVICE_LOCATOR);

		String txId = requestCtx.getOnlineContext().getTransaction().getTxId();
		IValueObject requestData = requestCtx.getBizData();
		IOnlineContext onlineCtx = requestCtx.getOnlineContext();

		if (txId == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("txId is null");
			}
			// return getResponseContext("FAIL", requestCtx, new DataSet(),
			// cmdViewMap);
			return getResponseContext(requestCtx, new DataSet(), cmdViewMap);
		}
		IServiceDelegator sDelegator = sLocator.getServiceDelegator(txId);
		
		String startTime = getDefaultCurrentDateTime();
		
		IValueObject responseData = sDelegator.delegate(txId, requestData,
				onlineCtx);
		
		String endTime = getDefaultCurrentDateTime();
		
		if (logger.isDebugEnabled()) {
			logger.debug("invoke Biz. Component with successfully.\n"
					+ responseData);
		}
		// 
		// Response IDataSet을 이용하여 ResponseContext 생성할 것.
		//
		// return getResponseContext("SUCCESS", requestCtx, responseData,
		// cmdViewMap);
		return getResponseContext(requestCtx, responseData, cmdViewMap);
	}
	
	public static String getDefaultCurrentDateTime() {
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sFormat.format(new Date());
    }	
}
