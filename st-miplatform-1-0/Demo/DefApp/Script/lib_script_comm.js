﻿var CONST_ASC_MARK="▲";
var CONST_DESC_MARK="▼";

function gfn_GridSort(Gridobj,dsObj,nCell,nColCnt) 
{
	var nheadText,sflag;

	if (right(Gridobj.GetCellProp("head",nCell,"text"),1) == CONST_ASC_MARK)
	{
		dsObj.sort(Gridobj.GetCellProp("Body",nCell,"colid"),false);
		nheadText = Gridobj.GetCellProp("head",nCell,"text");
		nheadText = replace(nheadText,CONST_ASC_MARK,"");
		nheadText = nheadText + CONST_DESC_MARK;
		sflag = CONST_DESC_MARK;
	}
	else
	{
		dsObj.sort(Gridobj.GetCellProp("Body",nCell,"colid"),true);
		nheadText = Gridobj.GetCellProp("head",nCell,"text");
		nheadText = replace(nheadText,CONST_DESC_MARK,"");
		nheadText = nheadText + CONST_ASC_MARK;   
		sflag = CONST_ASC_MARK;
	}
	 
	Gridobj.SetCellProp("head",nCell,"text",nheadText);
	
	var sRepText="";
	for(i=0; i<nColCnt; i++)
	{
		
		if (nCell <> i) 
		{
			sRepText = replace(Gridobj.GetCellProp("head",i,"text"), CONST_ASC_MARK,"");
			Gridobj.SetCellProp("head",i,"text", sRepText);
			
			sRepText = replace(Gridobj.GetCellProp("head",i,"text"), CONST_DESC_MARK,"");
			Gridobj.SetCellProp("head",i,"text", sRepText);
		}
	}

	return sflag;
}