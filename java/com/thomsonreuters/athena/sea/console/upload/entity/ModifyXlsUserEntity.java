package com.thomsonreuters.athena.sea.console.upload.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.thomsonreuters.athena.sea.entity.Field.Fields;

/**
 * @author Miro Zorboski
 * 
 */
public class ModifyXlsUserEntity extends AbstractXlsEntity {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3910821146661194057L;

	/**
	 * Instantiates a new modify xls user bean.
	 */
	public ModifyXlsUserEntity() {
		super();
	}

	/**
	 * Instantiates a new modify xls user bean.
	 * 
	 * @param system the system
	 * @param subscriberCode the subscriber code
	 * @param subAccount the sub account
	 * @param userId the user id
	 * @param billingNote the billing note
	 * @param comment the comment
	 * @param productsToAdd the products to add
	 * @param productsToRemove the products to remove
	 */
	public ModifyXlsUserEntity(String system, String subscriberCode,
			String subAccount, String userId, List<String> productsToAdd, 
			List<String> productsToRemove, String comment, String billingNote) {
		super();
		this.system = system;
		this.subscriberCode = subscriberCode;
		this.subAccount = subAccount;
		this.userId = userId;
		this.productsToAdd = productsToAdd;
		this.productsToRemove = productsToRemove;
		this.comment = comment;
		this.billingNote = billingNote;
	}

	/** The system. */
	private String system;
	
	/** The sub account. */
	private String subAccount;
	
	/** The user id. */
	private String userId;
	
	/** The products. */
	private List<String> productsToAdd = new ArrayList<String>();
	
	/** The products to remove. */
	private List<String> productsToRemove = new ArrayList<String>();;
	
	/** The billing note. */
	private String billingNote;
	
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

	/**
	 * @return the productsToAdd
	 */
	public List<String> getProductsToAdd() {
		return productsToAdd;
	}

	/**
	 * @param productsToAdd the productsToAdd to set
	 */
	public void setProductsToAdd(List<String> productsToAdd) {
		this.productsToAdd = productsToAdd;
	}

	/**
	 * @return the productsToRemove
	 */
	public List<String> getProductsToRemove() {
		return productsToRemove;
	}

	/**
	 * @param productsToRemove the productsToRemove to set
	 */
	public void setProductsToRemove(List<String> productsToRemove) {
		this.productsToRemove = productsToRemove;
	}

	/**
	 * @return the billingNote
	 */
	public String getBillingNote() {
		return billingNote;
	}

	/**
	 * @param billingNote the billingNote to set
	 */
	public void setBillingNote(String billingNote) {
		this.billingNote = billingNote;
	}
	
}
