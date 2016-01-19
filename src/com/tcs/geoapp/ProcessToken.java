package com.tcs.geoapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.synth.SynthScrollBarUI;
public class ProcessToken extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public int count = 0;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doProcess(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doProcess(request, response);
	}

	protected void doProcess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getParameter("userName");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String dob = request.getParameter("dob");

		GregorianCalendar calendar = new GregorianCalendar();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastVisit = format
				.format(new Date(calendar.getTime().getTime()));
		String tokenID = request.getSession().getId();
		System.out.println("tokenId:" + tokenID);
		int count=0;
		String password = request.getParameter("password");
		if (userName == null || firstName == null || lastName == null
				|| userName == "" || userName == null || email == null
				|| dob == null || tokenID == null || password == null
				|| userName == "" || firstName == ""
				|| lastName == "" || email == "" || dob == "" || tokenID == ""
				|| password == "") {

			if (userName == "" || userName == null) {
				String msg1 = "User name Parameter missing. Please fill all the parameters";
				request.setAttribute("msg1", msg1);
				count++;
			}
			if (firstName == "" || firstName == null) {
				String msg2 = "First name Parameter missing. Please fill all the parameters";
				request.setAttribute("msg2", msg2);
				count++;
			}
			if (lastName == "" || lastName == null) {
				String msg3 = "Last name Parameter missing. Please fill all the parameters";
				request.setAttribute("msg3", msg3);
				count++;
			}
			if (email == "" || email == null) {
				String msg4 = "Email Parameter missing. Please fill all the parameters";
				request.setAttribute("msg4", msg4);
				count++;
			}
			if (dob == "" || dob == null) {
				String msg5 = "date of birth Parameter missing. Please fill all the parameters";
				request.setAttribute("msg5", msg5);
				count++;
			}
			if (password == "" || password == null) {
				String msg6 = "Password Parameter missing. Please fill all the parameters";
				request.setAttribute("msg6", msg6);
				count++;
			}
			request.setAttribute("count", count);
			request.getRequestDispatcher("TokenFailure.jsp").forward(request,
					response);
			
		}
		try {
			Connection cnn = DatabaseUtil.getConnection();
			System.out.println("Connected");
			PreparedStatement st = cnn
					.prepareStatement("INSERT INTO geo_user(USER_NAME,FIRST_NAME,LAST_NAME,EMAIL,DOB,LAST_VISIT,TOKEN_ID,PASSWORD) VALUES(?,?,?,?,?,?,?,?)");

			st.setString(1, userName);
			st.setString(2, firstName);
			st.setString(3, lastName);
			st.setString(4, email);
			st.setString(5, dob);
			st.setString(6, lastVisit);
			st.setString(7, tokenID);
			st.setString(8, password);

			int c = st.executeUpdate();

			if (c != 0) {
				System.out.println("User Registered Successfully!");
				request.setAttribute("tokenID", tokenID);
				request.setAttribute("source", "regPage");
				request.getRequestDispatcher("TokenSuccess.jsp").forward(
						request, response);
			} else {
				System.out.println("User Registration Failed!");
				request.setAttribute("user","User name already exists");
				request.getRequestDispatcher("TokenFailure.jsp").forward(
						request, response);

			}

		} catch (Exception e) {
			String msg = e.getMessage();
			request.setAttribute("user","User name already exists");
			request.setAttribute("msg", msg);
			request.getRequestDispatcher("TokenFailure.jsp").forward(request,
					response);
			e.printStackTrace();
		}

	}
}