package com.vectorcat.venire.spi;

import java.util.List;

public interface RemoteInterfacer {

	public abstract <T> T createRemoteInterface(Class<T> clazz);

	public abstract <T> T createRemoteInterface(int interfaceIndex);

	public abstract List<Class<?>> getRemoteInterfaces();

}
