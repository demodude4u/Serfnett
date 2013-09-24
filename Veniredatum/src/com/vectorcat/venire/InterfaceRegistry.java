package com.vectorcat.venire;

import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class InterfaceRegistry<T> {

	public static class Builder<T> {
		Map<Object, Class<?>> mapInstanceInterface = Maps.newHashMap();
		List<Object> instanceOrder = Lists.newArrayList();

		public InterfaceRegistry<T> build() {
			return new InterfaceRegistry<T>(mapInstanceInterface, instanceOrder);
		}

		public <T2 extends T> void register(Class<T2> interfaceClass,
				T2 instance) {
			Preconditions.checkArgument(interfaceClass.isInterface());
			instanceOrder.add(instance);
			mapInstanceInterface.put(instance, interfaceClass);
		}
	}

	public static <T> Builder<T> builder() {
		return new Builder<>();
	}

	public static <T> InterfaceRegistry<T> createEmptyRegistry() {
		return new Builder<T>().build();
	}

	final ImmutableList<Object> instances;

	final ImmutableList<Class<?>> interfaces;

	final Map<Class<?>, Integer> lookupID = Maps.newHashMap();

	private InterfaceRegistry(final Map<?, Class<?>> mapInstanceInterface,
			List<?> instanceOrder) {
		this.instances = ImmutableList.copyOf(instanceOrder);

		this.interfaces = ImmutableList.copyOf(Lists.transform(instances,
				new Function<Object, Class<?>>() {
					@Override
					public Class<?> apply(Object instance) {
						return mapInstanceInterface.get(instance);
					}
				}));

		for (int i = 0; i < this.interfaces.size(); i++) {
			lookupID.put(this.interfaces.get(i), i);
		}
	}

	public int getID(Class<?> clazz) {
		return lookupID.get(clazz);
	}

	@SuppressWarnings("unchecked")
	public <T2 extends T> T2 getInstance(int ID) {
		return (T2) instances.get(ID);
	}

	public Class<?> getInterface(int ID) {
		return interfaces.get(ID);
	}

	public List<Class<?>> getInterfaces() {
		return interfaces;
	}
}
