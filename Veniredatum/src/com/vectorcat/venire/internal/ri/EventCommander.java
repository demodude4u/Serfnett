package com.vectorcat.venire.internal.ri;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Futures;
import com.vectorcat.venire.api.EventBus;

public class EventCommander {

	private class Reciever {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Subscribe
		public void recieveResponse(CommandResponseEvent<?> e) {
			ArrayBlockingQueue queue = mapResponses.get(e
					.getResponseCommandID());
			if (queue == null) {
				System.err
						.println("No Queue for ID" + e.getResponseCommandID());
				return;// XXX ??? Should I error?
			}

			queue.offer(e);
		}
	}

	private volatile int nextCommandID = 0;

	private final CommandIDGenerator idGenerator = new CommandIDGenerator() {
		@Override
		public synchronized int getNextCommandID() {
			return nextCommandID++;
		}
	};

	private final ConcurrentMap<Integer, ArrayBlockingQueue<? extends CommandResponseEvent<?>>> mapResponses = Maps
			.newConcurrentMap();

	private final EventBus bus;

	public EventCommander(EventBus bus) {
		this.bus = bus;

		bus.register(new Reciever());
	}

	private <C extends CommandEvent, R extends CommandResponseEvent<C>> Future<R> command(
			C event) {
		final int commandID = event.getCommandID();

		final ArrayBlockingQueue<R> queue = Queues.newArrayBlockingQueue(1);
		mapResponses.put(commandID, queue);

		try {
			bus.post(event);
		} catch (InterruptedException e) {
			System.err.println("Command interrupted on post!");
			mapResponses.remove(commandID);
			return Futures.immediateCancelledFuture();
		}

		Callable<R> callable = new Callable<R>() {
			@Override
			public R call() throws Exception {
				R ret = queue.take();

				mapResponses.remove(commandID);

				return ret;
			}
		};

		return new FutureTask<>(callable);
	}

	Future<Object> commandCall(int interfaceID, int functionID,
			Object[] parameters) {
		CallEvent event = new CallEvent(idGenerator, interfaceID, functionID,
				parameters);

		Future<ReturnEvent> future = command(event);

		return Futures.lazyTransform(future,
				new Function<ReturnEvent, Object>() {
					@Override
					public Object apply(ReturnEvent event) {
						if (event.isThrowableCaught()) {
							throw new RemoteInvocationTargetException(event
									.getThrowable());
						}
						return event.getRet();
					}
				});
	}

	Future<List<Class<?>>> commandRequestInterfaces() {
		RequestInterfacesEvent event = new RequestInterfacesEvent(idGenerator);

		Future<RecieveInterfacesEvent> future = command(event);

		return Futures.lazyTransform(future,
				new Function<RecieveInterfacesEvent, List<Class<?>>>() {
					@Override
					public List<Class<?>> apply(RecieveInterfacesEvent event) {
						return event.getClasses();
					}
				});
	}

}
