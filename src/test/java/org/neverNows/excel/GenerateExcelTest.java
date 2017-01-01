package org.neverNows.excel;

import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.neverNows.database.SqliteDb;

public class GenerateExcelTest {

	private GenerateExcel generateExcel;
	private SqliteDb sqliteDb;
	
	@Before
	public void init(){
		this.sqliteDb = new SqliteDb("test/db/ejemplo.db");
		this.generateExcel = new GenerateExcel(true, this.sqliteDb);
	}
	
	@Test
	public void generateFromDBTest(){
		this.generateExcel.addWordToReplace("no_parametro", "Sin Parametros");
		this.generateExcel.addWordToReplace("fk_trabajo", "Trabajo");
		this.generateExcel.generateFromDB(); 
	}
	
	
	@Test
	public void generateConfigTest(){
		
		Class<? extends GenerateExcel> classReflextion = this.generateExcel.getClass();
		
		this.generateExcel.addWordToReplace("no_parametro", "Sin Parametros");
		
        try {
        	Class noparams[] = {};
        	
			Method method = classReflextion.getDeclaredMethod(
					"generateConfig",noparams);
			method.setAccessible(true);
			
			method.invoke(this.generateExcel, null);
			
		}catch(IllegalAccessException | IllegalArgumentException  
	    		 | InvocationTargetException |NoSuchMethodException e) {
	        e.printStackTrace();
	       
	     }
		//verificar archivo con grep
		//obtener lo escrito
	}

}
