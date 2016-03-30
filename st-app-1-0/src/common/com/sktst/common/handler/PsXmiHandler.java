package com.sktst.common.handler;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.Record;
import nexcore.framework.core.data.RecordHeader;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.online.channel.handler.internal.StandardXmiHandler;

import com.sktst.common.file.IPsFileUploadManager;
import com.tobesoft.platform.data.ColumnInfo;
import com.tobesoft.platform.data.Dataset;


/**
 * 
 * @author admin
 *
 */
public class PsXmiHandler extends StandardXmiHandler{
	
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
	 */
	protected void parseFileDataSet(IRecordSet ncRecordSet, Dataset dataset){
        //header setting
        String[] headerName = {"SCREEN_ID", 
        		"DOC_ID", 
        		"FILE_PASS", 
        		"FILE_NM", 
        		"FILE_TYPE", 
        		"FILE_SIZE", 
        		"DEL_YN", 
        		"UPD_CNT", 
        		"INS_DTM", 
        		"INS_USER_ID", 
        		"MOD_DTM", 
        		"MOD_USER_ID"};
        for(int i = 0; i < headerName.length; i++){
        	ncRecordSet.addHeader(new RecordHeader(headerName[i], getColumnType(ColumnInfo.COLUMN_TYPE_STRING)));	
        }

        //to set field value for cudAll                
        boolean bCudFlag = false;              

        
        // 삭제 데이터 레코드 설정
        int deleteDatasetSize = dataset.getDeleteRowCount();
        String sysDocId = String.valueOf(System.currentTimeMillis()); // 없을 경우 최초 시스템 설정 값
        for (int i = 0; i < deleteDatasetSize; i++) {
            IRecord ncRecord = new Record();            
            
            for (int colIndex = 0; colIndex < headerName.length; colIndex++) {
           		ncRecord.add(dataset.getDeleteColumn(i, headerName[colIndex]).asString());            	
            }
//            String screenId = dataset.getColumnAsString(i, "SCREEN_ID");
//            String docId = dataset.getColumnAsString(i, "DOC_ID");
//            String filename = dataset.getColumnAsString(i, "FILE_NM");
//            String fileType = dataset.getColumnAsString(i, "FILE_TYPE");
//
//        	/////////////////////////////////////////////////////////////////////////////
//        	// delete file..
//        	////////////////////////////////////////////////////////////////////////////
//            getFileManager().removeFile(screenId, docId, filename, fileType);                        
            ncRecord.add("delete");                    
            ncRecordSet.addRecord(ncRecord);
            bCudFlag = true;
        }
        
        // 데이터 레코드 설정
        int datasetSize = dataset.getRowCount();
        for (int i = 0; i < datasetSize; i++) {
            IRecord ncRecord = new Record();
            
            String screenId = dataset.getColumnAsString(i, "SCREEN_ID");
            String docId = dataset.getColumnAsString(i, "DOC_ID");
            if(docId == null || docId.trim().equals("")){
            	docId = sysDocId;            	
            }
            String filename = dataset.getColumnAsString(i, "FILE_NM");
            String fileType = dataset.getColumnAsString(i, "FILE_TYPE");            
            String filePass = getFileManager().getFileFullPath(screenId, docId, filename, fileType);
            
            
            for (int colIndex = 0; colIndex < headerName.length; colIndex++) {
            	if(headerName[colIndex].equals("FILE_PASS")){
            		ncRecord.add(filePass);
            	}else if(headerName[colIndex].equals("DOC_ID")){
            		ncRecord.add(docId);
            	}else{
            		ncRecord.add(dataset.getColumnAsString(i, headerName[colIndex]));
            	}
            }
            //to set field value for cudAll                   
            String rowStatus = dataset.getRowStatus(i);
            if("insert".equals(rowStatus)){
            	/////////////////////////////////////////////////////////////////////////////
            	// save file..
            	////////////////////////////////////////////////////////////////////////////
            	byte[] fileContents = dataset.getColumn(i, "FILE_CONTENTS").getBinary();
            	getFileManager().saveTemporaryFile(fileContents, screenId, docId, filename, fileType);            	
                ncRecord.add(rowStatus);                            
                bCudFlag = true;
            }
            ncRecordSet.addRecord(ncRecord);
        }
                       
        //to add header for cudAll
        if(bCudFlag){
            ncRecordSet.addHeader(new RecordHeader(getRecordStatusName(), getColumnType(ColumnInfo.COLUMN_TYPE_STRING)));
        }                
	}
	
}
