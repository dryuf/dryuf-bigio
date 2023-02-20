package net.dryuf.bigio.string;

import net.dryuf.bigio.compare.FilenameVersionComparators;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class FilenameVersionComparatorTest
{
	@Test
	public void compare_noVersion_same()
	{
		int result = FilenameVersionComparators.FILENAME_ONLY_COMPARATOR.compare("hello", "world");
		assertEquals(result, 0);
	}

	@Test
	public void compare_versionAndNoversion_higher()
	{
		int result = FilenameVersionComparators.FILENAME_ONLY_COMPARATOR.compare("hello-1", "world");
		assertEquals(result, 1);
	}

	@Test
	public void compare_versionSingleDigit_higher()
	{
		int result = FilenameVersionComparators.FILENAME_ONLY_COMPARATOR.compare("hello-2", "world-1");
		assertEquals(result, 1);
	}

	@Test
	public void compare_versionMoreDigits_lower()
	{
		int result = FilenameVersionComparators.FILENAME_ONLY_COMPARATOR.compare("hello-2", "world-10");
		assertEquals(result, -1);
	}

	@Test
	public void compare_moreNumbers_lower()
	{
		int result = FilenameVersionComparators.FILENAME_ONLY_COMPARATOR.compare("hello-20-1", "world-20-2");
		assertEquals(result, -1);
	}

	@Test
	public void compare_moreNumbersNoNumber_lower()
	{
		int result = FilenameVersionComparators.FILENAME_ONLY_COMPARATOR.compare("hello-20-1", "world-20");
		assertEquals(result, 1);
	}

	@Test
	public void comparePath_noPathVersionMoreFileNumbersNoNumber_lower()
	{
		int result = FilenameVersionComparators.PATH_COMPARATOR.compare("abc/hello-20-1", "xyz/world-20");
		assertEquals(result, 1);
	}

	@Test
	public void comparePath_withPathVersionMoreFileNumbersNoNumber_lower()
	{
		int result = FilenameVersionComparators.PATH_COMPARATOR.compare("abc-1/hello-20-1", "xyz-2/world-20");
		assertEquals(result, -1);
	}

	@Test
	public void compareFileOnly_withPathVersionMoreFileNumbersNoNumber_higher()
	{
		int result = FilenameVersionComparators.FILENAME_ONLY_COMPARATOR.compare("abc-1/hello-20-1", "xyz-2/world-20");
		assertEquals(result, 1);
	}
}
