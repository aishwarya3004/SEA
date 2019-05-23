/**
 * 
 */
package com.thomsonreuters.athena.sea.console;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.thomsonreuters.athena.sea.util.JNDIUtilities;
import com.thomsonreuters.athena.services.jms.IMessageFactory;
import com.thomsonreuters.athena.services.jms.NotificationStatusMessage;

/**
 * @author Aleksandar Pecanov
 */
public class NotificationStartup implements ServletContextListener, MessageListener {
	final static String propertyType = "classNameProperty";

	TopicConnection tc = null;

	/*
	 * (non-Javadoc)
	 * @seejavax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if (tc != null)
			try {
				tc.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {/*
			TopicConnectionFactory tcf = JNDIUtilities.lookup("jms/ConnectionFactory", TopicConnectionFactory.class);
			tc = tcf.createTopicConnection();
			TopicSession session = tc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic topic = JNDIUtilities.lookup("jms/NotificationTopic", Topic.class);
			//TopicSubscriber ts = session.createDurableSubscriber(topic, getClass().getSimpleName());
			TopicSubscriber ts = session.createSubscriber(topic);
			ts.setMessageListener(this);
			tc.start();
		*/} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message arg0) {
		if (!isSupported(arg0))
			return;
		
		try {
			NotificationStatusMessage msg = (NotificationStatusMessage) IMessageFactory.Default.getInstance()
					.translateMessage(arg0);

			NotificationViewBean.notifications.add(msg);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * @param msg
	 * @return
	 */
	protected boolean isSupported(Message msg) {
		try {
			String className = msg.getStringProperty(propertyType);
			if (className == null)
				return false;

			Class.forName(className);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

}
