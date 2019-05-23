/**
 * 
 */
package com.thomsonreuters.athena.sea.console;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;

import com.thomsonreuters.athena.sea.util.JNDIUtilities;

/**
 * @author Aleksandar Pecanov
 */
public class QueueViewBean {
	
	//CR28680 - ID the notification queue
	private static final String NOTIFICATION_QUEUE_ID = "notificationQueue";

	private String name = "";
	
	private String idProperty = "";
	//CR28680 - Get properties of Notification Queue
	private String notificationAddRequestProperty = "";
	private String notificationRequestProperty = "";
	private String notificationSingleRequestProperty = "";
	
	public static class ViewItem { 
		private Date timestamp;
		private String requestId;
		private String login;
		/**
		 * @return the timestamp
		 */
		public Date getTimestamp() {
			return timestamp;
		}
		/**
		 * @param timestamp the timestamp to set
		 */
		public void setTimestamp(Date timestamp) {
			this.timestamp = timestamp;
		}
		/**
		 * @return the requestId
		 */
		public String getRequestId() {
			return requestId;
		}
		/**
		 * @param requestId the requestId to set
		 */
		public void setRequestId(String requestId) {
			this.requestId = requestId;
		}
		/**
		 * @return the login
		 */
		public String getLogin() {
			return login;
		}
		/**
		 * @param login the login to set
		 */
		public void setLogin(String login) {
			this.login = login;
		}

	}
	
	/**
	 * 
	 */
	public QueueViewBean() {
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the idProperty
	 */
	public String getIdProperty() {
		return idProperty;
	}

	/**
	 * @param idProperty the idProperty to set
	 */
	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	/**
	 * @return
	 */
	public List<ViewItem> getMessages() {
		try {
			ConnectionFactory cf = JNDIUtilities.lookup("ConnectionFactory", ConnectionFactory.class);
			return getQueueMessages(cf);
		} catch (Throwable t) {
			t.printStackTrace();
			return new ArrayList<ViewItem>();
		}
	}

	/**
	 * 
	 * @param cf
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected List<ViewItem> getQueueMessages(ConnectionFactory cf) throws Exception {
		QueueConnectionFactory qcf = (QueueConnectionFactory) cf;
		QueueConnection qc = qcf.createQueueConnection();
		QueueSession session = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		QueueBrowser qb = session.createBrowser(JNDIUtilities.lookup(getName(), Queue.class));
		Enumeration e = qb.getEnumeration();

		try {
			List<ViewItem> list = new ArrayList<ViewItem>();
			if (NOTIFICATION_QUEUE_ID.equalsIgnoreCase(getIdProperty())) {
				while (e.hasMoreElements()) {
					Message message = (Message) e.nextElement();
					ViewItem item = new ViewItem();
					// Construct Combined Requests
					String requestID = message.getStringProperty(getNotificationSingleRequestProperty());
					String addRequestIDs = message.getStringProperty(getNotificationAddRequestProperty());
					String requestIDs = message.getStringProperty(getNotificationRequestProperty());
					// Construct Request Display
					StringBuffer displayRequests = new StringBuffer();
					if (null != requestID && !requestID.equalsIgnoreCase("null")) {
						displayRequests.append(requestID);
					} else if (null != requestIDs && !requestIDs.equalsIgnoreCase("null")) {
						displayRequests.append(requestIDs);
					} else if (null != addRequestIDs && !addRequestIDs.equalsIgnoreCase("null")) {
						displayRequests.append(addRequestIDs);
					}
					item.setRequestId(displayRequests.toString());
					item.setTimestamp(new Date(message.getJMSTimestamp()));
					item.setLogin(message.getStringProperty("coordinatorLogin"));
					list.add(item);
				}
			} else {
				while (e.hasMoreElements()) {
					Message message = (Message) e.nextElement();
					ViewItem item = new ViewItem();
					item.setTimestamp(new Date(message.getJMSTimestamp()));
					item.setRequestId(message.getStringProperty(getIdProperty()));
					item.setLogin(message.getStringProperty("coordinatorLogin"));
					list.add(item);
				}
			}

			return list;
		} finally {
			qb.close();
			session.close();
			qc.close();
		}
	}

	/**
	 * @return the notificationAddRequestProperty
	 */
	public String getNotificationAddRequestProperty() {
		return notificationAddRequestProperty;
	}

	/**
	 * @param notificationAddRequestProperty the notificationAddRequestProperty to set
	 */
	public void setNotificationAddRequestProperty(String notificationAddRequestProperty) {
		this.notificationAddRequestProperty = notificationAddRequestProperty;
	}

	/**
	 * @return the notificationRequestProperty
	 */
	public String getNotificationRequestProperty() {
		return notificationRequestProperty;
	}

	/**
	 * @param notificationRequestProperty the notificationRequestProperty to set
	 */
	public void setNotificationRequestProperty(String notificationRequestProperty) {
		this.notificationRequestProperty = notificationRequestProperty;
	}

	/**
	 * @return the notificationSingleRequestProperty
	 */
	public String getNotificationSingleRequestProperty() {
		return notificationSingleRequestProperty;
	}

	/**
	 * @param notificationSingleRequestProperty the notificationSingleRequestProperty to set
	 */
	public void setNotificationSingleRequestProperty(String notificationSingleRequestProperty) {
		this.notificationSingleRequestProperty = notificationSingleRequestProperty;
	}

}
