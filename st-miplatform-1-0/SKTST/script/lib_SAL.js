﻿﻿???/*******************************************************************************
????????????????????* 프로그램ID : lib_SAL
* 업무명         : 영업
* 프로그램명  : 공통 스크립트
--------------------------------------------------------------------------------
* 작성자      : 정재열
* 작성일      : 2009.02.11
--------------------------------------------------------------------------------
* 1. 변경이력
수정자    :
수정일    :
수정 내역 :
*******************************************************************************/


/*******************************************************************************
 * @description    :  정산처 가져오기
 * @param sInDs   : 조건 Dataset
 * @param sOutDs   : 결과 받을 Dataset
 * @param sOutDs   :  거래처 코드
*******************************************************************************/
function sf_getStlPlc( sInDs, sOutDs, sPlc, sSaleDtm)
{
	var oInDs = Object(sInDs);

	oInDs.Clear();	
	cf_setParam(oInDs, "deal_co_cd", sPlc);
	cf_setParam(oInDs, "deal_dtm"  , sSaleDtm);

	// 트랜잭션 설정
	var sSvcID = "svcStlPlc";
	var sInDs  = "nc_input_fieldDs=" + sInDs;
	var sOutDs = sOutDs + "=List";
	var sArg   = "sktst.sal.SCO#getStlPlcByDealCoCd"; 
	
	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	
/*	리턴 구조체ukey_agency_cd
<Contents>
	<colinfo id="STL_PLC" size="255" summ="default" type="STRING"/>
	<colinfo id="STL_PLC_NM" size="255" summ="default" type="STRING"/>
	<colinfo id="ukey_agency_cd" size="255" summ="default" type="STRING"/>  추가 : 20090305
	<colinfo id="ukey_sub_cd" size="255" summ="default" type="STRING"/>     추가 : 20090310
	<colinfo id="ukey_channel_cd" size="255" summ="default" type="STRING"/> 추가 : 20090310
	<colinfo id="deal_end_yn" size="256" summ="default" type="STRING"/>
	<colinfo id="pay_stop_yn" size="256" summ="default" type="STRING"/>
	<colinfo id="sale_stop_yn" size="256" summ="default" type="STRING"/>
	<colinfo id="out_stop_yn" size="256" summ="default" type="STRING"/>
	<colinfo id="pos_agency" size="256" summ="default" type="STRING"/> 추가 : 20090324
	<colinfo id="pos_agency_nm" size="256" summ="default" type="STRING"/> 추가 : 20090430
	
</Contents>
*/
}


/*******************************************************************************
 * @description    : 담당자 리스트
 * @param sInDs   : 조건 Dataset
 * @param sOutDs   : 결과 받을 Dataset
 * @param sOrgArea   : 근무지 <소매>
 * @param sSalePlc   : 판매처 <도매>
 * @param sSaleDtm   : 판매일자 <도매>
*******************************************************************************/
function sf_getChgrgIdList(sInDs, sOutDs, sOrgArea, sSalePlc, sSaleDtm)
{
	var oInDs = Object(sInDs);
	oInDs.Clear();

	// 넘길 파라메터 설정
	cf_setParam(oInDs, "org_area", uf_null2space(sOrgArea)); 
	cf_setParam(oInDs, "sale_plc", uf_null2space(sSalePlc)); 
	cf_setParam(oInDs, "sale_dtm", uf_null2space(sSaleDtm)); 
	
	// 트랜잭션 설정
	var sArg;
	var sSvcID = "svcChrgrIdList";
	var sInDs  = "nc_input_fieldDs="+sInDs;
	var sOutDs = sOutDs + "=List";

	if(uf_isNull(sOrgArea) == true
		&& uf_isNull(sSalePlc) == true
		&& uf_isNull(sSaleDtm) == true)
	{	// 모든 담당자
		sArg   = "sktst.sal.SCO#getAllChgrgIdList";   
	}
	else
	{	// 소매,도매,온라인소매,온라인도매
		sArg   = "sktst.sal.SCO#getChrgrIdList";
	}

	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	
/*
<Contents>
	<colinfo id="user_id" size="256" summ="default" type="STRING"/>
	<colinfo id="user_nm" size="256" summ="default" type="STRING"/>
</Contents>	
*/				
}



/*******************************************************************************
 * @description    :  조직 리스트
 * @param sInDs   : 조건 Dataset
 * @param sOutDs   : 결과 받을 Dataset
 * @param sSupOrgId   :  조직 상위그룹
*******************************************************************************/
function sf_getCenterList( sInDs, sOutDs)
{
	var oInDs = Object(sInDs);
	
	oInDs.Clear();

	// 트랜잭션 설정
	var sSvcID = "svcCenterList";
	var sInDs  = "nc_input_fieldDs=" + sInDs;
	var sOutDs = sOutDs + "=List";
	var sArg   = "sktst.sal.SCO#getCenterList";
	
	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	
/*
<Contents>
	<colinfo id="ORG_ID" size="255" type="STRING"/>
	<colinfo id="ORG_NM" size="255" type="STRING"/>
</Contents>
*/
}


/*******************************************************************************
 * @description    :  상품 리스트 
 * @param sInDs   : 조건 Dataset
 * @param sOutDs   : 결과 받을 Dataset
 * @param sMfactId :  제조사 코드
 * @param sSProdCl :  상품구분 (생략가능)
*******************************************************************************/
function sf_getMdlList(sInDs, sOutDs, sMfactId, sSProdCl)
{
	var oInDs = Object(sInDs);

	oInDs.Clear();
	
	cf_setParam(oInDs, "mfact_id", uf_null2space(sMfactId));	// 제조사
	cf_setParam(oInDs, "prod_cl" , uf_null2space(sSProdCl));	// 상품구분
	
	// 트랜잭션 설정
	var sSvcID = "svcMdlList";
	var sInDs  = "nc_input_fieldDs=" + sInDs;
	var sOutDs = sOutDs + "=List";
	var sArg   = "sktst.sal.SCO#getProdList";
	
	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
/*
<Contents>
	<colinfo id="PROD_CD" size="255" type="STRING"/>
	<colinfo id="PROD_NM" size="255" type="STRING"/>
</Contents>	
*/
}


