package com.sktst.batch.sac.erp.biz;

import org.apache.commons.logging.Log;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;


public class SapUtil {
	
	private static final String LANGUAGE 		= "ko";        

	private static String sapClient 	= "";
	private static String userId 		= "";
	private static String password 		= "";
	private static String ipAddress 	= "";
	private static String systemNumber 	= "";

	private static final String POOL_NAME = "DEV";
	
	
	/**
	 * Creates an instance of a client connection to a remote SAP system.
	 * @author	하창주 (hachangjoo)
	 * @param	log	Log 객체
	 * @return	mConnection
	 */	
	public static void setProperties(String pSapClient,
			String pUserId,
			String pPassword,
			String pIpAddress,
			String pSystemNumber ) {
		
		sapClient		= pSapClient;
		userId			= pUserId;
		password		= pPassword;
		ipAddress		= pIpAddress;
		systemNumber	= pSystemNumber;
	}

	/**
	 * Creates an instance of a client connection to a remote SAP system.
	 * @author	하창주 (hachangjoo)
	 * @param	log	Log 객체
	 * @return	mConnection
	 */	
	public static JCO.Client getConnection(Log log)
	{
		JCO.Client mConnection = null;
		try {
			mConnection = JCO.createClient(sapClient,
					userId,
					password,
					LANGUAGE,
					ipAddress,
					systemNumber);

			mConnection.connect();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (log.isDebugEnabled()) {
				log.debug("Exception : " + ex);
			}
		}
		return mConnection;
	}
	
	/*
	public void getConnection(Log log)
	{
		try {
			JCO.Pool pool = JCO.getClientPoolManager().getPool(POOL_NAME);
			if (pool == null) {
				OrderedProperties logonProperties = OrderedProperties.load("/logon.properties");

				JCO.addClientPool(POOL_NAME, 20, logonProperties);
				if (log.isDebugEnabled()) {
					log.debug("tcreated Connection : ");
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			if (log.isDebugEnabled()) {
				log.debug("Exception : " + ex);
			}
		}
	}
	*/

	/**
	 * Delete a client pool from the pool list.
	 *
	 * @author 하창주 (hachangjoo)
	 * 
	 * @param	void
	 * 
	 * @return	void
	 * 
	 */	
	protected void removeCleanUp()
	{
		JCO.removeClientPool(POOL_NAME);
	}

	
	/**
	 * Creates a new SAP system repository
	 *
	 * @author 하창주 (hachangjoo)
	 * 
	 * @param	JCO.Client
	 * 			Log	Log 객체
	 * 
	 * @return	IRepository
	 * 
	 */	
	public static IRepository getRepository(JCO.Client conn, Log log)
	{
		IRepository aRepository = null;
		try {
			if(conn == null) {
				conn = getConnection(log);
			}
			aRepository = new JCO.Repository("PSPL", conn);
		}catch (Exception ex) {
			ex.printStackTrace();
			if (log.isDebugEnabled()) {
				log.debug("Exception : " + ex);
			}
		}
		return aRepository;
	}
	
	/**
	 *  Creates a function object from the template and returns it
	 *
	 * @author 하창주 (hachangjoo)
	 * 
	 * @param	IRepository
	 * 			String
	 * 			Log	Log 객체
	 * 
	 * @return	JCO.Function
	 * 
	 */	
	public static JCO.Function getFunction(IRepository repo, String name, Log log)
	{
		JCO.Function function = null;
		try {
			IFunctionTemplate functionTemplate = repo.getFunctionTemplate(name);
			function = functionTemplate.getFunction();
		}catch (Exception ex) {
			ex.printStackTrace();
			if (log.isDebugEnabled()) {
				log.debug("Exception : " + ex);
			}
		}
		return function;
	}


}
