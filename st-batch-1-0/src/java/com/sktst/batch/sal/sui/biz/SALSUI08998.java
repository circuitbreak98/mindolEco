package com.sktst.batch.sal.sui.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;
import java.text.*;


import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/영업</li>
 * <li>설 명 : 신규 및 판매전문을 처리하기 위한 임시 프로그램.</li>
 * <li>작성일 : 2009-03-30 09:00:00</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class SALSUI08998 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSUI08998";
	private static String ifRecTyp  = "";
	private static    int selectCnt = 0;
	private static    int arraySize = 100;

	/**
	 * 배치 프로그램을 수행한다.
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
		
		ifRecTyp = (String)request.get("args1");

		try {

			logMng.openLogFile(PROG_ID);
			logMng.writeLogFile("args1 : " + ifRecTyp);

			// Transaction Start Logging...
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();
			
			// FILE을 읽어서 DB insert.
			openDataFileAddDB(sqlMap);
			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			sqlMap.commitTransaction();

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
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void openDataFileAddDB(SqlMapClient sqlMap) throws Exception {
		logMng.writeLogFile("openDataFileAddDB method start......");

		String cNew     = "1";
		String cMod     = "2";
		String cCncl    = "3";
		String cUsim    = "4";
		String cAll     = "9";
		String cPortal  = "Z";

		String sOpDt    = "";
		String sOpTm    = "";
		   int sSeq     =  0;
		String sIfCl    = "";
		String sIfCtt   = "";

		   int sCol      =   0;
		String sRecTyp[] = new String[arraySize];

		try {


			sRecTyp[1] = ifRecTyp; 
			logMng.writeLogFile("Accept Parameter : " + ifRecTyp);
			sCol       =   1;

			if (!cNew.equals(ifRecTyp) && !cMod.equals(ifRecTyp)  && !cCncl.equals(ifRecTyp)
             && !cUsim.equals(ifRecTyp) && !cAll.equals(ifRecTyp) && !cPortal.equals(ifRecTyp)){
				sRecTyp[1] = ifRecTyp; // 입력된 문자를 전문유형으로 ...
				sCol       =   2;
			}
			
			if(cAll.equals(ifRecTyp))
			{
				logMng.writeLogFile("Process...All Sales U.Key I/F");
				sRecTyp[1]  = "21"; // 신규현금
				sRecTyp[2]  = "22"; // 신규할부
				sRecTyp[3]  = "24"; // 재가입(MNP)
				sRecTyp[4]  = "16"; // 기변 현금 -> 할부추가
				sRecTyp[5]  = "17"; // 보상 현금 -> 할부추가
				sRecTyp[6]  = "18"; // 교품
				sRecTyp[7]  = "19"; // A/S 기변
				sRecTyp[8]  = "20"; // 신규 현금 -> 할부추가
				sRecTyp[9]  = "25"; // 보상현금
				sRecTyp[10] = "26"; // 보상할부
				sRecTyp[11] = "27"; // 기변현금
				sRecTyp[12] = "28"; // 기변할부
				sRecTyp[13] = "65"; // 법인기변
				sRecTyp[14] = "66"; // 법인할부기변
				sRecTyp[15] = "67"; // 법인할부추가
				sRecTyp[16] = "48"; // 법인기변 취소
				sRecTyp[17] = "49"; // 법인할부 기변취소
				sRecTyp[18] = "50"; // 법인할부추가 취소
				sRecTyp[19] = "55"; // 신규가입 취소
				sRecTyp[20] = "56"; // 기변/보상 취소
				sRecTyp[21] = "57"; // 신규할부 취소
				sRecTyp[22] = "58"; // 보상할부 취소
				sRecTyp[23] = "59"; // 기변할부 취소
				sRecTyp[24] = "75"; // 해지
				sRecTyp[25] = "91"; // USIM 명의변경
				sRecTyp[26] = "92"; // USIM 명의변경 취소
				sRecTyp[27] = "93"; // USIM 카드변경
				sRecTyp[28] = "94"; // USIM 카드변경 취소
				sRecTyp[29] = "85"; // 채널변경
				sRecTyp[30] = "41"; // 약정변경
				sRecTyp[31] = "31"; // 부가서비스가입
				sRecTyp[32] = "32"; // 부가서비스해지
				sCol        = 33;
			}
			if(cNew.equals(ifRecTyp))
			{
				logMng.writeLogFile("Process...NEW Sales U.Key I/F");
				sRecTyp[1] = "21"; 
				sRecTyp[2] = "22"; 
				sRecTyp[3] = "24";
				sCol       =   4;
			}
			if(cMod.equals(ifRecTyp))
			{
				logMng.writeLogFile("Process...Change Sales U.Key I/F");
				sRecTyp[1]  = "16"; 
				sRecTyp[2]  = "17"; 
				sRecTyp[3]  = "18"; 
				sRecTyp[4]  = "19"; 
				sRecTyp[5]  = "20"; 
				sRecTyp[6]  = "25"; 
				sRecTyp[7]  = "26"; 
				sRecTyp[8]  = "27"; 
				sRecTyp[9]  = "28"; 
				sRecTyp[10] = "65"; 
				sRecTyp[11] = "66"; 
				sRecTyp[12] = "67"; 
				sCol        =   13;
			}
			if(cCncl.equals(ifRecTyp))
			{
				logMng.writeLogFile("Process...Cancel Sales U.Key I/F");
				sRecTyp[1] = "48"; 
				sRecTyp[2] = "49"; 
				sRecTyp[3] = "50"; 
				sRecTyp[4] = "55"; 
				sRecTyp[5] = "56"; 
				sRecTyp[6] = "57"; 
				sRecTyp[7] = "58"; 
				sRecTyp[8] = "59"; 
				sRecTyp[9] = "75"; 
				sCol       =  10;
			}
			if(cUsim.equals(ifRecTyp))
			{
				logMng.writeLogFile("Process...USIM Sales U.Key I/F");
				sRecTyp[1] = "91"; 
				sRecTyp[2] = "92"; 
				sRecTyp[3] = "93"; 
				sRecTyp[4] = "94"; 
				sCol       =   5;
			}
			if(cPortal.equals(ifRecTyp))
			{
				logMng.writeLogFile("Process...Portal SSO I/F");
				sRecTyp[1] = "A"; 
				sRecTyp[2] = "B"; 
				sRecTyp[3] = "C"; 
				sRecTyp[4] = "D"; 
				sCol       =   5;
			}

			Map<String, Object> requestMap    = new HashMap<String, Object>();
			Map<String, Object> requestMapCall = new HashMap<String, Object>();
			Map msgMap = new HashMap();
			List resultList;

	        for(int j=0; j < sCol; j++){

			 	requestMap.put("IF_CL", sRecTyp[j]);

				resultList = (List) sqlMap.queryForList("SALSUI08998.getUkeyIfMq", requestMap);
				selectCnt = resultList.size();

				logMng.writeLogFile("    전문번호 : "   + sRecTyp[j]);
				logMng.writeLogFile("Select Count : " + selectCnt);

				for (int i = 0; i < selectCnt; i++) {
					msgMap =         (Map) resultList.get(i);
					sOpDt  =      (String) msgMap.get("OP_DT");
					sOpTm  =      (String) msgMap.get("OP_TM");
					sSeq   = ((BigDecimal) msgMap.get("SEQ")).intValue();
					sIfCl  =      (String) msgMap.get("IF_CL");
					sIfCtt =      (String) msgMap.get("IF_CTT");

					logMng.writeLogFile(" Process => " + sOpDt + "/" + sOpTm + "/" + sSeq + "/" + sIfCl);

					// 프로시저 호출 
					requestMapCall.put("IV_OP_DT",  sOpDt);
					requestMapCall.put("IV_OP_TM",  sOpTm);
					requestMapCall.put("IV_SEQ",    sSeq);
					requestMapCall.put("IV_IF_CL",  sIfCl);
					requestMapCall.put("IV_IF_CTT", sIfCtt);

					sqlMap.update("SALSUI08998.call_SP_UKEY_IF_MQ", requestMapCall);
				}
	        }
		} finally {
			logMng.writeLogFile("------------------------------------------------------------");
			logMng.writeLogFile("openDataFileAddDB method end......");
		}
	}
}