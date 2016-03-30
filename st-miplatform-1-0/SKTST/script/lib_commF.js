﻿﻿﻿﻿﻿﻿/*******************************************************************************
* 프로그램ID : 공통 함수
* 업무명      : 공통 관리
* 프로그램명  : 스트립트
--------------------------------------------------------------------------------
* 작성자      : 최승호
* 작성일      : 2009.01.14
--------------------------------------------------------------------------------
* 1. 변경이력
수정자    :
수정일    :
수정 내역 :
*******************************************************************************/
#include "lib::lib_commMsg.js"

var msg_flag; // 메세지 플래그 - 서버 공통
var msg_message; // 메세지 내용 - 서버 공통
var CONST_ASC_MARK="▲"; // 그리드 정렬 마크
var CONST_DESC_MARK="▼"; // 그리드 정렬 마크

var FV_STR_CHECK_COLUMN_ID = "chk"; // 체크박스 공통 칼럼
var FV_BOOLEAN_POP_CALENDAR = false; // 팝업 달력 생성 여부
var FV_BOOLEAN_PROGRESSBAR = false; // 프로그레스바 활성화 여부
var FV_STR_POP_RESULT; // 거래처, 대리점 팝업의 결과

/*  마감월 취득과 관련 변수 */
var FV_BIZ_CD;           // 업무 코드     ( 입력받은 업무코드를 callback 으로 넘기기 위해 사용되는 변수)
var FV_INPUT_DATE;       // 입력일자      ( 입력받은 입력일자를 callback 으로 넘기기 위해 사용되는 변수) 
var FV_INPUT_DATE_NM;    // 입력일자 명   ( 입력받은 입력일자 명을 callback 으로 넘기기 위해 사용되는 변수) 
var FV_SYNC;             // http.sync 여부( 월마감 함수가 실행 하기 전 http.sync 상태  callback 으로 넘기기 위해 사용되는 변수)
var FV_CLOSE_MNTH;       // 마감월
var FV_BOOLEAN_CLOSE;    // 마감여부 ( 1: 마감, 0: 미마감)

/* 데이타 셋을 볼수있게 하여준다.	*/
function cf_debug(sDsName) 
{
	var sArg = "dsName="+quote(sDsName);
	dialog("tmp::G_debug.xml", sArg, 800, 400);
}


/*******************************************************************************
 * @description         : 트랜잭션의 input, output 의 데이타를 확인
 * @param sSvcID        : 트랜잭션 서비스 아이디
 	      nUseDebug     : 사용자 정의 디비그의 출력 여부를 결정 - 1 사용, 2 미사용
	      nType         : 사용자 정의 화면 타입 -  1 콘솔 output 창 , 2 팝업창    
 * @etc                 : gv_useDebug - 디비그의 출력 여부를 결정
						: gv_debugType - 공통 정의 화면 타입 -  1 콘솔 output 창 , 2 팝업창   
*******************************************************************************/
function cf_trDebug(sSvcID, nUseDebug, nType)
{
	if(gv_useDebug == 1 || nUseDebug == 1)
	{
		if(nType == null)
		{
			nType = 1;
		}
		if(gv_debugType == 2 || nType == 2)
		{
			var sArg = "svcId="+quote(sSvcID);
				sArg += " sendData="+quote(http.SendHttpStr);
				sArg += " receiveData="+quote(http.RecvContents);
				
			dialog("tmp::trDebug.xml", sArg, 744, 352);
		}
		else
		{
			trace(" 트랜잭션 아이디  = " + sSvcID);
			trace("\n -------- 보낸 데이타 ---------- \n" + http.SendHttpStr);
			trace("\n -------- 받은 데이타 ---------- \n" + http.RecvContents);
		}
	}
}

var FV_BOOLEAN_PROGRESSBAR_SVC_ID;
/*******************************************************************************
 * @description    : 공통 트랜잭션
 * @param sSvcID   : 서비스 아이디
	      sInDs    : 보내는 데이타셋
	      sOutDs   : 받는 데이타셋
	      sArg     : 추가적인 파라메터
	      sURL     : 별도로 지정하는 주소
	      bIng	   : 처리중 표시 여부 : true - 표시 , false - 표시 없음(default)
	      bControll: 입력 제한 기능 : true - 제한 , false - 제한 없음(default)
 * @return         : 없음
*******************************************************************************/
function cf_transaction(sSvcID, sInDs, sOutDs, sArg, sURL, bIng, bControll)
{	
	if(bIng != null && bIng == true)
	{
		FV_BOOLEAN_PROGRESSBAR = true;
		FV_BOOLEAN_PROGRESSBAR_SVC_ID = sSvcID;
	}
	
	// 트랜잭션 정의 변수
	var sFixedURL   = "svUrl::standard.xmi";
	if(sURL != null && sURL != "")
	{
		sFixedURL = sURL;
	}
	
	if(sInDs == null || sInDs == "")
	{
		sInDs = "";
	}
	
	if(sOutDs == null || sOutDs == "")
	{
		sOutDs = "";
	}
	SetCapture();
	SetWaitCursor(true);
	
	var sParam = "nc_trId=" + quote(sArg);
	var sCallBackFuntion = "cf_callBack";
	
	// 화면ID를 셋팅한다.
	var frmNum = ""; // 화면번호
	if(IsExistVar("pFrmNum",true)){  
		frmNum = pFrmNum;
	}
	
	// 화면ID를 dataSet에 셋팅한다.
	var oDs = cf_isValid("nc_menuNumDs");
	if(oDs == null)
	{   
		create("Dataset", "nc_menuNumDs");
		oDs = object("nc_menuNumDs");
	}
	else
	{		
		oDs.clear();
	}		
	
	oDs.AddColumn("menu_num");
	oDs.addRow();
	oDs.setColumn(0,"menu_num",frmNum);
	
	if(sInDs == "" || sInDs == null ){
		sInDs = "nc_menuNumDs=nc_menuNumDs";
	}else{
		sInDs = sInDs +" nc_menuNumDs=nc_menuNumDs";
	}	
	
	Transaction(sSvcID, sFixedURL, sInDs, sOutDs, sParam, sCallBackFuntion);
	
	if(bIng != null && bIng == true)
	{
		if(bControll != null && bControll == true)
		{
			dialog("main::processingBar.xml","",350,120,"TitleBar=false CloseFlag=false");
		}
		else 
		{
			open("main::processingBar.xml","",350,120,"TitleBar=false CloseFlag=false");
		}
	}
}

/*******************************************************************************
 * @description    : 공통 트랜잭션 - 메시지refresh 테스트용-2009.06.10.15:30(svUrl1적용)
 * @param sSvcID   : 서비스 아이디
	      sInDs    : 보내는 데이타셋
	      sOutDs   : 받는 데이타셋
	      sArg     : 추가적인 파라메터
	      sURL     : 별도로 지정하는 주소
	      bIng	   : 처리중 표시 여부 : true - 표시 , false - 표시 없음(default)
	      bControll: 입력 제한 기능 : true - 제한 , false - 제한 없음(default)
 * @return         : 없음
*******************************************************************************/
function cf_transaction1(sSvcID, sInDs, sOutDs, sArg, sURL, bIng, bControll)
{	
	if(bIng != null && bIng == true)
	{
		FV_BOOLEAN_PROGRESSBAR = true;
		FV_BOOLEAN_PROGRESSBAR_SVC_ID = sSvcID;
	}
	
	// 트랜잭션 정의 변수
	var sFixedURL   = "svUrl1::standard.xmi";
	if(sURL != null && sURL != "")
	{
		sFixedURL = sURL;
	}
	
	if(sInDs == null || sInDs == "")
	{
		sInDs = "";
	}
	
	if(sOutDs == null || sOutDs == "")
	{
		sOutDs = "";
	}
	SetCapture();
	SetWaitCursor(true);

	var sParam = "nc_trId=" + quote(sArg);
	var sCallBackFuntion = "cf_callBack";

    alert(sSvcID);
    alert(sFixedURL);
    alert(sParam);    

	Transaction(sSvcID, sFixedURL, sInDs, sOutDs, sParam, sCallBackFuntion);
	
	if(bIng != null && bIng == true)
	{
		if(bControll != null && bControll == true)
		{
			dialog("main::processingBar.xml","",350,120,"TitleBar=false CloseFlag=false");
		}
		else 
		{
			open("main::processingBar.xml","",350,120,"TitleBar=false CloseFlag=false");
		}
	}
}

/*******************************************************************************
 * @description    : 공통 트랜잭션 - 메시지refresh 테스트용-2009.06.10.15:30(svUrl2적용)
 * @param sSvcID   : 서비스 아이디
	      sInDs    : 보내는 데이타셋
	      sOutDs   : 받는 데이타셋
	      sArg     : 추가적인 파라메터
	      sURL     : 별도로 지정하는 주소
	      bIng	   : 처리중 표시 여부 : true - 표시 , false - 표시 없음(default)
	      bControll: 입력 제한 기능 : true - 제한 , false - 제한 없음(default)
 * @return         : 없음
*******************************************************************************/
function cf_transaction2(sSvcID, sInDs, sOutDs, sArg, sURL, bIng, bControll)
{	
	if(bIng != null && bIng == true)
	{
		FV_BOOLEAN_PROGRESSBAR = true;
		FV_BOOLEAN_PROGRESSBAR_SVC_ID = sSvcID;
	}
	
	// 트랜잭션 정의 변수
	var sFixedURL   = "svUrl2::standard.xmi";
	if(sURL != null && sURL != "")
	{
		sFixedURL = sURL;
	}
	
	if(sInDs == null || sInDs == "")
	{
		sInDs = "";
	}
	
	if(sOutDs == null || sOutDs == "")
	{
		sOutDs = "";
	}
	SetCapture();
	SetWaitCursor(true);

	var sParam = "nc_trId=" + quote(sArg);
	var sCallBackFuntion = "cf_callBack";

    alert(sSvcID);
    alert(sFixedURL);
    alert(sParam);    

	Transaction(sSvcID, sFixedURL, sInDs, sOutDs, sParam, sCallBackFuntion);
	
	if(bIng != null && bIng == true)
	{
		if(bControll != null && bControll == true)
		{
			dialog("main::processingBar.xml","",350,120,"TitleBar=false CloseFlag=false");
		}
		else 
		{
			open("main::processingBar.xml","",350,120,"TitleBar=false CloseFlag=false");
		}
	}
}



/*******************************************************************************
 * @description    	 : 에러시 처리 메세지 
 * @param sErrCode   : 에러 코드
*******************************************************************************/
function cf_callAlert(sErrCode, sSvcID, sErrMsg)
{
	//trace(http.RecvHttpStr);
	http.Sync = false;

    //SSO인증 관련 추가함.(20120712)

	if(sErrCode == -2085613056)
	{
		gv_closeFlag = true;
		//alert("판매점 포탈에서 재 로그인 후, 사용바랍니다.\n\nHTTP 302: Session Time Out. ");
		if (length(strCookieId) == 0) {
			alert("저희 T.Key+로 넘어온 판매점 포탈 로그인ID가 없습니다. (포탈ID: "+ strCookieId +")\n\n02-6400-0570~3 으로 연락바랍니다. \n\n");
		} else {
		    alert("판매점 포탈에서 재 로그인 후, 사용바랍니다.(포탈ID: "+ strCookieId +")\n\nHTTP 302: Session Time Out. ");
		}
        ExecBrowser(gv_url);
		exit();
		return;
	}

	
	
	var sMsg;
	
	if(msg_flag == "WARN")
	{
		sMsg = msg_message;
		alert(sMsg);
	}else if(msg_flag == "CLOSE_TIME")
	{
	
		if(IsExistVar("pFrmNum", true)){
			sMsg = "보다 좋은 서비스 제공을 위해 시스템 점검 작업중입니다. \n아래 점검 시간 동안 사용이 불가능 합니다.\n\n" 
			+ msg_message+" \n\n확인버튼 클릭시 자동 로그아웃 됩니다.";
		}else{
			sMsg = "보다 좋은 서비스 제공을 위해 시스템 점검 작업중입니다. \n아래 점검 시간 동안 사용이 불가능 합니다.\n\n" 
			+ msg_message+" \n\n점검시간 이후에 로그인 하시기 바랍니다.";
		}		
		
		alert(sMsg);
		gv_closeFlag = true;
		
		exit();
		return;		
	}
	else 
	{
		cf_trDebug(sSvcID, 1);
		sMsg = "error_code = "+sErrCode+" , msg_flag = "+msg_flag+"\n";
		sMsg += "msg_message = "+msg_message;
		
		alert(sMsg);
	}	
}

/*******************************************************************************
 * @description    : 공통 콜백 함수
 * @param sSvcID   : 서비스 아이디
	      sErrCode : 에러 코드
	      sErrMsg  : 에러 메세지
 * @return         : 없음
*******************************************************************************/
function cf_callBack(sSvcID, sErrCode, sErrMsg) 
{
setcapture();
setwaitcursor(true);
//alert("22222222");	
//		for(var a=0; a<1000000; a++) {
//		}
	if(FV_BOOLEAN_PROGRESSBAR)
	{
		if(FV_BOOLEAN_PROGRESSBAR_SVC_ID != sSvcID) return;
		
		FV_BOOLEAN_PROGRESSBAR = false;
		FV_BOOLEAN_PROGRESSBAR_SVC_ID = "";
		
		var oWinObj = AllWindows("processingBar");

		if(oWinObj[0] != null) 
		{
			oWinObj[0].close();
		}

	}

	if(sSvcID != "svcLogOut" && msg_flag == "LOGOUT")
	{	
		/*
		FV_BOOLEAN_PROGRESSBAR = false;
		var oWinObj = AllWindows("processingBar");
		if(oWinObj[0] != null) 
		{
			oWinObj[0].close();
		}	
		alert(MSG_00993);
		CloseSession();
		
		LoginSession();
		return;
		*/
		
		alert(MSG_00993);
		gv_closeFlag = true;
		//일반로그인일때만
		if (length(strCookieId) == 0) {
			execProc(AliasToRealPath("%TOBE%" + "MiPlatform320U\\MiPlatform320U.exe" ), "SKTST");
		} else {
		    //alert("SSO 로그아웃");
		}
		exit();
		return;
	} 

	if(msg_flag != "OK")
	{
		cf_callAlert(sErrCode, sSvcID, sErrMsg);	
	}
	else
	{
		cf_trDebug(sSvcID);
		if(sSvcID == "svcCommonCode")
		{
			cf_setCommoncodeCallBack();	
		}
		else if(sSvcID.indexOf("svcCommonPop") >= 0)
		{	
			cf_popCallback(sSvcID, sErrCode, sErrMsg);
		}
		else if(sSvcID == "getCloseInfo"){
			cf_setCloseInfoCallBack();
		}
		else if(sSvcID == "checkCloseMnth"){
			cf_checkCloseMnthCallBack();
	    }
		else if(sSvcID == "checkCloseDay"){
			cf_checkCloseDayCallBack();
		}else if(sSvcID == "getSessionExtension"){
			// 후처리 없음.
		}
		else
		{
			// 패스워드 만료일 메시지 창 출력.
			if(sSvcID == "svcLogIn" && msg_message != "로그인에 성공했습니다."){
				alert(msg_message);
			}else{
				cf_showSysMsg(msg_message);
			}
			
			CallScript("f_callBack('" + sSvcID + "')");
		}
	}

setwaitcursor(false);
releasecapture();
}

/*******************************************************************************
 * @description       : 파라메터를 데이타셋으로 설정
 * @param oDataSet    : 데이타셋 객체
	      sColumnID   : 컬럼 ID
	      sValue      : 설정값
 * @return            : 없음
*******************************************************************************/
function cf_setParam(oDataSet, sColumnID, sValue, nColumnType)
{
	if(oDataSet.GetColIndex(sColumnID) == -1)
	{
		var sDefaultType = "String";
		if(nColumnType != null)
		{
			sDefaultType = cf_getColumnType(nColumnType);
		}

		oDataSet.AddColumn(sColumnID, sDefaultType);
		
	}
	if(oDataSet.GetRowCount() < 1)
	{
		oDataSet.AddRow();
	}
	oDataSet.SetColumn(0, sColumnID, sValue);
}

/*******************************************************************************
 * @description       	 : 데이타셋 칼럼 타입 취득
 * @param nColumnType    : 타입 번호
*******************************************************************************/
function cf_getColumnType(nColumnType)
{
	var sColumnType;
	
	if(nColumnType == 0)
	{
		sColumnType = "String";
	}
	else if(nColumnType == 1)
	{
		sColumnType = "Int";
	}
	
	return sColumnType;
}

/*******************************************************************************
 * @description         : 그리드에 신규와 수정시의 효과 이벤트 설정
 * @param oGrd          : 그리드 객체
*******************************************************************************/
function cf_setGrdRowStatusEvent(oGrd, nCell)
{
	var sGrdDs = oGrd.BindDataset;
	var sRowStatusExpr = "expr:decode("+sGrdDs+".GetRowType(currow),'insert','icon_n','update','icon_u','')";
	if(nCell == null)
	{
		nCell = 0;
	}
	oGrd.SetCellProp("body", nCell, "BkImageID", sRowStatusExpr);
}

