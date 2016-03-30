package com.sktst.batch.sac.erp.biz;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/판매회계</li>
 * <li>설 명 : 판매회계 월ERP전송확정여부 확인 </li>
 * <li>작성일 : 2009-08-20</li>
 * </ul>
 *
 * @author 하창주 (hachangjoo)
 */
public class SACERP08300 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08300";
	/**
	 * 매출발생 배치 프로그램을 수행한다.
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

			//로그 출력 (AbsBatchJobBiz 클래스에서 제공하는 변수 log를 사용)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
				log.debug(request.toString());
			}
			
			//② Transaction 시작
			sqlMap.startTransaction();
			
			//③ 요청 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");
			String yymm = "";
			if( request.size() > 1 ) {
				yymm = (String)request.get("args1");
				
				logMng.writeLogFile("args1[년월] : " + yymm);
			}
			
			if (yymm.equals("")) {
				yymm = ErpCommon.getLastMonth();	//입금년월(전월)
			}
			log.debug("dpstYM(입금년월) : " + yymm);

			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("YYMM",		yymm);
			requestMap.put("PROG_ID",	PROG_ID);

			//④ DB 처리
			Map mCnt = (Map)sqlMap.queryForObject("SACERP08300.getMmErpTrmsNotConfirmCnt", requestMap);
			log.debug("mCnt : " + mCnt.toString());
			
			BigDecimal cnt = (BigDecimal)mCnt.get("CNT");
			if (cnt.compareTo(new BigDecimal("0")) == 0) {
				requestMap.put("RMKS",	"정상완료");
				String lastDay = ErpCommon.getLastDay(Integer.parseInt(yymm.substring(0, 4)),
						Integer.parseInt(yymm.substring(4)) );
				requestMap.put("OBJ_DT",	yymm + lastDay);
				sqlMap.insert("SACERP08300.insertTbasBatLog", requestMap);
			}
			
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
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
			logMng.closeLogFile();
		}

		//⑦ 처리 결과 코드 리턴
		return 0;
	}

}
