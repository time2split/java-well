package com.github.webzuri.well.numeric;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;

public final class Base
{
	private int base[];

	private Base(int... base)
	{
		this.base = base;
	}

	private Base(List<? extends Number> base)
	{
		this.base = base.stream().mapToInt(Number::intValue).toArray();
	}

	// ==========================================================================

	/**
	 * Create a numeric base of a determined length with a unique base number for each element.
	 * 
	 * @param base   the base for each element
	 * @param length the size of a number
	 * @return
	 */
	public static Base simple(int base, int length)
	{
		int[] abase = new int[length];
		Arrays.fill(abase, base);
		return new Base(abase);
	}

	public static Base from(int... base)
	{
		return new Base(base);
	}

	public static Base from(List<? extends Number> base)
	{
		return new Base(base);
	}

	public static Base cartesianProduct(Iterable<? extends Iterable<?>> sets)
	{
		int nb      = IterableUtils.size(sets);
		int ibase[] = new int[nb];
		int i       = 0;

		for (Iterable<?> set : sets)
			ibase[i++] = IterableUtils.size(set);

		return Base.from(ibase);
	}

	// ==========================================================================

	public int[] getBase()
	{
		return base.clone();
	}

	/**
	 * Increment 'num' considering the numeric base 'base'.
	 * 'num' represents a multi base number.
	 */
	public void increment(int num[])
	{
		assert (num.length == base.length);

		for (int i = num.length - 1; i >= 0; i--)
		{
			assert (base[i] != 0);
			num[i]++;

			if (num[i] % base[i] == 0)
				num[i] = 0;
			else
				break;
		}
	}

	public int toInt(int num[])
	{
		return (int) toLong(num);
	}

	public long toLong(int num[])
	{
		int  pos     = base.length - 1;
		long ret     = num[pos];
		long accBase = base[pos];

		while (pos-- != 0)
		{
			ret     += num[pos] * accBase;
			accBase *= base[pos + 1];
		}
		return ret;
	}

	public int[] getNum(int i)
	{
		return getNum((long) i);
	}

	public int[] getNum(long i)
	{
		int ret[] = new int[base.length];
		toNum(i, ret);
		return ret;
	}

	public void toNum(int i, int ret[])
	{
		toNum((long) i, ret);
	}

	public void toNum(long i, int ret[])
	{
		int pos = ret.length - 1;
		int b   = 1;

		while (pos != 0 && i != 0)
		{
			b = base[pos];
			int n = (int) (i % b);

			i        /= b;
			ret[pos]  = n;
			pos--;
		}
		Arrays.fill(ret, 0, pos + 1, 0);
	}

	private long max = -1;

	public int size()
	{
		return (int) longSize();
	}

	public long longSize()
	{
		if (max != -1)
			return max;
		if (base.length == 0)
		{
			max = 0;
			return max;
		}
		int pos = base.length - 1;
		max = base[pos];

		while (pos-- != 0)
			max *= base[pos];

		return max;
	}
}
