/**
 * 
 */
package com.thomsonreuters.athena.sea.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.NamingException;

import com.thomsonreuters.athena.sea.service.BulkTransactionProcessorService;
import com.thomsonreuters.athena.sea.util.JNDIUtilities;
import com.thomsonreuters.athena.sea.util.Logger;
import com.thomsonreuters.athena.services.ITransactionManagementService;
import com.thomsonreuters.athena.services.ITransactionManagementServiceLocal;
import com.thomsonreuters.athena.services.ServiceLocator;
import com.thomsonreuters.athena.services.auth.ICoordinator;
import com.thomsonreuters.athena.services.entity.Request;

/**
 * @author Aleksandar Pecanov
 */
public class RequestViewBean {
	
	private final static Logger log = Logger.getLogger(RequestViewBean.class);
	
	private long bulkInterval;
	
	/**
	 * Reschedule the Bulk Timer
	 */
	public void setRescheduleBulkQuartzTimer(String defaultInterval) {
		try {
			BulkTransactionProcessorService bulkTransactionProcessor = JNDIUtilities.lookup(
					BulkTransactionProcessorService.JNDI, BulkTransactionProcessorService.class);
			bulkTransactionProcessor.rescheduleTimer();
		} catch (NamingException e) {
			log.error("Oops");
			e.printStackTrace();
		}
	}
	

	/**
	 * 
	 * @return
	 */
	public List<Request> getRequests() {
		try {
			ICoordinator coordinator = HttpFilter.getCoordinator();
			ServiceLocator serviceLocator = ServiceLocator.getInstance(coordinator.getLogin(), coordinator.getPassword());
			ITransactionManagementService service = serviceLocator.getTransactionManagementService();
			
			List<Request> requestList = service.getAllRequests();
			Request r[] = new Request[requestList.size()];
			requestList.toArray(r);
			
			Arrays.sort(r, new Comparator<Request>() {

				@Override
				public int compare(Request o1, Request o2) {
					return o1.getStatusCode().compareTo(o2.getStatusCode());
				}

			});

			return Arrays.asList(r);
		} catch (Throwable t) {
			t.printStackTrace();
			return new ArrayList<Request>();
		}
	}

	/**
	 * 
	 * @param requestId
	 */
	public void setFinishedRequest(String requestId) throws Exception {
		ICoordinator coordinator = HttpFilter.getCoordinator();
		log.info("Rquest ID -"+requestId+" Pushed from Console By - "+coordinator.getLogin());
		ServiceLocator serviceLocator = ServiceLocator.getInstance(coordinator.getLogin(), coordinator.getPassword());
		ITransactionManagementService service = serviceLocator.getTransactionManagementService();
		service.finishRequest(requestId);
	}
	
	/**
	 * Sets the finished requests including archived.
	 * 
	 * @param requestIds the new finished requests including archived
	 * 
	 * @throws Exception the exception
	 */
	public void setFinishedRequestsIncludingArchived(String requestIds) throws Exception {
		try {
			ICoordinator coordinator = HttpFilter.getCoordinator();
			ServiceLocator serviceLocator = ServiceLocator.getInstance(coordinator.getLogin(), coordinator.getPassword());
			ITransactionManagementService service = serviceLocator.getTransactionManagementService();
			
			List<String> requestIdsList = new ArrayList<String>();

			if (requestIds.contains("--")) {
				String from = requestIds.substring(0, requestIds.indexOf("--")).trim();
				String to = requestIds.substring(requestIds.indexOf("--") + 2, requestIds.length()).trim();
				Integer fromNumber = Integer.valueOf(from.substring(3));
				Integer toNumber = Integer.valueOf(to.substring(3));

				for (int i = fromNumber; i <= toNumber; i++) {
					requestIdsList.add("SEA" + i);
				}
			} else {
				StringTokenizer tokenizer = new StringTokenizer(requestIds, ";");
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken().trim().toUpperCase();
					requestIdsList.add(token);
				}
			}
			log.info("Request IDs -" + requestIds + " Pushed from Console By - "+coordinator.getLogin());

			service.finishRequestIncludingArchived(requestIdsList);

		} catch (Exception e) {
			throw new Exception("Error completing exceptions.");
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean getTimerStatus() throws Exception {
		ICoordinator coordinator = HttpFilter.getCoordinator();
		ServiceLocator serviceLocator = ServiceLocator.getInstance(coordinator.getLogin(), coordinator.getPassword());
		ITransactionManagementService service = serviceLocator.getTransactionManagementService();
		return service.isRequestCheckupServiceRunning();
	}
	
	/**
	 * 
	 * @param run
	 * @throws Exception
	 */
	public void setTimerStatus(boolean run) throws Exception {
		ICoordinator coordinator = HttpFilter.getCoordinator();
		ServiceLocator serviceLocator = ServiceLocator.getInstance(coordinator.getLogin(), coordinator.getPassword());
		
		ITransactionManagementService service = serviceLocator.getTransactionManagementService();
		
		if( run )
			service.startRequestCheckupService();
		else
			service.stopRequestCheckupService();
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean getProductStatus() throws Exception {
		ICoordinator coordinator = HttpFilter.getCoordinator();
		
		ServiceLocator serviceLocator = ServiceLocator.getInstance(coordinator.getLogin(), coordinator.getPassword());
		ITransactionManagementService service = serviceLocator.getTransactionManagementService();
		//ITransactionManagementServiceLocal service=(ITransactionManagementServiceLocal)serviceLocator.getTransactionManagementServices();
		return service.isProductCheckupServiceRunning();
	}
	
	/**
	 * 
	 * @param run
	 * @throws Exception
	 */
	public void setProductStatus(boolean run) throws Exception {
		ICoordinator coordinator = HttpFilter.getCoordinator();
		ServiceLocator serviceLocator = ServiceLocator.getInstance(coordinator.getLogin(), coordinator.getPassword());
		
		ITransactionManagementService service = serviceLocator.getTransactionManagementService();
		
		if( run )
			service.startProductCheckupService();
		else
			service.stopProductCheckupService();
	}

	public long getBulkInterval() {
		return bulkInterval;
	}

	public void setBulkInterval(long bulkInterval) {
		this.bulkInterval = bulkInterval;
	}
}
