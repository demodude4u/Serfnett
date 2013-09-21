package com.vectorcat.venire.api;

import java.io.InputStream;
import java.io.OutputStream;

public interface StreamPipe {

	public abstract InputStream getLeftInputStream();

	public abstract OutputStream getLeftOutputStream();

	public abstract InputStream getRightInputStream();

	public abstract OutputStream getRightOutputStream();

}
