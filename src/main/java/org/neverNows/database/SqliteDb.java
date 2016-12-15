package org.neverNows.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

import org.neverNows.database.beans.StructureItemTable;
import org.neverNows.database.beans.StructureTable;
import org.neverNows.param.DriverList;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;

public class SqliteDb extends FatherDatabase{
	
	

	
	
	public SqliteDb(){
		super(DriverList.sqlite,
				"/home/leonel/exportFX/src/test/db/export.db");
		
		fillAllSql();
	}
	
	public SqliteDb(String database){
		
		super(DriverList.sqlite,
			 database);
		
		fillAllSql();
	}
	
	@Override
	protected void fillAllSql() {
		this.sqlGetAllTables = "SELECT * FROM sqlite_master WHERE type="
				+ "'table';";	
		
		this.sqlGetStructureTable = "PRAGMA table_info(<<nameTable>>)";
		
		this.sqlCountTable = "select count(*) as total from <<nameTable>>;";
		
		this.sqlSelectAll = "select * from <<nameTable>>;";
	} 
	
	
	
	@Override
	public List<String> getAllTables() {
		
		List<String> result = new ArrayList<>();
		
    	Handle handle = null;
    	DBI dbi = new DBI(getDriverString());
    	//
    	try {

    		String sql = this.sqlGetAllTables;
            handle = dbi.open();
            Query<Map<String, Object>> q = handle.createQuery(sql);
            List<Map<String, Object>> l = q.list();

            for (Map<String, Object> m : l) {
            
            	String tableName = m.get("name").toString().toLowerCase();
            	
            	if(m.get("type").toString().toLowerCase().equals("table")){
            		
            		if(tableName.equals("sqlite_sequence")){
            			continue;
            		}else{
            			result.add(tableName);
            		}
            	}
            }

        }catch (Exception e) {
        	System.err.println(e.getMessage());
        	return new ArrayList<>();
		}
    	finally {
            if (handle != null) {
                handle.close();
            }
        }
		
		return result;
	}

	@Override
	public StructureTable getStructureTable(String nameTable) {
		
		StructureTable structureTable = new StructureTable(nameTable);
		
    	Handle handle = null;
    	DBI dbi = new DBI(getDriverString());
		
    	try {

    		String sql = this.sqlGetStructureTable.replaceAll("<<nameTable>>",
    				nameTable);
    		
            handle = dbi.open();
            Query<Map<String, Object>> q = handle.createQuery(sql);
            List<Map<String, Object>> l = q.list();

            for (Map<String, Object> m : l) {
            	
            	StructureItemTable itemTable = new StructureItemTable();
            	
            	itemTable.setId((int) m.get("cid"));
            	itemTable.setName( m.get("name").toString());
            	itemTable.setNotNull( 
            			(((Integer) m.get("notnull")).intValue() == 1) ? true : false );
            	
            	itemTable.setDefaulValue((m.get("dflt_value") == null ) ? null :
            		(m.get("dflt_value").toString()));
            	
            	itemTable.setFk(
            			(((Integer) m.get("pk")).intValue() == 1) ? true : false );
            	
            	itemTable.setType(m.get("type").toString());
            	
            	structureTable.addItemTable(itemTable);
            }

        }catch (Exception e) {
        	System.err.println(e.getMessage());
        	return null;
		}
    	finally {
            if (handle != null) {
                handle.close();
            }
        }
    	
    	structureTable.setParamTable(this.tableIsParam(nameTable));;
	
		return structureTable;
	}

	@Override
	public List<StructureTable> getStructureTables() {
		
		List<StructureTable> structureTables = new ArrayList<>();
		
		List<String> tables = getAllTables();
		
		//StructureTable getStructureTable
		for(String table : tables){
			StructureTable structureTable = this.getStructureTable(table);
			structureTables.add(structureTable);
		}
		
		return structureTables;
	}

	@Override
	public boolean tableIsParam(String nameTable) {
		
		
		int count = 0;
		
    	Handle handle = null;
    	DBI dbi = new DBI(getDriverString());
		
    	try {

    		String sql = this.sqlCountTable.replaceAll("<<nameTable>>", nameTable);
            handle = dbi.open();
            Query<Map<String, Object>> q = handle.createQuery(sql);
            List<Map<String, Object>> l = q.list();

            for (Map<String, Object> m : l) {
            	count = (Integer)m.get("total");
            }

        }catch (Exception e) {
        	System.err.println(e.getMessage());
        	return false;
		}
    	finally {
            if (handle != null) {
                handle.close();
            }
        }
		return (count > 0)? true : false ;
	}

	@Override
	public String valuesFormTableSimple(String nameTable) {
		JSONObject obj = new JSONObject();
	 
		List<JSONObject> data  = new ArrayList<>();
		

    	Handle handle = null;
    	DBI dbi = new DBI(getDriverString());
	  
    	try {
    		String sql = this.sqlSelectAll.replaceAll(
    				"<<nameTable>>", nameTable);
    		
            handle = dbi.open();
            Query<Map<String, Object>> q = handle.createQuery(sql);
            List<Map<String, Object>> l = q.list();

            for (Map<String, Object> map : l) {
            	JSONObject dataJsonTable = new JSONObject();
            	
            	for (Map.Entry<String, Object> entry : map.entrySet()) {
            	    String key = entry.getKey();
            	    Object value = entry.getValue();
            	    dataJsonTable.put(key, value);
            	}
            	data.add(dataJsonTable);
            }

        }catch (Exception e) {
        	System.err.println(e.getMessage());
        	return "";
		}
    	finally {
            if (handle != null) {
                handle.close();
            }
        }
    
	    obj.put("table", nameTable);
	    obj.put("data", data);

	    
	    return obj.toString();
	}


	

}