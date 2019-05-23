package com.thomsonreuters.athena.sea.console.upload.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.thomsonreuters.athena.sea.entity.Field.Fields;

/**
 * @author Miro Zorboski
 * 
 */
public abstract class AbstractXlsEntity implements Serializable, Comparable<AbstractXlsEntity> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1370287475556995120L;
	
	/** The xls row number. */
	protected Integer xlsRowNumber;
	
	/** The subscriber code. */
	protected String subscriberCode;

	/** The comment. */
	protected String comment;

	/** The transaction id. */
	protected Integer transactionId = null;
	
	/** The valid. */
	protected Boolean valid;
	
	/** The validation errors. */
	protected List<String> validationErrors = new ArrayList<String>();
	
	/**
	 * Instantiates a new abstract xls bean.
	 */
	public AbstractXlsEntity() {
		super();
	}
	
	/**
	 * Gets the fields map.
	 * 
	 * @return the fields map
	 */
	public abstract HashMap<Fields, String> getFieldsMap();
	
	/**
	 * @return the xlsRowNumber
	 */
	public Integer getXlsRowNumber() {
		return xlsRowNumber;
	}

	/**
	 * @param xlsRowNumber the xlsRowNumber to set
	 */
	public void setXlsRowNumber(Integer xlsRowNumber) {
		this.xlsRowNumber = xlsRowNumber;
	}

	/**
	 * @return the subscriberCode
	 */
	public String getSubscriberCode() {
		return subscriberCode;
	}

	/**
	 * @param subscriberCode the subscriberCode to set
	 */
	public void setSubscriberCode(String subscriberCode) {
		this.subscriberCode = subscriberCode;
	}

	/**
	 * Gets the comment.
	 * 
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Sets the comment.
	 * 
	 * @param comment the new comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Gets the transaction id.
	 * 
	 * @return the transaction id
	 */
	public Integer getTransactionId() {
		return transactionId;
	}

	/**
	 * Sets the transaction id.
	 * 
	 * @param transactionId the new transaction id
	 */
	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}
	
	/**
	 * Checks if is valid.
	 * 
	 * @return true, if is valid
	 */
	public Boolean isValid() {
		return valid;
	}

	/**
	 * Sets the valid.
	 * 
	 * @param valid the new valid
	 */
	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	/**
	 * Gets the validation errors.
	 * 
	 * @return the validation errors
	 */
	public List<String> getValidationErrors() {
		return validationErrors;
	}

	/**
	 * Sets the validation errors.
	 * 
	 * @param validationErrors the new validation errors
	 */
	public void setValidationErrors(List<String> validationErrors) {
		this.validationErrors = validationErrors;
	}
	
	/**
	 * Adds the error.
	 * 
	 * @param error the error
	 */
	public void addError(String error) {
		this.validationErrors.add(error);
	}
	
	/**
	 * Checks for errors.
	 * 
	 * @return true, if successful
	 */
	public boolean hasErrors() {
		return this.validationErrors.size() > 0 ? true : false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AbstractXlsEntity o) {
		return this.isValid().compareTo(o.isValid());
	}
	
}
