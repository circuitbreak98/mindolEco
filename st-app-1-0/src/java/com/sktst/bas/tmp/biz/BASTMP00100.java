/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.tmp.biz;

import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/*
 import java.util.Calendar;
 import java.util.Date;
 import java.text.SimpleDateFormat;
 */
/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-03-19 09:29:25</li>
 * </ul>
 *
 * @author 원종윤 (wonjongyoon)
 */
public class BASTMP00100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet addUkeyLog(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("addUkeyLog method start");
		}

		Map oMap = null;

		IRecordSet rs = requestData.getRecordSet("ds_ukeyLogs");

		int insertCount = 0;

		String sDate = new java.text.SimpleDateFormat("yyyyMMdd")
				.format(new java.util.Date());
		String sTime = new java.text.SimpleDateFormat("HHmmss")
				.format(new java.util.Date());

		/*Calendar calendar = Calendar.getInstance();
		 java.util.Date date = calendar.getTime();
		 String today = (new SimpleDateFormat("yyyyMMddHHmmss").format(date));
		 log.debug("today:++++++++++++++"+today);
		 */
		//log.debug("Date:++++++++++++++" + sDate);
		//log.debug("Time:++++++++++++++" + sTime);

		if (rs != null) {
			oMap = rs.getRecordMap(0);
			String sIF_CTT = oMap.get("IF_CTT").toString();
			//log.debug("sUkeyLog:::::::::" + sUkeyLog);
			oMap.put("IF_CTT", sIF_CTT);
			oMap.put("OP_DT", sDate);
			oMap.put("OP_TM", sTime);
			
			byte[] sByte = sIF_CTT.getBytes();
			String sIF_CL = new String(sByte, 0, 2).trim();
			oMap.put("IF_CL", sIF_CL);
			
			
			// Ukey I/F MQ 테이블로부터 SEQ 데이터 가져오기
			if(log.isDebugEnabled()){
				log.debug("BASTMP00100.execute.getUKeyIFSeq");
			}			
			IRecordSet seqList = null;
			seqList = queryForRecordSet(
					"BASTMP00100.getUKeyIFSeq", oMap, onlineCtx);			
			Map resultMap = null;			
			resultMap = seqList.getRecordMap(0);			
			int seq =Integer.parseInt(resultMap.get("SEQ").toString());
			oMap.put("SEQ", seq);
			
			// Ukey I/F MQ 테이블에 데이터 넣기 
			insert("BASTMP00100.addUKeyMQ", oMap, onlineCtx);
			
			// Ukey I/F Log 테이블에 데이터 넣기 
			insert("BASTMP00100.addUKeyLog", oMap, onlineCtx);			

			insertCount++;
			
			// 프로시저 호출
			if(log.isDebugEnabled()){
				log.debug("BASTMP00100.execute.callUkeyIFProcedure");
			}		
			queryForObject("BASTMP00100.callUkeyIFProcedure", oMap, onlineCtx);
		}

		if (insertCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}

		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { ""
						+ insertCount });
	}

}