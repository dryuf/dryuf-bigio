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
public class CompositeMappedFlatBuffer extends MappedFlatBuffer
{
	public CompositeMappedFlatBuffer(FileChannel channel, FileChannel.MapMode mode, long offset, long len) throws IOException
	{
		if (len < 0) {
			len = channel.size();
		}
		if (len > (long) Integer.MAX_VALUE* BLOCK_SIZE) {
			throw new IllegalArgumentException("Too big mapping, max supported size is 2^62-2^30, provided: 0x"+Long.toHexString(len));
		}
		buffers = new ByteBuffer[(int)((len-1)/ BLOCK_SIZE +1)];
		this.size = len;
		for (int i = 0; i < buffers.length-1; ++i) {
			buffers[i] = channel.map(mode, offset+i*(long) BLOCK_SIZE, BLOCK_SIZE);
		}
		buffers[buffers.length-1] = channel.map(mode, offset+(buffers.length-1)*(long) BLOCK_SIZE, size-((buffers.length)-1)*(long) BLOCK_SIZE);
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
		for (ByteBuffer b: buffers) {
			b.order(order);
		}
		isBigEndian = order.equals(ByteOrder.BIG_ENDIAN);
		return this;
	}

	@Override
	public byte getByte(long pos)
	{
		checkSafeLengthBounds(pos, 1);
		ByteBuffer buf0 = findBuffer(pos);
		return buf0.get(localPos(pos));
	}

	@Override
	public short getShort(long pos)
	{
		checkSafeLengthBounds(pos, 2);
		ByteBuffer buf0 = findBuffer(pos);
		if (((pos+1)&(BLOCK_MASK&~1)) != 0) {
			return buf0.getShort(localPos(pos));
		}
		else {
			ByteBuffer buf1 = findBuffer(pos+1);
			return (short) (!isBigEndian ?
					(buf0.get(localPos(pos))&0xff) | buf1.get(localPos(pos+1))<<8 :
					buf0.get(localPos(pos))<<8 | buf1.get(localPos(pos+1))&0xff);
		}
	}

	@Override
	public int getInt(long pos)
	{
		checkSafeLengthBounds(pos, 4);
		if (((pos+3)&(BLOCK_MASK&~3)) != 0) {
			ByteBuffer buf0 = findBuffer(pos);
			return buf0.getInt(localPos(pos));
		}
		else {
			return (!isBigEndian ?
					(getShort(pos)&0xffff | getShort(pos+2)<<16) :
					(getShort(pos)<<16 | getShort(pos+2)&0xffff));
		}

	}

	@Override
	public long getLong(long pos)
	{
		checkSafeLengthBounds(pos, 8);
		if (((pos+7)&(BLOCK_MASK&~7)) != 0) {
			ByteBuffer buf0 = findBuffer(pos);
			return buf0.getLong(localPos(pos));
		}
		else {
			return (!isBigEndian ?
					(getInt(pos)&0xffffffffL | (long) getInt(pos+4)<<32) :
					((long) getInt(pos)<<32 | getInt(pos+4)&0xffffffffL));
		}
	}

	@Override
	public void getBytes(long pos, byte[] data, int offset, int length)
	{
		checkBounds(pos, length);
		if ((offset|(offset+length)|(data.length-length-offset)) < 0) {
			if (offset < 0) {
				throw new IndexOutOfBoundsException("offset out of bounds: "+offset);
			}
			if (length < 0 || offset+length < 0 || offset+length > data.length) {
				throw new IndexOutOfBoundsException("length out of bounds: "+length);
			}
		}
		ByteBuffer buf0 = findBuffer(pos);
		if (length == 0 || ((pos+length-1)& BLOCK_MASK) >= length-1) {
			getFromPosition(buf0, localPos(pos), data, offset, length);
		}
		else {
			ByteBuffer bufLast = findBuffer(pos+length-1);
			int i = BLOCK_SIZE -localPos(pos);
			getFromPosition(buf0, localPos(pos), data, offset, i);
			for (;;) {
				ByteBuffer bufMid = findBuffer(pos+i);
				if (bufMid == bufLast)
					break;
				getFromPosition(bufMid, 0, data, offset+i, BLOCK_SIZE);
				i += BLOCK_SIZE;
			}
			getFromPosition(bufLast, 0, data, offset+i, length-i);
		}
	}

	@Override
	public void putByte(long pos, byte val)
	{
		checkSafeLengthBounds(pos, 1);
		ByteBuffer buf0 = findBuffer(pos);
		buf0.put(localPos(pos), val);
	}

	@Override
	public void putShort(long pos, short val)
	{
		checkSafeLengthBounds(pos, 2);
		ByteBuffer buf0 = findBuffer(pos);
		if (((pos+1)&(BLOCK_MASK&~1)) != 0) {
			buf0.putShort(localPos(pos), val);
		}
		else {
			ByteBuffer buf1 = findBuffer(pos+1);
			buf0.put(localPos(pos), (byte) (!isBigEndian ? val : val>>>8));
			buf1.put(localPos(pos+1), (byte) (!isBigEndian ? val>>>8 : val));
		}
	}

	@Override
	public void putInt(long pos, int val)
	{
		checkSafeLengthBounds(pos, 4);
		if (((pos+3)&(BLOCK_MASK&~3)) != 0) {
			ByteBuffer buf0 = findBuffer(pos);
			buf0.putInt(localPos(pos), val);
		}
		else {
			putShort(pos, (short) (!isBigEndian ? val : (val>>>16)));
			putShort(pos+2, (short) (!isBigEndian ? val>>>16 : val));
		}
	}

