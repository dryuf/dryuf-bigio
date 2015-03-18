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

package net.dryuf.bigio;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import sun.nio.ch.FileChannelImpl;
import sun.misc.Unsafe;


/**
 * Mapped file implementation of {@link FlatBuffer}.
 */
@SuppressWarnings("restriction")
public class MappedFlatBuffer extends AbstractFlatBuffer
{
	private static Method getClassMethod(Class<?> cls, String name, Class<?>... params) throws Exception
	{
		Method m = cls.getDeclaredMethod(name, params);
		m.setAccessible(true);
		return m;
	}

	public MappedFlatBuffer(FileChannel channel, FileChannel.MapMode mode, long len) throws IOException
	{
		if (len < 0) {
			len = channel.size();
		}
		this.size = len;
		long sizeArg = this.size;
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			// windows implementation has a bug as the last parameter is truncated, therefore we always map
			// whole file
			sizeArg = 0;
			this.size = channel.size();
		}
		try {
			this.address = (long) mmap.invoke(channel, translateMode(mode), 0L, sizeArg);
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public void close()
	{
		try {
			unmmap.invoke(null, this.address, this.size);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long size()
	{
		return this.size;
	}

	@Override
	public ByteOrder getByteOrder()
	{
		return ByteOrder.nativeOrder();
	}

	@Override
	public byte getByte(long pos)
	{
		checkBounds(pos, 1);
		return unsafe.getByte(pos +address);
	}

	@Override
	public short getShort(long pos)
	{
		checkBounds(pos, 2);
		return unsafe.getShort(pos +address);
	}

	@Override
	public int getInt(long pos)
	{
		checkBounds(pos, 4);
		return unsafe.getInt(pos +address);
	}

	@Override
	public long getLong(long pos)
	{
		checkBounds(pos, 8);
		return unsafe.getLong(pos +address);
	}

	@Override
	public void putByte(long pos, byte val)
	{
		checkBounds(pos, 1);
		unsafe.putShort(pos +address, val);
	}

	@Override
	public void putShort(long pos, short val)
	{
		checkBounds(pos, 2);
		unsafe.putShort(pos +address, val);
	}

	@Override
	public void putInt(long pos, int val)
	{
		checkBounds(pos, 4);
		unsafe.putInt(pos +address, val);
	}

	@Override
	public void putLong(long pos, long val)
	{
		checkBounds(pos, 8);
		unsafe.putLong(pos +address, val);
	}

	@Override
	public void getBytes(long pos, byte[] data, int offset, int length)
	{
		if ((offset|length|(offset+length)|(data.length-length-offset)) < 0) {
			if (offset < 0) {
				throw new IndexOutOfBoundsException("offset out of bounds: "+offset);
			}
			if (length < 0 || offset+length < 0 || offset+length > data.length) {
				throw new IndexOutOfBoundsException("length out of bounds: "+length);
			}
		}
		checkBounds(pos, length);
		unsafe.copyMemory(null, pos +address, data, BYTE_ARRAY_OFFSET+offset, length);
	}

	@Override
	public void putBytes(long pos, byte[] data, int offset, int length)
	{
		if ((offset|length|(offset+length)|(data.length-length-offset)) < 0) {
			if (offset < 0) {
				throw new IndexOutOfBoundsException("offset out of bounds: "+offset);
			}
			if (length < 0 || offset+length < 0 || offset+length >= data.length) {
				throw new IndexOutOfBoundsException("length out of bounds: "+length);
			}
		}
		checkBounds(pos, length);
		unsafe.copyMemory(data, BYTE_ARRAY_OFFSET, null, pos +address, data.length);
	}

	private final void checkBounds(long pos, int length)
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

	private static int translateMode(FileChannel.MapMode mode)
	{
		if (mode == FileChannel.MapMode.READ_ONLY) {
			return 0;
		}
		else if (mode == FileChannel.MapMode.READ_WRITE) {
			return 1;
		}
		throw new IllegalArgumentException("Unsupported memory map mode: "+mode);
	}

	private long address;

	private long size;

	private static final Unsafe unsafe;

	private static final Method mmap;
	private static final Method unmmap;

	private static final int BYTE_ARRAY_OFFSET;

	static
	{
		try {
			Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);
			unsafe = (Unsafe)theUnsafeField.get(null);

			mmap = getClassMethod(FileChannelImpl.class, "map0", int.class, long.class, long.class);
			unmmap = getClassMethod(FileChannelImpl.class, "unmap0", long.class, long.class);

			BYTE_ARRAY_OFFSET = unsafe.arrayBaseOffset(byte[].class);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
