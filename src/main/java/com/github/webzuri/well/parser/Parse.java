package com.github.webzuri.well.parser;

import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public final class Parse
{
	public Parse()
	{
		throw new AssertionError();
	}

	public static Pair<String, String> parseWordDelimiters(String word, Map<String, String> delimiters) throws ParseException
	{
		if (word.isEmpty())
			return Pair.of(word, "");

		var dstart = word.substring(0, 1);
		var dend   = delimiters.get(dstart);

		if (null == dend)
			return Pair.of(word, "");

		var send = word.substring(word.length() - 1);

		if (!send.equals(dend))
			throw new ParseException(String.format("Expected %s, have %s", dend, send), word.length() - 1);

		return Pair.of(word, dstart + dend);
	}

	public static Pair<String, String> removeWordDelimiters(String word, Map<String, String> delimiters) throws ParseException
	{
		var res = parseWordDelimiters(word, delimiters);
		var d   = res.getValue();

		if (d.isEmpty())
			return res;

		return Pair.of(word.substring(1, word.length() - 1), res.getValue());
	}
}
