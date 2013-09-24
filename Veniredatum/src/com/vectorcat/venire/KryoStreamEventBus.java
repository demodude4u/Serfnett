package com.vectorcat.venire;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.Service;
import com.vectorcat.venire.api.EventBus;
import com.vectorcat.venire.api.StreamPipe;

/**
 * This is an adapter for the {@link com.google.common.eventbus.EventBus} found
 * in Guava, to run events through a {@link Kryo}-serialized {@link StreamPipe}.
 * You need both sides of the {@link StreamPipe} to be adapted (with correct
 * {@link Kryo} setup) for this to work correctly.
 */
public class KryoStreamEventBus implements EventBus {

	private final Kryo kryoRead;
	private final Kryo kryoWrite;

	private final Service inputService;
	private final com.google.common.eventbus.EventBus eventBus;

	private final Output output;

	/**
	 * @param kryo
	 *            A pre-configured {@link Kryo} instance that is able to
	 *            serialize events being posted.
	 * @param inputStream
	 * @param outputStream
	 */
	public KryoStreamEventBus(Kryo kryoRead, Kryo kryoWrite,
			InputStream inputStream, OutputStream outputStream) {
		Preconditions.checkArgument(kryoRead != kryoWrite,
				"Kryo Read and Kryo Write cannot be the same Kryo instance!");

		this.kryoRead = kryoRead;
		this.kryoWrite = kryoWrite;

		// kryoRead.setAutoReset(true);
		kryoWrite.setAutoReset(true);

		inputService = createInputService(inputStream);
		eventBus = new com.google.common.eventbus.EventBus();

		output = new Output(outputStream);

		inputService.start();
	}

	public KryoStreamEventBus(Kryo kryoRead, Kryo kryoWrite,
			StreamPipe.Connector pipeConnector) {
		this(kryoRead, kryoWrite, pipeConnector.getInputStream(), pipeConnector
				.getOutputStream());
	}

	private Service createInputService(final InputStream inputStream) {
		return new AbstractScheduledService() {
			Input input = new Input(inputStream);

			@Override
			protected void runOneIteration() throws Exception {
				Object event = kryoRead.readClassAndObject(input);
				kryoRead.reset();

				eventBus.post(event);
			}

			@Override
			protected Scheduler scheduler() {
				return Scheduler.newFixedRateSchedule(0, 1,
						TimeUnit.MICROSECONDS);
			}
		};
	}

	private Runnable createPostTask(final Object event) {
		return new Runnable() {
			@Override
			public void run() {
				kryoWrite.writeClassAndObject(output, event);
				output.flush();
			}
		};
	}

	@Override
	public synchronized void post(Object event) {
		Runnable postTask = createPostTask(event);

		postTask.run();
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
