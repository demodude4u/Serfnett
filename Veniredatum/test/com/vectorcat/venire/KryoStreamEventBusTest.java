package com.vectorcat.venire;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.minlog.Log;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.vectorcat.venire.test.Tests;

public class KryoStreamEventBusTest {

	public static class TestEvent {
		private String string;

		public TestEvent() {
		}

		public TestEvent(String string) {
			this.string = string;
		}
	}

	private class TestReciever {
		List<TestEvent> recieved = Lists.newArrayList();

		@Subscribe
		public void recieveTestEvent(TestEvent event) {
			recieved.add(event);
		}
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	// Create with SimpleStreamPipe
	// Send TestEvent both directions
	// Confirm received
	public void testSimpleIntegration() {
		Log.TRACE();

		Kryo kryoRead1 = new Kryo();
		Kryo kryoWrite1 = new Kryo();
		Kryo kryoRead2 = new Kryo();
		Kryo kryoWrite2 = new Kryo();

		SimpleStreamPipe pipe = new SimpleStreamPipe();

		KryoStreamEventBus bus1 = new KryoStreamEventBus(kryoRead1, kryoWrite1,
				pipe.getLeft());
		KryoStreamEventBus bus2 = new KryoStreamEventBus(kryoRead2, kryoWrite2,
				pipe.getRight());

		TestEvent event1 = new TestEvent("Potato");
		TestEvent event2 = new TestEvent("BBQ");

		final TestReciever reciever1 = new TestReciever();
		final TestReciever reciever2 = new TestReciever();

		bus1.register(reciever1);
		bus2.register(reciever2);

		bus1.post(event1);
		bus2.post(event2);

		Tests.assertTrueEventually(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return reciever1.recieved.size() == 1
						&& reciever2.recieved.size() == 1;
			}
		}, 5000, new Callable<String>() {
			@Override
			public String call() throws Exception {
				return "Reciever 1: " + reciever1.recieved.size()
						+ " Reciever 2: " + reciever2.recieved.size();
			}
		});

		assertEquals(1, reciever1.recieved.size());
		assertEquals(1, reciever2.recieved.size());

		assertEquals("BBQ", reciever1.recieved.get(0).string);
		assertEquals("Potato", reciever2.recieved.get(0).string);

	}
}
