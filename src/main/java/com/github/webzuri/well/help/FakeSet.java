package com.github.webzuri.well.help;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections4.IteratorUtils;

final class FakeSet<E> implements Set<E>
{
	@Override
	public boolean add(E arg0)
	{
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0)
	{
		return false;
	}

	@Override
	public void clear()
	{
	}

	@Override
	public boolean contains(Object arg0)
	{
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0)
	{
		return false;
	}

	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public Iterator<E> iterator()
	{
		return IteratorUtils.emptyIterator();
	}

	@Override
	public boolean remove(Object arg0)
	{
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> arg0)
	{
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0)
	{
		return false;
	}

	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public Object[] toArray()
	{
		return new Object[0];
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] arg0)
	{
		return (T[]) new Object[0];
	}
}
