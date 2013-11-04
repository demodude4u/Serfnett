package com.vectorcat.ingamus.example.particles;

import com.vectorcat.ingamus.spi.IngamusEvent;

public class UpdateParticlesEvent implements IngamusEvent {
	private final double simulationSeconds;
	private final double simulationSecondsElapsed;

	public UpdateParticlesEvent(double simulationSeconds,
			double simulationSecondsElapsed) {
		this.simulationSeconds = simulationSeconds;
		this.simulationSecondsElapsed = simulationSecondsElapsed;
	}

	public double getSimulationSeconds() {
		return simulationSeconds;
	}

	public double getSimulationSecondsElapsed() {
		return simulationSecondsElapsed;
	}
}
