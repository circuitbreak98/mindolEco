package com.sktst.batch.hello;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

public class HelloBizUnit extends AbsBatchJobBiz{

	/* (non-Javadoc)
	 * @see com.sktst.batch.base.IBatchBizUnit#execute(java.util.Map)
	 */
	public int execute(Map request) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("HelloBizUnit.execute");
		}
		SqlMapClient sqlMap = getSqlMapClient();
		try{
			if(log.isDebugEnabled()){
				log.debug("HelloBizUnit.execute.startTransaction");
			}			
			
			//업무 시작.
			sqlMap.startTransaction ();

			//요청 데이터를 Map형태로 만들어서 사용할 수도 있으며,
			//일반 Bean 클래스를 만들어서도 사용이 가능하다. 
			//단, 이경우, SqlMap정의 파일, xsql에서 resultClass 부분을 적절히 설정변경해야 한다.
			Map<String, String> requestMap = new HashMap<String, String>();
			requestMap.put("ID", "test");
			
			if(log.isDebugEnabled()){
				log.debug("HelloBizUnit.execute.queryForObject1");
			}			
			Map resultMap = (Map)sqlMap.queryForObject("HelloBizUnit.selectHello", requestMap);
			if(log.isDebugEnabled()){
				log.debug("HelloBizUnit.execute.queryForObject2");
			}			
			resultMap = (Map)sqlMap.queryForObject("HelloBizUnit.selectHello", requestMap);
			if(log.isDebugEnabled()){
				log.debug("HelloBizUnit.execute.queryForObject3");
			}			
			List resultList = (List)sqlMap.queryForList("HelloBizUnit.selectHello", requestMap);
			System.out.println(resultList);
			
			requestMap.put("ID", "test02");
			requestMap.put("NAME", "테스트01");
			requestMap.put("MEMO", "시험입니다.");
//			delete(sqlMap, requestMap);
//			insert(sqlMap, requestMap);
//			update(sqlMap, requestMap);
			
			//처리 결과 commit
			if(log.isDebugEnabled()){
				log.debug("HelloBizUnit.execute.commitTransaction");
			}			
			sqlMap.commitTransaction ();
			
		} finally {
			//처리 완료. (commit이 안된 경우 rollback)
			if(log.isDebugEnabled()){
				log.debug("HelloBizUnit.execute.endTransaction");
			}			
			sqlMap.endTransaction ();
		}
		return 0;
	}
	
	/**
	 * insert예제.
	 * @param sqlMap
	 * @param requestMap
	 * @throws Exception
	 */
	private void insert(SqlMapClient sqlMap, Map requestMap) throws Exception {
		sqlMap.insert("HelloBizUnit.insertHello", requestMap);
		
	}

	/**
	 * delete예제.
	 * @param sqlMap
	 * @param requestMap
	 * @throws Exception
	 */
	private void delete(SqlMapClient sqlMap, Map requestMap) throws Exception {
		sqlMap.delete("HelloBizUnit.insertHello", requestMap);
	}
	
	/**
	 * update예제.
	 * @param sqlMap
	 * @param requestMap
	 * @throws Exception
	 */
	private void update(SqlMapClient sqlMap, Map requestMap) throws Exception {
		sqlMap.update("HelloBizUnit.insertHello", requestMap);
	}
	
}
