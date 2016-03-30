<%@ page contentType="text/xml; charset=utf-8" %>
<%
	String uacMode = request.getParameter("UAC");

    uacMode.replaceAll("<","&lt;");
    uacMode.replaceAll(">","&gt;");
    uacMode.replaceAll("&lt;p&gt;", "<p>"); 
    uacMode.replaceAll("&lt;P&gt;", "<P>"); 
    uacMode.replaceAll("&lt;br&gt;", "<br>"); 
    uacMode.replaceAll("&lt;BR&gt;", "<BR>"); 

%>

<?xml version='1.0' encoding="utf-8" ?>
<root>
	<params>
	<param id='VERSION' type='string'>2007,3,9,1</param>
	<param id='Config' type='string'>true</param>
	</params>
	
	<dataset id='win32'>
		<colinfo id='SOURCE' type='string' size='256'/>
		<colinfo id='DEVICE' type='string' size='256'/>
		<colinfo id='VERSION' type='string' size='256'/>
		<colinfo id='STATUS' type='string' size='2'/>

<% 
	if("X".equals(uacMode))
	{
%>
	<record>
		<DEVICE>Win32</DEVICE>
		<SOURCE>install_cab.xml</SOURCE>
	</record>
	<record>
		<DEVICE>Win32</DEVICE>
		<SOURCE>update_engine.xml</SOURCE>
	</record>
<!--
	<record>
		<DEVICE>Win32</DEVICE>
		<SOURCE>update_ocx_files.xml</SOURCE>
	</record>
-->
<% 
	}
	else
	{
%>
	<record>
		<DEVICE>Win32</DEVICE>
		<SOURCE>update_msi.jsp?UAC=<%=uacMode%></SOURCE>
	</record>
<% 
	} 
%>
	<record> 
		<DEVICE>Win32</DEVICE>
		<SOURCE>update_component_files.xml</SOURCE>
	</record>
		<record>
		<DEVICE>Win32</DEVICE>
		<SOURCE>update_etc_files.xml</SOURCE>
	</record>
<!--
	<record>
		<DEVICE>Win32</DEVICE>
		<SOURCE>update_etc_files.xml</SOURCE>
	</record>

-->
	</dataset>
</root>
