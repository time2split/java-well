package com.github.webzuri.well.memoization;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.webzuri.well.function.SupplierThrows;

public final class Memoizers
{
	public Memoizers()
	{
		throw new AssertionError();
	}

	public static <T, R> Function<T, R> asFunction(Function<T, R> toStore, Map<T, R> memoryMap)
	{
		return k -> memoryMap.computeIfAbsent(k, toStore);
	}

	public static <T, R> Function<T, R> asFunction(Function<T, R> toStore)
	{
		return asFunction(toStore, new HashMap<>());
	}

	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> SupplierThrows<T, E> lazyThrows(SupplierThrows<T, E> compute)
	{
		Object val[] = new Object[] { null };

		return () -> (T) (val[0] != null ? val[0] : (val[0] = compute.getThrows()));
	}

	@SuppressWarnings("unchecked")
	public static <T> Supplier<T> lazy(Supplier<T> compute)
	{
		Object val[] = new Object[] { null };

		return () -> (T) (val[0] != null ? val[0] : (val[0] = compute.get()));
	}

	@SuppressWarnings("unchecked")
	public static <T> Supplier<T> lazyReference(Supplier<Reference<T>> compute)
	{
		Reference<?> val[] = new Reference<?>[] { null };

		return () -> {
			var v = (Reference<T>) val[0];

			if (null == v || null == v.get())
				return (T) (val[0] = compute.get()).get();

			return v.get();
		};
	}
}
