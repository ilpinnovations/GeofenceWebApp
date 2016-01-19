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
import java.util.Random;

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
public class ProcessTrigger extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static  String  gcmID;
	private static final String GOOGLE_SERVER_KEY = "AIzaSyD9rVi356QEkdtqk-Y14vW_CqtY-E-3yvs";   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProcessTrigger() {
        super();
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doProcess(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doProcess(request,response);
	}

	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//prevents direct access to the servlet
		String referer = request.getHeader("referer");
		if(referer==null){
			response.sendRedirect("unauthorisedAccess.jsp");
			return ;
		}
		
		
		try{
			HttpSession session = request.getSession();

			// parameters from session
			String val = session.getAttribute("userID").toString();
			System.out.println("User ID " +  Integer.parseInt(val));

			int userID = Integer.valueOf(val);
			String tokenID = (String)session.getAttribute("tokenID");

			// parameters from request
			String place = request.getParameter("place");


			double latitude = Double.parseDouble(request.getParameter("latitude"));
			double longitude = Double.parseDouble(request.getParameter("longitude"));			

			int radius = Integer.parseInt(request.getParameter("radius"));	


			String notificationText = request.getParameter("notificationText");


			String startDateTime = request.getParameter("startDateTimePicker");
			String endDateTime = request.getParameter("endDateTimePicker");


			Connection cnn = DatabaseUtil.getConnection();
			PreparedStatement st1 = cnn
					.prepareStatement("select count(*) from geo_trigger_2");
			ResultSet rs = st1.executeQuery();
			int c = 0;
			Random r=new Random();
			while (rs.next()) {
			 c = rs.getInt(1)+r.nextInt(99999);
			}
			
			PreparedStatement st = cnn.prepareStatement("INSERT INTO geo_trigger_2" +
					" (TRIGGER_ID,USER_ID,TOKEN_ID,PLACE,LATITUDE,LONGITUDE,RADIUS,NOTIFICATION_TEXT,START_DATE,END_DATE,STATUS) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
			st.setInt(1, c);
			st.setInt(2, userID);
			st.setString(3, tokenID);
			st.setString(4,place);
			st.setDouble(5,latitude);
			st.setDouble(6,longitude);
			st.setInt(7,radius);
			st.setString(8, notificationText);
			System.out.println(startDateTime);
			Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(startDateTime);
			String sDate =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
			st.setString(9,sDate);

			System.out.println(sDate);
			date = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(endDateTime);
			String eDate =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

			System.out.println(sDate);
			System.out.println(eDate);

			st.setString(10,eDate);
			st.setString(11, "Active");

			int count = st.executeUpdate();

			response.setContentType("text/html");
			if(count!=0){
				System.out.println("Insertion successful!");
				PrintWriter out = response.getWriter();
				out.write("Insertion Success!");
			}
			
			request.getRequestDispatcher("viewTriggers.jsp").forward(request,response);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
}
