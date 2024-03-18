/*
 * Copyright 2015-2024 Zbynek Vyskovsky mailto:kvr000@gmail.com http://github.com/kvr000/ https://github.com/dryuf/ https://www.linkedin.com/in/zbynek-vyskovsky/
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

package net.dryuf.concurrent.collection.benchmark;

import net.dryuf.bigio.FlatBuffer;
import net.dryuf.bigio.MappedFlatBuffer;
import net.dryuf.bigio.sort.FlatSort;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS, batchSize = 1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1)
public class FileSortBenchmark
{
	public static final long ITEMS = 10_000_000;

	@State(Scope.Benchmark)
	public static class FileSortState
	{
		@Setup(Level.Invocation)
		public void setup() throws Exception
		{
			Random random = new Random(0);
			if (file == null) {
				file = Files.createTempFile("integers", ".bin");
				file.toFile().deleteOnExit();
			}
			try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
				buffer = MappedFlatBuffer.from(channel, FileChannel.MapMode.READ_WRITE, 0, ITEMS * 28)
					.order(ByteOrder.LITTLE_ENDIAN);
				for (long i = 0; i < ITEMS; ++i) {
					buffer.putInt(i * 28, random.nextInt());
				}
				buffer.putByte(ITEMS * 28 - 1, (byte) 0);
			}
		}

		@TearDown(Level.Invocation)
		public void teardown()
		{
		}

		Path file;
		public FlatBuffer buffer;
	}

	@State(Scope.Benchmark)
	public static class JavaSortState
	{
		@Setup(Level.Invocation)
		public void setup() throws Exception
		{
			Random random = new Random(0);
			for (long i = 0; i < ITEMS; ++i) {
				integers.add(new One(random.nextInt()));
			}
		}

		@TearDown(Level.Invocation)
		public void teardown()
		{
		}

		ArrayList<One> integers = new ArrayList<>();

		class One
		{
			public One(int value) { this.value = value; }
			int value;
			int more0, more1, more2, more3, more4, more5, more6;
		}
	}

	@Benchmark
	public void			sort_bytes(Blackhole blackhole, FileSortState state)
	{
		FlatSort.sort(state.buffer, Integer.BYTES, 0, ITEMS * 28, Comparator.comparing(b -> b.getInt(0)));
	}

	@Benchmark
	public void			sort_flat(Blackhole blackhole, FileSortState state)
	{
		FlatSort.sort(state.buffer, Integer.BYTES, 0, ITEMS * 28, (b, l, r) -> Integer.compare(b.getInt(l), b.getInt(r)));
	}

	@Benchmark
	public void			sort_java(Blackhole blackhole, JavaSortState state)
	{
		state.integers.sort(Comparator.comparing((JavaSortState.One o) -> o.value));
	}

}
