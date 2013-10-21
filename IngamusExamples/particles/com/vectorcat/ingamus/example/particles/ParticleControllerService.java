package com.vectorcat.ingamus.example.particles;

import java.util.LinkedHashSet;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.vectorcat.ingamus.ActorDisposedEvent;
import com.vectorcat.ingamus.ActorSpawnedEvent;
import com.vectorcat.ingamus.IngamusEventBus;
import com.vectorcat.ingamus.example.particles.Particle.F;
import com.vectorcat.ingamus.spi.AbstractIngamusFPSService;

public class ParticleControllerService extends AbstractIngamusFPSService
		implements FPSMonitor {

	public static final double FPS = 60;

	private final ParticleSpawnerService spawnerService;
	private final ParticleController controller;

	private final LinkedHashSet<Particle> particles = Sets.newLinkedHashSet();

	private boolean hasFirstIteration = false;
	private long firstIterationTimeStamp;
	private long lastIterationTimeStamp;

	private double simulationSecondsElapsed = 0;

	@Inject
	ParticleControllerService(IngamusEventBus eventBus,
			ParticleSpawnerService spawnerService, ParticleController controller) {
		this.spawnerService = spawnerService;
		this.controller = controller;

		eventBus.register(this);
	}

	private void applyPhysicsForDuration(LinkedHashSet<Particle> particles,
			double seconds) {
		Particle.F f = new F();
		for (Particle particle : particles) {
			f.get(particle);

			double t = seconds;

			f.x = f.ax / 2.0 * t * t + f.dx * t + f.x;
			f.y = f.ay / 2.0 * t * t + f.dy * t + f.y;
			f.dx = f.ax * t + f.dx;
			f.dy = f.ay * t + f.dy;

			f.lifeSeconds += t;

			f.set(particle);
		}
	}

	@Override
	protected double getDesiredFPS() {
		return FPS;
	}

	@Subscribe
	public void onActorDisposed(ActorDisposedEvent event) {
		if (event.actorInstanceOf(Particle.class)) {
			Particle particle = event.getCastedActor();
			controller.onParticleDisposed(particle);
			onParticleDisposed(particle);
		}
	}

	@Subscribe
	public void onActorSpawned(ActorSpawnedEvent event) {
		if (event.actorInstanceOf(Particle.class)) {
			Particle particle = event.getCastedActor();
			controller.onParticleSpawned(particle);
			onParticleSpawned(particle);
		}
	}

	private void onParticleDisposed(Particle particle) {
		particles.remove(particle);
	}

	private void onParticleSpawned(Particle particle) {
		particles.add(particle);
	}

	@Override
	protected void runOneFrame() {
		long currentTimeMillis = System.currentTimeMillis();

		if (!hasFirstIteration) {
			hasFirstIteration = true;
			firstIterationTimeStamp = lastIterationTimeStamp = currentTimeMillis;
		}

		double seconds = (currentTimeMillis - lastIterationTimeStamp) / 1000.0;
		double secondsElapsed = (currentTimeMillis - firstIterationTimeStamp) / 1000.0;

		// double simulationSeconds = seconds;
		double simulationSeconds = 1.0 / FPS;
		// double simulationSeconds = 0.1 / FPS;
		simulationSecondsElapsed += simulationSeconds;

		controller.runDuration(simulationSeconds, simulationSecondsElapsed);

		spawnerService.updateActors();

		applyPhysicsForDuration(particles, simulationSeconds);

		lastIterationTimeStamp = currentTimeMillis;
	}

	@Override
	protected void uncaughtException(RuntimeException e) {
		e.printStackTrace();
	}

}
