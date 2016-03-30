package com.sktst.batch.dis.uer.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;
import com.sktst.batch.util.crypt.*;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/재고</li>
 * <li>설 명 : 신세계 엑서사리 data import</li>
 * <li>작성일 : 2009-03-30 09:00:00</li>
 * </ul>
 *
 * @author 장화수 (janghwasoo)
 */
public class DISUER08200 extends AbsBatchJobBiz {

	private static final String PROG_ID = "DISUER08200";
	private static final String USER_ID = "DISUER0820";

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 장화수 (janghwasoo)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	public int execute(Map request) throws Exception {
		super.getProperties();
		
		logMng.openLogFile(PROG_ID);
		
		if (log.isDebugEnabled()) {
			logMng.writeLogFile("Shinsegae Data import start......");
			log.debug(PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();
		
		String sFileName = "";
		if( request.size() > 1 ) {
			sFileName = (String)request.get("args1");
		}
		
		if (sFileName.equals("")) {
			Calendar cl = Calendar.getInstance(Locale.KOREA);
			cl.add(Calendar.DATE, -1);                                 // 작업이 새벽에 수행 되므로 일자를 하루 감해서 Working 일자를 Setting 한다.
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date   cDate    = cl.getTime();
			sFileName = sdf.format(cDate);
			
		}
		
		logMng.writeLogFile("args1 : " + sFileName);

		try {
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

			// 업무 시작.
			sqlMap.startTransaction();
			
			// FILE을 읽어서 DB insert.
			openUrl(sqlMap, sFileName);
			logMng.writeLogFile("------------------------------------------------------------");

			// 처리 결과 commit
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.commitTransaction");
			}
			sqlMap.commitTransaction();

			logMng.writeLogFile("Shinsegae Data import completed......");
			log.debug("######### Shinsegae Data import completed......");

		} catch(Exception e) {
			//System.out.println(e.getMessage());
			logMng.writeLogFile("ERRCODE:F");
			logMng.writeLogFile("execute Exception : " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("ERRCODE:F");
				log.debug("execute Exception : " + e.getMessage());
				log.debug("######### Shinsegae Data import fail......");
			}
		} finally {
			// 처리 완료. (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.endTransaction");
			}
			sqlMap.endTransaction();
			logMng.closeLogFile();
			
		}
		
		return 0;
	}

	
	
	
	/**
	 * 신세계 xml url 에 접속한다.
	 *
	 * @author 장화수 (janghwasoo)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fFileName
	 *            파일명
	 * @return void
	 * 
	 * @throws Exception
	 */
	
