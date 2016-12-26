package org.neverNows.database.beans;

import org.neverNows.database.beans.StructureItemTable;
import java.util.ArrayList;
import java.util.List;


/**
 *           	  ,/{}
                ,/  {|
            ,,,/    {|,
      __--~~        {| ~-,
__--~~              {     `\
                        ,__ \
                       `,\{),\,
                      __-~  `_ ~-_
                   _-~        ~~-_`~-_
                  '               `~-_`~-__
                  `,                  `~-\_|
                   `,      _-----___    _,'
                   / /--__  ~~--__  `~,~
                    /     ~~--__  ~-',
                   /
leonelsoriano3@gmail.com  
 */
/**
 * es la estrutura total de una tabla en base de datos
 * @author leonelsoriano3@gmail.com
 *
 */
public class StructureTable {
	
	/**
	 * el nombre de la tabla
	 */
	private String nameTable;
	
	/**
	 * son las columnas de la tabla
	 */
	private List<StructureItemTable> itemTables;
	
	/**
	 * este es para saber si la tabla no esta vacia de ser asi
	 * quiere decir que es una tabla de configuracion
	 */
	private boolean paramTable;
	
	public StructureTable(String nameTable) {
		this.nameTable = nameTable;
		this.itemTables = new ArrayList<>();
	}
	
	public String getNameTable(){
		return this.nameTable;
	}
	
	public List<StructureItemTable> getItemTables(){
		return this.itemTables;
	}
	
	public void addItemTable(StructureItemTable itemTable){
		this.itemTables.add(itemTable);
	}

	public boolean isParamTable() {
		return paramTable;
	}

	public void setParamTable(boolean paramTable) {
		this.paramTable = paramTable;
	}
	
	/**
	 * verifica si una comulna que se le pasa como parametro
	 * existe en la tabla
	 * @param nameColum es el nombre de la columna a verificar
	 * @return devuelve verdadero si la tabla existe
	 */
	public boolean existInEstrucute(String nameColum){
		
		for(StructureItemTable itemTable : this.itemTables){
			if(itemTable.getName().toUpperCase().equals(nameColum.toUpperCase())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * consigue un item dependiendo el nombre de esa columna
	 * @return regresa el item que posea el mismo nombre
	 */
	public StructureItemTable getItemTableByName(String nameColumn){
		for(StructureItemTable itemTable : this.itemTables){
			if(itemTable.getName().toUpperCase().equals(nameColumn.toUpperCase())){
				return itemTable;
			}
		}
		
		return null;
	}
	
	

}
