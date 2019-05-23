package com.thomsonreuters.athena.sea.console.upload.xls.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Miro Zorboski
 * 
 */
public class Utils {
	
	/**
	 * Gets the current date.
	 * 
	 * @return the current date
	 */
	public static Date getCurrentDate() {
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}
	
	/**
	 * Gets the current date time.
	 * 
	 * @return the current date time
	 */
	public static Date getCurrentDateTime() {
		Calendar calendar = Calendar.getInstance();
		
		return calendar.getTime();
	}
	
	/**
	 * Checks if is empty.
	 * 
	 * @param value the value
	 * 
	 * @return true, if is empty
	 */
	public static boolean isEmpty(String value) {
		if(value == null || value.equals("")) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if is empty.
	 * 
	 * @param value the value
	 * 
	 * @return true, if is empty
	 */
	public static boolean isEmpty(List<Object> value) {
		if(value == null || value.isEmpty()) {
			return false;
		}
		
		return true;
	}
	
}
