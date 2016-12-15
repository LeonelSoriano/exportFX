package org.neverNows.database;

import static org.junit.Assert.*;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.neverNows.database.beans.StructureTable;

public class SqliteDbTest {
	
	
	private SqliteDb sqliteDb;
	
	@Before
	public void init(){
		sqliteDb = new SqliteDb();
	}

	@Test
	public void testGetAllTables() {
		
		List<String> data = sqliteDb.getAllTables();
		
		assertNotNull("un problea no a devuelto nada",data);
		
		assertEquals( "valor debe ser de 4",4, data.size());
		
		int countEqual = 0;
		
		for(String item : data){
			if(item.toLowerCase().equals("persona")){
				countEqual++;
			}else if(item.toLowerCase().equals("ejemplo")){
				countEqual++;
			}else if(item.toLowerCase().equals("trabajo")){
				countEqual++;
			}else if(item.toLowerCase().equals("no_parametro")){
				countEqual++;
			}
		}
		
		assertEquals("Valores no coinciden", 4, countEqual);
	}
	
	@Test
	public void getStructureTableTest(){
		StructureTable structureTable = this.sqliteDb.getStructureTable("persona");
		
		assertEquals(structureTable.getNameTable(), "persona");
		
		assertEquals(structureTable.getItemTables().get(0).getId(), 0);
		
		assertEquals(structureTable.getItemTables().get(0).isFk(), true);
		
		assertEquals(structureTable.getItemTables().get(1).getName(), "nombre");
		
		assertEquals(structureTable.getItemTables().get(2).getType().toUpperCase(),
				"TEXT");
		
		assertEquals(structureTable.getItemTables().get(3).isNotNull(), false);
		
		assertEquals(structureTable.getItemTables().get(4).getDefaulValue(), null);
		
	}
	
	@Test
	public void getStructureTablesTest(){
		assertEquals(this.sqliteDb.getStructureTables().size() , 4);
	}
	
	@Test
	public void tableIsParamTest(){
		assertEquals(sqliteDb.tableIsParam("persona"), true);
		assertEquals(sqliteDb.tableIsParam("ejemplo"), true);
		assertEquals(sqliteDb.tableIsParam("trabajo"), true);
		assertEquals(sqliteDb.tableIsParam("no_parametro"), false);
	}
	
	@Test
	public void valuesFormTableSimpleTest(){
	
		JSONObject obj = new JSONObject(
				sqliteDb.valuesFormTableSimple("ejemplo"));
		
		assertEquals(" tabla no es igual ", obj.getString("table"), "ejemplo");
		
		JSONArray jsonArray = obj.getJSONArray("data");
		
		JSONObject dataObj = (JSONObject) jsonArray.get(0);
		
		assertEquals(" name no es igual", dataObj.getString("name"), "leonel");
		assertEquals(" id no es igual ", dataObj.getInt("id"), 1);

	}

}
