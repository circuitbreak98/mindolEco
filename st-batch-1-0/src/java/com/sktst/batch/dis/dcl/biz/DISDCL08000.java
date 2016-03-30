package com.sktst.batch.dis.dcl.biz;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;
import com.sktst.batch.sac.erp.biz.ErpCommon;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : 상품재고 월 마감 </li>
 * <li>작성일 : 2010-01-26</li>
 * </ul>
 *
 * @author 이강수 (leejunghyun)
 */
public class DISDCL08000 extends AbsBatchJobBiz {

	private static final String PROG_ID = "DISDCL08000";
	private static final String SHELL = "/bin/sh";
	private static final String BATCH_PATH = "/app/st-1-0/batch/";

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 이강수 (leekangsu)
	 *
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	public int execute(Map request) throws Exception {
		super.getProperties();

		//① DB 정보 취득 (SqlMapClient 인스턴스 취득)
		SqlMapClient sqlMap = getSqlMapClient();

		try {
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			logMng.openLogFile(PROG_ID);

			//로그 출력 (AbsBatchJobBiz 클래스에서 제공하는 변수 log를 사용)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute");
			}

			if (log.isDebugEnabled()) {
				log.debug(request.toString());
			}

			String sProcDt = "";
			String sActFlag = "";
			if( request.size() > 1 ) {
				sProcDt = (String)request.get("args1");
				sActFlag = (String)request.get("args2");
				log.debug(sProcDt + ".sProcDt");
				log.debug(sActFlag + ".sActFlag");
			}

			//② Transaction 시작
			sqlMap.startTransaction();

			/**
			 *   현재월 전월 가져오기
			 */
			String tranYm = ErpCommon.getLastMonth();
			log.debug("######### 전월  ######### : [" + tranYm + "]");
			/**
			 *   마감월 가져오기
			 */
			Map  mapData = (Map)sqlMap.queryForObject("DISDCL08000.getCloseYm", "");
			String closeYm  = null;
			if(mapData != null){
				closeYm = (String) mapData.get("CLOSE_YM");
			}
//			closeYm = "200910";
			log.debug("######### 최종마감월  ######### : [" + closeYm + "]");
			
			if(closeYm.compareTo(tranYm) < 0){

				/************************************************************
				 *   상품재고_월마감
				 ************************************************************/
				sendDISDCL08100(tranYm);
//				String[] command = new String[4];
//				command[0] = SHELL;
//				String shellName = "DISDCL08100_001.sh";
//				Process proc = null;
//				BufferedReader br = null;
//				String line = null;
//
//				command[1] = BATCH_PATH + shellName;
//				command[2] = tranYm;
//				command[3] = "I";
//				
//				log.debug("######### 상품재고_월마감  #########");
//				proc = Runtime.getRuntime().exec(command);
//
//				br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//				while ((line = br.readLine()) != null) {
//					logMng.writeLogFile(line);
//				}

				/************************************************************
				 *   기타상품재고_월마감
				 ************************************************************/
				//20111213 주석처리
				//sendDISDCL08200(tranYm);
//				shellName = "DISDCL08201_001.sh";
//				proc = null;
//				br = null;
//				line = null;
//
//				command[1] = BATCH_PATH + shellName;
//				command[2] = tranYm;
//				command[3] = "I";
//				
//				log.debug("######### 기타상품재고_월마감  #########");
//				proc = Runtime.getRuntime().exec(command);
//
//				br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//				while ((line = br.readLine()) != null) {
//					logMng.writeLogFile(line);
//				}

				/************************************************************
				 *   상품재고_판매회계 재고수불원장 월마감
				 ************************************************************/
				//20111213 주석처리
				//sendDISDCL08300(tranYm);
//				shellName = "DISDCL08301_001.sh";
//				proc = null;
//				br = null;
//				line = null;
//
//				command[1] = BATCH_PATH + shellName;
//				command[2] = tranYm;
//				command[3] = "I";
//				
//				log.debug("######### 상품재고_판매회계 재고수불원장 월마감  #########");
//				proc = Runtime.getRuntime().exec(command);

			}else{
				log.debug("######### 전월 ["+tranYm+"]이 마감되었습니다. #########");
			}
			
			//③ 요청 데이터 처리, ④ DB 처리, ⑤ 결과 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");


			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}

			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("Transaction Exception = [" + e + "]");
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
			}
		} finally {
			//⑥-2 Transaction rollback (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			sqlMap.endTransaction();
		}

		logMng.closeLogFile();

		//⑦ 처리 결과 코드 리턴
		return 0;
	}
	/**
	 * 상품재고_월마감
	 * @author	하창주 (hachangjoo)
	 * @param	String tranYm : 마감월
	 * @return	void
	 */
	private void sendDISDCL08100(String tranYm) throws Exception {

		/************************************************************
		 *   상품재고_월마감
		 ************************************************************/
		String[] command = new String[4];
		command[0] = SHELL;
		String shellName = "DISDCL08100_001.sh";
		Process proc = null;
		BufferedReader br = null;
		String line = null;

		command[1] = BATCH_PATH + shellName;
		command[2] = tranYm;
		command[3] = "I";
		
		log.debug("######### 상품재고_월마감  #########");
		proc = Runtime.getRuntime().exec(command);

		br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		while ((line = br.readLine()) != null) {
			logMng.writeLogFile(line);
		}
	}
	
	/**
	 * 기타상품재고_월마감
	 * @author	하창주 (hachangjoo)
	 * @param	String tranYm : 마감월
	 * @return	void
	 */
	private void sendDISDCL08200(String tranYm) throws Exception {

		/************************************************************
		 *   기타상품재고_월마감
		 ************************************************************/
		String[] command = new String[4];
		command[0] = SHELL;
		String shellName = "DISDCL08200_001.sh";
		Process proc = null;
		BufferedReader br = null;
		String line = null;

		command[1] = BATCH_PATH + shellName;
		command[2] = tranYm;
		command[3] = "I";
		
		log.debug("######### 기타상품재고_월마감  #########");
		proc = Runtime.getRuntime().exec(command);

		br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		while ((line = br.readLine()) != null) {
			logMng.writeLogFile(line);
		}
	}	
	/**
	 * 상품재고_판매회계 재고수불원장 월마감
	 * @author	하창주 (hachangjoo)
	 * @param	String tranYm : 마감월
	 * @return	void
	 */
	private void sendDISDCL08300(String tranYm) throws Exception {

		/************************************************************
		 *   기타상품재고_월마감
		 ************************************************************/
		String[] command = new String[4];
		command[0] = SHELL;
		String shellName = "DISDCL08300_001.sh";
		Process proc = null;
		BufferedReader br = null;
		String line = null;

		command[1] = BATCH_PATH + shellName;
		command[2] = tranYm;
		command[3] = "I";
		
		log.debug("######### 상품재고_판매회계 재고수불원장 월마감  #########");
		proc = Runtime.getRuntime().exec(command);

		br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		while ((line = br.readLine()) != null) {
			logMng.writeLogFile(line);
		}
	}		
}