package com.sktst.common.base;

/**
 * <ul>
 * <li>업무 그룹명 : SKT PS 공통</li>
 * <li>서브 업무명 : </li>
 * <li>설 명 : </li>
 * <li>작성일 : 2007. 05. 31</li>
 * <li>작성자 : 04889</li>
 * </ul>
 */
public class BaseConstants {
	
	//////////////////////////////////////////////////////////////////////////////
	// 범용 static key
	//////////////////////////////////////////////////////////////////////////////	
	/** 로그인 처리 결과 키명 */
	public static final String LOGIN_RESULT_KEY = "nc_loginResultKey";

	/** 로그인 처리 결과 파라메터 키명 */	
	public static final String LOGIN_RESULT_PARAMETER = "nc_loginResultParameter";
	
	
	//////////////////////////////////////////////////////////////////////////////
	// 메시지 ID 
	// 프레임워크 및 공통 관련 메세지	:	1000 번대의 번호
	// 업무 처리 관련 메시지			:	5000 번대의 번호
	//////////////////////////////////////////////////////////////////////////////
	
	// 프레임워크 및 공통 관련 메시지 /////////////////////////////////////////////////// 
	/** 로그인 - 등록되지 않은 사용자 입니다. */
	public static final String LOGIN_ID_FAIL							= "PSMW1001";

	/** 로그인 - 잘못된 Password 입니다. */
	public static final String LOGIN_PWD_FAIL 							= "PSMW1002";

	/** 로그인 - 사용 중지된 사용자 입니다. 관리자에게 문의 하세요. */
	public static final String LOGIN_USE_FAIL 							= "PSMW1003";

	/** 로그인 - 사용자의 조직정보가 불충분합니다. 관리자에게 문의 하세요. */
	public static final String LOGIN_ORG_FAIL 							= "PSMW1004";
	
	/** 로그인 - 비밀번호 만료일이 경과하여 로그인 할 수 없습니다. 비밀번호를 변경하시기 바랍니다. */
	public static final String LOGIN_PWD_EXPIRE_FAIL 					= "PSMW1005";
	
	/** 로그인 - 로그인이 불가능한 Location 입니다. */
	public static final String LOGIN_IP_FAIL 							= "PSMW1006";	
	
	/** 로그인 - 로그인에 성공했습니다.  */
	public static final String LOGIN_SUCCESS 							= "PSMI1001";
	
	/** 로그인 - 비밀번호 만료일이 {0}입니다. 비밀번호를 변경하시기 바랍니다. */
	public static final String LOGIN_PWD_EXPIRE_CHECK 					= "PSMI1002";
	
	/** 로그아웃 - 로그아웃에 성공했습니다.  */
	public static final String LOGOUT_SUCCESS 							= "PSMI1003";
	
	
		
	/** 파일 다운로드 - 파일 다운로드에 성공했습니다.  */	
	public static final String DOWNLOAD_SUCCESS 						= "PSMI1050";

	/** 파일 다운로드 - 파일 다운로드에 실패했습니다.  */
	public static final String DOWNLOAD_FAIL							= "PSME1050";
	
	/** 파일 업로드 - 파일 업로드에 실패했습니다.  */
	public static final String UPLOAD_FAIL								= "PSME1060";
	
	/** 엑셀파일에 {0}값이 누락되었습니다. */
	public static final String EXCEL_SKIP								= "PSMW5001";
	
	/** 존재하지 않는 {0}입니다.*/
	public static final String CHECK_EXIST								= "PSMW5002";
	
	/** {0}은 업로드 할 수 없습니다. */
	public static final String EXCEL_UPLOAD								= "PSMW5003";
	
	/** 사용중지인 모델입니다. */
	public static final String CHECK_USE								= "PSMW5004";
	
	/** 색상이 존재 하지 않습니다.*/
	public static final String CHECK_COLOR								= "PSMW5005";
	
	/** 잘못된 {0} 형식입니다.*/
	public static final String CHECK__FORM								= "PSMW5006";
	
	/** 상품의 {0) 확인하세요.*/
	public static final String CHECK_PROD								= "PSMW5007";
	
	/** 이미 등록된 {0} 입니다.*/
	public static final String CHECK_REG								= "PSMW5008";
	
	
	
	// 업무 처리 관련 메시지 ///////////////////////////////////////////////////
    /** 메시지 - 입력 작업이 성공하였습니다. */
    public static final String INSERT_OK_SIMPLE_MESSAGE_ID				= "PSMI5001";

    /** 메시지 - 총 {0} 건이 입력되었습니다. */
    public static final String INSERT_OK_MESSAGE_ID						= "PSMI5002";

    /** 메시지 - 수정작업이 성공하였습니다. */
    public static final String UPDATE_OK_SIMPLE_MESSAGE_ID				= "PSMI5003";

    /** 메시지 - 총 {0} 건이 수정되었습니다. */
    public static final String UPDATE_OK_MESSAGE_ID						= "PSMI5004";

    /** 메시지 - 삭제 작업이 성공하였습니다. */
    public static final String DELETE_OK_SIMPLE_MESSAGE_ID				= "PSMI5005";

    /** 메시지 - 총 {0} 건이 삭제되었습니다. */
    public static final String DELETE_OK_MESSAGE_ID						= "PSMI5006";

    /** 메시지 - 검색작업이 성공하였습니다. */
    public static final String QUERY_OK_SIMPLE_MESSAGE_ID				= "PSMI5007";

    /** 메시지 - 총 {0} 건이 검색되었습니다. */
    public static final String QUERY_OK_MESSAGE_ID						= "PSMI5008";

    /** 메시지 - 저장작업이 성공하였습니다. */
    public static final String UPDATEALL_OK_SIMPLE_MESSAGE_ID			= "PSMI5009";

    /** 메시지 - {0} 건 입력, {1} 건 수정, {2} 건이 삭제 되었습니다. */
    public static final String UPDATEALL_OK_MESSAGE_ID					= "PSMI5010";

    
    
    /** 에러메시지 - 이미 등록된 데이터입니다. */
    public static final String DUPLICATION_EXCEPTION_SIMPLE_MESSAGE_ID	= "PSME5001";

    /** 에러메시지 - 이미 등록된 데이터입니다. (테이블:{0}, 키:{1}) */
    public static final String DUPLICATION_EXCEPTION_MESSAGE_ID			= "PSME5002";

    /** 에러메시지 - 요청한 자료가 존재하지 않습니다. */
    public static final String SINGLE_QUERY_FAIL_SIMPLE_MESSAGE_ID		= "PSME5003";

    /** 에러메시지 - 요청한 자료가 존재하지 않습니다. ({0}) */
    public static final String SINGLE_QUERY_FAIL_MESSAGE_ID				= "PSME5004";
        
    /** 에러메시지 - 수정 또는 삭제된 레코드가 없습니다. */
    public static final String NO_RECORD_UPDATED_EXCEPTION_MESSAGE_ID	= "PSME5005";
      
    /** 에러메시지 - 이미 삭제된 자료입니다.*/
    public static final String NO_RECORD_DELETED_EXCEPTION_MESSAGE_ID 	= "PSME5006";

    /** 에러메시지 - 해당 자료가 없습니다. */
    public static final String NO_RECORD_EXCEPTION_MESSAGE_ID			= "PSME5007";

    
    /** 객체 생성방지용 생성 */
    private BaseConstants() {
        // 객체생성방지
    }

}