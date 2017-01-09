package org.neverNows.databaseTest.beanTest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.nevernows.database.SqliteDb;
import org.nevernows.database.beans.StructureItemTable;
import org.nevernows.database.beans.StructureTable;

public class StructureTableTest {

	private SqliteDb  sqliteDb;
	
	@Before
	public void init(){
		this.sqliteDb = new SqliteDb("test/db/ejemplo.db");
	}
	
	
	@Test
	public final void testExistInEstrucute() {
		
		StructureTable structureTable = 
				sqliteDb.getStructureTable("no_parametro");
		
		assertEquals(structureTable.existInEstrucute("id2"), false);
		assertEquals(structureTable.existInEstrucute("id"), true);
		assertEquals(structureTable.existInEstrucute("name"), true);
	}
	
	
	@Test
	public void getItemTableByNae(){
		StructureTable structureTable = 
				sqliteDb.getStructureTable("no_parametro");
		StructureItemTable itemTableName = structureTable.getItemTableByName("name");
		StructureItemTable itemTableId = structureTable.getItemTableByName("id");
		
		assertEquals(itemTableName.getType(), "TEXT");
		assertEquals(itemTableName.getName(), "name");
		assertEquals(itemTableName.getId(), 0);
		assertEquals(itemTableName.getDefaulValue(), null);
		assertEquals(itemTableId.getId(), 1);
	}

}
