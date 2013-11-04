package com.vectorcat.ingamus.example.particles;

import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ParticleSampler {

	private final Particle particle;
	private final double sampleRateSeconds;
	private final int sampleCount;

	private final Deque<Particle.F> samples = new ConcurrentLinkedDeque<>();
	private double lastSampleTimeStamp = 0;

	public ParticleSampler(Particle particle, double sampleRateSeconds,
			int sampleCount) {
		this.particle = particle;
		this.sampleRateSeconds = sampleRateSeconds;
		this.sampleCount = sampleCount;
	}

	public Collection<Particle.F> getSamples() {
		return samples;
	}

	public void sample(double secondsElapsed) {
		if (secondsElapsed - lastSampleTimeStamp < sampleRateSeconds) {
			return;
		}
		lastSampleTimeStamp = secondsElapsed;

		if (samples.size() == sampleCount) {
			samples.removeLast();
		}

		samples.addFirst(new Particle.F(particle));
	}
}
