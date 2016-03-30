package com.sktst.common.log;

import java.util.Map;

import nexcore.framework.core.prototype.AbsFwkService;
import nexcore.framework.integration.db.ISqlManager;

public class LogToDbUtil extends AbsFwkService{
	
	private ISqlManager sqlManager;
	
	public void setSqlManager(ISqlManager sqlManager){
		this.sqlManager = sqlManager;
	}
	
	public void logToDB(String mapStmtName, Map queryParam){
		sqlManager.insert(mapStmtName, queryParam);
	}
}
