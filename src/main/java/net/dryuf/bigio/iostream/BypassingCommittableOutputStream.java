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

import net.dryuf.base.function.ThrowingFunction;
import net.dryuf.bigio.Committable;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * {@link Committable} enabled {@link OutputStream}, passing committable indicator to wrapping stream.
 */
public class BypassingCommittableOutputStream extends FilterCommittableOutputStream
{
	private final CommittableOutputStream committableWrapper;

	public static BypassingCommittableOutputStream from(CommittableOutputStream committable, ThrowingFunction<? super CommittableOutputStream, OutputStream, IOException> wrapperFactory) throws IOException
	{
		return new BypassingCommittableOutputStream(wrapperFactory.apply(committable), committable);
	}

	public static BypassingCommittableOutputStream buffered(CommittableOutputStream committable) throws IOException
	{
		return from(committable, BufferedOutputStream::new);
	}

	public BypassingCommittableOutputStream(OutputStream output, CommittableOutputStream committableWrapper) throws IOException
	{
		super(output);
		this.committableWrapper = committableWrapper;
	}

	@Override
	public void committable(boolean committable)
	{
		committableWrapper.committable(committable);
	}
}
