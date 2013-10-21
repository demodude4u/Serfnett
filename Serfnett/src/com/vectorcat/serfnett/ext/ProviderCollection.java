package com.vectorcat.serfnett.ext;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.vectorcat.serfnett.spi.Service;
import com.vectorcat.serfnett.spi.ServiceNode;
import com.vectorcat.serfnett.spi.ServiceProvider;

public class ProviderCollection implements ServiceProvider {

	public static ProviderCollection ofNothing() {
		return new ProviderCollection("Nothing", ImmutableList.<Service> of());
	}

	private final String descriptor;
	private final ImmutableCollection<Service> services;

	public ProviderCollection(String descriptor, Collection<Service> services) {
		this.descriptor = descriptor;
		this.services = ImmutableList.copyOf(services);
	}

	public ProviderCollection(String descriptor, Service... services) {
		this(descriptor, Arrays.asList(services));
	}

	@Override
	public Collection<? extends ServiceNode> getConnectedNodes() {
		return ImmutableList.of();
	}

	@Override
	public String getDescriptor() {
		return descriptor;
	}

	@Override
	public Collection<Service> getServices() {
		return services;
	}
}
