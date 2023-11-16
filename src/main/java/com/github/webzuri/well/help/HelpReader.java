package com.github.webzuri.well.help;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;

import com.github.webzuri.well.codec.IDecoder;
import com.github.webzuri.well.memoization.Memoizers;

/**
 * {@link HelpReader} enable filtering on {@link Reader}s and provides some
 * transformations. A {@link Builder} class allow to add multiple operation with
 * a Fluent API.
 */
public final class HelpReader {

	private HelpReader() {
		throw new AssertionError();
	}

	private static class SubReader extends Reader {

		protected Reader parent;

		SubReader(Reader parent) {
			this.parent = parent;
		}

		// ====================================================================
		@Override
		public void mark(int readAheadLimit) throws IOException {
			parent.mark(readAheadLimit);
		}

		@Override
		public boolean markSupported() {
			return parent.markSupported();
		}

		@Override
		public void close() throws IOException {
			parent.close();
		}

		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			return parent.read(cbuf, off, len);
		}

		@Override
		public int read(char[] cbuf) throws IOException {
			return parent.read(cbuf);
		}

		@Override
		public int read() throws IOException {
			return parent.read();
		}

		@Override
		public boolean ready() throws IOException {
			return parent.ready();
		}

		@Override
		public long skip(long n) throws IOException {
			return parent.skip(n);
		}

