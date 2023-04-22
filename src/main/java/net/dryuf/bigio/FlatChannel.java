package net.dryuf.bigio;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Channel providing stateless reading and writing from arbitrary position.
 *
 * <p>This interface should have been part of JDK and FileChannel should have been one of the implementing classes,
 * otherwise it's very difficult to use concurrent access on virtual channels.
 */
public interface FlatChannel
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
