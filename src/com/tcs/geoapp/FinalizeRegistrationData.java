package com.tcs.geoapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class FinalizeRegistration
 */
public class FinalizeRegistrationData extends HttpServlet {
	private static final long serialVersionUID = 1L;
  	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String referer = request.getHeader("referer");
		if(referer==null){
			response.sendRedirect("unauthorisedAccess.jsp");
			return ;
		}
		try{
			Connection cnn = DatabaseUtil.getConnection();
			String imei = request.getParameter("imei");
			String devName = request.getParameter("devName");
			String pkgName = request.getParameter("pkgName");
		
			
			PreparedStatement st = cnn.prepareStatement("SELECT GCM_ID FROM geo_devices WHERE IMEI=? AND DEVICE_NAME=? AND PACKAGE_NAME=?");
			st.setString(1, imei);
			st.setString(2, devName);
			st.setString(3, pkgName);
			
			ResultSet rSet = st.executeQuery();
			
			response.setContentType("text/html");
			if(rSet.next()){
				
				String gcmID = rSet.getString("GCM_ID");
				System.out.println("GCM ID exists in database : " + gcmID);
				response.getWriter().write("Exists");
			}else{
				st = cnn.prepareStatement("INSERT INTO geo_devices (IMEI,DEVICE_NAME,PACKAGE_NAME) VALUES(?,?,?)");
				st.setString(1, imei);
				st.setString(2, devName);
				st.setString(3, pkgName);
				rSet = st.executeQuery();
				if(rSet.next()){
					System.out.println("IMEI Details entered in database");
				}else{
					System.out.println("Error : IMEI Details not entered in database");
				}
				
				// incomplete
				
			}
			
			response.setStatus(201);
			response.getWriter().write("Registered");
			
//			request.getRequestDispatcher("TriggerListToJSON").forward(request, response);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
