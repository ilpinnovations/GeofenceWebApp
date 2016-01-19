package com.tcs.geoapp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class ProcessTrigger
 */
public class ProcessCampaignTrigger extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String gcmID;
	private static final String GOOGLE_SERVER_KEY = "AIzaSyDA5dlLInMWVsJEUTIHV0u7maB82MCsZbU";
	public ProcessCampaignTrigger() {
		super();
	}
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doProcess(request, response);
	}
	protected void doProcess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String triggerID;
		String referer = request.getHeader("referer");
		if (referer == null) {
			response.sendRedirect("unauthorisedAccess.jsp");
			return;
		}

		try {
			HttpSession session = request.getSession();
			String tokenID = request.getParameter("tokenID");
			// parameters from request
			String place = request.getParameter("place");

			double latitude = Double.parseDouble(request
					.getParameter("latitude"));
			double longitude = Double.parseDouble(request
					.getParameter("longitude"));
			String rad=request.getParameter("radius").toString();
			int radius = Integer.parseInt(request.getParameter("radius"));

			String notificationText = request.getParameter("notificationText");
			int count=0;
			String startDateTime = request.getParameter("startDateTimePicker");
			String endDateTime = request.getParameter("endDateTimePicker");
						if (tokenID == null || place == null || latitude==0.0
					|| tokenID == "" || longitude == 0.0 || rad == null
					|| notificationText == null || startDateTime == null || endDateTime == null
					|| endDateTime == "" || startDateTime == ""
					|| notificationText == "" || place == "" ||rad=="") {

				if (tokenID == "" || tokenID == null) {
					String msg1 = "Token ID Parameter missing. Please fill all the parameters";
					request.setAttribute("msg1", msg1);
					count++;
				}
				if (place == "" || place == null) {
					String msg2 = "Place Parameter missing. Please fill all the parameters";
					request.setAttribute("msg2", msg2);
					count++;
				}
				if (latitude==0.0) {
					String msg3 = "Latitude Parameter missing. Please fill all the parameters";
					request.setAttribute("msg3", msg3);
					count++;
				}
				if (longitude==0.0) {
					String msg4 = "Longitude Parameter missing. Please fill all the parameters";
					request.setAttribute("msg4", msg4);
					count++;
				}
				if (rad==null||rad=="") {
					String msg5 = "Radius Parameter missing. Please fill all the parameters";
					request.setAttribute("msg5", msg5);
					count++;
				}
				if (notificationText == "" || notificationText == null) {
					String msg6 = "Notification Text Parameter missing. Please fill all the parameters";
					request.setAttribute("msg6", msg6);
					count++;
				}
				if (startDateTime == "" || startDateTime == null) {
					String msg7 = "Start date time Parameter missing. Please fill all the parameters";
					request.setAttribute("msg7", msg7);
					count++;
				}
				if (endDateTime == "" || endDateTime == null) {
					String msg8 = "End date time Parameter missing. Please fill all the parameters";
					request.setAttribute("msg8", msg8);
					count++;
				}
				request.setAttribute("count", count);
				request.getRequestDispatcher("TriggerFailure.jsp").forward(request,
						response);
				
			}

			Connection cnn = DatabaseUtil.getConnection();
			PreparedStatement ps2 = cnn
					.prepareStatement("select * from geo_user where token_id=?");
			ps2.setString(1, tokenID);
			ResultSet rs1 = ps2.executeQuery();
			if (!rs1.next()) {
				String msg="Token ID does not exists. <br>Please enter a valid token ID";
				request.setAttribute("msg", msg);
				RequestDispatcher dis = request.getRequestDispatcher("TriggerFailure.jsp");
				dis.forward(request, response);
			}

			PreparedStatement st1 = cnn
					.prepareStatement("select count(*) from geo_trigger_2");
			ResultSet rs = st1.executeQuery();
			int c = 0;
			Random r=new Random();
			while (rs.next()) {
			 c = rs.getInt(1)+r.nextInt(99999);
			}
			PreparedStatement st = cnn
					.prepareStatement("INSERT INTO geo_trigger_2"
							+ " (TRIGGER_ID,TOKEN_ID,PLACE,LATITUDE,LONGITUDE,RADIUS,NOTIFICATION_TEXT,START_DATE,END_DATE,STATUS) VALUES(?,?,?,?,?,?,?,?,?,?)");
			st.setInt(1, c);
			st.setString(2, tokenID);
			st.setString(3, place);
			st.setDouble(4, latitude);
			st.setDouble(5, longitude);
			st.setInt(6, radius);
			st.setString(7, notificationText);
			System.out.println(startDateTime);
			Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm")
					.parse(startDateTime);
			String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(date);
			st.setString(8, sDate);

			date = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(endDateTime);
			String eDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(date);

			System.out.println(sDate);
			System.out.println(eDate);

			st.setString(9, eDate);
			st.setString(10, "Active");

			int res = st.executeUpdate();

			response.setContentType("text/html");
			if (res != 0) {
				System.out.println("Insertion successful!");
				PrintWriter out = response.getWriter();
				out.write("Insertion Success!");
			}
			request.setAttribute("triggerID", c);
			request.getRequestDispatcher("TriggerSuccess.jsp").forward(request,
					response);
		} catch (Exception e) {
			String msg=e.getMessage();
			request.setAttribute("msg", msg);
			request.getRequestDispatcher("TriggerFailure.jsp").forward(request,
					response);
			e.printStackTrace();
		}

	}
}