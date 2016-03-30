/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.bco.biz;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordHeader;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordHeader;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.CryptoUtils;

import org.apache.commons.logging.Log;

import com.sktst.common.aes.AES;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2011-12-16 11:23:39</li>
 * </ul>
 *
 * @author 김만수 (kimmansoo)
 */
public class CommonCryptoManager extends com.sktst.common.base.BaseBizUnit {

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 * @throws Exception 
	 */
	public IDataSet encode(IDataSet requestData, IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		IRecordSet rs_info = requestData.getRecordSet("cptItem");
		String dsName = rs_info.get(0, "ds_name");
		String col1 = rs_info.get(0, "col_name1");
		String col2 = rs_info.get(0, "col_name2");
		String col3 = rs_info.get(0, "col_name3");
		String col4 = rs_info.get(0, "col_name4");
		String col5 = rs_info.get(0, "col_name5");
		String col6 = rs_info.get(0, "col_name6");

		IRecordSet rs = requestData.getRecordSet(dsName);

		int rows = rs.getRecordCount();

		log.debug("rows : " + rows);

		for (int i = 0; i < rows; i++) {
			IRecord row = rs.getRecord(i);
			log.debug("key : " + row.get(0));
			if (!"".equals(col1) && row.containsKey(col1)) {
				if (!"".equals(row.get(col1) + "")
						&& !"null".equals(row.get(col1) + "")
						&& row.get(col1).indexOf("=") < 0) {
//					String v = CryptoUtils.encode(row.get(col1));
					String v = AES.encrypt(row.get(col1));
					row.set(col1, v);
				}
			}

			if (!"".equals(col2) && row.containsKey(col2)) {
				if (!"".equals(row.get(col2) + "")
						&& !"null".equals(row.get(col2) + "")
						&& row.get(col2).indexOf("=") < 0) {
//					String v = CryptoUtils.encode(row.get(col2));
					String v = AES.encrypt(row.get(col2));
					row.set(col2, v);
				}
			}

			if (!"".equals(col3) && row.containsKey(col3)) {
				if (!"".equals(row.get(col3) + "")
						&& !"null".equals(row.get(col3) + "")
						&& row.get(col3).indexOf("=") < 0) {
//					String v = CryptoUtils.encode(row.get(col3));
					String v = AES.encrypt(row.get(col3));				
					row.set(col3, v);
				}
			}

			if (!"".equals(col4) && row.containsKey(col4)) {
				//log.debug("암호화4-1 : " + row.get(col4) + "");
				if (!"".equals(row.get(col4) + "")
						&& !"null".equals(row.get(col4) + "")
						&& row.get(col4).indexOf("=") < 0) {
//					String v = CryptoUtils.encode(row.get(col4));
					String v = AES.encrypt(row.get(col4));				
					row.set(col4, v);
				}
			}

			if (!"".equals(col5) && row.containsKey(col5)) {
				if (!"".equals(row.get(col5) + "")
						&& !"null".equals(row.get(col5) + "")
						&& row.get(col5).indexOf("=") < 0) {
//					String v = CryptoUtils.encode(row.get(col5));
					String v = AES.encrypt(row.get(col5));				
					row.set(col5, v);
				}
			}

			if (!"".equals(col6) && row.containsKey(col6)) {
				if (!"".equals(row.get(col6) + "")
						&& !"null".equals(row.get(col6) + "")
						&& row.get(col6).indexOf("=") < 0) {
//					String v = CryptoUtils.encode(row.get(col6));
					String v = AES.encrypt(row.get(col6));				
					row.set(col6, v);
				}
			}
		}

		requestData.putRecordSet(dsName, rs);

		return requestData;
	}

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 * @throws Exception 
	 */
	public IDataSet decode(IDataSet requestData, IOnlineContext onlineCtx) throws Exception {

		Log log = LogManager.getLog(onlineCtx);

		IRecordSet rs_info = requestData.getRecordSet("cptItem");
		String dsName = rs_info.get(0, "ds_name");
		String col1 = rs_info.get(0, "col_name1");
		String col2 = rs_info.get(0, "col_name2");
		String col3 = rs_info.get(0, "col_name3");
		String col4 = rs_info.get(0, "col_name4");
		String col5 = rs_info.get(0, "col_name5");
		String col6 = rs_info.get(0, "col_name6");
		String col7 = rs_info.get(0, "col_name7");

		IRecordSet rs = requestData.getRecordSet(dsName);
		int rows = rs.getRecordCount();

		for (int i = 0; i < rows; i++) {
			IRecord row = rs.getRecord(i);

			if (!"".equals(col1) && row.containsKey(col1)) {
				if (!"".equals(row.get(col1) + "") 
						&& !"null".equals(row.get(col1) + "")) {

//					String v = CryptoUtils.decode(row.get(col1));
					String v = AES.decrypt(row.get(col1));

					row.set(col1, v);
				}
			}

			if (!"".equals(col2) && row.containsKey(col2)) {
				if (!"".equals(row.get(col2) + "") 
						&& !"null".equals(row.get(col2) + "")) {

//					String v = CryptoUtils.decode(row.get(col2));
					String v = AES.decrypt(row.get(col2));
					
					row.set(col2, v);
				}
			}

			if (!"".equals(col3) && row.containsKey(col3)) {
				if (!"".equals(row.get(col3) + "") 
						&& !"null".equals(row.get(col3) + "")) {

//					String v = CryptoUtils.decode(row.get(col3));
					String v = AES.decrypt(row.get(col3));
					
					row.set(col3, v);
				}
			}

			if (!"".equals(col4) && row.containsKey(col4)) {
				if (!"".equals(row.get(col4) + "") 
						&& !"null".equals(row.get(col4) + "")) {

//					String v = CryptoUtils.decode(row.get(col4));
					String v = AES.decrypt(row.get(col4));
					
					row.set(col4, v);
				}
			}

			if (!"".equals(col5) && row.containsKey(col5)) {
				if (!"".equals(row.get(col5) + "") 
						&& !"null".equals(row.get(col5) + "")) {

//					String v = CryptoUtils.decode(row.get(col5));
					String v = AES.decrypt(row.get(col5));
					row.set(col5, v);
				}
			}

			if (!"".equals(col6) && row.containsKey(col6)) {
				if (!"".equals(row.get(col6) + "") 
						&& !"null".equals(row.get(col6) + "")) {

//					String v = CryptoUtils.decode(row.get(col6));
					String v = AES.decrypt(row.get(col6));
					
					row.set(col6, v);
				}
			}
			
			if (!"".equals(col7) && row.containsKey(col7)) {
				if (!"".equals(row.get(col7) + "") 
						&& !"null".equals(row.get(col7) + "")) {

//					String v = CryptoUtils.decode(row.get(col7));
					String v = AES.decrypt(row.get(col7));
					
					if(v.length() > 6)
					{
						v = v.substring(0, 6);
					}

					row.set(col7, v);
				}
			}
		}

		requestData.putRecordSet(dsName, rs);

		return requestData;
	}

	/**
	 * 
	 *
	 * @author 김만수 (kimmansoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getDataSet(IDataSet requestData, IOnlineContext onlineCtx) {

		IDataSet result = new DataSet();
		// TODO 업무 로직 작성 필요
		
		IRecordSet iSet = new RecordSet("cptItem");
		IRecordHeader iHeader = new RecordHeader("ds_name");
		iSet.addHeader(iHeader);
		iHeader = new RecordHeader("col_name1");
		iSet.addHeader(iHeader);
		iHeader = new RecordHeader("col_name2");
		iSet.addHeader(iHeader);
		iHeader = new RecordHeader("col_name3");
		iSet.addHeader(iHeader);
		iHeader = new RecordHeader("col_name4");
		iSet.addHeader(iHeader);
		iHeader = new RecordHeader("col_name5");
		iSet.addHeader(iHeader);
		iHeader = new RecordHeader("col_name6");
		iSet.addHeader(iHeader);
		iHeader = new RecordHeader("col_name7");
		iSet.addHeader(iHeader);
		
		result.putRecordSet("cptItem", iSet);

		return result;
	}

}