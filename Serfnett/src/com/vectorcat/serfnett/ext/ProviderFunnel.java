package com.vectorcat.serfnett.ext;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.vectorcat.serfnett.api.Service;
import com.vectorcat.serfnett.api.ServiceNetwork;
import com.vectorcat.serfnett.api.ServiceProvider;
import com.vectorcat.serfnett.api.ServiceRegistry;

public class ProviderFunnel implements ServiceProvider, ServiceNetwork {

	private final Collection<ServiceProvider> providers;

	public ProviderFunnel(Iterable<ServiceProvider> providers) {
		this.providers = ImmutableList.copyOf(providers);
	}

	public ProviderFunnel(ServiceProvider... providers) {
		this(Arrays.asList(providers));
	}

	@Override
	public Collection<ServiceProvider> getProviders() {
		return providers;
	}

	@Override
	public Collection<ServiceRegistry> getRegistries() {
		return ImmutableList.of();
	}

	@Override
	public Collection<Service> getServices() {
		Builder<Service> builder = ImmutableList.builder();

		for (ServiceProvider provider : providers) {
			builder.addAll(provider.getServices());
		}

		return builder.build();
	}

}
