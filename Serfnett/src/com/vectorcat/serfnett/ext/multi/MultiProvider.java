package com.vectorcat.serfnett.ext.multi;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.vectorcat.serfnett.api.Service;
import com.vectorcat.serfnett.api.ServiceProvider;

public class MultiProvider implements ServiceProvider {

	private final ServiceProvider[] providers;

	public MultiProvider(ServiceProvider... providers) {
		this.providers = providers;
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
