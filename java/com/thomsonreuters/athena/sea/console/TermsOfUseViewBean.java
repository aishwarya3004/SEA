package com.thomsonreuters.athena.sea.console;

import com.thomsonreuters.athena.sea.service.CoordinatorService;
import com.thomsonreuters.athena.sea.service.ServiceLocator;
import com.thomsonreuters.athena.sea.service.ServiceLocator.Services;


public class TermsOfUseViewBean {
	
	public void setUpdateData(String temp){
		String checkList = HttpFilter.getRequest().getParameter("textarea_terms");

		if (checkList != null) {
			CoordinatorService coordinatorService = null;
				try {
					coordinatorService = (CoordinatorService) ServiceLocator.getInstance().getService(Services.CoordinatorServices);
					coordinatorService.resetTermsOfAgreementText(checkList);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
	}
	
	public String getTermsOfUseData(){
		String res = "";
		try {
			CoordinatorService service = (CoordinatorService) ServiceLocator.getInstance().getService(Services.CoordinatorServices);
			res = service.getTermsOfAgreementText();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}
}
