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
import com.sktst.batch.util.crypt.aesUtils;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST</li>
 * <li>설 명 : Ukey 상담 배치 I/F 추가 전문을 DB에 반영한다.</li>
 * <li>작성일 : 2012-01-16 09:00:00</li>
 * </ul>
 *
 * @author 이문규
 */
public class BASUKC09200 extends AbsBatchJobBiz {

	private static final String PROG_ID = "BASUKC09200";
	private static final String FILE_SEQ = "1";	
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
		
//		logMng.openLogFile(PROG_ID);
		
		if (log.isDebugEnabled()) {
			log.debug(PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();
		
		String sCurDate = "";
		if( request.size() > 1 ) {
			sCurDate = (String)request.get("args1");
//			logMng.writeLogFile("args1 : " + sCurDate);
		}
		
		String sFileName = "";
/*		
		sFileName = new StringBuffer()
           .append("zeqpbbiz00600-")
           .append(sCurDate)
           .append("-5")
           .append(".dat")
           .toString();
		*/
		sFileName = new StringBuffer()
        .append("EQ05.SKCC.")
        .append(sCurDate)
        .append(".dat") 
        .toString();				
//		logMng.writeLogFile("sFileName : " + sFileName);
		
		try {
//			logMng.writeLogFile(PROG_ID + ".execute.startTransaction");
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();
			
			// FILE을 읽어서 DB insert.
			openDataFileAddDB(sqlMap, sFileName, sCurDate);
//			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
//			logMng.writeLogFile(PROG_ID + ".execute.commitTransaction");

			//sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
		} finally {
			// 처리 완료. (commit이 안된 경우 rollback)
//			logMng.writeLogFile(PROG_ID + ".execute.endTransaction");
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.endTransaction");
			}
			sqlMap.endTransaction();
//			logMng.closeLogFile();
		}

//		logMng.closeLogFile();
		
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
//		logMng.writeLogFile("openDataFileAddDB method start......");
		FileReader fr = null;
		BufferedReader br = null;

		int insertCnt = 0;
		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append(fFileName)
			.toString();
		
		try {
			fr = new FileReader(dataPath);
		 
			br = new BufferedReader(fr);

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {

					addProdInoutIf(sqlMap, readLine, sCurDate);
					insertCnt++;
					
					sqlMap.getCurrentConnection().commit();

				} catch (Exception ex) {
					logMng.writeLogFile("ex 1"+ex.getMessage());
					logMng.writeLogFile("ex 2"+ex);
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
//		logMng.writeLogFile("Insert Count : " + insertCnt);
//		logMng.writeLogFile("openDataFileAddDB method end......");
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
		
		byte[] sByte = null;
		sByte = fieldBuffer.getBytes();
		
		/* 암호화 대상에 null, "" 체크 */
		String sIfResNo = new String(sByte, 1109, 30).trim();

		if (!"".equals(sIfResNo)) {
			if (!"".equals(sIfResNo + "")
					&& !"null".equals(sIfResNo + "")
					&& sIfResNo.indexOf("=") < 0) {
				sIfResNo = aesUtils.encrypt(sIfResNo);
			}
		}

		String sIfAccNo = new String(sByte, 1162, 50).trim();

		if (!"".equals(sIfAccNo)) {
			if (!"".equals(sIfAccNo + "")
					&& !"null".equals(sIfAccNo + "")
					&& sIfAccNo.indexOf("=") < 0) {
				sIfAccNo = aesUtils.encrypt(sIfAccNo);
			}
		}

		String sIfTelNo = new String(sByte, 1212, 30).trim();

		if (!"".equals(sIfTelNo)) {
			if (!"".equals(sIfTelNo + "")
					&& !"null".equals(sIfTelNo + "")
					&& sIfTelNo.indexOf("=") < 0) {
				sIfTelNo = aesUtils.encrypt(sIfTelNo);
			}
		}	

		// 전문양식에 맞게 읽은 자료를 자른다.
		requestMap.put("OP_DT", sCurDate);
		requestMap.put("OP_TM", "999999"); // MQ로 들어오는 정상적인 데이터와 듐을 피하기 위해 
		requestMap.put("SEQ", 1);

		requestMap.put("IF_CON_MGMT_NO"    , new String(sByte, 0 , 12).trim());	
		requestMap.put("IF_PROD_CD"        , new String(sByte, 12, 15).trim());	
		requestMap.put("IF_SER_NUM"        , new String(sByte, 27, 30).trim());	
		requestMap.put("IF_CON_DT"         , new String(sByte, 57, 8).trim());	
		requestMap.put("IF_CON_PLC_CD"     , new String(sByte, 65, 10).trim());	
		requestMap.put("IF_COLOR_CD"       , new String(sByte, 75, 2).trim());	
		requestMap.put("IF_SVC_DT"         , new String(sByte, 77, 8).trim());	
		requestMap.put("IF_EQP_ST"         , new String(sByte, 85, 4).trim());	
		requestMap.put("IF_USEPRD_DDCT_AMT", "");	
		requestMap.put("IF_CORROSION_YN"   , "");	
		requestMap.put("IF_POWER_YN"       , "");	
		requestMap.put("IF_CALL_YN"        , "");	
		requestMap.put("IF_DATA_YN"        , "");	
		requestMap.put("IF_DISP_YN"        , "");	
		requestMap.put("IF_FUNCTION_YN"    , "");	
		requestMap.put("IF_LOST_YN"        , new String(sByte, 89, 1).trim());	
		requestMap.put("IF_RETURN_YN"      , new String(sByte, 90, 1).trim());	
		requestMap.put("IF_LEASE_YN"       , new String(sByte, 91, 1).trim());	
		requestMap.put("IF_FLOOD_YN"       , "");	
		requestMap.put("IF_USIM_YN"        , new String(sByte, 92, 1).trim());	
		requestMap.put("IF_OTHER_YN"       , new String(sByte, 93, 1).trim());	
		requestMap.put("IF_RMKS"           , new String(sByte, 94, 1000).trim());	
		requestMap.put("IF_CONFIRM_YN"     , "");	
		requestMap.put("IF_FULLSET_YN"     , "");	
		requestMap.put("IF_CON_AMT"        , new String(sByte, 1094, 15).trim());	
		requestMap.put("IF_BUY_CL"         , "");	
//		requestMap.put("IF_RES_NO"         , new String(sByte, 1109, 30).trim());
//		requestMap.put("IF_RES_NO"         , sIfResNo);
		requestMap.put("IF_RES_NO"         , "");
		requestMap.put("IF_CUST_NM"        , new String(sByte, 1139, 20).trim());	
		requestMap.put("IF_BANK_CD"        , new String(sByte, 1159, 3).trim());
//		requestMap.put("IF_ACC_NO"         , sIfAccNo);	
//		requestMap.put("IF_TEL_NO"         , sIfTelNo);
		requestMap.put("IF_ACC_NO"         , "");	
		requestMap.put("IF_TEL_NO"         , "");
		requestMap.put("IF_ZIP_CD1"        , new String(sByte, 1242, 3).trim());	
		requestMap.put("IF_ZIP_CD2"        , new String(sByte, 1245, 3).trim());	
		requestMap.put("IF_ADDR"           , new String(sByte, 1248, 200).trim());	
		requestMap.put("IF_DTL_ADDR"       , new String(sByte, 1448, 200).trim());	
		requestMap.put("IF_OUT_YN"         , new String(sByte, 1648, 1).trim());	
		requestMap.put("IF_DEL_YN"         , new String(sByte, 1649, 1).trim());	
		requestMap.put("IF_UPD_CNT"        , new String(sByte, 1650, 7).trim());	
		requestMap.put("IF_INS_DTM"        , new String(sByte, 1657, 14).trim());	
		requestMap.put("IF_INS_USER_ID"    , new String(sByte, 1671, 10).trim());	
		requestMap.put("IF_MOD_DTM"        , new String(sByte, 1681, 14).trim());	
		requestMap.put("IF_MOD_USER_ID"    , new String(sByte, 1695, 10).trim());	
		requestMap.put("IF_CHAG_DEDT_YN"   , new String(sByte, 1705, 1).trim());	
		requestMap.put("IF_SVC_NO"         , new String(sByte, 1706, 30).trim());	
		requestMap.put("IF_SVC_MGMT_NO"    , new String(sByte, 1736, 10).trim());	
		requestMap.put("IF_IMEI"           , new String(sByte, 1746, 15).trim());	
		requestMap.put("IF_IN_YN"          , "");	
		requestMap.put("IF_IN_DT"          , "");	
		requestMap.put("IF_IN_RMKS"        , "");	
		requestMap.put("IF_OTHER_CPNT"     , new String(sByte, 1761, 1000).trim());	
		requestMap.put("IF_POL_YN"         , new String(sByte, 2761, 1).trim());	
		requestMap.put("IF_POL_CD"         , "");	
		requestMap.put("IF_PRC_ST"         , new String(sByte, 2762, 2).trim());	
		requestMap.put("IF_ACC_ORG_ID"     , new String(sByte, 2764, 10).trim());	
		requestMap.put("IF_ACC_ORG_ID2"    , new String(sByte, 2774, 10).trim());
		requestMap.put("IF_CLCT_DT"        , new String(sByte, 2784, 8).trim());
		
		sqlMap.queryForObject("BASUKC09200.callSP_BASUKC09200", requestMap);

//		logMng.writeLogFile("Commit Record(s) : " + requestMap.get("INS_CNT"));
		
	}
}
