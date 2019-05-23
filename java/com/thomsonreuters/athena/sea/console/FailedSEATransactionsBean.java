/**
 * 
 */
package com.thomsonreuters.athena.sea.console;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import com.thomsonreuters.athena.sea.athena.sea.common.cache.impl.GenericCachedObject;
import com.thomsonreuters.athena.sea.athena.sea.common.cache.manager.CacheManager;
import com.thomsonreuters.athena.sea.service.ServiceLocator;
import com.thomsonreuters.athena.sea.service.TransactionService;
import com.thomsonreuters.athena.sea.service.ServiceLocator.Services;
import com.thomsonreuters.athena.sea.util.DatabaseException;
import com.thomsonreuters.athena.sea.view.entity.ViewInconsistentTxn;

/**
 * Dated:7-April-2013
 * @author sankhabrata.burman
 *
 */
public class FailedSEATransactionsBean {

	private Integer daysInterval;
	private List<ViewInconsistentTxn> txnViewList = new ArrayList<ViewInconsistentTxn>();
	private List<ViewInconsistentTxn> completedTxnViewList = new ArrayList<ViewInconsistentTxn>();
	private String sessionID;

	@SuppressWarnings("unchecked")
	public void setDays(Integer value) {
		daysInterval = value;
	}
	
	@SuppressWarnings("unchecked")
	public void setSendRequest(String paramDetails) {
		String param[] = paramDetails.split("-");
		Integer interval = Integer.parseInt(param[0]);
		setDays(interval);
		String sessionId = param[1];
		setSessionID(sessionId);
		ViewInconsistentTxn viewObj = new ViewInconsistentTxn();
		viewObj.setDayInterval(interval);
		try {
			TransactionService tranService = (TransactionService) ServiceLocator.getInstance().getService(Services.TransactionServices);
				if (tranService != null) {
					List<ViewInconsistentTxn> viewTxnList = tranService	.findAllInconsistentTxn(viewObj);
					if (viewTxnList != null && viewTxnList.size() > 0) {
						setTxnViewList(viewTxnList);
					}

				}
		} catch (NamingException nexp) {
			nexp.printStackTrace();
		} catch (Exception exp) {
			exp.printStackTrace();
		}

	}
	
	/**
	 * 
	 * @param paramDetails
	 */
	@SuppressWarnings("unchecked")
	public void setCompleteRequest(String paramDetails){
		String []inputParam = paramDetails.split(";");
		String requestId = inputParam[0];
		Integer interval = Integer.parseInt(inputParam[1]);
		//String sessionId = inputParam[2];
		List<ViewInconsistentTxn> objList = new ArrayList<ViewInconsistentTxn>();
		ViewInconsistentTxn obj = new ViewInconsistentTxn();
		obj.setTxnId(requestId);
		obj.setDayInterval(interval);
		objList.add(obj);
		try{
			TransactionService tranService = (TransactionService) ServiceLocator.getInstance().getService(Services.TransactionServices);
			try {
				List<ViewInconsistentTxn> modTxnList = new ArrayList<ViewInconsistentTxn>();
				List<ViewInconsistentTxn> txnViewList = tranService	.findAllInconsistentTxn(obj);
				List<ViewInconsistentTxn> completedRequestList = tranService.completeInconsistentTxn(objList);
				if(completedRequestList.get(0).getTxnId().contains("-")){
						modTxnList = removeFromCache(completedRequestList,txnViewList);
					}else{
						for(int i=0;i<txnViewList.size();i++){
							if(txnViewList.get(i).getTxnId().equalsIgnoreCase(completedRequestList.get(0).getTxnId())){
								txnViewList.remove(i);
							}
						}
						modTxnList.addAll(txnViewList);
					}
					setCompletedTxnViewList(modTxnList);
				} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch(NamingException nexp){
			nexp.printStackTrace();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param completedRequestList
	 * @param txnViewList
	 * @return
	 */
	private List<ViewInconsistentTxn> removeFromCache(List<ViewInconsistentTxn> completedRequestList,List<ViewInconsistentTxn> txnViewList){
		String requestId = completedRequestList.get(0).getTxnId().substring(0, completedRequestList.get(0).getTxnId().indexOf("-"));
		for(Iterator<ViewInconsistentTxn> itr = txnViewList.iterator();itr.hasNext();)  
        {  
			ViewInconsistentTxn element1 = itr.next();
            if(element1.getTxnId().contains("-")){
            	String element = element1.getTxnId().substring(0, element1.getTxnId().indexOf("-"));
                
                if(element.equals(requestId))  
                {  
                    itr.remove();  
                }
            }
              
        }  
				
		return txnViewList;
	}
	
	
	public void setTxnId(String txnId){
		System.out.println("txnid:"+txnId);
	}

	public Integer getDays() {
		return daysInterval;
	}

	/**
	 * @return the txnViewList
	 */
	public List<ViewInconsistentTxn> getTxnViewList() {
		return txnViewList;
	}

	/**
	 * @param txnViewList
	 *            the txnViewList to set
	 */
	public void setTxnViewList(List<ViewInconsistentTxn> txnViewList) {
		this.txnViewList = txnViewList;
		for(int i=0;i<txnViewList.size();i++){
			txnViewList.get(i).setDayInterval(getDays());
			txnViewList.get(i).setSessionId(getSessionID());
		}
	}
	
	
	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * @param sessionID
	 *            the sessionID to set
	 */
	@SuppressWarnings("unchecked")
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
		ViewInconsistentTxn viewObj = new ViewInconsistentTxn();
		viewObj.setDayInterval(daysInterval);
		if(sessionID.length()!=0){
			try {
				TransactionService tranService = (TransactionService) ServiceLocator.getInstance().getService(Services.TransactionServices);
					if (tranService != null) {
						List<ViewInconsistentTxn> viewTxnList = tranService	.findAllInconsistentTxn(viewObj);
							if (viewTxnList != null && viewTxnList.size() > 0) {
							setTxnViewList(viewTxnList);
						}

					}
				} catch (NamingException nexp) {
				nexp.printStackTrace();
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		

	}

	/**
	 * @return the completedTxnViewList
	 */
	public List<ViewInconsistentTxn> getCompletedTxnViewList() {
		return completedTxnViewList;
	}

	/**
	 * @param completedTxnViewList the completedTxnViewList to set
	 */
	public void setCompletedTxnViewList(List<ViewInconsistentTxn> completedTxnViewList) {
		this.completedTxnViewList = completedTxnViewList;
		for(int i=0;i<completedTxnViewList.size();i++){
			completedTxnViewList.get(i).setDayInterval(getDays());
			completedTxnViewList.get(i).setSessionId(getSessionID());
		}
		
	}
}
