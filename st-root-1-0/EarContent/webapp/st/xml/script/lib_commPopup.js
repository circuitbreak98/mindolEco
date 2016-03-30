﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿/*******************************************************************************
* 프로그램ID  : lib_commPopup
* 업무명      : 공통 팝업
* 프로그램명  : 스크립트
--------------------------------------------------------------------------------
* 작성자      : 한병곤
* 작성일      : 2009.02.02
--------------------------------------------------------------------------------
* 1. 변경이력
수정자    :
수정일    :
수정 내역 :
*******************************************************************************/


/*******************************************************************************
 * @description       : 우편번호 팝업 호출
--------------------------------------------------------------------------------
 * @param edt_zip1    : 우편번호 앞자리 obiect id
	      edt_zip2    : 우편번호 뒷자리 obiect id
	      edt_othAddr : 상세주소 obiect id
 * @return            : 없음
*******************************************************************************/
function cf_commPopPost(edt_zip1, edt_zip2, edt_addr)
{
	var sArg  = "sZipCd1="+quote(edt_zip1);
		sArg += " sZipCd2=" + quote(edt_zip2);   
		sArg += " sAddr=" + quote(edt_addr);
		
	dialog("BAS::BASBCO00400.xml", sArg, 360, 384 );
}

/*******************************************************************************
 * @description     : 담당자 팝업 호출 (조직명이 필요없을 경우 orgNm 에 "" )
--------------------------------------------------------------------------------
 * @param userCd    : 사번 obiect id
	      userNm    : 이름 obiect id
	      orgNm     : 조직 obiect id
	      nChar     : 눌러진 Key의 Code 값
 * @return          : 없음
*******************************************************************************/
function cf_commPopUser(userCd, userNm, orgNm, nChar)
{
	if (nChar==13) { 		
		var sArg =  " userCd='"+userCd+"'";
			sArg += " userNm='"+userNm+"'"; 
			sArg += " orgNm='"+orgNm+"'";	
			sArg += " dsFlag=false";
		dialog("BAS::BASBCO00300.xml", sArg , 300, 500);
    } else {
		if (userCd != null) {
			eval(userNm).Text = "";
		}
    }
}

/*******************************************************************************
 * @description     : 담당자 팝업 호출 2 - dataset binding 되어있을때 
 *  그리드(dataset)에서 호출할때 (조회결과 수정 및 신규 등록 시)
--------------------------------------------------------------------------------
 * @param  dsParent : dataset 이름 (obiect id)
	       rowParent: dataset의  해당 Row (Value) 
 * @return          : 없음
*******************************************************************************/
function cf_commPopUserDs(dsParent, rowParent)
{
	var sArg  = "dsParent='"+dsParent+"'";
		sArg += " rowParent='"+rowParent+"'";
		sArg += " dsFlag=true";		
		
	dialog("BAS::BASBCO00300.xml", sArg , 300, 500);
}


/*******************************************************************************
 * @description     : 거래처 팝업 1
 * 전체 거래처구분에 대한 검색 
-------------------------------------------------------------------------------- 
 * @param dealNm    : 거래처명 - obiect id
	      dealCd    : 거래처 코드 - obiect id
	      dealCoCl1 : 거래처구분1 - obiect id
		  dealCoCl2 : 거래처구분2 (판매점구분) - obiect id 
		  disableYn1 : 팝업창에서 거래처구분1 콤보 disable 여부 - Value
					   (Y:disable, enable일경우 '')
		  disableYn2 : 팝업창에서 거래처구분2 콤보 disable 여부 - Value 
					   (Y:disable, enable일경우 '')
 * @return          : 없음
*******************************************************************************/
function cf_commPopDeal(dealNm, dealCd, dealCoCl1, dealCoCl2, disableYn1, disableYn2)
{
	 var sArg  = "dealNm='"+dealNm+"'";
		 sArg += " dealCd='"+dealCd+"'";
         sArg += " dealCoCl1='"+dealCoCl1+"'";
         sArg += " dealCoCl2='"+dealCoCl2+"'";
         sArg += " disableYn1='"+disableYn1+"'";
         sArg += " disableYn2='"+disableYn2+"'";

    dialog("BAS::BASBCO00500.xml", sArg , 430, 300);
}

