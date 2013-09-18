package com.vectorcat.serfnett;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.vectorcat.serfnett.api.Service;
import com.vectorcat.serfnett.api.ServiceProvider;
import com.vectorcat.serfnett.api.ServiceRegistry;

public class ServiceInjector {

	private final ServiceProvider provider;
	private final Module existingModule;
	private final ServiceRegistry registry;

	public <Server extends ServiceProvider & ServiceRegistry> ServiceInjector(
			Module module, Server server) {
		this(server, module, server);
	}

	public ServiceInjector(ServiceProvider provider, Module module,
			ServiceRegistry registry) {
		this.provider = provider;
		this.existingModule = module;
		this.registry = registry;
	}

	private Injector createInjector() {

		Module module = new AbstractModule() {
			@SuppressWarnings("unchecked")
			private <T extends Service> void bindFeatureInstance(Binder binder,
					T service) {
				binder.bind((Class<T>) service.getClass()).toInstance(service);
			}

			@Override
			protected void configure() {
				existingModule.configure(binder());

				Collection<Service> services = provider.getServices();

				for (Service existingFeature : services) {
					bindFeatureInstance(binder(), existingFeature);
				}

				Matcher<? super TypeLiteral<?>> matcher = new AbstractMatcher<TypeLiteral<?>>() {
					@Override
					public boolean matches(TypeLiteral<?> t) {
						return Service.class.isAssignableFrom(t.getRawType());
					}
				};

				{
					final Set<Service> lookupServices = ImmutableSet
							.copyOf(services);
					final Set<Service> foundNewServices = Sets.newHashSet();

					bindListener(matcher, new TypeListener() {
						@Override
						public <I> void hear(TypeLiteral<I> literal,
								TypeEncounter<I> encounter) {

							encounter.register(new InjectionListener<I>() {
								@Override
								public void afterInjection(I injectee) {

									Service service = (Service) injectee;

									if (!lookupServices.contains(service)
											&& !foundNewServices
													.contains(service)) {

										foundNewServices.add(service);

										registry.addService(service);
									}
								}
							});
						}
					});
				}
			}
		};

		Injector injector = Guice.createInjector(module);

		return injector;
	}

	public <T extends Service> T getService(Class<T> clazz) {
		Injector injector = createInjector();
		return injector.getInstance(clazz);
	}

}
