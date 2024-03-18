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

package net.dryuf.bigio.file;

import net.dryuf.bigio.MappedFlatBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;


/**
 * {@link MappedFlatBuffer} based on series of {@link java.nio.MappedByteBuffer}.
 */
public class SmallMappedFlatBuffer extends MappedFlatBuffer
{
	public SmallMappedFlatBuffer(FileChannel channel, FileChannel.MapMode mode, long offset, long len) throws IOException
	{
		if (len < 0) {
			len = channel.size();
		}
		if (len > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Too big mapping, max supported size is 2^31-1, provided: 0x"+Long.toHexString(len));
		}
		this.size = (int) len;
		buffer = channel.map(mode, offset, size);
	}

	public SmallMappedFlatBuffer(ByteBuffer buffer) throws IOException
	{
		this.size = buffer.limit();
		this.buffer = buffer;
		this.isBigEndian = this.buffer.order() == ByteOrder.BIG_ENDIAN;
	}

	@Override
	public synchronized void close()
	{
		if (isClosed)
			return;
		isClosed = true;
	}

	@Override
	public long size()
	{
		return this.size;
	}

	@Override
	public ByteOrder getByteOrder()
	{
		return isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
	}

	@Override
	public MappedFlatBuffer order(ByteOrder order)
	{
		buffer.order(order);
		isBigEndian = order.equals(ByteOrder.BIG_ENDIAN);
		return this;
	}

	@Override
	public byte getByte(long pos)
	{
		return buffer.get(localPos(pos));
	}

	@Override
	public short getShort(long pos)
	{
		return buffer.getShort(localPos(pos));
	}

	@Override
	public int getInt(long pos)
	{
		return buffer.getInt(localPos(pos));
	}

	@Override
	public long getLong(long pos)
	{
		return buffer.getLong(localPos(pos));
	}

	@Override
	public void getBytes(long pos, byte[] data, int offset, int length)
	{
		getFromPosition(buffer, localPos(pos), data, offset, length);
	}

	@Override
	public void getByteBuffer(long pos, ByteBuffer bufferRead)
	{
		if (bufferRead.hasArray()) {
			getBytes(pos, bufferRead.array(), bufferRead.arrayOffset() + bufferRead.position(), bufferRead.limit() - bufferRead.position());
		}
		else {
			for (int r = bufferRead.limit() - bufferRead.position(); r > 0; --r) {
				bufferRead.put(getByte(pos + r));
			}
		}
	}

	@Override
	public ByteBuffer subByteBuffer(long pos, long length)
	{
		return buffer.slice(Math.toIntExact(pos), Math.toIntExact(length))
			.order(buffer.order());
	}

	@Override
	public void putByteBuffer(long pos, ByteBuffer buffer)
	{
		buffer.put(Math.toIntExact(pos), buffer, buffer.position(), buffer.remaining());
	}

	@Override
	public void putByte(long pos, byte val)
	{
		buffer.put(localPos(pos), val);
	}

	@Override
	public void putShort(long pos, short val)
	{
		buffer.putShort(localPos(pos), val);
	}

	@Override
	public void putInt(long pos, int val)
	{
		buffer.putInt(localPos(pos), val);
	}

	@Override
	public void putLong(long pos, long val)
	{
		buffer.putLong(localPos(pos), val);
	}

	@Override
	public void putBytes(long pos, byte[] data, int offset, int length)
	{
		putToPosition(buffer, localPos(pos), data, offset, length);
	}

	@Override
	public boolean equalsBytes(long pos, byte[] data, int offset, int length)
	{
		return equalsAtPosition(buffer, localPos(pos), data, offset, length);
	}

	@Override
	public int compareBytes(long pos, byte[] data, int offset, int length)
	{
		return compareAtPosition(buffer, localPos(pos), data, offset, length);
	}

	private int localPos(long pos)
	{
		if (pos < 0 || pos >= size) {
			throw new IllegalArgumentException("pos behind size: pos=" + pos);
		}
		return (int) pos;
	}

	private static void getFromPosition(ByteBuffer buf, int pos, byte[] data, int offset, int length)
	{
		buf.get(pos, data, offset, length);
	}

	private static void putToPosition(ByteBuffer buf, int pos, byte[] data, int offset, int length)
	{
		buf.put(pos, data, offset, length);
	}

	private static boolean equalsAtPosition(ByteBuffer buf, int pos, byte[] data, int offset, int length)
	{
		ByteBuffer dup = buf.duplicate();
		dup.position(pos);
		dup.limit(pos+length);
		return dup.equals(ByteBuffer.wrap(data, offset, length));
	}

	private static int compareAtPosition(ByteBuffer buf, int pos, byte[] data, int offset, int length)
	{
		ByteBuffer dup = buf.duplicate();
		dup.position(pos);
		dup.limit(pos+length);
		return dup.compareTo(ByteBuffer.wrap(data, offset, length));
	}

	private final int size;

	private boolean isBigEndian = true;

	private ByteBuffer buffer;

	private boolean isClosed = false;
}
