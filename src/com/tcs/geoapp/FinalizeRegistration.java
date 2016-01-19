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
public class FinalizeRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
 	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			Connection cnn = DatabaseUtil.getConnection();
			String tokenID = request.getParameter("tokenID");
			String gcmID = request.getParameter("gcmID");
			
		
			
			PreparedStatement st = cnn.prepareStatement("SELECT * FROM geo_mapping WHERE TOKEN_ID=? AND GCM_ID IS NULL");
			st.setString(1, tokenID);
			
			ResultSet rSet = st.executeQuery();
			

			
			if(rSet.next()){

				System.out.println("GCM ID is NULL in database");
				st = cnn.prepareStatement("UPDATE geo_mapping SET GCM_ID = ? WHERE TOKEN_ID=? AND GCM_ID IS NULL");
				st.setString(1, gcmID);
				st.setString(2, tokenID);
				int count = st.executeUpdate();
				if(count!=0){
					System.out.println("GCM ID is updated in database");
				}
				else{
					System.out.println("Error : GCM ID not updated in database");
				}				
			}else{
				st = cnn.prepareStatement("INSERT INTO geo_mapping (TOKEN_ID,GCM_ID) VALUES (?,?)");
				st.setString(1, tokenID);
				st.setString(2, gcmID);
				int count = st.executeUpdate();
				if(count!=0){
					System.out.println("GCM ID inserted in database");
				}else{
					System.out.println("Error : GCM ID is not inserted in database");
				}
			}
			
			response.setContentType("text/html");
			response.setStatus(201);
			response.getWriter().write("Registered with Server Successfully!");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
