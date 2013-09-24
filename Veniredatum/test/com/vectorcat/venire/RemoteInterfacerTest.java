package com.vectorcat.venire;

import static junit.framework.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.minlog.Log;
import com.vectorcat.venire.InterfaceRegistry.Builder;
import com.vectorcat.venire.api.EventBus;

public class RemoteInterfacerTest {

	private interface RemoteTest {
		public String getString();

		public void setString(String string);
	}

	@Before
	public void setUp() throws Exception {
	}

	// Create RI pair with different RemoteTest instances
	// Request remote RI's and call RI methods
	// Confirm RI's returned correct values
	private void simpleTest(EventBus bus1, EventBus bus2) {
		Log.TRACE();

		Builder<Object> builder = InterfaceRegistry.builder();
		builder.register(RemoteTest.class, new RemoteTest() {
			private String string;

			@Override
			public String getString() {
				return string;
			}

			@Override
			public void setString(String string) {
				this.string = string;
			}
		});
		builder.register(RemoteTest.class, new RemoteTest() {
			private String string;

			@Override
			public String getString() {
				return string;
			}

			@Override
			public void setString(String string) {
				this.string = string + "bob";
			}
		});

		RemoteInterfacer ri1 = new RemoteInterfacer(bus1);
		new RemoteInterfacer(bus2, builder.build());

		List<Class<?>> remoteInterfaces = ri1.getRemoteInterfaces();
		assertEquals(2, remoteInterfaces.size());
		assertSame(RemoteTest.class, remoteInterfaces.get(0));
		assertSame(RemoteTest.class, remoteInterfaces.get(1));

		RemoteTest test1 = ri1.createRemoteInterface(0);
		test1.setString("Billy Mays");
		assertEquals("Billy Mays", test1.getString());

		RemoteTest test2 = ri1.createRemoteInterface(1);
		test2.setString("Billy Mays");
		assertEquals("Billy Maysbob", test2.getString());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	// Create Bus using KryoStreamEventBus + SimpleStreamPipe
	// @See SimpleTest
	public void testKryoSimpleIntegration() throws Exception {
		Kryo kryoRead1 = new Kryo();
		Kryo kryoWrite1 = new Kryo();
		Kryo kryoRead2 = new Kryo();
		Kryo kryoWrite2 = new Kryo();

		SimpleStreamPipe pipe = new SimpleStreamPipe();

		KryoStreamEventBus bus1 = new KryoStreamEventBus(kryoRead1, kryoWrite1,
				pipe.getLeft());
		KryoStreamEventBus bus2 = new KryoStreamEventBus(kryoRead2, kryoWrite2,
				pipe.getRight());

		simpleTest(bus1, bus2);
	}

	@Test
	// Create Bus using a pair of Guava's EventBus
	// @See SimpleTest
	public void testSimpleIntegration() {
		final com.google.common.eventbus.EventBus guavaBus1 = new com.google.common.eventbus.EventBus();
		final com.google.common.eventbus.EventBus guavaBus2 = new com.google.common.eventbus.EventBus();
		EventBus bus1 = new EventBus() {
			@Override
			public void post(Object event) throws InterruptedException {
				guavaBus2.post(event);
			}

			@Override
			public void register(Object listener) {
				guavaBus1.register(listener);
			}

			@Override
			public void unregister(Object listener) {
				guavaBus1.unregister(listener);
			}
		};
		EventBus bus2 = new EventBus() {
			@Override
			public void post(Object event) throws InterruptedException {
				guavaBus1.post(event);
			}

			@Override
			public void register(Object listener) {
				guavaBus2.register(listener);
			}

			@Override
			public void unregister(Object listener) {
				guavaBus2.unregister(listener);
			}
		};

		simpleTest(bus1, bus2);
	}
}
