/*******************************************************************************
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
#include "lib::lib_util.js"
#include "lib::lib_commF.js"
#include "lib::lib_commMsg.js"

/*******************************************************************************
 * @description       : 최초 마이플랫폼 기동시 처리
*******************************************************************************/
function gf_onInit() 
{
	var sAppName = "st";
	var serverUrl=global.StartXML;

	if(serverUrl.IndexOf("http://") != -1)	{
		gv_url = substr(serverUrl,0, indexOf(serverUrl,"/",8))+"/"+sAppName+"/";
	} else if (serverUrl.IndexOf("https://") != -1) {
		gv_url = substr(serverUrl,0, indexOf(serverUrl,"/",9))+"/"+sAppName+"/";
	}
	else
	{
		gv_url = "http://localhost:8088/"+sAppName+"/";
		//gv_url = "http://203.235.217.198:9060/"+sAppName+"/";
	}
	
}

/*******************************************************************************
 * @description       : 종료 직전 처리 
*******************************************************************************/
function gf_onBeforeExit() 
{
    var checkLogOut = false;
	if(!gv_closeFlag)
	{
		if(confirm("종료 하시겠습니까?") == true)
		{
			checkLogOut = true;
		}
		else
		{
			return false;
		}
	}
	
	if(checkLogOut)
	{
		http.Sync = true;
		//docTop.f_logOut();
		//InitSession(false);
		gv_closeFlag = true;
		http.Sync = false;
	}	
}

function gf_onError(obj, nErrorCode, strErrorMsg,strID)
{
	//trace(obj + " / " + nErrorCode+ " / " + strErrorMsg+ " / " +strID );
	if(strID == "ScriptLoader")
	{
		alert("미개발 화면이거나 해당 화면에 오류가 존재합니다");
		return;
	}
}


/*******************************************************************************
 * @description       : HTTP 에러시 발생
*******************************************************************************/
function gf_onHttpError(obj, sUrl, nErrorCode, sErrorMsg)
{
	switch (nErrorCode)
	{
		case -2085605317:
			alert("Server연결에 실패 하였습니다.\nNetwork에 이상이 있는지,경로가 맞는지 확인해주시기 바랍니다.");
			break;
		case -2085601264:
			alert("HTTP 404: Not found, 문서를 찾을 수 없음 ");
			break;
		case -2085601263:
			alert("HTTP 405: Method not allowed, 리소스를 허용안함 ");
			break;
		case -2085601262:
			alert("HTTP 500: Internal server error, 내부서버 오류(잘못된 스크립트 실행시)");
			break;
		case -2085601261:
			alert("HTTP 503: Service unavailable, 외부 서비스가 죽었거나 현재 멈춤 상태");
			break;
		case -2085613056:
			alert("HTTP 302: Session Time Out, 사용자 세션이 끊겼으니 재 로그인 후, 사용바랍니다.");
			break;
	}
}


/*******************************************************************************
 * @description       : 열린 메뉴 정보 설정
 * @param sTitle      : 메뉴 제목
	     sMenuNum     : 메뉴 번호
	     sUrl         : 메뉴 주소
	     sTabId       : 윈도우 아이디
	     sTabIndex    : 메뉴 탭 인덱스
*******************************************************************************/
function gf_addOpenMenu(sTitle, sMenuNum, sUrl, sTabId)
{
	var nRow = gds_openMenu.AddRow();
	gds_openMenu.SetColumn(nRow,"pageTitle", sTitle);
	gds_openMenu.SetColumn(nRow,"pageNum", sMenuNum);
	gds_openMenu.SetColumn(nRow,"pageUrl", sUrl);
	gds_openMenu.SetColumn(nRow,"pageTabId", sTabId);
	//gds_openMenu.SetColumn(nRow,"pageTabIndex", sTabIndex);
	
	var oTopMenuDs = docTop.ds_topMenu;
	if(oTopMenuDs.findRow("menu_id", sMenuNum) == -1)
	{
		var nTopMenuRow = oTopMenuDs.addRow();
		
		oTopMenuDs.SetColumn(nTopMenuRow,"menu_id", sTabId);
		oTopMenuDs.SetColumn(nTopMenuRow,"menu_name", sTitle);
		oTopMenuDs.SetColumn(nTopMenuRow,"menu_level", 2);
	}
}

