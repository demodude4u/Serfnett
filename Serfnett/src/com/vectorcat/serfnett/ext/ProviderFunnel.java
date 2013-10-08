package com.vectorcat.serfnett.ext;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.vectorcat.serfnett.spi.Service;
import com.vectorcat.serfnett.spi.ServiceNode;
import com.vectorcat.serfnett.spi.ServiceProvider;

public class ProviderFunnel extends AbstractServiceNode implements
		ServiceProvider {

	private final ImmutableList<ServiceProvider> providers;

	public ProviderFunnel(String descriptor, Iterable<ServiceProvider> providers) {
		super(descriptor);
		this.providers = ImmutableList.copyOf(providers);
	}

	@Override
	public Collection<? extends ServiceNode> getConnectedNodes() {
		return providers;
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
