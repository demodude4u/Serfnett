package com.vectorcat.serfnett.api;

import java.util.Collection;

public interface ServiceNode {
	public Collection<? extends ServiceNode> getConnectedNodes();

	public String getDescriptor();
}