	@Override
	public void putLong(long pos, long val)
	{
		checkSafeLengthBounds(pos, 8);
		if (((pos+7)&(BLOCK_MASK&~7)) != 0) {
			ByteBuffer buf0 = findBuffer(pos);
			buf0.putLong(localPos(pos), val);
		}
		else {
			putInt(pos, (int) (!isBigEndian ? val : (val>>>32)));
			putInt(pos+4, (int) (!isBigEndian ? val>>>32 : val));
		}
	}

	@Override
	public void putBytes(long pos, byte[] data, int offset, int length)
	{
		if ((offset|(offset+length)|(data.length-length-offset)) < 0) {
			if (offset < 0) {
				throw new IndexOutOfBoundsException("offset out of bounds: "+offset);
			}
			if (length < 0 || offset+length < 0 || offset+length >= data.length) {
				throw new IndexOutOfBoundsException("length out of bounds: "+length);
			}
		}
		checkBounds(pos, length);
		ByteBuffer buf0 = findBuffer(pos);
		if (length == 0 || ((pos+length-1)& BLOCK_MASK) >= length-1) {
			putToPosition(buf0, localPos(pos), data, offset, length);
		}
		else {
			ByteBuffer bufLast = findBuffer(pos+length-1);
			int i = BLOCK_SIZE -localPos(pos);
			putToPosition(buf0, localPos(pos), data, offset, i);
			for (;;) {
				ByteBuffer bufMid = findBuffer(pos+i);
				if (bufMid == bufLast)
					break;
				putToPosition(bufMid, 0, data, offset+i, BLOCK_SIZE);
				i += BLOCK_SIZE;
			}
			putToPosition(bufLast, 0, data, offset+i, length-i);
		}
	}

	@Override
	public boolean equalsBytes(long pos, byte[] data, int offset, int length)
	{
		ByteBuffer buf0 = findBuffer(pos);
		if (length == 0 || ((pos+length-1)& BLOCK_MASK) >= length-1) {
			return equalsAtPosition(buf0, localPos(pos), data, offset, length);
		}
		else {
			ByteBuffer bufLast = findBuffer(pos+length-1);
			int i = BLOCK_SIZE -localPos(pos);
			if (!equalsAtPosition(buf0, localPos(pos), data, offset, i))
				return false;
			for (;;) {
				ByteBuffer bufMid = findBuffer(pos+i);
				if (bufMid == bufLast)
					break;
				if (!equalsAtPosition(bufMid, 0, data, offset+i, BLOCK_SIZE))
					return false;
				i += BLOCK_SIZE;
			}
			return equalsAtPosition(bufLast, 0, data, offset+i, length-i);
		}
	}

	@Override
	public int compareBytes(long pos, byte[] data, int offset, int length)
	{
		ByteBuffer buf0 = findBuffer(pos);
		if (length == 0 || ((pos+length-1)& BLOCK_MASK) >= length-1) {
			return compareAtPosition(buf0, localPos(pos), data, offset, length);
		}
		else {
			ByteBuffer bufLast = findBuffer(pos+length-1);
			int i = BLOCK_SIZE -localPos(pos);
			int r;
			if ((r = compareAtPosition(buf0, localPos(pos), data, offset, i)) != 0)
				return r;
			for (;;) {
				ByteBuffer bufMid = findBuffer(pos+i);
				if (bufMid == bufLast)
					break;
				if ((r = compareAtPosition(bufMid, 0, data, offset+i, BLOCK_SIZE)) != 0)
					return r;
				i += BLOCK_SIZE;
			}
			return compareAtPosition(bufLast, 0, data, offset+i, length-i);
		}
	}

	private void checkSafeLengthBounds(long pos, int length)
	{
		if ((pos|(this.size-length-pos)) < 0) {
			if (pos < 0) {
				throw new IndexOutOfBoundsException("position out of bounds: "+pos);
			}
			if (length < 0 || pos+length < 0 || pos+length > this.size) {
				throw new IndexOutOfBoundsException("length out of bounds: "+length);
			}
		}
	}

	private void checkBounds(long pos, int length)
	{
		if ((pos|length|(pos+length)|(this.size-length-pos)) < 0) {
			if (pos < 0) {
				throw new IndexOutOfBoundsException("position out of bounds: "+pos);
			}
			if (length < 0 || pos+length < 0 || pos+length > this.size) {
				throw new IndexOutOfBoundsException("length out of bounds: "+length);
			}
		}
	}

	private ByteBuffer findBuffer(long pos)
	{
		return buffers[(int)(pos/ BLOCK_SIZE)];
	}

	private static int localPos(long pos)
	{
		return (int) pos & BLOCK_MASK;
	}

	private static void getFromPosition(ByteBuffer buf, int pos, byte[] data, int offset, int length)
	{
		ByteBuffer dup = buf.duplicate();
		dup.position(pos);
		dup.get(data, offset, length);
	}

	private static void putToPosition(ByteBuffer buf, int pos, byte[] data, int offset, int length)
	{
		ByteBuffer dup = buf.duplicate();
		dup.position(pos);
		dup.put(data, offset, length);
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

	static final int BLOCK_SIZE = 1024*1024*1024;
	static final int BLOCK_MASK = BLOCK_SIZE -1;

	private final long size;

	private boolean isBigEndian = true;

	private ByteBuffer buffers[];

	private boolean isClosed = false;
}
