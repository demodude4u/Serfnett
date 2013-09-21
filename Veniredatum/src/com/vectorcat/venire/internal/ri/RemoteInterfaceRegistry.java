package com.vectorcat.venire.internal.ri;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class RemoteInterfaceRegistry {

	private final EventCommander eventCommander;

	private final Map<Class<?>, Integer> lookupID = Maps.newHashMap();

	private Optional<List<Class<?>>> remoteInterfaces = Optional.absent();

	public RemoteInterfaceRegistry(EventCommander eventCommander) {
		this.eventCommander = eventCommander;
	}

	private void checkOrFetchInterfaces() {
		if (!remoteInterfaces.isPresent()) {
			try {
				List<Class<?>> interfaces = eventCommander
						.commandRequestInterfaces().get();

				remoteInterfaces = Optional.of(interfaces);

				for (int i = 0; i < interfaces.size(); i++) {
					lookupID.put(interfaces.get(i), i);
				}

			} catch (InterruptedException e) {
				throw new Error(e);
			} catch (ExecutionException e) {
				// XXX Should be throwing the error, but no public api to do so
				// yet
				throw new Error(e);
			}
		}
	}

	public int getID(Class<?> clazz) {
		checkOrFetchInterfaces();
		return lookupID.get(clazz);
	}

	public Class<?> getInterface(int ID) {
		checkOrFetchInterfaces();
		return remoteInterfaces.get().get(ID);
	}

	public List<Class<?>> getInterfaces() {
		checkOrFetchInterfaces();
		return remoteInterfaces.get();
	}
}
