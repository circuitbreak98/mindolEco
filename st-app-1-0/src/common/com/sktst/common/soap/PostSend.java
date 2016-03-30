package com.sktst.common.soap;

import java.io.*;
import java.util.*;
import java.net.*;
import org.apache.commons.logging.Log;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.log.LogManager;


public class PostSend {
	
	private String MNAME = "PostSend";
	
	public PostSend(){}
	
	public void log(String loc, String log, IOnlineContext onlineCtx){
		
		Log logs = LogManager.getLog(onlineCtx);
		
		if (logs.isDebugEnabled()) {
			logs.debug("sendData start >>>>>>>");
		}
		
		logs.debug("[위치]"+log+", [메세지]"+log);
	}
	
	public String sendData(Hashtable inputhash, IOnlineContext onlineCtx){
		
		
		Log log = LogManager.getLog(onlineCtx);
		
		log.debug("sendData start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		if (log.isDebugEnabled()) {
			log.debug("sendData start >>>>>>>");
		}
				
		String ip = null;
		String port = null;
		String reqstr = null;
		String urlpath = null;
		String Referer = null;
		
		String errstr = "";
		boolean errflag = false;
		
		if((ip = (String)inputhash.get("ip"))==null){
			
			errflag = true;
			errstr += "[ip]";
		}
		
		
		if((ip = (String)inputhash.get("port"))==null){
			
			errflag = true;
			errstr += "[port]";
		}
		
		if((ip = (String)inputhash.get("reqstr"))==null){
					
			errflag = true;
			errstr += "[reqstr]";
		}
		
		if((ip = (String)inputhash.get("Referer"))==null){
			
			errflag = true;
			errstr += "[Referer]";
		}
		
		if(errflag){
			log(MNAME+".sendData()","정보가없습니다.\n"+errstr,onlineCtx);
			return null;
		}
		
		Socket soc = null;
		PrintWriter out = null;
		BufferedReader in =null;
		
		String result = null;
		
		try{
			
			soc = new Socket(ip, Integer.parseInt(port));
			out = new PrintWriter(soc.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
		}catch(UnknownHostException ukhe){
			log(MNAME+".sendData()","11111111111111111111111======"+ukhe.toString(),onlineCtx);
			return null;
		}catch(IOException ioe){
			log(MNAME+".sendData()","22222222222222222222222======"+ioe.toString(),onlineCtx);
			return null;
		}catch(Exception e){
			log(MNAME+".sendData()","33333333333333333333333======"+e.toString(),onlineCtx);
			return null;
		}
		
		try{
			byte[] reqbyte = reqstr.getBytes();
			int reglen = reqbyte.length;
			
			String sendstr = null;
			sendstr = "POST "+urlpath+" HTTP/1.0\n";
			sendstr += "Referer: "+Referer+"\n";
			sendstr += "Content-type:application/x-www-form-urlencoded\n";
			sendstr += "Content-length:"+reglen+"\n\n";
			sendstr += reqstr;
			
			out.println(sendstr);
			
			String getstr = null;
			int linecnt = 0;
			
			while((getstr = in.readLine()) != null){
				linecnt++;
				
				if(linecnt == 2){
					result = getstr;
				}
			}
			
			return result;
			
		}catch(Exception e){
			log(MNAME+".sendData()","전송실패======"+e.toString(),onlineCtx);
		}finally{
			
			try{
				if(out != null) out.close();
				if(in != null) in.close();
				if(soc !=null) soc.close();
			}catch(Exception e1){}
			
		}
		
		return result;
		
		
	}
}

