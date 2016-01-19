package com.tcs.geoapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class UpdateProfile
 */
public class UpdateProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;
 	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String referer = request.getHeader("referer");
		if(referer==null){
			response.sendRedirect("unauthorisedAccess.jsp");
			return ;
		}

		String userName = request.getParameter("userName");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String dob = request.getParameter("dob");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");
		System.err.println("Parameters");
		System.out.println(userName + firstName + lastName + dob + email);
		
		try {
			Connection cnn = DatabaseUtil.getConnection();
			
			PreparedStatement st = cnn.prepareStatement("UPDATE geo_user SET FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ?, DOB = ? WHERE USER_NAME = ?");

			st.setString(1, firstName);
			st.setString(2, lastName);
			st.setString(3, email);
			st.setString(4, dob);
			st.setString(5, userName);
 
			int count = st.executeUpdate();
			
			if(count!=0){
				System.out.println("User Details Updated Successfully!");
				
				cnn = DatabaseUtil.getConnection();
				
				st = cnn.prepareStatement("SELECT USER_ID,USER_NAME,FIRST_NAME,LAST_NAME,EMAIL,DOB,LAST_VISIT,TOKEN_ID FROM geo_user WHERE USER_NAME = ?");
			
				
				st.setString(1, request.getParameter("userName"));
				
				ResultSet rSet = st.executeQuery();
				
				if(rSet.next()){
					HttpSession session = request.getSession();

					session.setAttribute("userID", rSet.getInt("USER_ID"));
					session.setAttribute("userName", rSet.getString("USER_NAME"));
					session.setAttribute("firstName", rSet.getString("FIRST_NAME"));
					session.setAttribute("lastName", rSet.getString("LAST_NAME"));
					session.setAttribute("email", rSet.getString("EMAIL"));
					session.setAttribute("dob", rSet.getString("DOB"));
					session.setAttribute("tokenID", rSet.getString("TOKEN_ID"));
				}
				
				request.removeAttribute("msg");
				request.setAttribute("msg", "success");
				request.getRequestDispatcher("viewProfile.jsp").forward(request, response);
			}else{
				System.out.println("User Details Updation Failed!");
				request.removeAttribute("msg");
				request.setAttribute("msg", "failed");
				request.getRequestDispatcher("viewProfile.jsp").forward(request, response);
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
		
	}
	
	
}
