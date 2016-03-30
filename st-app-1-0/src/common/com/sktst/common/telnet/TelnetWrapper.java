/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.common.telnet;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

import java.util.Vector;
import java.util.Properties;

import java.awt.Dimension;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/정산</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-12-01 10:51:07</li>
 * </ul>
 *
 * @author 장화수 (janghwasoo)
 */

public class TelnetWrapper extends TelnetProtocolHandler {

  /** debugging level */
  private final static int debug = 0;

  protected ScriptHandler scriptHandler = new ScriptHandler();
  private Thread reader;

  protected InputStream in;
  protected OutputStream out;
  protected Socket socket;
  protected String host;
  protected int port = 23;
  protected Vector script = new Vector();

  /** Connect the socket and open the connection. */
  public void connect(String host, int port) throws IOException {
    if(debug>0) System.err.println("TelnetWrapper: connect("+host+","+port+")");
    try {
      socket = new java.net.Socket(host, port);
      in = socket.getInputStream();
      out = socket.getOutputStream();
      reset();
    } catch(Exception e) {
      System.err.println("TelnetWrapper: "+e);
      disconnect();
      throw ((IOException)e);
    }
  }  
 
  /** Disconnect the socket and close the connection. */
  public void disconnect() throws IOException {
    if(debug>0) System.err.println("TelnetWrapper: disconnect()");
    if (socket != null)
	socket.close();
  }

  /** sent on IAC EOR (prompt terminator for remote access systems). */
  public void notifyEndOfRecord() {
  }

  /**
   * Login into remote host. This is a convenience method and only
   * works if the prompts are "login:" and "Password:".
   * @param user the user name 
   * @param pwd the password
   */
  public void login(String user, String pwd) throws IOException {
    waitfor("login:");		// throw output away
    send(user);
    waitfor("Password:");	// throw output away
    send(pwd);
  }

  /**
   * Set the prompt for the send() method.
   */
  private String prompt = null;
  public void setPrompt(String prompt) {
    this.prompt = prompt;
  }

  /**
   * Send a command to the remote host. A newline is appended and if
   * a prompt is set it will return the resulting data until the prompt
   * is encountered.
   * @param cmd the command
   * @return output of the command or null if no prompt is set
   */
  public String send(String cmd) throws IOException {
    byte arr[]; 
    arr = (cmd+"\n").getBytes();
    transpose(arr);
    if(prompt != null)
      return waitfor(prompt);
    return null;
  }

  /**
   * Wait for a string to come from the remote host and return all
   * that characters that are received until that happens (including
   * the string being waited for).
   *
   * @param match the string to look for
   * @return skipped characters
   */

  public String waitfor( String[] searchElements ) throws IOException {
    ScriptHandler[] handlers = new ScriptHandler[searchElements.length];
    for ( int i = 0; i < searchElements.length; i++ ) {
      // initialize the handlers
      handlers[i] = new ScriptHandler();
      handlers[i].setup( searchElements[i] );
    }

    byte[] b = new byte[256];
    int n = 0;
    StringBuffer ret = new StringBuffer();
    String current;

    while(n >= 0) {
      n = read(b);
      if(n > 0) {
	current = new String( b, 0, n );
	if (debug > 0)
	  System.err.print( current );
	ret.append( current );
	for ( int i = 0; i < handlers.length ; i++ ) {
	  if ( handlers[i].match( b, n ) ) {
	    return ret.toString();
	  } // if
	} // for
      } // if
    } // while
    return null; // should never happen
  }

  public String waitfor(String match) throws IOException {
    String[] matches = new String[1];

    matches[0] = match;
    return waitfor(matches);
  }

  /**
   * Read data from the socket and use telnet negotiation before returning
   * the data read.
   * @param b the input buffer to read in
   * @return the amount of bytes read
   */
  public int read(byte[] b) throws IOException {
    int n = negotiate(b);

    if (n>0)
      return n;

    while (n<=0) {
      do {
	n = negotiate(b);
	if (n>0)
	  return n;
      } while (n==0);
      n = in.read(b);
      if (n<0)
        return n;
      inputfeed(b,n);
      n = negotiate(b);
    }
    return n;
  }

  /**
   * Write data to the socket.
   * @param b the buffer to be written
   */
  public void write(byte[] b) throws IOException {
    out.write(b);
  }

  public String getTerminalType() {
    return "dumb";
  }

  public Dimension getWindowSize() {
    return new Dimension(80,24);
  }

  public void setLocalEcho(boolean echo) {
    if(debug > 0)
      System.err.println("local echo "+(echo ? "on" : "off"));
  }
}
