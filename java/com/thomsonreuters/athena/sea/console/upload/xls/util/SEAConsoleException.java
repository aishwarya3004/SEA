package com.thomsonreuters.athena.sea.console.upload.xls.util;

import java.util.Locale;
import java.util.ResourceBundle;

import com.thomsonreuters.athena.sea.ws.WSErrorCodes;

/**
 * @author Miro Zorboski
 * 
 */
public class SEAConsoleException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1266918317494888083L;
	
	/** The Constant BUNDLE_FILE_NAME. */
	private static final String BUNDLE_FILE_NAME = "ws_error_data";
	
	/** The error bundle. */
	private static ResourceBundle errorBundle;
	
	private String code;
	
	private String[] messageParameters;
	
	/**
	 * Instantiates a new sEA web service exception.
	 * 
	 * @param error the error
	 */
	public SEAConsoleException(WSErrorCodes error, String... messageParameters) {
		super();
		this.code = error.getValue();
		this.messageParameters = messageParameters;
	}

	/**
	 * Instantiates a new sEA web service exception.
	 * 
	 * @param code the code
	 */
	public SEAConsoleException(String code) {
		super();
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the messageParameters
	 */
	public String[] getMessageParameters() {
		return messageParameters;
	}

	/**
	 * @param messageParameters the messageParameters to set
	 */
	public void setMessageParameters(String[] messageParameters) {
		this.messageParameters = messageParameters;
	}
	
	/**
	 * Gets the bundle.
	 * 
	 * @return the bundle
	 */
	private static ResourceBundle getErrorBundle() {
		if (errorBundle == null) {
			errorBundle = ResourceBundle.getBundle(BUNDLE_FILE_NAME,
					Locale.ENGLISH);
		}
		return errorBundle;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		if (getErrorBundle().containsKey(code)) {
			String message = getErrorBundle().getString(code);
			int counter = 0;
			if (messageParameters != null) {
				for (Object o : messageParameters) {
					String value = o != null ? o.toString() : "";
					message = message.replaceAll("\\{" + (counter++) + "\\}", value);
				}
				return message;	
			}
		}
		
		return code;
	}
	
}
