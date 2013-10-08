package com.vectorcat.venire.spi;

import java.io.InputStream;
import java.io.OutputStream;

public interface StreamPipe {

	public interface Connector {
		public InputStream getInputStream();

		public OutputStream getOutputStream();
	}

	public Connector getLeft();

	public InputStream getLeftInputStream();

	public OutputStream getLeftOutputStream();

	public Connector getRight();

	public InputStream getRightInputStream();

	public OutputStream getRightOutputStream();

}
