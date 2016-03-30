﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿/**
 * @type   : function
 * @access : public
 * @sig    : value
 * @desc   : 값이 null이거나 빈 string인지 확인한다.                
 * @param  : @value - null 체크할 값       
 * @return : n/a                                                            
 */
function uf_isEmpty(sStr)
{
	if(sStr == null || sStr == "" || sStr.length == 0)
	{
		return true;
	}
	else
	{
		return false;
	}
}

/**
 * @type   : function
 * @access : public
 * @sig    : value
 * @desc   : uf_isEmpty 체크 후 빈값 또는 원래값을 리턴        
 * @param  : @sStr - 체크할 값       
 * @return : n/a                                                            
 */
function uf_null2space(sStr)
{
	if(uf_isEmpty(sStr.toString()))
	{
		return "";
	}
	else
	{
		return sStr;
	}
}

/**
 * @type   : function
 * @access : public
 * @sig    : value
 * @desc   : IsDigit 체크 후 숫자형으로 리턴               
 * @param  : @value - null 체크할 값       
 * @return : n/a                                                            
 */
function uf_checkStr2Int(sNo)
{
	if(!isDigit(sNo))
	{
		sNo = 0;
	}
	
	return parseInt(sNo);
}


/**
 * @type   : function
 * @access : public
 * @sig    : value
 * @desc   : 값이 null이거나 빈 string인지 확인한다.                
 * @param  : @value - null 체크할 값       
 * @return : n/a                                                            
 */
function uf_IsNull(value) 
{
	if(value == null || value == "")
		return true;
	else
		return false;
}

/**
 * @type   : function
 * @access : public
 * @sig    : sValue, sDateFormat
 * @desc   : 문자열이 올바른 날짜형식인지를 첵크한다.
 * @param  : @sValue      - 첵크할 문자열    
 * @param  : @sDateFormat - DATE형식 YYYYMMDD YYYYMM MMDD
 * @return : true/false                                      
 */
function uf_CheckDate(sValue, sDateFormat)
{
     checkStr = uf_StripSpecialChar(sValue);
     
     sDateFormat = toUpper(sDateFormat);
     if( sDateFormat == "YYYYMMDD" )
     {
          if( length(checkStr) != 8 )
              return false;
             
          if( AddDate(AddDate(checkStr, 1),-1) == checkStr)
              return true;
          else
              return false;
     }
     else if( sDateFormat == "YYYYMMDDHH24MI" )
     {
          if( length(checkStr) != 12 )
              return false;
              
          var yyyymmdd = substr(checkStr,0,8);
          var hh       = parseInt(substr(checkStr,8,2));
          var mi       = parseInt(substr(checkStr,10,2));
          
          if( AddDate(AddDate(yyyymmdd, 1),-1) == yyyymmdd )
          {
              if( 0 <= hh && hh < 25 && 0 <= mi && mi < 60 )
                  return true;
              else
                  return false;
          }
          else
              return false;          
     }
     else if( sDateFormat == "YYYYMMDDHHMI" )
     {
          if( length(checkStr) != 12 )
              return false;
              
          var yyyymmdd = substr(checkStr,0,8);
          var hh       = parseInt(substr(checkStr,8,2));
          var mi       = parseInt(substr(checkStr,10,2));
          
          if( AddDate(AddDate(yyyymmdd, 1),-1) == yyyymmdd )
          {
              if( 0 <= hh && hh <= 12 && 0 <= mi && mi < 60 )
                  return true;
              else
                  return false;
          }
          else
              return false;          
     }
     else if( sDateFormat == "HHMI" )
     {
          if( length(checkStr) != 4 )
              return false;
              
          var hh       = parseInt(substr(checkStr,0,2));
          var mi       = parseInt(substr(checkStr,2,2));
          
          if( 0 <= hh && hh <= 12 && 0 <= mi && mi < 60 )
              return true;
          else
              return false;
     }
     else if( sDateFormat == "HH24MI" )
     {
          if( length(checkStr) != 4 )
              return false;
              
          var hh       = parseInt(substr(checkStr,0,2));
          var mi       = parseInt(substr(checkStr,2,2));
          
          if( 0 <= hh && hh < 24 && 0 <= mi && mi < 60 )
              return true;
          else
              return false;
     }
     else if( sDateFormat == "YYYYMM" )
     {
          if( length(checkStr) != 6 )
              return false;
          
          checkStr = checkStr+"01";              
          if( AddDate(AddDate(checkStr, 1),-1) == checkStr)
              return true;
          else
              return false;
     }
     else if( sDateFormat == "MMDD" )
     {
          if( length(checkStr) != 4 )
              return false;
          
          checkStr = "2000"+checkStr;              
          if( AddDate(AddDate(checkStr, 1),-1) == checkStr)
              return true;
          else
              return false;
     }
     
     return false;
}

