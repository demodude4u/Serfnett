package com.vectorcat.ingamus.example.particles;

public interface ParticleController {
	public void onParticleDisposed(Particle particle);

	public void onParticleSpawned(Particle particle);

	public void runDuration(double seconds, double secondsElapsed);
}
