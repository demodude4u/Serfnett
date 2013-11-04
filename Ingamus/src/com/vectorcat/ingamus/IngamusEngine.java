package com.vectorcat.ingamus;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.vectorcat.serfnett.ServiceInjector;
import com.vectorcat.serfnett.ext.ProviderCollection;
import com.vectorcat.serfnett.ext.ProviderFunnel;
import com.vectorcat.serfnett.spi.Service;
import com.vectorcat.serfnett.spi.ServiceNode;
import com.vectorcat.serfnett.spi.ServiceProvider;
import com.vectorcat.serfnett.spi.ServiceRegistry;

public final class IngamusEngine implements ServiceProvider, ServiceRegistry {

	public static final class Builder {
		private final List<Module> modules = Lists.newArrayList();
		private final Set<ServiceProvider> providers = Sets.newLinkedHashSet();

		public Builder addModule(Module module) {
			modules.add(module);
			return this;
		}

		public Builder addServiceProvider(ServiceProvider provider) {
			providers.add(provider);
			return this;
		}

		public IngamusEngine build(String descriptor) {
			IngamusEventBus eventBus = createEventBus();
			IngamusActorService actorService = createActorService(eventBus);

			return new IngamusEngine(descriptor, new CompositionFactory(),
					ImmutableList.<Service> of(eventBus, actorService),
					modules, providers);
		}

		IngamusActorService createActorService(IngamusEventBus eventBus) {
			return new IngamusActorService(eventBus);
		}

		IngamusEventBus createEventBus() {
			return new IngamusEventBus();
		}

		ServiceProvider createInjectorsServiceProvider(
				List<ServiceProvider> providers) {
			if (providers.size() <= 1) {
				return Iterables.getFirst(providers,
						ProviderCollection.ofNothing());
			} else {
				return new ProviderFunnel("Ingamus External Providers",
						providers);
			}
		}
	}

	// XXX Find a cleaner way to do this "assisted inject"
	static class CompositionFactory {
		ServiceInjector createInjector(IngamusEngine engine,
				final Collection<Module> modules,
				Set<ServiceProvider> serviceProviders) {
			ProviderFunnel provider = new ProviderFunnel(
					"Ingamus Internal Funnel", ImmutableList
							.<ServiceProvider> builder()
							.addAll(serviceProviders).add(engine).build());

			Module module = new AbstractModule() {
				@Override
				protected void configure() {
					for (Module externalModule : modules) {
						install(externalModule);
					}
				}
			};

			return new ServiceInjector(provider, module, engine);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private final ImmutableSet<ServiceProvider> externalProviders;

	private final ServiceInjector serviceInjector;
	private final Collection<Service> addedServices;

	private final Collection<Service> allServices;

	private final String descriptor;

	IngamusEngine(String descriptor, CompositionFactory factory,
			Collection<Service> internalServices, Collection<Module> modules,
			Set<ServiceProvider> externalProviders) {
		this.descriptor = descriptor;
		this.externalProviders = ImmutableSet.copyOf(externalProviders);

		this.serviceInjector = factory.createInjector(this, modules,
				externalProviders);

		{
			ArrayListMultimap<String, Service> multimap = ArrayListMultimap
					.create();

			multimap.get("Builtin").addAll(internalServices);

			this.addedServices = multimap.get("Added");

			allServices = multimap.values();
		}

		for (Service service : allServices) {
			service.start();
		}
	}

	@Override
	public void addService(Service service) {
		if (!addedServices.contains(service)) {
			service.start();
			addedServices.add(service);
		}
	}

	@Override
	public Collection<? extends ServiceNode> getConnectedNodes() {
		return externalProviders;
	}

	@Override
	public String getDescriptor() {
		return descriptor;
	}

	@Override
	public Collection<Service> getServices() {
		return allServices;
	}

	public <T extends Service> T injectService(Class<T> clazz) {
		return serviceInjector.getService(clazz);
	}

	@Override
	public void removeService(Service service) {
		boolean removed = addedServices.remove(service);
		if (removed) {
			service.stop();
		}
	}

}
