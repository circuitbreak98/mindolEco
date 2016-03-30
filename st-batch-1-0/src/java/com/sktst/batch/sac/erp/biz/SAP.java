package com.sktst.batch.sac.erp.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;

import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sktst.batch.base.AbsPropertyManager;
import com.sktst.batch.log.LogManager;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class SAP extends AbsPropertyManager {
	//JCO.Client mConnection = JCO.getClient(SapUtil.POOL_NAME);
	//IRepository aRepository = JCO.createRepository("MYRepository", SapUtil.POOL_NAME);
	private JCO.Client mConnection = null;
	private IRepository aRepository = null;
	
	private Log log = LogManager.getLog();
	
	/**
	 * 생성자
	 * @author	하창주 (hachangjoo)
	 * @param	void
	 * @return	void
	 */	
	SAP() {
		getSapProperties();
		mConnection = SapUtil.getConnection(log);
		aRepository = SapUtil.getRepository(mConnection, log);
	}
	
	/**
	 * disconnect
	 * @author	하창주 (hachangjoo)
	 * @param	void
	 * @return	void
	 */	
	public void disconnect() {
		mConnection.disconnect();
	}
	
	/**
	 * ERP 연동 정보를 properties file에서 읽어 변수에 set
	 * @author	하창주 (hachangjoo)
	 * @param	void
	 * @return	void
	 */	
	private void getSapProperties() {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(super.getPropertyPath()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String sapClient	= props.getProperty("jco.client.client");
		String userId		= props.getProperty("jco.client.user");
		String password		= props.getProperty("jco.client.passwd");
		String ipAddress	= props.getProperty("jco.client.ashost");
		String systemNumber	= props.getProperty("jco.client.sysnr");
		
		log.debug("ipAddress : " + ipAddress);
		SapUtil.setProperties(sapClient, userId, password, ipAddress, systemNumber);
	}
	
	/**
	 * RFC를 호출하고 Java Data를 구성하고 리턴
	 * @author 하창주 (hachangjoo)
	 * @param  String fName : RFC 명
	 *         Map imParams : import할 Parameter list
	 *         Map imTables : import할 Table list
	 * @return MAP : RFC 실행결과
	 */
	public Map<String, Object> executeRFC(String fName,
			Map<String, Object> imParams,
			Map<String, Object> imTables)
	{
		JCO.Function execFunc 			= null;
		//JCO.ParameterList outTables 	= null;
        if (log.isDebugEnabled()) {
            log.debug("SAP executeRFC ..... ");
        }
        
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if (mConnection == null) {
		        if (log.isDebugEnabled()) {
		            log.debug("!!! connect fail to remote SAP system.");
		        }
		        
		        throw new Exception();
		        //return null;
			}
			
	        if (log.isDebugEnabled()) {
	            log.debug("Function Module : " + fName);
	            //log.debug("imTables : " + imTables.toString());
	        }
	 
			execFunc = SapUtil.getFunction(aRepository, fName/*"Z_HR_0003"*/, log);
			if (imParams != null)
				buildImportParams(execFunc.getImportParameterList(), imParams);
			if (imTables != null)
				buildImportTables(execFunc.getTableParameterList(), imTables);

	        if (log.isDebugEnabled()) {
	            log.debug("mConnection : " + mConnection);
	            //log.debug("Tables : " + execFunc.getTableParameterList().toString());
	        }
	        
			mConnection.execute(execFunc);
			
			//exportParameter 가져오기
			//log.debug("Tables : " + execFunc.toString());
			//retMap.putAll(parseExportParams(execFunc.getExportParameterList()));
			//export Table 가져오기
			if(execFunc.getTableParameterList()!=null)
				retMap.putAll(parseExportTables(execFunc.getTableParameterList()));
			
	        if (log.isDebugEnabled()) {
	            log.debug("\n succed executing RFC : " + fName);
	        }

		} catch (Exception ex) {
			ex.printStackTrace();
	        if (log.isDebugEnabled()) {
	            log.debug("\n Exception : " + ex);
	        }
		}
		//JCO.releaseClient(mConnection);
		
		return retMap;
	}
	
	/**
	 * Import Parameter를 분석하여 JCO.paremeterList를 생성한다.
	 * 
	 * @author 하창주 (hachangjoo)
	 * 
	 * @param  Map imParams      : import할 Parameter List 
	 * @return JCO.ParameterList : JCO형식의 parameter list
	 */
	private void buildImportParams(JCO.ParameterList paramList, Map<String, Object> imParams)
	{
		//JCO.ParameterList paramList = new JCO.ParameterList();
		
		Set<String> keySet = imParams.keySet();
		Object []keys = keySet.toArray();
		
		for (int i = 0; i < keys.length; i++) {
			String key = (String)keys[i];
			paramList.setValue((String)imParams.get(key), key);
		}
	}
	
	/**
	 * Import Table 분석하여 JCO.paremeterList를 생성한다.
	 * 
	 * @author 하창주 (hachangjoo)
	 * 
	 * @param  Map imParams      : import할 Table List 
	 * @return JCO.ParameterList : JCO형식의 parameter list
	 */
	private void buildImportTables(JCO.ParameterList paramList, Map<String, Object> imTables)
	{
		//JCO.ParameterList paramList = new JCO.ParameterList();

		try {
			Set<String> tableSet = imTables.keySet();
			Object []tableNames = tableSet.toArray();
			if (log.isDebugEnabled()) {
				//log.debug("tableNames.length : " + tableNames.length);
			}


			for (int i = 0; i < tableNames.length; i++) {
				if (log.isDebugEnabled()) {
					log.debug("table name : " + tableNames[i]);
					log.debug("imTables : " + imTables.get((String)tableNames[i]).toString());
				}
				
				JCO.Table table = paramList.getTable((String)tableNames[i]);
				Object []srcTable = (Object[])imTables.get((String)tableNames[i]);

				for (int row = 0; row < srcTable.length; row++) {
					table.appendRow();
					setRow(table, (Map<String, Object>)srcTable[row]);
	
					//log.debug("srcTable : " + srcTable[row].toString());
				}
			}
		} catch (Exception e) {
			log.debug("Exception : " + e.toString());
			log.debug("Exception : " + e);
		}
	}
	
	/**
	 * Export된 parameter를 분석하여 Java 형식의 Data structure를 구성
	 * 
	 * @author 하창주 (hachangjoo)
	 * 
	 * @param  JCO.ParameterList : Export된 JCO형식의 parameterList 
	 * @return Map imParams      : Java 형식의 Export Data
	 */
	private Map<String, Object> parseExportParams(JCO.ParameterList outParams)
	{
		Map<String, Object> retMap = new HashMap<String, Object>();
		log.debug("outParams : " + outParams.toString());
		for (int i = 0; i < outParams.getFieldCount(); i++) {
			log.debug("outParams : " +  outParams.getValue(i));
			retMap.put(outParams.getName(i), outParams.getValue(i));
		}
		
		return retMap;
	}
	
	/**
	 * Export된 Table을 분석하여 Java 형식의 Data structure를 구성
	 * 
	 * @author 하창주 (hachangjoo)
	 * 
	 * @param  JCO.ParameterList : Export된 JCO형식의 table list 
	 * @return Map imParams      : Java 형식의 Export Data
	 */
	private Map<String, Object> parseExportTables(JCO.ParameterList outTables)
	{
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		for (int i = 0; i < outTables.getFieldCount(); i++) {

			//Table object
			//JCO.Table table = outTables.getTable(outTables.getName(i));
			JCO.Table table = outTables.getTable(i);
			ArrayList<Object> tableData = new ArrayList<Object>();
			for (int rowNum = 0; rowNum < table.getNumRows(); rowNum++) { 
				table.setRow(rowNum);
				tableData.add(getRowData(table));
			}
			retMap.put(outTables.getName(i), tableData);
		}
		
		return retMap;
	}
	
	private Map<String, Object> getRowData(JCO.Table table) 
	{
		Map<String, Object> rowMap = new HashMap<String, Object>();
		for (int c = 0; c < table.getNumColumns(); c++) {
			rowMap.put(table.getName(c), table.getValue(c));
			//row출력
			//System.out.println("rowMap : "+table.getName(c) + ", " +table.getType(c) + ", " + table.getValue(c) + ", " + table.getString(c));
		}
		return rowMap;
	}

	private void setRow(JCO.Table table, Map<String, Object> srcRow)
	{
		for (int c = 0; c < table.getNumColumns(); c++) {
			//rowMap.put(table.getName(c), table.getValue(c));
			//table.setValue((String)srcRow.get(table.getName(c)), table.getName(c));
			table.setValue(srcRow.get(table.getName(c)), table.getName(c));
			//row출력
			//System.out.println("rowMap : "+table.getName(c) + ", " +srcRow.get(table.getName(c)));
		}
	}


}