/*******************************************************************************
 * @description    : 그리드에 바인딩된 데이타셋의 OnLoadCompleted 이벤트 계승 
                    - 헤더 타이틀을 자동 초기화 하기위해서 
*******************************************************************************/
function cf_initGrdHeadTitle(obj,nErrorCode,strErrorMsg,nReason)
{
	if(nReason == 0)
	{
		var oGrd = object(obj.Argument);
		cf_initGrdHeader(oGrd);
	}
}

/*******************************************************************************
 * @description    		: 그리드의 헤더 타이틀을 초기화
 * @param oGrd          : 그리드 객체
*******************************************************************************/
function cf_initGrdHeader(oGrd)
{
	var nHeadCount = oGrd.GetCellCount("head");
	var sHeadTitle;
	for(var i=0; i<nHeadCount; i++)
	{
		if(oGrd.GetCellProp("head", i, "display") == "text")
		{
			sHeadTitle = oGrd.GetCellProp("head", i, "text");
			sHeadTitle = replace(sHeadTitle, CONST_ASC_MARK,"");
			sHeadTitle = replace(sHeadTitle, CONST_DESC_MARK,"");
			oGrd.SetCellProp("head", i, "text", sHeadTitle);
		}
	}
}

/*******************************************************************************
 * @description    : 그리드의 헤더별로 소팅 및 마크 표시 - OnHeadClick 이벤트 계승
*******************************************************************************/
function cf_markGridSort(obj, nCell, nX, nY, nPivotIndex)
{
	var sHeadText,sflag;
	var dsObj = obj.getForm().object(obj.BindDataset);
	dsObj.Argument = obj.id;
	if(length(dsObj.OnLoadCompleted).length < 1)
	{
		dsObj.OnLoadCompleted = "cf_initGrdHeadTitle";
	}
	if(dsObj.getRowCount() > 0)
	{
		SetWaitCursor(true);
		
		//---------------------------------------------------------
		//  해당하는 셀의 타이틀의 소트마크를 추가 또는 변경한다.
		//---------------------------------------------------------
		var nSelCol = obj.GetCellProp("head", nCell, "col");
		var nSelRow = obj.GetCellProp("head", nCell, "row");
		var nBodyCell;
		var sProp = "";
		var bSub = false;
	
		var nBodyRow = 0;
		for(var i = 0; i < obj.GetCellCount("body"); i++)
		{	
	/*
			if(obj.GetCellProp("body", i, "col") == nSelCol
			&& obj.GetCellProp("body", i, "row") == nSelRow)
			{
				nBodyCell = i;
				break;
			}
	*/
			if(obj.GetCellProp("body", i, "col") == nSelCol)
			{
				if(nBodyRow <= nSelRow)
				{
					nBodyCell = i;
				}
				nBodyRow = nBodyRow + toNumber(obj.GetCellProp("body", i, "rowspan"));
			}
		}
	
		if(obj.GetSubCellCount("head", nCell) > 0)
		{
			bSub = true;
			if(uf_IsEmpty(obj.GetSubCellProp("head", nCell, 0, "expr")) == false)
			{
				sHeadText = obj.GetSubCellProp("head", nCell, 0, "expr");
				sProp = "Expr";
			}
			else if(uf_IsEmpty(obj.GetSubCellProp("head", nCell, 0, "text")) == false)
			{
				sHeadText = obj.GetSubCellProp("head", nCell, 0, "text");
				sProp = "Text";		
			}
		}
		else if(uf_IsEmpty(obj.GetCellProp("head", nCell, "Expr")) == false)
		{
			sHeadText = obj.GetCellProp("head", nCell, "Expr");
			sProp = "Expr";
		}
		else if(uf_IsEmpty(obj.GetCellProp("head", nCell, "Text")) == false)
		{
			sHeadText = obj.GetCellProp("head", nCell, "Text");
			sProp = "Text";			
		}
		
		if(right(sHeadText,1) == CONST_ASC_MARK
		|| right(sHeadText,2) == (CONST_ASC_MARK + "'"))
		{
			dsObj.sort(obj.GetCellProp("Body",nBodyCell,"colid"),false);
			sFlag = CONST_DESC_MARK;
		}
		else
		{
			dsObj.sort(obj.GetCellProp("Body",nBodyCell,"colid"),true);
			sFlag = CONST_ASC_MARK;
		}
		
		sHeadText = replace(sheadText,CONST_ASC_MARK,"");
		sHeadText = replace(sheadText,CONST_DESC_MARK,"");
		if(right(sHeadText, 1) == "'")
		{
			sHeadText = left(sHeadText, sHeadText.length - 1) + sflag + "'"; 
		}
		else
		{
			sHeadText = sHeadText + sflag; 
		}
	
		if(bSub)
		{
			obj.SetSubCellProp("head", nCell, 0, sProp, sHeadText);
		}
		else
		{
			obj.SetCellProp("head", nCell, sProp, sHeadText);
		}
	
		
		//---------------------------------------------------------
		//  해당하는 셀 이외의 해더 타이틀의 소트마크를 제거한다.
		//---------------------------------------------------------
		var sRepText="";
		for(i = 0; i < obj.GetCellCount("head"); i++)
		{
			bSub = false;
			sRepText = "";
			if(nCell <> i)
			{
				if(obj.GetSubCellCount("head", i) > 0)
				{
					bSub = true;
				
					if(uf_IsEmpty(obj.GetSubCellProp("head", i, 0, "expr")) == false)
					{
						sRepText = obj.GetSubCellProp("head", i, 0, "expr");				
						sProp = "Expr";
					}
					else if(uf_IsEmpty(obj.GetSubCellProp("head", i, 0, "text")) == false)
					{
						sRepText = obj.GetSubCellProp("head", i, 0, "text");
						sProp = "Text";			
					}
				}
				else if(uf_IsEmpty(obj.GetCellProp("head", i, "Expr")) == false)
				{
					sRepText = obj.GetCellProp("head", i, "Expr");
					sProp = "Expr";			
				}
				else if(uf_IsEmpty(obj.GetCellProp("head", i, "Text")) == false)
				{
					sRepText = obj.GetCellProp("head", i, "Text");
					sProp = "Text";			
				}
				
				if(uf_IsEmpty(sRepText))
					continue;
				
				sRepText = replace(sRepText, CONST_ASC_MARK,"");
				sRepText = replace(sRepText, CONST_DESC_MARK,"");
				if(bSub)
				{
					obj.SetSubCellProp("head",i, 0, sProp, sRepText);
				}
				else
				{
					obj.SetCellProp("head",i, sProp, sRepText);
				}
			}
		}
		
		SetWaitCursor(false);
	
		return sflag;
	}
}

/*******************************************************************************
 * @description         : 트랜잭션의 디버그 모드를 설정
 * @param nUse          : 디버그 사용 여부
	      nType         : 화면 타입 -  1 콘솔 output 창 , 2 팝업창      
*******************************************************************************/
function cf_setDebugMode(nUse, nType)
{
	gv_useDebug = nUse;
	gv_debugType = nType;
}



/*******************************************************************************
 * @description         : 인수로 넘겨 받은 명의 함수를 실행학소 창을 종료
 * @param sMethodName   : 메소드 명 
		  sParam   		: 메소드 인자
*******************************************************************************/
function cf_callnClose(sMethodName, sParam)
{
	if(parent.IsExistFunc(sMethodName))
	{
		var sArg = "";
		if(sParam != null && sParam.length > 0)
		{
			var arrParam = sParam.split(",");
			var nParamCount = arrParam.length;
			for(var i=0; i<nParamCount; i++)
			{
				sArg += ",'"+arrParam[i]+"'";
			}
			
			sArg = substr(sArg, 1);
		}
		CallScript("parent." + sMethodName + "("+sArg+")");
	}
	this.Close();
}

/*******************************************************************************
 * @description             : 엑셀 파일을 읽어 지정하는 데이타셋에 로드
 * @param sDsName   		: 데이타셋 아이디
		  nUseDsInfo   		: 데이타셋 칼럼 정보 사용 유무 - 1: 사용, 0: 무시
		  nStartIndex   	: 엑셀을 읽을 시작 행 위치
 * @return	                : 선택한 엑셀파일의 풀패스 
 * @etc                     : DS 칼럼 정보 사용시 엑셀의 셀 순서와 DS 칼럼 순서가 같아야 함!
*******************************************************************************/
function cf_importExcel(sDsName, nUseDsInfo, nStartIndex, nSheetIndex)
{
	fld_excel.Type = "OPEN";
	var bResult = fld_excel.Open();
	
	if ( bResult )
	{
		var sFullPath = fld_excel.FilePath + '\\' + fld_excel.FileName;

		if(nUseDsInfo == null)
		{
			nUseDsInfo = 1;
		}
		
		if(nStartIndex == null)
		{
			nStartIndex = 1;
		}
		
		if(nSheetIndex == null)
		{
			nSheetIndex = 0;
		}

		var nResult = ext_ExcelImportByIndex(sFullPath, nSheetIndex, sDsName, nUseDsInfo, 0, 0, nStartIndex);
		
		if(nResult == 0)
		{
			return sFullPath;
		}
	}
	
	
}


/*******************************************************************************
 * @description         : 그리드의 데이터를 엑셀화로 출력
 * @param oGrd   		: 그리드 객체 
		  nMode   		: 엑셀 저장 모드 - 1 or null : 엑셀로드 , 2 : 파일 저장 , 3 : bin 파일 변환 방식 파일 저장
		  
  3 : cf_excelEx_OnTimer 를 해주어야 함.  
*******************************************************************************/
var str_FileName;
function cf_exportExcel(oGrd, nMode)
{
	/*
    alert("개발 중 입니다.");
    return;
    */
	var sFilePath = "";
	var sFileName = "";
	var sSavedName = "";
	
	var sSheetName = "sheet1";
	var stmp;
	
	var oGrdDs = object(oGrd.BindDataset);
	
	if (oGrdDs.GetRowCount() < 1) 
	{
		alert(MSG_00983);
		return;
	}
			
	if(nMode != null && nMode == 3){
		if( fdg_excel.Open("C:\\") ){

			FileName = fdg_excel.FilePath + "\\" + fdg_excel.FileName;
			var file = split(fdg_excel.FileName, ".");
			var filepath = fdg_excel.FilePath +"\\";
			
			if(G_isNull(file[1])) {
				file[1] += ".bin"; //확장자를 임의로 변경하여 저장합니다. 
			}
			else
			{
				file[0] += ".";
			}
			 
			str_FileName = filepath+file[0] + file[1];
			
			oGrd.SaveExcel(str_FileName, "sheet1");
	
			setTimer(1,1000); //임의변경한 확장자를 다시 엑셀포맷으로 변경합니다.
			}
	} else if(nMode != null && nMode == 2){
		if( fld_excel.Open("C:\\") )
		{
			
			sFilePath = fld_excel.FilePath;
			sFileName = fld_excel.FileName;
			sTmp = substr(sFileName, length(sFileName) - 3, length(sFileName));
			if (sTmp != "xls")
			{
			   sFileName = sFileName + ".xls";
			}
			sSavedName = sFilePath + "\\" + sFileName;
			oGrd.SaveExcel(sSavedName, sSheetName);
			
		}
		else
		{
			alert(MSG_00982);
		}
	}
	else
	{
		oGrd.ExportExcelEx(sSheetName, "A1", false, true);
	}
}

function G_isNull(as_source)
{
	if (length(toString(as_source)) == 0 || as_source == null) {
		return true;
	}
	else {
		return false;
	}
}

function cf_excelEx_OnTimer(obj,nEventID)
{
	if( nEventID == "1" )
	{
		killTimer(1);
		//ExtCommon api의 함수를 이용하여 엑셀형식으로 재저장합니다.
		ext_ExcelSaveAs(str_FileName ,replace(str_FileName,".bin",".lsx"),"");
		b_File.Delete(str_FileName); //임시로 저장한 파일을 지웁니다.
	}
}

/*******************************************************************************
 * @description         : 그리드의 데이터를 엑셀화로 출력시 로그남기기
 * @param ds_xlslog	: 자바로 넘길 파라메터
		  oGrd   		: 그리드 객체 
		  sMsg   		: 로그내용
		  sFrmID		: 화면 아이디
		  iRows			: 엑셀 레코드 개수
*******************************************************************************/
function cf_exportExcelLog(ds_xlslog, oGrd, sMsg, sFrmID, iRows)
{
	if(iRows > 0)
	{
		cf_setParam(ds_xlslog, "EXPORT_DTM",     GetDate());
		cf_setParam(ds_xlslog, "EXPORT_SCREEN",  sFrmID);
		cf_setParam(ds_xlslog, "EXPORT_COND",    sMsg); 
		cf_setParam(ds_xlslog, "EXPORT_USER",    gds_session.GetColumn(0, "loginId"));
		cf_setParam(ds_xlslog, "USER_LOGIN_IP",  gds_session.GetColumn(0, "ip"));
		cf_setParam(ds_xlslog, "USER_ORG_CD",    gds_session.GetColumn(0, "orgId"));
		cf_setParam(ds_xlslog, "USER_AGENCY_CD", gds_session.GetColumn(0, "posAgencyId"));
		cf_setParam(ds_xlslog, "USER_USER_GRP",  gds_session.GetColumn(0, "userGrp"));
		cf_setParam(ds_xlslog, "EXPORT_CNT",     iRows);
	
		// 트랜잭션 설정
		var s1 	= "addExcelExportHst";
		var s2 	= "nc_input_fieldDs=ds_xlslog";
		var s3 	= "";
		var s4 	= "sktst.bas.PRM#addExcelExportHst";
		
		// 공통 트랜잭션 호출
		cf_Transaction(s1, s2, s3, s4);
	}
}


/*******************************************************************************
 * @description         	 : 엑셀화할 정보 설정
 * @param sGrdId   		 	 : 그리드 아이디
		  sSheetName   		 : 엑셀 쉬트 이름
		  sCellNo   		 : 표시될 셀 기준 번호
		  bGrdShape   		 : 그리드의 틀 표시 여부
*******************************************************************************/
function cf_setExcelInfo(sGrdId, sSheetName, sCellNo,  bGrdShape)
{ 
	var oDs = cf_isValid("ds_dinamicExcel");
	if(oDs == null)
	{
		create("Dataset", "ds_dinamicExcel");
		oDs = ds_dinamicExcel;
		oDs.AddColumn("sheetName");
		oDs.AddColumn("cellNo");
		oDs.AddColumn("gridId");
		oDs.AddColumn("grdShape");
	}
	var nRow = oDs.addRow();
	oDs.setColumn(nRow, "gridId", sGrdId);
	
	if(sSheetName == null)
	{
		sSheetName = "sheet1";
	}
	oDs.setColumn(nRow, "sheetName", sSheetName);
	
	if(sCellNo == null)
	{
		sCellNo = "A1";
	}
	oDs.setColumn(nRow, "cellNo", sCellNo);
	
	if(bGrdShape == null)
	{
		bGrdShape = "false";
	}
	oDs.setColumn(nRow, "grdShape", bGrdShape);
}

/*******************************************************************************
 * @description         			:  n개의 그리드의 데이터를 엑셀화 
 * @param sActiveSheetName   		: 그리드 객체 
		  nMode   					: 엑셀 저장 모드 - 1 or null : 엑셀로드 , 2 : 파일 저장
*******************************************************************************/
function cf_exportDinamicExcel(nMode, sActiveSheetName)
{
	/*
    alert("개발 중 입니다.");
    return;
    */
    
	var nRowCount = ds_dinamicExcel.getRowCount();
	if(nRowCount > 0)
	{
		var oDExel;
		var sXML;
				
		oDExel = CreateExportObject();
		oDExel.ExportType		 = "Excel";
		
		var sFilePath = "";
		var sFileName = "";
		var sSavedName = "";
		if(nMode != null && nMode == 2)
		{
			if( fld_excel.Open( "C:\\" ) )
			{
				sFilePath = fld_excel.FilePath;
				sFileName = fld_excel.FileName;
				sTmp = substr(sFileName, length(sFileName) - 3, length(sFileName));
				if (sTmp != "xls")
				{
				   sFileName = sFileName + ".xls";
				}
				sSavedName = sFilePath + "\\" + sFileName;
				
			}
			else
			{
				alert(MSG_00982);
				return;
			}
		}
		else
		{
			sSavedName = "c:\\"+pFrmNum+"_"+getDate()+".xls";
		}

		oDExel.ExportFileName	 = sSavedName;
		
		if(sActiveSheetName == null)
		{
			sActiveSheetName = ds_dinamicExcel.getcolumn(0, "sheetName");
		}
		oDExel.ActiveSheetName   = sActiveSheetName;
		oDExel.MakeEmptyFileWhenNotExist = true;
		// oDExel.ExportSinglePivotColHead = false;
		
		var sSheetName, sCellNo, sGrdId, bVisible, bGrdShape;
		for(var i=0; i<nRowCount; i++)
		{
			sSheetName = ds_dinamicExcel.getColumn(i, "sheetName");
			sCellNo = ds_dinamicExcel.getColumn(i, "cellNo");
			sGrdId = ds_dinamicExcel.getColumn(i, "gridId");
			bGrdShape = ds_dinamicExcel.getColumn(i, "grdShape");
			
			oDExel.AddExportGrid(sSheetName + "!" + sCellNo, object(sGrdId), true, bGrdShape);	
		}			
		oDExel.Export();
		oDExel.Close();
		oDExel = null;
	}
	else
	{
		alert(MSG_00983);
	}
}

