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

package net.dryuf.bigio.file;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.dryuf.bigio.FlatChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


/**
 * {@link FileChannel} based implementation of {@link FlatChannel} .
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FileChannelFlatChannel implements FlatChannel
{
	private final FileChannel fileChannel;

	/**
	 * Constructs new instance of {@link FileChannelFlatChannel} .
	 *
	 * @param fileChannel
	 * 	file channel
	 *
	 * @return
	 * 	FlatChannel to file.
	 */
	public static FileChannelFlatChannel from(FileChannel fileChannel)
	{
		return new FileChannelFlatChannel(fileChannel);
	}

	@Override
	public int read(ByteBuffer buffer, long position) throws IOException
	{
		return fileChannel.read(buffer, position);
	}

	@Override
	public int write(ByteBuffer buffer, long position) throws IOException
	{
		return fileChannel.write(buffer, position);
	}

	@Override
	public void close() throws IOException
	{
		fileChannel.close();
	}
}