	private void openUrl(SqlMapClient sqlMap, String fFileName) throws Exception {

		
		Map<String, Object> itemMap    = new HashMap<String, Object>();
		itemMap.put("tran_dt", fFileName);

		Map<String, Object> resultCnt = (Map)sqlMap.queryForObject("DISUER08200.getCntShinsegaeData", itemMap);
		int sCnt   = Integer.parseInt(resultCnt.get("SCNT").toString());
		
		if ( sCnt > 0 ) {

			logMng.writeLogFile("Fail : 이미 ERP 전송이 되어 있습니다...... "+sCnt+"건");
			log.debug("######### Fail : 이미 ERP 전송이 되어 있습니다...... "+sCnt+"건");

		} else {

			int sAccess = 1;
			//문서팩토리생성
			DocumentBuilderFactory domf = DocumentBuilderFactory.newInstance();
			//문서생성
			DocumentBuilder builder = null;
			try {
				builder = domf.newDocumentBuilder();
			
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			//문서위치
			Document xmlDoc = null;
			try {
				xmlDoc = builder.parse("http://asp2.good-md.com:8001/xml/psm/"+fFileName+".xml");
			} catch (SAXException e) {
				logMng.writeLogFile("shinsegae good-md server error......!!!");
				log.debug("######### shinsegae good-md server error......!!!");
				sAccess = 0;
				//e.printStackTrace();
			} catch (IOException e) {
				logMng.writeLogFile("NO DATA......!!!");
				log.debug("######### NO DATA......!!!");
				//e.printStackTrace();
				sAccess = 0;				
			}
			
			if ( sAccess > 0 ) {

				logMng.writeLogFile("Shinsegae Good-md Server Access Success......!!!");
				log.debug("######### Shinsegae Good-md Server Access Success......!!!");

				openDataXmlAddDB(sqlMap, fFileName, xmlDoc, itemMap);
				
				
			}
			
			
		}
		
		
		

	}
	
	
	/**
	 * 파일을 읽어서 DB에 기록한다.
	 *
	 * @author 장화수 (janghwasoo)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @param fFileName
	 *            파일명
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void openDataXmlAddDB(SqlMapClient sqlMap, String fFileName, Document xmlDoc, Map<String, Object> itemMap) throws Exception {
		
		
		logMng.writeLogFile("openDataFileAddDB method start......");
		log.debug("######### openDataFileAddDB method start......");

		int sSeq = 0;
		short nodeType = xmlDoc.getNodeType();
		
		//문서구성
		Element em = xmlDoc.getDocumentElement();
		
		NodeList nd = xmlDoc.getElementsByTagName("ResultList");
		//NodeList nd = xmlDoc.getElementById("ResultList");
		NodeList ni = null;
		
		String sItem = "";
		String[] itemArray = null;
		//String[] itemArray;


		String recCl = "";	
		String tranCl = "";	
		String tranDt = "";	
		String dealCoCd = "";
		String amt = "";	
		String inCoCd = "";	
		String cardCoCd = "";	
		
		sqlMap.delete("DISUER08200.deleteShinsegaeData", itemMap);

		for (int i=0; i<nd.getLength(); ++i) {
			ni = nd.item(i).getChildNodes();			
			//System.out.println(nd.item(i).getNodeName());
			logMng.writeLogFile(nd.item(i).getNodeName());
			for (int j=0; j<ni.getLength()-1; ++j) {
				sItem = aesUtils.decrypt(ni.item(j).getTextContent());
				if (!sItem.equals("")) {
					sSeq++;
					log.debug("######### " + sSeq + " ===" + sItem);	
					logMng.writeLogFile("=== " + sSeq + " ===" + sItem);
					
					itemArray = sItem.split("\\|",8);

					log.debug("######### StringToken Success ");
					
					
					//순서바꾸지 말것

					recCl = itemArray[0].trim().replaceAll("\\|", ""); 
					tranCl = itemArray[1].trim().replaceAll("\\|", "");
					tranDt = itemArray[2].trim().replaceAll("\\|", "");
					dealCoCd = itemArray[3].trim().replaceAll("\\|", "");
					amt = itemArray[4].trim().replaceAll("\\|", ""); 
					inCoCd = itemArray[5].trim().replaceAll("\\|", ""); 
					cardCoCd = itemArray[6].trim().replaceAll("\\|", "");					
					log.debug("######### StringToken get a data Success");

					
					itemMap.put("seq",         sSeq);
					itemMap.put("rec_cl",      recCl);
					itemMap.put("tran_cl",     tranCl);
					itemMap.put("tran_dt",     tranDt);
					itemMap.put("deal_co_cd",  dealCoCd);
					itemMap.put("amt",         amt);
					itemMap.put("in_co_cd",    inCoCd);
					itemMap.put("card_co_cd",  cardCoCd);

                    /**
					log.debug("######### seq = "+sSeq);
					log.debug("######### rec_cl = "+recCl);
					log.debug("######### tran_cl = "+tranCl);
					log.debug("######### tran_dt = "+tranDt);	
					log.debug("######### deal_co_cd = "+dealCoCd);	
					log.debug("######### amt = "+amt);	
					log.debug("######### in_co_cd = "+inCoCd);
					log.debug("######### card_co_cd = "+cardCoCd);	
					**/


					sqlMap.update("DISUER08200.addXmlShinsegaeData", itemMap);
					itemArray = null;
					
				}
			}
			
		}
		logMng.writeLogFile("Insert Count : " + sSeq);
		logMng.writeLogFile("Success : openDataXmlAddDB method end......");
		log.debug("######### Insert Count : " + sSeq);
		log.debug("######### Success : openDataXmlAddDB method end......");
		
	}
	
	
}