/*******************************************************************************
 * @description     : 거래처 팝업 2 - 조회조건에서 호출할때 
 * 특정 거래처구분 (예:매입처, 판매점 등)에 대한 거래처만 검색할때
--------------------------------------------------------------------------------
 * @param dealNm    : 거래처명 - obiect id
	      dealCd    : 거래처 코드 - obiect id
	      dealCoCl1 : 거래처구분1 - Value
	      nChar     : 눌러진 Key의 Code 값
		  orgId     : 
 * @return          : 없음
*******************************************************************************/
function cf_commPopDealCo(dealCd, dealNm, dealCoCl1, nChar, posAgency)
{
	if (nChar==13) { 		
		 var sArg  = "dealCd='"+dealCd+"'";
			 sArg += " dealNm='"+dealNm+"'";
			 sArg += " dealCoCl1='"+dealCoCl1+"'";
			 sArg += " posAgency='"+posAgency+"'";
			 sArg += " dsFlag=false";
			 
		dialog("BAS::BASBCO00510.xml", sArg , 430, 300);		
    } else {
		if (dealCd != null) {
			eval(dealNm).Text = "";
		}
    }
}

/*******************************************************************************
 * @description     : 거래처 팝업 3 - dataset binding 되어있을때 
 * 특정 거래처구분 (예:매입처, 판매점 등)에 대한 거래처만 검색할때
-------------------------------------------------------------------------------- 
 * @param  dsParent : dataset 이름 (obiect id)
	       rowParent: dataset의  해당 Row (Value) 
	       dealCoCl1 : 거래처구분1 - Value
 * @return          : 없음
*******************************************************************************/
function cf_commPopDealDs(dsParent, rowParent, dealCoCl1, posAgency)
{
	 var sArg  = "dsParent='"+dsParent+"'";
		 sArg += " rowParent='"+rowParent+"'";
		 sArg += " dealCoCl1='"+dealCoCl1+"'";
		 sArg += " posAgency='"+posAgency+"'";
		 sArg += " dsFlag=true";
		 
    dialog("BAS::BASBCO00510.xml", sArg , 430, 300);
}

/*******************************************************************************
 * @description     : 소속조직 팝업 1 - 조회조건에서 호출할때  
-------------------------------------------------------------------------------- 
 * @param  orgNm    : 조직명 - obiect id
	       orgId    : 조직코드 - obiect id
	       nChar    : 눌러진 키 
	       orgCl	: 조직구분 - VALUE
 * @return          : 없음
*******************************************************************************/
function cf_commPopOrgCd(orgId, orgNm, nChar, orgCl)
{
	if (nChar==13) { 		
		var sArg  = "orgNm='"+orgNm+"'";
			sArg += " orgId='"+orgId+"'";
			sArg += " dsFlag=false";
			sArg += " orgCl='"+orgCl+"'";
			
		dialog("BAS::BASBCO00100.xml", sArg , 300, 300);
    } else {
		if (orgId != null) {
			eval(orgNm).Text = "";
		}
    }
}

/*******************************************************************************
 * @description     : 소속조직 팝업 2 - dataset binding 되어있을때 
-------------------------------------------------------------------------------- 
 * @param  dsParent : dataset 이름 (obiect id)
	       rowParent: dataset의  해당 Row (Value) 
 * @return          : 없음
*******************************************************************************/
function cf_commPopOrgCdDs(dsParent, rowParent)
{
	 var sArg  = "dsParent='"+dsParent+"'";
		 sArg += " rowParent='"+rowParent+"'";
		 sArg += " dsFlag=true";
		 
	dialog("BAS::BASBCO00100.xml", sArg , 300, 300);
}


/*******************************************************************************
 * @description     : 상품 팝업 1 - 조회조건에서 호출할때  
-------------------------------------------------------------------------------- 
 * @param  orgNm    : 조직명 - obiect id
	       orgId    : 조직코드 - obiect id
	       nChar
 * @return          : 없음
*******************************************************************************/
function cf_commPopProd(prodCd, prodNm, nChar, prodCl)
{
	if (nChar==13) { 		
		var sArg  = "prodCd='"+prodCd+"'";
			sArg += " prodNm='"+prodNm+"'";
			if(prodCl !=null){
				sArg += " prodCl='"+prodCl+"'";
			}
			sArg += " dsFlag=false";
			
		dialog("BAS::BASBCO00700.xml", sArg , 400, 400);
    } else {
		if (prodCd != null) {
			eval(prodNm).Text = "";
		}
    }
}

