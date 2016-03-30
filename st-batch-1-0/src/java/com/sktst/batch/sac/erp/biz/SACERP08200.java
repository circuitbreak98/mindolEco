package com.sktst.batch.sac.erp.biz;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/판매회계</li>
 * <li>설 명 : 전표처리 결과 확인. </li>
 * <li>작성일 : 2009-05-18</li>
 * </ul>
 *
 * @author 하창주 (hachangjoo)
 */
public class SACERP08200 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08200";
	
	private SAP sap = null;
	
	private List lstCust	= null;		//거래처 I/F
	private List lstHead	= null;		//전표마스터Head
	private List lstX01Head	= null;		//부가세전송Head
	private List lstItem	= null;		//전표마스터Item
	
	/**
	 * 전표처리 결과 확인 배치 프로그램을 수행한다.
	 * @author	하창주 (hachangjoo)
	 * @param	Map request : request
	 * @return	int : 수행결과(0이면 정상, 아니면 비정상)
	 * @throws	Exception
	 */	
	public int execute(Map request) throws Exception {
		super.getProperties();
		
		//① DB 정보 취득 (SqlMapClient 인스턴스 취득)
		SqlMapClient sqlMap = getSqlMapClient();
		
		try {
			logMng.openLogFile(PROG_ID);
			sap = new SAP();

			//로그 출력 (AbsBatchJobBiz 클래스에서 제공하는 변수 log를 사용)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
				log.debug(PROG_ID + ".execute");
				log.debug(request.toString());
			}
			
			//② Transaction 시작
			sqlMap.startTransaction();
			
			//③ 요청 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");
			String sendDt = "";
			log.debug("sendDt : " + sendDt);
			if( request.size() > 1 ) {
				sendDt	= (String)request.get("args1");
				logMng.writeLogFile("args1[전송일자] : " + sendDt);
			}

			if (sendDt.equals("")) {
				//sendDt = ErpCommon.getDayInterval(-1);	//전송일자(어제)
				sendDt = ErpCommon.getDate();	//전송일자(현재 날짜)
				logMng.writeLogFile("전송일자[sendDt] : " + sendDt);
			}
			
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("ZIFDATE",	sendDt);

			//④ DB 처리
			getCustIterfaceList(sqlMap, requestMap);
			getHeadItemList(sqlMap, requestMap);
			
			if ( lstHead.size() == 0 ) {
				logMng.writeLogFile("전표처리 결과 확인 건이 존재하지 않습니다.");
				log.debug("전표처리 결과 확인 건이 존재하지 않습니다.");
			} else {
				//ERP전송
				sendZifDocResult(sqlMap);

				//⑤ 결과 데이터 처리
			}
			
			
			if ( lstX01Head.size() == 0 ) {
				logMng.writeLogFile("부가세전송 결과 확인 건이 존재하지 않습니다.");
				log.debug("부가세전송 결과 확인 건이 존재하지 않습니다.");
			} else {
				//ERP전송
				sendZfiSalesAddedtaxOut(sqlMap);

				//⑤ 결과 데이터 처리
			}

			if ( lstCust.size() == 0 ) {
				logMng.writeLogFile("거래처 마스터 결과 확인 건이 존재하지 않습니다.");
				log.debug("거래처 마스터 결과 확인 건이 존재하지 않습니다.");
			} else {
				//ERP전송
				sendZfiSalesCusmstOut(sqlMap);

				//⑤ 결과 데이터 처리
			}
			
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:3");
			//logMng.writeLogFile("Transaction Exception = [" + e + "]");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
			}
		} finally {
			//⑥-2 Transaction rollback (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			
			sqlMap.endTransaction();
			sap.disconnect();
			logMng.closeLogFile();
		}

		//⑦ 처리 결과 코드 리턴
		return 0;
	}
	
	/**
	 * 전표마스터 Head, Item 조회
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
 	 * @throws	Exception
	 */
	private void getHeadItemList(SqlMapClient sqlMap, Map<String, Object> requestMap) throws Exception {
		lstHead		= (List)sqlMap.queryForList("SACERP08200.getZift0003List", requestMap);
		lstX01Head	= (List)sqlMap.queryForList("SACERP08200.getZift0003X01List", requestMap);
		lstItem		= (List)sqlMap.queryForList("SACERP08200.getZift0004List", requestMap);
		int selectCnt = lstHead.size();

		Map rowMap = null;
		for (int i = 0; i < selectCnt; i++) {
			rowMap = (Map)lstHead.get(i);
			//if (log.isDebugEnabled()) {
				//log.debug("row[" + i + "] : " + rowMap.toString());
			//}
		}
	}

	/**
	 * 거래처 I/F 조회
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
 	 * @throws	Exception
	 */
	private void getCustIterfaceList(SqlMapClient sqlMap, Map<String, Object> requestMap) throws Exception {
		lstCust = (List)sqlMap.queryForList("SACERP08200.getZift0001List", requestMap);
		int selectCnt = lstCust.size();

		Map rowMap = null;
		for (int i = 0; i < selectCnt; i++) {
			rowMap = (Map)lstCust.get(i);
			if (log.isDebugEnabled()) {
				log.debug("row[" + i + "] : " + rowMap.toString());
			}
		}
	}

	/**
	 * 전표처리 결과 확인 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
 	 * @throws	Exception
	 */
	private void sendZifDocResult(SqlMapClient sqlMap) throws Exception {
		Map<String, Object> imParams = new HashMap<String, Object>();
		Map<String, Object> imTables = new HashMap<String, Object>();

		ArrayList alKey 	= new ArrayList();
		ArrayList alHead	= new ArrayList();
		//Erp Head table row map
		Map mHead = null;
		//Erp Item table row map
		Map mItem = null;

		for (int i = 0; i < lstHead.size(); i++) {
			mHead = (Map)lstHead.get(i);
			alKey.add(mHead);
		}


		for (int j = 0; j < lstItem.size(); j++) {
			mItem = (Map)lstItem.get(j);
			alHead.add(mItem);
		}

		imTables.put("KEY",		alKey.toArray());
		imTables.put("HEAD",	alHead.toArray());

		Map<String, Object> retMap = sap.executeRFC("ZIF_DOC_RESULT", imParams, imTables);
		ErpCommon.writeLogResultMsg(retMap, logMng);

		//if (log.isDebugEnabled()) {
			//log.debug("retMap : " + retMap.toString());
		//}

		//⑤ 결과 데이터 처리
		updateZifDocResult(sqlMap, retMap);

	}

	/**
	 * 부가세전송 결과 확인 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
 	 * @throws	Exception
	 */
	private void sendZfiSalesAddedtaxOut(SqlMapClient sqlMap) throws Exception {
		Map<String, Object> imParams = new HashMap<String, Object>();
		Map<String, Object> imTables = new HashMap<String, Object>();

		//Erp Head table row map
		Map mHead = null;
		Map<String, Object> retMap = null;
		
		for (int i = 0; i < lstX01Head.size(); i++) {
			mHead = (Map)lstX01Head.get(i);

			imParams.put("I_ZBUDAT",	(String)mHead.get("ZBUDAT"));
			imParams.put("I_ZGSBER",	(String)mHead.get("ZGSBER"));
			imParams.put("I_ZGUBUN",	(String)mHead.get("ZGUBUN"));
			imParams.put("I_ZIFDATE",	(String)mHead.get("ZIFDATE"));

			retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_OUT", imParams, imTables);
			//ErpCommon.writeLogResultMsg(retMap, logMng);

			if (log.isDebugEnabled()) {
				//log.debug("retMap : " + retMap.toString());
			}

			//⑤ 결과 데이터 처리
			updateZfiSalesAddedtaxOut(sqlMap, retMap, mHead);
		}

	}
	

	/**
	 * 전표처리 결과 확인 update
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
 	 * @throws	Exception
	 */
	private void updateZifDocResult(SqlMapClient sqlMap, Map<String, Object> retMap) throws Exception {
		ArrayList<Object> alKey		= (ArrayList<Object>)retMap.get("KEY");
		ArrayList<Object> alHead	= (ArrayList<Object>)retMap.get("HEAD");
		
		Map rowMap	= null;
		for (int i = 0; i < alKey.size(); i++) {
			rowMap	= (Map<String, Object>)alKey.get(i);
			
			sqlMap.update("SACERP08200.updateZift0003", rowMap);
			sqlMap.update("SACERP08200.updateTsacDdErpTrms", rowMap);
			sqlMap.update("SACERP08200.updateTsacMmErpTrms", rowMap);
			
		}
	
		for (int j = 0; j < alHead.size(); j++) {
			rowMap	= (Map<String, Object>)alHead.get(j);
			rowMap.put("ZCONFDAT",	getZconfdat(alKey, rowMap));	//확정일자
			
			sqlMap.update("SACERP08200.updateZift0004", rowMap);
		}
	}

	
	/**
	 * Key table에서 확정일자를 얻는다.
	 * @author	하창주 (hachangjoo)
	 * @param	ArrayList<Object> alKey : Key table ArrayList
	 * @return	String : 확정일자
	 */
	private String getZconfdat(ArrayList<Object> alKey, Map<String, Object> mHead) {
		
		String zconfdat = "";
		Map rowMap	= null;
		
		for (int i = 0; i < alKey.size(); i++) {
			rowMap	= (Map<String, Object>)alKey.get(i);
			if ( ((String)rowMap.get("ZBUDAT")).equals((String)mHead.get("ZBUDAT")) &&
					((String)rowMap.get("ZGSBER")).equals((String)mHead.get("ZGSBER")) &&
					((String)rowMap.get("ZGUBUN")).equals((String)mHead.get("ZGUBUN")) &&
					((String)rowMap.get("ZIFDATE")).equals((String)mHead.get("ZIFDATE"))
			) {
				zconfdat = (String)rowMap.get("ZCONFDAT");
				continue;
			}
		}
		
		return zconfdat;
	}


	/**
	 * 부가세전송 결과 확인 update
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
 	 * @throws	Exception
	 */
	private void updateZfiSalesAddedtaxOut(SqlMapClient sqlMap, Map<String, Object> retMap, Map mHead) throws Exception {
		ArrayList<Object> alHead	= (ArrayList<Object>)retMap.get("T_HEAD");
		ArrayList<Object> alBody	= (ArrayList<Object>)retMap.get("T_BODY");
		
		//log.debug("alHead size : " + alHead.size());
		//log.debug("alBody size : " + alBody.size());
		Map rowMap	= null;
		for (int i = 0; i < alHead.size(); i++) {
			rowMap	= (Map<String, Object>)alHead.get(i);
			rowMap.put("ZBUDAT",	(String)mHead.get("ZBUDAT"));	//귀속일자
			rowMap.put("ZCONFDAT",	(String)mHead.get("ZCONFDAT"));	//확정일
			
			sqlMap.update("SACERP08200.updateZift0003", rowMap);
			sqlMap.update("SACERP08200.updateTsacMmTaxTrms", rowMap);
			sqlMap.update("SACERP08200.updateTsacMmTaxErpTrms", rowMap);
		}
	
		for (int j = 0; j < alBody.size(); j++) {
			rowMap	= (Map<String, Object>)alBody.get(j);
			rowMap.put("ZBUDAT"   , (String)mHead.get("ZBUDAT"));	//귀속일자
			rowMap.put("ZCONFDAT" , (String)mHead.get("ZCONFDAT"));	//확정일
			sqlMap.update("SACERP08200.updateZift0006", rowMap);
		}
	}

	/**
	 * 거래처 마스터 결과 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
 	 * @throws	Exception
	 */
	private void sendZfiSalesCusmstOut(SqlMapClient sqlMap) throws Exception {
		Map<String, Object> imParams = new HashMap<String, Object>();
		Map<String, Object> imTables = new HashMap<String, Object>();

		//거래처 I/F table row map
		Map mCust = null;
		Map<String, Object> retMap = null;

		for (int i = 0; i < lstCust.size(); i++) {
			mCust = (Map)lstCust.get(i);

			imParams.put("I_ZBUDAT",	(String)mCust.get("ZBUDAT"));	//귀속일자
			imParams.put("I_ZGSBER",	(String)mCust.get("ZGSBER"));	//영업센터
			imParams.put("I_ZGUBUN",	(String)mCust.get("ZGUBUN"));	//전송구분코드
			imParams.put("I_ZIFDATE",	(String)mCust.get("ZIFDATE"));	//전송일시분초(전송차수)

			retMap = sap.executeRFC("ZFI_SALES_CUSMST_OUT", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			if (log.isDebugEnabled()) {
				log.debug("retMap : " + retMap.toString());
			}

			//⑤ 결과 데이터 처리
			updateZfiSalesCusmstOut(sqlMap, retMap);
		}

	}
	
	/**
	 * 거래처 마스터 결과 확인 update
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
 	 * @throws	Exception
	 */
	private void updateZfiSalesCusmstOut(SqlMapClient sqlMap, Map<String, Object> retMap) throws Exception {
		ArrayList<Object> alLogmsg		= (ArrayList<Object>)retMap.get("T_LOGMSG");
		
		Map rowMap	= null;
		for (int i = 0; i < alLogmsg.size(); i++) {
			rowMap	= (Map<String, Object>)alLogmsg.get(i);
			
			sqlMap.update("SACERP08200.updateZift0001", rowMap);
			sqlMap.update("SACERP08200.updateZift0002", rowMap);
			//log.debug("rowMap : " + rowMap.toString());
			sqlMap.update("SACERP08200.updateSacCustMaster", rowMap);
		}
	
	}

	/**
	 * ERP전송 test
	 * @author 하창주 (hachangjoo)
	 * @param void
	 * @return void
	 */
	private void sendTest() {
		try {
			String osName = System.getProperty("os.name");
			log.debug("osName : " + osName);

			Map<String, Object> imParams = new HashMap<String, Object>();
			Map<String, Object> imTables = new HashMap<String, Object>();

			imParams.put("DATE", "20090412");
			imParams.put("TIME", "101023");
			if (log.isDebugEnabled()) {
				log.debug("imParams : " + imParams.toString());
			}

			Object[] objs = new Object[2];
			Map<String, Object> rowMap1 = new HashMap<String, Object>();
			rowMap1.put("SEQ", 111);
			rowMap1.put("TEXT", "하나");
			objs[0] = rowMap1;
			Map<String, Object> rowMap2 = new HashMap<String, Object>();
			rowMap2.put("SEQ", 222);
			rowMap2.put("TEXT", "둘");
			objs[1] = rowMap2;

			imTables.put("BODY", objs);

			SAP sap = new SAP();
			Map<String, Object> retMap = sap.executeRFC("Z_RFC_INBOUND", imParams, imTables);
			if (log.isDebugEnabled()) {
				log.debug("retMap : " + retMap.toString());
			}

		}
		catch (Exception ex) {
			ex.printStackTrace();
			if (log.isDebugEnabled()) {
				log.debug("Exception : "+ex);
			}
		}
	}
	
}

