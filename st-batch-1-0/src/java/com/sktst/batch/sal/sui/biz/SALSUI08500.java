package com.sktst.batch.sal.sui.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.text.*;
import java.math.*;


import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : U.Key에서 I/F된 부가상품 가입정보 SAM File을 읽어서 부가서비스판매 IF 자료에 INSERT / UPDATE 한다.</li>
 * <li>작성일 : 2010-06-04 09:00:00</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class SALSUI08500 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSUI08500";
	private static final String USER_ID = "SALSUI0850";
	private static String fileTime = "";
	private static String cNullValue = "";

	private static    int valueNotFound =  -1;

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
		
		fileTime = (String)request.get("args1");

		try {
			
			logMng.openLogFile(PROG_ID);
			logMng.writeLogFile("args1 : " + fileTime);

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
	private void openDataFileAddDB(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("openDataFileAddDB method start......");
		FileReader fr = null;
		BufferedReader br = null;
		
		BufferedWriter oOut = null;

		
		int readCnt     = 0;	// I/F된 sam file read count
		int insertCnt   = 0; 	// 부가서비스 insert count
		int skipNonUse  = 0;    // 사용중이지 않은 부가상품 Skip 건수
		int skipNonSupl = 0;    // 유치된 부가상품이 없어 Skip한 건수

		// Input File 자료처리를 위한 변수 선언
		String fAgencyCd     = "";
		String fSubCd        = "";
		String fSvcMgmtNum   = "";		
		String fMdlCd        = "";
		String fSerNum       = "";
		String fGnrlSaleNo   = "";
		String fScrbDt       = "";
		String fScrbTm       = "";
		String fUserID       = "";
		String fSuplCl       = "";
		String fSuplSvcCd    = "";
		String fSuplDCode    = "";
		String fPosAgency    = "";
		String fSvcPlc       = "";
		String fProcUser     = "";
		String fSuplAgency   = "";
		   int fExistCnt     =  0;


        // QueryForObject를 위한 Map 선언
        Map    resultSale;

		Map<String, Object> requestMap    = new HashMap<String, Object>();

		String sCurdate = "";

		if (fileTime == null || cNullValue.equals(fileTime)) {
			// 인자값으로 입력된 일자가 없을 경우 현재일자 취득
		 	resultSale      = (Map)    sqlMap.queryForObject("SALSUI08500.getToDay", requestMap);
		 	sCurdate = (String) resultSale.get("WORK_DT");
		} else {
			sCurdate = fileTime;
		}
		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append("SKTPSD97." + sCurdate + ".dat")
			.toString();

		logMng.writeLogFile(" U.Key I/F SAM 일자 [" + sCurdate + "] => " + "SKTPSD97." + sCurdate + ".dat ...");
		
		String oPath = new StringBuffer()
		.append(inFilePath + "/")
		.append("SKTPSD97TEMPSET." + sCurdate + ".dat")
		.toString();

		// 작업 처리시간을 줄이기 위해 부가상품 정보를 조회하여 사용중인 부가상품 목록을 생성
		List resultList = (List) sqlMap.queryForList("SALSUI08500.getSuplList", requestMap);
		int selectCnt2 = resultList.size();

		String sPolListStr = " ";
		Map printList = new HashMap();
		for (int J = 0; J < selectCnt2; J++) {
			printList = (Map) resultList.get(J);
			sPolListStr = sPolListStr + (String) printList.get("SUPL_SVC_CD") + " "; 
		}

	 	// 해당 월 자료 삭제
    	requestMap.put("WORK_MONTH", sCurdate.substring(0, 6));
   	    sqlMap.update("SALSUI08500.deleteWorkMonth", requestMap);

		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);


			oOut = new BufferedWriter(new FileWriter(oPath));
			String oTempSet = null;
			
			for (String readLine; (readLine = br.readLine()) != null;) {

				try {

					readCnt++;

					// 전문양식에 맞게 읽은 자료를 자른다.
					fAgencyCd     = readLine.substring(  2,  8).trim();
					fSubCd        = readLine.substring(  8, 12).trim();
					fSvcMgmtNum   = readLine.substring( 12, 22).trim();
					fMdlCd        = readLine.substring( 22, 27).trim();
					fSerNum       = readLine.substring( 27, 42).trim();
					fGnrlSaleNo   = readLine.substring( 42, 53).trim();
					fScrbDt       = readLine.substring( 53, 61).trim();
					fScrbTm       = readLine.substring( 61, 67).trim();
					fUserID       = readLine.substring( 67, 82).trim();
					fSuplCl       = readLine.substring( 82, 83).trim();
					fSuplSvcCd    = readLine.substring( 83, 93).trim();
					fSuplDCode    = readLine.substring( 93, 99).trim();


                    // 부가상품 및 유치일자가 Null인 경우 Skip
					if (cNullValue.equals(fSuplSvcCd) || cNullValue.equals(fScrbDt)) {
		        		skipNonSupl++;
		        		continue;
					}
                    // 사용중인 부가상품인지 확인
					if (sPolListStr.indexOf(fSuplSvcCd) == valueNotFound) {
		        		skipNonUse++;
		        		continue;
					}

					// 개통 대리점 정보 조회
				 	requestMap.put("UKEY_AGENCY_CD", fAgencyCd);
				 	requestMap.put("UKEY_SUB_CD",    fSubCd);
				 	requestMap.put("EFF_DTM",        fScrbDt + fScrbTm);
				 	requestMap.put("SUPL_AGENCY_CD", fSuplDCode);
				 	requestMap.put("U_KEY_ID",       fUserID);
				 	
				 	resultSale = (Map)    sqlMap.queryForObject("SALSUI08500.getAgencyDealInfo", requestMap);

				 	if (resultSale == null) {
				 		fPosAgency  = "Null";
			 		    fSvcPlc  = "Null";
				 		fSuplAgency  = "Null";
			 		    fProcUser  = "Null";
						logMng.writeLogFile(" ALL Not Mached... [" + fAgencyCd + "], [" + fSubCd + "], [" + fSuplDCode + "], [" + fUserID + "]");
				 	} else {
					 	
					 	if (     resultSale.get("DEAL_CO_CD") == null 
						 		  || resultSale.get("DEAL_CO_CD").toString().equals("") ) {
					 		    fPosAgency  = "Null";
								logMng.writeLogFile(" UKEY_AGENCY_CD Not Mached... [" + fAgencyCd + "]");
						 	} else {
						 		fPosAgency    = (String) resultSale.get("DEAL_CO_CD");
						 		
						 	}
					 	if (     resultSale.get("SUB_DEAL_CO_CD") == null 
					 		  || resultSale.get("SUB_DEAL_CO_CD").toString().equals("") ) {
					 		    fSvcPlc  = "Null";
								logMng.writeLogFile(" UKEY_AGENCY_CD / SUB_CD Not Mached... [" + fAgencyCd + "/" + fSubCd + "]");
					 	} else {
							 	fSvcPlc    = (String) resultSale.get("SUB_DEAL_CO_CD");
					 		
					 	}
					 	if (     resultSale.get("SUPL_DEAL_CO_CD") == null 
						 		  || resultSale.get("SUPL_DEAL_CO_CD").toString().equals("") ) {
						 		fSuplAgency  = fSuplDCode;
						 	} else {
						 		fSuplAgency    = (String) resultSale.get("SUPL_DEAL_CO_CD");
						 		
						 	}
					 	if (     resultSale.get("USER_ID") == null 
						 		  || resultSale.get("USER_ID").toString().equals("") ) {
					 		    fProcUser  = "Null";
						 	} else {
						 		fProcUser    = (String) resultSale.get("USER_ID");
						 		
						 	}

				 	}
				 	




	            	requestMap.put("WK_MONTH",       sCurdate.substring(0, 6));
	            	requestMap.put("UKEY_AGENCY_CD", fAgencyCd  );
	            	requestMap.put("UKEY_SUB_CD",    fSubCd     );
	            	requestMap.put("SVC_MGMT_NUM",   fSvcMgmtNum);
	            	requestMap.put("EQP_MDL_CD",     fMdlCd     );
	            	requestMap.put("EQP_SER_NUM",    fSerNum    );
	            	requestMap.put("GNRL_SALE_NO",   fGnrlSaleNo);
	            	requestMap.put("SCRB_DT",        fScrbDt    );
	            	requestMap.put("SCRB_TM",        fScrbTm    );
	            	requestMap.put("UKEY_USER_ID",   fUserID    );
	            	requestMap.put("SUPL_SVC_CL",    fSuplCl    );
	            	requestMap.put("SUPL_SVC_CD",    fSuplSvcCd );
	            	requestMap.put("SCRB_AGENCY_CD", fSuplDCode );
	            	requestMap.put("POS_AGENCY_CD",  fPosAgency);
	            	requestMap.put("SVC_PLC",        fSvcPlc);
	            	requestMap.put("PROC_USER_ID",   fProcUser);
	            	requestMap.put("SUPL_AGENCY_CD", fSuplAgency);
	            	requestMap.put("USER_ID",      USER_ID);				// 처리자ID - SALSUI0850

	            	sqlMap.update("SALSUI08500.addSktpsd97IF", requestMap);
	            	insertCnt++;

	        		/**
	            	oTempSet = new StringBuffer()
	            	.append(inFilePath + "/")
	        		.append("SKTPSD97TEMPSET." + sCurdate + ".dat")
	        		.toString();
	        		oOut.write(oTempSet);
	        		oOut.newLine();
	        		**/
	            	
	            	
				} catch (Exception ex) {
					logMng.writeLogFile("ERRCODE:E ");
					logMng.writeLogFile(ex.getMessage());
					logMng.writeLogFile("Error Rec==>[" + readLine + "]");

					if (log.isDebugEnabled()) {
						log.debug(ex.getMessage());
					}
				}
			
			}
			oOut.close();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				logMng.writeLogFile(e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage());
				}
			}
		}

		logMng.writeLogFile("openDataFileAddDB method end......");
		logMng.writeLogFile(" ");

		logMng.writeLogFile("------------------------------------------------------------");
		logMng.writeLogFile("    File Read Count : " + readCnt);
		logMng.writeLogFile("       Insert Count : " + insertCnt);
		logMng.writeLogFile("         Skip Count : " + skipNonUse);
		logMng.writeLogFile("------------------------------------------------------------");
	}
}