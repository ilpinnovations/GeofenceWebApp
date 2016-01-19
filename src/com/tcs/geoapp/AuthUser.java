package com.tcs.geoapp;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.naming.CommunicationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class AuthUser
 */
public class AuthUser extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doAuthenticate(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doAuthenticate(request, response);
	}

	protected void doAuthenticate(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// prevents direct access to the servlet
		String referer = request.getHeader("referer");
		if (referer == null) {
			response.sendRedirect("unauthorisedAccess.jsp");
			return;
		}
		Connection cnn = null;
		try {
			cnn = DatabaseUtil.getConnection();
			System.out.println("receivd connection");
			PreparedStatement st = cnn.prepareStatement(
					"SELECT USER_ID,USER_NAME,FIRST_NAME,LAST_NAME,EMAIL,DOB,LAST_VISIT,TOKEN_ID FROM geo_user WHERE USER_NAME = ? AND PASSWORD = ?");

			st.setString(1, request.getParameter("userName"));
			st.setString(2, request.getParameter("password"));

			ResultSet rSet = st.executeQuery();

			if (rSet.next()) {
				System.out.println("executing");
				HttpSession session = request.getSession();

				session.setAttribute("userID", rSet.getInt("USER_ID"));
				session.setAttribute("userName", rSet.getString("USER_NAME"));
				session.setAttribute("firstName", rSet.getString("FIRST_NAME"));
				session.setAttribute("lastName", rSet.getString("LAST_NAME"));
				session.setAttribute("email", rSet.getString("EMAIL"));
				session.setAttribute("dob", rSet.getString("DOB"));
				session.setAttribute("tokenID", rSet.getString("TOKEN_ID"));
				String lastVisit = rSet.getString("LAST_VISIT");

				Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastVisit);
				lastVisit = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss aaa").format(date);
				System.out.println(lastVisit);
				System.out.println(date.getTime());
				st = cnn.prepareStatement("UPDATE geo_user SET LAST_VISIT = ? WHERE USER_ID = ?");

				GregorianCalendar calendar = new GregorianCalendar();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				String updatedLastVisit = format.format(new Date(calendar.getTime().getTime()));
				session.setAttribute("lastVisit",
						lastVisit + " (" + AppUtil.toDuration(calendar.getTime().getTime() - date.getTime()) + ")");

				System.out.println("Calender " + updatedLastVisit);

				st.setString(1, updatedLastVisit);
				st.setInt(2, rSet.getInt("USER_ID"));

				st.executeUpdate();

				session.setAttribute("msg", "Success");
				// RequestDispatcher rd =
				// request.getRequestDispatcher("dashboard.jsp");
				// rd.forward(request, response);

				String reqSource = (String) request.getAttribute("source");
				if ("regPage".equals(reqSource)) {
					response.sendRedirect("newUserSuccess.jsp");
				} else {
					response.sendRedirect("dashboard.jsp");
				}
			} else {
				HttpSession session = request.getSession();
				session.setAttribute("msg", "Failed");

				// RequestDispatcher rd =
				// request.getRequestDispatcher("index.jsp");
				// rd.forward(request, response);
				response.sendRedirect("index.jsp");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.getWriter().write(
					"<h1>Connection failure</h1><hr><h4>You are not connected to the internet</h4><p>Please connect to the internet and try again</p><br>");
			e.printStackTrace();
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			response.getWriter().write(
					"<h1>Connection failure</h1><hr><h4>You are not connected to the internet</h4><p>Please connect to the internet and try again</p><br>");

			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
