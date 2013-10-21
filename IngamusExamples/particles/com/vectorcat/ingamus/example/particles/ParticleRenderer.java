package com.vectorcat.ingamus.example.particles;

import java.awt.Graphics2D;

public interface ParticleRenderer {
	public void onParticleDisposed(Particle particle);

	public void onParticleSpawned(Particle particle);

	public void render(Graphics2D g) throws Exception;
}
