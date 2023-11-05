package com.github.webzuri.well.codec;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

import org.apache.commons.io.IOUtils;

import com.github.webzuri.well.function.FunctionThrows;

@FunctionalInterface
public interface IDecoder<T>
{
	T decode(Reader reader) throws IOException, ParseException;

	default T decode(String from) throws ParseException
	{
		try
		{
			return decode(new StringReader(from));
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	public static <T> IDecoder<T> from(FunctionThrows<String, T, ? extends ParseException> fromString)
	{
		return new IDecoder<T>()
		{
			@Override
			public T decode(Reader reader) throws IOException, ParseException
			{
				return decode(IOUtils.toString(reader));
			}

			@Override
			public T decode(String from) throws ParseException
			{
				return fromString.applyThrows(from);
			}
		};
	}
}