		@Override
		public long transferTo(Writer out) throws IOException {
			return parent.transferTo(out);
		}
	}

	/**
	 * A {@link SubReader} which must only implement read()
	 */
	private static class SubReaderRead extends SubReader {

		SubReaderRead(Reader parent) {
			super(parent);
		}

		@Override
		public final int read(char[] cbuf, int off, int len) throws IOException {

			for (int nb = 0;;) {
				int c = read();

				if (c == -1)
					return nb > 0 ? nb : -1;

				cbuf[nb++] = (char) c;
			}
		}

		@Override
		public final int read(char[] cbuf) throws IOException {
			return read(cbuf, 0, cbuf.length);
		}
	}

	// ====================================================================

	private static class SkipReader extends SubReader {

		private Predicate<Character> skipTest;

		SkipReader(Reader parent, Predicate<Character> skipTest) {
			super(parent);
			this.skipTest = skipTest;
		}

		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			var nb = parent.read(cbuf, off, len);
			var skip = filter(cbuf, off, nb, skipTest);
			return nb - skip;
		}

		@Override
		public int read(char[] cbuf) throws IOException {
			var nb = parent.read(cbuf);
			var skip = filter(cbuf, 0, nb, skipTest);
			return nb - skip;
		}

		@Override
		public int read() throws IOException {

			for (;;) {
				int c = parent.read();

				if (c == -1)
					return -1;
				if (skipTest.test((char) c))
					return c;
			}
		}

		private static int filter(char[] cbuf, int off, int nb, Predicate<Character> skipTest) {
			int skip = 0;

			for (int i = off, l = nb + off; i < l;) {
				char c = cbuf[i];

				if (skipTest.test(c))
					skip++;
				else
					i++;

				cbuf[i] = cbuf[i + skip];
			}
			return skip;
		}
	}

	/**
	 * Skip some characters of the {@link Reader}.
	 * 
	 * @param skipTest Check the characters to skip
	 * @return A {@link IDecoder} to decore the {@link Reader}
	 */
	public static IDecoder<Reader> skip(Predicate<Character> skipTest) {
		return (Reader reader) -> new SkipReader(reader, skipTest);
	}

	// ====================================================================

	private static class SkipEmptyLineReader extends SubReader {

		private Predicate<Character> endOfLine;

		private boolean isEmpty = true;

		SkipEmptyLineReader(Reader parent, Predicate<Character> endOfLine) {
			super(parent);
			this.endOfLine = endOfLine;
		}

		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			var nb = parent.read(cbuf, off, len);
			var skip = filter(cbuf, off, nb, endOfLine);
			return nb - skip;
		}

		@Override
		public int read(char[] cbuf) throws IOException {
			var nb = parent.read(cbuf);
			var skip = filter(cbuf, 0, nb, endOfLine);
			return nb - skip;
		}

		@Override
		public int read() throws IOException {

			for (;;) {
				int c = parent.read();

				if (c == -1)
					return -1;

				if (isEmpty) {

					if (!endOfLine.test((char) c)) {
						isEmpty = false;
						return c;
					}

				} else {

					if (endOfLine.test((char) c))
						isEmpty = true;

					return c;
				}
			}
		}

		private int filter(char[] cbuf, int off, int nb, Predicate<Character> endOfLine) {
			int skip = 0;

			for (int i = off, l = nb + off; i < l;) {
				char c = cbuf[i];

				if (isEmpty) {

					if (endOfLine.test(c)) {
						skip++;
					} else {
						isEmpty = false;
						i++;
					}
				} else {
					i++;

					if (endOfLine.test(c))
						isEmpty = true;
				}
				cbuf[i] = cbuf[i + skip];
			}
			return skip;
		}
	}

	/**
	 * * Skip the empty lines of the {@link Reader}.
	 * 
	 * @param endOfLine Check the end of line character
	 * @return A {@link IDecoder} to decore the {@link Reader}
	 * 
	 */
	public static IDecoder<Reader> skipEmptyLine(Predicate<Character> endOfLine) {
		return (Reader reader) -> new SkipEmptyLineReader(reader, endOfLine);
	}

	// ====================================================================

	private static class LTrimReader extends SubReaderRead {

		private Predicate<Character> trimChar;
		private Predicate<Character> endOfLine;

		private boolean isTrimming = true;
		private boolean stop = false;

		LTrimReader(Reader parent, Predicate<Character> trimChar, Predicate<Character> endOfLine) {
			super(parent);
			this.trimChar = trimChar;
			this.endOfLine = endOfLine;
		}

		@Override
		public int read() throws IOException {

			if (stop)
				return -1;

			if (isTrimming) {

				for (;;) {
					var c = parent.read();

					if (c == -1) {
						stop = true;
						return -1;
					}
					if (endOfLine.test((char) c))
						return c;
					if (!trimChar.test((char) c)) {
						isTrimming = false;
						return c;
					}
				}
			} else {
				var c = parent.read();

				if (c == -1) {
					stop = true;
					return -1;
				}

				if (endOfLine.test((char) c))
					isTrimming = true;

				return c;
			}
		}
	}

	/**
	 * Trim some characters to the left of the {@link Reader}.
	 * 
	 * @param trimChar  Check the characters to trim
	 * @param endOfLine Check the end of line character
	 * @return A {@link IDecoder} to decore the {@link Reader}
	 */
	public static IDecoder<Reader> ltrim(Predicate<Character> trimChar, Predicate<Character> endOfLine) {
		return (Reader reader) -> new LTrimReader(reader, trimChar, endOfLine);
	}

	// ====================================================================

	private static class SkipCommentCharLineReader extends SubReaderRead {

		private Predicate<Character> commentChar;
		private Predicate<Character> endOfLine;

		SkipCommentCharLineReader(Reader parent, Predicate<Character> commentChar, Predicate<Character> endOfLine) {
			super(parent);
			this.commentChar = commentChar;
			this.endOfLine = endOfLine;
		}

		@Override
		public int read() throws IOException {

			int c = parent.read();

			if (c == -1)
				return -1;

			if (commentChar.test((char) c))
				while (-1 != (c = parent.read()) && !endOfLine.test((char) c))
					;

			return c;
		}
	}

	/**
	 * Skip the comment lines of the {@link Reader}. A comment line is defined by a
	 * specific leading character.
	 * 
	 * @param commentCharTest Check the leading comment character
	 * @param endOfLine       Check the end of line character
	 * @return A {@link IDecoder} to decore the {@link Reader}
	 */
	public static IDecoder<Reader> skipCommentLine(Predicate<Character> commentCharTest,
			Predicate<Character> endOfLine) {
		return (Reader reader) -> new SkipCommentCharLineReader(reader, commentCharTest, endOfLine);
	}

	// ====================================================================

	private static class StopReader extends SubReaderRead {

		private Predicate<Character> stopTest;

		private boolean stopped = false;

		StopReader(Reader parent, Predicate<Character> skipTest) {
			super(parent);
			this.stopTest = skipTest;
		}

		@Override
		public int read() throws IOException {

			if (stopped)
				return -1;

			int c = parent.read();

			if (c == -1 || stopTest.test((char) c)) {
				stopped = true;
				return -1;
			}
			return c;
		}
	}

	/**
	 * Stop the reading of a {@link Reader} at the encounter of a sepcific
	 * character.
	 * 
	 * @param stopTest Check the stop character
	 * @return A {@link IDecoder} to decore the {@link Reader}
	 */
	public static IDecoder<Reader> stop(Predicate<Character> stopTest) {
		return (Reader reader) -> new StopReader(reader, stopTest);
	}

	// ====================================================================

	private static class ReaderSplitIterator implements Iterator<Reader> {
		private BufferedReader reader;
		private Predicate<Character> splitTest;

		public ReaderSplitIterator(Reader reader, Predicate<Character> splitTest) {
			this.reader = IOUtils.buffer(reader);
			this.splitTest = splitTest;
		}

		@Override
		public boolean hasNext() {
			try {
				reader.mark(1);

				if (reader.read() == -1)
					return false;

				reader.reset();
				return true;
			} catch (IOException e) {
				throw new Error(e);
			}
		}

		@Override
		public Reader next() {
			return new StopReader(reader, splitTest);
		}

	}

	/**
	 * Split a {@link Reader} into multiple {@link Reader}s according to a separator
	 * character.
	 * 
	 * @param splitTest Check the split character
	 * @return A {@link IDecoder} to decore the {@link Reader}
	 */
	public static IDecoder<Iterable<Reader>> split(Predicate<Character> splitTest) {
		return (Reader reader) -> () -> new ReaderSplitIterator(reader, splitTest);
	}

	// ========================================================================

	public static final class Builder {
		private Reader reader;

		private Builder(Reader reader) {
			this.reader = reader;
		}

		public Reader build() {
			return reader;
		}

		/**
		 * @see HelpReader#ltrim(Predicate, Predicate)
		 * @param trimChar
		 * @param endOfLine
		 * @return
		 */
		public Builder ltrim(Predicate<Character> trimChar, Predicate<Character> endOfLine) {
			try {
				reader = HelpReader.ltrim(trimChar, endOfLine).decode(reader);
				return this;
			} catch (IOException | ParseException e) {
				throw new AssertionError(e);
			}
		}

		/**
		 * @see HelpReader#ltrim(Predicate, Predicate)
		 * @return
		 */
		public Builder ltrim() {
			return ltrim(Character::isWhitespace, onEndOfLine());
		}

		/**
		 * @see HelpReader#skip(Predicate)
		 * @param commentCharTest
		 * @return
		 */
		public Builder skip(Predicate<Character> commentCharTest) {
			try {
				reader = HelpReader.skip(commentCharTest).decode(reader);
				return this;
			} catch (IOException | ParseException e) {
				throw new AssertionError(e);
			}
		}

		/**
		 * @see HelpReader#skipCommentLine(Predicate, Predicate)
		 * @param commentCharTest
		 * @return
		 */
		public Builder skipCommentLine(Predicate<Character> commentCharTest) {
			return skipCommentLine(commentCharTest, onEndOfLine());
		}

		/**
		 * @see HelpReader#skipCommentLine(Predicate, Predicate)
		 * @param commentCharTest
		 * @param endOfLine
		 * @return
		 */
		public Builder skipCommentLine(Predicate<Character> commentCharTest, Predicate<Character> endOfLine) {
			try {
				reader = HelpReader.skipCommentLine(commentCharTest, endOfLine).decode(reader);
				return this;
			} catch (IOException | ParseException e) {
				throw new AssertionError(e);
			}
		}

		/**
		 * @see HelpReader#skipEmptyLine(Predicate)
		 * @param endOfLine
		 * @return
		 */
		public Builder skipEmptyLine(Predicate<Character> endOfLine) {
			try {
				reader = HelpReader.skipEmptyLine(endOfLine).decode(reader);
				return this;
			} catch (IOException | ParseException e) {
				throw new AssertionError(e);
			}
		}

		/**
		 * @see HelpReader#skipEmptyLine(Predicate)
		 * @return
		 */
		public Builder skipEmptyLine() {
			return skipEmptyLine(onEndOfLine());
		}

		/**
		 * @see HelpReader#split(Predicate)
		 * @param splitTest
		 * @return
		 */
		public Iterable<Reader> split(Predicate<Character> splitTest) {
			try {
				return HelpReader.split(splitTest).decode(reader);
			} catch (IOException | ParseException e) {
				throw new AssertionError(e);
			}
		}

		/**
		 * @see HelpReader#stop(Predicate)
		 * @param stopTest
		 * @return
		 */
		public Builder stop(Predicate<Character> stopTest) {
			try {
				reader = HelpReader.stop(stopTest).decode(reader);
				return this;
			} catch (IOException | ParseException e) {
				throw new AssertionError(e);
			}
		}
	}

	/**
	 * Get a {@link Builder} on a {@link Reader}.
	 * 
	 * @param reader The reader to build.
	 * @return A new {@link Reader} with all the operations added in sequence.
	 */
	public static Builder builder(Reader reader) {
		return new Builder(reader);
	}

	// ========================================================================

	/**
	 * @param c The character of comparison
	 * @return A {@link Predicate} testing that an input character correspond to the
	 *         one given in parameter
	 */
	public static Predicate<Character> onChar(char c) {
		return (Character cc) -> (char) cc == c;
	}

	/**
	 * @param c The set of characters of comparison
	 * @return A {@link Predicate} testing that an input character belongs to the
	 *         set of characters given in parameter
	 */
	public static Predicate<Character> onChar(String c) {
		return (Character cc) -> (-1 != c.indexOf(cc.charValue()));
	}

	private static Supplier<Predicate<Character>> onEndOfLineMemo;

	static {
		onEndOfLineMemo = Memoizers.lazy(() -> {
			var eol = System.lineSeparator();

			if (eol.length() >= 2)
				return onChar(eol);

			return onChar(eol.charAt(0));
		});
	}

	/**
	 * 
	 * @return A {@link Predicate} testing that a character is a new line character.
	 */
	public static Predicate<Character> onEndOfLine() {
		return onEndOfLineMemo.get();
	}
}
