package com.github.webzuri.well.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public final class StdUrlStreamHandler
{
	private StdUrlStreamHandler()
	{
		throw new AssertionError();
	}

	public static URLConnection stdout(URL url)
	{
		return new URLConnection(url)
		{
			@Override
			public OutputStream getOutputStream() throws IOException
			{
				return System.out;
			}

			@Override
			public void connect() throws IOException
			{

			}
		};
	}

	public static URLConnection stdin(URL url)
	{
		return new URLConnection(url)
		{
			@Override
			public InputStream getInputStream() throws IOException
			{
				return System.in;
			}

			@Override
			public void connect() throws IOException
			{

			}
		};
	}
}
