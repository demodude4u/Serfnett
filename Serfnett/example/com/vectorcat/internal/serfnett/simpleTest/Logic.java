package com.vectorcat.internal.serfnett.simpleTest;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vectorcat.serfnett.api.Service;

@Singleton
class Logic extends AbstractScheduledService implements Service {

	@Inject
	Logic(Data data) {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void runOneIteration() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("LOGIC RUNONCEITERATION!");
	}

	@Override
	protected Scheduler scheduler() {
		// TODO Auto-generated method stub
		System.out.println("LOGIC SCHEDULER!");
		return Scheduler.newFixedRateSchedule(0, 100, TimeUnit.MILLISECONDS);
	}

}