/*******************************************************************************
 * @description          : 데이타셋의 카피 - 대상 DS를 기준으로 목적 DS에 정보를 복사
                          - 없으면 추가한다.
 * @param oTargetDs      : 목적 DS 객체
		  oSourceDs      : 대상 DS 객체
		  nRow           : 대상 로우 - 없으면 전체
		  bOption        : false시 목적 DS에 로우가 존재하고, 칼럼이 있는 것만 복사, defualt - true
		  bAppend        : true시 목적 DS의 로우에 이어서 추가, default - false
*******************************************************************************/
function cf_copyDataSet(oTargetDs, oSourceDs, nRow, bOption, bAppend)
{
	oTargetDs.FireEvent = false;
	var nSourceColCount = oSourceDs.GetColCount();
	var nSourceRowCount = oSourceDs.GetRowCount();
	var sChildColId = "";
	var nTargetRow = 0;
	if(bOption == null)
	{
		bOption = true;
	}
	if(bAppend == null)
	{
		bAppend = false;
	}
	if(nRow == null)
	{
		if(bAppend == false)
		{
			oTargetDs.clearData();
		}
		for(var i=0; i<nSourceRowCount; i++)
		{
			nTargetRow = oTargetDs.AddRow();
			for(var j=0; j<nSourceColCount; j++)
			{
				sChildColId = oSourceDs.GetColID(j);
				
				if(bOption)
				{
					if(oTargetDs.GetColIndex(sChildColId) == -1)
					{
						oTargetDs.AddColumn(sChildColId);
					}
					
					if(oTargetDs.getRowCount() < 1)
					{
						nTargetRow = oTargetDs.addRow();
					}
				}
		
				oTargetDs.SetColumn(nTargetRow, sChildColId, oSourceDs.GetColumn(i, sChildColId));
			}
		}
	}
	else
	{
		if(bAppend == false)
		{
			oTargetDs.clearData();
		}
		
		if(bAppend || ( bOption && oTargetDs.getRowCount() < 1))
		{
			nTargetRow = oTargetDs.addRow();
		}
		
		for(var j=0; j<nSourceColCount; j++)
		{
			sChildColId = oSourceDs.GetColID(j);
			
			if(bOption)
			{
				if(oTargetDs.GetColIndex(sChildColId) == -1)
				{
					oTargetDs.AddColumn(sChildColId);
				}
				
				if(oTargetDs.getRowCount() < 1)
				{
					nTargetRow = oTargetDs.addRow();
				}
			}
			
			oTargetDs.SetColumn(nTargetRow, sChildColId, oSourceDs.GetColumn(nRow, sChildColId));
		}
	}
	oTargetDs.FireEvent = true;
}

/*******************************************************************************
 * @description    : 공통 코드 콜백 함수
*******************************************************************************/
function cf_setCommoncodeCallBack()
{

	var nRowCount = ds_tmpCommonEnvInfo.getRowCount();
	var sCommCdId;
	if(nRowCount > 0)
	{
		for(var i=0; i<nRowCount; i++)
		{
			ds_tmpCommonEnvInfo.SetColumn(i, "transaction", 0);
			sCommCdId = ds_tmpCommonEnvInfo.getColumn(i, "codeId");
			
			var oResult = ext_FindRow("gds_comCode", "COMM_CD_ID", sCommCdId);
			var nResultCount = oResult.length;
			
			if(nResultCount > 0)
			{	
				for (j = 0 ; j < nResultCount ; j++)
				{
					gds_comCode.SelectRow(oResult[j]);
				}
				gds_comCode.DeleteSelected();
			}

		}
		//Destroy("ds_tmpCodeCondition");
		if(gds_temp.GetRowCount() > 0)
		{
		
			if(gds_comCode.GetRowCount() > 0)
			{
				gds_comCode.AppendDataset(gds_temp);
			}
			else
			{
				gds_comCode.Copy(gds_temp);
			}		

			cf_checkCommonCode();
		}
		else
		{
			alert(sCommCdId+" 공통 코드가 존재하지 않습니다.");
			ds_tmpCommonEnvInfo.clearData();
			return;
		}
	}
}

/*******************************************************************************
 * @description          : 공통 코드의 설정을 위한 데이타셋 생성
*******************************************************************************/
function cf_setCommonCodeDsEnv()
{
	var oCodeCondtionDs = cf_isValid("ds_tmpCommonEnvInfo");
	if(oCodeCondtionDs == null)
	{
		create("Dataset", "ds_tmpCommonEnvInfo");
		ds_tmpCommonEnvInfo.AddColumn("componentId");
		ds_tmpCommonEnvInfo.AddColumn("codeId");
		ds_tmpCommonEnvInfo.AddColumn("dsOption");
		ds_tmpCommonEnvInfo.AddColumn("cellNo");
		ds_tmpCommonEnvInfo.AddColumn("transaction");		
	}
	else
	{
		ds_tmpCommonEnvInfo.clearData();
	}
}

/*******************************************************************************
 * @description          : 공통 코드를 세팅할 컴포넌트 설정
 * @param sCompId        : 컴포넌트 아이디
		  sCodeId        : 공통코드 아이디
		  sOpt           : 데이타셋 옵션
		  nCell          : 그리드인 경우 셀의 번호
		  nTrs           : 실시간 반영 여부 1(true) - 트랜잭션 , 0(false) - 기존값
*******************************************************************************/
function cf_setCommonCode(sCompId, sCodeId, sOpt, nCell, nTrs)
{
	var nRow = ds_tmpCommonEnvInfo.AddRow();
	ds_tmpCommonEnvInfo.SetColumn(nRow, "componentId", sCompId);
	ds_tmpCommonEnvInfo.SetColumn(nRow, "codeId", sCodeId);
	if(sOpt != null)
	{
		ds_tmpCommonEnvInfo.SetColumn(nRow, "dsOption", sOpt);
	}
	if(nCell != null)
	{
		ds_tmpCommonEnvInfo.SetColumn(nRow, "cellNo", nCell);
	}
	if(nTrs != null)
	{
		ds_tmpCommonEnvInfo.SetColumn(nRow, "transaction", nTrs);
	}
	else
	{
		ds_tmpCommonEnvInfo.SetColumn(nRow, "transaction", true);
	}
}

/*******************************************************************************
 * @description          : 공통 코드의 존재 여부 체크 및 데이타셋 바인드 호출
*******************************************************************************/
function cf_checkCommonCode()
{
//setcapture();
//setwaitcursor(true);

	var sCommonCodeId;
	var nTrs;
	for(var i=ds_tmpCommonEnvInfo.getRowCount(); i>=0; i--)
	{
		sCommonCodeId = ds_tmpCommonEnvInfo.getColumn(i, "codeId");
		nTrs = ds_tmpCommonEnvInfo.getColumn(i, "transaction");
		
		if(gds_comCode.FindRow("COMM_CD_ID", sCommonCodeId) >= 0 && nTrs == 0)
		{
			// 기존 코드 세팅
			cf_setComponentBind(i);
			ds_tmpCommonEnvInfo.DeleteRow(i);
		}
	}
	
	// 새로 추가 
	var nLastConditionCount = ds_tmpCommonEnvInfo.getRowCount();
	if(nLastConditionCount > 0)
	{

		var oCodeCondtionDs = cf_isValid("ds_tmpCodeCondition");
		if(oCodeCondtionDs == null)
		{
			create("Dataset", "ds_tmpCodeCondition");
		}
		else
		{
			ds_tmpCodeCondition.clear();
		}

		var nColNo = 0;
		var nNowRow;
		var nFindRow;
		
		// 중복 코드 제거 ,  파라메터 설정
		for( i=ds_tmpCommonEnvInfo.getRowCount(); i>=0; i--)
		{	
			nNowRow = i-1;
			if(nNowRow >= 0)
			{
				nFindRow = ds_tmpCommonEnvInfo.FindRow("codeId", ds_tmpCommonEnvInfo.GetColumn(nNowRow, "codeId"));
		
				if(nNowRow == nFindRow)
				{
					ds_tmpCodeCondition.addColumn("COMM_CD_ID"+nColNo);
					if(ds_tmpCodeCondition.getRowCount() < 1)
					{
						ds_tmpCodeCondition.addRow();
					}
					ds_tmpCodeCondition.setColumn(0, "COMM_CD_ID"+nColNo, ds_tmpCommonEnvInfo.GetColumn(nNowRow, "codeId"));
					nColNo++;
				}
				
			}
		}

		// 트랜잭션 설정
		var sSvcID = "svcCommonCode";
		var sInDs = "nc_input_fieldDs=ds_tmpCodeCondition";
		var sOutDs = "gds_temp=commonCode";
		var sArg = "sktst.bas.CDM#getCommonCodeListByID"; 

		// 공통 트랜잭션 호출
		cf_transaction(sSvcID, sInDs, sOutDs, sArg);
	
	
//setwaitcursor(false);
//releasecapture();	
	}

}

/*******************************************************************************
 * @description          : 공통 코드를 컴포넌트에 자동 설정
 * @param nRow           : 설정 정보를 가지고 있는 DS의 로우 번호
*******************************************************************************/
function cf_setComponentBind(nRow)
{
	var sComponentId = ds_tmpCommonEnvInfo.getColumn(nRow, "componentId");
	var oComponent = object(sComponentId);
	
	var sComponentType = oComponent.getType();
	var sCodeColumn = "COMM_CD_VAL";
	var sDataColumn = "COMM_CD_VAL_NM";
	var sCommonCodeDsId = ds_tmpCommonEnvInfo.getColumn(nRow, "codeId");
	var sOpt = ds_tmpCommonEnvInfo.getColumn(nRow, "dsOption");
	var nCell = ds_tmpCommonEnvInfo.getColumn(nRow, "cellNo"); 
	var sOptCd = "";
	var sOptText = "";

	// 옵션 설정
	if(sOpt != null && sOpt.length > 0)
	{
		var arrOpt = sOpt.split(",");
	
		if(arrOpt.length > 1)
		{
			sOptCd = trim(arrOpt[0]);
			sOptText = 	trim(arrOpt[1]);
		}
		else if(arrOpt.length > 0)
		{
			sOptCd = "";
			sOptText = 	trim(arrOpt[0]);
		}
	}

	gds_comCode.Filter("COMM_CD_ID == '"+sCommonCodeDsId+"'");
	if(gds_comCode.GetRowCount() < 1)
	{
		alert(MSG_00981);
		return;
	}
	
	if(sComponentType == "Dataset")
	{
		oComponent.copyF(gds_comCode);
		gds_comCode.UnFilter();
		if(!uf_isEmpty(sOptText))
		{
			oComponent.InsertRow(0);
			oComponent.SetColumn(0, sCodeColumn, sOptCd);
			oComponent.SetColumn(0, sDataColumn, sOptText);
		}		
		return;
	}	

	var sDsId = sCommonCodeDsId+"_"+nRow;
	var oDs = cf_isValid(sDsId);
	if(oDs == null)
	{
		create("Dataset", sDsId);
		oDs = object(sDsId);
	}
	else
	{
		oDs.clear();
	}

	oDs.FireEvent = false;
	oDs.copyF(gds_comCode);
	gds_comCode.UnFilter();
	
	if(!uf_isEmpty(sOptText))
	{
		oDs.InsertRow(0);
		oDs.SetColumn(0, sCodeColumn, sOptCd);
		oDs.SetColumn(0, sDataColumn, sOptText);
	}
	oComponent.reDraw = false;
	if(sComponentType == "Grid")
	{
		oComponent.SetCellProp("body",nCell,"ComboDataset",sDsId); 
		oComponent.SetCellProp("body",nCell,"ComboCol",sCodeColumn); 
		oComponent.SetCellProp("body",nCell,"ComboText",sDataColumn); 
		oDs.FireEvent = true;
		oComponent.redraw = true;
		return;
	}
	
	
	oComponent.InnerDataset = sDsId;
	oComponent.CodeColumn = sCodeColumn;
	oComponent.DataColumn = sDataColumn;

	oComponent.Index = 0;
	oDs.FireEvent = true;
	oComponent.redraw = true;
}

/*******************************************************************************
 * @description               : 콤보 컴포넌트에 초기 항목을 설정
 * @param oCmb                : 콤보 객체
	      sInitTxt            : 설정할 텍스트
	      sInitCd             : 설정할 코드 - 생략시 '' 세팅
*******************************************************************************/
function cf_setInitDataForCmb(oCmb, sInitTxt, sInitCd)
{
	var sInnerDataset = oCmb.InnerDataset;

	if(sInnerDataset.length > 0)
	{
		var oCmbDs = object(sInnerDataset);
		if(sInitCd == null)
		{
			sInitCd = "";
		}
		
		if(oCmbDs.GetColIndex(oCmb.CodeColumn) == -1 )
		{
			oCmbDs.addColumn(oCmb.CodeColumn);
			oCmbDs.addColumn(oCmb.DataColumn);
		}
		oCmbDs.insertRow(0);			
		oCmbDs.SetColumn(0, oCmb.CodeColumn , sInitCd);
		oCmbDs.SetColumn(0, oCmb.DataColumn , sInitTxt);
		
		oCmb.index = 0;
	}
}


/*******************************************************************************
 * @description      : 그리드에 달력 이벤트 설정- OnExpandUp 이벤트
 * @param nSetCell   : 
 * @event 		     : oGrd.OnExpandUp 
*******************************************************************************/
function cf_setGrdCalendarEvent(oGrd)
{
	oGrd.OnExpandUp = "cf_setGrdCalendar";
}

/*******************************************************************************
 * @description    : 그리드에 팝업 달력을 표시
 * @param oGrd     : 그리드 객체
		  nRow     : 선택 로우
		 nCell     : 선택 셀
*******************************************************************************/
function cf_setGrdCalendar(oGrd, nRow, nCell)
{	
	var sType =  oGrd.GetCellProp("Body", nCell, "display");
	if(sType == "date")
	{
		var rect = oGrd.GetCellRect(nRow, nCell);
		var nLeft = ClientToScreenX(oGrd, rect[0]);
		var nTop = ClientToScreenY(oGrd, rect[1]);
		var nCellWidth = rect[2] - rect[0];
		var nCellHeight = rect[3] - rect[1];
	
		var sDsColId =  oGrd.GetCellProp("Body", nCell, "ColId");
		var sGrdDs = oGrd.BindDataset;
		var sColValue = object(sGrdDs).GetColumn(nRow, sDsColId);
		if(uf_isEmpty(sColValue))
		{
			sColValue = today();
		}
		var sUserData = sGrdDs+","+nRow+","+sDsColId;
		var sCalContent;
		if (FV_BOOLEAN_POP_CALENDAR == false) 
		{
			FV_BOOLEAN_POP_CALENDAR = true;
			Create("PopupDiv", "popCalendar", 'width="152" height="133"');

			sCalContent += '<Contents>';
			sCalContent += '<Calendar Id="calendar" Value="'+sColValue+'" UserData="'+sUserData+'" OnDayClick="cf_setDateValueFromPopCal"  ' + chr(10);
			sCalContent += 'Border="Flat" ButtonImageID="btn_i_calendar" DayFont="Default,-1" '+ chr(10);
			sCalContent += 'HeaderBorder="NONE" HeaderFont="Default,-1" HeadStyle="sty_calendar_head" '+ chr(10);
			sCalContent += 'Height="152" Left="0" LeftMargin="2" MonthOnly="TRUE"  '+ chr(10);
			sCalContent += 'NullValue="&#32;" RightMargin="2" SaturdayTextColor="user28" Style="sty_div_search" '+ chr(10);
			sCalContent += 'SundayTextColor="user27" TabOrder="1" TodayColor="user26" Top="0" '+ chr(10);
			sCalContent += 'WeeksFont="Default,-1" WeekStyle="sty_calendar_week" Width="152"></Calendar>'+ chr(10);
			sCalContent += '</Contents>';
			
			popCalendar.Contents = sCalContent;        
		}
		else
		{
			popCalendar.calendar.Value = sColValue;
			popCalendar.calendar.UserData = sUserData;
		}
		var nSize = popCalendar.width - nCellWidth;
		//trace(nLeft + " / " + nTop + " / " + nCellWidth + " / " + nCellHeight);
		popCalendar.TrackPopup(nLeft-nSize, nTop, nCellWidth, nCellHeight);
	}
}

/*******************************************************************************
 * @description      : 달력 객체에서 선택한 값을 그리드의 데이타셋으로 전달
 * @param oCal       : 달력 객체
		 sValue      : 선택 값
*******************************************************************************/
function cf_setDateValueFromPopCal(oCal, sValue)
{
	var sGrdDsInfo = oCal.UserData;

	var arrDsInfo = sGrdDsInfo.split(",");
	var oGrdDs = object(arrDsInfo[0]);
	var nRow = parseInt(arrDsInfo[1]);
	var sColId = arrDsInfo[2];

	oGrdDs.SetColumn(nRow, sColId, sValue);
	popCalendar.ClosePopup(true);
}