/**
 * @type   : function
 * @access : public
 * @sig    : sValue
 * @desc   : 문자열에 괴상한 문자및 공백을 를 벗겨낸다.
 * @param  : @sValue  - 첵크할 문자열    
 * @return : @String  - 괴상한 문자열이 벗겨진 문자열                                       
 */
function uf_StripSpecialChar(sValue)
{
    var rtnVal = sValue;
    rtnVal = replace(rtnVal, "-", "");
    rtnVal = replace(rtnVal, ".", "");
    rtnVal = replace(rtnVal, ":", "");
    rtnVal = replace(rtnVal, " ", "");
    rtnVal = replace(rtnVal, "/", "");
    return rtnVal;
}

/**
 * @type   : function
 * @access : public
 * @sig    : sValue, sDateFormat
 * @desc   : target데이터 이동.
 * @param  : @ds      - Move 할 DataSet   
 * @param  : @dsTarget - Move 된 DataSet
 * @return : true/false                                      
 */
function uf_MoveShuttle(ds, dsTarget)
{
	dsTarget.addRow();
	ds.copyToRow(ds.row, dsTarget.id, dsTarget.row );	
	ds.DeleteRow(ds.row);
}


/**
 * @type   : function
 * @access : public
 * @sig    : sStatus)	//grid의 font 
 * @desc   : 그리드의 row의 text색을 반환
 * @param  : status - status정보 (C:생성, U:변경, D:삭제)
 * @return : color
 */
function uf_GetColor(sStatus)	//grid의 font color변경
{
	var color = 'default';

	if(sStatus == "C") {		//추가	
		color = '#000000';
	} else if(sStatus == "U") {	//수정	
		color = '#FF0000';
	} else if(sStatus == "D") {  //삭제	
		color = '#555555';
	}	
	
	return color;
}

/**
 * @type   : function
 * @access : public
 * @sig    : sYyyymm
 * @desc   : 해당월의 마지막 날을 구함
 * @param  : sYyyymm : 년월
 * @return : String
 */
function uf_MonthLastDay(sYyyymm)
{
	return uf_MonthLastDay2(substr(sYyyymm,0,4), substr(sYyyymm,4,2));
}


/**
 * @type   : function
 * @access : public
 * @sig    : sYyyy, sMm
 * @desc   : 해당월의 마지막 날을 구함
 * @param  : sYyyy : 년
 * @param  : sMm : 월
 * @return : String
 */
function uf_MonthLastDay2(sYyyy, sMm)
{
	var sNextMonth;
	var sMonthLastDay;
	
    // 당월에 +1을 하여 다음달을 구함
    sNextMonth = left(AddMonth(sYyyy + sMm + "01", 1) ,6);

    //다음달 년월이 구해지면 AddDate를 사용하여 -1을 해주면 당월의 마지막 일자 Get
    sMonthLastDay = substr(AddDate((sNextMonth + "01"), -1), 6, 2);
    
    return sMonthLastDay;
}

 /*-----------------------------------------------------------------------------------
 * DESC   : Object의Enable 속성을 설정 한다.
 * PARAM  : @obj        	 - object name 
            @bStatus         - 속성(true,false)
 * RETURN : void
 * @author : 
 *----------------------------------------------------------------------------------*/
function uf_ObjComponentEnable(obj,bStatus)
{
    for(var nRow=0 ; nRow < obj.Components.count() ; nRow++)
    {
		if(obj.Components[nRow].getType() == "Static" || obj.Components[nRow].getType() == "Shape")
		{
			continue;
		}

        obj.Components[nRow].Enable = bStatus;
    }
}

 /*-----------------------------------------------------------------------------------
 * DESC   : 이메일 주소 형태의 정상 여부를 체크한다
 * PARAM  : @sEmail        	 - 이메일 주소
 * RETURN : true - 정상 , false - 잘못된 주소
 *----------------------------------------------------------------------------------*/
