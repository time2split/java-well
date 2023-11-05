package com.github.webzuri.well.function;

@FunctionalInterface
public interface SupplierThrows<R, E extends Throwable>
{
	public R getThrows() throws E;
}
