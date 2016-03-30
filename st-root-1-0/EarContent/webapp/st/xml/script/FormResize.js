/**********************************************************************************************
//                                 Copyright 2006 Tobesoft
// -. ��� ���
// nWidth,nHeight �� ���� Form size�� ��������� �����ؾ� ��...
// Form  OnLoadCompleted �̺�Ʈ���� Gfn_ResizeInit(nWidth,nHeight); �� ȣ���Ͻø� �ڵ����� Resize ó���� �˴ϴ�.
//   ����
  #include "lib::FormResize.js"
  function form_OnLoadCompleted(obj)
  {
	Gfn_ResizeInit(1024,768);
  }
// -. ���� ����
// Running �߰��� Component�� Destory�ϴ� ó���� ������ Resizeó���� �� �۵� �� �� �ֽ��ϴ�.
// Gfn_ResizeInit();  ȣ���Ŀ� Component ��ġ �� size�� �����ϸ� Resize�ÿ� ������ ��ġ�� size�� ���õ˴ϴ�.   
// Tab �ΰ�� Url Link ó�� �Ǹ鼭 Preload���� false�ϸ� �ε���� ���� Gfn_ResizeInit�ϸ�
// Resize�� Control�� ������� ���Ͽ� �ε���� �ʾƼ� ó���� �ȵǰ� �Ǿ� �ֽ��ϴ�.

//function btn_print_OnSize(obj,nCx,nCy,nState)
//{
//	trace("nCx==" + nCx );
//}
**********************************************************************************************/

var fv_nResizeProcCnt = 0;
var fv_FirstResize = false;
var fv_orgWidth;
var fv_orgHeight;

var fv_forgWidth;
var fv_forgHeight;

var fv_ArrHorzPosition = Array();
var fv_ArrVertPosition = Array();


var fv_ArrSubHorzPosition = Array();
var fv_ArrSubVertPosition = Array();
var fv_SubSeq = -1;


var fv_SuborgWidth = Array();
var fv_SuborgHeight = Array();

var fv_SubforgWidth = Array();
var fv_SubforgHeight = Array();


function Gfn_ResizeInit(nWidth,nHeight)
{
	this.OnSize = "Gfn_frm_OnSize";
		
	fv_orgWidth   = nWidth;
	fv_orgHeight  = nHeight;
	fv_forgWidth  = nWidth;
	fv_forgHeight = nHeight;
	var seq = 0;
	for ( var i = 0 ; i < this.Components.Count ; i++ )
	{
		if (  ( this.Components[i].GetType() == "Dataset" ) ||
			  ( this.Components[i].GetType() == "File" ) ||	
			  ( this.Components[i].GetType() == "FileDialog" ) ||	
			  ( this.Components[i].GetType() == "PopupDiv" ) 
		   )	
			continue;
		fv_ArrHorzPosition[seq] = Components[i].left;
		fv_ArrVertPosition[seq] = Components[i].top;
		seq++;
		fv_ArrHorzPosition[seq] = Components[i].right;
		fv_ArrVertPosition[seq] = Components[i].bottom;
		seq++;
		if ( Components[i].IsComposite() )
		{
			Gfn_SubFormResizeInit(Components[i]);
		}
	}
	
	//alert(this.width + "///" + this.height);
	fv_FirstResize = true;
	Gfn_frm_OnSize(this,this.width,this.height);

}

function Gfn_SubFormResizeInit(obj)
{
	var seq = 0;
	var tmpArrayHorz = Array();
	var tmpArrayVert = Array();
	fv_SubSeq++;
	var subseq = fv_SubSeq;
	fv_SuborgWidth[subseq] = obj.width;
	fv_SuborgHeight[subseq] = obj.height;

	fv_SubforgWidth[subseq] = obj.width;
	fv_SubforgHeight[subseq] = obj.height;
	
	for ( var i = 0 ; i < obj.Components.Count ; i++ )
	{
		if (  ( obj.Components[i].GetType() == "Dataset" ) ||
			  ( obj.Components[i].GetType() == "File" ) ||	
			  ( obj.Components[i].GetType() == "FileDialog" ) ||	
			  ( obj.Components[i].GetType() == "PopupDiv" ) 
		   )	
			continue;
		tmpArrayHorz[seq] = obj.Components[i].left;
		tmpArrayVert[seq] = obj.Components[i].top;
		seq++;
		tmpArrayHorz[seq] = obj.Components[i].right;
		tmpArrayVert[seq] = obj.Components[i].bottom;
		seq++;
		if ( obj.Components[i].IsComposite() )
		{
			Gfn_SubFormResizeInit(obj.Components[i]);
		}
	}
	fv_ArrSubHorzPosition[subseq] = tmpArrayHorz;
	fv_ArrSubVertPosition[subseq] = tmpArrayVert;	
}

