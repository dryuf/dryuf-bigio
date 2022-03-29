package net.dryuf.bigio.file;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import net.dryuf.bigio.FlatChannel;

import java.nio.channels.FileChannel;

/**
 * {@link FileChannel} based implementation of {@link FlatChannel} .
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FileChannelFlatChannel implements FlatChannel
{
	@Delegate
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
}
