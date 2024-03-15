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
	 * should only close the underlying connection but not attempt to send the data (e.g. should hard reset and
	 * close the channel instead of completing the operation gracefully).
	 *
	 * The method can be called repeatedly with different settings, for example, when writing is in the middle and
	 * the stream is unsafe to commit at that time.
	 *
	 * @param committable
	 * 	sets whether the object is committable.
	 */
	void committable(boolean committable);
}
