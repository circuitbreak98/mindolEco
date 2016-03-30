package com.sktst.batch.bas.ukc.biz;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;
import com.sktst.batch.util.crypt.aesUtils;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST</li>
 * <li>설 명 : TBAS_PORTAL_ORG에 있는 I/F 대리점,판매점 DATA를 tbas_deal_co_mgmt에 insert . </li>
 * <li>작성일 : 2012-02-02 10:43:46</li>
 * </ul>
 *
 * @author 이문규
 */
public class BASUKC09100 extends AbsBatchJobBiz {

	private static final String PROG_ID = "BASUKC09100";
	private static final String NULL_VALUE = "";
	private static String fileTime = "";
	private PrintWriter dataFile;

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 이문규
	 *
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	public int execute(Map request) throws Exception {
		super.getProperties();
		
		//① DB 정보 취득 (SqlMapClient 인스턴스 취득)
		SqlMapClient sqlMap = getSqlMapClient();

		try {
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			logMng.openLogFile(PROG_ID);

			//로그 출력 (AbsBatchJobBiz 클래스에서 제공하는 변수 log를 사용)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute");
			}

			if (log.isDebugEnabled()) {
				log.debug(request.toString());
			}

			String sProcDt = "";
			String sActFlag = "";
			if( request.size() > 1 ) {
				sProcDt = (String)request.get("args1");
				log.debug(sProcDt + ".sProcDt");
			}

			//② Transaction 시작
			sqlMap.startTransaction();

			//③ 요청 데이터 처리, ④ DB 처리, ⑤ 결과 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");

			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("OP_PROCDT", sProcDt);
			requestMap.put("ERRCODE", "");
			requestMap.put("ERRMSG", "");
			
			// 프로시저 호출
			if(log.isDebugEnabled()){
				log.debug("BASUKC09100.execute.callSP_BASUKC09100");
				log.debug("requestMap.toString()"+requestMap.toString());
			}
			
			log.debug("### 처리일자     : ["+sProcDt+ "]");
			log.debug("### 처리자     : ["+PROG_ID.substring(0, 10)+ "]");
			
			sqlMap.queryForObject("BASUKC09100.callSP_BASUKC09100", requestMap);
			
			if(requestMap.get("ERRMSG") != null)  {
				logMng.writeLogFile("ERRCODE:"+requestMap.get("ERRCODE").toString()
						+"/ERRMSG:" + requestMap.get("ERRMSG").toString());
			}
			
			logMng.writeLogFile("------------------------------------------------------------");

			
			
			
			
			/* 임시 - 시작  */
			/* 암호화 처리 부분 : 처리 종료시 이전 으로 원복시켜야함. */
			
			/* 상담 */
/*			
			List resultList = (List) sqlMap.queryForList("BASUKC09100.getConsultMgmtList", "");
			int selectCnt = resultList.size();
			int writeCnt = 0;

			Map msgMap = new HashMap();
			Map updateMap = new HashMap();
			
			String sConNo = "";
			String sIfResNo = "";
			String sIfAccNo = "";
			String sIfTelNo = "";

			logMng.writeLogFile("------------------------------------------------------------");
			logMng.writeLogFile("tcst_consult_mgmt 처리 시작");
			logMng.writeLogFile("처리 대상 : " + selectCnt + " 건");
			
			for (int i = 0; i < selectCnt; i++) {
				msgMap = (Map) resultList.get(i);
				
				sConNo = (String) msgMap.get("CON_MGMT_NO");
				sIfResNo = (String) msgMap.get("RES_NO");
				sIfAccNo = (String) msgMap.get("ACC_NO");
				sIfTelNo = (String) msgMap.get("TEL_NO");
				
				if (!"".equals(sIfResNo)) {
					if (!"".equals(sIfResNo + "")
							&& !"null".equals(sIfResNo + "")
							&& sIfResNo.indexOf("=") < 0) {
						sIfResNo = aesUtils.encrypt(sIfResNo);
					}
				}
				
				if (!"".equals(sIfAccNo)) {
					if (!"".equals(sIfAccNo + "")
							&& !"null".equals(sIfAccNo + "")
							&& sIfAccNo.indexOf("=") < 0) {
						sIfAccNo = aesUtils.encrypt(sIfAccNo);
					}
				}
				
				if (!"".equals(sIfTelNo)) {
					if (!"".equals(sIfTelNo + "")
							&& !"null".equals(sIfTelNo + "")
							&& sIfTelNo.indexOf("=") < 0) {
						sIfTelNo = aesUtils.encrypt(sIfTelNo);
					}
				}
				
				
				updateMap.put("CON_MGMT_NO", sConNo);
				updateMap.put("RES_NO", sIfResNo); // MQ로 들어오는 정상적인 데이터와 듐을 피하기 위해 
				updateMap.put("ACC_NO", sIfAccNo);
				updateMap.put("TEL_NO", sIfTelNo);
				
	        	sqlMap.update("BASUKC09100.updateConsultMgmt", updateMap);
	        	writeCnt = writeCnt + 1;
			}
			logMng.writeLogFile("처리 건수 : " + writeCnt + " 건");
			logMng.writeLogFile("------------------------------------------------------------");
*/
			
			/* 매입 */
/*			
			List resultList1 = (List) sqlMap.queryForList("BASUKC09100.getPrchsMgmtList", "");
			int selectCnt1 = resultList1.size();
			int writeCnt1 = 0;

			Map msgMap1 = new HashMap();
			Map updateMap1 = new HashMap();
			
			String sConNo1 = "";
			String sIfResNo1 = "";
			String sIfAccNo1 = "";
			String sIfTelNo1 = "";

			logMng.writeLogFile("------------------------------------------------------------");
			logMng.writeLogFile("tcst_prchs_mgmt 처리 시작");
			logMng.writeLogFile("처리 대상 : " + selectCnt1 + " 건");
			
			for (int i = 0; i < selectCnt1; i++) {
				msgMap1 = (Map) resultList1.get(i);
				
				sConNo1   = (String) msgMap1.get("PRCHS_MGMT_NO");
				sIfResNo1 = (String) msgMap1.get("RES_NO");
				sIfAccNo1 = (String) msgMap1.get("ACC_NO");
				sIfTelNo1 = (String) msgMap1.get("TEL_NO");
				
				if (!"".equals(sIfResNo1)) {
					if (!"".equals(sIfResNo1 + "")
							&& !"null".equals(sIfResNo1 + "")
							&& sIfResNo1.indexOf("=") < 0) {
						sIfResNo1 = aesUtils.encrypt(sIfResNo1);
					}
				}
				
				if (!"".equals(sIfAccNo1)) {
					if (!"".equals(sIfAccNo1 + "")
							&& !"null".equals(sIfAccNo1 + "")
							&& sIfAccNo1.indexOf("=") < 0) {
						sIfAccNo1 = aesUtils.encrypt(sIfAccNo1);
					}
				}
				
				if (!"".equals(sIfTelNo1)) {
					if (!"".equals(sIfTelNo1 + "")
							&& !"null".equals(sIfTelNo1 + "")
							&& sIfTelNo1.indexOf("=") < 0) {
						sIfTelNo1 = aesUtils.encrypt(sIfTelNo1);
					}
				}
				
				
				updateMap1.put("PRCHS_MGMT_NO", sConNo1);
				updateMap1.put("RES_NO", sIfResNo1); // MQ로 들어오는 정상적인 데이터와 듐을 피하기 위해 
				updateMap1.put("ACC_NO", sIfAccNo1);
				updateMap1.put("TEL_NO", sIfTelNo1);
				
	        	sqlMap.update("BASUKC09100.updatePrchsMgmt", updateMap1);
	        	writeCnt1 = writeCnt1 + 1;
			}			
			logMng.writeLogFile("처리 건수 : " + writeCnt1 + " 건");
			logMng.writeLogFile("------------------------------------------------------------");
*/			

			/* 인터페이스 상담 */
/*			
			List resultList2 = (List) sqlMap.queryForList("BASUKC09100.getIfConsultMgmtList", "");
			int selectCnt2 = resultList2.size();
			int writeCnt2 = 0;

			Map msgMap2 = new HashMap();
			Map updateMap2 = new HashMap();
			
			String sOpDt = "";
			String sOpTm = "";
			String sSeq  = "";
			String sIfResNo2 = "";
			String sIfAccNo2 = "";
			String sIfTelNo2 = "";
			
			logMng.writeLogFile("------------------------------------------------------------");
			logMng.writeLogFile("tbas_ukey_if_consult_mgmt 처리 시작");
			logMng.writeLogFile("처리 대상 : " + selectCnt2 + " 건");
			
			for (int i = 0; i < selectCnt2; i++) {
				msgMap2 = (Map) resultList2.get(i);
				
				sOpDt     = (String) msgMap2.get("OP_DT");
				sOpTm     = (String) msgMap2.get("OP_TM");
				sSeq      =  msgMap2.get("SEQ").toString();
				sIfResNo2 = (String) msgMap2.get("IF_RES_NO");
				sIfAccNo2 = (String) msgMap2.get("IF_ACC_NO");
				sIfTelNo2 = (String) msgMap2.get("IF_TEL_NO");
				
				if (!"".equals(sIfResNo2)) {
					if (!"".equals(sIfResNo2 + "")
							&& !"null".equals(sIfResNo2 + "")
							&& sIfResNo2.indexOf("=") < 0) {
						sIfResNo2 = aesUtils.encrypt(sIfResNo2);
					}
				}
				
				if (!"".equals(sIfAccNo2)) {
					if (!"".equals(sIfAccNo2 + "")
							&& !"null".equals(sIfAccNo2 + "")
							&& sIfAccNo2.indexOf("=") < 0) {
						sIfAccNo2 = aesUtils.encrypt(sIfAccNo2);
					}
				}
				
				if (!"".equals(sIfTelNo2)) {
					if (!"".equals(sIfTelNo2 + "")
							&& !"null".equals(sIfTelNo2 + "")
							&& sIfTelNo2.indexOf("=") < 0) {
						sIfTelNo2 = aesUtils.encrypt(sIfTelNo2);
					}
				}
				
				
				updateMap2.put("OP_DT", sOpDt);
				updateMap2.put("OP_TM", sOpTm);
				updateMap2.put("SEQ"  , sSeq);
				updateMap2.put("IF_RES_NO", sIfResNo2);  
				updateMap2.put("IF_ACC_NO", sIfAccNo2);
				updateMap2.put("IF_TEL_NO", sIfTelNo2);
				
	        	sqlMap.update("BASUKC09100.updateIfConsultMgmt", updateMap2);
	        	writeCnt2 = writeCnt2 + 1;
				
			}			
		
			logMng.writeLogFile("처리 건수 : " + writeCnt2 + " 건");
			logMng.writeLogFile("------------------------------------------------------------");
*/        	
			/* 임시 - 종료  */        	
			
			
			
			
        	
			// 처리 결과 commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}

			//⑥-1 Transaction Commit
			log.debug("#### 처리완료. ####");
			sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("Transaction Exception = [" + e + "]");
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
			}
		} finally {
			//⑥-2 Transaction rollback (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			sqlMap.endTransaction();
		}

		logMng.closeLogFile();

		//⑦ 처리 결과 코드 리턴
		return 0;
	}
}
