package com.sktst.batch.dis.dsm.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : 단말기최종이력 전문을 DB에 반영한다.</li>
 * <li>작성일 : 2009-03-30 09:00:00</li>
 * 
 *  배치 실행 : DISDSM08110_001.sh 1 2 3 4 SKTPSH01.YYYYMMDDHH.dat
 * </ul>
 *
 * @author 김중일 (kimjoongil)
 */
public class DISDSM08110 extends AbsBatchJobBiz {

	private static final String PROG_ID = "DISDSM08110";
	private static final String USER_ID = "DISDSM0811";
//	private static final String IN_FILE_ID = "SKTPSD96";
//	private static final String FILE_EXTENSION = ".dat";
//	private static final String FILE_TIME_FORMAT = yyyymmdd; 

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 김중일 (kimjoongil)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	public int execute(Map request) throws Exception {
		
		super.getProperties();
		
		logMng.openLogFile(PROG_ID);
		
		if (log.isDebugEnabled()) {
			log.debug(PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();
		
		String sFileName = "";
		if( request.size() > 1 ) {
			sFileName = (String)request.get("args1");
			logMng.writeLogFile("args1 : " + sFileName);
		}
		
		try {
			logMng.writeLogFile(PROG_ID + ".execute.startTransaction");
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();
			
			// FILE을 읽어서 DB insert.
			openDataFileAddDB(sqlMap, sFileName);
			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			logMng.writeLogFile(PROG_ID + ".execute.commitTransaction");

			sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
		} finally {
			// 처리 완료. (commit이 안된 경우 rollback)
			logMng.writeLogFile(PROG_ID + ".execute.endTransaction");
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
	 * @author 김중일 (kimjoongil)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fFileName
	 *            파일명
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void openDataFileAddDB(SqlMapClient sqlMap, String fFileName) throws Exception {
		logMng.writeLogFile("openDataFileAddDB method start......");
		FileReader fr = null;
		BufferedReader br = null;

		int readCnt = 0;
		int insertCnt = 0;
		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append(fFileName)
			.toString();
	
		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {
					if(readCnt == 0) {
						deleteOldRtnIfTable(sqlMap, readLine);
					}
					addOldRtnIfTable(sqlMap, readLine);
					insertCnt++;
					
					if((insertCnt % 100) == 0) {
						sqlMap.commitTransaction();
						logMng.writeLogFile("Commit Record(s) : " + insertCnt);
					}

				} catch (Exception ex) {
					logMng.writeLogFile(ex.getMessage());
				}
				readCnt++;
			}
		} catch (Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("openDataFileAddDB Exception : " + e.getMessage());

			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				logMng.writeLogFile(e.getMessage());
			}
		}
		logMng.writeLogFile("File Read Count : " + readCnt);
		logMng.writeLogFile("Insert Count : " + insertCnt);
		logMng.writeLogFile("openDataFileAddDB method end......");
	}

	/**
	 * 상품_개통이력 UKEY IF 테이블(tdis_prod_svc_hst_if)에 insert한다.
	 *
	 * @author 김중일 (kimjoongil)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fieldBuffer
	 *            String
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void addOldRtnIfTable(SqlMapClient sqlMap,
			String fieldBuffer) throws Exception {

		Map<String, String> requestMap = new HashMap<String, String>();
		
		requestMap.put("REC_STR", fieldBuffer);
		requestMap.put("USER_ID", USER_ID);

		//logMng.writeLogFile("rec_str==>[" + fieldBuffer + "]");
		
		Map dataMap = new HashMap<String, String>();

	    dataMap.put("UKEY_ORG_CD"         , fieldBuffer.substring(2  , 8).trim());
	    dataMap.put("UKEY_SUB_ORG_CD"     , fieldBuffer.substring(8  ,12).trim());
	    dataMap.put("PROD_CD"             , fieldBuffer.substring(22 ,27).trim());

	    /**
	     *   임시
	     *   35 자리 값을 확인
	     *   'F' 인지 아닌지 확인
	     *   "F" 인경우 Usim
	     */
	    String prodCl = (String) fieldBuffer.substring(35 ,36).trim();

        if("F".equals(prodCl)){
    	    dataMap.put("SER_NUM"             , fieldBuffer.substring(27 ,35).trim());
        }else{
    	    dataMap.put("SER_NUM"             , fieldBuffer.substring(27 ,42).trim());
        }
	    dataMap.put("SVC_HST_YN"          , fieldBuffer.substring(42 ,43).trim());
	    dataMap.put("SVC_DT"              , fieldBuffer.substring(43 ,51).trim());
	    dataMap.put("UKEY_SVC_ORG_CD"     , fieldBuffer.substring(51 ,57).trim());
	    dataMap.put("UKEY_SVC_SUB_ORG_CD" , fieldBuffer.substring(57 ,61).trim());
	    dataMap.put("USER_ID", USER_ID);

		sqlMap.insert("DISDSM08110.saveProdSvcHstIf", dataMap);
	}

	/**
	 * 상품_개통이력 UKEY IF 테이블(tdis_prod_svc_hst_if)을 delete한다.
	 *
	 * @author 김중일 (kimjoongil)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fieldBuffer
	 *            String
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void deleteOldRtnIfTable(SqlMapClient sqlMap,
			String fieldBuffer) throws Exception {

		Map<String, String> requestMap = new HashMap<String, String>();
		
		requestMap.put("REC_STR", fieldBuffer);
		requestMap.put("USER_ID", USER_ID);

		logMng.writeLogFile("rec_str==>[" + fieldBuffer + "]");

		sqlMap.delete("DISDSM08110.saveDelProdSvcHstIf", requestMap);
	}
}