package com.thomsonreuters.athena.sea.console.upload;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.athena.sea.console.HttpFilter;
import com.thomsonreuters.athena.sea.console.upload.entity.CreateXlsUserEntity;
import com.thomsonreuters.athena.sea.console.upload.validation.UploadValidator;
import com.thomsonreuters.athena.sea.console.upload.xls.util.SEAConsoleException;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadAction;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadErrors;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadResult;
import com.thomsonreuters.athena.sea.console.upload.xls.util.Utils;
import com.thomsonreuters.athena.sea.entity.Coordinator;
import com.thomsonreuters.athena.sea.entity.Product;
import com.thomsonreuters.athena.sea.entity.User;
import com.thomsonreuters.athena.sea.entity.Field.Fields;
import com.thomsonreuters.athena.sea.entity.Status.TransactionStatus;
import com.thomsonreuters.athena.sea.entity.Transaction.TransactionAction;
import com.thomsonreuters.athena.sea.view.entity.ViewSettings;
import com.thomsonreuters.athena.sea.ws.WSErrorCodes;

/**
 * @author Miro Zorboski
 * 
 */
public class CreateUsersBean extends AbstractUploadBean<CreateXlsUserEntity> {

	/**
	 * Instantiates a new creates the users bean.
	 * 
	 * @param uploadAction the upload action
	 */
	public CreateUsersBean(UploadAction uploadAction) {
		super(uploadAction);
	}
	
	/* (non-Javadoc)
	 * @see com.thomsonreuters.athena.sea.console.upload.AbstractUploadBean#createTransactions(java.util.List)
	 */
	protected UploadResult<CreateXlsUserEntity> createTransactions(List<CreateXlsUserEntity> entities) throws Exception {
		
		Date targetDate = Utils.getCurrentDate();
		
		boolean errorsFound = false;
		boolean transactionErrorsFound = false;
		
		List<String> uniqueTsaId = new ArrayList<String>();
		Map<String, List<String>> athenaLogins = new HashMap<String, List<String>>();
		
		Map<String, Integer> servers = new HashMap<String, Integer>();
		
		//Validate all users
		for(CreateXlsUserEntity entity : entities) {
			validate(entity);
			
			if(entity.hasErrors()) {
				errorsFound = true;
			}
			
			if(servers.containsKey(entity.getServer())) {
				servers.put(entity.getServer(), servers.get(entity.getServer()) + 1);
			} else {
				servers.put(entity.getServer(), 1);
			}
			
			//Validate unique users tsaId in xls file
			if(uniqueTsaId.contains(entity.getUserLogin().trim().toLowerCase())) {
				entity.addError("User login (TsaId) is not unique in xls file");
				errorsFound = true;
			} else {
				uniqueTsaId.add(entity.getUserLogin().trim().toLowerCase());
			}
			
			//Validate unique users athena login on server in xls file
			if(entity.getServer() != null && !entity.getServer().equals("")) {
				if(athenaLogins.containsKey(entity.getServer())) {
					if(entity.getAthenaLogin() != null && !entity.getAthenaLogin().equals("")) {
						List<String> login = athenaLogins.get(entity.getServer());
						if(login.contains(entity.getAthenaLogin().toLowerCase())) {
							entity.addError("User athena login is not unique on server in xls file");
							errorsFound = true;
						} else {
							athenaLogins.get(entity.getServer()).add(entity.getAthenaLogin().toLowerCase());
						}
					}
				} else {
					if(entity.getAthenaLogin() != null && !entity.getAthenaLogin().equals("")) {
						athenaLogins.put(entity.getServer(), new ArrayList<String>());
						athenaLogins.get(entity.getServer()).add(entity.getAthenaLogin().toLowerCase());
					}
				}
			}
		}
		
		//Validate server capacity
		if(!errorsFound) {
			for(CreateXlsUserEntity entity : entities) {
				if(servers.containsKey(entity.getServer())) {
					UploadErrors errors = getValidator().validateServerCapacity(entity.getServer(), entity.getSubscriberCode(), servers.get(entity.getServer()));
					if (errors.hasErrors()) {
						entity.getValidationErrors().addAll(errors.getErrorMessages());
						errorsFound = true;
				    } else {
				    	servers.remove(entity.getServer());
				    }
				}
			}
		}

		Map<String, List<CreateXlsUserEntity>> groupedEntities = groupEntitiesBySubscriber(entities);
		
		//If all users valid
		if(!errorsFound) {
			Coordinator creator = getCoordinatorService().searchCoordinator(HttpFilter.getCoordinator().getLogin(), new ViewSettings()).get(0);
			
			for(String key : groupedEntities.keySet()) {
				
				List<CreateXlsUserEntity> entityList = groupedEntities.get(key);
				String subscriberId = getSiteService().getSite(key).getId().toString();
				
				for(CreateXlsUserEntity entity : entityList) {

					if(!entity.hasErrors()) {
						
						Coordinator coordinator = null;
						if(entity.getCoordinator() != null && !entity.getCoordinator().equals("")) {
							User user = getUserService().getUserByTsaId(entity.getCoordinator());
							coordinator = getCoordinatorService().findCoordinatorBySubId(user.getSubId());
						} else {
							coordinator = creator;
						}
						
						List<HashMap<Fields, String>> usersFieldsList = new ArrayList<HashMap<Fields,String>>();
						HashMap<Fields, String> usersFields = entity.getFieldsMap();
						usersFields.put(Fields.SubscriberId, subscriberId);
						usersFieldsList.add(usersFields);
	
						List<Product> products = (List<Product>)getProductService().findByCodes(entity.getProducts());
						List<Integer> productsToAdd = new ArrayList<Integer>();
						for(Product product : products) {
							productsToAdd.add(product.getId());
						}
		
						Integer tranId = saveTransaction(null, TransactionAction.Create, TransactionStatus.Initiated, 
										targetDate, entity.getComment(),null, usersFieldsList, null, productsToAdd, coordinator.getId(), creator.getId(), false);
						
						if(tranId == null) {
							entity.addError("Transaction not created. Database error occured while saving transaction.");
							transactionErrorsFound = true;
						} else {
							entity.setTransactionId(tranId);
						}
					}
				}
			}
		}
		
		UploadResult<CreateXlsUserEntity> results = new UploadResult<CreateXlsUserEntity>(groupedEntities, errorsFound, transactionErrorsFound);
		return results;
	}

