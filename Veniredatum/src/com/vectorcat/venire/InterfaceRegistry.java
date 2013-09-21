package com.vectorcat.venire;

import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class InterfaceRegistry {

	public static class Builder {
		Map<Object, Class<?>> mapInstanceInterface = Maps.newHashMap();
		List<Object> instanceOrder = Lists.newArrayList();

		public InterfaceRegistry build() {
			return new InterfaceRegistry(mapInstanceInterface, instanceOrder);
		}

		public <T> void register(Class<T> interfaceClass, T instance) {
			Preconditions.checkArgument(interfaceClass.isInterface());
			instanceOrder.add(instance);
			mapInstanceInterface.put(instance, interfaceClass);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	final ImmutableList<Object> instances;

	final ImmutableList<Class<?>> interfaces;

	final Map<Class<?>, Integer> lookupID = Maps.newHashMap();

	private InterfaceRegistry(final Map<Object, Class<?>> mapInstanceInterface,
			List<Object> instanceOrder) {
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
	public <T> T getInstance(int ID) {
		return (T) instances.get(ID);
	}

	public Class<?> getInterface(int ID) {
		return interfaces.get(ID);
	}

	public List<Class<?>> getInterfaces() {
		return interfaces;
	}

}
