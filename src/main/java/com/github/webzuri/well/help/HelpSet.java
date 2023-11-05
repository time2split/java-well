package com.github.webzuri.well.help;

import java.util.Set;

public final class HelpSet
{
	private HelpSet()
	{
		throw new AssertionError();
	}

	private static enum FakeSetE
	{
		INSTANCE;

		FakeSet<?> i = new FakeSet<>();
	}

	@SuppressWarnings("unchecked")
	public static <E> Set<E> fakeSet()
	{
		return (Set<E>) FakeSetE.INSTANCE.i;
	}
}
