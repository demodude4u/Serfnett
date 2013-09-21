package com.vectorcat.venire;

import java.io.InputStream;
import java.io.OutputStream;

import com.vectorcat.venire.api.StreamPipe;
import com.vectorcat.venire.internal.CircularByteBuffer;

public class SimpleStreamPipe implements StreamPipe {

	private final CircularByteBuffer buffer1 = new CircularByteBuffer(10000000);
	private final CircularByteBuffer buffer2 = new CircularByteBuffer(10000000);

	@Override
	public InputStream getLeftInputStream() {
		return buffer1.getInputStream();
	}

	@Override
	public OutputStream getLeftOutputStream() {
		return buffer2.getOutputStream();
	}

	@Override
	public InputStream getRightInputStream() {
		return buffer2.getInputStream();
	}

	@Override
	public OutputStream getRightOutputStream() {
		return buffer1.getOutputStream();
	}

}
