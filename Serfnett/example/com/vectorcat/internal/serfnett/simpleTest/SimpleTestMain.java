package com.vectorcat.internal.serfnett.simpleTest;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.vectorcat.serfnett.ServiceInjector;
import com.vectorcat.serfnett.ext.SimpleServer;
import com.vectorcat.serfnett.spi.Service;

public class SimpleTestMain {

	public static void main(String[] args) throws InterruptedException {
		SimpleServer simpleServer = new SimpleServer("SimpleTestMain");

		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(UI.class).to(UIImpl.class);
			}
		};
		ServiceInjector injector = new ServiceInjector(module, simpleServer);

		// Start!
		UIImpl ui = injector.getService(UIImpl.class);
		ui.showUI();

		// Pretend I'm running...
		Thread.sleep(5000);

		// Pretend I want to shutdown now
		Collection<Service> services = ImmutableList.copyOf(simpleServer
				.getServices());
		for (Service service : services) {
			simpleServer.removeService(service);
		}
	}
}