/*******************************************************************************
 * @description    :  운용단가표의 상품 리스트 
 * @param sInDs   : 조건 Dataset
 * @param sOutDs   : 결과 받을 Dataset
 * @param sMfactId :  제조사 코드
 * @param sSProdCl :  상품구분 (생략가능)
*******************************************************************************/
function sf_getUplMdlList(sInDs, sOutDs, sMfactId, sSProdCl)
{
	var oInDs = Object(sInDs);

	oInDs.Clear();
	
	cf_setParam(oInDs, "mfact_id", sMfactId);	// 제조사
	cf_setParam(oInDs, "prod_cl" , sSProdCl);	// 상품구분
	
	// 트랜잭션 설정
	var sSvcID = "svcUplMdlList";
	var sInDs  = "nc_input_fieldDs=" + sInDs;
	var sOutDs = sOutDs + "=List";
	var sArg   = "sktst.sal.SCO#getUplstMdlList";
	
	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
/*
<Contents>
	<colinfo id="PROD_CD" size="255" type="STRING"/>
	<colinfo id="PROD_NM" size="255" type="STRING"/>
</Contents>	
*/
}

/*******************************************************************************
 * @description    :  상품 색상 리스트
 * @param sInDs   : 조건 Dataset
 * @param sOutDs   : 결과 받을 Dataset
 * @param sMdlCd  :  모델 코드
 * @param sSaleDtm :  판매일자
*******************************************************************************/
function sf_prodColorList(sInDs, sOutDs, sMdlCd)
{
	var oInDs = Object(sInDs);

	oInDs.Clear();
	
	cf_setParam(oInDs, "prod_cd", uf_null2space(sMdlCd));	// 상품모델
	
	// 트랜잭션 설정
	var sSvcID = "svcColorList";
	var sInDs  = "nc_input_fieldDs=" + sInDs;
	var sOutDs = sOutDs + "=ProdColorList";
  var sArg = "sktst.dis.DCO#getProdColorList";
	
	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
/*
<Contents>
	<colinfo id="COLOR_CD" size="255" type="STRING"/>
	<colinfo id="COLOR_CD_NM" size="255" type="STRING"/>
</Contents>	
*/	
}




/*******************************************************************************
 * @description    :  운용단가표 취득
 * @param sInDs   : 조건 Dataset
 * @param sOutDs   : 결과 받을 Dataset
 * @param sMdlCd  :  모델 코드
 * @param sSaleDtm :  판매일자 
*******************************************************************************/
function sf_getUplst(sInDs, sOutDs, sMdlCd, sSaleDtm)
{
	var oInDs = Object(sInDs);
	
	oInDs.Clear();
	
	cf_setParam(oInDs, "mdl_id"  , sMdlCd);		// 상품모델
	cf_setParam(oInDs, "sale_dtm", sSaleDtm);		// 상품구분
	
	// 트랜잭션 설정
	var sSvcID = "svcUplst";
	var sInDs  = "nc_input_fieldDs=" + sInDs;
	var sOutDs = sOutDs + "=List";
	var sArg   = "sktst.sal.SCO#getUplst";
	
	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	
/*
<Contents>
	<colinfo id="EXIT_CASH_PRCHS_PRC" size="15" type="DECIMAL"/>
	<colinfo id="EXIT_CRDT_PRCHS_PRC" size="15" type="DECIMAL"/>
	<colinfo id="EXIT_STRD_SALE_PRC" size="15" type="DECIMAL"/>
	<colinfo id="FIX_CASH_PRCHS_PRC" size="15" type="DECIMAL"/>
	<colinfo id="FIX_CRDT_PRCHS_PRC" size="15" type="DECIMAL"/>
	<colinfo id="FIX_STRD_SALE_PRC" size="15" type="DECIMAL"/>
</Contents>
*/
}



/*******************************************************************************
 * @description    :  판매취소입고
 * @param sInDs   : 조건 Dataset
 * @param sOutDs   : 결과 받을 Dataset
 * @param sMdlCd  :  모델 코드
 * @param sSaleDtm :  판매일자 
*******************************************************************************/
function sf_getInDisSale(sInDs, sOutDs, sMdlCd, sSaleDtm)
{
	var oInDs = Object(sInDs);
	
	oInDs.Clear();
	
	// 트랜잭션 설정
	var sSvcID = "getInDisSale";
	var sInDs  = "nc_input_fieldDs=" + sInDs;
	var sOutDs = sOutDs + "=List";
	var sArg   = "sktst.sal.SCO#addInDisSale";
	
	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	
/*
	cf_setParam(oInDs, "act_flag"         , sActFlag);		// 업무처리구분 (S:예정등록,F:확정등록,SI:판매취소입고,EI:기기교환입고)
	cf_setParam(oInDs, "gnrl_sale_no"     , sGnrlSaleNo);		// 일반판매번호                                                        
	cf_setParam(oInDs, "gnrl_sale_chg_seq", sGnrlSaleChgSeq);		// 일반판매변경순번                                                    
	cf_setParam(oInDs, "in_schd_dt"       , sInSchdDt);		// 입고예정일자                                                        
	cf_setParam(oInDs, "in_plc_id"        , sInPlcId);		// 입고처ID                                                            
	cf_setParam(oInDs, "prod_cl"          , sProdCl);		// 상품구분                                                            
	cf_setParam(oInDs, "prod_cd"          , sProdCd);		// 상품코드                                                            
	cf_setParam(oInDs, "color_cd"         , sColorCd);		// 색상코드                                                            
	cf_setParam(oInDs, "bad_yn"           , sBadYn);		// 불량여부                                                            
	cf_setParam(oInDs, "dis_st"           , DisSt);			// 재고상태                                                            
	cf_setParam(oInDs, "ser_num"          , sSerNum);		// 일련번호                                                            
	cf_setParam(oInDs, "in_qty"           , sInQty);		// 입고수량  
*/
}


