package com.vectorcat.serfnett.ext;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.vectorcat.serfnett.api.Service;
import com.vectorcat.serfnett.api.ServiceNetwork;
import com.vectorcat.serfnett.api.ServiceProvider;
import com.vectorcat.serfnett.api.ServiceRegistry;

public class ProviderFilter implements ServiceProvider, ServiceNetwork {

	private final ServiceProvider provider;
	private final Predicate<Service> filter;

	public ProviderFilter(ServiceProvider provider, Predicate<Service> filter) {
		this.provider = provider;
		this.filter = filter;
	}

	@Override
	public Collection<ServiceProvider> getProviders() {
		return ImmutableList.of(provider);
	}

	@Override
	public Collection<ServiceRegistry> getRegistries() {
		return ImmutableList.of();
	}

	@Override
	public Collection<Service> getServices() {
		return ImmutableList.copyOf(Iterables.filter(provider.getServices(),
				filter));
	}

}
