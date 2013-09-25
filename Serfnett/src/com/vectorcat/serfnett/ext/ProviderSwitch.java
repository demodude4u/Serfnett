package com.vectorcat.serfnett.ext;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.vectorcat.serfnett.api.Service;
import com.vectorcat.serfnett.api.ServiceNode;
import com.vectorcat.serfnett.api.ServiceProvider;

public class ProviderSwitch extends AbstractServiceNode implements
		ServiceProvider {

	private final ImmutableList<ServiceProvider> providers;
	private final Function<List<ServiceProvider>, ServiceProvider> selector;

	public <T> ProviderSwitch(String descriptor,
			List<ServiceProvider> providers,
			Function<List<ServiceProvider>, ServiceProvider> selector) {
		super(descriptor);
		this.providers = ImmutableList.copyOf(providers);
		this.selector = selector;
	}

	@Override
	public Collection<? extends ServiceNode> getConnectedNodes() {
		return providers;
	}

	@Override
	public Collection<Service> getServices() {
		ServiceProvider selectedProvider = selector.apply(providers);
		return selectedProvider.getServices();
	}
}
