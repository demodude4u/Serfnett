package com.vectorcat.serfnett.api;

import java.util.Collection;

public interface ServiceProvider extends ServiceNode {
	public Collection<Service> getServices();
}
