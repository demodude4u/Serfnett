package com.vectorcat.serfnett.ext;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.vectorcat.serfnett.spi.Service;
import com.vectorcat.serfnett.spi.ServiceNode;
import com.vectorcat.serfnett.spi.ServiceRegistry;

public class RegistryDistribution extends AbstractServiceNode implements
		ServiceRegistry {

	private final Function<Service, ServiceRegistry> transform;
	private final ImmutableList<ServiceRegistry> registries;

	public RegistryDistribution(String descriptor,
			Iterable<ServiceRegistry> registries,
			Function<Service, ServiceRegistry> transform) {
		super(descriptor);
		this.registries = ImmutableList.copyOf(registries);
		this.transform = transform;
	}

	@Override
	public void addService(Service service) {
		getRegistry(service).addService(service);
	}

	@Override
	public Collection<? extends ServiceNode> getConnectedNodes() {
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
