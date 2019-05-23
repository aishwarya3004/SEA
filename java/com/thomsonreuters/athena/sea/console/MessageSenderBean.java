/**
 * 
 */
package com.thomsonreuters.athena.sea.console;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.thomsonreuters.athena.sea.jms.ISeaMessageProducer;
import com.thomsonreuters.athena.sea.util.JNDIUtilities;
import com.thomsonreuters.athena.sea.util.Logger;
import com.thomsonreuters.athena.services.auth.ICoordinator;

/**
 * @author Aleksandar Pecanov
 */
public class MessageSenderBean {
	
	private final static Logger log = Logger.getLogger(MessageSenderBean.class);

	public String beginTransactionId;
	public String transactionId;

	/**
	 * @return the beginTransactionId
	 */
	public String getBeginTransactionId() {
		return beginTransactionId;
	}

	/**
	 * @param beginTransactionId the beginTransactionId to set
	 */
	public void setBeginTransactionId(String beginTransactionId) {
		this.beginTransactionId = beginTransactionId;
	}

	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) throws Exception{
		this.transactionId = transactionId;
		List<Integer> list = new ArrayList<Integer>();
		
		if( beginTransactionId!=null ) {
			for( int i=Integer.parseInt(beginTransactionId); i<Integer.parseInt(transactionId);i++ )
				list.add(i);
		} else
			list.add(Integer.parseInt(transactionId));
		//CR28680 - Demarcate txns for bulk / UI MQ
		//Bug fix - Transactions were marked as future dated.
		ICoordinator coordinator = HttpFilter.getCoordinator();
		log.info("Below Transactions Pushed to SEA Queue By -"+coordinator.getLogin());
		for(Integer val:list) {
			log.info(val.toString());
		}
		getMessageProducerBean().sendMessageToSeaQueue(list, null,false,false);
	}

	/**
	 * @return
	 * @throws NamingException
	 */
	protected ISeaMessageProducer getMessageProducerBean() throws NamingException {
		return JNDIUtilities.lookup(ISeaMessageProducer.JNDI, ISeaMessageProducer.class);
	}

}
