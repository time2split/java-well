package com.github.webzuri.well.parser;

import java.text.ParseException;
import java.util.function.BiFunction;

@FunctionalInterface
public interface IParse<VAL> extends BiFunction<String, String, VAL>
{
	VAL parse(String s, String delimiters) throws ParseException;

	default VAL parse(String s) throws ParseException
	{
		return parse(s, "");
	}

	@Override
	default VAL apply(String s, String delimiters)
	{
		try
		{
			return parse(s, delimiters);
		}
		catch (ParseException e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	default VAL apply(String s)
	{
		return apply(s, "");
	}

	static IParse<String> identity()
	{
		return (s, d) -> s;
	}
}
