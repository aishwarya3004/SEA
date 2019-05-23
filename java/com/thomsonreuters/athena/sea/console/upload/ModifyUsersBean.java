package com.thomsonreuters.athena.sea.console.upload;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.athena.sea.console.HttpFilter;
import com.thomsonreuters.athena.sea.console.upload.entity.ModifyXlsUserEntity;
import com.thomsonreuters.athena.sea.console.upload.validation.UploadValidator;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadAction;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadErrors;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadResult;
import com.thomsonreuters.athena.sea.console.upload.xls.util.Utils;
import com.thomsonreuters.athena.sea.entity.Coordinator;
import com.thomsonreuters.athena.sea.entity.Product;
import com.thomsonreuters.athena.sea.entity.Field.Fields;
import com.thomsonreuters.athena.sea.entity.Status.TransactionStatus;
import com.thomsonreuters.athena.sea.entity.Transaction.TransactionAction;
import com.thomsonreuters.athena.sea.entity.User.UserStatus;
import com.thomsonreuters.athena.sea.view.entity.ViewSettings;

/**
 * @author Miro Zorboski
 * 
 */
public class ModifyUsersBean extends AbstractUploadBean<ModifyXlsUserEntity> {
	
	/**
	 * Instantiates a new modify users bean.
	 * 
	 * @param uploadAction the upload action
	 */
	public ModifyUsersBean(UploadAction uploadAction) {
		super(uploadAction);
	}
	
	/* (non-Javadoc)
	 * @see com.thomsonreuters.athena.sea.console.upload.AbstractUploadBean#createTransactions(java.util.List)
	 */
	@Override
	protected UploadResult<ModifyXlsUserEntity> createTransactions(
			List<ModifyXlsUserEntity> entities) throws Exception {
		
		Date targetDate = Utils.getCurrentDate();
		
		boolean errorsFound = false;
		boolean transactionErrorsFound = false;
		
		//Validate all users
		for(ModifyXlsUserEntity modifyEntity : entities) {
			validate(modifyEntity);

			if(modifyEntity.hasErrors()) {
				errorsFound = true;
			}
		}
		
		Map<String, List<ModifyXlsUserEntity>> groupedEntities = groupEntitiesBySubscriber(entities);
		
		//If all entities valid
		if(!errorsFound) {
			Coordinator creator = getCoordinatorService().searchCoordinator(HttpFilter.getCoordinator().getLogin(), new ViewSettings()).get(0);
			
			for(String key : groupedEntities.keySet()) {
				
				List<ModifyXlsUserEntity> entityList = groupedEntities.get(key);
				String subscriberId = getSiteService().getSite(key).getId().toString();
				
				for(ModifyXlsUserEntity entity : entityList) {
					if(!entity.hasErrors()) {
						List<HashMap<Fields, String>> usersFieldsList = new ArrayList<HashMap<Fields,String>>();
						HashMap<Fields, String> usersFields = entity.getFieldsMap();
						usersFields.put(Fields.SubscriberId, subscriberId);
						usersFieldsList.add(usersFields);
						
						List<Integer> productsToAdd = new ArrayList<Integer>();
						if(entity.getProductsToAdd().size() > 0) {
							List<Product> productsEntitiesToAdd = (List<Product>)getProductService().findByCodes(entity.getProductsToAdd());
							for(Product product : productsEntitiesToAdd) {
								productsToAdd.add(product.getId());
							}
						}
						List<Integer> productsToRemove = new ArrayList<Integer>();
						if(entity.getProductsToRemove().size() > 0) {
							List<Product> productsEntitiesToRemove = (List<Product>)getProductService().findByCodes(entity.getProductsToRemove());
							for(Product product : productsEntitiesToRemove) {
								productsToRemove.add(product.getId());
							}
						}
						
						Integer tranId = saveTransaction(null, TransactionAction.Modify, TransactionStatus.Initiated, 
										targetDate, entity.getComment(), null, usersFieldsList, productsToRemove, productsToAdd, creator.getId(), creator.getId(), false);
						
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
		
		UploadResult<ModifyXlsUserEntity> results = new UploadResult<ModifyXlsUserEntity>(groupedEntities, errorsFound, transactionErrorsFound);
		return results;
	}

	/* (non-Javadoc)
	 * @see com.thomsonreuters.athena.sea.console.upload.AbstractUploadBean#validate(java.lang.Object)
	 */
	@Override
	protected void validate(ModifyXlsUserEntity entity) throws Exception {
		
		if(entity != null && entity.getXlsRowNumber() != null) {
			System.out.println("Validating row: " + entity.getXlsRowNumber());
		}

		UploadErrors totalErrors = new UploadErrors();
		
		UploadValidator validator = new UploadValidator();
		
		//ValidateSystem
		UploadErrors errors = validator.validateSystem(entity.getSystem());
		if (errors.hasErrors()) {
			totalErrors.mergeErrors(errors);
	    }
		//Validate Subscriber
		errors = validator.validateSubscriber(entity.getSubscriberCode());
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
		//Validate user exists and active
		if(!totalErrors.hasErrors()) {
			errors = validator.validateUser(entity.getUserId(), entity.getSubscriberCode(), UserStatus.Active);
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    } 
		}
	    if(!totalErrors.hasErrors()) {
	    	//Validate products
	    	errors = validator.validateProducts(entity.getUserId(), entity.getSubscriberCode(), entity.getProductsToAdd(), entity.getProductsToRemove());
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    }
	    }
	    //Validate pending delete transaction
		if(!totalErrors.hasErrors()) {
			errors = validator.validateUsersPendingTransactions(entity.getUserId(), TransactionAction.Delete);
			if (errors.hasErrors()) {
				totalErrors.mergeErrors(errors);
		    } 
		}
	    //Validate pending move transaction
		if(!totalErrors.hasErrors()) {
			errors = validator.validateUsersPendingTransactions(entity.getUserId(), TransactionAction.Move);
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
