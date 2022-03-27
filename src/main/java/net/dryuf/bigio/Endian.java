package net.dryuf.bigio;

import java.nio.ByteOrder;


/**
 * Endian conversion utilities.
 */
public class Endian
{
	public static final boolean IS_LITTLE_ENDIAN = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN);
	public static final boolean IS_BIG_ENDIAN = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);

	public static short swapLe16(short x)
	{
		if (IS_LITTLE_ENDIAN)
			return x;
		return Short.reverseBytes(x);
	}

	public static int swapLe32(int x)
	{
		if (IS_LITTLE_ENDIAN)
			return x;
		return Integer.reverseBytes(x);
	}

	public static long swapLe64(long x)
	{
		if (IS_LITTLE_ENDIAN)
			return x;
		return Long.reverseBytes(x);
	}

	public static short swapBe16(short x)
	{
		if (IS_BIG_ENDIAN)
			return x;
		return Short.reverseBytes(x);
	}

	public static int swapBe32(int x)
	{
		if (IS_BIG_ENDIAN)
			return x;
		return Integer.reverseBytes(x);
	}

	public static long swapBe64(long x)
	{
		if (IS_BIG_ENDIAN)
			return x;
		return Long.reverseBytes(x);
	}
}
