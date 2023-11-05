package com.github.webzuri.well.numeric;

import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.github.webzuri.well.codec.IDecoder;
import com.github.webzuri.well.codec.IEncoder;

public final class Interval implements Iterable<Long>
{
	long min, max;

	private boolean empty;

	private Interval()
	{
		empty = true;
	}

	private Interval(long min, long max)
	{
		this.min = min;
		this.max = max;
		empty    = false;
	}

	public static Interval empty()
	{
		return new Interval();
	}

	public static Interval of(long n)
	{
		return new Interval(n, n);
	}

	public static Interval of(long min, long max)
	{
		return new Interval(min, max);
	}

	public static Interval intersection(Interval a, Interval b)
	{
		if (a.max < b.min || a.min > b.max)
			return empty();
		if (a.containsAll(b))
			return b;
		if (b.containsAll(a))
			return a;

		if (a.max > b.min)
			return of(b.min, a.max);
		else
			return of(a.min, b.max);
	}

	public static int cmp(Interval a, Interval b)
	{
		long ret = a.min - b.min;

		if (ret != 0)
			return (int) ret;

		ret = a.max - b.max;
		return (int) ret;
	}

	// ==========================================================================

	public boolean add(long n)
	{
		if (empty)
		{
			min   = max = n;
			empty = false;
			return true;
		}

		if (contains(n))
			return false;

		if (n == min - 1)
			min--;
		else if (n == max + 1)
			max++;
		else
			throw new IllegalArgumentException(String.format("Cannot add %s to %s", n, this));

		return true;
	}

	public boolean addAll(Interval add)
	{
		if (empty)
		{
			min   = add.min;
			max   = add.max;
			empty = false;
			return true;
		}
		if (add.min > max + 1)
			throw new IllegalArgumentException(String.format("add.min(%d) > this.max(%d) + 1", add.min, max));
		if (add.max + 1 < max)
			throw new IllegalArgumentException(String.format("add.min(%d) > this.max(%d) + 1", add.min, max));
		if (add.min < min)
			min = add.min;
		if (add.max > max)
			max = add.max;
		return true;
	}

	public boolean isEmpty()
	{
		return empty;
	}

	public boolean contains(long n)
	{
		return !empty && min <= n && n <= max;
	}

	public boolean containsAll(Interval a)
	{
		return !empty && a.min >= min && a.max <= max;
	}

	public Stream<Long> stream()
	{
		if (empty)
			return Stream.empty();

		return LongStream.range(min, max).boxed();
	}

	@Override
	public Iterator<Long> iterator()
	{
		return stream().iterator();
	}

	public boolean remove(long n)
	{
		if (empty)
			return false;
		if (n == max)
			max--;
		else if (n == min)
			min++;
		else
			throw new IllegalArgumentException(String.format("%s cannot remove '$%d", this, n));

		return true;
	}

	public boolean removeAll(Interval toRemove)
	{
		if (empty)
			return false;

		if (toRemove.max < max && toRemove.min > min)
			throw new IllegalArgumentException(String.format("%s: cannot remove %s", this, toRemove));

		if (toRemove.min > min)
			max = toRemove.min;
		else
			min = toRemove.max;

		return true;
	}

	public long getMin()
	{
		return min;
	}

	public long getMax()
	{
		return max;
	}

	public int size()
	{
		return (int) lsize();
	}

	public long lsize()
	{
		return empty ? 0 : (max - min + 1);
	}

	// ==========================================================================

	public static IEncoder<Interval> encoder()
	{
		return IEncoder.from(Interval::toString);
	}

	private static Pattern intervalPattern = Pattern.compile("\\s*\\[(\\d+)(?:\\.\\.(\\d+))?\\]\\s*");

	public static IDecoder<Interval> decoder()
	{
		return IDecoder.from(s -> {
			var matcher = intervalPattern.matcher(s);

			if (!matcher.matches())
				throw new IllegalArgumentException(String.format("Invalid Interval string '%s'", s));

			long min = Long.parseLong(matcher.group(1));
			var  g2  = matcher.group(2);
			long max = g2 == null ? min : Long.parseLong(g2);

			return Interval.of(min, max);
		});

	}

	@Override
	public String toString()
	{
		if (empty)
			return "[]";
		if (min == max)
			return String.format("[%d]", min);

		return String.format("[%d..%d]", min, max);
	}
}