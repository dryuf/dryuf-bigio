package net.dryuf.bigio.memory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.dryuf.bigio.FlatChannel;

import java.io.IOException;
import java.nio.ByteBuffer;


@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BytesFlatChannel implements FlatChannel
{
	public static BytesFlatChannel from(byte[] bytes)
	{
		return new BytesFlatChannel(bytes);
	}

	@Override
	public int read(ByteBuffer buffer, long position) throws IOException
	{
		if (position < 0 || position > bytes.length) {
			throw new IndexOutOfBoundsException("Position outside of channel boundaries: "+position);
		}
		int length = Math.min(buffer.remaining(), bytes.length-(int)position);
		buffer.put(bytes, (int) position, length);
		return length;
	}

	@Override
	public int write(ByteBuffer buffer, long position) throws IOException
	{
		if (position < 0 || position > bytes.length) {
			throw new IndexOutOfBoundsException("Position outside of channel boundaries: "+position);
		}
		int length = Math.min(buffer.remaining(), bytes.length-(int)position);
		buffer.get(bytes, (int) position, length);
		return length;
	}

	private byte[] bytes;
}
