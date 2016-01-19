package com.tcs.geoapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class TriggerListToJSON extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		try {

			String tokenID = request.getParameter("tokenID");
			
			Connection cnn = DatabaseUtil.getConnection();
			PreparedStatement st = cnn
					.prepareStatement("SELECT * FROM geo_trigger_2 WHERE TOKEN_ID = ?");
			
			st.setString(1, tokenID);

			ResultSet rSet = st.executeQuery();

			response.setContentType("text/html");
			JSONObject triggerInfo = null;
			JSONObject mainObject = new JSONObject();
			JSONArray triggerArray = new JSONArray();
			int triggerCount = DatabaseUtil.getRowCount(rSet);
			if(triggerCount!=0){

				while (rSet.next()) {

					int triggerID = rSet.getInt(1);
					int userID = rSet.getInt(2);
					tokenID = rSet.getString(3);
					String place = rSet.getString(4);

					double latitude = rSet.getDouble(5);
					double longitude = rSet.getDouble(6);
					int radius = rSet.getInt(7);

					String notificationText = rSet.getString(8);
					String startDate = rSet.getString(9);
					String endDate = rSet.getString(10);
					String status = rSet.getString(11);

					triggerInfo = new JSONObject();
					triggerInfo.put("triggerID", triggerID);
					triggerInfo.put("userID", userID);
					triggerInfo.put("tokenID", tokenID);
					triggerInfo.put("place", place);
					triggerInfo.put("latitude", latitude);
					triggerInfo.put("longitude", longitude);
					triggerInfo.put("radius", radius);
					triggerInfo.put("notificationText", notificationText);
					triggerInfo.put("startDate", startDate);
					triggerInfo.put("endDate", endDate);
					
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date sDate = format.parse(startDate);
					Date eDate = format.parse(endDate);

					System.out.println(sDate);
					System.out.println(eDate);
					
					final long MILLI_TO_HOUR = 1000 * 60 * 60;
					int hours = (int) ((eDate.getTime() - sDate.getTime()) / MILLI_TO_HOUR);					
					triggerInfo.put("expires", hours);
					triggerInfo.put("status", status);

					triggerArray.put(triggerInfo);

					mainObject.put("triggerData", triggerArray);

				}// end of while
				response.getWriter().write(mainObject.toString());	
			} // end of if

			else{
				JSONObject triggerInfo2 = new JSONObject();
				triggerArray.put(triggerInfo2);
				mainObject.put("nullTrigger", triggerArray);
				response.getWriter().write(mainObject.toString());	
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
