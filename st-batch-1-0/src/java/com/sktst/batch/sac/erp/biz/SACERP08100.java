package com.sktst.batch.sac.erp.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/판매회계</li>
 * <li>설 명 : ERP 전송 Main </li>
 * <li>작성일 : 2009-04-28</li>
 * </ul>
 *
 * @author 하창주 (hachangjoo)
 */
public class SACERP08100 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08100";
	private static final String SHELL = "/bin/sh";
	private static final String BATCH_PATH = "/app/batch/";
	
	private List lstDdErpTrms	= null;		//일 ERP 전송 목록
	private List lstMmErpTrms	= null;		//월 ERP 전송 목록
	private Map mErpTrms = null;			//Erp 전송 map


	private String yymm		= "";	//정산월
	private String yymmdd	= "";	//정산일
	private String agency	= "";	//대리점 거래처코드
	private String dealCoCd	= "";	//거래처코드
	private String trmsItem	= "";	//전송구분

	/**
	 * ERP 전송 Main 배치 프로그램을 수행한다.
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

			//④ DB 처리
			//(일) ERP 전송
			sendDdErpTrmsList(sqlMap);
			//(월) ERP 전송
			sendMmErpTrmsList(sqlMap);
			//(월) 부가세 전송
			sendMmTaxTrmsList(sqlMap);
			//(월) 전자세금계산서 전송
			sendMmTaxBillTrmsList(sqlMap);
			
			
			//⑤ 결과 데이터 처리
			
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:F");
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
			logMng.closeLogFile();
		}

		//⑦ 처리 결과 코드 리턴
		return 0;
	}

	/**
	 * (일) ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendDdErpTrmsList(SqlMapClient sqlMap) throws Exception {

		lstDdErpTrms = (List)sqlMap.queryForList("SACERP08100.getDdErpTrmsList");
		if ( lstDdErpTrms.size() == 0 ) {
			logMng.writeLogFile("(일) ERP전송 건이 존재하지 않습니다.");
			log.debug(PROG_ID + " : (일) ERP전송 건이 존재하지 않습니다.");
			return;
		}
		
		/* 전송여부를 전송중으로 수정 (중복실행방지)  20091110 수정 */
		Map dataMap = new HashMap();
		dataMap.put("remark", "전송진행중");
		
		sqlMap.update("SACERP08100.updateDdErpTrms",dataMap);
		sqlMap.commitTransaction();
		
		String[] command = new String[6];
		command[0] = SHELL;
		command[1] = BATCH_PATH + "SACERP08140_001.sh";

		Process proc = null;
		BufferedReader br = null;
		String line = null;
		
		for (int idx = 0; idx < lstDdErpTrms.size(); idx++) {
			mErpTrms = (Map)lstDdErpTrms.get(idx);

			yymmdd		= (String)mErpTrms.get("YYMMDD");		//년월일
			agency		= (String)mErpTrms.get("AGENCY");		//대리점
			trmsItem	= (String)mErpTrms.get("TRMS_ITEM");	//전송구분
			
			command[2] = yymmdd;
			command[3] = agency;
			command[4] = trmsItem;
			command[5] = "";
			
			proc = Runtime.getRuntime().exec(command);
			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while ((line = br.readLine()) != null) {
				logMng.writeLogFile(line);
			}
		}
	}


	/**
	 * (월) ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendMmErpTrmsList(SqlMapClient sqlMap) throws Exception {

		Map mmTaxTrms = (Map)sqlMap.queryForObject("SACERP08100.getMmErpTrmsCnt");
		if ( mmTaxTrms != null ) {
			logMng.writeLogFile("(월) 전표 ERP 전송중 입니다.");
			log.debug(PROG_ID + " : (월) 전표 ERP 전송중 입니다.");
			return;
		}

		
		lstMmErpTrms = (List)sqlMap.queryForList("SACERP08100.getMmErpTrmsList");
		if ( lstMmErpTrms.size() == 0 ) {
			logMng.writeLogFile("(월) ERP전송 건이 존재하지 않습니다.");
			log.debug(PROG_ID + " : (월) ERP전송 건이 존재하지 않습니다.");
			return;
		}
		
		/* 전송여부를 전송중으로 수정 (중복실행방지)  20091110 수정 */
		Map dataMap = new HashMap();
		dataMap.put("remark", "전송진행중");
		sqlMap.update("SACERP08100.updateMmErpTrms", dataMap);
		sqlMap.commitTransaction();
		
		String[] command = new String[6];
		command[0] = SHELL;

		Process proc = null;
		BufferedReader br = null;
		String line = null;
		String shellName = "";

		for (int idx = 0; idx < lstMmErpTrms.size(); idx++) {
			mErpTrms = (Map)lstMmErpTrms.get(idx);

			yymm		= (String)mErpTrms.get("YYMM");
			agency		= (String)mErpTrms.get("AGENCY");
			trmsItem	= (String)mErpTrms.get("TRMS_ITEM");	//전송구분
			
			/** 
			 *  B01 : ZGUBUN_CASH_SALE  : (월)현금매출발생
			 *  B02 : ZGUBUN_CARD_SALE  : (월)카드매출발생
			 */
			if ( ErpCommon.ZGUBUN_CASH_SALE.equals(trmsItem)          || ErpCommon.ZGUBUN_CARD_SALE.equals(trmsItem)){
				shellName = "SACERP08110_001.sh";
				
			/**
			 *  B03 : ZGUBUN_ALLOT_SALE      : (월)할부매출발생
			 *  B04 : ZGUBUN_ALLOT_INT       : (월)할부채권이자발생
			 *  F16 : ZGUBUN_SKT_AHEAD_DPST  : (월)SKT선입금
			 *  B05 : ZGUBUN_ASTAMT_SALE     : (월)보조금매출
			 *  B06 : ZGUBUN_PR_MNY_SALE_SKT : (월)장려금매출 - SKT
			 *  B08 : ZGUBUN_CMMS_SALE_SKT   : (월)위탁수수료매출 - SKT
			 *  C01 : ZGUBUN_ALLOT_MTCH      : (월)할부채권 1, 2차 상계
			 */
			} else if ( ErpCommon.ZGUBUN_ALLOT_SALE.equals(trmsItem)      || ErpCommon.ZGUBUN_ALLOT_INT.equals(trmsItem)
					 || ErpCommon.ZGUBUN_SKT_AHEAD_DPST.equals(trmsItem)  || ErpCommon.ZGUBUN_ASTAMT_SALE.equals(trmsItem)
					 || ErpCommon.ZGUBUN_PR_MNY_SALE_SKT.equals(trmsItem) || ErpCommon.ZGUBUN_CMMS_SALE_SKT.equals(trmsItem)
					 || ErpCommon.ZGUBUN_ALLOT_MTCH.equals(trmsItem) ){
				shellName = "SACERP08111_001.sh";
				
			/**
			 *  D02 : ZGUBUN_DPST_OCCR_CHAG    : (월)요금발생
			 *  D03 : ZGUBUN_DPST_OCCR_PRP     : (월)예수금발생
			 *  D04 : ZGUBUN_DPST_OCCR_ANO_PAY : (월)요금,예수금대납
			 *  D08 : ZGUBUN_DPST_OCCR_CHAG    : (월)요금발생(상계)
			 */
			} else if ( ErpCommon.ZGUBUN_DPST_OCCR_CHAG.equals(trmsItem)      || ErpCommon.ZGUBUN_DPST_OCCR_PRP.equals(trmsItem)
					 || ErpCommon.ZGUBUN_DPST_OCCR_ANO_PAY.equals(trmsItem)   || ErpCommon.ZGUBUN_DPST_OCCR_CHAG_A.equals(trmsItem) ){
				shellName = "SACERP08120_001.sh";
				
			//(월)판매수수료 ERP전송
			/** 
			 *  E01 : ZGUBUN_SALE_CMMS            : (월)판매수수료(부가세)
			 *  E02 : ZGUBUN_SALE_CMMS_ADD        : (월)판매수수료(부가세없는 증가)
			 *  E03 : ZGUBUN_SALE_CMMS_SUB        : (월)판매수수료(부가세없는 차감)
			 *  E06 : ZGUBUN_FIX_VIRTL_ACC_CMMS   : (월)확정가상계좌수수료
			 *  E07 : ZGUBUN_FIX_ACC_TRNSF_CMMS   : (월)확정계좌이체수수료
			 *  E08 : ZGUBUN_FIX_CARD_SETTLE_CMMS : (월)확정카드결제수수료
			 *  E09 : ZGUBUN_MC_SALE_CMMS         : (월)M채널 판매수수료(부가세)
			 *  
			 *  B10 : ZGUBUN_FIX_AGRMT_ASTAMT     : (월)확정_약정보조금
			 *  B11 : ZGUBUN_FIX_ALLOT_SALE_AMT   : (월)확정_할부매출
			 */
			} else if ( ErpCommon.ZGUBUN_SALE_CMMS.equals(trmsItem)          || ErpCommon.ZGUBUN_SALE_CMMS_ADD.equals(trmsItem)
					 || ErpCommon.ZGUBUN_SALE_CMMS_SUB.equals(trmsItem)      || ErpCommon.ZGUBUN_FIX_VIRTL_ACC_CMMS.equals(trmsItem)
					 || ErpCommon.ZGUBUN_FIX_ACC_TRNSF_CMMS.equals(trmsItem) || ErpCommon.ZGUBUN_FIX_CARD_SETTLE_CMMS.equals(trmsItem)
					 || ErpCommon.ZGUBUN_MC_SALE_CMMS.equals(trmsItem)       || ErpCommon.ZGUBUN_FIX_AGRMT_ASTAMT.equals(trmsItem)
					 || ErpCommon.ZGUBUN_FIX_ALLOT_SALE_AMT.equals(trmsItem) ){
				shellName = "SACERP08130_001.sh";
				
			//(월)단말기매입 ERP전송
			/**
			 *  A01 : ZGUBUN_EQP_PRCHS_SKN    : (월)단말기매입 - SKN
			 *  A02 : ZGUBUN_EQP_PRCHS_OTHERS : (월)단말기매입 - 타제조업체
			 */
			} else if ( ErpCommon.ZGUBUN_EQP_PRCHS_SKN.equals(trmsItem)          || ErpCommon.ZGUBUN_EQP_PRCHS_OTHERS.equals(trmsItem)){
				shellName = "SACERP08150_001.sh";
				
			//(월)입금정산 ERP전송
			/**
			 *  F12 : ZGUBUN_DPST_ACC_CHAG    : (월)요금입금
			 *  F13 : ZGUBUN_DPST_ACC_PRP     : (월)예수금입금
			 *  F14 : ZGUBUN_DPST_ACC_CASH    : (월)현금매출입금
			 *  F15 : ZGUBUN_DPST_ACC_CARD    : (월)카드입금
			 *  F91 : ZGUBUN_DPST_EXCESS_CHAG : (월)요금과납
			 *  F92 : ZGUBUN_DPST_EXCESS_PRP  : (월)예수금,현금매출과납
			 *  F93 : ZGUBUN_DPST_EXCESS_CARD : (월)카드과납
			 */
			} else if ( ErpCommon.ZGUBUN_DPST_ACC_CHAG.equals(trmsItem)    || ErpCommon.ZGUBUN_DPST_ACC_PRP.equals(trmsItem)
					 || ErpCommon.ZGUBUN_DPST_ACC_CASH.equals(trmsItem)    || ErpCommon.ZGUBUN_DPST_ACC_CARD.equals(trmsItem)
					 || ErpCommon.ZGUBUN_DPST_EXCESS_CHAG.equals(trmsItem) || ErpCommon.ZGUBUN_DPST_EXCESS_PRP.equals(trmsItem)
					 || ErpCommon.ZGUBUN_DPST_EXCESS_CARD.equals(trmsItem) ){
				shellName = "SACERP08160_001.sh";
				
			//(월)매출원가 ERP전송
			/**
			 *  G01 : ZGUBUN_DIS_SALE_AMT : (월)매출원가
			 *  G03 : ZGUBUN_DIS_MOV_OUT  : (월)재고이동 - 출고
			 *  G04 : ZGUBUN_DIS_MOV_IN   : (월)재고이동 - 입고
			 */
			} else if ( ErpCommon.ZGUBUN_DIS_SALE_AMT.equals(trmsItem)      || ErpCommon.ZGUBUN_DIS_MOV_OUT.equals(trmsItem)
					 || ErpCommon.ZGUBUN_DIS_MOV_IN.equals(trmsItem) ){
				shellName = "SACERP08170_001.sh";
				
			} else {
				logMng.writeLogFile("유효하지 않은 전송구분입니다.");
				continue;
			}
			
			command[1] = BATCH_PATH + shellName;
			command[2] = yymm;
			command[3] = agency;
			command[4] = "";
			command[5] = trmsItem;
			
			proc = Runtime.getRuntime().exec(command);
			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while ((line = br.readLine()) != null) {
				logMng.writeLogFile(line);
			}
		}
	}

	/**
	 * (월) 부가세 ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendMmTaxTrmsList(SqlMapClient sqlMap) throws Exception {

		List lstMmTaxTrms = (List)sqlMap.queryForList("SACERP08100.getMmTaxTrmsList");
		if ( lstMmTaxTrms.size() == 0 ) {
			logMng.writeLogFile("(월) TAX전송 건이 존재하지 않습니다.");
			log.debug(PROG_ID + " : (월) TAX전송 건이 존재하지 않습니다.");
			return;
		}
		
		/* 전송여부를 전송중으로 수정 (중복실행방지)  20091110 수정 */
		Map dataMap = new HashMap();
		dataMap.put("remark", "전송진행중");
		sqlMap.update("SACERP08100.updateMmTaxTrms", dataMap);
		sqlMap.commitTransaction();
		
		String[] command = new String[6];
		command[0] = SHELL;

		Process proc = null;
		BufferedReader br = null;
		String line = null;
		String shellName = "";
		String taxCl  = "";

		for (int idx = 0; idx < lstMmTaxTrms.size(); idx++) {
			mErpTrms = (Map)lstMmTaxTrms.get(idx);

			yymm		= (String)mErpTrms.get("YYMM");
			agency		= (String)mErpTrms.get("AGENCY");
			taxCl		= (String)mErpTrms.get("TAX_CL");	//부가세구분
			
			/** 
			 *   부가세 구분 코드
			 *   1 : 부가세(위탁거래)
			 *   2 : 부가세(직영점거래)
			 *   3 : 부가세(SKT장려금,위탁수수료 매출)
			 *   4 : 부가세(단말기 매입)
			 *   5 : 부가세(M채널)
			 *   6 : 부가세(온라인소매거래)
			 *   7 : 부가세(온라인도매거래)
			 *   8 : 부가세(할부채권/보조금)
			 */
			if ( "1".equals(taxCl) || "2".equals(taxCl) || "3".equals(taxCl) || "4".equals(taxCl) 
			  || "5".equals(taxCl) || "6".equals(taxCl) || "7".equals(taxCl) || "8".equals(taxCl)){
				shellName = "SACERP08180_001.sh";
			} else {
				logMng.writeLogFile("유효하지 않은 전송구분입니다.");
				continue;
			}
			
			command[1] = BATCH_PATH + shellName;
			command[2] = yymm;
			command[3] = agency;
			command[4] = "";
			command[5] = taxCl;
			
			proc = Runtime.getRuntime().exec(command);
			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while ((line = br.readLine()) != null) {
				logMng.writeLogFile(line);
			}
		}
	}	
	/**
	 * (월) 전자세금계산서 ERP전송
	 * 
	 *   전자 세금계산서 구분 코드
	 *   01 : 위탁거래 - 매출금액
	 *   02 : 위탁거래 - 수수료
	 *   03 : 직영점 거래금액
	 *   04 : SKT 장려금
	 *   05 : SKT 위탁수수료
	 *   06 : 제조업체 장려금 - skt를 제외한 장려금
	 *   07 : M채널
	 *   08 : 단말기 매입
	 *   09 : 온라인 소매 - 단말기
	 *   10 : 온라인 소매 - 판매수수료(입점수수료)
	 *   11 : 온라인 소매 - 판매수수료(가상계좌)
	 *   12 : 온라인 소매 - 판매수수료(계좌이체)
	 *   13 : 온라인 소매 - 판매수수료(카드결제)
	 *   14 : 온라인 도매 - 단말기
	 *   15 : 온라인 도매 - 판매수수료(입점수수료)
	 *   16 : 할부채권
	 *   17 : 할부채권 이자
	 *   18 : 약정보조금 
	 *   19 : 수정 전자세금계산서
	 * 
	 * @author	이강수 (leekangsu)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	void
	 */
	private void sendMmTaxBillTrmsList(SqlMapClient sqlMap) throws Exception {

		Map mmTaxTrms = (Map)sqlMap.queryForObject("SACERP08100.getMmTaxErpTrmsCnt");
		if ( mmTaxTrms != null ) {
			logMng.writeLogFile("(월) 전자세금계산서 ERP 전송중 입니다.");
			log.debug(PROG_ID + " : (월) 전자세금계산서 ERP 전송중 입니다.");
			return;
		}

		List lstMmTaxTrms = (List)sqlMap.queryForList("SACERP08100.getMmTaxErpTrmsList");
		if ( lstMmTaxTrms.size() == 0 ) {
			logMng.writeLogFile("(월) 전자세금계산서 전송 건이 존재하지 않습니다.");
			log.debug(PROG_ID + " : (월) 전자세금계산서 전송 건이 존재하지 않습니다.");
			return;
		}
		
		/* 전송여부를 전송중으로 수정 (중복실행방지)  20091110 수정 */
		Map dataMap = new HashMap();
		dataMap.put("remark", "전송진행중");
		sqlMap.update("SACERP08100.updateMmTaxErpTrms", dataMap);
		sqlMap.commitTransaction();
		
		String[] command = new String[2];
		command[0] = SHELL;

		Process proc = null;
		BufferedReader br = null;
		String line = null;
		String shellName = "";

		shellName = "SACERP08190_001.sh";
		command[1] = BATCH_PATH + shellName;
			
		proc = Runtime.getRuntime().exec(command);
		br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		while ((line = br.readLine()) != null) {
			logMng.writeLogFile(line);
		}
	}	
}

