package com.vectorcat.ingamus.example.particles;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.vectorcat.ingamus.IngamusActorService;
import com.vectorcat.ingamus.IngamusActorService.BatchJob;
import com.vectorcat.ingamus.spi.IngamusActor;

public class ParticleActor implements Particle, IngamusActor {

	public static interface Factory {
		public ParticleActor createParticle(@Assisted("x") double x,
				@Assisted("y") double y, @Assisted("dx") double dx,
				@Assisted("dy") double dy, @Assisted("ax") double ax,
				@Assisted("ay") double ay);
	}

	private final BatchJob batchJob;

	private double lifeSeconds;
	private double x, y;
	private double dx, dy;
	private double ax, ay;

	@Inject
	private ParticleActor(
			@Named("ParticleSpawner") IngamusActorService.BatchJob batchJob,
			@Assisted("x") double x, @Assisted("y") double y,
			@Assisted("dx") double dx, @Assisted("dy") double dy,
			@Assisted("ax") double ax, @Assisted("ay") double ay) {
		this.batchJob = batchJob;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.ax = ax;
		this.ay = ay;

		lifeSeconds = 0;
	}

	@Override
	public void dispose() {
		batchJob.disposeActor(this);
	}

	@Override
	public double getAx() {
		return ax;
	}

	@Override
	public double getAy() {
		return ay;
	}

	@Override
	public double getDx() {
		return dx;
	}

	@Override
	public double getDy() {
		return dy;
	}

	@Override
	public double getLifeSeconds() {
		return lifeSeconds;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void onDispose() {
		// NOP
	}

	@Override
	public void onSpawn() {
		// NOP
	}

	@Override
	public void setAcceleration(double ax, double ay) {
		this.ax = ax;
		this.ay = ay;
	}

	@Override
	public void setAx(double ax) {
		this.ax = ax;
	}

	@Override
	public void setAy(double ay) {
		this.ay = ay;
	}

	@Override
	public void setDx(double dx) {
		this.dx = dx;
	}

	@Override
	public void setDy(double dy) {
		this.dy = dy;
	}

	@Override
	public void setLifeSeconds(double lifeSeconds) {
		this.lifeSeconds = lifeSeconds;
	}

	@Override
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void setVelocity(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

}
