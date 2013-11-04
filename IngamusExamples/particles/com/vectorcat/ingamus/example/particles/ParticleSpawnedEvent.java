package com.vectorcat.ingamus.example.particles;

import com.vectorcat.ingamus.spi.IngamusEvent;

public class ParticleSpawnedEvent implements IngamusEvent {

	private final Particle particle;

	public ParticleSpawnedEvent(Particle particle) {
		this.particle = particle;
	}

	public Particle getParticle() {
		return particle;
	}

}