	/* (non-Javadoc)
	 * @see com.thomsonreuters.athena.sea.console.upload.AbstractUploadBean#validate(com.thomsonreuters.athena.sea.console.upload.entity.AbstractXlsEntity)
	 */
	@Override
	protected void validate(CreateXlsUserEntity entity) throws Exception {
		
		if(entity != null && entity.getXlsRowNumber() != null) {
			System.out.println("Validating row: " + entity.getXlsRowNumber());
		}

		UploadErrors totalErrors = new UploadErrors();
		
		UploadValidator validator = getValidator();
		
		//Validate Subscriber
		UploadErrors errors = validator.validateSubscriber(entity.getSubscriberCode());
		if (errors.hasErrors()) {
			totalErrors.mergeErrors(errors);
	    }
		//Validate sub account
		if (!totalErrors.hasErrors()) {
			errors = validator.validateSubAccount(entity.getSubscriberCode(), entity.getSubAccount());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		//Validate Coordinator
		if (!totalErrors.hasErrors()) {
			errors = validator.validateCoordinator(entity.getCoordinator());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		//Validate order number
		if (!totalErrors.hasErrors()) {
			errors = validator.validateOrderNumber(entity.getOrderNumber());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		//Validate Email
		if (!totalErrors.hasErrors()) {
			errors = validator.validateEmail(entity.getEmail());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		//Validate FirstName
		if (!totalErrors.hasErrors()) {
			errors = validator.validateFirstName(entity.getFirstName());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		//Validate LastName
		if (!totalErrors.hasErrors()) {
			errors = validator.validateLastName(entity.getLastName());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		//Validate User login
		if (!totalErrors.hasErrors()) {
			errors = validator.validateTsaId(entity.getUserLogin());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		//Validate TsaPassword
		if (!totalErrors.hasErrors()) {
			errors = validator.validateTsaPassword(entity.getPassword());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		//Validate Server
		if (!totalErrors.hasErrors()) {
			errors = validator.validateServer(entity.getServer(), entity.getSubscriberCode());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		//Validate Athena login
		if (!totalErrors.hasErrors()) {
			errors = validator.validateAthenaLogin(entity.getAthenaLogin(), entity.getSubscriberCode(), entity.getServer());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		//Validate Athena Password
		if (!totalErrors.hasErrors()) {
			errors = validator.validateAthenaPassword(entity.getAthenaPassword());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		//Validate Athena login and password
		if (!totalErrors.hasErrors()) {
			if(validator.validateMandatory(entity.getAthenaLogin()) && !validator.validateMandatory(entity.getAthenaPassword())) {
				totalErrors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgProvidedAthenaPassOrDeleteAthenaLogin));
			}
			if (!validator.validateMandatory(entity.getAthenaLogin()) && validator.validateMandatory(entity.getAthenaPassword())) {
				totalErrors.addError(new SEAConsoleException(WSErrorCodes.ErrMsgProvidedAthenaLoginOrDeletePass));
			}
		}
		//Validate seed account
		if (!totalErrors.hasErrors()) {
			errors = validator.validateSeedAccount(entity.getSeedAccount(), entity.getSubscriberCode());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		//Validate AKR
		if (!totalErrors.hasErrors()) {
			errors = validator.validateAKR(entity.getAkr());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		//Validate Products
		if(!totalErrors.hasErrors()) {
			errors = validator.validateProducts(null, entity.getSubscriberCode(), entity.getProducts(), new ArrayList<String>());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
		}
		
	    //Set users errors
	    if(totalErrors.hasErrors()) {
	    	List<String> errorsText = totalErrors.getErrorMessages();
	    	entity.setValidationErrors(errorsText);
	    }
	    
	    entity.setValid(totalErrors.hasErrors());
	}
	
}
