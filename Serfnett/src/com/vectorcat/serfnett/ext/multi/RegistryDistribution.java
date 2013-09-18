package com.vectorcat.serfnett.ext.multi;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.vectorcat.serfnett.api.Service;
import com.vectorcat.serfnett.api.ServiceNetwork;
import com.vectorcat.serfnett.api.ServiceProvider;
import com.vectorcat.serfnett.api.ServiceRegistry;

public class RegistryDistribution implements ServiceRegistry, ServiceNetwork {

	private final Function<Service, ServiceRegistry> transform;
	private final List<ServiceRegistry> registries;

	public RegistryDistribution(Iterable<ServiceRegistry> registries,
			Function<Service, ServiceRegistry> transform) {
		this.registries = ImmutableList.copyOf(registries);
		this.transform = transform;
	}

	@Override
	public void addService(Service service) {
		getRegistry(service).addService(service);
	}

	@Override
	public Collection<ServiceProvider> getProviders() {
		return ImmutableList.of();
	}

	@Override
	public Collection<ServiceRegistry> getRegistries() {
		return registries;
	}

	private ServiceRegistry getRegistry(Service service) {
		ServiceRegistry registry = transform.apply(service);
		return registry;
	}

	@Override
	public void removeService(Service service) {
		getRegistry(service).removeService(service);
	}

}
