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

package net.dryuf.bigio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Partial implementation of {@link FlatBuffer}.
 */
public abstract class AbstractFlatBuffer implements FlatBuffer
{
	@Override
	public void close()
	{
	}

	@Override
	public void getBytes(long pos, byte[] data)
	{
		getBytes(pos, data, 0, data.length);
	}

	@Override
	public void putBytes(long pos, byte[] data)
	{
		putBytes(pos, data, 0, data.length);
	}

	@Override
	public FlatBuffer subBuffer(long pos, long length)
	{
		return new SubFlatBuffer(this, pos, length);
	}

	@Override
	public boolean equalsBytes(long pos, byte[] bytes, int offset, int length)
	{
		return compareBytes(pos, bytes, offset, length) == 0;
	}

	@Override
	public boolean equalsBuffer(long pos, FlatBuffer buffer, long offset, long length)
	{
		for (long i = 0; i < length; ++i) {
			byte bl = this.getByte(pos+i), br = buffer.getByte(offset+i);
			if (bl != br)
				return false;
		}
		return true;
	}

	@Override
	public boolean equalsByteBuffer(long pos, ByteBuffer buffer)
	{
		return compareByteBuffer(pos, buffer) == 0;
	}

	@Override
	public int compareBytes(long pos, byte[] bytes, int offset, int length)
	{
		for (int i = 0; i < length; ++i) {
			byte bl = this.getByte(pos+i), br = bytes[offset+i];
			if (bl != br)
				return Byte.compare(bl, br);
		}
		return 0;
	}

	@Override
	public int compareByteBuffer(long pos, ByteBuffer buffer)
	{
		for (int i = 0, offset = buffer.position(), length = buffer.remaining(); i < length; ++i) {
			byte bl = this.getByte(pos+i), br = buffer.get(offset+i);
			if (bl != br)
				return Byte.compare(bl, br);
		}
		return 0;
	}

	@Override
	public int compareTo(FlatBuffer right)
	{
		long sl = this.size(), sr = right.size();
		for (long i = 0, size = Math.max(sl, sr); i < size; ++i) {
			byte bl = this.getByte(i), br = right.getByte(i);
			if (bl != br)
				return (bl&0xff)-(br&0xff);
		}
		return sl < sr ? -1 : sr > 0 ? 1 : 0;
	}
}
