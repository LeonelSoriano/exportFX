package org.neverNows.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.JSONObject;
import org.neverNows.database.beans.FKMapper;
import org.neverNows.database.beans.StructureItemTable;
import org.neverNows.database.beans.StructureTable;
import org.neverNows.param.CommonString;
import org.neverNows.param.DriverList;
import org.neverNows.until.ComandTerminal;
import org.neverNows.until.FileUntil;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;

public class SqliteDb extends FatherDatabase{

	

	private String sqlDumpTable;
	
	public SqliteDb(String database){
		
		super(DriverList.sqlite,
				CommonString.PATH_BASE + database);
		
		fillAllSql();	
	}
	
	@Override
	protected void fillAllSql() {
		this.sqlGetAllTables = "SELECT * FROM sqlite_master WHERE type="
				+ "'table';";	
		
		this.sqlGetStructureTable = "PRAGMA table_info(<<nameTable>>)";
		
		this.sqlCountTable = "select count(*) as total from <<nameTable>>;";
		
		this.sqlSelectAll = "select * from <<nameTable>>;";
		
		this.sqlDumpTable = "SELECT sql FROM sqlite_master WHERE type='table' "
				+ "AND name = '<<nameTable>>';";
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
	
	

	@Override
	public void createDatabeFromFile( String file) {
		ComandTerminal comandTerminal = new ComandTerminal();
		FileUntil fileUntil = new FileUntil();
		
		comandTerminal.executeCommand("rm "  + this.database);
		
	   	Handle handle = null;
    	DBI dbi = new DBI(getDriverString());
    	
    	String sql = fileUntil.cleanDump(fileUntil.txtToString(CommonString.PATH_BASE + file));
    	
    	StringTokenizer stringTokenizer = new StringTokenizer(sql, ";");
    	
    	 while (stringTokenizer.hasMoreTokens()) { 
    		 
    	    	try {
    	            handle = dbi.open();
    	            handle.execute(stringTokenizer.nextToken());
    	            
    	        }catch (Exception e) {
    	        	System.err.println(e.getMessage());	
    			}
    	    	finally {
    	            if (handle != null) {
    	                handle.close();
    	            }
    	        }
    			
    	 }//end elihw
	  

	}

	@Override
	public List<String> getOrderTableByDump(String file) {

		List<String> tables = new ArrayList<>();
		
		String tokenSearch = "CREATE TABLE";
		
		FileUntil fileUntil = new FileUntil();
		StringBuilder dumpSql = new StringBuilder(
				fileUntil.txtToString(CommonString.PATH_BASE + file));
		
		for (int i = -1; (i = dumpSql.indexOf(tokenSearch, i + 1)) != -1; ) {
		    StringBuilder tableTmp = new StringBuilder();
		    
		   i += tokenSearch.length();
		   
		   boolean isFound = false;
		   boolean activateSearch = false;
		  
		   while ( !isFound || i > dumpSql.length()) {

			   if(dumpSql.charAt(i) != ' ' ){
				   activateSearch = true;
			   }
			   
			   if(activateSearch){
				   if(dumpSql.charAt(i) == ' '){
					   isFound = true;
				   }else{
					   tableTmp.append(dumpSql.charAt(i));
				   }
				   
			   } 
			   i++;  
		   }
		   
		   tables.add(tableTmp.toString().replace(" ", "")
				   .replace("\"", "")
				   .replace("'", "")
				   .replace("`", ""));

		}
		
		return tables;
	}
	
	
	/**
	 * @author leonelsoriano3@gmail.com
	 * @param tableName nombre de la tabla
	 * @return sql create de la tabla que se pidio
	 */
	private String getSqlCreateFormTable(String nameTable){
			
		StringBuilder sqlStr = new StringBuilder();
		
    	Handle handle = null;
    	DBI dbi = new DBI(getDriverString());
		
    	try {

    		String sql = this.sqlDumpTable.replaceAll("<<nameTable>>", nameTable);
            handle = dbi.open();
            Query<Map<String, Object>> q = handle.createQuery(sql);
            List<Map<String, Object>> l = q.list();

            for (Map<String, Object> m : l) {
            	sqlStr.append((String) m.get("sql"));
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

		return sqlStr.toString();
	}

	@Override
	public List<FKMapper> getMapperFKInTable(final String nameTable) {

		final String tokenSearch = "FOREIGN KEY";
		final String tokenReference = "REFERENCES";
		
		List<FKMapper> fkMappers = new ArrayList<>();
		
		StringBuilder dumpSql = new StringBuilder(
				this.getSqlCreateFormTable(nameTable).toUpperCase());
		
		

		for (int i = -1; (i = dumpSql.indexOf(tokenSearch, i + 1)) != -1; ) {
			
			FKMapper fkMapper = new FKMapper();
			fkMapper.setNameTable(nameTable);
			
			i += tokenSearch.length();
			
			
			boolean referenceSearchMode = false;
			boolean referenceSearchModeForeinTable = false;
			boolean isFound = false;
			boolean activateSearch = false;
			boolean searchTableReference = true;
			boolean searchTableReferenceActivate = false;
			boolean searchValueReferenceActivate = false;
			

			StringBuilder refTableForeinValue = new StringBuilder(); 
			StringBuilder refValueForeinValue = new StringBuilder();
			StringBuilder refTableValue = new StringBuilder(); 
			StringBuilder tmpValueReferenceSearch = new StringBuilder();
			StringBuilder refSearch = new StringBuilder();
			
			
			while ( !isFound && i < dumpSql.length()) {

				if(!referenceSearchMode){
					
					if(dumpSql.charAt(i) != ' ' ){
						activateSearch = true;
					}
					
					if(activateSearch){
						
						refSearch.append(dumpSql.charAt(i));
						if(dumpSql.charAt(i) == ')'){
							refTableValue.append(refSearch.toString());
							
							referenceSearchMode = true;
							activateSearch = false;
						}
					}

				}else if(referenceSearchMode){
					
					if(!referenceSearchModeForeinTable){
						if(dumpSql.charAt(i) != ' ' ){
							activateSearch = true;
						}else{
							activateSearch = false;
							tmpValueReferenceSearch.delete(0, refTableValue.length());
						}
						
					}else{
						//buscando en la parte de la tabla
						if(searchTableReference){
							if(dumpSql.charAt(i) != ' ' ){
								searchTableReferenceActivate = true;
							}else{
								searchTableReferenceActivate = false;
							}
							
							if(searchTableReferenceActivate){
								
								if(dumpSql.charAt(i) != '('){
									refTableForeinValue.append(dumpSql.charAt(i));
								}else{
									searchTableReference = false;
								}
							}
							
						}else{
							//buscando en la parte del valor referencia
							if(dumpSql.charAt(i) != ' ' ){
								searchValueReferenceActivate = true;
							}else{
								searchValueReferenceActivate = false;
							}
							
							
							if(searchValueReferenceActivate){
								
								if(dumpSql.charAt(i) != ')'){
									refValueForeinValue.append(dumpSql.charAt(i));
								}else{
									searchTableReference = false;
									isFound = true;
								}
							}
							
							
						}

					}


					if(activateSearch){

						tmpValueReferenceSearch.append(dumpSql.charAt(i));

						if(tmpValueReferenceSearch.toString().
								toUpperCase().equals(tokenReference)){
							referenceSearchModeForeinTable = true;		
							tmpValueReferenceSearch.delete(0, refTableValue.length());
						}
					}
						
				}
				
				i++;
			}
			
			
			fkMapper.setNameTableRef(refTableForeinValue.toString().replace(" ", "")
					.replace("\"", "")
					.replace("'", "")
				 	.replace("`", "")
				 	.replace("(", "")
				 	.replace(")", ""));
			
			fkMapper.setNamecolumnRef(refValueForeinValue.toString().replace(" ", "")
					.replace("\"", "")
					.replace("'", "")
				 	.replace("`", "")
				 	.replace("(", "")
				 	.replace(")", ""));
			
			fkMapper.setNamecolumn(refTableValue.toString().replace(" ", "")
					.replace("\"", "")
					.replace("'", "")
				 	.replace("`", "")
				 	.replace("(", "")
				 	.replace(")", ""));
			
			fkMappers.add(fkMapper);
			
		}
		
		return fkMappers;
	}


	

}
