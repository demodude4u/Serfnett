package com.vectorcat.ingamus.example.particles;

import com.vectorcat.ingamus.spi.IngamusEvent;

public class ParticleDisposedEvent implements IngamusEvent {

	private final Particle particle;

	public ParticleDisposedEvent(Particle particle) {
		this.particle = particle;
	}

	public Particle getParticle() {
		return particle;
	}

}
