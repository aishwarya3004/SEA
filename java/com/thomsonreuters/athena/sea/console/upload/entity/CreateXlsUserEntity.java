package com.thomsonreuters.athena.sea.console.upload.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.thomsonreuters.athena.sea.entity.Field.Fields;

/**
 * @author Miro Zorboski
 * 
 */
public class CreateXlsUserEntity extends AbstractXlsEntity {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4003348554863567033L;

	/**
	 * Instantiates a new xls user.
	 */
	public CreateXlsUserEntity() {
		super();
	}
	
	/**
	 * Instantiates a new xls user.
	 * 
	 * @param xlsRowNumber the xls row number
	 * @param subscriberCode the subscriber code
	 * @param subAccount the sub account
	 * @param coordinator the coordinator
	 * @param orderNumber the order number
	 * @param email the email
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param userLogin the user login
	 * @param password the password
	 * @param athenaLogin the athena login
	 * @param athenaPassword the athena password
	 * @param server the server
	 * @param comment the comment
	 * @param seedAccount the seed account
	 * @param akr the akr
	 * @param products the products
	 * @param billingNote the billing note
	 */
	public CreateXlsUserEntity(Integer xlsRowNumber, String subscriberCode,
			String subAccount, String coordinator, String orderNumber,
			String email, String firstName, String lastName, String userLogin,
			String password, String athenaLogin, String athenaPassword,
			String server, String comment, String seedAccount, String akr,
			List<String> products, String billingNote) {
		super();
		this.xlsRowNumber = xlsRowNumber;
		this.subscriberCode = subscriberCode;
		this.subAccount = subAccount;
		this.coordinator = coordinator;
		this.orderNumber = orderNumber;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userLogin = userLogin;
		this.password = password;
		this.athenaLogin = athenaLogin;
		this.athenaPassword = athenaPassword;
		this.server = server;
		this.comment = comment;
		this.seedAccount = seedAccount;
		this.akr = akr;
		this.products = products;
		this.billingNote = billingNote;
	}
	
	/** The sub account. */
	private String subAccount;
	
	/** The order number. */
	private String orderNumber;
	
	/** The email. */
	private String email;
	
	/** The first name. */
	private String firstName;
	
	/** The last name. */
	private String lastName;
	
	/** The user login. */
	private String userLogin;
	
	/** The password. */
	private String password;
	
	/** The athena login. */
	private String athenaLogin;
	
	/** The athena password. */
	private String athenaPassword;
	
	/** The server. */
	private String server;
	
	/** The seed account. */
	private String seedAccount;
	
	/** The akr. */
	private String akr;
	
	/** The products. */
	private List<String> products = new ArrayList<String>();
	
	/** The coordinator. */
	private String coordinator;
	
	/** The billing note. */
	private String billingNote;
	
	/* (non-Javadoc)
	 * @see com.thomsonreuters.athena.sea.console.upload.entity.AbstractXlsEntity#getFieldsMap()
	 */
	public HashMap<Fields, String> getFieldsMap() {
		HashMap<Fields, String> userFields = new HashMap<Fields, String>();
		
		userFields.put(Fields.Subscriber, this.subscriberCode);
		userFields.put(Fields.SubAccount, this.subAccount);
		userFields.put(Fields.Order, this.orderNumber);
		userFields.put(Fields.Email, this.email);
		userFields.put(Fields.FirstName, this.firstName);
		userFields.put(Fields.LastName, this.lastName);
		userFields.put(Fields.TsaLogin, this.userLogin);
		userFields.put(Fields.TsaPassword, this.password);
		userFields.put(Fields.AthenaLogin, this.athenaLogin);
		userFields.put(Fields.AthenaPassword, this.athenaPassword);
		userFields.put(Fields.Server, this.server);
		userFields.put(Fields.SeedAccount, this.seedAccount);
		userFields.put(Fields.AKR, this.akr);
		
		return userFields;
	}

	/**
	 * Gets the order number.
	 * 
	 * @return the order number
	 */
	public String getOrderNumber() {
		return orderNumber;
	}

	/**
	 * Sets the order number.
	 * 
	 * @param orderNumber the new order number
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * Gets the email.
	 * 
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 * 
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the first name.
	 * 
	 * @return the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name.
	 * 
	 * @param firstName the new first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the last name.
	 * 
	 * @return the last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name.
	 * 
	 * @param lastName the new last name
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Gets the user login.
	 * 
	 * @return the user login
	 */
	public String getUserLogin() {
		return userLogin;
	}

	/**
	 * Sets the user login.
	 * 
	 * @param userLogin the new user login
	 */
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	/**
	 * Gets the password.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 * 
	 * @param password the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the athena login.
	 * 
	 * @return the athena login
	 */
	public String getAthenaLogin() {
		return athenaLogin;
	}

	/**
	 * Sets the athena login.
	 * 
	 * @param athenaLogin the new athena login
	 */
	public void setAthenaLogin(String athenaLogin) {
		this.athenaLogin = athenaLogin;
	}

	/**
	 * Gets the athena password.
	 * 
	 * @return the athena password
	 */
	public String getAthenaPassword() {
		return athenaPassword;
	}

	/**
	 * Sets the athena password.
	 * 
	 * @param athenaPassword the new athena password
	 */
	public void setAthenaPassword(String athenaPassword) {
		this.athenaPassword = athenaPassword;
	}

	/**
	 * Gets the server.
	 * 
	 * @return the server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Sets the server.
	 * 
	 * @param server the new server
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * Gets the seed account.
	 * 
	 * @return the seed account
	 */
	public String getSeedAccount() {
		return seedAccount;
	}

	/**
	 * Sets the seed account.
	 * 
	 * @param seedAccount the new seed account
	 */
	public void setSeedAccount(String seedAccount) {
		this.seedAccount = seedAccount;
	}

	/**
	 * Gets the akr.
	 * 
	 * @return the akr
	 */
	public String getAkr() {
		return akr;
	}

	/**
	 * Sets the akr.
	 * 
	 * @param akr the new akr
	 */
	public void setAkr(String akr) {
		this.akr = akr;
	}

	/**
	 * Gets the products.
	 * 
	 * @return the products
	 */
	public List<String> getProducts() {
		return products;
	}

	/**
	 * Sets the products.
	 * 
	 * @param products the new products
	 */
	public void setProducts(List<String> products) {
		this.products = products;
	}

	/**
	 * Gets the sub account.
	 * 
	 * @return the sub account
	 */
	public String getSubAccount() {
		return subAccount;
	}

	/**
	 * Sets the sub account.
	 * 
	 * @param subAccont the new sub account
	 */
	public void setSubAccount(String subAccont) {
		this.subAccount = subAccont;
	}

	/**
	 * Gets the coordinator.
	 * 
	 * @return the coordinator
	 */
	public String getCoordinator() {
		return coordinator;
	}

	/**
	 * Sets the coordinator.
	 * 
	 * @param coordinator the new coordinator
	 */
	public void setCoordinator(String coordinator) {
		this.coordinator = coordinator;
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
