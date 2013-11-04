package com.vectorcat.ingamus.example.particles;

import javax.media.opengl.GL2;

public interface ParticleRendererJOGL {

	public static interface Factory {
		public ParticleRendererJOGL create(GL2 gl2);
	}

	public void onParticleDisposed(Particle particle);

	public void onParticleSpawned(Particle particle);

	public void render(GL2 gl2) throws Exception;
}
