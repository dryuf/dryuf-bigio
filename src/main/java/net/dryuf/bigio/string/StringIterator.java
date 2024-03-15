/*
 * dryuf-bigio - Java framework for handling IO operations.
 *
 * Copyright 2015-2024 Zbynek Vyskovsky mailto:kvr000@gmail.com http://github.com/kvr000/ https://github.com/dryuf/dryuf-bigio/ https://www.linkedin.com/in/zbynek-vyskovsky/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dryuf.bigio.string;

import java.util.NoSuchElementException;


/**
 * Bidirectional Iterator of String.
 */
public class StringIterator
{
	private final CharSequence input;

	private int position;

	private final int end;

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