function Gfn_ResizeProc(nCx,nCy)
{
	fv_SubSeq = -1;
	var seq = 0;
	var nWidthRate;
	var nHeightRate;
	var bProcSizeFlag = true;
	for ( var i = 0 ; i < this.Components.Count ; i++ )
	{
		if (  ( this.Components[i].GetType() == "Dataset" ) ||
			  ( this.Components[i].GetType() == "File" ) ||	
			  ( this.Components[i].GetType() == "FileDialog" ) ||	
			  ( this.Components[i].GetType() == "PopupDiv" ) 
		   )	
			continue;
		
		bProcSizeFlag = true;
		
		if ( this.Components[i].GetType() == "Button" )  bProcSizeFlag = false;
		else if ( this.Components[i].GetType() == "Image" )
		{
			if ( this.Components[i].FillType == "NONE" ) bProcSizeFlag = false;
		}
		if ( Components[i].GetType() != "TabPage" )
		{		    
			if ( !bProcSizeFlag ) // ��ġ�� �߾����� ����
			{
				nWidthRate  = parseInt( (ToNumber(fv_ArrHorzPosition[seq]) * ToNumber(nCx)) / ToNumber(fv_forgWidth) );
				nHeightRate = parseInt( (ToNumber(fv_ArrVertPosition[seq]) * ToNumber(nCy)) / ToNumber(fv_forgHeight) );
				seq++;
				nWidthRate  = nWidthRate + parseInt( ((ToNumber(fv_ArrHorzPosition[seq]) * ToNumber(nCx)) / ToNumber(fv_forgWidth) - nWidthRate)/2) - parseInt(this.Components[i].Width/2);
				nHeightRate = nHeightRate + parseInt( ((ToNumber(fv_ArrVertPosition[seq]) * ToNumber(nCy)) / ToNumber(fv_forgHeight) - nHeightRate)/2) - parseInt(this.Components[i].Height/2);
				this.Components[i].left = nWidthRate;
				this.Components[i].top = nHeightRate;
				seq++;
			}
			else // size ���� ó��.
			{
				nWidthRate  = parseInt( (ToNumber(fv_ArrHorzPosition[seq]) * ToNumber(nCx)) / ToNumber(fv_forgWidth) );
				nHeightRate = parseInt( (ToNumber(fv_ArrVertPosition[seq]) * ToNumber(nCy)) / ToNumber(fv_forgHeight) );
				this.Components[i].left = nWidthRate;
				this.Components[i].top = nHeightRate;
				seq++;
				nWidthRate  = parseInt( (ToNumber(fv_ArrHorzPosition[seq]) * ToNumber(nCx)) / ToNumber(fv_forgWidth) );
				nHeightRate = parseInt( (ToNumber(fv_ArrVertPosition[seq]) * ToNumber(nCy)) / ToNumber(fv_forgHeight) );
				this.Components[i].right = nWidthRate;
				this.Components[i].bottom = nHeightRate;
				seq++;
			 }
		 }
		 else
		 {
			seq++;
			seq++;
		 }
		 if ( this.Components[i].GetType() == "Grid" ) this.Components[i].FitToArea();
		 if ( this.Components[i].IsComposite() )
		 {
			Gfn_SubFormResizeProc(this.Components[i]);
		 }
	}
}

