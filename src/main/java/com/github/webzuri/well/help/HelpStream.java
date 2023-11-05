package com.github.webzuri.well.help;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.webzuri.well.function.ProcessFunction;

public final class HelpStream
{
	private HelpStream()
	{
		throw new AssertionError();
	}

	@SuppressWarnings("unchecked")
	public static <T> Stream<T> toStreamDownCast(Iterator<? extends T> iterator)
	{
		return StreamSupport.stream(((Iterable<T>) () -> (Iterator<T>) iterator).spliterator(), false);
	}

	public static <T> Stream<T> toStream(Iterator<T> iterator)
	{
		return StreamSupport.stream(((Iterable<T>) () -> iterator).spliterator(), false);
	}

	public static <T> Stream<T> toStreamDownCast(Iterable<? extends T> iterable)
	{
		return toStreamDownCast(iterable.iterator());
	}

	public static <T> Stream<T> toStream(Iterable<T> iterable)
	{
		return toStream(iterable.iterator());
	}

	public static <T> Iterable<T> toIterable(Stream<T> stream)
	{
		return () -> stream.iterator();
	}

	public static <T> Stream<T> callback(Stream<T> stream, ProcessFunction process)
	{
		return HelpStream.toStream(() -> HelpIterator.callback(stream.iterator(), process));
	}

	public static <T> Stream<List<T>> batch(Stream<T> stream, int batchSize)
	{
		return toStream(HelpIterable.batch(() -> stream.iterator(), batchSize));
	}

	public static <T> Stream<List<T>> batch(Stream<T> stream, int batchSize, BatchSize bsize)
	{
		return toStream(HelpIterable.batch(() -> stream.iterator(), batchSize, bsize));
	}

	public static <T> Stream<T> count(Stream<T> stream, BatchSize bsize)
	{
		return toStream(HelpIterable.count(() -> stream.iterator(), bsize));
	}

	public static <T> Stream<T> clamp(Stream<T> stream, ProcessFunction start, ProcessFunction end)
	{

		return toStream(new Iterator<T>()
		{
			private Iterator<T> it = stream.iterator();

			@Override
			public boolean hasNext()
			{
				start.process();
				var ret = it.hasNext();
				end.process();
				return ret;
			}

			@Override
			public T next()
			{
				return it.next();
			}
		});
	}
}
