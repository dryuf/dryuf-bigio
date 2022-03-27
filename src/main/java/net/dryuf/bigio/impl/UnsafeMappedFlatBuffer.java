//package net.dryuf.bigio.impl;
//
//import net.dryuf.bigio.MappedFlatBuffer;
//import sun.misc.Unsafe;
//import sun.nio.ch.FileChannelImpl;
//
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.nio.ByteOrder;
//import java.nio.channels.FileChannel;
//
//
///**
// * {@link MappedFlatBuffer} based on internals of {@link Unsafe} and {@link FileChannelImpl} classes.
// */
//public class UnsafeMappedFlatBuffer extends MappedFlatBuffer
//{
//	public UnsafeMappedFlatBuffer(FileChannel channel, FileChannel.MapMode mode, long len) throws IOException
//	{
//		if (len < 0) {
//			len = channel.size();
//		}
//		long sizeArg = len;
//		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
//			// windows implementation has a bug as the last parameter is truncated, therefore we always map
//			// whole file
//			sizeArg = 0;
//			len = channel.size();
//		}
//		try {
//			this.size = len;
//			this.mapper = mapperFactory.map(channel, translateMode(mode), sizeArg);
//			this.address = mapper.address;
//		}
//		catch (Exception e) {
//			throw new IOException(e);
//		}
//	}
//
//	@Override
//	public synchronized void close()
//	{
//		if (isClosed)
//			return;
//		try {
//			isClosed = true;
//			mapper.unmap();
//		}
//		catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	@Override
//	public long size()
//	{
//		return this.size;
//	}
//
//	@Override
//	public ByteOrder getByteOrder()
//	{
//		return ByteOrder.nativeOrder();
//	}
//
//	@Override
//	public byte getByte(long pos)
//	{
//		checkBounds(pos, 1);
//		return unsafe.getByte(pos +address);
//	}
//
//	@Override
//	public short getShort(long pos)
//	{
//		checkBounds(pos, 2);
//		return unsafe.getShort(pos +address);
//	}
//
//	@Override
//	public int getInt(long pos)
//	{
//		checkBounds(pos, 4);
//		return unsafe.getInt(pos +address);
//	}
//
//	@Override
//	public long getLong(long pos)
//	{
//		checkBounds(pos, 8);
//		return unsafe.getLong(pos +address);
//	}
//
//	@Override
//	public void putByte(long pos, byte val)
//	{
//		checkBounds(pos, 1);
//		unsafe.putByte(pos +address, val);
//	}
//
//	@Override
//	public void putShort(long pos, short val)
//	{
//		checkBounds(pos, 2);
//		unsafe.putShort(pos +address, val);
//	}
//
//	@Override
//	public void putInt(long pos, int val)
//	{
//		checkBounds(pos, 4);
//		unsafe.putInt(pos +address, val);
//	}
//
//	@Override
//	public void putLong(long pos, long val)
//	{
//		checkBounds(pos, 8);
//		unsafe.putLong(pos +address, val);
//	}
//
//	@Override
//	public void getBytes(long pos, byte[] data, int offset, int length)
//	{
//		if ((offset|length|(offset+length)|(data.length-length-offset)) < 0) {
//			if (offset < 0) {
//				throw new IndexOutOfBoundsException("offset out of bounds: "+offset);
//			}
//			if (length < 0 || offset+length < 0 || offset+length > data.length) {
//				throw new IndexOutOfBoundsException("length out of bounds: "+length);
//			}
//		}
//		checkBounds(pos, length);
//		unsafe.copyMemory(null, pos +address, data, BYTE_ARRAY_OFFSET+offset, length);
//	}
//
//	@Override
//	public void putBytes(long pos, byte[] data, int offset, int length)
//	{
//		if ((offset|length|(offset+length)|(data.length-length-offset)) < 0) {
//			if (offset < 0) {
//				throw new IndexOutOfBoundsException("offset out of bounds: "+offset);
//			}
//			if (length < 0 || offset+length < 0 || offset+length >= data.length) {
//				throw new IndexOutOfBoundsException("length out of bounds: "+length);
//			}
//		}
//		checkBounds(pos, length);
//		unsafe.copyMemory(data, BYTE_ARRAY_OFFSET, null, pos +address, data.length);
//	}
//
//	private final void checkBounds(long pos, int length)
//	{
//		if ((pos|length|(pos+length)|(this.size-length-pos)) < 0) {
//			if (pos < 0) {
//				throw new IndexOutOfBoundsException("position out of bounds: "+pos);
//			}
//			if (length < 0 || pos+length < 0 || pos+length > this.size) {
//				throw new IndexOutOfBoundsException("length out of bounds: "+length);
//			}
//		}
//	}
//
//	private static int translateMode(FileChannel.MapMode mode)
//	{
//		if (mode == FileChannel.MapMode.READ_ONLY) {
//			return 0;
//		}
//		else if (mode == FileChannel.MapMode.READ_WRITE) {
//			return 1;
//		}
//		throw new IllegalArgumentException("Unsupported memory map mode: "+mode);
//	}
//
//	private static Method getClassMethod(Class<?> cls, String name, Class<?>... params)
//	{
//		Method m = null;
//		try {
//			m = cls.getDeclaredMethod(name, params);
//		}
//		catch (NoSuchMethodException e) {
//			throw new UnsupportedOperationException(e);
//		}
//		m.setAccessible(true);
//		return m;
//	}
//
//	private final Mapper mapper;
//
//	private final long address;
//
//	private final long size;
//
//	private boolean isClosed = false;
//
//	private static final Unsafe unsafe;
//
//	private static final MapperFactory mapperFactory;
//
//	private static final int BYTE_ARRAY_OFFSET;
//
//	private static abstract class Mapper
//	{
//		long address;
//		long size;
//
//		abstract void unmap();
//	}
//
//	private static abstract class MapperFactory
//	{
//		abstract Mapper map(FileChannel channel, int prot, long size);
//	}
//
//	private static class Jdk8MapperFactory extends MapperFactory
//	{
//		private final Method mapMethod = getClassMethod(FileChannelImpl.class, "map0", int.class, long.class, long.class);
//		private final Method unmapMethod = getClassMethod(FileChannelImpl.class, "unmap0", long.class, long.class);
//
//		@Override
//		Mapper map(FileChannel channel, int prot, long size0)
//		{
//			try {
//				long address0 = (long) mapMethod.invoke(channel, prot, 0, size0);
//				return new Mapper()
//				{
//					{
//						this.address = address0;
//						this.size = size0;
//					}
//
//					@Override
//					void unmap()
//					{
//						try {
//							unmapMethod.invoke(channel, address, size);
//						}
//						catch (IllegalAccessException|InvocationTargetException e) {
//							throw new RuntimeException(e);
//						}
//					}
//				};
//			}
//			catch (IllegalAccessException|InvocationTargetException e) {
//				throw new RuntimeException(e);
//			}
//		}
//	}
//
//	private static class Jdk17MapperFactory extends MapperFactory
//	{
//		private final Method mapMethod = getClassMethod(FileChannelImpl.class, "map0", int.class, long.class, long.class, boolean.class);
//		private final Method unmapMethod = getClassMethod(FileChannelImpl.class, "unmap0", long.class, long.class);
//
//		@Override
//		Mapper map(FileChannel channel, int prot, long size0)
//		{
//			try {
//				long address0 = (long) mapMethod.invoke(channel, prot, 0, size0, false);
//				return new Mapper()
//				{
//					{
//						this.address = address0;
//						this.size = size0;
//					}
//
//					@Override
//					void unmap()
//					{
//						try {
//							unmapMethod.invoke(channel, address, size);
//						}
//						catch (IllegalAccessException|InvocationTargetException e) {
//							throw new RuntimeException(e);
//						}
//					}
//				};
//			}
//			catch (IllegalAccessException|InvocationTargetException e) {
//				throw new RuntimeException(e);
//			}
//		}
//	}
//
//	static
//	{
//		try {
//			Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
//			theUnsafeField.setAccessible(true);
//			unsafe = (Unsafe)theUnsafeField.get(null);
//
//			MapperFactory mapperFactory0;
//			try {
//				mapperFactory0 = new Jdk8MapperFactory();
//			}
//			catch (UnsupportedOperationException ex) {
//				mapperFactory0 = new Jdk17MapperFactory();
//			}
//			mapperFactory = mapperFactory0;
//
//			BYTE_ARRAY_OFFSET = unsafe.arrayBaseOffset(byte[].class);
//		}
//		catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//}
