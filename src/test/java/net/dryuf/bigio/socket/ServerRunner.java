package net.dryuf.bigio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ServerRunner
{
	public static void main(String[] args) throws IOException
	{
		ServerSocketChannel listener = ServerSocketChannel.open();
		listener.bind(new InetSocketAddress(12345));
		for (;;) {
			try (SocketChannel server = listener.accept();
			     SocketCommittableOutputStream output = new SocketCommittableOutputStream(server)) {
				output.write("Hello\n".getBytes(StandardCharsets.UTF_8));
			}
		}
	}
}
