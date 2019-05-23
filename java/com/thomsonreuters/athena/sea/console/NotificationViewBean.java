/**
 * 
 */
package com.thomsonreuters.athena.sea.console;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.athena.services.jms.NotificationStatusMessage;

/**
 * @author Aleksandar Pecanov
 *
 */
public class NotificationViewBean {

	static List<NotificationStatusMessage> notifications = new ArrayList<NotificationStatusMessage>();
	
	/**
	 * 
	 * @return
	 */
	public List<NotificationStatusMessage> getNotifications() {
		return notifications;
	}
	
	/**
	 * 
	 * @param clear
	 */
	public void setClear(boolean clear) {
		notifications.clear();
	}
	
}
