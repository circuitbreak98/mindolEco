package com.sktst.batch.bas.uki.biz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.eai.mq.api.EaiMQApi;
import com.eai.mq.conf.EaiException;
import com.eai.mq.api.EaiApiData;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-03-30 09:00:00</li>
 * </ul>
 *
 * @author 원종윤 (wonjongyoon)
 */
public class BASUKI08120 extends AbsBatchJobBiz {

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
		
		super.getProperties();
		
		logMng.openLogFile(PROG_ID);
		
		if (log.isDebugEnabled()) {
			log.debug(PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();
		try {
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();
			
			int ret; // Used for multiple purposes
			String sDate = "";
			String sTime = "";
			String sRecv_buf = "";
			String sIF_CL = "";
			byte[] sByte = null;
			
			sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			sTime = new SimpleDateFormat("HHmmss").format(new Date());
			
			Map<String, Object> requestMap = new HashMap<String, Object>();
			
			requestMap.put("OP_DT", sDate);
			requestMap.put("OP_TM", sTime);
			
			/* I/F 임시 Test Start */
			//sRecv_buf= "99DTEST600007022733175          20090102091932 P292890000LGWB 0363844        1C김강현                                            600508       01086369546           BF0  D1485638       NA0000220020063926399         C                                            0708329307217F      Y                    224000130000N 00                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             1000000000011000N                                                  00000000055000";
//			sRecv_buf= "26D1485600001057633406          20090108200938P34429000007PTEF 0297572        10김성택                                            710508       01102754034         24D1485623                           C00000000000000000000000000043450000000000000SKE8 0505046                            N224000100000000000000                    100000000000000 20020129NA3";
//			sByte = sRecv_buf.getBytes();
//			sIF_CL = new String(sByte, 0, 2).trim();
//			requestMap.put("IF_CL", sIF_CL);
//			requestMap.put("IF_CTT", sRecv_buf);
//			/* I/F 임시 Test End */
//			
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

			try {
				EaiMQApi mqapi = new EaiMQApi();
				
				// EAI MQ API를 사용하기 위해 해당 큐 매니저에 연결 함
				ret = mqapi.mq_connect();
				System.out.println("connect ret = [" + ret + "]");
			
				while(true) {
					EaiApiData ApiData = new EaiApiData();
					ApiData.initGet();
					ApiData.m_mqget_t.in_intf_id = "ORD.AGN_SALE_INFO_MFF";
					
					if ((ret = mqapi.mq_get(ApiData)) != 0) {
						break;
						// throw new Exception("mqapi.mq_get error") ;
					}
		
					sRecv_buf = ApiData.m_mqget_t.out_recv_buf;
					System.out.println("RECV_BUF[" + sRecv_buf + "]");
					sByte = sRecv_buf.getBytes();
					sIF_CL = new String(sByte, 0, 2).trim();
					
					sqlMap.insert("BASUKI08100.insertUkeyIFMQ", requestMap);
					sqlMap.insert("BASUKI08100.insertUkeyIFLog", requestMap);
		
				}
			} catch(EaiException e) {
				System.out.println("EaiException = [" + e + "]");
			} catch(Exception e) {
				System.out.println("Exception = [" + e + "]");
			}
			
			// 처리 결과 commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			sqlMap.commitTransaction();

		} finally {
			// 처리 완료. (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.endTransaction");
			}
			sqlMap.endTransaction();
		}

		logMng.closeLogFile();
		return 0;
	}



}