function uf_checkEmailAddr(sEmail) 
{
    var i;
    var check=0;
    var dot=0;
    var before = "";
    var after = "";

	sEmail = trim(sEmail);

    if(sEmail.length == 0) return(false);
	
    for(i=0; i<sEmail.length; i++) 
    {
        if(charAt(sEmail, i) == '@') { check = check + 1; }
        else if(check == 0) { before = before + charAt(sEmail,i); }
        else if(check == 1) { after = after + charAt(sEmail,i); }
    }

    if( check >= 2 || check == 0 ) {
    	alert("@는 반드시 한개 존재해야 합니다. ");        
        return false;
    }

	if(before.length < 1)
	{
		alert("이메일의 @앞에 유효 아이디가 존재해야 합니다."); 
		return false;
	}
	
    for(i=0; i<before.length; i++) {
        if(!(IsAlnum(charAt(before,i))||
             (charAt(before,i) == '_') || (charAt(before,i) == '.') ||
             (charAt(before,i) == '-'))) {
             	   alert("이메일의 @앞에 한글이나 특수기호는 사용하실수 없습니다. ");
                   //alert("잘못된 전자우편 주소입니다.");
                   return false;
        }
    }

    for(i=0; i<after.length; i++) {
        if(!( IsAlnum(charAt(after,i)) ||
             (charAt(after,i) == '_') || (charAt(after,i) == '.') ||
             (charAt(after,i) == '-'))) {
             	   alert("이메일의 @뒤에 한글이나 특수기호는 사용하실수 없습니다. ");
                   //alert("잘못된 전자우편 주소입니다.");
                   return false;
        }
    }

    for(i=0; i<after.length; i++) {
        if(charAt(after,i) == '.') {
            dot = dot + 1;
        }
    }

    if( dot < 1 ) {
    	alert("전자우편 주소에 . 이 반드시 존재해야합니다. ");
        //alert("잘못된 전자우편 주소입니다.");
        return false;
    }

	var atidx = sEmail.indexOf('@');
	var endchar = substr(sEmail,atidx+1);
	if(endchar == "" || endchar == "co.kr" || endchar == "or.kr" || endchar == "ac.kr" || endchar == "pe.kr" ||
	 	endchar == "ne.kr" || endchar == "com" || endchar == "org" || endchar == "net" || endchar == "gov" ){
		alert("잘못된 전자우편 주소입니다.");
		return false;
	}else {
		var compobj = substr(endchar, 0, endchar.indexOf("."));
		var exeobj = substr(endchar, endchar.indexOf(".")+1);
		//if(((compobj == "hanmail" || compobj == "chol" || compobj == "chollian") && exeobj != "net") ){
		if(((compobj == "hanmail") && exeobj != "net") ){
			alert("입력하신 E-mail은 " + compobj + ".net 이 정확하오니 \n다시 한번 확인해 보시기 바랍니다.");
			return false;
		}
		else if((compobj == "dreamwiz" || compobj == "freechal" || compobj == "hotmail" || compobj == "naver") && exeobj != "com"){
			alert("입력하신 E-mail은 " + compobj + ".com 이 정확하오니 \n다시 한번 확인해 보시기 바랍니다.");
			return false;
		}
	}

    return true;
}


 /*-----------------------------------------------------------------------------------
 * DESC   : 에디트 컴포넌트에 숫자 및 마이너스만을 허용
 * EVENT  : OnChar 이벤트에 사용
 *----------------------------------------------------------------------------------*/
function uf_allowNumMinusEvent(obj,strPreText,nChar,strPostText,LLParam,HLParam)
{
	if(!((nChar >= 48 && nChar <= 57) || nChar == 45 || nChar == 8))
	{
		return false;
	}
}

 /*-----------------------------------------------------------------------------------
 * DESC   : 에디트 컴포넌트에 숫자 및 .만을 허용
 * EVENT  : OnChar 이벤트에 사용
 *----------------------------------------------------------------------------------*/
function uf_allowNumDotEvent(obj,strPreText,nChar,strPostText,LLParam,HLParam)
{
	if(!((nChar >= 48 && nChar <= 57) || nChar == 46 || nChar == 8))
	{
		return false;
	}
}


/*-----------------------------------------------------------------------------------
 * DESC   : Object의 Value 값을 비교한다
 * PARAM  : @objStart  - object name 
            @objEnd    - object name 
 * RETURN : boolean
 *----------------------------------------------------------------------------------*/
