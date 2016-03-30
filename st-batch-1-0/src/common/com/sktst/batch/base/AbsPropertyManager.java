package com.sktst.batch.base;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/공통</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-03-30 </li>
 * </ul>
 *
 * @author 원종윤 (wonjongyoon)
 */

public abstract class AbsPropertyManager {
//	protected static final String PROPERTY_FILE = 
//		"batch.build.properties";
	protected static final String OS_ABB = "Win";
	
	/**
	 * OS에 따라 Property File의 Path의 위치를 결정한다.
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 * 
	 * @return property file path
	 * 
	 */
	protected String getPropertyPath() {
		String propertyPath = "";
		String osName = System.getProperty("os.name");
		
		// OS가 Windows인지 아닌지에 따라 property path 결정
		if(osName.substring(0, 3).equals(OS_ABB)) {
			propertyPath = "C:/project-workspace/st-batch-1-0/batch.build.properties";
		} else {
			propertyPath = "/home/webwas/.jenkins/jobs/test_batch/workspace/st-batch-1-0/batch.build.properties";
		}
		return propertyPath;
	}
	
	/**
	 * Property File로부터 property 값을 읽어오기 위한 메소드 선언
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 * 
	 * @return void
	 * 
	 */
	public void getProperties() {}
}