/*******************************************************************************
 * @description          : 그리드에 체크 박스 및 소트 이벤트를 설정
 * @param oGrd           : 그리드 객체
		 nBodyCell       : 적용 바디셀
		 nHeadCell       : 적용 헤드셀	 
 * @event 		         : oGrd.OnHeadClick , oGrdDs.OnLoadCompleted
*******************************************************************************/
function cf_setGrdChecknSortEvent(oGrd, nCell)
{
	if(nCell == null)
	{
		oGrd.OnHeadClick = "cf_markGridSort";
	}
	else
	{
		oGrd.OnHeadClick = "cf_allChecknSort";
		var oGrdDs = object(oGrd.BindDataset);
		oGrdDs.OnLoadCompleted = "cf_setGrdCheckStatus";
		
		if(oGrdDs.GetColIndex(FV_STR_CHECK_COLUMN_ID) == -1)
		{
			oGrdDs.AddColumn(FV_STR_CHECK_COLUMN_ID);
		}
	
		if(nCell == null)
		{
			nCell = 0;
		}
		
		oGrd.SetCellProp("body", nCell, "ColID", FV_STR_CHECK_COLUMN_ID);
	}
}


/*******************************************************************************
 * @description    : 전체 선택 및 소팅- OnHeadClick 이벤트 계승
*******************************************************************************/
function cf_allChecknSort(obj, nCell, nX, nY, nPivotIndex)
{ 
	if(object(obj.bindDataset).getRowCount() > 0)
	{
		var sType = obj.GetCellProp("head", nCell, "display");
		if(sType == "checkbox")	
		{
			obj.Redraw = false;
			var oGrdDs = object(obj.bindDataSet);
			var nGrdDsRouCount = oGrdDs.RowCount();
			var sValue = '0';
			//그리드의 셀이 checkbox로 되어 있는 경우만.
			var sChkValue = obj.GetCellProp("head", nCell, "text");		
			
			if (sChkValue == 1) sValue='0';
			else sValue='1';
			
			obj.SetCellProp("head",nCell,"text",sValue);
	
			for (i=0; i<nGrdDsRouCount; i++)
			{
				oGrdDs.SetColumn(i, FV_STR_CHECK_COLUMN_ID, sValue);
			}
			obj.Redraw = true;
		}
		else
		{
			cf_markGridSort(obj, nCell, nX, nY, nPivotIndex);
		}
	}
}

/*******************************************************************************
 * @description      : 그리드에 바인딩된 데이타셋에 체크박스 컬럼을 추가 - OnLoadCompleted 이벤트
*******************************************************************************/
function cf_setGrdCheckStatus(obj,nErrorCode,strErrorMsg,nReason)
{
	if(obj.GetColIndex(FV_STR_CHECK_COLUMN_ID) == -1)
	{
		obj.AddColumn(FV_STR_CHECK_COLUMN_ID);
	}
}



/*******************************************************************************
 * @description           : 공통 메세지와 지정한 문자를 조합
 * @param sFixedMsg       : 공통 메세지
		 sMsgStr          : 지정 문자
*******************************************************************************/
function cf_getMessage(sFixedMsg, sMsgStr)
{
	// param 이 없을 경우.
	if(sMsgStr == null){	
		return sFixedMsg;
	}
	
	var arrMsg = sFixedMsg.split(FV_STR_MSG_KEY,"webstyle");
	var arrValue = sMsgStr.split(FV_STR_MSG_KEY,"webstyle");
	var nMsgLength = arrMsg.length;
	if(nMsgLength != arrValue.length+1)
	{
		alert(MSG_00986+"  "+(nMsgLength-1)+" / " +arrValue.length);
		return;
	}
	var sResult;
	for(var i=0; i<nMsgLength; i++)
	{
		sResult += arrMsg[i]+arrValue[i];
	}
	return sResult;
}

/*******************************************************************************
 * @description           : 그리드의 필수 및 최소 항목 체크
 * @param oGrd            : 그리드 객체
		 sChkCell         : 체크 대상 셀번호 및 옵션 
                          ex)   단일시 "3" , 복수시 "2,4,7" 
                          ex)   옵션 - 단일시 "3:2" , 복수시 "2:4,4,7:2" 
*******************************************************************************/
function cf_checkGrdValues(oGrd, sChkCell)
{
	var oGrdDs = object(oGrd.BindDataset);
	var nGrdDsRowCount = oGrdDs.getRowCount();
	var arrChkCell = sChkCell.split(",");
	var nCellCount = arrChkCell.length;
	var sCheckValue;
	if(nGrdDsRowCount < 1 && oGrdDs.GetDelRowCount() < 1)
	{
		alert(MSG_00980);
		return false;
	}

	for(var i=0; i<nGrdDsRowCount; i++)
	{
		for(var j=0; j<nCellCount; j++)
		{
			var sCellInfo = trim(arrChkCell[j]);
			var arrCellInfo = sCellInfo.split(":");
			nCellNo = parseInt(trim(arrCellInfo[0]));
			sCheckValue = oGrdDs.getColumn(i, oGrd.GetCellProp("body", nCellNo, "colid")).toString;
			
			if(arrCellInfo.length > 1)
			{
				var nMinValue = parseInt(trim(arrCellInfo[1]));
				if(sCheckValue.length < nMinValue)
				{
					var sHeadTitle = oGrd.GetCellProp("head", nCellNo, "text");
					oGrdDs.row = i;
					sMsg = sHeadTitle+";"+nMinValue;
					alert(cf_getMessage(MSG_00999, sMsg));
					oGrd.SetFocus();
					oGrd.SetCellPos(nCellNo);	
					return false;
				}
			}
			else
			{
				if(sCheckValue.length < 1)
				{
					var sHeadTitle = oGrd.GetCellProp("head", nCellNo, "text");
					oGrdDs.row = i;
					alert(cf_getMessage(MSG_00083, sHeadTitle));
					oGrd.SetFocus();
					oGrd.SetCellPos(nCellNo);	
					return false;
				}
			}
		}
	}
	
	return true;
}

/*******************************************************************************
 * @description           : 컴포넌트들의 필수 및 최소 항목 체크
 * @param oGrd            : 컴포넌트 객체
		  bNew            : 새롭게 체크 대상 구성 여부
 필수 체크 : userData 에 체크 대상의 명칭               ex) 아이디
 최소 체크 : userData 에 체크 대상의 명칭 , 최소수치    ex) 번호,3
*******************************************************************************/
function cf_checkObjValues(oObj, bNew)
{
	var oFullObj = oObj.Components;
	var sObjType;
	var sObjUserData;
	var sDsName = "ds_"+oObj.id+"ByTaborder";
	var oCheckDs = cf_isValid(sDsName);
	if(oCheckDs == null)
	{
		create("dataset", sDsName);
		oCheckDs = object(sDsName);
		oCheckDs.addColumn("compId");
		oCheckDs.addColumn("compType");
		oCheckDs.addColumn("compOrder", "INT");
		oCheckDs.addColumn("compUserData");
	}
	
	if(bNew != null && bNew)
	{
		oCheckDs.clearData();
	}
	
	if(oCheckDs.getRowCount() < 1)
	{
		var nRow;
		for(var i=0; i<oFullObj.GetCount; i++)
		{
			sObjType = oFullObj[i].getType();
			if((sObjType == "Dataset" ) || ( sObjType == "File" ) ||	
			   (sObjType == "FileDialog" ) || (sObjType == "PopupDiv" ))
			{
				continue;
			}
			sObjUserData = oFullObj[i].UserData;
			if(trim(sObjUserData).length > 0)
			{
				nRow = oCheckDs.addRow();
				oCheckDs.setColumn(nRow, "compId", oFullObj[i].id);
				oCheckDs.setColumn(nRow, "compType", sObjType);
				oCheckDs.setColumn(nRow, "compOrder", oFullObj[i].TabOrder);
				oCheckDs.setColumn(nRow, "compUserData", sObjUserData);
			}
		}
		oCheckDs.sort("compOrder");
		//trace(oCheckDs.saveXML());
	}

	var nRowCount = oCheckDs.getRowCount();
	var oCheckComp;
	if(nRowCount > 0)
	{
		for(var i=0; i<nRowCount; i++)
		{
			oCheckComp = oObj.object(oCheckDs.getColumn(i, "compId"));
			sObjUserData = oCheckDs.getColumn(i, "compUserData");
			if(sObjUserData.length > 0)
			{
				var arrMsg = sObjUserData.split(",");
				var sMsg;
				if(arrMsg.length > 1)
				{
					var nMinValue = parseInt(trim(arrMsg[1]));
					sMsg = trim(arrMsg[0])+";"+nMinValue;
					if(toString(oCheckComp.value).length < nMinValue)
					{
						alert(cf_getMessage(MSG_00999, sMsg));
						oCheckComp.setFocus();
						return false;
					}
				}
				else
				{
					sMsg = trim(arrMsg[0]);
					if(toString(oCheckComp.value).length < 1)
					{
						alert(cf_getMessage(MSG_00083, sMsg));
						oCheckComp.setFocus();
						return false;
					}
				}
			}
		}
	}

	return true;
}


/*******************************************************************************
 * @description           : 버튼들의 권한 체크
 * @param sMenuNum        : 메뉴 번호
*******************************************************************************/
function cf_setAuth(sMenuNum)
{
	var nRow = gds_menu.FindRow("menuNum", sMenuNum);

	var sSearchFlag = gds_menu.GetColumn(nRow, "searchAuthYn");
	var sSaveFlag = gds_menu.GetColumn(nRow, "saveAuthYn");
	var sDeleteFlag = gds_menu.GetColumn(nRow, "delAuthYn");
	var sApproveFlag = gds_menu.GetColumn(nRow, "aprvAuthYn");
	var sFinishFlag = gds_menu.GetColumn(nRow, "closeAuthYn");
	var sEtc1Flag = gds_menu.GetColumn(nRow, "etc1AuthYn");
	
	
	/*
	var sRequestFlag = gds_menu.GetColumn(nRow, "reqAuthYn");
	var sCancelFlag = gds_menu.GetColumn(nRow, "canAuthYn");	
	var sPrintFlag = gds_menu.GetColumn(nRow, "printAuthYn");
	var sInitFlag = gds_menu.GetColumn(nRow, "initAuthYn");
*/
	var oDiv;
	var oComp;
	
	// 조회
	if(sSearchFlag == "Y")
	{
		oDiv = this.find("div_search");
		if(oDiv != null)
		{
			oComp = oDiv.find("btn_search");
			if(oComp != null)
			{
				oComp.visible = true;
			}
		}		
	}

	// 신규, 저장, 승인요청
	if(sSaveFlag == "Y")
	{
		oComp = this.find("btn_save");
		if(oComp != null)
		{
			oComp.visible = true;
		}
		
		oComp = this.find("btn_new");
		if(oComp != null)
		{
			oComp.visible = true;
		}
		
		oComp = this.find("btn_approvalReq");
		if(oComp != null)
		{
			oComp.visible = true;
		}
	}
	
	// 삭제
	if(sDeleteFlag == "Y")
	{
		oComp = this.find("btn_delete");
		if(oComp != null)
		{
			oComp.visible = true;
		}
	}
	
	// 승인, 반려
	if(sApproveFlag == "Y")
	{
		oComp = this.find("btn_approve");
		if(oComp != null)
		{
			oComp.visible = true;
		}
		
		oComp = this.find("btn_reject");
		if(oComp != null)
		{
			oComp.visible = true;
		}
	}	

	// 전송
	if(sEtc1Flag == "Y")
	{
		oComp = this.find("btn_send");
		if(oComp != null)
		{
			oComp.visible = true;
		}
	}
	
	
	// 마감
	if(sFinishFlag == "Y")
	{
		oComp = this.find("btn_finish");
		if(oComp != null)
		{
			oComp.visible = true;
		}
	}
	
	
	
	/*
	// 취소
	if(sCancelFlag == "Y")
	{
		oComp = this.find("btn_cancel");
		if(oComp != null)
		{
			oComp.visible = true;
		}
	}
	
		// 요청
	if(sRequestFlag == "Y")
	{
		oComp = this.find("btn_request");
		if(oComp != null)
		{
			oComp.visible = true;
		}
	}
	
	// 출력
	if(sPrintFlag == "Y")
	{
		oComp = this.find("btn_print");
		if(oComp != null)
		{
			oComp.visible = true;
		}
	}
	
	// 초기화
	if(sInitFlag == "Y")
	{
		oComp = docTop.find("btn_reset");
		if(oComp != null)
		{
			oComp.visible = true;
		}
	}*/
}

/*******************************************************************************
 * @description           : 파라메터를 데이타셋으로 만들기
 * @param sDsId           : 생성할 데이타셋
		sArg              : 지정할 파라메터
*******************************************************************************/
function cf_setArg(sDsId, sArg)
{
	var oArgDs = cf_isValid(sDsId);
	var arrParams = sArg.split(" ", "webstyle");
	if(oArgDs == null)
	{
		create("dataset", sDsId);
		oArgDs = object(sDsId);
		
		for(var i=0, n=arrParams.length; i<n; i++)
		{
			var arrParamInfo= arrParams[i].split("=","webstyle");
			var sParamName = arrParamInfo[0];
			var sparamValue = arrParamInfo[1];
			oArgDs.addColumn(sParamName);
			if(oArgDs.getRowCount() < 1)
			{
				oArgDs.addRow();
			}
			oArgDs.setColumn(0, sParamName, unQuote(sparamValue));
		}
	}
	else
	{
		oArgDs.clearData();
		oArgDs.addRow();
		for(var i=0, n=arrParams.length; i<n; i++)
		{
			var arrParamInfo= arrParams[i].split("=","webstyle");
			var sParamName = arrParamInfo[0];
			if(oArgDs.getColIndex(sParamName) == -1)
			{
				oArgDs.addColumn(sParamName);
			}
			var sparamValue = arrParamInfo[1];
			oArgDs.setColumn(0, sParamName, unQuote(sparamValue));
		}
	}

}

/*******************************************************************************
 * @description           : 디비젼 안의 컴포넌트들의 초기화
 * @param oObj            : div 아이디
*******************************************************************************/
function cf_init(oObj)
{
	oObj.enable = false;
	var oFullObj = oObj.Components;
	var sObjType;
	var sObjUserData;
	for(var i=0; i<oFullObj.GetCount; i++)
	{
		sObjType = oFullObj[i].getType();
		
		if((sObjType == "Dataset" ) || ( sObjType == "File" ) ||	
		   (sObjType == "FileDialog" ) || (sObjType == "PopupDiv" ) ||
		   (sObjType == "Image" ) || (sObjType == "Shape" ) ||
		   (sObjType == "Static" ) || (sObjType == "Button" ))
		{
		   	continue;
		}
			
		if (oFullObj.IsComposite())
		{
			cf_init(oFullObj[i]);
		} 
		else 
		{
			if(sObjType == "Combo")
			{
				oFullObj[i].index = 0;
			}
			else
			{
				oFullObj[i].value = "";
			}
		}		
	}
	oObj.enable = true;
	oObj.setFocus();
	return true;
}



/*******************************************************************************
 * @description           : 시스템 영역에 메세지를 출력
 * @param sFixedMsg       : 공통 메세지
		 sMsgStr          : 지정 문자
*******************************************************************************/
function cf_showSysMsg(sFixedMsg, sMsgStr)
{

	if(docBottom.find("div_info") != null)
	{
		var sResultMsg;
		if(sMsgStr != null)
		{
			sResultMsg = cf_getMessage(sFixedMsg, sMsgStr);
		}
		else
		{
			sResultMsg =  sFixedMsg;
		}
		
		docBottom.div_info.img_sysMsg.text = sResultMsg;
	}
}


/*******************************************************************************
 * @description            : 월 전용 달력 설정
 * @param oTargetObj       : 선택한 년월의 값이 설정될 컴포넌트
*******************************************************************************/
function cf_callMonthCalendar(oTargetObj)
{
	var oCalendar;
	Create("PopupDiv", "popMonthCalendar", 'width="152" height="133"');
	oCalendar = popMonthCalendar;
	oCalendar.Url = "main::monthCalendar.xml";
	var nX = ClientToScreenX(oTargetObj, oTargetObj.Width);
	var nY = ClientToScreenY(oTargetObj, oTargetObj.Height);
	var rtn = oCalendar.TrackPopup(nX - oTargetObj.Width, nY);
	if(rtn != null)
	{
		oTargetObj.value = rtn;
	}
	oTargetObj.readOnly = true;
	Destroy(oCalendar.id);
}

