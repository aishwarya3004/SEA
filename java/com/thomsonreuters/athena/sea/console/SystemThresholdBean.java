/**
 * 
 */
package com.thomsonreuters.athena.sea.console;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.thomsonreuters.athena.sea.service.ISEAQueueFeederLogicService;
import com.thomsonreuters.athena.sea.service.ISEAQueueFeederLogicServiceLocal;
import com.thomsonreuters.athena.sea.service.ServiceLocator;
import com.thomsonreuters.athena.sea.service.ServiceLocator.Services;

/**
 * Provides insights into System Threshold Parameters
 * 
 * @author Rohit.Gupta1
 * 
 */
public class SystemThresholdBean {

	public static class SystemParam {
		private boolean isSystemThresholdBreached;
		private long mainQueueSize;
		private long spmlRequestSize;
		private long mainQueueSizeThreshold;
		private long spmlRequestSizeThreshold;

		public boolean getIsSystemThresholdBreached() {
			return isSystemThresholdBreached;
		}

		public void setIsSystemThresholdBreached(boolean isSystemThresholdBreached) {
			this.isSystemThresholdBreached = isSystemThresholdBreached;
		}

		public long getMainQueueSize() {
			return mainQueueSize;
		}

		public void setMainQueueSize(long mainQueueSize) {
			this.mainQueueSize = mainQueueSize;
		}

		public long getSpmlRequestSize() {
			return spmlRequestSize;
		}

		public void setSpmlRequestSize(long spmlRequestSize) {
			this.spmlRequestSize = spmlRequestSize;
		}

		public long getMainQueueSizeThreshold() {
			return mainQueueSizeThreshold;
		}

		public void setMainQueueSizeThreshold(long mainQueueSizeThreshold) {
			this.mainQueueSizeThreshold = mainQueueSizeThreshold;
		}

		public long getSpmlRequestSizeThreshold() {
			return spmlRequestSizeThreshold;
		}

		public void setSpmlRequestSizeThreshold(long spmlRequestSizeThreshold) {
			this.spmlRequestSizeThreshold = spmlRequestSizeThreshold;
		}

	}

	public SystemThresholdBean() {

	}

/**
	 * Return system Parameters
	 * 
	 * @return
	 * @throws NamingException
	 */
	public List<SystemParam> getSystemParams() throws NamingException {
		SystemParam systemParam = new SystemParam();
		ISEAQueueFeederLogicService seaQueueFeederLogicService = (ISEAQueueFeederLogicService) ServiceLocator.getInstance().getService(Services.SEAQueueFeederLogicServices);
		systemParam.setIsSystemThresholdBreached(seaQueueFeederLogicService.isSystemThresholdBreached());
		systemParam.setMainQueueSize(seaQueueFeederLogicService.getMAIN_QUEUE_SIZE());
		systemParam.setSpmlRequestSize(seaQueueFeederLogicService.getSPML_REQUEST_SIZE());
		systemParam.setMainQueueSizeThreshold(seaQueueFeederLogicService.getMAIN_QUEUE_SIZE_THRESHOLD());
		systemParam.setSpmlRequestSizeThreshold(seaQueueFeederLogicService.getSPML_REQUEST_SIZE_THRESHOLD());
		List<SystemParam> listSysParam = new ArrayList<SystemParam>();
		listSysParam.add(systemParam);
		return listSysParam;
	}
}