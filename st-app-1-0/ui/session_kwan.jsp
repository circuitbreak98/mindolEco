<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.net.InetAddress"%>
<%@ page import="java.io.*,java.util.*"%>
<html>
<%

	InetAddress ip;
	String hostname;
	ip = InetAddress.getLocalHost();
	hostname = ip.getHostName();
	String realIp = ip.getHostAddress();
	
	
	 session.setAttribute("id", "test"); // (name, value)
	
	 String requestId = (String)session.getAttribute("id");
	
	 String sessionId =(String)session.getId();
	
	 Boolean isNew = session.isNew();
	 
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Session Test</title>
</head>
<body>
<table border=1 bordercolor="gray" cellspacing=1 cellpadding=0
	width="100%">
	<tr bgcolor="gray">
		<td colspan=2 align="center"><font color="white"><b>Session
		Info</b></font></td>
	</tr>
	<tr>
		<td>Server HostName</td>
		<td><%=hostname %></td>
	</tr>
	<tr>
		<td>Server IP</td>
		<td><%=realIp %></td>
	</tr>
	<tr>
		<td>requestId</td>
		<td><%=requestId %></td>
	</tr>
	<tr>
		<td>SessionID</td>
		<td><%=sessionId %></td>
	</tr>
	<tr>
		<td>isNew</td>
		<td><%=isNew %></td>
	</tr>
	<tr>
		<td>Creation Time</td>
		<td><%=(new Date(session.getCreationTime()))%></td>
	</tr>
	<tr>
		<td>Last Accessed Time</td>
		<td><%=(new Date(session.getLastAccessedTime()))%></td>
	</tr>
	<tr>
		<td>Max Inactive Interval (second)</td>
		<td><%=(session.getMaxInactiveInterval()/60)%>min</td>
	</tr>
	<!-- <tr bgcolor="cyan">
		<td colspan=2 align="center"><b>Session Value List</b></td>
	</tr>
	<tr>
		<td align="center">NAME</td>
		<td align="center">VAULE</td>
	</tr> -->
	<% 
	
	if(session!=null){
        //세션정보가 있다면 -> 로그인이 되었다면
        session.invalidate();
    } %>
</table>
</body>
</html>