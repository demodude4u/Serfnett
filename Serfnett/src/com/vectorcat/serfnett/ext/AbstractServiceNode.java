package com.vectorcat.serfnett.ext;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.vectorcat.serfnett.api.ServiceNode;

public abstract class AbstractServiceNode implements ServiceNode {

	private final String descriptor;

	public AbstractServiceNode() {
		this.descriptor = getClass().getSimpleName();
	}

	public AbstractServiceNode(String descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public Collection<? extends ServiceNode> getConnectedNodes() {
		return ImmutableList.of();
	}

	@Override
	public String getDescriptor() {
		return descriptor;
	}

}
