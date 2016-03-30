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
 * <li>설 명 : 부가세 전송 </li>
 * <li>작성일 : 2009-04-28</li>
 * </ul>
 *
 * @author 하창주 (hachangjoo)
 */
public class SACERP08180 extends AbsBatchJobBiz {
	private static final String PROG_ID = "SACERP08180";

	private SAP sap = null;

	private List lstAmtInfo			= null;		//금액정보
	private List lstCmmsInfo		= null;		//판매수수료정보
	private List lstAgency			= null;		//대리점목록
	
	private String zbudat	= "";	//귀속일자
	private String userCd	= "";	//대리점 정산담당자 사번
	private String agency	= "";	//대리점 거래처코드
	private String pAgency	= "";	//대리점 거래처코드(shell parameter)
	private String pDealCo	= "";	//거래처코드(shell parameter)
	private String pZgubun	= "";	//전송구분(shell parameter)

	private Map<String, Object> imParams = null;
	private Map<String, Object> imTables = null;
	private Map<String, Object> agencyMap = null;
	private ArrayList alItem = null;

	private Object[] oHead = null;	//Erp vat head table row map array
	private Map mHead = null;		//Erp Head table row map
	private Map mAgency = null;		//대리점 map

	private Map<String, Object> retMap = null;	//전송결과 map
	private Map<String, Object> requestMap = new HashMap<String, Object>();

	/**
	 * 부가세전송 배치 프로그램을 수행한다.
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
				log.debug(request.toString());
			}
			
			//② Transaction 시작
			sqlMap.startTransaction();
			
			//③ 요청 데이터 처리
			logMng.writeLogFile("------------------------------------------------------------");
			String dpstYM = "";
			if( request.size() > 1 ) {
				dpstYM	= (String)request.get("args1");
				pAgency	= (String)request.get("args2");
				pDealCo	= (String)request.get("args3");
				pZgubun	= (String)request.get("args4");

				dpstYM = dpstYM.equals("?") ? "" : dpstYM;
				pAgency = pAgency.equals("?") ? "" : pAgency;
				pDealCo = pDealCo.equals("?") ? "" : pDealCo;
				pZgubun = pZgubun.equals("?") ? "" : pZgubun;

				logMng.writeLogFile("args1[입금년월] : " + dpstYM);
				logMng.writeLogFile("args2[대리점] : " + pAgency);
				logMng.writeLogFile("args3[거래처] : " + pDealCo);
				logMng.writeLogFile("args4[전송구분] : " + pZgubun);
			}
			
			if (dpstYM.equals("")) {
				dpstYM = ErpCommon.getLastMonth();	//입금년월(전월)
			} else if (dpstYM.length() != 6) {
				logMng.writeLogFile("args1[입금년월] : " + dpstYM + " 입력이 올바르지 않습니다.(ex. 200901)");
				log.debug("args1[입금년월] : " + dpstYM + " 입력이 올바르지 않습니다.(ex. 200901)");
				return 1;
			}
			String lastDay = ErpCommon.getLastDay(Integer.parseInt(dpstYM.substring(0, 4)),
					Integer.parseInt(dpstYM.substring(4)) );
			zbudat = dpstYM + lastDay;	//귀속일자

			log.debug("dpstYM(입금년월) : " + dpstYM);
			log.debug("zbudat(귀속일자) : " + zbudat);

			requestMap.put("YYMM",	dpstYM);
			requestMap.put("AGENCY", pAgency);
			requestMap.put("DEAL_CO_CD", pDealCo);
			requestMap.put("TAX_CL", pZgubun);

			/*
			BigDecimal testPrice = new BigDecimal("10");
			BigDecimal testdiv = testPrice.divide(new BigDecimal("1.1"), BigDecimal.ROUND_HALF_UP);
			log.debug("공급가 : " + testdiv.toString());
			log.debug("공급가 : " + testPrice.divide(new BigDecimal("1.1"), 0, BigDecimal.ROUND_HALF_UP).toString());
			log.debug("세액 : " + testPrice.subtract(testdiv).toString());
			*/

			/**
			 *   대리점 정보 조회
			 */
			agencyMap = (Map)sqlMap.queryForObject("SACERP08180.getAgency", requestMap);
			if ( agencyMap == null ) {
				log.debug("대리점이 존재하지 않습니다.");
				logMng.writeLogFile("대리점이 존재하지 않습니다.");
				requestMap.put("REMARK", "대리점이 존재하지 않습니다.");
				sqlMap.update("SACERP08180.updateTsacMmTaxTrms2", requestMap);
				return 1;
			}
			
