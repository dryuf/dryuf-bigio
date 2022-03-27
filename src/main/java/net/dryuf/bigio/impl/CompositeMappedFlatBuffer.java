package net.dryuf.bigio.impl;

import net.dryuf.bigio.MappedFlatBuffer;

import java.io.IOException;
import java.io.UncheckedIOException;
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
		if (len > (long) Integer.MAX_VALUE*ONE_SIZE) {
			throw new IllegalArgumentException("Too big mapping, max supported size is 2^62-2^30, provided: 0x"+Long.toHexString(len));
		}
		buffers = new ByteBuffer[(int)((len-1)/ONE_SIZE+1)];
		this.size = len;
		try {
			for (int i = 0; i < buffers.length-1; ++i) {
				buffers[i] = channel.map(mode, offset+i*(long) ONE_SIZE, ONE_SIZE);
			}
			buffers[buffers.length-1] = channel.map(mode, offset+(buffers.length-1)*(long) ONE_SIZE, size-((buffers.length)-1)*(long) ONE_SIZE);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
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
		return ByteOrder.BIG_ENDIAN;
	}

	@Override
	public MappedFlatBuffer order(ByteOrder order)
	{
		for (ByteBuffer b: buffers) {
			b.order(order);
		}
		isLittleEndian = order.equals(ByteOrder.LITTLE_ENDIAN);
		return this;
	}

	@Override
	public byte getByte(long pos)
	{
		checkShortBounds(pos, 1);
		ByteBuffer buf0 = findBuffer(pos);
		return buf0.get(localPos(pos));
	}

	@Override
	public short getShort(long pos)
	{
		checkShortBounds(pos, 2);
		ByteBuffer buf0 = findBuffer(pos);
		if (((pos+1)&ONE_MASK) >= 1) {
			return buf0.getShort(localPos(pos));
		}
		else {
			ByteBuffer buf1 = findBuffer(pos+1);
			return (short) (isLittleEndian ?
					(buf0.get(localPos(pos))&0xff) | buf1.get(localPos(pos+1))<<8 :
					buf0.get(localPos(pos))<<8 | buf1.get(localPos(pos+1))&0xff);
		}
	}

	@Override
	public int getInt(long pos)
	{
		checkShortBounds(pos, 4);
		if (((pos+3)&ONE_MASK) >= 3) {
			ByteBuffer buf0 = findBuffer(pos);
			return buf0.getInt(localPos(pos));
		}
		else {
			return (isLittleEndian ?
					(getShort(pos)&0xffff | getShort(pos+2)<<16) :
					(getShort(pos)<<16 | getShort(pos+2)&0xffff));
		}

	}

	@Override
	public long getLong(long pos)
	{
		checkShortBounds(pos, 8);
		if (((pos+3)&ONE_MASK) >= 7) {
			ByteBuffer buf0 = findBuffer(pos);
			return buf0.getLong(localPos(pos));
		}
		else {
			return (isLittleEndian ?
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
		if (length == 0 || ((pos+length-1)&ONE_MASK) >= length-1) {
			getFromPosition(buf0, localPos(pos), data, offset, length);
		}
		else {
			ByteBuffer bufLast = findBuffer(pos+length-1);
			int i = ONE_SIZE-localPos(pos);
			getFromPosition(buf0, localPos(pos), data, offset, i);
			for (;;) {
				ByteBuffer bufMid = findBuffer(pos+i);
				if (bufMid == bufLast)
					break;
				getFromPosition(bufMid, 0, data, offset+i, ONE_SIZE);
				i += ONE_SIZE;
			}
			getFromPosition(bufLast, 0, data, offset+i, length-i);
		}
	}

	@Override
	public void putByte(long pos, byte val)
	{
		checkShortBounds(pos, 1);
		ByteBuffer buf0 = findBuffer(pos);
		buf0.put(localPos(pos), val);
	}

	@Override
	public void putShort(long pos, short val)
	{
		checkShortBounds(pos, 2);
		ByteBuffer buf0 = findBuffer(pos);
		if (((pos+1)&ONE_MASK) >= 1) {
			buf0.putShort(localPos(pos), val);
		}
		else {
			ByteBuffer buf1 = findBuffer(pos+1);
			buf0.put(localPos(pos), (byte) (isLittleEndian ? val : val>>>8));
			buf1.put(localPos(pos+1), (byte) (isLittleEndian ? val>>>8 : val));
		}
	}

	@Override
	public void putInt(long pos, int val)
	{
		checkShortBounds(pos, 4);
		if (((pos+3)&ONE_MASK) >= 3) {
			ByteBuffer buf0 = findBuffer(pos);
			buf0.putInt(localPos(pos), val);
		}
		else {
			putShort(pos, (short) (isLittleEndian ? val : val>>>16));
			putShort(pos+2, (short) (isLittleEndian ? val>>>16 : val));
		}
	}

	@Override
	public void putLong(long pos, long val)
	{
		checkShortBounds(pos, 8);
		if (((pos+7)&ONE_MASK) >= 7) {
			ByteBuffer buf0 = findBuffer(pos);
			buf0.putLong(localPos(pos), val);
		}
		else {
			putInt(pos, (int) (isLittleEndian ? val : val>>>32));
			putInt(pos+4, (int) (isLittleEndian ? val>>>32 : val));
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
		if (length == 0 || ((pos+length-1)&ONE_MASK) >= length-1) {
			putToPosition(buf0, localPos(pos), data, offset, length);
		}
		else {
			ByteBuffer bufLast = findBuffer(pos+length-1);
			int i = ONE_SIZE-localPos(pos);
			putToPosition(buf0, localPos(pos), data, offset, i);
			for (;;) {
				ByteBuffer bufMid = findBuffer(pos+i);
				if (bufMid == bufLast)
					break;
				putToPosition(bufMid, 0, data, offset+i, ONE_SIZE);
				i += ONE_SIZE;
			}
			putToPosition(bufLast, 0, data, offset+i, length-i);
		}
	}

	@Override
	public boolean equalsBytes(long pos, byte[] data, int offset, int length)
	{
		ByteBuffer buf0 = findBuffer(pos);
		if (length == 0 || ((pos+length-1)&ONE_MASK) >= length-1) {
			return equalsAtPosition(buf0, localPos(pos), data, offset, length);
		}
		else {
			ByteBuffer bufLast = findBuffer(pos+length-1);
			int i = ONE_SIZE-localPos(pos);
			if (!equalsAtPosition(buf0, localPos(pos), data, offset, i))
				return false;
			for (;;) {
				ByteBuffer bufMid = findBuffer(pos+i);
				if (bufMid == bufLast)
					break;
				if (!equalsAtPosition(bufMid, 0, data, offset+i, ONE_SIZE))
					return false;
				i += ONE_SIZE;
			}
			return equalsAtPosition(bufLast, 0, data, offset+i, length-i);
		}
	}

	@Override
	public int compareBytes(long pos, byte[] data, int offset, int length)
	{
		ByteBuffer buf0 = findBuffer(pos);
		if (length == 0 || ((pos+length-1)&ONE_MASK) >= length-1) {
			return compareAtPosition(buf0, localPos(pos), data, offset, length);
		}
		else {
			ByteBuffer bufLast = findBuffer(pos+length-1);
			int i = ONE_SIZE-localPos(pos);
			int r;
			if ((r = compareAtPosition(buf0, localPos(pos), data, offset, i)) != 0)
				return r;
			for (;;) {
				ByteBuffer bufMid = findBuffer(pos+i);
				if (bufMid == bufLast)
					break;
				if ((r = compareAtPosition(bufMid, 0, data, offset+i, ONE_SIZE)) != 0)
					return r;
				i += ONE_SIZE;
			}
			return compareAtPosition(bufLast, 0, data, offset+i, length-i);
		}
	}

	private void checkShortBounds(long pos, int length)
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
		return buffers[(int)(pos/ONE_SIZE)];
	}

	private static int localPos(long pos)
	{
		return (int) pos & ONE_MASK;
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

	static final int ONE_SIZE = 1024*1024*1024;
	static final int ONE_MASK = ONE_SIZE-1;

	private final long size;

	private boolean isLittleEndian = false;

	private ByteBuffer buffers[];

	private boolean isClosed = false;
}
