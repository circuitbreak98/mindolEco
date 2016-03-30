<%@ page contentType="text/html;charset=utf-8" %>
<HTML>
<HEAD>
<TITLE></TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=euc-kr">
<link rel="stylesheet" href="./image/style.css" type="text/css">
<SCRIPT LANGUAGE="JavaScript" src="./MiInstaller320_new.js"></SCRIPT>
<script language="JavaScript">
function Check_Module()
{
  try
  {
     //alert(MiInstaller.Version);
     if(MiInstaller.Version == undefined)
     return false; // 설치가 안되어 있거나 system32에 MiInstlr321.dll 이 없는 경우.(unregistered 된 경우)
     else
     return true;
    
  }
  catch(ex)
  {
    //alert("catch");
        // MiInstaller.js 가 올라오지 못해서 object를 사용할 수 없는 경우 
      return false;
  }
}
</script>

<%
//쿠키세팅
Cookie cookies [] = request.getCookies ();
Cookie sCookie = null;
if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        if (cookies [i].getName().equals ("SMSESSION")) {
          sCookie = cookies[i];
          break;
        }
      }
}

%>

<SCRIPT LANGUAGE="JavaScript">
    var pg_cell_At = 0, pg1_cell_At = 0, GcurIndx = 0, Gtotcnt = 0; 
    var IsError = false;

    var Server_Path = window.location.href;
    Server_Path = Server_Path.substring(0, Server_Path.lastIndexOf("/")) + "/";  // 서버 패스
    var keyName = "SKTPS";  // 프로젝트 키
    //var keyName = "T.Key+";  // 프로젝트 키
    var processWidth = 590;

    function fn_OnLoad()
    {
    //MiInstaller사용시
    //Object tag에서 Param으로 Version과 Key입력

        if ( Check_Module() == true )
        {
            var userId = "";
            userId = "<%=request.getHeader("HTTP_SM_TKEYPLUSID")%>";
            if ( userId == "null") {
                userId = "";
            }
            MiInstaller.GlobalVal = userId+"‡<%=sCookie!=null?sCookie.getValue():""%>";
            MiInstaller.Key = keyName;
            MiInstaller.Launch = true;  
            //MiInstaller.Width  = 1024;
            //MiInstaller.Height    = 768;
            MiInstaller.Retry = 1;
            MiInstaller.Timeout = 30000;
            //MiInstaller.OnlyOne = true;
            MiInstaller.ComponentPath = "%UserApp%"+keyName+"\\component";
            MiInstaller.StartXml  = Server_Path+"xml/ST_ci_main_svr.xml";
		    //MiInstaller.StartXml  = Server_Path+'xml/ST_ci_main_' + (Server_Path.indexOf('localhost') > -1 ? 'Win32.xml' : Server_Path.indexOf('203.235.217.198') > -1 ? 'svr.xml' : 'dev.xml'  );
            MiInstaller.Resource = "%Component%resource.xml";   
            MiInstaller.StartImage  = "%Component%start_img.gif"; // 시작 이미지
        //  MiInstaller.SetGlobalVariableValue( "gv_id", "tttttt");
            //MiInstaller.NotLaunchForErr = "true";
            
            var mode = checkOS();
    
            var IsUACEnabled = MiInstaller.IsUACEnabled();
            var uacMode = "X";
    
            if ( mode == "VISTA" )
            {
                uacMode = "Y";
                if (!MiInstaller.IsAdministratorsGroup()) 
                {
                    uacMode = "Y"
                } else if ( !IsUACEnabled )  //uac off
                {
                    uacMode = "N"
                } else  if ( MiInstaller.IsElevatedProcess() ) // uac on && admin권한 상속
                    uacMode = "N";
            } 

            MiInstaller.UpdateURL = Server_Path+"install/update/update_cfg.jsp?UAC=" + uacMode; 
            var Bcnt = MiInstaller.ExistVersionUpCnt(); 

            if ( Bcnt )
            {
                MiInstaller.StartDownload();
            } else {
                fn_run();
            }

        }

    }

    function page_link()
    {
    
        // 단축 아이콘 만들기
        // UBKImage : 250 * 300
        
        //BYTE가 256을 넘으면 안됨..
        // 256을 넘을 경우 MiInstaller의 property를 이용할것
    
        var strCommand = '-V 3.2 -D Win32U -R FALSE -K '+keyName+' -L TRUE -LE TRUE -BI "%component%update_img.gif" ';
     
        //MiInstaller.MakeShortCut("%system%MiUpdater322.exe",strCommand, keyName,"%Component%/start_icon.ico","");
        MiInstaller.MakeShortCut("%system%MiUpdater322.exe",strCommand, "T.Key+","%Component%/start_icon1.ico","");
        
        // MakeShortCut 바로가기를 만드는 함수
        // strExeName: 바로가기를 만들 실행 파일 이름
        // strCommand: 바로가기를 만들 때 필요한 Command 정보
        // strShortcutName: 바로가기 파일 이름
        // strIConPath: 변경하려는 Icon 경로를 %alias%형태로 입력할 수 있습니다.
        // strPos: Startmenu-시작 / Desktop-바탕화면(Default)
    
        // Alias 참고
        // %MiPlatform%
        // %Component%
        // %system%
        // %Window%
        // %ProgramFiles%
    
    } 

    function fn_run()
    {
        if ( IsError ) return ;

        //단축 아이콘 생성
        page_link();

        if ( confirm("업데이트가 완료되었습니다. 실행하시겠습니까?") )
        {
        /*
        MiInstaller.AutoSize = false;    
        MiInstaller.Key = "TEST";
        MiInstaller.startxml = "c:\\html\\mip_310L\\CS_TEST_Win32.xml";
        MiInstaller.InitUrl = "Dataset::Grid_Property_AreaSelect.xml";
        MiInstaller.DoRun();
*/
            MiInstaller.run();
            opener = self;
            self.close();
        }
        else 
        {
            opener = self;
            self.close();   
            //window.location = "MiPlatform320.jsp";
        }

    }


    function progress_clear(obj) 
    {
        obj.width = 0;
        if ( obj.id == "progress" ) pg_cell_At = 0;
        else pg1_cell_At = 0;       
    }
    
    function progress_update(obj, cur_cnt) 
    {
        if ( obj.id == "progress" ) obj.width = cur_cnt;
        else obj.width = cur_cnt;
    }

    
