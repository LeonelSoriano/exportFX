package org.neverNows.databaseTest;

import java.io.File;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
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
		
		assertEquals(confFile.exists(),false);
		
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

}
