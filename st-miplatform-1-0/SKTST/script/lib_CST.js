﻿/*******************************************************************************
* 프로그램ID  : lib_CST
* 업무명      : 상담
* 프로그램명  : 공통 스크립트
--------------------------------------------------------------------------------
* 작성자      : 민동운
* 작성일      : 2011.11.07
--------------------------------------------------------------------------------
* 1. 변경이력
수정자    :
수정일    :
수정 내역 :
*******************************************************************************/

/*******************************************************************************
 * @description    :  조회기간 날짜 유효성검사
*******************************************************************************/
function sf_isValidDateFromTo(oFromId, oToId, nInterValDay, bSameMonth)
{

	var bRetValue = true;

	if( uf_isNull(oFromId.Value) == true )
	{
		alert(cf_getMessage(MSG_00083, "시작일자"));
		oFromId.SetFocus();
		return false;
	}

	if( uf_isNull(oToId.Value) == true )
	{
		alert(cf_getMessage(MSG_00083, "종료일자"));
		oToId.SetFocus();
		return false;
	}

	var nCompared = sf_compare(oFromId.Value,oToId.Value);
	if(nCompared == -1)
	{
		alert(cf_getMessage(MSG_00095, "종료일자;시작일자"));
		oFromId.SetFocus();
		return false;
	}
	
		
	if(uf_isNull(bSameMonth) == false && bSameMonth == true)
	{	// 설정하지 않았을 경우
		if (substr(oFromId.Value, 0, 6) != substr(oToId.Value, 0, 6)) 
		{
			alert("동일한 월에 대해서만 조회가 가능합니다.");
			return false;
		}	
	}

	var nInterValDayTarget = f_getInterDay(oToId.Value, oFromId.Value);	

	if(uf_isNull(nInterValDay) == false && nInterValDay != 0)
	{	// 날짜 차 설정하지 않았을 경우
		if(nInterValDayTarget > nInterValDay)
		{
			alert(nInterValDay + "일을 초과 설정하실수 없습니다. ");
			return false;
		}
	}

	return true;
}


/*******************************************************************************
 * @description    :  두 날짜의 차
*******************************************************************************/
function f_getInterDay(sDateFrom, sDateTo)
{
	var oDateFrom = DateTime(sDateFrom);
	var oDateTo   = DateTime(sDateTo);
	
	return abs(oDateFrom - oDateTo) + 1;
}