</SCRIPT>

<SCRIPT language=JavaScript for=MiInstaller event=OnConfirm(ItemName)>
{
    
    var a;
    a  = "OnConfirm=>Item=";
    a += ItemName;  
    //alert(a);
}
</SCRIPT>

<SCRIPT LANGUAGE=javascript FOR=MiInstaller EVENT=OnProgress(ItemName,prgress,progressMax,StatusText)>
    if(ItemName != "")  {
        if( progressMax > 0 && prgress > 0 )    {
                var before_At = pg_cell_At;

                pg_cell_At = parseInt( ( ( ( GcurIndx - 1)/Gtotcnt ) * processWidth ) +  ( ( ( 1/Gtotcnt ) * processWidth ) * (prgress/progressMax) ) );
                if ( pg_cell_At > processWidth ) pg_cell_At = processWidth;
                        progress.width = pg_cell_At;
        }
    }
</SCRIPT>

<SCRIPT language=JavaScript for=MiInstaller event=OnStartDownLoad(VersionFileName,DownFileName,Type,TotalCnt,CurIndex)>
{
        if ( Type == 1 ) //EVENTCONFIG
        {
            //progress_clear(progress);
            //progress_clear(progress1);
        }
        else if ( Type == 2 ) 
        {
                progress.width = 0;
                Distproc.width = 0;
                comp_cnt.innerHTML = "&nbsp;" + CurIndex + "/" + TotalCnt;      
                //tot_ditem.innerHTML = "&nbsp;";
        
        } 
        else if ( Type == 3 ) //EVENTDOWNLOAD
        {
            GcurIndx = CurIndex;
            Gtotcnt = TotalCnt;
            comp_cnt.innerHTML = "&nbsp;" + CurIndex + "/" + TotalCnt;      
            
            var tpos = DownFileName.lastIndexOf("/") + 1;
            var Fname = DownFileName.substr(tpos,DownFileName.length - tpos);

            item_nm.innerHTML = "&nbsp;" + Fname;
        
            if ( prc_msg != "" && prc_msg != null && prc_msg != "undefined" ) prc_msg.innerHTML = "&nbsp;파일 다운로드 중....";
        }
        else if ( Type == 4 ) //EVENTDISTRIBUTE
        {
            //tot_ditem.innerHTML = "&nbsp;" + CurIndex + "/" + TotalCnt;       
            
            var tpos = DownFileName.lastIndexOf("/") + 1;
            var Fname = DownFileName.substr(tpos,DownFileName.length - tpos);

            item_nm.innerHTML = "&nbsp;" + Fname;
        
            if ( prc_msg != "" && prc_msg != null && prc_msg != "undefined" ) prc_msg.innerHTML = "&nbsp;실제 경로 배포 중....";
        }   
        

}


</SCRIPT>
 
