package com.github.webzuri.well.parser;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.CharUtils;

public final class Delimiters
{
	public Delimiters()
	{
		throw new AssertionError();
	}

	private static enum Pairs
	{
		Classic(Map.of("{", "}" //
			, "(", ")" //
			, "[", "]" //
			, "<", ">" //
		)) //
		, nonWord(Map.copyOf(makeNonWord())) //
		, Strings(Map.of("\"", "\"", "'", "'")) //
		;

		Map<String, String> map;

		private Pairs(Map<String, String> map)
		{
			this.map = map;
		}
	}

	private static Map<String, String> makeNonWord()
	{
		var ret = new HashMap<String, String>();

		for (int i = 0; i < 128; i++)
		{
			char c = (char) i;
			var  s = String.valueOf(c);

			if (!CharUtils.isAsciiAlphanumeric(c) && CharUtils.isAsciiPrintable(c) && c != ' ')
				ret.put(s, s);
		}
		ret.putAll(Pairs.Classic.map);
		ret.remove("_");
		return ret;
	}

	public static Map<String, String> classic()
	{
		return Pairs.Classic.map;
	}

	public static Map<String, String> nonWord()
	{
		return Pairs.nonWord.map;
	}

	public static Map<String, String> strings()
	{
		return Pairs.Strings.map;
	}
}
