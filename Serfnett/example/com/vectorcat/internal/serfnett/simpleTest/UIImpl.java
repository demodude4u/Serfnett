package com.vectorcat.internal.serfnett.simpleTest;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.inject.Inject;

class UIImpl extends AbstractExecutionThreadService implements UI {

	@Inject
	UIImpl(Logic logic, Data data) {
	}

	@Override
	protected void run() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("UI RUN!");
	}

	@Override
	public void showUI() {
		// TODO Auto-generated method stub
		System.out.println("UI SHOWUI!");
	}

}
