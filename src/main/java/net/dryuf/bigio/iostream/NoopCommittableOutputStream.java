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


/**
 * {@link Committable} enabled {@link OutputStream} with no-operation taken due to enabled or disabled committable.
 */
public class NoopCommittableOutputStream extends FilterCommittableOutputStream
{
	public NoopCommittableOutputStream(OutputStream output) throws IOException
	{
		super(output);
	}

	@Override
	public void committable(boolean committable)
	{
	}
}