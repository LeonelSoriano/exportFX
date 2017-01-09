package org.neverNows.excel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.nevernows.excel.GenerateExcel;

public class GenerateExcelTest {

	private GenerateExcel generateExcel;

	
	@Before
	public void init(){
		this.generateExcel = new GenerateExcel("test/db/ejemplo.db",true);
		this.generateExcel.addFkConf("persona.fk_trabajo","nombre");
		this.generateExcel.addManyConf("padre_hijo.fk_padre", "padre.nombre");
		this.generateExcel.addManyConf("padre_hijo.fk_hijo", "hijo.nombre");
	}
	
	@Test
	public void generateFromDBTest(){
		this.generateExcel.addWordToReplace("no_parametro", "Sin Parametros");
		this.generateExcel.addWordToReplace("fk_trabajo", "Trabajo");
		this.generateExcel.addWordToReplace("fk_hijo", "Hijo");
		this.generateExcel.addWordToReplace("fk_padre", "Padre");

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
