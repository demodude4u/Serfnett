package com.vectorcat.ingamus.spi;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.AbstractExecutionThreadService;

public abstract class AbstractIngamusFPSService extends
		AbstractExecutionThreadService implements IngamusService {

	private static class FrameDelayed implements Delayed {
		int frameID;
		private final long endTimeStamp;

		public FrameDelayed(int frameID) {
			this.frameID = frameID;
			endTimeStamp = System.currentTimeMillis() + 1000;
		}

		@Override
		public int compareTo(Delayed o) {
			return Integer.compare(frameID, ((FrameDelayed) o).frameID);
		}

		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(endTimeStamp - System.currentTimeMillis(),
					TimeUnit.MILLISECONDS);
		}
	}

	private int frameCounter = 0;

	private final DelayQueue<Delayed> secondFrames = new DelayQueue<>();

	public int getActualFPS() {
		return secondFrames.size();
	}

	protected abstract double getDesiredFPS();

	public int getFramesExecuted() {
		return frameCounter;
	}

	@Override
	protected void run() throws Exception {
		while (isRunning()) {
			long startFrameTimeStamp = System.currentTimeMillis();

			try {
				runOneFrame();
			} catch (RuntimeException e) {
				try {
					uncaughtException(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			long endFrameTimeStamp = System.currentTimeMillis();

			frameCounter++;
			secondFrames.add(new FrameDelayed(frameCounter));
			while (secondFrames.poll() != null)
				;

			double desiredFPS = getDesiredFPS();

			double preciseMillis = Math.max(0, (1000.0 / desiredFPS)
					- (endFrameTimeStamp - startFrameTimeStamp));

			long millis = (long) (preciseMillis);
			int nanos = (int) ((preciseMillis - millis) * 1000000);
			Thread.sleep(millis, nanos);
		}
	}

	protected abstract void runOneFrame();

	protected abstract void uncaughtException(RuntimeException e);

}
