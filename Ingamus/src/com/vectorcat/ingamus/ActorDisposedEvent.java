package com.vectorcat.ingamus;

import com.google.common.base.Preconditions;
import com.vectorcat.ingamus.spi.IngamusActor;
import com.vectorcat.ingamus.spi.IngamusEvent;

public class ActorDisposedEvent implements IngamusEvent {
	private final IngamusActor actor;

	ActorDisposedEvent(IngamusActor actor) {
		Preconditions.checkNotNull(actor);
		this.actor = actor;
	}

	public boolean actorInstanceOf(Class<?> clazz) {
		return clazz.isAssignableFrom(actor.getClass());
	}

	public IngamusActor getActor() {
		return actor;
	}

	@SuppressWarnings("unchecked")
	public <T> T getCastedActor() {
		return (T) actor;
	}
}
