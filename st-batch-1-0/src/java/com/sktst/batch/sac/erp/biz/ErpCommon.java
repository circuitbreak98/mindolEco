package com.sktst.batch.sac.erp.biz;

import org.apache.commons.logging.Log;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.log.LogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Locale;

import java.text.SimpleDateFormat;
import java.text.ParsePosition;


/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/판매회계</li>
 * <li>설 명 : ERP 전송 공통업무 </li>
 * <li>작성일 : 2009-04-28</li>
 * </ul>
 *
 * @author 하창주 (hachangjoo)
 */
public class ErpCommon {

	private static Log log = LogManager.getLog();

	private static final int HEAD_CNT	= 1;

	//단말기매입 전송구분코드
	public static final String ZGUBUN_EQP_PRCHS_SKN			= "A01";	//(월)단말기매입 - SKN
	public static final String ZGUBUN_EQP_PRCHS_OTHERS		= "A02";	//(월)단말기매입 - 타제조업체
	public static final String ZGUBUN_ACCESSARY_EQP_PRCHS	= "A03";	//(월)악세사리 상품매입

	//매입 전송구분코드
	public static final String ZGUBUN_CASH_SALE				= "B01";	//(월)현금매출발생
	public static final String ZGUBUN_CARD_SALE				= "B02";	//(월)카드매출발생
	public static final String ZGUBUN_ALLOT_SALE			= "B03";	//(월)할부매출발생
	public static final String ZGUBUN_ALLOT_INT				= "B04";	//(월)할부채권이자발생
	public static final String ZGUBUN_ASTAMT_SALE			= "B05";	//(월)보조금매출
	public static final String ZGUBUN_PR_MNY_SALE_SKT		= "B06";	//(월)장려금매출 - SKT
	public static final String ZGUBUN_PR_MNY_SALE_OTHERS	= "B07";	//(월)장려금매출 - 타제조업체
	public static final String ZGUBUN_CMMS_SALE_SKT			= "B08";	//(월)위탁수수료매출 - SKT
	public static final String ZGUBUN_FIX_AGRMT_ASTAMT		= "B10";	//(월)확정_약정보조금
	public static final String ZGUBUN_FIX_ALLOT_SALE_AMT	= "B11";	//(월)확정_할부매출

	public static final String ZGUBUN_ALLOT_MTCH			= "C01";	//(월)할부채권 1, 2차 상계

	//발생 전송구분코드
	public static final String ZGUBUN_DPST_OCCR_CHAG		= "D02";	//(월)요금발생
	public static final String ZGUBUN_DPST_OCCR_PRP			= "D03";	//(월)예수금발생
	public static final String ZGUBUN_DPST_OCCR_ANO_PAY		= "D04";	//(월)요금,예수금대납
	public static final String ZGUBUN_DPST_OCCR_CHAG_A		= "D08";	//(월)요금발생A

	//판매수수료  전송구분코드
	public static final String ZGUBUN_SALE_CMMS				= "E01";	//(월)판매수수료(부가세)
	public static final String ZGUBUN_SALE_CMMS_ADD			= "E02";	//(월)판매수수료(부가세없는 증가)
	public static final String ZGUBUN_SALE_CMMS_SUB			= "E03";	//(월)판매수수료(부가세없는 차감)
	public static final String ZGUBUN_FIX_VIRTL_ACC_CMMS	= "E06";	//(월)확정가상계좌수수료
	public static final String ZGUBUN_FIX_ACC_TRNSF_CMMS	= "E07";	//(월)확정계좌이체수수료
	public static final String ZGUBUN_FIX_CARD_SETTLE_CMMS	= "E08";	//(월)확정카드결제수수료
	public static final String ZGUBUN_MC_SALE_CMMS			= "E09";	//(월)M채널 판매수수료(부가세)
	
	//입금처리 전송구분코드
	public static final String ZGUBUN_DPST_PROC_SKT_CHAG	= "F01";	//(일)요금입금
	public static final String ZGUBUN_DPST_PROC_SKT_PRP		= "F02";	//(일)예수금,현금매출입금
	public static final String ZGUBUN_DPST_PROC_CASA_AMT	= "F03";	//(일)현금매출입금
	public static final String ZGUBUN_DPST_PROC_CARD		= "F05";	//(일)입금(카드사)

	public static final String ZGUBUN_DPST_PROC_OCCR		= "F18";	//(일)예수금발생 sendDpstProcOccr
	public static final String ZGUBUN_ACCESSARY_CASH		= "F20";	//(일)현금매출발생
	public static final String ZGUBUN_ACCESSARY_CARD		= "F21";	//(일)카드매출발생
	public static final String ZGUBUN_ACCESSARY_COST		= "F22";	//(일)매출원가
	
	//입금적용 전송구분코드
	public static final String ZGUBUN_DPST_ACC_CHAG			= "F12";	//(월)요금입금
	public static final String ZGUBUN_DPST_ACC_PRP			= "F13";	//(월)예수금입금
	public static final String ZGUBUN_DPST_ACC_CASH			= "F14";	//(월)현금매출입금
	public static final String ZGUBUN_DPST_ACC_CARD			= "F15";	//(월)카드입금

	//선입금 전송구분코드
	public static final String ZGUBUN_SKT_AHEAD_DPST		= "F16";	//(월)SKT선입금
	
	//입금과납 전송구분코드
	public static final String ZGUBUN_DPST_EXCESS_CHAG		= "F91";	//(월)요금과납
	public static final String ZGUBUN_DPST_EXCESS_PRP		= "F92";	//(월)예수금,현금매출과납
	public static final String ZGUBUN_DPST_EXCESS_CARD		= "F93";	//(월)카드과납
	
