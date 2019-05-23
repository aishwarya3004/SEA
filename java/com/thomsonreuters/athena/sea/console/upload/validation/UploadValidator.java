package com.thomsonreuters.athena.sea.console.upload.validation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJBException;
import javax.naming.NamingException;

import com.thomsonreuters.athena.sea.console.HttpFilter;
import com.thomsonreuters.athena.sea.console.upload.entity.CreateXlsUserEntity;
import com.thomsonreuters.athena.sea.console.upload.xls.util.SEAConsoleException;
import com.thomsonreuters.athena.sea.console.upload.xls.util.Systems;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadErrors;
import com.thomsonreuters.athena.sea.entity.Coordinator;
import com.thomsonreuters.athena.sea.entity.Product;
import com.thomsonreuters.athena.sea.entity.Product.ProductType;
import com.thomsonreuters.athena.sea.entity.Site;
import com.thomsonreuters.athena.sea.entity.SiteAccount;
import com.thomsonreuters.athena.sea.entity.SiteReference.SiteReferenceStatus;
import com.thomsonreuters.athena.sea.entity.Transaction.TransactionAction;
import com.thomsonreuters.athena.sea.entity.User;
import com.thomsonreuters.athena.sea.entity.User.UserStatus;
import com.thomsonreuters.athena.sea.service.CoordinatorService;
import com.thomsonreuters.athena.sea.service.ProductService;
import com.thomsonreuters.athena.sea.service.ServiceLocator;
import com.thomsonreuters.athena.sea.service.ServiceLocator.Services;
import com.thomsonreuters.athena.sea.service.SiteService;
import com.thomsonreuters.athena.sea.service.TransactionService;
import com.thomsonreuters.athena.sea.service.UserService;
import com.thomsonreuters.athena.sea.service.ValidationTransactionService;
import com.thomsonreuters.athena.sea.validation.TransactionsFormWS;
import com.thomsonreuters.athena.sea.view.entity.ViewProduct;
import com.thomsonreuters.athena.sea.view.entity.ViewSubscriber;
import com.thomsonreuters.athena.sea.ws.WSErrorCodes;
import com.thomsonreuters.athena.services.IUserManagementService;
import com.thomsonreuters.athena.services.IValidationService;
import com.thomsonreuters.athena.services.ServerInfo;
import com.thomsonreuters.athena.services.auth.ICoordinator;
import com.thomsonreuters.athena.services.validation.CustomServiceException.CustomErrorCode;
import com.thomsonreuters.athena.services.validation.ErrorCode;
import com.thomsonreuters.athena.services.validation.ServiceException;

/**
 * @author Radisa.Pantic
 *
 */
public class UploadValidator {
	
	private Map<String, List<String>> validServers = new HashMap<String, List<String>>();
	
	private Map<String, Site> validSubscribers = new HashMap<String, Site>();
	
	/**
	 * Instantiates a new user validation.
	 * 
	 * @throws Exception the exception
	 */
	public UploadValidator() throws Exception {

	}
	
	/**
	 * Gets the validation service.
	 * 
	 * @return the validation service
	 * 
	 * @throws NamingException the naming exception
	 */
	protected IValidationService getValidationService() throws NamingException {
		ICoordinator coordinator = HttpFilter.getCoordinator();
		return com.thomsonreuters.athena.services.ServiceLocator.getInstance(coordinator.getLogin(), coordinator.getPassword()).getValidationService();
	}
	
	/**
	 * Gets the product service.
	 * 
	 * @return the product service
	 * 
	 * @throws NamingException the naming exception
	 */
	protected ProductService getProductService() throws NamingException {
		return (ProductService) ServiceLocator.getInstance().getService(Services.ProductServices);
	}
	
	/**
	 * Gets the site service.
	 * 
	 * @return the site service
	 * 
	 * @throws NamingException the naming exception
	 */
	protected SiteService getSiteService() throws NamingException {
		return (SiteService) ServiceLocator.getInstance().getService(Services.SiteServices);
	}
	
	/**
	 * Gets the user service.
	 * 
	 * @return the user service
	 * 
	 * @throws NamingException the naming exception
	 */
	protected UserService getUserService() throws NamingException {
		return (UserService) ServiceLocator.getInstance().getService(Services.UserServices);
	}
	
