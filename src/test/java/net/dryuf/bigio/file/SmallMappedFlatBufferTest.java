package net.dryuf.bigio.file;

import net.dryuf.bigio.MappedFlatBuffer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.internal.junit.ArrayAsserts;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;


public class SmallMappedFlatBufferTest
{
	@BeforeClass
	public void setup() throws IOException
	{
		testfile = Files.createTempFile("testfile", ".bin");
		testfile.toFile().deleteOnExit();
		channel = FileChannel.open(testfile, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.SPARSE);
		{
			byte[] pattern = new byte[512];
			for (int i = 0; i < 256; ++i) {
				pattern[i] = (byte) i;
			}
			channel.write(ByteBuffer.wrap(pattern));
		}

		buffer = new SmallMappedFlatBuffer(channel, FileChannel.MapMode.READ_WRITE, 0, -1);
		leBuffer = new SmallMappedFlatBuffer(channel, FileChannel.MapMode.READ_WRITE, 0, -1).order(ByteOrder.LITTLE_ENDIAN);
	}

	@AfterClass
	public void teardown() throws IOException
	{
		leBuffer.close();
		buffer.close();
		channel.close();
		Files.delete(testfile);
	}

	@Test
	public void testReadByte()
	{
		byte result = buffer.getByte(7);
		Assert.assertEquals(result, 7);
	}

	@Test
	public void testReadShort()
	{
		short result = buffer.getShort(7);
		Assert.assertEquals(result, (short) 0x0708);
	}

	@Test
	public void testReadInt()
	{
		int result = buffer.getInt(7);
		Assert.assertEquals(result, (int) 0x0708090a);
	}

	@Test
	public void testReadLong()
	{
		long result = buffer.getLong(7);
		Assert.assertEquals(result, 0x0708090a0b0c0d0eL);
	}

	@Test
	public void testReadBytes()
	{
		byte[] result = new byte[8];
		buffer.getBytes(7, result);
		ArrayAsserts.assertArrayEquals(new byte[]{ 7, 8, 9, 10, 11, 12, 13, 14 }, result);
	}

	@Test
	public void testLeReadByte()
	{
		byte result = leBuffer.getByte(7);
		Assert.assertEquals(result, 7);
	}

	@Test
	public void testLeReadShort()
	{
		short result = leBuffer.getShort(7);
		Assert.assertEquals(result, (short) 0x0807);
	}

	@Test
	public void testLeReadInt()
	{
		int result = leBuffer.getInt(7);
		Assert.assertEquals(result, (int) 0x0a090807);
	}

	@Test
	public void testLeReadLong()
	{
		long result = leBuffer.getLong(7);
		Assert.assertEquals(result, 0x0e0d0c0b0a090807L);
	}

	@Test
	public void testLeReadBytes()
	{
		byte[] result = new byte[8];
		leBuffer.getBytes(7, result);
		ArrayAsserts.assertArrayEquals(new byte[]{ 7, 8, 9, 10, 11, 12, 13, 14 }, result);
	}

	@Test
	public void testWriteByte()
	{
		buffer.putByte(256, (byte) 4);
		assertBytes(256, new byte[]{ 4 });
	}

	@Test
	public void testWriteShort()
	{
		buffer.putShort(258, (short) 0x506);
		assertBytes(258, new byte[]{ 5, 6 });
	}

	@Test
	public void testWriteInt()
	{
		buffer.putInt(260, 0x708090a);
		assertBytes(260, new byte[]{ 7, 8, 9, 10 });
	}

	@Test
	public void testWriteLong()
	{
		buffer.putLong(268, 0x708090a0b0c0d0eL);
		assertBytes(268, new byte[]{ 7, 8, 9, 10, 11, 12, 13, 14 });
	}

	@Test
	public void testWriteBytes()
	{
		buffer.putBytes(280, new byte[]{ 1, 2, 3, 4, 5 });
		assertBytes(280, new byte[]{ 1, 2, 3, 4, 5 });
	}

	@Test
	public void testLeWriteByte()
	{
		leBuffer.putByte(256, (byte) 4);
		assertBytes(256, new byte[]{ 4 });
	}

	@Test
	public void testLeWriteShort()
	{
		leBuffer.putShort(258, (short) 0x506);
		assertBytes(258, new byte[]{ 6, 5 });
	}

	@Test
	public void testLeWriteInt()
	{
		leBuffer.putInt(260, 0x708090a);
		assertBytes(260, new byte[]{ 10, 9, 8, 7 });
	}

	@Test
	public void testLeWriteLong()
	{
		leBuffer.putLong(268, 0x708090a0b0c0d0eL);
		assertBytes(268, new byte[]{ 14, 13, 12, 11, 10, 9, 8, 7 });
	}

	@Test
	public void testLeWriteBytes()
	{
		leBuffer.putBytes(280, new byte[]{ 1, 2, 3, 4, 5 });
		assertBytes(280, new byte[]{ 1, 2, 3, 4, 5 });
	}

	@Test
	public void testEqualsBytes()
	{
		byte[] expected = new byte[]{ 0, 1, 2, 3 };
		Assert.assertTrue(buffer.equalsBytes(0, expected, 0, 4));
	}

	@Test
	public void testCompareBytes()
	{
		byte[] expectedNeg = new byte[]{ 0, 2, 0, 0 };
		Assert.assertTrue(buffer.compareBytes(0, expectedNeg, 0, 4) < 0);
		byte[] expectedZero = new byte[]{ 0, 1, 2, 3 };
		Assert.assertTrue(buffer.compareBytes(0, expectedZero, 0, 4) == 0);
		byte[] expectedPos = new byte[]{ 0, 0, 1, 2 };
		Assert.assertTrue(buffer.compareBytes(0, expectedPos, 0, 4) > 0);
	}

	private void assertBytes(long pos, byte[] expected)
	{
		byte[] result = new byte[expected.length];
		leBuffer.getBytes(pos, result);
		if (!Arrays.equals(expected, result)) {
			// TestNG implementation is super slow, therefore we check using Java util first
			ArrayAsserts.assertArrayEquals(expected, result);
		}
	}

	private Path testfile;
	private FileChannel channel;
	private SmallMappedFlatBuffer buffer;
	private MappedFlatBuffer leBuffer;
}
