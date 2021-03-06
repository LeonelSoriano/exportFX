package org.nevernows.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.nevernows.SqlJsonNoEqualParamException;
import org.nevernows.database.beans.FKMapper;
import org.nevernows.database.beans.StructureItemTable;
import org.nevernows.database.beans.StructureTable;
import org.nevernows.enumeration.SqliteTypeEnum;
import org.nevernows.param.CommonString;
import org.nevernows.param.DriverList;
import org.nevernows.until.ComandTerminal;
import org.nevernows.until.FileUntil;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.exceptions.UnableToCreateStatementException;

public class SqliteDb extends FatherDatabase{

	private Logger logger = Logger.getLogger(SqliteDb.class.getName());
	
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
		
		this.sqlTruncate = "DELETE FROM <<nameTable>>;";
	} 
	
	@Override
	public List<String> getAllTables() {
		
		List<String> result = new ArrayList<>();
		
    
    	DBI dbi = new DBI(getDriverString());
    	
    	

    		String sql = this.sqlGetAllTables;
    		Handle handle = dbi.open();
            Query<Map<String, Object>> q = handle.createQuery(sql);
            List<Map<String, Object>> l = q.list();

            for (Map<String, Object> m : l) {
            
            	String tableName = m.get("name").toString().toLowerCase();
            	
            	if(m.get("type").toString().equalsIgnoreCase("table")){
            		
            		if("sqlite_sequence".equals(tableName)){
            			continue;
            		}else{
            			result.add(tableName);
            		}
            	}
            }

 
        handle.close();
		
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

            if(l.isEmpty()){
            	return null;
            }
            
            for (Map<String, Object> m : l) {
            	
            	StructureItemTable itemTable = new StructureItemTable();
            	
            	itemTable.setId((int) m.get("pk"));
            	itemTable.setName( m.get("name").toString());
            	itemTable.setNotNull( 
            			(((Integer) m.get("notnull")).intValue() == 1) ? true : false );
            	
            	itemTable.setDefaulValue((m.get("dflt_value") == null ) ? null :
            		(m.get("dflt_value").toString()));
            	
            	itemTable.setFk(
            			(((Integer) m.get("pk")).intValue() == 1) ? true : false );
            	
            	if(m.get("type").toString().contains("(")){
            		//TEXT(50)
            		StringBuilder maxValueStr = new StringBuilder();
            		StringBuilder typeValueStr = new StringBuilder();
            		boolean activateFillString = false;
            		for(char c : m.get("type").toString().toCharArray()){
            			if(c == ')'){
            				break;
            			}
            			
            			if(activateFillString){
            				maxValueStr.append(c);
            			}
            			
            			if(c == '('){
            				activateFillString = true;
            			}else if(!activateFillString){
            				typeValueStr.append(c);
            			}	
            		}
            		
            		itemTable.setMaxValue(Integer.parseInt(maxValueStr.toString()));
            		itemTable.setType(typeValueStr.toString());
            		
            	}else{
            		itemTable.setType(m.get("type").toString());
            		itemTable.setMaxValue(-1);
            	}
            	
            	
            	itemTable.setOrder((int)m.get("cid"));
            	
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
	public StructureTable getStructureTableWithData(String nameTable) {
		
		StringBuilder sqlSelect = new StringBuilder("SELECT ");
		
		StructureTable structureTable = this.getStructureTable(nameTable);
		
		boolean isFirstValue = true;
		for(StructureItemTable itemTable : structureTable.getItemTables() ){
			if(isFirstValue){
				isFirstValue = false;
			}else{
				sqlSelect.append(", ");
			}
			
			sqlSelect.append(itemTable.getName());
		}
		
		sqlSelect.append(" FROM ");
		sqlSelect.append(structureTable.getNameTable());
		
	 	Handle handle = null;
    	DBI dbi = new DBI(getDriverString());
		
    	try {
    		
            handle = dbi.open();
            Query<Map<String, Object>> q = handle.createQuery(sqlSelect.toString());
            List<Map<String, Object>> l = q.list();

            for (Map<String, Object> m : l) {
            	
            	for(int i = 0; i < structureTable.getItemTables().size(); i++){
            		structureTable.getItemTables().get(i).addDataValues(
            				m.get(structureTable.getItemTables().get(i).getName()));
            	}
            }

        }catch (Exception e) {
        	System.err.println(e.getMessage());
		}
    	finally {
            if (handle != null) {
                handle.close();
            }
        }
		
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
	
	
	@Override
	public Object getValueFK(String nameTable, String column, Object value){
		
		List<FKMapper> fkMappers = this.getMapperFKInTable(nameTable);
		
		
		if(fkMappers.isEmpty()){
			return null;
		}
		
		
		FKMapper fkMapper = null;
		
		for(int i = 0; i < fkMappers.size() ; i++){
			if(fkMappers.get(i).getNamecolumn().equalsIgnoreCase(
					column)){
				fkMapper = fkMappers.get(i);
				break;
			}
	
		}
		
		
		if(fkMapper == null){
			return null;
		}
		
    	Handle handle = null;
    	DBI dbi = new DBI(getDriverString());
    	
    	
    	StructureTable structureTable = this.getStructureTable(fkMapper.getNameTableRef());
    	
    	
    	if(structureTable == null){
    		return null;
    	}
    	
    
    	String idFk = this.getNamePrimaryKey(fkMapper.getNameTableRef());
    	
    	StringBuilder builder = new StringBuilder();
    	
    	
    	String aliasNameFK = fkMapper.getNamecolumnRef();
    	
    	// esto espara leer lo que esta enelarchivo de cof para
    	//saber a que columna esta aputnadno la fk
    	Map<String, String> fkMap = this.getFKConfiguration();
    	if(fkMap != null){
    		String aliasColumnFK = fkMap.get(fkMapper.getNameTable()+ "." +
    				fkMapper.getNamecolumn());
    		
    		if(aliasColumnFK != null){
    			aliasNameFK = aliasColumnFK;
    		}
    	}
    	
    	builder.append("SELECT ");
    	builder.append(aliasNameFK);
    	builder.append(" as valueFK FROM ");
    	builder.append(fkMapper.getNameTableRef());
    	builder.append(" WHERE ");
    	builder.append(idFk);
    	builder.append(" = ");
    	
    	if(value instanceof String){
    		builder.append("'");
    		builder.append(value);
    		builder.append("'");
    	}else{
    		builder.append(value);
    	}
    	
    	
    	
    	//SELECT * FROM TABLE WHERE COLUMN = VALUE
    	//TODO aca tengo q agregar los del fk y saber q tiene
       	try {

    		String sql = builder.toString();
            handle = dbi.open();
            Query<Map<String, Object>> q = handle.createQuery(sql);
            List<Map<String, Object>> l = q.list();

            for (Map<String, Object> m : l) {
            	
            	
            	return m.get("valueFK");
            }

        }catch (Exception e) {
        	
        	System.err.println(e.getMessage());
        	
		}
    	finally {
            if (handle != null) {
                handle.close();
            }
        }
    	
		return null;
    	
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
								equalsIgnoreCase(tokenReference)){
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
	
	
	@Override
	public String getNamePrimaryKey(String nameTable){
		
		StructureTable structureTable = this.getStructureTable(nameTable);
		
		if(structureTable == null){
			return null;
		}
		
		for(StructureItemTable itemTable : structureTable.getItemTables()){
			if(itemTable.getId() == 1){
				return itemTable.getName();
			}
		}
		return null;
	}

	@Override
	public void insertData(String jsonTable) throws SqlJsonNoEqualParamException {
		
		//tiene el sql de salida del insert
		StringBuilder builder = new StringBuilder("INSERT INTO ");
		JSONObject jsonObject = new JSONObject(jsonTable);
		
		builder.append(jsonObject.getString("table").toUpperCase());
		
		//datos a insertar en la tabla
		JSONArray jsonData = jsonObject.getJSONArray("data");
		
		StructureTable structureTable = 
				this.getStructureTable(jsonObject.getString("table").toUpperCase());
		
		List<String> valuesInsertFromJson = new ArrayList<>();
		
		/*
		 * en esta parte voy a validar el objeto con alguna pequeña logica
		 */
		JSONObject jsonIterate = (JSONObject) jsonData.get(0);
		
		Iterator<String> iter = jsonIterate.keys();
		int maxValueJson = 0;
		
		//TODO: agregar la excecion cuando se repitan las colummnas del json
		while (iter.hasNext()) {
			maxValueJson++;
			String key = iter.next();
			valuesInsertFromJson.add(key);
			
			if(!structureTable.existInEstrucute(key))
			{
				throw new SqlJsonNoEqualParamException();
			}			
		}
		
		if(maxValueJson != structureTable.getItemTables().size() ){
			throw new SqlJsonNoEqualParamException();
		}
		
		boolean isOnlyColumn = true;
		builder.append(" (");
		for(String column : valuesInsertFromJson){
			if(isOnlyColumn){
				isOnlyColumn = false;
			}else{
				builder.append(",");
			}
			builder.append(" ").append(column);
		}
		builder.append(") ");

		builder.append(" VALUES ");
		boolean isFirstValues = true;
		for(int i = 0; i < jsonData.length() ; i++){
			
			if(isFirstValues){
				isFirstValues = false;
			}else{
				builder.append(",");
			}
			
			builder.append(" (");
			
			JSONObject tmpJson = (JSONObject) jsonData.get(i);
			Iterator<String> iterTmp = tmpJson.keys();
			
			//reutilizo esta variable lo uqe hace es sabver si es el primero para
			// colocar coma o no
			isOnlyColumn = true;
			while (iterTmp.hasNext()) {
				
				if(isOnlyColumn){
					isOnlyColumn = false;
				}else{
					builder.append(",");
				}
				
				String key = iterTmp.next();
				StructureItemTable itemTableTmp = 
						structureTable.getItemTableByName(key);
				
				if(itemTableTmp == null){
					throw new SqlJsonNoEqualParamException();
				}
				
			
		        Object value = tmpJson.get(key);
			        
		        if(itemTableTmp.getType().equals(SqliteTypeEnum.BLOB.name()) ||
	        		itemTableTmp.getType().equals(SqliteTypeEnum.TEXT.name()) ){
		        	builder.append("'" + value + "'");
			        	
		        }else{
		        	builder.append(value);
		        }
			   
			   
				
			}
			

			builder.append(") ");
		}
		builder.append(";");

		
    	DBI dbi = new DBI(getDriverString());
        
   
    	Handle handle = dbi.open();
        handle.execute(builder.toString());
            
     
       handle.close();

	}

	@Override
	public Integer countTable(String nameTable) {
		

    	DBI dbi = new DBI(getDriverString());
    	Handle handle = dbi.open();
    	try{
    		String sql = this.sqlCountTable.replaceAll("<<nameTable>>", nameTable);
			
        	Query<Map<String, Object>> q = handle.createQuery(sql);
        	List<Map<String, Object>> l = q.list();

            for (Map<String, Object> m : l) {
            	
                handle.close();
            	
            	return (Integer)m.get("total");
            }

    	}catch (UnableToCreateStatementException e) {
    		logger.log(Level.WARNING, e.getMessage(), e);
    		handle.close();
    	}
		
		return null;
	}

	
	@Override
	public void cleanTable(String nameTable) {
    
    	DBI dbi = new DBI(getDriverString());
        
    	Handle handle = dbi.open();
        handle.execute(this.sqlTruncate.replace("<<nameTable>>",nameTable));
 
        handle.close();
               	
	}



	

}
