package com.vectorcat.ingamus.example.particles;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.vectorcat.ingamus.ActorDisposedEvent;
import com.vectorcat.ingamus.ActorSpawnedEvent;
import com.vectorcat.ingamus.IngamusEventBus;
import com.vectorcat.ingamus.spi.AbstractIngamusFPSService;

public class ParticleRenderingService extends AbstractIngamusFPSService
		implements FPSMonitor {

	private static final int FPS = 60;
	private final Canvas canvas;

	private final ParticleRenderer renderer;

	@Inject
	ParticleRenderingService(IngamusEventBus eventBus,
			@Named("Display") Canvas canvas, ParticleRenderer renderer) {
		this.canvas = canvas;
		this.renderer = renderer;

		eventBus.register(this);
	}

	public Canvas getCanvas() {
		return canvas;
	}

	@Override
	protected double getDesiredFPS() {
		return FPS;
	}

	@Subscribe
	public void onActorDisposed(ActorDisposedEvent event) {
		if (event.actorInstanceOf(Particle.class)) {
			Particle particle = event.getCastedActor();
			renderer.onParticleDisposed(particle);
		}
	}

	@Subscribe
	public void onActorSpawned(ActorSpawnedEvent event) {
		if (event.actorInstanceOf(Particle.class)) {
			Particle particle = event.getCastedActor();
			renderer.onParticleSpawned(particle);
		}
	}

	@Override
	protected void runOneFrame() {
		if (!canvas.isValid() || !canvas.isVisible()) {
			return;
		}

		BufferStrategy strategy = canvas.getBufferStrategy();
		if (strategy == null || strategy.contentsLost()) {
			canvas.createBufferStrategy(3);
			strategy = canvas.getBufferStrategy();
			if (strategy == null) {
				return;// \_(O.o)_/
			}
		}

		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		// g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		// RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		// g.setRenderingHint(RenderingHints.KEY_RENDERING,
		// RenderingHints.VALUE_RENDER_SPEED);
		// g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
		// RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

		AffineTransform screenXform = g.getTransform();

		g.setColor(Color.black);
		// g.setColor(new Color(0, 0, 0, 8));
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		try {
			double scale = Math.min(canvas.getWidth(), canvas.getHeight());
			g.translate((canvas.getWidth() + scale) / 2,
					(canvas.getHeight() + scale) / 2);
			g.scale(scale, scale);
			g.translate(-0.5f, -0.5f);

			renderer.render(g);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			g.dispose();
		}

		g.setTransform(screenXform);

		strategy.show();
	}

	@Override
	protected void uncaughtException(RuntimeException e) {
		e.printStackTrace();
	}

}
