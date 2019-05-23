package com.thomsonreuters.athena.sea.console.upload.entity;

import java.util.HashMap;

import com.thomsonreuters.athena.sea.entity.Field.Fields;

/**
 * @author Miro Zorboski
 * 
 */
public class DeleteXlsUserEntity extends AbstractXlsEntity {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1204865594921804407L;

	/**
	 * Instantiates a new delete xls user bean.
	 */
	public DeleteXlsUserEntity() {
		super();
	}

	/**
	 * Instantiates a new delete xls user bean.
	 * 
	 * @param system the system
	 * @param subscriberCode the subscriber code
	 * @param subAccount the sub account
	 * @param userId the user id
	 * @param comment the comment
	 */
	public DeleteXlsUserEntity(String system, String subscriberCode, String subAccount, String userId, String comment) {
		super();
		this.system = system;
		this.subscriberCode = subscriberCode;
		this.subAccount = subAccount;
		this.userId = userId;
		this.comment = comment;
	}

	/** The system. */
	private String system;
	
	/** The sub account. */
	private String subAccount;
	
	/** The user id. */
	private String userId;
	
	/* (non-Javadoc)
	 * @see com.thomsonreuters.athena.sea.console.upload.entity.AbstractXlsEntity#getFieldsMap()
	 */
	public HashMap<Fields, String> getFieldsMap() {
		HashMap<Fields, String> userFields = new HashMap<Fields, String>();
		
		userFields.put(Fields.Subscriber, this.subscriberCode);
		userFields.put(Fields.SubAccount, this.subAccount);
		userFields.put(Fields.SubId, this.userId);
		
		return userFields;
	}

	/**
	 * @return the system
	 */
	public String getSystem() {
		return system;
	}

	/**
	 * @param system the system to set
	 */
	public void setSystem(String system) {
		this.system = system;
	}

	/**
	 * @return the subAccount
	 */
	public String getSubAccount() {
		return subAccount;
	}

	/**
	 * @param subAccount the subAccount to set
	 */
	public void setSubAccount(String subAccount) {
		this.subAccount = subAccount;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
