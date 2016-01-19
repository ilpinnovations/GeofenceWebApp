package com.tcs.geoapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SelectTrigger extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String trigerid = request.getParameter("triggerid");
		if(trigerid==null || trigerid==""){
			response.setContentType("text/html");				
			response.getWriter().write("<h1>Trigger id Missing. Please fill the token id</h1>");
		}
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection cnn = null;
		cnn = DatabaseUtil.getConnection();

		Statement st = null;
		try {
			st = cnn.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ResultSet rSet = null;
		try {

			PreparedStatement ps = cnn
					.prepareStatement("SELECT NOTIFICATION_TEXT,place,start_date,end_date FROM geo_trigger_2 WHERE trigger_id=?");
			ps.setString(1, trigerid);
			rSet = ps.executeQuery();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {

			JSONObject triggerInfo = null;
			JSONObject mainObject = new JSONObject();
			JSONArray triggerArray = new JSONArray();
			int triggerCount = DatabaseUtil.getRowCount(rSet);
			if (triggerCount != 0) {

				while (rSet.next()) {

					String notification_text = rSet.getString(1);
					String place = rSet.getString(2);
					String startdate = rSet.getString(3);
					String enddate = rSet.getString(4);

					try {

						triggerInfo = new JSONObject();
						triggerInfo.put("notification_text", notification_text);
						triggerInfo.put("place", place);
						triggerInfo.put("startdate", startdate);
						triggerInfo.put("enddate", enddate);
						triggerArray.put(triggerInfo);
						mainObject.put("notificationdata", triggerArray);
					} catch (JSONException jse) {
						jse.printStackTrace();
					}
					response.getWriter().write(mainObject.toString());
				}
			}else{
				JSONObject triggerInfo2 = new JSONObject();
				triggerArray.put(triggerInfo2);
				mainObject.put("nullTrigger", triggerArray);
				response.setContentType("text/html");				
				response.getWriter().write("<h1>Trigger id "+ trigerid + " does not exists!</h1><br><br>"+mainObject.toString());	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}