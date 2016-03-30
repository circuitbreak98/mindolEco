package com.sktst.batch.adm.sys.biz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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
 * <li>업무 그룹명 : 프로젝트/SKTST/Admin</li>
 * <li>설 명 : Procdure, Function 백업</li>
 * <li>작성일 : 2009-07-06</li>
 * </ul>
 *
 * @author 원종윤 (wonjongyoon)
 */
public class ADMSYS08100 extends AbsBatchJobBiz {

	private static final String PROG_ID = "ADMSYS08100";
	private static final String FILE_EXTENSION = ".bak";
	private static String outFileTimeFormat = yyyymmdd;	
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
				log.debug("ADMSYS08100.execute.startTransaction");
			}
			logMng.writeLogFile("ADMSYS08100.execute.startTransaction");
			sqlMap.startTransaction();
			
			// 업무 시작.

			// Procedure 정보를 가져와서 파일 저장.
			getProcedureWriteFile(sqlMap);
			
			logMng.writeLogFile("------------------------------------------------------------");

			// Function 정보를 가져와서 파일 저장.
			getFunctionWriteFile(sqlMap);
			sqlMap.commitTransaction();
			
		} catch(Exception e) {
			logMng.writeLogFile("execute Exception : " + e);
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e);
			}
		} finally {
			sqlMap.endTransaction();
			// 처리 완료. (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug("ADMSYS08100.execute.endTransaction");
			}
			logMng.closeLogFile();
		}
		return 0;
	}

	/**
	 * Procedure 정보를 읽어서 File에 기록한다. 
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void getProcedureWriteFile(SqlMapClient sqlMap) throws Exception{
		logMng.writeLogFile("getProcedureWriteFile method start......");
		
		openDataFile("Procedure");
		
		List resultList = (List) sqlMap
				.queryForList("ADMSYS08100.getProcedureScript");
		int selectCnt = resultList.size();
		int writeCnt = 0;

		Map msgMap = new HashMap();
		for (int i = 0; i < selectCnt; i++) {
			msgMap = (Map) resultList.get(i);
			if(writeDataFile((String) msgMap.get("TEXT")) == true){
				writeCnt++;
			}
		}
		closeDataFile();
		logMng.writeLogFile("Select Count : " + selectCnt);
		logMng.writeLogFile("File Write Count : " + writeCnt);
		logMng.writeLogFile("getProcedureWriteFile method end......");
	}

	/**
	 * Function 정보를 읽어서 File에 기록한다. 
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void getFunctionWriteFile(SqlMapClient sqlMap) throws Exception{
		logMng.writeLogFile("getFunctionWriteFile method start......");
		
		openDataFile("Function");
		
		List resultList = (List) sqlMap
				.queryForList("ADMSYS08100.getFunctionScript");
		int selectCnt = resultList.size();
		int writeCnt = 0;

		Map msgMap = new HashMap();
		for (int i = 0; i < selectCnt; i++) {
			msgMap = (Map) resultList.get(i);
			if(writeDataFile((String) msgMap.get("TEXT")) == true){
				writeCnt++;
			}
		}
		closeDataFile();
		logMng.writeLogFile("Select Count : " + selectCnt);
		logMng.writeLogFile("File Write Count : " + writeCnt);
		logMng.writeLogFile("getFunctionWriteFile method end......");
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
	private void openDataFile(String srcKind) throws IOException {
		String dataPath = new StringBuffer()
			.append(outFilePath + "/")
			.append(srcKind + "_")
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