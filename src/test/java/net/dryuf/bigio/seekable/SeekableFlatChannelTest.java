package net.dryuf.bigio.seekable;

import net.dryuf.bigio.FlatChannel;
import net.dryuf.bigio.FlatChannels;
import net.dryuf.bigio.file.FileChannelFlatChannel;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;


public class SeekableFlatChannelTest
{
	public static final int COUNT = 100_000;

	FlatChannel channel;

	@BeforeClass
	public void setup() throws IOException
	{
		Path file = Files.createTempFile(SeekableFlatChannelTest.class.getName(), ".txt");
		file.toFile().deleteOnExit();
		Files.write(file, "hello\nworld\n".getBytes(StandardCharsets.UTF_8));
		channel = FlatChannels.fromSeekable(FileChannel.open(file));
	}

	@Test
	public void testParallelRead()
	{
		CompletableFuture<Void> reader0 = CompletableFuture.runAsync(() -> {
			for (int i = 0; i < COUNT; ++i) {
				ByteBuffer buffer = ByteBuffer.allocate(6);
				try {
					channel.read(buffer, 0);
				}
				catch (IOException e) {
					throw new UncheckedIOException(e);
				}
				AssertJUnit.assertArrayEquals(new byte[]{ 'h', 'e', 'l', 'l', 'o', '\n' }, buffer.array());
			}
		});
		CompletableFuture<Void> reader1 = CompletableFuture.runAsync(() -> {
			for (int i = 0; i < COUNT; ++i) {
				ByteBuffer buffer = ByteBuffer.allocate(6);
				try {
					channel.read(buffer, 6);
				}
				catch (IOException e) {
					throw new UncheckedIOException(e);
				}
				AssertJUnit.assertArrayEquals(new byte[]{ 'w', 'o', 'r', 'l', 'd', '\n' }, buffer.array());
			}
		});
		reader0.join();
		reader1.join();
	}
}
