package jeus_jspwork;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.io.*;
import java.util.*;


public class _500_Error500_5fjsp extends jeus.servlet.jsp.HttpJspBase {

  public final String[] __jeusGetIncludedJspFiles() {
    return null;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse  response)
      throws ServletException, IOException {

    JspFactory	_jspxFactory = null;
    PageContext	pageContext = null;
    HttpSession	session = null;
    ServletContext	application = null;
    ServletConfig	config = null;
    JspWriter	out = null;
    Object		page = this;
    String		_value = null;

    try {
      _jspxFactory = JspFactory.getDefaultFactory();
      pageContext = _jspxFactory.getPageContext(this, request, response, "", true, 8192, true);
      session = pageContext.getSession();
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      out = pageContext.getOut();

      response.setContentType("text/html;charset=UTF-8");

      // jsp code [from=(2,5);to=(5,1)]
      out.write(_jspx_template0);
      out.write(_jspx_template1);
      // jsp code [from=(6,3);to=(18,1)]
      
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


    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)) {
        if (out.getBufferSize() != 0) {
          try {
            out.clear();
          } catch (Exception _exc) { }
        }
        pageContext.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(pageContext);
    }
  }
  // jsp code [from=(2,5);to=(5,1)]
  private final static String _jspx_template0 = "\r\n";
  private final static String _jspx_template1 = "\r\n";
}
