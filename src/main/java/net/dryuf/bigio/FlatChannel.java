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

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Channel providing stateless reading and writing from arbitrary position.
 *
 * <p>This interface should have been part of JDK and FileChannel should have been one of the implementing classes,
 * otherwise it's very difficult to use concurrent access on virtual channels.
 */
public interface FlatChannel extends Closeable
{
	/**
	 * Reads a sequence of bytes from this channel into the given buffer, starting at the given file position.
	 *
	 * @param buffer
	 * 	buffer to write data into.
	 * @param position
	 * 	channel offset to read from.
	 *
	 * @return
	 * 	number of bytes read, possibly 0, -1 on end of channel.
	 */
	int read(ByteBuffer buffer, long position) throws IOException;

	/**
	 * Writes a sequence of bytes into this channel from the given buffer, starting at the given file position.
	 *
	 * @param buffer
	 * 	buffer to read data from.
	 * @param position
	 * 	channel offset to write to.
	 *
	 * @return
	 * 	number of bytes written, possibly 0.
	 */
	int write(ByteBuffer buffer, long position) throws IOException;
}
