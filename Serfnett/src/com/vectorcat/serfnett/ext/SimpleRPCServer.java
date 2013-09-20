package com.vectorcat.serfnett.ext;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import com.google.common.collect.Lists;
import com.vectorcat.serfnett.api.Service;
import com.vectorcat.serfnett.api.ServiceRegistry;

public class SimpleRPCServer implements ServiceRegistry {

	private final Collection<Service> services = Lists.newArrayList();

	private final InputStream inputStream;
	private final OutputStream outputStream;

	public SimpleRPCServer(InputStream inputStream, OutputStream outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	@Override
	public void addService(Service service) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeService(Service service) {
		// TODO Auto-generated method stub

	}

}
