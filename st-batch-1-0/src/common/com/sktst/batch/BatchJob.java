package com.sktst.batch;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.sktst.batch.base.IBatchJobBiz;
import com.sktst.batch.log.LogManager;

/**
 * 배치 프로그램을 시작하는 클래스.
 * @author admin
 */
public class BatchJob {

	private static final String beanConfigFile = "BeanConfig.xml";
	private static final String beanNameKey = "nc_beanName";
	private static Log fwkLog = LogManager.getFwkLog();
	
	SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
	long startTime = System.currentTimeMillis();
	protected static String logPath = "";
	protected static String inFilePath = "";
	protected static String outFilePath = "";
	
	/**
	 * 배치 프로그램 시작.
	 * @param args
	 */
	public static void main(String[] args){
		if(fwkLog.isInfoEnabled()){
			fwkLog.info("BatchJob call..");
		}
		int result = 0;
		BatchJob service =  new BatchJob();
		try{
			Map request = service.checkArgs(args);
				result = service.run(request);
			
		}catch(Exception ex){
			//exception
			fwkLog.warn("Exception : " + ex.getMessage());
			fwkLog.error(ex.getMessage(), ex);
			//System.exit(1);
		}
		//System.exit(result);
	}

	/**
	 * 파라메터를 체크한다.
	 * @param args
	 * @return
	 */
	public Map checkArgs(String[] args){
		if(fwkLog.isInfoEnabled()){
			fwkLog.info("Checking arguments..");
		}
		if(args.length < 1){
			fwkLog.error("com.sktst.batch.BatchService class needs argument..");
			throw new RuntimeException("com.sktst.batch.BatchService class needs argument..");
		}
		Map<String, String> result = new HashMap<String, String>();
		
		result.put(beanNameKey, args[0]); //batch bean name setting.
		fwkLog.info("arglength::" + args.length);
		for(int i = 1; i < args.length; i++){
			fwkLog.info("args" + "[" + i + "]" + args[i]);
			result.put("args" + i, args[i]);
		}
		if(fwkLog.isInfoEnabled()){
			fwkLog.info("Success arguments checking.");
		}		
		return result;
	}	

	/**
	 * 
	 * @param request
	 * @return
	 */
	public int run(Map request) throws Exception{
		if(fwkLog.isInfoEnabled()){
			fwkLog.info("Checking Bean Configuration..");
		}		
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource(beanConfigFile)); // 1.437초
		String beanName = (String)request.get(beanNameKey); // 0.003초
		IBatchJobBiz bizUnit = (IBatchJobBiz)factory.getBean(beanName); // 0.155초
		if(bizUnit == null){
			fwkLog.error(beanName + " is not set. please check Bean Configuration..");
			return 0;
		}
		if(fwkLog.isInfoEnabled()){
			fwkLog.info("Success Bean Configuration checking.");
			fwkLog.info("execute batch job : " + bizUnit.getClass().getName());
		}
		int result = bizUnit.execute(request);
		if(fwkLog.isInfoEnabled()){
			fwkLog.info("Completed batch job : " + bizUnit.getClass().getName());
		}
		return result;
	}
}