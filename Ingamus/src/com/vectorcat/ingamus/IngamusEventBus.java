package com.vectorcat.ingamus;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Singleton;
import com.vectorcat.ingamus.spi.IngamusEvent;
import com.vectorcat.ingamus.spi.IngamusService;

@Singleton
public final class IngamusEventBus extends AbstractIdleService implements
		IngamusService {

	private final EventBus eventBus;

	IngamusEventBus() {
		this(new EventBus("Ingamus"));
	}

	IngamusEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void post(IngamusEvent event) {
		eventBus.post(event);
	}

	public void register(Object object) {
		eventBus.register(object);
	}

	@Override
	protected void shutDown() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void startUp() throws Exception {
		// TODO Auto-generated method stub

	}

	public void unregister(Object object) {
		eventBus.unregister(object);
	}

}
