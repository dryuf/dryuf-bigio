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
		try {
			if (committable) {
				socket.shutdownOutput();
			}
		}
		finally {
			socket.close();
		}
	}
}
