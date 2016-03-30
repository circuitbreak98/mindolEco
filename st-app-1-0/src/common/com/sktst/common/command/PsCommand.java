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

public class PsCommand extends AbstractCommand {

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
		
		logToDB(requestData,onlineCtx,startTime,endTime);
		
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
	
	@SuppressWarnings("unchecked")
	private void logToDB(IValueObject requestData, IOnlineContext onlineCtx, String startTime, String endTime){
		
		IChannel channel  = onlineCtx.getChannel();
		ITransaction transaction = onlineCtx.getTransaction();
		PsLoginUserInfo userInfo = (PsLoginUserInfo) onlineCtx.getUserInfo();
		IDataSet dataSet = (IDataSet) requestData;
		
		String userId = userInfo.getLoginId();
		String orgId = userInfo.getOrgId();
		String agencyCd = userInfo.getOrgAreaPosAgencyCd();
		String orgArea = userInfo.getOrgAreaId();		
		String txId = transaction.getTxId();
		String clientIP = channel.getRequestSystemXd();
		
		/***********************************
		 *  로그 생성 시 제외 메소드
		 ***********************************/
		Map exceptMethodMap = new HashMap();
		exceptMethodMap.put("sktst.adm.NTC#getAttatchList","sktst.adm.NTC#getAttatchList");
		exceptMethodMap.put("sktst.bas.CDM#getCommonCodeListByID","sktst.bas.CDM#getCommonCodeListByID");
		exceptMethodMap.put("sktst.dis.DCO#getCommList","sktst.dis.DCO#getCommList");
		exceptMethodMap.put("sktst.dis.DTR#getDisProdList","sktst.dis.DTR#getDisProdList");
		exceptMethodMap.put("sktst.bas.BCO#getDealPop","sktst.bas.BCO#getDealPop");
		exceptMethodMap.put("sktst.bas.BCO#getAgencyPop","sktst.bas.BCO#getAgencyPop");
		exceptMethodMap.put("sktst.acc.ACO#getCloseInfo","sktst.acc.ACO#getCloseInfo");
		exceptMethodMap.put("sktst.sal.SCO#getChrgrIdList","sktst.sal.SCO#getChrgrIdList");
		exceptMethodMap.put("sktst.dis.DCO#getDealCoList","sktst.dis.DCO#getDealCoList");
		exceptMethodMap.put("sktst.adm.NTC#getMainInfo","sktst.adm.NTC#getMainInfo");
		exceptMethodMap.put("sktst.sal.SCO#getDealCoNmByDealCoCd","sktst.sal.SCO#getDealCoNmByDealCoCd");
		exceptMethodMap.put("sktst.adm.NTC#updateCount","sktst.adm.NTC#updateCount");
		exceptMethodMap.put("sktst.adm.MNU#getLoginMenuAuthInfo","sktst.adm.MNU#getLoginMenuAuthInfo");
		exceptMethodMap.put("sktst.adm.NTC#getNoticePopInfo","sktst.adm.NTC#getNoticePopInfo");
		exceptMethodMap.put("sktst.adm.NTC#getNoticeList","sktst.adm.NTC#getNoticeList");
		exceptMethodMap.put("sktst.adm.NTC#updateReadInfo","sktst.adm.NTC#updateReadInfo");
		exceptMethodMap.put("sktst.adm.MNU#getMyMenu","sktst.adm.MNU#getMyMenu");
		
		if(!exceptMethodMap.containsKey(txId)){
			
			long txTime = 0L;
			if(startTime!=null && endTime!=null){
				txTime = Long.parseLong(endTime) - Long.parseLong(startTime);
			}
			
			String menuId = null;
			IRecordSet recordSet = dataSet.getRecordSet("nc_menuNumDs");
			if(recordSet != null){
				menuId = recordSet.get(0, "menu_num");
			}
			
			Map<String, String> queryParam = new HashMap<String, String>();
			queryParam.put("tranDt", startTime.substring(0, 8));
			queryParam.put("userId", userId);
			queryParam.put("startTime", startTime.substring(8, 17));
			queryParam.put("endTime", endTime.substring(8, 17));
			queryParam.put("menuId", menuId);
			queryParam.put("txId", txId);
			queryParam.put("clientIP", clientIP);
			queryParam.put("orgId", orgId);
			queryParam.put("agencyCd", agencyCd);
			queryParam.put("orgArea", orgArea);
			queryParam.put("txTime", String.valueOf(txTime));
			LogToDbUtil logDb = (LogToDbUtil) ComponentRegistry.lookup("ps.biz.LogToDb");
			logDb.logToDB("biz.log.insertTxLog", queryParam);		
		}		
	}
	
	public static String getDefaultCurrentDateTime() {
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sFormat.format(new Date());
    }	
}