/*******************************************************************************
 * @description       : 메뉴 오픈
 * @param sTitle      : 메뉴 제목
	     sMenuNum     : 메뉴 번호
	     sUrl         : 메뉴 주소
	     sTabId       : 윈도우 아이디
	     sUserArg     : 사용자 추가 정의 파라메터
*******************************************************************************/
function gf_openChildWindow(sTitle, sMenuNum, sUrl, sTabId, sUserArg)
{
	var sArg  = "pFrmTitle=" + quote(sTitle);
		sArg += " pFrmNum=" + quote(sMenuNum);
		sArg += " pFrmUrl=" + quote(sUrl);
		sArg += " pFrmWinId=" + quote(sTabId);
		sArg += " pIsNew=1";
		if(sUserArg != null)
		{
			sArg += " "+sUserArg;
		}
	
	NewWindow(sTabId, "main::bodyFrame.xml", sArg, 830, 600, "OpenStyle=Max closeFlag=false", 0, 0);
}

/*******************************************************************************
 * @description       : 왼쪽메뉴를 통한 화면 오픈
 * @param sMenuNum   : 메뉴 번호
	       oDs        : 메뉴 DS
*******************************************************************************/
function gf_openMenu(sMenuNum)
{
	//if(oDs == null) oDs = gds_menu;
	var nRow = gds_menu.FindRow("menuNum", sMenuNum);
	
	var sTitle = gds_menu.GetColumn(nRow, "menuShotNm");
	var sTabTitle = gds_menu.GetColumn(nRow, "menuShotNm");

	var nMenuNum1 = gds_menu.FindRow("DELAUTHYN", gds_menu.GetColumn(nRow, "supMenuNum")); // 메뉴 소
	var nMenuNum2 = 0; // 메뉴중
	var nMenuNum3 = 0; // 메뉴대
	
	if(nMenuNum1 != -1){
	
		sTitle = gds_menu.GetColumn(nMenuNum1, "ETC1AUTHYN") +" > " + sTitle;
		
		nMenuNum2 = gds_menu.FindRow("DELAUTHYN", gds_menu.GetColumn(nMenuNum1, "MENUNM"));

		if(nMenuNum2 != -1){
		
			sTitle = gds_menu.GetColumn(nMenuNum2, "ETC1AUTHYN") +" > " + sTitle;			
		}
	}

	var sGroup = gds_menu.GetColumn(nRow, "topPrefixCd");
	var sForm = gds_menu.GetColumn(nRow, "screenId");
	var sSize = gds_menu.GetColumn(nRow, "screenSize");

	if(sSize == "Y")
	{
		docLeft.f_showLeftMenu(false);
	}

	var sUrl = sGroup+"::"+sForm;
	if(sUrl.length < 3) return;
	
	// 중복체크
	var nFindRow = gds_openMenu.FindRow("pageNum", sMenuNum);
	if(nFindRow != -1)
	{
		var sTabId = gds_openMenu.GetColumn(nFindRow, "pageTabId");
		var oWinObj = AllWindows(sTabId);
		if(oWinObj[0] != null)
		{
			docBottom.tab_openWindow.object(sTabId).setFocus();
			oWinObj[0].setFocus();
			return;
		}
	}	

	var sTabId = "winTab"+docBottom.FV_INT_TAB_COUNT;
	
	// Open 메뉴 리스트 추가 -- DATASET : gds_openMenu
	gf_addOpenMenu(sTitle, sMenuNum, sUrl, sTabId, docBottom.tab_openWindow.TabCount);

	// MDI 윈도우 실행 
	gf_OpenChildWindow(sTitle, sMenuNum, sUrl, sTabId);
	
	// Tab 추가
	docBottom.f_addNewWindow(sTabTitle, sMenuNum, sUrl);	
}


