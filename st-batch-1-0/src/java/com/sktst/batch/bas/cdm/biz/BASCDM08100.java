package com.sktst.batch.bas.cdm.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;
/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준정보</li>
 * <li>설 명 : 상폼코드, 단말기코드, 공통코드 전문을 DB에 반영한다.</li>
 * <li>작성일 : 2009-04-14 16:00:00</li>
 * </ul>
 *
 * @author 최승호 (choiseungho)
 */
public class BASCDM08100 extends AbsBatchJobBiz {

	private static final String PROG_ID = "BASCDM08100";
	private static final String FILE_TIME_FORMAT = hhmmss; 
	private static String inFileTimeFormat = "";

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 최승호 (choiseungho)
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
			
			logMng.writeLogFile(PROG_ID + ".execute");
			inFileTimeFormat = (String)request.get("args1");
			logMng.writeLogFile("args1 : " + inFileTimeFormat);
			logMng.writeLogFile(PROG_ID + ".execute.startTransaction");
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}			

			// 업무 시작.
			sqlMap.startTransaction();
			
			// FILE을 읽어서 DB insert.
			openDataFileAddDB(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			logMng.writeLogFile(PROG_ID + ".execute.commitTransaction");
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}			
			
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
	 * @author 최승호 (choiseungho)
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

		int readCnt = 0;
		int insertCnt = 0;
		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append(inFileTimeFormat)
			.toString();
	
		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {
					insertCnt = insertCnt + addOldRtnIfTable(sqlMap, readLine);
				} catch (Exception ex) {
					logMng.writeLogFile(ex.getMessage());
				}
				readCnt++;
			}
		} catch(Exception e) {
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
	 * 테이블에 insert한다.
	 *
	 * @author 최승호 (choiseungho)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fieldBuffer
	 *            String
	 * @return void
	 * 
	 * @throws Exception
	 */
	private int addOldRtnIfTable(SqlMapClient sqlMap,
			String fieldBuffer) throws Exception {

		Map<String, String> requestMap = new HashMap<String, String>();
		
		requestMap.put("REC_STR", fieldBuffer);
		requestMap.put("OP_TM", FILE_TIME_FORMAT);

		String recTyp = fieldBuffer.substring(0, 2);
		if("01".compareTo(recTyp) == 0) // 상품코드
		{
			String tempStr = getCutText(fieldBuffer, 98, false);
			String code = tempStr.substring(tempStr.length()-6, tempStr.length());
			String prodSt = code.substring(1, 6);
			
			String targetProdSt = "E1000,F1000,G1000"; // 인서트  대상인  상품상태
			if(targetProdSt.indexOf(prodSt) == -1)
			{
				return 0;
			}
			
			// 인서트 제외 대상인 상품구분코드
			String svcProdCd = code.substring(0, 1);
			if("4".compareTo(svcProdCd) == 0)
			{
				return 0;
			}
	
			sqlMap.insert("BASCDM08100.saveProdCdIf", requestMap);
		}
		else if("02".compareTo(recTyp) == 0) // 단말기코드
		{
			// 색상 코드를  where절  in 쿼리로 사용 하기 위해 가공.
			String sColorOrg = requestMap.get("REC_STR");
			sColorOrg = sColorOrg.substring(100,120);
			sColorOrg = sColorOrg.replaceAll(" ", "");
			
			String sColorData = "";
			
			if(sColorOrg.length() > 0){
				
				StringBuffer sb = new StringBuffer();
				
				for(int i = 0 ; i < sColorOrg.length() ; i=i+2){
					
					sb.append("'")
					  .append(sColorOrg.substring(i, i+2))
					  .append("'");
					
					if(i != sColorOrg.length()-2){
						sb.append(",");
					}
				}
				
				sColorData = sb.toString();
			}			
			
			requestMap.put("colorData", sColorData);
			sqlMap.insert("BASCDM08100.saveEqpMdlIf", requestMap);
		}
		else // 공통코드
		{
			sqlMap.insert("BASCDM08100.saveCommCdIf", requestMap);
		}
		
		logMng.writeLogFile("rec_str==>[" + fieldBuffer + "]");
		
		return 1;
	}
	

	/**
	 *
	 * @author 최승호 (choiseungho)
	 * 
	 * @param strTarget
	 *            대상문자열
	 * @param nLimit
	 *            기준 BYTE 위치
	 * @param bDot
	 *            ... 표시 여부
	 * @return String
	 */
	private String getCutText(String strTarget, int nLimit, boolean bDot)
	{
		if ( strTarget == null || strTarget.equals( "" ) )
		{
			return strTarget;
		}
		
		String retValue = null;
		
		int nLen = strTarget.length();
		int nTotal = 0;
		int nHex = 0;

		String strDot = "";
		
		if ( bDot )
			strDot = "...";
		
		for (int i = 0 ; i < nLen ; i++)
		{
			nHex = (int)strTarget.charAt( i );
			nTotal += Integer.toHexString( nHex ).length() / 2;
			
			if ( nTotal > nLimit )
			{
				retValue = strTarget.substring( 0, i ) + strDot;
				break;
			}
			else if ( nTotal == nLimit )
			{
				if ( i == (nLen - 1) )
				{
					retValue = strTarget.substring( 0, i - 1 ) + strDot;
					break;
				}
				retValue = strTarget.substring( 0, i + 1 ) + strDot;
				break;
			}
			else
			{
				retValue = strTarget;
			}
		}
		return retValue;
	}
}
