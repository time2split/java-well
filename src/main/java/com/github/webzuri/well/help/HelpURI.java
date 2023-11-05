package com.github.webzuri.well.help;

import java.net.URLEncoder;
import java.nio.charset.Charset;

public final class HelpURI
{
	private HelpURI()
	{
		throw new AssertionError();
	}

	public static String encodePath(String path)
	{
		return URLEncoder.encode(path, Charset.defaultCharset()) //
			.replaceAll("%2F", "/") //
			.replaceAll("\\+", "%20");
	}

	public static String encodeURI(String uri)
	{
		var parts = uri.split(":", 2);

		if (parts.length == 1)
			return encodePath(uri);

		return parts[0] + ":" + encodePath(parts[1]);
	}
}
