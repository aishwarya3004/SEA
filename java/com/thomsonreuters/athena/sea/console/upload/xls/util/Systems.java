package com.thomsonreuters.athena.sea.console.upload.xls.util;


/**
 * @author Miro Zorboski
 * 
 */
public enum Systems {
	
	ATHENA;
	
	/**
	 * From string.
	 * 
	 * @param value the value
	 * 
	 * @return the system
	 * 
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Systems fromString(String value) throws IllegalArgumentException {
		return Systems.valueOf(value.toUpperCase());
	}
	
	/**
	 * Checks if is valid system.
	 * 
	 * @param value the value
	 * 
	 * @return true, if is valid system
	 */
	public static boolean isValidSystem(String value) {
		try {
			Systems.fromString(value);
		} catch (IllegalArgumentException e) {
			return false;
		}
		
		return true;
	}
	
}
