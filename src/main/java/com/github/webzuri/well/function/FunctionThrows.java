package com.github.webzuri.well.function;

@FunctionalInterface
public interface FunctionThrows<T, R, E extends Throwable>
{
	public R applyThrows(T arg0) throws E;
}
