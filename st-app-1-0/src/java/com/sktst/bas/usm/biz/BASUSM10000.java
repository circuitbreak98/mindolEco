/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.usm.biz;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.Record;
import nexcore.framework.core.data.RecordHeader;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.util.DataSetFactory;

import com.eai.mq.api.EaiApiData;
import com.eai.mq.api.EaiMQApi;
import com.eai.mq.conf.EaiException;
import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2010-07-14 13:11:15</li>
 * </ul>
 *
 * @author 김연석 (kimyeunsuk)
 */
public class BASUSM10000 extends BaseBizUnit {

	private static final String SENDFAIL = "N";
	private static final String SENDOK   = "Y";
    private static String if_id          = null;
	private static String if_msg_id      = null;
    private static String if_op_code     = null;
	/**
	 * 
	 *
	 * @author 김연석 (kimyeunsuk)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet sendMqData(IDataSet requestData, IOnlineContext onlineCtx) {

		IDataSet result = new DataSet();
		// TODO 업무 로직 작성 필요

		return result;
	}

    public static IDataSet sendBill(String mqData) throws EaiException {

		// 레코드셋 생성
		IRecordSet ncRecordSet = new RecordSet("ds_MQResult");

		// 헤더 설정. (필요한 헤더 수 만큼 반복)
		ncRecordSet.addHeader(new RecordHeader("SEND_YN",      java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader("SEND_MESSAGE", java.sql.Types.VARCHAR));

		// 레코드 생성
		IRecord ncRecord = new Record();

    	int ret = 0; // MQ의 리턴값 확인용 변수
		EaiMQApi mqapi          = new EaiMQApi();		

        try {
	

    		EaiApiData apiData      = null;

			System.out.println("==== Before");
			// EAI MQ API를 사용하기 위해 해당 큐 매니저에 연결 함
			ret = mqapi.mq_connect();

			System.out.println("==== After "+ret+ " ->> " + mqData);

			//apiData = generateEaiApiData( mqData );


	        // Syncronous PUT 모드.
	        if_id = "TPS.MAPP_INFO_MFF";

	        apiData = new EaiApiData();
	        apiData.initPut();
	        apiData.m_mqput_t.in_intf_id = if_id;
	        apiData.m_mqput_t.in_intf_msg_id = if_msg_id;
	        apiData.m_mqput_t.in_intf_op_code = if_op_code;
	        apiData.m_mqput_t.in_send_buf = mqData;
	        apiData.m_mqput_t.in_send_buf_len = mqData.getBytes().length;

			System.out.println("==== apiData ["+apiData.m_mqput_t.in_send_buf +"]");

            if( ( ret = mqapi.mq_put(apiData)) != 0 ) {


    			System.out.println("==== PUT Fail "+ret);
        		// 전송 실패
        		ncRecord.add("SEND_YN",      SENDFAIL);                         // 송신 성공여부
        		ncRecord.add("SEND_MESSAGE", apiData.m_mqputs_t.out_error_msg); // Error Message

        		// 마직막으로 해당 레코드셋에 레코드 설정
        		ncRecordSet.addRecord(ncRecord);
        		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
        				BaseConstants.QUERY_OK_MESSAGE_ID, new String[]{"1"});

        		responseData.putRecordSet("ds_MQResult", ncRecordSet);
                return responseData;
            }

            if( (ret = mqapi.mq_commit()) != 0 ) {
    			System.out.println("==== COMMIT Fail "+ret);
        		// Commit 실패
        		ncRecord.add("SEND_YN",      SENDFAIL);                         // 송신 성공여부
        		ncRecord.add("SEND_MESSAGE", apiData.m_mqputs_t.out_error_msg); // Error Message

        		// 마직막으로 해당 레코드셋에 레코드 설정
        		ncRecordSet.addRecord(ncRecord);
        		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
        				BaseConstants.QUERY_OK_MESSAGE_ID, new String[]{"1"});

        		responseData.putRecordSet("ds_MQResult", ncRecordSet);
                return responseData;
            }

			System.out.println("==== PUT SUCCESS "+ret);
    		// 성공 Message
    		ncRecord.add("SEND_YN",      SENDOK);                           // 송신 성공여부
    		ncRecord.add("SEND_MESSAGE", apiData.m_mqputs_t.out_error_msg); // Error Message

    		// 마직막으로 해당 레코드셋에 레코드 설정
    		ncRecordSet.addRecord(ncRecord);
    		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
    				BaseConstants.QUERY_OK_MESSAGE_ID, new String[]{"1"});


    		responseData.putRecordSet("ds_MQResult", ncRecordSet);
            return responseData;

        }
        catch (EaiException e) {
            System.out.println("PORTAL MQ 시스템 연결 오류" + e);
        }
        finally {
            try {
                if( (ret = mqapi.mq_disconnect()) != 0 ) {
                    System.out.println( "MQ_DISCONNECT 오류 : " );
                }
            }
            catch (Exception e) { }
        }
        return null;
    }
}