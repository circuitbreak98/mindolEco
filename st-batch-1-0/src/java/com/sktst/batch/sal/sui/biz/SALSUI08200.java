package com.sktst.batch.sal.sui.biz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

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
public class SALSUI08200 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSUI08200";
	private static final String OUT_FILE_ID = "TPSD33";
	private static final String FILE_EXTENSION = ".dat";
	private static final String NULL_VALUE = "";
	private static String fileTime = "";
	private PrintWriter dataFile;

	/**
	 * PS를 통해 가입된 서비스관리번호를 추출한다.
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
				log.debug("SALSUI08200.execute.endTransaction");
			}			
			sqlMap.endTransaction ();

			logMng.closeLogFile();
		}
	
		// 처리 결과 코드 리턴
		return 0;
		}

	/**
	 * 내용 : 부가상품 가입정보를 취득하기 위하여 현재 PS 시스템에 유효한(판매취소되지 않은) 서비스 관리번호를 추출
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
		
		openDataFile();

	    String sIfDt      = "";  
	    BigDecimal        sIfSeq;
	    String sIfChgrg   = "";
	    String sStaDt     = ""; 
	    String sEndDt     = "";
	    String sAgency    = "";
	    String sSuplSvc   = "";


        String pAgencyCd   = "";
        String pSubCd      = "";
        String pSvcStaDt   = "";
        String pSvcMgmtNum = "";
        String pEqpMdlCd   = "";
        String pEqpSerNum  = "";
        String pGnrlSaleNo = "";
        String pSuplSvcCd  = "";
        String pMessage    = "";

        String sCurdate    = "";

	       int selectTot  = 0;

		   int writeCnt   = 0;
		   int selectCnt2 = 0;

		Map<String, Object> requestMap     = new HashMap<String, Object>();
		Map<String, Object> requestMapCond = new HashMap<String, Object>();
		Map<String, Object> requestMapUpd  = new HashMap<String, Object>();
		Map                 condList       = new HashMap();
		Map                 printList      = new HashMap();
		List                resultList2    = null;

		try {
			// Calendar 생성
	        Date             cDate = null;
			Calendar         cal   = Calendar.getInstance(Locale.KOREA);
			SimpleDateFormat sdf   = new SimpleDateFormat("yyyyMMdd");

			// Parameter로 받은 fileTime(YYYYMMDD)을  일자(YYYY-MM-DD)형식으로 변환
			// 		sbMonth : 0 - January, 1 - February, 2 - March, .....
			if (!(fileTime == null) && !(NULL_VALUE.equals(fileTime))) {
				sCurdate = fileTime;
			} else {
				cal.add(Calendar.DATE, -1);                                 // 작업이 새벽에 수행 되므로 일자를 하루 감해서 Working 일자를 Setting 한다.
				cDate    = cal.getTime();
				sCurdate = sdf.format(cDate);
			}


		 	requestMapCond.put("IF_REQ_DT", sCurdate);
			List resultList = (List) sqlMap.queryForList("SALSUI08200.getCondition", requestMapCond);

			int selectCnt = resultList.size();

			logMng.writeLogFile(" Condition Table Select Complete ... " + selectCnt);
			
			for (int i   = 0; i < selectCnt; i++) {
				condList   = (Map) resultList.get(i);

				logMng.writeLogFile(" Working Condition IF_REQ_DT/IF_REQ_CHGRG_ID/SUPL_JOIN_STA_DT/SUPL_JOIN_END_DT/AGENCY_CD/SUPL_PROD_CD ["
						            + sIfDt + "/" + sIfChgrg + "/" + sStaDt + "/" + sEndDt + "/" + sAgency + "/" + sSuplSvc + "]");

				sIfDt    =    (String) condList.get("IF_REQ_DT");
				sIfSeq   = (BigDecimal)condList.get("IF_REQ_SEQ");
				sIfChgrg =    (String) condList.get("IF_REQ_CHGRG_ID");
	        	sStaDt   =    (String) condList.get("SUPL_JOIN_STA_DT");
	        	sEndDt   =    (String) condList.get("SUPL_JOIN_END_DT");
	        	sAgency  =    (String) condList.get("AGENCY_CD");
	        	sSuplSvc =    (String) condList.get("SUPL_PROD_CD");

	        	
				requestMap.put("IF_REQ_DT",   sIfDt);	        	
				requestMap.put("IF_REQ_SEQ",  sIfSeq);	        	
				requestMap.put("SALE_DT_FR",  sStaDt);
				requestMap.put("SALE_DT_TO",  sEndDt);
				requestMap.put("DEAL_CO_CD",  sAgency);
			 	requestMap.put("SUPL_SVC_CD", sSuplSvc);

				logMng.writeLogFile(" SALSUI08200.getTgtSuplSvcList ==> " + sStaDt + "/" + sEndDt + "/" + sAgency + "/" + sSuplSvc);

				resultList2 = (List) sqlMap.queryForList("SALSUI08200.getTgtSuplSvcList", requestMap);
				selectCnt2  = resultList2.size();
				selectTot = selectTot + selectCnt2;

				for (int J = 0; J < selectCnt2; J++) {

					printList = (Map)    resultList2.get(J);
					pMessage  = (String) printList.get("MSG");

					if(writeDataFile(pMessage) == true){
						writeCnt++;
					}

			        pAgencyCd   = pMessage.substring( 2,  8).trim();
			        pSubCd      = pMessage.substring( 8, 12).trim();
			        pSvcStaDt   = pMessage.substring(12, 26).trim();
			        pSvcMgmtNum = pMessage.substring(26, 36).trim();
			        pEqpMdlCd   = pMessage.substring(36, 41).trim();
			        pEqpSerNum  = pMessage.substring(41, 56).trim();
			        pGnrlSaleNo = pMessage.substring(56, 67).trim();
			        pSuplSvcCd  = pMessage.substring(67, 77).trim(); 

					requestMapUpd.put("IF_REQ_DT",      sIfDt);
					requestMapUpd.put("IF_REQ_SEQ",     sIfSeq);
					requestMapUpd.put("UKEY_AGENCY_CD", pAgencyCd);
					requestMapUpd.put("UKEY_SUB_CD",    pSubCd);
					requestMapUpd.put("SVC_STA_DT",     pSvcStaDt);
					requestMapUpd.put("SVC_MGMT_NUM",   pSvcMgmtNum);
					requestMapUpd.put("EQP_MDL_CD",     pEqpMdlCd);
					requestMapUpd.put("EQP_SER_NUM",    pEqpSerNum);
					requestMapUpd.put("GNRL_SALE_NO",   pGnrlSaleNo);
					requestMapUpd.put("SUPL_PROD_CD",   pSuplSvcCd);
					requestMapUpd.put("USER_ID",       "SALSUI0820");

					sqlMap.update("SALSUI08200.addSuplProdRslt", requestMapUpd);
				}

				// SAM File 처리건수를 Update

				requestMapUpd.put("REQ_CNT",         selectCnt2);
				requestMapUpd.put("IF_REQ_DT",       sIfDt);
				requestMapUpd.put("IF_REQ_SEQ",      sIfSeq);
				requestMapUpd.put("IF_REQ_CHGRG_ID", sIfChgrg);

				sqlMap.update("SALSUI08200.saveReqCount", requestMapUpd);
				logMng.writeLogFile(" Process : " + sIfDt + " / " + sIfSeq + " / " + sIfChgrg + " / " + selectCnt2);
			}
	
			closeDataFile();
			logMng.writeLogFile("    Select Count : " + selectTot);
			logMng.writeLogFile("File Write Count : " + writeCnt);
			logMng.writeLogFile("getDBwriteDataFile method end......");
		} catch (Exception e) {
			logMng.writeLogFile("  Error ..." + e.toString());
			return;
		}
	}

	/**
	 * message를 읽어서 File에 기록한다. 
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param message
	 *            File에 기록할 메세지
	 * @return File에 정상적으로 기록될 경우 true를 리턴,
	 *            그렇지 않을 경우 false를 리턴한다.
	 */
	private boolean writeDataFile(String message) {
		try {

			dataFile.println(message);
			dataFile.flush();
		} catch (Exception e) {
			logMng.writeLogFile(e.toString());
			return false;
		}
		return true;
	}

	/**
	 * File로부터 message를 읽어온다. 
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 * 
	 * @return void
	 * 
 	 * @throws IOException
	 */	
	private void openDataFile() throws IOException {
		String dataPath = new StringBuffer()
			.append(outFilePath + "/")
			.append(OUT_FILE_ID + ".")
			.append(fileTime)
			.append(FILE_EXTENSION)
			.toString();
		
		logMng.writeLogFile("Output File : " + dataPath);

		File orgFile = new File(dataPath);
		orgFile.delete();

		FileOutputStream rsTemp = new FileOutputStream(dataPath, true);
		dataFile = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				rsTemp)), true);

	}
	
	/**
	 * File을 닫는다. 
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 * 
	 * @return void
	 * 
 	 * @throws IOException
	 */
	private void closeDataFile() throws IOException {
		dataFile.close();
	}

}