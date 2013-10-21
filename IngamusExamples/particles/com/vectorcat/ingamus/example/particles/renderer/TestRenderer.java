package com.vectorcat.ingamus.example.particles.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.vectorcat.ingamus.example.particles.FPSMonitor;
import com.vectorcat.ingamus.example.particles.Particle;
import com.vectorcat.ingamus.example.particles.Particle.F;
import com.vectorcat.ingamus.example.particles.ParticleRenderer;
import com.vectorcat.ingamus.example.particles.util.ColoringKey;
import com.vectorcat.ingamus.example.particles.util.FastMath;
import com.vectorcat.ingamus.example.particles.util.ImageResource;
import com.vectorcat.ingamus.example.particles.util.ParticleSprite;

public class TestRenderer implements ParticleRenderer {

	private static final float speedColorInterval = 1f / 12f;

	private static final Font DebugFont = new Font("Courier New", Font.PLAIN,
			12).deriveFont(0.025f);

	private static final double TRAIL_RADIUS = 0.015;
	private static final double TRAIL_TAILRATIO = 112.0 / 20.0;

	private static ColoringKey coloringKeyByTime = ColoringKey.builder()
			//
			.set(0, Color.black).next(1, Color.cyan).next(2, Color.lightGray)
			//
			.set(4, new Color(128, 128, 80)).next(0.25f, Color.yellow)
			.next(0.75f, Color.black)
			//
			.build();

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

	private final ImageResource imageResource = new ImageResource("test",
			getClass());

	private final ParticleSprite trailSprite = new ParticleSprite(
			imageResource.getImage("trail.png"), new Rectangle2D.Double(
					-TRAIL_RADIUS, -TRAIL_RADIUS, TRAIL_RADIUS * 2,
					TRAIL_RADIUS * 2 * TRAIL_TAILRATIO));

	private final FPSMonitor rendererMonitor;
	private final FPSMonitor controllerMonitor;

	private final Set<Particle> particles = Sets.newLinkedHashSet();
	private final ConcurrentLinkedQueue<Runnable> batchJob = Queues
			.newConcurrentLinkedQueue();

	@Inject
	TestRenderer(@Named("ParticleRenderer") FPSMonitor rendererMonitor,
			@Named("ParticleController") FPSMonitor controllerMonitor) {
		this.rendererMonitor = rendererMonitor;
		this.controllerMonitor = controllerMonitor;
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

			{
				AffineTransform pat = g.getTransform();
				g.translate(f.x, f.y);
				g.rotate(FastMath.atan2((float) f.dy, (float) f.dx) + Math.PI
						/ 2.0);

				double delta = Math.sqrt(f.dx * f.dx + f.dy * f.dy);
				// dotSprite.render(g);
				double trailScale = TRAIL_TAILRATIO * delta / 5.0;
				g.scale(1, trailScale);
				trailSprite.render(g);

				g.setTransform(pat);
			}

			// Uncommment to draw acceleration lines
			// g.setStroke(new BasicStroke(0.001f));
			// g.draw(new Line2D.Double(f.x, f.y, f.x + f.ax / 30.0, f.y + f.ay
			// / 30.0));
		}

		g.setColor(Color.white);
		g.setFont(DebugFont);
		g.drawString("" + rendererMonitor.getActualFPS(), -0.5f, -0.5f
				+ DebugFont.getSize2D());
		g.drawString("" + controllerMonitor.getActualFPS(), -0.5f, -0.5f
				+ DebugFont.getSize2D() * 2);
		g.drawString("" + particles.size(), -0.5f,
				-0.5f + DebugFont.getSize2D() * 3);

	}
}
