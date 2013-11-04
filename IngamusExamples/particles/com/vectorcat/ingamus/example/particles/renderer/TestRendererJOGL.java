package com.vectorcat.ingamus.example.particles.renderer;

import java.awt.Color;
import java.awt.Font;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.media.opengl.GL2;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.vectorcat.ingamus.example.particles.FPSMonitor;
import com.vectorcat.ingamus.example.particles.Particle;
import com.vectorcat.ingamus.example.particles.Particle.F;
import com.vectorcat.ingamus.example.particles.ParticleRendererJOGL;
import com.vectorcat.ingamus.example.particles.ParticleSamplerService;
import com.vectorcat.ingamus.example.particles.util.ColoringKey;
import com.vectorcat.ingamus.example.particles.util.FastMath;
import com.vectorcat.ingamus.example.particles.util.j2d.ResourceLoader;
import com.vectorcat.ingamus.example.particles.util.jogl.GLColors;

public class TestRendererJOGL implements ParticleRendererJOGL {

	private static final Font DebugFont = new Font("Courier New", Font.PLAIN,
			12);

	private static final double TRAIL_RADIUS = 0.005;

	private static final float speedColorInterval = 1f / 12f;
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

	private final ResourceLoader shaderResource = new ResourceLoader("jogl",
			getClass());

	private final FPSMonitor rendererMonitor;
	private final FPSMonitor controllerMonitor;
	private final ParticleSamplerService samplerService;

	private final Set<Particle> particles = Sets.newLinkedHashSet();
	private final ConcurrentLinkedQueue<Runnable> batchJob = Queues
			.newConcurrentLinkedQueue();

	private final TextRenderer debugTextRenderer;

	private final int shaderProgram;

	@Inject
	TestRendererJOGL(@Assisted GL2 gl2,
			@Named("ParticleRendererJOGL") FPSMonitor rendererMonitor,
			@Named("ParticleController") FPSMonitor controllerMonitor,
			ParticleSamplerService samplerService) {
		this.rendererMonitor = rendererMonitor;
		this.controllerMonitor = controllerMonitor;
		this.samplerService = samplerService;

		debugTextRenderer = new TextRenderer(DebugFont);

		shaderProgram = createShaderProgram(gl2,
				shaderResource.getText("test.vert"),
				shaderResource.getText("test.frag"));
	}

	private int createShaderProgram(GL2 gl2, String vertexShaderCode,
			String fragmentShaderCode) {
		int vertexShaderID = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);
		gl2.glShaderSource(vertexShaderID, 1,
				new String[] { vertexShaderCode }, (int[]) null, 0);
		gl2.glCompileShader(vertexShaderID);

		int fragmentShaderID = gl2.glCreateShader(GL2.GL_FRAGMENT_SHADER);
		gl2.glShaderSource(fragmentShaderID, 1,
				new String[] { fragmentShaderCode }, (int[]) null, 0);
		gl2.glCompileShader(fragmentShaderID);

		int shaderProgramID = gl2.glCreateProgram();
		gl2.glAttachShader(shaderProgramID, vertexShaderID);
		gl2.glAttachShader(shaderProgramID, fragmentShaderID);
		gl2.glLinkProgram(shaderProgramID);
		gl2.glValidateProgram(shaderProgramID);

		return shaderProgramID;
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
	public void render(GL2 gl2) throws Exception {
		while (!batchJob.isEmpty()) {
			batchJob.poll().run();
		}

		Particle.F f = new F();

		for (Particle particle : particles) {
			f.get(particle);

			gl2.glBegin(GL2.GL_TRIANGLE_STRIP);

			{
				float speed = (float) Math.sqrt(f.dx * f.dx + f.dy * f.dy);
				Color color = coloringKeyBySpeed.getColorAt(speed);
				GLColors.glColor(gl2, color);
				gl2.glVertex2d(f.x, f.y);
			}

			Collection<F> samples = samplerService.getSamplesFor(particle);
			int sampleIndex = 0;
			int sampleSize = samples.size();
			for (Particle.F sample : samples) {
				{
					float speed = (float) Math.sqrt(sample.dx * sample.dx
							+ sample.dy * sample.dy);
					Color color = coloringKeyBySpeed.getColorAt(speed);
					int alpha = 255 * (sampleSize - sampleIndex) / sampleSize;
					GLColors.glColor(gl2, color, alpha);
				}

				float dir = FastMath
						.atan2((float) sample.dy, (float) sample.dx)
						+ (float) Math.PI / 2f;
				float radius = (float) TRAIL_RADIUS
						* (sampleSize - sampleIndex) / sampleSize;
				gl2.glVertex2d(sample.x + radius * Math.cos(dir), sample.y
						+ radius * Math.sin(dir));
				gl2.glVertex2d(sample.x - radius * Math.cos(dir), sample.y
						- radius * Math.sin(dir));

				sampleIndex++;
			}

			gl2.glEnd();
		}

		debugTextRenderer.beginRendering(512, 512, true);
		debugTextRenderer.setColor(Color.white);
		debugTextRenderer.draw("Render: " + rendererMonitor.getActualFPS()
				+ " FPS", 10, DebugFont.getSize());
		debugTextRenderer.draw(
				"Controller: " + controllerMonitor.getActualFPS() + " FPS", 10,
				DebugFont.getSize() * 2);
		debugTextRenderer.draw("Particles: " + particles.size(), 10,
				DebugFont.getSize() * 3);
		debugTextRenderer.endRendering();
	}
}