/*******************************************************************************
 * @description            : 호출을 통한 직접 화면 오픈
 * @param sMenuNum         : 메뉴 번호
	       sArg            : 사용자 정의 추가 파라메터
	       sWinTitle       : 사용자 정의 창 이름
*******************************************************************************/
function gf_openWindow(sMenuNum, sArg, sWinTitle)
{
	var nRow = gds_menu.FindRow("menuNum", sMenuNum);
	if(nRow >= 0)
	{		
		var sTitle = gds_menu.GetColumn(nRow, "menuShotNm");
		var sTabTitle = gds_menu.GetColumn(nRow, "menuShotNm");
	
		var nMenuNum1 = gds_menu.FindRow("DELAUTHYN", gds_menu.GetColumn(nRow, "supMenuNum")); // 메뉴 소
		var nMenuNum2 = 0; // 메뉴중
		var nMenuNum3 = 0; // 메뉴대
		
		if(nMenuNum1 != -1){
		
			sTitle = gds_menu.GetColumn(nMenuNum1, "ETC1AUTHYN") +" > " + sTitle;
			
			nMenuNum2 = gds_menu.FindRow("DELAUTHYN", gds_menu.GetColumn(nMenuNum1, "MENUNM"));
	
			if(nMenuNum2 != -1){
			
				sTitle = gds_menu.GetColumn(nMenuNum2, "ETC1AUTHYN") +" > " + sTitle;			
			}
		}		
		
		var sGroup = gds_menu.GetColumn(nRow, "topPrefixCd");
		var sForm = gds_menu.GetColumn(nRow, "screenId");
		var sSize = gds_menu.GetColumn(nRow, "screenSize");
	
		if(sSize == "Y")
		{
			docLeft.f_showLeftMenu(false);
		}
	
		var sUrl = sGroup+"::"+sForm;
		if(sUrl.length < 3) return;
		
		// 중복체크
		if(gf_movenReset(sMenuNum, sArg)) return;
	
		var sTabId = "winTab"+docBottom.FV_INT_TAB_COUNT;
	
		// Open 메뉴 리스트 추가 -- DATASET : gds_openMenu
		gf_addOpenMenu(sTitle, sMenuNum, sUrl, sTabId);
	
		// MDI 윈도우 실행 
		gf_OpenChildWindow(sTitle, sMenuNum, sUrl, sTabId, sArg);
		
		// Tab 추가
		docBottom.f_addNewWindow(sTabTitle);
	}
	else
	{
		alert(MSG_00989);
		return;
	}
}


/*******************************************************************************
 * @description            : 화면 이동 및 재설정 함수 호출
 * @param sMenuNum         : 메뉴 번호
	       sFunc           : 호출 함수
	       sParam          : 함수 파라메터
*******************************************************************************/
function gf_movenReset(sFrmNum, sArg)
{
	if(sFrmNum != null)
	{
		var nRow = gds_openMenu.FindRow("pageNum", sFrmNum);

		if(nRow != -1)
		{
			docBottom.f_tabReset(gds_openMenu.GetColumn(nRow, "pageTabId"), sArg);
			return true;
		}
		else
		{
			return false;
		}
	}
}

/*******************************************************************************
 * @description            : 화면 이동 및 함수 호출
 * @param sMenuNum         : 메뉴 번호
	       sFunc           : 호출 함수
	       sParam          : 함수 파라메터
*******************************************************************************/
function gf_movenCall(sFrmNum, sFunc, sParam)
{
	if(sFrmNum != null)
	{
		var nRow = gds_openMenu.FindRow("pageNum", sFrmNum);
	
		if(nRow == -1)
		{
			var sTitle = gds_menu.LookUp("menuNum", sFrmNum, "menuShotNm");
			alert(cf_getMessage(MSG_00998, sTitle));
			return false;
		}

		docBottom.f_tabReCall(gds_openMenu.GetColumn(nRow, "pageTabId"), sFunc, sParam);
	}
}

/*******************************************************************************
 * @description   : F9 단축키 이벤트 ( 세션 시간 연장 시키기 위해 )
*******************************************************************************/
function gf_onKeyDown(obj,objSenderObj,nChar,bShift,bControl,bAlt,nLLParam,nHLParam){

	if(nChar == 120){

		// 트랜잭션 설정
		var sSvcID = "getSessionExtension";
		var sInDs  = "";
		var sOutDs = "";
		var sArg   = "sktst.bas.BCO#getSessionExtension"; 
	
		// 공통 트랜잭션 호출
		cf_transaction(sSvcID, sInDs, sOutDs, sArg);		
	}
}