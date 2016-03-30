/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.common.sso;

import java.security.*;
import java.security.MessageDigest;
import sun.misc.BASE64Encoder;

public class SHA1Hash {
  private static SHA1Hash m_instance = null;

  //private SHA1Hash() {}

  public SHA1Hash getInstance() {
    if ( m_instance != null ) {
      return m_instance;
    } else {
      m_instance = new SHA1Hash();
      return m_instance;
    }
  }

  /**
   * 엔크립션
   * @param word
   * @return
   * @throws Exception
   */
  public static String encode( String word ) throws Exception {

    MessageDigest md = MessageDigest.getInstance( "SHA-1" );

    md.update( word.getBytes() );

    byte[] raw = md.digest();

    BASE64Encoder encoder = new BASE64Encoder();

    return encoder.encode( raw );
  }


  private static String asHex( byte hash[] ) {
    StringBuffer buf = new StringBuffer( hash.length * 2 );
    int i;

    for ( i = 0; i < hash.length; i++ ) {
      if ( ( ( int ) hash[i] & 0xff ) < 0x10 )
        buf.append( "0" );

      buf.append( Long.toString( ( int ) hash[i] & 0xff, 16 ) );
    }

    return buf.toString();
  }
  /**
   *사용방법
   * SHA1Hash md = new SHA1Hash();
   * userPass = md.hash(password);
   * @param arg
   * @return
   */
  public static String hash( String arg ) {
    return hash( arg.getBytes() );
  }

  public String doHash( String arg ) {
    return hash( arg.getBytes() );
  }

  public static String hash( byte barray[] ) {
    byte[] result;

    try {
      MessageDigest m = MessageDigest.getInstance( "SHA-1" );
      m.reset();
      result = m.digest( barray );
      return asHex( result );
    } catch ( NoSuchAlgorithmException ex ) {
    }
    return "test";
  }

  public static void main( String[] args ) {
    for ( int i = 0; i < args.length; i++ ) {
      System.out.println( args[i] );
      System.out.println( hash( args[i] ) );
    }
    //Hex코드화 한다.
    //System.out.println(asHex("111".getBytes()) ) ;
  }

}
