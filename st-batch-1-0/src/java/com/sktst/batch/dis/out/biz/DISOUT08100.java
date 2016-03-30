package com.sktst.batch.dis.out.biz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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
 * <li>업무 그룹명 : 프로젝트/SKTST</li>
 * <li>설 명 : 재고목록을 전송한다.. </li>
 * <li>작성일 : 2012-06-20 10:43:46</li>
 * </ul>
 *
 * @author 전미량
 */

public class DISOUT08100 extends AbsBatchJobBiz {
	 
	private static final String PROG_ID = "DISOUT08100";
	private static final String OUT_FILE_ID_T = "SKGKYX1";
	private static final String OUT_FILE_ID_P = "SKGKYX2";
	private static final String OUT_FILE_ID_U = "SKGKYX3";
	
	private static final String OUT_FILE_ID_CT = "SKGKYX4";
	private static final String OUT_FILE_ID_CP = "SKGKYX5";
	private static final String OUT_FILE_ID_CU = "SKGKYX6";
	
	private static final String FILE_EXTENSION = ".dat";
	private static final String NULL_VALUE = "";
	private static String fileTime = "";
	private PrintWriter dataFileT;
	private PrintWriter dataFileP;
	private PrintWriter dataFileU;
	private PrintWriter dataFileCT;
	private PrintWriter dataFileCP;
	private PrintWriter dataFileCU;
	/**
	 * 
	 *
	 * @author 전미량
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
			
			updateTrmsYn(sqlMap); // 입출고 이력이 당일인 단말기 TRMS_YN = 'Y'
			
			/**
			 * 재고 판매
			 */
			
			// DB를 읽어서 FILE로 down. Tkey
			logMng.writeLogFile("getDBwriteDataFileT   --------------------------------------");
			getDBwriteDataFileT(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");
            // DB를 읽어서 FILE로 down. Tkey +
			logMng.writeLogFile("getDBwriteDataFileP   --------------------------------------");
			getDBwriteDataFileP(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");
			// DB를 읽어서 FILE로 down. Ukey 
			logMng.writeLogFile("getDBwriteDataFileU   --------------------------------------");
			getDBwriteDataFileU(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");
			
			updateSaleTrmsDt(sqlMap); // 판매 단말기 TRMS_DT UPDATE
			
			/**
			 * 재고 판매 취소
			 */
			
			logMng.writeLogFile("getDBwriteDataFileT   --------------------------------------");
			getDBwriteDataFileForCnclT(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");
            // DB를 읽어서 FILE로 down. Tkey +
			logMng.writeLogFile("getDBwriteDataFileP   --------------------------------------");
			getDBwriteDataFileForCnclP(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");
			// DB를 읽어서 FILE로 down. Ukey 
			logMng.writeLogFile("getDBwriteDataFileU   --------------------------------------");
			getDBwriteDataFileForCnclU(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");
			
			updateSaleCnclTrmsDt(sqlMap); // 판매취소 단말기 TRMS_DT UPDATE
			updateXTrmsYn(sqlMap); // 전송일자 안들어간 대상단말기 TRMS_YN = 'X'
			
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
				log.debug(PROG_ID + ".execute.endTransaction");
			}			
			sqlMap.endTransaction ();

			logMng.closeLogFile();
		}
	
		// 처리 결과 코드 리턴
		return 0;
	}
	
	
	
	/**
	 * 내용 : I/F 대상의 전송여부 UPDATE
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	
	private void updateTrmsYn(SqlMapClient sqlMap) throws Exception {
		
		Map query      = new HashMap();
		sqlMap.update("DISOUT08100.updateTrmsYn", query);
		
		return;
	}
	
	/**
	 * 내용 : 판매 대상 전송일자 UPDATE
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	
	private void updateSaleTrmsDt(SqlMapClient sqlMap) throws Exception {
		
		Map query      = new HashMap();
		sqlMap.update("DISOUT08100.updateSaleTrmsDt", query);
		
		return;
	}
	
	
	/**
	 * 내용 : 판매 취소 대상 전송일자 UPDATE
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	
	private void updateSaleCnclTrmsDt(SqlMapClient sqlMap) throws Exception {
		
		Map query      = new HashMap();
		sqlMap.update("DISOUT08100.updateSaleCnclTrmsDt", query);
		
		return;
	}
	
	/**
	 * 내용 : I/F대상 아닌 DATA UPDATE
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	
	private void updateXTrmsYn(SqlMapClient sqlMap) throws Exception {
		
		Map query      = new HashMap();
		sqlMap.update("DISOUT08100.updateXTrmsYn", query);
		
		return;
	}
	
		
	/**
	 * 내용 : 재고 정보 및 재고 구성품 정보 취득 TKey
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void getDBwriteDataFileT(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("getDBwriteDataFileT method start......");
		
		
		openDataFile(OUT_FILE_ID_T,"T");

        String pMessage    = "";
        String cpntStr = "";

	    int writeCnt   = 0;
	    int tempCnt = 0;

		Map<String, Object> requestMapCond = new HashMap<String, Object>();
		Map                 printList      = new HashMap();
		Map cpntList = new HashMap();
		
		List resultCpntList = null;

		try {
			requestMapCond.put("TRMS_GB", "T");
			List resultList = (List) sqlMap.queryForList("DISOUT08100.getDisProd", requestMapCond);

			int selectCnt = resultList.size();

			logMng.writeLogFile(" Condition Table Select Complete ... " + selectCnt);
			
			for (int i   = 0; i < selectCnt; i++) {
				
				printList = (Map)    resultList.get(i);
				
				/**
				 * 구성품 정보 조회
				 */
				
				resultCpntList = (List) sqlMap.queryForList("DISOUT08100.getDisProdCpnt", printList.get("SER_NUM"));
				
				cpntStr = "";
				
				if(resultCpntList != null){
					for( int k = 0; k<resultCpntList.size(); k++){
						
						cpntList = (Map)    resultCpntList.get(k);
						cpntStr = cpntStr + cpntList.get("PROD_CL_NM") + " ";
					}
					
					tempCnt = 200 - cpntStr.getBytes().length;
					
					for( int j = 0; j < tempCnt ; j++){
						cpntStr = cpntStr + " ";
					}
					
				}
				
				
				pMessage  = (String) printList.get("MSG") + cpntStr;

				if(writeDataFile(pMessage, "T") == true){
					writeCnt++;
				}
			}
	
			closeDataFile("T");
			
			logMng.writeLogFile("File Write Count : " + writeCnt);
			logMng.writeLogFile("getDBwriteDataFileT method end......");
		} catch (Exception e) {
			logMng.writeLogFile("  Error ..." + e.toString());
			return;
		}
	}
	
