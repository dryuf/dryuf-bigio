/*
 * Copyright 2015 Zbynek Vyskovsky mailto:kvr000@gmail.com http://kvr.znj.cz/ http://github.com/kvr000/
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

import net.dryuf.bigio.file.FileChannelFlatChannel;
import net.dryuf.bigio.memory.BytesFlatChannel;
import net.dryuf.bigio.seekable.SeekableChannelFlatChannel;

import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;


/**
 * {@link FlatChannel} factory.
 */
public class FlatChannels
{
	/**
	 * Wraps {@link SeekableByteChannel} to {@link FlatChannel}.
	 *
	 * @param bytes
	 * 	underlying byte array.
	 *
	 * @return
	 * 	wrapping {@link FlatChannel}.
	 */
	public static FlatChannel fromBytes(byte[] bytes)
	{
		return BytesFlatChannel.from(bytes);
	}

	/**
	 * Wraps {@link FileChannel} to {@link FlatChannel}.
	 *
	 * @param fileChannel
	 * 	underlying channel.
	 *
	 * @return
	 * 	wrapping {@link FlatChannel}.
	 */
	public static FlatChannel fromFile(FileChannel fileChannel)
	{
		return FileChannelFlatChannel.from(fileChannel);
	}

	/**
	 * Wraps {@link SeekableByteChannel} to {@link FlatChannel}. Note that this uses synchronized lock on the
	 * underlying channel to achieve atomicity of seek and read/write. In case there is another piece of code which
	 * does not follow this strategy, this wrapper will not work.
	 *
	 * @param seekableByteChannel
	 * 	underlying channel.
	 *
	 * @return
	 * 	wrapping {@link FlatChannel}.
	 */
	public static FlatChannel fromSeekable(SeekableByteChannel seekableByteChannel)
	{
		return SeekableChannelFlatChannel.from(seekableByteChannel);
	}

}
