package org.neverNows.database.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * es la estrutura total de una tabla en base de datos
 * @author leonelsoriano3@gmail.com
 *
 */
public class StructureTable {
	
	private String nameTable;
	
	private List<StructureItemTable> itemTables;
	
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
	
	

}
