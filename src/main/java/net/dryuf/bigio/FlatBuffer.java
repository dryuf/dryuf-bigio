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
 * Flat Buffer abstract. The class represents stateless (in terms of position and length) view on sequence of bytes. All
 * positions are represented using long, i.e. the maximum area is 63 bits.
 */
public interface FlatBuffer extends AutoCloseable, Comparable<FlatBuffer>
{
	@Override
	public void close();

	public ByteOrder getByteOrder();

	public FlatBuffer order(ByteOrder byteOrder);

	public long size();

	public byte getByte(long pos);

	public short getShort(long pos);

	public int getInt(long pos);

	public long getLong(long pos);

	public void getBytes(long pos, byte[] data);

	public void getBytes(long pos, byte[] data, int offset, int length);

	/**
	 * Gets a ByteBuffer at given position and length.  The ByteBuffer may or may not be zero-copy of original
	 * storage depending on implementation.  It must NOT be used for modifications.
	 *
	 * @param pos
	 *      position in this buffer
	 *
	 * @return
	 *      ByteBuffer representing the bytes at given position and length.
	 */
	public void getByteBuffer(long pos, ByteBuffer buffer);

	public void putByte(long pos, byte val);

	public void putShort(long pos, short val);

	public void putInt(long pos, int val);

	public void putLong(long pos, long val);

	public void putBytes(long pos, byte[] data);

	public void putBytes(long pos, byte[] data, int offset, int length);

	/**
	 * Writes a ByteBuffer at given position and length.
	 *
	 * @param pos
	 *      position in this buffer
	 * @param buffer
	 *      buffer to write
	 */
	public void putByteBuffer(long pos, ByteBuffer buffer);

	public FlatBuffer subBuffer(long pos, long length);

	/**
	 * Gets a ByteBuffer at given position and length.  The ByteBuffer may or may not be zero-copy of original
	 * storage depending on implementation.  It must NOT be used for modifications.
	 *
	 * @param pos
	 *      position in this buffer
	 * @param length
	 *      length of returned ByteBuffer
	 *
	 * @return
	 *      ByteBuffer representing the bytes at given position and length.
	 */
	public ByteBuffer subByteBuffer(long pos, long length);

	public boolean equalsBytes(long pos, byte[] data, int offset, int length);

	public boolean equalsBuffer(long pos, FlatBuffer buffer, long offset, long length);

	public boolean equalsByteBuffer(long pos, ByteBuffer buffer);

	public int compareBytes(long pos, byte[] data, int offset, int length);

	public int compareByteBuffer(long pos, ByteBuffer buffer);

	@Override
	public abstract int compareTo(FlatBuffer right);
}
