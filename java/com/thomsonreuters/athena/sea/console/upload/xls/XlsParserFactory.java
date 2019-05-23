package com.thomsonreuters.athena.sea.console.upload.xls;

import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadAction;

/**
 * @author Miro Zorboski
 * 
 */
public class XlsParserFactory {
	
	/**
	 * Gets the parser.
	 * 
	 * @param uploadAction the upload action
	 * 
	 * @return the parser
	 * 
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	public static AbstractXlsParser getParser(UploadAction uploadAction) throws Exception {
		switch (uploadAction) {
			case CREATE:
				return new CreateXlsParser();
			case DELETE:
				return new DeleteXlsParser();
			case MODIFY:
				return new ModifyXlsParser();
			default:
				throw new Exception("Unsupported action type");
		}
	}
	
}
