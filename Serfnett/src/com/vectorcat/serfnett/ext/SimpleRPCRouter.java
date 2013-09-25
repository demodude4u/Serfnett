package com.vectorcat.serfnett.ext;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.vectorcat.serfnett.api.Service;
import com.vectorcat.serfnett.api.ServiceProvider;
import com.vectorcat.venire.InterfaceRegistry;
import com.vectorcat.venire.KryoStreamEventBus;
import com.vectorcat.venire.RemoteInterfacer;
import com.vectorcat.venire.api.EventBus;
import com.vectorcat.venire.api.StreamPipe;

/**
 * This router creates virtual interfaces that communicate across a
 * bi-directional Stream of data. <br>
 * <br>
 * Due to current limitations with Veniredatum, the services that are "served"
 * must exist already on some other server, like {@link SimpleServer}. This is
 * because Veniredatum's {@link RemoteInterfacer} does not support changes to
 * it's collection of interfaces over time, yet. <br>
 * <br>
 * A simple, working setup can be as follows:<br>
 * 
 * <pre>
 * SimpleServer<---SimpleRPCRouter<==[Pipe]==>SimpleRPCRouter--->SimpleServer
 *       /\              /\                          /\              /\
 *        \              /                            \              /
 *         ProviderFunnel                              ProviderFunnel
 * </pre>
 */
public class SimpleRPCRouter extends AbstractServiceNode implements
		ServiceProvider {

	private final RemoteInterfacer remoteInterfacer;

	private Optional<Collection<Service>> remoteServices = Optional.absent();

	public SimpleRPCRouter(String descriptor,
			InterfaceRegistry<Service> localServiceRegistry, EventBus bus) {
		this(descriptor, new RemoteInterfacer(bus, localServiceRegistry));
	}

	/**
	 * @see KryoStreamEventBus
	 */
	public SimpleRPCRouter(String descriptor,
			InterfaceRegistry<Service> localServiceRegistry, Kryo kryoRead,
			Kryo kryoWrite, InputStream inputStream, OutputStream outputStream) {
		this(descriptor, localServiceRegistry, new KryoStreamEventBus(kryoRead,
				kryoWrite, inputStream, outputStream));
	}

	/**
	 * @see KryoStreamEventBus
	 */
	public SimpleRPCRouter(String descriptor,
			InterfaceRegistry<Service> localServiceRegistry, Kryo kryoRead,
			Kryo kryoWrite, StreamPipe.Connector pipeConnector) {
		this(descriptor, localServiceRegistry, new KryoStreamEventBus(kryoRead,
				kryoWrite, pipeConnector));
	}

	SimpleRPCRouter(String descriptor, RemoteInterfacer remoteInterfacer) {
		super(descriptor);
		this.remoteInterfacer = remoteInterfacer;
	}

	private Collection<Service> fetchServices() {
		List<Class<?>> remoteInterfaces = remoteInterfacer
				.getRemoteInterfaces();

		Builder<Service> builder = ImmutableList.builder();

		for (int index = 0; index < remoteInterfaces.size(); index++) {
			Class<?> remoteServiceClass = remoteInterfaces.get(index);

			if (Service.class.isAssignableFrom(remoteServiceClass)) {

				Service service = remoteInterfacer.createRemoteInterface(index);

				builder.add(service);
			}
		}

		return builder.build();
	}

	@Override
	public Collection<Service> getServices() {
		if (!remoteServices.isPresent()) {
			remoteServices = Optional.of(fetchServices());
		}
		return remoteServices.get();
	}

}
