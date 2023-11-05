package com.github.webzuri.well.help;

public final class BatchSize
{
	public long size;

	public long nbItems;

	public static BatchSize add(BatchSize a, BatchSize b)
	{
		var ret = new BatchSize();
		ret.size    = a.size + b.size;
		ret.nbItems = a.nbItems + b.nbItems;
		return ret;
	}
}