<!--ItemSize는 미지정 -->
<SCRIPT language=JavaScript for=MiInstaller event=OnEndDownLoad(VersionFileName,DownFileName,Type,TotalCnt,CurIndex)>
{
        if ( Type == 1 ) //EVENTCONFIG
        {
            if ( !IsError ) 
            {

                //TotalVersionFileCnt = TotalCnt;
                progress_update(progress,processWidth);
                progress_update(Distproc,processWidth);
                if ( prc_msg != "" && prc_msg != null && prc_msg != "undefined" ) prc_msg.innerHTML = "&nbsp;설치 완료";
            
                fn_run();
                //history.back();
            }
            
        }
        else if ( Type == 3 )//EVENTDOWNLOAD
        {
            var before_At = pg_cell_At;
                    pg_cell_At = parseInt( ( (CurIndex)/TotalCnt ) * processWidth );
                    progress_update(progress,pg_cell_At);
            
            if ( IsError ) {
                    t_err.className = "show";
                    reinstall.className = "show";
            }       
        
            if ( prc_msg != "" && prc_msg != null && prc_msg != "undefined" ) prc_msg.innerHTML = "&nbsp;파일 다운로드 완료....";
        }
        else if ( Type == 4 )//EVENTDISTRIBUTE
        {
            var before_At = pg1_cell_At;
                pg1_cell_At = parseInt( ( (CurIndex)/TotalCnt ) * processWidth );
                    progress_update(Distproc,pg1_cell_At);
                    
            if ( IsError ) {
                    t_err.className = "show";
                    reinstall.className = "show";
            }       
            
            if ( prc_msg != "" && prc_msg != null && prc_msg != "undefined" ) prc_msg.innerHTML = "&nbsp;실제 경로 배포 완료";
        }


}
</SCRIPT>
<SCRIPT language=JavaScript for=MiInstaller event=OnError(ItemName,ErrorCode,ErrorMsg)>
{
        IsError = true;
        err_msg.innerHTML += '<font class="f2" color="red">' + ItemName + '&nbsp;다운&nbsp;실패&nbsp; -- ErrorCode:' + ErrorCode + ' ' + ErrorMsg + "<br>";
        t_err.className = "show";
        reinstall.className = "show";
        MiInstaller.stop();

}

</SCRIPT>


<SCRIPT language=JavaScript>
{
    function f_selfDown(){
        window.location.href = Server_Path+"MiPlatform_SetupDeploy320U_200901.exe";
    }

}

</SCRIPT>



</HEAD>
<BODY leftmargin="0" topmargin="0" onload="fn_OnLoad()">
<br><br>
<center>
  <table id="ins_tbl" width="637" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><img src="image/insu_down_img01.gif" width="20" height="136"></td>
    <td><img src="image/insu_down_img02.gif" width="597" height="136"></td>
    <td><img src="image/insu_down_img03.gif" width="20" height="136"></td>
  </tr>
  <tr>
    <td><img src="image/insu_down_img04.gif" width="20" height="138"></td>
    <td height="138" align="center" background="image/insu_down_img05.gif"><table width="570" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td><table width="570" border="0" align="center" cellpadding="0" cellspacing="0" style="margin-top:8px">
            <tr>
              <td width="10" valign="top"><img src="image/insu_down_list_icon1.gif" width="8" height="11"></td>
              <td width="570">암호화 프로그램 설치여부를 묻는 보안경고창이 나타나면 반드시 
                “<strong><font color="#FF7200">예</font></strong>”를 
                선택하여주시기 바랍니다.<br>
                “아니오”를 선택하시면 보안을 위해 사용이 제한됩니다.</td>
            </tr>
        </table></td>
      </tr>
      <tr>
        <td><table width="570" border="0" align="center" cellpadding="0" cellspacing="0" style="margin-top:5px">
            <tr>
              <td width="10" valign="top"><img src="image/insu_down_list_icon1.gif" width="8" height="11"></td>
              <td width="570">윈도우 XP 서비스팩2 사용자께서는 주소 표시줄 아래 경고문구 
                “ <img src="image/insu_down_list_icon2.gif" width="15" height="15" align="absmiddle">이 
                사이트에서...여기를 클릭하십<br>
                시오” 를 선택하시어, Active X컨트롤을 설치하시기 바랍니다.</td>
            </tr>
        </table></td>
      </tr>
      <tr>
        <td><table width="570" border="0" align="center" cellpadding="0" cellspacing="0" style="margin-top:5px">
            <tr>
              <td width="10" valign="top"><img src="image/insu_down_list_icon1.gif" width="8" height="11"></td>
              <td width="570">프로그램 설치가 정상적이지 않을 경우에는 다음과 같이 하여 주십시오.</td>
    <!--
              <td width="570">프로그램 설치가 정상적이지 않을 경우에는 <strong><a href="javascript:f_selfDown()"><font color="#FF7200">수동설치</font></strong></a>를 
                통해 수동설치하시거나 <br><strong><font color="#FF7200">pskey@skpsnm.com</font></strong>으로
                질문하시면 신속히 답변을 드리겠습니다.</td>
    -->
            </tr>
            <tr>
              <td width="10" valign="top"></td>
              <td width="570">&nbsp;&nbsp;&nbsp;- 시작 > 제어판 > 프로그램 추가/제거 > MiPlatform_ 로 시작하는 프로그램 모두 삭제</td>
            </tr>
        </table></td>
      </tr>
      <tr>
        <td><table width="570" border="0" align="center" cellpadding="0" cellspacing="0" style="margin-top:5px">
            <tr>
              <td width="10" valign="top"><img src="image/insu_down_list_icon1.gif" width="8" height="11"></td>
              <td width="570">홈페이지를 통해 프로그램 설치가 안 될 경우에는 <strong><a href="javascript:f_selfDown()"><font color="#FF7200">수동설치</font></strong></a>하여 주십시오.</td>
            </tr>
        </table></td>
      </tr>
      <tr>
        <td><table width="570" border="0" align="center" cellpadding="0" cellspacing="0" style="margin-top:2px">
            <tr>
              <td width="10" valign="top"></td>
              <td width="570"></td>
            </tr>
        </table></td>
      </tr>
    </table></td>
    <td><img src="image/insu_down_img06.gif" width="20" height="138"></td>
  </tr>
  <tr>
    <td><img src="image/insu_down_img07.gif" width="20" height="16"></td>
    <td><img src="image/insu_down_img08.gif" width="597" height="16"></td>
    <td><img src="image/insu_down_img09.gif" width="20" height="16"></td>
  </tr>
