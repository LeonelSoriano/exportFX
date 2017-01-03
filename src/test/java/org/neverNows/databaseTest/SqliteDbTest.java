package org.neverNows.databaseTest;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neverNows.SqlJsonNoEqualParamException;
import org.neverNows.database.SqliteDb;
import org.neverNows.database.beans.FKMapper;
import org.neverNows.database.beans.StructureItemTable;
import org.neverNows.database.beans.StructureTable;
import org.neverNows.param.CommonString;


public class SqliteDbTest {
	
	
	private SqliteDb sqliteDb;
	
	@Before
	public void init(){
		sqliteDb = new SqliteDb("test/db/ejemplo.db");
	}
	
	
	@After
	public void finalize(){
		File confFile = new File(CommonString.PATH_BASE_TEST +
				CommonString.NAME_CONF_DB);
		confFile.delete();
	}
	
	
	@Test
	public void createDatabeFromFileTest(){
		SqliteDb sqliteDbDatabeFromFile = new SqliteDb("test/db/ejemploDatabeFromFile.db");;
		sqliteDbDatabeFromFile.createDatabeFromFile( "test/db/export.sql");
		
		assertEquals(sqliteDbDatabeFromFile.tableIsParam("persona"), true);
		assertEquals(sqliteDbDatabeFromFile.getStructureTables().size() , 4);
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
		
		assertEquals(structureTable.getItemTables().get(0).getId(), 1);
		
		assertEquals(structureTable.getItemTables().get(0).getOrder(), 0);
		
		assertEquals(structureTable.getItemTables().get(0).isFk(), true);
		
		assertEquals(structureTable.getItemTables().get(1).getName(), "nombre");
		
		assertEquals(structureTable.getItemTables().get(1).getType().toUpperCase(),
				"TEXT");
		assertEquals(structureTable.getItemTables().get(1).getMaxValue(),
				50);
		
		assertEquals(structureTable.getItemTables().get(2).getType().toUpperCase(),
				"TEXT");
		
		assertEquals(structureTable.getItemTables().get(2).getMaxValue(),
				-1);
		
		assertEquals(structureTable.getItemTables().get(3).isNotNull(), false);
		
		assertEquals(structureTable.getItemTables().get(3).getOrder(), 3);
		
		assertEquals(structureTable.getItemTables().get(3).getId(), 0);
		
		assertEquals(structureTable.getItemTables().get(4).getDefaulValue(), null);
		
		
		structureTable = this.sqliteDb.getStructureTable("fake_table");
		
		assertNull(structureTable);
		
	}
	
	
	@Test
	public void getStructureTableWithDataTest(){
		StructureTable structureTable = 
				this.sqliteDb.getStructureTableWithData("trabajo");
		
		assertEquals(structureTable.getNameTable(),"trabajo");
		assertEquals(structureTable.getItemTables().get(0).getName(),"id");
		assertEquals(structureTable.getItemTables().get(0).getDataByIndex(0),1);
		assertEquals(structureTable.getItemTables().get(0).getDataByIndex(3),4);
		assertEquals(structureTable.getItemTables().get(1).getName(),"nombre");
		assertEquals(structureTable.getItemTables().get(1).getDataByIndex(0),
				"programador");
		assertEquals(structureTable.getItemTables().get(1).getDataByIndex(3),
				"carpintero");
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
	
	@Test
	public void getOrderTableByDumpTest(){
		
		List<String> tablesOrder = this.sqliteDb.getOrderTableByDump(
				"test/db/export.sql");
		
		assertEquals(tablesOrder.get(0), "trabajo");
		assertEquals(tablesOrder.get(1), "persona");
		assertEquals(tablesOrder.get(2), "no_parametro");
		assertEquals(tablesOrder.get(3), "ejemplo");	
	}
	
	@Test
	public void getSqlCreateFormTableTest(){
		Class<? extends SqliteDb> classReflextion = this.sqliteDb.getClass();
		
		String returnForTable = "CREATE TABLE 'persona' (	`id`	"
				+ "INTEGER PRIMARY KEY AUTOINCREMENT,	`nombre`	NUMERIC"
				+ " NOT NULL,	`alias`	TEXT,	`cedula`	NUMERIC UNIQUE,	"
				+ "`fk_trabajo`	INTEGER NOT NULL,	FOREIGN KEY(`fk_trabajo`) "
				+ "REFERENCES `trabajo`(`id`))";
		
	     try {          
	        Method method = classReflextion.getDeclaredMethod(
	        		"getSqlCreateFormTable", new Class[]{String.class});
	        method.setAccessible(true);

			assertEquals((String)method.invoke(this.sqliteDb, "persona"), returnForTable);
			
	     }
	     catch(IllegalAccessException | IllegalArgumentException  
	    		 | InvocationTargetException |NoSuchMethodException e) {
	        e.printStackTrace();
	        fail();
	     }
	   
	}
	
	
	@Test
	public void getMapperFKInTableTest(){
		List<FKMapper> fKMappers = this.sqliteDb.getMapperFKInTable(
				 "ejemplo");
		assertEquals(fKMappers.size(), 0);
		
		fKMappers = this.sqliteDb.getMapperFKInTable( "persona");
		
		assertEquals(fKMappers.size(), 1);
		
		assertEquals(fKMappers.get(0).getNamecolumnRef(), "ID");
		
		assertEquals(fKMappers.get(0).getNameTableRef(), "TRABAJO");
		
	}
	
	@Test
	public void getValueFKTest(){
		
		this.sqliteDb.getValueConfFK().put("persona.fk_trabajo", "nombre");
		this.sqliteDb. generateConfig();
		
		
		Object object = this.sqliteDb.getValueFK("persona", "fk_trabajo",9999);
		assertNull(object);
		
		object = this.sqliteDb.getValueFK("persona", "fk_trabajo_fake",1);
		assertNull(object);
		
		object = this.sqliteDb.getValueFK("personaFake", "fk_trabajo",1);
		assertNull(object);
		
		object = this.sqliteDb.getValueFK("personaFake", "fk_trabajo_Fake",9999999);
		assertNull(object);
		
		
		object = this.sqliteDb.getValueFK("persona", "fk_trabajo",1);
		assertEquals(object, "programador");
		
		
		object = this.sqliteDb.getValueFK("persona", "fk_trabajo",2);
		assertEquals(object, "arquitecto");
		
		object = this.sqliteDb.getValueFK("persona", "fk_trabajo",3);
		assertEquals(object, "plomero");

	}
	
	
	
	@Test
	public void getNamePrimaryKeyTest(){
		assertEquals(this.sqliteDb.getNamePrimaryKey("fake_table"), "id");
		assertEquals(this.sqliteDb.getNamePrimaryKey("ejemplo"), "id");
		assertEquals(this.sqliteDb.getNamePrimaryKey("no_parametro"), "id");
		assertEquals(this.sqliteDb.getNamePrimaryKey("persona"), "id");
		assertEquals(this.sqliteDb.getNamePrimaryKey("trabajo"), "id");
	}
	
	
	/**
	 * este hace prueba el lipiar tabla contar e insertar es el test para los tres
	 * countTable insertData
	 */
	@Test 
	public void insertDataTest(){
		
		Integer preValue = this.sqliteDb.countTable("no_parametro");
		
		assertEquals(preValue, new Integer(0));
		
		try {
			this.sqliteDb.insertData(
					" {\"table\": \"no_parametro\", "
					+ " \"data\":[ {\"id\" : 1 , \"name\": \"ejemplo\"},{\"id\" : 2 , \"name\": \"ejemplo2\"}]}");
		} catch (SqlJsonNoEqualParamException e) {
			
			fail();
			e.printStackTrace();
		}
		
		Integer postValue = this.sqliteDb.countTable("no_parametro");
		
		assertEquals(postValue, new Integer(2));
		
		this.sqliteDb.cleanTable("no_parametro");
		
		Integer cleanValue = this.sqliteDb.countTable("no_parametro");
		
		assertEquals(cleanValue, new Integer(0));
	}
	
	
	@Test
	public void countTableTest(){
		assertEquals(this.sqliteDb.countTable("ejemplo"),new Integer(1));
		assertEquals(this.sqliteDb.countTable("no_parametro"), new Integer(0));
		assertEquals(this.sqliteDb.countTable("persona"), new Integer(3));
		assertEquals(this.sqliteDb.countTable("trabajo"), new Integer(5));
		assertNull(this.sqliteDb.countTable("fake_table"));
	}


}
