package com.vectorcat.serfnett.spi;

public interface ServiceRegistry extends ServiceNode {
	public void addService(Service service);

	public void removeService(Service service);
}
