package com.sktst.common.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.parameter.IConfigurationManager;
import nexcore.framework.core.prototype.AbsFwkService;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.core.util.FileUtils;
import nexcore.framework.integration.db.ISqlManager;
import nexcore.framework.integration.db.NoRecordAffectedException;
import nexcore.framework.integration.db.NoRecordFoundException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * 
 * @author admin
 *
 */
/**
 * @author admin
 *
 */
public class PsFileUploadManager extends AbsFwkService implements IPsFileUploadManager{

	
	/**
	 * 
	 */
    protected ISqlManager sqlManager;

    /**
     * 
     * @param sm
     */
    public void setSqlManager(ISqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    /**
     * 
     * @return
     */
	public String getRootDirectory() {
		IConfigurationManager configMng = (IConfigurationManager)ComponentRegistry.lookup(ServiceConstants.CONFIGURATION);
		String fileRoot = configMng.getConfig("upload.file.root.directory");
		if(fileRoot == null){
			return "";
		}
		if(!fileRoot.endsWith("/")){
			fileRoot = fileRoot + "/";
		}
		return fileRoot;
	}

	/**
	 * 
	 * @return
	 */
	public String getTemporaryDirectory() {
		IConfigurationManager configMng = (IConfigurationManager)ComponentRegistry.lookup(ServiceConstants.CONFIGURATION);
		String fileRoot = configMng.getConfig("upload.file.temp.directory");
		if(fileRoot == null){
			return "";
		}
		if(!fileRoot.endsWith("/")){
			fileRoot = fileRoot + "/";
		}
		return fileRoot;
	}
	
	/* (non-Javadoc)
	 * @see com.sktst.common.file.IPsFileManager#getFileFullPath(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getFileFullPath(String screenId, String docId, String filename, String fileType){		
		StringBuffer filePathBuffer = new StringBuffer();
		if(screenId != null && !screenId.equals("")){
			filePathBuffer.append(screenId);
			filePathBuffer.append(File.separator);
		}
		filePathBuffer.append(filename).append("_").append(docId).append(".").append(fileType);
        return filePathBuffer.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.sktst.common.file.IPsFileManager#saveTemporaryFile(byte[], java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void saveTemporaryFile(byte[] fileContents, String screenId, String docId, String filename, String fileType) {
		ByteArrayInputStream bis = null;
		FileOutputStream fos = null;
		
		try {	        
	        File tempDir = new File(getTemporaryDirectory());
	        if(!tempDir.exists()){
	        	tempDir.mkdirs();
	        }
	        String filePath = getFileFullPath(null, docId, filename, fileType);
	        bis = new ByteArrayInputStream(fileContents);
	        fos = new FileOutputStream(getTemporaryDirectory() + filePath);
	        	        
	        byte[] buffer = new byte[(int)fileContents.length];  
			int len = 0;
			int offset = 0;
	
			while ((len = bis.read(buffer, offset, buffer.length)) != -1 );
			len = bis.read(buffer, offset, buffer.length);	
			fos.write(buffer);
			
		} catch (IOException ex) {
			ex.printStackTrace();//임시 로그 
		} finally {
			try {
				if (bis != null) bis.close();
				if (fos != null) fos.close();
			} catch (IOException ex) {
				bis = null;
				fos = null;
			}
		}		
	}

	/**
	 * 
	 * @param screenId
	 * @param docId
	 * @param filename
	 * @param fileType
	 */
	public void removeTemporaryFile(String screenId, String docId, String filename, String fileType) {
        String filePath = getFileFullPath(null, docId, filename, fileType);
        File file = new File(getTemporaryDirectory() + filePath);
        if(file.exists()){
            file.delete();            	
        }
	}

	/**
	 * 
	 * @param screenId
	 * @param docId
	 * @param filename
	 * @param fileType
	 */
	public void removeFile(String screenId, String docId, String filename, String fileType) {
        String filePath = getFileFullPath(screenId, docId, filename, fileType);
        File file = new File(getRootDirectory() + filePath);
        if(file.exists()){
            file.delete();            	
        }
	}
	
	/**
	 * 
	 * @param screenId
	 * @param docId
	 * @param filename
	 * @param fileType
	 */
	public void copyFileToRootDirectory(String screenId, String docId, String filename, String fileType) {		
		String tempFilename = getTemporaryDirectory() + getFileFullPath(null, docId, filename, fileType);
		String realFilename = getRootDirectory() + getFileFullPath(screenId, docId, filename, fileType);

		//System.out.println(realFilename);
		try{
			File screenDir = new File(getRootDirectory() + screenId);
	        if(!screenDir.exists()){
	        	screenDir.mkdirs();
	        }		
			FileUtils.copyFile(tempFilename, realFilename);
		}catch(Exception ex){
			ex.printStackTrace();//임시 로그
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sktst.common.file.IPsFileManager#commitFile(java.lang.String, nexcore.framework.core.data.IDataSet)
	 */	
	public void commitFile(String datasetKey, IDataSet requestData){
		
		IRecordSet rs = requestData.getRecordSet(datasetKey);
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (BaseBizUnit.DELETE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(BaseBizUnit.CUD_FLAG_PARAM))) {
					//파일 삭제의 경우, 실제 파일 삭제.
					String screenId = rs.get(i, "SCREEN_ID");
					String docId = rs.get(i, "DOC_ID");
					String fileNm = rs.get(i, "FILE_NM");
					String fileType = rs.get(i, "FILE_TYPE");
					removeFile(screenId, docId, fileNm, fileType);
				}
			}
			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (BaseBizUnit.INSERT_FLAG.equalsIgnoreCase(rs.getRecord(i).get(BaseBizUnit.CUD_FLAG_PARAM))) {
					//파일 업로드의 경우
					String screenId = rs.get(i, "SCREEN_ID");					
					String docId = rs.get(i, "DOC_ID");
					String fileNm = rs.get(i, "FILE_NM");
					String fileType = rs.get(i, "FILE_TYPE");

					//System.out.println(screenId+"|"+docId+"|"+fileNm+"|"+fileType);
					//실제 파일 보관 위치에 업로드한 파일 복사.
					copyFileToRootDirectory(screenId, docId, fileNm, fileType);
					
					//임시 업로드 파일 삭제.		
					removeTemporaryFile(screenId, docId, fileNm, fileType);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sktst.common.file.IPsFileManager#rollbackFile(java.lang.String, nexcore.framework.core.data.IDataSet)
	 */
	public void rollbackFile(String datasetKey, IDataSet requestData){
		IRecordSet rs = requestData.getRecordSet(datasetKey);
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (BaseBizUnit.INSERT_FLAG.equalsIgnoreCase(rs.getRecord(i).get(BaseBizUnit.CUD_FLAG_PARAM))) {
					//파일 업로드의 경우
					String screenId = rs.get(i, "SCREEN_ID");					
					String docId = rs.get(i, "DOC_ID");
					String fileNm = rs.get(i, "FILE_NM");
					String fileType = rs.get(i, "FILE_TYPE");
					//임시 업로드 파일 삭제.
					removeTemporaryFile(screenId, docId, fileNm, fileType);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.sktst.common.file.IPsFileManager#putFileInfo(nexcore.framework.core.data.IDataSet, nexcore.framework.core.data.IOnlineContext)
	 */
	public IDataSet putFileInfo(IDataSet requestData, IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("IPsFileManager.putFileInfo method start");
		}
			
		sqlManager.insert("biz.file.insertFileInfo", requestData.getFieldMap(), onlineCtx);
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "1" });
	}

	/* (non-Javadoc)
	 * @see com.sktst.common.file.IPsFileManager#deleteFileInfo(nexcore.framework.core.data.IDataSet, nexcore.framework.core.data.IOnlineContext)
	 */
	public IDataSet deleteFileInfo(IDataSet requestData, IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("IPsFileManager.putFileInfo method start");
		}
			
		int deleteCount = sqlManager.delete("biz.file.deleteFileInfo", requestData.getFieldMap(), onlineCtx);
		if (deleteCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.DELETE_OK_MESSAGE_ID, new String[] { ""
						+ deleteCount });
	}
	
	/* (non-Javadoc)
	 * @see com.sktst.common.file.IPsFileManager#getFileInfo(nexcore.framework.core.data.IDataSet, nexcore.framework.core.data.IOnlineContext)
	 */
	public IDataSet getFileInfo(IDataSet requestData, IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("IPsFileManager.getFileInfo method start");
		}
			
		Map map = (Map) sqlManager.queryForObject("biz.file.selectFileInfo", requestData.getFieldMap(), onlineCtx);
		if (map == null) {
			throw new NoRecordFoundException();
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });
		responseData.putFieldMap(map);
		return responseData;
	}
	
	/* (non-Javadoc)
	 * @see com.sktst.common.file.IPsFileManager#getFileInfoList(java.lang.String, nexcore.framework.core.data.IDataSet, nexcore.framework.core.data.IOnlineContext)
	 */
	public IDataSet getFileInfoList(String datasetKey, IDataSet requestData, IOnlineContext onlineCtx) {
		
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("IPsFileManager.getFileInfoList method start");
		}
			
		IRecordSet rs = sqlManager.queryForRecordSet("biz.file.selectFileInfoList",	requestData.getFieldMap(), onlineCtx);
		if (rs == null) {
			rs = new RecordSet(datasetKey);
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(BaseConstants.QUERY_OK_MESSAGE_ID, 
				new String[] { String.valueOf(rs.getRecordCount()) });
		
		responseData.putRecordSet(datasetKey, rs);
		return responseData;
	}
	
	/* (non-Javadoc)
	 * @see com.sktst.common.file.IPsFileManager#getFileInfoList(nexcore.framework.core.data.IDataSet, nexcore.framework.core.data.IOnlineContext)
	 */
	public IRecordSet getFileInfoList2Rs(IDataSet requestData, IOnlineContext onlineCtx) {
		
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("IPsFileManager.getFileInfoList method start");
		}
			
		IRecordSet rs = sqlManager.queryForRecordSet("biz.file.selectFileInfoList",	requestData.getFieldMap(), onlineCtx);

		return rs;
	}

	 
	/* (non-Javadoc)
	 * @see com.sktst.common.file.IPsFileManager#saveAllFileInfo(java.lang.String, nexcore.framework.core.data.IDataSet, nexcore.framework.core.data.IOnlineContext)
	 */
	public int saveAllFileInfo(String datasetKey, IDataSet requestData, IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("IPsFileManager.saveFileToDB method start");
		}
		
		int allCount = 0;
		int insertCount = 0;
		int deleteCount = 0;
		
		IRecordSet rs = requestData.getRecordSet(datasetKey);		
		
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (BaseBizUnit.DELETE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(BaseBizUnit.CUD_FLAG_PARAM))) {
					deleteCount = deleteCount + sqlManager.delete("biz.file.deleteFileInfo", rs.getRecord(i), onlineCtx);
				}
			}
			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (BaseBizUnit.INSERT_FLAG.equalsIgnoreCase(rs.getRecord(i).get(BaseBizUnit.CUD_FLAG_PARAM))) {
					sqlManager.insert("biz.file.insertFileInfo", rs.getRecord(i), onlineCtx);
					insertCount++;
				}
			}
			allCount = insertCount + deleteCount;
		}
		return allCount;
	}

	/* (non-Javadoc)
	 * @see com.sktst.common.file.IPsFileUploadManager#getDocId(java.lang.String, nexcore.framework.core.data.IDataSet)
	 */
	public String getDocId(String datasetKey, IDataSet requestData) {
		IRecordSet rs = requestData.getRecordSet(datasetKey);
		String docId = null;
		if (rs != null) {
			docId = rs.get(0, "DOC_ID");
		}		
		return docId;
	}
	

}
