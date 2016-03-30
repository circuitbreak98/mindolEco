package com.sktst.common.organization;

import nexcore.framework.core.data.IRecordSet;

public interface IPsOrgLoader {

	IRecordSet getOrgComboList(String orgCl, String orgId);
}
