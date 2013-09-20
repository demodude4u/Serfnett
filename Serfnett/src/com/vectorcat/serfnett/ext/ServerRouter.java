package com.vectorcat.serfnett.ext;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.inject.Inject;
import com.vectorcat.serfnett.api.Service;
import com.vectorcat.serfnett.api.ServiceNetwork;
import com.vectorcat.serfnett.api.ServiceProvider;
import com.vectorcat.serfnett.api.ServiceRegistry;

public class ServerRouter implements ServiceProvider, ServiceRegistry,
		ServiceNetwork {

	private final ProviderFunnel funnel;
	private final RegistryDistribution distribution;

	public ServerRouter(Iterable<ServiceRegistry> registries,
			Function<Service, ServiceRegistry> transform,
			Iterable<ServiceProvider> providers) {
		distribution = new RegistryDistribution(registries, transform);
		funnel = new ProviderFunnel(providers);
	}

	@Inject
	public ServerRouter(ProviderFunnel funnel, RegistryDistribution distribution) {
		this.funnel = funnel;
		this.distribution = distribution;
	}

	@Override
	public void addService(Service service) {
		distribution.addService(service);
	}

	@Override
	public Collection<ServiceProvider> getProviders() {
		Builder<ServiceProvider> builder = ImmutableList.builder();
		builder.addAll(funnel.getProviders());
		builder.addAll(distribution.getProviders());
		return builder.build();
	}

	@Override
	public Collection<ServiceRegistry> getRegistries() {
		Builder<ServiceRegistry> builder = ImmutableList.builder();
		builder.addAll(distribution.getRegistries());
		builder.addAll(funnel.getRegistries());
		return builder.build();
	}

	@Override
	public Collection<Service> getServices() {
		return funnel.getServices();
	}

	@Override
	public void removeService(Service service) {
		distribution.removeService(service);
	}

}
