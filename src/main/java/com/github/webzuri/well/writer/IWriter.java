package com.github.webzuri.well.writer;

import java.io.IOException;
import java.io.Writer;

import com.github.webzuri.well.codec.IEncoder;

public interface IWriter<T> extends IEncoder<T>
{
	Writer getWriter();

	IWriter<T> setWriter(Writer writer);

	default void writeTo(T subject, Writer writer) throws IOException
	{
		encodeTo(subject, writer);
	}

	default void write(T subject) throws IOException
	{
		writeTo(subject, getWriter());
	}
}
