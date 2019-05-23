package com.thomsonreuters.athena.sea.console.upload.xls.util;

/**
 * @author Miro Zorboski
 * 
 */
public enum UploadAction {
	
	CREATE,
	DELETE,
	MODIFY;
	
	/**
	 * From string.
	 * 
	 * @param value the value
	 * 
	 * @return the upload action
	 */
	public static UploadAction fromString(String value) {
		return UploadAction.valueOf(value);
	}
	
}
