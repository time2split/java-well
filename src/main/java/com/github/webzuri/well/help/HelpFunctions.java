package com.github.webzuri.well.help;

import java.util.function.Function;

import com.github.webzuri.well.function.FunctionThrows;

public final class HelpFunctions
{
	private HelpFunctions()
	{
		throw new AssertionError();
	}
	// ==========================================================================

	@SuppressWarnings("unchecked")
	public static <T, R, E extends Throwable, RE extends Throwable> FunctionThrows<T, R, RE> rethrow(FunctionThrows<T, R, E> fun, Function<E, RE> factory)
	{
		return arg -> {
			try
			{
				return fun.applyThrows(arg);
			}
			catch (Throwable e)
			{
				throw factory.apply((E) e);
			}
		};
	}

	// ==========================================================================

	public static <T, R, E extends Throwable> Function<T, R> unchecked(FunctionThrows<T, R, E> fun, Function<E, ? extends RuntimeException> factory)
	{
		return rethrow(fun, factory)::applyThrows;
	}

	public static <T, R> Function<T, R> unchecked(FunctionThrows<T, R, ?> fun)
	{
		return rethrow(fun, RuntimeException::new)::applyThrows;
	}

	// ==========================================================================

	public static <T, R> Function<T, R> avoidException(FunctionThrows<T, R, ?> f)
	{
		return avoidException(f, null);
	}

	public static <T, R> Function<T, R> avoidException(FunctionThrows<T, R, ?> f, R value)
	{
		return (arg) -> {
			try
			{
				return f.applyThrows(arg);
			}
			catch (Throwable e)
			{
				return value;
			}
		};
	}
}
