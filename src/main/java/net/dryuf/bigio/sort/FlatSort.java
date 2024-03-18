package net.dryuf.bigio.sort;

import net.dryuf.bigio.FlatBuffer;

import java.nio.ByteBuffer;
import java.util.Comparator;


public class FlatSort
{
	public static void sort(FlatBuffer buffer, int objectSize, long start, long length, Comparator<ByteBuffer> comparator)
	{
		if (length % objectSize != 0) {
			throw new IllegalArgumentException("Area size is not dividable by objectSize");
		}
		if (length <= objectSize) {
			return;
		}
		byte[] tmp1 = new byte[objectSize];
		byte[] tmp2 = new byte[objectSize];

		sortInternal(buffer, objectSize, start, start + length - objectSize, comparator, tmp1, tmp2);
	}

	public static void sort(FlatBuffer buffer, int objectSize, long start, long length, FlatBufferComparator comparator)
	{
		if (length % objectSize != 0) {
			throw new IllegalArgumentException("Area size is not dividable by objectSize");
		}
		if (length <= objectSize) {
			return;
		}
		byte[] tmp1 = new byte[objectSize];
		byte[] tmp2 = new byte[objectSize];

		sortInternal(buffer, objectSize, start, start + length - objectSize, comparator, tmp1, tmp2);
	}

	static void sortInternal(FlatBuffer buffer, int objectSize, long offset, long end, Comparator<ByteBuffer> comparator, byte[] tmp1, byte[] tmp2)
	{
		for (;;) {
			if (end - offset <= objectSize) {
				if (end - offset <= 0) {
					return;
				}
				ByteBuffer left = buffer.subByteBuffer(offset, objectSize);
				ByteBuffer right = buffer.subByteBuffer(offset + objectSize, objectSize);
				if (comparator.compare(left, right) > 0) {
					_swapOffsets(buffer, offset, offset + objectSize, tmp1, tmp2, 0L);
				}
				return;
			}

			long pivotPos = offset + ((end - offset) - (end - offset)%(2L*objectSize)) / 2;
			ByteBuffer pivot = buffer.subByteBuffer(pivotPos, objectSize);

			long lt = offset, eq = offset, gt = end;
			while (eq <= gt) {
				ByteBuffer eb = buffer.subByteBuffer(eq, objectSize);
				int cmp = comparator.compare(eb, pivot);
				if (cmp < 0) {
					long pivotNew = _swapOffsets(buffer, eq, lt, tmp1, tmp2, pivotPos);
					if (pivotNew != pivotPos) {
						pivotPos = pivotNew;
						pivot = buffer.subByteBuffer(pivotPos, objectSize);
					}
					lt += objectSize;
					eq += objectSize;
				}
				else if (cmp > 0) {
					long pivotNew = _swapOffsets(buffer, eq, gt, tmp1, tmp2, pivotPos);
					if (pivotNew != pivotPos) {
						pivotPos = pivotNew;
						pivot = buffer.subByteBuffer(pivotPos, objectSize);
					}
					gt -= objectSize;
				}
				else {
					eq += objectSize;
				}
			}

			// Smaller goes recursive, bigger is TCO:
			if (lt - offset < end - gt) {
				sortInternal(buffer, objectSize, offset, lt - objectSize, comparator, tmp1, tmp2);
				offset = gt + objectSize;
			}
			else {
				sortInternal(buffer, objectSize, gt + objectSize, end, comparator, tmp1, tmp2);
				end = lt - objectSize;
			}
		}
	}

	static void sortInternal(FlatBuffer buffer, int objectSize, long offset, long end, FlatBufferComparator comparator, byte[] tmp1, byte[] tmp2)
	{
		for (;;) {
			if (end - offset <= objectSize) {
				if (end - offset <= 0) {
					return;
				}
				if (comparator.compare(buffer, offset, offset + objectSize) > 0) {
					_swapOffsetsPivot(buffer, offset, offset + objectSize, tmp1, tmp2, 0);
				}
				return;
			}
			long pivot = offset + ((end - offset) - (end - offset) % (2L*objectSize)) / 2;

			long lt = offset, eq = offset, gt = end;
			while (eq <= gt) {
				int cmp = comparator.compare(buffer, eq, pivot);
				if (cmp < 0) {
					pivot = _swapOffsetsPivot(buffer, eq, lt, tmp1, tmp2, pivot);
					lt += objectSize;
					eq += objectSize;
				}
				else if (cmp > 0) {
					pivot = _swapOffsetsPivot(buffer, eq, gt, tmp1, tmp2, pivot);
					gt -= objectSize;
				}
				else {
					eq += objectSize;
				}
			}

			// Smaller goes recursive, bigger is TCO:
			if (lt - offset < end - gt) {
				sortInternal(buffer, objectSize, offset, lt - objectSize, comparator, tmp1, tmp2);
				offset = gt + objectSize;
			}
			else {
				sortInternal(buffer, objectSize, gt + objectSize, end, comparator, tmp1, tmp2);
				end = lt - objectSize;
			}
		}
	}

	private static long _swapOffsets(FlatBuffer buffer, long i0, long i1, byte[] tmp1, byte[] tmp2, long pivotPos)
	{
		if (i0 == i1) {
			return pivotPos;
		}
		buffer.getBytes(i0, tmp1);
		buffer.getBytes(i1, tmp2);
		buffer.putBytes(i0, tmp2);
		buffer.putBytes(i1, tmp1);
		if (i0 == pivotPos) {
			return i1;
		}
		else if (i1 == pivotPos) {
			return i0;
		}
		else {
			return pivotPos;
		}
	}

	private static long _swapOffsetsPivot(FlatBuffer buffer, long i0, long i1, byte[] tmp1, byte[] tmp2, long pivot)
	{
		if (i0 == i1) {
			return pivot;
		}
		buffer.getBytes(i0, tmp1);
		buffer.getBytes(i1, tmp2);
		buffer.putBytes(i0, tmp2);
		buffer.putBytes(i1, tmp1);
		if (pivot == i0) {
			return i1;
		}
		else if (pivot == i1) {
			return i0;
		}
		else {
			return pivot;
		}
	}

	@FunctionalInterface
	public interface FlatBufferComparator
	{
		int compare(FlatBuffer buffer, long left, long right);
	}
}
