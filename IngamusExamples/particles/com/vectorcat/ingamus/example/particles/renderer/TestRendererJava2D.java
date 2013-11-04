package com.vectorcat.ingamus.example.particles.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.vectorcat.ingamus.example.particles.FPSMonitor;
import com.vectorcat.ingamus.example.particles.Particle;
import com.vectorcat.ingamus.example.particles.Particle.F;
import com.vectorcat.ingamus.example.particles.ParticleRendererJava2D;
import com.vectorcat.ingamus.example.particles.ParticleSamplerService;
import com.vectorcat.ingamus.example.particles.util.ColoringKey;
import com.vectorcat.ingamus.example.particles.util.FastMath;
import com.vectorcat.ingamus.example.particles.util.j2d.ResourceLoader;
import com.vectorcat.ingamus.example.particles.util.j2d.ParticleSprite;

public class TestRendererJava2D implements ParticleRendererJava2D {

	private static final float speedColorInterval = 1f / 12f;

	private static final Font DebugFont = new Font("Courier New", Font.PLAIN,
			12).deriveFont(0.025f);

	private static final double TRAIL_WIDTH = 0.015;

	private static ColoringKey coloringKeyBySpeed = ColoringKey.builder()
	//
			.set(0, Color.gray)
			//
			.next(speedColorInterval, Color.red)
			//
			.next(speedColorInterval, Color.orange)
			//
			.next(speedColorInterval, Color.yellow)
			//
			.next(speedColorInterval, Color.green)
			//
			.next(speedColorInterval, Color.blue)
			//
			.next(speedColorInterval, Color.magenta)
			//
			.build();

	private final ResourceLoader imageResource = new ResourceLoader("",
			getClass());

	private final ParticleSprite trailSprite = new ParticleSprite(
			imageResource.getImage("trail2.png"), new Rectangle2D.Double(
					-TRAIL_WIDTH / 2.0, 0, TRAIL_WIDTH, 1));

	private final FPSMonitor rendererMonitor;
	private final FPSMonitor controllerMonitor;
	private final ParticleSamplerService samplerService;

	private final Set<Particle> particles = Sets.newLinkedHashSet();
	private final ConcurrentLinkedQueue<Runnable> batchJob = Queues
			.newConcurrentLinkedQueue();

	@Inject
	TestRendererJava2D(
			@Named("ParticleRendererJava2D") FPSMonitor rendererMonitor,
			@Named("ParticleController") FPSMonitor controllerMonitor,
			ParticleSamplerService samplerService) {
		this.rendererMonitor = rendererMonitor;
		this.controllerMonitor = controllerMonitor;
		this.samplerService = samplerService;
	}

	@Override
	public void onParticleDisposed(final Particle particle) {
		batchJob.offer(new Runnable() {
			@Override
			public void run() {
				particles.remove(particle);
			}
		});
	}

	@Override
	public void onParticleSpawned(final Particle particle) {
		batchJob.offer(new Runnable() {
			@Override
			public void run() {
				particles.add(particle);
			}
		});
	}

	@Override
	public void render(Graphics2D g) throws Exception {
		while (!batchJob.isEmpty()) {
			batchJob.poll().run();
		}

		Particle.F f = new F();

		for (Particle particle : particles) {
			f.get(particle);

			// Choose a coloring of particles
			// g.setColor(coloringKeyByTime.getColorAt((float) f.lifeSeconds));
			g.setColor(coloringKeyBySpeed.getColorAt((float) Math.sqrt(f.dx
					* f.dx + f.dy * f.dy)));
			// g.setColor(coloringKeyBySpeed.getColorAt((float) Math.sqrt(f.ax
			// * f.ax + f.ay * f.ay)));

			// Uncommment to draw acceleration lines
			// g.setStroke(new BasicStroke(0.001f));
			// g.draw(new Line2D.Double(f.x, f.y, f.x + f.ax / 30.0, f.y + f.ay
			// / 30.0));

			g.setStroke(new BasicStroke(0.001f));

			Particle.F prevSample = f;
			Collection<F> samples = samplerService.getSamplesFor(particle);
			int sampleIndex = 0;
			int sampleSize = samples.size();
			for (Particle.F sample : samples) {
				float sdx = (float) (sample.x - prevSample.x);
				float sdy = (float) (sample.y - prevSample.y);

				float dist = (float) Math.sqrt(sdx * sdx + sdy * sdy);
				float speed = (float) Math.sqrt(prevSample.dx * prevSample.dx
						+ prevSample.dy * prevSample.dy);

				Color color = coloringKeyBySpeed.getColorAt(speed);
				g.setColor(color);

				// g.draw(new Line2D.Double(sample.x, sample.y, prevSample.x,
				// prevSample.y));

				AffineTransform pat = g.getTransform();

				g.translate(prevSample.x, prevSample.y);

				g.rotate(FastMath.atan2(sdy, sdx) - Math.PI / 2.0);

				g.scale(speed / 0.25 + 1, dist);

				// dotSprite.render(g);
				// if (sampleIndex % 3 == 0)
				trailSprite.render(g, 1, sampleSize, 0, sampleIndex);

				g.setTransform(pat);

				prevSample = sample;
				sampleIndex++;
			}
		}

		g.setColor(Color.white);
		g.setFont(DebugFont);
		g.drawString("Render: " + rendererMonitor.getActualFPS() + " FPS",
				-0.5f, -0.5f + DebugFont.getSize2D());
		g.drawString(
				"Controller: " + controllerMonitor.getActualFPS() + " FPS",
				-0.5f, -0.5f + DebugFont.getSize2D() * 2);
		g.drawString("Particles: " + particles.size(), -0.5f,
				-0.5f + DebugFont.getSize2D() * 3);

	}
}
