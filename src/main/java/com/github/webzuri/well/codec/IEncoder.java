package com.github.webzuri.well.codec;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.Function;

@FunctionalInterface
public interface IEncoder<T>
{
	void encodeTo(T object, Writer writer) throws IOException;

	default String encode(T object)
	{
		try
		{
			var s = new StringWriter();
			encodeTo(object, s);
			return s.toString();
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	// ==========================================================================

	static <T> IEncoder<T> from(Function<T, String> encode)
	{
		return new IEncoder<T>()
		{
			@Override
			public void encodeTo(T object, Writer writer) throws IOException
			{
				writer.write(encode(object));
			}

			@Override
			public String encode(T object)
			{
				return encode.apply(object);
			}
		};
	}

	static <T> IEncoder<T> toStringEncoder(String delimiters)
	{
		assert (delimiters.length() >= 2);

		return (o, w) -> {
			w.append(delimiters.charAt(0)).append(o.toString()).append(delimiters.charAt(1));
		};
	}

	static <T> IEncoder<T> toStringEncoder()
	{
		return (o, w) -> {
			w.append(o.toString());
		};
	}
}
