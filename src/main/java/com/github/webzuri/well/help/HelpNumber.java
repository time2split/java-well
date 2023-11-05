package com.github.webzuri.well.help;

public final class HelpNumber
{
	private HelpNumber()
	{
		throw new AssertionError();
	}

	public static boolean isInt(double val)
	{
		return val == Math.ceil(val);
	}

	public static boolean isInt(Number n)
	{
		return isInt(n.doubleValue());
	}
}
