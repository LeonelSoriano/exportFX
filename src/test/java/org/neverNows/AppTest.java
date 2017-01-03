package org.neverNows;


import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;


import org.junit.Test;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.DBI;

import org.skife.jdbi.v2.Query;
/**
 * Unit test for simple App.
 */
public class AppTest{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( )
    {
       
    }
    
    @Test
    public void ejemplo(){
    	
    	Handle handle = null;
    	DBI dbi = new DBI("jdbc:sqlite:/home/leonel/exportFX/src/test/db/export.db");
        
    	try {

    		String sql = "select * FROM ejemplo";
            handle = dbi.open();
            Query<Map<String, Object>> q = handle.createQuery(sql);
            List<Map<String, Object>> l = q.list();

            for (Map<String, Object> m : l) {
            
                System.out.printf("%d ", m.get("id"));
                System.out.printf("%s ", m.get("name"));
               
            }

        }catch (Exception e) {
			System.out.println(e);
		}
    	finally {
            if (handle != null) {
                handle.close();
            }
        }
    	
    	
    	assertTrue(true);
    	
    }


}
