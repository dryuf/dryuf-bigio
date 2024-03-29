/*
 * Copyright 2017 Zbynek Vyskovsky mailto:kvr000@gmail.com http://kvr.znj.cz/ http://github.com/kvr000/
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

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;


public class MappedFlatBufferTest
{
	@Test
	public void testSimple() throws IOException
	{
		try (
				FileChannel channel = FileChannel.open(Paths.get("target/test-classes/log4j2.xml"));
				FlatBuffer buffer = MappedFlatBuffer.from(channel, FileChannel.MapMode.READ_ONLY, 0, -1)
		) {
			AssertJUnit.assertEquals((byte)'<', buffer.getByte(0));
		}
	}
}
