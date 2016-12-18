package org.neverNows.untilTest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.neverNows.until.FileUntil;

public class FileUntilTest {
	
	private FileUntil fileUntil;
	
	@Before
	public void init(){
		this.fileUntil = new FileUntil();
	}
	
	
	@Test
	public void txtToStringTest(){
		assertEquals(this.fileUntil.txtToString("test/text.sql"),"text ejemplo");
	}

	
	@Test
	public void cleanDumpTest(){
		assertEquals( this.fileUntil.cleanDump(this.fileUntil.txtToString(
				"test/db/export.sql")).contains("BEGIN TRANSACTION;") , false);
		assertEquals( this.fileUntil.cleanDump(this.fileUntil.txtToString(
				"test/db/export.sql")).contains("COMMIT;") , false);
		assertEquals( this.fileUntil.cleanDump(this.fileUntil.txtToString(
				"test/db/export.sql")).contains("\n") , false);
		assertEquals( this.fileUntil.cleanDump(this.fileUntil.txtToString(
				"test/db/export.sql")).contains("\"") , false);

	}
	
}
