package com.tcs.geoapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DownloadPlugIn
 */
public class DownloadPlugIn extends HttpServlet {
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doProcess(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doProcess(request, response);
	}
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileName = "go_geofence.apk";
		
		ServletContext context = getServletContext();
		String fullPath = context.getRealPath("/WEB-INF/" + fileName);
		System.out.println(fullPath);
		response.setContentType("application/vnd.android.package-archive");
	    response.setHeader("Content-disposition", "attachment; filename=\""+fileName+"\"");
	    response.setHeader("Cache-Control", "no-cache");
	    response.setHeader("Expires", "-1");
	    BufferedReader br = new BufferedReader(new FileReader(new File(fullPath)));
	    String result = "";
	    StringBuffer sb = new StringBuffer();
	    while((result=br.readLine())!=null){
	    	sb.append(result);
	    }
	    response.getOutputStream().write(sb.toString().getBytes());
		
	}
	

}
