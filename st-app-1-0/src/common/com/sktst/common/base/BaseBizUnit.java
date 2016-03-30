package com.sktst.common.base;

import java.util.Map;

import nexcore.framework.core.Constants;
import nexcore.framework.core.biz.AbsBizUnit;
import nexcore.framework.integration.db.ISqlManager;

/**
 * 단위업무 상위 클래스
 * @author dev
 *
 */
public abstract class BaseBizUnit extends AbsBizUnit {

	/** 레코드 추가 판별용 */
	public static final String INSERT_FLAG = "insert";

	/** 레코드 수정 판별용 */
	public static final String UPDATE_FLAG = "update";

	/** 레코드 삭제 판별용 */
	public static final String DELETE_FLAG = "delete";

	/** 레코드 추가/수정/삭제 판별용 컬럼키 */
	public static final String CUD_FLAG_PARAM = "nc_rec_status";

	
	/** 프로젝트에서 사용할 DataSource명 */
	protected static final String DS_NAME = "ProjectDs"; 
	
	/**
	 * @see nexcore.framework.core.biz.AbsBizUnit#getSqlManager()
	 */
	@Override
	protected ISqlManager getSqlManager() {
		//NEXCORE Sample 용		
		//return getSqlManager(Constants.DEFAULT_DS);
		return getSqlManager(DS_NAME);
	}

	/**
	 * 페이징처리를 하기 위한 방법을 제공한다. 입력맵에 반드시 "nc_pageNo" 와 "nc_recordCountPerPage" 값이 있어야
	 * 한다.
	 * 
	 * @param params
	 */
	protected void setPagenatedParams(Map<String, String> params) {
		String strPageNo = (String) params.get(Constants.PAGE_NO);
		int pageNo = 1;
		if (strPageNo != null && strPageNo.length() > 0) {
			pageNo = Integer.parseInt(strPageNo);
			if (pageNo == 0){
				pageNo = 1;
			}
		}
		String strRecordCountPerPage = (String) params.get(Constants.RC_COUNT_PER_PAGE);
		int recordCountPerPage = 10;
		if (strRecordCountPerPage != null && strRecordCountPerPage.length() > 0) {
			recordCountPerPage = Integer.parseInt(strRecordCountPerPage);
		}
		params.put(Constants.PAGE_NO, String.valueOf(pageNo));
		params.put(Constants.RC_COUNT_PER_PAGE, String.valueOf(recordCountPerPage));
		params.put("nc_lastRowIndex", String.valueOf(pageNo * recordCountPerPage));
		params.put("nc_firstRowIndex", String.valueOf((pageNo - 1) * recordCountPerPage + 1));
	}
	

}
