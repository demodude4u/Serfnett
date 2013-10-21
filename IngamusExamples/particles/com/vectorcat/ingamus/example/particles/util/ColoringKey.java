package com.vectorcat.ingamus.example.particles.util;

import java.awt.Color;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class ColoringKey {

	public static class Builder {
		TreeMap<Float, Color> map = Maps.newTreeMap();

		float lastKey = 0f;

		public ColoringKey build() {
			return new ColoringKey(map);
		}

		public Builder next(float relativeToLastKey, Color color) {
			return set(lastKey + relativeToLastKey, color);
		}

		public Builder set(float key, Color color) {
			map.put(key, color);

			lastKey = key;

			return this;
		}

		public Builder set(float[] keys, Color[] colors) {
			Preconditions.checkArgument(keys.length == colors.length,
					"Arrays do not match in length!");

			for (int i = 0; i < keys.length; i++) {
				set(keys[i], colors[i]);
			}

			return this;
		}
	}

	public static final Color NONE = new Color(0, 0, 0, 0);

	public static ColoringKey.Builder builder() {
		return new Builder();
	}

	private final TreeMap<Float, Color> map;

	private ColoringKey(TreeMap<Float, Color> map) {
		this.map = map;
	}

	public Color getColorAt(float key) {
		Entry<Float, Color> ceilingEntry = map.ceilingEntry(key);
		Entry<Float, Color> floorEntry = map.floorEntry(key);

		if (ceilingEntry == null && floorEntry == null) {
			return NONE;
		} else if (ceilingEntry == null) {
			return floorEntry.getValue();
		} else if (floorEntry == null) {
			return ceilingEntry.getValue();
		}

		float floorKey = floorEntry.getKey();
		float ceilingKey = ceilingEntry.getKey();
		float f = (key - floorKey) / (ceilingKey - floorKey);

		return interpolate(f, floorEntry.getValue(), ceilingEntry.getValue());
	}

	private Color interpolate(float f, Color c1, Color c2) {
		int rgb1 = c1.getRGB();
		int rgb2 = c2.getRGB();
		int b = interpolate(f, rgb1 & 0xFF, rgb2 & 0xFF);
		rgb1 >>= 8;
		rgb2 >>= 8;
		int g = interpolate(f, rgb1 & 0xFF, rgb2 & 0xFF);
		rgb1 >>= 8;
		rgb2 >>= 8;
		int r = interpolate(f, rgb1 & 0xFF, rgb2 & 0xFF);
		rgb1 >>= 8;
		rgb2 >>= 8;
		int a = interpolate(f, rgb1 & 0xFF, rgb2 & 0xFF);
		return new Color(r, g, b, a);
	}

	private int interpolate(float f, int i1, int i2) {
		int ret = (int) (f * (i2 - i1) + i1);
		if (ret > 0xFF) {
			ret = 0xFF;
		} else if (ret < 0) {
			ret = 0x00;
		}
		return ret;
	}
}
