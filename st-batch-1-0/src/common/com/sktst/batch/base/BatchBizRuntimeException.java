package com.sktst.batch.base;

/**
 * 
 * @author admin
 *
 */
public class BatchBizRuntimeException extends RuntimeException{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2884610989775528106L;

	/**
	 * 
	 *
	 */
	public BatchBizRuntimeException(){
		super();
	}
	
	/**
	 * 
	 * @param message
	 */
	public BatchBizRuntimeException(String message){
		super(message);
	}
	
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public BatchBizRuntimeException(String message, Throwable cause){
		super(message, cause);
	}

}