/*******************************************************************************
 * @description    :  판매 출고
 * @param sInDs   : 조건 Dataset
 * @param sOutDs   : 결과 받을 Dataset
 * @param sMdlCd  :  모델 코드
 * @param sSaleDtm :  판매일자 
*******************************************************************************/
function sf_getDisSaleOut(sInDs, sOutDs, sMdlCd, sSaleDtm)
{
	var oInDs = Object(sInDs);
	
	oInDs.Clear();
	
	// 트랜잭션 설정
	var sSvcID = "getDisSaleOut";
	var sInDs  = "nc_input_fieldDs=" + sInDs;
	var sOutDs = sOutDs + "=List";
	var sArg   = "sktst.sal.SCO#addDisSaleOut";
	
	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	
/*
	cf_setParam(oInDs, "act_flag"          , sActFlag);		// 업무처리구분 (S:예정등록,F:확정등록,SO:판매출고,EO:기기교환출고,CO:교품반품출고)
	cf_setParam(oInDs, "gnrl_sale_no"      , sActFlag);		// 일반판매번호                                                                    
	cf_setParam(oInDs, "gnrl_sale_chg_seq" , sActFlag);		// 일반판매변경순번                                                                
	cf_setParam(oInDs, "out_schd_dt"       , sActFlag);		// 출고예정일자                                                                    
	cf_setParam(oInDs, "out_plc_id"        , sActFlag);		// 출고처ID                                                                        
	cf_setParam(oInDs, "prod_cl"           , sActFlag);		// 상품구분                                                                        
	cf_setParam(oInDs, "prod_cd"           , sActFlag);		// 상품코드                                                                        
	cf_setParam(oInDs, "color_cd"          , sActFlag);		// 색상코드                                                                        
	cf_setParam(oInDs, "bad_yn"            , sActFlag);		// 불량여부                                                                        
	cf_setParam(oInDs, "dis_st"            , sActFlag);		// 재고상태                                                                        
	cf_setParam(oInDs, "ser_num"           , sActFlag);		// 일련번호                                                                        
	cf_setParam(oInDs, "out_qty"          , sActFlag);		// 출고수량                                                                        
*/
}


/*******************************************************************************
 * @description    :  부가서비스리스트 취득
 * @param sOutDs   : 결과 받을 Dataset
*******************************************************************************/
function sf_getSuppSvcList(sOutDs)
{	
	
	// 트랜잭션 설정
	var sSvcID = "svcSuppSvcList";
	var sInDs  = "";
	var sOutDs = sOutDs + "=List";
	var sArg   = "sktst.sal.SCO#getSupSvcList";
	
	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	
/*
<Contents>
	<colinfo id="SUPL_SVC_CD" size="255" type="STRING"/>
	<colinfo id="SUPL_SVC_NM" size="255" type="STRING"/>
</Contents>
*/
}


/*******************************************************************************
 * @description    :  로컬 현재 날짜로 시간 셋팅
 * @param sCalendar   :  Calendar Control Name
 * @param sEdtHour   :  Hour Edit Control Name
 * @param sEdtMinute  :  Minute Control Name
*******************************************************************************/
function sf_setDate(sCalendar,sEdtHour, sEdtMinute)
{
	var oCalendar = Object(sCalendar);
	var oEdtHour  = Object(sEdtHour);
	var oEdtMinute= Object(sEdtMinute);

	var sDate = GetDate();
	oCalendar.Value = SubStr(sDate, 0,8);
	oEdtHour.Text   = SubStr(sDate, 8,2);
	oEdtMinute.Text = SubStr(sDate, 10,2);
}


/*******************************************************************************
 * @description    :  DaataSet 값으로 시간 셋팅
 * @param sCalendar   :  Calendar Control Name
 * @param sEdtHour   :  Hour Edit Control Name
 * @param sEdtMinute  :  Minute Control Name 
 * @param sValue  :  설정 값 
*******************************************************************************/
function sf_setDateByDataSet(sCalendar,sEdtHour, sEdtMinuts, sValue)
{
	var oCalendar = Object(sCalendar);
	var oEdtHour  = Object(sEdtHour);
	var oEdtMinute= Object(sEdtMinuts);

	if( uf_IsNull(sValue) == true)
	{
		oCalendar.Value = "";
		oEdtHour.Text   = "";
		oEdtMinute.Text = "";		
	}
	else
	{
		oCalendar.Value = SubStr(sValue, 0,8);
		oEdtHour.Text   = SubStr(sValue, 8,2);
		oEdtMinute.Text = SubStr(sValue, 10,2);	
	}
}


/*******************************************************************************
 * @description    :  컨트롤 활성화 / 비활성화
 * @param sControls   :  Controls 
 * @param bEnable   :  활성화 여부
*******************************************************************************/
function sf_enable(sControls, bEnable)
{
	var arrControl = Split(sControls, ",");
	var nCntCntrl = arrControl.length();
	
	for(var i=0; i<nCntCntrl; i++)
	{
		var oCntrl = eval(arrControl[i]);
		oCntrl.Enable = bEnable;
	}
}

/*******************************************************************************
 * @description    :  컨트롤 초기화
 * @param sControls   :  Controls 
*******************************************************************************/

function sf_clearControls(sControls)
{
	var arrControl = Split(sControls, ",");
	var nCntCntrl = arrControl.length();
	
	for(var i=0; i<nCntCntrl; i++)
	{
		var oCntrl = eval(arrControl[i]);
		if(oCntrl == null) 
		{
//			alert(arrControl[i] + "존재하지 않는 컨트롤입니다.");
			return;
		}
		oCntrl.Text = "";
	}
}



/*******************************************************************************
 * @description    :  지정한 로우의 값이 변경된 필드명 리턴
 * @param sDsName   :  Dataset Name  
 * @param nRow   :  Dataset Row
*******************************************************************************/
function sf_checkRowChangeField(sDsName, nRow)
{
	var sChangeField = "";
	var oDataSet = Object(sDsName);
	var sRowType = oDataSet.GetRowType(nRow);
	
	if(oDataSet.GetUpdate() == false) return "";
	if( sRowType == "normal") return "";
	
	var nCntCol = oDataSet.GetColCount();
	for(var i=0; i < nCntCol;i++)
	{
		var sOrg = oDataSet.GetOrgColumn(nRow, i);
		var sSrc = oDataSet.GetColumn(nRow, i);
//		trace("=> sf_checkRowChangeField(" +sOrg + " / " + sSrc +")");
		if(sRowType == "insert")
		{
			sChangeField += "|" + "INSERT-ROW";
			break;
		}
		else
		{
			if(uf_isNull(sOrg) == false && uf_isNull(sSrc) == false)
			{
				if(sOrg != sSrc)
				{
					sChangeField += "|" + oDataSet.GetColID(i);
				}
			}		
		}
	}
//trace("sf_checkRowChangeField(" + nRow + ") : " + sChangeField);
	var nLength = Length(sChangeField);
	return Iif( nLength > 1,ToUpper(substr(sChangeField,1, nLength)),"");
}

