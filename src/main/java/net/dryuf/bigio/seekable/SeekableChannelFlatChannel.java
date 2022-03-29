package net.dryuf.bigio.seekable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.dryuf.bigio.FlatChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SeekableChannelFlatChannel implements FlatChannel
{
	public static SeekableChannelFlatChannel from(SeekableByteChannel channel)
	{
		return new SeekableChannelFlatChannel(channel);
	}

	@Override
	public int read(ByteBuffer buffer, long position) throws IOException
	{
		synchronized (seekableChannel) {
			seekableChannel.position(position);
			return seekableChannel.read(buffer);
		}
	}

	@Override
	public int write(ByteBuffer buffer, long position) throws IOException
	{
		synchronized (seekableChannel) {
			seekableChannel.position(position);
			return seekableChannel.write(buffer);
		}
	}

	private final SeekableByteChannel seekableChannel;
}
