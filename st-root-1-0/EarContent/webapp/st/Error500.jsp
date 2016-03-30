<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- 
    this page is for temporary use. 
    should be modified to handle exception in a better way.
--%>
<%
System.err.println("** ERROR occurred **");
Throwable cause = (Throwable) request.getAttribute("javax.servlet.error.exception");
if (cause == null) {
    cause = (Throwable) request.getAttribute("javax.servlet.jsp.jspException");
}
if (cause == null) {
    cause = (Throwable) request.getAttribute("nexcore.bizlogic.exception");
}
if (cause != null) {
    cause.printStackTrace();
}
%>