	/**
	 * Gets the coordinator service.
	 * 
	 * @return the coordinatorService
	 * 
	 * @throws NamingException the naming exception
	 */
	protected CoordinatorService getCoordinatorService() throws NamingException {
		return (CoordinatorService) ServiceLocator.getInstance().getService(Services.CoordinatorServices);
	}
	
	/**
	 * Gets the transaction service.
	 * 
	 * @return the transaction service
	 * 
	 * @throws NamingException the naming exception
	 */
	protected TransactionService getTransactionService() throws NamingException {
		return (TransactionService) ServiceLocator.getInstance().getService(Services.TransactionServices);
	}

	/**
	 * Gets the validation transaction service.
	 * 
	 * @return the validationTransactionService
	 * 
	 * @throws NamingException the naming exception
	 */
	protected ValidationTransactionService getValidationTransactionService() throws NamingException {
		return (ValidationTransactionService) ServiceLocator.getInstance().getService(Services.ValidationTransactionServices);
	}
	
	/**
	 * Gets the user management service.
	 * 
	 * @return the user management service
	 * 
	 * @throws NamingException the naming exception
	 */
	protected IUserManagementService getUserManagementService() throws NamingException {
		ICoordinator coordinator = HttpFilter.getCoordinator();
		return com.thomsonreuters.athena.services.ServiceLocator.getInstance(coordinator.getLogin(), coordinator.getPassword()).getUserManagementService();
	}
	
	/**
	 * Validate system.
	 * 
	 * @param system the system
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateSystem(String system) {
		UploadErrors errors = new UploadErrors();
		
		if(system != null && !system.equals("")) {
			if(!Systems.isValidSystem(system)) {
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrConsoleInvalidSystem));
			}
		}
		
		return errors;
	}

	/**
	 * Validate email.
	 * 
	 * @param email the email
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateEmail(String email){
		UploadErrors errors = new UploadErrors();
		
		if(email != null && !"".equals(email)){
			try {
				getValidationService().validateEmail(email);
			} catch (Exception e) {
				e.printStackTrace();
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidEmail));
			}
		}
		
		return errors;
	}
	
	/**
	 * Validate first name.
	 * 
	 * @param firstName the first name
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateFirstName(String firstName) {
		UploadErrors errors = new UploadErrors();
		
		if (firstName.contains(" ")){
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidFirstName));
		} else {
			 if (validateMandatory(firstName)) {
				if (!validateFirst(firstName)){
					errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidFirstName));
				}
			 } else {
				 errors.addError(new SEAConsoleException(WSErrorCodes.ErrMandatoryFirstName));
			 }
		}
		
		return errors;
	}
	
	/**
	 * Validate last name.
	 * 
	 * @param lastName the last name
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateLastName(String lastName) {
		UploadErrors errors = new UploadErrors();
		
		if (validateMandatory(lastName)) {
			if (validateMinChars(lastName) && validateName(lastName)) {
				if (!validateName(lastName)) {
					errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidLastName));
				}
			} else {
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidLengthLastName));
			}
		} else {
			 errors.addError(new SEAConsoleException(WSErrorCodes.ErrMandatoryLastName));
		}
		
		return errors;
	}
	
	/**
	 * Validate mandatory.
	 * 
	 * @param test the test
	 * 
	 * @return true, if successful
	 */
	public boolean validateMandatory(String test){
		if (test == null || test != null && test.isEmpty() ){
			return false;
		}
		return true;
	}
	
	/**
	 * Validate min chars.
	 * 
	 * @param test the test
	 * 
	 * @return true, if successful
	 */
	public boolean validateMinChars(String test) {
		if (!validateEmpty(test)){
			if (test.length() < 2 )
				return false;
		}
		return true;
	}
	
	/**
	 * Validate max chars.
	 * 
	 * @param test the test
	 * 
	 * @return true, if successful
	 */
	public boolean validateMaxChars (String test) {
		if (!validateEmpty(test)) {
			if (test.length() > 255)
				return false;
		}
		return true;
	}
	
