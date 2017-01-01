package org.neverNows.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neverNows.SqlJsonNoEqualParamException;
import org.neverNows.database.beans.FKMapper;
import org.neverNows.database.beans.StructureTable;

public abstract class FatherDatabase {
	
	protected String driver;
	
	protected String database;
	
	protected String sqlGetAllTables;
	protected String sqlGetStructureTable;
	protected String sqlCountTable;
	protected String sqlSelectAll;
	protected String sqlTruncate;
	private Map<String , String> valueConfFK;
	
	
	
	public FatherDatabase(String driver, String database){
		this.driver = driver;
		this.database = database;
		this.valueConfFK = new HashMap<>();
	}
	
	
	/**
	 * genera el archivo de configuracion de la base de datos
	 */
	public void generateConfig(){
		
		
	}
	
	
	/**
	 * @author leonelsoriano3@gmail.com
	 * metodo que llena las string de sql en la clase
	 * este debe ser llamado en el contructor del hijo
	 */
	protected abstract void fillAllSql();

	/**
	 * @author leonelsoriano3@gmail.com
	 * consigue todos los nombre de las tablas en la db
	 * @return {@link List} de nombre de tablas
	 */
	public abstract List<String> getAllTables();
	
	/**
	 * @author leonelsoriano3@gmail.com
	 * consigue la structura de una tabla en concreto si no existe en 
	 * la base de datos devuelve null
	 * @param nameTable nombre de la tabla a verificar
	 * @return regresa un bean que mapea la structura
	 */
	public abstract StructureTable getStructureTable(String nameTable);
	
	/**
	 * consigue una {@link StructureTable} con la data que tiene cada columna en la 
	 * base de datos
	 * @param nameTable nombre de la tabla
	 * @return structura de la tabla mas los datos en la base de datos
	 */
	public abstract StructureTable getStructureTableWithData(String nameTable);
	
	
	/**
	 * @author leonelsoriano3@gmail.com
	 * consigue todas las structuras de las tablas en la base de datos
	 * @return {@link List<StructureTable>} lista de estructura de tablas
	 */
	public abstract List<StructureTable> getStructureTables();
	
	
	/**
	 * @author leonelsoriano3@gmail.com
	 * verifica si una tabla tiene datos es decir es de parametros
	 * @param nameTable nombre de la tabla a verificar
	 * @return verdadero si posee datos es decir es de parametros
	 */
	public abstract boolean tableIsParam(String nameTable);
	
	
	/**
	 * @author leonelsoriano3@gmail.com
	 * obtiene un json de la inforacion de la tabla pero no obtiene
	 * una tabla asociada solo su fk
	 * @param nameTable nombre de la tabla
	 * @return un json que indica la inforacion de la tabla
	 */
	public abstract String valuesFormTableSimple(String nameTable);
	
	/**
	 * @author leonelsoriano3@gmail.com
	 * inicia la base de datos desde un archivo dump
	 * @param file archivo dump de la base de datos
	 */
	public abstract void createDatabeFromFile( String file);
	
	
	/**
	 * consigue el nombre de la columna que es primary key
	 * @param tableName nombre de la tabla
	 * @return nobre de la columna con el primary key
	 */
	public abstract String getNamePrimaryKey(String nameTable);
	
	
	
	/**
	 * @author leonelsoriano3@gmail.com
	 * consigue el order como seran verificados y guardados
	 * desde el archivo dump, esto me ahorra hacerlo yo y 
	 * aprobechar ya los dump de las base de datos
	 * @param file archivo dump
	 * @return lista ordenada de las tablas
	 */
	public abstract List<String> getOrderTableByDump(String file);
	
	
	/**
	 * @author leonelsoriano3@gmail.com
	 * consigue las tablas correspondientes a los fk de otra tabla
	 * @param nameTable nombre de la tabla
	 * @return {@link List<FKMapper>} array list que tiene todos los mapeos de fk
	 */
	public abstract List<FKMapper> getMapperFKInTable( String nameTable);
	
	
	/**
	 * consigue el valor especifico de la tabla asociada al fk
	 * devuelve null si no consigue algo que concuerde con los param
	 * 
	 * @param nombre de la tabla
	 * @param nombre de la columna
	 * @return el valor de la tabla asociada al fk
	 */
	public abstract Object getValueFK(String nameTable, String column, Object value);
	
	/**
	 * @author leonelsoriano3@gmail.com
	 * inserta valores desde un json 
	 * @param jsonTable json de datos ejemplo { "table" : "nombre tabla" , values :
	 * [{value1 : null : value2 : "ejemplo"}  ] }
	 * @throws SqlJsonNoEqualParamException si los parametros del json no son iguales a los
	 * 		      de su tabla en la base de datos
	 */
	public abstract void insertData(String jsonTable) throws SqlJsonNoEqualParamException;
	
	
	/**
	 * @author leonelsoriano3@gmail.com
	 * cuenta la cantidad de itenes en una tabla
	 * @param tabla que se contara
	 * @return el numero de itenes totales en la tabla
	 */
	public abstract Integer countTable(String nameTable);
	
	
	/**
	 * limpia todos los registro de  una tabla
	 * @param nameTable nombre de la tabla a limpiar
	 */
	public abstract void cleanTable(String nameTable);
	

	

	/**
	 * @author leonelsoriano3@gmail.com
	 * concatena la inforacion para obtener el string del driver
	 * @return el string del driver
	 */
	protected String getDriverString() {
		
		StringBuilder strDriver = new StringBuilder();
		strDriver.append(this.driver);
		strDriver.append(":");
		strDriver.append(this.database);
		return strDriver.toString();
	}


	
	
	public Map<String, String> getValueConfFK() {
		return valueConfFK;
	}



	
}
