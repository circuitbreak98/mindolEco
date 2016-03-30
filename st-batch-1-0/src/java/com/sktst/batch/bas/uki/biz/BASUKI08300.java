package com.sktst.batch.bas.uki.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.text.*;
import java.math.*;

import com.eai.mq.api.EaiApiData;
import com.eai.mq.api.EaiMQApi;
import com.eai.mq.conf.EaiException;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : U.Key에서 I/F된 부가상품 가입정보 SAM File을 읽어서 부가서비스판매 자료에 INSERT / UPDATE 한다.</li>
 * <li>작성일 : 2009-03-30 09:00:00</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class BASUKI08300 extends AbsBatchJobBiz {

	private static final String PROG_ID = "BASUKI08300";
	private static final String USER_ID = "BASUKI0830";

	// 작업처리에 필요한 상수 설정
    private static String mqData        = "";
    private static String mqDataSend    = "";
    private static String mqID          = null;
	private static String mqMsgID       = null;
    private static String mqOpCode      = null;

	private static SimpleDateFormat todayFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    private static SimpleDateFormat longFormat  = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
	private static SimpleDateFormat timeFormat  = new SimpleDateFormat("HHmmss", Locale.KOREA); 

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	public int execute(Map request) throws Exception {
		
		super.getProperties();
		
		if (log.isDebugEnabled()) {
			log.debug(PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();
		
		mqData = (String)request.get("args1");

		try {
			
			logMng.openLogFile(PROG_ID);
			mqDataSend = mqReplace(mqData, "_", " ");
			logMng.writeLogFile(" Convert Before [" + mqData     + "]");
			logMng.writeLogFile(" Convert Before [" + mqDataSend + "]");

			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();
			
			// FILE을 읽어서 DB insert.
			openDataFileAddDB(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("execute Exception : " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
			}

		} finally {
			// 처리 완료. (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.endTransaction");
			}
			sqlMap.endTransaction();

			logMng.closeLogFile();
		}
		return 0;
	}

	/**
	 * Source String내의 특정 문자를 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @return void
	 * 
	 * @throws Exception
	 */
	private static String mqReplace(String sStr, String bStr, String aStr){
		byte[]    b = sStr.getBytes();
		int     len = b.length;

		String tStr = "";
		for (int i = 0; i < len; i++){
			if (bStr.equals(sStr.substring(i, i + 1))) {
				tStr += aStr;
			} else {
				tStr += sStr.substring(i, i + 1);
			}
		}

		return tStr;
	}

	/**
	 * Parameter로 받은 String을 MQ를 통해 판매점 포탈로 Put 한다.
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void openDataFileAddDB(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("openDataFileAddDB method start......");

        // TBAS_UKEY_IF_LOG에 결과 저장
		Map<String, Object> requestMapUpd = new HashMap<String, Object>();

		int ret = 0;

		try {

			EaiMQApi mqapi = new EaiMQApi();

			// EAI MQ API를 사용하기 위해 해당 큐 매니저에 연결 함
			logMng.writeLogFile("    before Connect ..");
			ret = mqapi.mq_connect();
			logMng.writeLogFile("    After  Connect .." + ret);
			if (ret != 0) {
				logMng.writeLogFile("    MQ Connect Error.. [" + ret + "] [" + mqDataSend + "]");
			}

    		EaiApiData apiData      = null;

	        // Syncronous PUT 모드.
	        mqID = "TPS.MAPP_INFO_MFF";

	        apiData = new EaiApiData();
	        apiData.initPut();
	        apiData.m_mqput_t.in_intf_id = mqID;
	        apiData.m_mqput_t.in_intf_msg_id = mqMsgID;
	        apiData.m_mqput_t.in_intf_op_code = mqOpCode;
	        apiData.m_mqput_t.in_send_buf = mqDataSend;
	        apiData.m_mqput_t.in_send_buf_len = mqDataSend.getBytes().length;

            if( ( ret = mqapi.mq_put(apiData)) != 0 ) {

        		// 전송 실패
            	logMng.writeLogFile("    PUT Fail "+ret + "[" + mqDataSend + "]");
                return;
            }

            if( (ret = mqapi.mq_commit()) != 0 ) {
        		// Commit 실패
            	logMng.writeLogFile("    COMMIT Fail "+ret + "[" + mqDataSend + "]");
                return;
            }

            logMng.writeLogFile("    PUT SUCCESS "+ret + "[" + mqDataSend + "]");

    		Calendar cal     = Calendar.getInstance(Locale.KOREA);
    		Date     cDate   = cal.getTime();
    		String   sOpDt   = todayFormat.format(cDate);
    		String   sOpTm   = timeFormat.format(cDate);
    		String   sInsDtm = longFormat.format(cDate);

            requestMapUpd.put("OP_DT",    sOpDt);
            requestMapUpd.put("OP_TM",    sOpTm);
            requestMapUpd.put("IF_CL",   "PT");
            requestMapUpd.put("IF_CTT",   mqDataSend);
            requestMapUpd.put("OP_RSLT", "S");
            requestMapUpd.put("INS_DTM",  sInsDtm);
            requestMapUpd.put("MOD_DTM",  sInsDtm);

            sqlMap.update("BASUKI08300.addBasUkeyIFLog", requestMapUpd);
			
			// EAI MQ API사용후 connection을 해제 
			if ((ret = mqapi.mq_disconnect()) != 0) {
				logMng.writeLogFile("    Disconnect Fail " + ret);
				throw new Exception("mqapi.mq_disconnect error");
			}			
		} finally {
		}

	}
}