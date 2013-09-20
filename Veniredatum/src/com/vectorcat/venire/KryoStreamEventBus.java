package com.vectorcat.venire;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.Service;
import com.vectorcat.venire.api.EventBus;

/**
 * This is an adapter for the {@link com.google.common.eventbus.EventBus} found
 * in Guava, to run events through a {@link Kryo}-serialized {@link StreamPipe}.
 * You need both sides of the {@link StreamPipe} to be adapted (with correct
 * {@link Kryo} setup) for this to work correctly.
 */
public class KryoStreamEventBus implements EventBus {

	private final Kryo kryo;

	private final Service inputService;
	private final com.google.common.eventbus.EventBus eventBus;

	private final ExecutorService outputExecutorService;
	private final Output output;

	/**
	 * @param kryo
	 *            A pre-configured {@link Kryo} instance that is able to
	 *            serialize events being posted.
	 * @param inputStream
	 * @param outputStream
	 */
	public KryoStreamEventBus(Kryo kryo, InputStream inputStream,
			OutputStream outputStream) {
		this.kryo = kryo;

		kryo.setAutoReset(true);

		inputService = createInputService(inputStream);
		eventBus = new com.google.common.eventbus.EventBus();

		outputExecutorService = createOutputExecutorService();
		output = new Output(outputStream);

		inputService.start();
	}

	private ThreadFactory createDaemonThreadFactory() {
		return new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		};
	}

	private Service createInputService(final InputStream inputStream) {
		return new AbstractScheduledService() {
			Input input = new Input(inputStream);

			@Override
			protected void runOneIteration() throws Exception {
				Object event = kryo.readClassAndObject(input);

				eventBus.post(event);
			}

			@Override
			protected Scheduler scheduler() {
				return Scheduler.newFixedRateSchedule(0, 1,
						TimeUnit.MICROSECONDS);
			}
		};
	}

	private ExecutorService createOutputExecutorService() {
		return Executors.newSingleThreadExecutor(createDaemonThreadFactory());
	}

	private Runnable createPostTask(final Object event) {
		return new Runnable() {
			@Override
			public void run() {
				kryo.writeClassAndObject(output, event);
				output.flush();
			}
		};
	}

	@Override
	public void post(Object event) throws InterruptedException {
		Runnable postTask = createPostTask(event);

		Future<?> future = outputExecutorService.submit(postTask);

		try {
			future.get();
		} catch (ExecutionException e) {
			throw new Error(e);
		}
	}

	@Override
	public void register(Object listener) {
		eventBus.register(listener);
	}

	@Override
	public void unregister(Object listener) {
		eventBus.unregister(listener);
	}

}
