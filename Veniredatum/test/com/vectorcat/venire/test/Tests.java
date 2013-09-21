package com.vectorcat.venire.test;

import static org.junit.Assert.*;

import java.util.concurrent.Callable;

public final class Tests {
	public static void assertTrueEventually(Callable<Boolean> assertTrue,
			int timeout, Callable<String> failMessage) {
		long startStamp = System.currentTimeMillis();
		try {
			while (!assertTrue.call()
					&& System.currentTimeMillis() - startStamp < timeout) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					throw new Error(e);
				}
			}
		} catch (Exception e) {
			throw new Error(e);// Oops...
		}

		if (System.currentTimeMillis() - startStamp > timeout) {
			try {
				fail("Timed out before eventual assertion! "
						+ failMessage.call());
			} catch (Exception e) {
				throw new Error(e);
			}
		}
	}

	private Tests() {
	}
}
