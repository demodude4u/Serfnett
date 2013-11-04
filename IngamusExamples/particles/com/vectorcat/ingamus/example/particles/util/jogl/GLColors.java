package com.vectorcat.ingamus.example.particles.util.jogl;

import java.awt.Color;

import javax.media.opengl.fixedfunc.GLPointerFunc;

import org.javatuples.Quartet;

public final class GLColors {

	public static void glColor(GLPointerFunc gl, Color color) {
		int rgb = color.getRGB();
		float b = (0xFF & rgb) / 255f;
		rgb >>= 8;
		float g = (0xFF & rgb) / 255f;
		rgb >>= 8;
		float r = (0xFF & rgb) / 255f;
		rgb >>= 8;
		float a = rgb / 255f;
		gl.glColor4f(r, g, b, a);
	}

	public static void glColor(GLPointerFunc gl, Color color, int alpha) {
		int rgb = color.getRGB();
		float b = (0xFF & rgb) / 255f;
		rgb >>= 8;
		float g = (0xFF & rgb) / 255f;
		rgb >>= 8;
		float r = (0xFF & rgb) / 255f;
		float a = (0xFF & alpha) / 255f;
		gl.glColor4f(r, g, b, a);
	}

	/**
	 * @return {r,g,b,a}
	 */
	public static Quartet<Float, Float, Float, Float> toFloat(Color color) {
		int rgb = color.getRGB();
		float b = (0xFF & rgb) / 255f;
		rgb >>= 8;
		float g = (0xFF & rgb) / 255f;
		rgb >>= 8;
		float r = (0xFF & rgb) / 255f;
		rgb >>= 8;
		float a = rgb / 255f;
		return new Quartet<>(r, g, b, a);
	}

	private GLColors() {
	}
}
