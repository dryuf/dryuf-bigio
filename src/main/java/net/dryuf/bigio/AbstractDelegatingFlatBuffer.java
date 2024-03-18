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
 * Implementation {@link FlatBuffer}, changing the byte order.
 */
public class AbstractDelegatingFlatBuffer extends AbstractFlatBuffer
{
	public AbstractDelegatingFlatBuffer(FlatBuffer underlying)
	{
		this.underlying = underlying;
	}

	@Override
	public void close()
	{
		underlying.close();
	}

	@Override
	public FlatBuffer order(ByteOrder order)
	{
		return underlying.order(order);
	}

	@Override
	public ByteOrder getByteOrder()
	{
		return underlying.getByteOrder();
	}

	@Override
	public long size()
	{
		return underlying.size();
	}

	@Override
	public byte getByte(long pos)
	{
		return underlying.getByte(pos);
	}

	@Override
	public short getShort(long pos)
	{
		return underlying.getShort(pos);
	}

	@Override
	public int getInt(long pos)
	{
		return underlying.getInt(pos);
	}

	@Override
	public long getLong(long pos)
	{
		return underlying.getLong(pos);
	}

	@Override
	public void putByte(long pos, byte val)
	{
		underlying.putByte(pos, val);
	}

	@Override
	public void putShort(long pos, short val)
	{
		underlying.putShort(pos, val);
	}

	@Override
	public void putInt(long pos, int val)
	{
		underlying.putInt(pos, val);
	}

	@Override
	public void putLong(long pos, long val)
	{
		underlying.putLong(pos, val);
	}

	@Override
	public void getBytes(long pos, byte[] data, int offset, int length)
	{
		underlying.getBytes(pos, data, offset, length);
	}

	@Override
	public void getByteBuffer(long pos, ByteBuffer buffer)
	{
		underlying.getByteBuffer(pos, buffer);
	}

	@Override
	public ByteBuffer subByteBuffer(long pos, long length)
	{
		return underlying.subByteBuffer(pos, length);
	}

	@Override
	public void putByteBuffer(long pos, ByteBuffer buffer)
	{
		underlying.putByteBuffer(pos, buffer);
	}

	@Override
	public void putBytes(long pos, byte[] data, int offset, int length)
	{
		underlying.putBytes(pos, data, offset, length);
	}

	protected final FlatBuffer underlying;
}