/*******************************************************************************
 * @description    :  두 값을 비교하여 
					  좌측 값과 우측 값이 동일 할 경우 0
					  좌측 값이 클 경우 -1 
                      우측 값이 클 경우 1
*******************************************************************************/
function sf_compare(sData1, sData2)
{
	var nRetValue;
	if(sData1 == sData2)
	{
		nRetValue = 0;
	}
	else if(sData1 > sData2)
	{
		nRetValue = -1;
	}
	else
	{
		 nRetValue = 1;
	}
	return nRetValue;
}


/*******************************************************************************
 * @description     : 상품 팝업 2 - dataset binding 되어있을때 
-------------------------------------------------------------------------------- 
 * @param  dsParent : dataset 이름 (obiect id)
	       rowParent: dataset의  해당 Row (Value) 
	       aplyDt   : 적용일(yyyymmdd)
	       callFunction : 확인 버튼 클릭 시 실행 할 부모창의 function.
 * @return          : 없음
*******************************************************************************/
function sf_salProdPopDs(dsParent, rowParent, aplyDt, prod_cl, mfact_id, callFunction)
{
	 var sArg  = "dsParent='"+dsParent+"'";
		 sArg += " rowParent='"+rowParent+"'";
		 sArg += " prod_cl='"+prod_cl+"'";
		 sArg += " mfact_id='"+mfact_id+"'";		 
		 sArg += " aplyDt='"+aplyDt+"'";
		 sArg += " dsFlag=true";
		 sArg += " callFunction='"+callFunction+"'";
    
    dialog("SAL::SALSCO00900.xml", sArg , 400, 400);
}


/*******************************************************************************
 * @description     : 상품 팝업 2 - dataset binding 되어있을때 
-------------------------------------------------------------------------------- 
 * @param  dsParent : dataset 이름 (obiect id)
	       rowParent: dataset의  해당 Row (Value) 
	       aplyDt   : 적용일(yyyymmdd)
	       callFunction : 확인 버튼 클릭 시 실행 할 부모창의 function.
 * @return          : 없음
*******************************************************************************/
function sf_salProdPop(aplyDt, prod_nm, mfact_id, prod_cl, ds_Flag)
{
	 var sArg  = "prod_cl='"+prod_cl+"'";
		 sArg += " prod_nm='"+prod_nm+"'";		 
		 sArg += " mfactId='"+mfact_id+"'";		 
		 sArg += " aplyDt='"+aplyDt+"'";
		 sArg += " ds_Flag='"+ds_Flag+"'";
    
    dialog("SAL::SALSCO01300.xml", sArg , 400, 400);
}


/*******************************************************************************
 * @description     : 중복상품 팝업 - dataset binding 되어있을때 
*******************************************************************************/
function sf_salDupProdPop(nRow, sApyParentDs, sParentDs, sFunction)
{	
	 var sArg  = " parentDs='" + sParentDs + "'";
		 sArg += " apyParentDs='"+sApyParentDs+"'";
	     sArg += " row='"+nRow+"'";
		 sArg += " exeFunction='"+sFunction+"'";
    
    dialog("SAL::SALSCO01400.xml", sArg , 400, 400);
}


/*******************************************************************************
 * @description     : 부가상품 팝업
*******************************************************************************/
function sf_salSupProdPop(supProdNm)
{
	 var sArg  = "supProdNm='"+supProdNm+"'";
    
    dialog("SAL::SALSCO01500.xml", sArg , 400, 400);
}


/*******************************************************************************
 * @description     : 개통번호 얻어 지정한 컨트롤에 설정
-------------------------------------------------------------------------------- 
 * @param  sSvcNum1, sSvcNum2,sSvcNum3
 * @return          : 없음
*******************************************************************************/
function sf_getSvcNum(sSvcNum1,sSvcNum2,sSvcNum3, sTarget )
{
	if(uf_isNull(sTarget) == true) return sTarget;
	var sValue = uf_StripSpecialChar(sTarget);
	
	
	var nLength = Length(sValue);
	
	if(nLength != 10 && nLength != 11) return sTarget;
	
	var oSvcNum1 = object(sSvcNum1);
	var oSvcNum2 = object(sSvcNum2);
	var oSvcNum3 = object(sSvcNum3);
	
	if(oSvcNum1 == null || oSvcNum2 == null || oSvcNum3 == null)
	{
		alert("오브젝트를 찾지 못했습니다. \r\no SvcNum1:" + oSvcNum1 + "/oSvcNum2:" + oSvcNum2 + "/oSvcNum3:" + oSvcNum3);
		return;
	}
	
	oSvcNum1.Text = substr(sTarget, 0,3);
	
	if(nLength == 10)
	{
		oSvcNum2.Text = substr(sTarget, 3,3);
		oSvcNum3.Text = substr(sTarget, 6,4);
	}
	else
	{
		oSvcNum2.Text = substr(sTarget, 3,4);
		oSvcNum3.Text = substr(sTarget, 7,4);	
	}
}

