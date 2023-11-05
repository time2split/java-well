package com.github.webzuri.well.writer;

import java.io.Writer;

public abstract class AbstractWriter<T> implements IWriter<T>
{
	private Writer writer;

	@Override
	public Writer getWriter()
	{
		return writer;
	}

	@Override
	public IWriter<T> setWriter(Writer writer)
	{
		this.writer = writer;
		return this;
	}
}
