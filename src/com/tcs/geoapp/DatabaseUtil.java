
package com.tcs.geoapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sun.mail.iap.Response;

public class DatabaseUtil {
	static Connection cnn;
	static String HOST_ADDRESS = "192.168.4.236:3306";
	static String USERNAME = "ilpinnovations";
	static String PASSWORD = "ILPinnovations2@";
	static String DATABASE_NAME = "geodb2";
	//static String HOST_ADDRESS = "localhost";
	//static String USERNAME = "root";
	//static String PASSWORD = "";
	//static String DATABASE_NAME = "geodb";
	static{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			System.out.println("Connection could not be established");
			e.printStackTrace();
		} 
	}
	
	public static Connection getConnection(){
		try{
			if(cnn!=null){
				return cnn;
			}
			else{
				cnn = DriverManager.getConnection("jdbc:mysql://" +HOST_ADDRESS+ "/" + DATABASE_NAME,USERNAME,PASSWORD); 
				System.out.println("connected");
			}
		}catch(Exception e){
			
			System.out.println("unable to connect to database");
			e.printStackTrace();
		}
		return cnn;		
	}
	public static int getRowCount(ResultSet resultSet) {
	    if (resultSet == null) {
	        return 0;
	    }
	    try {
	        resultSet.last();
	        return resultSet.getRow();
	    } catch (SQLException exp) {
	        exp.printStackTrace();
	    } finally {
	        try {
	            resultSet.beforeFirst();
	        } catch (SQLException exp) {
	            exp.printStackTrace();
	        }
	    }
	    return 0;
	}
}