/*******************************************************************************
 * @description    : 데이타셋에서 코드 값의 코드명을 가져와 텍스트 박스 넣어준다.
-------------------------------------------------------------------------------- 
 * @param          : oDsCmb   - 코드 DataSet
 *                   sEdt     - 코드명을 넣어줄 edit box
 *                   sFindCode- 찾을 코드값
 *                   sTyp     - 1 : 기타 사용자 정의 테이블,   기타 : 공통코드테이블, 
 *                   sCdVal   - 코드 DataSet의 코드 컬럼
 *                   sCdValNm - 코드 DataSet의 코드명 컬럼
*******************************************************************************/
function sf_findCmbDsCdNm(oDsCmb, sEdt, sFindCode, sTyp, sCdVal, sCdValNm )
{
	var oEdt = Object(sEdt);

    if ( sTyp != "1" )
    {
        sCdVal   = "comm_cd_val";
        sCdValNm = "comm_cd_val_nm";
    }

    oEdt.Text = oDsCmb.getColumn(oDsCmb.FindRow(sCdVal,sFindCode), sCdValNm);
}


/*******************************************************************************
 * @description     : 개통번호 얻어 지정한 컨트롤에 설정
-------------------------------------------------------------------------------- 
 * @param  sSvcNum1, sSvcNum2,sSvcNum3
 * @return          : 없음
*******************************************************************************/
function sf_getSvcNumToSecure(sSvcNum1,sSvcNum2,sSvcNum3, sTarget )
{
	if(uf_isNull(sTarget) == true) return sTarget;
	var sValue = uf_StripSpecialChar(sTarget);
	
	
	var nLength = Length(sValue);
	
	if(nLength != 10 && nLength != 11) return sTarget;
	
	var oSvcNum1 = object(sSvcNum1);
	var oSvcNum2 = object(sSvcNum2);
	var oSvcNum3 = object(sSvcNum3);
	
	if(oSvcNum1 == null || oSvcNum2 == null || oSvcNum3 == null)
	{
		alert("오브젝트를 찾지 못했습니다. \r\no SvcNum1:" + oSvcNum1 + "/oSvcNum2:" + oSvcNum2 + "/oSvcNum3:" + oSvcNum3);
		return;
	}
	
	var sSvcNum1= substr(sTarget, 0,3);
	var sSvcNum2 = "";
	var sSvcNum3 = "";		
	
	if(nLength == 10)
	{
		sSvcNum2 = substr(sTarget, 3,3);
		sSvcNum3 = substr(sTarget, 6,4);
	}
	else
	{
		sSvcNum2 = substr(sTarget, 3,4);
		sSvcNum3 = substr(sTarget, 7,4);	
	}	
	
	oSvcNum1.Text = sSvcNum1;
	oSvcNum2.Text = sf_formatString(sSvcNum2, "##**");
	oSvcNum3.Text = sf_formatString(sSvcNum3, "#***");
	//alert(sf_formatString(sSvcNum2, "##**"));
                    
}

/*******************************************************************************
 * @description     : 구분에 따라 마킹 처리
*******************************************************************************/
function sf_getMasking(strSrc, sType)
{
	var strFormat = "";
	switch(sType.ToLower())
	{
	case "custbiznum":	// 주민번호
		strFormat = "####**-*******";
		break;
		
	case "svcnum":	// 개통번호
		strFormat = "###-##**-#***";
		break;
	
	case "custnm":	// 성명
		strFormat = "#*##############";
		break;
	
	case "sernum":
		strFormat = "##*************";
		break;
	}
	return sf_formatString( strSrc, strFormat );
}

/*******************************************************************************
 * @description     : 지정 포멧팅 설정
-------------------------------------------------------------------------------- 
 * @param  sSvcNum1, sSvcNum2,sSvcNum3
 * @return          : 없음
*******************************************************************************/
function sf_formatString( strSrc, strFormat )
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
 * @description    :  카드사 취득
*******************************************************************************/
function sf_getCardCorpList(sInDs, sOutDs, sSaleDtm, sSvcId)
{
	var oInDs = Object(sInDs);
	
	oInDs.Clear();
	
	cf_setParam(oInDs, "sale_dtm", sSaleDtm);
	
	if(uf_isNull(sSvcId) == true)
	{
		sSvcId = "svcCardCorpList";
	}
	// 트랜잭션 설정
	var sSvcID = sSvcId;
	var sInDs  = "nc_input_fieldDs=" + sInDs;
	var sOutDs = sOutDs + "=List";
	var sArg   = "sktst.sal.SCO#getCardCorpList";
	
	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	
/*
<Contents>
	<colinfo id="deal_co_cd" size="255" type="STRING/>
	<colinfo id="deal_co_nm" size="255" type="STRING/>
</Contents>
*/
}

/*******************************************************************************
 * @description    :  메뉴로 거래처 구분 취득
*******************************************************************************/
function sf_getSlClByScreenID()
{
	var retValue = "";
	var sIdx = gds_menu.FindRow("MENUNUM", pFrmNum);	
	var sSupMenuNum = gds_menu.GetColumn(sIdx, "SUPMENUNUM");
	
	switch(sSupMenuNum)
	{
	// 영업공통 : 영업관리
	case "100000450":  	// 영업관리 (리얼)
	case "100000327":	// 영업공통
	case "100000329":	// 영업관리현황
	case "100000476":	// 경영현황 > 영업현황	
		retValue = "00";
		break;
	
	// 소매영업
	case "100000073":	// 소매영업
	case "100000074":	// 일반판매
	case "100000079":	// 부가상품판매
	case "100000081":	// SKT수납대행
	case "100000321":	// 공기기판매	
		retValue = "01";
		break;
		
	// 도매영업
	case "100000085":	// 도매영업
	case "100000086":	// 일반판매
	case "100000091":	// 부가상품판매
	case "100000093":	// SKT수납대행
	case "100000322":	// 공기기판매
		retValue = "02";
		break;

	// 온라인 소매영업
	case "100000206":	// 온라인소매영업
	case "100000352":	// 일반판매
	case "100000356":	// 부가상품판매
	case "100000355":	// SKT수납대행
	case "100000354":	// 공기기판매
		retValue = "03";
		break;

	// 온라인 도매영업
	case "100000280":	// 온라인도매영업
	case "100000364":	// 일반판매
	case "100000359":	// 부가상품판매
	case "100000358":	// SKT수납대행
	case "100000357":	// 공기기판매
		retValue = "04";
		break;
				
	case "100000490":	// 판매점관리 > 영업현황(판매점MM은 도매매로 본다)
		retValue = "01-1";
		break;
		
	// M Channel 영업 
	case "100000622":	// 온라인도매영업
	case "100000623":	// 일반판매
	case "100000624":	// 부가상품판매
	case "100000625":	// SKT수납대행
	case "100000626":	// 공기기판매
		retValue = "05";
		break;
				
	default:
		alert(pFrmNum + "확인되지 않은 메뉴 ID입니다.");
		retValue = "";
		break;
	}
	
	return retValue;
}