/*******************************************************************************
 * @description     : 파일 선택
 * @param oFileDs   : 업로드 파일 DS
	      sDocId    : doc id
*******************************************************************************/
function cf_setUploadFile(oFileDs, sDocId)
{
	var oFldComp = this.Find("fld_upFile");
	if(oFldComp == null)
	{
		create("FileDialog", "fld_upFile");
		oFldComp = fld_upFile;	
	}
	oFldComp.MultiSelect = true;
	
	var oFilComp = this.Find("fil_file");
	if(oFilComp == null)
	{
		create("File", "fil_file");
		oFilComp = fil_file;
	}	
	
	if(oFileDs.getColIndex("FILE_CONTENTS") == -1)
	{
		oFileDs.AddColumn("SCREEN_ID");
		oFileDs.AddColumn("DOC_ID");
		oFileDs.AddColumn("FILE_NM");
		oFileDs.AddColumn("FILE_TYPE");
		oFileDs.AddColumn("FILE_SIZE");
		oFileDs.AddColumn("FILE_PASS");
		oFileDs.AddColumn("FILE_CONTENTS", "Blob", 16960);
	}
	
	var sScreenId = substr(pFrmUrl, pFrmUrl.indexOf("::")+2);
	var sFullPath;
	var sFileName;
	var nFileSize;
	if(oFldComp.Open())
	{
		array = oFldComp.FileNameList;

		for( i=0; i<array.length; i++ )
		{
			var nRow = oFileDs.AddRow();
			
			if(sScreenId.length < 1)
			{
				alert(MSG_00989);
				return;
			}
			oFileDs.SetColumn(nRow, "SCREEN_ID", sScreenId);
			oFileDs.SetColumn(nRow, "DOC_ID", sDocId);
			
			sFullPath = oFldComp.FilePath + "\\";
			sFileName = array[i];
			oFilComp.FileName = sFullPath+sFileName;
			oFileDs.SetColumn(nRow, "FILE_NM", substr(sFileName, 0, sFileName.length-4));
			oFileDs.SetColumn(nRow, "FILE_TYPE", substr(sFileName, sFileName.length-3));
			
			oFilComp.Open("rb");
			oFileDs.SetColumn(nRow, "FILE_CONTENTS", oFilComp.ReadBinary());
			oFilComp.Close();
			
			nFileSize = oFilComp.GetLength();
			
			if(nFileSize < 1)
			{
				alert(sFileName+"은(는) 파일크기가 0 입니다.");
			}
			
			// if(nFileSize > 10000000)
			// {
				// alert("파일 용량이 10M를 초과하여 업로드가 불가능 합니다.");
				// oFileDs.DeleteRow(nRow);
				// return;
			// }
						
			oFileDs.SetColumn(nRow, "FILE_SIZE", nFileSize);
		}
	}
}


/*******************************************************************************
 * @description        : 파일 다운로드
 * @param sScreenId    : 스크린 아이디(메뉴번호)
	      sDocId       : doc_id
	      sFileName    : 파일명
	      sFileType    : 파일 확장자      	      
*******************************************************************************/
function cf_downLoad(sScreenId, sDocId, sFileName, sFileType)
{	
	//var gvUrl = "http://sales.jungboboho.com/ps/";
	var gvUrl = "http://salesplus.jungboboho.com:9060/st/";

	//var gvUrl = "http://localhost:8088/ps/";

	//var gvUrl = "http://10.40.10.29/ps/";
	//var gvUrl = "http://150.2.236.145/ps/";
	
	//var sServer = gvUrl+"download.xmi";
	var sServer = gv_url+"download.xmi";
	var sParam  = ";jsessionid="+JSESSIONID;
		sParam += "?SCREEN_ID="+sScreenId;
		sParam += "&DOC_ID="+sDocId;
		sParam += "&FILE_NM="+uf_encodeUtf8(sFileName);
		sParam += "&FILE_TYPE="+sFileType;

	var oWebComponent = this.Find("web_download");
	if(oWebComponent == null)
	{
		create("WebBrowser", "web_download");
		oWebComponent = web_download;
	}
	//trace(sServer+sParam);
	oWebComponent.PageUrl = sServer+sParam;
	oWebComponent.Run();
}

/*******************************************************************************
 * @description     : 전체 파일 일괄 다운로드
 * @param oDs       : 파일 DS
*******************************************************************************/
function cf_allDownLoad(oDs)
{
	var oDsCount =oDs.GetRowCount();
	//var gvUrl =  "http://sales.jungboboho.com/ps/";
	//var gvUrl =  "http://salesplus.jungboboho.com/ps/";
	
	var sRemoteUrl = gv_url + "download.xmi"; 
	//var sRemoteUrl = gvUrl + "download.xmi";
	var nResult;
	var sLocalPathForSave;
	var nDownloadPacket = 1048576;
	
	if (oDsCount < 1) 
	{
		alert(cf_getMessage(MSG_00084, "다운로드할 파일"));
		return;
	}

	var oFilComp = this.Find("fil_saveFile");
	if(oFilComp == null)
	{
		create("FileDialog", "fil_saveFile");
		oFilComp = fil_saveFile;
	}
	oFilComp.Type = "Dir";
	if ( oFilComp.Open("c:\\") )
	{
		sLocalPathForSave = oFilComp.FilePath+"\\";
	}
	else
	{
		alert(MSG_00987);
	}

	var sScreenId;
	var sDocId;
	var sFileName;
	var sFileType;
	var sParam;
	
	var oHttp = this.Find("htf_httpFile");
	if(oHttp == null)
	{
		create("HttpFile", "htf_httpFile");
		oHttp = htf_httpFile;
	}

	for(var i=0; i<oDsCount; i++)
	{
		sScreenId = oDs.getColumn(i, "SCREEN_ID");
		sDocId = oDs.getColumn(i, "DOC_ID");
		sFileName = oDs.getColumn(i, "FILE_NM");
		sFileType = oDs.getColumn(i, "FILE_TYPE");
		
		sParam  = ";jsessionid="+JSESSIONID;
		sParam += "?SCREEN_ID="+sScreenId;
		sParam += "&DOC_ID="+sDocId;
		sParam += "&FILE_NM="+uf_encodeUtf8(sFileName);
		sParam += "&FILE_TYPE="+sFileType;	

		nResult = oHttp.OpenFile(sRemoteUrl+sParam, sLocalPathForSave+sFileName+"."+sFileType, "GET");

	    //trace(sRemoteUrl+sParam);

		if( nResult < 0 ) 
		{
			alert(oHttp.ErrorMsg);
			return;
		}

		while(1)
		{
			read_size = oHttp.ReadFile(nDownloadPacket);
			if( (read_size == 0) || ( read_size == -1) ) break;
		}	
					
		oHttp.CloseFile();			
	}

	
	
}

/*******************************************************************************
 * @description           : 로그인 조직에 따른 센터 데이타 설정
 * @param oComponent      : 컴포넌트
	      sInitText       : 데이타셋 옵션 텍스트
	      sInitCd         : 데이타셋 옵션 코드    	      
*******************************************************************************/
function cf_setOrgInfo(oComponent, sInitText, sInitCd)
{
	if(gds_org.GetRowCount() > 0)
	{
		var sDsName = "ds_orgInfo";
		var sColName = "ORGNM";
		var sColCode = "ORGID";
		
		create("dataset", sDsName);
		object(sDsName).copy(gds_org);
		oComponent.InnerDataset = sDsName;
		oComponent.CodeColumn = sColCode;
		oComponent.DataColumn = sColName;
		if(!uf_isEmpty(sInitText))
		{
			var sOptCd = "";
			ds_orgInfo.InsertRow(0);
			if(sInitCd != null)
			{
				sOptCd = sInitCd;
			}
			ds_orgInfo.SetColumn(0, sColCode, sOptCd);
			ds_orgInfo.SetColumn(0, sColName, sInitText);
		}
		
		oComponent.index = 0;
		
		if(gds_session.GetColumn(0, "orgCl") == "3")
		{
			oComponent.enable = false;
		}
	}
}

/*******************************************************************************
 * @description           :  컴포넌트의 존재 여부에 따른 객체 반환
 * @param sComponentId    : 컴포넌트 아이디
 * @return                :	존재시 - 검포넌트 객체	, 없을시 - null          	      
*******************************************************************************/
function cf_isValid(sComponentId, oTarget)
{
	if(oTarget == null)
	{
		oTarget = this;
	}

	var oComponent = oTarget.Components;

	for(var i=0, n=oComponent.count; i<n; i++)
	{	
		if(oComponent[i].id == sComponentId)
		{
			return oComponent[i];
		}
	}
	
	return null;
}

/*******************************************************************************
 * @description           : 3버튼 컨펌 창을 오픈
 * @param sContent   	  : 내용 
		  sTitle          : 창 타이틀
		  sStyle          : 창 스타일
 * @return                :	예 - 6	, 아니오 -7 , 취소 - 2          	      
*******************************************************************************/
function cf_confirmMsg(sContent, sTitle, sStyle)
{
	if(sTitle == null)
	{
		sTitle = this.title;
	}
	
	if(sContent == null)
	{
		sContent = "";
	}
	
	if(sStyle == null)
	{
		sStyle = "MB_ICONQUESTION|MB_YESNOCANCEL|MB_DEFBUTTON1";
	}

	return ext_MsgBox(sTitle, sContent, sStyle);
}







/*******************************************************************************
 * @description           : child 창의 메소드 실행
 * @param sFormId   	  : child 창의 폼 ID
  	      sFuncName   	  : 실행할 함수명
  	      sParam   	      : 함수의 파라메터
*******************************************************************************/
function cf_callChildFunc(sFormId, sFuncName, sParam)
{
	var oWinObj, sTabId;
	var nTabCount = Global.windows.count;
	for ( var i = nTabCount-1; i >= 0; i -- )
	{
		oWinObj = global.Windows[i];
		if(oWinObj.ID == sFormId)
		{
			if(oWinObj.IsExistFunc(sFuncName))
			{
				if(sParam == null)
				{
					sParam = "";
				}
				oWinObj.CallScript(sFuncName + "("+sParam+")");
			}
			else
			{
				alert(cf_getMessage(MSG_00084, "호출하는 함수 "+sFuncName));
			}
			return;
		}
		else
		{
			alert(cf_getMessage(MSG_00084, sFormId+" 창"));
			return;
		}
	}
}


/*******************************************************************************
 * @description    		 	  : 판매유형 트리 오픈
 * @param   sComponentId   	  : 코드가 설정될 컴포넌트 아이디
			sComponentNm   	  : 이름이 설정될 컴포넌트 아이디
			sMode   	  	  : 트리의 선택 모드 
*******************************************************************************/
function cf_callSaleTree(sComponentId, sComponentNm, sMode)
{
	if(sMode == null)
	{
		sMode = "";
	}
	var sArg  = "mode="+quote(sMode);
	var sResult = dialog("BAS::BASBCO01200.xml", sArg, 255, 370, "closeFlag=false");
	if(sResult == "1" && ds_selectedSale.getRowCount() > 0)
	{
		object(sComponentId).text = ds_selectedSale.getColumn(0, "COMM_CD_VAL");
		object(sComponentNm).text = ds_selectedSale.getColumn(0, "COMM_CD_VAL_NM");
	}
		
	return sResult;
}


/*******************************************************************************
 * @description    		 	  : 센터 조직 트리 오픈
 * @param   sComponentId   	  : 조직 코드가 설정될 컴포넌트 아이디
			sComponentNm   	  : 조직 이름이 설정될 컴포넌트 아이디
			sMode   	  	  : 트리의 선택 모드 - centerOnly : 센터만 선택 가능 
*******************************************************************************/
function cf_callOrgTree(sComponentId, sComponentNm, sMode)
{
	if(sMode == null)
	{
		sMode = "";
	}
	var sArg  = "mode="+quote(sMode);
	var sResult = dialog("BAS::BASBCO01100.xml", sArg, 255, 300, "closeFlag=false");
	if(sResult == "1" && ds_selectedOrg.getRowCount() > 0)
	{
		object(sComponentId).text = ds_selectedOrg.getColumn(0, "org_id");
		object(sComponentNm).text = ds_selectedOrg.getColumn(0, "org_nm");
	}
	
	return sResult;
}

/*******************************************************************************
 * @description    		 	  : 센터 조직 트리 오픈
 * @param   sComponentId   	  : 조직 코드가 설정될 컴포넌트 아이디
			sComponentNm   	  : 조직 이름이 설정될 컴포넌트 아이디
			sMode   	  	  : 트리의 선택 모드 - centerOnly : 센터만 선택 가능 
*******************************************************************************/
function cf_callOrgTree2(sComponentId, sComponentNm, sMode, sOrgClComponent)
{
	if(sMode == null)
	{
		sMode = "";
	}
	
	if(sOrgClComponent == null)
	{
		sOrgClComponent = "";
	}
		
	var sArg  = "mode="+quote(sMode);
	var sResult = dialog("BAS::BASBCO01110.xml", sArg, 255, 300, "closeFlag=false");
	if(sResult == "1" && ds_selectedOrg.getRowCount() > 0)
	{
		object(sComponentId).text = ds_selectedOrg.getColumn(0, "org_id");
		object(sComponentNm).text = ds_selectedOrg.getColumn(0, "org_nm");
		
		// 조직구분 셋팅.
		if(sOrgClComponent != ""){
			object(sOrgClComponent).text = ds_selectedOrg.getColumn(0, "org_cl");
		}
	}
	
	return sResult;
}

/*******************************************************************************
 * @description    		 	  : 센터 조직 트리 오픈
 * @param   sComponentId   	  : 조직 코드가 설정될 컴포넌트 아이디
			sComponentNm   	  : 조직 이름이 설정될 컴포넌트 아이디
			sMode   	  	  : 트리의 선택 모드 - centerOnly : 센터만 선택 가능 
 by 심냥 
*******************************************************************************/
function cf_callOrgTree3(sComponentId, sComponentNm, sComponentCl, sMode)
{
	if(sMode == null)
	{
		sMode = "";
	}
	var sArg  = "mode="+quote(sMode);
	var sResult = dialog("BAS::BASBCO01110.xml", sArg, 255, 300, "closeFlag=false");
	if(sResult == "1" && ds_selectedOrg.getRowCount() > 0)
	{
		object(sComponentId).text = ds_selectedOrg.getColumn(0, "org_id");
		object(sComponentNm).text = ds_selectedOrg.getColumn(0, "org_nm");
		object(sComponentCl).text = ds_selectedOrg.getColumn(0, "org_cl");
		
	}
	
	return sResult;
}


/*******************************************************************************
 * @description    		 	  : 대리점 포함 조직 트리 오픈
 * @param   sComponentId   	  : 조직 코드가 설정될 컴포넌트 아이디
			sComponentNm   	  : 조직 이름이 설정될 컴포넌트 아이디
*******************************************************************************/
function cf_callFullOrgTree(sComponentId, sComponentNm, sComponentLvl)
{
	var sResult = dialog("BAS::BASBCO01500.xml", "", 255, 300, "closeFlag=false");
	if(sResult.length > 0 && sResult != 0)
	{
		var arrResult = sResult.split("^");
		var sId = arrResult[0];
		var sNm = arrResult[1];
		var sLvl = arrResult[2];
		object(sComponentId).text = sId;
		object(sComponentNm).text = sNm;
		object(sComponentLvl).text = sLvl;
	}
	
	return sResult;
}

/*******************************************************************************
 * @description    		 	  : 전체 조직 트리 오픈 (로그인 사용자의 조직과 관겨없이 전체.)
 * @param   sComponentId   	  : 조직 코드가 설정될 컴포넌트 아이디
			sComponentNm   	  : 조직 이름이 설정될 컴포넌트 아이디
*******************************************************************************/
function cf_callFullOrgTreeAll(sComponentId, sComponentNm, sComponentLvl)
{
	var sResult = dialog("BAS::BASBCO01500.xml", "totalYn='Y'", 255, 300, "closeFlag=false");
	if(sResult.length > 0 && sResult != 0)
	{
		var arrResult = sResult.split("^");
		var sId = arrResult[0];
		var sNm = arrResult[1];
		var sLvl = arrResult[2];
		object(sComponentId).text = sId;
		object(sComponentNm).text = sNm;
		object(sComponentLvl).text = sLvl;
	}
	
	return sResult;
}

/*******************************************************************************
 * @description    		 	  : 거래처 팝업 검색
 * @param   sArg      		  : 파라메터 정보
								 - deal_co_cl1 		: 필수 (거래처 구분) - 복수시 A1,A2,A3 형태의 콤바구분자로 설정
								 - eff_dtm 			: 옵션 (기준일) - yyyyMMdd 형태의 날짜
								 - deal_co_cd 		: 옵션 (거래처 코드)
								 - org_id 			: 옵션 (조직 코드)
								 - pos_agency 		: 옵션 (소속 대리점 코드)
								 - chrgr_user_id    : 옵션 (영업 사원)
								 - deal_co_cl2 		: 옵션 (거래처 유형)
								 - deal_status 		: 옵션 (거래처 상태) - 1: 수납정지 N  , 2: 출고정지 N , 3: 판매정지 N 

			oComponent     	  : 설정될 컴포넌트 - dataset or division
			sUserColumns      : 결과가 세팅될 데이타셋 칼럼 아이다 or 컴포넌트 아이디
			                    - 복수개의 경우 콤마 구분자로 설정 ex) edt_dealNm,edt_dealCd,edt_uKeyAgencyCd
			nRow     		  : 결과가 설정될 로우 번호	 - 그리드의 경우	
			sTotalYn          : 종료된 거래처 조회 여부 
*******************************************************************************/
function cf_callDealPop(sArg, oComponent, sUserColumns, nRow, sTotalYn)
{
	var sSvcID = "svcCommonPop2";
	if(sArg.indexOf("deal_co_cl1=") == -1)
	{
		alert(MSG_00979);
		return;
	}
	cf_setCommonPopInfo(sArg, oComponent, sUserColumns, nRow, sSvcID, sTotalYn);
}

