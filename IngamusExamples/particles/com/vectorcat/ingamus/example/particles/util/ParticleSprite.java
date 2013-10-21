package com.vectorcat.ingamus.example.particles.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

public class ParticleSprite {

	private final ConcurrentMap<Integer, VolatileImage> mapColorized = Maps
			.newConcurrentMap();

	private final BufferedImage original;
	private final int width;
	private final int height;

	private final AffineTransform xFormToAnchor;

	public ParticleSprite(BufferedImage original, Rectangle2D anchor) {
		this.original = original;
		width = original.getWidth();
		height = original.getHeight();

		xFormToAnchor = createXformToAnchor(width, height, anchor);
	}

	private AffineTransform createXformToAnchor(int width, int height,
			Rectangle2D anchor) {
		AffineTransform ret = new AffineTransform();
		ret.translate(anchor.getX(), anchor.getY());
		ret.scale(anchor.getWidth() / width, anchor.getHeight() / height);
		return ret;
	}

	private VolatileImage generateColorized(Color color) {
		GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();

		VolatileImage ret = graphicsConfiguration
				.createCompatibleVolatileImage(width, height,
						Transparency.TRANSLUCENT);

		Graphics2D g = ret.createGraphics();

		g.setColor(color);
		g.fillRect(0, 0, width, height);

		g.setComposite(AlphaComposite.DstIn);
		g.drawImage(original, 0, 0, null);

		g.dispose();

		return ret;
	}

	private GraphicsConfiguration getGraphicsConfiguration() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration();
	}

	public void render(Graphics2D g) {
		Color color = g.getColor();
		int rgb = color.getRGB();
		rgb &= 0xF0F0F0F0;
		rgb |= rgb >> 4;// 16-bit, copied hi to lo
		VolatileImage colorized = mapColorized.get(rgb);
		if (colorized == null || colorized.contentsLost()) {
			mapColorized.put(rgb, colorized = generateColorized(color));
		}

		g.drawImage(colorized, xFormToAnchor, null);
	}

}
