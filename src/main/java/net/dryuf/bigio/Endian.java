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
