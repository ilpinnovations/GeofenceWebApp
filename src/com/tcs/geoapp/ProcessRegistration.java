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
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ProcessRegistration
 */
public class ProcessRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProcessRegistration() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doProcess(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doProcess(request, response);
	}
	
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//prevents direct access to the servlet
		String referer = request.getHeader("referer");
		if(referer==null){
			response.sendRedirect("unauthorisedAccess.jsp");
			return ;
		}
		
		
		System.out.println("Inside");
		
		String userName = request.getParameter("userName");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String dob = request.getParameter("dob");
		
		
		GregorianCalendar calendar = new GregorianCalendar();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastVisit = format.format(new Date(calendar.getTime().getTime()));
		String tokenID = request.getSession().getId();
		
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");
		
		

		
		
		
		try {
			Connection cnn = DatabaseUtil.getConnection();
			
			PreparedStatement st = cnn.prepareStatement("INSERT INTO geo_user (USER_NAME,FIRST_NAME,LAST_NAME,EMAIL,DOB,LAST_VISIT,TOKEN_ID,PASSWORD) VALUES(?,?,?,?,?,?,?,?)");

			st.setString(1,userName );
			st.setString(2, firstName);
			st.setString(3, lastName);
			st.setString(4, email);
			st.setString(5, dob);
			st.setString(6, lastVisit);
			st.setString(7, tokenID);
			st.setString(8, password);
 
			int count = st.executeUpdate();
			
			if(count!=0){
				System.out.println("User Registered Successfully!");
				request.setAttribute("source", "regPage");
				request.getRequestDispatcher("AuthUser").forward(request, response);
			}else{
				System.out.println("User Registration Failed!");
				HttpSession session = request.getSession();
				session.setAttribute("msg", "Failed");
				request.getRequestDispatcher("index.jsp").forward(request, response);
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("User Registration Failed!");
			HttpSession session = request.getSession();
			session.setAttribute("msg", "Failed");
			request.getRequestDispatcher("newUserSuccess.jsp").forward(request, response);
		}
		
		
	}
}
