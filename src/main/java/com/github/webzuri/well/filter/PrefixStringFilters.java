package com.github.webzuri.well.filter;

import java.util.List;

import com.github.webzuri.well.codec.IDecoder;
import com.github.webzuri.well.codec.IEncoder;
import com.github.webzuri.well.help.HelpString;

public final class PrefixStringFilters
{
	private PrefixStringFilters()
	{
		throw new AssertionError();
	}

	// ==========================================================================

	private enum Factory
	{
		Empty;

		IPrefixStringFilter i = empty();
	}

	// ==========================================================================

	private static IPrefixStringFilter empty()
	{
		return new PrefixStringFilter(0)
		{
			@Override
			public boolean isEmpty()
			{
				return true;
			}

			@Override
			public void addPrefix(String s)
			{
			}

			@Override
			public void addPrefix(String s, String... more)
			{
			}

			@Override
			public void addPrefix(Iterable<String> slist)
			{
			}

			@Override
			public boolean test(String s)
			{
				return true;
			}

			@Override
			public int size()
			{
				return 0;
			}

			@Override
			public int prefixSize()
			{
				return 0;
			}

		};
	}

	public static IPrefixStringFilter create(int prefixSize)
	{
		if (prefixSize == 0)
			return Factory.Empty.i;

		return new PrefixStringFilter(prefixSize);
	}

	// ==========================================================================

	private static final String default_separator = "^";

	// TODO: escape separator in data

	public static IEncoder<IPrefixStringFilter> encoder()
	{
		return encoder(default_separator);
	}

	public static IEncoder<IPrefixStringFilter> encoder(String separator)
	{
		var replaceFun = HelpString.doubleEscape(List.of(separator));

		return (prefFilter, writer) -> {
			var prefixStrFiltern = (PrefixStringFilter) prefFilter;

			var it = prefixStrFiltern.prefixes.mapIterator();

			if (!it.hasNext())
				return;

			writer.append(Integer.toString(prefixStrFiltern.prefixSize)).append(default_separator);
			writer.append(replaceFun.apply(it.next()));

			while (it.hasNext())
				writer.append(separator).append(replaceFun.apply(it.next()));
		};
	}

	public static IDecoder<IPrefixStringFilter> decoder()
	{
		return decoder(default_separator, List.of());
	}

	public static IDecoder<IPrefixStringFilter> decoder(Iterable<String> toEscape)
	{
		return decoder(default_separator, toEscape);
	}

	public static IDecoder<IPrefixStringFilter> decoder(String separator, Iterable<String> toEscape)
	{
		var unescape = HelpString.doubleUnescape(List.of(separator));
		var pattern  = HelpString.notDoublePattern(separator);

		// TODO: split directly the reader
		return IDecoder.from(line -> {
			var ret     = new PrefixStringFilter(-1);
			int maxSize = 0;
			var parts   = pattern.split(line, 2);
			ret.prefixSize = Integer.parseInt(parts[0]);

			for (var s : pattern.split(parts[1]))
			{
				ret.addPrefix(unescape.apply(s));
				maxSize = Math.max(maxSize, s.length());
			}
			return ret;
		});
	}
}
