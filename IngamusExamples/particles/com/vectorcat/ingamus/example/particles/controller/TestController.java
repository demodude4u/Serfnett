package com.vectorcat.ingamus.example.particles.controller;

import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.vectorcat.ingamus.example.particles.Particle;
import com.vectorcat.ingamus.example.particles.Particle.F;
import com.vectorcat.ingamus.example.particles.ParticleController;
import com.vectorcat.ingamus.example.particles.ParticleSpawnerService;
import com.vectorcat.ingamus.example.particles.util.FastMath;

public class TestController implements ParticleController {

	private static final double spawnRatePerSecond = 250;
	private static final double lifetime = 10;

	private final ParticleSpawnerService spawnerService;

	private final Set<Particle> particles = Sets.newLinkedHashSet();

	private double lastSpawnSecondsElapsed = 0;
	private final Random rand = new Random();

	@Inject
	TestController(ParticleSpawnerService spawnerService) {
		this.spawnerService = spawnerService;
	}

	private void doDisposing(Set<Particle> particles) {
		for (Particle particle : particles) {
			if (particle.getLifeSeconds() > lifetime) {
				particle.dispose();
			}
		}
	}

	private void doForces(Set<Particle> particles) {
		Particle.F f = new F();
		for (Particle particle : particles) {
			f.get(particle);

			double d = Math.sqrt(f.x * f.x + f.y * f.y) * 2;
			d = Math.min(0.95, d);
			double a = Math.sqrt(d) * Math.sin(d * 2 * Math.PI + Math.PI);
			double arad = Math.sqrt(1 - d)
					* Math.sin((1 - d) * 2 * Math.PI + Math.PI);
			a *= 1;
			arad *= 0.25;

			double dir = FastMath.atan2((float) -f.y, (float) -f.x);

			f.ax = Math.cos(dir) * a + Math.cos(dir + Math.PI / 2.0) * arad;
			f.ay = Math.sin(dir) * a + Math.sin(dir + Math.PI / 2.0) * arad;

			f.set(particle);
		}
	}

	private void doSpawning(double secondsElapsed) {
		double secondsBetweenSpawns = 1.0 / spawnRatePerSecond;
		while (secondsElapsed - lastSpawnSecondsElapsed > secondsBetweenSpawns) {
			lastSpawnSecondsElapsed += secondsBetweenSpawns;

			double r = 0.4 + rand.nextDouble() * 0.1;
			double d = -0;
			double dir = rand.nextDouble() * Math.PI * 2;

			double x = r * Math.cos(dir);
			double y = r * Math.sin(dir);
			double dx = d * Math.cos(dir + Math.PI / 2.0);
			double dy = d * Math.sin(dir + Math.PI / 2.0);

			spawnerService.spawnParticle(x, y, dx, dy);
		}
	}

	@Override
	public void onParticleDisposed(Particle particle) {
		particles.remove(particle);
	}

	@Override
	public void onParticleSpawned(Particle particle) {
		particles.add(particle);
	}

	@Override
	public void runDuration(double seconds, double secondsElapsed) {
		doSpawning(secondsElapsed);
		doForces(particles);
		doDisposing(particles);
	}
}
