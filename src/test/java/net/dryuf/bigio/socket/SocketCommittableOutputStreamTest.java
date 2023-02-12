package net.dryuf.bigio.socket;

import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SocketCommittableOutputStreamTest
{
	ExecutorService executor = Executors.newCachedThreadPool();

	@Test(timeOut = 10_000L)
	public void testNoCommit() throws IOException
	{
		ServerSocketChannel listener = ServerSocketChannel.open();
		listener.bind(new InetSocketAddress("localhost", 0));
		CompletableFuture<Void> serverFuture = CompletableFuture.runAsync(() -> {
			try (SocketChannel server = listener.accept();
			     SocketCommittableOutputStream output = new SocketCommittableOutputStream(server)) {
				output.write("Hello\n".getBytes(StandardCharsets.UTF_8));
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, executor);
		Socket client = new Socket(((InetSocketAddress) listener.getLocalAddress()).getAddress(), ((InetSocketAddress) listener.getLocalAddress()).getPort());
		byte[] response = new byte[6];
		client.getInputStream().read(response);
		try {
			client.getInputStream().read(response);
		}
		catch (IOException ex) {
			// this may end up in Connection Reset By Peer, but not guaranteed
		}
		serverFuture.join();
	}


	@Test(timeOut = 10_000L)
	public void testCommit() throws IOException
	{
		ServerSocketChannel listener = ServerSocketChannel.open();
		listener.bind(new InetSocketAddress("localhost", 0));
		CompletableFuture<Void> serverFuture = CompletableFuture.runAsync(() -> {
			try (SocketChannel server = listener.accept();
			     SocketCommittableOutputStream output = new SocketCommittableOutputStream(server)) {
				output.write("Hello\n".getBytes(StandardCharsets.UTF_8));
				output.committable(true);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, executor);
		Socket client = new Socket(((InetSocketAddress) listener.getLocalAddress()).getAddress(), ((InetSocketAddress) listener.getLocalAddress()).getPort());
		byte[] response = new byte[6];
		client.getInputStream().read(response);
		client.getInputStream().read(response);
		serverFuture.join();
	}

	@Test(timeOut = 10_000L)
	public void testNonBlocking() throws IOException, InterruptedException
	{
		int count = 100_000;
		byte[] payload = new byte[10_000];
		Arrays.fill(payload, (byte) 'C');
		ServerSocketChannel listener = ServerSocketChannel.open();
		listener.bind(new InetSocketAddress("localhost", 0));
		CompletableFuture<Void> serverFuture = CompletableFuture.runAsync(() -> {
			try (SocketChannel server = listener.accept();
			     SocketCommittableOutputStream output = new SocketCommittableOutputStream(server)) {
				server.configureBlocking(false);
				for (int i = 0; i < count; ++i) {
					output.write(payload);
				}
				output.committable(true);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, executor);
		Socket client = new Socket(((InetSocketAddress) listener.getLocalAddress()).getAddress(), ((InetSocketAddress) listener.getLocalAddress()).getPort());
		byte[] response = new byte[1_000_000];
		Thread.sleep(100);
		for (int remaining = count*payload.length; remaining > 0; ) {
			remaining -= client.getInputStream().read(response);
		}
		serverFuture.join();
	}
}