function uf_compValue(objStart,objEnd)
{
	var nStart = toNumber(objStart.Value);
	var nEnd   = toNumber(objEnd.Value);
	if( nStart > nEnd ){
		return false;
	}
	
	return true;
}


/*-----------------------------------------------------------------------------------
 * DESC   : 그리드의 원하는 cell에 위치하게 함.
 * PARAM  : @grGrid - grid
            @nRow   - row
            @nCol   - cell
 *----------------------------------------------------------------------------------*/
function uf_SelPos(grGrid, nRow, nCol)
{
	grGrid.AutoEnter = true;
	
	var objDs = object(grGrid.BindDataset);
	objDs.row = nRow;
	
	grGrid.SetCellPos(nCol);
	grGrid.Setfocus();
	grGrid.AutoEnter = false;
}


/*-----------------------------------------------------------------------------------
 * DESC   : 싱글 쿼테이션을 설정한다 - 콤마 구분자 단위로 복수개 설정 가능
 * PARAM  : @sStr  -  대상 문자열
 * RETURN : 싱글 쿼테이션이 붙여진 문자열
 *----------------------------------------------------------------------------------*/
function uf_setSingleQuote(sStr)
{
	if(sStr != null && sStr.length > 0)
	{
		var sResult;
		var sTmp;
		var arrStr = sStr.split(",","webstyle");
		var arrLength = arrStr.length;
		for(var i=0; i<arrLength; i++)
		{
			sTmp = trim(arrStr[i]);
			if(sTmp.length > 0)
			{
				sResult += ",'"+sTmp+"'";
			}
		}
		
		return substr(sResult,1);
	}
}

/*-----------------------------------------------------------------------------------
 * DESC   : 사업자 번호의 유효성을 체크
 * PARAM  : @sBizNo  -  사업자 번호
 * RETURN : true - 유효, false - 유효하지 않음
 *----------------------------------------------------------------------------------*/
function uf_isBizNo(sBizNo) 
{	
	if(sBizNo.indexOf("-") >= 0)
	{
		sBizNo = uf_StripSpecialChar(sBizNo);
	}
	
	if(!isDigit(sBizNo) || sBizNo.length != 10)
	{
		alert("사업자 번호는 10자리의 숫자 형식이어야 합니다!");
		return false;
	}

    var nSum=0, nRemainder =0, nCheck=0,;
    
    var sCheckValue = "137137135";
    for(var i=0;i<9;i++) 
    {
        nSum += toNumber(charAt(sBizNo, i)) * toNumber(charAt(sCheckValue, i));
    }

    nSum = nSum + parseInt((toNumber(charAt(sBizNo, 8))*5)/10);
    nRemainder = nSum - (parseInt(toNumber(nSum) / 10) * 10) ;
 
    if(nRemainder == 0) 
    {
        nCheck = 0;
    } 
    else 
    {
        nCheck = 10 - nRemainder;
    }

    if(nCheck == eval(charAt(sBizNo, 9))) 
    {
        return true;
    } 
    else 
    {
		alert("올바르지 않은 사업자 번호 입니다");
        return false;
    }
}

/*-----------------------------------------------------------------------------------
 * DESC   : 주민번호 유효성을 체크
 * PARAM  : @strValue1  -  주민번호 13자리 또는 6자리
 * PARAM  : @strValue2 -  주민번호 @strValue1 6자리 입력됐을 경우 나머지 7자리
 * RETURN : true - 유효, false - 유효하지 않음
 *----------------------------------------------------------------------------------*/