	//매출원가 전송구분코드
	public static final String ZGUBUN_DIS_SALE_AMT			= "G01";	//(월)매출원가
	public static final String ZGUBUN_DIS_MOV_OUT			= "G03";	//(월)재고이동 - 출고
	public static final String ZGUBUN_DIS_MOV_IN			= "G04";	//(월)재고이동 - 입고

	//부가세 전송구분
	public static final String ZGUBUN_VAT		= "X01";	//(월)부가세
	
	private static final String YES	= "Y";	//YN Yes
	private static final String NO	= "N";	//YN N

	//발생유형(ZSAC_C_00070)
	private static final String OCCU_TYP_A	= "A";		//매입
	private static final String OCCU_TYP_B	= "B";		//매츨
	private static final String OCCU_TYP_D	= "D";		//수납대행
	private static final String OCCU_TYP_E	= "E";		//판매수수료
	private static final String OCCU_TYP_F	= "F";		//입금

	//ERP 전송 항목
	private static final String MANDT 		= "500";	//클라이언트(500:운영, 501:개발테스트)
	private static final String BUKRS 		= "1000";	//회사코드
	private static final String ZPERIOD_D	= "D";		//주기(일)
	private static final String ZPERIOD_M	= "M";		//주기(월)

	/**
	 * 현재 날짜를 포맷된 형태로 추출해낸다.
	 * @author	하창주 (hachangjoo)
	 * @param	String format
	 * @return	포맷된 날짜의 문자열값
	 */
	public static String getDate(String format){
		Date d =new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}

	/**
	 * 현재 날짜를 yyyymmdd까지의 형태로 추출해낸다.
	 * @author	하창주 (hachangjoo)
	 * @param	void
	 * @return	String : yyyymmdd형태로 변경되된 문자열값
	 */
	public static String getDate(){
		return getDate("yyyyMMdd");
	}

