package com.vectorcat.ingamus.example.particles;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.vectorcat.ingamus.IngamusEventBus;
import com.vectorcat.ingamus.spi.IngamusService;

public class ParticleSamplerService extends AbstractIdleService implements
		IngamusService {

	private final Map<Particle, ParticleSampler> samplerMap = Maps
			.newLinkedHashMap();

	@Inject
	ParticleSamplerService(IngamusEventBus eventBus) {
		eventBus.register(this);
	}

	public Collection<Particle.F> getSamplesFor(Particle particle) {
		ParticleSampler sampler = samplerMap.get(particle);
		if (sampler == null) {
			return ImmutableList.of();
		} else {
			return sampler.getSamples();
		}
	}

	@Subscribe
	public void onParticleDisposed(ParticleDisposedEvent event) {
		Particle particle = event.getParticle();
		samplerMap.remove(particle);
	}

	@Subscribe
	public void onParticleSpawned(ParticleSpawnedEvent event) {
		Particle particle = event.getParticle();
		samplerMap.put(particle, new ParticleSampler(particle, 0.025, 8));
	}

	@Subscribe
	public void onUpdateParticles(UpdateParticlesEvent event) {
		for (ParticleSampler sampler : samplerMap.values()) {
			sampler.sample(event.getSimulationSecondsElapsed());
		}
	}

	@Override
	protected void shutDown() throws Exception {
		// NOP
	}

	@Override
	protected void startUp() throws Exception {
		// NOP
	}

}
