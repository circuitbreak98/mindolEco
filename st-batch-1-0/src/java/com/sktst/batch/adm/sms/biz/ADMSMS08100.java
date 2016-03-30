package com.sktst.batch.adm.sms.biz;

import com.eai.mq.api.EaiApiData;
import com.eai.mq.api.EaiMQApi;
import com.eai.mq.conf.EaiException;
import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준</li>
 * <li>설 명 : SMS 수신 프로그램</li>
 * <li>작성일 : 2009-05-11</li>
 * </ul>
 *
 * @author 원종윤 (wonjongyoon)
 */
public class ADMSMS08100 extends AbsBatchJobBiz {

	private static final String PROG_ID = "ADMSMS08100";
	
	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	public int execute(Map request) throws Exception {
		
		super.getProperties();
		SqlMapClient sqlMap = getSqlMapClient();
		try {
			
			logMng.openLogFile(PROG_ID);
			
			if (log.isDebugEnabled()) {
				log.debug("ADMSMS08100.execute.startTransaction");
			}
			logMng.writeLogFile("ADMSMS08100.execute.startTransaction");
			sqlMap.startTransaction();
			
			// 업무 시작.
			// MQ로부터 수신한 데이터를 DB insert.
			getMqAddDB(sqlMap);

		} catch(Exception e) {
			logMng.writeLogFile("execute Exception : " + e);
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e);
			}
		} finally {
			sqlMap.endTransaction();
			// 처리 완료. (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug("ADMSMS08100.execute.endTransaction");
			}
			logMng.closeLogFile();
		}
		return 0;
	}

	/**
	 * MQ로부터 수신한 데이터를 DB insert.
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void getMqAddDB(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("getMqAddDB method start......");
		
		String sRecvBuf = "";

		try {
			
			int ret; // MQ의 리턴값 확인용 변수
			EaiMQApi mqapi = new EaiMQApi();
			// EAI MQ API를 사용하기 위해 해당 큐 매니저에 연결 함
			ret = mqapi.mq_connect();
			
			EaiApiData ApiData = new EaiApiData();
			
			while(true) {
				ApiData.initGet();
				ApiData.m_mqget_t.in_intf_id = "SMS.TPS_SMS_RSLT_MFF";
				if ((ret = mqapi.mq_get(ApiData)) != 0) {
					if (ret == -2) continue; // no message일 때 재시도
					if ((ret = mqapi.mq_rollback()) != 0) {
						logMng.writeLogFile("mqapi.mq_rollback error: ret = " + ret);
						throw new Exception("mqapi.mq_rollback error");
					}
					break;
				}
				
				sRecvBuf = ApiData.m_mqget_t.out_recv_buf;
				
				logMng.writeLogFile("sRecvBuf=>[" + sRecvBuf + "]"); 
				
				// MQ 데이터를 SMS 결과 테이블(TADM_SMS_RESULT)에 insert
				addSmsResult(sRecvBuf, sqlMap);
				
				// Commit MQ
				if ((ret = mqapi.mq_commit()) != 0) {
					logMng.writeLogFile("mqapi.mq_commit error : ret = " + ret);
					throw new Exception("mqapi.mq_commit error");
				}
			} // end while
			
			// EAI MQ API사용후 connection을 해제 
			if ((ret = mqapi.mq_disconnect()) != 0) {
				logMng.writeLogFile("mqapi.mq_disconnect error : ret = " + ret);
				throw new Exception("mqapi.mq_disconnect error");
			}

		} catch(EaiException e) {
			logMng.writeLogFile("getMqAddDB EaiException = [" + e + "]");
		} catch (Exception e) {
			logMng.writeLogFile("getMqAddDB Exception = [" + e + "]");
			if (log.isDebugEnabled()) {
				log.debug(e);
			}
		} finally {
			logMng.writeLogFile("getMqAddDB method end......");
		}
	}

	/**
	 * SMS 결과 테이블(TADM_SMS_RESULT)에 insert한다.
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fieldBuffer
	 *            String
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void addSmsResult(String fieldBuffer, SqlMapClient sqlMap){
		Map<String, Object> requestMap = new HashMap<String, Object>();

		try {
			byte[] sByte = fieldBuffer.getBytes();		
			int msgSerNum = Integer.parseInt(new String(sByte, 25, 15).trim());
			
			requestMap.put("MSG_SER_NUM", msgSerNum);
			logMng.writeLogFile("msgSerNum=[" + msgSerNum + "]");
			
			// SMS 전송 테이블에서 SMS전송일, SMS전송차수 가져오기
			Map resultMap = (Map)sqlMap.queryForObject("ADMSMS08100.getSmsTranInfo", requestMap);
			
			logMng.writeLogFile(resultMap.toString());
			String tran_dt = resultMap.get("TRAN_DT").toString();
			String tran_cnt = resultMap.get("TRAN_CNT").toString();
			
			requestMap.put("RESULT_MSG", fieldBuffer);
			requestMap.put("TRAN_DT", tran_dt);
			requestMap.put("TRAN_CNT", tran_cnt);
			
			// SMS 결과 테이블에 Insert
			sqlMap.insert("ADMSMS08100.addSmsResult", requestMap);
			
			// 처리 결과 commit
			sqlMap.commitTransaction();
			
		} catch(Exception e) {
			logMng.writeLogFile("addSmsResult Exception = [" + e + "]");
		}
	}
}