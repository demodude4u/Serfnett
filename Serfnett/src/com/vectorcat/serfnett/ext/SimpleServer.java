package com.vectorcat.serfnett.ext;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.vectorcat.serfnett.spi.Service;
import com.vectorcat.serfnett.spi.ServiceProvider;
import com.vectorcat.serfnett.spi.ServiceRegistry;

public class SimpleServer extends AbstractServiceNode implements
		ServiceProvider, ServiceRegistry {

	private final Set<Service> services = Sets.newLinkedHashSet();

	public SimpleServer(String descriptor) {
		super(descriptor);
	}

	@Override
	public void addService(Service service) {
		if (!services.contains(service)) {
			service.start();
			services.add(service);
		}
	}

	@Override
	public Collection<Service> getServices() {
		return services;
	}

	@Override
	public void removeService(Service service) {
		boolean removed = services.remove(service);
		if (removed) {
			service.stop();
		}
	}

}