/*******************************************************************************
 * @description     : 상품 팝업 2 - dataset binding 되어있을때 
-------------------------------------------------------------------------------- 
 * @param  dsParent : dataset 이름 (obiect id)
	       rowParent: dataset의  해당 Row (Value) 
	       callFunction : 확인 버튼 클릭 시 실행 할 부모창의 function.
 * @return          : 없음
*******************************************************************************/
function cf_commPopProdDs(dsParent, rowParent, callFunction)
{
	 var sArg  = "dsParent='"+dsParent+"'";
		 sArg += " rowParent='"+rowParent+"'";
		 sArg += " dsFlag=true";
		 sArg += " callFunction='"+callFunction+"'";
    
    dialog("BAS::BASBCO00700.xml", sArg, 400,400);
}

/*******************************************************************************
 * @description     : 색상 팝업  1
-------------------------------------------------------------------------------- 
 * @param  dsParent : dataset 이름 (obiect id)
	       rowParent: dataset의  해당 Row (Value) 
 * @return          : 없음
*******************************************************************************/
function cf_commPopColor(dsParent, rowParent)
{
	 var sArg  = "dsParent='"+dsParent+"'";
		 sArg += " rowParent='"+rowParent+"'";
		 sArg += " dsFlag=true";

    dialog("BAS::BASBCO00600.xml", sArg, 396, 490);
}


/*******************************************************************************
 * @description     : 색상 팝업  2 - dataset binding 되어있을때 
-------------------------------------------------------------------------------- 
 * @param  dsParent : dataset 이름 (obiect id)
	       rowParent: dataset의  해당 Row (Value) 
 * @return          : 없음
*******************************************************************************/
function cf_commPopColorDs(dsParent, rowParent)
{
	 var sArg  = "dsParent='"+dsParent+"'";
		 sArg += " rowParent='"+rowParent+"'";
		 sArg += " dsFlag=true";

    dialog("BAS::BASBCO00610.xml", sArg, 330, 320);
}

/*******************************************************************************
 * @description     : 소속조직검색- 조회조건에서 호출할때  
-------------------------------------------------------------------------------- 
 * @param  orgNm    : 조직명 - obiect id
	       orgId    : 조직코드 - obiect id
	       nChar
 * @return          : 없음
*******************************************************************************/
function cf_commPopPosition(ukeyCd, dealNm, nChar)
{
	if (nChar==13) { 		
		var sArg  = "ukeyCd='"+ukeyCd+"'";
			sArg += " dealNm='"+dealNm+"'";
			sArg += " dsFlag=false";
			
		dialog("BAS::BASBCO00800.xml", sArg , 400, 400);
    } else {
		if (ukeyCd != null) {
			eval(dealNm).Text = "";
		}
    }
}
       
/*******************************************************************************
 * @description     : 소속검색- dataset binding 되어있을때 
-------------------------------------------------------------------------------- 
 * @param  dsParent : dataset 이름 (obiect id)
	       rowParent: dataset의  해당 Row (Value) 
 * @return          : 없음
*******************************************************************************/
function cf_commPopPositionDs(dsParent, rowParent)
{
	 var sArg  = "dsParent='"+dsParent+"'";
		 sArg += " rowParent='"+rowParent+"'";
		 sArg += " dsFlag=true";

    dialog("BAS::BASBCO00800.xml", sArg, 330, 320);
}
       



/*******************************************************************************
 * @description       : dataset copy
*******************************************************************************/
function cf_copyDataSetPop(oTargetDs, nTargetRow, oSourceDs, nSourceRow)
{
	var nSourceColCount = oSourceDs.GetColCount();
	var sChildColId = "";

	for(var i=0; i<nSourceColCount; i++)
	{
		sChildColId = oSourceDs.GetColID(i);

		oTargetDs.SetColumn(nTargetRow, sChildColId, oSourceDs.GetColumn(nSourceRow, sChildColId));
	}
}

/*******************************************************************************
 * @description       : 거래처코드 쿼리에 적합하도록 변신!!
*******************************************************************************/
function cf_DealValCheck(sArrCell)
{
	var rtn="";
	var arrCol = split(sArrCell,";","webstyle");
	var arrCnt = length(arrCol);
	
	if(arrCnt== 0) return;
 
	for(var i=0; i<arrCnt-1; i++)
	{
		if (arrCnt==1) {
			rtn = "'";
			rtn += arrCol[i];
			rtn += "'";
		} else {
			if (i==(arrCnt-2)){
				rtn += "'";
				rtn += arrCol[i];
				rtn += "'";
			} else {
				rtn += "'";
				rtn += arrCol[i];
				rtn += "',";
			}			
		} 
	}
	return rtn;
}