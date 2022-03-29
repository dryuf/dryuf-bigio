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

package net.dryuf.bigio.iostream;

import org.apache.commons.io.IOUtils;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Supplier;


public class LinedInputMultiStreamTest
{
	private final static String BIG_MESSAGE = new Supplier<String>() {
		@Override
		public String get() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 4096; ++i) {
				sb.append("hello ");
			}
			return sb.toString();
		}
	}.get();

	@Test
	public void testSmallSingleLine() throws IOException
	{
		byte[] input = "hello\n".getBytes(StandardCharsets.UTF_8);
		try (MultiStream multiStream = new LinedInputMultiStream(new ByteArrayInputStream(input))) {
			try (InputStream stream = multiStream.nextStream()) {
				AssertJUnit.assertEquals("hello", IOUtils.toString(stream, StandardCharsets.UTF_8));
			}
			AssertJUnit.assertNull(multiStream.nextStream());
		}
	}

	@Test
	public void testSmallSingleLineUnfinished() throws IOException
	{
		byte[] input = "hello".getBytes(StandardCharsets.UTF_8);
		try (MultiStream multiStream = new LinedInputMultiStream(new ByteArrayInputStream(input))) {
			try (InputStream stream = multiStream.nextStream()) {
				AssertJUnit.assertEquals("hello", IOUtils.toString(stream, StandardCharsets.UTF_8));
			}
			AssertJUnit.assertNull(multiStream.nextStream());
		}
	}

	@Test
	public void testSmallMultiLines() throws IOException
	{
		byte[] input = "hello\nworld\n".getBytes(StandardCharsets.UTF_8);
		try (MultiStream multiStream = new LinedInputMultiStream(new ByteArrayInputStream(input))) {
			try (InputStream stream = multiStream.nextStream()) {
				AssertJUnit.assertEquals("hello", IOUtils.toString(stream, StandardCharsets.UTF_8));
			}
			try (InputStream stream = multiStream.nextStream()) {
				AssertJUnit.assertEquals("world", IOUtils.toString(stream, StandardCharsets.UTF_8));
			}
			AssertJUnit.assertNull(multiStream.nextStream());
		}
	}

	@Test
	public void testSmallMultiLinesUnfinished() throws IOException
	{
		byte[] input = "hello\nworld".getBytes(StandardCharsets.UTF_8);
		try (MultiStream multiStream = new LinedInputMultiStream(new ByteArrayInputStream(input))) {
			try (InputStream stream = multiStream.nextStream()) {
				AssertJUnit.assertEquals("hello", IOUtils.toString(stream, StandardCharsets.UTF_8));
			}
			try (InputStream stream = multiStream.nextStream()) {
				AssertJUnit.assertEquals("world", IOUtils.toString(stream, StandardCharsets.UTF_8));
			}
			AssertJUnit.assertNull(multiStream.nextStream());
		}
	}

	@Test
	public void testSmallPartial() throws IOException
	{
		byte[] input = ("hello\nworld\n").getBytes(StandardCharsets.UTF_8);
		try (MultiStream multiStream = new LinedInputMultiStream(new ByteArrayInputStream(input))) {
			try (InputStream stream = multiStream.nextStream()) {
				byte[] actual = new byte["hello".length()];
				actual[0] = (byte)stream.read();
			}
			try (InputStream stream = multiStream.nextStream()) {
				AssertJUnit.assertEquals("world", IOUtils.toString(stream, StandardCharsets.UTF_8));
			}
			AssertJUnit.assertNull(multiStream.nextStream());
		}
	}

	@Test
	public void testBigSingleLine() throws IOException
	{
		byte[] input = (BIG_MESSAGE+"\n").getBytes(StandardCharsets.UTF_8);
		try (MultiStream multiStream = new LinedInputMultiStream(new ByteArrayInputStream(input))) {
			try (InputStream stream = multiStream.nextStream()) {
				AssertJUnit.assertEquals(BIG_MESSAGE, IOUtils.toString(stream, StandardCharsets.UTF_8));
			}
			AssertJUnit.assertNull(multiStream.nextStream());
		}
	}

	@Test
	public void testBigSingleLineUnfinished() throws IOException
	{
		byte[] input = BIG_MESSAGE.getBytes(StandardCharsets.UTF_8);
		try (MultiStream multiStream = new LinedInputMultiStream(new ByteArrayInputStream(input))) {
			try (InputStream stream = multiStream.nextStream()) {
				AssertJUnit.assertEquals(BIG_MESSAGE, IOUtils.toString(stream, StandardCharsets.UTF_8));
			}
			AssertJUnit.assertNull(multiStream.nextStream());
		}
	}

	@Test
	public void testBigMultiLineUnfinished() throws IOException
	{
		byte[] input = (BIG_MESSAGE+"\n"+BIG_MESSAGE).getBytes(StandardCharsets.UTF_8);
		try (MultiStream multiStream = new LinedInputMultiStream(new ByteArrayInputStream(input))) {
			try (InputStream stream = multiStream.nextStream()) {
				AssertJUnit.assertEquals(BIG_MESSAGE, IOUtils.toString(stream, StandardCharsets.UTF_8));
			}
			try (InputStream stream = multiStream.nextStream()) {
				AssertJUnit.assertEquals(BIG_MESSAGE, IOUtils.toString(stream, StandardCharsets.UTF_8));
			}
			AssertJUnit.assertNull(multiStream.nextStream());
		}
	}

	@Test
	public void testBigVariousReadMethods() throws IOException
	{
		byte[] input = (BIG_MESSAGE+"\n"+BIG_MESSAGE).getBytes(StandardCharsets.UTF_8);
		try (MultiStream multiStream = new LinedInputMultiStream(new ByteArrayInputStream(input))) {
			try (InputStream stream = multiStream.nextStream()) {
				byte[] actual = new byte[BIG_MESSAGE.length()];
				actual[0] = (byte)stream.read();
				IOUtils.readFully(stream, actual, 1, actual.length-1);
				AssertJUnit.assertEquals(BIG_MESSAGE, new String(actual, StandardCharsets.UTF_8));
			}
			try (InputStream stream = multiStream.nextStream()) {
				AssertJUnit.assertEquals(BIG_MESSAGE, IOUtils.toString(stream, StandardCharsets.UTF_8));
			}
			AssertJUnit.assertNull(multiStream.nextStream());
		}
	}

	@Test
	public void testBigPartial() throws IOException
	{
		byte[] input = (BIG_MESSAGE+"\n"+BIG_MESSAGE).getBytes(StandardCharsets.UTF_8);
		try (MultiStream multiStream = new LinedInputMultiStream(new ByteArrayInputStream(input))) {
			try (InputStream stream = multiStream.nextStream()) {
				byte[] actual = new byte[BIG_MESSAGE.length()];
				actual[0] = (byte)stream.read();
			}
			try (InputStream stream = multiStream.nextStream()) {
				AssertJUnit.assertEquals(BIG_MESSAGE, IOUtils.toString(stream, StandardCharsets.UTF_8));
			}
			AssertJUnit.assertNull(multiStream.nextStream());
		}
	}
}
