package com.thomsonreuters.athena.sea.console.upload;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import com.thomsonreuters.athena.sea.console.HttpFilter;
import com.thomsonreuters.athena.sea.console.upload.entity.AbstractXlsEntity;
import com.thomsonreuters.athena.sea.console.upload.validation.UploadValidator;
import com.thomsonreuters.athena.sea.console.upload.xls.XlsParserFactory;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadAction;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadResult;
import com.thomsonreuters.athena.sea.entity.Field.Fields;
import com.thomsonreuters.athena.sea.entity.Status.TransactionStatus;
import com.thomsonreuters.athena.sea.entity.Transaction.TransactionAction;
import com.thomsonreuters.athena.sea.entity.Transaction.TransactionType;
import com.thomsonreuters.athena.sea.service.CoordinatorService;
import com.thomsonreuters.athena.sea.service.ProductService;
import com.thomsonreuters.athena.sea.service.ServiceLocator;
import com.thomsonreuters.athena.sea.service.SiteService;
import com.thomsonreuters.athena.sea.service.TransactionService;
import com.thomsonreuters.athena.sea.service.UserService;
import com.thomsonreuters.athena.sea.service.ServiceLocator.Services;

import com.thomsonreuters.athena.sea.util.Logger;
import com.thomsonreuters.athena.services.IValidationService;
import com.thomsonreuters.athena.services.auth.ICoordinator;

/**
 * @author Miro Zorboski
 *
 */
public abstract class AbstractUploadBean<T extends AbstractXlsEntity> {

	/** The Constant log. */
	private final static Logger log = Logger.getLogger(AbstractUploadBean.class);

	/** The upload action. */
	protected UploadAction uploadAction;

	/** The validator. */
	private UploadValidator validator;

	/**
	 * Instantiates a new abstract upload bean.
	 *
	 * @param uploadAction the upload action
	 */
	public AbstractUploadBean(UploadAction uploadAction) {
		super();
		this.uploadAction = uploadAction;
	}

	/**
	 * Upload.
	 *
	 * @param xlsData the xls data
	 *
	 * @return the upload result< t>
	 *
	 * @throws Exception the exception
	 */
	public UploadResult<T> upload(byte[] xlsData) throws Exception {
		List<T> entities = parse(xlsData);
		UploadResult<T> results = createTransactions(entities);

		return results;
	}

	/**
	 * Parses the.
	 *
	 * @param xlsData the xls data
	 *
	 * @return the list< t>
	 *
	 * @throws DatabaseException the exception
	 */
	@SuppressWarnings("unchecked")
	protected List<T> parse(byte[] xlsData) throws Exception {
		List<T> entities = (List<T>)XlsParserFactory.getParser(getUploadAction()).parse(xlsData);

		return entities;
	}

	/**
	 * Creates the transactions.
	 *
	 * @param entities the entities
	 *
	 * @return the upload result< t>
	 *
	 * @throws DatabaseException the exception
	 * @throws Exception 
	 */
	protected abstract UploadResult<T> createTransactions(List<T> entities) throws Exception;

	/**
	 * Save transaction.
	 *
	 * @param parentTransactionId the parent transaction id
	 * @param action the action
	 * @param status the status
	 * @param targetDate the target date
	 * @param comment the comment
	 * @param transactonUsersFields the transaction users fields
	 * @param productsToRemove the products to remove
	 * @param productsToAdd the products to add
	 * @param coordinatorId the coordinator id
	 * @param creator the creator
	 * @param enforceMassTransaction the enforce mass transaction
	 *
	 * @return the integer
	 *
	 * @throws DatabaseException the exception
	 */
	protected Integer saveTransaction(Integer parentTransactionId, TransactionAction action, TransactionStatus status,
			Date targetDate, String comment, String caaNumber, List<HashMap<Fields, String>> transactionUsersFields,
			List<Integer> productsToRemove, List<Integer> productsToAdd,  int coordinatorId, int creator, Boolean enforceMassTransaction) throws Exception {

		Integer tranId = null;

		boolean tranSaved = false;
		Integer tranSaveCount = 1;

		while(!tranSaved && tranSaveCount <= 5) {
			try {
				tranId = getTransactionService().saveTransactions(parentTransactionId, action, status,
						targetDate, comment, null, transactionUsersFields,null, productsToRemove, productsToAdd,
						coordinatorId, creator, enforceMassTransaction, TransactionType.Bulk, null, null, false, false , null, null, null, null,false);
				tranSaved = true;
			} catch (Exception e) {
				log.debug("Exception while saving transaction. Retrying: " + tranSaveCount);
				tranSaveCount++;
				e.printStackTrace();
				Thread.sleep(2000);
			}
		}

		return tranId;
	}

	/**
	 * Validate.
	 *
	 * @param bean the bean
	 *
	 * @throws Exception the exception
	 */
	protected abstract void validate(T entity) throws Exception;


	/**
	 * Group entities by subscriber.
	 *
	 * @param entities the entities
	 *
	 * @return the map< string, list< t>>
	 *
	 * @throws DatabaseException the exception
	 */
	protected Map<String, List<T>> groupEntitiesBySubscriber(List<T> entities) throws Exception {

		Map<String, List<T>> groupedEntities = new HashMap<String, List<T>>();

		for(T entity : entities) {
			if(groupedEntities.containsKey(entity.getSubscriberCode().trim())) {
				groupedEntities.get(entity.getSubscriberCode().trim()).add(entity);
			} else {
				List<T> entityList = new ArrayList<T>();
				entityList.add(entity);
				groupedEntities.put(entity.getSubscriberCode().trim(), entityList);
			}
		}

		return groupedEntities;
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
	 * Gets the coordinator service.
	 *
	 * @return the coordinator service
	 *
	 * @throws NamingException the naming exception
	 */
	protected CoordinatorService getCoordinatorService() throws NamingException {
		return (CoordinatorService) ServiceLocator.getInstance().getService(Services.CoordinatorServices);
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
	 * Gets the user service.
	 *
	 * @return the userService
	 *
	 * @throws NamingException the naming exception
	 */
	public UserService getUserService() throws NamingException {
		return (UserService) ServiceLocator.getInstance().getService(Services.UserServices);
	}

	/**
	 * Gets the validator.
	 *
	 * @return the validator
	 *
	 * @throws DatabaseException the exception
	 */
	public UploadValidator getValidator() throws Exception {
		if(validator == null) {
			validator = new UploadValidator();
		}

		return validator;
	}

	/**
	 * @return the uploadAction
	 */
	public UploadAction getUploadAction() {
		return uploadAction;
	}

}
