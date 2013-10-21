package com.vectorcat.ingamus;

import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Singleton;
import com.vectorcat.ingamus.spi.IngamusActor;
import com.vectorcat.ingamus.spi.IngamusService;

@Singleton
public final class IngamusActorService extends AbstractIdleService implements
		IngamusService {

	public class BatchJob {
		Queue<Runnable> tasks = Queues.newConcurrentLinkedQueue();

		public void disposeActor(final IngamusActor actor) {
			doTask(new Runnable() {
				@Override
				public void run() {
					IngamusActorService.this.disposeActor(actor);
				}
			});
		}

		public void doTask(Runnable runnable) {
			tasks.offer(runnable);
		}

		/**
		 * Designed to be called again if exception is caught and you still want
		 * to finish processing tasks. It will not process the same task more
		 * than once.
		 */
		public void processAll() {
			while (!tasks.isEmpty()) {
				tasks.poll().run();
			}
		}

		public void spawnActor(final IngamusActor actor) {
			doTask(new Runnable() {
				@Override
				public void run() {
					IngamusActorService.this.spawnActor(actor);
				}
			});
		}
	}

	private final IngamusEventBus eventBus;
	private final Set<IngamusActor> spawned = Sets
			.newSetFromMap(new WeakHashMap<IngamusActor, Boolean>());

	IngamusActorService(IngamusEventBus eventBus) {
		this.eventBus = eventBus;
	}

	public synchronized void disposeActor(IngamusActor actor) {
		Preconditions.checkArgument(spawned.contains(actor), actor.getClass()
				.getSimpleName() + " not spawned! [" + actor + "]");

		eventBus.post(new ActorDisposedEvent(actor));

		spawned.remove(actor);

		actor.onDispose();
	}

	@Override
	protected void shutDown() throws Exception {
		// NOP
	}

	public synchronized void spawnActor(IngamusActor actor) {
		Preconditions.checkArgument(!spawned.contains(actor), actor.getClass()
				.getSimpleName() + " already spawned! [" + actor + "]");

		eventBus.post(new ActorSpawnedEvent(actor));

		spawned.add(actor);

		actor.onSpawn();
	}

	@Override
	protected void startUp() throws Exception {
		// NOP
	}

}
