package com.github.webzuri.well.help;

import java.time.Duration;

public final class HelpDuration
{
	private HelpDuration()
	{
		throw new AssertionError();
	}

	public static Duration parse(String s)
	{
		var ret = Duration.ofDays(0);
		var sb  = new StringBuilder();
		var sa  = s.toCharArray();
		int pos = 0;

		while (pos < sa.length)
		{
			sb.setLength(0);

			for (; pos < sa.length && !Character.isLetter(sa[pos]); pos++)
				sb.append(sa[pos]);

			var sval = sb.toString();
			sb.setLength(0);

			for (; pos < sa.length && Character.isLetter(sa[pos]); pos++)
				sb.append(sa[pos]);

			var sunit = sb.toString();
			var val   = Long.parseLong(sval);

			if (val == 0)
				continue;

			ret = add(ret, sunit, val);
		}
		return ret;
	}

	private static Duration add(Duration d, String unit, long value)
	{
		switch (unit.toLowerCase())
		{
		case "h":
			return d.plusHours(value);
		case "m":
			return d.plusMinutes(value);
		case "s":
			return d.plusSeconds(value);
		case "ms":
			return d.plusMillis(value);
		case "ns":
			return d.plusNanos(value);
		default:
			throw new NumberFormatException(String.format("Invalid duration format `%s`", unit));
		}
	}
}