</table>
</center>
<table  id="ins_tbl" align="center" border=0 width="600" style="margin-top:10px">
<tr>
    <td>
    <table border="0" cellpadding="0" cellspacing="0" >

        <tr>
            <td> <img src="image/bul_tit.gif" width="15" height="25" align="absmiddle"><span style="color:#014FAB; font-weight:bold">파일다운로드 진행 상황</span></td>
            <td id="comp_cnt" class="f2" align=left NOWRAP>&nbsp;</td>
        </tr>
    </table>    </td>
</tr>
<tr>
<td valign="middle"  >
    <div  valign="middle" style="font-size:3px;padding:1px;border:1px GROOVE silver;">
    <IMG ID="progress" SRC='image/installbar.jpg' WIDTH=0% HEIGHT=11 >  </div></td>
</tr>
<tr>
    <td>
    <table border="0" cellpadding="0" cellspacing="0" style="margin-top:5px">
        <tr>
        <td ><img src="image/bul_tit.gif" width="15" height="25" align="absmiddle"><span style="color:#014FAB; font-weight:bold">파일설치 진행 상황</span></td>
            <td id="comp_cnt" class="f2" align=left NOWRAP>&nbsp;</td>
        </tr>
    </table>    </td>       
</tr>
<tr>
    <td valign="middle" >
    <div  style="font-size:3px;padding:1px;border:1px GROOVE silver;">
    <IMG ID="Distproc" SRC='image/installbar.jpg' WIDTH=0% HEIGHT=10 >  </div>  </td>
</tr>
<tr>
    <td>
    <table border="0" cellpadding="0" cellspacing="0" style="margin-top:5px">
        <tr>
            <td><img src="image/bul_tit.gif" width="15" height="25" align="absmiddle"><span style="color:#014FAB; font-weight:bold">대상파일</span></td>
            <td id="item_nm" class="f2" align=left NOWRAP>&nbsp;</td>
        </tr>
    </table>    </td>       
</tr>
<tr>
  <td id=prc_msg class="f2" ></tr>  
</table>
<table width="600" align=center class="hide" id=t_err>
    <tr>
        <td><img src="image/bul_tit.gif" width="15" height="25" align="absmiddle"><span style="color:#014FAB; font-weight:bold">설치시 에러가 발생한 항목</span></td>
    </tr>
    <tr>
        <td id=err_msg class="f2">&nbsp;</td>
    </tr>
</table>
<SCRIPT LANGUAGE="JavaScript">
    CreateMiInstlr("MiInstaller","Win32U","3.2", keyName);  
</SCRIPT>
</BODY>
</HTML>