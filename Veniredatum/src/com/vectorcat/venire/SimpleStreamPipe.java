package com.vectorcat.venire;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Copied from Zephr and Friends.
 */
public class SimpleStreamPipe {

	private class HalfPipe {
		private class Chunk {
			Chunk next;

			byte[] array = new byte[chunkSize];
			int ID;

			private Chunk() {
				initialize();
			}

			private void initialize() {
				next = null;
				ID = nextID++;
			}
		}

		private class ReadingInputStream extends InputStream {

			private volatile Chunk pc = null;
			private Chunk c;
			private int ptr = 0;

			private ReadingInputStream(Chunk c) {
				this.c = c;
			}

			@Override
			public int read() throws IOException {
				while ((writer.c == c) && (writer.ptr == ptr)) {
					try {
						synchronized (readlock) {
							readlock.wait(100);
						}
					} catch (InterruptedException e) {
						throw new IOException(e);
					}
				}
				int ret = c.array[ptr++] & 0xFF;
				if (ptr >= chunkSize) {
					while (c.next == null) {
						try {
							synchronized (readlock) {
								readlock.wait(100);
							}
						} catch (InterruptedException e) {
							throw new IOException(e);
						}
					}

					Chunk tmp = c;
					c = c.next;
					pc = tmp;
					ptr = 0;

					synchronized (writelock) {
						writelock.notify();
					}
				}
				return ret;
			}
		}

		private class WritingOutputStream extends OutputStream {

			private Chunk c;
			private volatile int ptr = 0;

			private WritingOutputStream(Chunk c) {
				this.c = c;
			}

			@Override
			public void flush() throws IOException {
				synchronized (readlock) {
					readlock.notifyAll();
				}
			}

			@Override
			public void write(int b) throws IOException {
				if (ptr >= c.array.length) {
					while ((c.ID - reader.c.ID + 1) > maxChunks) {
						try {
							synchronized (writelock) {
								writelock.wait();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					ptr = 0;

					Chunk nextc;
					if (reader.pc != null && c != reader.pc) {
						nextc = reader.pc;
						nextc.initialize();
					} else {
						nextc = new Chunk();
					}
					c.next = nextc;
					c = nextc;

					synchronized (readlock) {
						readlock.notifyAll();
					}
				}
				c.array[ptr] = (byte) b;
				ptr++;
			}
		}

		private int nextID = 0;

		private final int chunkSize;
		private final int maxChunks;

		private final WritingOutputStream writer;

		private final ReadingInputStream reader;

		private final Object readlock = new Object();
		private final Object writelock = new Object();

		public HalfPipe(int chunkSize, int maxSize) {
			this.chunkSize = chunkSize;
			this.maxChunks = (int) Math.ceil((float) maxSize
					/ (float) chunkSize);
			Chunk firstChunk = new Chunk();
			writer = new WritingOutputStream(firstChunk);
			reader = new ReadingInputStream(firstChunk);
		}

		public InputStream getInputStream() {
			return reader;
		}

		public OutputStream getOutputStream() {
			return writer;
		}

	}

	private final HalfPipe half1;
	private final HalfPipe half2;

	private final InputStream leftInputStream;
	private final OutputStream leftOutputStream;
	private final InputStream rightInputStream;
	private final OutputStream rightOutputStream;

	public SimpleStreamPipe() {
		this(200000, 10000000);// Taken from experience in Bonzai
	}

	public SimpleStreamPipe(int chunkSize, int maxSize) {
		half1 = new HalfPipe(chunkSize, maxSize);
		half2 = new HalfPipe(chunkSize, maxSize);

		leftInputStream = half2.getInputStream();
		leftOutputStream = half1.getOutputStream();
		rightInputStream = half1.getInputStream();
		rightOutputStream = half2.getOutputStream();
	}

	public InputStream getLeftInputStream() {
		return leftInputStream;
	}

	public OutputStream getLeftOutputStream() {
		return leftOutputStream;
	}

	public InputStream getRightInputStream() {
		return rightInputStream;
	}

	public OutputStream getRightOutputStream() {
		return rightOutputStream;
	}

}
