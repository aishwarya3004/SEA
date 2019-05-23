package com.thomsonreuters.athena.sea.console.upload.xls.util;

import java.util.Comparator;

import com.thomsonreuters.athena.sea.console.upload.entity.AbstractXlsEntity;

/**
 * @author Miro Zorboski
 * 
 */
public class SubscriberComparator implements Comparator<AbstractXlsEntity> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(AbstractXlsEntity o1, AbstractXlsEntity o2) {
		return o1.getSubscriberCode().trim().compareToIgnoreCase(o2.getSubscriberCode().trim());
	}

}