/*******************************************************************************
 * @description    		 	  : 대리점 검색 팝업
 * @param   sArg      		  : 파라메터 정보
								 - org_id 		: 필수 (조직 코드)
								 - eff_dtm 			: 옵션 (기준일) - yyyyMMdd 형태의 날짜
								 
			oComponent     	  : 설정될 컴포넌트 - dataset or division
			sUserColumns      : 결과가 세팅될 데이타셋 칼럼 아이다 or 컴포넌트 아이디
			                    - 복수개의 경우 콤마 구분자로 설정 ex) edt_dealNm,edt_dealCd,edt_uKeyAgencyCd			
			nRow     		  : 결과가 설정될 로우 번호	 - 그리드의 경우	 	
			sSupOrg           : 상위조직 (옵션)
*******************************************************************************/
function cf_callAgencyPop(sArg, oComponent, sUserColumns, nRow, sSupOrg)
{
	var sSvcID = "svcCommonPop1";
	if(sArg.indexOf("org_id=") == -1)
	{
		alert(MSG_00978);
		return;
	}
	cf_setCommonPopInfo(sArg, oComponent, sUserColumns, nRow, sSvcID, null, sSupOrg);
}

/*******************************************************************************
 * @description    		 	  : 팝업 공통 정보 설정
 * @param   sArg      		  : 파라메터 정보
			oComponent     	  : 설정될 컴포넌트 - dataset or division
			sUserColumns      : 결과가 세팅될 데이타셋 칼럼 아이다 or 컴포넌트 아이디
			nRow     		  : 결과가 설정될 로우 번호
			sSvcID     		  : 팝업 트랜잭션 아이디	
			sTotalYn          : 종료된 거래처 모두 조회 여부.
*******************************************************************************/
function cf_setCommonPopInfo(sArg, oComponent, sUserColumns, nRow, sSvcID, sTotalYn, sSupOrg)
{
	var sResult;
	var sTrId;
	FV_STR_POP_RESULT = "";
	if(nRow == null)
	{
		nRow = 0;
	}
	
	if(sTotalYn == null)
	{
		sTotalYn = "N";
	}
		
	cf_setArg("ds_popCondition", sArg);	
	if(sUserColumns != null)
	{
		var sCompType = oComponent.getType();
		var arrUserColumn = sUserColumns.split(",","webstyle");
		var nColumnCount = arrUserColumn.length;
		var nMaxColumn = 3;
		sTrId = "sktst.bas.BCO#getAgencyPop";
		if(sSvcID == "svcCommonPop2")
		{
			nMaxColumn = 5;
			sTrId = "sktst.bas.BCO#getDealPop";
			ds_popCondition.setColumn(0, "deal_co_cl1", uf_setSingleQuote(ds_popCondition.getColumn(0, "deal_co_cl1")));
	
			// 거래구분2 셋팅.			
			if(ds_popCondition.GetColIndex("deal_co_cl2") != -1){
				ds_popCondition.setColumn(0, "deal_co_cl2", uf_setSingleQuote(ds_popCondition.getColumn(0, "deal_co_cl2")));
			}

		}
		if(nColumnCount > nMaxColumn)
		{
			alert("리턴 인자는 최대 "+nMaxColumn+"개 까지 가능합니다.");
			return;
		}
		
		if(ds_popCondition.getColIndex("sUserColumns") == -1)
		{
			ds_popCondition.addColumn("sUserColumns");
			ds_popCondition.addColumn("componentType");
			ds_popCondition.addColumn("componentId");
			ds_popCondition.addColumn("search_condition");
			ds_popCondition.addColumn("dsRow");
		}
		ds_popCondition.setColumn(0, "sUserColumns", sUserColumns);
		ds_popCondition.setColumn(0, "componentId", oComponent.id);
		ds_popCondition.setColumn(0, "componentType", sCompType);
		ds_popCondition.setColumn(0, "dsRow", nRow);
		
		if(sCompType == "Dataset")
		{
			var sColIndex;
			for(var i=0; i<nColumnCount; i++)
			{
				if(trim(arrUserColumn[i]).length > 0)
				{
					sColIndex = oComponent.getColIndex(trim(arrUserColumn[i]));
				
					if(sColIndex == -1)
					{
						alert(arrUserColumn[i] +"는 존재하지 않는 칼럼 입니다.");
						return;
					}
				}
				
				if(i == 0)
				{
					ds_popCondition.setColumn(0, "search_condition", oComponent.getColumn(nRow, arrUserColumn[i]));
				}
			}			
		}
		else if(sCompType == "Div")
		{
			var oFullComp;
			for(var i=0; i<nColumnCount; i++)
			{
				if(trim(arrUserColumn[i]).length > 0)
				{
					oFullComp = oComponent.object(trim(arrUserColumn[i]));
					if(!isValid(oFullComp))
					{
						alert(arrUserColumn[i] +"는 존재하지 않는 컴포넌트 입니다.");
						return;
					}
				}
				
				if(i == 0)
				{
					ds_popCondition.setColumn(0, "search_condition", oFullComp.text);
				}
	
			}
		}
		else
		{
			alert(MSG_00977);
			return;
		}
	}
	else
	{
		alert(MSG_00976);
		return;
	}

	
	// 조회 조건 설정
	var sEffDtm = ds_popCondition.getColumn(0, "eff_dtm");
	if(sEffDtm != null)
	{
		var effStrDtm = "";
		var effEndDtm = "";
		
		if(sEffDtm.length == 8)
		{
			effStrDtm = sEffDtm + "000000";
			effEndDtm = sEffDtm + "235959";
		}else if(sEffDtm.length == 10)
		{
			effStrDtm = sEffDtm + "0000";
			effEndDtm = sEffDtm + "2359";
		}
		else if(sEffDtm.length == 12)
		{
			effStrDtm = sEffDtm + "00";
			effEndDtm = sEffDtm + "59";
			
		}else if(sEffDtm.length == 14)
		{
			effStrDtm = sEffDtm;
			effEndDtm = sEffDtm;
		}
		
		//ds_popCondition.setColumn(0, "eff_dtm", left(sEffDtm, 8)+"235959");
		ds_popCondition.AddColumn("eff_str_dtm");
		ds_popCondition.AddColumn("eff_end_dtm");
		ds_popCondition.setColumn(0, "eff_str_dtm", effStrDtm);
		ds_popCondition.setColumn(0, "eff_end_dtm", effEndDtm);
	
	}	
	
	// 종료된 거래처를 조회 할수 있는 옵션
	ds_popCondition.AddColumn("total_yn");
	ds_popCondition.setColumn(0, "total_yn", sTotalYn);
	
	// 특정 상위조직에 해당하는 대리점만 조회할 수 있는 옵션
	ds_popCondition.AddColumn("sup_org");
	ds_popCondition.setColumn(0, "sup_org", sSupOrg);	
	
	gds_temp.Clear();
	// 트랜잭션 설정
	var sInDs = "nc_input_fieldDs=ds_popCondition";
	var sOutDs = "gds_temp=output";

	http.Sync = true;

	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sTrId);
}


/*******************************************************************************
 * @description    		 	  : 공통 팝업 조회 콜백
*******************************************************************************/
function cf_popCallBack(sSvcID, sErrCode, sErrMsg) 
{
	var sResult;
	var nResultCount = gds_temp.GetRowCount();

	if(nResultCount == 1)
	{
		var sDealCoNm = gds_temp.GetColumn(0, "deal_co_nm");
		var sDealCoCd = gds_temp.GetColumn(0, "deal_co_cd");
		var sUkeyAgencyCd = gds_temp.GetColumn(0, "ukey_agency_cd");
		var sUkeySubCd = gds_temp.GetColumn(0, "ukey_sub_cd");
		var sUkeyChannelCd = gds_temp.GetColumn(0, "ukey_channel_cd");
		var sDealCoCl1 = gds_temp.GetColumn(0, "deal_co_cl1");

		sResult = sDealCoNm+"^"+sDealCoCd+"^"+sUkeyAgencyCd+"^"+sUkeySubCd+"^"+sUkeyChannelCd+"^"+sDealCoCl1;
		
		cf_setPopResult(sResult);
	}
	else if(nResultCount > 1)
	{
		if(sSvcID == "svcCommonPop1")
		{
			sResult = dialog("BAS::BASBCO01300.xml", "", 490, 338);
		}
		else
		{
			sResult = dialog("BAS::BASBCO01400.xml", "", 700, 338);
		}

		if(sResult != 0 && sResult != null)
		{
			cf_setPopResult(sResult);
		}
	}
	else
	{	
		alert(cf_getMessage(MSG_00067, ""));
		cf_setPopResult("^^^^");
	}
	
	http.Sync = false;
}

/*******************************************************************************
 * @description    		 	  : 팝업 결과 설정
 * @param   sResult      	  : 결과 값

 * @param etc   : 결과 값은 값이 존재하는 경우
				 거래처 코드+"^"+거래처 명칭+"^"+UKey 대리점 코드+"^"+UKey 서브 코드+"^"+UKey 채널 코드
				 의 형태며, 그 외에는 0 또는 null의 형태(팝업을 그냥 닫을 경우)
				 필요한 경우만 사용
*******************************************************************************/
function cf_setPopResult(sResult)
{
	var arrResult = sResult.split("^", "webstyle");
	
	var sParams = ds_popCondition.getColumn(0, "sUserColumns");
	if(sParams != null)
	{
		var arrParam = sParams.split(",", "webstyle");

		if(ds_popCondition.getColumn(0, "componentType") == "Dataset")
		{
			var nRow = ds_popCondition.getColumn(0, "dsRow");
			for(var i=0, n=arrParam.length; i<n; i++)
			{
				object(ds_popCondition.getColumn(0, "componentId")).setColumn(nRow, trim(arrParam[i]), arrResult[i]);
			}
		}
		else
		{
			for(var i=0, n=arrParam.length; i<n; i++)
			{
				object(ds_popCondition.getColumn(0, "componentId")+"."+trim(arrParam[i])).text = arrResult[i];
			}
		}
		
		FV_STR_POP_RESULT = sResult;
	}
}

/*******************************************************************************
 * @description           : 그리드가 현재 데이타에서 마지막 로우에 도달시 다음 데이타 조회
 * @event 		    	  : oGrd.OnScrollLast   	      
*******************************************************************************/
function cf_setPagingEvent(obj)
{
	df_getNextData();
	return;
}

/*******************************************************************************
 * @description           : 페이징 결과 설정
 * @param oGrd   	      : 조건 설정 그리드 객체   	      
*******************************************************************************/
function cf_setPageResult(oGrd)
{	
	var oDs = object(oGrd.bindDataset);
	oGrd.bindDataset = "";
	var nRowCount = gds_temp.GetRowCount();
	var nTotalCount = gds_temp.GetConstColumn("nc_totalRecordCount");
	var nPageNo = gds_temp.GetConstColumn("nc_pageNo");
	if(nPageNo != 1 && div_pagingBar.FV_INT_TOTAL_COUNT != 0 && nTotalCount != div_pagingBar.FV_INT_TOTAL_COUNT)
	{
		alert(MSG_00975);
		div_pagingBar.FV_INT_TOTAL_COUNT = 0;
		div_pagingBar.FV_INT_NOW_PAGE = 0;
		div_pagingBar.btn_next.enable = false;
		div_pagingBar.edt_pageInfo.text = "";
		oDs.clear();
		return;
	}
	
	if(oDs.GetRowCount() < 1 || nPageNo  == 1)
	{
		if(oDs.GetColIndex(FV_STR_CHECK_COLUMN_ID) >= 0)
		{
			gds_temp.AddColumn(FV_STR_CHECK_COLUMN_ID);
		}
		oDs.clear();
		oDs.Copy(gds_temp);
		div_pagingBar.FV_INT_TOTAL_COUNT = nTotalCount;
		oGrd.OnScrollLast = "cf_setPagingEvent";
	}
	else
	{
		cf_copyDataset(oDs, gds_temp, null, false, true);
	}

	if(nTotalCount > 0 && nRowCount > 0)
	{
		div_pagingBar.btn_next.enable = true;
		div_pagingBar.FV_INT_NOW_PAGE = nPageNo;
		
		div_pagingBar.edt_pageInfo.text = (nPageNo-1) * FV_INT_PAGE_COUNT + gds_temp.rowcount +" / " +nTotalCount;
	}
	else
	{
		div_pagingBar.FV_INT_TOTAL_COUNT = 0;
		div_pagingBar.FV_INT_NOW_PAGE = 0;
		div_pagingBar.btn_next.enable = false;
		div_pagingBar.edt_pageInfo.text = "";
		oDs.clear();
	}

	oGrd.bindDataset = oDs.id;
}


/*******************************************************************************
 * @description           : 페이징용 파라메터 설정
 * @param oDs   	      : 조건 설정 DS
		  sPagingType     : F - 첫 페이지 조회, N - 다음 페이지 조회
 * @return                :	true - 조건설정 ok , false - 조건 설정 x       	      
*******************************************************************************/
function cf_setPagingParam(oDs, sPagingType)
{
	if(FV_INT_PAGE_COUNT > 0)
	{
		sPagingType = toUpper(sPagingType);
		var nPageNo;
		if(sPagingType == "F")
		{
			nPageNo = 1;
		}
		else if(sPagingType == "N")
		{
			nPageNo = div_pagingBar.FV_INT_NOW_PAGE;
			if(nPageNo*div_pagingBar.FV_INT_PAGE_COUNT >= div_pagingBar.FV_INT_TOTAL_COUNT)
			{
				alert(MSG_00988);
				return false;
			}

			nPageNo++;	
		}
		else
		{
			alert(MSG_00974);
			return false;
		}

		cf_setParam(ds_condition, "nc_pageNo", nPageNo);
		cf_setParam(ds_condition, "nc_recordCountPerPage", FV_INT_PAGE_COUNT);
		
		return true;
	}
	else
	{
		alert(cf_getMessage(MSG_00992, FV_INT_PAGE_COUNT));
		return false;
	}
}

/*******************************************************************************
 * @description            : 서비스의 신규 / 기존 여부 판단
 * @param sArg             : 지정할 파라메터
*******************************************************************************/
function cf_isNewService(sArg)
{
	var bIsNew = false;
	if(sArg.indexOf("pIsNew=1") >= 0)
	{
		pIsNew = 1;
		bIsNew = true;
	}
	else
	{
		pIsNew = 0;
	}
	
	return bIsNew;
}

/*******************************************************************************
 * @description            : MDI 에 설정된 파라메터를 재설정
 * @param sArg             : 지정할 파라메터
*******************************************************************************/
function cf_setArg2Param(sArg)
{
	var arrParams = sArg.split(" ", "webstyle");

	for(var i=0, n=arrParams.length; i<n; i++)
	{
		var arrParamInfo= arrParams[i].split("=","webstyle");
		var sParamName = arrParamInfo[0];
		var sparamValue = arrParamInfo[1];
		
		if(!this.IsExistVariable(sParamName, "Local"))
		{
			this.AddVariable(sParamName, unQuote(sparamValue));
		}
		else
		{
			this.setVariable(sParamName, unQuote(sparamValue));
		}		
	}
}


/*******************************************************************************
 * @description    			: 지정한 영역의 콤보 박스 인덱스를 재설정
 * @param sArg              : 지정할 파라메터 
*******************************************************************************/
function cf_setComboIndex(oComponent)
{
	var oFullObj = oComponent.Components;
	var sObjType;
	
	for(var i=0, n=oFullObj.GetCount; i<n; i++)
	{	
		sObjType = oFullObj[i].getType();
	
		if(sObjType == "Combo")
		{
			if(oFullObj[i].index == -1)
			{
				oFullObj[i].index = 0;
			}
		}
		
		if (oFullObj[i].IsComposite())
		{
			cf_setComboIndex(oFullObj[i]);
		} 
	}
}

/*******************************************************************************
 * @description    			: 파라메터로 설정하는 데이타셋을 원본으로 지정
 * @param oDs               : 지정할 데이타셋
 * @event 		            : FORM.OnUnloadCompleted  - 하나의 데이타셋만을 기준으로 변경 여부를
                                                        설정할 경우 자동으로 이벤트에 설정
                                                        2개 이상인 경우는 개별 폼에 이벤트 설정
*******************************************************************************/
function cf_setOrgDs(oDs)
{	
	var sDsId = oDs.id+"_org";
	var oOrgDs = cf_isValid(sDsId);
	if(oOrgDs == null)
	{
		create("Dataset", sDsId);
		object(sDsId).copy(oDs);
	}
	else
	{
		object(sDsId).copy(oDs);
	}

	var sUnOnEvent = this.OnUnloadCompleted;
	var sFormUserData = this.userData;
	if(trim(sUnOnEvent).length < 1)
	{
		this.userData = oDs.id;
		this.OnUnloadCompleted = "cf_setCommonOnUnloadEvent";
	}

	if(sFormUserData != oDs.id && sUnOnEvent == "cf_setCommonOnUnloadEvent")
	{
		this.userData = "";
		this.OnUnloadCompleted = "";
	}

}

