package com.vectorcat.internal.serfnett.simpleTest;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vectorcat.serfnett.spi.Service;

@Singleton
class Data extends AbstractIdleService implements Service {

	@Inject
	Data() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void shutDown() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("DATA SHUTDOWN!");
	}

	@Override
	protected void startUp() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("DATA STARTUP!");
	}

}
