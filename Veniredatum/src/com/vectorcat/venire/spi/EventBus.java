package com.vectorcat.venire.spi;

public interface EventBus {

	public abstract void unregister(Object listener);

	public abstract void register(Object listener);

	public abstract void post(Object event) throws InterruptedException;

}
