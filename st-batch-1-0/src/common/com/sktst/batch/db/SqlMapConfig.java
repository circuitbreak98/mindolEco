package com.sktst.batch.db;

import java.io.Reader;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * 배치 SqlMapClient정의용 클래스.
 * @author admin
 *
 */
public class SqlMapConfig {

	private static final String sqlMapConfigFile = "SqlMapConfig.xml";	
	private static final SqlMapClient sqlMap;

	static {
		Reader reader = null;
		try {
			reader = Resources.getResourceAsReader(sqlMapConfigFile);
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error initializing class. Cause:" + e);
		} finally {
			if(reader != null) {
				safeClose(reader);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public static SqlMapClient getSqlMapInstance() {
		return sqlMap;
	}
	
	/**
	 * 
	 * @return
	 */
	public static void safeClose(Reader reader) {
		if(reader != null) {
			try {
				reader.close();
			} catch(Exception e){
				throw new RuntimeException("Error safeClose Cause:" + e);
			}
		}
	}
	
}
