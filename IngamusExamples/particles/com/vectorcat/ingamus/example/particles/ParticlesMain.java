package com.vectorcat.ingamus.example.particles;

import java.awt.Canvas;
import java.awt.Dimension;

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
import com.vectorcat.ingamus.example.particles.renderer.TestRenderer;

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
				bindNamed(FPSMonitor.class, "ParticleRenderer").to(
						ParticleRenderingService.class);

				bind(ParticleController.class).to(TestController.class);
				bind(ParticleRenderer.class).to(TestRenderer.class);
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

		ParticleRenderingService renderingService = engine
				.injectService(ParticleRenderingService.class);

		viewCanvas(renderingService.getCanvas(), 512, 512);
	}

	private static void viewCanvas(Canvas canvas, int width, int height) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(canvas);
		frame.getContentPane().setPreferredSize(new Dimension(width, height));
		frame.pack();
		frame.setVisible(true);
	}

}
