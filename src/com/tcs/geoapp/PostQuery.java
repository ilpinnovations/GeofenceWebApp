package com.tcs.geoapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class PostQuery extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public PostQuery() {
		super();
		// TODO Auto-generated constructor stub
	}
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		post(request, response);
	}
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		post(request, response);
	}

	public static String USER_NAME = "komaldua09@gmail.com";
	public static String PASSWORD = "16121988";
	public void post(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String referer = request.getHeader("referer");
		if(referer==null){
			response.sendRedirect("unauthorisedAccess.jsp");
			return ;
		}		
		final String user_email = (String) request.getSession().getAttribute("email");
		System.out.println(user_email);
		String tokenID = (String) request.getSession().getAttribute("tokenID");
		final String user_password=getUser(user_email);
		System.out.println(user_password);
		final String username = USER_NAME;
		final String password = PASSWORD;
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(user_email, "kom@ldu@09");
					}
				});
		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(user_email));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(username));
			message.setSubject("Go Geofence - Query");
			String query = request.getParameter("query");
			message.setContent(
					"Dear admin,<br>User with following tokenID has posted a query."
							+ tokenID + "<br>" + query, "text/html");

			Transport.send(message);
			request.getRequestDispatcher("QuerySent.jsp").forward(request, response);			

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	public static String getUser(String email) {
		String password = null;
		try {
			Connection cnn = DatabaseUtil.getConnection();
			PreparedStatement ps = cnn
					.prepareStatement("SELECT password FROM geo_user WHERE EMAIL = ?");
			ps.setString(1, email);
			ResultSet rSet = ps.executeQuery();
			if (rSet.next()) {
				password = rSet.getString("PASSWORD");
			}
		} catch (Exception e) {

		}
		return password;
	}
}