/*******************************************************************************
 * @description         : 현재 폼에 UnOnloadCompleted 이벤트를 설정 - 이벤트
*******************************************************************************/
function cf_setCommonOnUnloadEvent(obj)
{
	var oDs;
	if(trim(obj.userdata).length > 0)
	{
		oDs = object(obj.userdata);
	}
	
	var nResult = cf_checkDsChanging(oDs);

	var sMsg;
	if(nResult != 0)
	{
		if(nResult == 1)
		{
			sMsg = MSG_00973;
		}
		else if(nResult == 2 || nResult == 3 || nResult == 4)
		{
			sMsg = MSG_00972;
		}
		
		if(confirm(sMsg) == true)
		{
			return true;
		}
		
		return false;
	}
	else
	{
		return true;
	}	
}

/*******************************************************************************
 * @description         : DS의 데이타 변경 여부를 체크한다
 * @param oDs           : 체크 대상 DS
 * @return	            : 1 - 삭제 데이타 존재 , 2 - 원본과 로우가 틀림, 
                          3 - 원본과 칼럼이 틀림, 4 - 원본과 데이타가 틀림
*******************************************************************************/
function cf_checkDsChanging(oDs)
{
	var nResult = 0;
	var nDeletedRowCount = oDs.GetDelRowCount();
	if(nDeletedRowCount > 0)
	{
		nResult = 1;
		return nResult;
	}

	var oOrgDs = cf_isValid(oDs.id+"_org");
	if(oOrgDs == null)
	{
		return nResult;
	}
		
	var nRowCount = oDs.GetRowCount();
	var nOrgRowCount = oOrgDs.GetRowCount();
	
	var nColCount = oDs.GetColCount();
	var nOrgColCount = oOrgDs.GetColCount();
	
	/*
	if(nRowCount != nOrgRowCount)
	{
		nResult = 2;
		//return 2;
	}
	*/
	
	if(nColCount != nOrgColCount)
	{
		nResult = 3;
		return nResult;
	}

	var sColumnId;
	if(nRowCount > 0)
	{
		for(var i=0; i<nRowCount; i++)
		{
			if(oDs.getRowType(i) != "normal")
			{
				for(var j=0; j<nColCount; j++)
				{
					//trace(oDs.GetColID(j)+" :: "+uf_null2space(oDs.GetColumn(i, j)) + " / " + uf_null2space(oOrgDs.GetColumn(i,j)));//GetOrgColumn
					if(uf_null2space(oDs.GetColumn(i,j)) != uf_null2space(oOrgDs.GetColumn(i,j)))
					{
						if(oDs.GetColID(j) == FV_STR_CHECK_COLUMN_ID)
						{
							if(oDs.GetColumn(i,j) == "0" && oOrgDs.GetColumn(i,j) == null)
							{
								continue;
							}
						}
						
						nResult = 4;
						return nResult;
					}
				}
			}
		}
	}
	
	return nResult;
}

/*******************************************************************************
 * @description         : 지정하는 갯수만큼의 DS의 데이타 변경 여부를 체크한다 - 최대 5개
 * @param nResult1      : 첫번째 데이타셋 체크 결과값
		  nResult2      : 두번째 데이타셋 체크 결과값
		  nResult3      : 세번째 데이타셋 체크 결과값
		  nResult4      : 네번째 데이타셋 체크 결과값
		  nResult5      : 다섯번째 데이타셋 체크 결과값
 * @return	            : true - 화면 닫음 , false - 닫기 취소
*******************************************************************************/
function cf_callDsCheckMsg(nResult1, nResult2, nResult3, nResult4, nResult5)
{
	var nTotalResult = 0;
	if(nResult1 != null)
	{
		nTotalResult += nResult1;
	}
	if(nResult2 != null)
	{
		nTotalResult += nResult2;
	}
	if(nResult3 != null)
	{
		nTotalResult += nResult3;
	}
	if(nResult4 != null)
	{
		nTotalResult += nResult4;
	}
	if(nResult5 != null)
	{
		nTotalResult += nResult5;
	}
	
	if(nTotalResult != 0)
	{		
		if(confirm(MSG_00972) == true)
		{
			return true;
		}
		
		return false;
	}
	else
	{
		return true;
	}
}

/*******************************************************************************
 * @description     	   : 권한이 적용되는 팝업 오픈
 * @param sMenuNum         : 메뉴 번호
	       sUserArg        : 사용자 정의 추가 파라메터
	       nWidth          : 팝업 가로 사이즈 - 미지정시 자동 설정
	       nHeight         : 팝업 세로 사이즈 - 미지정시 자동 설정
*******************************************************************************/
function cf_openAuthPop(sMenuNum, sUserArg, nWidth, nHeight)
{

	var nRow = gds_menu.FindRow("menuNum", sMenuNum);
	if(nRow >= 0)
	{
		var sTitle= gds_menu.GetColumn(nRow, "menuShotNm");
		var sGroup = gds_menu.GetColumn(nRow, "topPrefixCd");
		var sForm = gds_menu.GetColumn(nRow, "screenId");

		var sUrl = sGroup+"::"+sForm;

		if(sUrl.length < 3) return;

		var sArg  = "pFrmTitle=" + quote(sTitle);
			sArg += " pFrmNum=" + quote(sMenuNum);
			sArg += " pFrmUrl=" + quote(sUrl);

			if(sUserArg != null)
			{
				sArg += " "+sUserArg;
			}
		
		if(nWidth == null || nHeight == null)
		{
			nWidth = -1;
			nHeight = -1;
		}
		if(sUrl.indexOf("xml") == -1)
		{
			sUrl += ".xml";
		}
		dialog(sUrl, sArg , nWidth, nHeight, "CloseFlag=false");
	}
	else
	{
		alert(MSG_00971); // 해당 화면에 권한이 없습니다!
		return;
	}	
}

/*******************************************************************************
 * @description     	   		   : 로컬 영역에 파일 작성
 * @param sFileBody                : 파일의 내용
	       sFilePath        	   : 저장될 파일 경로
	       bFileSelection          : 다이얼로그로 작성될 파일 선택 여부 - true : 사용 , false : 미사용(default)
	       sMode         		   : 파일 작성 모드 - r:read, w:write, a:append, b:binary, t:text 
									tw(default)
*******************************************************************************/
function cf_writeFile(sFileBody, sFilePath, bFileSelection, sMode)
{
	var oFile = cf_isValid("fil_file");
	if(oFile == null)
	{
		create("File", "fil_file");
		oFile = fil_file;
	}
	
	if(bFileSelection != null && bFileSelection == true)
	{
	
		var oFldComp = this.Find("fld_selectFile");
		if(oFldComp == null)
		{
			create("FileDialog", "fld_selectFile");
			oFldComp = fld_selectFile;	
		}
		
		fld_selectFile.Type = "OPEN";
		if(fld_selectFile.Open("C:\\"))
		{
			var sFilePath = fld_selectFile.FilePath;
			var sFileName = fld_selectFile.FileName;

			var sSavedName = sFilePath + "\\" + sFileName;
			oFile.filename = sSavedName;
		}
	}
	else
	{
		oFile.filename = sFilePath;
	}
	
	
	if(sMode == null)
	{
		sMode = "tw";
	}
	
	if( oFile.open(sMode) ) 
	{
		var len = oFile.write(sFileBody);
		oFile.close();
	}
	
}


 /*******************************************************************************
 * @description    : 데이타셋 비교
 * @param oSrcDs   : 원본 데이타셋
		  oTarDs   : 비교 데이타셋
		  nRow     : 비교 로우 - 미지정시 전체 비교
*******************************************************************************/
function cf_compareDs(oSrcDs, oTarDs, nRow)
{
	var nResult = 0;
	var nSrcDsRowCount = oSrcDs.getRowCount();
	var nTarDsRowCount = oTarDs.getRowCount();
	
	var nSrcDsColCount = oSrcDs.getColCount();
	var nTarDsColCount = oTarDs.getColCount();

	if(nSrcDsRowCount != nTarDsRowCount)
	{
		nResult = -1;
	}
	
	if(nSrcDsColCount != nTarDsColCount)
	{
		nResult = -2;
	}

	if(nSrcDsRowCount > 0)
	{
		if(nRow != null)
		{
			for(var j=0; j<nSrcDsColCount; j++)
			{
				//trace(uf_null2space(oSrcDs.GetColumn(nRow, j))+" / " + uf_null2space(oTarDs.GetColumn(nRow, j)));
				if(uf_null2space(oSrcDs.GetColumn(nRow, j)) != uf_null2space(oTarDs.GetColumn(nRow, j)))
				{	
					nResult = nRow + "/"+ j;
					return nResult;
				}
			}
			
			return nResult;
		}
		
		for(var i=0; i<nSrcDsRowCount; i++)
		{
			for(var j=0; j<nSrcDsColCount; j++)
			{				
				if(uf_null2space(oSrcDs.GetColumn(i,j)) != uf_null2space(oTarDs.GetColumn(i,j)))
				{
					nResult = i + "/"+ j;
					return nResult;
				}
			}
		}
				
		return nResult;		
	}
}

 /*******************************************************************************
 * @description    : 서버와 동기화된 시간을 셋팅한다.
*******************************************************************************/
function cf_setDate(objValue)
{
	var year      = substr(objValue,0,4);
	var month     = substr(objValue,4,2);
	var day       = substr(objValue,6,2);
	var hour      = substr(objValue,8,2);
	var minuts    = substr(objValue,10,2);
	var second    = substr(objValue,12,2);
	var milSecond = substr(objValue,14,2);

	/*
	ExtInitReferenceCounter(strYear,strMonth,strDay,strHour,strMinute,strSecond,strMilliseconds) ; 
		   argument 값을 구성 한 후 초기 한번 호출합니다.
	  
		   argument는 
				   strYear : 년(string 4자리)
				   strMonth: 월(string 2자리 ) 
				   strDay: 일(string)    
				   strHour: 시(string)    
				   strMinute: 분(string)
				   strSecond: 초(string)
				   strMilliseconds: 밀리세컨드(string)
	*/

	ExtInitReferenceCounter(year,month,day,hour,minuts,second,"000") ;
}

 /*******************************************************************************
 * @description    : 서버와 동기화된 시간을 가져온다. (YYYYMMDD24HHMISSsss)
*******************************************************************************/
function cf_getDate()
{
	var sSysdate = ExtGetHDTime(); // YYYYMMDD24HHMISSsss
	
	if(uf_IsNull(sSysdate)){
		alert("현재 날짜가 유효하지 않습니다. 담당자에게 문의하세요.");
		this.close();
	}	
	return sSysdate; // YYYYMMDD24HHMISSsss
}

 /*******************************************************************************
 * @description    : 서버와 동기화된 시간을 가져온다. (YYYYMMDD)
*******************************************************************************/
function cf_today()
{
	var sSysdate = ExtGetHDTime(); // YYYYMMDD24HHMISSsss
	
	if(uf_IsNull(sSysdate)){
		alert("현재 날짜가 유효하지 않습니다. 담당자에게 문의하세요.");
		this.close();
	}	
	return substr(sSysdate,0,8); // YYYYMMDD
}

/*******************************************************************************
 * @description      : 월마감 조회.
 * @param sBizCd     : 업무 코드 (필수)
                         - 01 : 재고, 02 : 영업, 03 : 정산, 04 : 정책, 05 : 회계                 
*******************************************************************************/
function cf_getCloseInfo(sBizCd)
{	
	FV_SYNC       = http.Sync;
	FV_BIZ_CD     = sBizCd;     // 업무 코드 
	FV_CLOSE_MNTH = "";         // 마감월 초기화

	// http sync 되어 있지 않는 경우 true 셋팅.
	if(!http.Sync){
		http.Sync = true;
	}
	
	if(sBizCd == null || sBizCd == ""){
	
		alert('업무 코드는 필수 입니다. \n\nex)01 : 재고, 02 : 영업, 03 : 정산, 04 : 정책, 05 : 회계');
		
		f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정		
		
		return false;
	}

	// 트랜잭션 설정
	var sSvcID = "getCloseInfo";
	var sInDs  = "";
	var sOutDs = "gds_temp=ds_closeInfo";
	var sArg   = "sktst.acc.ACO#getCloseInfo"; 

	// 공통 트랜잭션 호출
	cf_transaction(sSvcID, sInDs, sOutDs, sArg);
}

/*******************************************************************************
 * @description          : 월마감 조회 callBack.
*******************************************************************************/
function cf_setCloseInfoCallBack()
{	
	var sAllCloseInfo = gds_temp.GetColumn(0,"close_info"); //  모든 업무가 포함된 마감월.
	var arrCloseInfo = Array();
	var sCloseInfo;
	var sCloseYYYYMM = "";
	
	//  서버에서 조회한 전체 마감월에서 업무코드에 해당하는 마감월 추출.
	for(var i = 0 ; i < sAllCloseInfo.length ; i=i+8 ){
	
		sCloseInfo = "";
		sCloseInfo = substr(sAllCloseInfo,i,8);
		
		if(substr(sCloseInfo,0,2) == FV_BIZ_CD){
			sCloseYYYYMM = substr(sCloseInfo,2);
			break;
		}
	}
	
	if(sCloseYYYYMM == null || sCloseYYYYMM == ""){
	
		alert("마감월이 존재 하지 않습니다. 관리자에게 문의 하세요.");
		
		f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정	
		
		return false;
	}

	f_resetCloseMnthVar(); // 마감월 변수 초기화 및 http sync 설정

	FV_CLOSE_MNTH =  sCloseYYYYMM; // 글로벌 변수에 마감월 셋팅.
}

/*******************************************************************************
 * @description      : 월마감 체크.
 * @param sBizCd     : 업무 코드 (필수)
                         - 01 : 재고, 02 : 영업, 03 : 정산, 04 : 정책, 05 : 회계, 06 : 판매수수료');
 * @param sInputDate : 입력일   (필수)     
 * @param sInputNm   : 입력일명 (필수)                   
*******************************************************************************/
function cf_CheckCloseMnth(sBizCd, sInputDate, sInputNm)
{	
	FV_SYNC          = http.Sync;
	FV_BIZ_CD        = sBizCd;     // 업무 코드 
	FV_INPUT_DATE    = sInputDate; // 입력일
	FV_INPUT_DATE_NM = sInputNm;   // 입력일 명
	FV_BOOLEAN_CLOSE = "";         // 마감여부 초기화

	// http sync 되어 있지 않는 경우 true 셋팅.
	if(!http.Sync){
		http.Sync = true;
	}
	
	if(sBizCd == null || sBizCd == ""){
	
		alert('업무 코드는 필수 입니다. \n\nex)01 : 재고, 02 : 영업, 03 : 정산, 04 : 정책, 05 : 회계, 06 : 판매수수료');
		
		FV_BOOLEAN_CLOSE = true;
		f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정
		
		return false;
	}
	
	if(sInputDate == null || sInputDate == ""){
	
		alert(FV_INPUT_DATE_NM+'은(는) 필수 입니다.');
		
		FV_BOOLEAN_CLOSE = true;
		f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정	
		
		return false;
	}	

	if(sBizCd == "06"){   //판매수수료 회계월마감이면...
		gds_closeInfo.SetColumn(0,"gubun","02");
	} else if(sBizCd == "05"){   //회계월마감이면...
		gds_closeInfo.SetColumn(0,"gubun","03");
	} else {  //월마감이면...
		gds_closeInfo.SetColumn(0,"gubun","01");
	}
	

	// 트랜잭션 설정
	var sSvcID = "checkCloseMnth";
	var sInDs = "nc_input_fieldDs=gds_closeInfo";
	//var sInDs  = "";
	var sOutDs = "gds_temp=ds_closeInfo";
	var sArg   = "sktst.acc.ACO#getCloseInfo"; 

	// 공통 트랜잭션 호출
	cf_transaction(sSvcID, sInDs, sOutDs, sArg);
}

