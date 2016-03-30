<%@ page contentType="text/xml; charset=utf-8" %>
<%
	String uacMode = request.getParameter("UAC");
%>
<?xml version='1.0' encoding='utf-8'?>
<root>
<params>
	<param id='DEVICE' type='string'>Win32</param>
	<param id='SOURCE' type='string'>./files_msi/</param>
	<param id='TARGET' type='string'></param>
	<param id='VERSION' type='string'>2006,3,16,1</param>
	<param id='ACTION' type='string'>DOWNCHECK</param>
	<param id='STATUS' type='string'>0</param>
</params>
<dataset id='output'>
	<colinfo id='DEVICE' type="string" size='255'/>
	<colinfo id='SOURCE' type="string" size='255'/>
	<colinfo id='TARGET' type="string" size='255'/>
	<colinfo id='FILENAME' type="string" size='255'/>
	<colinfo id='VERSION' type="string" size='255'/>
	<colinfo id='ACTION' type="string" size='255'/>
	<colinfo id='ARGUMENT' type="string" size='255'/>
	<colinfo id='STATUS' type="string" size='2'/>
	<record>
		<DEVICE>Win32</DEVICE>
		<ACTION>MSI</ACTION>
		<ARGUMENT>/qb</ARGUMENT> 
		<TARGET>{3964575C-D828-4587-AED1-E538EAAFC083}</TARGET>
		<FILENAME>MiPlatform_InstallBase320.msi</FILENAME>
		<VERSION>System::3.20.10</VERSION>
	</record>	
	<record>
		<DEVICE>Win32</DEVICE>
		<ACTION>MSI</ACTION>
		<ARGUMENT>/qb</ARGUMENT> 
		<TARGET>{65673658-248C-49AC-9EC4-25682074A312}</TARGET>
		<FILENAME>MiPlatform_InstallEngine320U.msi</FILENAME>
		<PATCHFILENAME>MiPlatform_InstallEngine320U.msp</PATCHFILENAME>
		<OLDVERSION>System::3.20.220</OLDVERSION>
		<VERSION>System::3.20.210</VERSION>
	</record>
<% 
	if("N".equals(uacMode))
	{
%>
	<record>
		<DEVICE>Win32</DEVICE>
		<ACTION>MSI</ACTION>
		<ARGUMENT>/qb</ARGUMENT> 
		<TARGET>{A1482B04-047E-4960-B49E-5BBA25272B6B}</TARGET>
		<FILENAME>MiPlatform_FixUACProblem320U.msi</FILENAME>
		<VERSION>System::3.20.60</VERSION>
	</record>	
<% 
	} 
%>
	<record>
		<DEVICE>Win32</DEVICE>
		<ACTION>MSI_</ACTION>
		<ARGUMENT>/qb</ARGUMENT> 
		<TARGET>{0ACA9DE0-A29B-4BC3-A662-5CC43113754D}</TARGET>
		<FILENAME>MiPlatform_Updater322.msi</FILENAME>
		<VERSION>System::3.20.210</VERSION>
	</record>	
	<record>
		<DEVICE>Win32</DEVICE>
		<ACTION>MSI</ACTION>
		<ARGUMENT>/qb</ARGUMENT> 
		<TARGET>{3CD02841-0CF2-46D2-8D8A-DA6C6C8C9483}</TARGET>
		<FILENAME>VsReport.msi</FILENAME>
		<VERSION>System::3.2.50</VERSION>
	</record>  
<!--
		<record>
			<DEVICE>Win32</DEVICE>
			<ACTION>MSI</ACTION>
			<ARGUMENT>/qb</ARGUMENT> 
			<ARGUMENT>/qn REBOOT=ReallySupress</ARGUMENT>
			<TARGET>{2572E0C6-0443-4F9C-846F-87E64E672FA1}</TARGET>
			<FILENAME>ChartFX5040.msi</FILENAME>
			<VERSION>System::3.20.30</VERSION>
		</record>  
-->
</dataset>
</root>
