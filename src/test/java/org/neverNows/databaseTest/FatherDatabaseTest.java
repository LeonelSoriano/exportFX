package org.neverNows.databaseTest;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.neverNows.database.FatherDatabase;
import org.neverNows.database.SqliteDb;
import org.neverNows.param.CommonString;
import org.neverNows.until.FileUntil;

public class FatherDatabaseTest {

	private FatherDatabase fatherDatabase;
	
	@Before
	public void init() {
		fatherDatabase = new SqliteDb("test/db/ejemplo.db");
	}
	
	@Test
	public void generateConfigTest(){
		
		File confFile = new File(CommonString.PATH_BASE_TEST + CommonString.NAME_CONF_DB);
		
		assertEquals("debes iniciar la prueba con el archivo de"
				+ "configuracion eliminado",confFile.exists(),false);
		
		this.fatherDatabase.getValueConfFK().put("TEST_FK", "value1");
		this.fatherDatabase.getValueConfFK().put("TEST_FK2", "value2");
		
		this.fatherDatabase.generateConfig();
		
		confFile = new File(CommonString.PATH_BASE_TEST + CommonString.NAME_CONF_DB);
		
		assertEquals(confFile.exists(), true);
		
		
		FileUntil fileUntil = new FileUntil();
		
		String jsonStr = fileUntil.txtToString(
				CommonString.PATH_BASE_TEST + CommonString.NAME_CONF_DB);
		
		JSONObject jsonObject = new JSONObject(jsonStr);																				
							
		JSONArray jsonFK = jsonObject.getJSONArray("fk_config");
		
		assertNotNull(jsonFK);
	
		   try {
			   
			   for(int i = 0; i < jsonFK.length() ; i ++){
				   JSONObject json = (JSONObject) jsonFK.get(i);
				   Iterator<String> temp = json.keys();
				   while (temp.hasNext()) {
			            String key = temp.next();
			            String value = json.get(key).toString();
			            
			            if(key.toUpperCase().equals("TEST_FK")){
			            	assertEquals(value, "value1");
			            }else if(key.toUpperCase().equals("TEST_FK2")){
			            	assertEquals(value, "value2");
			            }
			        }
			   }
		        
		    } catch (JSONException e) {
		        e.printStackTrace();
		        fail(e.getMessage());
		    }
		
		
								
		confFile = new File(CommonString.PATH_BASE_TEST + CommonString.NAME_CONF_DB);
		
		assertEquals(confFile.delete(), true);
		
		assertEquals(confFile.exists(), false);
		
	}
	
	
	@Test
	public void getFKConfigurationTest(){
		
		File confFile = new File(CommonString.PATH_BASE_TEST + CommonString.NAME_CONF_DB);
		
		assertEquals("debes iniciar la prueba con el archivo de"
				+ "configuracion eliminado",confFile.exists(),false);		
		
		
		//enviarlo antes de existir el archivo 
		Map<String,String> fkConfg = this.fatherDatabase.getFKConfiguration();
		
		
		assertNull(fkConfg);
		
		
		confFile = new File(CommonString.PATH_BASE_TEST + CommonString.NAME_CONF_DB);
		
		this.fatherDatabase. generateConfig();
		fkConfg = this.fatherDatabase.getFKConfiguration();
		 
		 assertEquals(fkConfg.size(), 0);
		
		
		 confFile.delete();
		 
		this.fatherDatabase.getValueConfFK().put("persona.fk_trabajo", "nombre");
		
		this.fatherDatabase. generateConfig();
		
		fkConfg = this.fatherDatabase.getFKConfiguration();
		
		
		for (Map.Entry<String, String> entry : fkConfg.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			
			assertTrue(key.equalsIgnoreCase("persona.fk_trabajo"));
			assertEquals(value,"nombre");
			
		}
		
		assertEquals(confFile.delete(), true);
		assertEquals(confFile.exists(), false);
	}
	
	

}
