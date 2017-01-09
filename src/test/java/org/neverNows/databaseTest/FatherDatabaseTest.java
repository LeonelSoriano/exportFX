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
import org.nevernows.database.FatherDatabase;
import org.nevernows.database.SqliteDb;
import org.nevernows.param.CommonString;
import org.nevernows.until.FileUntil;

public class FatherDatabaseTest {

	private FatherDatabase fatherDatabase;
	
	@Before
	public void init() {
		fatherDatabase = new SqliteDb("test/db/ejemplo.db");
		
		this.fatherDatabase.getValueConfFK().put("persona.fk_trabajo", "nombre");
		this.fatherDatabase.getValueConfFK().put("TEST_FK", "value1");
		this.fatherDatabase.getValueConfFK().put("TEST_FK2", "value2");
		
		//este agrega un como y elimina la columna de su tabla de origen
		this.fatherDatabase.getValueConfManyToMany().put("padre_hijo.fk_padre", "padre.nombre");
		this.fatherDatabase.getValueConfManyToMany().put("padre_hijo.fk_hijo", "hijo.nombre");
	}
	
	@Test
	public void generateConfigTest(){
		
		File confFile = new File(CommonString.PATH_BASE_TEST + CommonString.NAME_CONF_DB);
		
		assertEquals("debes iniciar la prueba con el archivo de"
				+ "configuracion eliminado",confFile.exists(),false);
	
		
		this.fatherDatabase.generateConfig();
		
		confFile = new File(CommonString.PATH_BASE_TEST + CommonString.NAME_CONF_DB);
		
		assertEquals(confFile.exists(), true);
		
		
		FileUntil fileUntil = new FileUntil();
		
		String jsonStr = fileUntil.txtToString(
				CommonString.PATH_BASE_TEST + CommonString.NAME_CONF_DB);
		
		JSONObject jsonObject = new JSONObject(jsonStr);	
		
		JSONArray jsonManyToMany = jsonObject.getJSONArray("many_to_many");
		
		assertNotNull(jsonManyToMany);			
		
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
		confFile.delete();
		
//		assertEquals("debes iniciar la prueba con el archivo de"
//				+ "configuracion eliminado",confFile.exists(),false);		
		
		
		//enviarlo antes de existir el archivo 
		Map<String,String> fkConfg = this.fatherDatabase.getFKConfiguration();
		assertNull(fkConfg);
		
		
		this.fatherDatabase.generateConfig();
		fkConfg = this.fatherDatabase.getFKConfiguration();
		
		assertEquals(fkConfg.get(("persona.fk_trabajo").toUpperCase()), "nombre" );
		
		assertEquals(confFile.delete(), true);
		assertEquals(confFile.exists(), false);
	}
	
	
	@Test
	public void getManyToManyConfigurationTest(){
		File confFile = new File(CommonString.PATH_BASE_TEST + CommonString.NAME_CONF_DB);
		confFile.delete();
		
		this.fatherDatabase.generateConfig();
		
		Map<String,String> manyConfg = this.fatherDatabase.getManyToManyConfiguration();
	
		assertEquals(manyConfg.get(("padre_hijo.fk_padre").toUpperCase()), ("padre.nombre").toUpperCase() );
		assertEquals(manyConfg.get(("padre_hijo.fk_hijo").toUpperCase()), ("hijo.nombre").toUpperCase() );
		this.fatherDatabase.getValueConfManyToMany().put("padre_hijo.fk_padre", "hijo.nombre");
	}
	
	

}
