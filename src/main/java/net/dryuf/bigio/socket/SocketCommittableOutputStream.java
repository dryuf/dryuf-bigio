package net.dryuf.bigio.socket;

import net.dryuf.bigio.iostream.CommittableOutputStream;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


/**
 * Socket based {@link java.io.OutputStream} , shutting down the output in case it was marked as committed.
 */
public class SocketCommittableOutputStream extends CommittableOutputStream
{
	private final SocketChannel socket;

	private boolean committable = false;

	public SocketCommittableOutputStream(SocketChannel socket) throws IOException
	{
		this.socket = socket;
	}

	@Override
	public void committable(boolean committable)
	{
		this.committable = committable;
	}

	@Override
	public void write(int i) throws IOException
	{
		write(new byte[]{ (byte) i });
	}

	@Override
	public void write(@Nonnull byte[] data, int offset, int length) throws IOException
	{
		ByteBuffer buffer = ByteBuffer.wrap(data, offset, length);
		for (;;) {
			if (buffer.remaining() == 0) {
				return;
			}
			if (socket.write(buffer) <= 0)
				break;
		}
		try (Selector selector = Selector.open()) {
			socket.register(selector, SelectionKey.OP_WRITE);
			selector.select();
			do {
				if (socket.write(buffer) <= 0) {
					selector.select();
				}
			} while (buffer.remaining() > 0);
		}
	}

	@Override
	public void close() throws IOException
	{
		if (committable) {
			socket.shutdownOutput();
		}
	}
}
