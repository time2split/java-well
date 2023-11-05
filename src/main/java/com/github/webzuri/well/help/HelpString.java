package com.github.webzuri.well.help;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class HelpString
{
	private HelpString()
	{
		throw new AssertionError();
	}

	public static int codePointLength(String s)
	{
		return s.codePointCount(0, s.length());
	}

	public static String subCodePoints(String s, int offset, int endIndex)
	{
		try
		{
			return s.substring(s.offsetByCodePoints(0, offset), s.offsetByCodePoints(0, endIndex));
		}
		catch (IndexOutOfBoundsException e)
		{
			throw e;
		}
	}

	public static String removePrefix(String s, String prefix)
	{
		if (s.startsWith(prefix))
			return s.substring(prefix.length(), s.length());

		return s;
	}

	public static String removeSuffix(String s, String suffix)
	{
		if (s.endsWith(suffix))
			return s.substring(0, s.length() - suffix.length());

		return s;
	}

	public static Pattern escapePattern(Iterable<String> toEscape)
	{
		var patternBuilder = new StringBuilder();

		for (var e : toEscape)
			patternBuilder.append(Pattern.quote(e)).append('|');

		patternBuilder.deleteCharAt(patternBuilder.length() - 1);
		return Pattern.compile(patternBuilder.toString());
	}

	public static Pattern unescapePattern(String escape)
	{
		var patternBuilder = new StringBuilder();
		patternBuilder.append(Pattern.quote(escape)).append("(.)");
		return Pattern.compile(patternBuilder.toString());
	}

	public static Pattern notDoublePattern(String searchFor)
	{
		searchFor = Pattern.quote(searchFor);
		return Pattern.compile(new StringBuilder() //
			.append("(?<!").append(searchFor).append(")") //
			.append(searchFor) //
			.append("(?!").append(searchFor).append(")") //
			.toString());
	}

	// ==========================================================================

	private enum EscapeFunctions
	{
		Classic("\\", "\\\\"), //
		;

		private Function<String, String> escapeFunction;

		private EscapeFunctions(String escape, String replacement)
		{
			this.escapeFunction = HelpString.escape(HelpString.escapePattern(List.of(escape)), replacement);
		}
	}

	public static Function<String, String> classicEscapeFunction()
	{
		return EscapeFunctions.Classic.escapeFunction;
	}

	public static Function<String, String> doubleEscape(Iterable<String> replacements)
	{
		var replMap = HelpStream.toStream(replacements).collect(Collectors.toMap(r -> r, r -> r + r));
		return replaceAll(replMap);
	}

	public static Function<String, String> doubleUnescape(Iterable<String> replacements)
	{
		var replMap = HelpStream.toStream(replacements).collect(Collectors.toMap(r -> r + r, r -> r));
		return replaceAll(replMap);
	}

	public static Function<String, String> replaceAll(Map<String, String> replacements)
	{
		var pattern = escapePattern(replacements.keySet());

		return s -> pattern.matcher(s).replaceAll(m -> {
			var capture = s.substring(m.start(), m.end());
			return replacements.get(capture);
		});
	}

	public static Function<String, String> escape(Pattern pattern, String escape)
	{
		return s -> pattern.matcher(s).replaceAll(escape + "$0");
	}

	public static Function<String, String> unescape(Pattern pattern)
	{
		return unescape(pattern, "$1");
	}

	private static Function<String, String> unescape(Pattern pattern, String replacement)
	{
		return s -> pattern.matcher(s).replaceAll(replacement);
	}
}
