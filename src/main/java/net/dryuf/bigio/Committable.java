package net.dryuf.bigio;


/**
 * Interface requiring explicit commit on the input or output, discarding anything unflushed otherwise.
 *
 * This is specifically useful for output streams where writer cannot continue writing due to error in source data and
 * needs to indicate the output should not be continued streamed to the upstream.  Few examples include a servlet which
 * wants to write output but object fails to serialize and connection should be killed.  Or a remote stream, which
 * cannot be completed for the same reason and should not be uploaded to remote storage after completion.
 */
public interface Committable extends AutoCloseable
{
	/**
	 * Marks the object as committable upon call to {@link #close()}.  Without this, any call to {@link #close()}
	 * should only close the underlying connection but not attempt to send the data.
	 * The method can be called repeatedly with different settings, for example, when writing is in the middle and
	 * the stream is unsafe to commit at that time.
	 *
	 * @param committable
	 * 	sets whether the object is committable.
	 */
	void committable(boolean committable);
}
