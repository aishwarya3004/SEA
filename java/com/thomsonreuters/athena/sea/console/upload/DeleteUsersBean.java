package com.thomsonreuters.athena.sea.console.upload;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.athena.sea.console.HttpFilter;
import com.thomsonreuters.athena.sea.console.upload.entity.DeleteXlsUserEntity;
import com.thomsonreuters.athena.sea.console.upload.validation.UploadValidator;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadAction;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadErrors;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadResult;
import com.thomsonreuters.athena.sea.console.upload.xls.util.Utils;
import com.thomsonreuters.athena.sea.entity.Coordinator;
import com.thomsonreuters.athena.sea.entity.Field.Fields;
import com.thomsonreuters.athena.sea.entity.Status.TransactionStatus;
import com.thomsonreuters.athena.sea.entity.Transaction.TransactionAction;
import com.thomsonreuters.athena.sea.entity.User.UserStatus;
import com.thomsonreuters.athena.sea.view.entity.ViewSettings;

/**
 * @author Miro Zorboski
 * 
 */
public class DeleteUsersBean extends AbstractUploadBean<DeleteXlsUserEntity> {

	/**
	 * Instantiates a new delete users bean.
	 * 
	 * @param uploadAction the upload action
	 */
	public DeleteUsersBean(UploadAction uploadAction) {
		super(uploadAction);
	}
	
	/* (non-Javadoc)
	 * @see com.thomsonreuters.athena.sea.console.upload.AbstractUploadBean#createTransactions(java.util.List)
	 */
	@Override
	protected UploadResult<DeleteXlsUserEntity> createTransactions(
			List<DeleteXlsUserEntity> entities) throws Exception {
		
		Date targetDate = Utils.getCurrentDate();
		
		boolean errorsFound = false;
		boolean transactionErrorsFound = false;
		
		List<String> uniqueUsers = new ArrayList<String>();
		
		//Validate all users
		for(DeleteXlsUserEntity deleteEntity : entities) {
			validate(deleteEntity);
			
			if(deleteEntity.hasErrors()) {
				errorsFound = true;
			}
			
			//Validate unique users in xls file
			if(uniqueUsers.contains(deleteEntity.getUserId().trim()) && !deleteEntity.getUserId().trim().equals("")) {
				deleteEntity.addError("User is not unique in xls file");
				errorsFound = true;
			} else {
				uniqueUsers.add(deleteEntity.getUserId().trim());
			}
		}
		
		Map<String, List<DeleteXlsUserEntity>> groupedEntities = groupEntitiesBySubscriber(entities);
		
		//If all entities valid
		if(!errorsFound) {
			Coordinator creator = getCoordinatorService().searchCoordinator(HttpFilter.getCoordinator().getLogin(), new ViewSettings()).get(0);
			
			for(String key : groupedEntities.keySet()) {
				
				List<DeleteXlsUserEntity> entityList = groupedEntities.get(key);
				String subscriberId = getSiteService().getSite(key).getId().toString();
				List<HashMap<Fields, String>> usersFieldsList = new ArrayList<HashMap<Fields,String>>();
				
				for(DeleteXlsUserEntity entity : entityList) {
					if(!entity.hasErrors()) {
						HashMap<Fields, String> usersFields = entity.getFieldsMap();
						usersFields.put(Fields.SubscriberId, subscriberId);
						usersFieldsList.add(usersFields);
					}
				}
				
				//Save transaction
				Integer tranId = saveTransaction(null, TransactionAction.Delete, TransactionStatus.Initiated, 
								targetDate, null, null, usersFieldsList, null, null, creator.getId(), creator.getId(), true);
				
				for(DeleteXlsUserEntity entity : entityList) {
					if(tranId == null) {
						entity.addError("Transaction not created. Database error occured while saving transaction.");
						transactionErrorsFound = true;
					} else {
						entity.setTransactionId(tranId);
					}
				}
			}
		}
		
		UploadResult<DeleteXlsUserEntity> results = new UploadResult<DeleteXlsUserEntity>(groupedEntities, errorsFound, transactionErrorsFound);
		return results;
	}

	/* (non-Javadoc)
	 * @see com.thomsonreuters.athena.sea.console.upload.AbstractUploadBean#validate(java.lang.Object)
	 */
	@Override
	protected void validate(DeleteXlsUserEntity entity) throws Exception {
		
		if(entity != null && entity.getXlsRowNumber() != null) {
			System.out.println("Validating row: " + entity.getXlsRowNumber());
		}
		
		UploadErrors totalErrors = new UploadErrors();
		
		UploadValidator validator = getValidator();
		
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
	    //Validate pending delete transaction
	   if (!totalErrors.hasErrors()) {
	    	errors = validator.validateUsersPendingTransactions(entity.getUserId(), TransactionAction.Delete);
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
