package com.vectorcat.venire;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.vectorcat.venire.test.Tests;

public class SimpleStreamPipeTest {

	private class Feeder extends AbstractExecutionThreadService {

		private final InputStream inputStream;
		private final OutputStream outputStream;

		private volatile int bytesFed = 0;

		public Feeder(InputStream inputStream, OutputStream outputStream) {
			this.inputStream = inputStream;
			this.outputStream = outputStream;
		}

		@Override
		protected void run() throws Exception {
			byte[] buffer = new byte[2048];
			while (true) {
				int count;
				while ((count = inputStream.read(buffer)) == -1) {
					Thread.yield();// BUSY WAITING :D
				}

				outputStream.write(buffer, 0, count);
				bytesFed += count;

				outputStream.flush();
			}
		};
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSimpleTest() {
		final byte[] inputData = new byte[100000];
		Random rand = new Random(1234);
		rand.nextBytes(inputData);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(inputData);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
				inputData.length);

		SimpleStreamPipe pipe = new SimpleStreamPipe();

		final Feeder feedIn = new Feeder(inputStream,
				pipe.getLeftOutputStream());
		final Feeder feedLoopback = new Feeder(pipe.getRightInputStream(),
				pipe.getRightOutputStream());
		final Feeder feedOut = new Feeder(pipe.getLeftInputStream(),
				outputStream);

		feedIn.start();
		feedLoopback.start();
		feedOut.start();

		Tests.assertTrueEventually(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return feedOut.bytesFed == inputData.length;
			}
		}, 5000, new Callable<String>() {
			@Override
			public String call() throws Exception {
				return feedIn.bytesFed + "/" + feedLoopback.bytesFed + "/"
						+ feedOut.bytesFed + " out of " + inputData.length;
			}
		});

		assertArrayEquals(inputData, outputStream.toByteArray());
	}

}
