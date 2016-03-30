﻿﻿﻿﻿﻿﻿﻿﻿﻿/*******************************************************************************
* 프로그램ID : lib_DIS
* 업무명         : 재고
* 프로그램명  : 공통 스크립트
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
 * @description    : 현재월의 1일을 리턴한다.
 * @return         : 현재월1일
*******************************************************************************/
function sf_getFirstDay()
{
	var strDate = cf_getDate();
	
	return Mid(strDate,0,6) + "01";	 
}

/*******************************************************************************
 * @description     : 상품 팝업 1 - 조회조건에서 호출할때  
-------------------------------------------------------------------------------- 
 * @param  prodCd    : 상품명 - obiect id
	       prodNm    : 상품코드 - obiect id
	       aplyDt   : 적용일(yyyymmdd)
	       callFunction : 확인 버튼 클릭 시 실행 할 부모창의 function.	       
	       nChar
 * @return          : 없음
*******************************************************************************/
function cf_disProdPop(prodCd, prodNm, aplyDt, callFunction, nChar)
{
	if (nChar==13) { 		
		var sArg  = "prodCd='"+prodCd+"'";
			sArg += " prodNm='"+prodNm+"'";
			sArg += " aplyDt='"+aplyDt+"'";
			sArg += " dsFlag=false";
			sArg += " callFunction='"+callFunction+"'";
			
		dialog("DIS::DISDCO01000.xml", sArg , 400, 400);
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
	       aplyDt   : 적용일(yyyymmdd)
	       callFunction : 확인 버튼 클릭 시 실행 할 부모창의 function.
 * @return          : 없음
*******************************************************************************/
function cf_disProdPopDs(dsParent, rowParent, aplyDt, callFunction)
{
	 var sArg  = "dsParent='"+dsParent+"'";
		 sArg += " rowParent='"+rowParent+"'";
		 sArg += " aplyDt='"+aplyDt+"'";
		 sArg += " dsFlag=true";
		 sArg += " callFunction='"+callFunction+"'";
    
    dialog("DIS::DISDCO01000.xml", sArg , 400, 400);
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
function cf_disProdPopDsByProdCl(dsParent, rowParent, aplyDt, callFunction, prodClCd)
{
	 var sArg  = "dsParent='"+dsParent+"'";
		 sArg += " rowParent='"+rowParent+"'";
		 sArg += " aplyDt='"+aplyDt+"'";
		 sArg += " dsFlag=true";
		 sArg += " callFunction='"+callFunction+"'";
		 sArg += " prodClCd='"+prodClCd+"'";
    
    dialog("DIS::DISDCO01000.xml", sArg , 400, 400);
}

/*******************************************************************************
 * @description    : 재고의 권한을 셋팅한다.
 * @return         : V_DIS_AUTH 권한.
*******************************************************************************/
function sf_setDisAuth()
{

	var V_SAL_CENTER_ID = "3"; //영업센터
	var V_DIS_GRP       = "20";//사용자그룹 (재고)
	var V_DIS_AUTH; // 권한.

	if(( gds_session.GetColumn(0,"orgCl") == V_SAL_CENTER_ID
		&& gds_session.GetColumn(0,"userGrp") == V_DIS_GRP)){
			
		V_DIS_AUTH = "W"; // write			
	}else{		
	
		V_DIS_AUTH = "R"; // read
	}

	return V_DIS_AUTH;
}

/*******************************************************************************
 * @description          : 데이타셋의 카피 - 대상 DS를 기준으로 목적 DS에 정보를 복사
                          - 없으면 추가한다.
 * @param oTargetDs      : 목적 DS 객체
		  oSourceDs      : 대상 DS 객체
		  oDisCd         : OUT :출고 , INN :입고
*******************************************************************************/
function sf_copyDataSet(oTargetDs, oSourceDs, oDisCd)
{
	oTargetDs.FireEvent = false;
	var nSourceColCount = oSourceDs.GetColCount();
	var nSourceRowCount = oSourceDs.GetRowCount();
	var sChildColId = "";
	var nTargetRow = 0;

	for(var i=0; i<nSourceRowCount; i++)
	{		
	
		if(oSourceDs.GetColumn(i,"prod_cl") == "9"){

			// target에 존재할 경우 addrow 하지 않고 수량만 업데이트.
			var iRtn = oTargetDs.FindRow("prod_cd_color_cd",oSourceDs.GetColumn(i,"prod_cd_color_cd"));
			
			if( iRtn == -1 ){
			
				nTargetRow = oTargetDs.AddRow();
				
				for(var j=0; j<nSourceColCount; j++)
				{
					sChildColId = oSourceDs.GetColID(j);				
	
					if(oTargetDs.GetColIndex(sChildColId) == -1)
					{
						oTargetDs.AddColumn(sChildColId);
					}
					
					if(oTargetDs.getRowCount() < 1)
					{
						nTargetRow = oTargetDs.addRow();
					}	
			
					oTargetDs.SetColumn(nTargetRow, sChildColId, oSourceDs.GetColumn(i, sChildColId));
				}				
				
			}else{
			
				if(oDisCd == "OUT"){
					oTargetDs.SetColumn(iRtn,"out_qty", toNumber(oTargetDs.GetColumn(iRtn,"out_qty")) + toNumber(oSourceDs.GetColumn(i,"out_qty")));
				}else{
					oTargetDs.SetColumn(iRtn,"in_qty", toNumber(oTargetDs.GetColumn(iRtn,"in_qty")) + toNumber(oSourceDs.GetColumn(i,"in_qty")));
				}
			}
					
		}else{
	
			nTargetRow = oTargetDs.AddRow();
			
			for(var j=0; j<nSourceColCount; j++)
			{
				sChildColId = oSourceDs.GetColID(j);				

				if(oTargetDs.GetColIndex(sChildColId) == -1)
				{
					oTargetDs.AddColumn(sChildColId);
				}
				
				if(oTargetDs.getRowCount() < 1)
				{
					nTargetRow = oTargetDs.addRow();
				}	
		
				oTargetDs.SetColumn(nTargetRow, sChildColId, oSourceDs.GetColumn(i, sChildColId));
			}
		}
	}

	
	oTargetDs.FireEvent = true;
}