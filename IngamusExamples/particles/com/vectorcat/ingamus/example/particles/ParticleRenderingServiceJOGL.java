package com.vectorcat.ingamus.example.particles;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jogamp.opengl.util.FPSAnimator;
import com.vectorcat.ingamus.ActorDisposedEvent;
import com.vectorcat.ingamus.ActorSpawnedEvent;
import com.vectorcat.ingamus.IngamusEventBus;
import com.vectorcat.ingamus.example.particles.ParticleRendererJOGL.Factory;
import com.vectorcat.ingamus.spi.IngamusService;

@Singleton
public class ParticleRenderingServiceJOGL extends AbstractIdleService implements
		IngamusService, FPSMonitor {

	private static final int FPS = 60;

	private Optional<ParticleRendererJOGL> renderer = Optional.absent();

	private final GLCanvas canvas;
	private final FPSAnimator animator;

	private final Factory rendererFactory;

	@Inject
	ParticleRenderingServiceJOGL(IngamusEventBus eventBus,
			ParticleRendererJOGL.Factory rendererFactory) {
		this.rendererFactory = rendererFactory;

		eventBus.register(this);

		GLProfile profile = createProfile();
		GLCapabilities capabilities = createCapabilities(profile);
		canvas = createCanvas(capabilities);

		animator = createAnimator(canvas);
		animator.setUpdateFPSFrames(FPS / 4, null);

		canvas.addGLEventListener(new GLEventListener() {
			@Override
			public void display(GLAutoDrawable drawable) {
				render(drawable.getGL().getGL2(), drawable.getWidth(),
						drawable.getHeight());
			}

			@Override
			public void dispose(GLAutoDrawable drawable) {
				// NOP
			}

			@Override
			public void init(GLAutoDrawable drawable) {
				initGL(drawable.getGL().getGL2());
			}

			@Override
			public void reshape(GLAutoDrawable drawable, int x, int y,
					int width, int height) {
				setup(drawable.getGL().getGL2(), width, height);
			}
		});
	}

	private FPSAnimator createAnimator(GLCanvas canvas) {
		return new FPSAnimator(canvas, FPS, true);
	}

	private GLCanvas createCanvas(GLCapabilities capabilities) {
		return new GLCanvas(capabilities);
	}

	private GLCapabilities createCapabilities(GLProfile profile) {
		return new GLCapabilities(profile);
	}

	private GLProfile createProfile() {
		return GLProfile.getDefault();
	}

	@Override
	public int getActualFPS() {
		return (int) animator.getLastFPS();
	}

	public GLCanvas getCanvas() {
		return canvas;
	}

	private void initGL(GL2 gl2) {
		gl2.glEnable(GL2.GL_TEXTURE_2D);

		gl2.glEnable(GL2.GL_BLEND);
		gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		gl2.glDisable(GL2.GL_DEPTH_TEST);

		gl2.glClearColor(0, 0, 0, 0);

		try {
			renderer = Optional.of(rendererFactory.create(gl2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Subscribe
	public void onActorDisposed(ActorDisposedEvent event) {
		if (event.actorInstanceOf(Particle.class)) {
			Particle particle = event.getCastedActor();
			if (renderer.isPresent()) {
				renderer.get().onParticleDisposed(particle);
			}
		}
	}

	@Subscribe
	public void onActorSpawned(ActorSpawnedEvent event) {
		if (event.actorInstanceOf(Particle.class)) {
			Particle particle = event.getCastedActor();
			if (renderer.isPresent()) {
				renderer.get().onParticleSpawned(particle);
			}
		}
	}

	private void render(GL2 gl2, int width, int height) {
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();

		try {
			renderer.get().render(gl2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		gl2.glFlush();
	}

	private void setup(GL2 gl2, int width, int height) {
		gl2.glViewport(0, 0, width, height);
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(-0.5, 0.5, 0.5, -0.5, -1, 1);
	}

	@Override
	protected void shutDown() throws Exception {
		animator.stop();
	}

	@Override
	protected void startUp() throws Exception {
		animator.start();
	}

}
