package com.github.webzuri.well.numeric;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

public final class MultiInterval implements Iterable<Long>
{
	private LinkedList<Interval> intervals;

	private MultiInterval()
	{
		intervals = new LinkedList<>();
	}

	public static MultiInterval of(Interval... intervals)
	{
		var ret = new MultiInterval();
		ret.intervals = new LinkedList<>(List.of(intervals));
		return ret;
	}

	private static enum Factory
	{
		NULL;

		private MultiInterval mi = new MultiInterval();
	}

	public static MultiInterval nullValue()
	{
		return Factory.NULL.mi;
	}

	public static boolean isNullValue(MultiInterval i)
	{
		return Factory.NULL.mi == i;
	}

	public boolean isNull()
	{
		return Factory.NULL.mi == this;
	}

	public static MultiInterval empty()
	{
		return new MultiInterval();
	}

	public List<Interval> getIntervals()
	{
		return Collections.unmodifiableList(intervals);
	}

	// ==========================================================================

	private void mergeRight(Interval i, ListIterator<Interval> it)
	{
		while (it.hasNext())
		{
			var left = it.next();

			if (i.max + 1 == left.min)
			{
				it.remove();
				i.max = left.max;
			}
			else
				return;
		}
	}

	private void mergeLeft(Interval i, ListIterator<Interval> it)
	{
		while (it.hasPrevious())
		{
			var left = it.previous();

			if (i.min - 1 == left.max)
			{
				it.remove();
				i.min = left.min;
			}
			else
				return;
		}
	}

	public boolean add(long n)
	{
		var it = intervals.listIterator();

		while (it.hasNext())
		{
			var i = it.next();

			if (i.contains(n))
				return false;

			if (n < i.min)
			{
				if (n == i.min - 1)
				{
					i.min = n;
					mergeLeft(i, it);
					return true;
				}
				else
				{
					it.previous();
					it.add(Interval.of(n));
					return true;
				}
			}
			else
			{
				if (n == i.max + 1)
				{
					i.max = n;
					mergeRight(i, it);
					return true;
				}
			}
		}
		intervals.add(Interval.of(n));
		return true;
	}

	public boolean isEmpty()
	{
		return intervals.isEmpty();
	}

	public boolean contains(long n)
	{
		return intervals.stream().anyMatch(i -> i.contains(n));
	}

	public Stream<Long> stream()
	{
		return intervals.stream().flatMap(Interval::stream);
	}

	@Override
	public Iterator<Long> iterator()
	{
		return stream().iterator();
	}

	public int size()
	{
		return (int) lsize();
	}

	public long lsize()
	{
		return intervals.stream().mapToLong(Interval::lsize).sum();
	}

	@Override
	public String toString()
	{
		if (intervals.isEmpty())
			return "[]";

		StringBuilder sb = new StringBuilder();

		for (var i : intervals)
			sb.append(i).append(",");

		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
}