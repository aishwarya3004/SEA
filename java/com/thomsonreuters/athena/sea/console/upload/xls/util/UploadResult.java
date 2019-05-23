package com.thomsonreuters.athena.sea.console.upload.xls.util;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.athena.sea.console.upload.entity.AbstractXlsEntity;

/**
 * @author Miro Zorboski
 * 
 */
public class UploadResult<T extends AbstractXlsEntity> {
	
	/** The validation errors found. */
	private boolean validationErrorsFound;
	
	/** The transaction erors found. */
	private boolean transactionErorsFound;
	
	/** The results. */
	private Map<String, List<T>> results;

	/**
	 * Instantiates a new upload result.
	 * 
	 * @param results the results
	 * @param validationErrorsFound the validation errors found
	 * @param transactionErorsFound the transaction erors found
	 */
	public UploadResult(Map<String, List<T>> results, boolean validationErrorsFound, boolean transactionErorsFound) {
		super();
		this.validationErrorsFound = validationErrorsFound;
		this.transactionErorsFound = transactionErorsFound;
		this.results = results;
	}

	/**
	 * @return the errorsFound
	 */
	public boolean isValidationErrorsFound() {
		return validationErrorsFound;
	}

	/**
	 * @param errorsFound the errorsFound to set
	 */
	public void setValidationErrorsFound(boolean errorsFound) {
		this.validationErrorsFound = errorsFound;
	}

	/**
	 * @return the transactionErorsFound
	 */
	public boolean isTransactionErorsFound() {
		return transactionErorsFound;
	}

	/**
	 * @param transactionErorsFound the transactionErorsFound to set
	 */
	public void setTransactionErorsFound(boolean transactionErorsFound) {
		this.transactionErorsFound = transactionErorsFound;
	}

	/**
	 * @return the results
	 */
	public Map<String, List<T>> getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(Map<String, List<T>> results) {
		this.results = results;
	}
	
}
