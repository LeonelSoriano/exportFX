package org.neverNows.untilTest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.neverNows.until.ComandTerminal;

public class ComandTerminalTest {

	private ComandTerminal comandTerminal;
	
//	cat db.sql | sqlite3 database.db
	@Before
	public void init(){
		this.comandTerminal = new ComandTerminal();
	}
	
	@Test
	public void executeCommandTest(){
		assertEquals(this.comandTerminal.executeCommand("pwd"),"/home/leonel/exportFX\n");
		
		assertEquals(this.comandTerminal.executeCommand("echo hola mundo"),"hola mundo\n");
	}
	

}