	/**
	 * 내용 : 재고 정보 및 재고 구성품 정보 취득 TKEY+
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void getDBwriteDataFileP(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("getDBwriteDataFileP method start......");
		
		openDataFile(OUT_FILE_ID_P,"P");

        String pMessage    = "";
        String cpntStr = "";

	    int writeCnt   = 0;
	    int tempCnt = 0;

		Map<String, Object> requestMapCond = new HashMap<String, Object>();
		Map                 printList      = new HashMap();
		Map cpntList = new HashMap();
		
		List resultCpntList = null;

		try {
			
			requestMapCond.put("TRMS_GB", "P");
			List resultList = (List) sqlMap.queryForList("DISOUT08100.getDisProd", requestMapCond);

			int selectCnt = resultList.size();

			logMng.writeLogFile(" Condition Table Select Complete ... " + selectCnt);
			
			for (int i   = 0; i < selectCnt; i++) {
				
				printList = (Map)    resultList.get(i);
				
				/**
				 * 구성품 정보 조회
				 */
				
				resultCpntList = (List) sqlMap.queryForList("DISOUT08100.getDisProdCpnt", printList.get("SER_NUM"));
				
				cpntStr = "";
				
				if(resultCpntList != null){
					for( int k = 0; k<resultCpntList.size(); k++){
						
						cpntList = (Map)    resultCpntList.get(k);
						cpntStr = cpntStr + cpntList.get("PROD_CL_NM") + " ";
					}
					
					tempCnt = 200 - cpntStr.getBytes().length;
					
					for( int j = 0; j < tempCnt; j++){
						cpntStr = cpntStr + " ";
					}
					
				}
				
				
				pMessage  = (String) printList.get("MSG") + cpntStr;

