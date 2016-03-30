package com.sktst.batch.bas.uki.biz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.eai.mq.api.EaiMQApi;
import com.eai.mq.conf.EaiException;
import com.eai.mq.api.EaiApiData;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;
import com.sktst.batch.log.LogManager;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-03-30</li>
 * </ul>
 *
 * @author 원종윤 (wonjongyoon)
 */
public class BASUKI08130 extends AbsBatchJobBiz {
	public int execute(Map request) throws Exception {
		ThreadGet2 th = new ThreadGet2();
		th.start();
		return 0;
	}
}
class ThreadGet2 extends Thread {
	private static final String PROG_ID = "BASUKI08100";
	
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
		BASUKI08130 bau = new BASUKI08130();
		bau.getProperties();
		
		bau.logMng.openLogFile(PROG_ID);
		
		Log log = LogManager.getErrorLog();  // 0.001초 소요됨
		
		if (log.isDebugEnabled()) {
			log.debug(PROG_ID + ".execute");
		}
		SqlMapClient sqlMap = bau.getSqlMapClient();  // 약 0.8초 소요됨
		try {
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();
			
			/* I/F 임시 Test Start */
//			int ret; // Used for multiple purposes
//			String sDate = "";
//			String sTime = "";
//			String sRecv_buf = "";
//			String sIF_CL = "";
//			byte[] sByte = null;
//			
//			sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
//			sTime = new SimpleDateFormat("HHmmss").format(new Date());
//			
//			Map<String, Object> requestMap = new HashMap<String, Object>();
			
			
			//sRecv_buf= "99DTEST600007022733175          20090102091932 P292890000LGWB 0363844        1C김강현                                            600508       01086369546           BF0  D1485638       NA0000220020063926399         C                                            0708329307217F      Y                    224000130000N 00                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             1000000000011000N                                                  00000000055000";
//			sRecv_buf= "26D1485600001057633406          20090108200938P34429000007PTEF 0297572        10김성택                                            710508       01102754034         24D1485623                           C00000000000000000000000000043450000000000000SKE8 0505046                            N224000100000000000000                    100000000000000 20020129NA3";
//			sByte = sRecv_buf.getBytes();
//			sIF_CL = new String(sByte, 0, 2).trim();
//			requestMap.put("OP_DT", sDate);
//			requestMap.put("OP_TM", sTime);
//			requestMap.put("IF_CL", sIF_CL);
//			requestMap.put("IF_CTT", sRecv_buf);
//			// Ukey I/F MQ 테이블에 데이터 넣기 
//			if(log.isDebugEnabled()){
//				log.debug("BASUKI08100.execute.addUkeyIFMQ");
//			}			
//			sqlMap.insert("BASUKI08100.addUkeyIFMQ", requestMap);
//			
//			// Ukey I/F Log 테이블에 데이터 넣기 
//			if(log.isDebugEnabled()){
//				log.debug("BASUKI08100.execute.addUkeyIFLog");
//			}			
//			sqlMap.insert("BASUKI08100.addUkeyIFLog", requestMap);
//			
//			// Ukey I/F MQ 테이블로부터 데이터 가져오기
//			if(log.isDebugEnabled()){
//				log.debug("BASUKI08100.execute.getUKeyIFSeq");
//			}			
//			Map resultMap = (Map)sqlMap.queryForObject("BASUKI08100.getUKeyIFSeq", requestMap);
//			int seq = Integer.parseInt(resultMap.get("SEQ").toString());
//			requestMap.put("SEQ", seq);
//			
//			// 업무단 프로시저 호출하는 프로시저 호출 
//			if(log.isDebugEnabled()){
//				log.debug(PROG_ID + ".execute.callUkeyIFProcedure");
//			}			
//			sqlMap.queryForObject(PROG_ID + ".callUkeyIFProcedure", requestMap);
//			/* I/F 임시 Test End */
			
			try {
				
				int ret; // Used for multiple purposes
				String sDate = "";
				String sTime = "";
				String sRecv_buf = "";
				String sIF_CL = "";
				byte[] sByte = null;
				
				sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
				sTime = new SimpleDateFormat("HHmmss").format(new Date());
				
				Map<String, Object> requestMap = new HashMap<String, Object>();
				
				EaiMQApi mqapi = new EaiMQApi();  // 약 0.15초 소요
				// EAI MQ API를 사용하기 위해 해당 큐 매니저에 연결 함
				ret = mqapi.mq_connect();
				bau.logMng.writeLogFile("connect ret = [" + ret + "]");
				Map resultMap = null; 
				int nextSeq = 0;
				EaiApiData ApiData = new EaiApiData();
				while(true) {
					ApiData.initGet();
					ApiData.m_mqget_t.in_intf_id = "ORD.AGN_SALE_INFO_MFF";
					if ((ret = mqapi.mq_get(ApiData)) != 0) {
						break;
						// throw new Exception("mqapi.mq_get error") ;
					}
		
					sRecv_buf = ApiData.m_mqget_t.out_recv_buf;
					bau.logMng.writeLogFile("Header=" + ApiData.m_mqget_t.out_eai_header);
					bau.logMng.writeLogFile("MSG_ID=" + new String(ApiData.m_mqget_t.out_mq_msg_id));
					bau.logMng.writeLogFile("RECV_BUF[====================================>\n" + sRecv_buf);
					
					sByte = sRecv_buf.getBytes();
					sIF_CL = new String(sByte, 0, 2).trim();

					requestMap.put("OP_DT", sDate);
					requestMap.put("OP_TM", sTime);
					requestMap.put("IF_CL", sIF_CL);
					requestMap.put("IF_CTT", sRecv_buf);
					
					// Ukey I/F MQ 테이블에 데이터 넣기
					if(log.isDebugEnabled()){
						log.debug("BASUKI08100.execute.addUkeyIFMQ");
					}			
					sqlMap.insert("BASUKI08100.addUkeyIFMQ", requestMap);
					
					// Ukey I/F Log 테이블에 데이터 넣기
					if(log.isDebugEnabled()){
						log.debug("BASUKI08100.execute.addUkeyIFLog");
					}			
					sqlMap.insert("BASUKI08100.addUkeyIFLog", requestMap);
					
					// Ukey I/F MQ 테이블로부터 SEQ 데이터 가져오기
					if(log.isDebugEnabled()){
						log.debug("BASUKI08100.execute.getUKeyIFSeq");
					}			
					resultMap = (Map)sqlMap.queryForObject("BASUKI08100.getUKeyIFSeq", requestMap);
					nextSeq = Integer.parseInt(resultMap.get("SEQ").toString());
					requestMap.put("SEQ", nextSeq);
					
					// 업무단 프로시저 호출하는 프로시저 호출 
					if(log.isDebugEnabled()){
						log.debug("BASUKI08100.execute.callUkeyIFProcedure");
					}
					bau.logMng.writeLogFile((String)(requestMap.get("OP_DT")));
					bau.logMng.writeLogFile((String)(requestMap.get("OP_TM")));
					bau.logMng.writeLogFile((String)(requestMap.get("IF_CL")));
					bau.logMng.writeLogFile((String)(requestMap.get("IF_CTT")));
					bau.logMng.writeLogFile(requestMap.get("SEQ").toString());

					sqlMap.queryForObject("BASUKI08100.callUkeyIFProcedure", requestMap);

					// Commit MQ
					if ((ret = mqapi.mq_commit()) != 0) {
						throw new Exception("mqapi.mq_commit error");
					}
				} // end while
				// EAI MQ API사용후 connection을 해제  --> Better to define in the final clause for abnormal exception.
				if ((ret = mqapi.mq_disconnect()) != 0) {
					throw new Exception("mqapi.mq_disconnect error");
				}
			} catch(Exception e) {
				bau.logMng.writeLogFile("MQ Exception = [" + e + "]");
			}
			// 처리 결과 commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			sqlMap.commitTransaction();
		} catch(Exception e) {
			bau.logMng.writeLogFile("Transaction Exception = [" + e + "]");
		}
		finally {
			// 처리 완료. (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.endTransaction");
			}
			sqlMap.endTransaction();
		}

		bau.logMng.closeLogFile();
		return 0;
	}



}