package com.sktst.batch.bas.ukc.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST</li>
 * <li>설 명 : Ukey 상담상세 배치 I/F 추가 전문을 DB에 반영한다.</li>
 * <li>작성일 : 2012-01-16 09:00:00</li>
 * </ul>
 *
 * @author 이문규
 */
public class BASUKC08210 extends AbsBatchJobBiz {

	private static final String PROG_ID = "BASUKC08210";
	private static final String FILE_SEQ = "2";	
	private static SimpleDateFormat todayFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    private static SimpleDateFormat timeFormatMilSec = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS", Locale.KOREA); 

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
		
		//logMng.openLogFile(PROG_ID);
		
		if (log.isDebugEnabled()) {
			log.debug(PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();

		String sCurDate = "";
		if( request.size() > 1 ) {
			sCurDate = (String)request.get("args1");
			//logMng.writeLogFile("args1 : " + sCurDate);
		}
		
		String sFileName = "";
/*		
		sFileName = new StringBuffer()
           .append("zeqpbbiz00600-")
           .append(sCurDate)
           .append("-7")
           .append(".dat")
           .toString();
*/
		sFileName = new StringBuffer()
        .append("EQ07.SKCC.")
        .append(sCurDate)
        .append(".dat")        
        .toString();             
		//logMng.writeLogFile("sFileName : " + sFileName);
		
		try {
			//logMng.writeLogFile(PROG_ID + ".execute.startTransaction");
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();
			
			// FILE을 읽어서 DB insert.
			openDataFileAddDB(sqlMap, sFileName, sCurDate);
			//logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			//logMng.writeLogFile(PROG_ID + ".execute.commitTransaction");

			//sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
		} finally {
			// 처리 완료. (commit이 안된 경우 rollback)
			//logMng.writeLogFile(PROG_ID + ".execute.endTransaction");
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.endTransaction");
			}
			sqlMap.endTransaction();
			//logMng.closeLogFile();
		}

		//logMng.closeLogFile();
		
		return 0;
	}

	/**
	 * 파일을 읽어서 DB에 기록한다.
	 *
	 * @author 이문규
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fFileName
	 *            파일명
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void openDataFileAddDB(SqlMapClient sqlMap, String fFileName, String sCurDate) throws Exception {
		//logMng.writeLogFile("openDataFileAddDB method start......");
		FileReader fr = null;
		BufferedReader br = null;

		int insertCnt = 0;
		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append(fFileName)
			.toString();
	
		try {
			fr = new FileReader(dataPath);
			//logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {
					
					addProdInoutIf(sqlMap, readLine, sCurDate);
					insertCnt++;
					
					sqlMap.getCurrentConnection().commit();

				} catch (Exception ex) {
					logMng.writeLogFile("ex "+ex.getMessage());
				}
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
		//logMng.writeLogFile("Insert Count : " + insertCnt);
		//logMng.writeLogFile("openDataFileAddDB method end......");
	}

	/**
	 * tdis_prod_inout_if에 insert한다.
	 *
	 * @author han byung gon
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fieldBuffer
	 *            String
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void addProdInoutIf(SqlMapClient sqlMap,
			String fieldBuffer, String sCurDate) throws Exception {

		Map requestMap = new HashMap();
		
		// procedure param setting
		String opDt = "";
		Date date = new Date();
		SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd");
		
		Calendar cal = Calendar.getInstance();
		date = cal.getTime();
		
		opDt = day.format(date);
		
		

		// 전문양식에 맞게 읽은 자료를 자른다.
		requestMap.put("OP_DT", sCurDate);
		requestMap.put("OP_TM", "999999"); // MQ로 들어오는 정상적인 데이터와 듐을 피하기 위해 
		requestMap.put("SEQ", 1);

		requestMap.put("IF_CON_MGMT_NO" , fieldBuffer.substring(0 , 12).trim());	
		requestMap.put("IF_CON_SEQ"     , fieldBuffer.substring(12, 15).trim());	
		requestMap.put("IF_DTL_CL"      , fieldBuffer.substring(15, 17).trim());	
		requestMap.put("IF_TYPE_CL"     , "");	
		requestMap.put("IF_PROD_CD"     , fieldBuffer.substring(17, 32).trim());	
//		requestMap.put("IF_CPNT_PROD_CD", fieldBuffer.substring(32, 47).trim());	
		requestMap.put("IF_CPNT_PROD_CD", fieldBuffer.substring(139, 154).trim());	
		requestMap.put("IF_QTY"         , fieldBuffer.substring(47, 54).trim());	
		requestMap.put("IF_NRL_QTY"     , fieldBuffer.substring(54, 61).trim());	
		requestMap.put("IF_HLD_QTY"     , fieldBuffer.substring(61, 68).trim());	
		requestMap.put("IF_DDCT_AMT"    , fieldBuffer.substring(68, 83).trim());	
		requestMap.put("IF_DEL_YN"      , fieldBuffer.substring(83, 84).trim());	
		requestMap.put("IF_UPD_CNT"     , fieldBuffer.substring(84, 91).trim());	
		requestMap.put("IF_INS_DTM"     , fieldBuffer.substring(91, 105).trim());	
		requestMap.put("IF_INS_USER_ID" , fieldBuffer.substring(105, 115).trim());	
		requestMap.put("IF_MOD_DTM"     , fieldBuffer.substring(115, 129).trim());	
		requestMap.put("IF_MOD_USER_ID" , fieldBuffer.substring(129, 139).trim());	

		  
		// 프로시저 호출 
		//logMng.writeLogFile("requestMap.toString()"+requestMap.toString());
		
//		sqlMap.queryForObject("BASUKC08210.callSP_BASUKC08210", requestMap);
		sqlMap.queryForObject("BASUKC08200.callSP_BASUKC08200", requestMap);
		//logMng.writeLogFile("Commit Record(s) : " + requestMap.get("INS_CNT"));

		
	}
}
