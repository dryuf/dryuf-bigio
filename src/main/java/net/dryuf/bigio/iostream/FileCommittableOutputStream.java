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

package net.dryuf.bigio.iostream;

import net.dryuf.bigio.Committable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * {@link Committable} enabled {@link OutputStream}.
 */
public class FileCommittableOutputStream extends CommittableOutputStream
{
	private final Path file;

	private final OutputStream output;

	private boolean commitable;

	public FileCommittableOutputStream(Path file) throws IOException
	{
		this.file = file;
		this.output = Files.newOutputStream(this.file);
	}

	@Override
	public void write(int b) throws IOException
	{
		output.write(b);
	}

	@Override
	public void write(byte[] b, int o, int l) throws IOException
	{
		output.write(b, o, l);
	}

	@Override
	public void flush() throws IOException
	{
		output.flush();
	}

	@Override
	public void close() throws IOException
	{
		try {
			output.close();
		}
		finally {
			if (!commitable) {
				Files.delete(file);
			}
		}
	}

	@Override
	public void committable(boolean committable)
	{
		this.commitable = committable;
	}
}
