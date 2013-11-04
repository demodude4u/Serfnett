package com.vectorcat.ingamus.example.particles.util.j2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.common.collect.Maps;

public class ResourceLoader {
	private static final BufferedImage MISSING_IMAGE = createMissingImage();

	private static BufferedImage createMissingImage() {
		BufferedImage ret = new BufferedImage(512, 512,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = ret.createGraphics();
		g.setColor(Color.magenta);
		g.fillRect(0, 0, ret.getWidth(), ret.getHeight());
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(20));
		g.drawLine(0, 0, ret.getWidth(), ret.getHeight());
		g.drawLine(0, ret.getHeight(), ret.getWidth(), 0);
		g.dispose();
		return ret;
	}

	private final ClassLoader classLoader;
	private final String rootPath;

	private final Map<String, BufferedImage> mapLoaded = Maps.newHashMap();

	public ResourceLoader(String packageRoot, Class<?> relativeToClass) {
		classLoader = relativeToClass.getClassLoader();
		String classPackagePath = createClassPackagePath(relativeToClass);
		rootPath = classPackagePath + "/" + packageRoot;
	}

	private String createClassPackagePath(Class<?> clazz) {
		String packageName = clazz.getPackage().getName();
		String packagePath = packageName.replace('.', '/');
		return packagePath;
	}

	public BufferedImage getImage(String imagePath) {
		BufferedImage ret = mapLoaded.get(imagePath);
		if (ret == null) {
			String path = rootPath + "/" + imagePath;
			try {
				InputStream resourceAsStream = classLoader
						.getResourceAsStream(path);
				ret = ImageIO.read(resourceAsStream);
			} catch (Exception e) {
				System.err.println("Error reading image resource: " + path);
				e.printStackTrace();
				ret = MISSING_IMAGE;
			}
			mapLoaded.put(imagePath, ret);
		}
		return ret;
	}

	public String getText(String textPath) {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer, true);

		String path = rootPath + "/" + textPath;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					classLoader.getResourceAsStream(path)));
			{
				String line;
				while ((line = reader.readLine()) != null) {
					printWriter.println(line);
				}
			}
		} catch (Exception e) {
			System.err.println("Error reading text resource: " + path);
			e.printStackTrace();
			return null;
		}

		return writer.toString();
	}
}
