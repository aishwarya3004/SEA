/**
 * 
 */
package com.thomsonreuters.athena.sea.console;

import java.util.List;

import javax.naming.NamingException;

import com.thomsonreuters.athena.sea.service.InvalidSession;
import com.thomsonreuters.athena.sea.service.ServiceLocator;
import com.thomsonreuters.athena.sea.service.ServiceLocator.Services;
import com.thomsonreuters.athena.sea.service.session.SessionInformation;
import com.thomsonreuters.athena.sea.service.session.SessionService;
import com.thomsonreuters.athena.sea.util.Logger;

/**
 * @author Aleksandar Pecanov
 */
public class SessionViewBean {
	private final static Logger log = Logger.getLogger(SessionViewBean.class);
	
	/**
	 * @return
	 * @throws NamingException 
	 */
	public List<SessionInformation> getSessionInfo() throws NamingException {
		return getSessionService().getSessionInformation();
	}

	/**
	 * @param id
	 * @throws InvalidSession
	 * @throws NamingException 
	 */
	public void setInvalidSession(String id) throws Exception {
		SessionInformation info = getSessionService().getSessionInformation(id);
		log.info("Requested forced session invalidation for coordinator "+info.getCoordinatorLogin());
		
		getSessionService().invalidateSession(id);
	}
	
	
	/* added by Swateek CR 34721. Dt: 28th May, 2013 */
	
	public void setInactive(String num) throws Exception {
		log.info("Deleting All Inactive Co-ordinator Sessions");	
		getSessionService().deleteInactive();
	}
	
	
	/**
	 * @return
	 * @throws NamingException 
	 */
	protected SessionService getSessionService() throws NamingException {
		return (SessionService) ServiceLocator.getInstance().getService(Services.SessionServices);
	}

}
