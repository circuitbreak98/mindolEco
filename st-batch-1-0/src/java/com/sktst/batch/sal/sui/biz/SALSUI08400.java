package com.sktst.batch.sal.sui.biz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업관리</li>
 * <li>설 명 : 부가상품 가입정보를 취득하기 위해 서비스 관리번호 파일을 생성한다.. </li>
 * <li>작성일 : 2009-03-30 10:43:46</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class SALSUI08400 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSUI08400";
	private static       String fileTime = "";
	private         PrintWriter dataFile;

	/**
	 * U.Key로 부터 I/F된 해지 정보를 기준으로 판매정보에 Update 한다.
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
		
		fileTime = (String)request.get("args1");

		try{

			logMng.openLogFile(PROG_ID);
			logMng.writeLogFile("args1 : " + fileTime);

			// Transaction Start Logging...
			if(log.isDebugEnabled()){
				log.debug( PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();

			// DB를 읽어서 FILE로 down.
			logMng.writeLogFile("------------------------------------------------------------");
			getDBwriteDataFile(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");

			// Transaction Commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			sqlMap.commitTransaction ();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
			}

		} finally {
			//처리 완료. (commit이 안된 경우 rollback)
			if(log.isDebugEnabled()){
				log.debug("SALSUI08400.execute.endTransaction");
			}			
			sqlMap.endTransaction ();

			logMng.closeLogFile();
		}
	
		// 처리 결과 코드 리턴
		return 0;
		}

	/**
	 * 내용 : U.Key로 부터 I/F된 해지 정보 중 미처리건만을 추출하여 판매영업정보에 Update
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void getDBwriteDataFile(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("getDBwriteDataFile method start......");

		String sOpDt          = "";
		String sOpTm          = "";
		String sSeq           = "";
        String sSvcMgmtNum    = "";
        String sEqpMdlCd      = "";
        String sEqpSerNum     = "";
        String sUkeyAgencyCd  = "";
        String sUkeySubCd     = "";
        String sUkeyChannelCd = "";
        String sProcDt        = "";
        String sProcTm        = "";
        String sUserID        = "";
        String sProcPlc       = "";

        String sValueNull     = "";
        String sGnrlSaleNo    = "";
           int sTermCnt       =  0;
           int sProcCnt       =  0;
           int sPlcCnt        =  0;

        Map    resultGnrl;
        Map    mapTermList;
        List   resultList;
		Map<String, Object> requestMap     = new HashMap<String, Object>();
		Map<String, Object> requestMapCall = new HashMap<String, Object>();

		requestMap.put("SVC_MGMT_NUM", sSvcMgmtNum);
		requestMap.put("EQP_MDL_CD",   sEqpMdlCd);
		requestMap.put("EQP_SER_NUM",  sEqpSerNum);
		resultList = (List)sqlMap.queryForList("SALSUI08400.getGeneralTermIF", requestMap);
		int selectCnt = resultList.size();

		for (int i = 0; i < selectCnt; i++) {

			mapTermList   = (Map) resultList.get(i);

			sTermCnt++;

			sOpDt          = (String) mapTermList.get("OP_DT");
			sOpTm          = (String) mapTermList.get("OP_TM");
			sSeq           = (String) mapTermList.get("SEQ");
	        sSvcMgmtNum    = (String) mapTermList.get("SVC_MGMT_NUM");
	        sEqpMdlCd      = (String) mapTermList.get("EQP_MDL_CD");
	        sEqpSerNum     = (String) mapTermList.get("EQP_SER_NUM");
	        sUkeyAgencyCd  = (String) mapTermList.get("UKEY_AGENCY_CD");
	        sUkeySubCd     = (String) mapTermList.get("UKEY_SUB_CD");
	        sUkeyChannelCd = (String) mapTermList.get("UKEY_CHANNEL_CD");
	        sProcDt        = (String) mapTermList.get("PROC_DT");
	        sProcTm        = (String) mapTermList.get("PROC_TM");
	        sUserID        = (String) mapTermList.get("USER_ID");

	        // 판매정보 조회
	        requestMap.put("SVC_MGMT_NUM", sSvcMgmtNum);
	        requestMap.put("EQP_MDL_CD",   sEqpMdlCd);
	        requestMap.put("EQP_SER_NUM",  sEqpSerNum);

            resultGnrl = (Map)sqlMap.queryForObject("SALSUI08400.getGeneralInfo", requestMap);
	     	if (resultGnrl != null)  {
	     		// 일반판매관리번호 Setting
	     		sGnrlSaleNo = (String) resultGnrl.get("GNRL_SALE_NO");

	    		// 해지처리 거래처 정보 취득 - 거래처정보 조회 프로시저 호출
				requestMapCall.put("ov_ErrorCl",    sValueNull);
				requestMapCall.put("ov_ErrorMsg",   sValueNull);
				requestMapCall.put("ov_SalePLC",    sValueNull);
				requestMapCall.put("ov_StlPLC",     sValueNull);
				requestMapCall.put("ov_DisPLC",     sValueNull);
				requestMapCall.put("ov_DealCl1",    sValueNull);
				requestMapCall.put("ov_DealCl2",    sValueNull);
				requestMapCall.put("ov_SaleStopYN", sValueNull);
				requestMapCall.put("ov_PayStopYn",  sValueNull);
				requestMapCall.put("ov_SlCl",       sValueNull);
				requestMapCall.put("ov_SlNetCd",    sValueNull);

				requestMapCall.put("iv_SaleDtm",    sProcDt + sProcTm);
				requestMapCall.put("iv_ProcDtm",    sProcDt + sProcTm);
				requestMapCall.put("iv_AgencyCd",   sUkeyAgencyCd);
				requestMapCall.put("iv_SubCd",      sUkeySubCd);
				requestMapCall.put("iv_ChannelCd",  sUkeyChannelCd);

				sqlMap.queryForObject("SALSUI08400.call_SP_SALDEALINFO", requestMapCall);
				
				if (requestMapCall == null) {
				    logMng.writeLogFile("Term Plc Not Found [" + sUkeyAgencyCd + "/" + sUkeySubCd + "/" + sUkeyChannelCd + "]");
				    sProcPlc = "";
				    sPlcCnt++;
				} else {
					sProcPlc = (String) requestMapCall.get("ov_SalePLC");
				}

				// 영업판매정보에 해지정보 Update
				requestMap.put("GNRL_SALE_NO", sGnrlSaleNo);
				requestMap.put("PROC_PLC",     sProcPlc);
				requestMap.put("PROC_DTM",     sProcDt + sProcTm);
				requestMap.put("USER_ID",      sUserID);

            	sqlMap.update("SALSUI08400.saveGeneralSale", requestMap);

				// 해지전문에 처리되었음을 Update
				requestMap.put("OP_DT", sOpDt);
				requestMap.put("OP_TM", sOpTm);
				requestMap.put("SEQ",   sSeq);

            	sqlMap.update("SALSUI08400.saveGeneralTermIF", requestMap);

    			sProcCnt++;
	    	}

		}

		logMng.writeLogFile("   Term I/F Count : " + sTermCnt);
		logMng.writeLogFile("    Process Count : " + sProcCnt);
		logMng.writeLogFile("    Not Found Plc : " + sPlcCnt);
		logMng.writeLogFile("getDBwriteDataFile method end......");
	}

}