package com.vectorcat.ingamus;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.vectorcat.ingamus.IngamusEngine.CompositionFactory;
import com.vectorcat.serfnett.ServiceInjector;
import com.vectorcat.serfnett.spi.Service;
import com.vectorcat.serfnett.spi.ServiceNode;
import com.vectorcat.serfnett.spi.ServiceProvider;

public class IngamusEngineTest {

	private IngamusEngine ingamusEngine;

	private Module moduleMock;
	private Service serviceMock;
	private CompositionFactory compositionFactoryMock;
	private ServiceInjector serviceInjectorMock;
	private ServiceProvider serviceProviderMock;

	@Before
	public void setUp() throws Exception {
		compositionFactoryMock = mock(CompositionFactory.class);
		serviceInjectorMock = mock(ServiceInjector.class);
		stub(
				compositionFactoryMock.createInjector(any(IngamusEngine.class),
						anyListOf(Module.class),
						anySetOf(ServiceProvider.class))).toReturn(
				serviceInjectorMock);

		serviceMock = mock(Service.class);
		moduleMock = mock(Module.class);
		serviceProviderMock = mock(ServiceProvider.class);
		ingamusEngine = new IngamusEngine("Test", compositionFactoryMock,
				ImmutableList.of(serviceMock), ImmutableList.of(moduleMock),
				ImmutableSet.of(serviceProviderMock));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddService() {
		Service serviceMock2 = mock(Service.class);
		ingamusEngine.addService(serviceMock2);

		verify(serviceMock2).start();
		assertTrue(ingamusEngine.getServices().contains(serviceMock2));
	}

	@Test
	public void testGetConnectedNodes() {
		Collection<? extends ServiceNode> connectedNodes = ingamusEngine
				.getConnectedNodes();

		assertTrue(connectedNodes.contains(serviceProviderMock));
	}

	@Test
	public void testGetDescriptor() {
		String descriptor = ingamusEngine.getDescriptor();

		assertEquals("Test", descriptor);
	}

	@Test
	public void testGetService() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetServices() {
		Collection<Service> services = ingamusEngine.getServices();

		assertTrue(services.contains(serviceMock));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testIngamusEngine() {
		ArgumentCaptor<List> modulesCaptor = ArgumentCaptor
				.forClass(List.class);
		ArgumentCaptor<Set> providersCaptor = ArgumentCaptor
				.forClass(Set.class);

		verify(compositionFactoryMock).createInjector(eq(ingamusEngine),
				modulesCaptor.capture(), providersCaptor.capture());
		assertTrue(modulesCaptor.getValue().contains(moduleMock));
		assertTrue(providersCaptor.getValue().contains(serviceProviderMock));

		verify(serviceMock).start();
	}

	@Test
	public void testRemoveService() {
		fail("Not yet implemented");
	}

}
