package org.neverNows.database;

import java.util.List;

import org.neverNows.database.beans.FKMapper;
import org.neverNows.database.beans.StructureTable;

abstract class FatherDatabase {
	
	protected String driver;
	
	protected String database;
	
	
	protected String sqlGetAllTables;
	protected String sqlGetStructureTable;
	protected String sqlCountTable;
	protected String sqlSelectAll;
	
	public FatherDatabase(String driver, String database){
		this.driver = driver;
		this.database = database;
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
	 * consigue la structura de una tabla en concreto
	 * @param nameTable nombre de la tabla a verificar
	 * @return regresa un bean que mapea la structura
	 */
	public abstract StructureTable getStructureTable(String nameTable);
	
	
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
	public abstract List<FKMapper> getMapperFKInTable(String nameTable);

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

}
