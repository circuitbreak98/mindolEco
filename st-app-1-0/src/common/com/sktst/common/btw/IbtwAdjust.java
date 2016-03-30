package com.sktst.common.btw;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;

/**
 * 
 * @author admin
 *
 */
public interface IbtwAdjust {

	double getAdjustAmt(String fPosAgency, String fEqpModel, String fSettlCond, String fSaleDtm, double fSaleAmt, IRecordSet listAgency, IRecordSet listAdjust);
	IRecordSet getAgencyDataset(String fApplyMonth, String fPosAgency);
	IRecordSet getAdjustDataset(String fApplyMonth);
	
}