function Gfn_SubFormResizeProc(obj)
{
	var nCx = obj.width;
	var nCy = obj.height;
	var seq = 0;
	var nWidthRate;
	var nHeightRate;
	var bProcSizeFlag = true;
	var tmpArrayHorz;
	var tmpArrayVert;
	fv_SubSeq++;
	var subseq = fv_SubSeq;
	fv_SuborgWidth[subseq] = obj.width;
	fv_SuborgHeight[subseq] = obj.height;

	tmpArrayHorz = fv_ArrSubHorzPosition[subseq];
	tmpArrayVert = fv_ArrSubVertPosition[subseq];	
	var loopCnt = 	tmpArrayHorz.length()/2;
	for ( var i = 0 ; i < loopCnt ; i++ )
	{
		if (  ( obj.Components[i].GetType() == "Dataset" ) ||
			  ( obj.Components[i].GetType() == "File" ) ||	
			  ( obj.Components[i].GetType() == "FileDialog" ) ||	
			  ( obj.Components[i].GetType() == "PopupDiv" ) 
		   )	
			continue;
		
		bProcSizeFlag = true;
		
		//trace(obj.id + "////" + obj.Components[i].id + "///" + obj.Components[i].GetType());
		if ( obj.Components[i].GetType() == "Button" )  bProcSizeFlag = false;
		else if ( obj.Components[i].GetType() == "Image" )
		{
			if ( obj.Components[i].FillType == "NONE" ) bProcSizeFlag = false;
		}
		
		if ( obj.Components[i].GetType() != "TabPage" )
		{
			if ( !bProcSizeFlag ) // ��ġ�� �߾����� ����
			{
				nWidthRate  = parseInt( (ToNumber(tmpArrayHorz[seq]) * ToNumber(nCx)) / ToNumber(fv_SubforgWidth[subseq]) );
				nHeightRate = parseInt( (ToNumber(tmpArrayVert[seq]) * ToNumber(nCy)) / ToNumber(fv_SubforgHeight[subseq]) );
				seq++;
				nWidthRate  = nWidthRate + parseInt( ((ToNumber(tmpArrayHorz[seq]) * ToNumber(nCx)) / ToNumber(fv_SubforgWidth[subseq]) - nWidthRate)/2) - parseInt(obj.Components[i].Width/2);
				nHeightRate = nHeightRate + parseInt( ((ToNumber(tmpArrayVert[seq]) * ToNumber(nCy)) / ToNumber(fv_SubforgHeight[subseq]) - nHeightRate)/2) - parseInt(obj.Components[i].Height/2);
				obj.Components[i].left = nWidthRate;
				obj.Components[i].top = nHeightRate;
				seq++;
			}
			else // size ���� ó��.
			{
				nWidthRate  = parseInt( (ToNumber(tmpArrayHorz[seq]) * ToNumber(nCx)) / ToNumber(fv_SubforgWidth[subseq]) );
				nHeightRate = parseInt( (ToNumber(tmpArrayVert[seq]) * ToNumber(nCy)) / ToNumber(fv_SubforgHeight[subseq]) );
				obj.Components[i].left = nWidthRate;
				obj.Components[i].top = nHeightRate;
				seq++;
				nWidthRate  = parseInt( (ToNumber(tmpArrayHorz[seq]) * ToNumber(nCx)) / ToNumber(fv_SubforgWidth[subseq]) );
				nHeightRate = parseInt( (ToNumber(tmpArrayVert[seq]) * ToNumber(nCy)) / ToNumber(fv_SubforgHeight[subseq]) );
				obj.Components[i].right = nWidthRate;
				obj.Components[i].bottom = nHeightRate;
				seq++;
			 }
		 }
		 else
		 {
			seq++;
			seq++;
		 }
		 if ( obj.Components[i].GetType() == "Grid" ) obj.Components[i].FitToArea();
		 if ( obj.Components[i].IsComposite() )
		 {
			Gfn_SubFormResizeProc(obj.Components[i]);
		 }
	}
	
	if ( obj.GetType() != "TabPage" )
		obj.ResizeScroll();
}

function Gfn_frm_OnSize(obj,nCx,nCy,nState)
{
	if ( ( fv_orgWidth == nCx ) && ( fv_orgHeight == nCy ) ) return;

	fv_nResizeProcCnt++;
	
	if ( fv_nResizeProcCnt > 1 )
	{
		fv_nResizeProcCnt--;
		return;
	}
	
	var GapW;
	var GapH;
	
	if ( !fv_FirstResize )
	{
		fv_orgWidth = nCx;
		fv_orgHeight = nCy;
		fv_FirstResize = true;
		fv_nResizeProcCnt--;
		return;
	}

    Gfn_ResizeProc(nCx,nCy);
	ResizeScroll();
	fv_orgWidth = nCx;
	fv_orgHeight = nCy;
	fv_nResizeProcCnt--;
}