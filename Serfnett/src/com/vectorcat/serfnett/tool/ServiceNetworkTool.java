package com.vectorcat.serfnett.tool;

import com.google.common.base.Preconditions;
import com.vectorcat.serfnett.api.ServiceNetwork;
import com.vectorcat.serfnett.api.ServiceProvider;
import com.vectorcat.serfnett.api.ServiceRegistry;

public class ServiceNetworkTool {

	/**
	 * @param root
	 *            Either a {@link ServiceNetwork}, {@link ServiceProvider}, or
	 *            {@link ServiceRegistry}
	 */
	public ServiceNetworkTool(Object root) {
		Preconditions
				.checkArgument(
						!(root instanceof ServiceNetwork)
								&& !(root instanceof ServiceProvider)
								&& !(root instanceof ServiceRegistry),
						"Argument must either be a ServiceNetwork, ServiceProvider, or ServiceRegistry!");

	}

	public void dispose() {
		// TODO
	}

	public void hide() {
		// TODO
	}

	public void refresh() {
		// TODO
	}

	public void show() {
		// TODO
	}

}
