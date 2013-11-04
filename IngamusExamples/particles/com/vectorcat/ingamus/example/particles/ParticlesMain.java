package com.vectorcat.ingamus.example.particles;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.vectorcat.ingamus.IngamusActorService;
import com.vectorcat.ingamus.IngamusActorService.BatchJob;
import com.vectorcat.ingamus.IngamusEngine;
import com.vectorcat.ingamus.IngamusEngine.Builder;
import com.vectorcat.ingamus.example.particles.controller.TestController;
import com.vectorcat.ingamus.example.particles.renderer.TestRendererJOGL;
import com.vectorcat.ingamus.example.particles.renderer.TestRendererJava2D;

public class ParticlesMain {

	private static Module createModule() {
		return new AbstractModule() {
			private <T> LinkedBindingBuilder<T> bindNamed(Class<T> clazz,
					String named) {
				return bind(clazz).annotatedWith(Names.named(named));
			}

			@Override
			protected void configure() {
				install(new FactoryModuleBuilder()
						.build(ParticleActor.Factory.class));

				bindNamed(Canvas.class, "Display").toInstance(new Canvas());
				bindNamed(FPSMonitor.class, "ParticleController").to(
						ParticleControllerService.class);
				bindNamed(FPSMonitor.class, "ParticleRendererJava2D").to(
						ParticleRenderingServiceJava2D.class);
				bindNamed(FPSMonitor.class, "ParticleRendererJOGL").to(
						ParticleRenderingServiceJOGL.class);

				bind(ParticleController.class).to(TestController.class);
				bind(ParticleRendererJava2D.class).to(TestRendererJava2D.class);

				install(new FactoryModuleBuilder().implement(
						ParticleRendererJOGL.class, TestRendererJOGL.class)
						.build(ParticleRendererJOGL.Factory.class));
			}

			@Provides
			@Singleton
			@Named("ParticleSpawner")
			protected BatchJob provideBatchJob(IngamusActorService actorService) {
				return actorService.new BatchJob();
			}
		};
	}

	public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "true");

		Builder builder = IngamusEngine.builder();
		builder.addModule(createModule());
		IngamusEngine engine = builder.build("Test1");

		engine.injectService(ParticleControllerService.class);

		// Uncomment to show service network
		// ServiceNetworkTool serviceNetworkTool = new
		// ServiceNetworkTool(engine);
		// serviceNetworkTool.show();

		// Uncomment to show the Java2D version of rendering
		// ParticleRenderingServiceJava2D renderingServiceJava2D = engine
		// .injectService(ParticleRenderingServiceJava2D.class);
		// viewComponent(renderingServiceJava2D.getCanvas(), 0, 512, 512);

		ParticleRenderingServiceJOGL renderingServiceJOGL = engine
				.injectService(ParticleRenderingServiceJOGL.class);
		viewComponent(renderingServiceJOGL.getCanvas(), 550, 512, 512);
	}

	private static void viewComponent(Component component, int x, int width,
			int height) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.getContentPane().add(component);
		frame.getContentPane().setPreferredSize(new Dimension(width, height));
		frame.pack();
		frame.setVisible(true);
		frame.setLocation(x, 0);
	}
}
