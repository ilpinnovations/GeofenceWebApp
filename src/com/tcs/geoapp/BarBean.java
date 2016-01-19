package com.tcs.geoapp;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import org.apache.taglibs.standard.lang.jstl.parser.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

public class BarBean {
	public JSONObject getBarDetails(String gcm_id, String startDate, String endDate) {
		JSONObject mainObject = new JSONObject();
		Connection con = null;
		java.sql.Date startD = null;
		java.sql.Date endD = null;
		System.out.println(startDate);
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date parsed1 = null;
			java.util.Date parsed2 = null;
			parsed1 = format.parse(startDate);
			parsed2 = format.parse(endDate);
			startD=new Date(parsed1.getTime());
			endD=new Date(parsed2.getTime());
			System.out.println(gcm_id);
			System.out.println(startD);
			System.out.println(endD);
			con=DatabaseUtil.getConnection();
			PreparedStatement st = con
					.prepareStatement("select geo_date,count_entry from geo_analytics where gcm_id=? and geo_date between ? and ?");
			st.setString(1, gcm_id);
			st.setDate(2, startD);
			st.setDate(3, endD);
			ResultSet rSet = st.executeQuery();

			JSONObject triggerInfo = null;
			JSONArray triggerArray = new JSONArray();
			
				while (rSet.next()) {
					System.out.println("in while");
					Date geo_date = rSet.getDate(1);
					int count_entry = rSet.getInt(2);
					System.out.println("c"+count_entry);
					triggerInfo = new JSONObject();
					triggerInfo.put("geo_date", geo_date);
					triggerInfo.put("count_entry", count_entry);
					triggerArray.put(triggerInfo);

					mainObject.put("triggerData", triggerArray);

				}// end of while
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(mainObject);
		return mainObject;
	}
}