	/**
	 * 현재 날짜를 입력받은 포맷의 형태로 변환하여 결과값을 리턴하도로 한다. 
	 * 예) format : yyyyMMddHHmmss --> 20031130124130 로 결과값 반환
	 * @author	하창주 (hachangjoo)
     * @param	String format : 변환하고자 하는 문자열
     * @return	String : 널이 아닌 문자열 
	 */
	public static String getTime(String format) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}
	
	/**
	 * 현재 날짜를 HHmmss 형태로 값을 얻어낸다.
	 * @author	하창주 (hachangjoo)
	 * @param	void
	 * @return	HHmmss 의 형태로 바뀐 현재 시간값
	 */
	public static String getTime() {
		return getTime("HHmmss");
	}

	/**
     * 데이타가 널값일 경우 공백문자로 반환
	 * @author	하창주 (hachangjoo)
     * @param	String 변환하고자 하는 문자열
     * @return	String 널이 아닌 문자열 
     */
	public static String nullToSpace(String str) {
		if (str == null) str = "";
		return str;
	}
	
	/**
	 * GMT기준시간중의 한국표준시를 반환한다. 
	 * @author	하창주 (hachangjoo)
	 * @param	void
	 * @return GMT+09:00형태의 대한민국표준시
	 */
	public static Calendar getCalendar(){
		Calendar calendar =
		new GregorianCalendar(
			TimeZone.getTimeZone("GMT+09:00"),Locale.KOREA);
		calendar.setTime (new Date());

		return calendar;
	}
	
	/**
	 * GMT기준시간중의 한국표준시를 반환한다. 
	 * @author	하창주 (hachangjoo)
	 * @param	String dateString : Date형태로 만들게 될 yyyyMMdd 형태의 문자열
	 * @return	GMT+09:00형태의 대한민국표준시
	 */
	public static Calendar getCalendar(String dateString){
		Calendar calendar =
        new GregorianCalendar(
            TimeZone.getTimeZone("GMT+09:00"),Locale.KOREA);
    	calendar.setTime(stringToDate(dateString, "yyyyMMdd"));

    	return calendar;
	}
	
	/**
	 * 문자열 데이터를 사용자형태의 Date형태의 객체로 바꾸어준다
	 * String -> Date (20000925)
	 * @author	하창주 (hachangjoo)
	 * @param	String s : Date형태로 만들게 될 yyyyMMdd 형태의 문자열
	 * @return	yyyymmdd형태로 변경되어진 Date객체
	 */
	public static java.util.Date stringToDate(String s, String format){
		java.util.Date d = null;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			d = sdf.parse(s, new ParsePosition(0) );
		}catch(Exception e){throw new RuntimeException("Date format not valid.");}
		return d;
	}

	/**
	 * 오늘을 기준으로 입력받은 숫자만큼의 차이가 나는 날짜를 알아낸다
	 * @author	하창주 (hachangjoo)
	 * @param int distance 날짜 차이값
	 * @return yyyy-mm-dd형태로 변경되된 문자열값
	 */
	public static String getDayInterval(int distance){
		return getDayInterval(distance, "yyyyMMdd");
	}
	/**
	 * 오늘을 기준으로 입력받은 숫자만큼의 차이가 나는 날짜를 알아낸다
	 * @author	하창주 (hachangjoo)
	 * @param String format 날짜 포맷
	 * @param int distance 날짜 차이
	 * @return yyyy-mm-dd형태로 변경되된 문자열값
	 */
	public static String getDayInterval(int distance, String format){
		Calendar cal = getCalendar();
		//roll에서 add로 변경(년월까지 반영)
		cal.add(Calendar.DATE , distance);
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}

	/**
	 * 입력받은 날자의 날짜를 기준으로 해당일의 과거나 미래일을 알아낸다
	 * @author	하창주 (hachangjoo)
	 * @param String dateString 날짜
	 * @param int distance 날짜 차이
	 * @return format형태로 변경되된 문자열값
	 */
	public static String getDayInterval(String dateString, int distance){
		return getDayInterval(dateString, distance, "yyyyMMdd");
	}
	
	/**
	 * 입력받은 날자의 날짜를 기준으로 해당일의 과거나 미래일을 알아낸다
	 * @author	하창주 (hachangjoo)
	 * @param String dateString 날짜
	 * @param String format 날짜 포맷
	 * @param int distance 날짜 차이
	 * @return format형태로 변경되된 문자열값
	 */
	public static String getDayInterval(String dateString, int distance, String format){
		Calendar cal = getCalendar(dateString);
		//roll에서 add로 변경(년월까지 반영)
		cal.add(Calendar.DATE , distance);
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}

	/**
	 * 입력된 년월을 기준으로 해당월의 마지막 일자를 가져온다.
	 * @author	하창주 (hachangjoo)
	 * @param	void
	 * @return	dd 포맷의 해당월 마지막 일. 
	 */
	public static String getLastDay(int iYear, int iMonth){

		Calendar cal  = Calendar.getInstance();
		cal.set(iYear,iMonth,1);
		cal.add(Calendar.DATE, -1); //해당월의 마지막 날짜 
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		return sdf.format(d);
		 
	}

	/**
	 * 당월의 전월을 알아낸다.
	 * @author	하창주 (hachangjoo)
	 * @param	void
	 * @return	yyyyMM형태로 변경되된 문자열값
	 */
	public static String getLastMonth(){
		Calendar cal = getCalendar();
		cal.add(Calendar.YEAR, 0);
		cal.add(Calendar.MONTH, -1);
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		return sdf.format(d);
	}
	
	/**
	 * 분개기준정보조회
	 * @author	하창주 (hachangjoo)
	 * @param	SqlMapClient sqlMap : SqlMapClient 인스턴스
	 * @return	resultList : 결과 list
 	 * @throws	Exception
	 */
	public static List getTsacJourStdInfoList(SqlMapClient sqlMap, String jourCd) throws Exception {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("JOUR_CD", jourCd);
		
		List resultList = (List)sqlMap.queryForList("SACERP08100.getTsacJourStdInfoList", requestMap);
		int selectCnt = resultList.size();

		Map rowMap = null;
		for (int i = 0; i < selectCnt; i++) {
			rowMap = (Map)resultList.get(i);
			//if (log.isDebugEnabled()) {
				//log.debug("row[" + i + "] : " + rowMap.toString());
			//}
		}
		
		return resultList;
	}

	/**
	 * Key map create
	 * @author	하창주 (hachangjoo)
	 * @param	String zgsber : 영업센터
	 * 			String zbudat : 귀속일자
	 * 			String zgubun : 전송구분코드
	 * 			String ztrusr : 전송자사번
	 * 			String zcnt : Item Record 건수
	 * @return	Object[]
	 */
	public static Object[] makeKey(String zgsber,
			String zbudat,
			String zgubun,
			String ztrusr,
			String zcnt) {
		
		//zgsber = "D14910";	//영업센터 테스트 용
		Object[] oKey = new Object[HEAD_CNT];
		Map<String, Object> mKey = new HashMap<String, Object>();
		mKey.put("MANDT",		MANDT);
		mKey.put("BUKRS",		BUKRS);						//회사 코드
		mKey.put("ZBUDAT",		zbudat);					//귀속일자
		mKey.put("ZGSBER",		nullToSpace(zgsber));		//영업센터
		mKey.put("ZGUBUN",		nullToSpace(zgubun));		//전송구분코드
		mKey.put("ZIFDATE",		getDate() + getTime());		//전송일시분초(전송차수)
		mKey.put("ZPERIOD",		getZperiod(nullToSpace(zgubun)));		//주기
		mKey.put("ZTRUSR",		ztrusr);					//전송자 사번
		mKey.put("ZCNT",		zcnt);						//Item Record 건수
		mKey.put("ZCONFIRM",	"0");						//확정여부 (0:미수행, 1:에러, 2:성공 , 3:확정에러 , 4:확정)
		//mKey.put("ZCONFDAT",	"");
		//mKey.put("ZCONFUSR",	"");
		//mKey.put("ZDELE",		"");
		mKey.put("ERRLOG",		"");						//Error Log

		if (log.isDebugEnabled()) {
			log.debug("mKey : " + mKey.toString());
		}
		
		oKey[0] = mKey;
		return oKey;
	}
	
	/**
	 * 전송구분으로 전송 주기를 return
	 * @author	하창주 (hachangjoo)
	 * @param	String Zgubun : 전송구분
	 * @return	String : 전송주기
	 */
	private static String getZperiod(String zgubun) {
		if ( zgubun.equals(ZGUBUN_DPST_PROC_SKT_CHAG)    || zgubun.equals(ZGUBUN_DPST_PROC_SKT_PRP) ||
				zgubun.equals(ZGUBUN_DPST_PROC_CASA_AMT) || zgubun.equals(ZGUBUN_DPST_PROC_CARD) ||
				zgubun.equals(ZGUBUN_DPST_PROC_OCCR)     || zgubun.equals(ZGUBUN_ACCESSARY_CASH) || 
				zgubun.equals(ZGUBUN_ACCESSARY_CARD)     || zgubun.equals(ZGUBUN_ACCESSARY_COST) ) {
			return ZPERIOD_D;
		} else {
			return ZPERIOD_M;
		}
	}

	/**
	 * Head map create
	 * @author	하창주 (hachangjoo)
	 * @param	Map<String, Object> mKey : Key table row map
	 * 			String zitem : 항목번호
	 * 			String blart : 전표유형
	 * 			String wrbtr : 차변 합계금액
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> makeHead(Map<String, Object> mKey,
			String zitem,
			String blart,
			String wrbtr) {
		
		Map<String, Object> mHead = new HashMap<String, Object>();
		mHead.put("MANDT",		mKey.get("MANDT"));
		mHead.put("BUKRS",		mKey.get("BUKRS"));			//회사 코드
		mHead.put("ZBUDAT",		mKey.get("ZBUDAT"));		//귀속일자(현재일)
		mHead.put("ZGSBER",		mKey.get("ZGSBER"));		//영업센터
		mHead.put("ZGUBUN",		mKey.get("ZGUBUN"));		//전송구분코드
		mHead.put("ZIFDATE",	mKey.get("ZIFDATE"));		//전송일시분초(전송차수)
		mHead.put("BUDAT",		mKey.get("BUDAT"));		    //전표기장일
		
		//minus 금액 구분
		if (Long.parseLong(wrbtr) > 0 ) {
			mHead.put("MINUS_CL",	"1");		//minus 구분
		} else {
			mHead.put("MINUS_CL",	"-1");		//minus 구분
			log.debug("wrbtr : " + wrbtr.toString());
			wrbtr = String.valueOf(Long.parseLong(wrbtr) * (-1));
		}

		mHead.put("ZITEM",		zitem);						//항목번호
		mHead.put("BLART",		blart);						//전표유형
		mHead.put("WRBTR",		wrbtr);						//차변  합계금액
		mHead.put("ZCONFIRM",	"0");						//(0:미수행, 1:에러, 2:성공 , 3:확정에러 , 4:확정)
		mHead.put("ERRLOG",		"");						//에러메시지
		mHead.put("BLDAT",		mKey.get("ZBUDAT"));		//전표기장일(귀속일자와 동일)

		//mHead.put("WAERS",		"");
		//mHead.put("BKTXT",		"");					//전송구분코드 + 코드명
		//mHead.put("BELNR",		"");					//ERP 생성 전표번호
		//mHead.put("ZBELNR",		"");					//ERP 역전표번호
		mHead.put("OCCU_TYP",	getOccuTyp((String)mKey.get("ZGUBUN")));	//발생유형

		mHead.put("ACC_PLC",		"");	//정산처
		mHead.put("CARD_CO",		"");	//카드사
		mHead.put("ACC_MM",			"");	//정산월

		if (log.isDebugEnabled()) {
			//log.debug("mHead : " + mHead.toString());
		}
		
		return mHead;
	}
	
	/**
	 * 전송구분으로 발생유형을 return
	 * @author	하창주 (hachangjoo)
	 * @param	String Zgubun : 전송구분
	 * @return	String : 발생유형
	 */
	private static String getOccuTyp(String zgubun) {
		
		if (zgubun == null || zgubun.equals("")) {
			return "";
		}
		
		if (zgubun.substring(0, 1).equals(OCCU_TYP_A)) {
			return "10";
		} else if (zgubun.substring(0, 1).equals(OCCU_TYP_B)) {
			return "20";
		} else if (zgubun.substring(0, 1).equals(OCCU_TYP_D)) {
			return "30";
		} else if (zgubun.substring(0, 1).equals(OCCU_TYP_E)) {
			return "40";
		} else if (zgubun.substring(0, 1).equals(OCCU_TYP_F)) {
			return "50";
		} else {
			return "";
		}
	}

	/**
	 * item map create
	 * @author	하창주 (hachangjoo)
	 * @param	Map<String, Object> mHead : Head row map
	 * 			String zitemseq : 전표 item No.
	 * @return	Map<String, Object>
	 */
	public static Map<String, Object> makeItem(Map<String, Object> mHead,
			String zitemseq, Map mJourStd,
			String dpstPlc, String wrbtr,
			String zgcode) {
		
		Map<String, Object> mItem= new HashMap<String, Object>();
		mItem.put("MANDT",		mHead.get("MANDT"));
		mItem.put("BUKRS",		mHead.get("BUKRS"));		//회사 코드
		mItem.put("ZBUDAT",		mHead.get("ZBUDAT"));		//귀속일자(현재일)
		mItem.put("ZGSBER",		mHead.get("ZGSBER"));		//영업센터
		mItem.put("ZGUBUN",		mHead.get("ZGUBUN"));		//전송구분코드
		mItem.put("ZIFDATE",	mHead.get("ZIFDATE"));		//전송일시
		mItem.put("ZITEM",		mHead.get("ZITEM"));		//전표헤더 항목번호
		mItem.put("ZSEQ",		zitemseq);					//회계 전표의 개별 항목 번호
		
		//minus 금액의 경우 역전기 키로 보냄
		if (Long.parseLong(wrbtr) < 0 ) {
			mItem.put("BSCHL",		nullToSpace((String)mJourStd.get("INV_FIRT_KEY")));	//역전기 키
			mItem.put("MINUS_CL",	"-1");		//minus 구분
			wrbtr = String.valueOf(Long.parseLong(wrbtr) * (-1));
			mItem.put("XREF5",		"X");		//참고필드5 금액이 마이너스인경우를 구분하기위해. 20091015
		} else {
			mItem.put("BSCHL",		nullToSpace((String)mJourStd.get("FIRT_KEY")));		//전기 키
			mItem.put("MINUS_CL",	"1");		//minus 구분
			mItem.put("XREF5",		nullToSpace((String)mJourStd.get("REF5")));		//참고필드5
		}
		mItem.put("UMSKZ",		nullToSpace((String)mJourStd.get("SPEC_GL")));		//특별 G/L 지시자
		
		if ( nullToSpace((String)mJourStd.get("CUST_YN")).equals(YES) ) {
			mItem.put("REGNO",		nullToSpace(dpstPlc));	//정산처코드(공급업체 또는 채권자의 계정 번호)
		} else {
			mItem.put("REGNO",		"");	//정산처코드(공급업체 또는 채권자의 계정 번호)
		}
		
		mItem.put("HKONT",		nullToSpace((String)mJourStd.get("GL_ACC")));		//GL(총계정원장) 계정 - 매출부가세(영업)계정도 item으로 생성
		mItem.put("WRBTR",		wrbtr);		//전표통화금액(부가세계정시 세액)
		mItem.put("ZGCODE",		zgcode);	//직영점 u-key code 4자리값
		mItem.put("BUPLA",		"");		//사업장
		mItem.put("KOSTL",		"");		//코스트 센터(귀속부서)
		mItem.put("ZTERM",		nullToSpace((String)mJourStd.get("PAY_COND")));		//지급 조건 키
		mItem.put("ZLSCH",		nullToSpace((String)mJourStd.get("PAY_MTH")));		//지급 방법
		mItem.put("ZLSPR",		nullToSpace((String)mJourStd.get("PAY_DEF")));		//지급 보류 키
		mItem.put("ZFBDT",		nullToSpace((String)mJourStd.get("PAY_INIT_DT")));	//만기 계산 기준일(지급기산일)
		mItem.put("BVTYP",		"");		//파트너 은행 유형(사용미정)
		mItem.put("SGTXT",		"");		//품목텍스트
		mItem.put("XREF1",		nullToSpace((String)mJourStd.get("REF1")));		//참고필드1
		mItem.put("XREF2",		nullToSpace((String)mJourStd.get("REF2")));		//참고필드2
		mItem.put("XREF3",		nullToSpace((String)mJourStd.get("REF3")));		//참고필드3
		mItem.put("XREF4",		nullToSpace((String)mJourStd.get("REF4")));		//참고필드4
		//mItem.put("XREF5",		nullToSpace((String)mJourStd.get("REF5")));		//참고필드5
		mItem.put("ERRLOG",		"");		//에러메시지
		mItem.put("DC_CAT",		nullToSpace((String)mJourStd.get("DC_CAT")));	//차대구분

		if (log.isDebugEnabled()) {
			//log.debug("mItem : " + mItem.toString());
		}
		
		return mItem;
	}


	/**
	 * var header map create
	 * @author	하창주 (hachangjoo)
	 * @param	String zgsber : 영업센터
	 * 			String zbudat : 귀속일자
	 * 			String ztrusr : 전송자사번
	 * @return	Object[]
	 */
	public static Map<String, Object> makeVatHead(String zgsber, String zbudat, String ztrusr, String taxCl) {
		
		Map<String, Object> mVatHead = new HashMap<String, Object>();
		mVatHead.put("MANDT",		MANDT);
		mVatHead.put("BUKRS",		BUKRS);						//회사 코드
		mVatHead.put("ZBUDAT",		zbudat);					//귀속일자
		mVatHead.put("ZGSBER",		nullToSpace(zgsber));		//영업센터
		mVatHead.put("ZGUBUN",		ZGUBUN_VAT);				//전송구분코드
		mVatHead.put("ZIFDATE",		getDate() + getTime());		//전송일시분초(전송차수)
		mVatHead.put("ZPERIOD",		ZPERIOD_M);					//주기
		mVatHead.put("ZTRUSR",		ztrusr);					//전송자 사번
		mVatHead.put("ZCNT",		"0");						//Item Record 건수
		mVatHead.put("ZCONFIRM",	"0");						//확정여부 (0:미수행, 1:에러, 2:성공 , 3:확정에러 , 4:확정)
		//mVatHead.put("ZCONFDAT",	"");
		//mVatHead.put("ZCONFUSR",	"");
		//mVatHead.put("ZDELE",		"");
		mVatHead.put("ERRLOG",		"");						//Error Log
		mVatHead.put("TAX_CL",		taxCl);						//부가세구분

		if (log.isDebugEnabled()) {
			//log.debug("mVatHead : " + mVatHead.toString());
		}
		
		return mVatHead;
	}

	/**
	 * vat item map create
	 * @author	하창주 (hachangjoo)
	 * @param	Map<String, Object> mHead : vat head row map
	 * 			Map<String, Object> mVatItem : vat item row map
	 * @return	Map<String, Object>
	 */
	public static Map<String, Object> makeVatItem(Map<String, Object> mHead, Map<String, Object> mVatItem) {
		
		Map<String, Object> mItem= new HashMap<String, Object>();
		mItem.put("MANDT",		mHead.get("MANDT"));
		mItem.put("BUKRS",		mHead.get("BUKRS"));			//회사 코드
		mItem.put("ZBUDAT",		mHead.get("ZBUDAT"));			//귀속일자(현재일)
		mItem.put("ZGSBER",		mHead.get("ZGSBER"));			//영업센터
		mItem.put("ZGUBUN",		mHead.get("ZGUBUN"));			//전송구분코드
		mItem.put("ZIFDATE",	mHead.get("ZIFDATE"));			//전송일시
		mItem.put("ZITEM",		mVatItem.get("ZITEM"));			//항목번호(k) : 레코드단위
		mItem.put("ZTAXDAT",	mHead.get("ZBUDAT"));			//세금계산서일자 : 매출일, 매입일. 단, 세금코드'15'인 경우는 매출일자별 합계
		
		mItem.put("MWSKZ",		mVatItem.get("MWSKZ"));			//부가가치세 코드 : '11'-매출세금계산서발행(사업자), '12'-매출세금계산서발행(개인), '15'-매출세금계산서미발행(개인), '41'-과세매입일반
		mItem.put("ZGCODE",		nullToSpace((String)mVatItem.get("ZGCODE")));		//직영점코드 : 직영점 u-key code 4자리값
		mItem.put("BUPLA",		"");			//사업장 : ERP생성
		mItem.put("TAXGU",		mVatItem.get("TAXGU"));			//원천코드 : IA'(매출), 'IV'(매입)
		mItem.put("NAME1",		nullToSpace((String)mVatItem.get("NAME1")));		//이름 1 : 세금코드'11','12','41' : 상호명 또는 이름, 세금코드'15' : space
		mItem.put("HWBAS",		mVatItem.get("HWBAS"));			//과세 표준 금액(공급가액)	: 공급가액(단, 세금코드'15'인 경우는 일자별 합계액)
		mItem.put("HWSTE",		mVatItem.get("HWSTE"));			//금액(세액) : 세액(단, 세금코드'15'인 경우는 일자별 합계액)
		mItem.put("ZTOTAMT",	mVatItem.get("ZTOTAMT"));		//합계금액 : 합계액(단, 세금코드'15'인 경우는 일자별 합계액)
		mItem.put("STCD1",		nullToSpace((String)mVatItem.get("STCD1")));		//주민등록번호 : 세금코드'11','15','41' : space, 세금코드'12' : 필수
		mItem.put("STCD2",		nullToSpace((String)mVatItem.get("STCD2")));		//사업자등록번호 : 세금코드'11' ,'41' : 필수, 세금코드'12','15' : space
		mItem.put("J_1KFREPRE",	nullToSpace((String)mVatItem.get("J_1KFREPRE")));	//대표자명 : 세금코드'11', '41' : 필수, 세금코드'12','15' : space
		mItem.put("ORT01",		nullToSpace((String)mVatItem.get("ORT01")));		//주소 : 세금코드'11','12','41' : 필수, 세금코드'15' : space
		mItem.put("J_1KFTBUS",	nullToSpace((String)mVatItem.get("J_1KFTBUS")));	//업태 : 세금코드'11','41' : 필수, 세금코드'12','15' : space
		mItem.put("J_1KFTIND",	nullToSpace((String)mVatItem.get("J_1KFTIND")));	//업종 : 세금코드'11','41' : 필수, 세금코드'12','15' : space
		mItem.put("ZARKTX1",	nullToSpace((String)mVatItem.get("ZARKTX1")));		//품목텍스트 : 세금코드 '11', '12'인 경우 : 품목적요
		mItem.put("ZTAXCOUNT",	nullToSpace((String)mVatItem.get("ZTAXCOUNT")));	//세금계산서매수 : 세금코드'11','12','41' : space, 세금코드'15' : 총개인미발행거래수(세금계산서매수)
		mItem.put("ZREFER1",	nullToSpace((String)mVatItem.get("ZREFER1")));		//참고1 : 여분필드1
		mItem.put("ZREFER2",	nullToSpace((String)mVatItem.get("ZREFER2")));		//참고2 : 여분필드2
		mItem.put("ZREFER3",	nullToSpace((String)mVatItem.get("ZREFER3")));		//참고3 : 여분필드3
		mItem.put("ZREFER4",	nullToSpace((String)mVatItem.get("ZREFER4")));		//참고4 : 여분필드4
		mItem.put("ZREFER5",	nullToSpace((String)mVatItem.get("ZREFER5")));		//참고5 : 여분필드5
		mItem.put("ZREFER6",	nullToSpace((String)mVatItem.get("DEAL_CO_CD")));	//참고6 : 거래처코드(전자세금계산서 관련추가사항)
		mItem.put("ZCONFIRM",	"0");		//확정여부 : I/F시 '0'으로 전송. Value값('0'-Val.미수행, '1'-Val.에러, '2'-Val.성공, '3'-확정에러, '4'-확정)
		mItem.put("ERRLOG",		"");		//Error Log : 에러메시지
		mItem.put("ZDELE",		"");		//삭제표시 : 확정이후 ERP 부가세신고테이블 삭제표시의 경우 사용
		mItem.put("ZTAXNO",		"");		//세금계산서번호 : ERP 생성번호 -> 확정시 전표유형 'ZS'의 번호범위로 자동채번
		mItem.put("GJAHR",		"");		//회계연도 : 확정시 세금계산서일자로 회계연도 확정
		
		/**
		 *   수정 전자 세금계산서 인 경우 세금계산서 번호가 입력되어 있다.
		 *   세금계산서 번호가 있는 경우 세금계산서 번호를 넣어 준다.
		 */
		if(mVatItem.get("ZTAXNO") == null || "".equals(mVatItem.get("ZTAXNO"))){
			mItem.put("ZTAXNO",		"");		//세금계산서번호 : ERP 생성번호 -> 확정시 전표유형 'ZS'의 번호범위로 자동채번
			mItem.put("GJAHR",		"");		//회계연도 : 확정시 세금계산서일자로 회계연도 확정
		}else{
			mItem.put("ZTAXNO",		nullToSpace((String)mVatItem.get("ZTAXNO")));		//세금계산서번호 : ERP 생성번호 -> 확정시 전표유형 'ZS'의 번호범위로 자동채번
			mItem.put("GJAHR",		nullToSpace((String)mVatItem.get("GJAHR")));		//회계연도 : 확정시 세금계산서일자로 회계연도 확정
		}
		mItem.put("DEAL_CO_CD",	mVatItem.get("DEAL_CO_CD"));		//PS거래처코드

		if (log.isDebugEnabled()) {
			//log.debug("mItem : " + mItem.toString());
		}
		
		return mItem;
	}

	/**
	 * 전표마스터 트랜젝션 테이블 insert
	 * @author	하창주 (hachangjoo)
	 * @param	sqlMap : SqlMapClient 인스턴스
	 * @return	void
 	 * @throws	Exception
	 */
	public static void insertAccTrTable(SqlMapClient sqlMap,
			Map<String, Object> imTables,
			Map<String, Object> retMap) throws Exception {
		
		Object[] oKey	= (Object[])imTables.get("KEY");
		Object[] oHead	= (Object[])imTables.get("HEAD");
		Object[] oItem	= (Object[])imTables.get("ITEM");

		ArrayList<Object> alKey		= (ArrayList<Object>)retMap.get("KEY");
		ArrayList<Object> alHead	= (ArrayList<Object>)retMap.get("HEAD");
		ArrayList<Object> alItem	= (ArrayList<Object>)retMap.get("ITEM");

		if (log.isDebugEnabled()) {
			//log.debug("alKey.length : " + alKey.size());
			//log.debug("alHead.length : " + alHead.size());
			//log.debug("alItem.length : " + alItem.size());
		}
		
		int i = 0;
		Map imMap	= null;
		Map rowMap	= null;
		
		//INSERT ZIFT0003
		for (i = 0; i < alKey.size(); i++) {
			rowMap	= (Map<String, Object>)alKey.get(i);
			imMap	= (Map<String, Object>)oKey[i];
			rowMap.put("MANDT", imMap.get("MANDT"));
			//log.debug("rowMap : " + rowMap.toString());
			sqlMap.insert("SACERP08100.insertZift0003", rowMap);
			
			rowMap.put("REMARK", (String)retMap.get("EMSGTXT"));
			sqlMap.update("SACERP08100.updateTsacMmErpTrms", rowMap);
			rowMap.put("WRBTR", ((Map<String, Object>)oHead[i]).get("WRBTR"));
			sqlMap.update("SACERP08100.updateTsacDdErpTrms", rowMap);
		}

		long wrbtr = 0;
		//INSERT ZIFT0004
		for (i = 0; i < alHead.size(); i++) {
			rowMap	= (Map<String, Object>)alHead.get(i);
			imMap	= (Map<String, Object>)oHead[i];
			rowMap.put("MANDT",		imMap.get("MANDT"));
			rowMap.put("OCCU_TYP",	imMap.get("OCCU_TYP"));
			rowMap.put("ACC_PLC",	imMap.get("ACC_PLC"));			//정산처
			rowMap.put("CARD_CO",	imMap.get("CARD_CO"));			//카드사
			rowMap.put("ACC_MM",	imMap.get("ACC_MM"));			//정산월
			
			//log.debug("WRBTR : " + Long.parseLong((String)imMap.get("WRBTR")));
			//log.debug("MINUS_CL : " + Long.parseLong((String)imMap.get("MINUS_CL")));
			wrbtr = Long.parseLong((String)imMap.get("WRBTR")) * Long.parseLong((String)imMap.get("MINUS_CL"));
			rowMap.put("WRBTR",		String.valueOf(wrbtr));
			//log.debug("rowMap : " + rowMap.toString());

			sqlMap.insert("SACERP08100.insertZift0004", rowMap);
		}
		
		//INSERT ZIFT0005
		for (i = 0; i < alItem.size(); i++) {
			rowMap	= (Map<String, Object>)alItem.get(i);
			imMap	= (Map<String, Object>)oItem[i];
			rowMap.put("MANDT",		imMap.get("MANDT"));
			rowMap.put("ZITEMSEQ",	(String)rowMap.get("ZSEQ"));
			rowMap.put("HKONT",		imMap.get("HKONT"));
			rowMap.put("DC_CAT",	imMap.get("DC_CAT"));

			wrbtr = Long.parseLong((String)imMap.get("WRBTR")) * Long.parseLong((String)imMap.get("MINUS_CL"));
			rowMap.put("WRBTR",		String.valueOf(wrbtr));
			log.debug("rowMap : " + rowMap.toString());
			sqlMap.insert("SACERP08100.insertZift0005", rowMap);
		}
	}

	/**
	 * 부가세 트랜젝션 테이블 insert
	 * @author	하창주 (hachangjoo)
	 * @param	sqlMap : SqlMapClient 인스턴스
	 * @return	void
 	 * @throws	Exception
	 */
	public static void insertVatTrTable(SqlMapClient sqlMap,
			Map<String, Object> imTables,
			Map<String, Object> retMap) throws Exception {

		Object[] oHead	= (Object[])imTables.get("T_HEAD");
		Object[] oItem	= (Object[])imTables.get("T_BODY");

		ArrayList<Object> alHead	= (ArrayList<Object>)retMap.get("T_HEAD");
		ArrayList<Object> alItem	= (ArrayList<Object>)retMap.get("T_BODY");

		int i = 0;
		Map imMap	= null;
		Map rowMap	= null;
		
		//INSERT ZIFT0003
		for (i = 0; i < alHead.size(); i++) {
			rowMap	= (Map<String, Object>)alHead.get(i);
			imMap	= (Map<String, Object>)oHead[i];
			rowMap.put("ZBUDAT",	imMap.get("ZBUDAT"));
			rowMap.put("TAX_CL",	imMap.get("TAX_CL"));
			//log.debug("rowMap : " + rowMap.toString());
			sqlMap.insert("SACERP08100.insertZift0003", rowMap);

			rowMap.put("REMARK", (String)retMap.get("EMSGTXT"));
			sqlMap.update("SACERP08100.updateTsacMmTaxTrms", rowMap);
		}


		//INSERT ZIFT0006
		for (i = 0; i < alItem.size(); i++) {
			rowMap	= (Map<String, Object>)alItem.get(i);
			imMap	= (Map<String, Object>)oItem[i];
			rowMap.put("ZBUDAT",		imMap.get("ZBUDAT"));
			rowMap.put("ZTAXDAT",		imMap.get("ZTAXDAT"));
			rowMap.put("DEAL_CO_CD",	imMap.get("DEAL_CO_CD"));

			if( i == 0 ) {
				log.debug("rowMap : " + rowMap.toString());
			}
			sqlMap.insert("SACERP08100.insertZift0006", rowMap);
		}
		
	}
	
	/**
	 * 전자 세금계산서 트랜젝션 테이블 insert
	 * @author	이강수 (leekangsu)
	 * @param	sqlMap : SqlMapClient 인스턴스
	 * @return	void
 	 * @throws	Exception
	 */
	public static void insertTaxTrTable(SqlMapClient sqlMap,
			Map<String, Object> imTables,
			Map<String, Object> retMap) throws Exception {

		Object[] oHead	= (Object[])imTables.get("T_HEAD");
		Object[] oItem	= (Object[])imTables.get("T_BODY");

		ArrayList<Object> alHead	= (ArrayList<Object>)retMap.get("T_HEAD");
		ArrayList<Object> alItem	= (ArrayList<Object>)retMap.get("T_BODY");

		int i = 0;
		Map imMap	= null;
		Map rowMap	= null;
		
		//INSERT ZIFT0003
		for (i = 0; i < alHead.size(); i++) {
			rowMap	= (Map<String, Object>)alHead.get(i);
			imMap	= (Map<String, Object>)oHead[i];
			rowMap.put("ZBUDAT",	imMap.get("ZBUDAT"));
			rowMap.put("TAX_CL",	imMap.get("TAX_CL"));
			//log.debug("rowMap : " + rowMap.toString());
			sqlMap.insert("SACERP08100.insertZift0003", rowMap);

			rowMap.put("REMARK", (String)retMap.get("EMSGTXT"));
			
			rowMap.put("YYMM"   , (String)retMap.get("YYMM"));
			rowMap.put("AGENCY" , (String)retMap.get("AGENCY"));
			rowMap.put("STL_PLC", (String)retMap.get("STL_PLC"));
			rowMap.put("SEQ", (String)retMap.get("SEQ"));
			sqlMap.update("SACERP08100.updateTsacMmTaxErpTrms", rowMap);
		}


		//INSERT ZIFT0006
		for (i = 0; i < alItem.size(); i++) {
			rowMap	= (Map<String, Object>)alItem.get(i);
			imMap	= (Map<String, Object>)oItem[i];
			rowMap.put("ZBUDAT",		imMap.get("ZBUDAT"));
			rowMap.put("ZTAXDAT",		imMap.get("ZTAXDAT"));
			rowMap.put("DEAL_CO_CD",	imMap.get("DEAL_CO_CD"));

			if( i == 0 ) {
				log.debug("rowMap : " + rowMap.toString());
			}
			sqlMap.insert("SACERP08100.insertZift0006", rowMap);
		}
		
	}
	

	/**
	 * write 연동결과 in the logfile
	 * @author	하창주 (hachangjoo)
	 * @param	Map<String, Object> retMap : 연동결과 map
	 * @return	void
 	 * @throws	Exception
	 */
	public static void writeLogResultMsg(Map<String, Object> retMap, LogManager logMng) {
		
		logMng.writeLogFile("ECODE : " + (String)retMap.get("ECODE")
				+ ", ETYPE : " + (String)retMap.get("ETYPE")
				+ ", EMSGTXT : " + (String)retMap.get("EMSGTXT"));
		
		log.debug("ECODE : " + (String)retMap.get("ECODE")
				+ ", ETYPE : " + (String)retMap.get("ETYPE")
				+ ", EMSGTXT : " + (String)retMap.get("EMSGTXT"));
		
	}
	
	/**
	 * ERP 전송요청 테이블에 결과 update in the logfile
	 * @author	하창주 (hachangjoo)
	 * @param	Map<String, Object> retMap : 연동결과 map
	 * @return	void
 	 * @throws	Exception
	 */
	public static void updateTsacErpTrms(SqlMapClient sqlMap, Map<String, Object> requestMap, String remarkMsg) throws Exception {

		requestMap.put("REMARK", remarkMsg);
		
		String trmsItem = (String)requestMap.get("TRMS_ITEM");
		
		if ( trmsItem.equals(ZGUBUN_DPST_PROC_SKT_CHAG) || trmsItem.equals(ZGUBUN_DPST_PROC_SKT_PRP) ||
				trmsItem.equals(ZGUBUN_DPST_PROC_CARD) ) {	//(일) ERP 전송
			sqlMap.update("SACERP08100.updateTsacDdErpTrms2", requestMap);
		}else{
			sqlMap.update("SACERP08100.updateTsacMmErpTrms2", requestMap);
		}
		log.debug(remarkMsg);
	}
	
	/**
	 * 숫자앞에 지정한 길이에맞게 0를 채워 문자열로 리턴한다.(007777)
	 * @author	하창주 (hachangjoo)
	 * @param	String value : 문자열
	 * @return	String : 0이 채워진 문자열
 	 * @throws	Exception
	 */
	public static String fillZeroFront(String value, int len){
		String reStrValue = "";

		for(int i = 0; i < len - value.length() ; i++){
			reStrValue += "0";
		}

		reStrValue += value;
		return reStrValue;
	}

	
	/**
	 * 대리점별 SKN 거래처 코드를 추출해낸다.
	 * @author	하창주 (hachangjoo)
	 * @param	String format
	 * @return	SKN 거래처 코드
	 */
	public static String getSknDealCoCd(String agency) {
		String sknDealCoCd = "10002";
		
		if ( "37001".equals(agency) ) {				//부산
			sknDealCoCd = "21005";
		} else if ( "38001".equals(agency) || "46001".equals(agency) ) {	//서대구, 동대구
			sknDealCoCd = "21004";
		} else if ( "39001".equals(agency) ) {		//광주
			sknDealCoCd = "21003";
		} else if ( "40001".equals(agency) ) {		//대전
			sknDealCoCd = "21002";
		} else if ( "47001".equals(agency) ) {		//천안
			sknDealCoCd = "21006";
		}
		
		return sknDealCoCd;
	} 

}