			//④ DB 처리
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
			if ( "".equals(pZgubun) ) {
				logMng.writeLogFile("부가세(위탁거래) send >>> ");
				sendCnsgDealVat(sqlMap);

				long saveTime = System.currentTimeMillis();
				long currTime = 0;

				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				
				logMng.writeLogFile("부가세(직영점거래) send >>> ");
				sendDirBrDealVat(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;

				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				
				logMng.writeLogFile("부가세(SKT장려금,위탁수수료 매출) send >>> ");
				sendPrMnyCmmsVat(sqlMap);
				
				saveTime = System.currentTimeMillis();
				currTime = 0;

				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}

				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("부가세(M채널) send >>> ");
				sendEqpMcVat(sqlMap);
				
				logMng.writeLogFile("부가세(단말기 매입) send >>> ");
				sendEqpPrchsVat(sqlMap);

				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("부가세(온라인소매거래) send >>> ");
				sendEqpOsVat(sqlMap);

				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("부가세(온라인도매거래) send >>> ");
				sendEqpOaVat(sqlMap);

				while (currTime - saveTime < 1000) {
					currTime = System.currentTimeMillis();
				}
				logMng.writeLogFile("부가세(할부채권/보조금) send >>> ");
				sendBondVat(sqlMap);
				
			} else if ( "1".equals(pZgubun) ) {
				logMng.writeLogFile("부가세(위탁거래) send >>> ");
				sendCnsgDealVat(sqlMap);
				
			} else if (  "2".equals(pZgubun) ) {
				logMng.writeLogFile("부가세(직영점거래) send >>> ");
				sendDirBrDealVat(sqlMap);
				
			} else if (  "3".equals(pZgubun) ) {
				logMng.writeLogFile("부가세(SKT장려금,위탁수수료 매출) send >>> ");
				sendPrMnyCmmsVat(sqlMap);
				
			} else if (  "4".equals(pZgubun) ) {
				logMng.writeLogFile("부가세(단말기 매입) send >>> ");
				sendEqpPrchsVat(sqlMap);
			} else if (  "5".equals(pZgubun) ) {
				logMng.writeLogFile("부가세(M채널) send >>> ");
				sendEqpMcVat(sqlMap);
			} else if (  "6".equals(pZgubun) ) {
				logMng.writeLogFile("부가세(온라인소매거래) send >>> ");
				sendEqpOsVat(sqlMap);
			} else if (  "7".equals(pZgubun) ) {
				logMng.writeLogFile("부가세(온라인도매거래) send >>> ");
				sendEqpOaVat(sqlMap);
			} else if (  "8".equals(pZgubun) ) {
				logMng.writeLogFile("부가세(할부채권/보조금) send >>> ");
				sendBondVat(sqlMap);
			}
			
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();

		} catch(Exception e) {
			logMng.writeLogFile("ERRCODE:1");
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
	 * (월)부가세(위탁거래) ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendCnsgDealVat(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mCnsgDealAmt = null;			//위탁거래금액조회 row map
		BigDecimal saleAmt = null;			//현금매출 + 카드매출
		BigDecimal saleAmt2 = null;			//할부채권이자
		BigDecimal splyPrc = null;			//공급가
		BigDecimal splyPrc2 = null;			//공급가
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAgency = (List)sqlMap.queryForList("SACERP08180.getCnsgDealAgencyList", requestMap);
		if ( lstAgency.size() == 0 ) {
			log.debug("(월)부가세(위탁거래) ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile("(월)부가세(위탁거래) ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)부가세(위탁거래) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08180.updateTsacMmTaxTrms2", requestMap);
			return;
		}

		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			
			requestMap.put("AGENCY", agency);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08180.getCnsgDealSaleAmtList", requestMap);
			lstCmmsInfo = (List)sqlMap.queryForList("SACERP08180.getCnsgDealCmmsAmtList", requestMap);
			if ( lstAmtInfo.size() == 0 && lstCmmsInfo.size() == 0) {
				log.debug(agency + " : (월)부가세(위탁거래) ERP전송 건이 존재하지 않습니다.");
				logMng.writeLogFile(agency + " : (월)부가세(위탁거래) ERP전송 건이 존재하지 않습니다.");
				requestMap.put("REMARK", "(월)부가세(위탁거래) ERP전송 건이 존재하지 않습니다.");
				sqlMap.update("SACERP08180.updateTsacMmTaxTrms2", requestMap);
				continue;
			}

			//set vat header info
			mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
					zbudat, userCd.equals("") ? PROG_ID : userCd, "1");

			alItem = new ArrayList();
			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCnsgDealAmt = (Map)lstAmtInfo.get(i);
				
				saleAmt = (BigDecimal)mCnsgDealAmt.get("SALE_AMT");
				splyPrc = saleAmt.divide(new BigDecimal("1.1"), BigDecimal.ROUND_HALF_UP);
				
				mVatItem = new HashMap<String, Object>();	//부가세 Item row map
				
				mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
				mVatItem.put("MWSKZ",		"15");									//부가가치세 코드
				mVatItem.put("TAXGU",		"IA");									//원천코드 : IA'(매출), 'IV'(매입)
				mVatItem.put("HWBAS",		splyPrc.toString());		//과세 표준 금액(공급가액)
				mVatItem.put("HWSTE",		saleAmt.subtract(splyPrc).toString());	//금액(세액)
				mVatItem.put("ZTOTAMT",		saleAmt.toString());		//합계금액
				mVatItem.put("DEAL_CO_CD",	(String)mCnsgDealAmt.get("AGENCY"));	//대리점
				mVatItem.put("ZTAXCOUNT",	"100");		//세금계산서번호

				log.debug("mVatItem : " + mVatItem.toString());
				//set item info
				alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
				
			}

			for (int j = 0; j < lstCmmsInfo.size(); j++) {
				mCnsgDealAmt = (Map)lstCmmsInfo.get(j);
				
				saleAmt = (BigDecimal)mCnsgDealAmt.get("CNSG_CMMS");
				splyPrc = saleAmt.divide(new BigDecimal("1.1"), BigDecimal.ROUND_HALF_UP);

				mVatItem = new HashMap<String, Object>();	//부가세 Item row map
				
				mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));			//항목번호(k) : 레코드단위
				mVatItem.put("MWSKZ",		"41");										//부가가치세 코드
				mVatItem.put("TAXGU",		"IV");										//원천코드 : IA'(매출), 'IV'(매입)
				mVatItem.put("HWBAS",		splyPrc.toString());						//과세 표준 금액(공급가액)
				mVatItem.put("HWSTE",		saleAmt.subtract(splyPrc).toString());		//금액(세액)
				mVatItem.put("ZTOTAMT",		saleAmt.toString());						//합계금액
				mVatItem.put("NAME1",		(String)mCnsgDealAmt.get("DEAL_CO_NM"));	//이름 1
				mVatItem.put("STCD2",		(String)mCnsgDealAmt.get("BIZ_NUM"));		//사업자등록번호
				mVatItem.put("J_1KFREPRE",	(String)mCnsgDealAmt.get("REP_USER_NM"));	//대표자명
				mVatItem.put("ORT01",		(String)mCnsgDealAmt.get("ADDR"));			//주소
				mVatItem.put("J_1KFTBUS",	(String)mCnsgDealAmt.get("BIZ_CON"));		//업태
				mVatItem.put("J_1KFTIND",	(String)mCnsgDealAmt.get("TYP_OF_BIZ"));	//업종
				mVatItem.put("DEAL_CO_CD",	(String)mCnsgDealAmt.get("ACC_PLC"));		//지급처

				//set item info
				alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
				
			}
			
			log.debug("alItem size : " + alItem.size());

			mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
			oHead = new Object[1];
			oHead[0] = mHead;
			
			imTables.put("T_HEAD",	oHead);
			imTables.put("T_BODY",	alItem.toArray());
			
			retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			//⑤ 결과 데이터 처리
			
			ErpCommon.insertVatTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}

	/**
	 * (월)부가세(직영점거래) ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendDirBrDealVat(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = new HashMap<String, Object>();	//부가세 Item row map
		Map mDirBrDealAmt = null;			//직영점거래금액조회 row map
		BigDecimal saleAmt = null;			//현금매출 + 커드매출
		BigDecimal splyPrc = null;			//공급가
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAgency = (List)sqlMap.queryForList("SACERP08180.getDirBrDealAgencyList", requestMap);
		if ( lstAgency.size() == 0 ) {
			log.debug("(월)부가세(직영점거래) ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile("(월)부가세(직영점거래) ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)부가세(직영점거래) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08180.updateTsacMmTaxTrms2", requestMap);
			return;
		}

		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			
			requestMap.put("AGENCY", agency);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08180.getDirBrDealAmtList", requestMap);
			if ( lstAmtInfo.size() == 0 ) {
				log.debug(agency + " : (월)부가세(직영점거래) ERP전송 건이 존재하지 않습니다.");
				logMng.writeLogFile(agency + " : (월)부가세(직영점거래) ERP전송 건이 존재하지 않습니다.");
				requestMap.put("REMARK", "(월)부가세(직영점거래) ERP전송 건이 존재하지 않습니다.");
				sqlMap.update("SACERP08180.updateTsacMmTaxTrms2", requestMap);
				continue;
			}

			//set vat header info
			mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
					zbudat, userCd.equals("") ? PROG_ID : userCd, "2");

			alItem = new ArrayList();
			
			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mDirBrDealAmt = (Map)lstAmtInfo.get(i);
				
				saleAmt = (BigDecimal)mDirBrDealAmt.get("SALE_AMT");
				splyPrc = saleAmt.divide(new BigDecimal("1.1"), BigDecimal.ROUND_HALF_UP);
				//log.debug("HWBAS : " + splyPrc.toString());
				//log.debug("HWSTE : " + saleAmt.subtract(splyPrc).toString());

				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", mDirBrDealAmt.get("DEAL_CO_CD"));
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mDirBrDealAmt.put("UKEY_SUB_CD", ukeySubCd);
					}
				}
				
				mVatItem.put("ZITEM",		String.valueOf(i+1));						//항목번호(k) : 레코드단위
				mVatItem.put("MWSKZ",		"15");										//부가가치세 코드
				mVatItem.put("ZGCODE",		(String)mDirBrDealAmt.get("UKEY_SUB_CD"));	//직영점코드 : 직영점 u-key code 4자리값
				mVatItem.put("TAXGU",		"IA");										//원천코드 : IA'(매출), 'IV'(매입)
				mVatItem.put("HWBAS",		splyPrc.toString());						//과세 표준 금액(공급가액)
				mVatItem.put("HWSTE",		saleAmt.subtract(splyPrc).toString());		//금액(세액)
				mVatItem.put("ZTOTAMT",		saleAmt.toString());						//합계금액
				mVatItem.put("ZTAXCOUNT",	"50");										//세금계산서번호
				mVatItem.put("DEAL_CO_CD",	(String)mDirBrDealAmt.get("DEAL_CO_CD"));	//거래처(직영점에 해당하는 거래처코드)

				//log.debug("mVatItem : " + mVatItem.toString());
				//set item info
				alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
				
			}
			
			mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
			oHead = new Object[1];
			oHead[0] = mHead;
			
			imTables.put("T_HEAD",	oHead);
			imTables.put("T_BODY",	alItem.toArray());
			
			retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			//⑤ 결과 데이터 처리
			ErpCommon.insertVatTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}
	
	/**
	 * (월)부가세(SKT장려금,위탁수수료 매출) ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendPrMnyCmmsVat(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mrMnyCmmsAmt = null;				//SKT장려금,위탁수수료 매출금액조회 row map
		Map mrPrMnyOthers = null;				//제조업체장려금 row map
		BigDecimal fixSplyPrc = null;			//공급가
		BigDecimal fixVat = null;				//부가세
		BigDecimal fixAmt = null;				//금액
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAmtInfo = (List)sqlMap.queryForList("SACERP08180.getPrMnyCmmsAmtList", requestMap);
		if ( lstAmtInfo.size() == 0 ) {
			log.debug("(월)부가세(SKT장려금,위탁수수료 매출) ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile("(월)부가세(SKT장려금,위탁수수료 매출) ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)부가세(SKT장려금,위탁수수료 매출) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08180.updateTsacMmTaxTrms2", requestMap);
			return;
		}
		List lstPrMnyOthersInfo	= null;		//제조업체장려금정보

		for (int idx = 0; idx < lstAmtInfo.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mrMnyCmmsAmt = (Map)lstAmtInfo.get(idx);

			agency = (String)mrMnyCmmsAmt.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mrMnyCmmsAmt.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			
			//set vat header info
			mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
					zbudat, userCd.equals("") ? PROG_ID : userCd, "3");

			alItem = new ArrayList();
			
			//장려금
			fixSplyPrc		= (BigDecimal)mrMnyCmmsAmt.get("FIX_SPLY_PRC");		// 공급가 */
			fixVat			= (BigDecimal)mrMnyCmmsAmt.get("FIX_VAT");			// 부가세 */
			fixAmt			= (BigDecimal)mrMnyCmmsAmt.get("FIX_AMT");			// 금액 */

			mVatItem = new HashMap<String, Object>();
			mVatItem.put("ZITEM",		String.valueOf(1));			//항목번호(k) : 레코드단위
			mVatItem.put("MWSKZ",		"11");						//부가가치세 코드
			mVatItem.put("TAXGU",		"IA");						//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("HWBAS",		fixSplyPrc.toString());		//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		fixVat.toString());			//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());			//합계금액
			mVatItem.put("NAME1",		(String)mrMnyCmmsAmt.get("DEAL_CO_NM"));	////이름 1
			mVatItem.put("STCD2",		(String)mrMnyCmmsAmt.get("BIZ_NUM"));		//사업자등록번호
			mVatItem.put("J_1KFREPRE",	(String)mrMnyCmmsAmt.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01",		(String)mrMnyCmmsAmt.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS",	(String)mrMnyCmmsAmt.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND",	(String)mrMnyCmmsAmt.get("TYP_OF_BIZ"));	//업종
			mVatItem.put("DEAL_CO_CD",	(String)mrMnyCmmsAmt.get("ACC_PLC"));		//정산처
			mVatItem.put("ZARKTX1",		"장려금");		//품목텍스트

			log.debug("mVatItem : " + mVatItem.toString());
			//set item info
//			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));

			//위탁수수료
			fixSplyPrc		= (BigDecimal)mrMnyCmmsAmt.get("CMMS_FIX_SPLY_PRC");	// 공급가 */
			fixVat			= (BigDecimal)mrMnyCmmsAmt.get("CMMS_FIX_VAT");			// 부가세 */
			fixAmt			= (BigDecimal)mrMnyCmmsAmt.get("CMMS_FIX_AMT");			// 금액 */

			/**
			 *   위탁수수료가 있는 경우 만 처리 한다.
			 */ 
			if(!new BigDecimal("0").equals(fixAmt)){
				mVatItem = new HashMap<String, Object>();
				mVatItem.put("ZITEM",		String.valueOf(2));			//항목번호(k) : 레코드단위
				mVatItem.put("MWSKZ",		"11");						//부가가치세 코드
				mVatItem.put("TAXGU",		"IA");						//원천코드 : IA'(매출), 'IV'(매입)
				mVatItem.put("HWBAS",		fixSplyPrc.toString());		//과세 표준 금액(공급가액)
				mVatItem.put("HWSTE",		fixVat.toString());			//금액(세액)
				mVatItem.put("ZTOTAMT",		fixAmt.toString());			//합계금액
				mVatItem.put("NAME1",		(String)mrMnyCmmsAmt.get("DEAL_CO_NM"));	////이름 1
				mVatItem.put("STCD2",		(String)mrMnyCmmsAmt.get("BIZ_NUM"));		//사업자등록번호
				mVatItem.put("J_1KFREPRE",	(String)mrMnyCmmsAmt.get("REP_USER_NM"));	//대표자명
				mVatItem.put("ORT01",		(String)mrMnyCmmsAmt.get("ADDR"));			//주소
				mVatItem.put("J_1KFTBUS",	(String)mrMnyCmmsAmt.get("BIZ_CON"));		//업태
				mVatItem.put("J_1KFTIND",	(String)mrMnyCmmsAmt.get("TYP_OF_BIZ"));	//업종
				mVatItem.put("DEAL_CO_CD",	(String)mrMnyCmmsAmt.get("ACC_PLC"));		//정산처
				mVatItem.put("ZARKTX1",		"위탁수수료");		//품목텍스트

				log.debug("mVatItem : " + mVatItem.toString());
				//set item info
				alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
			}
			//제조업체장려금
			lstPrMnyOthersInfo = (List)sqlMap.queryForList("SACERP08180.getPrMnyOthersList", requestMap);
			for ( int i = 0; i < lstPrMnyOthersInfo.size() ; i++ ) {
				mrPrMnyOthers = (Map)lstPrMnyOthersInfo.get(i);
				
				//장려금
				fixSplyPrc	= (BigDecimal)mrPrMnyOthers.get("FIX_SPLY_PRC");	// 공급가 */
				fixVat		= (BigDecimal)mrPrMnyOthers.get("FIX_VAT");			// 부가세 */
				fixAmt		= (BigDecimal)mrPrMnyOthers.get("FIX_AMT");			// 금액 */

				mVatItem = new HashMap<String, Object>();
				mVatItem.put("ZITEM",		String.valueOf(i+3));		//항목번호(k) : 레코드단위
				mVatItem.put("MWSKZ",		"11");						//부가가치세 코드
				mVatItem.put("TAXGU",		"IA");						//원천코드 : IA'(매출), 'IV'(매입)
				mVatItem.put("HWBAS",		fixSplyPrc.toString());		//과세 표준 금액(공급가액)
				mVatItem.put("HWSTE",		fixVat.toString());			//금액(세액)
				mVatItem.put("ZTOTAMT",		fixAmt.toString());			//합계금액
				mVatItem.put("NAME1",		(String)mrMnyCmmsAmt.get("DEAL_CO_NM"));	////이름 1
				mVatItem.put("STCD2",		(String)mrMnyCmmsAmt.get("BIZ_NUM"));		//사업자등록번호
				mVatItem.put("J_1KFREPRE",	(String)mrMnyCmmsAmt.get("REP_USER_NM"));	//대표자명
				mVatItem.put("ORT01",		(String)mrMnyCmmsAmt.get("ADDR"));			//주소
				mVatItem.put("J_1KFTBUS",	(String)mrMnyCmmsAmt.get("BIZ_CON"));		//업태
				mVatItem.put("J_1KFTIND",	(String)mrMnyCmmsAmt.get("TYP_OF_BIZ"));	//업종
				mVatItem.put("DEAL_CO_CD",	(String)mrMnyCmmsAmt.get("ACC_PLC"));		//정산처
				mVatItem.put("ZARKTX1",		"장려금");		//품목텍스트

				//set item info
				alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
				
			}

			mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
			oHead = new Object[1];
			oHead[0] = mHead;
			
			imTables.put("T_HEAD",	oHead);
			imTables.put("T_BODY",	alItem.toArray());
			
			retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			//⑤ 결과 데이터 처리
			ErpCommon.insertVatTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}

	/**
	 * (월)부가세(M채널) ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendEqpMcVat(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = new HashMap<String, Object>();	//부가세 Item row map
		Map mDirBrDealAmt = null;			//거래금액조회 row map
		BigDecimal splyPrcAmt	= null;			//공급가
		BigDecimal vat 			= null;			//부가세
		BigDecimal fixAmt		= null;			//금액
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAgency = (List)sqlMap.queryForList("SACERP08180.getEqpMcVatAgencyList", requestMap);
		if ( lstAgency.size() == 0 ) {
			log.debug("(월)부가세(M채널) ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile("(월)부가세(M채널) ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)부가세(M채널) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08180.updateTsacMmTaxTrms2", requestMap);
			return;
		}

		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			
			requestMap.put("AGENCY", agency);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08180.getEqpMcVatList", requestMap);
			if ( lstAmtInfo.size() == 0 ) {
				log.debug(agency + " : (월)부가세(M채널) ERP전송 건이 존재하지 않습니다.");
				logMng.writeLogFile(agency + " : (월)부가세(M채널) ERP전송 건이 존재하지 않습니다.");
				requestMap.put("REMARK", "(월)부가세(M채널) ERP전송 건이 존재하지 않습니다.");
				sqlMap.update("SACERP08180.updateTsacMmTaxTrms2", requestMap);
				continue;
			}

			//set vat header info
			mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
					zbudat, userCd.equals("") ? PROG_ID : userCd, "5");

			alItem = new ArrayList();
			
			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mDirBrDealAmt = (Map)lstAmtInfo.get(i);
				
				splyPrcAmt	= (BigDecimal)mDirBrDealAmt.get("SPLY_PRC_AMT");		// 공급가 */
				vat			= (BigDecimal)mDirBrDealAmt.get("VAT");				// 부가세 */
				fixAmt		= (BigDecimal)mDirBrDealAmt.get("FIX_AMT");			// 금액 */
				
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", mDirBrDealAmt.get("DEAL_CO_CD"));
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mDirBrDealAmt.put("UKEY_SUB_CD", ukeySubCd);
					}
				}
								
				mVatItem.put("ZITEM",		String.valueOf(i+1));						//항목번호(k) : 레코드단위
				mVatItem.put("MWSKZ",		"15");										//부가가치세 코드
				mVatItem.put("ZGCODE",		(String)mDirBrDealAmt.get("UKEY_SUB_CD"));	//직영점코드 : 직영점 u-key code 4자리값
				mVatItem.put("TAXGU",		"IA");										//원천코드 : IA'(매출), 'IV'(매입)

				mVatItem.put("HWBAS",		splyPrcAmt.toString());		//과세 표준 금액(공급가액)
				mVatItem.put("HWSTE",		vat.toString());			//금액(세액)
				mVatItem.put("ZTOTAMT",		fixAmt.toString());			//합계금액

				mVatItem.put("ZTAXCOUNT",	"50");										//세금계산서번호
				mVatItem.put("DEAL_CO_CD",	(String)mDirBrDealAmt.get("DEAL_CO_CD"));	//거래처(직영점에 해당하는 거래처코드)
				

				//log.debug("mVatItem : " + mVatItem.toString());
				//set item info
				alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
				
			}
			
			mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
			oHead = new Object[1];
			oHead[0] = mHead;
			
			imTables.put("T_HEAD",	oHead);
			imTables.put("T_BODY",	alItem.toArray());
			
			retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);

			//⑤ 결과 데이터 처리
			ErpCommon.insertVatTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}
		
	/**
	 * 부가세(단말기 매입) ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendEqpPrchsVat(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mEqpPrchs			= null;			//단말기매입조회 row map
		BigDecimal splyPrcAmt	= null;			//공급가
		BigDecimal vat 			= null;			//부가세
		BigDecimal fixAmt		= null;			//금액
		
		String sknDealCoCd		= "";			//SKN 거래처 코드
		Map mSknInfo			= null;			//Skn거래처정보 map
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAgency = (List)sqlMap.queryForList("SACERP08180.getPrchsDebtSknList", requestMap);
		if ( lstAgency.size() == 0 ) {
			log.debug("부가세(단말기 매입) ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile("부가세(단말기 매입) ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)부가세(단말기 매입) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08180.updateTsacMmTaxTrms2", requestMap);
			return;
		}

		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);

			//set vat header info
			mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
					zbudat, userCd.equals("") ? PROG_ID : userCd, "4");

			alItem = new ArrayList();

			splyPrcAmt	= (BigDecimal)mAgency.get("SPLY_PRC_AMT");		// 공급가 */
			vat			= (BigDecimal)mAgency.get("VAT");				// 부가세 */
			fixAmt		= (BigDecimal)mAgency.get("FIX_AMT");			// 금액 */

			mVatItem = new HashMap<String, Object>();
			mVatItem.put("ZITEM",		String.valueOf(1));			//항목번호(k) : 레코드단위
			mVatItem.put("MWSKZ",		"41");						//부가가치세 코드
			mVatItem.put("TAXGU",		"IV");						//원천코드 : IA'(매출), 'IV'(매입)
			mVatItem.put("HWBAS",		splyPrcAmt.toString());		//과세 표준 금액(공급가액)
			mVatItem.put("HWSTE",		vat.toString());			//금액(세액)
			mVatItem.put("ZTOTAMT",		fixAmt.toString());			//합계금액
			mVatItem.put("NAME1",		(String)mAgency.get("DEAL_CO_NM"));		//이름 1
			mVatItem.put("STCD2",		(String)mAgency.get("BIZ_NUM"));		//사업자등록번호
			mVatItem.put("J_1KFREPRE",	(String)mAgency.get("REP_USER_NM"));	//대표자명
			mVatItem.put("ORT01",		(String)mAgency.get("ADDR"));			//주소
			mVatItem.put("J_1KFTBUS",	(String)mAgency.get("BIZ_CON"));		//업태
			mVatItem.put("J_1KFTIND",	(String)mAgency.get("TYP_OF_BIZ"));		//업종
			mVatItem.put("DEAL_CO_CD",	(String)mAgency.get("PRCHS_PLC"));		//매입처
			mVatItem.put("ZARKTX1",		"단말기매입");							//품목텍스트
			
			//SKN서울지사가 아닌 대리점의 경우 해당 대리점의 지정 SKN지사의 정보로 대체한다.
			sknDealCoCd = ErpCommon.getSknDealCoCd(agency);
			if ( !"10002".equals(sknDealCoCd) ) {
				requestMap.put("DEAL_CO_CD", sknDealCoCd);
				mSknInfo = (Map)sqlMap.queryForObject("SACERP08180.getSknInfo", requestMap);

				mVatItem.put("NAME1",		(String)mSknInfo.get("DEAL_CO_NM"));	//이름 1
				mVatItem.put("STCD2",		(String)mSknInfo.get("BIZ_NUM"));		//사업자등록번호
				mVatItem.put("J_1KFREPRE",	(String)mSknInfo.get("REP_USER_NM"));	//대표자명
				mVatItem.put("ORT01",		(String)mSknInfo.get("ADDR"));			//주소
				mVatItem.put("J_1KFTBUS",	(String)mSknInfo.get("BIZ_CON"));		//업태
				mVatItem.put("J_1KFTIND",	(String)mSknInfo.get("TYP_OF_BIZ"));	//업종
				mVatItem.put("DEAL_CO_CD",	(String)mSknInfo.get("DEAL_CO_CD"));	//매입처
			}

			log.debug("mVatItem : " + mVatItem.toString());
			//set item info
			alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));

			requestMap.put("AGENCY", agency);
			//제조업체 단말기 매입 조회
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08180.getPrchsDebtOthersList", requestMap);

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mEqpPrchs = (Map)lstAmtInfo.get(i);
				
				splyPrcAmt	= (BigDecimal)mEqpPrchs.get("SPLY_PRC_AMT");		// 공급가 */
				vat			= (BigDecimal)mEqpPrchs.get("VAT");				// 부가세 */
				fixAmt		= (BigDecimal)mEqpPrchs.get("FIX_AMT");			// 금액 */

				mVatItem = new HashMap<String, Object>();	//부가세 Item row map
				
				mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
				mVatItem.put("MWSKZ",		"41");									//부가가치세 코드
				mVatItem.put("TAXGU",		"IV");									//원천코드 : IA'(매출), 'IV'(매입)
				mVatItem.put("HWBAS",		splyPrcAmt.toString());					//과세 표준 금액(공급가액)
				mVatItem.put("HWSTE",		vat.toString());						//금액(세액)
				mVatItem.put("ZTOTAMT",		fixAmt.toString());						//합계금액
				mVatItem.put("NAME1",		(String)mEqpPrchs.get("DEAL_CO_NM"));	//이름 1
				mVatItem.put("STCD2",		(String)mEqpPrchs.get("BIZ_NUM"));		//사업자등록번호
				mVatItem.put("J_1KFREPRE",	(String)mEqpPrchs.get("REP_USER_NM"));	//대표자명
				mVatItem.put("ORT01",		(String)mEqpPrchs.get("ADDR"));			//주소
				mVatItem.put("J_1KFTBUS",	(String)mEqpPrchs.get("BIZ_CON"));		//업태
				mVatItem.put("J_1KFTIND",	(String)mEqpPrchs.get("TYP_OF_BIZ"));	//업종
				mVatItem.put("DEAL_CO_CD",	(String)mEqpPrchs.get("PRCHS_PLC"));	//매입처
				mVatItem.put("ZARKTX1",		"단말기매입");							//품목텍스트

				//set item info
				alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
				
			}
			
			log.debug("alItem size : " + alItem.size());

			mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
			oHead = new Object[1];
			oHead[0] = mHead;
			
			imTables.put("T_HEAD",	oHead);
			imTables.put("T_BODY",	alItem.toArray());
			
			retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			//⑤ 결과 데이터 처리
			
			ErpCommon.insertVatTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}

	/**
	 * 부가세(온라인소매거래) ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendEqpOsVat(SqlMapClient sqlMap) throws Exception {

		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mEqpPrchs			= null;			//단말기매입조회 row map
		BigDecimal splyPrcAmt	= null;			//공급가
		BigDecimal vat 			= null;			//부가세
		BigDecimal fixAmt		= null;			//금액
		
		String sknDealCoCd		= "";			//SKN 거래처 코드
		String type             = null;
		Map mSknInfo			= null;			//Skn거래처정보 map
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAgency = (List)sqlMap.queryForList("SACERP08180.getEqpOsVatAgencyList", requestMap);
		if ( lstAgency.size() == 0 ) {
			log.debug("부가세(온라인소매거래) ERP전송 건이 존재하지 않습니다.1");
			logMng.writeLogFile("부가세(온라인소매거래) ERP전송 건이 존재하지 않습니다.1");
			requestMap.put("REMARK", "(월)부가세(온라인소매거래) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08180.updateTsacMmTaxTrms2", requestMap);
			return;
		}

		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);

			//set vat header info
			mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
					zbudat, userCd.equals("") ? PROG_ID : userCd, "6");

			alItem = new ArrayList();

			requestMap.put("AGENCY", agency);
			//제조업체 단말기 매입 조회
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08180.getEqpOsVatList", requestMap);

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mEqpPrchs = (Map)lstAmtInfo.get(i);
				mEqpPrchs.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	//거래처 코드

				splyPrcAmt	= (BigDecimal)mEqpPrchs.get("SPLY_PRC_AMT");		// 공급가 */
				vat			= (BigDecimal)mEqpPrchs.get("VAT");				// 부가세 */
				fixAmt		= (BigDecimal)mEqpPrchs.get("FIX_AMT");			// 금액 */
				type		= (String)mEqpPrchs.get("TYPE");			        // 부가세유형 */

				mVatItem = new HashMap<String, Object>();	//부가세 Item row map
				
				mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
				mVatItem.put("HWBAS",		splyPrcAmt.toString());					//과세 표준 금액(공급가액)
				mVatItem.put("HWSTE",		vat.toString());						//금액(세액)
				mVatItem.put("ZTOTAMT",		fixAmt.toString());						//합계금액
				mVatItem.put("DEAL_CO_CD",  (String)mEqpPrchs.get("DEAL_CO_CD"));	//거래처 코드
				mVatItem.put("NAME1",		(String)mEqpPrchs.get("DEAL_CO_NM"));	//이름 1
				mVatItem.put("STCD2",		(String)mEqpPrchs.get("BIZ_NUM"));		//사업자등록번호
				mVatItem.put("J_1KFREPRE",	(String)mEqpPrchs.get("REP_USER_NM"));	//대표자명
				mVatItem.put("ORT01",		(String)mEqpPrchs.get("ADDR"));			//주소
				mVatItem.put("J_1KFTBUS",	(String)mEqpPrchs.get("BIZ_CON"));		//업태
				mVatItem.put("J_1KFTIND",	(String)mEqpPrchs.get("TYP_OF_BIZ"));	//업종
				
				/**
				 *   부가세 구분
				 *   1 : 단말기
				 *   2 : 판매수수료(입점수수료)
				 *   3 : 판매수수료(가상계좌)
				 *   4 : 판매수수료(계좌이체)
				 *   5 : 판매수수료(카드결제)
				 */
				if("1".equals(type)){
					mVatItem.put("MWSKZ",		"15");						//부가가치세 코드
					mVatItem.put("TAXGU",		"IA");						//원천코드 : IA'(매출), 'IV'(매입)
					mVatItem.put("ZARKTX1",		"단말기");					//품목텍스트
				}else if("2".equals(type)){
					mVatItem.put("MWSKZ",		"41");						//부가가치세 코드
					mVatItem.put("TAXGU",		"IV");						//원천코드 : IA'(매출), 'IV'(매입)
					mVatItem.put("ZARKTX1",		"판매수수료(입점수수료)");		//품목텍스트
				}else if("3".equals(type)){
					mVatItem.put("MWSKZ",		"41");						//부가가치세 코드
					mVatItem.put("TAXGU",		"IV");						//원천코드 : IA'(매출), 'IV'(매입)
					mVatItem.put("ZARKTX1",		"판매수수료(가상계좌)");		//품목텍스트
				}else if("4".equals(type)){
					mVatItem.put("MWSKZ",		"41");						//부가가치세 코드
					mVatItem.put("TAXGU",		"IV");						//원천코드 : IA'(매출), 'IV'(매입)
					mVatItem.put("ZARKTX1",		"판매수수료(계좌이체)");		//품목텍스트
				}else if("5".equals(type)){
					mVatItem.put("MWSKZ",		"41");						//부가가치세 코드
					mVatItem.put("TAXGU",		"IV");						//원천코드 : IA'(매출), 'IV'(매입)
					mVatItem.put("ZARKTX1",		"판매수수료(카드결제)");		//품목텍스트
				}

				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", mEqpPrchs.get("DEAL_CO_CD"));
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mEqpPrchs.put("UKEY_SUB_CD", ukeySubCd);
						mEqpPrchs.put("ZGCODE", ukeySubCd);
						mVatItem.put("ZGCODE",	ukeySubCd);		//품목텍스트
					}
				}

				//set item info
				alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
				
			}
			
			log.debug("alItem size : " + alItem.size());

			mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
			oHead = new Object[1];
			oHead[0] = mHead;
			
			imTables.put("T_HEAD",	oHead);
			imTables.put("T_BODY",	alItem.toArray());
			
			retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			//⑤ 결과 데이터 처리
			
			ErpCommon.insertVatTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}		
	}
	
	/**
	 * 부가세(온라인도매거래) ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendEqpOaVat(SqlMapClient sqlMap) throws Exception {

		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mEqpPrchs			= null;			//단말기매입조회 row map
		BigDecimal splyPrcAmt	= null;			//공급가
		BigDecimal vat 			= null;			//부가세
		BigDecimal fixAmt		= null;			//금액
		
		String sknDealCoCd		= "";			//SKN 거래처 코드
		String type             = null;
		Map mSknInfo			= null;			//Skn거래처정보 map
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAgency = (List)sqlMap.queryForList("SACERP08180.getEqpOaVatAgencyList", requestMap);
		if ( lstAgency.size() == 0 ) {
			log.debug("부가세(온라인도매거래) ERP전송 건이 존재하지 않습니다.1");
			logMng.writeLogFile("부가세(온라인도매거래) ERP전송 건이 존재하지 않습니다.1");
			requestMap.put("REMARK", "부가세(온라인도매거래) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08180.updateTsacMmTaxTrms2", requestMap);
			return;
		}

		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);

			//set vat header info
			mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
					zbudat, userCd.equals("") ? PROG_ID : userCd, "7");

			alItem = new ArrayList();

			requestMap.put("AGENCY", agency);
			//제조업체 단말기 매입 조회
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08180.getEqpOaVatList", requestMap);

			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mEqpPrchs = (Map)lstAmtInfo.get(i);
				mEqpPrchs.put("DEAL_CO_CD",  (String)mEqpPrchs.get("ACC_PLC"));	//거래처 코드
				
				splyPrcAmt	= (BigDecimal)mEqpPrchs.get("SPLY_PRC_AMT");	// 공급가 */
				vat			= (BigDecimal)mEqpPrchs.get("VAT");				// 부가세 */
				fixAmt		= (BigDecimal)mEqpPrchs.get("FIX_AMT");			// 금액 */
				type		= (String)mEqpPrchs.get("TYPE");			    // 부가세유형 */

				mVatItem = new HashMap<String, Object>();	//부가세 Item row map
				
				mVatItem.put("ZITEM",		String.valueOf(alItem.size()+1));		//항목번호(k) : 레코드단위
				mVatItem.put("HWBAS",		splyPrcAmt.toString());					//과세 표준 금액(공급가액)
				mVatItem.put("HWSTE",		vat.toString());						//금액(세액)
				mVatItem.put("ZTOTAMT",		fixAmt.toString());						//합계금액
				mVatItem.put("DEAL_CO_CD",  (String)mEqpPrchs.get("DEAL_CO_CD"));	//거래처 코드
				mVatItem.put("NAME1",		(String)mEqpPrchs.get("DEAL_CO_NM"));	//이름 1
				mVatItem.put("STCD2",		(String)mEqpPrchs.get("BIZ_NUM"));		//사업자등록번호
				mVatItem.put("J_1KFREPRE",	(String)mEqpPrchs.get("REP_USER_NM"));	//대표자명
				mVatItem.put("ORT01",		(String)mEqpPrchs.get("ADDR"));			//주소
				mVatItem.put("J_1KFTBUS",	(String)mEqpPrchs.get("BIZ_CON"));		//업태
				mVatItem.put("J_1KFTIND",	(String)mEqpPrchs.get("TYP_OF_BIZ"));	//업종

				/**
				 *   부가세 구분
				 *   1 : 단말기
				 *   2 : 판매수수료(입점수수료)
				 *   3 : 판매수수료(가상계좌)
				 *   4 : 판매수수료(계좌이체)
				 *   5 : 판매수수료(카드결제)
				 */
				if("1".equals(type)){
					mVatItem.put("MWSKZ",		"15");				//부가가치세 코드
					mVatItem.put("TAXGU",		"IA");				//원천코드 : IA'(매출), 'IV'(매입)
					mVatItem.put("ZARKTX1",		"단말기");			//품목텍스트
				}else if("2".equals(type)){
					mVatItem.put("MWSKZ",		"41");				//부가가치세 코드
					mVatItem.put("TAXGU",		"IV");				//원천코드 : IA'(매출), 'IV'(매입)
					mVatItem.put("ZARKTX1",		"판매수수료");		//품목텍스트
				}
				
				/**
				 *    현대백화점 서브점코드  조회
				 *    ZSAC_C_00110 : 현대백화점 서브점코드
				 *    ukey_sub_cd : 서브점코드
				 */
				ukeySubCdMap     = new HashMap();
				ukeySubCdMap.put("deal_co_cd", mEqpPrchs.get("DEAL_CO_CD"));
				ukeySubCdMap     = (Map) sqlMap.queryForObject("SACERP08100.getUkeySubCode", ukeySubCdMap);
				if(ukeySubCdMap != null){
					ukeySubCd = (String) ukeySubCdMap.get("UKEY_SUB_CD");
					if(!"0000".equals(ukeySubCd)){
						mEqpPrchs.put("UKEY_SUB_CD", ukeySubCd);
						mEqpPrchs.put("ZGCODE", ukeySubCd);
						mVatItem.put("ZGCODE",	ukeySubCd);		//품목텍스트
					}
				}				
				//set item info
				alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
				
			}
			
			log.debug("alItem size : " + alItem.size());

			mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
			oHead = new Object[1];
			oHead[0] = mHead;
			
			imTables.put("T_HEAD",	oHead);
			imTables.put("T_BODY",	alItem.toArray());
			
			retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			//⑤ 결과 데이터 처리
			
			ErpCommon.insertVatTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}

	/**
	 * (월)부가세(할부채권/보조금) ERP전송
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap	: SqlMapClient 인스턴스(DB 정보)
	 * @return	void
	 */
	private void sendBondVat(SqlMapClient sqlMap) throws Exception {
		
		Map<String, Object> mVatItem = null;	//부가세 Item row map
		Map mCnsgDealAmt = null;			//위탁거래금액조회 row map
		BigDecimal saleAmt = null;			//현금매출 + 카드매출
		BigDecimal saleAmt2 = null;			//할부채권이자
		BigDecimal agrmtAmt = null;			//보조금
		BigDecimal splyPrc = null;			//공급가
		BigDecimal splyPrc2 = null;			//공급가
		int num = 0;
		Map ukeySubCdMap = null;
		String ukeySubCd = "";

		lstAgency = (List)sqlMap.queryForList("SACERP08180.getBondAgencyList", requestMap);
		if ( lstAgency.size() == 0 ) {
			log.debug("(월)부가세(할부채권/보조금) ERP전송 건이 존재하지 않습니다.");
			logMng.writeLogFile("(월)부가세(할부채권/보조금) ERP전송 건이 존재하지 않습니다.");
			requestMap.put("REMARK", "(월)부가세(할부채권/보조금) ERP전송 건이 존재하지 않습니다.");
			sqlMap.update("SACERP08180.updateTsacMmTaxTrms2", requestMap);
			return;
		}

		for (int idx = 0; idx < lstAgency.size(); idx++) {
			imParams = new HashMap<String, Object>();
			imTables = new HashMap<String, Object>();

			mAgency = (Map)lstAgency.get(idx);

			agency = (String)mAgency.get("AGENCY");
			userCd = ErpCommon.nullToSpace((String)mAgency.get("USER_CD"));
			userCd = ErpCommon.fillZeroFront(userCd, 8);
			
			requestMap.put("AGENCY", agency);
			lstAmtInfo = (List)sqlMap.queryForList("SACERP08180.getBondAmtList", requestMap);
			if ( lstAmtInfo.size() == 0 ) {
				log.debug(agency + " : (월)부가세(할부채권/보조금) ERP전송 건이 존재하지 않습니다.");
				logMng.writeLogFile(agency + " : (월)부가세(할부채권/보조금) ERP전송 건이 존재하지 않습니다.");
				requestMap.put("REMARK", "(월)부가세(할부채권/보조금) ERP전송 건이 존재하지 않습니다.");
				sqlMap.update("SACERP08180.updateTsacMmTaxTrms2", requestMap);
				continue;
			}

			//set vat header info
			mHead = ErpCommon.makeVatHead((String)agencyMap.get("UKEY_AGENCY_CD"),
					zbudat, userCd.equals("") ? PROG_ID : userCd, "8");

			alItem = new ArrayList();
			for (int i = 0; i < lstAmtInfo.size(); i++) {
				mCnsgDealAmt = (Map)lstAmtInfo.get(i);
				
				num = 0;
				saleAmt = (BigDecimal)mCnsgDealAmt.get("SALE_AMT");
				saleAmt2 = (BigDecimal)mCnsgDealAmt.get("ALLOT_AMT");
				agrmtAmt = (BigDecimal)mCnsgDealAmt.get("AGRMT_AMT");
				
				/**
				 *   할부채권
				 */
				if(!new BigDecimal("0").equals(saleAmt)){
					num++;
					splyPrc = saleAmt.divide(new BigDecimal("1.1"), BigDecimal.ROUND_HALF_UP);
					
					mVatItem = new HashMap<String, Object>();	//부가세 Item row map
					
					mVatItem.put("ZITEM",		String.valueOf(num));		//항목번호(k) : 레코드단위
					mVatItem.put("MWSKZ",		"15");									//부가가치세 코드
					mVatItem.put("TAXGU",		"IA");									//원천코드 : IA'(매출), 'IV'(매입)
					mVatItem.put("HWBAS",		splyPrc.toString());		//과세 표준 금액(공급가액)
					mVatItem.put("HWSTE",		saleAmt.subtract(splyPrc).toString());	//금액(세액)
					mVatItem.put("ZTOTAMT",		saleAmt.toString());		//합계금액
					mVatItem.put("DEAL_CO_CD",	(String)mCnsgDealAmt.get("AGENCY"));	//대리점
					mVatItem.put("ZTAXCOUNT",	"100");		        //세금계산서번호
					mVatItem.put("ZARKTX1",		"1.할부채권");			//품목텍스트

					log.debug("mVatItem : " + mVatItem.toString());
					//set item info
					alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
				}

				/**
				 *   할부채권 이자
				 */
				if(!new BigDecimal("0").equals(saleAmt2)){
					num++;
					splyPrc2 = saleAmt2.multiply(new BigDecimal("10"));
					
					mVatItem = new HashMap<String, Object>();	//부가세 Item row map
					
					mVatItem.put("ZITEM",		String.valueOf(num));		//항목번호(k) : 레코드단위
					mVatItem.put("MWSKZ",		"15");									//부가가치세 코드
					mVatItem.put("TAXGU",		"IA");									//원천코드 : IA'(매출), 'IV'(매입)
					mVatItem.put("HWBAS",		splyPrc2.toString());		//과세 표준 금액(공급가액)
					mVatItem.put("HWSTE",		saleAmt2.toString());	    //금액(세액)
					mVatItem.put("ZTOTAMT",		splyPrc2.add(saleAmt2).toString());		//합계금액
					mVatItem.put("DEAL_CO_CD",	(String)mCnsgDealAmt.get("AGENCY"));	//대리점
					mVatItem.put("ZTAXCOUNT",	"100");		        //세금계산서번호
					mVatItem.put("ZARKTX1",		"2.할부채권이자");			//품목텍스트

					log.debug("mVatItem : " + mVatItem.toString());
					//set item info
					alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
				}

				/**
				 *   보조금
				 */
				if(!new BigDecimal("0").equals(agrmtAmt)){
					num++;
					splyPrc = agrmtAmt.divide(new BigDecimal("1.1"), BigDecimal.ROUND_HALF_UP);
					
					mVatItem = new HashMap<String, Object>();	//부가세 Item row map
					
					mVatItem.put("ZITEM",		String.valueOf(num));		//항목번호(k) : 레코드단위
					mVatItem.put("MWSKZ",		"15");									//부가가치세 코드
					mVatItem.put("TAXGU",		"IA");									//원천코드 : IA'(매출), 'IV'(매입)
					mVatItem.put("HWBAS",		splyPrc.toString());		//과세 표준 금액(공급가액)
					mVatItem.put("HWSTE",		agrmtAmt.subtract(splyPrc).toString());	    //금액(세액)
					mVatItem.put("ZTOTAMT",		agrmtAmt.toString());		//합계금액
					mVatItem.put("DEAL_CO_CD",	(String)mCnsgDealAmt.get("AGENCY"));	//대리점
					mVatItem.put("ZTAXCOUNT",	"100");		        //세금계산서번호
					mVatItem.put("ZARKTX1",		"3.보조금");			//품목텍스트

					log.debug("mVatItem : " + mVatItem.toString());
					//set item info
					alItem.add(ErpCommon.makeVatItem(mHead, mVatItem));
				}
				
			}
			
			log.debug("alItem size : " + alItem.size());

			mHead.put("ZCNT", String.valueOf(alItem.size()));	//Item Record 건수
			oHead = new Object[1];
			oHead[0] = mHead;
			
			imTables.put("T_HEAD",	oHead);
			imTables.put("T_BODY",	alItem.toArray());
			
			retMap = sap.executeRFC("ZFI_SALES_ADDEDTAX_IN", imParams, imTables);
			ErpCommon.writeLogResultMsg(retMap, logMng);
			//⑤ 결과 데이터 처리

//logMng.writeLogFile("retMap : " + retMap);
			
			ErpCommon.insertVatTrTable(sqlMap, imTables, retMap);
			//⑥-1 Transaction Commit
			sqlMap.commitTransaction();
		}
	}	
}
