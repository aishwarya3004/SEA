package com.thomsonreuters.athena.sea.console;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.athena.sea.entity.Notification;
import com.thomsonreuters.athena.sea.entity.Notification.NotifyType;
import com.thomsonreuters.athena.sea.service.ServiceLocator;
import com.thomsonreuters.athena.sea.service.TransactionNotificationService;
import com.thomsonreuters.athena.sea.service.TransactionService;
import com.thomsonreuters.athena.sea.service.ServiceLocator.Services;
import com.thomsonreuters.athena.sea.view.entity.ViewSettings;


public class BillingNotificationViewBean {
	
	@SuppressWarnings("unchecked")
	public List<Notification> getUnsendTransaction(){
		List<Notification> list = new ArrayList<Notification>();
		try {
			TransactionNotificationService service = (TransactionNotificationService) ServiceLocator.getInstance().getService(Services.TransactionNotification);
			ViewSettings vs = new ViewSettings();
			vs.setCurrentPage(-1);
			vs.setResultsPerPage(-1);
			ViewSettings result = service.searchNotification(null, "N", 4, NotifyType.WS_SYNCHRONIC.getCode(), null, vs);
			List<Notification> res = (List<Notification>)result.getSearchResults();
			list = res;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getMissingNotification(){
		List<Integer> res = new ArrayList<Integer>();
		
		try {
			TransactionNotificationService service = (TransactionNotificationService) ServiceLocator.getInstance().getService(Services.TransactionNotification);
			List<Integer> list = service.getMissingNotification();
			res.addAll(list);

		} catch (Throwable e) {
			e.printStackTrace();
		}

		List<String>  listInprocesTran = (List<String>)HttpFilter.getRequest().getSession().getAttribute("inprocess_trans");
		if (listInprocesTran != null) {
			for (String t : listInprocesTran) {
				if (res.contains(Integer.valueOf(t))) {
					res.remove(Integer.valueOf(t));
				}
			}
		}
				
		return res;
	}
	
		
	public void setSend(String temp){
		String[] checkList = HttpFilter.getRequest().getParameterValues("check_list");
		List<String> selectedTran = new ArrayList<String>();
		if (checkList != null) {
			for (String string : checkList) {
				selectedTran.add(string);
			}
			List<Notification> list = getUnsendTransaction();
			List<Notification> updateTrans = new ArrayList<Notification>();
			for (Notification notification : list) {
				if (selectedTran.contains(String.valueOf(notification
						.getTransactionId()))) {
					notification.setRetryNumber(0);
					notification.setModificationDate(new Timestamp(new Date()
							.getTime()));
					notification.setModificationSuid(null);
					notification.setResponseNote(null);
					notification.setResponseReferenceId(null);
					notification.setResponseStatus(null);
					updateTrans.add(notification);
				}
			}
			
			if (!updateTrans.isEmpty()) {
				TransactionService transactionService = null;
				TransactionNotificationService notifyService = null;
				try {
					transactionService = (TransactionService) ServiceLocator.getInstance().getService(Services.TransactionServices);
					notifyService = (TransactionNotificationService) ServiceLocator.getInstance().getService(Services.TransactionNotification);
					for (Notification n : updateTrans) {
						notifyService.saveNotification(n);
						transactionService.sendTransactionToCS(String.valueOf(n.getTransactionId()));
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setSendMissingTransacion(String temp){
		String[] checkList = HttpFilter.getRequest().getParameterValues("check_list");
		List<String> selectedTran = new ArrayList<String>();
		if (checkList != null) {
			for (String string : checkList) {
				selectedTran.add(string);
			}			
			if (!selectedTran.isEmpty()) {
				TransactionService transactionService = null;
				try {
					transactionService = (TransactionService) ServiceLocator.getInstance().getService(Services.TransactionServices);
					for (String n : selectedTran) {
						transactionService.sendTransactionToCS(n);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
				List<String>  listInprocesTran = (List<String>)HttpFilter.getRequest().getSession().getAttribute("inprocess_trans");
				if (listInprocesTran != null) {
					for (String t : listInprocesTran) {
						if(!selectedTran.contains(t)){
							selectedTran.add(t);
						}
					}
				}
				HttpFilter.getRequest().getSession().setAttribute("inprocess_trans",selectedTran);
				
			}
		}
	}
}
