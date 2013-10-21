package com.vectorcat.ingamus.example.particles;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.vectorcat.ingamus.IngamusActorService;
import com.vectorcat.ingamus.IngamusActorService.BatchJob;
import com.vectorcat.ingamus.example.particles.ParticleActor.Factory;
import com.vectorcat.ingamus.spi.IngamusService;

@Singleton
public class ParticleSpawnerService extends AbstractIdleService implements
		IngamusService {

	private final Factory factory;
	private final BatchJob batchJob;

	@Inject
	ParticleSpawnerService(ParticleActor.Factory factory,
			@Named("ParticleSpawner") IngamusActorService.BatchJob batchJob) {
		this.factory = factory;
		this.batchJob = batchJob;
	}

	@Override
	protected void shutDown() throws Exception {
		// NOP
	}

	public void spawnParticle(double x, double y) {
		spawnParticle(x, y, 0, 0, 0, 0);
	}

	public void spawnParticle(double x, double y, double dx, double dy) {
		spawnParticle(x, y, dx, dy, 0, 0);
	}

	public void spawnParticle(double x, double y, double dx, double dy,
			double ax, double ay) {
		ParticleActor particleActor = factory.createParticle(x, y, dx, dy, ax,
				ay);
		batchJob.spawnActor(particleActor);
	}

	@Override
	protected void startUp() throws Exception {
		// NOP
	}

	public void updateActors() {
		batchJob.processAll();
	}

}
