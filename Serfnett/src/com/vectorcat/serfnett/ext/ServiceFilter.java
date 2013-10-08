package com.vectorcat.serfnett.ext;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.vectorcat.serfnett.spi.Service;
import com.vectorcat.serfnett.spi.ServiceNode;
import com.vectorcat.serfnett.spi.ServiceProvider;

public class ServiceFilter extends AbstractServiceNode implements
		ServiceProvider {

	private final ServiceProvider provider;
	private final Predicate<Service> filter;

	public ServiceFilter(String descriptor, ServiceProvider provider,
			Predicate<Service> filter) {
		super(descriptor);
		this.provider = provider;
		this.filter = filter;
	}

	@Override
	public Collection<? extends ServiceNode> getConnectedNodes() {
		return ImmutableList.of(provider);
	}

	@Override
	public Collection<Service> getServices() {
		return ImmutableList.copyOf(Iterables.filter(provider.getServices(),
				filter));
	}

}
