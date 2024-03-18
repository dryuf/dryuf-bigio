package net.dryuf.bigio.sort;

import net.dryuf.bigio.file.SmallMappedFlatBuffer;
import org.testng.annotations.Test;
import org.testng.internal.junit.ArrayAsserts;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static org.testng.Assert.assertEquals;


public class FlatSortTest
{
	@Test
	public void sort_sorted_keep() throws IOException
	{
		ByteBuffer input = ByteBuffer.wrap(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		FlatSort.sort(new SmallMappedFlatBuffer(input), 1, 0, 10, Comparator.comparing(b -> b.get(0)));
		assertEquals(input, ByteBuffer.wrap(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }));
	}

	@Test
	public void sort_reversed_reverse() throws IOException
	{
		ByteBuffer input = ByteBuffer.wrap(new byte[]{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 });
		FlatSort.sort(new SmallMappedFlatBuffer(input), 1, 0, 10, Comparator.comparing(b -> b.get(0)));
		assertEquals(input, ByteBuffer.wrap(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }));
	}

	@Test
	public void sort_unsorted_sort() throws IOException
	{
		ByteBuffer input = ByteBuffer.wrap(new byte[]{ 8, 7, 6, 5, 9, 1, 4, 3, 2, 0 });
		FlatSort.sort(new SmallMappedFlatBuffer(input), 1, 0, 10, Comparator.comparing(b -> b.get(0)));
		assertEquals(input, ByteBuffer.wrap(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }));
	}

	@Test
	public void sort_dups_sort() throws IOException
	{
		ByteBuffer input = ByteBuffer.wrap(new byte[]{ 8, 7, 7, 6, 5, 5, 9, 1, 4, 3, 2, 4, 0 });
		FlatSort.sort(new SmallMappedFlatBuffer(input), 1, 0, input.remaining(), Comparator.comparing(b -> b.get(0)));
		assertEquals(input, ByteBuffer.wrap(new byte[]{ 0, 1, 2, 3, 4, 4, 5, 5, 6, 7, 7, 8, 9 }));
	}

	@Test
	public void sort_pivotGreatest_sort() throws IOException
	{
		ByteBuffer input = ByteBuffer.wrap(new byte[]{ 8, 7, 7, 6, 5, 9, 5, 1, 4, 3, 2, 4, 0 });
		FlatSort.sort(new SmallMappedFlatBuffer(input), 1, 0, input.remaining(), Comparator.comparing(b -> b.get(0)));
		assertEquals(input, ByteBuffer.wrap(new byte[]{ 0, 1, 2, 3, 4, 4, 5, 5, 6, 7, 7, 8, 9 }));
	}

	@Test
	public void sort_pivotLowest_sort() throws IOException
	{
		ByteBuffer input = ByteBuffer.wrap(new byte[]{ 8, 7, 7, 6, 5, 0, 5, 1, 4, 3, 2, 4, 0 });
		FlatSort.sort(new SmallMappedFlatBuffer(input), 1, 0, input.remaining(), Comparator.comparing(b -> b.get(0)));
		assertEquals(input, ByteBuffer.wrap(new byte[]{ 0, 0, 1, 2, 3, 4, 4, 5, 5, 6, 7, 7, 8 }));
	}

	@Test
	public void sort_random_sort() throws IOException
	{
		Random random = new Random();
		byte[] input = new byte[100];

		for (int c = 0; c < 10_000; ++c) {
			random.nextBytes(input);

			byte[] sorted = Arrays.copyOf(input, input.length);
			Arrays.sort(sorted);

			byte[] bsorted = Arrays.copyOf(input, input.length);
			ByteBuffer buffer = ByteBuffer.wrap(bsorted);

			try {
				FlatSort.sort(new SmallMappedFlatBuffer(buffer), 1, 0, bsorted.length, Comparator.comparing(b -> b.get(0)));

				ArrayAsserts.assertArrayEquals(sorted, bsorted);
			}
			catch (Throwable ex) {
				System.out.print("Test failure on: ");
				for (byte b: input) {
					System.out.print(b);
					System.out.print(" ");
				}
				System.out.println();
				throw ex;
			}
		}
	}

	@Test
	public void sort_intTwo_sort() throws Throwable
	{
		int[] input = new int[]{ 2, 1 };

		intTester(input);
	}

	@Test
	public void sort_intThree_sort() throws Throwable
	{
		int[] input = new int[]{ 2, 1, 3 };

		intTester(input);
	}

	@Test
	public void sort_intRandom_sort() throws Throwable
	{
		Random random = new Random();
		int[] input = new int[1000];

		for (int c = 0; c < 1_000; ++c) {
			for (int i = 0; i < input.length; ++i) {
				input[i] = random.nextInt();
			}

			intTester(input);
		}
	}

	@Test
	public void sortFlat_intRandom_sort() throws Throwable
	{
		Random random = new Random();
		int[] input = new int[1000];

		for (int c = 0; c < 1_000; ++c) {
			for (int i = 0; i < input.length; ++i) {
				input[i] = random.nextInt();
			}

			intTesterFlat(input);
		}
	}

	private void intTester(int[] input) throws Throwable
	{
		int[] sorted = Arrays.copyOf(input, input.length);
		Arrays.sort(sorted);

		byte[] bsorted = new byte[input.length*4];
		ByteBuffer buffer = ByteBuffer.wrap(bsorted).order(ByteOrder.LITTLE_ENDIAN);
		buffer.asIntBuffer().put(input);

		try {
			FlatSort.sort(new SmallMappedFlatBuffer(buffer), 4, 0, bsorted.length, Comparator.comparing(b -> b.getInt(0)));

			int[] isorted = new int[input.length];
			buffer.asIntBuffer().get(isorted);
			ArrayAsserts.assertArrayEquals(sorted, isorted);
		}
		catch (Throwable ex) {
			System.out.print("Test failure on: ");
			for (int b: input) {
				System.out.print(b);
				System.out.print(" ");
			}
			System.out.println();
			throw ex;
		}
	}

	private void intTesterFlat(int[] input) throws Throwable
	{
		int[] sorted = Arrays.copyOf(input, input.length);
		Arrays.sort(sorted);

		byte[] bsorted = new byte[input.length*4];
		ByteBuffer buffer = ByteBuffer.wrap(bsorted).order(ByteOrder.LITTLE_ENDIAN);
		buffer.asIntBuffer().put(input);

		try {
			FlatSort.sort(new SmallMappedFlatBuffer(buffer), 4, 0, bsorted.length, (b, left, right) -> Integer.compare(b.getInt(left), b.getInt(right)));

			int[] isorted = new int[input.length];
			buffer.asIntBuffer().get(isorted);
			ArrayAsserts.assertArrayEquals(sorted, isorted);
		}
		catch (Throwable ex) {
			System.out.print("Test failure on: ");
			for (int b: input) {
				System.out.print(b);
				System.out.print(" ");
			}
			System.out.println();
			throw ex;
		}
	}
}
