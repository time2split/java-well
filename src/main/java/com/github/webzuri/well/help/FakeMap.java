package com.github.webzuri.well.help;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

final class FakeMap<K, V> implements Map<K, V>
{
	@Override
	public void clear()
	{
	}

	@Override
	public boolean containsKey(Object arg0)
	{
		return false;
	}

	@Override
	public boolean containsValue(Object arg0)
	{
		return false;
	}

	@Override
	public Set<Entry<K, V>> entrySet()
	{
		return Collections.emptySet();
	}

	@Override
	public V get(Object arg0)
	{
		return null;
	}

	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public Set<K> keySet()
	{
		return Collections.emptySet();
	}

	@Override
	public V put(K arg0, V arg1)
	{
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0)
	{
	}

	@Override
	public V remove(Object arg0)
	{
		return null;
	}

	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public Collection<V> values()
	{
		return Collections.emptySet();
	}
}
