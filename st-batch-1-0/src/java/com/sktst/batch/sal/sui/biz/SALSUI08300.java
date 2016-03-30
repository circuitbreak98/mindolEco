package com.sktst.batch.sal.sui.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.*;
import java.text.*;
import java.math.*;

import org.springframework.jdbc.support.SQLErrorCodes;


import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : 요금수납정보를 U.Key로 부터 I/F받아 영업 업무용 Temp Table에 저장한다.</li>
 * <li>작성일 : 2009-03-30 09:00:00</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class SALSUI08300 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSUI08300";
	private static       String Null_Value = "";

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
			log.debug("[DEBUG]" + PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();
		
		String sFileName = "";
		if( request.size() > 1 ) {
			sFileName = (String)request.get("args1");
		}

		try {
			
			logMng.openLogFile(PROG_ID);
			logMng.writeLogFile("args1 : " + sFileName);

			if (log.isDebugEnabled()) {
				log.debug("[DEBUG]" + PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();
			
			// FILE을 읽어서 DB insert.
			openDataFileAddDB(sqlMap, sFileName);
			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			if (log.isDebugEnabled()) {
				log.debug("[DEBUG]" + PROG_ID + ".execute.commitTransaction");
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
				log.debug("[DEBUG]" + PROG_ID + ".execute.endTransaction");
			}
			sqlMap.endTransaction();
			logMng.closeLogFile();
		}

		return 0;
	}

	/**
	 * 파일을 읽어서 DB에 기록한다.
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void openDataFileAddDB(SqlMapClient sqlMap, String fFileName) throws Exception {
		logMng.writeLogFile("openDataFileAddDB method start......");
		FileReader     fr = null;
		BufferedReader br = null;

		   int readCnt           =   0;
		   int skipSvcCnt        =   0;
		   int skipCardCnt       =   0;
		   int skipInvalid       =   0;
		   int skipPayPlc        =   0;
		   int skipUserId        =   0;
		   int skipPayCl         =   0;
		   int skipScrb          =   0;
		   int insertCnt         =   0;
		   int insertScrb        =   0;
		   int insertPrprc       =   0;
		   int insertCharge      =   0;

		   int sPlcCnt           =  0;   		// 수납처 갯수 Check용
		String sValueYes         =  "Y";		// 수납중지여부 Check
		String sOPDT             =  "";
		String sOPTM             =  "";
		   int sSeq              =  0;
		String sErrCode          =  "";
		String sErrMsg           =  "";
	    String sSlNetCd          =  "";

		String sValueC           =  "C";		// 서비스코드 비교하기위한 상수 'C' - 이동전화
		String sValueW           =  "W";		// 서비스코드 비교하기위한 상수 'W' - Wibro
		String sValueD           =  "D";		// 서비스코드 비교하기위한 상수 'W' - TU DMB

		String sValueF           =  "F";		// F Channel Initialize용 상수
		String sValueChn         =  "";  		// F Channel Initialize용 변수
		String sValueScrb        =  "CRGAP00";	// 초기 I/F에 이동전화가입비 항목이 전송됨을 대비
		String sValueScrb3G      =  "CRG3P00";	// 초기 I/F에 3G즉납가입비 항목이 전송됨을 대비
		String sValueMnp         =  "CRTSC00";	// 초기 I/F에 번호이동수수료 항목이 전송됨을 대비
		String sScrbFee          =  "28";       // 가입비 분납전환 창구코드
		String sScrbAdj          =  "A2";       // 가입비 조정     창구코드
		String sValuePay         =  "1";		// 수납구분 - 수납
		String sValueRefund      =  "5";		//          환불
		String sValueCash        =  "01";      	// 결제조건 - 현금
		String sValueCard        =  "02";		//          카드
		String sValueMerBond     =  "07";		//          상품권
		String sValueDaeche      =  "52";       //          대체
		String sValueSub         =  "0000";	 	// 서브점 - 개통처
		String sValueSS          =  "SS";       // 거래처 정보 조회 성공
		String sValueNull        =  "";

		String sRecTypScrb       =  "ZZ";       // 가입비 분납전환 전문번호 임의 부여
        String sRecTypPrprc      =  "ZY";		// 예수금 카드완납 전문번호 임의 부여
        String sRecTypPay        =  "50";		// SKT요금수납

           int pPlus             =   0;
         float pCashPayAmt       =   0;
         float pPrxpayAmt        =   0;
         float pMerBondPayAmt    =   0;
        String pPayStopYn        =  "";

        String sIfRecTyp         =  "";
        String sUkeyAgencyCd     =  "";
        String sUkeySubCd        =  "";
        String sUkeyChannelCd    =  "";
        String sUkeyChannelSubCd =  "";
        String sPayPlc           =  "";
        String sPayReqPlc        =  "";
        String sStlPlc           =  "";
        String sSvcMgmtNum       =  "";
        String sProcDt           =  "";
        String sProcTm           =  "";
        String sPayCl            =  "";
        String sSettlWay         =  "";
        String sWcktDealTyp      =  "";
        String sCustNm           =  "";
        String sSvcNum           =  "";
        String sProcId           =  "";
        String sProcChgrgId      =  "";
        String sSvcCd            =  "";
        String sRevItmCd         =  "";
        String sRevItmNm         =  "";
         float sPayObjAmt        =   0;
        String sSlCl             =  "";
        String sPsApndYn         = "N";
        String sErrorCl          =  "";
        String sRmks             =  "";
        String sDelYn            = "N";
           int sUpdCnt           =   1;
        String sInsUserId        =  "SALSUI0830";

        // 작업을 위한 임시변수 선언
        StringBuffer sbText      = new StringBuffer();

        // QueryForObject를 위한 Map 선언
        Map    resultSam; 
        Map    resultUser; 

		Map<String, Object> requestSam     = new HashMap<String, Object>();
		Map<String, Object> requestUser    = new HashMap<String, Object>();
		Map<String, Object> requestPay     = new HashMap<String, Object>();
		Map<String, Object> requestMapCall = new HashMap<String, Object>();

		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append(fFileName)
			.toString();

		// I/F된 SAM File이 이미 처리되었는지 확인한다.
    	requestSam.put("IF_SAM_FILE",  fFileName);

    	resultSam = (Map)sqlMap.queryForObject("SALSUI08300.getChargeChk", requestSam);
    	if (resultSam == null)  {
    		sSlCl    = "";
    	} else {
    		logMng.writeLogFile("이미 처리된 SAM FIle 입니다. : " + fFileName);
    		logMng.writeLogFile("처리일자  : " + (String)  resultSam.get("PROC_DT"));
    		logMng.writeLogFile("처리시간  : " + (String)  resultSam.get("PROC_TM"));

    		return;
    	}

		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);

			// I/F 처리 일시를 Setting하기 위함...
			br = new BufferedReader(fr);
			Calendar cl = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat stf = new SimpleDateFormat("HHmmss");

			Date   cDate    = cl.getTime();
			String sCurdate = sdf.format(cDate);
			String sCurTime = stf.format(cDate).substring(0, 6);

			sOPDT = sCurdate;
			sOPTM = sCurTime;

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {
					readCnt++;

					// 전문양식에 맞게 SAM File을 자른다.
			        sIfRecTyp         =  readLine.substring(   0,   2).trim(); 
			        sUkeyAgencyCd     =  readLine.substring(   2,   8).trim();
			        sUkeySubCd        =  readLine.substring(   8,  12).trim();
			        sSvcMgmtNum       =  readLine.substring(  12,  22).trim();
			        sProcDt           =  readLine.substring(  22,  30).trim();
			        sProcTm           =  readLine.substring(  30,  36).trim();		// 2010.01.22 Time이 8Byte로 변경됨
			        sUkeyChannelCd    =  readLine.substring(  38,  44).trim();
			        sUkeyChannelSubCd =  readLine.substring(  44,  48).trim();
			        sPayCl            =  readLine.substring(  48,  49).trim();
			        sSettlWay         =  readLine.substring(  49,  51).trim();
			        sWcktDealTyp      =  readLine.substring(  51,  53).trim();
			        sPayObjAmt        =  Float.parseFloat(readLine.substring(  53,  64).trim());

			        sCustNm           =  getSubstrByte(readLine,  64, 50);
			        sSvcNum           =  getSubstrByte(readLine, 114, 20);
			        sProcId           =  getSubstrByte(readLine, 134, 15);
			        sSvcCd            =  getSubstrByte(readLine, 149,  1);
			        sRevItmNm         =  getSubstrByte(readLine, 150, 50);
			        sRevItmCd         =  getSubstrByte(readLine, 200,  7);
			        sRmks             =  "";
			        sErrorCl          =  "";
				    sPsApndYn         =  "N";
				    sSeq++;

		    		sbText = new StringBuffer(""); 

			        // 'F' Channel은 Channel Code를 Null로 Initialize - 사용자 요구사항. 2009.05.31
				    sValueChn = readLine.substring(  38,  39).trim().trim();;
			        if (sValueF.equals(sValueChn)) {
					    logMng.writeLogFile("F Code Initialize [" + sUkeyChannelCd  + "]");
			        	sUkeyChannelCd = "";
			        }

			        // 채널코드에 D Code 값이 I/F된 경우 Initialize - 사용자 요구사항. 2010.01.25
				    if (sUkeyAgencyCd.equals(sUkeyChannelCd) && sUkeySubCd.equals(sUkeyChannelSubCd)) {
					    logMng.writeLogFile("P Code vs D Code   Initialize [" + sUkeyChannelCd  + "]");
					    sUkeyChannelCd    = "";
					    sUkeyChannelSubCd = "";
			        }

			        // 결제코드가 대체(52)인 경우 반영제외 처리 - 사용자 요구사항. 2010.01.25
				    if (sValueDaeche.equals(sSettlWay)) {
					    logMng.writeLogFile("Settl_Way '52'  반영제외 Process [" + sUkeyChannelCd  + "]");
					    sPsApndYn         =  "Z";
			        }

		    		if (sValueCard.equals(sSettlWay)){

			    		// 결제수단이 신용카드인 경우 예수금 카드완납만 처리..
		    			// 이외의 건은 Skip
		                if (sValueScrb.equals(sRevItmCd) || sValueScrb3G.equals(sRevItmCd) || sValueMnp.equals(sRevItmCd)) {


							// 거래처정보 조회 프로시저 호출
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
							requestMapCall.put("iv_ChannelCd",  sValueNull);

							sqlMap.queryForObject("SALSUI08300.call_SP_SALDEALINFO", requestMapCall);
							
							if (requestMapCall == null) {
							    logMng.writeLogFile("PAY_PLC     Procedure Error (Card) " + readLine);
							}
							sPayPlc   = (String) requestMapCall.get("ov_SalePLC");

				        	// 수납자 정보를 조회한다. =======================================================================
				        	requestUser.put("U_KEY_ID",  sProcId);
						    resultUser = (Map)sqlMap.queryForObject("SALSUI08300.getUserCount", requestUser);

				        	sPlcCnt = ((BigDecimal) resultUser.get("USER_CNT")).intValue();
				        	
					    	if (sPlcCnt == 0)  {
						    	sProcChgrgId  = "";
					    	} else if (sPlcCnt > 1) {
						    	sProcChgrgId  = "";
					    	} else {

							    resultUser = (Map)sqlMap.queryForObject("SALSUI08300.getUserInfo", requestUser);
							    sProcChgrgId  = (String)  resultUser.get("USER_ID");
					    	}

							requestPay.put("OP_DT",               sOPDT);
							requestPay.put("OP_TM",               sOPTM);
							requestPay.put("SEQ",                 sSeq);
							requestPay.put("IF_REC_TYP",          sRecTypPrprc);
							requestPay.put("UKEY_AGENCY_CD",      sUkeyAgencyCd);
							requestPay.put("UKEY_SUB_CD",         sUkeySubCd);
							requestPay.put("UKEY_CHANNEL_CD",     sUkeyChannelCd);
							requestPay.put("UKEY_CHANNEL_SUB_CD", sUkeyChannelSubCd);
							requestPay.put("SALE_CHG_PLC",        sPayPlc);
							requestPay.put("PROC_DT",             sProcDt);
							requestPay.put("PROC_TM",             sProcTm);
							requestPay.put("CUST_NM",             sCustNm);
							requestPay.put("SVC_MGMT_NUM",        sSvcMgmtNum);
							requestPay.put("SVC_NUM",             sSvcNum);
							requestPay.put("PROC_ID",             sProcId);
							requestPay.put("PROC_CHGRG_ID",       sProcChgrgId);
							requestPay.put("SVC_CD",              sSvcCd);
							requestPay.put("SL_CL",               sSlCl);
							requestPay.put("GNRL_SALE_NO",        "");
							requestPay.put("PS_APND_YN",          sPsApndYn);
							requestPay.put("ERROR_CL",            sErrorCl);
							requestPay.put("RMKS",                sRmks);
							requestPay.put("DEL_YN",              sDelYn);
							requestPay.put("UPD_CNT",             sUpdCnt);
							requestPay.put("INS_USER_ID",         sInsUserId);
							requestPay.put("INS_DTM",             sOPDT + sOPTM);

							sqlMap.insert("SALSUI08300.addScrbfeeChgIf", requestPay);

							insertPrprc++;

		                } else {

			    			skipCardCnt++;

		                }

		    		} else {

				        // SVC_CD가 'C', 'W', 'D'가 아닌 경우 제외한다. - 2009.08.24
				        if (sSvcCd.equals(sValueC) || sSvcCd.equals(sValueW) || sSvcCd.equals(sValueD)) {

				        	// 수납처 정보를 조회한다. =======================================================================


			        		//개통처(UKEY_SUB_CD = '0000')에서는 수납할 수 없음
			        		if (sUkeySubCd.equals(sValueSub) && (sUkeyChannelCd == null || sUkeyChannelCd.equals(""))) {
					    		sErrorCl = "01";
					    		sbText.append("개통처["+ sUkeyAgencyCd + "/" + sUkeySubCd + "]는 수납의뢰를 할 수 없습니다.\n");
			        		}

							// 거래처정보 조회 프로시저 호출
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
							requestMapCall.put("iv_ChannelCd",  sValueNull);

							sqlMap.queryForObject("SALSUI08300.call_SP_SALDEALINFO", requestMapCall);
							
							if (requestMapCall == null) {
							    logMng.writeLogFile("PAY_PLC     Procedure Error" + readLine);
				    			sErrorCl = "01";
					    		sbText.append("SP_SALDEALINFO Error");

					    		skipPayPlc++;
							}

		        			//수납처
							sErrCode  = (String) requestMapCall.get("ov_ErrorCl");
							sErrMsg   = (String) requestMapCall.get("ov_ErrorMsg");
							sPayPlc   = (String) requestMapCall.get("ov_SalePLC");

							if (!sValueSS.equals(sErrCode)) {
					    		sPayPlc  = "";
					    		if (sErrCode == null) {
					    			sErrorCl = "01";
						    		sbText.append("SP_SALDEALINFO Error");
					    		} else {
					    			sErrorCl = sErrCode;
						    		sbText.append(sErrMsg);
					    		}

					    		skipPayPlc++;
							}

				        	// 수납의뢰처 정보를 조회한다. =======================================================================
							// 거래처정보 조회 프로시저 호출
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

							sqlMap.queryForObject("SALSUI08300.call_SP_SALDEALINFO", requestMapCall);

							if (requestMapCall == null) {
							    logMng.writeLogFile("PAY_REQ_PLC Procedure Error" + readLine);
				    			sErrorCl = "01";
					    		sbText.append("SP_SALDEALINFO Error");

					    		skipPayPlc++;
							}

		        			//수납의뢰처
							sErrCode    = (String) requestMapCall.get("ov_ErrorCl");
							sErrMsg     = (String) requestMapCall.get("ov_ErrorMsg");
							sPayReqPlc  = (String) requestMapCall.get("ov_SalePLC");
				    		sStlPlc     = (String) requestMapCall.get("ov_StlPLC");
				    		sSlCl       = (String) requestMapCall.get("ov_SlCl");
				    		pPayStopYn  = (String) requestMapCall.get("ov_PayStopYn");

				    		// 정책코드에 의해 수납의뢰처가 Null인 경우
				    		if (Null_Value.equals(sPayReqPlc)) {
					    		sErrorCl    = "01";
					    		sbText.append("정책코드가 I/F 되었습니다. 수납의뢰처를 확인하세요.\n");
					    		skipPayPlc++;				    			
				    		}

							if (sValueSS.equals(sErrCode)) {

								// 수납중지된 거래인 경우 오류 처리
					    		if (sValueYes.equals(pPayStopYn)) {
						    		sErrorCl    = "01";
						    		sbText.append("수납중지된 수납의뢰처["+ sUkeyAgencyCd + "/" + sUkeySubCd + "] 입니다..\n");
						    		skipPayPlc++;
					    		}
							} else {
								sPayReqPlc  = "";
					    		sStlPlc     = "";
					    		sSlCl       = "";
					    		if (sErrCode == null) {
					    			sErrorCl = "01";
						    		sbText.append("SP_SALDEALINFO Error");
					    		} else {
					    			sErrorCl = sErrCode;
						    		sbText.append(sErrMsg);
					    		}

					    		skipPayPlc++;
							}

				        	// 수납처 정보를 조회  끝 . =======================================================================

				        	// 수납자 정보를 조회한다. =======================================================================
				        	requestUser.put("U_KEY_ID",  sProcId);

						    resultUser = (Map)sqlMap.queryForObject("SALSUI08300.getUserCount", requestUser);

				        	sPlcCnt = ((BigDecimal) resultUser.get("USER_CNT")).intValue();
				        	
					    	if (sPlcCnt == 0)  {
						    	sErrorCl      = "02";
						    	sProcChgrgId  = "";
						    	sbText.append("수납담당자["+ sProcId + "] 정보가 없습니다.\n");
					    		skipUserId++;
					    	} else if (sPlcCnt > 1) {
						    	sErrorCl      = "02";
						    	sProcChgrgId  = "";
						    	sbText.append("수납담당자["+ sProcId + "]가 2명 이상 정의되어 있습니다.\n");
					    		skipUserId++;
					    	} else {

							    resultUser = (Map)sqlMap.queryForObject("SALSUI08300.getUserInfo", requestUser);
							    if (resultUser == null)  {
							    	sErrorCl      = "02";
							    	sProcChgrgId  = "";
							    	sbText.append("수납담당자["+ sProcId + "] 정보가 없습니다.\n");
						    		skipUserId++;
							    } else {
							    	sProcChgrgId  = (String)  resultUser.get("USER_ID");
							    }
					    		
					    	}

					        // 수납자 정보를 조회  끝 . =======================================================================

						    // 결제구분이 '1'(수납), '5'(환불)인 경우 Pass 아니면 Fail
						    if (!sPayCl.equals(sValuePay) && !sPayCl.equals(sValueRefund)){
						    	sErrorCl      = "03";
						    	sbText.append("정의되지 않은 수납구분["+ sPayCl + "]입니다.\n");
					    		skipPayCl++;
						    }

						    // 3G즉납가입비와 이동전화 가입비, 번호이동수수료 Check
						    if (sRevItmCd.equals(sValueScrb) || sRevItmCd.equals(sValueScrb3G) || sRevItmCd.equals(sValueMnp)){

							    // 3G즉납가입비와 이동전화 가입비 이지만  창구거래유형이 'A2'(가입비조정)이고 수납구분이 '5'(환불)인 경우 복지할인 - 가입비조정 항목으로 수납처리
						    	// 위의 경우가 아니면 Skip - 반영제외
							    if ((sRevItmCd.equals(sValueScrb) || sRevItmCd.equals(sValueScrb3G)) && sWcktDealTyp.equals(sScrbAdj) && sPayCl.equals(sValueRefund)){
							    	sbText.append("복지할인 예수금 항목 입니다.\n");
							    } else {

							    	sbText.append("예수금 항목이므로 비대상 처리 됩니다.\n");
							    	sErrorCl  = "99";
							    	sPsApndYn = "Z";
						    		skipScrb++;
							    }
						    }

				    		sRmks  =  sbText.toString();
					    	if (sErrorCl == null || sErrorCl.equals("")){
							    // 결제구분이 '01' 현금, '07' 상품권인 경우만 SKT수납대행 Table에 바로 반영 함
							    if (sSettlWay.equals(sValueCash) || sSettlWay.equals(sValueMerBond)){

						    		// 환불인경우 금액(-) 처리
						    		if (sPayCl.equals(sValuePay)){
						    			pPlus = 1;
						    		} else{
						    			pPlus = -1;
						    		}

						    		//  현금
						    		if (sSettlWay.equals(sValueCash)){

						    			pCashPayAmt    = sPayObjAmt * pPlus;
						    			pPrxpayAmt     = 0;
						    			pMerBondPayAmt = 0;

						    		}

						    		// 상품권
						    		if (sSettlWay.equals(sValueMerBond)){

						    			pCashPayAmt    = 0;
						    			pPrxpayAmt     = 0;
						    			pMerBondPayAmt = sPayObjAmt * pPlus;

						    		}

									requestPay.put("PAY_DTM",              sProcDt + sProcTm);
									requestPay.put("PAY_PLC",              sPayPlc);
									requestPay.put("SVC_MGMT_NUM",         sSvcMgmtNum);
									requestPay.put("PAY_CHGRG_ID",         sProcChgrgId);
									requestPay.put("CUST_NM",              sCustNm);
									requestPay.put("SVC_NUM",              sSvcNum);
									requestPay.put("PAY_CL",               sPayCl);
									requestPay.put("PAY_OBJ_AMT",          sPayObjAmt * pPlus);
									requestPay.put("STL_PLC",              sStlPlc);
									requestPay.put("PAY_REQ_PLC",          sPayReqPlc);
									requestPay.put("SL_CL",                sSlCl);
									requestPay.put("SETTL_WAY",            sSettlWay);
									requestPay.put("REV_ITM_CD",           sRevItmCd);
									requestPay.put("REV_ITM_NM",           sRevItmNm);
									requestPay.put("CASH_PAY_AMT",         pCashPayAmt);
									requestPay.put("PRXPAY_AMT",           pPrxpayAmt);
									requestPay.put("MER_BOND_PAY_AMT",     pMerBondPayAmt);
									requestPay.put("INS_USER_ID",          sInsUserId);
									requestPay.put("INS_DTM",              sOPDT + sOPTM);

									sqlMap.insert("SALSUI08300.addSktCharge", requestPay);
						    		sPsApndYn = "Y";
						    		insertCharge++;

							    } else {
						    		sbText.append("처리대상 결제조건["+ sSettlWay + "]이 아닙니다.\n");
						    		skipInvalid++;
						    	}
					    	}

					    	// 창구거래유형이 '28'(분납전환)인 경우 가입비분납전환_UKEY_IF Table 에 INSERT		
					    	if (sWcktDealTyp.equals(sScrbFee)) {

								requestPay.put("OP_DT",               sOPDT);
								requestPay.put("OP_TM",               sOPTM);
								requestPay.put("SEQ",                 sSeq);
								requestPay.put("IF_REC_TYP",          sRecTypScrb);
								requestPay.put("UKEY_AGENCY_CD",      sUkeyAgencyCd);
								requestPay.put("UKEY_SUB_CD",         sUkeySubCd);
								requestPay.put("UKEY_CHANNEL_CD",     sUkeyChannelCd);
								requestPay.put("UKEY_CHANNEL_SUB_CD", sUkeyChannelSubCd);
								requestPay.put("SALE_CHG_PLC",        sPayReqPlc);
								requestPay.put("PROC_DT",             sProcDt);
								requestPay.put("PROC_TM",             sProcTm);
								requestPay.put("CUST_NM",             sCustNm);
								requestPay.put("SVC_MGMT_NUM",        sSvcMgmtNum);
								requestPay.put("SVC_NUM",             sSvcNum);
								requestPay.put("PROC_ID",             sProcId);
								requestPay.put("PROC_CHGRG_ID",       sProcChgrgId);
								requestPay.put("SVC_CD",              sSvcCd);
								requestPay.put("SL_CL",               sSlCl);
								requestPay.put("GNRL_SALE_NO",        "");
								requestPay.put("PS_APND_YN",          sPsApndYn);
								requestPay.put("ERROR_CL",            sErrorCl);
								requestPay.put("RMKS",                sRmks);
								requestPay.put("DEL_YN",              sDelYn);
								requestPay.put("UPD_CNT",             sUpdCnt);
								requestPay.put("INS_USER_ID",         sInsUserId);
								requestPay.put("INS_DTM",             sOPDT + sOPTM);

								sqlMap.insert("SALSUI08300.addScrbfeeChgIf", requestPay);

								insertScrb++;
					    	}

							requestPay.put("OP_DT",               sOPDT);
							requestPay.put("OP_TM",               sOPTM);
							requestPay.put("SEQ",                 sSeq);
							requestPay.put("IF_REC_TYP",          sIfRecTyp);
							requestPay.put("PS_DATA_TYP",         sRecTypPay);
							requestPay.put("UKEY_AGENCY_CD",      sUkeyAgencyCd);
							requestPay.put("UKEY_SUB_CD",         sUkeySubCd);
							requestPay.put("UKEY_CHANNEL_CD",     sUkeyChannelCd);
							requestPay.put("UKEY_CHANNEL_SUB_CD", sUkeyChannelSubCd);
							requestPay.put("PAY_PLC",             sPayPlc);
							requestPay.put("PAY_REQ_PLC",         sPayReqPlc);
							requestPay.put("SVC_MGMT_NUM",        sSvcMgmtNum);
							requestPay.put("PROC_DT",             sProcDt);
							requestPay.put("PROC_TM",             sProcTm);   
							requestPay.put("PAY_CL",              sPayCl); 
							requestPay.put("SETTL_WAY",           sSettlWay);
							requestPay.put("WCKT_DEAL_TYP",       sWcktDealTyp);
							requestPay.put("CUST_NM",             sCustNm);
							requestPay.put("SVC_NUM",             sSvcNum);
							requestPay.put("PROC_ID",             sProcId);
							requestPay.put("PROC_CHGRG_ID",       sProcChgrgId);
							requestPay.put("SVC_CD",              sSvcCd);
							requestPay.put("REV_ITM_CD",          sRevItmCd);
							requestPay.put("REV_ITM_NM",          sRevItmNm);
							requestPay.put("PAY_OBJ_AMT",         sPayObjAmt);
							requestPay.put("SL_CL",               sSlCl);
							requestPay.put("SL_NET_CD",           sSlNetCd);
							requestPay.put("PS_APND_YN",          sPsApndYn);
							requestPay.put("ERROR_CL",            sErrorCl);
							requestPay.put("RMKS",                sRmks);
							requestPay.put("DEL_YN",              sDelYn);
							requestPay.put("UPD_CNT",             sUpdCnt);
							requestPay.put("INS_USER_ID",         sInsUserId);
							requestPay.put("INS_DTM",             sOPDT + sOPTM);

							sqlMap.insert("SALSUI08300.addSktChargeIf", requestPay);

							insertCnt++;

				        } else {

				        	skipSvcCnt++;

				        }
		    		}
		    		//-------------------//
				} catch (Exception ex) {
					logMng.writeLogFile(ex.getMessage());
					logMng.writeLogFile("Error Rec==>[" + readLine + "]");
					if (log.isDebugEnabled()) {
						log.debug(ex.getMessage());
					}
				}
			}
		} catch (Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("openDataFileAddDB Exception : " + e.getMessage());
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();

				requestSam.put("IF_SAM_FILE",       fFileName);
				requestSam.put("PROC_DT",           sOPDT);
				requestSam.put("PROC_TM",           sOPTM);
				requestSam.put("RECORD_CNT",        readCnt);
				requestSam.put("IF_INSERT_CNT",     insertCnt);
				requestSam.put("CHARGE_INSERT_CNT", insertCharge);
				requestSam.put("SCRB_INSERT_CNT",   insertScrb);
				requestSam.put("SVC_SKIP_CNT",      skipSvcCnt);
				requestSam.put("CARD_SKIP_CNT",     skipCardCnt);
				requestSam.put("INVALID_SKIP_CNT",  skipInvalid);
				requestSam.put("PRPRC_SKIP_CNT",    skipScrb);
				requestSam.put("PAY_PLC_SKIP_CNT",  skipPayPlc);
				requestSam.put("USER_ID_SKIP_CNT",  skipUserId);   
				requestSam.put("PAY_CL_SKIP_CNT",   skipPayCl); 
				requestSam.put("INS_USER_ID",       sInsUserId);
				requestSam.put("INS_DTM",           sOPDT + sOPTM);

				sqlMap.insert("SALSUI08300.addChargeChk", requestSam);

			} catch (Exception e) {
				logMng.writeLogFile(e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage());
				}
			}
		}


		logMng.writeLogFile("------------------------------------------------------------");
		logMng.writeLogFile("  File Read Count : " + readCnt);
		logMng.writeLogFile(" Insert I/F Count : " + insertCnt);
		logMng.writeLogFile("Insert Main Count : " + insertCharge);
		logMng.writeLogFile("   Insert Scrbfee : " + insertScrb);
		logMng.writeLogFile("Insert Card Prprc : " + insertPrprc);
		logMng.writeLogFile("     SVC_CD  Skip : " + skipSvcCnt);
		logMng.writeLogFile("       Card  Skip : " + skipCardCnt);
		logMng.writeLogFile("    Invalid  Skip : " + skipInvalid);
		logMng.writeLogFile("     즉납건  Skip : " + skipScrb);
		logMng.writeLogFile("     PayPlc Error : " + skipPayPlc);
		logMng.writeLogFile("     UserId Error : " + skipUserId);
		logMng.writeLogFile("      PayCl Error : " + skipPayCl);
		logMng.writeLogFile("------------------------------------------------------------");
		logMng.writeLogFile("openDataFileAddDB method end......");
	}

	/**
	 * 한글이 포함된 문자열의 byte 단위 substring을 수행 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param str        String 객체,  분할 대상이 되는 문자열
	 *        startPos   Integer 객체, 문자 추출 시작 위치
	 *        getLength  Integer 객체, 문자 추출 Length
	 * @return String
	 * 
	 * @throws Exception
	 */
	public static String getSubstrByte(String str, int startPos, int getLength) {
		  if (str==null) return ""; 

		  String returnStr = new String(str.getBytes(), startPos, getLength);

		  return returnStr.trim();
	}

}