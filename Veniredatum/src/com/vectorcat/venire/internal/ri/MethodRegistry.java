package com.vectorcat.venire.internal.ri;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Maps;

public class MethodRegistry {

	private static class ClassEntry {
		private final ImmutableList<Method> methods;
		private final Map<Method, Integer> lookupID = Maps.newHashMap();

		public ClassEntry(Collection<Method> methods) {
			this.methods = ImmutableList.copyOf(methods);
			for (int i = 0; i < this.methods.size(); i++) {
				lookupID.put(this.methods.get(i), i);
			}
		}
	}

	Map<Class<?>, ClassEntry> mapClasses = Maps.newHashMap();

	private ClassEntry generateEntryFor(Class<?> clazz) {
		Builder<Method> builder = ImmutableList.builder();

		while (clazz != null) {

			Method[] declaredMethods = clazz.getDeclaredMethods();
			Arrays.sort(declaredMethods, new Comparator<Method>() {
				@Override
				public int compare(Method o1, Method o2) {
					return o2.toString().compareTo(o1.toString());
				}
			});

			for (Method method : declaredMethods) {
				method.setAccessible(true);

				builder.add(method);
			}

			clazz = clazz.getSuperclass();
		}

		return new ClassEntry(builder.build());
	}

	private ClassEntry getEntryOrCreate(Class<?> clazz) {
		ClassEntry entry = mapClasses.get(clazz);

		if (entry == null) {
			mapClasses.put(clazz, entry = generateEntryFor(clazz));
		}

		return entry;
	}

	public int getID(Class<?> clazz, Method method) {
		Map<Method, Integer> lookupID = getEntryOrCreate(clazz).lookupID;

		return lookupID.get(method);
	}

	public Method getMethod(Class<?> clazz, int methodID) {
		List<Method> methods = getEntryOrCreate(clazz).methods;

		return methods.get(methodID);
	}
}
