/*
 * Copyright 2015 Zbynek Vyskovsky mailto:kvr000@gmail.com http://kvr.znj.cz/ http://github.com/kvr000/
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

package net.dryuf.bigio.iostream;

import lombok.NonNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;


/**
 * {@link InputStream} wrapper, providing {@link InputStream} for each line.
 */
public class LinedInputMultiStream implements MultiStream
{
	public static final byte NEW_LINE_BYTE = '\n';

	public static final int DEFAULT_CACHE_SIZE = 4096;

	public LinedInputMultiStream(@NonNull InputStream underlyingStream)
	{
		this(underlyingStream, DEFAULT_CACHE_SIZE);
	}

	public LinedInputMultiStream(@NonNull InputStream underlyingStream, int cacheSize)
	{
		this.underlyingStream = underlyingStream;
		this.cache = new byte[cacheSize];
	}

	@Override
	public void close() throws IOException
	{
		this.underlyingStream.close();
	}

	@Override
	@Nullable
	public InputStream nextStream() throws IOException
	{
		if (unfinishedLine) {
			for (;;) {
				int c = readNextByte();
				if (c < 0) {
					return null;
				}
				if (c == '\n') {
					break;
				}
			}
		}
		int c = cacheNextByte();
		if (c < 0) {
			return null;
		}
		unfinishedLine = true;
		++lineNumber;
		return new InputStream()
		{
			@Override
			public int read() throws IOException
			{
				if (myLineNumber != lineNumber) {
					throw new IOException("Reading from stream while already moved to next line");
				}
				else if (!unfinishedLine) {
					return -1;
				}
				int c = readNextByte();
				if (c < 0) {
					return -1;
				}
				if (c == NEW_LINE_BYTE) {
					unfinishedLine = false;
					return -1;
				}
				return c;
			}

			@Override
			public int read(byte[] buf, int pos, int len) throws IOException
			{
				if (myLineNumber != lineNumber) {
					throw new IOException("Reading from stream while already moved to next line");
				}
				else if (!unfinishedLine) {
					return -1;
				}
				int read = readNextArray(buf, pos, len);
				if (read < 0) {
					return read;
				}
				return read;
			}

			private int readNextArray(byte[] buf, int pos, int len) throws IOException
			{
				if (cachePos < cacheEnd) {
					int toRead = Math.min(cacheEnd-cachePos, len);
					for (int i = 0; i < toRead; ++i) {
						if (cache[cachePos+i] == NEW_LINE_BYTE) {
							toRead = i;
							unfinishedLine = false;
							break;
						}
					}
					System.arraycopy(cache, cachePos, buf, pos, toRead);
					cachePos += toRead+(unfinishedLine ? 0 : 1);
					return toRead == 0 ? -1 : toRead;
				}
				else if (cacheEnd < 0) {
					return cacheEnd;
				}
				else {
					int read = underlyingStream.read(buf, pos, Math.min(len, cache.length));
					if (read < 0) {
						cacheEnd = read;
						return read;
					}
					else {
						for (int i = 0; i < read; ++i) {
							if (buf[pos+i] == NEW_LINE_BYTE) {
								System.arraycopy(buf, pos+i+1, cache, 0, read-i-1);
								cachePos = 0;
								cacheEnd = read-i-1;
								unfinishedLine = false;
								return i == 0 ? -1 : i;
							}
						}
						return read;
					}
				}
			}

			private long myLineNumber = lineNumber;
		};
	}

	private int cacheNextByte() throws IOException
	{
		if (cachePos >= cacheEnd) {
			cachePos = 0;
			cacheEnd = underlyingStream.read(cache, 0, cache.length);
			if (cacheEnd < 0) {
				return cacheEnd;
			}
		}
		else if (cacheEnd < 0) {
			return cacheEnd;
		}
		return cache[cachePos]&0xff;
	}

	private int readNextByte() throws IOException
	{
		if (cachePos == cacheEnd) {
			cachePos = 0;
			cacheEnd = underlyingStream.read(cache, 0, cache.length);
			if (cacheEnd < 0) {
				return cacheEnd;
			}
		}
		else if (cacheEnd < 0) {
			return cacheEnd;
		}
		return cache[cachePos++]&0xff;
	}

	private final InputStream underlyingStream;

	private final byte[] cache;

	private int cachePos = 0;

	private int cacheEnd = 0;

	private boolean unfinishedLine = false;

	private long lineNumber = 0;
}
