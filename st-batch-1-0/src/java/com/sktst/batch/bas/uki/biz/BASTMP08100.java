package com.sktst.batch.bas.uki.biz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class BASTMP08100 extends AbsBatchJobBiz {

	private static final String PROG_ID = "BASTMP08100";
	//private static final String IN_FILE_ID = "SKTP01";
	private static final String OUT_FILE_ID = "TPSD01";
	private static final String FILE_EXTENSION = ".dat";
	private static final String FILE_TIME_FORMAT = yyyymmdd;
	private static String inFileTimeFormat = "";
	private static String outFileTimeFormat = "";	
	private static final int COL_SIZE = 1000;
	private PrintWriter dataFile;

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
				log.debug(PROG_ID + ".execute");
			}
			
			inFileTimeFormat = (String)request.get("args1");
			logMng.writeLogFile("args1 : " + inFileTimeFormat);
			
			outFileTimeFormat = (String)request.get("args2");
			String args2 = "${args2}";
			if(outFileTimeFormat.equals(args2))
			{
				outFileTimeFormat = FILE_TIME_FORMAT;
				logMng.writeLogFile("args2 기본날짜 적용 : " + outFileTimeFormat);
			} else {			
				logMng.writeLogFile("args2 : " + outFileTimeFormat);
			}
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();
			
			// FILE을 읽어서 DB insert.
			//openDataFileAddDB(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");

			// DB를 읽어서 FILE로 down.
			//getDBwriteDataFile(sqlMap);
			
			// Procedure Call
			Map<String, Object> requestMap = new HashMap<String, Object>();
			String sDate="20090526";
			requestMap.put("OP_STDT", sDate);
			requestMap.put("OP_USER", PROG_ID.substring(0, 10));
			logMng.writeLogFile("--------BASTMP08100.callSP_BASTMP08100---------------");
			logMng.writeLogFile(requestMap.toString());
			sqlMap.queryForObject("BASTMP08100.callSP_BASTMP08100", requestMap);
			
			if(requestMap.get("ERRMSG") != null)  {
				logMng.writeLogFile("ERRCODE:"+requestMap.get("ERRCODE").toString()
						+"/ERRMSG:" + requestMap.get("ERRMSG").toString());
			} else
			{
				logMng.writeLogFile("null!!!!!!");
			}

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
	 * @author 원종윤 (wonjongyoon)
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
		String[] fieldBuffer = new String[COL_SIZE];

		int readCnt = 0;
		int insertCnt = 0;
		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
//			.append(IN_FILE_ID + "_")
//			.append(FILE_TIME_FORMAT)
//			.append(FILE_EXTENSION)
			.append(inFileTimeFormat)
			.toString();
		
		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
		
			br = new BufferedReader(fr);

			for (String readLine; (readLine = br.readLine()) != null;) {

				fieldBuffer = readLine.split(";");

				try {
					addSampleTable(sqlMap, fieldBuffer);
					insertCnt++;
				} catch (Exception ex) {
					logMng.writeLogFile(ex.getMessage());
					if (log.isDebugEnabled()) {
						log.debug(ex.getMessage());
					}
				}
				readCnt++;
			}
		} catch (Exception e) {
			logMng.writeLogFile("openDataFileAddDB Exception : " + e.getMessage());
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				logMng.writeLogFile(e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage());
				}
			}
			logMng.writeLogFile("File Read Count : " + readCnt);
			logMng.writeLogFile("Insert Count : " + insertCnt);
			logMng.writeLogFile("openDataFileAddDB method end......");
		}
	}

	/**
	 * 샘플 테이블(SKF_HELLO)에 insert한다.
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fieldBuffer
	 *            String[]
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void addSampleTable(SqlMapClient sqlMap,
			String[] fieldBuffer) throws Exception {

		String[] colnames = new String[] { "ID", "NAME", "MEMO" };
		Map<String, String> requestMap = new HashMap<String, String>();
		for (int i = 0; i < colnames.length; i++) {
			requestMap.put(colnames[i], fieldBuffer[i]);
		}

		sqlMap.insert("BASTMP08100.insertHello2", requestMap);
	}

	/**
	 * File을 읽어서 DB에 기록한다. 
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void getDBwriteDataFile(SqlMapClient sqlMap) throws Exception{
		logMng.writeLogFile("getDBwriteDataFile method start......");
		
		openDataFile();
		
		List resultList = (List) sqlMap
				.queryForList("BASTMP08100.TEST");
		int selectCnt = resultList.size();
		int writeCnt = 0;

		Map msgMap = new HashMap();
		for (int i = 0; i < selectCnt; i++) {
			msgMap = (Map) resultList.get(i);
			if(writeDataFile((String) msgMap.get("MSG")) == true){
				writeCnt++;
			}
		}
		closeDataFile();
		logMng.writeLogFile("Select Count : " + selectCnt);
		logMng.writeLogFile("File Write Count : " + writeCnt);
		logMng.writeLogFile("getDBwriteDataFile method end......");
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
//			.append(FILE_TIME_FORMAT)
			.append(outFileTimeFormat)
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