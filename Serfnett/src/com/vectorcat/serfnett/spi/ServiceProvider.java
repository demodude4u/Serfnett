package com.vectorcat.serfnett.spi;

import java.util.Collection;

public interface ServiceProvider extends ServiceNode {
	public Collection<Service> getServices();
}
