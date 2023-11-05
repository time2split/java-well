package com.github.webzuri.well.help;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.webzuri.well.function.ProcessFunction;
import com.github.webzuri.well.memoization.Memoizers;

public final class HelpIterator
{
	private HelpIterator()
	{
		throw new AssertionError();
	}

	private static abstract class AbstractIIterator<IN, OUT> implements Iterator<OUT>
	{
		protected Iterator<IN> it;

		public AbstractIIterator(Iterator<IN> it)
		{
			this.it = it;
		}

		@Override
		public boolean hasNext()
		{
			return it.hasNext();
		}
	}

	// ==========================================================================

	/**
	 * Execute a function at the end of the iterator
	 */
	public static <E> Iterator<E> callback(Iterator<E> iterator, ProcessFunction callback)
	{
		var doonce = Memoizers.lazy(() -> {
			callback.process();
			return true;
		});
		return new AbstractIIterator<E, E>(iterator)
		{
			@Override
			public E next()
			{
				return iterator.next();
			}

			@Override
			public boolean hasNext()
			{
				var ret = it.hasNext();

				if (!it.hasNext())
					doonce.get();

				return ret;
			}
		};
	}

	public static <E> Iterator<E> count(Iterator<E> iterator, BatchSize bsize)
	{
		return new AbstractIIterator<E, E>(iterator)
		{
			@Override
			public E next()
			{
				bsize.nbItems++;
				return iterator.next();
			}
		};
	}

	public static <E> Iterator<List<E>> batch(Iterator<E> iterator, int batchSize)
	{
		return new AbstractIIterator<E, List<E>>(iterator)
		{
			@Override
			public List<E> next()
			{
				var ret = new ArrayList<E>(batchSize);

				for (int i = 0; i < batchSize && hasNext(); i++)
					ret.add(it.next());

				ret.trimToSize();
				return ret;
			}
		};
	}

	public static <E> Iterator<List<E>> batch(Iterator<E> iterator, int batchSize, BatchSize bsize)
	{
		if (null == bsize)
			return batch(iterator, batchSize);

		return new AbstractIIterator<E, List<E>>(iterator)
		{
			@Override
			public List<E> next()
			{
				var ret = new ArrayList<E>(batchSize);
				int i;

				for (i = 0; i < batchSize && iterator.hasNext(); i++)
					ret.add(iterator.next());

				bsize.nbItems += i;
				bsize.size++;
				ret.trimToSize();
				return ret;
			}
		};
	}

	public static <T> Iterable<T> iterable(Iterator<T> it)
	{
		return () -> it;
	}
}
