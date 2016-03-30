package com.sktst.common.file;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;

/**
 * 
 * @author admin
 *
 */
public interface IPsFileUploadManager {

	/**
	 * 파일 디렉토리 루트 취득
	 * @return
	 */
	String getRootDirectory();
//	
//	/**
//	 * 임시 파일 디렉토리 취득
//	 * @return
//	 */
//	String getTemporaryDirectory();
	
	/**
	 * 파일 패스 리턴.
	 * @param screenId
	 * @param docId
	 * @param filename
	 * @param fileType
	 * @return
	 */
	String getFileFullPath(String screenId, String docId, String filename, String fileType);

	/**
	 * 임시 파일 저장 처리
	 * @param fileContents
	 * @param screenId
	 * @param docId
	 * @param filename
	 * @param fileType
	 */
	void saveTemporaryFile(byte[] fileContents, String screenId, String docId, String filename, String fileType);
	
//	/**
//	 * 파일 삭제 처리
//	 * @param screenId
//	 * @param docId
//	 * @param filename
//	 * @param fileType
//	 */
//	void removeFile(String screenId, String docId, String filename, String fileType);
//	
//	/**
//	 * 임시 파일을 실제 파일로 복사하는 처리
//	 * @param screenId
//	 * @param docId
//	 * @param filename
//	 * @param fileType
//	 */
//	void copyFileToRootDirectory(String screenId, String docId, String filename, String fileType);

	/**
	 * 
	 * @param datasetKey
	 * @param requestData
	 */
	void commitFile(String datasetKey, IDataSet requestData);
	
	/**
	 * 
	 * @param datasetKey
	 * @param requestData
	 */
	void rollbackFile(String datasetKey, IDataSet requestData);
	
	/**
	 * 
	 * @param requestData
	 * @param onlineCtx
	 * @return
	 */
	IDataSet putFileInfo(IDataSet requestData, IOnlineContext onlineCtx);
	
	/**
	 * 
	 * @param requestData
	 * @param onlineCtx
	 * @return
	 */
	IDataSet deleteFileInfo(IDataSet requestData, IOnlineContext onlineCtx);
	
	/**
	 * 
	 * @param requestData
	 * @param onlineCtx
	 * @return
	 */
	IDataSet getFileInfo(IDataSet requestData, IOnlineContext onlineCtx);
	
	/**
	 * 
	 * @param datasetKey
	 * @param requestData
	 * @param onlineCtx
	 * @return
	 */
	IDataSet getFileInfoList(String datasetKey, IDataSet requestData, IOnlineContext onlineCtx);
	
	/**
	 * 
	 * @param requestData
	 * @param onlineCtx
	 * @return
	 */
	IRecordSet getFileInfoList2Rs(IDataSet requestData, IOnlineContext onlineCtx);
	
	
	/**
	 * 
	 * @param datasetKey
	 * @param requestData
	 * @param onlineCtx
	 * @return
	 */
	int saveAllFileInfo(String datasetKey, IDataSet requestData, IOnlineContext onlineCtx);
	
	/**
	 * 
	 * @param datasetKey
	 * @param requestData
	 * @return
	 */
	String getDocId(String datasetKey, IDataSet requestData);
	
}