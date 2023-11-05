package com.github.webzuri.well.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SequenceOutputStream extends OutputStream
{
	private List<OutputStream> streams;

	public SequenceOutputStream(List<OutputStream> streams)
	{
		this.streams = new ArrayList<>(streams);
	}

	public SequenceOutputStream(OutputStream... streams)
	{
		this.streams = List.of(streams);
	}

	@Override
	public void write(int b) throws IOException
	{
		for (var s : streams)
			s.write(b);
	}

	@Override
	public void close() throws IOException
	{
		for (var s : streams)
			if (s != System.out && s != System.err)
				s.close();
	}
}