/*******************************************************************************
 * @description    :  메뉴로 거래처 구분 취득
*******************************************************************************/
function sf_getDealCoCl1(oGubun)
{
	var retValue = "A2,A3,B1,B2,C1";
	var FV_USER_GRP = gds_session.GetColumn(0, "userGrp");	// 소속구분;	// kimys
	
	if(uf_isNull(oGubun) == true) oGubun = false;
	
	if(oGubun == true) return retValue;
	
	if(oGubun == "OF") return "A2,A3,C1";	// 오프라인
	if(oGubun == "ON") return "B1,B2";	// 온라인
	if(oGubun == "R") return "A2,B1";	// 소매
	if(oGubun == "A") return "A3,B2";	// 도매
		
	switch(sf_getSlClByScreenID())
	{
	// 영업관리
	case "00":
		retValue = "A2,A3,B1,B2,C1";
		break;
	
	// 소매영업
	case "01":	// 소매영업
		retValue = "A2";
		break;
		
	// 판매점 MM
	case "01-1":	// 판매점 MM은 소매로 처리
		if(FV_USER_GRP = "P12") {
			retValue = "A2,A3,B1,B2,C1";
		} else {
			retValue = "A3";
		}
		break;

		
	// 도매영업
	case "02":	// 도매영업
		retValue = "A3";
		break;

	// 온라인 소매영업
	case "03":	
		retValue = "B1";
		break;

	// 온라인 소매영업
	case "04":	
		retValue = "B2";
		break;

	case "05":	
		retValue = "C1";
		break;

	default:
		retValue = "";
		break;
	}
	
	return retValue;
}


/*******************************************************************************
 * @description    : 현재월의 1일을 리턴한다.
 * @return         : 현재월1일
*******************************************************************************/
function sf_getFirstDay()
{
	var strDate = GetDate();
	
	return Mid(strDate,0,6) + "01";	 
}



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



/*******************************************************************************
 * @description    :  콤보박스 값 설정
*******************************************************************************/
function sf_setComboValue(oCmbCntrl, sValue)
{
	if(uf_isNull(sValue) == true)
	{
		oCmbCntrl.index = 0;
	}
	else
	{
		oCmbCntrl.Value = sValue;
	}
}


/*******************************************************************************
 * @description    :  대리점 / 판매처 권한에 따라 컨트롤 제어하기
*******************************************************************************/
function sf_setAgencyDealPlcBySession(sDiv, sAgencyControls, sDealControls)
{
	alert('sf_setAgencyDealPlcBySession 사용중입니다. 삭제하세요.');
}


/*******************************************************************************
 * @description    :  거래처구분코드를 영업구분코드로 변환
*******************************************************************************/
function sf_convertDealCoClToSlCl(sDealCoCl)
{
	var sRetValue;
	switch(sDealCoCl)
	{
	case "A2":
	case "A4":	// A4일 경우 소매로 처리	
		sRetValue = "01";
		break;

	case "A3":
		sRetValue = "02";
		break;

	case "B1":	
		sRetValue = "03";
		break;

	case "B2":	
		sRetValue = "04";
		break;

	case "C1":	
		sRetValue = "05";
		break;
	
	default:
		break;
	}
	return sRetValue;
}
/*******************************************************************************
 * @description    :  영업구분코드에 다라 거래처구분코드로 변환
*******************************************************************************/
function sf_convertSlClToDealCoCl(sDealCoCl)
{
	var sRetValue;
	switch(sDealCoCl)
	{
	case "01":
		sRetValue = "A2";
		break;

	case "02":
		sRetValue = "A3";
		break;

	case "03":	
		sRetValue = "B1";
		break;

	case "04":	
		sRetValue = "B2";
		break;

	case "05":	
		sRetValue = "C1";
		break;

	default:
		sRetValue = "A2,A3,B1,B2,C1";
		break;
	}
	return sRetValue;
}


/*******************************************************************************
 * @description    :  거래처 팝업의 리턴 값으로 거래처구분 구하기
*******************************************************************************/
function sf_getDealCoClByDealPop(sReturnValue)
{
	if(uf_isNull(sReturnValue) == true) return sReturnValue;
	
	var sArrDeal = sReturnValue.split("^", "webstyle");

	return sArrDeal[5];
}


/*******************************************************************************
 * @description    :  페이지에서 페이지로 인자값 넘긴 인자값 구하기
*******************************************************************************/
function sf_getArguments(sParamNm, oDsParam)
{
	if(IsExistVar(sParamNm, true) == true)
	{ 
		return eval(sParamNm);
	}
	else
	{
		if(oDsParam != null)
		{
			return oDsParam.GetColumn(0, sParamNm);
		}
	}	
}


/*******************************************************************************
 * @description    :  권한으로 영업구분 콤보박스 설정 및 / 화면 권한 설정
*******************************************************************************/
function sf_enableSlCLByAuth(oCmbSlCl)
{
	var sDealCoCl1 = gds_session.GetColumn(0, "orgareacl1");		
	var sSlCl      = sf_convertDealCoClToSlCl(sDealCoCl1);
	
	if(sDealCoCl1 == "A2" || sDealCoCl1 == "B1")
	{
		sf_setComboValue(oCmbSlCl, sSlCl);
		oCmbSlCl.enable = false;		
	}
}



/*******************************************************************************
 * @description    :  지정한 그리드 헤더 값의 인덱스 구하기
*******************************************************************************/
function sf_getIndexByGridHeader(oGrid, sGridHearder, nCntHeader)
{
	var nCntFind = 0; 
	
	var nCntCols = oGrid.GetCellCount("head");

	if(uf_isNull(nCntHeader) == true)
	{
		nCntHeader = 0;
	}
	for(var i=0; i < nCntCols; i++)
	{
		var sHeaderNm = oGrid.GetCellProp("head", i, "text");

		if(sHeaderNm == sGridHearder)
		{
			var nColIndex = oGrid.GetCellProp("head", i, "col");

			if(nCntHeader == nCntFind)
			{
				return toNumber(nColIndex);
			}
			nCntFind++;
		}
	}
	
	return -1;
}


