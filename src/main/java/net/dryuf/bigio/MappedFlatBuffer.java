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

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import net.dryuf.bigio.file.CompositeMappedFlatBuffer;
import net.dryuf.bigio.file.SmallMappedFlatBuffer;


/**
 * Mapped file implementation of {@link FlatBuffer}.
 */
@SuppressWarnings("restriction")
public abstract class MappedFlatBuffer extends AbstractFlatBuffer
{
	public static MappedFlatBuffer from(FileChannel channel, FileChannel.MapMode mode, long offset, long length) throws IOException
	{
		long realLength = length < 0 ? channel.size() : length;
		return realLength > Integer.MAX_VALUE ?
			new CompositeMappedFlatBuffer(channel, mode, offset, length) :
			new SmallMappedFlatBuffer(channel, mode, offset, length);
	}

	public static MappedFlatBuffer from(Path file, FileChannel.MapMode mode) throws IOException
	{
		OpenOption[] open = mode == FileChannel.MapMode.READ_ONLY ? new OpenOption[]{ StandardOpenOption.READ } : new OpenOption[]{ StandardOpenOption.READ, StandardOpenOption.WRITE };
		try (FileChannel channel = FileChannel.open(file, open)) {
			return from(channel, mode, 0, -1);
		}
	}
}
