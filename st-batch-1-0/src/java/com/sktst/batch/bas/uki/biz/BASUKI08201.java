package com.sktst.batch.bas.uki.biz;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import oracle.jdbc.OracleDriver;

import com.eai.mq.api.EaiApiData;
import com.eai.mq.api.EaiMQApi;
import com.eai.mq.conf.EaiException;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준</li>
 * <li>설 명 : UKey I/F MQ 수신 Main 프로그램</li>
 * <li>작성일 : 2009-03-30</li>
 * </ul>
 *
 * @author 원종윤 (wonjongyoon)
 */
public class BASUKI08201 {

	private static final String PROG_ID = "BASUKI08201";
	private static final String LOG_PATH = "/app/st-1-0/batch/log/batch/applog";
	private static PrintWriter logFile;
	
	private static SimpleDateFormat todayFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    private static SimpleDateFormat timeFormatMilSec = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS", Locale.KOREA); 
	
	private static Properties connectionProperties = null;  
  
	/**
	 * 배치 프로그램 main 메소드를 수행한다.
	 *
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 *            
	 * @return void
	 *            
	 */
	public static void main(String[] args) {	


		
		String sDate = "";
		String sTime = "";
		String sRecvBuf = "";
		String sIFClass = "";
		byte[] sByte = null;
		
		int seq = 0;
		String errCode = "";
		String errMsg = "";
		
		Connection conn = null;
		CallableStatement cstmt = null;
		//DBConnResource1 resource = new DBConnResource1();		

		try {
			openLogFile(PROG_ID);
			
			int ret; // MQ의 리턴값 확인용 변수
			
			//conn = resource.getConnection();




if (connectionProperties == null) {
	// db property 파일을 읽는다.
	connectionProperties = new Properties();			
	String propertyPath = "/app/st-1-0/batch/build/com/sktst/batch/bas/uki/db.properties";
	
	try {
		connectionProperties.load(new FileInputStream(propertyPath));
	} catch (FileNotFoundException e) {
		throw new RuntimeException(e);
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
}

// db property 접속정보를 읽어온다.
//writeLogFile("DriverManager = "  );				

OracleDriver od = new oracle.jdbc.OracleDriver();

	DriverManager.registerDriver(od);

	conn = DriverManager.getConnection(connectionProperties
			.getProperty("database.url"), connectionProperties
			.getProperty("database.username"), connectionProperties
			.getProperty("database.password"));
	
			String sqlSP = new StringBuffer()
				.append("{                                           ")
				.append("    CALL SP_BASUKI00100                     ")
				.append("    /*+ BASUKI08100_main_원종윤_원종윤 */   ")
				.append("    (?, ?, ?, ?, ?, ?, ?)                   ")
				.append("}                                           ")
				.toString();

			cstmt = conn.prepareCall(sqlSP);
			EaiMQApi mqapi = new EaiMQApi();

			// EAI MQ API를 사용하기 위해 해당 큐 매니저에 연결 함
			ret = mqapi.mq_connect();

			EaiApiData ApiData = new EaiApiData();
			ApiData.initGet();		

			while(true) {
				ApiData.m_mqget_t.in_intf_id = "USCAN.ORG_INFO_MFF";
				
				if ((ret = mqapi.mq_get(ApiData)) != 0) {
					
					if (ret == -2) continue; // no message일 때 재시도

					if ((ret = mqapi.mq_rollback()) != 0) {
						throw new Exception("mqapi.mq_rollback error");
					}
					break;
				}
				
				sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
				sTime = new SimpleDateFormat("HHmmss").format(new Date());
				sRecvBuf = ApiData.m_mqget_t.out_recv_buf;
				sByte = sRecvBuf.getBytes();
				sIFClass = new String(sByte, 0, 1).trim(); 

				cstmt.setString(4, sDate);
				cstmt.setString(5, sTime);
				cstmt.setString(6, sIFClass);
				cstmt.setString(7, sRecvBuf);
				cstmt.registerOutParameter(1, Types.VARCHAR);  
				cstmt.registerOutParameter(2, Types.VARCHAR);
				cstmt.registerOutParameter(3, Types.INTEGER);
				cstmt.execute();
				conn.commit();
				
				seq = cstmt.getInt(3);
				
				// 에러메시지가 null이 아니면 로그 출력
				if(cstmt.getString(1) != null) {
					errCode = cstmt.getString(1);
					if(cstmt.getString(2) != null) {
						errMsg = cstmt.getString(2);
					}	
				}
				
				// Commit MQ
				if ((ret = mqapi.mq_commit()) != 0) {
					throw new Exception("mqapi.mq_commit error");
				}

			} // end while
			
			// EAI MQ API사용후 connection을 해제 
			if ((ret = mqapi.mq_disconnect()) != 0) {
				throw new Exception("mqapi.mq_disconnect error");
			}

		} catch(EaiException e) {
		} catch(Exception e) {
			if (conn == null) {
				try {
					conn.rollback();
				} catch (Exception re) {
					writeLogFile("Conn Exception = [" + re + "]");
				}
			}
			writeLogFile("Exception = [" + e + "]");
		} finally {
			if(conn != null) {
				try {
					//closeDB(conn, cstmt);
					
					if (cstmt != null) {
						try {
							cstmt.close();
						} catch (SQLException se) {
							throw new RuntimeException(se);
						}
					}
					if (conn != null) {
						try {
							conn.close();
						} catch (SQLException se) {
							throw new RuntimeException(se);
						}
					}
					
				} catch(Exception e){
					writeLogFile("DB Exception = [" + e + "]");
				}
			}
			closeLogFile();
		}
	}
	
    /**
	 * Log File을 open한다. 
	 * 
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param pgmId
	 *             파일명
	 * @return void
	 */		
	private static void openLogFile(String pgmId) throws IOException {
		String today = todayFormat.format(new Date());
		
		String dataPath = new StringBuffer()
			.append(LOG_PATH + "/")
			.append(PROG_ID + "_")
			.append(today + ".log")
			.toString();
		
		FileOutputStream rsTemp = new FileOutputStream(dataPath, true);
		logFile = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				rsTemp)), true);
		writeLogFile("* ----------<<< BASUKI08200 Program Starting >>>---------- *");
	}
	
	/**
	 * Log File에 write한다.
	 * 
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param message
	 *             로그파일에 기록할 메세지
	 * @return void
	 */		
	private static void writeLogFile(String message) {
		try {
			logFile.println("[" + timeFormatMilSec.format(new Date()) + "] " + message);
			logFile.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Log File을 close한다.
	 * 
	 * @author 원종윤 (wonjongyoon)
	 * 
	 * @param void
	 * 
	 * @return void
	 */	
	public static void closeLogFile() {
		long currentDate = System.currentTimeMillis();
		String endTime = timeFormatMilSec.format(new Date(currentDate));
		
		writeLogFile("------------------------------------------------------------");
		writeLogFile("End Time     : " + endTime);
		writeLogFile("* -----------<<< BASUKI08200 Program End >>>-------------- *");
	
		try {
			logFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
}

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/기준</li>
 * <li>설 명 : DB 접속/해제</li>
 * <li>작성일 : 2009-03-30</li>
 * </ul>
 *
 * @author 원종윤 (wonjongyoon)
 */
