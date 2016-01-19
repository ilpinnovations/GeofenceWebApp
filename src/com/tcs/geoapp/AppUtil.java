package com.tcs.geoapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AppUtil {
	// to be filled for forgot password request
	public static String USER_NAME = "abc@abc.com";
	public static String PASSWORD = "testpassword";
	public static char[] passwordChars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', '0', '@', '#', '$', '%', '^', '&', '*', '-', '_', '=',
			'+' };

	public static final List<Long> times = Arrays.asList(
			TimeUnit.DAYS.toMillis(365), TimeUnit.DAYS.toMillis(30),
			TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(1),
			TimeUnit.MINUTES.toMillis(1), TimeUnit.SECONDS.toMillis(1));
	public static final List<String> timesString = Arrays.asList("year",
			"month", "day", "hour", "minute", "second");

	public static String toDuration(long duration) {

		StringBuffer res = new StringBuffer();
		for (int i = 0; i < times.size(); i++) {
			Long current = times.get(i);
			long temp = duration / current;
			if (temp > 0) {
				res.append(temp).append(" ").append(timesString.get(i))
						.append(temp > 1 ? "s" : "").append(" ago");
				break;
			}
		}
		if ("".equals(res.toString()))
			return "0 second ago";
		else
			return res.toString();
	}

	public static boolean requestPassword(String email) {

		System.out.println("Inside request password");
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
						return new PasswordAuthentication(username, password);
					}
				});

		UserBean userObj = AppUtil.getUser(email);

		try {

			System.out.println("Inside request password try");
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(USER_NAME));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(email));
			message.setSubject("Go Geofence - Password Reset Request");
			String pwd = generatePassword();

			message.setContent(
					"Dear <b>"
							+ userObj.getFirstName()
							+ " "
							+ userObj.getLastName()
							+ "<b>, <br><br> Your Password has been reset to : "
							+ pwd
							+ " <br><br><b>Administrator<br>Go Geofence<br>Tata Consultancy Services</b> ",
					"text/html");

			Connection cnn = DatabaseUtil.getConnection();
			PreparedStatement st = cnn
					.prepareStatement("UPDATE geo_user SET PASSWORD = ? WHERE USER_NAME = ?");
			System.out.println(userObj.getUserName());
			st.setString(1, pwd);
			st.setString(2, userObj.getUserName());
			int count = st.executeUpdate();
			if (count != 0) {
				System.out.println("Done");
			} else {
				System.out.println("Error in reset password");
			}

			Transport.send(message);
			return true;

		} catch (Exception e) {

		}

		return false;
	}

	public static UserBean getUser(String email) {
		UserBean userObj = null;
		try {

			Connection cnn = DatabaseUtil.getConnection();

			PreparedStatement ps = cnn
					.prepareStatement("SELECT * FROM geo_user WHERE EMAIL = ?");
			ps.setString(1, email);
			ResultSet rSet = ps.executeQuery();

			if (rSet.next()) {
				int userID = rSet.getInt("USER_ID");
				String userName = rSet.getString("USER_NAME");
				String password = rSet.getString("PASSWORD");
				String firstName = rSet.getString("FIRST_NAME");
				String lastName = rSet.getString("LAST_NAME");
				String dob = rSet.getString("DOB");
				String tokenID = rSet.getString("TOKEN_ID");
				userObj = new UserBean(userID, userName, password, firstName,
						lastName, email, dob, tokenID);
			}

		} catch (Exception e) {

		}
		return userObj;
	}

	public static String generatePassword() {
		System.out.println("Inside generate password");
		Random rand = new Random();
		String genPassword = "";

		for (int i = 0; i < 10; i++) {
			int index = rand.nextInt(passwordChars.length);
			System.out.println("Random Number :" + index);
			System.out.println("Random Char :" + passwordChars[index]);
			genPassword += String.valueOf(passwordChars[index]);
		}

		System.out.println("Generated Password " + genPassword);
		return genPassword;

	}

}
