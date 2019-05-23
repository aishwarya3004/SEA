package com.thomsonreuters.athena.sea.console.upload;

import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadAction;

/**
 * @author Miro Zorboski
 * 
 */
public class UploadFactory {

	/**
	 * Gets the upload bean.
	 * 
	 * @param uploadAction the upload action
	 * 
	 * @return the upload bean
	 * 
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	public static AbstractUploadBean getUploadBean(UploadAction uploadAction) throws Exception {
		switch (uploadAction) {
			case CREATE:
				return new CreateUsersBean(uploadAction);
			case DELETE:
				return new DeleteUsersBean(uploadAction);
			case MODIFY:
				return new ModifyUsersBean(uploadAction);
			default:
				throw new Exception("Unsupported action type");
		}
	}
	
}