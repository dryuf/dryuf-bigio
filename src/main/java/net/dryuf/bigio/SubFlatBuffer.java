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


/**
 * Implementation {@link FlatBuffer}, changing the byte order.
 */
public class SubFlatBuffer extends AbstractDelegatingFlatBuffer
{
	public SubFlatBuffer(FlatBuffer underlying, long offset, long length)
	{
		super(underlying);
		this.offset = offset;
		this.length = length;
	}

	@Override
	public void close()
	{
	}

	@Override
	public long size()
	{
		return this.length;
	}

	@Override
	public byte getByte(long pos)
	{
		checkBounds(pos, 1);
		return underlying.getByte(offset+pos);
	}

	@Override
	public short getShort(long pos)
	{
		checkBounds(pos, 2);
		return underlying.getShort(offset+pos);
	}

	@Override
	public int getInt(long pos)
	{
		checkBounds(pos, 4);
		return underlying.getInt(offset+pos);
	}

	@Override
	public long getLong(long pos)
	{
		checkBounds(pos, 8);
		return underlying.getLong(offset+pos);
	}

	@Override
	public void putByte(long pos, byte val)
	{
		checkBounds(pos, 1);
		underlying.putByte(offset+pos, val);
	}

	@Override
	public void putShort(long pos, short val)
	{
		checkBounds(pos, 2);
		underlying.putShort(offset+pos, val);
	}

	@Override
	public void putInt(long pos, int val)
	{
		checkBounds(pos, 4);
		underlying.putInt(offset+pos, val);
	}

	@Override
	public void putLong(long pos, long val)
	{
		checkBounds(pos, 8);
		underlying.putLong(offset+pos, val);
	}

	@Override
	public void getBytes(long pos, byte[] data, int offset, int length)
	{
		checkBounds(pos, length);
		underlying.getBytes(offset+pos, data, offset, length);
	}

	@Override
	public void putBytes(long pos, byte[] data, int offset, int length)
	{
		checkBounds(pos, length);
		underlying.putBytes(offset+pos, data, offset, length);
	}

	private void checkBounds(long pos, long size)
	{
		if (pos > length || pos+size > length) {
			throw new ArrayIndexOutOfBoundsException("Reading beyond buffer end: "+(pos+size));
		}
	}

	protected final long offset;

	protected final long length;
}