function uf_isRegNo( val1, val2 ) {

	var str_f_num = null;
	var str_l_num = null;
	
	if(val1.length == 13)
	{ 
		str_f_num = substr(val1, 0, 6 );
		str_l_num = substr(val1, 6 );
		
		val1 = str_f_num;
		val2 = str_l_num;
	}
	
	var tmp1, tmp2, tmp3;
	var t1, t2, t3, t4, t5, t6, t7;
	tmp1 = val1.substr( 2, 2 );
	tmp2 = val1.substr( 4 );
    tmp3 = val2.substr( 0, 1 );
           
	if( fnDoCheckProResnoLength( val1 ) == -1 ) return false;
	if( fnDoCheckPreResnoLength( val2 ) == -1 ) return false;
	if ( (tmp1 < "01") || (tmp1 > "12") ) return false;
	if ( (tmp2 < "01") || (tmp2 > "31") ) return false;
	if ( (tmp3 < "1" ) || (tmp3 > "4" ) ) return false;

	t1  = val1.substr( 0, 1 );
	t2  = val1.substr( 1, 1 );
	t3  = val1.substr( 2, 1 );
	t4  = val1.substr( 3, 1 );
	t5  = val1.substr( 4, 1 );
	t6  = val1.substr( 5, 1 );
	t11 = val2.substr( 0, 1 );
	t12 = val2.substr( 1, 1 );
	t13 = val2.substr( 2, 1 );
	t14 = val2.substr( 3, 1 );
	t15 = val2.substr( 4, 1 );
	t16 = val2.substr( 5, 1 );
	t17 = val2.substr( 6, 1 );
	
	var tot = tointeger(t1)  * 2 + tointeger(t2)  * 3 + tointeger(t3)  * 4 + tointeger(t4)  * 5 + tointeger(t5)  * 6 + tointeger(t6)  * 7;
	tot    += tointeger(t11) * 8 + tointeger(t12) * 9 + tointeger(t13) * 2 + tointeger(t14) * 3 + tointeger(t15) * 4 + tointeger(t16) * 5 ;
		
	var result = tointeger(tot) % 11;
	result = ( 11 - tointeger(result) ) % 10;
	
	if (result == t17) {
		return true;
	} else {
		return false;
	}
}


// 주민번호 형식 check (앞 6자리)
function fnDoCheckProResnoLength( szProResno ) {
	//var nResno = parseInt( szProResno ).toString();
			
	if( szProResno.length == 6 ) {
    	return 0;
    } else {
    	return -1;	
    }
		
}	

// 주민번호 형식 check (뒤 7자리)
function fnDoCheckPreResnoLength( szPreResno ) {
	//var nResno = parseInt( szPreResno ).toString();
			
	if( szPreResno.length == 7 ) {
		return 0;
	} else {
		return -1;	
	}
	
}

 /*-----------------------------------------------------------------------------------
 * NAME   : cm_ObjVisible(pds,psCellName)
 * DESC   : Object의 Visible 속성을 제어 한다.
 * PARAM  : @pobj        	 - object name 
            @psCellName    - 감추기(false), 보이기(true)
 * RETURN : void
 *----------------------------------------------------------------------------------*/
 function uf_objVisible(pobj, pbToggle) 
 {
	if (pbToggle == null) {
		if(pobj.Visible) {
			pobj.Visible = false;
		} else {
			pobj.Visible = true;
		}
	}else {
		pobj.Visible = pbToggle;
	}
 }
 
/*******************************************************************************
 * @description    :  컨트롤 초기화
*******************************************************************************/
function uf_clearControls(sControls)
{
	var arrControl = Split(sControls, ",");
	var nCntCntrl = arrControl.length();
	
	for(var i=0; i<nCntCntrl; i++)
	{
		var oCntrl = eval(arrControl[i]);
		oCntrl.Text = "";
	}
}


var sUnReserved = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.~";
var sReserved = "!*'();:@&=+$,/?%#[]";
var sHexchars = "0123456789ABCDEFabcdef";

/*******************************************************************************
 * @description    :  헥사 코드 리턴
*******************************************************************************/
function uf_getHex(decimal) 
{
	return "%" + charAt(sHexchars, ext_getBitCalc(">>", decimal,4)) + 
                 charAt(sHexchars, ext_getBitCalc("&", decimal,15));
}