/*******************************************************************************
 * @description          : 월마감 조회 callBack.
*******************************************************************************/
function cf_checkCloseMnthCallBack()
{	
	
	var sCloseYYYYMM = gds_temp.GetColumn(0,"close_info"); //  모든 업무가 포함된 마감월.

	if(sCloseYYYYMM == null || sCloseYYYYMM == "" || sCloseYYYYMM.length < 6 ){
	
		alert("마감월이 존재 하지 않습니다. 관리자에게 문의 하세요.");
		
		FV_BOOLEAN_CLOSE = true;
		f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정	
		
		return false;
	}

    if(FV_BIZ_CD == "06") {
		// 판매수수료 회계월마감 입력일을 마감월과 비교한다.
		if( toNumber(substr(FV_INPUT_DATE,0,6)) != toNumber(sCloseYYYYMM) ){
			var sInputDateByFormat = substr(FV_INPUT_DATE,0,4)+'-'+substr(FV_INPUT_DATE,4,2);

		    alert("입력하신 " +FV_INPUT_DATE_NM+"["+sInputDateByFormat+ "] 판매수수료 회계마감은 최종 월마감과 다름니다.");
			FV_BOOLEAN_CLOSE = true;
			f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정	
					
			return false;
		}
    } else if(FV_BIZ_CD == "05") {
		// 회계월마감 입력일을 마감월과 비교한다.
		if( toNumber(substr(FV_INPUT_DATE,0,6)) != toNumber(sCloseYYYYMM) ){
			var sInputDateByFormat = substr(FV_INPUT_DATE,0,4)+'-'+substr(FV_INPUT_DATE,4,2);

		    alert("입력하신 " +FV_INPUT_DATE_NM+"["+sInputDateByFormat+ "] 회계월은 최종 회계월마감과 다름니다.");
			FV_BOOLEAN_CLOSE = true;
			f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정	
					
			return false;
		}
    } else if(FV_BIZ_CD == "03") {
		// 입력일을 마감월과 비교한다.
		if( toNumber(substr(FV_INPUT_DATE,0,6)) != toNumber(sCloseYYYYMM) ){	
			
			var sCloseYYYYMMByFormay = substr(sCloseYYYYMM,0,4)+'-'+substr(sCloseYYYYMM,4,2);
			var sInputDateByFormat = substr(FV_INPUT_DATE,0,4)+'-'+substr(FV_INPUT_DATE,4,2);
			
			if(FV_INPUT_DATE.length > 6){
				sInputDateByFormat = sInputDateByFormat + '-'+substr(FV_INPUT_DATE,6,2);
			}
			
			alert('입력하신 '+FV_INPUT_DATE_NM+'['+sInputDateByFormat+']은 마감월과 다릅니다. '
				 +'\n\n최종 마감월['+sCloseYYYYMMByFormay+'] 확인 후 입력하시기 바랍니다.');
			
			FV_BOOLEAN_CLOSE = true;
			f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정	
					
			return false;
		}
    } else {  //영업재고용...
		// 입력일을 마감월과 비교한다.
		if( toNumber(substr(FV_INPUT_DATE,0,6)) <= toNumber(sCloseYYYYMM) ){	
			
			var sCloseYYYYMMByFormay = substr(sCloseYYYYMM,0,4)+'-'+substr(sCloseYYYYMM,4,2);
			var sInputDateByFormat = substr(FV_INPUT_DATE,0,4)+'-'+substr(FV_INPUT_DATE,4,2);
			
			if(FV_INPUT_DATE.length > 6){
				sInputDateByFormat = sInputDateByFormat + '-'+substr(FV_INPUT_DATE,6,2);
			}
			
			alert('입력하신 '+FV_INPUT_DATE_NM+'['+sInputDateByFormat+']은 마감월에 해당합니다. '
				 +'\n\n최종 마감월['+sCloseYYYYMMByFormay+'] 확인 후 입력하시기 바랍니다.');
			
			FV_BOOLEAN_CLOSE = true;
			f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정	
					
			return false;
		}
    }
	
	FV_BOOLEAN_CLOSE = false;
	// 처리 후 초기화.

	f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정
}


/*******************************************************************************
 * @description      : 일마감 체크.
 * @param sBizCd     : 업무 코드 (필수)
                         - 01 : 재고, 02 : 영업, 03 : 정산, 04 : 정책, 05 : 회계, 06 : 판매수수료');
 * @param sInputDate : 입력일   (필수)     
 * @param sInputNm   : 입력일명 (필수)                   
*******************************************************************************/
function cf_CheckCloseDay(sBizCd, sInputDate, sInputNm)
{	
	FV_SYNC          = http.Sync;
	FV_BIZ_CD        = sBizCd;     // 업무 코드 
	FV_INPUT_DATE    = sInputDate; // 입력일
	FV_INPUT_DATE_NM = sInputNm;   // 입력일 명
	FV_BOOLEAN_CLOSE = "";         // 마감여부 초기화

	// http sync 되어 있지 않는 경우 true 셋팅.
	if(!http.Sync){
		http.Sync = true;
	}
	
	if(sBizCd == null || sBizCd == ""){
	
		alert('업무 코드는 필수 입니다. \n\nex)01 : 재고, 02 : 영업, 03 : 정산, 04 : 정책, 05 : 회계, 06 : 판매수수료');
		
		FV_BOOLEAN_CLOSE = true;
		f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정
		
		return false;
	}
	
	if(sInputDate == null || sInputDate == ""){
	
		alert(FV_INPUT_DATE_NM+'은(는) 필수 입니다.');
		
		FV_BOOLEAN_CLOSE = true;
		f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정	
		
		return false;
	}	
	
	if(sInputDate.length < 8){
		alert(FV_INPUT_DATE_NM+'의 입력이 잘못되었습니다.');
		
		FV_BOOLEAN_CLOSE = true;
		f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정	
		
		return false;
	}

	gds_closeInfo.SetColumn(0,"gubun","00");
	

    if(FV_BIZ_CD == "03") {
       FV_BIZ_CD = "04";
    }
	cf_CheckCloseMnth(FV_BIZ_CD,substr(FV_INPUT_DATE,0,8),FV_INPUT_DATE_NM); 

	
	/**
	// 트랜잭션 설정
	var sSvcID = "checkCloseDay";
	var sInDs = "nc_input_fieldDs=gds_closeInfo";
	//var sInDs  = "";
	var sOutDs = "gds_temp=ds_closeInfo";
	var sArg   = "sktst.acc.ACO#getCloseInfo"; 

	// 공통 트랜잭션 호출
	cf_transaction(sSvcID, sInDs, sOutDs, sArg);
    **/
}

/*******************************************************************************
 * @description          : 일마감 조회 callBack.
*******************************************************************************/
function cf_checkCloseDayCallBack()
{	
	
	var sCloseYYYYMMDD = gds_temp.GetColumn(0,"close_info"); //  모든 업무가 포함된 마감일.

	if(sCloseYYYYMMDD == null || sCloseYYYYMMDD == "" || sCloseYYYYMMDD.length < 8 ){
	
		alert("마감일이 존재 하지 않습니다. 관리자에게 문의 하세요.");
		
		FV_BOOLEAN_CLOSE = true;
		f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정	
		
		return false;
	}

    if(FV_BIZ_CD == "03") {
			// 입력일을 마감일을 비교한다.
			if( toNumber(substr(FV_INPUT_DATE,0,8)) > toNumber(sCloseYYYYMMDD) ){	
				
				var sCloseYYYYMMDDByFormay = substr(sCloseYYYYMMDD,0,4)+'-'+substr(sCloseYYYYMMDD,4,2)+'-'+substr(sCloseYYYYMMDD,6,2);
				var sInputDateByFormat = substr(FV_INPUT_DATE,0,4)+'-'+substr(FV_INPUT_DATE,4,2)+'-'+substr(FV_INPUT_DATE,6,2);
				
				if(FV_INPUT_DATE.length > 8){
					sInputDateByFormat = sInputDateByFormat + ' '+substr(FV_INPUT_DATE,8,2) + ':'+substr(FV_INPUT_DATE,10,2);
				}
				
				alert('입력하신 '+FV_INPUT_DATE_NM+'['+sInputDateByFormat+']은 마감되지 않았습니다. '
					 +'\n\n최종 마감일['+sCloseYYYYMMDDByFormay+'] 이전으로 입력하시기 바랍니다.');
				
				FV_BOOLEAN_CLOSE = true;
				f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정	
						
				return false;
			}
			
    } else {
			// 입력일을 마감일을 비교한다.
			if( toNumber(substr(FV_INPUT_DATE,0,8)) <= toNumber(sCloseYYYYMMDD) ){	
				
				var sCloseYYYYMMDDByFormay = substr(sCloseYYYYMMDD,0,4)+'-'+substr(sCloseYYYYMMDD,4,2)+'-'+substr(sCloseYYYYMMDD,6,2);
				var sInputDateByFormat = substr(FV_INPUT_DATE,0,4)+'-'+substr(FV_INPUT_DATE,4,2)+'-'+substr(FV_INPUT_DATE,6,2);
				
				if(FV_INPUT_DATE.length > 8){
					sInputDateByFormat = sInputDateByFormat + ' '+substr(FV_INPUT_DATE,8,2) + ':'+substr(FV_INPUT_DATE,10,2);
				}
				
				alert('입력하신 '+FV_INPUT_DATE_NM+'['+sInputDateByFormat+']은 마감일에 해당합니다. '
					 +'\n\n최종 마감일['+sCloseYYYYMMDDByFormay+'] 이후로 입력하시기 바랍니다.');
				
				FV_BOOLEAN_CLOSE = true;
				f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정	
						
				return false;
			}
			cf_CheckCloseMnth(FV_BIZ_CD,substr(FV_INPUT_DATE,0,8),FV_INPUT_DATE_NM); 
    }

	
	FV_BOOLEAN_CLOSE = false;
	// 처리 후 초기화.
	f_resetCloseMnthVar(); //마감월 변수 초기화 및 http sync 설정
}


/*******************************************************************************
 * @description          : 마감월 변수 초기화 및 http sync 설정
*******************************************************************************/
function f_resetCloseMnthVar()
{
	// cf_getCloseInfo()에서 http sync를 셋팅한 경우 false 셋팅.

    if(FV_BIZ_CD == "01") {
	  if(FV_SYNC){
	 	http.Sync = false;
	  }	
	} else {
	  if(!FV_SYNC){
	 	http.Sync = false;
	  }	
    }		
	
	FV_SYNC          = "";
	FV_BIZ_CD        = "";
	FV_INPUT_DATE    = "";
	FV_INPUT_DATE_NM = "";
	gds_temp.clearData();
		
}




function cf_setComboSalePlc(sCompId){

	var oCodeCondtionDs = cf_isValid("ds_tmpStlPlcInfo");
	
	var oComponent = object(sCompId);
	var sComponentType = oComponent.getType();
				
	if(oCodeCondtionDs == null)
	{
		create("Dataset", "ds_tmpStlPlcInfo");
		oCodeCondtionDs = ds_tmpStlPlcInfo;
		oCodeCondtionDs.AddColumn("componentId");
		oCodeCondtionDs.AddColumn("org_area");
	}
	else
	{
		ds_tmpStlPlcInfo.clearData();		
	}
	
	var nRow = oCodeCondtionDs.addRow();
	
	if(sComponentType != "Combo"){
		alert("적합하지 않은 컴포넌트입니다.");
		return false;		
	}else{
	   
	   oCodeCondtionDs.SetColumn(nRow, "componentId", sCompId);
	}
		
	if(gds_session.GetColumn(0, "orgAreaId").length !=5){
		alert("존재하지 않는 판매처 코드입니다.");
		return false;
	}else{
		
		oCodeCondtionDs.SetColumn(nRow, "org_area", gds_session.GetColumn(0, "orgAreaId"));
	}
	
	// 트랜잭션 설정
	var sSvcID = "svcComboStlPlc";
	var sInDs = "nc_input_fieldDs=ds_tmpStlPlcInfo";
	var sOutDs = "gds_temp=ds_stlPlc";
	var sArg = "sktst.bas.CDM#getStlPlcListByID"; 

	// 공통 트랜잭션 호출
	cf_transaction(sSvcID, sInDs, sOutDs, sArg);
}



function cf_checkStlPlcCallBack(){
	
	var sComponentId = ds_tmpStlPlcInfo.GetColumn(0, "componentId");
	
	var sCommonCodeDsId = ds_tmpStlPlcInfo.GetColumn(0, "org_area");
	var oComponent = object(sComponentId);
	
	var sComponentType = oComponent.getType();
	var sCodeColumn = "DEAL_CO_CD";
	var sDataColumn = "DEAL_CO_NM";
	
	
	if(gds_temp.GetRowCount() < 1)
	{
		alert("판매처목록이 존재하지 않습니다.");
		return;
	}

	var sDsId = sCommonCodeDsId;
	var oDs = cf_isValid(sDsId);
	if(oDs == null)
	{
		create("Dataset", sDsId);
		oDs = object(sDsId);
	}
	else
	{
		oDs.clear();
	}
	
	oComponent.reDraw = false;
	oDs.FireEvent = false;
	oDs.copyF(gds_temp);
	
	oComponent.InnerDataset = sDsId;
	oComponent.CodeColumn = sCodeColumn;
	oComponent.DataColumn = sDataColumn;

	oComponent.Index = 0;
	oDs.FireEvent = true;
	oComponent.redraw = true;
}


/*******************************************************************************
 * @description    : ID별 권한 매핑(정책)
 * @return         : 없음
*******************************************************************************/
function cf_idAuthSet(FV_ID)
{

	if (    FV_ID == "140419" 
		 || FV_ID == "180081"
		 || FV_ID == "141492"
		 || FV_ID == "180404"
	   ) {
		FV_ID   = "OK";
	} else {
		FV_ID   = "NO";
	}

    return FV_ID;

}


/*******************************************************************************
 * @description    : 대리점매핑(정책)
 * @return         : 없음
*******************************************************************************/
function cf_orgMapping(FV_ORG_ID,TM_ORG_ID)
{
   //대리점매핑
	/**
	1.상위-서대구블루골드
	   하위-구미블루골드/서대구블루골드특판
	
	2.상위-동대구블루골드
	   하위-포항블루골드/동대구블루골드특판
	
	3.상위-대전블루골드
	   하위-대전블루골드특판/대전블루골드TFS
	
	4.상위-서부산블루골드(C20001)
	   하위-창원블루골드(C20005)/진주블루골드(C20004)
	
	5.상위-동부산블루골드(C20002)
	   하위-울산블루골드(C20003)/부산비즈블루골드(C20002)
	
	6.상위-광주블루골드
	   하위-광주블루골드TFS/광주블루골드 BIZ
	
	7.상위-성장센터(C10011)
	   하위-광주센터(C40001)/대전센터(C60001)

	8.상위-동부센터(C10007)
	   하위-강남센터(C10005)

	9.상위-천안센터(C60002)
	   하위-원주센터(C60004)/강릉센터(C60005)/
	
	10.상위-대전센터(C60001)
	   하위-충북센터(C60003)

	**/   
	
	if (FV_ORG_ID == "C30001" && TM_ORG_ID == "C30003") { 
		FV_ORG_ID = "C30003";   //서대구가 구미를 클릭하면 --> 구미
	}
	if (FV_ORG_ID == "C30002" && TM_ORG_ID == "C30004") { 
		FV_ORG_ID = "C30004";   //동대구가 포항을 클릭하면 --> 포항
	}
	if (FV_ORG_ID == "C20001" && TM_ORG_ID == "C20005") {  
		FV_ORG_ID = "C20005";   //서부산이 창원을 클릭하면 --> 창원
	}
	if (FV_ORG_ID == "C20001" && TM_ORG_ID == "C20004") {  
		FV_ORG_ID = "C20004";   //서부산이 진주를 클릭하면 --> 진주
	}

	if (FV_ORG_ID == "C20002" && TM_ORG_ID == "C20003") {  
		FV_ORG_ID = "C20003";   //동부산이 울산을 클릭하면 --> 울산
	}

	if (FV_ORG_ID == "C10011" && TM_ORG_ID == "C40001") {  
		FV_ORG_ID = "C40001";   //성장이 광주를 클릭하면 --> 광주
	}

	if (FV_ORG_ID == "C10007" && TM_ORG_ID == "C10005") {  
		FV_ORG_ID = "C10005";   //동부센터가 강남센터을 클릭하면 --> 강남센터
	}

	if (FV_ORG_ID == "C10005" && TM_ORG_ID == "C10007") {  
		FV_ORG_ID = "C10007";   //강남센터가 동부센터을 클릭하면 --> 동부센터
	}

	if (FV_ORG_ID == "C60002" && TM_ORG_ID == "C60004") {  
		FV_ORG_ID = "C60004";   //천안센터가 원주센터을 클릭하면 --> 원주센터
	}
	if (FV_ORG_ID == "C60002" && TM_ORG_ID == "C60005") {  
		FV_ORG_ID = "C60005";   //천안센터가 강릉센터을 클릭하면 --> 강릉센터
	}

	if (FV_ORG_ID == "C60001" && TM_ORG_ID == "C60003") {  
		FV_ORG_ID = "C60003";   //대전센터가 충북센터을 클릭하면 --> 충북센터
	}

    return FV_ORG_ID;

}

/*******************************************************************************
 * @description    : DS to XML
 * @param arrObj   : 데이터셋 배열
*******************************************************************************/
function cf_dsToXml(arrObj){
	var strTemp = "";
	
	var dsobj = split(arrObj,",");
	var ds_cnt = length(dsobj);
				
	strTemp += '<?xml version="1.0" encoding="euc-kr"?>' + chr(10);
	strTemp += '<root>';
	
	for (i=0; i<toNumber(ds_cnt); i++){
	    var sTmp = replace(dsobj[i], "[", "");
	    sTmp = replace(sTmp, "]", "");
	    sTmp = replace(sTmp, "Dataset:", "");
		strTemp += object(sTmp).saveXML(object(sTmp).id, "A", false);
	}
	
	strTemp += chr(10) + '</root>';
	
	return strTemp;
}