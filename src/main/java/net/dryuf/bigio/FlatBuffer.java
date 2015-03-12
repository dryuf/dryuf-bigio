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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Flat Buffer abstract. The class represents stateless (in terms of position abd length) view on sequence of bytes. All
 * positions are represented using long, i.e. the maximum area is 63 bits.
 */
public abstract class FlatBuffer implements AutoCloseable, Comparable<FlatBuffer>
{
	@Override
	public abstract void close();

	public abstract ByteOrder getByteOrder();

	public abstract FlatBuffer withByteOrder(ByteOrder byteOrder);

	public abstract long size();

	public abstract byte getByte(long pos);

	public abstract short getShort(long pos);

	public abstract int getInt(long pos);

	public abstract long getLong(long pos);

	public abstract void getBytes(long pos, byte[] data);

	public abstract void getBytes(long pos, byte[] data, int offset, int length);

	public abstract void putByte(long pos, byte val);

	public abstract void putShort(long pos, short val);

	public abstract void putInt(long pos, int val);

	public abstract void putLong(long pos, long val);

	public abstract void putBytes(long pos, byte[] data);

	public abstract void putBytes(long pos, byte[] data, int offset, int length);

	public abstract ByteBuffer getByteBuffer(long pos, long length);

	public abstract FlatBuffer subBuffer(long pos, long length);

	public abstract boolean equalsBytes(long pos, byte[] data, int offset, int length);

	public abstract boolean equalsBuffer(long pos, FlatBuffer buffer, long offset, long length);

	public abstract boolean equalsByteBuffer(long pos, ByteBuffer buffer);

	@Override
	public abstract int compareTo(FlatBuffer right);
}
