package com.vectorcat.serfnett.spi;

import java.util.Collection;

public interface ServiceNode {
	public Collection<? extends ServiceNode> getConnectedNodes();

	public String getDescriptor();
}
