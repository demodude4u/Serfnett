package com.vectorcat.serfnett.ext.unit;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.vectorcat.serfnett.api.Service;
import com.vectorcat.serfnett.api.ServiceProvider;
import com.vectorcat.serfnett.api.ServiceRegistry;

public class SimpleUnitServer implements ServiceProvider, ServiceRegistry {

	private final Set<Service> services = Sets.newLinkedHashSet();

	@Override
	public void addService(Service service) {
		services.add(service);
	}

	@Override
	public Collection<Service> getServices() {
		return services;
	}

	@Override
	public void removeService(Service service) {
		services.add(service);
	}

}