//==================================================================================
//# uf_getIndexByGridHeader : 찾고자 하는 ColID의 위치 Index
//@ param - objGrid : Grid Object
//@ param - strGridHearder : Grid Header
//@ param - nCntHeader : 찾고자하는 몇번째 GridHead
//@ Return - 찾고자 하는 ColID의 위치 Index
//----------------------------------------------------------------------------------
function sf_getIndexByGridColId(objGrid, strGridColId, nCntColId)
{
	var nCntFind = 0; 
	
	if(nCntColId == null)
	{
		nCntColId = 0;
	}
	
	var nCntCols = objGrid.GetCellCount("BODY");
	
	for(var i=0; i < nCntCols; i++)
	{
		var strColId = objGrid.GetCellProp("BODY", i, "COLID");

		if(strColId == strGridColId)
		{
			var nColIndex = objGrid.GetCellProp("HEAD", i, "COL");

			if(nCntColId == nCntFind)
			{
				return toNumber(nColIndex);
			}
			nCntFind++;
		}
	}
	
	return -1;
}

/*******************************************************************************
 * @description    : 판매변경이력구분에 따른 정산변경이력구분 셋팅(사용코드 : ZSAL_C_00310)
 * @ param  - sSaleChgHstCl : 일반판매변경이력구분 코드
 * @ Return - sAccChgHstCl  : 정산변경이력구분 코드
*******************************************************************************/
function sf_getAccChgHstCl(sSaleChgHstCl)
{
    var sAccChgHstCl = "";
    
    switch(sSaleChgHstCl)
    {
        case "01":                //판매
        case "09":                //판매수정
            sAccChgHstCl = "01";
            break;
        case "02":                //기기변경
        case "12":                //판매처변경
        case "13":                //유통망변경
            sAccChgHstCl = "02";
            break;
        case "03":                //할부추가
            sAccChgHstCl = "03";
            break;
        case "04":                //할부취소
            sAccChgHstCl = "04";
            break;
        case "05":                //판매가변경
            sAccChgHstCl = "05";
            break;
        case "06":                //부가서비스변경
            sAccChgHstCl = "06";
            break;
        case "07":                //판매취소
            sAccChgHstCl = "07";
            break;
        case "14":                //가입비분납전환 - 가입비분납전환
        case "15":                //가입비분납전환 - SKT예수금카드완납
            sAccChgHstCl = "08";
            break;
        default:
            sAccchgHstCl = "";
            break;
    }
    return sAccChgHstCl;
}


/*******************************************************************************
 * @description    : 영업 담당자 리스트
 * @param sInDs   : 조건 Dataset
 * @param sOutDs   : 결과 받을 Dataset
*******************************************************************************/
function sf_getChrgrUserList(sInDs, sOutDs, sAgencyCd)
{
	var oInDs = Object(sInDs);
	oInDs.Clear();

	// 넘길 파라메터 설정
	cf_setParam(oInDs, "agency_cd", uf_null2space(sAgencyCd)); 
	
	// 트랜잭션 설정
	var sArg   = "sktst.sal.SCO#getChrgrUserList"; 
	var sSvcID = "getChrgrUserList";
	var sInDs  = "nc_input_fieldDs="+sInDs;
	var sOutDs = sOutDs + "=List";


	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	
/*
<Contents>
	<colinfo id="user_id" size="256" summ="default" type="STRING"/>
	<colinfo id="user_nm" size="256" summ="default" type="STRING"/>
</Contents>	
*/				
}


/*******************************************************************************
 * @description    : Ukey I/F 미처리건 check
 * @param sInDs   : 조건 Dataset
 * @param sOutDs   : 결과 받을 Dataset
*******************************************************************************/
function sf_chkUkeyIfNonProc(sInDs, sOutDs, sProcDt, sProcTm, sSvcMgmtNum, sEqpMdlCd, sEqpSerNum)
{
	var sRtn   = "";
	var oInDs  = Object(sInDs);
	var oOutDs = Object(sOutDs);
	oInDs.Clear();

	// 넘길 파라메터 설정
	cf_setParam(oInDs, "proc_dt"     , uf_null2space(sProcDt)); 
	cf_setParam(oInDs, "proc_tm"     , uf_null2space(sProcTm)); 
	cf_setParam(oInDs, "svc_mgmt_num", uf_null2space(sSvcMgmtNum)); 
	cf_setParam(oInDs, "eqp_mdl_cd"  , uf_null2space(sEqpMdlCd)); 
	cf_setParam(oInDs, "eqp_ser_num" , uf_null2space(sEqpSerNum)); 
		
	// 트랜잭션 설정
	var sSvcID = "getUkeyIfNonProcInfo";
	var sInDs  = "nc_input_fieldDs="+sInDs;
	var sOutDs = sOutDs + "=ds_nonProc";
	var sArg   = "sktst.sal.SUI#getUkeyIfNonProcInfo";	
	
	http.Sync = true;
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	http.Sync = false;
	
	if (oOutDs.rowcount() > 0 )
	{
	    sRtn = false;
	}
	else
	{
	    sRtn = true;
	}
	
	return sRtn;
}


