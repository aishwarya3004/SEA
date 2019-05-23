package com.thomsonreuters.athena.sea.console.upload.xls.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Miro Zorboski
 * 
 */
public class UploadErrors {
	
	private List<SEAConsoleException> errors = new ArrayList<SEAConsoleException>();

	/**
	 * Gets the errors.
	 * 
	 * @return the errors
	 */
	public List<SEAConsoleException> getErrors() {
		return errors;
	}

	/**
	 * Sets the errors.
	 * 
	 * @param errors the errors to set
	 */
	public void setErrors(List<SEAConsoleException> errors) {
		this.errors = errors;
	}
	
	/**
	 * Adds the error.
	 * 
	 * @param error the error
	 */
	public void addError(SEAConsoleException error) {
		errors.add(error);
	}
	
	/**
	 * Merge errors.
	 * 
	 * @param uploadErrors the upload errors
	 */
	public void mergeErrors(UploadErrors uploadErrors) {
		this.errors.addAll(uploadErrors.getErrors());
	}
	
	/**
	 * Checks for errors.
	 * 
	 * @return true, if successful
	 */
	public boolean hasErrors() {
		return errors.size() > 0 ? true: false;
	}
	
	/**
	 * Gets the error messages.
	 * 
	 * @return the error messages
	 */
	public List<String> getErrorMessages() {
		List<String> errorsText = new ArrayList<String>();
		for(SEAConsoleException error : getErrors()) {
			errorsText.add(error.getMessage());
		}
	    
	    return errorsText;
	}
	
}
