package net.dryuf.bigio.string;

import java.util.NoSuchElementException;


public class StringIterator
{
	private final CharSequence input;

	private int position;

	private int end;

	public StringIterator(CharSequence input)
	{
		this(input, 0, input.length());
	}

	public static StringIterator fromOffsetEnd(CharSequence input, int offset, int end)
	{
		return new StringIterator(input, offset, end);
	}

	public static StringIterator fromOffsetLength(CharSequence input, int offset, int length)
	{
		return new StringIterator(input, offset, offset + length);
	}

	protected StringIterator(CharSequence input, int offset, int end)
	{
		if (offset < 0) {
			throw new IllegalArgumentException("offset must be positive: offset=" + offset);
		} else if (end < 0 || end > input.length()) {
			throw new IllegalArgumentException("end not within input boundaries: end=" + end + " input.length=" + input.length());
		}
		this.input = input;
		this.position = offset;
		this.end = end;
	}

	public boolean hasNext()
	{
		return position < end;
	}

	public char next()
	{
		if (position >= end) {
			throw new NoSuchElementException("Iterating behind end: position=" + position);
		}
		return input.charAt(position++);
	}

	public void back()
	{
		if (position <= 0) {
			throw new NoSuchElementException("Iterating before beginning: position=" + position);
		}
		--position;
	}
}
