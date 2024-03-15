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

import java.nio.ByteOrder;


/**
 * Implementation {@link FlatBuffer}, changing the byte order.
 */
public class SwappedBytesFlatBuffer extends AbstractDelegatingFlatBuffer
{
	public SwappedBytesFlatBuffer(FlatBuffer underlying)
	{
		super(underlying);
	}

	@Override
	public ByteOrder getByteOrder()
	{
		return underlying.getByteOrder() == ByteOrder.LITTLE_ENDIAN ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
	}

	@Override
	public short getShort(long pos)
	{
		return Short.reverseBytes(underlying.getShort(pos));
	}

	@Override
	public int getInt(long pos)
	{
		return Integer.reverseBytes(underlying.getInt(pos));
	}

	@Override
	public long getLong(long pos)
	{
		return Long.reverseBytes(underlying.getLong(pos));
	}

	@Override
	public void putShort(long pos, short val)
	{
		underlying.putShort(pos, Short.reverseBytes(val));
	}

	@Override
	public void putInt(long pos, int val)
	{
		underlying.putInt(pos, Integer.reverseBytes(val));
	}

	@Override
	public void putLong(long pos, long val)
	{
		underlying.putLong(pos, Long.reverseBytes(val));
	}
}
