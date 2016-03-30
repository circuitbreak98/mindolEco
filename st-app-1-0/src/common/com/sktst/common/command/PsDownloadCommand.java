package com.sktst.common.command;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.exception.BaseException;
import nexcore.framework.core.exception.BaseRuntimeException;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.parameter.IConfigurationManager;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.online.channel.core.ICommandViewMap;
import nexcore.framework.online.channel.core.IRequestContext;
import nexcore.framework.online.channel.core.IResponseContext;
import nexcore.framework.online.channel.core.command.AbstractCommand;
import nexcore.framework.online.channel.util.WebUtils;

import com.sktst.common.base.BaseConstants;
import com.sktst.common.file.IPsFileUploadManager;

/**
 * 
 * @author admin
 *
 */
public class PsDownloadCommand extends AbstractCommand {
	
	/**
	 * 
	 * @return
	 */
	private IPsFileUploadManager getFileManager(){
		IPsFileUploadManager fileManager = (IPsFileUploadManager)ComponentRegistry.lookup(ServiceConstants.FILEUPLOAD);
		return fileManager;
	}
	
    /**
     * 
     * @see nexcore.framework.online.channel.core.ICommand#execute(nexcore.framework.online.channel.core.IRequestContext,
     *      nexcore.framework.online.channel.core.ICommandViewMap)
     */
    public IResponseContext execute(IRequestContext requestCtx, ICommandViewMap cmdViewMap)
            throws BaseException, BaseRuntimeException {
    	
        HttpServletRequest request = (HttpServletRequest) (requestCtx.getReadProtocol());
        HttpServletResponse response = (HttpServletResponse) requestCtx.getWriteProtocol();
    	
        //IDataSet requestData = (IDataSet)requestCtx.getBizData();
        
//        String screenId = requestData.getField("SCREEN_ID");
//        String docId = requestData.getField("DOC_ID");
//        String filename = requestData.getField("FILE_NM");
//        String fileType = requestData.getField("FILE_TYPE");

      String screenId = request.getParameter("SCREEN_ID");
      String docId = request.getParameter("DOC_ID");
      String filename = request.getParameter("FILE_NM");
      String fileType = request.getParameter("FILE_TYPE");
        
      //response.reset();
      //response.resetBuffer();
      //response.setHeader("Content-Type", "application/unknown");
      //response.setHeader("Content-Disposition", "attachment;filename=\""+filename + "." + fileType + "\"");
        
        String clientFilename = filename + "." + fileType;
        String serverFilename = getFileManager().getRootDirectory() + getFileManager().getFileFullPath(screenId, docId, filename, fileType);
        
        IDataSet responseData = null;



        try {
            downloadFileByNameAndPath(request, response, clientFilename, serverFilename);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Exception Caughted during downloading file '" + serverFilename + "'.", e);
            }
            request.setAttribute("nexcore.bizlogic.exception", e);
            responseData = DataSetFactory.createWithOKResultMessage(BaseConstants.DOWNLOAD_FAIL, new String[]{});;
            //return getResponseContext(requestCtx, responseData, cmdViewMap);
            return getResponseContext("FAIL", requestCtx, responseData, cmdViewMap);
        }

        //responseData = DataSetFactory.createWithOKResultMessage(BaseConstants.DOWNLOAD_SUCCESS, new String[]{});;        
    	//return getResponseContext(requestCtx, responseData, cmdViewMap);
        return getResponseContext("SUCCESS", requestCtx, responseData, cmdViewMap);
    }    
    
    /**
     * Perform download with parameter 'file' and 'fileName'
     * 
     * @param req
     * @param res
     * @param fileName
     * @param filePath
     * @throws IOException
     */
    private void downloadFileByNameAndPath(HttpServletRequest req, HttpServletResponse res,
                             String clientFilename, String serverFilename) throws IOException{
    	


        File file = new File(serverFilename);


        if (!file.exists()) {
            throw new BaseRuntimeException("The " + file.getName()
                    + " file does not exist.");
        }

        
        BufferedInputStream in = null;
        ServletOutputStream out = null;       
        FileInputStream fis = new FileInputStream(file);
        //BufferedOutputStream out = null;        
        try {
            in = new BufferedInputStream(fis);
            WebUtils.presetForDownload(req, res, clientFilename);
            
            
            byte[] buffer = new byte[getDownloadBufferSize()];
            int num = 0;
            out = res.getOutputStream();           
            //while ((num = in.read(buffer)) != -1) {
            while ((num = in.read(buffer,0,getDownloadBufferSize())) != -1) {
                out.write(buffer, 0, num);
            }
            out.flush();
		} catch (IOException ex) {
			ex.printStackTrace();//임시 로그 
        } finally {
            if (fis != null) fis.close();
            if (in != null) in.close();
            if (out != null) out.close();
        }

    }
    
    /**
     * 
     * @return
     */
    private int getDownloadBufferSize(){
		IConfigurationManager configMng = (IConfigurationManager)ComponentRegistry.lookup(ServiceConstants.CONFIGURATION);
		String bufferSize = configMng.getConfig("download.file.buffersize");
		if(bufferSize == null || bufferSize.trim().equals("")){
			return 1024;
		}else{
			return Integer.parseInt(bufferSize);
		}
    	
    }
}
