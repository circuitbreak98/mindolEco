package com.sktst.common.menu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.Record;
import nexcore.framework.core.data.RecordHeader;
import nexcore.framework.core.data.RecordSet;

/**
 * 
 * @author admin
 *
 */
public class PsMenuToRecordSet implements IPsMenuToRecordSet{

	
	/** RecordSet Id */
	private String recordsetId = "GBL_MENU_DS"; //기본값 설정 
	
	/** noUseRecorsetid */
	private String noUseRecorsetId = "GBL_NOUSE_MENU_DS"; //내부적 사용 메뉴 DS 기본값 설정
		
	/**
	 * 
	 * @param recordSetId
	 */
	public void setRecordsetId(String recordsetId){
		if(recordsetId != null && !recordsetId.trim().equals("")){
			this.recordsetId = recordsetId;			
		}
	}
	
	/**
	 * 
	 * @param noUseRecorsetid
	 */
	public void setNoUseRecorsetId(String noUseRecorsetId){
		if(noUseRecorsetId != null && !noUseRecorsetId.trim().equals("")){
			this.noUseRecorsetId = noUseRecorsetId;			
		}		
	}
		
	/**
	 * 
	 * @param menu
	 * @return
	 */
	public Map getMenuRecordSet(PsMenu menu){
		return getMenuRecordSet(menu, null);
	}
	
	/**
	 * 
	 * @param menu
	 * @param exceptMenuId
	 * @return
	 */
	public Map getMenuRecordSet(PsMenu menu, String exceptMenuId){
		IRecordSet ncRecordSet = new RecordSet(recordsetId);		
		IRecordSet ncNoUseRecordSet = new RecordSet(noUseRecorsetId);
		setMenuHeader(ncRecordSet);		
		setMenuHeader(ncNoUseRecordSet);
		setMenuRecord(0, ncRecordSet, ncNoUseRecordSet, (PsMenu)menu, exceptMenuId);
		Map<String, IRecordSet> result = new HashMap<String, IRecordSet>();
		result.put(recordsetId, ncRecordSet);
		result.put(noUseRecorsetId, ncNoUseRecordSet);
		return result;		
	}
	
	/**
	 * 
	 * @param ncRecordSet
	 */
	private void setMenuHeader(IRecordSet ncRecordSet){
		ncRecordSet.addHeader(new RecordHeader(PsMenu.MENU_NUM, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.MENU_NM, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.MENU_SHOT_NM, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.TOP_MENU_NUM, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.MENU_GROUP, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.MENU_LVL_CD, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.MENU_URL, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.SORT_SEQ, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.SCREEN_ID, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.SUP_MENU_NUM, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.TOP_PREFIX_CD, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.SCREEN_SIZE, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.USE_YN, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.REMARK, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenu.TREE_STEP, java.sql.Types.VARCHAR));

		ncRecordSet.addHeader(new RecordHeader(PsMenuAuth.SEARCH_AUTH_YN, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenuAuth.SAVE_AUTH_YN, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenuAuth.DEL_AUTH_YN, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenuAuth.REQ_AUTH_YN, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenuAuth.APRV_AUTH_YN, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenuAuth.CAN_AUTH_YN, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenuAuth.CLOSE_AUTH_YN, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenuAuth.PRINT_AUTH_YN, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenuAuth.INIT_AUTH_YN, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenuAuth.ETC1_AUTH_YN, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenuAuth.ETC2_AUTH_YN, java.sql.Types.VARCHAR));
		ncRecordSet.addHeader(new RecordHeader(PsMenuAuth.ETC3_AUTH_YN, java.sql.Types.VARCHAR));		
	}

	/**
	 * 
	 * @param ncRecordSet
	 * @param ncNoUseRecordSet
	 * @param menu
	 * @param exceptMenuId
	 */
	private void setMenuRecord(int treeStep, IRecordSet ncRecordSet, IRecordSet ncNoUseRecordSet, PsMenu menu, String exceptMenuId){
		if(menu == null){
			return;
		}
		if(menu.hasAuth()){
			if(exceptMenuId != null && menu.getMenuNum().equals(exceptMenuId)){
				//임시 루트 메뉴 skip
			}else{
				String useYn = menu.getUseYn(); 
				menu.setTreeStep("" + treeStep);
				if(useYn != null && useYn.trim().toUpperCase().equals("Y")){					
					ncRecordSet.addRecord(getMenuRecord(menu));
				}else{
					ncNoUseRecordSet.addRecord(getMenuRecord(menu));
				}
			}			
			if(menu.hasChildren()){
				treeStep++;
				List children = menu.getChildren();
				for (Iterator it = children.iterator(); it.hasNext();){
					PsMenu child = (PsMenu) it.next();
					setMenuRecord(treeStep, ncRecordSet, ncNoUseRecordSet, child, exceptMenuId);					
				}
			}
		}		
	}
	
	/**
	 * 
	 * @return
	 */
	private IRecord getMenuRecord(PsMenu menu){
		IRecord record = new Record();		
		record.add(menu.getMenuNum());
		record.add(menu.getMenuNm());
		record.add(menu.getMenuShotNm());
		record.add(menu.getTopMenuNum());
		record.add(menu.getMenuGroup());
		record.add(menu.getMenuLvlCd());
		record.add(menu.getMenuUrl());
		record.add(menu.getSortSeq());
		record.add(menu.getScreenId());
		record.add(menu.getSupMenuNum());
		record.add(menu.getTopPrefixCd());
		record.add(menu.getScreenSize());
		record.add(menu.getUseYn());
		record.add(menu.getRemark());
		record.add(menu.getTreeStep());
		
		record.add(menu.getMenuAuth().getSearchAuthYn());
		record.add(menu.getMenuAuth().getSaveAuthYn());
		record.add(menu.getMenuAuth().getDelAuthYn());
		record.add(menu.getMenuAuth().getReqAuthYn());
		record.add(menu.getMenuAuth().getAprvAuthYn());
		record.add(menu.getMenuAuth().getCanAuthYn());
		record.add(menu.getMenuAuth().getCloseAuthYn());
		record.add(menu.getMenuAuth().getPrintAuthYn());
		record.add(menu.getMenuAuth().getInitAuthYn());
		record.add(menu.getMenuAuth().getEtc1AuthYn());
		record.add(menu.getMenuAuth().getEtc2AuthYn());
		record.add(menu.getMenuAuth().getEtc3AuthYn());
		return record;
	}
}
