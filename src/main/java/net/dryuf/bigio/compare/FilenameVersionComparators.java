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

package net.dryuf.bigio.compare;

import net.dryuf.bigio.string.StringIterator;

import java.util.Comparator;


/**
 * Compares two filenames with their version.  The comparator ignores anything except numbers.
 */
public class FilenameVersionComparators
{
	public static final Comparator<String> PATH_COMPARATOR = new PathVersionComparator();

	public static final Comparator<String> FILENAME_ONLY_COMPARATOR = new FilenameOnlyVersionComparator();

	public static class PathVersionComparator implements Comparator<String>
	{
		@Override
		public int compare(String o1, String o2)
		{
			return compareIterators(new StringIterator(o1), new StringIterator(o2));
		}
	}

	public static class FilenameOnlyVersionComparator implements Comparator<String>
	{
		@Override
		public int compare(String o1, String o2)
		{
			return compareIterators(
				StringIterator.fromOffsetEnd(o1, o1.lastIndexOf('/') + 1, o1.length()),
				StringIterator.fromOffsetEnd(o2, o2.lastIndexOf('/') + 1, o2.length())
			);
		}
	}

	public static int compareIterators(StringIterator i1, StringIterator i2)
	{
		for (;;) {
			char c1, c2;
			do {
				if (!i1.hasNext()) {
					c1 = '\0';
					break;
				}
				c1 = i1.next();
			} while (!Character.isDigit(c1));
			do {
				if (!i2.hasNext()) {
					c2 = '\0';
					break;
				}
				c2 = i2.next();
			} while (!Character.isDigit(c2));
			if (c1 == '\0') {
				if (c2 != '\0') {
					return -1;
				}
				else {
					return 0;
				}
			}
			else if (c2 == '\0') {
				return 1;
			}
			int v1 = parseNumber(i1, c1 - '0');
			int v2 = parseNumber(i2, c2 - '0');
			if (v1 != v2) {
				return v1 < v2 ? -1 : 1;
			}
		}
	}

	static int parseNumber(StringIterator s, int initial)
	{
		int value = initial;
		for (;;) {
			if (!s.hasNext()) {
				return value;
			}
			char c = s.next();
			if (!Character.isDigit(c)) {
				s.back();
				return value;
			}
			else {
				value = value * 10 + (c - '0');
			}
		}
	}
}
