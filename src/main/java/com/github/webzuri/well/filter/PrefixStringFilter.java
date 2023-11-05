package com.github.webzuri.well.filter;

import java.util.List;

import org.apache.commons.collections4.trie.PatriciaTrie;

import com.github.webzuri.well.help.HelpString;

class PrefixStringFilter implements IPrefixStringFilter
{
	int prefixSize;

	PatriciaTrie<Object> prefixes;

	PrefixStringFilter(int prefixSize)
	{
		prefixes = new PatriciaTrie<>();

		if (prefixSize < -1)
			prefixSize = -1;

		this.prefixSize = prefixSize;
	}

	public boolean isEmpty()
	{
		return prefixes.isEmpty();
	}

	private String getPrefix(String s)
	{
		var nbcodePoints = Character.codePointCount(s, 0, s.length());
		return prefixSize < 0 || nbcodePoints <= prefixSize ? s : HelpString.subCodePoints(s, 0, prefixSize);
	}

	public void addPrefix(String s)
	{
		if (!s.isEmpty())
			prefixes.put(getPrefix(s), null);
	}

	public void addPrefix(String s, String... more)
	{
		addPrefix(s);

		for (var ss : List.of(more))
			addPrefix(ss);
	}

	public void addPrefix(Iterable<String> slist)
	{
		for (var s : slist)
			addPrefix(s);
	}

	@Override
	public boolean test(String s)
	{
		return prefixes.containsKey(getPrefix(s));
	}

	public int size()
	{
		return prefixes.size();
	}

	public int prefixSize()
	{
		return prefixSize;
	}
}
