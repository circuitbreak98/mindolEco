/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.common.telnet;

import java.util.Vector;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/정산</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-12-01 10:51:07</li>
 * </ul>
 *
 * @author 장화수 (janghwasoo)
 */ 

public class ScriptHandler {

  /** debugging level */
  private final static int debug = 0;

  private int matchPos;		// current position in the match
  private byte[] match;		// the current bytes to look for
  private boolean done = true;	// nothing to look for!

  /**
   * Setup the parser using the passed string. 
   * @param match the string to look for
   */
  public void setup(String match) {
    if(match == null) return;
    this.match = match.getBytes();
    matchPos = 0;
    done = false;
  }

  /**
   * Try to match the byte array s against the match string.
   * @param s the array of bytes to match against
   * @param length the amount of bytes in the array
   * @return true if the string was found, else false
   */
  public boolean match(byte[] s, int length) {
    if(done) return true;
    for(int i = 0; !done && i < length; i++) {
      if(s[i] == match[matchPos]) {
        // the whole thing matched so, return the match answer 
        // and reset to use the next match
        if(++matchPos >= match.length) {
          done = true;
          return true;
        }
      } else
        matchPos = 0; // get back to the beginning
    }
    return false;
  }
}