/*******************************************************************************
 * @description    :  문자를 utf-8로 인코딩
*******************************************************************************/
function uf_encodeUtf8(str) 
{
	var sEncoded = "";
	
	// ---------------- If UTF-8 character encoding was chosen: ----------------
	
	for (var i = 0, n=length(str); i<n; i++) 
	{
		var ch = charAt(str,i);
	
		if (indexOf(sUnReserved, ch) != -1) 
		{
			sEncoded +=  ch;
		} 
		else 
		{
			//   Position in   |  Bytes needed   | Binary representation
			//  Unicode table  |   for UTF-8     |       of UTF-8
			// ----------------------------------------------------------
			//     0 -     127 |    1 byte       | 0XXX.XXXX
			//   128 -    2047 |    2 bytes      | 110X.XXXX 10XX.XXXX
			//  2048 -   65535 |    3 bytes      | 1110.XXXX 10XX.XXXX 10XX.XXXX
			// 65536 - 2097151 |    4 bytes      | 1111.0XXX 10XX.XXXX 10XX.XXXX 10XX.XXXX
	
			var charcode = Asc(ch);
	
			// Position 0 - 127 : ASCII character encoding:
			if (charcode < 128) 
			{
			  sEncoded += uf_getHex(charcode);
			}
	
			// Position 128 - 2047 : UTF-8 character encoding (2 bytes)
			if (charcode > 127 && charcode < 2048) 
			{
			  // First UTF byte: Mask the first five bits of charcode with binary 110X.XXXX:
			  sEncoded +=  uf_getHex(ext_getBitCalc("|",ext_getBitCalc(">>",charcode,6),12));
			  // Second UTF byte: Get last six bits of charcode and mask them with binary 10XX.XXXX:
			  sEncoded +=  uf_getHex(ext_getBitCalc("|",ext_getBitCalc("&",charcode,63),128));
			}
	
			// Position 2048 - 65535 : UTF-8 character encoding (3 bytes)
			if (charcode > 2047 && charcode < 65536) 
			{
			  // First UTF byte: Mask the first four bits of charcode with binary 1110.XXXX:
			  sEncoded +=  uf_getHex(ext_getBitCalc("|",ext_getBitCalc(">>",charcode,12),224));
			  // Second UTF byte: Get the next six bits of charcode and mask them binary 10XX.XXXX:
			  sEncoded +=  uf_getHex(ext_getBitCalc("|",ext_getBitCalc("&",ext_getBitCalc(">>",charcode,6),63),128));
			  // Third UTF byte: Get the last six bits of charcode and mask them binary 10XX.XXXX:
			  sEncoded +=  uf_getHex(ext_getBitCalc("|",ext_getBitCalc("&",charcode,63),128));
			}
	
			// Position 65536 - : UTF-8 character encoding (4 bytes)
			if (charcode > 65535) 
			{
			  // First UTF byte: Mask the first three bits of charcode with binary 1111.0XXX:
			  sEncoded +=  uf_getHex(ext_getBitCalc("|",ext_getBitCalc(">>",charcode,18),240));
			  // Second UTF byte: Get the next six bits of charcode and mask them binary 10XX.XXXX:
			  sEncoded +=  uf_getHex(ext_getBitCalc("|",ext_getBitCalc("&",ext_getBitCalc(">>",charcode,12),63),128));
			  // Third UTF byte: Get the last six bits of charcode and mask them binary 10XX.XXXX:
			  sEncoded +=  uf_getHex(ext_getBitCalc("|",ext_getBitCalc("&",ext_getBitCalc(">>",charcode,6),63),128));
			  // Fourth UTF byte: Get the last six bits of charcode and mask them binary 10XX.XXXX:
			  sEncoded +=  uf_getHex(ext_getBitCalc("|",ext_getBitCalc("&",charcode,63),128));
			}
		}
	}
	
	return sEncoded;
}

/*******************************************************************************
 * @description     : 개통번호 얻어 지정한 컨트롤에 설정
-------------------------------------------------------------------------------- 
 * @param  sSvcNum1, sSvcNum2,sSvcNum3
 * @return          : 없음
*******************************************************************************/
function uf_formatString( strSrc, strFormat )
{
	var	retValue = "";
	var	j =	0; 
	

	if(strSrc.length != strSrc.lengthb)
	{	// Hangle
		for	(var i=0; i< strSrc.lengthb;i=i+2) 
		{
			if(strFormat.charAt(j) != "*")
			{
				retValue +=	Midb(strSrc, i, 2);
				j++;
			}
			else
			{
				retValue +=	strFormat.charAt(j++);
			}
		}		
	}
	else
	{	// English
		for	(var i=0; i< strSrc.length;	i++) 
		{
			if(strFormat.charAt(j) == "*")
			{
				retValue +=	strFormat.charAt(j);
				j++;
			}
			else
			{
				retValue +=	strSrc.charAt(i);
				j++;
			}
		
			if ( (j	< strSrc.length	&& j < strFormat.length) 
			      && (strFormat.charAt(j) != "9"
				  && strFormat.charAt(j).toLowerCase() !=	"x"
				  && strFormat.charAt(j) !=	"#"
				  && strFormat.charAt(j) !=	"*"))
			{
				retValue +=	strFormat.charAt(j++);
			}
			
		}
	}
	return retValue;
} 


