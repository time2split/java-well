package com.github.webzuri.well.help;

import java.util.HashMap;
import java.util.Map;

public final class HelpMap
{
	private HelpMap()
	{
		throw new AssertionError();
	}

	public static <K, V> Map<K, V> getSome(Map<? extends K, ? extends V> map, Iterable<? extends K> keys)
	{
		var ret = new HashMap<K, V>(map.size());

		for (var k : keys)
		{
			var v = map.get(k);

			if (null == v)
				continue;

			ret.put(k, v);
		}
		return ret;
	}

	private static enum FakeMapE
	{
		INSTANCE;

		FakeMap<?, ?> i = new FakeMap<>();
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> fakeMap()
	{
		return (Map<K, V>) FakeMapE.INSTANCE.i;
	}
}
