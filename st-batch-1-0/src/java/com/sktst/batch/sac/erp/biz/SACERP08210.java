package com.sktst.batch.sac.erp.biz;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.math.BigDecimal;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 가상계좌 거래내역</li>
 * <li>설 명 : Hana Bic Net 가상계좌 거래내역을 PS DB에 반영한다.</li>
 * <li>작성일 : 2009-05-01 16:00:00</li>
 * </ul>
 *
 * @author 하창주 (hachangjoo)
 */
public class SACERP08210 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08210";

	private List lstVirtHist	= null;		//Hana BIC net 가상계좌거래내역 조회
	
	private String yesterday = "";

	/**
	 * 가상계좌 거래내역 정보를 PS DB에 반영 배치 프로그램을 수행한다.
	 * @author	하창주 (hachangjoo)
	 * @param	Map request : request
	 * @return	int : 수행결과(0이면 정상, 아니면 비정상)
	 * @throws	Exception
	 */	
	public int execute(Map request) throws Exception {
		super.getProperties();
		
		//① DB 정보 취득 (SqlMapClient 인스턴스 취득)
		//SqlMapClient sqlMap = getSqlMapClient();
		//Hana Bic Net DB 정보 취득 : SqlMapConfigHB.xml 참조
		Reader reader = null;
		SqlMapClient sqlMap = getSqlMapClient();
		SqlMapClient sqlMapHB = null;
		
		try {
			reader = Resources.getResourceAsReader("SqlMapConfigHB.xml");
			sqlMapHB = SqlMapClientBuilder.buildSqlMapClient(reader);

			logMng.openLogFile(PROG_ID);

			//로그 출력 (AbsBatchJobBiz 클래스에서 제공하는 변수 log를 사용)
			if (log.isDebugEnabled()) {
				logMng.writeLogFile(PROG_ID + ".execute.startTransaction");
				log.debug(request.toString());
			}
			
			//② Transaction 시작
			sqlMap.startTransaction();
			sqlMapHB.startTransaction();
			
			//③ 요청 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");

			Map<String, Object> requestMap = new HashMap<String, Object>();
			//requestMap.put("send_dt", "20090506");
			requestMap.put("send_dt", yesterday);
			
			String hostAddress = "";
			String prodIp = "10.40.10.21";
			
			try {
				java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
				hostAddress = localHost.getHostAddress();
				
				log.debug("Host Address : " + hostAddress);
				log.debug("getCanonicalHostName : " + localHost.getCanonicalHostName());
			} catch (java.net.UnknownHostException e) {
				// Exception 처리
				log.debug(e.toString());
			} catch (Exception e) {
				log.debug(e.toString());
			}

			//④ DB 처리
			//HanaBicNet 가상계좌거래내역조회
			//egtflag 를 'S'로 update : 운영장비의 경우만
			if ( hostAddress.equals(prodIp) ) {
				logMng.writeLogFile("Host Address : " + hostAddress);
				sqlMapHB.update("SACERP08210.updateEgtFlagToS");
			}

			lstVirtHist	= (List)sqlMapHB.queryForList("SACERP08210.getVirtHistList", requestMap);
			

			if (log.isDebugEnabled()) {
				logMng.writeLogFile("TBLVIRTHIST select count : " + lstVirtHist.size());
				log.debug("TBLVIRTHIST select count : " + lstVirtHist.size());
			}

			Map mVirtHist		= null;
			Map mSeqNoDealNo	= null;
			BigDecimal dealNoCnt	= null;
			BigDecimal dectranAmt	= null;
			String bankCode = "";

			for (int i = 0; i < lstVirtHist.size(); i++) {
				mVirtHist = (Map)lstVirtHist.get(i);
				//은행코드
				bankCode = (String)mVirtHist.get("strbankcode");
				bankCode = bankCode == null ? "" : bankCode.trim();
				bankCode = bankCode.length() == 2 ? "0"+bankCode : bankCode;
				mVirtHist.put("strbankcode", bankCode);
				
				log.debug(mVirtHist.toString());
				
				mSeqNoDealNo = (Map)sqlMap.queryForObject("SACERP08210.getSeqNoDealNo", mVirtHist);
				log.debug(mSeqNoDealNo.toString());
				
				//거래번호가 존재하지 않는 경우만 등록
				dealNoCnt = (BigDecimal)mSeqNoDealNo.get("DEAL_NO_CNT");
				if (dealNoCnt.compareTo(new BigDecimal("0")) == 0) {

					//거래금액(BigDecimal)mVirtHist.get("dectran_amt");
					dectranAmt = (BigDecimal)mVirtHist.get("dectran_amt");
					log.debug("dectran_amt : " + dectranAmt.toString() );
					mVirtHist.put("dectran_amt", String.valueOf(dectranAmt.longValue()));

					sqlMap.insert("SACERP08210.addAccImagAccIf", mVirtHist);
				} else {
					logMng.writeLogFile("거래번호가 존재합니다.");
					log.debug(mVirtHist.toString());
				}
			}

			//insert 후  egtflag 'S' -> 'N'로 update : 운영장비의 경우만
			if ( hostAddress.equals(prodIp) ) {
				log.debug("Host Address : " + hostAddress);
				sqlMapHB.update("SACERP08210.updateEgtFlagToN");
			}

			//⑤ 결과 데이터 처리
			
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
			sqlMapHB.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
			//logMng.writeLogFile("Transaction Exception = [" + e + "]");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
				log.debug("execute Exception : " + e.toString());
			}
		} finally {
			//⑥-2 Transaction rollback (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				logMng.writeLogFile(PROG_ID + ".execute.commitTransaction");
			}
			
			sqlMap.endTransaction();
			sqlMapHB.endTransaction();
			logMng.closeLogFile();
			
			if (reader != null) {
				safeClose(reader);
			}
		}
		
		//⑦ 처리 결과 코드 리턴
		return 0;
	}
	
	/**
	 * 리소스 해제
	 * @author	하창주 (hachangjoo)
	 * @param	Reader reader : request
	 * @return	void
	 * @throws	
	 */	
	public void safeClose(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch(IOException ioe) {
				logMng.writeLogFile("ERRCODE:F");
				logMng.writeLogFile("execute Exception : " + ioe.getMessage());
			}
		}
	}

}
