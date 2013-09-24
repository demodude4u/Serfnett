package com.vectorcat.serfnett;

import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractService;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.vectorcat.serfnett.ServiceInjector;
import com.vectorcat.serfnett.api.Service;
import com.vectorcat.serfnett.api.ServiceProvider;
import com.vectorcat.serfnett.api.ServiceRegistry;

public class ServiceInjectorTest {

	public static class F1 extends AbstractService implements Service {

		private final IF2 f2;
		private final F3 f3;

		@Inject
		public F1(IF2 f2, F3 f3) {
			this.f2 = f2;
			this.f3 = f3;
		}

		@Override
		protected void doStart() {

		}

		@Override
		protected void doStop() {

		}
	}

	public static class F2 extends AbstractService implements Service, IF2 {

		private final F3 f3;

		@Inject
		public F2(F3 f3) {
			this.f3 = f3;
		}

		@Override
		protected void doStart() {

		}

		@Override
		protected void doStop() {

		}
	}

	public static class F3 extends AbstractService implements Service {
		@Override
		protected void doStart() {

		}

		@Override
		protected void doStop() {

		}
	}

	public static interface IF2 {
	}

	@Test
	// Working DI
	// Borrow from Collection
	// New DI on every call
	public void testSimpleIntegration() {
		F3 f3 = new F3();
		final Collection<Service> services = ImmutableList.<Service> of(f3);

		ServiceProvider provider = new ServiceProvider() {
			@Override
			public Collection<com.vectorcat.serfnett.api.Service> getServices() {
				return services;
			}
		};

		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IF2.class).to(F2.class);
			}
		};

		final List<Service> registryRemove = Lists.newArrayList();
		final List<Service> registryAdd = Lists.newArrayList();

		ServiceRegistry registry = new ServiceRegistry() {
			@Override
			public void addService(Service service) {
				registryAdd.add(service);
			}

			@Override
			public void removeService(Service service) {
				registryRemove.add(service);
			}
		};

		ServiceInjector serviceInjector = new ServiceInjector(provider, module,
				registry);

		F1 oldF1;
		F2 oldF2;
		{
			registryRemove.clear();
			registryAdd.clear();
			F1 f1 = serviceInjector.getService(F1.class);

			Assert.assertNotNull(f1.f2);
			Assert.assertNotNull(f1.f3);
			Assert.assertSame(f3, f1.f3);
			Assert.assertTrue(f1.f2 instanceof F2);
			F2 f2 = (F2) f1.f2;
			Assert.assertSame(f3, f2.f3);

			Assert.assertTrue(registryRemove.isEmpty());

			Assert.assertSame(2, registryAdd.size());
			Assert.assertTrue(registryAdd.contains(f1));
			Assert.assertTrue(registryAdd.contains(f2));
			Assert.assertFalse(registryAdd.contains(f3));

			oldF1 = f1;
			oldF2 = f2;
		}

		{
			registryRemove.clear();
			registryAdd.clear();
			F1 f1 = serviceInjector.getService(F1.class);

			Assert.assertNotNull(f1.f2);
			Assert.assertNotNull(f1.f3);
			Assert.assertSame(f3, f1.f3);
			Assert.assertTrue(f1.f2 instanceof F2);
			F2 f2 = (F2) f1.f2;
			Assert.assertSame(f3, f2.f3);

			Assert.assertNotSame(oldF1, f1);
			Assert.assertNotSame(oldF2, f2);
		}

	}

}
