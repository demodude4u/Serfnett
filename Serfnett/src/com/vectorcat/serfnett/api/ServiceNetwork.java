package com.vectorcat.serfnett.api;

import java.util.Collection;

public interface ServiceNetwork {
	public Collection<ServiceProvider> getProviders();

	public Collection<ServiceRegistry> getRegistries();
}