/*******************************************************************************
 * @description    : 소매 일반 담당자 여부
*******************************************************************************/
function uf_ObjComponentClear(obj)
{
    for(var nRow=0 ; nRow < obj.Components.count() ; nRow++)
    {
		var sControlNm = obj.Components[nRow].getType().ToLower();
		
		if(sControlNm == "static" 
			|| sControlNm == "shape"
			|| sControlNm == "image"
			|| sControlNm == "button")
		{
			continue;
		}
		
		if( sControlNm== "edit" || sControlNm== "textarea")
		{
			obj.Components[nRow].Text = "";
		}
		else if(sControlNm == "maskedit")
		{	
			if(obj.Components[nRow].Type.ToLower() == "number")
			{
				obj.Components[nRow].Value = 0;
			}
			else
			{
				obj.Components[nRow].Value = "";
			}
		}
		else if(sControlNm == "calendar")
		{
			obj.Components[nRow].Value = "";
		}
		else
		{
			obj.Components[nRow].index = 0;
		}
    }
}




function uf_debugControl(sDiv, sCntrl)
{
	var sErr = "[개발오류]";
	var bErr = false;
	if(eval(sDiv) == null)
	{
		sErr += sDiv + " 컨트롤 찾을 수 없습니다.\n";
		bErr = true;
	}
	var sArrCntrl = sCntrl.split(",");
	for(var i=0; i < sArrCntrl.length; i++)
	{
		if(eval(sDiv + "." + sArrCntrl[i]) == null)
		{
			bErr = true;
			sErr += sDiv + sArrCntrl[i] + " 컨트롤 찾을 수 없습니다.\n";
		}
	}
	if(bErr == true)
	{
		alert(sErr);
	}
	return bErr;
}

// 대리점 화면 권한 / 세션 값 설정
// @Param sDiv        : 대리점 컨트롤이 속한 DIV 컨트롤 아이디 문자열
// @Param sAgency     : 대리점명, 대리점코드, 대리점버튼 순의 아이디 문자열
// @Param sAgencyUkey : 대리점 Ukey 코드(생략가)
function uf_enableAgencyByAuth(sDiv, sEdtAgency, sAgencyUkey)
{
if(uf_debugControl(sDiv, sEdtAgency) == true) return;
	
	var nAuth = uf_getAuthCode();
	
	var sAgencyArr = sEdtAgency.split(",");
	var nCnt       = sAgencyArr.length;
	if( nCnt != 3) 
	{
		alert("[개발 디버깅용]필수항목 [대리점명, 대리점코드, 대리점버튼] 컨트롤 갯수가 부족합니다.");
		return;
	}
	
	var oPosAgencyNm = eval(sDiv + "." + sAgencyArr[0]);
	var oPosAgency   = eval(sDiv + "." + sAgencyArr[1]);
	var oBtnAgency   = eval(sDiv + "." + sAgencyArr[2]);
	
	if(nAuth == 0) return;
	
	var sPosAgency     = gds_session.GetColumn(0, "posAgencyId");        // 소속 대리점ID
	var sPosAgencyNm   = gds_session.GetColumn(0, "posAgencyNm");        // 소속 대리점명
	var sPosAgencyUkey = gds_session.GetColumn(0, "posAgencyUkeyId");    // 소속 대리점UKEY ID	
	
	oPosAgencyNm.Text = sPosAgencyNm;
	oPosAgency.Text   = sPosAgency;
	
	if(uf_isNull(sAgencyUkey) == false)
	{
if(uf_debugControl(sDiv, sAgencyUkey) == true) return;	

		var oAgencyUkey = eval(sDiv + "." + sAgencyUkey);		
		oAgencyUkey.Text   = sPosAgencyUkey;
		oAgencyUkey.enable = false;
	}
	oPosAgencyNm.enable = false;
	oBtnAgency.enable = false;
}