/*******************************************************************************
 * @description    : 거래처정보변경 여부
 * @param sInDs   : 조건 Dataset
 * @param sOutDs   : 결과 받을 Dataset
*******************************************************************************/
function sf_isChgDealInfo(sInDs, sOutDs, sDealCoCd, sSaleDtm, bOnlyStlPlc)
{
	var oInDs = Object(sInDs);
	var oOutDs = Object(sOutDs);
	
	oInDs.Clear();

	if(uf_IsNull(bOnlyStlPlc) == true)
	{
		bOnlyStlPlc = false;
	}
	
	// 넘길 파라메터 설정
	cf_setParam(oInDs, "sale_dtm"  , sSaleDtm); 
	cf_setParam(oInDs, "deal_co_cd", sDealCoCd); 
	
	// 트랜잭션 설정
	var sArg   = "sktst.sal.SUI#getDealChgInfo"; 
	var sSvcID = "getDealChgInfo";
	var sInDs  = "nc_input_fieldDs="+sInDs;
	var sOutDs = sOutDs + "=List";


	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	
	
	var bSameStlPlc = false;
	var bSameHldPlc = false;
	var retValue = true;
	
	if(oOutDs.getColumn(0, "STL_PLC") == oOutDs.getColumn(1, "STL_PLC"))
	{	// 정산처 변경여부
		bSameStlPlc = true;
	}
	if(oOutDs.getColumn(0, "DIS_HLD_PLC") == oOutDs.getColumn(1, "DIS_HLD_PLC"))
	{	// 재고처 변경여부
		bSameHldPlc = true;
	}
	
	if(bOnlyStlPlc == true)
	{	// 정산처만 확인
		if(bSameStlPlc == false)
		{
			retValue = false;
		}
	}
	else
	{
		if(!(bSameStlPlc == true && bSameHldPlc == true))
		{	// 정산처와 재고처 하나라도 변경됐을 경우
			retValue = false;
		}
	}
		
	return retValue;			
}



/*******************************************************************************
 * @description    : 거래처 UKEY 로 PSCODE 가져오기
 * @param sInDs   : 조건 Dataset
 * @param sOutDs   : 결과 받을 Dataset
*******************************************************************************/
function sf_getDealCoCdByUkey(sInDs, sOutDs, sProcDt, sProcTm, sUkeyAgencyCd, sUkeySubCd, sUKeyChannelCd)
{
	var oInDs = Object(sInDs);
	var oOutDs = Object(sOutDs);
	
	oInDs.Clear();


	// 넘길 파라메터 설정
	cf_setParam(oInDs, "proc_dt"        , sProcDt); 
	cf_setParam(oInDs, "proc_tm"        , sProcTm); 
	cf_setParam(oInDs, "ukey_agency_cd" , sUkeyAgencyCd); 
	cf_setParam(oInDs, "ukey_sub_cd"    , sUkeySubCd); 
	cf_setParam(oInDs, "ukey_channel_cd", sUKeyChannelCd); 
	
	// 트랜잭션 설정
	var sArg   = "sktst.sal.SUI#getDealCoCdByUkey"; 
	var sSvcID = "getDealCoCdByUkey";
	var sInDs  = "nc_input_fieldDs="+sInDs;
	var sOutDs = sOutDs + "=List";


	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	
	return oOutDs.GetColumn(0, "DEAL_CO_CD");		
}

/*******************************************************************************
 * @description 	: 매출 금액 가져오기
 * @param sInDs 		: 조건 Dataset
 * @param sOutDs		: 결과 받을 Dataset
 * @param sSaleTypCd 	: 거래구분 (01:B/B, 02:B/C)
 * @param sDealCoCd 	: 거래처 코드
 * @param sProdCd 		: 상품 코드
 * @param sSerNum		: 일련번호
 * @param sSalesDt		: 매출일자
*******************************************************************************/
function sf_getSalesAmt(sInDs, sOutDs, sSaleTypCd, sDealCoCd, sProdCd, sSerNum, sSalesDt)
{
	var oInDs = Object(sInDs);
	var oOutDs = Object(sOutDs);
	
	oInDs.Clear();


	// 넘길 파라메터 설정
	cf_setParam(oInDs, "sale_typ_cd", sSaleTypCd); 
	cf_setParam(oInDs, "deal_co_cd" , sDealCoCd); 
	cf_setParam(oInDs, "prod_cd" 	, sProdCd); 
	cf_setParam(oInDs, "ser_num"    , sSerNum); 
	cf_setParam(oInDs, "sales_dt"	, SubStr(sSalesDt, 0,8)); 
	
	// 트랜잭션 설정
	var sArg   = "sktst.sal.SCO#getSalesAmt"; 
	var sSvcID = "getSalesAmt";
	var sInDs  = "nc_input_fieldDs="+sInDs;
	var sOutDs = sOutDs + "=List";

	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
}


/*******************************************************************************
 * @description 	: 상품재고 체크
 * @param sInDs 		: 조건 Dataset
 * @param sOutDs		: 결과 받을 Dataset
*******************************************************************************/
function sf_validProdDis(sInDs, sOutDs)
{
	var oOutDs = Object(sOutDs);
	
	oOutDs.Clear();
	
	// 트랜잭션 설정
	var sArg   = "sktst.sal.SCO#getProdDisSt"; 
	var sSvcID = "validProdDis";
	var sInDs  = "nc_input_fieldDs="+sInDs;
	var sOutDs = sOutDs + "=List";

	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	
/*
<Contents>
	<colinfo id="dis_st" size="256" summ="default" type="STRING"/>
	<colinfo id="prod_cd" size="256" summ="default" type="STRING"/>
	<colinfo id="color_cd" size="256" summ="default" type="STRING"/>
	<colinfo id="ser_num" size="256" summ="default" type="STRING"/>
</Contents>	
*/	
}


/*******************************************************************************
 * @description 	: 상품재고 체크 (여러건)
 * @param sInDs 		: 조건 Dataset
 * @param sOutDs		: 결과 받을 Dataset
*******************************************************************************/
function sf_validProdDisAll(sInDs, sOutDs)
{
	var oOutDs = Object(sOutDs);
	
	oOutDs.Clear();
	
	// 트랜잭션 설정
	var sArg   = "sktst.sal.SCO#getProdDisStAll"; 
	var sSvcID = "validProdDisAll";
	var sInDs  = "itemList="+sInDs;
	var sOutDs = sOutDs + "=List";

	// 공통 트랜잭션 호출
	cf_Transaction(sSvcID, sInDs, sOutDs, sArg);
	
/*
<Contents>
	<colinfo id="dis_st" size="256" summ="default" type="STRING"/>
	<colinfo id="prod_cd" size="256" summ="default" type="STRING"/>
	<colinfo id="color_cd" size="256" summ="default" type="STRING"/>
	<colinfo id="ser_num" size="256" summ="default" type="STRING"/>
</Contents>	
*/	
}