	/**
	 * Validate name.
	 * 
	 * @param name the name
	 * 
	 * @return true, if successful
	 */
	public boolean validateName(String name) {
		if (!validateEmpty(name)) {
			if (!name.matches("[a-zA-Z0-9\\s\\-\\.]{2,15}")){
				return false;
			}
		}
		return true;
	}
    
	/**
	 * Validate first.
	 * 
	 * @param test the test
	 * 
	 * @return true, if successful
	 */
	public boolean validateFirst(String test) {
		if (!validateEmpty(test)) {
			if (!test.matches("[a-zA-Z0-9\\.]{2,14}")){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Validate empty.
	 * 
	 * @param test the test
	 * 
	 * @return true, if successful
	 */
	public static boolean validateEmpty(String test){
	   return test == null || test.trim().equals("");
	}
	
	/**
	 * Validate tsa id.
	 * 
	 * @param userLogin the user login
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateTsaId(String userLogin) {
		UploadErrors errors = new UploadErrors();
		
		if (validateMandatory(userLogin)) {
			try {
				getValidationService().validateTSAID(userLogin);
			} catch (Throwable e) {
				ServiceException se = getException(e);
				
				if(se.getErrorCode().equals(ErrorCode.INVALID_CONTAINMENT))
					errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidTSALogin, userLogin));
				else if(se.getErrorCode().equals(ErrorCode.CUSTOM_ERROR)) {
					errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidUserTSALoginFormat, userLogin));
				}
				else if(se.getErrorCode().equals(ErrorCode.ALREADY_EXISTS)) {
					errors.addError(new SEAConsoleException(WSErrorCodes.ErrUserTSALoginNotUnique));
				}
			}
		} else {
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrMandatoryUserTSALogin));
		}
		
		return errors;
	}
	
	/**
	 * Gets the exception.
	 * 
	 * @param e the e
	 * 
	 * @return the exception
	 */
	public static ServiceException getException(Throwable e){
		if (e instanceof ServiceException)
			return (ServiceException)e;
		return (ServiceException) ((RemoteException) ((EJBException) e)
				.getCausedByException()).getCause().getCause();
	}
	
	/**
	 * Validate tsa password.
	 * 
	 * @param password the password
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateTsaPassword(String password) {
		UploadErrors errors = new UploadErrors();
		
		if (validateMandatory(password)) {
			try {
				getValidationService().validateTSAPassword(password);
			} catch (Throwable e) {
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidUserTsaPass));
			}
		} else {
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrMandatoryUserTSAPass));
		}
		
		return errors;
	}
	
	/**
	 * Validate server.
	 * 
	 * @param server the server
	 * @param subscriberCode the subscriber code
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateServer(String server, String subscriberCode) {
		UploadErrors errors = new UploadErrors();
		
		if (validateMandatory(server)) {
			
			if(getValidServers().containsKey(subscriberCode)) {
				if(getValidServers().get(subscriberCode).contains(server)) {
					return errors;
				}
			}
			
			try {
				Site site = getSite(subscriberCode);
				getValidationService().validateServer(server, site.getCompanyId());
				
				if(site != null) {
					List<String> validServers = getUserManagementService().getServers(site.getCompanyId(), site.getOfficeId(), null);
					if(!validServers.contains(server.trim())) {
						errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgServerInvalid, server));
					}
				}
			} catch (Throwable e) {
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgServerInvalid, server));
			}
		} else {
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgServerMissing));
		}
		
		//Cache server
		if(!errors.hasErrors()) {
			if(getValidServers().containsKey(subscriberCode)) {
				getValidServers().get(subscriberCode).add(server);
			} else {
				getValidServers().put(subscriberCode, new ArrayList<String>());
				getValidServers().get(subscriberCode).add(server);
			}
		}
		
		return errors;
	}
	
	/**
	 * Validate server capacity.
	 * 
	 * @param server the server
	 * @param subscriberCode the subscriber code
	 * @param capacity the capacity
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateServerCapacity(String server, String subscriberCode, Integer capacity) {
		UploadErrors errors = new UploadErrors();
		
		if (validateMandatory(server)) {
			try {
				Site site = getSiteService().getSite(subscriberCode);
				ICoordinator coordinator = HttpFilter.getCoordinator();
				if(site != null) {
					/*
					 * 17-06-2011 Satyajit
					 * Added new extra parameter "coordinatorNum" into SP "sse_check_server_capacity".
					 * CR 18955 Allow External Coordinator to Assign Internet Servers.
					 */
					List<ServerInfo> validServers = getUserManagementService().getServersInfoByType(site.getCompanyId(), site.getOfficeId(), "",coordinator.getId());
					for(ServerInfo serverInfo : validServers) {
						if(serverInfo.getName().trim().equalsIgnoreCase(server)) {
							if(serverInfo.getAvailableSpaces() < capacity) {
								errors.addError(new SEAConsoleException(WSErrorCodes.ErrServerFull, server, String.valueOf(capacity - serverInfo.getAvailableSpaces())));
							}
						}
					}
				}
			} catch (Throwable e) {
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgServerInvalid, server));
			}
		}
		
		return errors;
	}
	
	/**
	 * Validate seed account.
	 * 
	 * @param seedAccount the seed account
	 * @param subscriberCode the subscriber code
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateSeedAccount(String seedAccount, String subscriberCode) {
		UploadErrors errors = new UploadErrors();
		
		if(!validateEmpty(seedAccount)) {
			  try {
				  getValidationService().validateSeedAccount(seedAccount, getSiteService().getSite(subscriberCode).getCompanyId());
			  } catch (Throwable e) {
				  errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidSeedAccount));
			  }
		}
		
		return errors;
	}
	
	/**
	 * Validate akr.
	 * 
	 * @param akr the akr
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateAKR(String akr) {
		UploadErrors errors = new UploadErrors();
		
		if(!validateEmpty(akr)) {
			if (!akr.matches("[0-9]{3}")) {
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidLengthAKR));
			}
		}
		
		return errors;
	}
	
	/**
	 * Validate sub account.
	 * 
	 * @param subAccount the sub account
	 * @param subscriberCode the subscriber code
	 * 
	 * @return true, if successful
	 * 
	 * @throws NamingException the naming exception
	 */
	public UploadErrors validateSubAccount(String subscriberCode, String subAccount) throws NamingException,Exception {
		UploadErrors errors = new UploadErrors();
		
		if (validateMandatory(subAccount)) {
			if (subAccount.matches("[0-9]{3}")) {
				Site site = getSite(subscriberCode);
				
				List<SiteAccount> siteAccounts = getSiteService().findSiteAccounts(site.getId());
				for(SiteAccount siteAccount : siteAccounts) {
					if(siteAccount.getAccountId().equals(subAccount)) {
						return errors;
					}
				}
					
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgInvalidSubAccount));
			} else {
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgInvalidSubAccount));
			}
		} else {
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgMandatorySubAccount));
		}
		
		return errors;
	}
	
	/**
	 * Validate subscriber.
	 * 
	 * @param subscriber the subscriber
	 * 
	 * @return true, if successful
	 * 
	 * @throws NamingException the naming exception
	 */
	public UploadErrors validateSubscriber(String subscriber) throws NamingException,Exception {
		UploadErrors errors = new UploadErrors();
		
		if (validateMandatory(subscriber)) {
			
			if(getValidSubscribers().containsKey(subscriber.toUpperCase())) {
				return errors;
			}
			
			if (subscriber.matches("[A-Za-z]{2}[0-9]{5}")) {
				Site site = getSite(subscriber);
				if(site != null) {
					ViewSubscriber viewSubscriber = getSiteService().getSiteByCode(subscriber);
					if(!viewSubscriber.getStatus().equals(SiteReferenceStatus.Active)) {
						errors.addError(new SEAConsoleException(WSErrorCodes.ErrInactiveSubscriber, subscriber));
					}
					
					//Cache subscriber
					if(!errors.hasErrors()) {
						if(!getValidSubscribers().containsKey(subscriber.toUpperCase())) {
							getValidSubscribers().put(subscriber.toUpperCase(), site);
						}
					}
				} else {
					errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgSubscriberNotExist, subscriber));
				}
			} else {
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgSubscriberNotExist, subscriber));
			}
		} else {
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrMandatorySubscriberCode));
		}
		
		return errors;
	}
	
	/**
	 * Validate coordinator.
	 * 
	 * @param coordinator the coordinator
	 * 
	 * @return true, if successful
	 * 
	 * @throws NamingException the naming exception
	 */
	public UploadErrors validateCoordinator(String coordinator) throws NamingException,Exception {
		UploadErrors errors = new UploadErrors();
		
		if (!validateEmpty(coordinator)) {
			if (coordinator.matches("[a-zA-Z_0-9\\-\\.\\+%#!]+@[a-zA-Z_0-9\\-\\.\\+%#!]{1,50}")) {
				User user = getUserService().getUserByTsaId(coordinator);
				if(user == null) {
					errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidCoordinatorName, coordinator));
				} else {
					Coordinator coord = getCoordinatorService().findCoordinatorBySubId(user.getSubId());
					if(coord == null) {
						errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidCoordinatorName, coordinator));
					}
				}
			} else {
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidCoordinatorName, coordinator));
			}
		}
		
		return errors; 	
	}
	
	/**
	 * Validate order number.
	 * 
	 * @param orderNumber the order number
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateOrderNumber(String orderNumber) {
		UploadErrors errors = new UploadErrors();
		
		if(!validateEmpty(orderNumber)) {
			if(!validateMaxChars(orderNumber)){
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgInvalidOrderNumber));
			}
		}
		
		return errors;
	}
	
	/**
	 * Validate athena password.
	 * 
	 * @param password the password
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateAthenaPassword(String password) {
		UploadErrors errors = new UploadErrors();
		
		if(!validateEmpty(password)){
			try {
				getValidationService().validatePassword(password);
			} catch (Exception e) {
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrIlegalCharsAthenaPassword));
			}
		}
		
		return errors;
	}
    
	/**
	 * Validate athena login.
	 * 
	 * @param login the login
	 * @param server the server
	 * @param subscriberCode the subscriber code
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateAthenaLogin(String login, String subscriberCode, String server) {
		UploadErrors errors = new UploadErrors();
		
		if(!validateEmpty(login)) {
			try {
				getValidationService().validateLogin(login, getSiteService().getSite(subscriberCode).getCompanyId(), server);
			} catch (Exception e) {				
				ServiceException se = getException(e);
				
				if(se.getErrorCode().equals(CustomErrorCode.ENTIA_INVALID_LOGINID))
					errors.addError(new SEAConsoleException(WSErrorCodes.ErrIlegalCharsLogin, login));
				else if(se.getErrorCode().equals(CustomErrorCode.INVALID_LOGIN_LENGTH8)) {
					errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidLengthLogin));
				} else if(se.getErrorCode().equals(CustomErrorCode.INVALID_LOGIN_LENGTH16)) {
					errors.addError(new SEAConsoleException(WSErrorCodes.ErrInvalidLengthLogin));
				}
				else if(se.getErrorCode().equals(ErrorCode.CUSTOM_ERROR)) {
					errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgDuplicateAthenaLogin, login));
				}
			}
		}
		
		return errors;
	}
	
	/**
	 * Validate products.
	 * 
	 * @param userId the user id
	 * @param subscriberCode the subscriber code
	 * @param productToAdd the product to add
	 * @param productToRemove the product to remove
	 * 
	 * @return the upload errors
	 */
	public UploadErrors validateProducts(String userId , String subscriberCode , List<String> productToAdd, List<String> productToRemove) {
		UploadErrors errors = new UploadErrors();

		try {
			getValidationService().validateProducts(getSiteService().getSiteByCode(subscriberCode).getSiteId(), productToAdd);
			
			List<ViewProduct> userProducts;
			if(userId != null) {
				userProducts = getUserService().getUserPortfolioItems(userId);
				for(ViewProduct product : userProducts) {
					if(productToAdd.contains(product.getProductCode())) {
						errors.addError(new SEAConsoleException(WSErrorCodes.ErrDuplicateProductInPortfolio, product.getProductCode()));
					}
				}
				List<String> userProductCodes = new ArrayList<String>();
				for(ViewProduct product : userProducts) {
					userProductCodes.add(product.getProductCode());
				}
				for(String productCode : productToRemove) {
					if(!userProductCodes.contains(productCode)) {
						errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgUserProdsNotExistForRemoving, productCode));
					}
				}
			} else {
				userProducts = new ArrayList<ViewProduct>();
			}
			
			//Solutions
			List<String> userProductCodes = new ArrayList<String>();
			for(ViewProduct product : userProducts) {
				userProductCodes.add(product.getProductCode());
			}
			if(productToAdd != null) {
				userProductCodes.addAll(productToAdd);
			}
			if(productToRemove != null) {
				userProductCodes.removeAll(productToRemove);
			}
			
			Integer solutionProducts = 0;
			if(userProductCodes.size() > 0) {
				List<Product> products = getProductService().findByCodes(userProductCodes);
				for(Product product : products) {
					if(product.getType().equals(ProductType.Solution)) {
						solutionProducts++;
					}
				}
			} else {
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrPortfolioCannotBeEmpty));
				return errors;
			}
			
			if (solutionProducts > 1){
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgTranUserOnlyOneSolution, userId));
			}
			if (solutionProducts == 0){
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgTranUserNoSolutions, userId));
			}
			
		} catch (Exception e) {
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgProductsNotExistInSubsriber, "", subscriberCode));
		}
		
		return errors;
	}
	
	/**
	 * Validate add products.
	 * 
	 * @param productNames the product names
	 * @param subscriberCode the subscriber code
	 * @param userId the user id
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateAddProducts(String userId , String subscriberCode , List<String> productNames) {
		UploadErrors errors = new UploadErrors();
		
		try {
			getValidationService().validateProducts(getSiteService().getSiteByCode(subscriberCode).getSiteId(), productNames);
			
			if(userId != null) {
				List<ViewProduct> userProducts = getUserService().getUserPortfolioItems(userId);
				for(ViewProduct product : userProducts) {
					if(productNames.contains(product.getProductCode())) {
						errors.addError(new SEAConsoleException(WSErrorCodes.ErrDuplicateProductInPortfolio, product.getProductCode()));
					}
				}
			}
		} catch (Exception e) {
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgProductsNotExistInSubsriber, "", subscriberCode));
		}
		
		return errors;
	}
	
	/**
	 * Validate remove products.
	 * 
	 * @param subscriberId the subscriber id
	 * @param productNames the product names
	 * 
	 * @return true, if successful
	 */
	public UploadErrors validateRemoveProducts(String userId , List<String> productNames) throws NamingException,Exception {
		UploadErrors errors = new UploadErrors();
		
		List<ViewProduct> userProducts = getUserService().getUserPortfolioItems(userId);
		List<String> userProductCodes = new ArrayList<String>();
		for(ViewProduct product : userProducts) {
			userProductCodes.add(product.getProductCode());
		}
		for(String productCode : productNames) {
			if(!userProductCodes.contains(productCode)) {
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgUserProdsNotExistForRemoving, productCode));
			}
		}
		
		return errors;
	}
	
	/**
	 * Validate solutions.
	 * 
	 * @param productsToAdd the products to add
	 * @param productsToRemove the products to remove
	 * @param userId the user id
	 * 
	 * @return true, if successful
	 * 
	 * @throws NamingException the naming exception
	 */
	public UploadErrors validateSolutions(String userId, List<String> productsToAdd, List<String> productsToRemove) throws NamingException,Exception {
		UploadErrors errors = new UploadErrors();

		List<ViewProduct> userProducts;
		if(userId != null) {
			userProducts = getUserService().getUserPortfolioItems(userId);
		} else {
			userProducts = new ArrayList<ViewProduct>();
		}
		
		List<String> userProductCodes = new ArrayList<String>();
		for(ViewProduct product : userProducts) {
			userProductCodes.add(product.getProductCode());
		}
		if(productsToAdd != null) {
			userProductCodes.addAll(productsToAdd);
		}
		if(productsToRemove != null) {
			userProductCodes.removeAll(productsToRemove);
		}
		
		Integer solutionProducts = 0;
		if(userProductCodes.size() > 0) {
			List<Product> products = getProductService().findByCodes(userProductCodes);
			for(Product product : products) {
				if(product.getType().equals(ProductType.Solution)) {
					solutionProducts++;
				}
			}
		} else {
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrPortfolioCannotBeEmpty));
			return errors;
		}
		
		if (solutionProducts > 1){
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgTranUserOnlyOneSolution, userId));
		}
		if (solutionProducts == 0){
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgTranUserNoSolutions, userId));
		}
		
		return errors;
	}
	
	/**
	 * Validate xls unique tsa id.
	 * 
	 * @param loadedUsers the loaded users
	 * @param newUser the new user
	 * 
	 * @return true, if successful
	 */
	public boolean validateXlsUniqueTsaId(List<CreateXlsUserEntity> loadedUsers, CreateXlsUserEntity newUser) {
		for(CreateXlsUserEntity user : loadedUsers) {
			if(user.getUserLogin().trim().equals(newUser.getUserLogin().trim())) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Validate user.
	 * 
	 * @param userId the user id
	 * @param userStatus the user status
	 * @param siteCode the site code
	 * 
	 * @return true, if successful
	 * 
	 * @throws NamingException the naming exception
	 */
	public UploadErrors validateUser(String userId, String siteCode, UserStatus userStatus) throws NamingException,Exception {
		UploadErrors errors = new UploadErrors();
		
		if(userId.trim().equals("")) {
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgNoUserProvided, userId));
			return errors;
		}
		
		User user = getUserService().getUser(userId);
		
		if(user == null) {
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrUserDoesNotExist, userId));
		} else if(user.getSiteCode() == null || (user.getSiteCode() != null && !user.getSiteCode().equals(siteCode))) {
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgUserNotExistInSubscriber, userId, siteCode));
		} else if(userStatus != null) {
			if(!user.getStatus().equals(userStatus.getCode())) {
				errors.addError(new SEAConsoleException(WSErrorCodes.ErrInactiveUser, userId));
			}
		}
		
		return errors;
	}
	
	/**
	 * Validate users pending transactions.
	 * 
	 * @param subId the sub id
	 * @param action the action
	 * 
	 * @return true, if successful
	 * 
	 * @throws Exception the exception
	 */
	public UploadErrors validateUsersPendingTransactions(String subId, TransactionAction action) throws Exception {
		UploadErrors errors = new UploadErrors();
		
		Integer count = getTransactionService().numberOfPendingTransactions(subId, action.getAction());
		
		if(count != null && count > 0) {
			errors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgPendingTransacions, subId, action.name()));
		}
		
		return errors;
	}
	
	/**
	 * Validate transaction form.
	 * 
	 * @param transactionsForm the transactions form
	 * 
	 * @return the transactions form ws
	 * 
	 * @throws Exception the exception
	 */
	public TransactionsFormWS validateTransactionForm(TransactionsFormWS transactionsForm) throws Exception {
		switch (transactionsForm.getTransactionAction()) {
			case Create:
				return getValidationTransactionService().validateCreateTransaction(transactionsForm);
			case Modify:
				return getValidationTransactionService().validateModifyUserPortfolioTransaction(transactionsForm);
			case Delete:
				return getValidationTransactionService().validateDeleteTransaction(transactionsForm);
			case Move:
				return getValidationTransactionService().validateMoveTransactions(transactionsForm);
			case Reinstate:
				return getValidationTransactionService().validateReinstateTransaction(transactionsForm);
			case Update:
				return getValidationTransactionService().validateUpdateTransacion(transactionsForm);
			default:
				return null;
		}
	}

	/**
	 * @return the validServers
	 */
	public Map<String, List<String>> getValidServers() {
		return validServers;
	}

	/**
	 * @return the validSubscribers
	 */
	public Map<String, Site> getValidSubscribers() {
		return validSubscribers;
	}
	
	/**
	 * Gets the site.
	 * 
	 * @param code the code
	 * 
	 * @return the site
	 * 
	 * @throws NamingException the naming exception
	 */
	public Site getSite(String code) throws NamingException ,Exception{
		if(getValidSubscribers().containsKey(code)) {
			return getValidSubscribers().get(code);
		} else {
			return getSiteService().getSite(code);
		}
	}
	
}