// 거래처 화면 권한 / 세션 값 설정
// @Param sDiv      : 거래처 컨트롤이 속한 DIV 컨트롤 아이디 문자열
// @Param sEdtDeal  : 거래처명, 거래처코드, 거래처버튼 순의 아이디 문자열
// @Param sDealUkey : 거래처UKEY_CD, SUB_CD, CHANNEL_CD 순의 아이디 문자열(생략가)
function uf_enableDealByAuth(sDiv, sEdtDeal, sDealUkey)
{
if(uf_debugControl(sDiv, sEdtDeal) == true) return;	
	var nAuth = uf_getAuthCode();

	var sArrDeal = sEdtDeal.split(",");
	var nCnt = sArrDeal.length();
	if( nCnt != 3) 
	{
		alert("[개발 디버깅용]필수항목 [거래처명, 거래처코드, 거래처버튼] 컨트롤 갯수가 부족합니다.");
		return;
	}

	if(nAuth == 0) return;

	var sOrgAreaId        = gds_session.GetColumn(0, "orgareaid");				// 근무지 ID
	var sOrgAreaNm        = gds_session.GetColumn(0, "orgareanm");				// 근무지명
	var sOrgAreaUkeyId    = gds_session.GetColumn(0, "orgareaukeyid");			// 근무지ukeyid
	var sOrgAreaUkySubCd  = gds_session.GetColumn(0, "orgareaukeysubcd");		// 근무지subcd
	var sOrgAreaChannelCd = gds_session.GetColumn(0, "orgareaukeychannelcd");	// 근무지 channelcd	
	var sArrDealUkey;
		
	if(nAuth == 2)
	{	// 직영점/판매점 권한
		var oDealNm  = eval(sDiv + "." + sArrDeal[0]);
		var oDealCd  = eval(sDiv + "." + sArrDeal[1]);
		var oBtnDeal = eval(sDiv + "." + sArrDeal[2]);

		oDealNm.Text = sOrgAreaNm;
		oDealCd.Text = sOrgAreaId;
		
		oDealNm.enable = false;
		oDealCd.enable = false;
		oBtnDeal.enable = false;
	

		if(uf_isNull(sDealUkey) == false)
		{
	if(uf_debugControl(sDiv, sDealUkey) == true) return;		
			sArrDealUkey = sDealUkey.split(",");
			var nCnt = sArrDealUkey.length(); 
		
			if(nCnt >= 1)
			{
				eval(sDiv + "." + sArrDealUkey[0]).Text = sOrgAreaUkeyId;
			}
			if(nCnt >= 2)
			{
				eval(sDiv + "." + sArrDealUkey[1]).Text = sOrgAreaUkySubCd;
			}
			if(nCnt >= 3)
			{
				eval(sDiv + "." + sArrDealUkey[2]).Text = sOrgAreaChannelCd;
			}
			//trace(sOrgAreaUkeyId + " / " + sOrgAreaUkySubCd +" / " + sOrgAreaChannelCd);
		}
	}
}

// 담당자 활성화 / 비활성화 (세션 권한으로 컨트롤)
// @Param sDiv : 대리점 컨트롤이 속한 DIV 컨트롤 아이디 문자열
// @Param sAgency : 대리점명, 대리점코드, 대리점버튼 순의 아이디 문자열
// @Param sAgencyUkey : 대리점 Ukey 코드
function uf_enableChgrgByAuth(oCmbChgrg)
{
	var nAuthCode = uf_getAuthCode();
	
	if(oCmbChgrg == null)
	{
		alert('[개발용 오류]담당자 CoboBox[oCmbChgrg] 인자값은 필수 항목입니다.');
		return;
	}
	
	oCmbChgrg.Value = gds_session.GetColumn(0, "loginid");	
	
	if(nAuthCode == 2)
	{	// 직영점/ 판매점
		oCmbChgrg.enable = false;
	}
}


// -1 : Error
// 0 : 센터급 이상 (대리점 변경가/ 판매처 : 변경가)
// 1 : 대리점 권한 (대리점 변경불가 / 판매처 변경가)
// 2 : 직영점 / 판매점 권한 (대리점 변경불가 / 판매처 변경불가)
function uf_getAuthCode()
{
	var nRetValue = -1;
	var sPosAgency = gds_session.GetColumn(0, "posAgencyId"); 
	var sOrgArea   = gds_session.GetColumn(0, "orgareaid");
	var sDuty      = gds_session.GetColumn(0, "duty");		
	var sUsergrp   = gds_session.GetColumn(0, "usergrp");		

	if(sUsergrp == "D14") return 2;

	if((uf_isNull(sPosAgency) == true) && uf_isNull(sOrgArea) == true)
	{	// 센터급 이상
		nRetValue = 0;
	}
	else if(sPosAgency == sOrgArea)
	{	// 대리점
		nRetValue = 1;
	}
	else if(sPosAgency != sOrgArea)
	{	// 직영점 / 판매처 
		if(sDuty == "1")
		{	// 관리자
			nRetValue = 1;
		}
		else
		{	// 일반 사용자 / 외부
			nRetValue = 2;
		}
	}
	else
	{
		nRetValue = -1;
	}
	return nRetValue;
}


