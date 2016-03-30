package com.sktst.batch.acc.dcl.biz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

public class ACCDCL01000 extends AbsBatchJobBiz {

	private String currentDate = null;
	private PrintWriter dataFile;
	private PrintWriter logFileIn;
	private PrintWriter logFileOut;
	private String dataFilePathIn = "C:\\project-workspace\\ps-batch-1-0\\bin\\com\\sktst\\batch\\file\\in";
	private String dataFilePathOut = "C:\\project-workspace\\ps-batch-1-0\\bin\\com\\sktst\\batch\\file\\out";
	private String logFilePathIn = "C:\\project-workspace\\ps-batch-1-0\\bin\\com\\sktst\\batch\\log\\in";
	private String logFilePathOut = "C:\\project-workspace\\ps-batch-1-0\\bin\\com\\sktst\\batch\\log\\out";
	
	String fName = ACCDCL01000.class.getSimpleName();
	
	public int execute(Map request) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("ACCDCL01000.execute");
		}
		
		for(int i = 1; i < request.size(); i++){
			System.out.println(request.get("args"+i));
		}
		
		long startTime = System.currentTimeMillis();
		SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

		currentDate = fileFormat.format(new java.util.Date(startTime));
		
		SqlMapClient sqlMap = getSqlMapClient();
		
		try{
			if(log.isDebugEnabled()){
				log.debug("ACCDCL01000.execute.startTransaction");
			}			
			
			     
			
			//업무 시작.
			sqlMap.startTransaction ();

			// FILE을 읽어서 DB insert.
			// insert(sqlMap);
			
			// DB를 읽어서 FILE로 down.
			select(sqlMap);
			
			
			
			
			
			//처리 결과 commit
			if(log.isDebugEnabled()){
				log.debug("ACCDCL01000.execute.commitTransaction");
			}			
			sqlMap.commitTransaction ();
			
		} finally {
			//처리 완료. (commit이 안된 경우 rollback)
			if(log.isDebugEnabled()){
				log.debug("ACCDCL01000.execute.endTransaction");
			}			
			sqlMap.endTransaction ();
		}
		return 0;
	}
	
	/**
	 * insert예제.
	 * @param sqlMap
	 * @param requestMap
	 * @throws Exception
	 */
	private void insert(SqlMapClient sqlMap) throws Exception {
		
		logFileInOpen();
		logFileInWrite("start insert");
		
		FileReader fr = null;
		BufferedReader br = null;
		String[] fieldBuffer = new String[100];
		
		int insertNum = 0;
		
		
		//fr = new FileReader("C:/project-workspace/ps-batch-1-0/build/com/sktst/batch/acc"+"/"+fName+".TXT");
		fr = new FileReader(dataFilePathIn+"/"+currentDate+"/"+fName+".TXT");
		try {
			br = new BufferedReader(fr);
	
			for (String readLine; (readLine = br.readLine()) != null;) {
	
				fieldBuffer = readLine.split(";");
	
				try {
					insertSKF_HELLOTablerow(sqlMap, fieldBuffer);
				} catch (Exception ex) {
					if(log.isDebugEnabled()){
						log.debug(ex.getMessage());
					}
				}
				insertNum++;
			}
		} finally {
			try {
				br.close();
			} catch(Exception e) {
				if(log.isDebugEnabled()){
					log.debug(e.getMessage());
				}
			}
		}
		logFileInWrite("insert count("+insertNum+")");
		logFileInWrite("end insert");
		logFileInClose();
	}
	
	/**
	 * insert예제.
	 * @param sqlMap
	 * @param requestMap
	 * @throws Exception
	 */
	private void insertSKF_HELLOTablerow(SqlMapClient sqlMap, String[] fieldBuffer) throws Exception {
		
		String[] colnames = new String[]{"ID","NAME","MEMO"};
		Map<String, String> requestMap = new HashMap<String, String>();
		for(int i=0; i<colnames.length; i++) {
			requestMap.put(colnames[i], fieldBuffer[i]);
		}
		
		sqlMap.insert("ACCDCL01000.insertHello", requestMap);
		
	}
	
	/**
	 * select예제.
	 * @param sqlMap
	 * @param requestMap
	 * @throws Exception
	 */
	private void select(SqlMapClient sqlMap) throws Exception {
		logFileOutOpen();
		logFileOutWrite("select start");
		datafileOpen();
		
		List resultList = (List)sqlMap.queryForList("ACCDCL01000.selectHelloListDown");
		System.out.println(resultList);
		Map msgMap = new HashMap();
		for (int i=0; i<resultList.size(); i++) {
			msgMap = (Map)resultList.get(i);
			dataFileWrite((String)msgMap.get("MSG"));
		}
		datafileClose();
		logFileInWrite("select count("+resultList.size()+")");
		logFileOutWrite("select end");
		logFileOutClose();
	}
	
	private void dataFileWrite(String message) {

		try {
			
			dataFile.println(message);
			dataFile.flush();
		} catch (Exception e) {
		}
	}
	
	private void datafileOpen() throws IOException {
		
		//String dataPath = "C:/project-workspace/ps-batch-1-0/build/com/sktst/batch/acc"+ "/" + "accfilercv" + ".TXT";
		String dataPath = dataFilePathOut+"/"+currentDate+"/"+fName+".TXT";
		try {
			makeFolderFile(dataPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File orgFile = new File(dataPath);
		orgFile.delete();
		
		FileOutputStream rsTemp = new FileOutputStream(dataPath, true);
		dataFile = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				rsTemp)), true);
		
	}
	
	private void datafileClose() throws IOException {
		dataFile.close();
	}
	
	private void logFileInOpen() throws IOException {
		String dataPath = logFilePathIn+"/"+currentDate+"/"+fName+"_in.log";
		try {
			makeFolderFile(dataPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File orgFile = new File(dataPath);
		orgFile.delete();
		
		FileOutputStream rsTemp = new FileOutputStream(dataPath, true);
		logFileIn = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				rsTemp)), true);
	}
	
	private void logFileInWrite(String message) {

		try {
			
			logFileIn.println(message);
			logFileIn.flush();
		} catch (Exception e) {
		}
	}
	
	private void logFileInClose() throws IOException {
		logFileIn.close();
	}
	
	private void logFileOutOpen() throws IOException {
		String dataPath = logFilePathOut+"/"+currentDate+"/"+fName+"_out.log";
		try {
			makeFolderFile(dataPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File orgFile = new File(dataPath);
		orgFile.delete();
		
		FileOutputStream rsTemp = new FileOutputStream(dataPath, true);
		logFileOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				rsTemp)), true);
	}
	
	private void logFileOutWrite(String message) {

		try {
			
			logFileOut.println(message);
			logFileOut.flush();
		} catch (Exception e) {
		}
	}
	
	private void logFileOutClose() throws IOException {
		logFileOut.close();
	}
	
	private void makeFolderFile(String fileNameFullPath) throws Exception {
		String tempPath = "";
		String tempToken = "";
		try {
			fileNameFullPath = fileNameFullPath.replaceAll("\\\\", "/");

			StringTokenizer st = new StringTokenizer(fileNameFullPath, "/",
					false);

			while (st.hasMoreTokens()) {
				tempToken = st.nextToken();

				if ("C:".equals(tempToken.toUpperCase())
						|| "/".equals(tempToken.toUpperCase())) {
					tempPath = tempToken;
					continue;
				}

				tempPath = tempPath + "/" + tempToken;

				if (st.hasMoreTokens()) {
					File fileFolder = new File(tempPath);

					if (!fileFolder.exists()) {
						fileFolder.mkdir();
					}
				} else {
					File file = new File(tempPath);
					if (!file.exists()) {
						file.createNewFile();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void logFileOpen() throws IOException {
		String dataPath = logFilePathIn+"/"+currentDate+"/"+fName+"_in.log";
		try {
			makeFolderFile(dataPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File orgFile = new File(dataPath);
		orgFile.delete();
		
		FileOutputStream rsTemp = new FileOutputStream(dataPath, true);
		logFileIn = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				rsTemp)), true);
	}
	
	public void logFileWrite(String message) {

		try {
			
			logFileIn.println(message);
			logFileIn.flush();
		} catch (Exception e) {
		}
	}
	
	public void logFileClose() throws IOException {
		logFileIn.close();
	}
}