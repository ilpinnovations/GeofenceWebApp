package com.tcs.geoapp;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class DownloadJSON
 */
public class DownloadJSON extends HttpServlet {
	private static final long serialVersionUID = 1L;
   	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
		doProcess(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doProcess(request, response);
	}
	
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//prevents direct access to the servlet
		String referer = request.getHeader("referer");
		if(referer==null){
			response.sendRedirect("unauthorisedAccess.jsp");
			return ;
		}
			
		HttpSession session = request.getSession();
		String tokenID = (String)session.getAttribute("tokenID");
		String userName = "token";
		response.setContentType("application/json");
	    response.setHeader("Content-disposition", "attachment; filename=\""+userName+ ".json\"");
	    response.setHeader("Cache-Control", "no-cache");
	    response.setHeader("Expires", "-1");
	    
	   JSONObject tokenKey  = new JSONObject();
	   
	   
	    // actually send result bytes
	    try {
			response.getWriter().write(tokenKey.put("tokenID", tokenID).toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