				if(writeDataFile(pMessage, "P") == true){
					writeCnt++;
				}
			}
	
			closeDataFile("P");
			
			logMng.writeLogFile("File Write Count : " + writeCnt);
			logMng.writeLogFile("getDBwriteDataFileP method end......");
		} catch (Exception e) {
			logMng.writeLogFile("  Error ..." + e.toString());
			return;
		}
	}
	
	/**
	 * 내용 : 재고 정보 및 재고 구성품 정보 취득 UKey
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void getDBwriteDataFileU(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("getDBwriteDataFileU method start......");
		
		openDataFile(OUT_FILE_ID_U,"U");

        String pMessage    = "";
        String cpntStr = "";

	    int writeCnt   = 0;
	    int tempCnt = 0;

		Map<String, Object> requestMapCond = new HashMap<String, Object>();
		Map                 printList      = new HashMap();
		Map cpntList = new HashMap();
		Map insertM	= new HashMap();
		List resultCpntList = null;

		try {
			requestMapCond.put("TRMS_GB", "U");
			List resultList = (List) sqlMap.queryForList("DISOUT08100.getDisProd", requestMapCond);
			int selectCnt = resultList.size();

			logMng.writeLogFile(" Condition Table Select Complete ... " + selectCnt);
			
			for (int i   = 0; i < selectCnt; i++) {
				
				printList = (Map)    resultList.get(i);
				
				/**
				 * 구성품 정보 조회
				 */
				
				resultCpntList = (List) sqlMap.queryForList("DISOUT08100.getDisProdCpnt", printList.get("SER_NUM"));
				
				cpntStr = "";
				
				if(resultCpntList != null){
					for( int k = 0; k<resultCpntList.size(); k++){
						
						cpntList = (Map)    resultCpntList.get(k);
						cpntStr = cpntStr + cpntList.get("PROD_CL_NM") + " ";
					}
					
					tempCnt = 200 - cpntStr.getBytes().length;
					
					for( int j = 0; j < tempCnt ; j++){
						cpntStr = cpntStr + " ";
					}
					
				}
				
				
				pMessage  = (String) printList.get("MSG") + cpntStr + "A";

				if(writeDataFile(pMessage, "U") == true){
					writeCnt++;
				}
				insertM.put("SER_NUM", printList.get("SER_NUM"));
				insertM.put("RMKS", cpntStr);
				sqlMap.insert("DISOUT08100.addProdDisIfHist", requestMapCond);
			}
	
			closeDataFile("U");
			
			logMng.writeLogFile("File Write Count : " + writeCnt);
			logMng.writeLogFile("getDBwriteDataFileU method end......");
		} catch (Exception e) {
			logMng.writeLogFile("  Error ..." + e.toString());
			return;
		}
	}
	
	
	/**
	 * 내용 : 판매 취소 재고 정보 및 재고 구성품 정보 취득 TKey
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void getDBwriteDataFileForCnclT(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("getDBwriteDataFileForCnclT method start......");
		
		openDataFile(OUT_FILE_ID_CT,"CT");

        String pMessage    = "";
        String cpntStr = "";

	    int writeCnt   = 0;
	    int tempCnt = 0;

		Map<String, Object> requestMapCond = new HashMap<String, Object>();
		Map                 printList      = new HashMap();
		Map cpntList = new HashMap();
		
		List resultCpntList = null;

		try {
			requestMapCond.put("TRMS_GB", "T");
			List resultList = (List) sqlMap.queryForList("DISOUT08100.getDisProdCncl", requestMapCond);

			int selectCnt = resultList.size();

			logMng.writeLogFile(" Condition Table Select Complete ... " + selectCnt);
			
			for (int i   = 0; i < selectCnt; i++) {
				
				printList = (Map)    resultList.get(i);
				
				/**
				 * 구성품 정보 조회
				 */
				
				resultCpntList = (List) sqlMap.queryForList("DISOUT08100.getDisProdCpnt", printList.get("SER_NUM"));
				
				cpntStr = "";
				
				if(resultCpntList != null){
					for( int k = 0; k<resultCpntList.size(); k++){
						
						cpntList = (Map)    resultCpntList.get(k);
						cpntStr = cpntStr + cpntList.get("PROD_CL_NM") + " ";
					}
					
					tempCnt = 200 - cpntStr.getBytes().length;
					
					for( int j = 0; j < tempCnt ; j++){
						cpntStr = cpntStr + " ";
					}
					
				}
				
				
				pMessage  = (String) printList.get("MSG") + cpntStr;

				if(writeDataFile(pMessage, "CT") == true){
					writeCnt++;
				}
			}
	
			closeDataFile("CT");
			
			logMng.writeLogFile("File Write Count : " + writeCnt);
			logMng.writeLogFile("getDBwriteDataFileForCnclT method end......");
		} catch (Exception e) {
			logMng.writeLogFile("  Error ..." + e.toString());
			return;
		}
	}
	
	
	/**
	 * 내용 : 판매 취소 재고 정보 및 재고 구성품 정보 취득 TKey+
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void getDBwriteDataFileForCnclP(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("getDBwriteDataFileForCnclP method start......");
		
		openDataFile(OUT_FILE_ID_CP,"CP");

        String pMessage    = "";
        String cpntStr = "";

	    int writeCnt   = 0;
	    int tempCnt = 0;

		Map<String, Object> requestMapCond = new HashMap<String, Object>();
		Map                 printList      = new HashMap();
		Map cpntList = new HashMap();
		
		List resultCpntList = null;

		try {
			requestMapCond.put("TRMS_GB", "P");
			List resultList = (List) sqlMap.queryForList("DISOUT08100.getDisProdCncl", requestMapCond);

			int selectCnt = resultList.size();

			logMng.writeLogFile(" Condition Table Select Complete ... " + selectCnt);
			
			for (int i   = 0; i < selectCnt; i++) {
				
				printList = (Map)    resultList.get(i);
				
				/**
				 * 구성품 정보 조회
				 */
				
				resultCpntList = (List) sqlMap.queryForList("DISOUT08100.getDisProdCpnt", printList.get("SER_NUM"));
				
				cpntStr = "";
				
				if(resultCpntList != null){
					for( int k = 0; k<resultCpntList.size(); k++){
						
						cpntList = (Map)    resultCpntList.get(k);
						cpntStr = cpntStr + cpntList.get("PROD_CL_NM") + " ";
					}
					
					tempCnt = 200 - cpntStr.getBytes().length;
					
					for( int j = 0; j < tempCnt ; j++){
						cpntStr = cpntStr + " ";
					}
					
				}
				
				
				pMessage  = (String) printList.get("MSG") + cpntStr;

				if(writeDataFile(pMessage, "CP") == true){
					writeCnt++;
				}
			}
	
			closeDataFile("CP");
			
			logMng.writeLogFile("File Write Count : " + writeCnt);
			logMng.writeLogFile("getDBwriteDataFileForCnclP method end......");
		} catch (Exception e) {
			logMng.writeLogFile("  Error ..." + e.toString());
			return;
		}
	}
	
	/**
	 * 내용 : 판매 취소 재고 정보 및 재고 구성품 정보 취득 TKey+
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	private void getDBwriteDataFileForCnclU(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("getDBwriteDataFileForCnclU method start......");
		
		openDataFile(OUT_FILE_ID_U,"U");

        String pMessage    = "";
        String cpntStr = "";

	    int writeCnt   = 0;
	    int tempCnt = 0;

		Map<String, Object> requestMapCond = new HashMap<String, Object>();
		Map                 printList      = new HashMap();
		Map cpntList = new HashMap();
		Map insertM = new HashMap();
		
		List resultCpntList = null;

		try {
			List resultList = (List) sqlMap.queryForList("DISOUT08100.getDisProdCncl", requestMapCond);

			int selectCnt = resultList.size();

			logMng.writeLogFile(" Condition Table Select Complete ... " + selectCnt);
			
			for (int i   = 0; i < selectCnt; i++) {
				
				printList = (Map)    resultList.get(i);
				
				/**
				 * 구성품 정보 조회
				 */
				requestMapCond.put("TRMS_GB", "U");
				resultCpntList = (List) sqlMap.queryForList("DISOUT08100.getDisProdCpnt", printList.get("SER_NUM"));
				sqlMap.update("DISOUT08100.updateSaleCnclTrmsDt", requestMapCond);
				
				cpntStr = "";
				
				if(resultCpntList != null){
					for( int k = 0; k<resultCpntList.size(); k++){
						
						cpntList = (Map)    resultCpntList.get(k);
						cpntStr = cpntStr + cpntList.get("PROD_CL_NM") + " ";
					}
					
					tempCnt = 200 - cpntStr.getBytes().length;
					
					for( int j = 0; j < tempCnt ; j++){
						cpntStr = cpntStr + " ";
					}
					
				}
				
				
				pMessage  = (String) printList.get("MSG") + cpntStr + "C";

				if(writeDataFile(pMessage, "U") == true){
					writeCnt++;
				}
				
				insertM.put("SER_NUM", printList.get("SER_NUM"));
				insertM.put("RMKS", cpntStr);
				sqlMap.insert("DISOUT08100.addProdDisIfHist", requestMapCond);
			}
	
			closeDataFile("U");
			
			logMng.writeLogFile("File Write Count : " + writeCnt);
			logMng.writeLogFile("getDBwriteDataFileForCnclP method end......");
		} catch (Exception e) {
			logMng.writeLogFile("  Error ..." + e.toString());
			return;
		}
	}
	
	/**
	 * message를 읽어서 File에 기록한다. 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param message
	 *            File에 기록할 메세지
	 * @return File에 정상적으로 기록될 경우 true를 리턴,
	 *            그렇지 않을 경우 false를 리턴한다.
	 */
	private boolean writeDataFile(String message, String dataGb) {
		
		try {
			if("T".equals(dataGb)){
				dataFileT.println(message);
				dataFileT.flush();
			}else if("P".equals(dataGb)){
				dataFileP.println(message);
				dataFileP.flush();
			}else if("U".equals(dataGb)){
				dataFileU.println(message);
				dataFileU.flush();
			}else if("CT".equals(dataGb)){
				dataFileCT.println(message);
				dataFileCT.flush();
			}else if("CP".equals(dataGb)){
				dataFileCP.println(message);
				dataFileCP.flush();
			}else if("CU".equals(dataGb)){
				dataFileCU.println(message);
				dataFileCU.flush();
			}

			
		} catch (Exception e) {
			logMng.writeLogFile(e.toString());
			return false;
		}
		return true;
	}
	
	/**
	 * File로부터 message를 읽어온다. 
	 *
	 * @author 전미량 (jeonmiryang)
	 * 
	 * @param void
	 * 
	 * @return void
	 * 
 	 * @throws IOException
	 */	
	private void openDataFile(String fileName, String dataGb) throws IOException {
		
		String dataPath = new StringBuffer()
			.append(outFilePath + "/")
			.append(fileName + ".")
			.append(fileTime)
			.append(FILE_EXTENSION)
			.toString();
		
		logMng.writeLogFile("Output File : " + dataPath);

		File orgFile = new File(dataPath);
		//orgFile.delete();

		FileOutputStream rsTemp = new FileOutputStream(dataPath, true);
		
		if("T".equals(dataGb)){
			dataFileT = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					rsTemp)), true);
		}else if("P".equals(dataGb)){
			dataFileP = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					rsTemp)), true);
		}else if("U".equals(dataGb)){
			dataFileU = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					rsTemp)), true);
		}else if("CT".equals(dataGb)){
			dataFileCT = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					rsTemp)), true);
		}else if("CP".equals(dataGb)){
			dataFileCP = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					rsTemp)), true);
		}else if("CU".equals(dataGb)){
			dataFileCU = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					rsTemp)), true);
		}
	}

	/**
	 * File을 닫는다. 
	 *
	 * @author 전미량 (jeon miryang)
	 * 
	 * @param void
	 * 
	 * @return void
	 * 
 	 * @throws IOException
	 */
	private void closeDataFile(String dataGb) throws IOException {
		
		if("T".equals(dataGb)){
			dataFileT.close();
		}else if("P".equals(dataGb)){
			dataFileP.close();
		}else if("U".equals(dataGb)){
			dataFileU.close();
		}else if("CT".equals(dataGb)){
			dataFileCT.close();
		}else if("CP".equals(dataGb)){
			dataFileCP.close();
		}else if("CU".equals(dataGb)){
			dataFileCU.close();
		}
	}
	
}
